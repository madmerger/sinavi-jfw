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

package jp.co.ctc_g.jfw.xlsbeans.bean.field;

import java.io.Serializable;
import java.util.List;

import com.github.takezoe.xlsbeans.annotation.Sheet;
import jp.co.ctc_g.jse.core.excel.JxVerticalRecords;

@Sheet(name = "vertical")
public class VerticalFieldTestBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @JxVerticalRecords(recordClass = ListFieldTestData.class, tableLabel = "リストテスト")
    public List<ListFieldTestData> list;

    @JxVerticalRecords(recordClass = ArrayFieldDataTestBean.class, tableLabel = "配列テスト")
    public ArrayFieldDataTestBean[] array;

}
