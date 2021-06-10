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

-   [boat-annotations](../boat-annotations/README.md): 核心注解;

-   [boat-core](../boat-core/README.md): 核心库;

-   [boat-serialize](../boat-serialize/README.md):
    序列化和反序列化核心库 (支持 `JSON`);

-   [boat-codec](../boat-codec/README.md): 编码核心库 (支持 `HEX`,
    `BASE64`, `SHA`, `MD`, `HMAC`, `AES`, `RSA`, `SM2` 等);

-   [boat-id](../boat-id/README.md): 快速ID生成框架;

-   [boat-others](../boat-others/README.md): 第三方支持和扩展库 (如
    `protobuf`);

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

-   [boat-annotations.adoc
    English](../boat-annotations/docs/README_en.adoc)

-   [boat-annotations.adoc
    简体中文](../boat-annotations/docs/README_zh.adoc)

-   [boat-annotations.md English](../boat-annotations/docs/README_en.md)

-   [boat-annotations.md
    简体中文](../boat-annotations/docs/README_zh.md)

-   [boat-annotations.html
    English](../boat-annotations/docs/README_en.html)

-   [boat-annotations.html
    简体中文](../boat-annotations/docs/README_zh.html)

### Boat Core

-   [boat-core.adoc English](../boat-core/docs/README_en.adoc)

-   [boat-core.adoc 简体中文](../boat-core/docs/README_zh.adoc)

-   [boat-core.md English](../boat-core/docs/README_en.md)

-   [boat-core.md 简体中文](../boat-core/docs/README_zh.md)

-   [boat-core.html English](../boat-core/docs/README_en.html)

-   [boat-core.html 简体中文](../boat-core/docs/README_zh.html)

### Boat Serialize

-   [boat-serialize.adoc English](../boat-serialize/docs/README_en.adoc)

-   [boat-serialize.adoc
    简体中文](../boat-serialize/docs/README_zh.adoc)

-   [boat-serialize.md English](../boat-serialize/docs/README_en.md)

-   [boat-serialize.md 简体中文](../boat-serialize/docs/README_zh.md)

-   [boat-serialize.html English](../boat-serialize/docs/README_en.html)

-   [boat-serialize.html
    简体中文](../boat-serialize/docs/README_zh.html)

### Boat Codec

-   [boat-codec.adoc English](../boat-codec/docs/README_en.adoc)

-   [boat-codec.adoc 简体中文](../boat-codec/docs/README_zh.adoc)

-   [boat-codec.md English](../boat-codec/docs/README_en.md)

-   [boat-codec.md 简体中文](../boat-codec/docs/README_zh.md)

-   [boat-codec.html English](../boat-codec/docs/README_en.html)

-   [boat-codec.html 简体中文](../boat-codec/docs/README_zh.html)

### Boat Id

-   [boat-id.adoc English](../boat-id/docs/README_en.adoc)

-   [boat-id.adoc 简体中文](../boat-id/docs/README_zh.adoc)

-   [boat-id.md English](../boat-id/docs/README_en.md)

-   [boat-id.md 简体中文](../boat-id/docs/README_zh.md)

-   [boat-id.html English](../boat-id/docs/README_en.html)

-   [boat-id.html 简体中文](../boat-id/docs/README_zh.html)

### Boat Others

-   [boat-others.adoc English](../boat-others/docs/README_en.adoc)

-   [boat-others.adoc 简体中文](../boat-others/docs/README_zh.adoc)

-   [boat-others.md English](../boat-others/docs/README_en.md)

-   [boat-others.md 简体中文](../boat-others/docs/README_zh.md)

-   [boat-others.html English](../boat-others/docs/README_en.html)

-   [boat-others.html 简体中文](../boat-others/docs/README_zh.html)
