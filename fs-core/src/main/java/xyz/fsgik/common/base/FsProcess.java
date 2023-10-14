package xyz.fsgik.common.base;

import xyz.fsgik.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utilities for process.
 *
 * @author fredsuvn
 */
public class FsProcess {

    /**
     * Starts a process with given command.
     *
     * @param cmd given command
     * @return the process
     */
    public static Process start(String cmd) {
        String[] splits = cmd.split(" ");
        List<String> actualCmd = Arrays.stream(splits)
            .filter(FsString::isNotBlank)
            .map(String::trim)
            .collect(Collectors.toList());
        return start(false, actualCmd);
    }

    /**
     * Starts a process with given command.
     *
     * @param cmd given command
     * @return the process
     */
    public static Process start(String... cmd) {
        return start(false, cmd);
    }

    /**
     * Starts a process with given command and whether redirect error stream.
     *
     * @param redirectErrorStream whether redirect error stream
     * @param cmd                 given command
     * @return the process
     */
    public static Process start(boolean redirectErrorStream, String... cmd) {
        return start(redirectErrorStream, Arrays.asList(cmd));
    }

    /**
     * Starts a process with given command and whether redirect error stream.
     *
     * @param redirectErrorStream whether redirect error stream
     * @param cmd                 given command
     * @return the process
     */
    public static Process start(boolean redirectErrorStream, List<String> cmd) {
        return start(null, null, redirectErrorStream, cmd);
    }

    /**
     * Starts a process with given command, given environment, given directory file, and whether redirect error stream.
     *
     * @param env                 given environment
     * @param dir                 given directory file
     * @param redirectErrorStream whether redirect error stream
     * @param cmd                 given command
     * @return the process
     */
    public static Process start(
        @Nullable Map<String, String> env,
        @Nullable File dir,
        boolean redirectErrorStream,
        List<String> cmd
    ) {
        ProcessBuilder builder = buildProcess(env, dir, redirectErrorStream);
        builder.command(cmd);
        try {
            return builder.start();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Starts a process with given command, given environment, given directory file, and whether redirect error stream.
     *
     * @param env                 given environment
     * @param dir                 given directory file
     * @param redirectErrorStream whether redirect error stream
     * @param cmd                 given command
     * @return the process
     */
    public static Process start(
        @Nullable Map<String, String> env,
        @Nullable File dir,
        boolean redirectErrorStream,
        String... cmd
    ) {
        ProcessBuilder builder = buildProcess(env, dir, redirectErrorStream);
        builder.command(cmd);
        try {
            return builder.start();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static ProcessBuilder buildProcess(
        @Nullable Map<String, String> env, @Nullable File dir, boolean redirectErrorStream) {
        ProcessBuilder builder = new ProcessBuilder();
        if (env != null) {
            builder.environment().putAll(env);
        }
        if (dir != null) {
            builder.directory(dir);
        }
        if (redirectErrorStream) {
            builder.redirectErrorStream(true);
        }
        return builder;
    }
}
