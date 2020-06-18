package xyz.srclab.common.convert;

/**
 * @author sunqian
 */
public interface Classifier {

    static Classifier getDefault() {
        return Classifier0.defaultClassifier();
    }

    Category classify(Class<?> type);
}
