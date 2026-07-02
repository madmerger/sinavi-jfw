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

package jp.co.ctc_g.jse.vid;

import java.util.LinkedList;

import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 * このクラスは、{@link ViewId 画面 ID} のコンテナを取得・保存するインタフェースです。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public interface ViewIdStore {

    /**
     * セッションIDを取得します。
     * @return セッションID
     */
    String semaphore();
    
    /**
     * 画面IDを保持するコンテナオブジェクトをセッションから取得します。
     * もし、セッションスコープに存在しない場合は新たにインスタンスを生成します。
     * @return コンテナオブジェクト
     */
    LinkedList<ViewId> find();
    
    /**
     * 画面IDを保持するコンテナオブジェクトをセッションから取得します。
     * もし、セッションに存在しない場合は新たにインスタンスを生成します。
     * @param needToCreate 新規にインスタンスを生成するかどうか。true：生成する、false:生成しない
     * @return コンテナオブジェクト
     */
    LinkedList<ViewId> find(boolean needToCreate);
    
    /**
     * 画面IDを保持しているコンテナオブジェクトをセッションに保存します。
     * @param container コンテナオブジェクト
     */
    void store(LinkedList<ViewId> container);
    
    /**
     * 画面IDを保持しているコンテナオブジェクトをセッションから破棄します。
     */
    void remove();
    
    /**
     * 指定した画面IDを保持している画面IDオブジェクトをコンテナオブジェクトから破棄します。
     * @param id 画面IDを表現する文字列
     */
    void remove(String id);
    
    /**
     * 画面IDのコンテナオブジェクトをどのスコープから取得するかどうかを決定するファクトリです。
     */
    class Factory {
        
        /**
         * {@link LazySessionStore}のインスタンスを生成します。
         * @param request リクエスト
         * @return {@link LazySessionStore}のインスタンス
         */
        public ViewIdStore create(HttpServletRequest request) {
            return new LazySessionStore(request);
        }
    }
}
