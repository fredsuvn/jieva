package xyz.srclab.common.bytecode.provider.cglib;

interface NoOp extends Callback {

    NoOp INSTANCE = new NoOp() {
    };
}
