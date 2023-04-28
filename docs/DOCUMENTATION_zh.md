# <span class="image">![logo](../logo.svg)</span> Boat: SrcLab的Java/kotlin核心基础库

<span id="author" class="author">孙谦</span>
<span id="email" class="email"><fredsuvn@163.com></span>

目录

-   [简介](#_简介)
-   [获取](#_获取)
    -   [获取 Boat](#_获取_boat)
    -   [使用 BOM](#_使用_bom)
-   [用法](#_用法)
    -   [Boat Annotations](#_boat_annotations)
    -   [Boat Core](#_boat_core)
    -   [Boat Serialize](#_boat_serialize)
    -   [Boat Codec](#_boat_codec)
    -   [Boat Id](#_boat_id)
    -   [Boat Others](#_boat_others)

## 简介

Boat是一组Java/Kotlin核心库集合（JDK 1.8+）, 主要由Kotlin编写,
广泛应用于 [SrcLab](https://github.com/srclab-projects) 里的项目.
它提供了许多方便快捷的接口, 函数和工具.

Boat 包括:

-   [boat-annotations](../boat-annotations/DOCUMENTATION.md): 核心注解;

-   [boat-core](../boat-core/DOCUMENTATION.md): 核心库;

-   [boat-data](../boat-data/DOCUMENTATION.md):
    序列化和反序列化核心库 (支持 `JSON`);

-   [boat-codec](../boat-codec/DOCUMENTATION.md): 编码核心库 (支持
    `HEX`, `BASE64`, `SHA`, `MD`, `HMAC`, `AES`, `RSA`, `SM2` 等);

-   [boat-id](../boat-id/DOCUMENTATION.md): 快速ID生成框架;

-   [boat-others](../boat-others/DOCUMENTATION.md): 第三方支持和扩展库
    (如 `protobuf`);

-   [boat-test](../boat-test/): 测试库的依赖管理工程;

-   [boat-bom](../boat-bom/): BOM (gradle platform) project;

如果你需要一次性导入常用库 (`annotations`, `core`, `codec`, `serialize`
and `id`), 只需要导入:

-   [boat](../boat/).

## 获取

### 获取 Boat

Gradle

    implementation "xyz.srclab.common:boat:0.0.1"

Maven

    <dependency>
      <groupId>xyz.srclab.common</groupId>
      <artifactId>boat</artifactId>
      <version>0.0.1</version>
    </dependency>

### 使用 BOM

Gradle

    api implementation("xyz.srclab.common:boat-bom:0.0.1")

Maven

    <dependencyManagement>
      <dependencies>
        <dependency>
          <groupId>xyz.srclab.common</groupId>
          <artifactId>boat</artifactId>
          <version>0.0.1</version>
          <type>pom</type>
          <scope>import</scope>
        </dependency>
      </dependencies>
    </dependencyManagement>

Source Code

<https://github.com/srclab-projects/boat>

## 用法

### Boat Annotations

-   AsciiDoc:

    -   [English](../boat-annotations/docs/DOCUMENTATION_en.adoc)

    -   [简体中文](../boat-annotations/docs/DOCUMENTATION_zh.adoc)

-   Markdown:

    -   [English](../boat-annotations/docs/DOCUMENTATION_en.md)

    -   [简体中文](../boat-annotations/docs/DOCUMENTATION_zh.md)

-   HTML:

    -   [English](../boat-annotations/docs/DOCUMENTATION_en.html)

    -   [简体中文](../boat-annotations/docs/DOCUMENTATION_zh.html)

### Boat Core

-   AsciiDoc:

    -   [English](../boat-core/docs/DOCUMENTATION_en.adoc)

    -   [简体中文](../boat-core/docs/DOCUMENTATION_zh.adoc)

-   Markdown:

    -   [English](../boat-core/docs/DOCUMENTATION_en.md)

    -   [简体中文](../boat-core/docs/DOCUMENTATION_zh.md)

-   HTML:

    -   [English](../boat-core/docs/DOCUMENTATION_en.html)

    -   [简体中文](../boat-core/docs/DOCUMENTATION_zh.html)

### Boat Serialize

-   AsciiDoc:

    -   [English](../boat-data/docs/DOCUMENTATION_en.adoc)

    -   [简体中文](../boat-data/docs/DOCUMENTATION_zh.adoc)

-   Markdown:

    -   [English](../boat-data/docs/DOCUMENTATION_en.md)

    -   [简体中文](../boat-data/docs/DOCUMENTATION_zh.md)

-   HTML:

    -   [English](../boat-data/docs/DOCUMENTATION_en.html)

    -   [简体中文](../boat-data/docs/DOCUMENTATION_zh.html)

### Boat Codec

-   AsciiDoc:

    -   [English](../boat-codec/docs/DOCUMENTATION_en.adoc)

    -   [简体中文](../boat-codec/docs/DOCUMENTATION_zh.adoc)

-   Markdown:

    -   [English](../boat-codec/docs/DOCUMENTATION_en.md)

    -   [简体中文](../boat-codec/docs/DOCUMENTATION_zh.md)

-   HTML:

    -   [English](../boat-codec/docs/DOCUMENTATION_en.html)

    -   [简体中文](../boat-codec/docs/DOCUMENTATION_zh.html)

### Boat Id

-   AsciiDoc:

    -   [English](../boat-id/docs/DOCUMENTATION_en.adoc)

    -   [简体中文](../boat-id/docs/DOCUMENTATION_zh.adoc)

-   Markdown:

    -   [English](../boat-id/docs/DOCUMENTATION_en.md)

    -   [简体中文](../boat-id/docs/DOCUMENTATION_zh.md)

-   HTML:

    -   [English](../boat-id/docs/DOCUMENTATION_en.html)

    -   [简体中文](../boat-id/docs/DOCUMENTATION_zh.html)

### Boat Others

-   AsciiDoc:

    -   [English](../boat-others/docs/DOCUMENTATION_en.adoc)

    -   [简体中文](../boat-others/docs/DOCUMENTATION_zh.adoc)

-   Markdown:

    -   [English](../boat-others/docs/DOCUMENTATION_en.md)

    -   [简体中文](../boat-others/docs/DOCUMENTATION_zh.md)

-   HTML:

    -   [English](../boat-others/docs/DOCUMENTATION_en.html)

    -   [简体中文](../boat-others/docs/DOCUMENTATION_zh.html)
