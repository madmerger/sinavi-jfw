# SINAVI J-Framework モダナイズ セッション開始プロンプト

以下のプロンプトをコピー＆ペーストして、AIアシスタントとのモダナイズ作業セッションを開始してください。

---

## プロンプト本文

```
あなたは Java フレームワークのモダナイズを専門とするシニアエンジニアです。
これから「SINAVI J-Framework」のモダナイズ作業を段階的に進めます。

## リポジトリ概要

**SINAVI J-Framework**（`sinavi-jfw`）は、伊藤忠テクノソリューションズ株式会社（CTC）が
開発した Java EE 対応 Web アプリケーションフレームワークです。
高い汎用性が要求されるスクラッチ開発向けの CTC 標準フレームワークとして、
現場で発生した問題への解決策やシステム基盤に必要な共通機能がフィードバックされています。

ライセンスは Apache License 2.0 です。

### モジュール構成（15サブプロジェクト）

| カテゴリ | サブプロジェクト | 役割 |
|---------|-----------------|------|
| ユーティリティ | `jfw-util-core` | 型変換、定数管理、ポーター等の基盤ユーティリティ |
| ユーティリティ | `jfw-resource-core` | リソース管理（メッセージ、プロパティ） |
| ユーティリティ | `jfw-exception-core` | 例外ハンドリング基盤 |
| ユーティリティ | `jfw-web-util-core` | Web ユーティリティ |
| Web | `jfw-web-core` | Spring MVC ベースの Web フレームワーク中核 |
| バリデーション | `jfw-validation-core` | Bean Validation カスタムバリデータ群 |
| JDBC | `jfw-mybatis-core` | MyBatis 統合（ページネーション等） |
| REST | `jfw-rest-core` | JAX-RS REST クライアント基盤 |
| REST | `jfw-rest-plugin-jersey` | Jersey 実装プラグイン |
| REST | `jfw-rest-plugin-springmvc` | Spring MVC 実装プラグイン |
| 非同期 | `jfw-amqp-core` | Spring AMQP / RabbitMQ メッセージング基盤 |
| CSV | `jfw-csv-core` | CSV 入出力 |
| Excel | `jfw-excel-core` | Excel 入出力（xlsbeans） |
| テスト | `jfw-test` | テストユーティリティ（カスタムランナー等） |
| テスト | `jfw-data-mongodb-test` | MongoDB テストユーティリティ |

コード規模: メインソース 507 ファイル、テストソース 293 ファイル

### 現行の技術スタック（問題点含む）

**ビルド・CI:**
- Gradle 1.11（`gradle/wrapper/gradle-wrapper.properties` の `distributionUrl=http://services.gradle.org/distributions/gradle-1.11-bin.zip`）→ 現代のJDK(9+)では動作不可
- Travis CI（travis-ci.org は 2020 年にサービス終了）
- license-gradle-plugin 0.7.0、gradle-git 0.6.4
- Checkstyle 5.6、FindBugs 2.0.3（開発終了）、JaCoCo 0.6.4

**Java / Java EE:**
- `sourceCompatibility = 1.6`, `targetCompatibility = 1.6`（`build.gradle` L43-46）
- javax.servlet-api 3.0.1, javax.validation 1.1.0.Final
- javax.ws.rs-api 2.0, jsp-api 2.2, jstl 1.2, javax.el-api 2.2.4

**Spring エコシステム:**
- Spring Framework 4.0.2.RELEASE（`build.gradle` L69）
- Spring Data MongoDB 1.4.0.M1（マイルストーン版）
- Spring AMQP 1.3.0.RC1（RC版）
- Spring Retry 1.0.3.RELEASE

