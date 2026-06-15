# SINAVI J-Framework セキュリティ・依存関係監査レポート

**監査日**: 2026-06-15  
**対象リポジトリ**: madmerger/sinavi-jfw  
**ライセンス**: Apache License 2.0  
**ビルドシステム**: Gradle 1.11  
**Java バージョン**: ソース/ターゲット 1.6、CI: Oracle JDK 7  

---

## エグゼクティブサマリー

本フレームワークは **2014年前後に開発が停止** しており、使用しているすべての依存関係が **重大なセキュリティリスク** を含んでいます。特に以下の点が深刻です：

| 重要度 | 件数 | 概要 |
|--------|------|------|
| 🔴 P0 (Critical) | 5件 | リモートコード実行 (RCE)、デシリアライゼーション攻撃 |
| 🟠 P1 (High) | 5件 | 任意コード実行、SSL バイパス、DoS |
| 🟡 P2 (Medium) | 5件 | EOL ランタイム、古い依存関係 |
| 🔵 ライセンス | 2件 | ライセンス不明な依存関係 |

---

## 対象モジュール一覧

| # | モジュール名 | カテゴリ | 主な依存関係 |
|---|------------|---------|-------------|
| 1 | jfw-util-core | ユーティリティ | Commons Collections, Spring Beans |
| 2 | jfw-resource-core | リソース管理 | Spring Core/Context |
| 3 | jfw-exception-core | 例外処理 | Spring Core |
| 4 | jfw-web-util-core | Web ユーティリティ | Spring Core/Context |
| 5 | jfw-web-core | Web コア | Spring MVC, Commons DBCP, FileUpload, Validator |
| 6 | jfw-validation-core | バリデーション | Hibernate Validator, Bean Validation |
| 7 | jfw-rest-core | REST コア | JAX-RS, Jackson (Codehaus) |
| 8 | jfw-rest-plugin-jersey | Jersey プラグイン | Jersey 2.7 |
| 9 | jfw-rest-plugin-springmvc | Spring MVC プラグイン | Spring MVC, HttpClient, Jetty |
| 10 | jfw-mybatis-core | データベース | MyBatis, Spring JDBC |
| 11 | jfw-amqp-core | メッセージング | Spring AMQP, Spring Retry, RabbitMQ |
| 12 | jfw-csv-core | CSV処理 | OpenCSV, MyBatis |
| 13 | jfw-excel-core | Excel処理 | XLSBeans |
| 14 | jfw-test | テスト | Spring Test, JUnit, Mockito |
| 15 | jfw-data-mongodb-test | MongoDB テスト | Spring Data MongoDB, Jackson |

---

## 🔴 P0: Critical — 即時対応が必要

### P0-1: Apache Commons Collections 3.2.1 — リモートコード実行 (RCE)

| 項目 | 詳細 |
|------|------|
| **CVE** | CVE-2015-7501, CVE-2015-6420 |
| **CVSS** | 9.8 (Critical) |
| **影響モジュール** | jfw-util-core, jfw-resource-core, jfw-exception-core, jfw-web-util-core, jfw-web-core |
| **攻撃手法** | Java デシリアライゼーションを通じた任意コード実行 |
| **修正方法** | `3.2.1` → `3.2.2` にアップデート |
| **互換性** | APIの後方互換性あり。デシリアライゼーション保護のみ追加 |

**詳細**: `InvokerTransformer` クラスを悪用し、デシリアライゼーション時に任意のJavaメソッドを呼び出すことが可能。Apache Commons Collections 3.2.2 では安全でないシリアライゼーションがデフォルト無効化。

---

### P0-2: Spring Framework 4.0.2.RELEASE — 複数の重大脆弱性

| 項目 | 詳細 |
|------|------|
| **CVE** | CVE-2022-22965 (Spring4Shell), CVE-2018-1270, CVE-2016-9878 等多数 |
| **CVSS** | 9.8 (Critical) |
| **影響モジュール** | 全モジュール |
| **攻撃手法** | RCE (Spring4Shell), WebSocketを通じたRCE, ディレクトリトラバーサル |
| **修正方法** | Spring 4.x → Spring 5.3.x (最低限) または Spring 6.x (推奨) |
| **互換性** | ⚠️ 大規模な破壊的変更あり。段階的移行が必要 |

