package xyz.srclab.bytecode.provider.cglib;

public interface NoOp extends Callback {

    NoOp INSTANCE = new NoOp() {
    };
}
