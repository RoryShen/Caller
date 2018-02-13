package com.ck_telecom.caller.utils;

import java.util.Random;

/**
 * Author/作者: Rory
 * Date/日期： 2018/02/13
 * Description/描述：
 */

public class BaseUtils {

    /**
     * Get a random str
     *
     * @param base   the base string
     * @param length string length.
     * @return the random staring
     */
    public static String getRandomString(String base, int length) {

        int randomNum;
        char randomChar;
        Random random = new Random();
        // Create a string buffer to operation string.
        StringBuffer str = new StringBuffer();

        for (int i = 0; i < length; i++) {

            //Get position between 0 to N.
            randomNum = random.nextInt(base.length());
            // get the char of the rand position.
            randomChar = base.charAt(randomNum);
            // get a random string
            str.append(randomChar);
        }
        return str.toString();
    }
}
