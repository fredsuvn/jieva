package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface CharRef {

    static CharRef empty() {
        return Ref0.newCharRef();
    }

    static CharRef of(char value) {
        return Ref0.newCharRef(value);
    }

    char get();

    void set(char value);
}
