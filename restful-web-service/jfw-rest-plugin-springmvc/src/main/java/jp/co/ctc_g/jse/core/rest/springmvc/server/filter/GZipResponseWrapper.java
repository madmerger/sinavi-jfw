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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * <p>
 * このクラスは、サーブレットのレスポンス内容をgzip圧縮転送するための{@link javax.servlet.http.HttpServletResponseWrapper HttpServletResponseWrapper}拡張クラスです。
 * </p>
 * <p>
 * このクラスは{@link javax.servlet.http.HttpServletResponseWrapper}をラップし、
 * このラッパを通した全てのレスポンスを{@link GZipResponseStream}経由でgzip圧縮します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class GZipResponseWrapper extends HttpServletResponseWrapper {

    /**
     * ラップ元のサーブレットレスポンス.
     */
    private HttpServletResponse response;

    /**
     * {@link GZipResponseStream}によりラッピングされたレスポンスの出力ストリーム.
     */
    private ServletOutputStream stream;

    /**
     * サーブレットレスポンスのライタ.
     */
    private PrintWriter writer;

    /**
     * サーブレットレスポンスを元にラッパーを生成します.
     * @param response サーブレットレスポンス
     */
    public GZipResponseWrapper(HttpServletResponse response) {
        super(response);
        this.response = response;
    }

    /**
     * ストリームをクローズします.
     * @throws IOException IO例外
     */
    public void close() throws IOException {
        if (this.writer != null) {
            this.writer.close();
        }
        if (stream != null) {
            this.stream.close();
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.servlet.ServletResponseWrapper#flushBuffer()
     */
    public void flushBuffer() throws IOException {
        this.stream.flush();
    }

    /**
     * {@inheritDoc}
     * @see javax.servlet.ServletResponseWrapper#getOutputStream()
     */
    public ServletOutputStream getOutputStream() throws IOException {
        if (this.stream == null) {
            this.stream = this.createOutputStream(this.response);
        }
        return this.stream;
    }

    /**
     * {@inheritDoc}
     * @see javax.servlet.ServletResponseWrapper#getWriter()
     */
    public PrintWriter getWriter() throws IOException {
        if (this.writer != null) { return this.writer; }
        this.stream = this.createOutputStream(this.response);
        this.writer = new PrintWriter(new OutputStreamWriter(stream, this.response.getCharacterEncoding()));
        return this.writer;
    }

    /**
     * 新規にgzip圧縮用ストリームを作成する.
     * @param res HTTPレスポンス
     * @return GZipResponseStream レスポンスをラップしたGZipResponseStream
     * @throws java.io.IOException IO例外
     */
    protected ServletOutputStream createOutputStream(HttpServletResponse res) throws IOException {
        return new GZipResponseStream(res);
    }

    /**
     * ServletOutputStreamを返す.
     * @return ServletOutputStream ServletOutputStreamのインスタンス
     */
    public ServletOutputStream getStream() {
        return stream;
    }

    /**
     * ServletOutputStreamをセットする.
     * @param stream ServletOutputStreamのインスタンス
     */
    public void setStream(ServletOutputStream stream) {
        this.stream = stream;
    }

    /**
     * HttpServletResponseをセットする.
     * @param response HttpServletResponseのインスタンス
     */
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
