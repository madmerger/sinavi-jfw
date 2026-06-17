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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * このクラスは、サーブレットのレスポンス内容をgzip圧縮転送するための{@link javax.servlet.ServletOutputStream ServletOutputStream}拡張クラスです。
 * </p>
 * このクラスは{@link javax.servlet.ServletOutputStream}をラップし、このラッパを通した全てのレスポンスをgzip圧縮します。
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class GZipResponseStream extends ServletOutputStream {

    /**
     * バイト配列による出力ストリーム.
     */
    private ByteArrayOutputStream baos;

    /**
     * gzip圧縮のための出力ストリーム.
     */
    private GZIPOutputStream gzipStream;

    /**
     * ラッピングする元となるサーブレットレスポンス.
     */
    private HttpServletResponse response;

    /**
     * ラッピングする元となるサーブレットレスポンスの出力ストリーム.
     */
    private ServletOutputStream output;

    /**
     * デフォルトコンストラクタです。
     */
    public GZipResponseStream() {}

    /**
     * コンストラクタ.<br>
     * 指定したサーブレットレスポンスの出力ストリームをラップする. GZipResponseStreamを生成します。
     * @param response HTTPレスポンス
     * @throws java.io.IOException IO例外
     */
    public GZipResponseStream(HttpServletResponse response) throws IOException {
        super();
        this.response = response;
        this.output = response.getOutputStream();
        this.baos = new ByteArrayOutputStream();
        this.gzipStream = new GZIPOutputStream(baos);
    }

    /**
     * {@inheritDoc}
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException {
        this.gzipStream.finish();
        byte[] bytes = this.baos.toByteArray();
        this.response.addHeader("Content-Length", String.valueOf(bytes.length));
        this.response.addHeader("Content-Encoding", "gzip");
        this.baos.writeTo(output);
        this.baos.close();
        this.output.close();
    }

    /**
     * {@inheritDoc}
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException {
        this.gzipStream.flush();
    }

    /**
     * {@inheritDoc}
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException {
        this.gzipStream.write((byte) b);
    }

    /**
     * {@inheritDoc}
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(byte[] bytes) throws IOException {
        this.write(bytes, 0, bytes.length);
    }

    /**
     * {@inheritDoc}
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] bytes, int offset, int length) throws IOException {
        this.gzipStream.write(bytes, offset, length);
    }

    /**
     * ByteArrayOutputStreamをセットする.
     * @param baos ByteArrayOutputStreamのインスタンス
     */
    public void setBaos(ByteArrayOutputStream baos) {
        this.baos = baos;
    }

    /**
     * GZIPOutputStreamをセットする.
     * @param gzipStream GZIPOutputStreamのインスタンス
     */
    public void setGzipStream(GZIPOutputStream gzipStream) {
        this.gzipStream = gzipStream;
    }

    /**
     * ServletOutputStreamをセットする.
     * @param output ServletOutputStreamのインスタンス
     */
    public void setOutput(ServletOutputStream output) {
        this.output = output;
    }

    /**
     * HttpServletResponseをセットする.
     * @param response HttpServletResponseのインスタンス
     */
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        throw new UnsupportedOperationException();
    }
}
