package xyz.srclab.common.id

/**
 * Core interface to generate global unique id.
 *
 * To generate a global unique id, we need 3 values:
 *
 * * [S]: seed value, such as timestamp or UUID;
 * * [L]: local value, local value (with seed) value make id local unique, such as sequence number;
 * * [I] instance value, instance value (with seed) value make id unique in all instances, such as sequence number;
 *
 * [R] is type of final global unique id, the final id comes from [S], [L] and [I].
 */
interface IdGenerator<S, L, I, R> {

    fun next(): R

    companion object {

        @JvmStatic
        fun <S, L, I, R> newIdGenerator(
            seed: () -> S,
            local: (S) -> L,
            instance: (S) -> I,
            joiner: (L, I) -> R
        ): IdGenerator<S, L, I, R> {
            return object : IdGenerator<S, L, I, R> {
                override fun next(): R {
                    val s = seed()
                    val l = local(s)
                    val i = instance(s)
                    return joiner(l, i)
                }
            }
        }
    }
}