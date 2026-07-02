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

package jp.co.ctc_g.jfw.xlsbeans.bean;

import java.util.Map;

import com.github.takezoe.xlsbeans.annotation.Column;
import com.github.takezoe.xlsbeans.annotation.MapColumns;

public class VerticalRecordTestBean {

    @Column(columnName = "カラム")
    public String column;

    @MapColumns(previousColumnName = "マッピングカラム")
    public Map<String, String> mapping;
}
