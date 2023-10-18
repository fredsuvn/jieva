# ![](logo.svg) fs: Core Libraries of [SrcLab](https://github.com/srclab-projects) and [Me](https://github.com/fredsuvn) for JVM

## Documentation:

- AsciiDoc:
  * [English](docs/DOCUMENTATION_en.adoc)
  * [简体中文](docs/DOCUMENTATION_zh.adoc)

More see [docs/](docs/)

## Build

This project depends on [srclab-dependencies](https://github.com/srclab-projects/srclab-dependencies), you may:

```shell
# install srclab-dependencies
git clone -b master https://github.com/srclab-projects/srclab-dependencies.git

cd srclab-dependencies && mvn clean install

# build fs
git clone -b master https://github.com/fredsuvn/fs.git

cd boat && gradle clean build
```

**Note:**

* Some properties should be configured if you want to enable publish to remote, see publish info part
  of [build.gradle](build.gradle)
* `fs-core` need `protoc` to compile protobuf files, some architectures don't support it (such as `ARM`);

## Join

* [SrcLab](https://github.com/srclab-projects)
* [Me](https://github.com/fredsuvn)
* QQ group: 566185308

## License

[Apache 2.0 license][license]

[license]: https://www.apache.org/licenses/LICENSE-2.0.html