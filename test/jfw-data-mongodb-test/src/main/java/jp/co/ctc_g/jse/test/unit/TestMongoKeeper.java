/*
 * Copyright (c) 2013 ITOCHU Techno-Solutions Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.ctc_g.jse.test.unit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jfw.core.util.Arrays;
import jp.co.ctc_g.jfw.core.util.Lists;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * <p>
 * このクラスは、ユニットテストの際のMongoDBの状態を管理します。
 * </p>
 * <p>
 * J-Frameworkが提供するユニットテストのためのMongoDB管理機能についての詳細は、
 * {@link MongoInitialize}を参照してください。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see MongoInitialize
 * @see jp.co.ctc_g.jfw.test.unit.J2Unit4ClassRunner
 */
public class TestMongoKeeper extends AbstractTestExecutionListener {

    private static final Logger L = LoggerFactory.getLogger(TestMongoKeeper.class);

    private static final ResourceBundle R = InternalMessages.getBundle(TestMongoKeeper.class);

    /**
     * デフォルトコンストラクタです。
     */
    public TestMongoKeeper() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeTestMethod(TestContext context) throws Exception {
        if (context.getTestMethod().isAnnotationPresent(MongoInitialize.class)) {
            MongoInitialize config = context.getTestMethod().getAnnotation(MongoInitialize.class);
            initialize(context, config);
        }
    }

    /**
     * MongoDBを初期化します。
     * @param context 現在のテスト実行コンテキスト
     * @param config コンフィグ
     */
    protected void initialize(TestContext context, MongoInitialize config) {
        String[] candidates = createFileLocationCandidates(context, config);
        MongoOperations operations = findMongoOperations(context, config);
        Class<?> mapping = findMapping(context, config);
        truncate(operations, config);
        if (mapping != Void.class) {
            String content = readJsonFile(candidates, context, config);
            Object executables = createExecutables(content, mapping);
            insert(operations, executables, config);
        }
    }

    /**
     * JSONファイルの検索場所候補を作成します。
     * この候補は、同一パッケージ内の、
     * <ol>
     *  <li>クラス名-メソッド名.json</li>
     *  <li>クラス名.json</li>
     *  <li>MongoInitaize.json</li>
     * </ol>
     * です。
     * @param context 現在のテスト実行コンテキスト
     * @param config コンフィグ
     * @return JSONファイルの検索場所候補の配列
     */
    protected String[] createFileLocationCandidates(TestContext context, MongoInitialize config) {
        if (config.file().equals("")) {
            // クラス名-メソッド名 ex)/jp/co/ctc_g/jfw/sample/Sample-method
            String clazzMethod = Strings.join(
                    "/",
                    context.getTestClass().getName().replace(".", "/"),
                    "-",
                    context.getTestMethod().getName());
            // クラス名 ex)/jp/co/ctc_g/jfw/sample/Sample
            String clazz = Strings.join(
                    "/",
                    context.getTestClass().getName().replace(".", "/"));
            // パッケージ ex)/jp/co/ctc_g/jfw/sample/DatabaseInitialize
            String pakkage = Strings.join(
                    "/",
                    context.getTestClass().getPackage().getName().replace(".", "/"),
                    "/",
                    MongoInitialize.class.getSimpleName());
            return Arrays.gen(clazzMethod, clazz, pakkage);
        } else {
            return Arrays.gen(config.file());
        }
    }

