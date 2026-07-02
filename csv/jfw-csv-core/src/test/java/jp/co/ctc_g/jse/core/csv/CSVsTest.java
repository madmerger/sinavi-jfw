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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.util.Dates;
import jp.co.ctc_g.jfw.test.unit.FileInitailize;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;

@RunWith(Enclosed.class)
public class CSVsTest {

    public static class CSVReadersTest {

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Rule
        public FileInitailize file = new FileInitailize(CSVReadersTest.class);

        private CSVReaders readers;

        @After
        public void teardown() {
            if (readers != null) {
                readers.close();
            }
            file.delete();
        }

        @Test
        public void OPENしていなければ読み込みできない() {
            thrown.expect(InternalException.class);
            thrown.expectMessage("ストリームがオープンされていない状態での読込操作は許可されていません。");
            CSVs.readers(new File("temp.csv")).next();
        }

        @Test
        public void ファイルが見つからなければ例外がスローされる() {
            thrown.expect(InternalException.class);
            thrown.expectMessage("指定されたファイルが見つかりません。");
            CSVs.readers("temp.csv").open();
        }

        @Test
        public void サポートされないファイルエンコードが指定された場合は例外がスローされる() throws Exception {
            thrown.expect(InternalException.class);
            thrown.expectMessage("指定されたエンコードがサポートされていません。");
            File f = file.copy("CSVsTest-デフォルト設定で読み込める.csv", "temp.csv");
            readers = CSVs.readers(f, CSVConfigs.config().encode("unsupported")).open();
        }

