package xyz.srclab.common.object;

import java.lang.reflect.Type;
import java.util.function.Predicate;

/**
 * @author sunqian
 */
public interface UnitPredicate extends Predicate<Type> {

    static UnitPredicate defaultPredicate() {
        return UnitPredicateSupport.defaultPredicate();
    }

    static UnitPredicateBuilder newUBuilder() {
        return UnitPredicateBuilder.newBuilder();
    }
}
