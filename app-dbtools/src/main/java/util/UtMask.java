package util;

import kz.app.appcore.utils.*;

public class UtMask {

    public static String mask(String token) {
        return token.substring(0, 3) + UtString.repeat("*", token.length() - 3);
    }

}
