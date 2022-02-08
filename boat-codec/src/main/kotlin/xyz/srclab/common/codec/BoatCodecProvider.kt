package xyz.srclab.common.codec

import xyz.srclab.common.Boat
import xyz.srclab.common.base.asTyped
import xyz.srclab.common.codec.gm.SM2Cipher
import java.lang.Boolean
import java.lang.reflect.Field
import java.security.AccessController
import java.security.PrivilegedAction
import java.security.Provider
import java.security.Security
import kotlin.Exception

object BoatCodecProvider : Provider(Boat.NAME, "${Boat.VERSION.major}.${Boat.VERSION.minor}".toDouble(), Boat.NAME) {

    init {
        AccessController.doPrivileged(PrivilegedAction<Any?> {
            put("Cipher.SM2", SM2Cipher::class.java.name)
            null
        })

        put("Cipher.SM2", SM2Cipher::class.java.name)
        Security.addProvider(this)
        try {
            val clazz = Class.forName("javax.crypto.JceSecurity")
            val field: Field = clazz.getDeclaredField("verificationResults")
            field.setAccessible(true)
            val map:MutableMap<Provider,Any> = field.get(clazz).asTyped()
            map.put(this,true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}