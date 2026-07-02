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

package jp.co.ctc_g.jfw.core.jdbc;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jfw.core.util.typeconverter.TypeConverters;
import jp.co.ctc_g.jfw.paginate.Paginatable;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

/**
 * <p>
 * このクラスは、J-Framework により拡張された {@link SqlSession} です。
 * SqlSessionのラッパーとして動作します。
 * J-Frameworkによる拡張は主としてページングに関するそれです。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see JxSqlSession
 * @see JxSqlSessionFactory
 * @see JxSqlSessionFactoryBuilder
 * @see PaginationEnableMatcher
 * @see CountSqlResolver
 * @see PaginatedResultHandler
 * @see Paginatable
 */
public class JxSqlSession implements SqlSession {

    private SqlSession delegate;

    private PaginationEnableMatcher paginationEnableMatcher;
    private CountSqlResolver countSqlResolver;
    private PaginatedResultHandler paginatedResultHandler;

    /**
     * デフォルトコンストラクタです。
     */
    public JxSqlSession() {}

    /**
     * 指定された {@link SqlSession}を内部的に利用するこのクラスのインスタンスを生成します。
     * @param delegate 内部的に利用するSqlSession
     */
    public JxSqlSession(SqlSession delegate) {
        this.delegate = delegate;
    }

    /**
     * {@link Paginatable} を利用してページング検索をします。
     * @param statement 実行するSQLのID
     * @param parameter 検索条件とページング情報を肘しているパラメータ
     * @return ページングされた検索結果
     */
    protected <E> List<E> selectListWithPaginating(String statement, Paginatable parameter) {
        String countStatement = countSqlResolver.resolve(statement, parameter);
        Integer count = count(countStatement, parameter);
        List<E> result = delegate.selectList(statement, parameter);
        return paginatedResultHandler.createPaginatedResult(countStatement, parameter, count, result);
    }

    /**
     * {@link Paginatable} を利用してページング検索をします。
     * @param statement 実行するSQLのID
     * @param parameter 検索条件とページング情報を保持しているパラメータ
     * @return ページングされた検索結果
     */
    protected <E> List<E> selectListWithPaginating(String statement, Object parameter, RowBounds rowBounds) {
        String countStatement = countSqlResolver.resolve(statement, parameter);
        Integer count = count(countStatement, parameter);
        List<E> result = delegate.selectList(statement, parameter, rowBounds);
        return paginatedResultHandler.createPaginatedResult(countStatement, parameter, rowBounds, count, result);
    }

    /**
     * トータル件数を取得します。
     * @param statement 発行するSQLのID
     * @param parameter 検索条件を保持しているパラメータ
     * @return トータル件数
     */
    protected Integer count(String statement, Object parameter) {
        Object result = delegate.selectOne(statement, parameter);
        Integer count = TypeConverters.convert(result, Integer.class);
        return Args.proper(count, 0);
    }

    /**
     * 複数件のデータを検索します。
     * @param <E> 検索結果の型
     * @param statement 発行するSQLのID
     * @param parameter 検索条件を保持しているパラメータ
     * @return 検索結果
     */
    public <E> List<E> selectList(String statement, Object parameter) {
        return selectList(statement, parameter, RowBounds.DEFAULT);
    }

