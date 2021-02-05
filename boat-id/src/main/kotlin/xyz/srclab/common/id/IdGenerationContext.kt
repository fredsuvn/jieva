package xyz.srclab.common.id

/**
 * Context of Id Generator.
 *
 * @author sunqian
 */
interface IdGenerationContext {

    fun <T> getComponent(name: String): IdComponent<T>?
}