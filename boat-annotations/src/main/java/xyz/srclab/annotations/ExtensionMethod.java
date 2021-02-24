package xyz.srclab.annotations;

/**
 * Denote method is an extension function.
 * <p>
 * Extension function is a type of method of which first parameter is called <b>receiver parameter</b>. In some jvm
 * language (such as kotlin), there may exist special syntax for extension function.
 *
 * @author sunqian
 */
public @interface ExtensionMethod {

    Accepted[] receiverAcceptable() default {};

    Rejected[] receiverRejectable() default {};
}
