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

package jp.co.ctc_g.jse.core.framework;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

import jp.co.ctc_g.jfw.core.internal.Config;
import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.resource.Rs;
import jp.co.ctc_g.jfw.core.util.Arrays;
import jp.co.ctc_g.jfw.core.util.Beans;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.internal.WebCoreInternals;
import jp.co.ctc_g.jse.core.message.MessageContext;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.Lists;

/**
 * <p>
 * このクラスは、ポストバックの処理プロセスを管理します。<br/>
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public final class PostBackManager {

    private static final String BINDING_RESULT_KEY = PostBackManager.class.getName() + ".bindingResult";

    private static final String STORE_KEY_TO_REQUEST = PostBackManager.class.getName() + ".storeKeyToRequest";

    private static final String PARAMETER_MODEL_PARAMETER_PREFIX = "model:";

    private PostBack.Action[] actions;

    private HttpServletRequest request;

    private Class<?> modelAttributeType;

    private Class<?> targetControllerType;
    
    /**
     * 配列形式かどうかを判断する正規表現の文字列です。
     */
    protected static final String IDEXED_PATTERN_REGEX = "\\[\\d+\\]";
    
    /**
     * 配列形式かどうかを判断するパターンです。
     */
    protected static final Pattern INDEXED_PATTERN = Pattern.compile(IDEXED_PATTERN_REGEX);
    
    /**
     * 配列のインデクスを取得する正規表現の文字列です。
     */
    protected static final String INDEX_REGEX = "\\d+";
    
    /**
     * 配列のインデクスを取得するパターンです。
     */
    protected static final Pattern INDEX_PATTERN = Pattern.compile(INDEX_REGEX);
    
    private static final String DELIMITER_REGEX = "\\.";
    
    private static final String MESSAGE_TEMPLATE;
    private static final String INDEXED_MESSAGE_TEMPLATE;
    static {
        Config config = WebCoreInternals.getConfig(PostBackManager.class);
        MESSAGE_TEMPLATE = config.find("message_template");
        INDEXED_MESSAGE_TEMPLATE = config.find("indexed_message_template");
    }

    /**
     * コンストラクタです。
     * インスタンスの生成を抑止します。
     */
    private PostBackManager() {}

    /**
     * <p>
     * ディスパッチのタイプを表現する列挙型です。
     * </p>
     * @author ITOCHU Techno-Solutions Corporation.
     */
    public enum DispatchType {
        /**
         * JSPにディスパッチすることを表します。
         */
        JSP,

        /**
         * フォワードでディスパッチすることを表します。
         */
        FORWARD,

        /**
         * リダイレクトでディスパッチすることを表します。
         */
        REDIRECT
    }

    // インスタンス化 抑止
    private PostBackManager(HttpServletRequest request, HandlerMethod handlerMethod) {
        this.actions = findPostBackActionAnnotations(handlerMethod.getMethod());
        this.request = request;
    }

    private PostBack.Action getInternalPostBackAction(Throwable t) {
        if (actions != null) {
            for (PostBack.Action action : actions) {
                if (action.type().equals(t.getClass()))
                    return action;
            }
        }
        return null;
    }

    /**
     * {@link Controller} によって注釈されたコントローラのハンドラーメソッドに設定されている{@link PostBack#Action}注釈
     * を取得します。
     * @param method コントローラのハンドラーメソッド
     * @return {@link PostBack#Action}注釈の配列
     */
    public static PostBack.Action[] findPostBackActionAnnotations(Method method) {
        PostBack.Action[] actions = null;
        PostBack.Action i = AnnotationUtils.getAnnotation(method, PostBack.Action.class);
        if (i != null) {
            actions = Arrays.gen(i);
        } else {
            PostBack.Action.List actionList = AnnotationUtils.getAnnotation(method, PostBack.Action.List.class);
            if (actionList != null && actionList.value().length > 0) {
                actions = actionList.value();
            }
        }
        return actions;
    }

    /**
     * {@link Controller} によって注釈されたコントローラのハンドラーメソッドに設定されている{@link PostBack#Action}注釈
     * に、指定した例外タイプに対するアクションが定義されているかどうかを判定します。
     * @param method コントローラのハンドラーメソッド
     * @param exceptionType 例外タイプ
     * @return 指定した例外タイプのアクションが定義されている場合に{@code true}を返却します。
     */
    public static boolean isPostBackActionAnnotationDeclared(Method method, Type exceptionType) {
        PostBack.Action[] actions = findPostBackActionAnnotations(method);
        if (actions != null) {
            for (PostBack.Action action : actions) {
                if (action.type().equals(exceptionType))
                    return true;
            }
        }
        return false;
    }

    /**
     * コントローラのハンドラーメソッドに設定されている{@link PostBack#Action} 注釈から
     * 指定した例外タイプに対応するアクションが定義を検索し返却します。もし指定した例外タイプに対応する
     * アクション定義が存在しない場合は{@code null}が返却されます。
     * @param method コントローラのハンドラーメソッド
     * @param exceptionType 例外タイプ
     * @return 指定した例外に対応するアクション定義
     */
    public static PostBack.Action findPostBackActionAnnotation(Method method, Type exceptionType) {
        PostBack.Action[] actions = findPostBackActionAnnotations(method);
        for (PostBack.Action action : actions) {
            if (action.type().equals(exceptionType))
                return action;
        }
        return null;
    }

    private boolean isTargetException(Throwable t) {
        if (actions != null) {
            for (PostBack.Action action : actions) {
                if (action.type().equals(t.getClass()))
                    return true;
            }
        }
        return false;
    }

    Class<?> getModelAttributeType() {
        return this.modelAttributeType;
    }

    Class<?> getTargetControllerType() {
        return this.targetControllerType;
    }
    /**
     * <p>
     * 現在のリクエストに対してポストバック機構を開始します。
     * </p>
     * @param request リクエスト
     * @param handlerMethod ハンドラ
     */
    public static void begin(HttpServletRequest request, HandlerMethod handlerMethod) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        PostBackManager instance = new PostBackManager(request , handlerMethod);
        requestAttributes.setAttribute(STORE_KEY_TO_REQUEST, instance, RequestAttributes.SCOPE_REQUEST);
        MessageContext messageContext = (MessageContext) requestAttributes.getAttribute(MessageContext.MESSAGE_CONTEXT_ATTRIBUTE_KEY, RequestAttributes.SCOPE_REQUEST);
        if (messageContext == null) {
            requestAttributes.setAttribute(MessageContext.MESSAGE_CONTEXT_ATTRIBUTE_KEY, new MessageContext(request), RequestAttributes.SCOPE_REQUEST);
        }
        instance.targetControllerType = handlerMethod.getBeanType();
        for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
            ModelAttribute attr = methodParameter.getParameterAnnotation(ModelAttribute.class);
            if (attr != null) {
                instance.modelAttributeType = methodParameter.getParameterType();
            }
        }
    }

    /**
     * <p>
     * 現在のリクエストに対してポストバック機構を終了します。
     * </p>
     */
    public static void end() {
    }

    /**
     * {@link PostBack} インスタンスを保存します。
     * @param postBack {@link PostBack} インスタンス
     */
    public static void save(PostBack postBack) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        DispatchType dispatchType = getDispatchType(postBack.getException());
        switch (dispatchType) {
        case JSP:
            requestAttributes.setAttribute(PostBack.POST_BACK_ATTRIBUTE_KEY, postBack, RequestAttributes.SCOPE_REQUEST);
            break;
        case FORWARD:
            requestAttributes.setAttribute(PostBack.POST_BACK_ATTRIBUTE_KEY, postBack, RequestAttributes.SCOPE_REQUEST);
            break;
        case REDIRECT:
            PostBackManager instance = (PostBackManager) requestAttributes.getAttribute(STORE_KEY_TO_REQUEST, RequestAttributes.SCOPE_REQUEST);
            FlashMap flashMap = RequestContextUtils.getOutputFlashMap(instance.request);
            flashMap.put(PostBack.POST_BACK_ATTRIBUTE_KEY, postBack);
            break;
        default:
            throw new InternalException(PostBackManager.class, "E-POSTBACK#0001");
        }
    }

    /**
     * <p>
     * ポストバック処理をトリガーする例外でるかどうかを判定します。<br/>
     * </p>
     * @param t コントローラのハンドラ・メソッドがスローした例外
     * @return ポストバック処理をトリガーする例外である場合 {@code true}
     */
    public static boolean isPostBackTargetException(Throwable t) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        PostBackManager instance = (PostBackManager) requestAttributes.getAttribute(STORE_KEY_TO_REQUEST, RequestAttributes.SCOPE_REQUEST);
        return instance.isTargetException(t);
    }

    /**
     * 現在のリクエストに対応するコントローラのハンドラーメソッドに設定されている例外発生時のアクション定義（{@link PostBack#Action}注釈）
     * から、指定した例外に対応するアクションが定義を検索し返却します。
     * @param t 例外
     * @return 例外発生時のアクション定義（{@link PostBack#Action}注釈）
     */
    public static PostBack.Action getPostBackAction(Throwable t) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        PostBackManager instance = (PostBackManager) requestAttributes.getAttribute(STORE_KEY_TO_REQUEST, RequestAttributes.SCOPE_REQUEST);
        return instance.getInternalPostBackAction(t);
    }

    /**
     * {@link PostBack}インスタンスをリクエスト・スコープに保存します。
     * @param postBack {@link PostBack}インスタンス
     */
    public static void saveToRequest(PostBack postBack) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        requestAttributes.setAttribute(PostBack.POST_BACK_ATTRIBUTE_KEY, postBack, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * {@link PostBack}インスタンスをフラッシュ・スコープに保存します。
     * @param request {@link HttpServletRequest} インスタンス
     * @param postBack {@link PostBack}インスタンス
     */
    public static void saveToFlash(HttpServletRequest request, PostBack postBack) {
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        flashMap.put(PostBack.POST_BACK_ATTRIBUTE_KEY, postBack);
    }

    /**
     * 現在のリクエスト・スコープで有効な{@link PostBack}インスタンスを返却します。
     * @return 現在のリクエスト・スコープで有効な{@link PostBack}インスタンス
     */
    public static PostBack getCurrentPostBack() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return (PostBack) requestAttributes.getAttribute(PostBack.POST_BACK_ATTRIBUTE_KEY, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * 現在のリクエスト・スコープで有効な{@link PostBackManager}インスタンスを返却します。
     * @return 現在のリクエスト・スコープで有効な{@link PostBackManager}インスタンス
     */
    public static PostBackManager getCurrentPostBackManager() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return (PostBackManager) requestAttributes.getAttribute(STORE_KEY_TO_REQUEST, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * {@link BindingResult}インスタンスをリクエスト・スコープに保存します。
     * @param bindingResult {@link BindingResult}インスタンス
     */
    public static void saveBindingResult(BindingResult bindingResult) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        requestAttributes.setAttribute(BINDING_RESULT_KEY, bindingResult, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * 現在のリクエスト・スコープで有効な{@link BindingResult}インスタンスを返却します。
     * @return 現在のリクエスト・スコープで有効な{@link BindingResult}インスタンス
     */
    public static BindingResult getCurrentBindingResult() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return (BindingResult) requestAttributes.getAttribute(BINDING_RESULT_KEY, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * 現在のリクエスト・スコープで有効な{@link MessageContext}インスタンスを返却します。
     * @return 現在のリクエスト・スコープで有効な{@link MessageContext}インスタンス
     */
    public static MessageContext getMessageContext() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return (MessageContext) requestAttributes.getAttribute(MessageContext.MESSAGE_CONTEXT_ATTRIBUTE_KEY, RequestAttributes.SCOPE_REQUEST);
    }
    /**
     * <p>
     * キー文字列、値の順番で構成された配列を{@link Map}型に変換するユーティリティです。<br/>
     * 引数として設定する配列はキー文字列、値の順番で構成されていることを期待するためこれに違反する場合は、
     * {@link InternalException}がスローされます。
     * </p>
     * @param parameters キー文字列、値の順番で構成された配列
     * @return キー文字列、値の{@link Map} インスタンス
     */
    private static Map<String, String> parseParameterArray(String[] parameters) {
        if (parameters == null || parameters.length == 1)
            return null;

        if (parameters.length % 2 != 0)
            throw new InternalException(PostBackManager.class, "E-SPRINGMVC_EXT#0002");

        Map<String, String> m = new HashMap<String, String>();

        for (int i = 0; i < parameters.length; i = i + 2) {
            String key = parameters[i];
            String value = parameters[i + 1];
            if (Strings.isEmpty(key) || Strings.isEmpty(value))
                throw new InternalException(PostBackManager.class, "E-SPRINGMVC_EXT#0002");
            m.put(parameters[i], parameters[i + 1]);
        }

        return m;
    }

    /**
     * クエリ・パラメータ付きのURLを生成します。
     * @param path パス
     * @param parameters クエリ・パラメータが格納された{@link Map}インスタンス
     * @param encode エンコードの有無
     * @return クエリ・パラメータ付きのURL
     */
    public static String buildUri(String path, Map<String, String> parameters, boolean encode) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(path);
        if (!parameters.isEmpty()) {
            Set<String> sets = parameters.keySet();
            for (String key : sets) {
                builder.queryParam(key, parameters.get(key));
            }
        }
        if (encode) {
            return builder.build().encode().toString();
        } else {
            return builder.build().toString();
        }
    }

    /**
     * 現在のリクエストに対応するコントローラのハンドラーメソッドに設定されている例外発生時のアクション定義（{@link PostBack#Action}注釈）
     * から、指定した例外に対応するアクションが定義を検索しポスト・バックするURLを生成します。
     * URL生成時に指定した例外タイプに該当する{@link PostBack#Action}注釈の{@code params}属性を解析し
     * クエリ・パラメータを付加します。
     * @param t 例外タイプ
     * @param o モデルオブジェクト
     * @param encode エンコードの有無
     * @return ポスト・バック先URL
     */
    public static String buildUri(Throwable t, Object o, boolean encode) {
        PostBack.Action action = getPostBackAction(t);
        String path = action.value();
        String[] pathParameters = action.pathParams();
        if (pathParameters.length > 1) {
            path = Strings.substitute(path, values(o, pathParameters));
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(path);
        String[] parameters = action.params();
        if (parameters.length > 1) {
            Map<String, String> valueMaps = values(o, parameters);
            Set<String> sets = valueMaps.keySet();
            for (String key : sets) {
                builder.queryParam(key, valueMaps.get(key));
            }
        }
        if (encode) {
            return builder.build().encode().toString();
        } else {
            return builder.build().toString();
        }
    }
    
    protected static Map<String, String> values(Object o, String[] params) {
        Map<String, String> valueMaps = new HashMap<String, String>();
        Map<String, String> p = parseParameterArray(params);
        Set<String> sets = p.keySet();
        for (String key : sets) {
            String value = p.get(key);
            if (value.startsWith(PARAMETER_MODEL_PARAMETER_PREFIX)) {
                String propertyName = value.substring(PARAMETER_MODEL_PARAMETER_PREFIX.length());
                value = Beans.readPropertyValueNamed(propertyName, o).toString();
            }
            valueMaps.put(key, value);
        }
        return valueMaps;
    }

    /**
     * 現在のリクエストに対応するコントローラのハンドラーメソッドに設定されている例外発生時のアクション定義（{@link PostBack#Action}注釈）
     * から、指定した例外に対応するアクションが定義を検索しディスパッチ・タイプを返却します。
     * @param t 例外タイプ
     * @return ディスパッチ・タイプ({@link DispatchType}インスタンス)
     */
    public static DispatchType getDispatchType(Throwable t) {
        PostBack.Action action = getPostBackAction(t);
        String path = action.value();
        String typeString = path.indexOf(":") != -1 ? path.substring(0, path.indexOf(":") + 1) : "";
        if (Controllers.FORWARD.equals(typeString)) {
            return DispatchType.FORWARD;
        } else if (Controllers.REDIRECT.equals(typeString)) {
            return DispatchType.REDIRECT;
        } else {
            return DispatchType.JSP;
        }
    }

    /**
     * メッセージを保存します。
     * @param t 例外
     */
    public static void saveMessage(Throwable t) {
        if (t instanceof BindException) {
            saveValidationMessage((BindException)t);
        } else {
            saveErrorMessage(t);
        }
    }

    private static void saveValidationMessage(BindException e) {
        PostBack.Action action = getPostBackAction(e);
        BindingResult br = e.getBindingResult();
        String modelName = e.getObjectName();
        if (br != null && br.hasFieldErrors()) {
            List<FieldError> fieldErrors = br.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                String msg = detectValidationMessage(action, fieldError);
                DispatchType dispatchType = getDispatchType(e);
                switch(dispatchType) {
                case JSP:
                case FORWARD:
                    getMessageContext().saveValidationMessageToRequest(msg, fieldError.getField(), fieldError.getCodes()[3], modelName);
                    break;
                case REDIRECT:
                    getMessageContext().saveValidationMessageToFlash(msg, fieldError.getField(), fieldError.getCodes()[3], modelName);
                    break;
                default:
                    throw new InternalException(PostBackManager.class, "E-POSTBACK#0001");
                }
            }
        }
    }
    
    private static String detectValidationMessage(PostBack.Action action, FieldError fieldError) {
        String msg = fieldError.getDefaultMessage();
        if (action.template()) {
            String label = Rs.find(fieldError);
            if (!msg.equals(label)) {
                msg = Strings.substitute(MESSAGE_TEMPLATE, Maps.hash("label", label).map("msg", fieldError.getDefaultMessage()));
                int index = getIndex(fieldError.getField());
                if (index != -1) {
                    msg = Strings.substitute(INDEXED_MESSAGE_TEMPLATE, Maps.hash("label", label).map("msg", fieldError.getDefaultMessage()).map("index", Integer.toString(index)));
                }
            }
        }
        return msg;
    }

    private static void saveErrorMessage(Throwable t) {
        MessageContext messageContext = getMessageContext();
        DispatchType dispatchType = getDispatchType(t);
        switch (dispatchType) {
        case JSP:
        case FORWARD:
            messageContext.saveErrorMessageToRequest(t.getMessage());
            break;
        case REDIRECT:
            messageContext.saveErrorMessageToFlash(t.getMessage());
            break;
        default:
            throw new InternalException(PostBackManager.class, "E-POSTBACK#0001");
        }
    }
    
    /**
     * 配列のインデクス番号を返します。(オリジンは1です。)
     * インデクス番号はネストした最後の配列番号を利用します。
     * つまり、hoge.foo[1].bar[2].nameでエラーが発生した場合のインデクス番号は3番目(barの配列の+1)となります。
     * @return インデクス番号
     */
    private static int getIndex(String field) {
        String[] ps = field.split(DELIMITER_REGEX);
        for (String p : Lists.reverse(java.util.Arrays.asList(ps))) {
            Matcher m = INDEXED_PATTERN.matcher(p);
            if (m.find()) {
                String index = "";
                Matcher i = INDEX_PATTERN.matcher(m.group(0));
                if (i.find()) {
                    index = i.group();
                }
                return Integer.parseInt(index) + 1;
            }
        }
        return -1;
    }
    
}
