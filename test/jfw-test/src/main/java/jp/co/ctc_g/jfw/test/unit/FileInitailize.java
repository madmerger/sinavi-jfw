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

package jp.co.ctc_g.jfw.test.unit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * このクラスは、テスト時にファイルの内容を確認する際に指定されたファイルの内容をコピーします。
 * </p>
 * <p>
 * 読込対象のファイルはこのクラスのインスタンス生成時に指定されたクラスのクラスパスよりロードします。
 * JUnit 5では{@code @TempDir}と組み合わせて使用してください。
 * </p>
 * <pre class="brush:java">
 * public class FooServiceTest {
 *   &#64;TempDir
 *   Path tempDir;
 *
 *   FileInitailize file = new FileInitailize(FooService.class);
 *
 *   &#64;Test
 *   public void test() {
 *     File f = file.copy("test.csv", tempDir, "temp.csv");
 *     String content = Files.readString(f.toPath());
 *     assertThat(content, is(file.load("test.csv")));
 *   }
 * }
 * </pre>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class FileInitailize {

    private static final Logger L = LoggerFactory.getLogger(FileInitailize.class);
    private static final ResourceBundle R = InternalMessages.getBundle(FileInitailize.class);
    private static final String DEFAULT_FILE_ENCODE = "MS932";
    private static final String URL_ENCODE = "UTF-8";
    private Class<?> target;
    private String encode = DEFAULT_FILE_ENCODE;
    private Path tempDir;

    /**
     * デフォルトコンストラクタです。
     */
    public FileInitailize() {}

    /**
     * コンストラクタです。
     * @param target 読込ファイルパスのターゲットとなるクラス
     */
    public FileInitailize(Class<?> target) {
        this.target = target;
    }

    /**
     * コンストラクタです。
     * @param target 読込ファイルパスのターゲットとなるクラス
     * @param encode ファイルエンコード
     */
    public FileInitailize(Class<?> target, String encode) {
        this(target);
        this.encode = encode;
    }

    /**
     * 一時ディレクトリを設定します。
     * @param tempDir 一時ディレクトリ
     */
    public void setTempDir(Path tempDir) {
        this.tempDir = tempDir;
    }

    /**
     * 指定されたファイルのデータを指定されたファイルへコピーする処理です。
     * @param src コピー元ファイル名
     * @param dest コピー先ファイル名
     * @return ファイルのインスタンス
     */
    public File copy(String src, String dest) {
        L.debug(Strings.substitute(R.getString("D-TEST#0002"), Maps.hash("src", src).map("dest", dest)));
        InputStream is = null;
        File destFile = null;
        InternalException exception = null;
        try {
            is = target.getResourceAsStream(URLDecoder.decode(src, URL_ENCODE));
            String contents = IOUtils.toString(is, encode);
            if (tempDir == null) {
                tempDir = Files.createTempDirectory("jfw-test");
            }
            destFile = tempDir.resolve(dest).toFile();
            com.google.common.io.Files.write(contents, destFile, Charset.forName(encode));
        } catch (IOException e) {
            exception = new InternalException(FileInitailize.class, "E-TEST#0018");
        } finally {
            try {
                if (is != null) is.close();
            } catch(IOException e) {
                if (exception == null) {
                    exception = new InternalException(FileInitailize.class, "E-TEST#0019");
                }
            }
            if (exception != null) throw exception;
        }
        return destFile;
    }

    /**
     * ファイルのデータをロードします。
     * @param src ファイルパス
     * @return ファイルデータ
     */
    public String load(String src) {
        L.debug(Strings.substitute(R.getString("D-TEST#0003"), Maps.hash("src", src)));
        InputStream is = null;
        String content = "";
        InternalException exception = null;
        try {
            is = target.getResourceAsStream(URLDecoder.decode(src, URL_ENCODE));
            content = IOUtils.toString(is, encode);
        } catch (IOException e) {
            exception = new InternalException(FileInitailize.class, "E-TEST#0020");
        } finally {
            try {
                if (is != null) is.close();
            } catch(IOException e) {
                if (exception == null) {
                    exception = new InternalException(FileInitailize.class, "E-TEST#0019");
                }
            }
            if (exception != null) throw exception;
        }
        return content;
    }

    /**
     * 一時ディレクトリのルートを返します。
     * @return 一時ディレクトリのルートファイル
     */
    public File getRoot() {
        if (tempDir == null) {
            try {
                tempDir = Files.createTempDirectory("jfw-test");
            } catch (IOException e) {
                throw new InternalException(FileInitailize.class, "E-TEST#0018");
            }
        }
        return tempDir.toFile();
    }

    /**
     * 一時ディレクトリを削除します。
     */
    public void delete() {
        if (tempDir != null) {
            try {
                org.apache.commons.io.FileUtils.deleteDirectory(tempDir.toFile());
            } catch (IOException e) {
                L.warn("Failed to delete temp directory: " + tempDir, e);
            }
            tempDir = null;
        }
    }

}
