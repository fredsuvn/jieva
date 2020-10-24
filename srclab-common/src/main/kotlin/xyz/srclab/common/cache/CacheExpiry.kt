package xyz.srclab.common.cache

import xyz.srclab.common.base.CachingProductBuilder
import java.time.Duration

/**
 * @author sunqian
 */
interface CacheExpiry {

    fun expiryAfterCreate(): Duration
    fun expiryAfterRead(): Duration
    fun expiryAfterUpdate(): Duration

    class Builder : CachingProductBuilder<CacheExpiry>() {

        private var expiryAfterCreate: Duration = Duration.ZERO
        private var expiryAfterRead: Duration = Duration.ZERO
        private var expiryAfterUpdate: Duration = Duration.ZERO

        fun expiryAfterCreate(expiryAfterCreate: Duration): Builder {
            this.expiryAfterCreate = expiryAfterCreate
            this.commitChange()
            return this
        }

        fun expiryAfterRead(expiryAfterRead: Duration): Builder {
            this.expiryAfterRead = expiryAfterRead
            this.commitChange()
            return this
        }

        fun expiryAfterUpdate(expiryAfterUpdate: Duration): Builder {
            this.expiryAfterUpdate = expiryAfterUpdate
            this.commitChange()
            return this
        }

        override fun buildNew(): CacheExpiry {
            return CacheExpiryImpl(this)
        }

        private class CacheExpiryImpl(builder: Builder) : CacheExpiry {

            private val expiryAfterCreate: Duration
            private val expiryAfterRead: Duration
            private val expiryAfterUpdate: Duration

            override fun expiryAfterCreate(): Duration {
                return expiryAfterCreate
            }

            override fun expiryAfterRead(): Duration {
                return expiryAfterRead
            }

            override fun expiryAfterUpdate(): Duration {
                return expiryAfterUpdate
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as CacheExpiryImpl

                if (expiryAfterCreate != other.expiryAfterCreate) return false
                if (expiryAfterRead != other.expiryAfterRead) return false
                if (expiryAfterUpdate != other.expiryAfterUpdate) return false

                return true
            }

            override fun hashCode(): Int {
                var result = expiryAfterCreate.hashCode() ?: 0
                result = 31 * result + (expiryAfterRead.hashCode() ?: 0)
                result = 31 * result + (expiryAfterUpdate.hashCode() ?: 0)
                return result
            }

            init {
                expiryAfterCreate = builder.expiryAfterCreate
                expiryAfterRead = builder.expiryAfterRead
                expiryAfterUpdate = builder.expiryAfterUpdate
            }
        }
    }

    companion object {

        @JvmField
        val ZERO = newBuilder().build()

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }
    }
}