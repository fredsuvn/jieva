package xyz.fsgek.common.base;

import xyz.fsgek.common.collect.GekColl;
import xyz.fsgek.common.io.GekIO;
import xyz.fsgek.common.io.GekIOConfigurer;
import xyz.fsgek.common.io.GekIOException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class is used to configure and start a {@link Process} in method chaining:
 * <pre>
 *     process.input(in).output(out).start();
 * </pre>
 *
 * @author fredsuvn
 */
public abstract class GekProcess implements GekIOConfigurer<GekProcess> {

    static GekProcess newInstance() {
        return new OfJdk8();
    }

    private List<String> command;
    private Object input;
    private Object output;
    private Object errorOutput;
    private File directory;
    private ProcessBuilder builder;

    GekProcess() {
    }

    /**
     * Sets command.
     * This method will split given command with space chars:
     * <pre>
     *     command.split(" +")
     * </pre>
     *
     * @param command command
     * @return this
     */
    public GekProcess command(String command) {
        return command(Arrays.asList(command.split(" +")));
    }

    /**
     * Sets command.
     *
     * @param command command
     * @return this
     */
    public GekProcess command(String... command) {
        return command(Arrays.asList(command));
    }

    /**
     * Sets command.
     *
     * @param command command
     * @return this
     */
    public GekProcess command(List<String> command) {
        this.command = command;
        return this;
    }

    @Override
    public GekProcess input(byte[] array) {
        this.input = array;
        return this;
    }

    @Override
    public GekProcess input(ByteBuffer buffer) {
        this.input = buffer;
        return this;
    }

    @Override
    public GekProcess input(InputStream in) {
        this.input = in;
        return this;
    }

    /**
     * Sets input to given file.
     *
     * @param file given file
     * @return this
     */
    public GekProcess input(File file) {
        this.input = file;
        return this;
    }

    /**
     * Sets input to given file.
     *
     * @param file given file
     * @return this
     */
    public GekProcess input(Path file) {
        this.input = file;
        return this;
    }

    /**
     * Sets input to given redirect.
     *
     * @param redirect given redirect
     * @return this
     */
    public GekProcess input(ProcessBuilder.Redirect redirect) {
        this.input = redirect;
        return this;
    }

    @Override
    public GekProcess output(byte[] array) {
        this.output = array;
        return this;
    }

    @Override
    public GekProcess output(ByteBuffer buffer) {
        this.output = buffer;
        return this;
    }

    @Override
    public GekProcess output(OutputStream out) {
        this.output = out;
        return this;
    }

    /**
     * Sets output to given file.
     *
     * @param file given file
     * @return this
     */
    public GekProcess output(File file) {
        this.output = file;
        return this;
    }

    /**
     * Sets output to given file.
     *
     * @param file given file
     * @return this
     */
    public GekProcess output(Path file) {
        this.output = file;
        return this;
    }

    /**
     * Sets output to given redirect.
     *
     * @param redirect given redirect
     * @return this
     */
    public GekProcess output(ProcessBuilder.Redirect redirect) {
        this.output = redirect;
        return this;
    }

    /**
     * Sets error output to given array.
     * The error output can be same with output, in this case both output will be written into same destination.
     *
     * @param array given array
     * @return this
     */
    public GekProcess errorOutput(byte[] array) {
        this.errorOutput = array;
        return this;
    }

    /**
     * Sets error output to given array, starting from given offset.
     * The error output can be same with output, in this case both output will be written into same destination.
     *
     * @param array  given array
     * @param offset given offset
     * @return this
     */
    public GekProcess errorOutput(byte[] array, int offset) {
        return errorOutput(ByteBuffer.wrap(array, offset, array.length - offset));
    }

    /**
     * Sets error output to given buffer.
     * The error output can be same with output, in this case both output will be written into same destination.
     *
     * @param buffer given buffer
     * @return this
     */
    public GekProcess errorOutput(ByteBuffer buffer) {
        this.errorOutput = buffer;
        return this;
    }

    /**
     * Sets error output to given output stream.
     * The error output can be same with output, in this case both output will be written into same destination.
     *
     * @param out given output stream
     * @return this
     */
    public GekProcess errorOutput(OutputStream out) {
        this.errorOutput = out;
        return this;
    }

    /**
     * Sets error output to given file.
     * The error output can be same with output, in this case both output will be written into same destination.
     *
     * @param file given file
     * @return this
     */
    public GekProcess errorOutput(File file) {
        this.errorOutput = file;
        return this;
    }

    /**
     * Sets error output to given file.
     * The error output can be same with output, in this case both output will be written into same destination.
     *
     * @param file given file
     * @return this
     */
    public GekProcess errorOutput(Path file) {
        this.errorOutput = file;
        return this;
    }

    /**
     * Sets error output to given redirect.
     * The error output can be same with output, in this case both output will be written into same destination.
     *
     * @param redirect given redirect
     * @return this
     */
    public GekProcess errorOutput(ProcessBuilder.Redirect redirect) {
        this.errorOutput = redirect;
        return this;
    }