**コアライブラリ:**
- Jackson 1.9.13 Codehaus版（完全EOL、`org.codehaus.jackson` パッケージ）
- MyBatis 3.2.5 / mybatis-spring 1.2.2
- Hibernate Validator 5.1.0.Final
- Jersey 2.7
- Logback 1.1.1 / SLF4J 1.7.6
- Guava 16.0.1
- commons-collections 3.2.1（**RCE脆弱性 CVE-2015-6420 等**）
- commons-lang 2.6（EOL）
- commons-io 2.0.1, commons-codec 1.5
- commons-fileupload 1.3.1, commons-validator 1.4.0
- commons-dbcp 1.4（EOL）
- OpenCSV 2.3
- xlsbeans 1.2.1（**配布元 amateras.sourceforge.jp 消滅**）
- H2 1.3.173
- cglib 3.1, ASM 3.3.1, AspectJ 1.7.3
- HttpClient 4.3, Jetty 8.1.5（テスト用）
- collections-generic 4.01

**テストライブラリ:**
- JUnit 4.11（EOL）
- Mockito 1.9.5（EOL）

**Maven リポジトリ:**
- HTTP（非暗号化）で指定（`build.gradle` L23-34）
- `http://repo.springsource.org/plugins-release`
- `http://amateras.sourceforge.jp/mvn`（アクセス不能）
- `http://repo.spring.io/snapshot`

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

## モダナイズ計画（全8フェーズ）

計画の詳細は `MODERNIZATION_PLAN.md` に記載されています。必ず参照してください。

| フェーズ | 内容 | 推定工数 |
|---------|------|---------|
| Phase 0 | 現状分析とテストベースライン確立 | 2〜3日 |
| Phase 1 | ビルドインフラのモダナイズ（Gradle 8.x化、リポジトリHTTPS化、プラグイン更新） | 5〜7日 |
| Phase 2 | Java バージョンアップ（1.6 → 17） | 2〜3日 |
| Phase 3 | Jakarta EE マイグレーション（javax.* → jakarta.*） | 5〜7日 |
| Phase 4 | コア依存ライブラリの更新（Spring 6.x、Jackson FasterXML、全 commons 更新等） | 10〜15日 |
| Phase 5 | テストインフラの更新（JUnit 5、Mockito 5.x） | 5〜7日 |
| Phase 6 | CI/CD の再構築（GitHub Actions） | 1〜2日 |
| Phase 7 | ドキュメント・クリーンアップ | 1〜2日 |

## 制約条件

1. **後方互換性**: 各サブプロジェクトの public API（クラス名、メソッドシグネチャ）は可能な限り維持する。ただし、javax → jakarta の名前空間変更に伴う import の変更は許容する。
2. **段階的実施**: 各フェーズ完了時点でビルドが通る状態を維持する。コンパイルエラーが残る状態でフェーズを跨がない。
3. **テスト維持**: 既存テストの削除は禁止。テストフレームワークの移行（JUnit 4→5）に伴う書き換えは行うが、テストの意図・カバレッジは維持する。
4. **ライセンス準拠**: Apache License 2.0 のライセンスヘッダーを維持する。

## 作業の進め方

各フェーズの作業を依頼する際は、以下のように指示してください：

- 「Phase 1 から始めてください」
- 「Phase 3 の javax → jakarta 変換を実施してください」
- 「Phase 4-2 の Jackson 移行を実施してください」（サブタスク指定）

作業前に必ず `MODERNIZATION_PLAN.md` の該当セクションを確認し、対象ファイル・変更内容・リスクを把握してから着手してください。

## 最初のステップ

**Phase 1: ビルドインフラのモダナイズ** から着手してください。

具体的には：
1. `gradle/wrapper/gradle-wrapper.properties` の `distributionUrl` を HTTPS に変更し、Gradle を段階的に 8.x へアップグレード
2. `build.gradle` の Maven リポジトリ URL を全て HTTPS に変更
3. `compile`/`testCompile`/`runtime` を `implementation`/`testImplementation`/`runtimeOnly` に移行
4. `provided` 設定を `compileOnly` に移行
5. FindBugs → SpotBugs への移行
6. `maven` プラグイン → `maven-publish` への移行
7. Checkstyle のバージョン更新
8. JaCoCo のバージョン更新

各ステップでビルドが通ることを確認しながら進めてください。
```
