# <span class="image">![logo](../logo.svg)</span> Boat: SrcLab Core Libraries for Java/kotlin

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

-   [boat-annotations](../boat-annotations/DOCUMENTATION.md): Core
    annotations;

-   [boat-core](../boat-core/DOCUMENTATION.md): Core lib;

-   [boat-data](../boat-data/DOCUMENTATION.md): Serialization
    and deserialization lib (supports `JSON`);

-   [boat-codec](../boat-codec/DOCUMENTATION.md): Codec lib (supports
    `HEX`, `BASE64`, `SHA`, `MD`, `HMAC`, `AES`, `RSA`, `SM2`, etc.);

-   [boat-id](../boat-id/DOCUMENTATION.md): Fast ID generation lib;

-   [boat-others](../boat-others/DOCUMENTATION.md): Third party
    supporting and extension lib (such as `protobuf`);

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
