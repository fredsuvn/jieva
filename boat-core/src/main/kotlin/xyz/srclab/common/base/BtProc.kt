/**
 * Process utilities.
 */
package xyz.srclab.common.base

import java.io.File

/**
 * Runs a new process with [command].
 *
 * @param env environments for the process
 * @param dir directory for the process
 */
@JvmName("run")
@JvmOverloads
fun runProcess(command: String, env: Map<String, String>? = null, dir: String? = null): Process {
    return runProcess(command, env, dir?.let { File(it) })
}

/**
 * Runs a new process with [command].
 *
 * @param evn environments for the process
 * @param dir directory for the process
 */
@JvmName("run")
fun runProcess(command: String, evn: Map<String, String>?, dir: File?): Process {
    val envArray = evn?.entries?.map { "${it.key}=${it.value}" }?.toTypedArray()
    return Runtime.getRuntime().exec(command, envArray, dir)
}

/**
 * Runs a new process with command array([cmdArray]).
 *
 * @param evn environments for the process
 * @param dir directory for the process
 */
@JvmName("run")
@JvmOverloads
fun runProcess(cmdArray: Array<out String>, evn: Map<String, String>? = null, dir: String? = null): Process {
    return runProcess(cmdArray, evn, dir?.let { File(it) })
}

/**
 * Runs a new process with command array([cmdArray]).
 *
 * @param evn environments for the process
 * @param dir directory for the process
 */
@JvmName("run")
fun runProcess(cmdArray: Array<out String>, evn: Map<String, String>?, dir: File?): Process {
    val envArray = evn?.entries?.map { "${it.key}=${it.value}" }?.toTypedArray()
    return Runtime.getRuntime().exec(cmdArray, envArray, dir)
}