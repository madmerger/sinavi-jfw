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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.Test;

public class BeansTest {

    @Test
    public void capitalizeテスト() {
        final String[] suspects = Arrays.gen(
                "",
                "a", "A",
                "ab", "Ab", "aB", "AB",
                "abc", "Abc", "aBc", "abC", "ABc", "AbC", "aBC",
                "a_b", "a_b_c", "A_b_c", "A_BC", "AB_C");
        final String[] expected = Arrays.gen(
                "",
                "A", "A",
                "Ab", "Ab", "aB", "AB",
                "Abc", "Abc", "aBc", "AbC", "ABc", "AbC", "aBC",
                "A_b", "A_b_c", "A_b_c", "A_BC", "AB_C");
        Arrays.each(suspects, new EachCall<String>() {
            public void each(String element, int index, int total) {
                String result = Beans.capitalize(element);
                assertEquals(expected[index], result);
            }
        });
    }

    @Test
    public void decapitalizeテスト() {
        final String[] suspects = Arrays.gen(
                "",
                "a", "A",
                "ab", "Ab", "aB", "AB",
                "abc", "Abc", "aBc", "abC", "ABc", "AbC", "aBC",
                "a_b", "a_b_c", "A_b_c", "A_BC", "AB_C");
        final String[] expected = Arrays.gen(
                "",
                "a", "a",
                "ab", "ab", "aB", "AB",
                "abc", "abc", "aBc", "abC", "ABc", "abC", "aBC",
                "a_b", "a_b_c", "a_b_c", "a_BC", "AB_C");
        Arrays.each(suspects, new EachCall<String>() {
            public void each(String element, int index, int total) {
                String result = Beans.decapitalize(element);
                assertEquals(expected[index], result);
            }
        });
    }

    // プロパティリード -----------------------------------------------------------------

    @Test
    public void readPropertyValueNamedシンプルテスト() {
        String value = (String) Beans.readPropertyValueNamed("prop", new Object() {
            @SuppressWarnings("unused")
            public String getProp() {
                return "prop";
            }
        });
        assertNotNull(value);
        assertEquals("prop", value);
    }

    @Test
    public void readPropertyValueNamedネストテスト() {
        Object target = new Object() {
            private Object prop = new Object() {
                @SuppressWarnings("unused")
                public String getNested() {
                    return "nested";
                }
            };
            @SuppressWarnings("unused")
            public Object getProp() {
                return prop;
            }
        };
        String value = (String) Beans.readPropertyValueNamed("prop.nested", target);
        assertNotNull(value);
        assertEquals("nested", value);
    }

    // プロパティ配列リードテスト -------------------------------------------------------------------------

