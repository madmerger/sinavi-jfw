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

package jp.co.ctc_g.jse.core.token;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import jp.co.ctc_g.jse.core.token.IDGenerator;

import org.junit.jupiter.api.Test;

public class IDGeneratorTest {

    @Test
    public void トークIDを生成() {
        IDGenerator generator = new IDGenerator.DefaultIdGenerator();
        String tokenId = generator.generate();
        assertThat(tokenId, is(notNullValue()));
        assertThat(tokenId.length(), is(32));
    }

}