    /**
     * Sets directory where the process works.
     *
     * @param directory directory where the process works
     * @return this
     */
    public GekProcess directory(String directory) {
        return directory(new File(directory));
    }

    /**
     * Sets directory where the process works.
     *
     * @param directory directory where the process works
     * @return this
     */
    public GekProcess directory(Path directory) {
        return directory(directory.toFile());
    }

    /**
     * Sets directory where the process works.
     *
     * @param directory directory where the process works
     * @return this
     */
    public GekProcess directory(File directory) {
        this.directory = directory;
        return this;
    }

    /**
     * Sets environment where the process works.
     * This method provides default environment map into parameter of {@link Consumer}, the map is independent for each
     * process and can be modified.
     *
     * @param envConsumer environment where the process works
     * @return this
     */
    public GekProcess environment(Consumer<Map<String, String>> envConsumer) {
        envConsumer.accept(processBuilder().environment());
        return this;
    }

    @Override
    public GekProcess clear() {
        this.input = null;
        this.output = null;
        this.errorOutput = null;
        this.command = null;
        this.directory = null;
        this.builder = null;
        return this;
    }

    /**
     * Starts and returns process which is configured by this.
     * If the process's input, output or error output is redirected to an array, buffer or stream, current thread will
     * be blocked to read/write stream from the process until the process ends.
     * However, if redirected to files, this method will return immediately after starting process.
     *
     * @return the process which is started
     * @throws GekIOException IO exception
     */
    public Process start() throws GekIOException {
        if (GekColl.isEmpty(command)) {
            throw new IllegalArgumentException("No command.");
        }
        try {
            return startWithProcessBuilder();
        } catch (Exception e) {
            throw new GekIOException(e);
        }
    }

    private Process startWithProcessBuilder() throws IOException {
        ProcessBuilder builder = processBuilder();
        builder.command(command);
        if (directory != null) {
            builder.directory(directory);
        }
        InputStream in = null;
        if (input != null) {
            if (input instanceof ProcessBuilder.Redirect) {
                builder.redirectInput((ProcessBuilder.Redirect) input);
            } else if (input instanceof File) {
                builder.redirectInput((File) input);
            } else if (input instanceof Path) {
                builder.redirectInput(((Path) input).toFile());
            } else {
                in = inputToStream();
                builder.redirectInput(ProcessBuilder.Redirect.PIPE);
            }
        }
        OutputStream out = null;
        if (output != null) {
            if (output instanceof ProcessBuilder.Redirect) {
                builder.redirectOutput((ProcessBuilder.Redirect) output);
            } else if (output instanceof File) {
                builder.redirectOutput((File) output);
            } else if (output instanceof Path) {
                builder.redirectOutput(((Path) output).toFile());
            } else {
                out = outputToStream(output);
                builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
            }
        }
        OutputStream err = null;
        if (errorOutput != null) {
            if (Objects.equals(errorOutput, output)) {
                builder.redirectErrorStream(true);
            }
            if (errorOutput instanceof ProcessBuilder.Redirect) {
                builder.redirectError((ProcessBuilder.Redirect) errorOutput);
            } else if (errorOutput instanceof File) {
                builder.redirectError((File) errorOutput);
            } else if (errorOutput instanceof Path) {
                builder.redirectError(((Path) errorOutput).toFile());
            } else {
                err = outputToStream(errorOutput);
                builder.redirectError(ProcessBuilder.Redirect.PIPE);
            }
        }
        Process process = builder.start();
        if (in != null) {
            GekIO.readTo(in, process.getOutputStream());
        }
        if (out != null) {
            GekIO.readTo(process.getInputStream(), out);
        }
        if (err != null) {
            GekIO.readTo(process.getErrorStream(), out);
        }
        return process;
    }

    private InputStream inputToStream() {
        if (input instanceof byte[]) {
            return GekIO.toInputStream((byte[]) input);
        }
        if (input instanceof ByteBuffer) {
            return GekIO.toInputStream((ByteBuffer) input);
        }
        if (input instanceof InputStream) {
            return (InputStream) input;
        }
        throw new IllegalArgumentException("Error input type: " + input.getClass());
    }

    private OutputStream outputToStream(Object out) {
        if (out instanceof byte[]) {
            return GekIO.toOutputStream((byte[]) out);
        }
        if (out instanceof ByteBuffer) {
            return GekIO.toOutputStream((ByteBuffer) out);
        }
        if (out instanceof OutputStream) {
            return (OutputStream) out;
        }
        throw new IllegalArgumentException("Error output type: " + out.getClass());
    }

    private ProcessBuilder processBuilder() {
        if (builder == null) {
            builder = new ProcessBuilder();
        }
        return builder;
    }

    private static final class OfJdk8 extends GekProcess {
    }
}
