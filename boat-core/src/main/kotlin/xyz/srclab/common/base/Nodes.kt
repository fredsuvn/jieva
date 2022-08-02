package xyz.srclab.common.base

import lombok.Data
import java.io.Serializable

/**
 * Single linked list node.
 */
@Data
open class SNode<T : Any> @JvmOverloads constructor(
    open var value: T? = null,
    open var next: SNode<T>? = null,
) : Serializable

/**
 * Double linked list node.
 */
@Data
open class DNode<T : Any> @JvmOverloads constructor(
    open var value: T? = null,
    open var next: DNode<T>? = null,
    open var prev: DNode<T>? = null,
) : Serializable

/**
 * Tree node.
 */
@Data
open class TreeNode<T : Any> @JvmOverloads constructor(
    open var value: T? = null,
    open var children: MutableList<TreeNode<T>>? = null,
) : Serializable

/**
 * Binary Tree node.
 */
@Data
open class BTreeNode<T : Any> @JvmOverloads constructor(
    open var value: T? = null,
    open var left: BTreeNode<T>? = null,
    open var right: BTreeNode<T>? = null,
) : Serializable