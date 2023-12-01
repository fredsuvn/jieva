package xyz.fsgek.common.base;

import xyz.fsgek.common.collect.GekCollector;
import xyz.fsgek.common.io.GekIOConfigurer;

/**
 * This is base interface for method chaining of {@code build}-{@code process}:
 * <pre>
 *     configurer.setThis(a).setThat(b).process();
 * </pre>
 * It represents a special type of builder to build then process.
 * And generally, it is reusable, re-set and re-process are permitted.
 * <p>
 * Subtype needs to play the role of {@link T}, which represents subtype itself. For example, here is a subtype:
 * <pre>
 *     interface MyProcess extends GekConfigurer&lt;GekConfigurer&gt; {
 *
 *         void process();
 *     }
 * </pre>
 * Then this subtype can start its process in method chaining:
 * <pre>
 *     myProcess.setThis(a).setThat(b).process();
 * </pre>
 * Here are some implementations: {@link GekThread}, {@link GekThreadPool}, {@link GekProcess}, {@link GekCollector},
 * {@link GekIOConfigurer}.
 *
 * @param <T> subtype of this interface, and is subtype itself
 * @author fredsuvn
 */
public interface GekConfigurer<T extends GekConfigurer<T>> {

    /**
     * Resets current configurations.
     *
     * @return this
     */
    T reset();
}
