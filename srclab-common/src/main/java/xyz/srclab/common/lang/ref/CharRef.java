package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface CharRef {

    static CharRef empty() {
        return RefSupport.newCharRef();
    }

    static CharRef of(char value) {
        return RefSupport.newCharRef(value);
    }

    char get();

    void set(char value);
}
