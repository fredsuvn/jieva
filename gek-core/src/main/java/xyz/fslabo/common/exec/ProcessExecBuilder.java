package xyz.fslabo.common.exec;

import lombok.AllArgsConstructor;
import lombok.Data;
import xyz.fslabo.common.base.BaseBuilder;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.io.JieIO;

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
 * {@link Process}-based Builder of {@link ExecUnit}, to build instance of {@link ExecUnit} of thread.
 *
 * @author fredsuvn
 */
public abstract class ProcessExecBuilder implements BaseBuilder<ProcessExecUnit, ProcessExecBuilder> {

    static ProcessExecBuilder newInstance() {
        return new OfJdk8();
    }

    private List<String> command;
    private Object input;
    private Object output;
    private Object errorOutput;
    private File directory;
    private Consumer<Map<String, String>> envConfigurator;

    ProcessExecBuilder() {
        reset();
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
    public ProcessExecBuilder command(String command) {
        return command(Arrays.asList(command.split(" +")));
    }

    /**
     * Sets command.
     *
     * @param command command
     * @return this
     */
    public ProcessExecBuilder command(String... command) {
        return command(Arrays.asList(command));
    }

    /**
     * Sets command.
     *
     * @param command command
     * @return this
     */
    public ProcessExecBuilder command(List<String> command) {
        this.command = command;
        return this;
    }

    /**
     * Sets input to given array.
     *
     * @param array given array
     * @return this
     */
    public ProcessExecBuilder input(byte[] array) {
        this.input = array;
        return this;
    }

    /**
     * Sets input to given buffer.
     *
     * @param buffer given buffer
     * @return this
     */
    public ProcessExecBuilder input(ByteBuffer buffer) {
        this.input = buffer;
        return this;
    }

    /**
     * Sets input to given input stream.
     *
     * @param in given input stream
     * @return this
     */
    public ProcessExecBuilder input(InputStream in) {
        this.input = in;
        return this;
    }

    /**
     * Sets input to given file.
     *
     * @param file given file
     * @return this
     */
    public ProcessExecBuilder input(File file) {
        this.input = file;
        return this;
    }

    /**
     * Sets input to given file.
     *
     * @param file given file
     * @return this
     */
    public ProcessExecBuilder input(Path file) {
        this.input = file;
        return this;
    }

    /**
     * Sets input to given redirect.
     *
     * @param redirect given redirect
     * @return this
     */
    public ProcessExecBuilder input(ProcessBuilder.Redirect redirect) {
        this.input = redirect;
        return this;
    }

    /**
     * Sets output to given array.
     *
     * @param array given array
     * @return this
     */
    public ProcessExecBuilder output(byte[] array) {
        this.output = array;
        return this;
    }

    /**
     * Sets output to given buffer.
     *
     * @param buffer given buffer
     * @return this
     */
    public ProcessExecBuilder output(ByteBuffer buffer) {
        this.output = buffer;
        return this;
    }

    /**
     * Sets output to given out put stream.
     *
     * @param out given out put stream
     * @return this
     */
    public ProcessExecBuilder output(OutputStream out) {
        this.output = out;
        return this;
    }

    /**
     * Sets output to given file.
     *
     * @param file given file
     * @return this
     */
    public ProcessExecBuilder output(File file) {
        this.output = file;
        return this;
    }

    /**
     * Sets output to given file.
     *
     * @param file given file
     * @return this
     */
    public ProcessExecBuilder output(Path file) {
        this.output = file;
        return this;
    }

    /**
     * Sets output to given redirect.
     *
     * @param redirect given redirect
     * @return this
     */
    public ProcessExecBuilder output(ProcessBuilder.Redirect redirect) {
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
    public ProcessExecBuilder errorOutput(byte[] array) {
        this.errorOutput = array;
        return this;
    }

    /**
     * Sets error output to given buffer.
     * The error output can be same with output, in this case both output will be written into same destination.
     *
     * @param buffer given buffer
     * @return this
     */
    public ProcessExecBuilder errorOutput(ByteBuffer buffer) {
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
    public ProcessExecBuilder errorOutput(OutputStream out) {
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
    public ProcessExecBuilder errorOutput(File file) {
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
    public ProcessExecBuilder errorOutput(Path file) {
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
    public ProcessExecBuilder errorOutput(ProcessBuilder.Redirect redirect) {
        this.errorOutput = redirect;
        return this;
    }

    /**
     * Sets directory where the process works.
     *
     * @param directory directory where the process works
     * @return this
     */
    public ProcessExecBuilder directory(String directory) {
        return directory(new File(directory));
    }

    /**
     * Sets directory where the process works.
     *
     * @param directory directory where the process works
     * @return this
     */
    public ProcessExecBuilder directory(Path directory) {
        return directory(directory.toFile());
    }

    /**
     * Sets directory where the process works.
     *
     * @param directory directory where the process works
     * @return this
     */
    public ProcessExecBuilder directory(File directory) {
        this.directory = directory;
        return this;
    }

    /**
     * Sets environment by specified environment configurator - {@code envConfigurator}.
     * <p>
     * The {@code envConfigurator} will be passed into initial environment of current system as a {@link Map}, then the
     * {@code envConfigurator} can modify the {@link Map} before the process execution unit starts.
     *
     * @param envConfigurator specified environment configurator
     * @return this
     */
    public ProcessExecBuilder environment(Consumer<Map<String, String>> envConfigurator) {
        this.envConfigurator = envConfigurator;
        return this;
    }

    @Override
    public ProcessExecBuilder reset() {
        this.input = null;
        this.output = null;
        this.errorOutput = null;
        this.command = null;
        this.directory = null;
        this.envConfigurator = null;
        return this;
    }

    @Override
    public ProcessExecUnit build() {
        return new ProcessExecUnit(() -> {
            Args args = new Args(command, input, output, errorOutput, directory, envConfigurator);
            return start(args);
        });
    }

    private Process start(Args args) throws ExecException {
        if (JieColl.isEmpty(command)) {
            throw new ExecException("Command is empty.");
        }
        try {
            return startWithProcessBuilder(args);
        } catch (Exception e) {
            throw new ExecException(e);
        }
    }

    private Process startWithProcessBuilder(Args args) throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(args.getCommand());
        if (args.getDirectory() != null) {
            builder.directory(args.getDirectory());
        }
        InputStream in = null;
        if (args.getInput() != null) {
            if (args.getInput() instanceof ProcessBuilder.Redirect) {
                builder.redirectInput((ProcessBuilder.Redirect) args.getInput());
            } else if (args.getInput() instanceof File) {
                builder.redirectInput((File) args.getInput());
            } else if (args.getInput() instanceof Path) {
                builder.redirectInput(((Path) args.getInput()).toFile());
            } else {
                in = inputToStream(args.getInput());
                builder.redirectInput(ProcessBuilder.Redirect.PIPE);
            }
        }
        OutputStream out = null;
        if (args.getOutput() != null) {
            if (args.getOutput() instanceof ProcessBuilder.Redirect) {
                builder.redirectOutput((ProcessBuilder.Redirect) args.getOutput());
            } else if (args.getOutput() instanceof File) {
                builder.redirectOutput((File) args.getOutput());
            } else if (args.getOutput() instanceof Path) {
                builder.redirectOutput(((Path) args.getOutput()).toFile());
            } else {
                out = outputToStream(args.getOutput());
                builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
            }
        }
        OutputStream err = null;
        if (args.getErrorOutput() != null) {
            if (Objects.equals(args.getErrorOutput(), args.getOutput())) {
                builder.redirectErrorStream(true);
            }
            if (args.getErrorOutput() instanceof ProcessBuilder.Redirect) {
                builder.redirectError((ProcessBuilder.Redirect) args.getErrorOutput());
            } else if (args.getErrorOutput() instanceof File) {
                builder.redirectError((File) args.getErrorOutput());
            } else if (args.getErrorOutput() instanceof Path) {
                builder.redirectError(((Path) args.getErrorOutput()).toFile());
            } else {
                err = outputToStream(args.getErrorOutput());
                builder.redirectError(ProcessBuilder.Redirect.PIPE);
            }
        }
        if (args.getEnvConfigurator() != null) {
            args.getEnvConfigurator().accept(builder.environment());
        }
        Process process = builder.start();
        if (in != null) {
            JieIO.readTo(in, process.getOutputStream());
        }
        if (out != null) {
            JieIO.readTo(process.getInputStream(), out);
        }
        if (err != null) {
            JieIO.readTo(process.getErrorStream(), out);
        }
        return process;
    }

    private InputStream inputToStream(Object in) {
        if (in instanceof byte[]) {
            return JieIO.toInputStream((byte[]) in);
        }
        if (in instanceof ByteBuffer) {
            return JieIO.toInputStream((ByteBuffer) in);
        }
        if (in instanceof InputStream) {
            return (InputStream) in;
        }
        throw new IllegalArgumentException("Error input type: " + in.getClass());
    }

    private OutputStream outputToStream(Object out) {
        if (out instanceof byte[]) {
            return JieIO.toOutputStream((byte[]) out);
        }
        if (out instanceof ByteBuffer) {
            return JieIO.toOutputStream((ByteBuffer) out);
        }
        if (out instanceof OutputStream) {
            return (OutputStream) out;
        }
        throw new IllegalArgumentException("Error output type: " + out.getClass());
    }

    @Data
    @AllArgsConstructor
    private static final class Args {
        private final List<String> command;
        private final Object input;
        private final Object output;
        private final Object errorOutput;
        private final File directory;
        private final Consumer<Map<String, String>> envConfigurator;
    }

    private static final class OfJdk8 extends ProcessExecBuilder {
    }
}
