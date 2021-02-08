//package xyz.srclab.common
//
//import xyz.srclab.common.base.*
//import java.time.ZoneId
//import java.time.ZonedDateTime
//
//interface AboutBoat : About {
//
//    @Suppress(INAPPLICABLE_JVM_NAME)
//    val logo: String
//        @JvmName("logo") get
//
//    fun showLogo()
//}
//
//@JvmOverloads
//fun aboutBoat(major: Int = currentMajor): About {
//    return when (major) {
//        0 -> aboutBoatV0
//        1 -> aboutBoatV1
//        else -> throw IllegalArgumentException("Version of major not found: $major")
//    }
//}
//
//private const val currentMajor = 0
//
//private const val nameFromV0 = "Boat"
//
//private const val urlFromV0 = "https://github.com/srclab-projects/boat"
//
//private val zoneIdFromV0 = ZoneId.of("Asia/Shanghai")
//
//private val licenceOfApache2 = Licence.of(
//    "Apache 2.0 License",
//    "https://www.apache.org/licenses/LICENSE-2.0.txt",
//    """
//                                 Apache License
//                           Version 2.0, January 2004
//                        http://www.apache.org/licenses/
//
//   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
//
//   1. Definitions.
//
//      "License" shall mean the terms and conditions for use, reproduction,
//      and distribution as defined by Sections 1 through 9 of this document.
//
//      "Licensor" shall mean the copyright owner or entity authorized by
//      the copyright owner that is granting the License.
//
//      "Legal Entity" shall mean the union of the acting entity and all
//      other entities that control, are controlled by, or are under common
//      control with that entity. For the purposes of this definition,
//      "control" means (i) the power, direct or indirect, to cause the
//      direction or management of such entity, whether by contract or
//      otherwise, or (ii) ownership of fifty percent (50%) or more of the
//      outstanding shares, or (iii) beneficial ownership of such entity.
//
//      "You" (or "Your") shall mean an individual or Legal Entity
//      exercising permissions granted by this License.
//
//      "Source" form shall mean the preferred form for making modifications,
//      including but not limited to software source code, documentation
//      source, and configuration files.
//
//      "Object" form shall mean any form resulting from mechanical
//      transformation or translation of a Source form, including but
//      not limited to compiled object code, generated documentation,
//      and conversions to other media types.
//
//      "Work" shall mean the work of authorship, whether in Source or
//      Object form, made available under the License, as indicated by a
//      copyright notice that is included in or attached to the work
//      (an example is provided in the Appendix below).
//
//      "Derivative Works" shall mean any work, whether in Source or Object
//      form, that is based on (or derived from) the Work and for which the
//      editorial revisions, annotations, elaborations, or other modifications
//      represent, as a whole, an original work of authorship. For the purposes
//      of this License, Derivative Works shall not include works that remain
//      separable from, or merely link (or bind by name) to the interfaces of,
//      the Work and Derivative Works thereof.
//
//      "Contribution" shall mean any work of authorship, including
//      the original version of the Work and any modifications or additions
//      to that Work or Derivative Works thereof, that is intentionally
//      submitted to Licensor for inclusion in the Work by the copyright owner
//      or by an individual or Legal Entity authorized to submit on behalf of
//      the copyright owner. For the purposes of this definition, "submitted"
//      means any form of electronic, verbal, or written communication sent
//      to the Licensor or its representatives, including but not limited to
//      communication on electronic mailing lists, source code control systems,
//      and issue tracking systems that are managed by, or on behalf of, the
//      Licensor for the purpose of discussing and improving the Work, but
//      excluding communication that is conspicuously marked or otherwise
//      designated in writing by the copyright owner as "Not a Contribution."
//
//      "Contributor" shall mean Licensor and any individual or Legal Entity
//      on behalf of whom a Contribution has been received by Licensor and
//      subsequently incorporated within the Work.
//
//   2. Grant of Copyright License. Subject to the terms and conditions of
//      this License, each Contributor hereby grants to You a perpetual,
//      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
//      copyright license to reproduce, prepare Derivative Works of,
//      publicly display, publicly perform, sublicense, and distribute the
//      Work and such Derivative Works in Source or Object form.
//
//   3. Grant of Patent License. Subject to the terms and conditions of
//      this License, each Contributor hereby grants to You a perpetual,
//      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
//      (except as stated in this section) patent license to make, have made,
//      use, offer to sell, sell, import, and otherwise transfer the Work,
//      where such license applies only to those patent claims licensable
//      by such Contributor that are necessarily infringed by their
//      Contribution(s) alone or by combination of their Contribution(s)
//      with the Work to which such Contribution(s) was submitted. If You
//      institute patent litigation against any entity (including a
//      cross-claim or counterclaim in a lawsuit) alleging that the Work
//      or a Contribution incorporated within the Work constitutes direct
//      or contributory patent infringement, then any patent licenses
//      granted to You under this License for that Work shall terminate
//      as of the date such litigation is filed.
//
//   4. Redistribution. You may reproduce and distribute copies of the
//      Work or Derivative Works thereof in any medium, with or without
//      modifications, and in Source or Object form, provided that You
//      meet the following conditions:
//
//      (a) You must give any other recipients of the Work or
//          Derivative Works a copy of this License; and
//
//      (b) You must cause any modified files to carry prominent notices
//          stating that You changed the files; and
//
//      (c) You must retain, in the Source form of any Derivative Works
//          that You distribute, all copyright, patent, trademark, and
//          attribution notices from the Source form of the Work,
//          excluding those notices that do not pertain to any part of
//          the Derivative Works; and
//
//      (d) If the Work includes a "NOTICE" text file as part of its
//          distribution, then any Derivative Works that You distribute must
//          include a readable copy of the attribution notices contained
//          within such NOTICE file, excluding those notices that do not
//          pertain to any part of the Derivative Works, in at least one
//          of the following places: within a NOTICE text file distributed
//          as part of the Derivative Works; within the Source form or
//          documentation, if provided along with the Derivative Works; or,
//          within a display generated by the Derivative Works, if and
//          wherever such third-party notices normally appear. The contents
//          of the NOTICE file are for informational purposes only and
//          do not modify the License. You may add Your own attribution
//          notices within Derivative Works that You distribute, alongside
//          or as an addendum to the NOTICE text from the Work, provided
//          that such additional attribution notices cannot be construed
//          as modifying the License.
//
//      You may add Your own copyright statement to Your modifications and
//      may provide additional or different license terms and conditions
//      for use, reproduction, or distribution of Your modifications, or
//      for any such Derivative Works as a whole, provided Your use,
//      reproduction, and distribution of the Work otherwise complies with
//      the conditions stated in this License.
//
//   5. Submission of Contributions. Unless You explicitly state otherwise,
//      any Contribution intentionally submitted for inclusion in the Work
//      by You to the Licensor shall be under the terms and conditions of
//      this License, without any additional terms or conditions.
//      Notwithstanding the above, nothing herein shall supersede or modify
//      the terms of any separate license agreement you may have executed
//      with Licensor regarding such Contributions.
//
//   6. Trademarks. This License does not grant permission to use the trade
//      names, trademarks, service marks, or product names of the Licensor,
//      except as required for reasonable and customary use in describing the
//      origin of the Work and reproducing the content of the NOTICE file.
//
//   7. Disclaimer of Warranty. Unless required by applicable law or
//      agreed to in writing, Licensor provides the Work (and each
//      Contributor provides its Contributions) on an "AS IS" BASIS,
//      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
//      implied, including, without limitation, any warranties or conditions
//      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
//      PARTICULAR PURPOSE. You are solely responsible for determining the
//      appropriateness of using or redistributing the Work and assume any
//      risks associated with Your exercise of permissions under this License.
//
//   8. Limitation of Liability. In no event and under no legal theory,
//      whether in tort (including negligence), contract, or otherwise,
//      unless required by applicable law (such as deliberate and grossly
//      negligent acts) or agreed to in writing, shall any Contributor be
//      liable to You for damages, including any direct, indirect, special,
//      incidental, or consequential damages of any character arising as a
//      result of this License or out of the use or inability to use the
//      Work (including but not limited to damages for loss of goodwill,
//      work stoppage, computer failure or malfunction, or any and all
//      other commercial damages or losses), even if such Contributor
//      has been advised of the possibility of such damages.
//
//   9. Accepting Warranty or Additional Liability. While redistributing
//      the Work or Derivative Works thereof, You may choose to offer,
//      and charge a fee for, acceptance of support, warranty, indemnity,
//      or other liability obligations and/or rights consistent with this
//      License. However, in accepting such obligations, You may act only
//      on Your own behalf and on Your sole responsibility, not on behalf
//      of any other Contributor, and only if You agree to indemnify,
//      defend, and hold each Contributor harmless for any liability
//      incurred by, or claims asserted against, such Contributor by reason
//      of your accepting any such warranty or additional liability.
//
//   END OF TERMS AND CONDITIONS
//
//   APPENDIX: How to apply the Apache License to your work.
//
//      To apply the Apache License to your work, attach the following
//      boilerplate notice, with the fields enclosed by brackets "[]"
//      replaced with your own identifying information. (Don't include
//      the brackets!)  The text should be enclosed in the appropriate
//      comment syntax for the file format. We also recommend that a
//      file or class name and description of purpose be included on the
//      same "printed page" as the copyright notice for easier
//      identification within third-party archives.
//
//   Copyright [yyyy] [name of copyright owner]
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//    """.trimIndent()
//)
//
//private val poweredBySrcLab = PoweredBy.of(
//    "SrcLab",
//    "https://github.com/srclab-projects",
//    "fredsuvn@163.com"
//)
//
//private val aboutBoatV0 = About.of(
//    nameFromV0,
//    urlFromV0,
//    Version.of(
//        ZonedDateTime.of(
//            2020, 10, 17,
//            23, 59, 59, 0,
//            zoneIdFromV0
//        ),
//        0, 0, 0
//    ),
//    licenceOfApache2,
//    poweredBySrcLab,
//    """
//        直长多行忽闲将欲拔停玉金
//        挂风歧路复来登渡剑杯盘樽
//        云破路难乘垂太黄四投珍清
//        帆浪，，舟钓行河顾箸羞酒
//        济会今行梦碧雪冰心不直斗
//        沧有安路日溪满塞茫能万十
//        海时在难边上山川然食钱千
//    """.trimIndent()
//)
//
//private val aboutBoatV1 = About.of(
//    nameFromV0,
//    urlFromV0,
//    Version.of(
//        ZonedDateTime.of(
//            2020, 11, 11,
//            23, 59, 59, 0,
//            zoneIdFromV0
//        ),
//        1, 0, 0, listOf("pre-plan")
//    ),
//    licenceOfApache2,
//    poweredBySrcLab,
//    """
//        君不见，黄河之水天上来，奔流到海不复回。
//        君不见，高堂明镜悲白发，朝如青丝暮成雪。
//        人生得意须尽欢，莫使金樽空对月。
//        天生我材必有用，千金散尽还复来。
//        烹羊宰牛且为乐，会须一饮三百杯。
//        岑夫子，丹丘生，将进酒，杯莫停。
//        与君歌一曲，请君为我倾耳听。
//        钟鼓馔玉不足贵，但愿长醉不愿醒。
//        古来圣贤皆寂寞，惟有饮者留其名。
//        陈王昔时宴平乐，斗酒十千恣欢谑。
//        主人何为言少钱，径须沽取对君酌。
//        五花马、千金裘，呼儿将出换美酒，与尔同销万古愁。
//    """.trimIndent()
//)
//
//interface Egg {
//
//    fun start()
//}
//
//class EggNotFoundException : RuntimeException("You asked me for an egg? Now I give you a bullshit!")
//
//@JvmOverloads
//fun giveMeAnEgg(major: Int = currentMajor): Egg {
//    return when (major) {
//        0 -> ALoneBoatWithAStrawDressedOldFisherInTheSnow
//        1 -> Starmon
//        else -> throw EggNotFoundException()
//    }
//}
//
//private object ALoneBoatWithAStrawDressedOldFisherInTheSnow : Egg {
//    override fun start() {
//        TODO("Not yet implemented")
//    }
//}
//
//private object Starmon : Egg {
//    override fun start() {
//        TODO("Not yet implemented")
//    }
//}
//
///*
// * __________                            ____________              _____
// * ___  ____/_____ ____________  __      __  ___/_  /______ _________  /_____________
// * __  __/  _  __ `/_  ___/_  / / /___________ \_  __/  __ `/_  ___/  __/  _ \_  ___/
// * _  /___  / /_/ /_(__  )_  /_/ /_/_____/___/ // /_ / /_/ /_  /   / /_ /  __/  /
// * /_____/  \__,_/ /____/ _\__, /        /____/ \__/ \__,_/ /_/    \__/ \___//_/
// *                        /____/
// *                                                                srclab.xyz, sunqian
// *                                                                   fredsuvn@163.com
// *
// * Source: http://www.network-science.de/ascii/
// * Font: speed
// */
//private val logo = """
// __________                            ____________              _____
// ___  ____/_____ ____________  __      __  ___/_  /______ _________  /_____________
// __  __/  _  __ `/_  ___/_  / / /___________ \_  __/  __ `/_  ___/  __/  _ \_  ___/
// _  /___  / /_/ /_(__  )_  /_/ /_/_____/___/ // /_ / /_/ /_  /   / /_ /  __/  /
// /_____/  \__,_/ /____/ _\__, /        /____/ \__/ \__,_/ /_/    \__/ \___//_/
//                        /____/
//                                                                srclab.xyz, sunqian
//                                                                   fredsuvn@163.com
//
// Source: http://www.network-science.de/ascii/
// Font: speed
//""".trimIndent()