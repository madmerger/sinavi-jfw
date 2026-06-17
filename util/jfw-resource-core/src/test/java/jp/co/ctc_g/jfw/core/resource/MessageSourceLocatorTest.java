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

package jp.co.ctc_g.jfw.core.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class MessageSourceLocatorTest {

    private ReloadableResourceBundleMessageSource messageSource;
    
    @BeforeEach
    public void setup() {
        messageSource = new ReloadableResourceBundleMessageSource();
    }

    @AfterEach
    public void teardown() {
        messageSource = null;
    }

    @Test
    public void デフォルト値が取得できるかどうか() {
        MessageSourceLocator.set(messageSource);
        MessageSource ms = MessageSourceLocator.get();
        assertThat(ms.getMessage("jfw.exception.UNDEFINED_ERROR_CODE", null, null), is(notNullValue()));
    }

    @Test
    public void オーバーライドしたキーのみが書き換わるかどうか() {
        messageSource.setBasename("classpath:/jp/co/ctc_g/jfw/core/resource/OverrideErrorResources");
        MessageSourceLocator.set(messageSource);
        MessageSource ms = MessageSourceLocator.get();
        assertThat(ms.getMessage("jfw.exception.UNDEFINED_ERROR_CODE", null, null), is("オーバライドしました。"));
    }

    @Test
    public void ロケールを指定した時に取得できるかどうか() {
        messageSource.setBasename("classpath:/jp/co/ctc_g/jfw/core/resource/LocaleResources");
        MessageSourceLocator.set(messageSource);
        MessageSource ms = MessageSourceLocator.get();
        assertThat(ms.getMessage("locale.test.key", null, Locale.ENGLISH), is("en"));
        assertThat(ms.getMessage("locale.test.key", null, Locale.JAPAN), is("日本"));
        assertThat(ms.getMessage("locale.test.key", null, Locale.JAPANESE), is("日本人"));
    }

    @Test
    public void リソースファイルがマージされるかどうか() {
        messageSource.setBasename("classpath:/jp/co/ctc_g/jfw/core/resource/MergedErrorResources");
        MessageSourceLocator.set(messageSource);
        MessageSource ms = MessageSourceLocator.get();
        assertThat(ms.getMessage("jfw.exception.UNDEFINED_ERROR_CODE", null, null), is(notNullValue()));
        assertThat(ms.getMessage("marged.key", null, null), is("マージされた"));
    }

    @Test
    public void XMLファイルの定義を参照できるかどうか() {
        messageSource.setBasename("classpath:/jp/co/ctc_g/jfw/core/resource/XmlResources");
        MessageSourceLocator.set(messageSource);
        MessageSource ms = MessageSourceLocator.get();
        assertThat(ms.getMessage("xml.properties.key", null, null), is("xmlに定義されたキーです。"));
    }

    @Test
    public void ファイルパスで参照できるかどうか() {
        messageSource.setBasename("file:src/test/resources/jp/co/ctc_g/jfw/core/resource/FilePathResources");
        MessageSourceLocator.set(messageSource);
        MessageSource ms = MessageSourceLocator.get();
        assertThat(ms.getMessage("file.load", null, null), is("ファイルパスでロード"));
    }
    
}
