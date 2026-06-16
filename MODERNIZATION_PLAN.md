# SINAVI J-Framework モダナイズ開発計画書

## 目次

- [背景](#背景)
- [対象リポジトリの概要](#対象リポジトリの概要)
- [モダナイズの目標](#モダナイズの目標)
- [Phase 0: 現状分析とテストベースライン確立](#phase-0-現状分析とテストベースライン確立)
- [Phase 1: ビルドインフラのモダナイズ](#phase-1-ビルドインフラのモダナイズ)
- [Phase 2: Java バージョンアップ](#phase-2-java-バージョンアップ)
- [Phase 3: Jakarta EE マイグレーション](#phase-3-jakarta-ee-マイグレーション)
- [Phase 4: コア依存ライブラリの更新](#phase-4-コア依存ライブラリの更新)
- [Phase 5: テストインフラの更新](#phase-5-テストインフラの更新)
- [Phase 6: CI/CD の再構築](#phase-6-cicd-の再構築)
- [Phase 7: ドキュメント・クリーンアップ](#phase-7-ドキュメントクリーンアップ)
- [付録: 依存ライブラリ バージョンマッピング一覧](#付録-依存ライブラリ-バージョンマッピング一覧)

---

## 背景

本リポジトリ `sinavi-jfw` は、伊藤忠テクノソリューションズ株式会社（CTC）が開発した Java EE 対応 Web アプリケーションフレームワーク「SINAVI J-Framework」である。2014年を最後に開発が停止しており、以下の深刻な問題を抱えている：

- **ビルドが不可能**: Gradle 1.11 は現代の JDK（9以降）では動作しない
- **セキュリティ脆弱性**: commons-collections 3.2.1 に既知の RCE 脆弱性（CVE-2015-6420 等）
- **EOL ライブラリ**: Jackson Codehaus版、Spring Framework 4.x、JUnit 4 など多数が EOL
- **不安定な依存**: Spring AMQP の RC版、Spring Data MongoDB の M1（マイルストーン）版を使用
- **CI 停止**: Travis CI（travis-ci.org）は2020年にサービス終了済み
- **非暗号化通信**: Maven リポジトリURLが HTTP で指定されている（MITM攻撃のリスク）
- **廃止リポジトリ**: `amateras.sourceforge.jp/mvn`（xlsbeans の配布元）がアクセス不能

## 対象リポジトリの概要

### モジュール構成（15サブプロジェクト）

```
sinavi-jfw/
├── util/
│   ├── jfw-util-core          # ユーティリティ（型変換、定数管理、ポーター等）
│   ├── jfw-resource-core      # リソース管理（メッセージ、プロパティ）
│   ├── jfw-exception-core     # 例外ハンドリング基盤
│   └── jfw-web-util-core      # Web ユーティリティ
├── web/
│   └── jfw-web-core           # Spring MVC ベースの Web フレームワーク
├── validation/
│   └── jfw-validation-core    # Bean Validation カスタムバリデータ
├── jdbc/
│   └── jfw-mybatis-core       # MyBatis 統合（ページネーション等）
├── restful-web-service/
│   ├── jfw-rest-core          # JAX-RS REST クライアント基盤
│   ├── jfw-rest-plugin-jersey # Jersey 実装プラグイン
│   └── jfw-rest-plugin-springmvc # Spring MVC 実装プラグイン
├── async/
│   └── jfw-amqp-core          # Spring AMQP / RabbitMQ メッセージング基盤
├── csv/
│   └── jfw-csv-core           # CSV 入出力
├── excel/
│   └── jfw-excel-core         # Excel 入出力（xlsbeans）
└── test/
    ├── jfw-test               # テストユーティリティ
    └── jfw-data-mongodb-test  # MongoDB テストユーティリティ
```

### コード規模

| 種別 | ファイル数 |
|------|-----------|
| メインソース（`src/main/java`） | 507 |
| テストソース（`src/test/java`） | 293 |

### 現行の技術スタック

| カテゴリ | ライブラリ | 現行バージョン | 問題 |
|---------|-----------|--------------|------|
| ビルドツール | Gradle | 1.11 | JDK 9+ で動作不可 |
| Java | JDK | 1.6（source/target） | EOL、3世代前 |
| DI/Web | Spring Framework | 4.0.2.RELEASE | EOL |
| メッセージング | Spring AMQP | 1.3.0.RC1 | RC版（不安定） |
| データベース | Spring Data MongoDB | 1.4.0.M1 | マイルストーン版 |
| リトライ | Spring Retry | 1.0.3.RELEASE | 旧版 |
| JSON | Jackson (Codehaus) | 1.9.13 | 完全EOL |
| ORM | MyBatis | 3.2.5 | 旧版 |
| ORM | mybatis-spring | 1.2.2 | 旧版 |
| バリデーション | Hibernate Validator | 5.1.0.Final | EOL |
| バリデーション | Bean Validation API | 1.1.0.Final | EOL |
| REST | Jersey | 2.7 | 旧版 |
| REST | JAX-RS API | 2.0 | 旧版 |
| ログ | Logback | 1.1.1 | 旧版 |
| ログ | SLF4J | 1.7.6 | 旧版 |
| ユーティリティ | Guava | 16.0.1 | 旧版 |
| ユーティリティ | commons-collections | 3.2.1 | **RCE脆弱性** |
| ユーティリティ | commons-lang | 2.6 | EOL |
| ユーティリティ | commons-io | 2.0.1 | 旧版 |
| ユーティリティ | commons-codec | 1.5 | 旧版 |
| アップロード | commons-fileupload | 1.3.1 | 旧版 |
| バリデーション | commons-validator | 1.4.0 | 旧版 |
| DB接続 | commons-dbcp | 1.4 | EOL |
| CSV | OpenCSV | 2.3 | 旧版 |
| Excel | xlsbeans | 1.2.1 | **配布元消滅** |
| テストDB | H2 | 1.3.173 | 旧版 |
| バイトコード | cglib | 3.1 | 旧版 |
| バイトコード | ASM | 3.3.1 | 旧版 |
| AOP | AspectJ | 1.7.3 | 旧版 |
| テスト | JUnit | 4.11 | EOL |
| テスト | Mockito | 1.9.5 | EOL |
| 静的解析 | Checkstyle | 5.6 | 旧版 |
| 静的解析 | FindBugs | 2.0.3 | **開発終了** |
| コードカバレッジ | JaCoCo | 0.6.4 | 旧版 |
| Gradle プラグイン | license-gradle-plugin | 0.7.0 | 旧版 |
| Gradle プラグイン | gradle-git | 0.6.4 | 旧版 |
| テスト（REST） | Jetty (embedded) | 8.1.5 | EOL |
| HTTP | HttpClient | 4.3 | 旧版 |
| コレクション | collections-generic | 4.01 | 旧版 |
| CI | Travis CI | — | **サービス終了** |

---

## モダナイズの目標

| 項目 | 目標 |
|------|------|
| Java | 17（LTS） |
| ビルドツール | Gradle 8.x |
| Spring Framework | 6.x |
| Jakarta EE | Jakarta EE 10（jakarta.* 名前空間） |
| CI/CD | GitHub Actions |
| 静的解析 | SpotBugs + Checkstyle 最新版 |
| テスト | JUnit 5 (Jupiter) + Mockito 5.x |

---

## Phase 0: 現状分析とテストベースライン確立

### 目的
モダナイズ作業の出発点となるベースラインを確立する。既存テストの状況を把握し、変更による退行を検知できる体制を整える。

### 作業内容

#### 0-1. 既存テストの棚卸し

| 対象ファイル | 変更内容 |
|-------------|---------|
| 全サブプロジェクトの `src/test/java/` | テストクラス・メソッド数の集計 |
| `build.gradle` L124-134 | テスト設定の確認 |

各サブプロジェクトのテスト状況（293テストファイル）：

| サブプロジェクト | テスト対象領域 |
|-----------------|---------------|
| `jfw-util-core` | 型変換、定数、ポーター、ユーティリティ |
| `jfw-resource-core` | リソース管理 |
| `jfw-exception-core` | 例外ハンドリング |
| `jfw-web-util-core` | Web ユーティリティ |
| `jfw-web-core` | Web フレームワーク、メッセージ、トークン、ページネーション |
| `jfw-validation-core` | カスタムバリデータ（多数） |
| `jfw-mybatis-core` | MyBatis 統合、JDBC |
| `jfw-rest-core` | REST エンティティ |
| `jfw-rest-plugin-jersey` | Jersey 例外マッパー、フィルター、設定 |
| `jfw-rest-plugin-springmvc` | SpringMVC REST ハンドラー、フィルター |
| `jfw-amqp-core` | AMQP メッセージング、リトライ、統合テスト |
| `jfw-csv-core` | CSV 入出力 |
| `jfw-excel-core` | Excel/xlsbeans |

#### 0-2. 動作確認可能な最低限の環境構築

| 対象 | 内容 |
|------|------|
| `Dockerfile`（新規作成） | JDK 7 + Gradle 1.11 + RabbitMQ + MongoDB のコンテナ環境 |
| `docker-compose.yml`（新規作成） | ビルド＋テスト実行用の構成 |
| `.travis.yml` | 既存CI設定を参考にする（`oraclejdk7`, `rabbitmq`, `mongodb`） |

> **注意**: Phase 0 の Docker 環境は「現状のビルド・テストが通ること」の確認が目的であり、モダナイズ後は不要となる。

#### 0-3. モジュール依存関係マップの作成

`settings.gradle` と `build.gradle` の `project(':...')` 依存から以下のマップを整理する：

```
jfw-util-core ← 全モジュールの基盤
  ├── jfw-resource-core ← jfw-exception-core
  │   └── jfw-exception-core ← jfw-web-util-core, jfw-web-core, jfw-rest-*, jfw-amqp-core, jfw-csv-core
  ├── jfw-web-util-core ← jfw-web-core, jfw-mybatis-core
  ├── jfw-validation-core ← jfw-csv-core, jfw-rest-plugin-jersey (test)
  ├── jfw-rest-core ← jfw-rest-plugin-jersey, jfw-rest-plugin-springmvc
  └── jfw-test ← jfw-web-core (test), jfw-mybatis-core (test), jfw-csv-core (test)
      └── jfw-data-mongodb-test
```

### リスク
- Oracle JDK 7 の入手が困難（ライセンス制約）→ OpenJDK 7 で代替
- RabbitMQ / MongoDB の古いバージョンが必要（AMQP 統合テスト用）
- `amateras.sourceforge.jp/mvn` が停止しており、xlsbeans 1.2.1 の取得に失敗する可能性

### 推定工数
**2〜3日**

---

## Phase 1: ビルドインフラのモダナイズ

### 目的
Gradle を現代のバージョンに更新し、ビルドスクリプトを最新の Gradle DSL に対応させる。

### 作業内容

#### 1-1. Gradle Wrapper のアップグレード

| 対象ファイル | 変更内容 |
|-------------|---------|
| `gradle/wrapper/gradle-wrapper.properties` L6 | `distributionUrl` を `https\://services.gradle.org/distributions/gradle-8.10.2-bin.zip` に変更 |
| `gradlew`, `gradlew.bat` | 最新 wrapper に再生成 |

段階的にアップグレードする（1.11 → 2.x → 3.x → 4.x → 5.x → 6.x → 7.x → 8.x）。各メジャーバージョンの breaking changes に対応しながら進める。

主要な破壊的変更：
- **Gradle 2.x**: `configurations.compile` が非推奨化の開始
- **Gradle 3.x**: Groovy/Kotlin DSL の変更
- **Gradle 4.x**: `compile` → `implementation`/`api` への移行推奨
- **Gradle 5.x**: `compile`/`testCompile`/`runtime` の非推奨化、`provided` 設定の標準化（`compileOnly`）
- **Gradle 6.x**: `compile`/`testCompile`/`runtime` の削除
- **Gradle 7.x**: `maven` プラグイン → `maven-publish`、`Wrapper` タスクタイプの変更
- **Gradle 8.x**: Java toolchain の推奨

#### 1-2. Maven リポジトリ URL の HTTPS 化

| 対象ファイル | 行番号 | 変更内容 |
|-------------|--------|---------|
| `build.gradle` | L26 | `http://repo.springsource.org/plugins-release` → `https://repo.spring.io/plugins-release` |
| `build.gradle` | L29 | `http://amateras.sourceforge.jp/mvn` → 代替手段を検討（後述） |
| `build.gradle` | L32 | `http://repo.spring.io/snapshot` → `https://repo.spring.io/snapshot` |

**`amateras.sourceforge.jp/mvn` の対応**:
- xlsbeans 1.2.1 は Maven Central に存在しない
- 対策案:
  1. xlsbeans の jar を `libs/` ディレクトリにローカル配置し `flatDir` で参照
  2. Phase 4 で代替ライブラリ（Apache POI 等）への移行を検討

#### 1-3. Gradle プラグインの更新

| プラグイン | 現行 | 移行先 | 変更内容 |
|-----------|------|--------|---------|
| `license-gradle-plugin` | 0.7.0 (`nl.javadude.gradle.plugins`) | `com.github.hierynomus.license` 0.16.1 | グループ名・設定方法の変更 |
| `gradle-git` | 0.6.4 (`org.ajoberstar`) | `org.ajoberstar.grgit` 5.x | GitHub Pages 公開のワークフロー見直し |
| `findbugs` | 組込みプラグイン | `com.github.spotbugs` 6.x | FindBugs → SpotBugs への完全移行 |
| `maven` | 組込みプラグイン | `maven-publish` | `uploadArchives` → `publishing` ブロックに書き換え |

#### 1-4. `build.gradle` の構文更新

| 対象 | 現行 | 変更先 |
|------|------|--------|
| `build.gradle` L62-66 | `configurations { provided; compile.extendsFrom provided }` | `compileOnly` 設定に移行 |
| `build.gradle` 全体 | `compile`, `testCompile`, `runtime` | `implementation`, `testImplementation`, `runtimeOnly` |
| `build.gradle` L172-176 | `task wrapper(type: Wrapper)` | 組込み `wrapper` タスクに移行 |
| `build.gradle` L205-210 | `signing` ブロック | 新 DSL に対応 |
| `build.gradle` L217-258 | `uploadArchives` + `mavenDeployer` | `maven-publish` プラグインの `publishing` ブロック |

#### 1-5. Checkstyle の更新

| 対象ファイル | 変更内容 |
|-------------|---------|
| `build.gradle` L104, L136-140 | `checkstyleVersion = '5.6'` → `10.x`（最新版） |
| `config/checkstyle/ctc-checks.xml` | 新 Checkstyle に対応するルール設定の見直し |

#### 1-6. JaCoCo の更新

| 対象ファイル | 変更内容 |
|-------------|---------|
| `build.gradle` L147-157 | `toolVersion = '0.6.4.201312101107'` → `0.8.12`（最新版） |
| `build.gradle` L155 | `html.destination` → `html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))` |

### リスク
- **高**: Gradle メジャーバージョン間の breaking changes が多く、段階的アップグレードが必要
- **中**: `uploadArchives` → `maven-publish` の移行で Sonatype 公開設定の書き換えが必要
- **中**: Checkstyle ルールの互換性（`ctc-checks.xml` がカスタムルールを含む場合）
- **低**: xlsbeans のローカル配置は一時的な対処であり、Phase 4 で恒久対策が必要

### 推定工数
**5〜7日**（Gradle 段階的アップグレードに時間を要する）

---

## Phase 2: Java バージョンアップ

### 目的
Java 1.6 から Java 17（LTS）へアップグレードし、現代の Java 機能を利用可能にする。

### 作業内容

#### 2-1. sourceCompatibility / targetCompatibility の更新

| 対象ファイル | 行番号 | 変更内容 |
|-------------|--------|---------|
| `build.gradle` | L43-46 | `sourceCompatibility = 1.6` → Java toolchain `java { toolchain { languageVersion = JavaLanguageVersion.of(17) } }` に変更 |

#### 2-2. Java 6 固有 API の修正

調査が必要な領域：

| 対象 | 確認内容 |
|------|---------|
| `sun.*` パッケージの使用 | Java 9 以降でアクセス不可（モジュールシステム） |
| `javax.xml.*` 関連 | Java 11 で `java.xml.bind` 等が削除 |
| `finalize()` メソッドのオーバーライド | Java 9 以降で非推奨 |
| `Thread.stop()` 等の非推奨メソッド | 削除確認 |

#### 2-3. Deprecation Warnings の解消

| 対象 | 内容 |
|------|------|
| 全 `src/main/java/` ファイル | `-Xlint:deprecation` でコンパイルし、警告を一覧化 |
| 全 `src/test/java/` ファイル | 同上 |

### リスク
- **中**: `javax.xml.bind`（JAXB）等の Java EE モジュールが Java 11 で削除されており、もし使用していれば外部ライブラリとして追加が必要（Phase 3 の Jakarta EE 移行と密接に関連）
- **低**: Java 6 → 17 の間にシンタックス非互換はないが、動作の変更（Unicode 処理、ソート安定性等）に注意

### 推定工数
**2〜3日**

---

## Phase 3: Jakarta EE マイグレーション（最大の変更）

### 目的
`javax.*` 名前空間を `jakarta.*` に移行し、Jakarta EE 10 対応とする。Spring Framework 6.x が Jakarta EE を必須とするため、Phase 4 の前提条件となる。

### 作業内容

#### 3-1. パッケージ名の一括変換

| 変換元 | 変換先 | 影響範囲 |
|--------|--------|---------|
| `javax.servlet` | `jakarta.servlet` | `jfw-web-core`, `jfw-rest-*`, `jfw-test` |
| `javax.servlet.jsp` | `jakarta.servlet.jsp` | `jfw-web-core` |
| `javax.servlet.http` | `jakarta.servlet.http` | `jfw-web-core`, `jfw-rest-*` |
| `javax.validation` | `jakarta.validation` | `jfw-validation-core`, `jfw-web-core`, `jfw-rest-*` |
| `javax.ws.rs` | `jakarta.ws.rs` | `jfw-rest-core`, `jfw-rest-plugin-jersey` |
| `javax.el` | `jakarta.el` | `jfw-validation-core`, `jfw-web-core` |

#### 3-2. 依存ライブラリの更新（Jakarta EE 10 対応版）

| 現行依存 | 変更先 | build.gradle の変更 |
|---------|--------|-------------------|
| `javax.servlet:javax.servlet-api:3.0.1` | `jakarta.servlet:jakarta.servlet-api:6.0.0` | L121 |
| `javax.validation:validation-api:1.1.0.Final` | `jakarta.validation:jakarta.validation-api:3.0.2` | L78, L426, L562 |
| `javax.ws.rs:javax.ws.rs-api:2.0` | `jakarta.ws.rs:jakarta.ws.rs-api:3.1.0` | L73, L485 |
| `javax.servlet.jsp:jsp-api:2.2` | `jakarta.servlet.jsp:jakarta.servlet.jsp-api:3.1.1` | L101, L442 |
| `javax.servlet:jstl:1.2` | `jakarta.servlet.jstl:jakarta.servlet.jstl-api:3.0.0` | L102, L443 |
| `javax.el:javax.el-api:2.2.4` | `jakarta.el:jakarta.el-api:5.0.1` | L80, L441, L564 |
| `org.glassfish.web:javax.el:2.2.4` | `org.glassfish.expressly:expressly:5.0.0` | L565 |

#### 3-3. ソースコード内の import 文の書き換え

全 `src/main/java/` および `src/test/java/` 内の Java ファイルに対し、以下の置換を実施：

```bash
find . -name "*.java" -exec sed -i 's/import javax\.servlet/import jakarta.servlet/g' {} +
find . -name "*.java" -exec sed -i 's/import javax\.validation/import jakarta.validation/g' {} +
find . -name "*.java" -exec sed -i 's/import javax\.ws\.rs/import jakarta.ws.rs/g' {} +
find . -name "*.java" -exec sed -i 's/import javax\.el/import jakarta.el/g' {} +
```

> **注意**: `javax.annotation` は `jakarta.annotation` に変更が必要だが、`javax.annotation.Nullable` 等の Google/JSR 305 アノテーションは対象外。個別に確認が必要。

#### 3-4. XML 設定ファイルの更新

| 対象 | 変更内容 |
|------|---------|
| `src/main/resources/*.xml` | XML 名前空間の `javax.*` → `jakarta.*` 変換 |
| Spring XML 設定ファイル | XSD URL の更新 |

### リスク
- **高**: 全サブプロジェクトに影響する大規模な変更。コンパイルエラーの一時的な大量発生が避けられない
- **高**: `javax.annotation` の変換は慎重に行う必要がある（JSR 305 と Jakarta Annotations の区別）
- **中**: サードパーティライブラリが Jakarta EE 対応版を提供しているか個別に確認が必要
- **中**: テスト用の Jetty 8.1.5（`jfw-rest-plugin-springmvc`）を Jakarta EE 対応版（Jetty 12）に更新する必要がある

### 推定工数
**5〜7日**

---

## Phase 4: コア依存ライブラリの更新

### 目的
全ての依存ライブラリを最新安定版に更新し、セキュリティ脆弱性を解消する。

### 作業内容

#### 4-1. Spring エコシステム

| ライブラリ | 現行 | 目標 | 対象ファイル | 備考 |
|-----------|------|------|-------------|------|
| Spring Framework | 4.0.2.RELEASE | 6.1.x | `build.gradle` L69 | Jakarta EE 必須 |
| Spring Data MongoDB | 1.4.0.M1 | 4.3.x | `build.gradle` L70 | パッケージ構造の変更あり |
| Spring AMQP | 1.3.0.RC1 | 3.1.x | `build.gradle` L71 | API の大幅な変更あり |
| Spring Retry | 1.0.3.RELEASE | 2.0.x | `build.gradle` L72 | |

**Spring Framework 4 → 6 の主要な変更点**:
- `WebMvcConfigurerAdapter` の削除 → `WebMvcConfigurer` インターフェース直接実装
- `@RequestMapping` の戻り値の型推論変更
- `RestTemplate` のデフォルト動作変更

#### 4-2. JSON ライブラリ

| ライブラリ | 現行 | 目標 | 備考 |
|-----------|------|------|------|
| Jackson (Codehaus) | 1.9.13 | FasterXML Jackson 2.17.x | パッケージ名が `org.codehaus.jackson` → `com.fasterxml.jackson` に変更 |

影響を受けるサブプロジェクト：
- `jfw-rest-core`（`build.gradle` L486-487）
- `jfw-amqp-core`（`build.gradle` L589-590）
- `jfw-data-mongodb-test`（`build.gradle` L405-408）

変換が必要なクラス名マッピング：

| Codehaus | FasterXML |
|----------|-----------|
| `org.codehaus.jackson.map.ObjectMapper` | `com.fasterxml.jackson.databind.ObjectMapper` |
| `org.codehaus.jackson.JsonGenerator` | `com.fasterxml.jackson.core.JsonGenerator` |
| `org.codehaus.jackson.JsonParser` | `com.fasterxml.jackson.core.JsonParser` |
| `org.codehaus.jackson.annotate.*` | `com.fasterxml.jackson.annotation.*` |
| `org.codehaus.jackson.map.annotate.*` | `com.fasterxml.jackson.databind.annotation.*` |

#### 4-3. データベース関連

| ライブラリ | 現行 | 目標 | 備考 |
|-----------|------|------|------|
| MyBatis | 3.2.5 | 3.5.x | |
| mybatis-spring | 1.2.2 | 3.0.x | Spring 6 対応版 |
| commons-dbcp | 1.4 | commons-dbcp2 2.12.x | パッケージ名変更 `org.apache.commons.dbcp` → `org.apache.commons.dbcp2` |
| H2 | 1.3.173 | 2.2.x | URL 形式の変更、互換モードの確認が必要 |

#### 4-4. バリデーション

| ライブラリ | 現行 | 目標 | 備考 |
|-----------|------|------|------|
| Hibernate Validator | 5.1.0.Final | 8.0.x | Jakarta Validation 3.0 対応 |
| commons-validator | 1.4.0 | 1.9.x | |

#### 4-5. REST / HTTP

| ライブラリ | 現行 | 目標 | 備考 |
|-----------|------|------|------|
| Jersey | 2.7 | 3.1.x | Jakarta REST 対応版 |
| HttpClient | 4.3 | 5.3.x | API の大幅な変更 |
| Jetty (test) | 8.1.5 | 12.x | Jakarta Servlet 6.0 対応 |

#### 4-6. Apache Commons

| ライブラリ | 現行 | 目標 | 備考 |
|-----------|------|------|------|
| commons-collections | 3.2.1 | commons-collections4 4.4 | **セキュリティ修正**。パッケージ名変更 `org.apache.commons.collections` → `org.apache.commons.collections4` |
| commons-lang | 2.6 | commons-lang3 3.14.x | パッケージ名変更 `org.apache.commons.lang` → `org.apache.commons.lang3` |
| commons-io | 2.0.1 | 2.16.x | |
| commons-codec | 1.5 | 1.17.x | |
| commons-fileupload | 1.3.1 | 2.0.x | Jakarta Servlet 対応版、API 変更あり |

#### 4-7. ユーティリティ・その他

| ライブラリ | 現行 | 目標 | 備考 |
|-----------|------|------|------|
| Guava | 16.0.1 | 33.x | 削除された API の確認が必要 |
| OpenCSV | 2.3 | 5.9 | API の大幅な変更（`CSVReader` のコンストラクタ等） |
| SLF4J | 1.7.6 | 2.0.x | |
| Logback | 1.1.1 | 1.5.x | |
| cglib | 3.1 | 3.3.x（または Byte Buddy 1.14.x への移行を検討） | Spring 6 は内部で Byte Buddy を使用 |
| ASM | 3.3.1 | 9.x（cglib に同梱） | |
| AspectJ | 1.7.3 | 1.9.x | |
| collections-generic | 4.01 | 削除（commons-collections4 で代替） | |

#### 4-8. Excel ライブラリの移行

| ライブラリ | 現行 | 目標 | 備考 |
|-----------|------|------|------|
| xlsbeans | 1.2.1 | Apache POI 5.x + xlsbeans 代替実装 | xlsbeans の配布元（amateras.sourceforge.jp）が消滅。Apache POI への移行が必要 |

`jfw-excel-core` サブプロジェクトの `xlsbeans` 依存（`build.gradle` L631）を Apache POI に置き換えるか、xlsbeans の後継である [xlsbean](https://github.com/mygreen/xlsmapper) 等への移行を検討する。

### リスク
- **高**: パッケージ名変更を伴うライブラリ（Jackson, commons-collections, commons-lang, commons-dbcp）は全ソースコードの修正が必要
- **高**: Spring Framework 4 → 6 は API の大幅な変更を伴い、特に `jfw-web-core` と `jfw-amqp-core` への影響が大きい
- **中**: OpenCSV 2.x → 5.x の API 変更が `jfw-csv-core` に大きく影響
- **中**: xlsbeans の代替が `jfw-excel-core` の API を変更する可能性
- **低**: H2 のバージョンアップで既存のテスト用 SQL に互換性問題が発生する可能性

### 推定工数
**10〜15日**（最も工数がかかるフェーズ）

---

## Phase 5: テストインフラの更新

### 目的
テストフレームワークを最新版に更新し、全テストの合格を確認する。

### 作業内容

#### 5-1. JUnit 4 → JUnit 5 (Jupiter) への移行

| 対象ファイル | 変更内容 |
|-------------|---------|
| `build.gradle` L93 | `junitVersion = '4.11'` → JUnit 5 BOM `org.junit:junit-bom:5.10.x` |
| `build.gradle` L114 | `testCompile "junit:junit"` → `testImplementation "org.junit.jupiter:junit-jupiter"` |
| 全 293 テストファイル | アノテーション・アサーションの書き換え |

主要な変換パターン：

| JUnit 4 | JUnit 5 |
|---------|---------|
| `import org.junit.Test` | `import org.junit.jupiter.api.Test` |
| `import org.junit.Before` | `import org.junit.jupiter.api.BeforeEach` |
| `import org.junit.After` | `import org.junit.jupiter.api.AfterEach` |
| `import org.junit.BeforeClass` | `import org.junit.jupiter.api.BeforeAll` |
| `import org.junit.Ignore` | `import org.junit.jupiter.api.Disabled` |
| `@Test(expected = Foo.class)` | `assertThrows(Foo.class, () -> ...)` |
| `@Test(timeout = 1000)` | `@Timeout(1)` |
| `@RunWith(...)` | `@ExtendWith(...)` |
| `@Rule` / `ExpectedException` | `assertThrows` |

> **注意**: `jfw-test` サブプロジェクトに `J2Unit4ClassRunner` や `OrderedRunner` 等のカスタムランナーがあり、これらを JUnit 5 の Extension に移行する必要がある。

#### 5-2. Mockito の更新

| 対象ファイル | 変更内容 |
|-------------|---------|
| `build.gradle` L94 | `mockitoVersion = '1.9.5'` → `5.12.x` |

主要な変更点：
- `org.mockito.runners.MockitoJUnitRunner` → `org.mockito.junit.jupiter.MockitoExtension`
- `Mockito.anyObject()` → `Mockito.any()`
- `Mockito.anyInt()` 等は互換あり

#### 5-3. テスト全件パス確認

```bash
./gradlew test
```

全サブプロジェクトのテストを実行し、失敗があれば修正する。

### リスク
- **高**: 293ファイルの JUnit 4 → 5 移行は機械的だが量が多い
- **中**: カスタムランナー（`J2Unit4ClassRunner`, `OrderedRunner`, `SpringMongoJUnit4ClassRunner`）の Extension 化に設計作業が必要
- **中**: Mockito 1.x → 5.x で内部動作の変更（strict stubbing がデフォルト）によりテスト修正が必要
- **低**: AMQP 統合テスト（`jfw-amqp-core` の `it/` パッケージ）は RabbitMQ 接続が必要

### 推定工数
**5〜7日**

---

## Phase 6: CI/CD の再構築

### 目的
Travis CI から GitHub Actions に移行し、ビルド・テスト・静的解析・脆弱性スキャンを自動化する。

### 作業内容

#### 6-1. GitHub Actions ワークフローの新規作成

| 対象ファイル | 内容 |
|-------------|------|
| `.github/workflows/ci.yml`（新規） | CI パイプライン |

```yaml
# ci.yml の想定構成
name: CI

on:
  push:
    branches: [master, main]
  pull_request:
    branches: [master, main]

jobs:
  build:
    runs-on: ubuntu-latest
    services:
      rabbitmq:
        image: rabbitmq:3-management
        ports:
          - 5672:5672
          - 15672:15672
      mongodb:
        image: mongo:7
        ports:
          - 27017:27017
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - uses: gradle/actions/setup-gradle@v4
      - run: ./gradlew build
      - run: ./gradlew test
      - run: ./gradlew jacocoTestReport
      - run: ./gradlew spotbugsMain
      - run: ./gradlew checkstyleMain
      - uses: actions/upload-artifact@v4
        with:
          name: test-reports
          path: '**/build/reports/'
```

#### 6-2. 脆弱性スキャンの追加

| ツール | 用途 |
|--------|------|
| `org.owasp:dependency-check-gradle` | 依存ライブラリの CVE スキャン |
| GitHub Dependabot | 自動的な依存更新 PR の作成 |

#### 6-3. Travis CI 設定の削除

| 対象ファイル | 変更内容 |
|-------------|---------|
| `.travis.yml` | 削除 |

### リスク
- **低**: GitHub Actions は成熟しており、移行リスクは小さい
- **低**: RabbitMQ と MongoDB の Docker サービスは GitHub Actions で利用可能

### 推定工数
**1〜2日**

---

## Phase 7: ドキュメント・クリーンアップ

### 目的
ドキュメントを更新し、モダナイズの成果を整理する。

### 作業内容

#### 7-1. README.md の更新

| 対象ファイル | 変更内容 |
|-------------|---------|
| `README.md` L4 | Travis CI バッジ → GitHub Actions バッジに変更 |
| `README.md` | ビルド手順、動作要件（Java 17, Gradle 8.x）の追記 |
| `README.md` | モジュール構成の説明追加 |

#### 7-2. CHANGELOG の作成

| 対象ファイル | 内容 |
|-------------|------|
| `CHANGELOG.md`（新規） | モダナイズの全変更履歴を記載 |

#### 7-3. SBOM の生成

| ツール | 用途 |
|--------|------|
| CycloneDX Gradle Plugin | Software Bill of Materials の自動生成 |

```groovy
// build.gradle に追加
plugins {
    id 'org.cyclonedx.bom' version '1.8.2'
}
```

#### 7-4. 不要ファイルの削除

| 対象ファイル | 理由 |
|-------------|------|
| `.travis.yml` | Phase 6 で削除済み |
| 古い Eclipse 設定（`.classpath`, `.project` 等があれば） | IDE 非依存化 |

### リスク
- **低**: ドキュメント作業のため技術リスクは小さい

### 推定工数
**1〜2日**

---

## 付録: 依存ライブラリ バージョンマッピング一覧

| # | ライブラリ | 現行バージョン | 目標バージョン | パッケージ名変更 |
|---|-----------|--------------|--------------|----------------|
| 1 | Gradle | 1.11 | 8.10.2 | — |
| 2 | Java | 1.6 | 17 | — |
| 3 | Spring Framework | 4.0.2.RELEASE | 6.1.x | — |
| 4 | Spring Data MongoDB | 1.4.0.M1 | 4.3.x | — |
| 5 | Spring AMQP | 1.3.0.RC1 | 3.1.x | — |
| 6 | Spring Retry | 1.0.3.RELEASE | 2.0.x | — |
| 7 | Jackson | 1.9.13 (Codehaus) | 2.17.x (FasterXML) | `org.codehaus.jackson` → `com.fasterxml.jackson` |
| 8 | MyBatis | 3.2.5 | 3.5.x | — |
| 9 | mybatis-spring | 1.2.2 | 3.0.x | — |
| 10 | Hibernate Validator | 5.1.0.Final | 8.0.x | — |
| 11 | Bean Validation API | 1.1.0.Final | Jakarta Validation 3.0.2 | `javax.validation` → `jakarta.validation` |
| 12 | Jersey | 2.7 | 3.1.x | — |
| 13 | JAX-RS API | 2.0 | Jakarta REST 3.1.0 | `javax.ws.rs` → `jakarta.ws.rs` |
| 14 | Servlet API | 3.0.1 | Jakarta Servlet 6.0.0 | `javax.servlet` → `jakarta.servlet` |
| 15 | JSP API | 2.2 | Jakarta Server Pages 3.1.1 | `javax.servlet.jsp` → `jakarta.servlet.jsp` |
| 16 | JSTL | 1.2 | Jakarta Standard Tag Library 3.0.0 | `javax.servlet.jstl` → `jakarta.servlet.jstl` |
| 17 | EL API | 2.2.4 | Jakarta EL 5.0.1 | `javax.el` → `jakarta.el` |
| 18 | Logback | 1.1.1 | 1.5.x | — |
| 19 | SLF4J | 1.7.6 | 2.0.x | — |
| 20 | Guava | 16.0.1 | 33.x | — |
| 21 | commons-collections | 3.2.1 | 4.4 | `o.a.c.collections` → `o.a.c.collections4` |
| 22 | commons-lang | 2.6 | 3.14.x | `o.a.c.lang` → `o.a.c.lang3` |
| 23 | commons-io | 2.0.1 | 2.16.x | — |
| 24 | commons-codec | 1.5 | 1.17.x | — |
| 25 | commons-fileupload | 1.3.1 | 2.0.x | — |
| 26 | commons-validator | 1.4.0 | 1.9.x | — |
| 27 | commons-dbcp | 1.4 | 2.12.x | `o.a.c.dbcp` → `o.a.c.dbcp2` |
| 28 | OpenCSV | 2.3 | 5.9 | — |
| 29 | xlsbeans | 1.2.1 | Apache POI 5.x（移行） | — |
| 30 | H2 | 1.3.173 | 2.2.x | — |
| 31 | cglib | 3.1 | 3.3.x | — |
| 32 | ASM | 3.3.1 | 9.x | — |
| 33 | AspectJ | 1.7.3 | 1.9.x | — |
| 34 | JUnit | 4.11 | 5.10.x | `org.junit` → `org.junit.jupiter.api` |
| 35 | Mockito | 1.9.5 | 5.12.x | — |
| 36 | Checkstyle | 5.6 | 10.x | — |
| 37 | FindBugs | 2.0.3 | SpotBugs 6.x | — |
| 38 | JaCoCo | 0.6.4 | 0.8.12 | — |
| 39 | HttpClient | 4.3 | 5.3.x | — |
| 40 | Jetty (test) | 8.1.5 | 12.x | — |
| 41 | collections-generic | 4.01 | 削除 | — |
| 42 | license-gradle-plugin | 0.7.0 | 0.16.1 | — |
| 43 | gradle-git | 0.6.4 | grgit 5.x | — |
| 44 | wagon-ssh | 2.2 | 不要（maven-publish 移行） | — |

### 総推定工数

| フェーズ | 推定工数 |
|---------|---------|
| Phase 0: 現状分析とテストベースライン確立 | 2〜3日 |
| Phase 1: ビルドインフラのモダナイズ | 5〜7日 |
| Phase 2: Java バージョンアップ | 2〜3日 |
| Phase 3: Jakarta EE マイグレーション | 5〜7日 |
| Phase 4: コア依存ライブラリの更新 | 10〜15日 |
| Phase 5: テストインフラの更新 | 5〜7日 |
| Phase 6: CI/CD の再構築 | 1〜2日 |
| Phase 7: ドキュメント・クリーンアップ | 1〜2日 |
| **合計** | **31〜46日** |
