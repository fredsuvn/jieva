package xyz.srclab.common.id

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME

/**
 *  Id component generator.
 *
 * @param [E] component type
 * @author sunqian
 */
interface IdComponentGenerator<E> {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
        @JvmName("name") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: String
        @JvmName("type") get

    fun generate(context: IdGenerationContext): E
}