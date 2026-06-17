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

package jp.co.ctc_g.jfw.core.jdbc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultCountSqlResolverTest {

    protected DefaultCountSqlResolver resolver;
    
    @BeforeEach
    public void instantiate() {
        resolver = new DefaultCountSqlResolver();
    }
    
    @Test
    public void デフォルトプレフィックスとサフィックスを利用できる() {
        assertThat(resolver.getPrefix(), is(""));
        assertThat(resolver.getSuffix(), is(DefaultCountSqlResolver.DEFAULT_SUFFIX));
        assertThat(resolver.resolve("test", null), is("test::count"));
    }
    
    @Test
    public void 任意のプレフィックスとサフィックスを利用できる() {
        resolver.setPrefix("prefix");
        resolver.setSuffix("suffix");
        assertThat(resolver.resolve("test", null), is("prefixtestsuffix"));
    }
}