**詳細**: Spring Framework 4.0.x は2019年にサポート終了。Spring4Shell (CVE-2022-22965) はJDK 9+で悪用可能だが、他のCVEはJDK 7でも影響あり。

---

### P0-3: Jackson (Codehaus) 1.9.13 — デシリアライゼーション脆弱性

| 項目 | 詳細 |
|------|------|
| **CVE** | CVE-2017-7525, CVE-2017-15095 等（FasterXML版で発見、Codehausも影響） |
| **CVSS** | 9.8 (Critical) |
| **影響モジュール** | jfw-rest-core, jfw-rest-plugin-jersey, jfw-amqp-core, jfw-data-mongodb-test |
| **攻撃手法** | ポリモーフィックデシリアライゼーションを通じたRCE |
| **修正方法** | Codehaus Jackson → FasterXML Jackson 2.x に移行 |
| **互換性** | ⚠️ パッケージ名変更 (`org.codehaus.jackson` → `com.fasterxml.jackson`) |

**詳細**: Codehaus Jackson は2013年に開発停止。FasterXML Jackson 2.x に完全移行済み。セキュリティパッチは一切提供されない。

---

### P0-4: Commons FileUpload 1.3.1 — リモートコード実行 (RCE)

| 項目 | 詳細 |
|------|------|
| **CVE** | CVE-2016-1000031 |
| **CVSS** | 9.8 (Critical) |
| **影響モジュール** | jfw-web-core, jfw-test |
| **攻撃手法** | DiskFileItem クラスのデシリアライゼーションを通じたRCE |
| **修正方法** | `1.3.1` → `1.3.3` にアップデート |
| **互換性** | APIの後方互換性あり |

---

### P0-5: HTTP リポジトリURL — MITM 攻撃リスク

| 項目 | 詳細 |
|------|------|
| **CVE** | N/A (設計上の問題) |
| **CVSS** | 8.1 (High) |
| **影響範囲** | build.gradle のリポジトリ設定 |
| **攻撃手法** | 中間者攻撃によるマルウェア入り依存関係の注入 |
| **修正方法** | `http://` → `https://` に変更 |
| **互換性** | 完全互換 |

**詳細**: 以下のURLがHTTPを使用:
- `http://repo.springsource.org/plugins-release`
- `http://amateras.sourceforge.jp/mvn`
- `http://repo.spring.io/snapshot`

---

## 🟠 P1: High — 早期対応推奨

### P1-1: Logback 1.1.1 — 任意コード実行

| 項目 | 詳細 |
|------|------|
| **CVE** | CVE-2017-5929 |
| **CVSS** | 7.5 |
| **影響モジュール** | 全モジュール（allprojects で定義） |
| **修正方法** | `1.1.1` → `1.2.13+` にアップデート |

---

### P1-2: H2 Database 1.3.173 — リモートコード実行

| 項目 | 詳細 |
|------|------|
| **CVE** | CVE-2021-42392 |
| **CVSS** | 9.8 |
| **影響モジュール** | jfw-mybatis-core (testCompile), jfw-csv-core (testCompile) |
| **修正方法** | `1.3.173` → `2.1.x+` にアップデート |
| **備考** | テスト依存のみ。本番環境への直接リスクは低い |

---

### P1-3: Apache HttpClient 4.3 — SSL ホスト名検証バイパス

| 項目 | 詳細 |
|------|------|
| **CVE** | CVE-2014-3577 |
| **CVSS** | 7.5 |
| **影響モジュール** | jfw-rest-plugin-springmvc |
| **修正方法** | `4.3` → `4.3.6+` にアップデート |

---

### P1-4: Jetty 8.1.5.v20120716 — 複数の脆弱性

| 項目 | 詳細 |
|------|------|
| **CVE** | CVE-2017-7656, CVE-2017-7657, CVE-2017-7658 等 |
| **CVSS** | 7.5 |
| **影響モジュール** | jfw-rest-plugin-springmvc (testCompile) |
| **修正方法** | Jetty 8.x → 9.4.x+ にアップデート |
| **備考** | テスト依存のみ |

