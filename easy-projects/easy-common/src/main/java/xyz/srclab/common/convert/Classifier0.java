package xyz.srclab.common.convert;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author sunqian
 */
final class Classifier0 {

    static Classifier defaultClassifier() {
        return ClassifierImpl.getInstance();
    }

    private static final class ClassifierImpl implements Classifier {

        public static ClassifierImpl getInstance() {
            return INSTANCE;
        }

        private static final ClassifierImpl INSTANCE = new ClassifierImpl();

        @Override
        public Category classify(Class<?> type) {
            if (type.isArray()) {
                return Category.ARRAY;
            }
            if (List.class.isAssignableFrom(type)) {
                return Category.LIST;
            }
            if (Set.class.isAssignableFrom(type)) {
                return Category.SET;
            }
            if (Collection.class.isAssignableFrom(type)) {
                return Category.COLLECTION;
            }
            if (Map.class.isAssignableFrom(type)) {
                return Category.MAP;
            }
            if (Iterable.class.isAssignableFrom(type)) {
                return Category.ITERABLE;
            }
            return isUnit(type) ? Category.UNIT : Category.RECORD;
        }

        private boolean isUnit(Class<?> type) {

        }
    }
}
