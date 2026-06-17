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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Test;


public class DateFormatTypeTest {

    @Test
    public void 正常にDateのインスタンスが生成できるかどうか() throws Exception {
        
        DateFormatType dft = new DateFormatType("yyyy/MM/dd HH:mm:ss.SSS");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        assertThat(dft.toString(), is("yyyy/MM/dd HH:mm:ss.SSS"));
        assertThat(dft.format(sdf.parse("2011/11/11 13:23:45.567")), is("2011/11/11 13:23:45.567"));
    }
    
}