---

### P1-5: Guava 16.0.1 — DoS (メモリ枯渇)

| 項目 | 詳細 |
|------|------|
| **CVE** | CVE-2018-10237, CVE-2020-8908 |
| **CVSS** | 7.5 |
| **影響モジュール** | 全モジュール（allprojects で定義） |
| **修正方法** | `16.0.1` → `32.0+` にアップデート |

---

## 🟡 P2: Medium — 計画的対応

### P2-1: Java ソース/ターゲット 1.6

- **状態**: 2013年にサポート終了 (EOL)
- **リスク**: セキュリティパッチなし、現代的な暗号スイート未対応
- **推奨**: Java 8 以上への移行

### P2-2: Gradle 1.11

- **状態**: 2013年リリース（現在は 8.x）
- **リスク**: 現代的なセキュリティ機能（依存関係検証等）未対応
- **推奨**: Gradle 7.x+ への移行

### P2-3: Commons DBCP 1.4

- **状態**: EOL。Apache DBCP2 に置き換え
- **リスク**: セキュリティパッチなし
- **推奨**: `commons-dbcp:commons-dbcp:1.4` → `org.apache.commons:commons-dbcp2:2.x`

### P2-4: MyBatis 3.2.5

- **状態**: 古い（現在は 3.5.x）
- **リスク**: 潜在的なSQLインジェクション脆弱性
- **推奨**: `3.2.5` → `3.5.x` にアップデート

### P2-5: Hibernate Validator 5.1.0.Final

- **CVE**: CVE-2017-7536, CVE-2020-10693
- **影響モジュール**: jfw-validation-core
- **推奨**: `5.1.0.Final` → `6.2.x+` にアップデート

---

## 🔵 ライセンス監査

### ライセンス互換性マトリックス

| 依存関係 | ライセンス | Apache 2.0互換 | 備考 |
|---------|-----------|---------------|------|
| Spring Framework | Apache 2.0 | ✅ | — |
| Guava | Apache 2.0 | ✅ | — |
| Commons * (全般) | Apache 2.0 | ✅ | — |
| SLF4J / Logback | MIT / LGPL 2.1 | ✅ | — |
| JUnit | EPL 1.0 | ✅ | テスト依存のみ |
| Mockito | MIT | ✅ | テスト依存のみ |
| Hibernate Validator | Apache 2.0 | ✅ | — |
| Jersey | CDDL 1.1 + GPL 2.0 (CPE) | ⚠️ | ClassPath例外付きGPL。再配布時注意 |
| Jackson (Codehaus) | Apache 2.0 / LGPL 2.1 | ✅ | — |
| **xlsbeans 1.2.1** | **不明** | ❓ | **日本のSourceForge。ライセンス確認不能** |
| **collections-generic 4.01** | **Apache 2.0** | ✅ | SourceForge。メンテナンス停止 |
| OpenCSV | Apache 2.0 | ✅ | — |
| H2 Database | MPL 2.0 / EPL 1.0 | ✅ | テスト依存のみ |

### ライセンスリスク

1. **xlsbeans 1.2.1** (`jp.sf.amateras.xlsbeans:xlsbeans:1.2.1`)
   - SourceForge Japan でホスト。ライセンスが明示されていない
   - リスク: 再配布時にライセンス違反の可能性
   - 推奨: ライセンス確認、または Apache POI ベースの代替ライブラリに移行

2. **Jersey CDDL/GPL デュアルライセンス**
   - ClassPath例外付きGPLのため、通常はApache 2.0と互換
   - ただし、ソースコード改変時は注意が必要

---

## 依存関係バージョン一覧と推奨バージョン

