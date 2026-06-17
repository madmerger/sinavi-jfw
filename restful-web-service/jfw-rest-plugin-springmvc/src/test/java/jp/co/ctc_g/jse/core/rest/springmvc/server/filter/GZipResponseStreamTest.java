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

package jp.co.ctc_g.jse.core.rest.springmvc.server.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

public class GZipResponseStreamTest {

    private MockHttpServletResponse response;

    private GZipResponseStream res;

    @BeforeEach
    public void setup() throws Exception {
        response = new MockHttpServletResponse();
        res = new GZipResponseStream(response);
    }

    @Test
    public void クローズできる() throws Exception {
        res.close();
    }

    @Test
    public void フラッシュできる() throws Exception {
        res.flush();
    }

    @Test
    public void writeできる() throws Exception {
        res.write("aaaa".getBytes());
    }
}
