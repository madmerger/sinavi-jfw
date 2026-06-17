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

import jakarta.validation.Validator;

import jp.co.ctc_g.jfw.core.internal.Config;
import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jse.core.csv.internal.CSVInternals;

/**
 * <p>
 * このクラスはCSV読込・書込ユーティリティの設定情報を保持するインスタンスを生成するユーティリティです。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public final class CSVConfigs {

    /**
     * CSVファイルのファイルエンコードを保持します。
     * デフォルト：MS932
     * 
     * この設定を一括で変更する場合は、FrameworkResources.propertiesに
     * 以下のキーで値を設定してください。
     * <pre class="brush:java">
     * jp.co.ctc_g.jse.core.csv.CSVConfigs.file_encode
     * </pre>
     */
    public static final String FILE_ENCODE;

    /**
     * CSVの改行コードを保持します。
     * デフォルト：CRLF(\r\n)
     * 
     * この設定を一括で変更する場合は、FrameworkResources.propertiesに
     * 以下のキーで値を設定してください。
     * <pre class="brush:java">
     * jp.co.ctc_g.jse.core.csv.CSVConfigs.line_separator
     * </pre>
     */
    public static final String LINE_SEPARATOR;

    /**
     * CSVファイルの区切り文字を保持します。
     * デフォルト：,
     * 
     * この設定を一括で変更する場合は、FrameworkResources.propertiesに
     * 以下のキーで値を設定してください。
     * <pre class="brush:java">
     * jp.co.ctc_g.jse.core.csv.CSVConfigs.separator_char
     * </pre>
     */
    public static final char SEPARATOR_CHARACTER;

    /**
     * CSVファイルの囲み文字を保持します。
     * デフォルト："
     * 
     * この設定を一括で変更する場合は、FrameworkResources.propertiesに
     * 以下のキーで値を設定してください。
     * <pre class="brush:java">
     * jp.co.ctc_g.jse.core.csv.CSVConfigs.quote_char
     * </pre>
     */
    public static final char QUOTE_CHARACTER;

    /**
     * CSV出力時のエスケープ文字を保持します。
     * デフォルト：\\
     * 
     * この設定を一括で変更する場合は、FrameworkResources.propertiesに
     * 以下のキーで値を設定してください。
     * <pre class="brush:java">
     * jp.co.ctc_g.jse.core.csv.CSVConfigs.escape_char
     * </pre>
     */
    public static final char ESCAPE_CHARACTER;

    /**
     * CSVファイルの読み込み行のスキップする行数を保持します。
     * デフォルト：0(0の場合スキップしません。)
     * 
     * この設定を一括で変更する場合は、FrameworkResources.propertiesに
     * 以下のキーで値を設定してください。
     * <pre class="brush:java">
     * jp.co.ctc_g.jse.core.csv.CSVConfigs.skip_lines
     * </pre>
     */
    public static final Integer SKIP_LINES;

    /**
     * 囲み文字以外の文字は無視するかどうかを保持します。
     * デフォルト：false
     * 
     * この設定を一括で変更する場合は、FrameworkResources.propertiesに
     * 以下のキーで値を設定してください。
     * <pre class="brush:java">
     * jp.co.ctc_g.jse.core.csv.CSVConfigs.strict_quotes
     * </pre>
     */
    public static final boolean STRICT_QUOTES;

    /**
     * 囲み文字の前に空白を無視するかどうかを保持します。
     * デフォルト：true
     * 
     * この設定を一括で変更する場合は、FrameworkResources.propertiesに
     * 以下のキーで値を設定してください。
     * <pre class="brush:java">
     * jp.co.ctc_g.jse.core.csv.CSVConfigs.ignore_leading_whitespace
     * </pre>
     */
    public static final boolean IGNORE_LEADING_WHITESPACE;

    /**
     * 1行の長さをチェックするかどうかを保持します。
     * デフォルト：false
     * 
     * この設定を一括で変更する場合は、FrameworkResources.propertiesに
     * 以下のキーで値を設定してください。
     * <pre class="brush:java">
     * jp.co.ctc_g.jse.core.csv.CSVConfigs.length_check
     * </pre>
     */
    public static final boolean LENGTH_CHECK;

    /**
     * CSVファイルの一時ディレクトリを指定します。
     * デフォルト：/tmp/csv
     * 
     * この設定を一括で変更する場合は、FrameworkResources.propertiesに
     * 以下のキーで値を設定してください。
     * <pre class="brush:java">
     * jp.co.ctc_g.jse.core.csv.CSVConfigs.temp_directory
     * </pre>
     */
    public static final String TEMP_DIR;
    
    /**
     * エラー発生時のメッセージテンプレート
     * デフォルト：${index}番目：${path}は${msg}
     * 
     * ${index}と${path}と${msg}は変更することができません。
     * 
     * この設定を一括で変更する場合は、FrameworkResources.propertiesに
     * 以下のキーで値を設定してください。
     * <pre class="brush:java">
     * jp.co.ctc_g.jse.core.csv.CSVConfigs.message_template
     * </pre>
     */
    public static final String MESSAGE_TEMPLATE;
    
    static {
        Config c = CSVInternals.getConfig(CSVConfigs.class);
        FILE_ENCODE = c.find("file_encode");
        LINE_SEPARATOR = c.find("line_separator");
        SEPARATOR_CHARACTER = c.find("separator_char").toCharArray()[0];
        QUOTE_CHARACTER = c.find("quote_char").toCharArray()[0];
        ESCAPE_CHARACTER = c.find("escape_char").toCharArray()[0];
        SKIP_LINES = Integer.valueOf(c.find("skip_lines"));
        STRICT_QUOTES = Boolean.valueOf(c.find("strict_quotes"));
        IGNORE_LEADING_WHITESPACE = Boolean.valueOf(c.find("ignore_leading_whitespace"));
        LENGTH_CHECK = Boolean.valueOf(c.find("length_check"));
        TEMP_DIR = c.find("temp_directory");
        MESSAGE_TEMPLATE = c.find("message_template");
    }

    /**
     * コンストラクタです。
     * インスタンスの生成を抑止します。
     */
    private CSVConfigs() {}

    /**
     * CSVファイルの読込・書込時の設定情報を保持するクラスです。
     * @return CSVファイルの読込・書込時の設定情報
     */
    public static CSVConfig config() {
        return new CSVConfig();
    }

    /**
     * <p>
     * このクラスはCSV読込・書込ユーティリティの設定情報を保持します。
     * </p>
     * @author ITOCHU Techno-Solutions Corporation.
     */
    public static class CSVConfig {

        /**
         * CSVファイルのファイルエンコード
         * デフォルト：MS932
         */
        private String encode = FILE_ENCODE;

        /**
         * CSVの改行コード
         * デフォルト：CRLF(\r\n)
         */
        private String line = LINE_SEPARATOR;

        /**
         * CSVの区切り文字
         * デフォルト：カンマ(,)
         */
        private char separator = SEPARATOR_CHARACTER;

        /**
         * CSVの囲み文字
         * デフォルト：ダブルクォテーション(")
         */
        private char quote = QUOTE_CHARACTER;

        /**
         * CSVのエスケープ文字
         * デフォルト：バックスラッシュ(\)
         */
        private char escape = ESCAPE_CHARACTER;

        /**
         * CSVファイルの読込を開始する行数
         * デフォルト：0
         */
        private int index = SKIP_LINES;

        /**
         * 囲み文字以外の文字は無視するかどうか
         * デフォルト：false
         */
        private boolean strictQuotes = STRICT_QUOTES;

        /**
         * 囲み文字の前に空白を無視するかどうか
         * デフォルト：true
         */
        private boolean whitespace = IGNORE_LEADING_WHITESPACE;

        /**
         * 1行の長さをチェックするオプションです。
         * trueが設定されている場合は読み込んだ1行の文字列配列の長さをチェックします。
         * 長さが一致しない場合は区切り文字が片方落ちている可能性があるため、InternalExceptionをスローすることが可能になります。
         * デフォルト：false
         */
        private boolean check = LENGTH_CHECK;

        /**
         * 長さチェックがtrueの場合にチェック対象となる長さを指定します。
         * デフォルト：-1
         */
        private int length = -1;

        /**
         * CSVファイルの一時ファイルを作成するディレクトリです。
         * デフォルト：/tmp/csv
         */
        private String tempDir = TEMP_DIR;
        
        /**
         * BeanValidationの実行インタフェースです。
         */
        private Validator validator;
        
        /**
         * エラー発生時のメッセージテンプレートです。
         */
        private String template = MESSAGE_TEMPLATE;

        /**
         * デフォルトコンストラクタです。
         */
        public CSVConfig() {}

        /**
         * 区切り文字を取得します。
         * @return 区切り文字
         */
        public char separator() {
            return separator;
        }

        /**
         * 区切り文字を設定します。
         * @param s 区切り文字
         * @return このクラス
         */
        public CSVConfig separator(char s) {
            separator = s;
            return this;
        }

        /**
         * 囲み文字を取得します。
         * @return 囲み文字
         */
        public char quote() {
            return quote;
        }

        /**
         * 囲み文字を設定します。
         * @param q 囲み文字
         * @return このクラス
         */
        public CSVConfig quote(char q) {
            quote = q;
            return this;
        }

        /**
         * エスケープ文字を取得します。
         * @return エスケープ文字
         */
        public char escape() {
            return escape;
        }

        /**
         * エスケープ文字を設定します。
         * @param e エスケープ文字
         * @return このクラス
         */
        public CSVConfig escape(char e) {
            escape = e;
            return this;
        }

        /**
         * 読込開始行を取得します。
         * @return 読込開始行
         */
        public int index() {
            return index;
        }

        /**
         * 読込開始行を設定します。
         * @param i 読込開始行
         * @return このクラス
         */
        public CSVConfig index(int i) {
            index = i;
            return this;
        }

        /**
         * 囲み文字以外の文字は無視するかどうかを取得します。
         * @return 囲み文字以外の文字は無視するかどうか
         */
        public boolean strictQuotes() {
            return strictQuotes;
        }

        /**
         * 囲み文字以外の文字は無視するかどうかを設定します。
         * @param s 囲み文字以外の文字は無視するかどうか
         * @return このクラス
         */
        public CSVConfig strictQuotes(boolean s) {
            strictQuotes = s;
            return this;
        }

        /**
         * 囲み文字の前に空白を無視するかどうかを取得します。
         * @return 囲み文字の前に空白を無視するかどうか
         */
        public boolean whitespace() {
            return whitespace;
        }

        /**
         * 囲み文字の前に空白を無視するかどうかを設定します。
         * @param w 囲み文字の前に空白を無視するかどうか
         * @return このクラス
         */
        public CSVConfig whitespace(boolean w) {
            whitespace = w;
            return this;
        }

        /**
         * 1行のデータの配列の長さをチェックするかどうかを取得します。
         * @return 1行のデータの配列の長さをチェックするかどうか
         */
        public boolean check() {
            return check;
        }

        /**
         * 1行のデータの配列の長さをチェックするかどうかを設定します。
         * @param c 1行のデータの配列の長さをチェックするかどうか
         * @return このクラス
         */
        public CSVConfig check(boolean c) {
            check = c;
            return this;
        }

        /**
         * 配列の長さを指定します。
         * @return 配列の長さ
         */
        public int length() {
            if (!check) return length;
            if (length == -1) { 
                throw new InternalException(CSVConfigs.class, "E-CSV#0007");
            }
            return length;
        }

        /**
         * 配列の長さを指定します。
         * @param l 配列の長さ
         * @return このクラス
         */
        public CSVConfig length(int l) {
            length = l;
            return this;
        }

        /**
         * ファイルエンコードを取得します。
         * @return ファイルエンコード
         */
        public String encode() {
            return encode;
        }

        /**
         * ファイルエンコードを設定します。
         * @param e ファイルエンコード
         * @return このクラス
         */
        public CSVConfig encode(String e) {
            encode = e;
            return this;
        }

        /**
         * 改行コードを取得します。
         * @return 改行コード
         */
        public String line() {
            return line;
        }

        /**
         * 改行コードを設定します。
         * @param l 改行コード
         * @return このクラス
         */
        public CSVConfig line(String l) {
            line = l;
            return this;
        }

        /**
         * 一時ディレクトリを取得します。
         * @return 一時ディレクトリ
         */
        public String tempDir() {
            return tempDir;
        }

        /**
         * 一時ディレクトリを設定します。
         * @param t 一時ディレクトリ
         * @return このクラス
         */
        public CSVConfig tempDir(String t) {
            tempDir = t;
            return this;
        }

        /**
         * バリデーションを実行するときに{@link Validator}を設定します。
         * @param v {@link Validator}
         * @return このクラス
         */
        public CSVConfig validator(Validator v) {
            validator = v;
            return this;
        }
        
        /**
         * {@link Validator}を取得します。
         * @return {@link Validator}
         */
        public Validator validator() {
            return this.validator;
        }
        
        /**
         * エラー発生時のメッセージテンプレートを取得します。
         * @return メッセージテンプレート
         */
        public String template() {
            return this.template;
        }
        
        /**
         * エラー発生時のメッセージテンプレートを設定します。
         * @param t メッセージテンプレート
         * @return このクラス
         */
        public CSVConfig template(String t) {
            template = t;
            return this;
        }

    }

}
