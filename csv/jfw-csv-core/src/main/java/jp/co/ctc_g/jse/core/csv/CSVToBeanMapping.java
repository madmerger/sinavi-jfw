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

package jp.co.ctc_g.jse.core.csv;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jfw.core.internal.TargetThrowsException;
import jp.co.ctc_g.jfw.core.util.Beans;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jfw.core.util.typeconverter.TypeConversionException;
import jp.co.ctc_g.jfw.core.util.typeconverter.TypeConverters;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;

/**
 * <p>
 * このクラスはCSVの1行をJavaBeanに変換するマッピングクラスです。
 * </p>
 * <p>
 * OpenCSV 5.xのAPI変更に対応し、独自のBean変換ロジックを提供します。
 * </p>
 * @param <T> マッピング対象の型
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class CSVToBeanMapping<T> {

    private static final ResourceBundle R = InternalMessages.getBundle(CSVToBeanMapping.class);

    /**
     * デフォルトコンストラクタです。
     */
    public CSVToBeanMapping() {}

    /**
     * 1行読み込んだデータを指定されたマッピング情報に従って
     * インスタンスを生成します。
     * @param mapper {@link ColumnPositionMappingStrategy}
     * @param csv CSV読込ユーティリティ
     * @param line 1レコードのデータ配列
     * @return T 読込後のデータ
     */
    public T parse(ColumnPositionMappingStrategy<T> mapper, CSVReader csv, String[] line) {
        if (line == null || line.length == 0) {
            return null;
        }
        try {
            return convert(mapper, line);
        } catch (BindException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalException(CSVToBeanMapping.class, "E-CSV#0012", e);
        }
    }

    @SuppressWarnings("unchecked")
    private T convert(ColumnPositionMappingStrategy<T> mapper, String[] line) throws Exception {
        T bean = (T) mapper.getType().getDeclaredConstructor().newInstance();
        String[] columnMapping = mapper.getColumnMapping();
        List<BindError> errors = new ArrayList<BindError>();
        for (int col = 0; col < line.length && col < columnMapping.length; col++) {
            String propertyName = columnMapping[col];
            if (propertyName == null || propertyName.isEmpty()) {
                continue;
            }
            try {
                PropertyDescriptor prop = new PropertyDescriptor(propertyName, mapper.getType());
                Class<?> propertyType = prop.getPropertyType();
                String value = checkForTrim(line[col], prop);
                if (Strings.isEmpty(value)) {
                    continue;
                }
                try {
                    Object converted = TypeConverters.convert(value, propertyType);
                    Beans.writePropertyValueNamed(propertyName, bean, converted);
                } catch (TargetThrowsException e) {
                    errors.add(new BindError(bean.getClass().getSimpleName(), propertyName, e.getMessage()));
                } catch (TypeConversionException e) {
                    errors.add(new BindError(bean.getClass().getSimpleName(), propertyName, e.getMessage()));
                } catch (NumberFormatException e) {
                    errors.add(new BindError(bean.getClass().getSimpleName(), propertyName, e.getMessage()));
                }
            } catch (IntrospectionException e) {
                errors.add(new BindError(bean.getClass().getSimpleName(), propertyName, e.getMessage()));
            }
        }
        if (!errors.isEmpty()) throw new BindException(R.getString("E-CSV#0010"), errors);
        return bean;
    }

    private String checkForTrim(String s, PropertyDescriptor prop) {
        return trimmableProperty(prop) ? s.trim() : s;
    }

    private boolean trimmableProperty(PropertyDescriptor prop) {
        return !prop.getPropertyType().getName().contains("String");
    }

    /**
     * OpenCSV内部でのみ使用される、Numberプロパティエディタです。
     *
     * <p>
     * このクラスは、整数型ラッパークラスのデフォルトプロパティエディタが、
     * 文字列 "08"、"09" を８進数としてデコードしてしまう問題を回避するために
     * 作成されました。
     * </p>
     *
     * @author ITOCHU Techno-Solutions Corporation.
     */
    protected abstract static class NumberEditor extends PropertyEditorSupport {

        /**
         * {@inheritDoc}
         */
        @Override
        public String getJavaInitializationString() {
            Object value = getValue();
            return (value != null) ? value.toString() : "null";
        }

    }

    /**
     * OpenCSV内部でのみ使用される、Byteプロパティエディタです。
     * @author ITOCHU Techno-Solutions Corporation.
     */
    protected static class ByteEditor extends NumberEditor {

        /** {@inheritDoc} */
        @Override
        public String getJavaInitializationString() {
            Object value = getValue();
            return (value != null) ? "((byte)" + value + ")" : "null";
        }

        /** {@inheritDoc} */
        @Override
        public void setAsText(String text) {
            setValue((text == null) ? null : Byte.valueOf(text));
        }
    }

    /**
     * OpenCSV内部でのみ使用される、Integerプロパティエディタです。
     * @author ITOCHU Techno-Solutions Corporation.
     */
    protected static class IntegerEditor extends NumberEditor {

        /** {@inheritDoc} */
        @Override
        public void setAsText(String text) {
            setValue((text == null) ? null : Integer.valueOf(text));
        }

    }

    /**
     * OpenCSV内部でのみ使用される、Longプロパティエディタです。
     * @author ITOCHU Techno-Solutions Corporation.
     */
    protected static class LongEditor extends NumberEditor {

        /** {@inheritDoc} */
        @Override
        public String getJavaInitializationString() {
            Object value = getValue();
            return (value != null) ? value + "L" : "null";
        }

        /** {@inheritDoc} */
        @Override
        public void setAsText(String text) {
            setValue((text == null) ? null : Long.valueOf(text));
        }

    }

    /**
     * OpenCSV内部でのみ使用される、Shortプロパティエディタです。
     * @author ITOCHU Techno-Solutions Corporation.
     */
    protected static class ShortEditor extends NumberEditor {

        /** {@inheritDoc} */
        @Override
        public String getJavaInitializationString() {
            Object value = getValue();
            return (value != null) ? "((short)" + value + ")" : "null";
        }

        /** {@inheritDoc} */
        @Override
        public void setAsText(String text) {
            setValue((text == null) ? null : Short.valueOf(text));
        }

    }
}
