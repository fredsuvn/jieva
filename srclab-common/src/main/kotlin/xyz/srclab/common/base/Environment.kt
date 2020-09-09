package xyz.srclab.common.base

/**
 * @author sunqian
 */
interface Environment {

    fun properties(): Map<String, String>

    fun getProperty(name: String): String

    fun setProperty(name: String)

    interface System : Java, Kotlin, OS, User, App, Ext

    interface Java : Environment {
        @get:JvmName("version")
         val version: String
         val vendor: String
         val vendorUrl: String
         val home: String
         val vmSpecificationVersion: String
         val vmSpecificationVendor: String
         val vmSpecificationName: String
         val vmVersion: String
         val vmVendor: String
         val vmName: String
         val specificationVersion: String
         val specificationVendor: String
         val specificationName: String
         val classVersion: String
         val classPath: String
         val libraryPath: String
         val ioTmpdir: String
         val compiler: String
         val extDirs: String
    }

    interface Kotlin : Environment

    interface OS : Environment

    interface User : Environment

    interface App : Environment

    interface Ext : Environment

    companion object {


        private object JavaImpl : Java {

            override fun properties(): Map<String, String> {
                TODO("Not yet implemented")
            }

            override fun getProperty(name: String): String {
                TODO("Not yet implemented")
            }

            override fun setProperty(name: String) {
                TODO("Not yet implemented")
            }
        }
    }
}