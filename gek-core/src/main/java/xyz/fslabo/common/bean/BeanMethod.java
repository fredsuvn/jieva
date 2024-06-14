package xyz.fslabo.common.bean;

import xyz.fslabo.annotations.Immutable;

/**
 * Information about the method of {@link BeanInfo}, commonly using {@link BeanInfo#getMethods()} or to get the
 * instance.
 *
 * @author fredsuvn
 * @see BeanInfo
 */
@Immutable
public interface BeanMethod extends BeanMember, BeanMethodBase {
}
