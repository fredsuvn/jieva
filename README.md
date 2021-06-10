# Boat: SrcLab Core Libraries for Java/kotlin

<p align="center">
<img src="logo.svg"/>
</p>

## Read Me:

- AsciiDoc:
  * [English](docs/README_en.adoc)
  * [简体中文](docs/README_zh.adoc)
- Markdown:
  * [English](docs/README_en.md)
  * [简体中文](docs/README_zh.md)
- HTML:
  * [English](docs/README_en.html)
  * [简体中文](docs/README_zh.html)

More see [docs/](docs/)

## Join

* fredsuvn@163.com
* https://github.com/srclab-projects/boat
* QQ group: 1037555759

## License

[Apache 2.0 license][license]

[license]: https://www.apache.org/licenses/LICENSE-2.0.html

###### _There seems to be something below?_

↓↓↓↓↓↓↓↓

<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

# boat-egg: _*Wow, you found me!*_

## Java

```java
public class EggSample {

  @Test
  public void testEgg() {
    EggManager eggManager = BoatEggManager.INSTANCE;
    Egg egg = eggManager.pick("O Battle");
    egg.hatchOut("Thank you, Taro.");
    //Or
    //egg.hatchOut("谢谢你，泰罗。");
  }
}
```

## Kotlin

```kotlin
class EggSample {

  @Test
  fun testEgg() {
    val egg = BoatEggManager.pick("O Battle")
    egg.hatchOut("Thank you, Taro.")
    //Or
    //egg.hatchOut("谢谢你，泰罗。")
  }
}
```