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

import java.io.IOException;

import jakarta.servlet.jsp.JspWriter;

public class JspWriterAdaptor extends JspWriter {

    public JspWriterAdaptor() {
        super(0xff, false);
    }
    
    protected JspWriterAdaptor(int bufferSize, boolean autoFlush) {
        super(bufferSize, autoFlush);
    }

    @Override
    public void clear() throws IOException {

    }

    @Override
    public void clearBuffer() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public int getRemaining() {

        return 0;
    }

    @Override
    public void newLine() throws IOException {

    }

    @Override
    public void print(boolean b) throws IOException {

    }

    @Override
    public void print(char c) throws IOException {

    }

    @Override
    public void print(int i) throws IOException {

    }

    @Override
    public void print(long l) throws IOException {

    }

    @Override
    public void print(float f) throws IOException {

    }

    @Override
    public void print(double d) throws IOException {

    }

    @Override
    public void print(char[] s) throws IOException {

    }

    @Override
    public void print(String s) throws IOException {

    }

    @Override
    public void print(Object obj) throws IOException {

    }

    @Override
    public void println() throws IOException {

    }

    @Override
    public void println(boolean x) throws IOException {

    }

    @Override
    public void println(char x) throws IOException {

    }

    @Override
    public void println(int x) throws IOException {

    }

    @Override
    public void println(long x) throws IOException {

    }

    @Override
    public void println(float x) throws IOException {

    }

    @Override
    public void println(double x) throws IOException {

    }

    @Override
    public void println(char[] x) throws IOException {

    }

    @Override
    public void println(String x) throws IOException {

    }

    @Override
    public void println(Object x) throws IOException {

    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {

    }

}
