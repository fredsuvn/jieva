package xyz.srclab.common.egg;

import xyz.srclab.common.time.TimeHelper;

class EasyStarterEgg implements Egg {

    /*
     * __________                            ____________              _____
     * ___  ____/_____ ____________  __      __  ___/_  /______ _________  /_____________
     * __  __/  _  __ `/_  ___/_  / / /___________ \_  __/  __ `/_  ___/  __/  _ \_  ___/
     * _  /___  / /_/ /_(__  )_  /_/ /_/_____/___/ // /_ / /_/ /_  /   / /_ /  __/  /
     * /_____/  \__,_/ /____/ _\__, /        /____/ \__/ \__,_/ /_/    \__/ \___//_/
     *                        /____/
     *
     * Source: http://www.network-science.de/ascii/
     * Font: speed
     */

    @Override
    public void hatchOut(String spell) {
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
        long nowMillis = TimeHelper.nowMillis();
        SPELL = "" + Math.sqrt(nowMillis);
    }
}