| ライブラリ | 現行バージョン | 推奨バージョン | 緊急度 |
|-----------|-------------|--------------|--------|
| Spring Framework | 4.0.2.RELEASE | 5.3.39+ / 6.1.x | 🔴 P0 |
| Commons Collections | 3.2.1 | 3.2.2 | 🔴 P0 |
| Jackson (Codehaus) | 1.9.13 | FasterXML 2.17.x | 🔴 P0 |
| Commons FileUpload | 1.3.1 | 1.3.3+ | 🔴 P0 |
| Logback | 1.1.1 | 1.4.x+ | 🟠 P1 |
| H2 Database | 1.3.173 | 2.2.x | 🟠 P1 |
| Apache HttpClient | 4.3 | 4.5.x+ | 🟠 P1 |
| Jetty | 8.1.5.v20120716 | 9.4.x+ | 🟠 P1 |
| Guava | 16.0.1 | 33.x | 🟠 P1 |
| SLF4J | 1.7.6 | 2.0.x | 🟡 P2 |
| Hibernate Validator | 5.1.0.Final | 6.2.x+ | 🟡 P2 |
| MyBatis | 3.2.5 | 3.5.x | 🟡 P2 |
| Commons DBCP | 1.4 | DBCP2 2.x | 🟡 P2 |
| Jersey | 2.7 | 2.41+ | 🟡 P2 |
| Mockito | 1.9.5 | 4.x+ | 🟡 P2 |
| CGLIB | 3.1 | 3.3.x | ℹ️ Low |
| ASM | 3.3.1 | 9.x | ℹ️ Low |
| AspectJ | 1.7.3 | 1.9.x | ℹ️ Low |

---

## 追加セキュリティ所見

### ビルドインフラのリスク

1. **Travis CI 設定** (`.travis.yml`)
   - Oracle JDK 7 を使用（EOL）
   - `sudo` コマンドを使用（セキュリティリスク）
   - 環境変数に機密情報なし（✅ 良好）

2. **Gradle Wrapper**
   - Gradle 1.11 のラッパー。SHA-256 検証なし
   - 依存関係の整合性検証機能なし

3. **Sonatype パスワード**
   - `build.gradle` で `sonatypeUsername` / `sonatypePassword` を参照
   - `gradle.properties` で管理されている模様（リポジトリにはコミットされていない ✅）

### コードレベルのセキュリティ懸念

1. **デシリアライゼーション**: AMQP メッセージ処理でJava標準シリアライゼーションを使用している可能性
2. **SQL インジェクション**: MyBatis XML マッパーで `${}` 構文（文字列連結）が使用されていないか要確認
3. **XSS**: Web コアモジュールでの出力エスケープの適切性を要確認

---

## 推奨アクションプラン

### フェーズ1: 即時対応 (P0) — 本日中

1. ✅ Commons Collections `3.2.1` → `3.2.2` (APIの互換性あり)
2. ✅ Commons FileUpload `1.3.1` → `1.3.3` (APIの互換性あり)
3. ✅ HTTP リポジトリURL → HTTPS に変更
4. ⚠️ Jackson (Codehaus) → FasterXML 移行の計画策定

### フェーズ2: 短期対応 (P1) — 1週間以内

1. Logback `1.1.1` → `1.2.13`
2. Guava `16.0.1` → `32.x`
3. HttpClient `4.3` → `4.5.x`
4. H2 `1.3.173` → `2.x` (テスト依存)
5. Jetty 8.x → 9.4.x (テスト依存)

### フェーズ3: 中期対応 (P2) — 1ヶ月以内

1. Spring Framework 4.x → 5.3.x 移行
2. Java ソース/ターゲット 1.6 → 8+
3. Gradle 1.11 → 7.x+
4. その他の依存関係アップデート

### フェーズ4: 長期対応 — 四半期内

1. Spring Framework 5.3.x → 6.x 移行 (Java 17+)
2. Jackson FasterXML 完全移行
3. ライセンス不明な依存関係の代替

---

## 結論

このフレームワークは **約10年間メンテナンスされていない** 状態であり、**本番環境での使用は重大なセキュリティリスク** を伴います。最低限、P0の即時修正が必要です。中長期的には、Spring Framework のメジャーバージョンアップを含む大規模な依存関係更新が不可避です。

---

*本レポートは自動監査ツールおよびCVEデータベースに基づき作成されました。*
