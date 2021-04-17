# ![Boat](logo.svg) Boat: SrcLab Core Libraries for Java/kotlin

## _Choose the doc style and language you like_:

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

## _Contact_

* fredsuvn@163.com
* https://github.com/srclab-projects/boat
* QQ group: 1037555759

## _License_

[Apache 2.0 license][license]

[license]: https://www.apache.org/licenses/LICENSE-2.0.html

---
There seems to be something below...
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
---

## _boat-egg: Wow, you found me!_

* Java

```java
public class EggSample {

  @Test
  public void testEgg() {
    EggManager eggManager = BoatEggManager.INSTANCE;
    Egg egg = eggManager.pick("O Space Battle");
    //egg.hatchOut("Thank you, Taro.");
    egg.hatchOut("谢谢你，泰罗。");
  }
}
```

* Kotlin

```kotlin
class EggSample {

  @Test
  fun testEgg() {
    val egg = BoatEggManager.pick("O Space Battle")
    //egg.hatchOut("Thank you, Taro.")
    egg.hatchOut("谢谢你，泰罗。")
  }
}
```