        @Test
        public void デフォルト設定で読み込める() throws Exception {
            File f = file.copy("CSVsTest-デフォルト設定で読み込める.csv", "temp.csv");
            readers = CSVs.readers(f).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(6));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("5000"));
            assertThat(values[2], is("2013年1月"));
            assertThat(values[3], is("2013/12/12"));
            assertThat(values[4], is("2013/12"));
            assertThat(values[5], is("5000"));
        }

        @Test
        public void ファイルパスの文字列で読み込める() throws Exception {
            File f = file.copy("CSVsTest-デフォルト設定で読み込める.csv", "temp.csv");
            readers = CSVs.readers(f.getPath()).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(6));
        }

        @Test
        public void エスケープ文字を指定して読み込める() throws Exception {
            File f = file.copy("CSVsTest-デフォルト設定で読み込める.csv", "temp.csv");
            readers = CSVs.readers(f, CSVConfigs.config().escape('\'')).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(6));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("5000"));
            assertThat(values[2], is("2013年1月"));
            assertThat(values[3], is("2013/12/12"));
            assertThat(values[4], is("2013/12"));
            assertThat(values[5], is("\\5000"));// 金額表記がエスケープされる
        }

        @Test
        public void 区切り文字が入り混じっていても正常に読み込める() throws Exception {
            File f = file.copy("CSVsTest-区切り文字が入り混じっていても正常に読み込める.csv", "temp.csv");
            readers = CSVs.readers(f.getPath(), CSVConfigs.config().escape('\'')).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(6));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("5000"));
            assertThat(values[2], is("2013年1月"));
            assertThat(values[3], is("2013/12/12"));
            assertThat(values[4], is("2013/12"));
            assertThat(values[5], is("\\5000"));// 金額表記がエスケープされる
        }

        @Test
        public void 区切り文字がペアでない場合は例外が発生する() throws Exception {
            thrown.expect(InternalException.class);
            thrown.expectMessage("指定された長さと読込行の長さが一致しません。");
            File f = file.copy("CSVsTest-区切り文字がペアでない場合.csv", "temp.csv");
            readers = CSVs.readers(f, CSVConfigs.config().check(true).length(6)).open();
            readers.next().get();
        }

        @Test
        public void 区切り文字がペアでない場合でも読み込める() throws Exception {
            File f = file.copy("CSVsTest-区切り文字がペアでない場合.csv", "temp.csv");
            readers = CSVs.readers(f.getPath()).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(3));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("5000"));
            assertThat(values[2], is("2013年1月"));
        }

        @Test
        public void 指定した区切り文字ではない場合は文字列として読み込める() throws Exception {
            File f = file.copy("CSVsTest-指定した区切り文字ではない場合.csv", "temp.csv");
            readers = CSVs.readers(f).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(1));
            assertThat(values[0], is("いろは\"\t\"にほへと"));
        }

        @Test
        public void データの途中に改行コードがある場合はLFに統一されて読み込める() throws Exception {
            File f = file.copy("CSVsTest-データの途中に改行コードがある場合.csv", "temp.csv");
            readers = CSVs.readers(f).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(2));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("※いろはに\nほへと"));

            values = readers.next().get();
            assertThat(readers.count(), is(2));
            assertThat(values.length, is(2));
            assertThat(values[0], is("2"));
            assertThat(values[1], is("※いろはに\nほへと。\nあいうえお\"12345\"かきくけこ"));
        }

        @Test
        public void ヘッダも読み込める() throws Exception {
            File f = file.copy("CSVsTest-ヘッダ.csv", "temp.csv");
            readers = CSVs.readers(f).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(2));
            assertThat(values[0], is("key"));
            assertThat(values[1], is("value"));

            values = readers.next().get();
            assertThat(readers.count(), is(2));
            assertThat(values.length, is(2));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("5000"));
        }

        @Test
        public void ヘッダを飛ばして読み込める() throws Exception {
            File f = file.copy("CSVsTest-ヘッダ.csv", "temp.csv");
            readers = CSVs.readers(f, CSVConfigs.config().index(1)).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(2));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("5000"));
        }

        @Test
        public void 値が設定されていない場合も読み込める() throws Exception {
            File f = file.copy("CSVsTest-値が設定されていない場合.csv", "temp.csv");
            readers = CSVs.readers(f, CSVConfigs.config()).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(2));
            assertThat(values[0], is("1"));
            assertThat(values[1], is(""));

            values = readers.next().get();
            assertThat(readers.count(), is(2));
            assertThat(values.length, is(2));
            assertThat(values[0], is("2"));
            assertThat(values[1], is(" "));

            values = readers.next().get();
            assertThat(readers.count(), is(3));
            assertThat(values.length, is(2));
            assertThat(values[0], is("3"));
            assertThat(values[1], is(""));
        }

        @Test
        public void 特殊文字が含まれる場合も読み込める() throws Exception {
            File f = file.copy("CSVsTest-特殊文字が含まれる場合.csv", "temp.csv");
            readers = CSVs.readers(f, CSVConfigs.config()).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(4));
            assertThat(values[0], is(" "));
            assertThat(values[1], is("<,>"));
            assertThat(values[2], is("<\t>"));
            assertThat(values[3], is("<t>"));

            values = readers.next().get();
            assertThat(readers.count(), is(2));
            assertThat(values.length, is(3));
            assertThat(values[0], is("<'>"));
            assertThat(values[1], is("<\">"));
            assertThat(values[2], is("<\n>"));

            readers.next();
            assertThat(readers.hasNext(), is(false));
        }

        @Test
        public void コンフィグをオーバライドできる() throws Exception {
            File f = file.copy("CSVsTest-コンフィグをオーバライドできる.csv", "temp.csv");
            readers = CSVs.readers(f).open().config(CSVConfigs.config().check(true).length(2));
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(2));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("name"));

            values = readers.config(CSVConfigs.config().check(true).length(3)).next().get();
            assertThat(readers.count(), is(2));
            assertThat(values.length, is(3));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("1"));
            assertThat(values[2], is("dept"));

            readers.next();
            assertThat(readers.hasNext(), is(false));
        }

        @Test
        public void 改行コードが含まれる場合() throws Exception {
            File f = file.copy("CSVsTest-改行コードのみの行がある場合.csv", "temp.csv");
            readers = CSVs.readers(f).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(2));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("name"));

            values = readers.next().get();
            assertThat(readers.count(), is(2));
            assertThat(values.length, is(1));
            assertThat(values[0], is(""));

            values = readers.next().get();
            assertThat(readers.count(), is(3));
            assertThat(values.length, is(3));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("1"));
            assertThat(values[2], is("dept"));
        }

        @Test
        public void 区切り文字を変更して読み込める() throws Exception {
            File f = file.copy("CSVsTest-区切り文字変更.tsv", "temp.tsv");
            readers = CSVs.readers(f).config(CSVConfigs.config().separator('\t')).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(6));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("5000"));
            assertThat(values[2], is("2013年1月"));
            assertThat(values[3], is("2013/12/12"));
            assertThat(values[4], is("2013/12"));
            assertThat(values[5], is("5000"));
        }

        @Test
        public void 囲み文字を変更して読み込める() throws Exception {
            File f = file.copy("CSVsTest-囲み文字変更.csv", "temp.csv");
            readers = CSVs.readers(f).config(CSVConfigs.config().quote('\'')).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(6));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("5000"));
            assertThat(values[2], is("2013年1月"));
            assertThat(values[3], is("2013/12/12"));
            assertThat(values[4], is("2013/12"));
            assertThat(values[5], is("5000"));
        }

        @Test
        public void データの中の囲み文字の前に空白がある場合は取り除く() throws Exception {
            File f = file.copy("CSVsTest-囲み文字の前に空白.csv", "temp.csv");
            readers = CSVs.readers(f).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(2));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("5000\"12345"));
        }

        @Test
        public void データの中の囲み文字を取り除く() throws Exception {
            File f = file.copy("CSVsTest-囲み文字.csv", "temp.csv");
            readers = CSVs.readers(f).config(CSVConfigs.config().strictQuotes(true)).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(2));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("1234512345"));
        }

        @Test
        public void 囲み文字の前に空白がある場合に空白を取り除かない() throws Exception {
            File f = file.copy("CSVsTest-囲み文字の前に空白.csv", "temp.csv");
            readers = CSVs.readers(f).config(CSVConfigs.config().whitespace(false)).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(2));
            assertThat(values[0], is("1"));
            assertThat(values[1], is(" \"5000\"12345"));
        }

        @Test
        public void データの中の囲み文字を取り除かない() throws Exception {
            File f = file.copy("CSVsTest-囲み文字.csv", "temp.csv");
            readers = CSVs.readers(f).config(CSVConfigs.config().strictQuotes(false)).open();
            String[] values = readers.next().get();
            assertThat(readers.count(), is(1));
            assertThat(values.length, is(2));
            assertThat(values[0], is("1"));
            assertThat(values[1], is("12345\"5000\"12345"));
        }

        @Test
        public void CSVReaderをクローズ中に例外が発生した場合は例外が変換される() throws Exception {
            thrown.expect(InternalException.class);
            thrown.expectMessage("ストリームをクローズ中にIO例外が発生しました。");
            CSVReader m = mock(CSVReader.class);
            doThrow(new IOException()).when(m).close();
            CSVReaders reader = CSVs.readers("temp.csv");
            reader.csv = m;
            reader.close();
        }

        @Test
        public void Readerをクローズ中に例外が発生した場合は例外が変換される() throws Exception {
            thrown.expect(InternalException.class);
            thrown.expectMessage("ストリームをクローズ中にIO例外が発生しました。");
            Reader m = mock(Reader.class);
            doThrow(new IOException()).when(m).close();
            CSVReaders reader = CSVs.readers("temp.csv");
            reader.reader = m;
            reader.close();
        }

        @Test
        public void InputStreamReaderをクローズ中に例外が発生した場合は例外が変換される() throws Exception {
            thrown.expect(InternalException.class);
            thrown.expectMessage("ストリームをクローズ中にIO例外が発生しました。");
            InputStreamReader m = mock(InputStreamReader.class);
            doThrow(new IOException()).when(m).close();
            CSVReaders reader = CSVs.readers("temp.csv");
            reader.iso = m;
            reader.close();
        }

        @Test
        public void FileInputStreamをクローズ中に例外が発生した場合は例外が変換される() throws Exception {
            thrown.expect(InternalException.class);
            thrown.expectMessage("ストリームをクローズ中にIO例外が発生しました。");
            FileInputStream m = mock(FileInputStream.class);
            doThrow(new IOException()).when(m).close();
            CSVReaders reader = CSVs.readers("temp.csv");
            reader.fis = m;
            reader.close();
        }

        @Test
        public void readNext実行中に例外が発生した場合は例外が変換される() throws Exception {
            File f = file.copy("CSVsTest-デフォルト設定で読み込める.csv", "temp.csv");
            thrown.expect(InternalException.class);
            thrown.expectMessage("読込処理中にIO例外が発生しました。");
            CSVReader m = mock(CSVReader.class);
            doThrow(new IOException()).when(m).readNext();
            CSVReaders reader = CSVs.readers(f);
            reader.csv = m;
            reader.next();
        }

    }

    public static class BeanMappingCSVReadersTest {

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Rule
        public FileInitailize file = new FileInitailize(CSVReadersTest.class);

        private BeanMappingCSVReaders<AnnotationTestBean> readers;

        @After
        public void teardown() {
            if (readers != null) {
                readers.close();
            }
            file.delete();
        }

        @Test
        public void get操作が禁止されている() {
            thrown.expect(InternalException.class);
            thrown.expectMessage("BeanMappingCSVReaders#getメソッドの利用は許可されていません。");
            readers = CSVs.readers(new File("temp.csv"), AnnotationTestBean.class);
            readers.get();
        }

        @Test
        public void ドメインとして取得できる() {
            File f = file.copy("CSVsTest-ドメインとしての取得ができる.csv", "temp.csv");
            readers = CSVs.readers(f, CSVConfigs.config(), AnnotationTestBean.class);
            readers.open();
            readers.next();
            AnnotationTestBean domain = readers.parse();
            assertThat(domain.getStr(), nullValue());
            assertThat(domain.getDate(), nullValue());
            assertThat(domain.getDateTime(), nullValue());
            assertThat(domain.getInteger(), nullValue());
            assertThat(domain.getLng(), nullValue());
            assertThat(domain.getBigInteger(), nullValue());
            assertThat(domain.getBigDecimal(), nullValue());

            // 2行目は全てのフィールドに値が入っている
            readers.next();
            domain = readers.parse();
            assertThat(domain.getStr(), is(" ")); // スペースはそのまま残る
            assertThat(domain.getDate(), is(Dates.makeFrom(2012, 2, 29)));
            assertThat(domain.getDateTime(), is("2012-02-29 06:07:08"));
            assertThat(domain.getInteger(), is(123456789));
            assertThat(domain.getLng(), is(9876543210L));
            assertThat(domain.getBigInteger(), is(new BigInteger("987654321098765432")));
            assertThat(domain.getBigDecimal(), is(new BigDecimal("98765432109876543210.12")));

            // 3行目
            readers.next();
            domain = readers.parse();
            assertThat(domain.getStr(), is("test"));
            assertThat(domain.getDate(), is(Dates.makeFrom(2013, 4, 5)));
            assertThat(domain.getDateTime(), is("2013-04-05 06:07:08"));
            assertThat(domain.getInteger(), is(-4));
            assertThat(domain.getLng(), is(-3L));
            assertThat(domain.getBigInteger(), is(new BigInteger("-2")));
            assertThat(domain.getBigDecimal(), is(new BigDecimal("-1")));

            // 4行目はない
            assertThat(readers.next().hasNext(), is(false));
            assertThat(readers.parse(), nullValue());
        }

        @Test
        public void CSVColumnアノテーションがなくてもドメインとして取得できる() {
            File f = file.copy("CSVsTest-ドメインとしての取得ができる.csv", "temp.csv");
            BeanMappingCSVReaders<SeqFieldTestBean> rds = CSVs.readers(f.getAbsolutePath(), CSVConfigs.config(), SeqFieldTestBean.class);
            rds.open();
            rds.next();
            SeqFieldTestBean domain = rds.parse();
            assertThat(domain.getStr(), nullValue());
            assertThat(domain.getDate(), nullValue());
            assertThat(domain.getDateTime(), nullValue());
            assertThat(domain.getInteger(), nullValue());
            assertThat(domain.getLng(), nullValue());
            assertThat(domain.getBigInteger(), nullValue());
            assertThat(domain.getBigDecimal(), nullValue());

            // 2行目は全てのフィールドに値が入っている
            rds.next();
            domain = rds.parse();
            assertThat(domain.getStr(), is(" ")); // スペースはそのまま残る
            assertThat(domain.getDate(), is(Dates.makeFrom(2012, 2, 29)));
            assertThat(domain.getDateTime(), is("2012-02-29 06:07:08"));
            assertThat(domain.getInteger(), is(123456789));
            assertThat(domain.getLng(), is(9876543210L));
            assertThat(domain.getBigInteger(), is(new BigInteger("987654321098765432")));
            assertThat(domain.getBigDecimal(), is(new BigDecimal("98765432109876543210.12")));

            // 3行目
            rds.next();
            domain = rds.parse();
            assertThat(domain.getStr(), is("test"));
            assertThat(domain.getDate(), is(Dates.makeFrom(2013, 4, 5)));
            assertThat(domain.getDateTime(), is("2013-04-05 06:07:08"));
            assertThat(domain.getInteger(), is(-4));
            assertThat(domain.getLng(), is(-3L));
            assertThat(domain.getBigInteger(), is(new BigInteger("-2")));
            assertThat(domain.getBigDecimal(), is(new BigDecimal("-1")));

            // 4行目はない
            assertThat(rds.next().hasNext(), is(false));
            assertThat(rds.parse(), nullValue());
            rds.close();
        }

        @Test
        public void マッピング定義をオーバライドできる() {
            File f = file.copy("CSVsTest-ドメインとしての取得ができる.csv", "temp.csv");
            BeanMappingCSVReaders<OverrideMappingTestBean> rds = CSVs.readers(f.getAbsolutePath(), OverrideMappingTestBean.class);
            rds.open();
            rds.next();
            rds.mapping(new String[] {
                "str", "date", "dateTime", "integer", "lng", "bigInteger", "bigDecimal"
            });
            OverrideMappingTestBean domain = rds.parse();
            assertThat(domain.getStr(), nullValue());
            assertThat(domain.getDate(), nullValue());
            assertThat(domain.getDateTime(), nullValue());
            assertThat(domain.getInteger(), nullValue());
            assertThat(domain.getLng(), nullValue());
            assertThat(domain.getBigInteger(), nullValue());
            assertThat(domain.getBigDecimal(), nullValue());

            // 2行目は全てのフィールドに値が入っている
            rds.next();
            domain = rds.parse();
            assertThat(domain.getStr(), is(" ")); // スペースはそのまま残る
            assertThat(domain.getDate(), is(Dates.makeFrom(2012, 2, 29)));
            assertThat(domain.getDateTime(), is("2012-02-29 06:07:08"));
            assertThat(domain.getInteger(), is(123456789));
            assertThat(domain.getLng(), is(9876543210L));
            assertThat(domain.getBigInteger(), is(new BigInteger("987654321098765432")));
            assertThat(domain.getBigDecimal(), is(new BigDecimal("98765432109876543210.12")));

            // 3行目
            rds.next();
            domain = rds.parse();
            assertThat(domain.getStr(), is("test"));
            assertThat(domain.getDate(), is(Dates.makeFrom(2013, 4, 5)));
            assertThat(domain.getDateTime(), is("2013-04-05 06:07:08"));
            assertThat(domain.getInteger(), is(-4));
            assertThat(domain.getLng(), is(-3L));
            assertThat(domain.getBigInteger(), is(new BigInteger("-2")));
            assertThat(domain.getBigDecimal(), is(new BigDecimal("-1")));

            // 4行目はない
            assertThat(rds.next().hasNext(), is(false));
            assertThat(rds.parse(), nullValue());
            rds.close();
        }

        @Test
        public void ドメインとして取得する際に型変換でエラーになる() throws Exception {
            File f = file.copy("CSVsTest-ドメインとして取得する際に型変換でエラーとなる場合.csv", "temp.csv");
            readers = CSVs.readers(f, AnnotationTestBean.class);
            readers.open();
            readers.next();
            try {
                readers.parse();
            } catch (BindException e) {
                List<BindError> errors = e.getErrors();
                assertThat(errors.size(), is(4));
                String[] properties = new String[errors.size()];
                int index = 0;
                for (BindError error : errors) {
                    properties[index] = error.getFieldName();
                    index++;
                }
                assertThat(Arrays.asList(properties), hasItems("integer", "lng", "bigInteger", "bigDecimal"));
            }
        }

        @Test
        @SuppressWarnings("unchecked")
        public void ヘッダ読込中にIO例外が発生した場合は例外が変換される() throws Exception {
            thrown.expect(InternalException.class);
            thrown.expectMessage("CSVのヘッダ読込中にIO例外が発生しました。");
            File f = file.copy("CSVsTest-dummy.csv", "temp.csv");
            readers = CSVs.readers(f, AnnotationTestBean.class);
            readers.open();
            readers.line = new String[] {
                "test"
            };
            ColumnPositionMappingStrategy<AnnotationTestBean> mockStrategy = Mockito.mock(ColumnPositionMappingStrategy.class);
            CSVToBeanMapping<AnnotationTestBean> parser = new CSVToBeanMapping<AnnotationTestBean>();
            doThrow(new IOException()).when(mockStrategy).captureHeader(readers.csv);
            readers.strategy = mockStrategy;
            readers.parser = parser;
            readers.parse();
        }

        @Test
        public void 変換対象のクラスを指定せずにmapping定義を作ろうとした場合は例外が発生する() {
            thrown.expect(InternalException.class);
            thrown.expectMessage("読込ヘッダの順番が定義されていません。");
            new BeanMappingCSVReaders<Object>(new File("temp.csv")).resolve();
        }

    }

    public static class CSVWritersTest {

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Rule
        public FileInitailize file = new FileInitailize(CSVReadersTest.class);

        @After
        public void teardown() {
            file.delete();
        }

        @Test
        public void OPENしていなければ書き込みできない() {
            thrown.expect(InternalException.class);
            thrown.expectMessage("ストリームがオープンされていない状態での書込操作は許可されていません。");
            CSVs.writers().write(new String[] {
                "テスト1", "テスト2"
            });
        }

        @Test
        public void OPEN後に書き込みできる() throws Exception {
            CSVWriters writers = CSVs.writers(CSVConfigs.config().tempDir(file.getRoot().getAbsolutePath())).touch().open();
            writers.write(new String[] {});
            writers.write(new String[] {
                null
            });
            writers.write(new String[] {
                "いろは", "<,>", "<\t>", "<\\t>"
            });
            writers.write(new String[] {
                "<'>", "<\">", "<\r\n>"
            });
            DownloadFile f = writers.get();
            assertThat(writers.count(), is(4));
            writers.close();
            File tmp = new File(f.getTempFileName());
            String content = FileUtils.readFileToString(tmp, "MS932");
            assertThat(content, is(file.load("CSVsTest-OPEN後に書き込みできる.csv")));
        }

        @Test
        public void 区切り文字を変更して書き込みできる() throws Exception {
            CSVWriters writers = CSVs.writers(CSVConfigs.config()
                .tempDir(file.getRoot().getAbsolutePath())
                .separator('\t')
                .escape('\"'))
                .touch()
                .open();
            writers.write(new String[] {
                "いろは", "<,>", "<\t>", "<\\t>"
            });
            writers.write(new String[] {
                "<'>", "<\">", "<\r\n>"
            });
            DownloadFile f = writers.get();
            writers.close();
            File tmp = new File(f.getTempFileName());
            String content = FileUtils.readFileToString(tmp, "MS932");
            assertThat(content, is(file.load("CSVsTest-区切り文字を変更して書き込みできる.tsv")));
        }

        @Test
        public void 囲み文字を変更して書き込みできる() throws Exception {
            CSVWriters writers = CSVs.writers(CSVConfigs.config().tempDir(file.getRoot().getAbsolutePath()).quote('\'').escape('\'')).touch()
                .open();
            writers.write(new String[] {
                "いろは", "<,>", "<\t>", "<\\t>"
            });
            writers.write(new String[] {
                "<'>", "<\">", "<\r\n>"
            });
            DownloadFile f = writers.get();
            writers.close();
            File tmp = new File(f.getTempFileName());
            String content = FileUtils.readFileToString(tmp, "MS932");
            assertThat(content, is(file.load("CSVsTest-囲み文字を変更して書き込みできる.csv")));
        }

        @Test
        public void CSVWriterをクローズ中に例外が発生した場合は例外が変換される() throws Exception {
            thrown.expect(InternalException.class);
            thrown.expectMessage("ストリームをクローズ中にIO例外が発生しました。");
            CSVWriter m = mock(CSVWriter.class);
            doThrow(new IOException()).when(m).close();
            CSVWriters writer = CSVs.writers();
            writer.csv = m;
            writer.close();
        }

        @Test
        public void Writerをクローズ中に例外が発生した場合は例外が変換される() throws Exception {
            thrown.expect(InternalException.class);
            thrown.expectMessage("ストリームをクローズ中にIO例外が発生しました。");
            Writer m = mock(Writer.class);
            doThrow(new IOException()).when(m).close();
            CSVWriters writer = CSVs.writers();
            writer.writer = m;
            writer.close();
        }

        @Test
        public void OutputStreamWriterをクローズ中に例外が発生した場合は例外が変換される() throws Exception {
            thrown.expect(InternalException.class);
            thrown.expectMessage("ストリームをクローズ中にIO例外が発生しました。");
            OutputStreamWriter m = mock(OutputStreamWriter.class);
            doThrow(new IOException()).when(m).close();
            CSVWriters writer = CSVs.writers();
            writer.osw = m;
            writer.close();
        }

        @Test
        public void FileOutputStreamをクローズ中に例外が発生した場合は例外が変換される() throws Exception {
            thrown.expect(InternalException.class);
            thrown.expectMessage("ストリームをクローズ中にIO例外が発生しました。");
            FileOutputStream m = mock(FileOutputStream.class);
            doThrow(new IOException()).when(m).close();
            CSVWriters writer = CSVs.writers();
            writer.fos = m;
            writer.close();
        }

    }

}
