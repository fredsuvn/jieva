package xyz.srclab.common.egg

import xyz.srclab.common.lang.Defaults

class EggNotFoundException(name: CharSequence) : RuntimeException(
    "Egg not found: $name${Defaults.lineSeparator}$MESSAGE"
) {
    companion object {
        private val MESSAGE = """
            It need a true name and a true spell~
            If you don't know the true name,
            try "Hello, Boat Egg!",
            it accepts any spell~.
            Or, see the shell of egg, maybe some clues there.
        """.trimIndent()
    }
}