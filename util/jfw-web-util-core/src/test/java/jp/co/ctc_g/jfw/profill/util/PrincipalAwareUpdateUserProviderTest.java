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

package jp.co.ctc_g.jfw.profill.util;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrincipalAwareUpdateUserProviderTest {

    private PrincipalAwareUpdateUserProvider provider;
    
    @BeforeEach
    public void instantiate() {
        provider = new PrincipalAwareUpdateUserProvider();
    }
    
    @Test
    public void プリンシパルがない場合にはデフォルトユーザ名が表示される() {
        final String NAME = "Hekousa";
        provider.setDefaultUserName(NAME);
        Object name = provider.provide(null, null);
        assertThat(name, is(notNullValue()));
        assertThat(name, is(instanceOf(String.class)));
        assertThat((String) name, is(NAME));
    }
    
    @Test
    public void プリンシパルがある場合にはプリンシパル名が表示される() {
        final String NAME = "Hekousa";
        PrincipalKeeper.setPrincipal(new PrincipalStub(NAME));
        Object name = provider.provide(null, null);
        assertThat(name, is(notNullValue()));
        assertThat(name, is(instanceOf(String.class)));
        assertThat((String) name, is(NAME));
    }
}
