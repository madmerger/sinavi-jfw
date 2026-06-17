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

package jp.co.ctc_g.jfw.profill.util;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import jp.co.ctc_g.jfw.core.util.Beans;
import jp.co.ctc_g.jfw.profill.MatchedProperty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UpdateStampProviderTest {

    private UpdateStampProvider provider;
    
    @BeforeEach
    public void instantiate() {
        provider = new UpdateStampProvider();
    }
            
    @Test
    public void トランザクション連携をしている場合にはTransactionTimeKeeperの値を利用する() {
        provider.setTransactionAware(true);
        TransactionTimeKeeper.beginTransaction();
        assertThat(TransactionTimeKeeper.getBeginTimeAsTimestamp(), is(provider.getTimestamp()));
        assertThat(TransactionTimeKeeper.getBeginTimeAsDate(), is(provider.getDate()));
    }
    
    @Test
    public void 設定先のプロパティの型がTimestampならTimestamp型が返却される() {
        PropertyDescriptor d = Beans.findPropertyDescriptorFor(LocalTimeUpdateStampBean.class, "updateStamp");
        Object provided = provider.provide(new MatchedProperty(d), null);
        assertThat(provided, is(instanceOf(Timestamp.class)));
    }
    
    @Test
    public void 設定先のプロパティの型がDateならDate型が返却される() {
        PropertyDescriptor d = Beans.findPropertyDescriptorFor(LocalTimeUpdateStampBean.class, "updateDate");
        Object provided = provider.provide(new MatchedProperty(d), null);
        assertThat(provided, is(instanceOf(Date.class)));
    }
    
    @Test
    public void 設定先のプロパティの型がStringならString型が返却される() {
        PropertyDescriptor d = Beans.findPropertyDescriptorFor(LocalTimeUpdateStampBean.class, "updateString");
        Object provided = provider.provide(new MatchedProperty(d), null);
        assertThat(provided, is(instanceOf(String.class)));
    }
    
    public static class LocalTimeUpdateStampBean implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private Timestamp updateStamp;
        private Date updateDate;
        private String updateString;
        
        public Timestamp getUpdateStamp() {
            return updateStamp;
        }
        
        @UpdateStamp
        public void setUpdateStamp(Timestamp updateStamp) {
            this.updateStamp = updateStamp;
        }
        
        public Date getUpdateDate() {
            return updateDate;
        }
        
        @UpdateStamp
        public void setUpdateDate(Date updateDate) {
            this.updateDate = updateDate;
        }

        public String getUpdateString() {
            return updateString;
        }

        @UpdateStamp
        public void setUpdateString(String updateString) {
            this.updateString = updateString;
        }
    }
}
