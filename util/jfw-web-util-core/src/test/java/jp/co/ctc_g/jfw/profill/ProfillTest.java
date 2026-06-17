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

package jp.co.ctc_g.jfw.profill;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import jp.co.ctc_g.jfw.core.internal.InternalException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProfillTest {

    private Profill profill;
    
    @BeforeEach
    public void instantiate() {
        profill = new Profill();
    }
    
    @Test
    public void 無効なFillingProviderでPropertyMatcherを作成しようとするとE$PROFILL$0001が発生する() {
        boolean called = false;
        try {
            profill.createPropertyMatcher(new FillingProvider() {
                @Override
                public Object provide(MatchedProperty property, Object bean) {
                    return null;
                }
            });
        } catch (InternalException ex) {
            assertThat(ex.getCode(), is("E-PROFILL#0001"));
            called = true;
        }
        assertThat(called, is(true));
    }
    
    @Test
    public void 有効なFillingProviderでPropertyMatcherを作成できる() {
        PropertyMatcher matcher = profill.createPropertyMatcher(new ValidFillingProvider());
        assertThat(matcher.getAnnotation() == Action.class, is(true));
        assertThat(matcher.getName(), is("test"));
        assertThat(matcher.getType() == String.class, is(true));
    }
    
    @Test
    public void 同じクラスのFillingProviderを追加しても登録されるのは1つだけ() {
        profill.addFillingProvider(new AnnotatedStringLiteralFillingProvider());
        profill.addFillingProvider(new AnnotatedStringLiteralFillingProvider());
        assertThat(profill.matchers.size(), is(1));
    }
    
    @ProvideFillingFor(annotation = Action.class, name = "test", type = String.class)
    public static class ValidFillingProvider implements FillingProvider {
        @Override public Object provide(MatchedProperty property, Object bean) {
            return null;
        }
    }
    
    // 例外テスト ---------------------------------------------------------------------------
    
    @Test
    public void FillingProviderにnullを指定できない() {
        assertThrows(InternalException.class, () -> {
            profill.addFillingProvider(null);
        });
    }
    
    @Test
    public void スレッドセーフモードにするとFillingProviderは追加できない() {
        assertThrows(CannotModifyProfillException.class, () -> {
            profill.makeSafeAgainstMultiThreadedAccess();
            profill.addFillingProvider(new AnnotatedStringLiteralFillingProvider());
        });
    }
    
    @Test
    public void スレッドセーフモードにするとtryToInstantiateIfNestedPropertyIsNullは追加できない() {
        assertThrows(CannotModifyProfillException.class, () -> {
            profill.makeSafeAgainstMultiThreadedAccess();
            profill.setTryToInstantiateIfNestedPropertyIsNull(true);
        });
    }
    
    protected @interface Action {
    }
}
