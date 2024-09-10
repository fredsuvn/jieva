package test.proxy;

public class TestA extends TestS {

    private final Child[] children;

    public TestA() {
        this.children = new Child[3];
        for (int i = 0; i < children.length; i++) {
            children[i] = new Child(i );
        }
    }

    public void m1() {

    }

    public void m2() {

    }

    public int m3() {
        return 0;
    }

    public Object callSuper(int i) {
        switch (i) {
            case 0:
                super.m1();
                return null;
            case 1:
                super.m2();
                return null;
            case 2:
                return super.m3();
        }
        return null;
    }


    private class Child {
        private final int i;

        private Child(int i) {
            this.i = i;
        }

        public Object call() {
            return callSuper(i);
        }
    }
}
