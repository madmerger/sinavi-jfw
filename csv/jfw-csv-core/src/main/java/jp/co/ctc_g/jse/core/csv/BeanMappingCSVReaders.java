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

package jp.co.ctc_g.jse.core.csv;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jakarta.validation.ConstraintViolation;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Reflects;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.csv.CSVConfigs.CSVConfig;
import com.opencsv.bean.ColumnPositionMappingStrategy;

/**
 * <p>
 * このクラスは、CSVファイルの1レコードとJavaBeansのプロパティとの
 * 単純なマッピングをするときに利用するCSV読込ユーティリティです。
 * </p>
 * <p>
 * 次のように利用します。
 * まず、{@link CSVs}よりCSV読込ユーティリティのインスタンスを生成します。
 * 次に{@link #open()}で CSV読込ユーティリティのストリームをオープンし、
 * {@link #next()}でCSVファイルよりレコードを1行読み込みます。
 * そして、{@link #parse()}でJavaBeansにマッピングされたデータを取得します。
 * これをレコード数繰り返します。
 * この{@link #parse()}では
 * <ol>
 *  <li>レコードの配列の長さチェック</li>
 *  <li>データのマッピング</li>
 *  <li>処理カウントをインクリメント</li>
 *  <li>BeanValidationが設定されているときはバリデーションを実行</li>
 *  <li>レコードの変換処理：デフォルトではなにもしません。</li>
 * </ol>
 * を行います。
 * 必ず最後にストリームを{@link #close()}してください。
 * 以下に実装例を示します。
 * <pre class="brush:java">
 * BeanMappingCSVReaders&lt;UserProfile&gt; readers = null;
 * try {
 *   readers = CSVs.readers(file, CSVConfigs.config().index(1), UserProfile.class);
 *   readers.open();
 *   while (readers.next().hasNext()) {
 *     UserProfile profile = readers.parse();
 *   }
 * } finally {
 *   if (readers != null) {
 *     readers.close();
 *   }
 * }
 * </pre>
 * </p>
 * <p>
 * CSVの1レコードとJavaBeansのプロパティとのマッピングには
 * {@link CSVColumn}で指定された順番を基にマッピングします。
 * この注釈を省略することも可能ですが、
 * 省略したときはJavaBeansのプロパティの定義順にマッピングすることになります。
 * なるべくマッピングの順番は注釈を用いて指定する方法で実装してください。
 * </p>
 * <p>
 * BeanValidationを実行するときは{@link CSVConfig#validator(javax.validation.Validator)}を用いて、
 * BeanValidationの実行クラスを設定してください。
 * また、エラーメッセージのテンプレートを指定することも可能です。
 * 本クラスを用いてバリデーションを実行するときの実装例は次の通りです。
 * <pre class="brush:java">
 *  &#64;Autowired
 *  private Validator validator;
 *  
 *  public void create(String file) {
 *   BeanMappingCSVReaders&lt;UserProfile&gt; readers = null;
 *   try {
 *    readers = CSVs.readers(file, CSVConfigs.config().index(1).validator(validator).template("${index}:${path}は${msg}"), UserProfile.class);
 *    readers.open();
 *    while (readers.next().hasNext()) {
 *    UserProfile profile = readers.parse();
 *    if (!readers.hasError()) {
 *    }
 *   }
 *   if (readers.hasError()) { 
 *    throw new BindException("W-CSV#001", readers.getBindError());
 *   }
 *  } finally {
 *   if (readers != null) {
 *    readers.close();
 *   }
 *  }
 * }
 * </pre>
 * </p>
 * @see CSVColumn
 * @see CSVConfigs
 * @see CSVConfig
 * @see CSVReaders
 * @param <T> 読込対象の型
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class BeanMappingCSVReaders<T> extends CSVReaders {
    
    protected CSVToBeanMapping<T> parser;

    protected ColumnPositionMappingStrategy<T> strategy;

    protected T domain;

    private Class<T> type;

    private String[] mapping;

    private List<BindError> bindErrors;

    /**
     * コンストラクタです。
     * @param file 読込対象のファイル
     */
    protected BeanMappingCSVReaders(File file) {
        super(file);
    }

    /**
     * コンストラクタです。
     * @param file 読込対象のファイル
     * @param config ユーティリティの設定情報
     */
    protected BeanMappingCSVReaders(File file, CSVConfig config) {
        super(file, config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] get() {
        throw new InternalException(BeanMappingCSVReaders.class, "E-CSV#0013");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        super.clear();
        domain = null;
    }

    /**
     * CSVファイルからデータを1行読み込み、データをオブジェクトへ設定します。
     * <ol>
     * <li>データ読込後に変換処理を実施(デフォルトでは行いません。)</li>
     * <li>読込データの配列の長さをチェック</li>
     * <li>データを設定</li>
     * <li>処理カウンタをインクリメント</li>
     * </ol>
     * @return 1レコードのデータ
     */
    public T parse() {
        Args.checkNotNull(type, R.getString("E-CSV#0014"));
        Args.checkNotNull(mapping, R.getString("E-CSV#0015"));
        if (strategy == null && parser == null) {
            create();
        }
        check();
        domain = parser.parse(strategy, csv, line);
        up();
        validate();
        T d = domain;
        clear();
        return d;
    }

    /**
     * データをマッピングする型を設定します。
     * 型を設定した後に必ず{@link BeanMappingCSVReaders#resolve()}を実行し、
     * マッピング対象のプロパティを設定してください。
     * また、手動で指定する場合は{@link BeanMappingCSVReaders#mapping(String[])}を利用して下さい。
     * @param t マッピングする型
     * @return ユーティリティ
     */
    public BeanMappingCSVReaders<T> type(Class<T> t) {
        this.type = t;
        return this;
    }

    /**
     * データをオブジェクトへマッピングするプロパティ名を設定します。
     * @param m プロパティ名
     * @return ユーティリティ
     */
    public BeanMappingCSVReaders<T> mapping(String[] m) {
        this.mapping = m;
        return this;
    }

    /**
     * バリデーションを実行します。
     */
    @Override
    protected void validate() {
        if (this.config.validator() == null) return;
        if (this.config.template() == null) return;
        Set<ConstraintViolation<T>> errors = this.config.validator().validate(domain);
        if (!errors.isEmpty()) {
            for (ConstraintViolation<T> error : errors) {
                bindErrors.add(new BindError(error.getPropertyPath().toString(),
                    Strings.substitute(this.config.template(), Maps.hash("index", Integer.toString(count()))
                        .map("path", error.getPropertyPath().toString())
                        .map("msg", error.getMessage()))));
            }
        }
    }

    /**
     * バリデーションエラーの情報を取得します。
     * @return {@link BindError}のリスト
     */
    public List<BindError> getBindError() {
        return bindErrors;
    }

    /**
     * バリデーションエラーが1レコードでも存在するかどうかを返します。
     * @return true: バリデーションエラーあり、false:バリデーションエラーなし
     */
    public boolean hasError() {
        return !this.bindErrors.isEmpty();
    }

    /**
     * マッピング対象のプロパティ名をクラスの型より解決します。
     * 指定されたクラスに{@link CSVColumn}アノテーションが付与されている場合は
     * アノテーションによってCSVカラムの順番にプロパティ名を設定します。
     * 指定されていない場合はプロパティの定義順とします。
     * @return ユーティリティ
     */
    public BeanMappingCSVReaders<T> resolve() {
        Args.checkNotNull(type, R.getString("E-CSV#0015"));
        Field[] fields = Reflects.findAllFields(type);
        Map<Double, String> map = new TreeMap<Double, String>();
        List<String> properties = new ArrayList<String>();
        for (Field f : fields) {
            CSVColumn c = f.getAnnotation(CSVColumn.class);
            if (c != null) {
                map.put(Double.valueOf(c.seq()), f.getName());
            } else {
                properties.add(f.getName());
            }
        }
        String[] m = null;
        if (map.isEmpty()) {
            m = properties.toArray(new String[0]);
        } else {
            m = map.values().toArray(new String[0]);
        }
        this.mapping = m;
        this.bindErrors = new ArrayList<BindError>();
        return this;
    }

    private void create() {
        this.strategy = new ColumnPositionMappingStrategy<T>();
        this.strategy.setType(type);
        this.strategy.setColumnMapping(mapping);
        this.parser = new CSVToBeanMapping<T>();
    }

}
