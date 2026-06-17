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

package jp.co.ctc_g.jfw.core.util;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

public class ClassesTest {

    @Test
    public void 距離0のhowFarテスト() {
        int distance = Classes.howFar(Object.class, Object.class);
        assertEquals(0, distance);
    }
    
    @Test
    public void 距離なしのhowFarテスト() {
        int distance = Classes.howFar(JFrame.class, SwingUtilities.class);
        assertEquals(-1, distance);
    }
    
    @Test
    public void 距離1のhowFarテスト() {
        int distance = Classes.howFar(JPanel.class, JComponent.class);
        assertEquals(1, distance);
    }
    
    @Test
    public void 距離4のhowFarテスト() {
        int distance = Classes.howFar(JPanel.class, Object.class);
        assertEquals(4, distance);
    }
}