    /**
     * 複数件のデータを検索します。
     * もしも指定されたSQLのIDが{@link PaginationEnableMatcher}により、
     * ページング対象だと判定されたなら、
     * {@link #selectListWithPaginating(String, Paginatable)}あるいは
     * {@link #selectListWithPaginating(String, Object, RowBounds)}に処理を委譲します。
     * ページング境界オブジェクトである {@link RowBounds} は、
     * paramterが {@link Paginatable} でない場合に利用されます。
     * @param <E> 検索結果の型
     * @param statement 発行するSQLのID
     * @param parameter 検索条件を保持しているパラメータ
     * @param rowBounds ページング境界
     * @return 検索結果
     */
    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
        if (paginationEnableMatcher.match(statement, parameter)) {
            if (parameter instanceof Paginatable && RowBounds.DEFAULT.equals(rowBounds)) {
                return selectListWithPaginating(statement, (Paginatable) parameter);
            } else {
                return selectListWithPaginating(statement, parameter, rowBounds);
            }
        }
        return delegate.selectList(statement, parameter, rowBounds);
    }

    /**
     * {@link CountSqlResolver}を返却します。
     * @return {@link CountSqlResolver}
     */
    public CountSqlResolver getCountSqlResolver() {
        return countSqlResolver;
    }

    /**
     * {@link CountSqlResolver}を設定します。
     * @param countSqlResolver {@link CountSqlResolver}
     */
    public void setCountSqlResolver(CountSqlResolver countSqlResolver) {
        this.countSqlResolver = countSqlResolver;
    }

    /**
     * {@link PaginatedResultHandler}を返却します。
     * @return {@link PaginatedResultHandler}
     */
    public PaginatedResultHandler getPaginatedResultHandler() {
        return paginatedResultHandler;
    }

    /**
     * {@link PaginatedResultHandler}を設定します。
     * @param paginatedResultHandler {@link PaginatedResultHandler}
     */
    public void setPaginatedResultHandler(PaginatedResultHandler paginatedResultHandler) {
        this.paginatedResultHandler = paginatedResultHandler;
    }

    /**
     * {@link PaginationEnableMatcher}を返却します。
     * @return {@link PaginationEnableMatcher}
     */
    public PaginationEnableMatcher getPaginationEnableMatcher() {
        return paginationEnableMatcher;
    }

    /**
     *  {@link PaginationEnableMatcher}を設定します。
     * @param paginationEnableMatcher  {@link PaginationEnableMatcher}
     */
    public void setPaginationEnableMatcher(PaginationEnableMatcher paginationEnableMatcher) {
        this.paginationEnableMatcher = paginationEnableMatcher;
    }

    // 以下、単純な委譲 ---------------------------------------------------------------

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#selectOne(String)} への単純な委譲です。
     * {@inheritDoc}
     */
    public <T> T selectOne(String statement) {
        return delegate.selectOne(statement);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#selectOne(String, Object)} への単純な委譲です。
     * {@inheritDoc}
     */
    public <T> T selectOne(String statement, Object parameter) {
        return delegate.selectOne(statement, parameter);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#selectList(String)} への単純な委譲です。
     * {@inheritDoc}
     */
    public <E> List<E> selectList(String statement) {
        return delegate.selectList(statement);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#selectMap(String, String)} への単純な委譲です。
     * {@inheritDoc}
     */
    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        return delegate.selectMap(statement, mapKey);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#selectMap(String, Object, String)} への単純な委譲です。
     * {@inheritDoc}
     */
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
        return delegate.selectMap(statement, parameter, mapKey);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#selectMap(String, Object, String, RowBounds)} への単純な委譲です。
     * {@inheritDoc}
     */
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey,
            RowBounds rowBounds) {
        return delegate.selectMap(statement, parameter, mapKey, rowBounds);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#select(String, Object, RowBounds, ResultHandler)} への単純な委譲です。
     * {@inheritDoc}
     */
    public void select(String statement, Object parameter, ResultHandler handler) {
        delegate.select(statement, parameter, handler);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#select(String, ResultHandler)} への単純な委譲です。
     * {@inheritDoc}
     */
    public void select(String statement, ResultHandler handler) {
        delegate.select(statement, handler);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#select(String, Object, RowBounds, ResultHandler)} への単純な委譲です。
     * {@inheritDoc}
     */
    public void select(String statement, Object parameter, RowBounds rowBounds,
            ResultHandler handler) {
        delegate.select(statement, parameter, rowBounds, handler);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#insert(String)} への単純な委譲です。
     * {@inheritDoc}
     */
    public int insert(String statement) {
        return delegate.insert(statement);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#insert(String, Object)} への単純な委譲です。
     * {@inheritDoc}
     */
    public int insert(String statement, Object parameter) {
        return delegate.insert(statement, parameter);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#update(String)} への単純な委譲です。
     * {@inheritDoc}
     */
    public int update(String statement) {
        return delegate.update(statement);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#update(String, Object)} への単純な委譲です。
     * {@inheritDoc}
     */
    public int update(String statement, Object parameter) {
        return delegate.update(statement, parameter);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#delete(String)} への単純な委譲です。
     * {@inheritDoc}
     */
    public int delete(String statement) {
        return delegate.delete(statement);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#delete(String, Object)} への単純な委譲です。
     * {@inheritDoc}
     */
    public int delete(String statement, Object parameter) {
        return delegate.delete(statement, parameter);
    }


    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#commit()} への単純な委譲です。
     * {@inheritDoc}
     */
    public void commit() {
        delegate.commit();
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#commit(boolean)} への単純な委譲です。
     * {@inheritDoc}
     */
    public void commit(boolean force) {
        delegate.commit(force);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#rollback()} への単純な委譲です。
     * {@inheritDoc}
     */
    public void rollback() {
        delegate.rollback();
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#rollback(boolean)} への単純な委譲です。
     * {@inheritDoc}
     */
    public void rollback(boolean force) {
        delegate.rollback(force);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#close()} への単純な委譲です。
     * {@inheritDoc}
     */
    public void close() {
        delegate.close();
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#clearCache()} への単純な委譲です。
     * {@inheritDoc}
     */
    public void clearCache() {
        delegate.clearCache();
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#getConfiguration()} への単純な委譲です。
     * {@inheritDoc}
     */
    public Configuration getConfiguration() {
        return delegate.getConfiguration();
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#getMapper(Class)} への単純な委譲です。
     * {@inheritDoc}
     */
    public <T> T getMapper(Class<T> type) {
        return delegate.getMapper(type);
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#getConnection()} への単純な委譲です。
     * {@inheritDoc}
     */
    public Connection getConnection() {
        return delegate.getConnection();
    }

    /**
     * このメソッドは {@link org.apache.ibatis.session.SqlSession#flushStatements()} への単純な委譲です。
     * {@inheritDoc}
     */
    public List<BatchResult> flushStatements() {
        return delegate.flushStatements();
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement) {
        return delegate.selectCursor(statement);
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement, Object parameter) {
        return delegate.selectCursor(statement, parameter);
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds) {
        return delegate.selectCursor(statement, parameter, rowBounds);
    }
}
