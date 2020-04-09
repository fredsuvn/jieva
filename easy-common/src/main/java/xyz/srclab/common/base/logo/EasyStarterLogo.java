package xyz.srclab.common.base.logo;

import xyz.srclab.common.time.TimeHelper;

class EasyStarterLogo implements Logo {

    /*
     *                                         _____              _____
     * ___________ ____________  __      _________  /______ _________  /_____________
     * _  _ \  __ `/_  ___/_  / / /________  ___/  __/  __ `/_  ___/  __/  _ \_  ___/
     * /  __/ /_/ /_(__  )_  /_/ /_/_____/(__  )/ /_ / /_/ /_  /   / /_ /  __/  /
     * \___/\__,_/ /____/ _\__, /        /____/ \__/ \__,_/ /_/    \__/ \___//_/
     *                    /____/
     *
     * Source: http://www.network-science.de/ascii/
     * Font: speed
     */

    @Override
    public void printLgo() {
        System.out.println("                                         _____              _____             ");
        System.out.println("___________ ____________  __      _________  /______ _________  /_____________");
        System.out.println("_  _ \\  __ `/_  ___/_  / / /________  ___/  __/  __ `/_  ___/  __/  _ \\_  ___/");
        System.out.println("/  __/ /_/ /_(__  )_  /_/ /_/_____/(__  )/ /_ / /_/ /_  /   / /_ /  __/  /    ");
        System.out.println("\\___/\\__,_/ /____/ _\\__, /        /____/ \\__/ \\__,_/ /_/    \\__/ \\___//_/     ");
        System.out.println("                   /____/                                                     ");
        System.out.println("                                                           srclab.xyz, sunqian");
        System.out.println("                                                              fredsuvn@163.com");
        System.out.println("                                                                              ");
        System.out.println("Source: http://www.network-science.de/ascii/                                  ");
        System.out.println("Font: speed                                                                   ");
    }

    static final String SECRET_CODE;

    static {
        long nowMillis = TimeHelper.nowMillis();
        SECRET_CODE = "" + Math.sqrt(nowMillis);
    }
}
