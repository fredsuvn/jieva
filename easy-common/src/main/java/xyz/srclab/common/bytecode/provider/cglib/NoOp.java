package xyz.srclab.common.bytecode.provider.cglib;

public interface NoOp extends Callback {

    NoOp INSTANCE = new NoOp() {
    };
}
