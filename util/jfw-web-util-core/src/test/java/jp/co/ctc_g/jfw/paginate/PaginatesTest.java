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
import static org.junit.jupiter.api.Assertions.assertThrows;
import jp.co.ctc_g.jfw.core.internal.InternalException;

import org.junit.jupiter.api.Test;

public class PaginatesTest {

    @Test
    public void 要素数を取得できる() {
        int[] offsets = { 1, 2, 10, 31, 11 };
        int[] limits = { 10, 12, 20, 40, 11 };
        int[] expecteds = { 10, 11, 11, 10, 1 };
        for (int i = 0; i < offsets.length; i++) {
            int ecpp = Paginates.getElementCountPerPart(offsets[i], limits[i]);
            assertThat(String.format("offset=%s,limit=%s", offsets[i], limits[i]), ecpp, is(expecteds[i]));
        }
    }
    
    @Test
    public void オフセットよりもリミットが大きい場合はE$PAGINATE$0003() {
        int[] offsets = {10};
        int[] limits = {0};
        int called = 0;
        int expected = 0;
        for (int i = 0; i < offsets.length; i++) {
            try {
                Paginates.getElementCountPerPart(offsets[i], limits[i]);
            } catch (UncalculatablePagingException e) {
                assertThat(e.getCode(), is("E-PAGINATE#0003"));
                called |= 1 << (i + 1);
            } finally {
                expected |= 1 << (i + 1);
            }
        }
        assertThat(called, is(expected));
    }
    
    @Test
    public void 部分要素の位置を取得できる() {
        int[] offsets =  {1, 11, 21, 11, 101};
        int[] limits = {10, 10, 10, 1, 10};
        int[] totals = {100, 100, 100, 100, 105};
        int[] expecteds = {1, 2, 3, 11, 11};
        for (int i = 0; i < offsets.length; i++) {
            int ecpp = Paginates.getPartIndex(offsets[i], limits[i], totals[i]);
            String msg = String.format("offset=%s,limit=%s,total=%s", offsets[i], limits[i], totals[i]);
            assertThat(msg, ecpp, is(expecteds[i]));
        }
    }
    
    @Test
    public void オフセットがページ開始位置にない場合はE$PAGINATE$0002() {
        int[] offsets = {11, 31, 51};
        int[] limits = {30, 50, 72};
        int[] totals = {100, 100, 100};
        int called = 0;
        int expected = 0;
        for (int i = 0; i < offsets.length; i++) {
            try {
                Paginates.getPartIndex(offsets[i], limits[i], totals[i]);
            } catch (UncalculatablePagingException e) {
                assertThat(e.getCode(), is("E-PAGINATE#0002"));
                called |= 1 << (i + 1);
            } finally {
                expected |= 1 << (i + 1);
            }
        }
        assertThat(called, is(expected));
    }
    
    @Test
    public void オフセットを取得できる() {
        int[] partNumbers = {1, 2, 3, 2};
        int[] elementCountPerParts = {10, 10, 10, 100};
        int[] expecteds = {1, 11, 21, 101};
        for (int i = 0; i < partNumbers.length; i++) {
            int offset = Paginates.getOffset(partNumbers[i], elementCountPerParts[i]);
            String msg = String.format("partNumbers=%s,elementCountPerParts=%s", partNumbers[i], elementCountPerParts[i]);
            assertThat(msg, offset, is(expecteds[i]));
        }
    }
    
    @Test
    public void オフセットのページ番号に不正な値が指定されたら例外が発生する() {
        assertThrows(InternalException.class, () -> {
            Paginates.getOffset(0, 10);
        });
    }
    
    @Test
    public void オフセットの表示件数に不正な値が指定されたら例外が発生する() {
        assertThrows(InternalException.class, () -> {
            Paginates.getOffset(1, 0);
        });
    }
    
    @Test
    public void テイルを取得できる() {
        int[] partNumbers = {1, 2, 3, 2};
        int[] elementCountPerParts = {10, 10, 10, 100};
        int[] expecteds = {10, 20, 30, 200};
        for (int i = 0; i < partNumbers.length; i++) {
            int offset = Paginates.getTail(partNumbers[i], elementCountPerParts[i]);
            String msg = String.format("partNumbers=%s,elementCountPerParts=%s", partNumbers[i], elementCountPerParts[i]);
            assertThat(msg, offset, is(expecteds[i]));
        }
    }
    
    @Test
    public void テイルのページ番号に不正な値が指定されたら例外が発生する() {
        assertThrows(InternalException.class, () -> {
            Paginates.getTail(0, 10);
        });
    }
    
    @Test
    public void テイルの表示件数に不正な値が指定されたら例外が発生する() {
        assertThrows(InternalException.class, () -> {
            Paginates.getTail(1, 0);
        });
    }
    
    @Test
    public void Paginatableに値を設定できる() {
        int[] partNumbers = {1, 2, 3, 2};
        int[] elementCountPerParts = {10, 10, 10, 100};
        int[][] expecteds = {
                {1, 10, 10},
                {11, 20, 10},
                {21, 30, 10},
                {101, 200, 100}
         };
        for (int i = 0; i < partNumbers.length; i++) {
            Paginatable paginatable = new PaginatableBean();
            Paginates.set(paginatable, partNumbers[i], elementCountPerParts[i]);
            String msg = String.format("partNumbers=%s,elementCountPerParts=%s", partNumbers[i], elementCountPerParts[i]);
            assertThat(msg, paginatable.getOffset(), is(expecteds[i][0]));
            assertThat(msg, paginatable.getTail(), is(expecteds[i][1]));
            assertThat(msg, paginatable.getLimit(), is(expecteds[i][2]));
        }
    }
    
    public static class PaginatableBean extends PaginatableSupport {
        private static final long serialVersionUID = 464883904004742418L;
    }
}
