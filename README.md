# _Welcome to Boat_

![Boat](logo.svg)

- Markdown:
    * [English](docs/README_en.md)
    * [简体中文](docs/README_zh.md)

- AsciiDoc:
    * [English](docs/README_en.adoc)
    * [简体中文](docs/README_zh.adoc)

More see [docs/](docs/)

## _Contact_

* fredsuvn@163.com
* https://github.com/srclab-projects/boat
* QQ group: 1037555759

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

```kotlin
class EggSampleKt {

    @Test
    fun testEgg() {
        val egg = BoatEggManager.pick("O Space Battle")
        //egg.hatchOut("Thank you, Taro.")
        egg.hatchOut("谢谢你，泰罗。")
    }
}
```