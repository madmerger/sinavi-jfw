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

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * この注釈は、あるテストメソッドがMongoDB内のデータの初期化を要求していることを示します。
 * この注釈がついているテストメソッドは、その実行前にテストデータを使ってMongoDB内のデータを初期化することができます。
 * </p>
 * <p>
 * この注釈を利用した単純な例を以下に示します。
 * <pre class="brush:java">
 * &#64;RunWith(SpringMongoJUnit4ClassRunner.class);
 * &#64;ContextConfiguration(locations = "/Context.xml")
 * public class FooServiceTest {
 *   &#64;Autowired
 *   private FooService service;
 *   
 *   &#64;Test
 *   &#64;MongoInitialize
 *   public void testFind() {
 *     Foo foo = service.find(1);
 *     assertNotNull(foo, "id=1のFooドメインが存在");
 *     assertEquals(1, foo.getId(, "idは1"));
 *     assertEquals("nameは'bar'", "bar", foo.getName());
 *   }
 *   
 * }
 * </pre>
 * 上記のようにテストメソッドに注釈を付けてください。
 * そして、メソッド実行時に実行したいJSONデータをまとめたファイルを作成します。
 * そして、このファイルをテストケースと同じパッケージに保存します。
 * ここでは名前をFooServiceTest-testFind.jsonとしました(ファイル名規則については後述)。
 * <pre>
 * [
 *  {
 *   "id" : 1,
 *   "name" : "bar"
 *  },
 *  {
 *   "id" : 2,
 *   "name" : "baz"
 *  },
 *  {
 *   "id" : 3,
 *   "name" : "qux"
 *  },
 *  {
 *   "id" : 4,
 *   "name" : "corge"
 *  }
 * ]
 * </pre>
 * テストケースを実行します。
 * するとテストメソッド実行前にテスト用のJSONデータの登録が実行されます。
 * </p>
 * <p>
 * ファイル検索順序は以下の通りです。
 * <ol>
 *  <li>クラス名-メソッド名.json という名前のファイル名を検索(例: FooServiceTest-testFind.json)</li>
 *  <li>クラス名.jsonという名前のファイル名を検索(例: FooServiceTest.json)</li>
 *  <li>MongoInitialize.json という名前のファイル名を検索</li>
 * </ol>
 * 最初に見つかったファイルが利用されます。
 * この法則にマッピングできないJSONファイルを利用しなければならない場合は、
 * 以下のようにJSONファイルを指定してください。(拡張子は必要ありません。)
 * <pre class="brush:java">
 * &#64;MongoInitialize(file="/jp/co/ctc_g/fw/TestJSON")
 * </pre>
 * </p>
 * @see TestMongoKeeper
 * @see SpringMongoJUnit4ClassRunner
 * @author ITOCHU Techno-Solutions Corporation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MongoInitialize {

    /**
     * コンバージョンで検出されるJSONファイルとは異なったJSONファイルを指定したい場合に、
     * この属性を指定します。末尾の拡張子は必要ありません。
     * デフォルトは空文字ですが、この場合はコンバージョンによる自動検出が実行されることを意味します。
     */
    String file() default "";

    /**
     * JSONファイルが記述されているキャラクタセットを指定します。
     */
    String charset() default "UTF-8";

    /**
     * テストデータ登録前にCollectionを削除するかどうかを指定します。
     */
    boolean truncate() default false;

    /**
     * データ登録対象のCollection名を指定します。
     */
    String collectionName() default "";

    /**
     * {@link org.springframework.data.mongodb.core.MongoOperations}のBean登録IDを指定します。
     */
    String operationsRef() default "";

    /**
     * JSONデータのマッピング対象の型を指定します。
     */
    Class<?> mapping() default Void.class;

}
