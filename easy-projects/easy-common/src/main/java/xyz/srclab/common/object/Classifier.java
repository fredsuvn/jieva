package xyz.srclab.common.object;

/**
 * @author sunqian
 */
public interface Classifier {

    static Classifier getDefault() {
        return Classifier0.defaultClassifier();
    }

    Category classify(Class<?> type);
}
