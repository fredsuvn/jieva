package xyz.srclab.bytecode.provider.cglib;

interface NoOp extends Callback {

    NoOp INSTANCE = new NoOp() {
    };
}
