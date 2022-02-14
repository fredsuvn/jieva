package xyz.srclab.common.net.udp

import java.nio.channels.NetworkChannel
import java.nio.channels.SelectableChannel

class S<T> where T : SelectableChannel, T : NetworkChannel