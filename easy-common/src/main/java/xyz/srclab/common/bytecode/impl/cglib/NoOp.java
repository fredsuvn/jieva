package xyz.srclab.common.bytecode.impl.cglib;

public interface NoOp extends Callback {

    NoOp INSTANCE = new NoOp() {
    };
}
