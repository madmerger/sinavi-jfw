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
import jakarta.servlet.http.HttpSession;

import static jp.co.ctc_g.jfw.core.util.Args.checkNotNull;

/**
 * <p>
 * このクラスは、セッションから{@link ViewId}のコンテナを取得・保存するインタフェースです。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class LazySessionStore implements ViewIdStore {
    
    protected HttpServletRequest request;

    /**
     * デフォルトコンストラクタです。
     */
    public LazySessionStore() {}

    /**
     * コンストラクタです。
     * @param request リクエスト
     */
    public LazySessionStore(HttpServletRequest request) {
        checkNotNull(request);
        this.request = request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String semaphore() {
        return request.getSession().getId().intern();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinkedList<ViewId> find() {
        return find(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public LinkedList<ViewId> find(boolean needToCreate) {
        HttpSession session = request.getSession();
        LinkedList<ViewId> ids = (LinkedList<ViewId>)
                session.getAttribute(ViewId.VIEW_ID_CONTAINER_KEY);
        if (ids == null && needToCreate) {
            ids = new LinkedList<ViewId>();
            session.setAttribute(ViewId.VIEW_ID_CONTAINER_KEY, ids);
        }
        return ids;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store(LinkedList<ViewId> container) {
        HttpSession session = request.getSession();
        session.setAttribute(ViewId.VIEW_ID_CONTAINER_KEY, container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
        HttpSession session = request.getSession();
        session.removeAttribute(ViewId.VIEW_ID_CONTAINER_KEY);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(String id) {
        LinkedList<ViewId> viewIds = find(false);
        if (viewIds != null) {
            for (int i = 0; i < viewIds.size(); i++) {
                if (viewIds.get(i).getId().equals(id))
                    viewIds.remove(i);
            }
        }
    }
}