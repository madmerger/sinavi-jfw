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

package jp.co.ctc_g.jfw.core.jdbc.mybatis;

import java.lang.reflect.Field;
import java.util.List;

import jp.co.ctc_g.jfw.core.internal.InternalException;

import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * <p>
 * MyBatisの{@link StatementHandler}のクエリー情報にアクセスするためのAPIを提供します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class QueryInformation {

    private String query;

    private String preparedQuery;

    private String callableQuery;

    private SqlCommandType sqlCommandType;

    private StatementType statementType;

    private TypeHandlerRegistry typeHandlerRegistry;

    private StatementHandler statementHandler;

    private MappedStatement mappedStatement;

    private BoundSql boundSql;

    private Object parameterObject;

    private MetaObject metaObject;

    private List<ParameterMapping> parameterMappingList;

    /**
     * デフォルトコンストラクタです。
     */
    public QueryInformation() {}

    /**
     * コンストラクタです。
     * MyBatisの{@link StatementHandler}の解析に失敗した場合に{@link InternalException}をスローします。
     * @param statementHandler {@link StatementHandler}
     */
    public QueryInformation(StatementHandler statementHandler) {
        try {
            this.typeHandlerRegistry = new TypeHandlerRegistry();
            this.statementHandler = getConcreteStatementHandler(statementHandler);
            this.mappedStatement = getMappedStatement(this.statementHandler);
            this.boundSql = this.statementHandler.getBoundSql();
            this.parameterObject = this.boundSql.getParameterObject();
            this.parameterMappingList = boundSql.getParameterMappings();
            this.sqlCommandType = mappedStatement.getSqlCommandType();
            this.statementType = mappedStatement.getStatementType();
            this.metaObject = parameterObject == null ? null : MetaObject.forObject(parameterObject, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new org.apache.ibatis.reflection.DefaultReflectorFactory());

            switch (statementType) {
                case STATEMENT:
                    this.query = boundSql.getSql();
                    break;
                case PREPARED:
                    this.preparedQuery = boundSql.getSql();
                    break;
                case CALLABLE:
                    this.callableQuery = boundSql.getSql();
                    break;
                default:
                    throw new InternalException(QueryInformation.class, "");
            }
        } catch (Exception e) {
            throw new InternalException(QueryInformation.class, "E-JDBC-MYBATIS#0002", e);
        }
    }

    /**
     * SQLを返却します。
     * @return SQL
     */
    public String getQuery() {
        return this.query;
    }

    /**
     * プリペアードクエリを返却します。
     * @return プリペアードクエリ
     */
    public String getPreparedQuery() {
        return this.preparedQuery;
    }

    /**
     * コーラブルクエリを返却します。
     * @return コーラブルクエリ
     */
    public String getCallablString() {
        return this.callableQuery;
    }

    /**
     * SQLのコマンドタイプを返却します。
     * @return SQLのコマンドタイプ {@link SqlCommandType}
     */
    public SqlCommandType getSqlCommandType() {
        return this.sqlCommandType;
    }

    /**
     * ステートメントタイプを返却します。
     * @return ステートメントタイプ {@link StatementType}
     */
    public StatementType getStatementType() {
        return this.statementType;
    }

    /**
     * {@link TypeHandlerRegistry}を返却します。
     * @return ステートメントタイプ {@link TypeHandlerRegistry}
     */
    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return this.typeHandlerRegistry;
    }

    /**
     * コーラブルクエリを返却します。
     * @return コーラブルクエリ
     */
    public String getCallableQuery() {
        return this.callableQuery;
    }

    /**
     * {@link BoundSql}を返却します。
     * @return {@link BoundSql}
     */
    public BoundSql getBoundSql() {
        return this.boundSql;
    }

    /**
     * パラメータを表現するオブジェクトを返却します。
     * @return パラメータを表現するオブジェクト
     */
    public Object getParameterObject() {
        return this.parameterObject;
    }

    /**
     * {@link MetaObject}を返却します。
     * @return {@link MetaObject}
     */
    public MetaObject getMetaObject() {
        return this.metaObject;
    }

    /**
     * {@link ParameterMapping}のリストを返却します。
     * @return {@link ParameterMapping}のリスト
     */
    public List<ParameterMapping> getParameterMappingList() {
        return this.parameterMappingList;
    }

    private StatementHandler getConcreteStatementHandler(StatementHandler handler) throws Exception {
        RoutingStatementHandler routingStatementHandler = (RoutingStatementHandler)handler;
        Field filed = routingStatementHandler.getClass().getDeclaredField("delegate");
        filed.setAccessible(true);
        return (StatementHandler)filed.get(routingStatementHandler);        
    }

    private MappedStatement getMappedStatement(StatementHandler handler) throws Exception {
        Field filed = handler.getClass().getSuperclass().getDeclaredField("mappedStatement");
        filed.setAccessible(true);
        return (MappedStatement)filed.get(handler);
    }
}
