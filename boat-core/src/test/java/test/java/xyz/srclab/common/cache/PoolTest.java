package test.java.xyz.srclab.common.cache;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.cache.Pool;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PoolTest {

    @Test
    public void testPool() throws Exception {
        int coreSize = 22;
        int maxSize = 33;
        long keepAliveMillis = 1000L;
        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger verify = new AtomicInteger(0);
        Pool<String> pool = Pool.simplePool(coreSize, maxSize, keepAliveMillis, () -> String.valueOf(count.getAndIncrement()));
        doTestPool(coreSize, maxSize, keepAliveMillis, count, verify, pool);

        count.set(0);
        Pool<String> pool2 = Pool.simplePool(coreSize, maxSize, keepAliveMillis, () -> String.valueOf(count.getAndIncrement()));
        Pool<String> syncPool = Pool.synchronizedPool(pool2);
        doTestPool(coreSize, maxSize, keepAliveMillis, count, verify, syncPool);
    }

    private void doTestPool(
        int coreSize,
        int maxSize,
        long keepAliveMillis,
        AtomicInteger count,
        AtomicInteger verify,
        Pool<String> pool
    ) throws Exception {

        verify.set(0);
        List<Pool.Node<String>> nodes = new LinkedList<>();

        // check core nodes and ext nodes
        for (int i = 0; i < maxSize; i++) {
            Pool.Node<String> node = pool.get();
            //System.out.println(node);
            Assert.assertEquals(node.getValue(), String.valueOf(verify.getAndIncrement()));
            nodes.add(node);
        }
        for (Pool.Node<String> node : nodes) {
            node.release();
        }
        nodes.clear();
        verify.set(0);

        // check excess
        for (int i = 0; i < maxSize * 2; i++) {
            Pool.Node<String> node = pool.get();
            //System.out.println(node);
            Assert.assertEquals(node.getValue(), String.valueOf(verify.getAndIncrement()));
            nodes.add(node);
        }
        for (Pool.Node<String> node : nodes) {
            node.release();
        }
        nodes.clear();
        verify.set(0);

        // check after excess
        for (int i = 0; i < maxSize; i++) {
            Pool.Node<String> node = pool.get();
            //System.out.println(node);
            Assert.assertEquals(node.getValue(), String.valueOf(verify.getAndIncrement()));
            nodes.add(node);
        }
        for (Pool.Node<String> node : nodes) {
            node.release();
        }
        nodes.clear();
        verify.set(0);

        // check clean
        pool.cleanUp();
        for (int i = 0; i < maxSize; i++) {
            Pool.Node<String> node = pool.get();
            //System.out.println(node);
            Assert.assertEquals(node.getValue(), String.valueOf(verify.getAndIncrement()));
            nodes.add(node);
            if (verify.get() >= coreSize) {
                verify.set(count.get());
            }
        }
        for (Pool.Node<String> node : nodes) {
            node.release();
        }
        nodes.clear();
        verify.set(0);

        // check ext timeout
        Thread.sleep(keepAliveMillis * 2);
        for (int i = 0; i < maxSize; i++) {
            Pool.Node<String> node = pool.get();
            //System.out.println(node);
            if (verify.get() != coreSize) {
                // note that pool will return first unused node whatever it is timeout
                Assert.assertEquals(node.getValue(), String.valueOf(verify.getAndIncrement()));
            }
            nodes.add(node);
            if (verify.get() == coreSize + 1) {
                verify.set(count.get());
            }
        }
        for (Pool.Node<String> node : nodes) {
            node.release();
        }
        nodes.clear();
        verify.set(0);
    }
}
