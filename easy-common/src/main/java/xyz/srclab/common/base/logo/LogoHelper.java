package xyz.srclab.common.base.logo;

public class LogoHelper {

    public static Logo findLogo(String secretCode) {
        if (!EasyStarterLogo.SECRET_CODE.equals(secretCode)) {
            throw new IllegalArgumentException("Wrong secret code: " + secretCode);
        }
        return new EasyStarterLogo();
    }
}
