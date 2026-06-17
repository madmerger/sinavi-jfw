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

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

public class DynamicArgsResolverTest {

    @Test
    public void test() {

        class Dyna {

            @SuppressWarnings("unused")
            public String needToResolve(String messege, Date today) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
                return messege + sdf.format(today);
            }
        }
        DynamicArgsResolver resolver = new DynamicArgsResolver();
        resolver.candidate(String.class, "メッセージ");
        resolver.candidate(Date.class, Dates.makeFrom(2011, 11));
        Method m = Reflects.findMethodNamed("needToResolve", Dyna.class);
        Object[] arguments = resolver.resolve(m);
        String ret = (String) Reflects.invoke(m, new Dyna(), arguments);
        assertThat(ret, is("メッセージ2011/11"));
    }

    @Test
    public void 実行できるかどうか() {

        class Dyna {

            @SuppressWarnings("unused")
            public String needToResolve(Integer m) {

                return m.toString();
            }
        }
        DynamicArgsResolver resolver = new DynamicArgsResolver();
        resolver.candidate(0);
        Method m = Reflects.findMethodNamed("needToResolve", Dyna.class);
        String ret = (String) resolver.invoke(m, new Dyna());
        assertThat(ret, is("0"));
    }

}
