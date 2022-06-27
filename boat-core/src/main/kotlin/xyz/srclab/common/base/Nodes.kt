package xyz.srclab.common.base

import lombok.Data

/**
 * Single linked list node.
 */
@Data
class SNode<T : Any> @JvmOverloads constructor(
    var value: T? = null,
    var next: SNode<T>? = null,
)

/**
 * Double linked list node.
 */
@Data
class DNode<T : Any> @JvmOverloads constructor(
    var value: T? = null,
    var next: DNode<T>? = null,
    var prev: DNode<T>? = null,
)

/**
 * Tree node.
 */
@Data
class TreeNode<T : Any> @JvmOverloads constructor(
    var value: T? = null,
    var children: MutableList<TreeNode<T>>? = null,
)

/**
 * Binary Tree node.
 */
@Data
class BTreeNode<T : Any> @JvmOverloads constructor(
    var value: T? = null,
    var left: BTreeNode<T>? = null,
    var right: BTreeNode<T>? = null,
)