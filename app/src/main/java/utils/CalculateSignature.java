package utils;

import android.util.Log;

import java.util.Random;

/**
 * Created by Li Wenzhao on 2017/11/12.
 */

public class CalculateSignature {
    private static Random r = new Random();

    public static String getSignature() {
        long t = System.currentTimeMillis();
        String timestamp = String.valueOf(t).substring(0,10);
        //生成四位随机数
        int random = r.nextInt(9999 - 1000 + 1) + 1000;
        String data = "random" + random + "timestamp" + timestamp;
        try {
            String signature = toHexString(Des.encrypt(data, String.valueOf(URL_UNIVERSAL.SYSID))).toUpperCase();
            return random + "@" + timestamp + "@" + signature;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    private static String toHexString(byte b[]) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String plainText = Integer.toHexString(0xff & b[i]);
            if (plainText.length() < 2)
                plainText = "0" + plainText;
            hexString.append(plainText);
        }

        return hexString.toString();
    }
}
