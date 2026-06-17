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

package jp.co.ctc_g.jse.vid.adaptor;

import java.util.Enumeration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

@SuppressWarnings({ "rawtypes", "deprecation", "unchecked"})
public class HttpSessionAdaptor implements HttpSession {

    public Object getAttribute(String name) {

        return null;
    }

    public Enumeration getAttributeNames() {

        return null;
    }

    public long getCreationTime() {

        return 0;
    }

    public String getId() {

        return null;
    }

    public long getLastAccessedTime() {

        return 0;
    }

    public int getMaxInactiveInterval() {

        return 0;
    }

    public ServletContext getServletContext() {

        return null;
    }

    public Object getValue(String name) {

        return null;
    }

    public String[] getValueNames() {

        return null;
    }

    public void invalidate() {

        
    }

    public boolean isNew() {

        return false;
    }

    public void putValue(String name, Object value) {

        
    }

    public void removeAttribute(String name) {

        
    }

    public void removeValue(String name) {

        
    }

    public void setAttribute(String name, Object value) {

        
    }

    public void setMaxInactiveInterval(int interval) {

        
    }

}