    /**
     * Mongoへ接続するためのクライアントを探します。
     * もし、{@link MongoInitialize#operationsRef()}にてクライアントのビーンIDが指定されていた場合、
     * そのクライアントを利用します。
     * 指定されたビーンIDがコンテナ内に存在しない場合、エラーID「E-MONGO-TEST#0001」で終了します。
     * 一方、ビーンIDが指定されていなかった場合は、
     * {@link MongoOperations}型に適合可能なインスタンスをDIコンテナ内から探します。
     * 現在のコンテナに見つからない場合、全ての親コンテナを対象に含めて検索します。
     * それでも見つからない場合、エラーID「E-MONGO-TEST#0002」を発生させて終了します。
     * @param context 現在のテスト実行コンテキスト
     * @param config コンフィグ
     * @return 検出されたデータソース
     */
    public MongoOperations findMongoOperations(TestContext context, MongoInitialize config) {
        String id = config.operationsRef();
        if ("".equals(id)) {
            String[] ids = context.getApplicationContext().getBeanNamesForType(MongoOperations.class);
            if (ids != null && ids.length > 0) {
                id = ids[0];
            } else {
                String[] included = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                        context.getApplicationContext(), MongoOperations.class);
                if (included != null && included.length > 0) {
                    id = included[0];
                }
                if ("".equals(id)) {
                    throw new InternalException(TestMongoKeeper.class, "E-MONGO-TEST#0001");
                }
            }
        }
        MongoOperations operations = null;
        try {
            operations = (MongoOperations) context.getApplicationContext().getBean(id);
        } catch (NoSuchBeanDefinitionException e) {
            Map<String, String> replace = Maps.hash("id", id);
            throw new InternalException(TestMongoKeeper.class, "E-MONGO-TEST#0002", replace, e);
        }
        return operations;
    }

    /**
     * JSONファイルのマッピングタイプを探します。
     * @param context 現在のテスト実行コンテキスト
     * @param config コンフィグ
     * @return JSONファイルのマッピング型
     */
    public Class<?> findMapping(TestContext context, MongoInitialize config) {
        Class<?> mapping = config.mapping();
        if (!config.truncate() && mapping == Void.class) {
            throw new InternalException(TestMongoKeeper.class, "E-MONGO-TEST#0003");
        }
        return mapping;
    }

    /**
     * 指定されたJSONファイル候補の中から適切なJSONファイルを特定し、
     * そのファイルのコンテンツを読みだします。
     * @param resolved JSONファイル検索場所候補
     * @param context 現在のテスト実行コンテキスト
     * @param config コンフィグ
     * @return 読みだしたJSON
     * @throws InternalException 想定外の文字コードの場合（E-MONGO-TEST#0004）
     * @throws InternalException ファイル読み込み時に入出力例外が発生した場合（E-MONGO-TEST#0005）
     */
    protected String readJsonFile(String[] resolved, TestContext context, MongoInitialize config) {
        String content = "";
        InputStream is = createInputStream(resolved);
        InputStreamReader isr = null;
        BufferedReader br = null;
        InternalException exception = null;
        try {
            isr = new InputStreamReader(is, config.charset());
            br = new BufferedReader(isr);
            StringBuilder sql = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sql.append(line).append(System.lineSeparator());
            }
            content = sql.toString();
        } catch (UnsupportedEncodingException e) {
            Map<String, String> replace = Maps.hash("charset", config.charset());
            exception = new InternalException(TestMongoKeeper.class, "E-MONGO-TEST#0004", replace, e);
        } catch (IOException e) {
            exception = new InternalException(TestMongoKeeper.class, "E-MONGO-TEST#0005", e);
        } finally {
            try {
                if (br != null) br.close();
                if (isr != null) isr.close();
                if (is != null) is.close();
            } catch (IOException e) {
                if (exception == null) {
                    exception = new InternalException(TestMongoKeeper.class, "E-MONGO-TEST#0005", e);
                }
            }
            if (exception != null) throw exception;
        }
        return content;
    }

    /**
     * 指定されたJSONファイル候補の中から、有効なファイルを特定してその入力ストリームを返却します。
     * @param resolved JSONファイル検索場所候補
     * @return 有効なファイルの入力ストリーム
     * @throws InternalException ファイルが見つからなかった場合（E-MONGO-TEST#0006）
     */
    protected InputStream createInputStream(String[] resolved) {
        InputStream file = null;
        for (String location : resolved) {
            String physical = location + ".json";
            InputStream is = getClass().getResourceAsStream(physical);
            if (is != null) {
                file = is;
                if (L.isInfoEnabled()) {
                    Map<String, String> replace = Maps.hash("file", physical);
                    L.info(Strings.substitute(R.getString("I-MONGO-TEST#0001"), replace));
                }
                break;
            }
        }
        if (file == null) {
            Map<String, String> replace = Maps.hash("searchPath", Strings.joinBy(", ", resolved));
            throw new InternalException(TestMongoKeeper.class, "E-MONGO-TEST#0006", replace);
        }
        return file;
    }

    /**
     * テストデータのJSON文字列をオブジェクトへマッピングし、MongoDBへ登録可能なデータを作成します。
     * @param content JSONファイルの文字列データ
     * @param mapping マッピング対象の型
     * @return 登録可能なデータ
     */
    protected Object createExecutables(String content, Class<?> mapping) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(content, mapping);
        } catch (JsonParseException e) {
            throw new InternalException(TestMongoKeeper.class, "E-MONGO-TEST#0007", e);
        } catch (JsonMappingException e) {
            throw new InternalException(TestMongoKeeper.class, "E-MONGO-TEST#0008", e);
        } catch (IOException e) {
            throw new InternalException(TestMongoKeeper.class, "E-MONGO-TEST#0009", e);
        }
    }

    /**
     * Collection名を検索します。
     * @param config {@link MongoInitialize}
     * @return Collection名
     */
    protected String findCollectionName(MongoInitialize config) {
        String collectionName = config.collectionName();
        if ("".equals(collectionName)) {
            throw new InternalException(TestMongoKeeper.class, "E-MONGO-TEST#0010");
        }
        return collectionName;
    }

    /**
     * {@link MongoInitialize#truncate()}で<code>true</code>が指定されたときにMongoDBより指定されたCollectionをすべて削除します。
     * @param operations {@link MongoOperations}
     * @param config {@link MongoInitialize}
     */
    protected void truncate(MongoOperations operations, MongoInitialize config) {
        if (config.truncate()) {
            L.info(Strings.substitute(R.getString("I-MONGO-TEST#0002"), Maps.hash("collectionName", config.collectionName())));
            operations.dropCollection(findCollectionName(config));
        }
    }

    /**
     * テストデータをMongoDBに登録します。
     * @param operations {@link MongoOperations}
     * @param executables 登録するJSONデータ
     * @param config {@link MongoInitialize}
     */
    protected void insert(MongoOperations operations, Object executables, MongoInitialize config) {
        L.info(Strings.substitute(R.getString("I-MONGO-TEST#0003"), Maps.hash("value", executables.toString())));
        if (executables.getClass().isArray()) {
            List<Object> gen = Lists.gen((Object[])executables);
            operations.insert(gen, findCollectionName(config));
        } else {
            operations.insert(executables, findCollectionName(config));
        }
    }
}
