# <span class="image">![Boat](../logo.svg)</span> Boat: SrcLab Core Libraries for Java/kotlin

<span id="author" class="author">Sun Qian</span>  
<span id="email" class="email"><fredsuvn@163.com></span>  

Table of Contents

-   [Introduction](#_introduction)
-   [Getting](#_getting)
    -   [Get Boat](#_get_boat)
    -   [With BOM](#_with_bom)
-   [Usages](#_usages)
    -   [Boat Annotations](#_boat_annotations)
    -   [Boat Core](#_boat_core)
    -   [Boat Serialize](#_boat_serialize)
    -   [Boat Codec](#_boat_codec)
    -   [Boat Id](#_boat_id)
    -   [Boat Others](#_boat_others)

## Introduction

Boat is a set of core Java/Kotlin libraries (JDK 1.8+), mostly written
by kotlin, widely used on JVM projects within
[SrcLab](https://github.com/srclab-projects). It provides many fast and
convenient interfaces, functions and utilities.

Boat includes:

-   [boat-annotations](../boat-annotations/README.md): Core annotations;

-   [boat-core](../boat-core/README.md): Core lib;

-   [boat-serialize](../boat-serialize/README.md): Serialization and
    deserialization lib (supports `JSON`);

-   [boat-codec](../boat-codec/README.md): Codec lib (supports `HEX`,
    `BASE64`, `SHA`, `MD`, `HMAC`, `AES`, `RSA`, `SM2`, etc.);

-   [boat-id](../boat-id/README.md): Fast ID generation lib;

-   [boat-others](../boat-others/README.md): Third party supporting and
    extension lib (such as `protobuf`);

-   [boat-test](../boat-test/): Testing libs dependencies management
    project;

-   [boat-bom](../boat-bom/): BOM (gradle platform) project;

If you want to import common jars at once (`annotations`, `core`,
`codec`, `serialize` and `id`), just import:

-   [boat](../boat/).

## Getting

### Get Boat

Gradle

    implementation "xyz.srclab.common:boat:0.0.1"

Maven

    <dependency>
      <groupId>xyz.srclab.common</groupId>
      <artifactId>boat</artifactId>
      <version>0.0.1</version>
    </dependency>

### With BOM

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

## Usages

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
