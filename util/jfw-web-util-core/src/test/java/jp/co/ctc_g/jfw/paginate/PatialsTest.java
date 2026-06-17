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

package jp.co.ctc_g.jfw.paginate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import jp.co.ctc_g.jfw.core.util.PartialList;

import org.junit.jupiter.api.Test;

public class PatialsTest {

    @Test
    public void 全体集合の要素数を取得できる() {

        assertThat(Partials.getElementCount(new PartialList<String>()), is(0));
        PartialList<String> suspect = new PartialList<String>();
        suspect.add("1");
        suspect.setElementCount(10);
        assertThat(Partials.getElementCount(suspect), is(10));
    }

    @Test
    public void インデックスを取得できる() {

        assertThat(Partials.getPartIndex(new PartialList<String>()), is(0));
        PartialList<String> suspect = new PartialList<String>();
        suspect.add("1");
        suspect.setPartIndex(1);
        assertThat(Partials.getPartIndex(suspect), is(1));
    }

    @Test
    public void 部分集合を取得できる() {

        assertThat(Partials.getPartCount(new PartialList<String>()), is(0));
        PartialList<String> suspect = new PartialList<String>();
        suspect.add("1");
        suspect.setPartCount(10);
        assertThat(Partials.getPartCount(suspect), is(10));
    }

    @Test
    public void 部分集合の要素数を取得できる() {

        assertThat(Partials.getElementCountPerPart(new PartialList<String>()), is(0));
        PartialList<String> suspect = new PartialList<String>();
        suspect.add("1");
        suspect.setElementCountPerPart(10);
        assertThat(Partials.getElementCountPerPart(suspect), is(10));
    }

    @Test
    public void 開始のインデックスを取得できる() {

        assertThat(Partials.getElementBeginIndex(new PartialList<String>()), is(0));
        PartialList<String> suspect = new PartialList<String>();
        suspect.add("1");
        suspect.setPartIndex(1);
        suspect.setElementCountPerPart(10);
        assertThat(Partials.getElementBeginIndex(suspect), is(1));
        suspect.setPartIndex(2);
        assertThat(Partials.getElementBeginIndex(suspect), is(11));
    }

    @Test
    public void 終了のインデックスを取得できる() {

        assertThat(Partials.getElementEndIndex(new PartialList<String>()), is(0));
        PartialList<String> suspect = new PartialList<String>();
        for (int i = 0; i < 10; i++) {
            suspect.add(Integer.toString(i));
        }
        suspect.setPartIndex(1);
        suspect.setElementCountPerPart(10);
        assertThat(Partials.getElementEndIndex(suspect), is(10));
        suspect.setPartIndex(2);
        assertThat(Partials.getElementEndIndex(suspect), is(20));
    }

}
