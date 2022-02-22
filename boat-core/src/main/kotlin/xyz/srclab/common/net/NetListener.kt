package xyz.srclab.common.net

/**
 * Listener for net socket.
 */
interface NetListener {

    /**
     * Starts listening. This method is blocking in the most time.
     */
    fun start()

    companion object {


        private class NioListener : NetListener {






            override fun start() {
                TODO("Not yet implemented")
            }
        }
    }
}