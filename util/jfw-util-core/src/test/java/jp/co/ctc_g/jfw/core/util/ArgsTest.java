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

package jp.co.ctc_g.jfw.core.util;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jp.co.ctc_g.jfw.core.internal.InternalException;

import org.junit.jupiter.api.Test;


public class ArgsTest {

    @Test
    public void 静的callerテスト() {
        String staticCaller = ArgsCallee.callStatic();
        assertEquals("jp.co.ctc_g.jfw.core.util.ArgsCallee.callStatic", staticCaller);
    }

    @Test
    public void 動的callerテスト() {
        String virtualCaller = new ArgsCallee().callVirtual();
        assertEquals("jp.co.ctc_g.jfw.core.util.ArgsCallee.callVirtual", virtualCaller);
    }

    @Test
    public void 静的calledByのFalse返却テスト() {
        boolean called = ArgsCallee.calledByStatic(Object.class);
        assertFalse(called);
    }

    @Test
    public void 動的calledByのFalse返却テスト() {
        boolean called = new ArgsCallee().calledByVirtual(Object.class);
        assertFalse(called);
    }

    @Test
    public void 静的calledByのTrue返却テスト() {
        boolean called = ArgsCallee.calledByStatic(this.getClass());
        assertTrue(called);
    }

    @Test
    public void 動的calledByのTrue返却テスト() {
        boolean called = new ArgsCallee().calledByVirtual(this.getClass());
        assertTrue(called);
    }

    @Test
    public void 静的calledByの深さ指定True返却テスト() {
        boolean called = ArgsCallee.calledByStatic(this.getClass(), 10);
        assertTrue(called);
    }

    @Test
    public void 動的calledByの深さ指定True返却テスト() {
        boolean called = new ArgsCallee().calledByVirtual(this.getClass(), 10);
        assertTrue(called);
    }

    @Test
    public void 静的calledByの深さ指定False返却テスト() {
        boolean called = ArgsCallee.calledByStatic(Object.class, 10);
        assertFalse(called);
    }

    @Test
    public void 動的calledByの深さ指定False返却テスト() {
        boolean called = new ArgsCallee().calledByVirtual(Object.class, 10);
        assertFalse(called);
    }

    @Test
    public void 静的calledByの深さ指定ミスFalse返却テスト() {
        boolean called = ArgsCallee.calledByStatic(org.junit.jupiter.api.Test.class, 1);
        assertFalse(called);
    }

    @Test
    public void 動的calledByの深さ指定ミスFalse返却テスト() {
        boolean called = new ArgsCallee().calledByVirtual(org.junit.jupiter.api.Test.class, 1);
        assertFalse(called);
    }

    @Test
    public void 第1引数がnullでないproperテスト() {
        Object first = new Object() {
            @Override public String toString() {return "first";}
        };
        Object second = new Object() {
            @Override public String toString() {return "second";}
        };
        String actual = Args.proper(first, second).toString();
        assertEquals("first", actual);
    }

    @Test
    public void 第1引数がnullのproperテスト() {
        Object first = null;
        Object second = new Object() {
            @Override public String toString() {return "second";}
        };
        String actual = Args.proper(first, second).toString();
        assertEquals("second", actual);
    }

    @Test
    public void checkNotNull正常テスト() {
        Args.checkNotNull("");
        assertTrue(true);
    }

    @Test
    public void checkNotNullで例外を発生させてみるテスト() {
        assertThrows(InternalException.class, () -> {
            Args.checkNotNull(null);
        });
    }

    @Test
    public void checkNotEmpty正常テスト() {
        Args.checkNotEmpty("a");
        Args.checkNotEmpty(Arrays.gen(null, null));
        Args.checkNotEmpty(Arrays.gen("", ""));
        Args.checkNotEmpty(Lists.gen(null, null));
        Args.checkNotEmpty(Lists.gen("", ""));
        assertTrue(true);
    }

    @Test
    public void checkNotEmptyで例外を発生させてみるテスト1() {
        assertThrows(InternalException.class, () -> {
            Args.checkNotEmpty("");
        });
    }

    @Test
    public void checkNotEmptyで例外を発生させてみるテスト2() {
        assertThrows(InternalException.class, () -> {
            Args.checkNotEmpty(new Object[0]);
        });
    }

    @Test
    public void checkNotEmptyで例外を発生させてみるテスト3() {
        assertThrows(InternalException.class, () -> {
            Args.checkNotEmpty(new ArrayList<String>());
        });
    }

    @Test
    public void checkNotEmptyで例外を発生させてみるテスト4() {
        assertThrows(InternalException.class, () -> {
            Args.checkNotEmpty(new HashMap<String, String>());
        });
    }

    @Test
    public void checkNotEmptyで例外を発生させてみるテスト5() {
        assertThrows(InternalException.class, () -> {
            Args.checkNotEmpty((String)null);
        });
    }

    @Test
    public void checkNotEmptyで例外を発生させてみるテスト6() {
        assertThrows(InternalException.class, () -> {
            Args.checkNotEmpty((Object[])null);
        });
    }

    @Test
    public void checkNotEmptyで例外を発生させてみるテスト7() {
        assertThrows(InternalException.class, () -> {
            Args.checkNotEmpty((Collection<?>)null);
        });
    }

    @Test
    public void checkNotEmptyで例外を発生させてみるテスト8() {
        assertThrows(InternalException.class, () -> {
            Args.checkNotEmpty((Map<?, ?>)null);
        });
    }

    @Test
    public void 引数例外のカスタムメッセージを検証するテスト() {
        try {
            ArgsCallee.exceptionWithCustomMessage();
        } catch (InternalException e) {
            assertEquals("カスタムメッセージ", e.getMessage());
        }
    }

}

class ArgsCallee {
    public String callVirtual() {
        return Args.caller();
    }
    public static String callStatic() {
        return Args.caller();
    }
    public boolean calledByVirtual(Class<?> caller) {
        return Args.calledBy(caller);
    }
    public static boolean calledByStatic(Class<?> caller) {
        return Args.calledBy(caller);
    }
    public boolean calledByVirtual(Class<?> caller, int depth) {
        return Args.calledBy(caller, depth);
    }
    public static boolean calledByStatic(Class<?> caller, int depth) {
        return Args.calledBy(caller, depth);
    }
    public static void exception() {
        Args.checkNotEmpty((String)null);
    }
    public static void exceptionWithCustomMessage() {
        Args.checkNotEmpty((String)null, "カスタムメッセージ");
    }
}