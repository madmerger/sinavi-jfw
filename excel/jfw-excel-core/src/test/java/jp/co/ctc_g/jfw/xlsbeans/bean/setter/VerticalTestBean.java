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

package jp.co.ctc_g.jfw.xlsbeans.bean.setter;

import java.io.Serializable;
import java.util.List;

import jp.co.ctc_g.jse.core.excel.JxVerticalRecords;
import com.github.takezoe.xlsbeans.annotation.Sheet;

@Sheet(name = "vertical")
public class VerticalTestBean implements Serializable {

    private static final long serialVersionUID = 1L;

    protected List<ListDataTestBean> list;

    private ArrayDataTestBean[] array;

    public List<ListDataTestBean> getList() {
        return list;
    }

    @JxVerticalRecords(recordClass = ListDataTestBean.class, tableLabel = "リストテスト")
    public void setList(List<ListDataTestBean> list) {
        this.list = list;
    }

    public ArrayDataTestBean[] getArray() {
        return array;
    }

    @JxVerticalRecords(recordClass = ArrayDataTestBean.class, tableLabel = "配列テスト")
    public void setArray(ArrayDataTestBean[] array) {
        this.array = array;
    }

}
