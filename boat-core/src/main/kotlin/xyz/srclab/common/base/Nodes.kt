package xyz.srclab.common.base

import lombok.Data
import java.io.Serializable

/**
 * Single linked list node.
 */
@Data
class SNode<T : Any> @JvmOverloads constructor(
    var value: T? = null,
    var next: SNode<T>? = null,
) : Serializable {
    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}

/**
 * Double linked list node.
 */
@Data
class DNode<T : Any> @JvmOverloads constructor(
    var value: T? = null,
    var next: DNode<T>? = null,
    var prev: DNode<T>? = null,
) : Serializable {
    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}

/**
 * Tree node.
 */
@Data
class TreeNode<T : Any> @JvmOverloads constructor(
    var value: T? = null,
    var children: MutableList<TreeNode<T>>? = null,
) : Serializable {
    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}

/**
 * Binary Tree node.
 */
@Data
class BTreeNode<T : Any> @JvmOverloads constructor(
    var value: T? = null,
    var left: BTreeNode<T>? = null,
    var right: BTreeNode<T>? = null,
) : Serializable {
    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}