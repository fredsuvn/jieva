package xyz.srclab.common.egg.impl.v0;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Current;
import xyz.srclab.common.egg.Egg;

import java.io.Console;
import java.io.PrintWriter;

final class V1Egg implements Egg {

    static final V1Egg INSTANCE = new V1Egg();

    /*
     * __________                            ____________              _____
     * ___  ____/_____ ____________  __      __  ___/_  /______ _________  /_____________
     * __  __/  _  __ `/_  ___/_  / / /___________ \_  __/  __ `/_  ___/  __/  _ \_  ___/
     * _  /___  / /_/ /_(__  )_  /_/ /_/_____/___/ // /_ / /_/ /_  /   / /_ /  __/  /
     * /_____/  \__,_/ /____/ _\__, /        /____/ \__/ \__,_/ /_/    \__/ \___//_/
     *                        /____/
     *                                                                srclab.xyz, sunqian
     *                                                                   fredsuvn@163.com
     *
     * Source: http://www.network-science.de/ascii/
     * Font: speed
     */

    private V1Egg() {
    }

    @Override
    public void hatchOut() {
        printLogo();
        startGame();
    }

    private void printLogo() {
        System.out.println("__________                            ____________              _____             ");
        System.out.println("___  ____/_____ ____________  __      __  ___/_  /______ _________  /_____________");
        System.out.println("__  __/  _  __ `/_  ___/_  / / /___________ \\_  __/  __ `/_  ___/  __/  _ \\_  ___/");
        System.out.println("_  /___  / /_/ /_(__  )_  /_/ /_/_____/___/ // /_ / /_/ /_  /   / /_ /  __/  /    ");
        System.out.println("/_____/  \\__,_/ /____/ _\\__, /        /____/ \\__/ \\__,_/ /_/    \\__/ \\___//_/     ");
        System.out.println("                       /____/                                                     ");
        System.out.println("                                                               srclab.xyz, sunqian");
        System.out.println("                                                                  fredsuvn@163.com");
        System.out.println("                                                                                  ");
        System.out.println("Source: http://www.network-science.de/ascii/                                      ");
        System.out.println("Font: speed                                                                       ");
    }

    private void startGame() {
        System.out.println();
        System.out.println("Welcome!");
    }

    static final String SPELL;

    static {
        long nowMillis = Current.milliseconds();
        SPELL = "" + Math.sqrt(nowMillis);
    }


    private static final class Screen {

        private final PrintWriter printWriter;

        private Screen() {
            this.printWriter = getPrintWriter();
        }

        private PrintWriter getPrintWriter() {
            @Nullable Console console = System.console();
            if (console == null) {
                return new PrintWriter(System.out);
            }
            return console.writer();
        }

        public void print(Object content) {
            printWriter.print(content);
        }

        public void println(Object content) {
            print(content);
            printWriter.println();
        }

        public String read() {

        }
    }
}
