package xyz.srclab.common.walk;

/**
 * @author sunqian
 */
public class ArrayWalker implements Walker {

    private final WalkerProvider walkerProvider;

    public ArrayWalker(WalkerProvider walkerProvider) {
        this.walkerProvider = walkerProvider;
    }

    @Override
    public void walk(Object walked, WalkVisitor visitor) {
        if (walked instanceof boolean[]) {
            walkPrimitive((boolean[]) walked, visitor);
        } else if (walked instanceof byte[]) {
            walkPrimitive((byte[]) walked, visitor);
        } else if (walked instanceof char[]) {
            walkPrimitive((char[]) walked, visitor);
        } else if (walked instanceof short[]) {
            walkPrimitive((short[]) walked, visitor);
        } else if (walked instanceof int[]) {
            walkPrimitive((int[]) walked, visitor);
        } else if (walked instanceof long[]) {
            walkPrimitive((long[]) walked, visitor);
        } else if (walked instanceof float[]) {
            walkPrimitive((float[]) walked, visitor);
        } else if (walked instanceof double[]) {
            walkPrimitive((double[]) walked, visitor);
        } else {
            walk((Object[]) walked, visitor);
        }
    }

    private void walk(Object[] walked, WalkVisitor visitor) {
        loop:
        for (int i = 0; i < walked.length; i++) {
            WalkVisitResult result = visitor.visit(i, walked[i], walkerProvider);
            switch (result) {
                case CONTINUE:
                    continue loop;
                case TERMINATE:
                    break loop;
            }
        }
    }

    private void walkPrimitive(boolean[] walked, WalkVisitor visitor) {
        loop:
        for (int i = 0; i < walked.length; i++) {
            WalkVisitResult result = visitor.visit(i, walked[i], walkerProvider);
            switch (result) {
                case CONTINUE:
                    continue loop;
                case TERMINATE:
                    break loop;
            }
        }
    }

    private void walkPrimitive(byte[] walked, WalkVisitor visitor) {
        loop:
        for (int i = 0; i < walked.length; i++) {
            WalkVisitResult result = visitor.visit(i, walked[i], walkerProvider);
            switch (result) {
                case CONTINUE:
                    continue loop;
                case TERMINATE:
                    break loop;
            }
        }
    }

    private void walkPrimitive(char[] walked, WalkVisitor visitor) {
        loop:
        for (int i = 0; i < walked.length; i++) {
            WalkVisitResult result = visitor.visit(i, walked[i], walkerProvider);
            switch (result) {
                case CONTINUE:
                    continue loop;
                case TERMINATE:
                    break loop;
            }
        }
    }

    private void walkPrimitive(short[] walked, WalkVisitor visitor) {
        loop:
        for (int i = 0; i < walked.length; i++) {
            WalkVisitResult result = visitor.visit(i, walked[i], walkerProvider);
            switch (result) {
                case CONTINUE:
                    continue loop;
                case TERMINATE:
                    break loop;
            }
        }
    }

    private void walkPrimitive(int[] walked, WalkVisitor visitor) {
        loop:
        for (int i = 0; i < walked.length; i++) {
            WalkVisitResult result = visitor.visit(i, walked[i], walkerProvider);
            switch (result) {
                case CONTINUE:
                    continue loop;
                case TERMINATE:
                    break loop;
            }
        }
    }

    private void walkPrimitive(long[] walked, WalkVisitor visitor) {
        loop:
        for (int i = 0; i < walked.length; i++) {
            WalkVisitResult result = visitor.visit(i, walked[i], walkerProvider);
            switch (result) {
                case CONTINUE:
                    continue loop;
                case TERMINATE:
                    break loop;
            }
        }
    }

    private void walkPrimitive(float[] walked, WalkVisitor visitor) {
        loop:
        for (int i = 0; i < walked.length; i++) {
            WalkVisitResult result = visitor.visit(i, walked[i], walkerProvider);
            switch (result) {
                case CONTINUE:
                    continue loop;
                case TERMINATE:
                    break loop;
            }
        }
    }

    private void walkPrimitive(double[] walked, WalkVisitor visitor) {
        loop:
        for (int i = 0; i < walked.length; i++) {
            WalkVisitResult result = visitor.visit(i, walked[i], walkerProvider);
            switch (result) {
                case CONTINUE:
                    continue loop;
                case TERMINATE:
                    break loop;
            }
        }
    }
}
