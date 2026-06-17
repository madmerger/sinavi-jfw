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

package jp.co.ctc_g.jse.vid;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.vid.victim.InvalidPermitConstraintVictim;
import jp.co.ctc_g.jse.vid.victim.MultiPermitConstraintVictim;
import jp.co.ctc_g.jse.vid.victim.PermitAndRejectConstraintVictim;
import jp.co.ctc_g.jse.vid.victim.PermitConstraintVictim;
import jp.co.ctc_g.jse.vid.victim.RejectConstraintVictim;

import org.junit.jupiter.api.Test;

public class ViewTransitionKeeperTest {

    @Test
    public void 生成テスト正常系1() {
        ViewTransitionKeeper k = new ViewTransitionKeeper(Object.class);
        // デフォルトパターン(see ViewTransitionKeeper.config.properties)
        assertEquals("^.*$", k.allowPattern.pattern());
        assertNull(k.exceptPattern);
        assertFalse(k.checkRequired);
    }

    @Test
    public void 許可指定生成テスト正常系1() {
        ViewTransitionKeeper k = new ViewTransitionKeeper(PermitConstraintVictim.class);
        assertEquals(p(PermitConstraintVictim.PERMIT), k.allowPattern.pattern());
        assertNull(k.exceptPattern);
        assertTrue(k.checkRequired);
    }

    @Test
    public void 許可指定生成テスト正常系2() throws InvalidViewTransitionException {
        ViewTransitionKeeper k = new ViewTransitionKeeper(PermitConstraintVictim.class);
        ViewId vid = new ViewId(PermitConstraintVictim.PERMIT);
        k.check(vid);
    }

    @Test
    public void 許可指定生成テスト正常系3() throws InvalidViewTransitionException {
        assertThrows(InvalidViewTransitionException.class, () -> {
            ViewTransitionKeeper k = new ViewTransitionKeeper(PermitConstraintVictim.class);
            ViewId vid = new ViewId(Strings.reverse(PermitConstraintVictim.PERMIT));
            k.check(vid);
            fail();
        });
    }

    @Test
    public void 許可指定テスト異常系1() {
        assertThrows(InternalException.class, () -> {
            new ViewTransitionKeeper(InvalidPermitConstraintVictim.class);
            fail();
        });
    }

    @Test
    public void 拒否指定生成テスト正常系1() {
        ViewTransitionKeeper k = new ViewTransitionKeeper(RejectConstraintVictim.class);
        // デフォルトパターン(see ViewTransitionKeeper.config.properties)
        assertEquals("^.*$", k.allowPattern.pattern());
        assertEquals(p(RejectConstraintVictim.REJECT), k.exceptPattern.pattern());
        assertTrue(k.checkRequired);
    }

    @Test
    public void 拒否指定生成テスト正常系2() throws InvalidViewTransitionException {
        ViewTransitionKeeper k = new ViewTransitionKeeper(RejectConstraintVictim.class);
        ViewId vid = new ViewId(Strings.reverse(RejectConstraintVictim.REJECT));
        k.check(vid);
    }

    @Test
    public void 拒否指定生成テスト正常系3() throws InvalidViewTransitionException {
        assertThrows(InvalidViewTransitionException.class, () -> {
            ViewTransitionKeeper k = new ViewTransitionKeeper(RejectConstraintVictim.class);
            ViewId vid = new ViewId(RejectConstraintVictim.REJECT);
            k.check(vid);
            fail();
        });
    }

    @Test
    public void 許可と拒否指定生成テスト正常系1() {
        ViewTransitionKeeper k = new ViewTransitionKeeper(PermitAndRejectConstraintVictim.class);
        assertEquals(p(PermitAndRejectConstraintVictim.PERMIT), k.allowPattern.pattern());
        assertEquals(p(PermitAndRejectConstraintVictim.REJECT), k.exceptPattern.pattern());
        assertTrue(k.checkRequired);
    }

    @Test
    public void 許可と拒否指定生成テスト正常系2() throws InvalidViewTransitionException {
        ViewTransitionKeeper k = new ViewTransitionKeeper(PermitAndRejectConstraintVictim.class);
        ViewId vid = new ViewId("ABCABC");
        k.check(vid);
    }

    @Test
    public void 許可と拒否指定生成テスト正常系3() throws InvalidViewTransitionException {
        assertThrows(InvalidViewTransitionException.class, () -> {
            ViewTransitionKeeper k = new ViewTransitionKeeper(PermitAndRejectConstraintVictim.class);
            ViewId vid = new ViewId("ABCDEF");
            k.check(vid);
        });
    }

    @Test
    public void 許可と拒否指定生成テスト正常系4() throws InvalidViewTransitionException {
        assertThrows(InvalidViewTransitionException.class, () -> {
            ViewTransitionKeeper k = new ViewTransitionKeeper(PermitAndRejectConstraintVictim.class);
            ViewId vid = new ViewId(Strings.reverse(PermitAndRejectConstraintVictim.PERMIT));
            k.check(vid);
            fail();
        });
    }

    @Test
    public void 許可複数指定生成テスト正常系1() {
        ViewTransitionKeeper k = new ViewTransitionKeeper(MultiPermitConstraintVictim.class);
        assertEquals(p(MultiPermitConstraintVictim.PERMIT), k.allowPattern.pattern());
        assertNull(k.exceptPattern);
        assertTrue(k.checkRequired);
    }

    @Test
    public void 許可複数指定生成テスト正常系2() throws InvalidViewTransitionException {
        ViewTransitionKeeper k = new ViewTransitionKeeper(MultiPermitConstraintVictim.class);
        String[] ids = MultiPermitConstraintVictim.PERMIT.split("\\|");
        for (String id : ids) {
            ViewId vid = new ViewId(id);
            k.check(vid);
        }
    }

    @Test
    public void 許可複数指定生成テスト正常系3() throws InvalidViewTransitionException {
        assertThrows(InvalidViewTransitionException.class, () -> {
            ViewTransitionKeeper k = new ViewTransitionKeeper(MultiPermitConstraintVictim.class);
            String[] ids = MultiPermitConstraintVictim.PERMIT.split("\\|");
            for (String id : ids) {
                id = Strings.reverse(id);
                ViewId vid = new ViewId(id);
                k.check(vid);
            }
            fail();
        });
    }

    @Test
    public void 不正オブジェクト時check状態異常系1() throws InvalidViewTransitionException {
        assertThrows(InternalException.class, () -> {
            ViewTransitionKeeper k = new ViewTransitionKeeper(PermitConstraintVictim.class);
            ViewId vid = new ViewId(PermitConstraintVictim.PERMIT);
            // permitPatternとrejectPatternが共にnullになる
            k.allowPattern = null;
            k.check(vid);
        });
    }

    private String p(String pattern) {
        return Strings.join("^", pattern, "$");
    }
}
