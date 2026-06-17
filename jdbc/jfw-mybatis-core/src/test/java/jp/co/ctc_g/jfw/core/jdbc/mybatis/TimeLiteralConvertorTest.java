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

package jp.co.ctc_g.jfw.core.jdbc.mybatis;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Time;
import java.util.Calendar;

import jp.co.ctc_g.jfw.core.internal.InternalException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TimeLiteralConvertorTest {

    private TimeLiteralConvertor target;

    @BeforeEach
    public void instantiate() {
        target = new TimeLiteralConvertor();
    }

    @Test
    public void Timeの値をリテラルに変換する() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(2011, 10, 1, 12, 0, 0);
        assertThat(target.convert(new Time(cal.getTimeInMillis())), is("'12:00:00'"));
    }

    @Test
    public void 引数がNULLであることを表明() throws Exception {
        assertThrows(InternalException.class, () -> {
            target.convert(null);
        });
    }
}