    @Test
    public void readPropertyValueNamedネスト配列テスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object getProp() {
                return new Object[] {"0", "1", "2"};
            }
        };
        String value = (String) Beans.readPropertyValueNamed("prop[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPropertyValueNamed("prop[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPropertyValueNamed("prop[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void readPropertyValueNamedネストネスト配列テスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object getProp() {
                return new Object() {
                    public Object[] getNested() {
                        return new Object[] {"0", "1", "2"};
                    }
                };
            }
        };
        String value = (String) Beans.readPropertyValueNamed("prop.nested[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPropertyValueNamed("prop.nested[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPropertyValueNamed("prop.nested[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void readPropertyValueNamedネスト配列ネスト配列テスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object[] getProp() {
                return new Object[] {
                    new Object() {
                        public Object[] getNested() {
                            return new Object[] {"0", "1", "2"};
                        }
                    },
                    new Object() {
                        public Object[] getNested() {
                            return new Object[] {"0", "1", "2"};
                        }
                    },
                    new Object() {
                        public Object[] getNested() {
                            return new Object[] {"0", "1", "2"};
                        }
                    }
                };
            }
        };
        for (int i = 0; i < 3; i++) {
            String value = (String) Beans.readPropertyValueNamed("prop[" + i +"].nested[0]", target);
            assertNotNull(value);
            assertEquals("0", value);
            value = (String) Beans.readPropertyValueNamed("prop[" + i +"].nested[1]", target);
            assertNotNull(value);
            assertEquals("1", value);
            value = (String) Beans.readPropertyValueNamed("prop[" + i +"].nested[2]", target);
            assertNotNull(value);
            assertEquals("2", value);
        }
    }

    // プロパティリストテスト -----------------------------------------------------------------------------

    @Test
    public void readPropertyValueNamedネストリストテスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object getProp() {
                return Lists.gen("0", "1", "2");
            }
        };
        String value = (String) Beans.readPropertyValueNamed("prop[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPropertyValueNamed("prop[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPropertyValueNamed("prop[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void readPropertyValueNamedネストネストリストテスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object getProp() {
                return new Object() {
                    public List<String> getNested() {
                        return Lists.gen("0", "1", "2");
                    }
                };
            }
        };
        String value = (String) Beans.readPropertyValueNamed("prop.nested[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPropertyValueNamed("prop.nested[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPropertyValueNamed("prop.nested[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void readPropertyValueNamedネストリストネストリストテスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object[] getProp() {
                return new Object[] {
                    new Object() {
                        public List<String> getNested() {
                            return Lists.gen("0", "1", "2");
                        }
                    },
                    new Object() {
                        public List<String> getNested() {
                            return Lists.gen("0", "1", "2");
                        }
                    },
                    new Object() {
                        public List<String> getNested() {
                            return Lists.gen("0", "1", "2");
                        }
                    }
                };
            }
        };
        for (int i = 0; i < 3; i++) {
            String value = (String) Beans.readPropertyValueNamed("prop[" + i +"].nested[0]", target);
            assertNotNull(value);
            assertEquals("0", value);
            value = (String) Beans.readPropertyValueNamed("prop[" + i +"].nested[1]", target);
            assertNotNull(value);
            assertEquals("1", value);
            value = (String) Beans.readPropertyValueNamed("prop[" + i +"].nested[2]", target);
            assertNotNull(value);
            assertEquals("2", value);
        }
    }

    // スードプロパティリード -----------------------------------------------------------------

    @Test
    public void readPseudoPropertyValueNamedシンプルテスト() {
        String value = (String) Beans.readPseudoPropertyValueNamed("prop", new Object() {
            @SuppressWarnings("unused") public String prop = "prop";
        });
        assertNotNull(value);
        assertEquals("prop", value);
    }

    @Test
    public void readPseudoPropertyValueNamedネストテスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object prop = new Object() {
                public String nested = "nested";
            };
        };
        String value = (String) Beans.readPseudoPropertyValueNamed("prop.nested", target);
        assertNotNull(value);
        assertEquals("nested", value);
    }

    // スードプロパティ配列テスト -------------------------------------------------------------------------

    @Test
    public void readPseudoPropertyValueNamedネスト配列テスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object prop = new Object[] {"0", "1", "2"};
        };
        String value = (String) Beans.readPseudoPropertyValueNamed("prop[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void readPseudoPropertyValueNamedネストネスト配列テスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object prop = new Object() {
                    public Object[] nested = new Object[] {"0", "1", "2"};
            };
        };
        String value = (String) Beans.readPseudoPropertyValueNamed("prop.nested[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop.nested[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop.nested[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void readPseudoPropertyValueNamedネスト配列ネスト配列テスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object[] prop = new Object[] {
                new Object() {
                    public Object[] nested = new Object[] {"0", "1", "2"};
                },
                new Object() {
                    public Object[] nested = new Object[] {"0", "1", "2"};
                },
                new Object() {
                    public Object[] nested = new Object[] {"0", "1", "2"};
                }
            };
        };
        for (int i = 0; i < 3; i++) {
            String value = (String) Beans.readPseudoPropertyValueNamed("prop[" + i +"].nested[0]", target);
            assertNotNull(value);
            assertEquals("0", value);
            value = (String) Beans.readPseudoPropertyValueNamed("prop[" + i +"].nested[1]", target);
            assertNotNull(value);
            assertEquals("1", value);
            value = (String) Beans.readPseudoPropertyValueNamed("prop[" + i +"].nested[2]", target);
            assertNotNull(value);
            assertEquals("2", value);
        }
    }

    // スードプロパティリストテスト -----------------------------------------------------------------------------

    @Test
    public void readPseudoPropertyValueNamedネストリストテスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object prop = Lists.gen("0", "1", "2");
        };
        String value = (String) Beans.readPseudoPropertyValueNamed("prop[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void readPseudoPropertyValueNamedネストネストリストテスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object prop = new Object() {
                public List<String> nested = Lists.gen("0", "1", "2");
            };
        };
        String value = (String) Beans.readPseudoPropertyValueNamed("prop.nested[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop.nested[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop.nested[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void readPseduoPropertyValueNamedネストリストネストリストテスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object[] prop = new Object[] {
                new Object() {
                    public List<String> nested = Lists.gen("0", "1", "2");
                },
                new Object() {
                    public List<String> nested = Lists.gen("0", "1", "2");
                },
                new Object() {
                    public List<String> nested =Lists.gen("0", "1", "2");
                }
            };
        };
        for (int i = 0; i < 3; i++) {
            String value = (String) Beans.readPseudoPropertyValueNamed("prop[" + i +"].nested[0]", target);
            assertNotNull(value);
            assertEquals("0", value);
            value = (String) Beans.readPseudoPropertyValueNamed("prop[" + i +"].nested[1]", target);
            assertNotNull(value);
            assertEquals("1", value);
            value = (String) Beans.readPseudoPropertyValueNamed("prop[" + i +"].nested[2]", target);
            assertNotNull(value);
            assertEquals("2", value);
        }
    }

    // プロパティライト -----------------------------------------------------------------

    @Test
    public void writePropertyValueNamedシンプルテスト() {
        Object target = new Object() {
            private String prop;
            @SuppressWarnings("unused")
            public String getProp() {
                return prop;
            }
            @SuppressWarnings("unused")
            public void setProp(String prop) {
                this.prop = prop;
            }
        };
        Beans.writePropertyValueNamed("prop", target, "prop");
        String value = (String) Beans.readPropertyValueNamed("prop", target);
        assertNotNull(value);
        assertEquals("prop", value);
    }

    @Test
    public void writePropertyValueNamedネストテスト() {
        Object target = new Object() {
            private Object prop = new Object() {
                private String nested;
                @SuppressWarnings("unused")
                public String getNested() {
                    return nested;
                }
                @SuppressWarnings("unused")
                public void setNested(String nested) {
                    this.nested = nested;
                }
            };
            @SuppressWarnings("unused")
            public Object getProp() {
                return prop;
            }
        };
        Beans.writePropertyValueNamed("prop.nested", target, "nested");
        String value = (String) Beans.readPropertyValueNamed("prop.nested", target);
        assertNotNull(value);
        assertEquals("nested", value);
    }

    // プロパティ配列ライトテスト -------------------------------------------------------------------------

    @Test
    public void writePropertyValueNamedネスト配列テスト() {
        Object target = new Object() {
            private String[] prop = new String[3];
            @SuppressWarnings("unused")
            public Object getProp() {
                return prop;
            }
        };
        Beans.writePropertyValueNamed("prop[0]", target, "0");
        Beans.writePropertyValueNamed("prop[1]", target, "1");
        Beans.writePropertyValueNamed("prop[2]", target, "2");
        String value = (String) Beans.readPropertyValueNamed("prop[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPropertyValueNamed("prop[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPropertyValueNamed("prop[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void writePropertyValueNamedネストネスト配列テスト() {
        Object target = new Object() {
            private String[] nested = new String[3];
            @SuppressWarnings("unused")
            public Object getProp() {
                return new Object() {
                    public Object[] getNested() {
                        return nested;
                    }
                };
            }
        };
        Beans.writePropertyValueNamed("prop.nested[0]", target, "0");
        Beans.writePropertyValueNamed("prop.nested[1]", target, "1");
        Beans.writePropertyValueNamed("prop.nested[2]", target, "2");
        String value = (String) Beans.readPropertyValueNamed("prop.nested[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPropertyValueNamed("prop.nested[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPropertyValueNamed("prop.nested[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void writePropertyValueNamedネスト配列ネスト配列テスト() {
        Object target = new Object() {
            private String[] nested1 = new String[3];
            private String[] nested2 = new String[3];
            private String[] nested3 = new String[3];
            @SuppressWarnings("unused")
            public Object[] getProp() {
                return new Object[] {
                    new Object() {
                        public Object[] getNested() {
                            return nested1;
                        }
                    },
                    new Object() {
                        public Object[] getNested() {
                            return nested2;
                        }
                    },
                    new Object() {
                        public Object[] getNested() {
                            return nested3;
                        }
                    }
                };
            }
        };
        for (int i = 0; i < 3; i++) {
            Beans.writePropertyValueNamed("prop[" + i + "].nested[0]", target, "0");
            Beans.writePropertyValueNamed("prop[" + i + "].nested[1]", target, "1");
            Beans.writePropertyValueNamed("prop[" + i + "].nested[2]", target, "2");
        }
        for (int i = 0; i < 3; i++) {
            String value = (String) Beans.readPropertyValueNamed("prop[" + i +"].nested[0]", target);
            assertNotNull(value);
            assertEquals("0", value);
            value = (String) Beans.readPropertyValueNamed("prop[" + i +"].nested[1]", target);
            assertNotNull(value);
            assertEquals("1", value);
            value = (String) Beans.readPropertyValueNamed("prop[" + i +"].nested[2]", target);
            assertNotNull(value);
            assertEquals("2", value);
        }
    }

    // プロパティリストテスト -----------------------------------------------------------------------------

    @Test
    public void writePropertyValueNamedネストリストテスト() {
        Object target = new Object() {
            private List<String> prop = Lists.gen("", "", "");
            @SuppressWarnings("unused")
            public Object getProp() {
                return prop;
            }
        };
        Beans.writePropertyValueNamed("prop[0]", target, "0");
        Beans.writePropertyValueNamed("prop[1]", target, "1");
        Beans.writePropertyValueNamed("prop[2]", target, "2");
        String value = (String) Beans.readPropertyValueNamed("prop[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPropertyValueNamed("prop[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPropertyValueNamed("prop[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void writePropertyValueNamedネストネストリストテスト() {
        Object target = new Object() {
            private List<String> nested = Lists.gen("", "", "");
            @SuppressWarnings("unused")
            public Object getProp() {
                return new Object() {
                    public List<String> getNested() {
                        return nested;
                    }
                };
            }
        };
        Beans.writePropertyValueNamed("prop.nested[0]", target, "0");
        Beans.writePropertyValueNamed("prop.nested[1]", target, "1");
        Beans.writePropertyValueNamed("prop.nested[2]", target, "2");
        String value = (String) Beans.readPropertyValueNamed("prop.nested[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPropertyValueNamed("prop.nested[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPropertyValueNamed("prop.nested[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void writePropertyValueNamedネストリストネストリストテスト() {
        Object target = new Object() {
            private List<String> nested1 = Lists.gen("", "", "");
            private List<String> nested2 = Lists.gen("", "", "");
            private List<String> nested3 = Lists.gen("", "", "");
            @SuppressWarnings("unused")
            public Object[] getProp() {
                return new Object[] {
                    new Object() {
                        public List<String> getNested() {
                            return nested1;
                        }
                    },
                    new Object() {
                        public List<String> getNested() {
                            return nested2;
                        }
                    },
                    new Object() {
                        public List<String> getNested() {
                            return nested3;
                        }
                    }
                };
            }
        };
        for (int i = 0; i < 3; i++) {
            Beans.writePropertyValueNamed("prop[" + i + "].nested[0]", target, "0");
            Beans.writePropertyValueNamed("prop[" + i + "].nested[1]", target, "1");
            Beans.writePropertyValueNamed("prop[" + i + "].nested[2]", target, "2");
        }
        for (int i = 0; i < 3; i++) {
            String value = (String) Beans.readPropertyValueNamed("prop[" + i +"].nested[0]", target);
            assertNotNull(value);
            assertEquals("0", value);
            value = (String) Beans.readPropertyValueNamed("prop[" + i +"].nested[1]", target);
            assertNotNull(value);
            assertEquals("1", value);
            value = (String) Beans.readPropertyValueNamed("prop[" + i +"].nested[2]", target);
            assertNotNull(value);
            assertEquals("2", value);
        }
    }

    // スードプロパティライト -----------------------------------------------------------------

    @Test
    public void writePseudoPropertyValueNamedシンプルテスト() {
        Object target = new Object() {
            @SuppressWarnings("unused") public String prop;
        };
        Beans.writePseudoPropertyValueNamed("prop", target, "prop");
        String value = (String) Beans.readPseudoPropertyValueNamed("prop", target);
        assertNotNull(value);
        assertEquals("prop", value);
    }

    @Test
    public void writePseudoPropertyValueNamedネストテスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object prop = new Object() {
                public String nested;
            };
        };
        Beans.writePseudoPropertyValueNamed("prop.nested", target, "nested");
        String value = (String) Beans.readPseudoPropertyValueNamed("prop.nested", target);
        assertNotNull(value);
        assertEquals("nested", value);
    }

    // スードプロパティ配列ライトテスト -------------------------------------------------------------------------

    @Test
    public void writePseudoPropertyValueNamedネスト配列テスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object prop = new Object[] {"", "", ""};
        };
        Beans.writePseudoPropertyValueNamed("prop[0]", target, "0");
        Beans.writePseudoPropertyValueNamed("prop[1]", target, "1");
        Beans.writePseudoPropertyValueNamed("prop[2]", target, "2");
        String value = (String) Beans.readPseudoPropertyValueNamed("prop[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void writePseudoPropertyValueNamedネストネスト配列テスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object prop = new Object() {
                    public Object[] nested = new Object[] {"", "", ""};
            };
        };
        Beans.writePseudoPropertyValueNamed("prop.nested[0]", target, "0");
        Beans.writePseudoPropertyValueNamed("prop.nested[1]", target, "1");
        Beans.writePseudoPropertyValueNamed("prop.nested[2]", target, "2");
        String value = (String) Beans.readPseudoPropertyValueNamed("prop.nested[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop.nested[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop.nested[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void writePseudoPropertyValueNamedネスト配列ネスト配列テスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object[] prop = new Object[] {
                new Object() {
                    public Object[] nested = new Object[] {"", "", ""};
                },
                new Object() {
                    public Object[] nested = new Object[] {"", "", ""};
                },
                new Object() {
                    public Object[] nested = new Object[] {"", "", ""};
                }
            };
        };
        for (int i = 0; i < 3; i++) {
            Beans.writePseudoPropertyValueNamed("prop[" + i + "].nested[0]", target, "0");
            Beans.writePseudoPropertyValueNamed("prop[" + i + "].nested[1]", target, "1");
            Beans.writePseudoPropertyValueNamed("prop[" + i + "].nested[2]", target, "2");
        }
        for (int i = 0; i < 3; i++) {
            String value = (String) Beans.readPseudoPropertyValueNamed("prop[" + i +"].nested[0]", target);
            assertNotNull(value);
            assertEquals("0", value);
            value = (String) Beans.readPseudoPropertyValueNamed("prop[" + i +"].nested[1]", target);
            assertNotNull(value);
            assertEquals("1", value);
            value = (String) Beans.readPseudoPropertyValueNamed("prop[" + i +"].nested[2]", target);
            assertNotNull(value);
            assertEquals("2", value);
        }
    }

    // スードプロパティリストライトテスト -----------------------------------------------------------------------------

    @Test
    public void writePseudoPropertyValueNamedネストリストテスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object prop = Lists.gen("", "", "");
        };
        Beans.writePseudoPropertyValueNamed("prop[0]", target, "0");
        Beans.writePseudoPropertyValueNamed("prop[1]", target, "1");
        Beans.writePseudoPropertyValueNamed("prop[2]", target, "2");
        String value = (String) Beans.readPseudoPropertyValueNamed("prop[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void writePseudoPropertyValueNamedネストネストリストテスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object prop = new Object() {
                public List<String> nested = Lists.gen("", "", "");
            };
        };
        Beans.writePseudoPropertyValueNamed("prop.nested[0]", target, "0");
        Beans.writePseudoPropertyValueNamed("prop.nested[1]", target, "1");
        Beans.writePseudoPropertyValueNamed("prop.nested[2]", target, "2");
        String value = (String) Beans.readPseudoPropertyValueNamed("prop.nested[0]", target);
        assertNotNull(value);
        assertEquals("0", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop.nested[1]", target);
        assertNotNull(value);
        assertEquals("1", value);
        value = (String) Beans.readPseudoPropertyValueNamed("prop.nested[2]", target);
        assertNotNull(value);
        assertEquals("2", value);
    }

    @Test
    public void writePseduoPropertyValueNamedネストリストネストリストテスト() {
        Object target = new Object() {
            @SuppressWarnings("unused")
            public Object[] prop = new Object[] {
                new Object() {
                    public List<String> nested = Lists.gen("", "", "");
                },
                new Object() {
                    public List<String> nested = Lists.gen("", "", "");
                },
                new Object() {
                    public List<String> nested =Lists.gen("", "", "");
                }
            };
        };
        for (int i = 0; i < 3; i++) {
            Beans.writePseudoPropertyValueNamed("prop[" + i + "].nested[0]", target, "0");
            Beans.writePseudoPropertyValueNamed("prop[" + i + "].nested[1]", target, "1");
            Beans.writePseudoPropertyValueNamed("prop[" + i + "].nested[2]", target, "2");
        }
        for (int i = 0; i < 3; i++) {
            String value = (String) Beans.readPseudoPropertyValueNamed("prop[" + i +"].nested[0]", target);
            assertNotNull(value);
            assertEquals("0", value);
            value = (String) Beans.readPseudoPropertyValueNamed("prop[" + i +"].nested[1]", target);
            assertNotNull(value);
            assertEquals("1", value);
            value = (String) Beans.readPseudoPropertyValueNamed("prop[" + i +"].nested[2]", target);
            assertNotNull(value);
            assertEquals("2", value);
        }
    }

    // スードプロパティリストライトテスト -----------------------------------------------------------------------------

    @Test
    public void detectDeclaredPropertyTypeテスト() {
        Class<?> s = Beans.detectDeclaredPropertyType("stringField", BeansBean.class);
        Class<?> i = Beans.detectDeclaredPropertyType("integerField", BeansBean.class);
        Class<?> bi = Beans.detectDeclaredPropertyType("bigIntegerField", BeansBean.class);
        Class<?> f = Beans.detectDeclaredPropertyType("floatField", BeansBean.class);
        Class<?> d = Beans.detectDeclaredPropertyType("doubleField", BeansBean.class);
        Class<?> bd = Beans.detectDeclaredPropertyType("bigDecimalField", BeansBean.class);
        assertEquals(String.class, s);
        assertEquals(Integer.class, i);
        assertEquals(BigInteger.class, bi);
        assertEquals(Float.class, f);
        assertEquals(Double.class, d);
        assertEquals(BigDecimal.class, bd);
    }

    // スードプロパティ一覧テスト ----------------------------------------------------------------------------------

    @Test
    public void listPseudoPropertyNamesテスト() {
        String[] props = Beans.listPseudoPropertyNames(BeansBeanAlternative.class);
        assertEquals(7, props.length, "プロパティの数");
    }
    
    @Test
    public void isPropertyReaderテスト() {
        assertTrue(Beans.isPropertyReader("getHoge"));
        assertTrue(Beans.isPropertyReader("isHoge"));
        assertFalse(Beans.isPropertyReader(null));
        assertFalse(Beans.isPropertyReader(""));
        assertFalse(Beans.isPropertyReader("getClass"));
        assertFalse(Beans.isPropertyReader("hasHoge"));
    }
    
    @Test
    public void isPropertyWriterテスト() {
        assertTrue(Beans.isPropertyWriter("setHoge"));
        assertFalse(Beans.isPropertyWriter(null));
        assertFalse(Beans.isPropertyWriter(""));
        assertFalse(Beans.isPropertyWriter("getClass"));
        assertFalse(Beans.isPropertyWriter("hasHoge"));
    }
}
