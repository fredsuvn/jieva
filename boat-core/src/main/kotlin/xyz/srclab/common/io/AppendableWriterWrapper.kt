package xyz.srclab.common.io

import org.apache.commons.io.output.AppendableWriter
import java.io.Writer


/**
 * Wraps [Appendable] as [Writer].
 */
open class AppendableWriterWrapper<T : Appendable>(appendable: T) : AppendableWriter<T>(appendable)