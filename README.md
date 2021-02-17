# _Welcome to Boat_

- Markdown:
    * [English](readme/README_en.md)
    * [简体中文](readme/README_zh.md)

- AsciiDoc:
    * [English](readme/README_en.adoc)
    * [简体中文](readme/README_zh.adoc)

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
        Egg egg = eggManager.pick("Hello, Boat Egg!");
        egg.hatchOut("出来吧，神龙！");
    }
}
```

```kotlin
class EggSampleKt {

    @Test
    fun testEgg() {
        val egg = BoatEggManager.pick("Hello, Boat Egg!")
        egg.hatchOut("出来吧，神龙！")
    }
}
```