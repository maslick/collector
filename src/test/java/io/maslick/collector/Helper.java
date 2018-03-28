package io.maslick.collector;


/**
 * Created by maslick on 4.1.2017.
 */

import java.util.Random;

public final class Helper {

    public static int randomInteger(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static String randomString(int length) {
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";

        Random r = new Random();
        String alphabet = letters + numbers;
        String out = "";
        for (int i = 0; i < length; i++) {
            out += alphabet.charAt(r.nextInt(alphabet.length()));
        }

        return out;
    }

    public static String randomString() {
        return randomString(5);
    }
}
