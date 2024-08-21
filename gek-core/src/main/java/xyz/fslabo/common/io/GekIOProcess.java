package xyz.fslabo.common.io;

import xyz.fslabo.common.base.JieChars;
import xyz.fslabo.common.base.JieString;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * This is sub-interface of {@link IOChainConfigurator} for IO process in method chaining.
 * Subtype needs to play the role of {@link T}, which represents subtype itself.
 * For example:
 * <pre>
 *     class MyOps extends GekIOProcess&lt;MyOps&gt; {
 *     }
 * </pre>
 * Then user can use implementation in method chaining:
 * <pre>
 *     myOps.input(in).output(out).doFinal();
 * </pre>
 *
 * @param <T> subtype of this interface, and is subtype itself
 * @author fredsuvn
 */
public interface GekIOProcess<T extends GekIOProcess<T>> extends IOChainConfigurator<T> {

    /**
     * Starts and does final process, returns number of bytes written in output.
     *
     * @return number of bytes written in output
     */
    long doFinal();

    /**
     * Starts and does final process, returns int number of bytes written in output, or {@link Integer#MAX_VALUE}
     * if the number is greater than {@link Integer#MAX_VALUE}.
     *
     * @return int number of bytes written in output or {@link Integer#MAX_VALUE} if the number is greater than
     * {@link Integer#MAX_VALUE}.
     */
    default int doFinalInt() {
        long num = doFinal();
        return (int) (num > Integer.MAX_VALUE ? Integer.MAX_VALUE : num);
    }

    /**
     * Starts and does final process, writes result into an array and returns.
     *
     * @return result array
     */
    default byte[] finalBytes() {
        return JieIO.read(finalStream());
    }

    /**
     * Returns an input stream which contains all configurations of current process,
     * and will start and do final data process when read methods are called.
     *
     * @return an input stream which contains all configurations of current process
     */
    InputStream finalStream();

    /**
     * Starts and does final process, builds result as string with {@link JieChars#defaultCharset()} and returns.
     *
     * @return result as string with specified charset
     */
    default String finalString() {
        return finalString(JieChars.defaultCharset());
    }

    /**
     * Starts and does final process, builds result as string with specified charset and returns.
     *
     * @param charset specified charset
     * @return result as string with specified charset
     */
    default String finalString(Charset charset) {
        return JieString.of(finalBytes(), charset);
    }
}
