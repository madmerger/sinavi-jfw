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

package jp.co.ctc_g.jse.core.rest.springmvc.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * このクラスは、SpringMVCの{@link RestOperations}を実装したクラスです。
 * </p>
 * <p>
 * SpringMVCのデフォルト実装である{@link org.springframework.web.client.RestTemplate}のAPIは
 * URLとURLの置換文字列が離れて指定しなければいけなかったり、
 * HTTPヘッダや独自の戻り値の型を利用しようとすると実装が大変になってしまいます。
 * </p>
 * <p>
 * {@link org.springframework.web.client.RestTemplate}のAPIを拡張し、より簡単にRESTクライアントとして開発できるように拡張しています。
 * このクラスを利用する場合は以下のようにSpringの設定ファイルに登録する必要があります。
 * <pre>
 *  &lt;bean id="restTemplate" class="org.springframework.web.client.RestTemplate" /&gt;
 *  &lt;bean id="restClientTemplate" class="jp.co.ctc_g.jse.core.framework.RestClientTemplate"&gt;
 *    &lt;property name="delegate" ref="restTemplate" /&gt;
 *  &lt;/bean&gt;
 * </pre>
 * SpringMVCデフォルトの{@link org.springframework.web.client.RestTemplate}をBeanとして登録し、
 * これをdelegateプロパティにインジェクションします。
 * </p>
 * @see RestClientOperations
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class RestClientTemplate implements RestClientOperations {

    /**
     * 拡張していない部分は処理を{@link RestOperations}に委譲します。
     */
    private RestOperations delegate;

    /**
     * デフォルトコンストラクタです。
     */
    public RestClientTemplate() {}
    
    /**
     * コンストラクタです。
     * @param delegate 拡張していない部分は処理を{@link RestOperations}に委譲します。
     */
    public RestClientTemplate(RestOperations delegate) {
        this.delegate = delegate;
    }

    /**
     * {@link RestOperations}を設定します。
     * @param delegate RestOperationsのインスタンス
     */
    public void setDelegate(RestOperations delegate) {
        this.delegate = delegate;
    }

    /**
     * {@link RestOperations}を取得します。
     * @return RestOperationsのインスタンス
     */
    protected RestOperations getDelegate() {
        return delegate;
    }
    
    /**
     * GETで対象URLにアクセスします。
     * 処理結果のBodyのみ必要な場合はこのメソッドを利用して下さい。
     * @param target 対象URL
     * @param responseType レスポンスのタイプ
     * @param <T> レスポンスのタイプ
     * @return 処理結果
     * @throws RestClientException 通信時に発生する例外
     */
    @Override
    public <T> T get(Target target, Class<T> responseType) throws RestClientException {
        return getForEntity(target, responseType).getBody();
    }
    
    /**
     * GETで対象URLにアクセスします。
     * 処理結果のステータスコードやヘッダなどが必要な場合はこのメソッドを利用して下さい。
     * @param target 対象URL
     * @param responseType レスポンスのタイプ
     * @param <T> レスポンスのタイプ
     * @return 処理結果
     * @throws RestClientException 通信時に発生する例外
     */
    @Override
    public <T> ResponseEntity<T> getForEntity(Target target, Class<T> responseType) throws RestClientException {
        return get(target.getUrl(), responseType);
    }
    
    /**
     * GETで対象URLにアクセスします。
     * 処理結果のステータスコードやヘッダなどが必要な場合はこのメソッドを利用して下さい。
     * @param url 対象URL
     * @param responseType レスポンスのタイプ
     * @param <T> レスポンスのタイプ
     * @return 処理結果
     * @throws RestClientException 通信時に発生する例外
     */
    protected <T> ResponseEntity<T> get(URI url, Class<T> responseType) throws RestClientException {
        return delegate.exchange(url, HttpMethod.GET, null, responseType);
    }

    /**
     * DELETEで対象URLにアクセスします。
     * データがない場合かつ処理結果のBodyのみ必要な場合はこのメソッドを利用して下さい。
     * @param target 対象URLや処理データなどを格納したオブジェクト
     * @param responseType レスポンスのタイプ
     * @param <T> レスポンスのタイプ
     * @return 処理結果
     * @throws RestClientException 通信時に発生する例外
     */
    @Override
    public <T> T delete(Target target, Class<T> responseType) throws RestClientException {
        return deleteForEntity(target, responseType).getBody();
    }
    
    /**
     * DELETEで対象URLにアクセスします。
     * 処理結果のBodyのみ必要な場合はこのメソッドを利用して下さい。
     * @param target 対象URL
     * @param entity 処理データなどを格納したオブジェクト
     * @param responseType レスポンスのタイプ
     * @param <T> レスポンスのタイプ
     * @return 処理結果
     * @throws RestClientException 通信時に発生する例外
     */
    @Override
    public <T> T delete(Target target, Entity<?> entity, Class<T> responseType) throws RestClientException {
        return deleteForEntity(target, entity, responseType).getBody();
    }

    /**
     * DELETEで対象URLにアクセスします。
     * データがない場合かつ処理結果のステータスコードやヘッダなどが必要な場合はこのメソッドを利用して下さい。
     * @param target 対象URL
     * @param responseType レスポンスのタイプ
     * @param <T> レスポンスのタイプ
     * @return 処理結果
     * @throws RestClientException 通信時に発生する例外
     */
    @Override
    public <T> ResponseEntity<T> deleteForEntity(Target target, Class<T> responseType) throws RestClientException {
        return delete(target.getUrl(), null, responseType);
    }
    
    /**
     * DELETEで対象URLにアクセスします。
     * 処理結果のステータスコードやヘッダなどが必要な場合はこのメソッドを利用して下さい。
     * @param target 対象URL
     * @param entity 処理データなどを格納したオブジェクト
     * @param responseType レスポンスのタイプ
     * @param <T> レスポンスのタイプ
     * @return 処理結果
     * @throws RestClientException 通信時に発生する例外
     */
    @Override
    public <T> ResponseEntity<T> deleteForEntity(Target target, Entity<?> entity, Class<T> responseType) throws RestClientException {
        return delete(target.getUrl(), entity, responseType);
    }

    /**
     * DELETEで対象URLにアクセスします。
     * @param url 対象URL
     * @param data 処理データ
     * @param responseType レスポンスのタイプ
     * @param <T> レスポンスのタイプ
     * @return 処理結果
     * @throws RestClientException 通信時に発生する例外
     */
    protected <T> ResponseEntity<T> delete(URI url, Entity<?> data, Class<T> responseType) throws RestClientException {
        return delegate.exchange(url, HttpMethod.DELETE, data != null ? data.get() : null, responseType);
    }

    /**
     * POSTで対象URLにアクセスします。
     * 処理結果のBodyのみ必要な場合はこのメソッドを利用して下さい。
     * @param target 対象URL
     * @param entity 処理データなどを格納したオブジェクト
     * @param responseType レスポンスのタイプ
     * @param <T> レスポンスのタイプ
     * @return 処理結果
     * @throws RestClientException 通信時に発生する例外
     */
    @Override
    public <T> T post(Target target, Entity<?> entity, Class<T> responseType) throws RestClientException {
        return postForEntity(target, entity, responseType).getBody();
    }

    /**
     * POSTで対象URLにアクセスします。
     * 処理結果のBodyのみ必要な場合はこのメソッドを利用して下さい。
     * @param target 対象URL
     * @param entity 処理データなどを格納したオブジェクト
     * @param responseType レスポンスのタイプ
     * @param <T> レスポンスのタイプ
     * @return 処理結果
     * @throws RestClientException 通信時に発生する例外
     */
    @Override
    public <T> ResponseEntity<T> postForEntity(Target target, Entity<?> entity, Class<T> responseType) throws  RestClientException {
        return post(target.getUrl(), entity, responseType);
    }

    /**
     * POSTで対象URLにアクセスします。
     * @param url 対象URL
     * @param data 処理データ
     * @param responseType レスポンスのタイプ
     * @param <T> レスポンスのタイプ
     * @return レスポンスエンティティ
     * @throws RestClientException 通信中に発生する例外
     */
    protected <T> ResponseEntity<T> post(URI url, Entity<?> data, Class<T> responseType) throws RestClientException {
        return delegate.exchange(url, HttpMethod.POST, data != null ? data.get() : null, responseType);
    }


    /**
     * PUTで対象URLにアクセスします。
     * 処理結果のBodyのみ必要な場合はこのメソッドを利用して下さい。
     * @param target 対象URL
     * @param entity 処理データなどを格納したオブジェクト
     * @param responseType レスポンスのタイプ
     * @param <T> レスポンスのタイプ
     * @return 処理結果
     * @throws RestClientException 通信中に発生する例外
     */
    public <T> T put(Target target, Entity<?> entity, Class<T> responseType) throws RestClientException {
        return putForEntity(target, entity, responseType).getBody();
    }

    
    /**
     * PUTで対象URLにアクセスします。
     * 処理結果のステータスコードやヘッダなどが必要な場合はこのメソッドを利用して下さい。
     * @param target 対象URL
     * @param entity 処理データなどを格納したオブジェクト
     * @param responseType レスポンスのタイプ
     * @param <T> レスポンスのタイプ
     * @return レスポンスエンティティ
     * @throws RestClientException 通信中に発生する例外
     */
    public <T> ResponseEntity<T> putForEntity(Target target, Entity<?> entity, Class<T> responseType) throws  RestClientException {
        return put(target.getUrl(), entity, responseType);
    }

    /**
     * PUTで対象URLにアクセスします。
     * @param url 対象URL
     * @param data 処理データ
     * @param responseType レスポンスのタイプ
     * @param <T> レスポンスのタイプ
     * @return レスポンスエンティティ
     * @throws RestClientException 通信中に発生する例外
     */
    protected <T> ResponseEntity<T> put(URI url, Entity<?> data, Class<T> responseType) throws RestClientException {
        return delegate.exchange(url, HttpMethod.PUT, data != null ? data.get() : null, responseType);
    }
    
    // 以下、単純な委譲 ---------------------------------------------------------------

    /**
     * このメソッドは {@link RestOperations#getForObject(String, Class, Object...)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return delegate.getForObject(url, responseType, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#getForObject(String, Class, Map)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return delegate.getForObject(url, responseType, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#getForObject(URI, Class)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> T getForObject(URI url, Class<T> responseType) throws RestClientException {
        return delegate.getForObject(url, responseType);
    }

    /**
     * このメソッドは {@link RestOperations#getForEntity(String, Class, Object...)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return delegate.getForEntity(url, responseType, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#getForEntity(String, Class, java.util.Map)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return delegate.getForEntity(url, responseType, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#getForEntity(java.net.URI, Class)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException {
        return delegate.getForEntity(url, responseType);
    }

    /**
     * このメソッドは {@link RestOperations#headForHeaders(String, Object...)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public HttpHeaders headForHeaders(String url, Object... uriVariables) throws RestClientException {
        return delegate.headForHeaders(url, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#headForHeaders(String, java.util.Map)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public HttpHeaders headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
        return delegate.headForHeaders(url, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#headForHeaders(URI)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public HttpHeaders headForHeaders(URI url) throws RestClientException {
        return delegate.headForHeaders(url);
    }

    /**
     * このメソッドは {@link RestOperations#postForLocation(String, Object, Object...)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public URI postForLocation(String url, Object request, Object... uriVariables) throws RestClientException {
        return delegate.postForLocation(url, request, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#postForLocation(String, Object, java.util.Map)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public URI postForLocation(String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
        return delegate.postForLocation(url, request, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#postForLocation(URI, Object)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public URI postForLocation(URI url, Object request) throws RestClientException {
        return delegate.postForLocation(url, request);
    }

    /**
     * このメソッドは {@link RestOperations#postForObject(String, Object, Class, Object...)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return delegate.postForObject(url, request, responseType, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#postForObject(String, Object, Class, Map)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> T postForObject(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return delegate.postForObject(url, request, responseType, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#postForObject(java.net.URI, Object, Class)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> T postForObject(URI url, Object request, Class<T> responseType) throws RestClientException {
        return delegate.postForObject(url, request, responseType);
    }

    /**
     * このメソッドは {@link RestOperations#postForEntity(String, Object, Class, Object...)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return delegate.postForEntity(url, request, responseType, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#postForEntity(String, Object, Class, java.util.Map)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return delegate.postForEntity(url, request, responseType, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#postForEntity(java.net.URI, Object, Class)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> ResponseEntity<T> postForEntity(URI url, Object request, Class<T> responseType) throws RestClientException {
        return delegate.postForEntity(url, request, responseType);
    }

    /**
     * このメソッドは {@link RestOperations#put(String, Object, Object...)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public void put(String url, Object request, Object... uriVariables) throws RestClientException {
        delegate.put(url, request, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#put(String, Object, java.util.Map)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public void put(String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
        delegate.put(url, request, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#put(java.net.URI, Object)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public void put(URI url, Object request) throws RestClientException {
        delegate.put(url, request);
    }

    /**
     * このメソッドは {@link RestOperations#delete(String, Object...)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public void delete(String url, Object... uriVariables) throws RestClientException {
        delegate.delete(url, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#delete(String, java.util.Map)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public void delete(String url, Map<String, ?> uriVariables) throws RestClientException {
        delegate.delete(url, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#delete(java.net.URI)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public void delete(URI url) throws RestClientException {
        delegate.delete(url);
    }

    /**
     * このメソッドは {@link RestOperations#optionsForAllow(String, Object...)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public Set<HttpMethod> optionsForAllow(String url, Object... uriVariables) throws RestClientException {
        return delegate.optionsForAllow(url, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#optionsForAllow(String, java.util.Map)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public Set<HttpMethod> optionsForAllow(String url, Map<String, ?> uriVariables) throws RestClientException {
        return delegate.optionsForAllow(url, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#optionsForAllow(java.net.URI)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public Set<HttpMethod> optionsForAllow(URI url) throws RestClientException {
        return delegate.optionsForAllow(url);
    }

    /**
     * このメソッドは {@link RestOperations#exchange(String, org.springframework.http.HttpMethod, org.springframework.http.HttpEntity, Class, Object...)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return delegate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#exchange(String, org.springframework.http.HttpMethod, org.springframework.http.HttpEntity, Class, java.util.Map)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return delegate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#exchange(java.net.URI, org.springframework.http.HttpMethod, org.springframework.http.HttpEntity, Class)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
        return delegate.exchange(url, method, requestEntity, responseType);
    }

    /**
     * このメソッドは {@link RestOperations#exchange(String, org.springframework.http.HttpMethod, org.springframework.http.HttpEntity, org.springframework.core.ParameterizedTypeReference, Object...)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {
        return delegate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#exchange(String, org.springframework.http.HttpMethod, org.springframework.http.HttpEntity, org.springframework.core.ParameterizedTypeReference, java.util.Map)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return delegate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#exchange(java.net.URI, org.springframework.http.HttpMethod, org.springframework.http.HttpEntity, org.springframework.core.ParameterizedTypeReference)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException {
        return delegate.exchange(url, method, requestEntity, responseType);
    }

    /**
     * このメソッドは {@link RestOperations#execute(String, org.springframework.http.HttpMethod, org.springframework.web.client.RequestCallback, org.springframework.web.client.ResponseExtractor, Object...)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor, Object... uriVariables) throws RestClientException {
        return delegate.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#execute(String, org.springframework.http.HttpMethod, org.springframework.web.client.RequestCallback, org.springframework.web.client.ResponseExtractor, java.util.Map)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException {
        return delegate.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    /**
     * このメソッドは {@link RestOperations#execute(java.net.URI, org.springframework.http.HttpMethod, org.springframework.web.client.RequestCallback, org.springframework.web.client.ResponseExtractor)} への単純な委譲です。
     * {@inheritDoc}
     */
    @Override
    public <T> T execute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) throws RestClientException {
        return delegate.execute(url, method, requestCallback, responseExtractor);
    }

    /** {@inheritDoc} */
    @Override
    public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
        return delegate.exchange(requestEntity, responseType);
    }

    /** {@inheritDoc} */
    @Override
    public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException {
        return delegate.exchange(requestEntity, responseType);
    }

    /** {@inheritDoc} */
    @Override
    public <T> T patchForObject(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return delegate.patchForObject(url, request, responseType, uriVariables);
    }

    /** {@inheritDoc} */
    @Override
    public <T> T patchForObject(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return delegate.patchForObject(url, request, responseType, uriVariables);
    }

    /** {@inheritDoc} */
    @Override
    public <T> T patchForObject(URI url, Object request, Class<T> responseType) throws RestClientException {
        return delegate.patchForObject(url, request, responseType);
    }

}
