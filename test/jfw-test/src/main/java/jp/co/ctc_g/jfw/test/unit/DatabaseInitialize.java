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

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * この注釈は、あるテストメソッドがRDBMS内データの初期化を要求していることを示します。
 * この注釈がついているテストメソッドは、その実行前にテスト用データを使ってRDBMS内データを初期化することができます。
 * </p>
 * <h4>動機</h4>
 * <p>
 * RDBMSと連携するユニットテストは、面倒です。
 * 何が面倒かといえば、テストの度にRDBMSのデータが同じであることを保証することが面倒なのです。
 * このような保証を実現するためにdbunitがありますが、
 * このツールは多くの事前準備を必要とするため、手軽さに欠けます。
 * そこで、J-Frameworkはユニットテストのメソッド実行前にRDBMSを初期化するだけの単純な機能を提供するに至りました。
 * </p>
 * <h4>実行</h4>
 * <p>
 * この注釈を利用した単純な例を以下に示します。
 * </p>
 * <pre class="brush:java">
 * &#64;RunWith(J2Unit4ClassRunner.class);
 * &#64;ContextConfiguration(locations = "/Context.xml")
 * public class FooServiceTest {
 *
 *     &#64;Autowired
 *     protected FooService service;
 *
 *     &#64;Test
 *     &#64;DatabaseInitialize
 *     public void testFind() {
 *         Foo foo = service.find(1);
 *         assertNotNull(foo, "id=1のFooドメインが存在");
 *         assertEquals(1, foo.getId(), "idは1");
 *         assertEquals("nameは'bar'", "bar", foo.getName());
 *     }
 * }
 * </pre>
 * <p>
 * 上記のようにテストメソッドに注釈を付けてください。
 * そして、メソッド実行時に実行したいSQL文をまとめたファイルを作成します。
 * そして、このファイルをテストケースと同じパッケージに保存します。
 * ここでは名前をFooServiceTest-testFind.sqlとしました（ファイル名規則については後述）。
 * </p>
 * <pre class="brush:sql">
 * create table if not exists FOO (
 *     ID integer(10) primary key,
 *     NAME varchar(20) not null
 * );
 *
 * truncate FOO;
 *
 * insert into FOO values (1, 'bar');
 * insert into FOO values (2, 'baz');
 * insert into FOO values (3, 'qux');
 * insert into FOO values (4, 'corge');
 * </pre>
 * <p>
 * テストケースを実行します。
 * すると、テストメソッド実行前にSQL文が実行されます。
 * なお、現時点では、SQLファイル内に記述されたSQL文は構文解析されずに、
 * 単純に ;（セミコロン）で分割して1つの文と見做しています。
 * よって、コメント内のセミコロンも分割対象になりますので、ご注意ください。
 * </p>
 * <h4>SQLファイルの名前とスコープ</h4>
 * <p>
 * SQL文を含んでいるファイルの名前をどのように命名するかは重要です。
 * ファイル名を直接指定しない場合は、コンバージョンによりどのSQLファイルを利用するかが決定されるからです。
 * ファイル検索順序は以下の通りです。
 * </p>
 * <ol>
 *  <li>クラス名-メソッド名.sql という名前のファイルを検索（例: FooServiceTest-testFind.sql）</li>
 *  <li>クラス名.sql という名前のファイルを検索（例: FooServiceTest.sql）</li>
 *  <li>DatabaseInitialize.sql という名前のファイルを検索</li>
 * </ol>
 * <p>
 * 最初に見つかったファイルが利用されます。
 * この法則にマッピングできないSQLファイルを利用しなければならない場合は、
 * 以下のようにしてSQLファイルを指定してください（拡張子は必要ありません）。
 * </p>
 * <pre class="brush:java">
 * &#64;DatabaseInitialize(file="/jp/co/ctc_g/jfw/TestSQL")
 * </pre>
 * <h4>データベース方言への対応</h4>
 * <p>
 * 上記例のSQLファイルは、{@code varchar}を利用しています。
 * これは、Oracleデータベースではぜひ{@code varchar2}としたいところです。
 * このような場合、ファイルを方言毎に作成し、ファイル名を変更します。
 * ファイル名には、_方言略名を末尾に追加します。例を示します。
 * </p>
 * <pre>
 * FooServiceTest-testFind_h2.sql // H2用SQLファイル
 * FooServiceTest-testFind_oracle.sql // Oracle用SQLファイル
 * </pre>
 * <p>
 * もちろん、メソッド指定なしでテストクラススコープへと広げる場合や、
 * パッケージスコープとしても問題ありません。
 * </p>
 * <pre>
 * FooServiceTest_h2.sql // テストクラススコープのH2用SQLファイル
 * FooServiceTest_oracle.sql // ストクラススコープのOracle用SQLファイル
 * DatabaseInitialize_h2.sql // パッケージスコープのH2用SQLファイル
 * DatabaseInitialize_oracle.sql // パッケージスコープのOracle用SQLファイル
 * </pre>
 * <p>
 * 方言略名として利用可能な文字列は、{@code derby, h2, oracle, sqlserver}です。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see TestDatabaseKeeper
 * @see J2Unit4ClassRunner
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DatabaseInitialize {

    /**
     * コンバージョンで検出されるSQLファイルとは異なったSQLファイルを指定したい場合に、
     * この属性を指定します。末尾の拡張子は必要ありません。
     * デフォルトは空文字ですが、この場合はコンバージョンによる自動検出が実行されることを意味します。
     */
    String file() default "";

    /**
     * SQLファイルが記述されているキャラクタセットを指定します。
     */
    String charset() default "UTF-8";

    /**
     * データソースのビーンIDを指定します。
     * デフォルトではDIコンテナ内で{@link javax.sql.DataSource}として利用可能なオブジェクトを自動的に検出します。
     * もし、DIコンテナ内に複数のデータソースがあり、
     * うまく意図したデータソースが利用されていないのであれば、
     * 明示的にビーンIDを指定することでそのデータソースを利用するようになります。
     */
    String dataSourceRef() default "";

}
