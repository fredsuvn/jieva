package test.xyz.srclab.common

import xyz.srclab.common.test.asserts.AssertHelper

fun doAssert(actual: Any?, expected: Any?) {
    AssertHelper.printAssert(actual, expected)
}