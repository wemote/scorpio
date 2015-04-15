package com.wemote.scorpio.modules.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Strings {

    public static final Object LOCK = new Object();
    private static final String TABLE_FIELD_FUZZY_FLAG = "%";

    private static char[] letters;
    private static char[] numbers;
    private static long nextLong;
    private static int longLength = numberLength(Long.MAX_VALUE);
    private static int nextInt;
    private static int intLength = numberLength(Integer.MAX_VALUE);

    private static int numberLength(long number) {
        int i = 0;
        for (; number > 0; number = number / 10) {
            i++;
        }
        return i;
    }

    static {
        letters = new char[62];
        int j = 0;
        for (int i = 0, k = 'A'; i < 26; i++) {
            letters[j++] = (char) (k + i);
        }
        for (int i = 0, k = 'a'; i < 26; i++) {
            letters[j++] = (char) (k + i);
        }
        for (int i = 0, k = '0'; i < 10; i++) {
            letters[j++] = (char) (k + i);
        }

        numbers = new char[10];
        int p = 0;
        for (int i = 0, k = '0'; i < 10; i++) {
            numbers[p++] = (char) (k + i);
        }
    }

    public static String randomString() {
        return randomString(8);
    }

    public static String randomString(int length) {
        char[] strChar = new char[length];
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            strChar[i] = letters[random.nextInt(letters.length)];
        }
        String randomString = new String(strChar);
        return randomString;
    }

    public static String randomNumber(int length) {
        char[] strChar = new char[length];
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            strChar[i] = numbers[random.nextInt(numbers.length)];
        }
        String randomNumber = new String(strChar);
        return randomNumber;
    }

    /**
     * 返回下一个数字，并且跟最大数字的字符串长度相等
     *
     * @return
     */
    public static String nextInt() {
        synchronized (LOCK) {
            return fillZero(nextInt++, intLength);
        }
    }

    /**
     * 返回下一个数字，并且跟最大数字的字符串长度相等
     *
     * @return
     */
    public static String nextLong() {
        synchronized (LOCK) {
            return fillZero(nextLong++, longLength);
        }
    }

    /**
     * 填充0
     *
     * @param number
     * @param length
     * @return
     */
    public static String fillZero(long number, int length) {
        StringBuilder builder = new StringBuilder();
        int least = length - numberLength(number);
        char[] zeros = new char[least];
        for (int i = 0; i < least; i++) {
            zeros[i] = '0';
        }
        builder.append(new String(zeros));
        builder.append(number);
        return builder.toString();
    }

    public static String transHumpWithDelimiter(String hump, String delimiter) {
        if (hump == null || delimiter == null || delimiter.length() != 1) {
            return null;
        }
        if (hump.length() == 0) {
            return hump;
        }
        String regexStr = "[A-Z]";
        Matcher matcher = Pattern.compile(regexStr).matcher(hump);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String g = matcher.group();
            matcher.appendReplacement(sb, delimiter + g.toLowerCase());
        }
        matcher.appendTail(sb);
        if (sb.charAt(0) == delimiter.toCharArray()[0]) {
            sb.delete(0, 1);
        }
        return sb.toString();

    }

    /**
     * add check null.
     *
     * @param str
     * @return
     */
    public static String trim(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    /**
     * add check null and empty.
     *
     * @param str
     * @return
     */
    public static String toLowerCase(String str) {
        if (com.google.common.base.Strings.isNullOrEmpty(str)) {
            return str;
        }
        return str.toLowerCase();
    }

    /**
     * add check null and empty.
     *
     * @param str
     * @return
     */
    public static String toFuzzyCase(String str) {
        if (com.google.common.base.Strings.isNullOrEmpty(str)) {
            return TABLE_FIELD_FUZZY_FLAG;
        }
        return TABLE_FIELD_FUZZY_FLAG + str + TABLE_FIELD_FUZZY_FLAG;
    }

    /**
     * add check null and empty.
     *
     * @param str
     * @return
     */
    public static String toLeftFuzzyCase(String str) {
        if (com.google.common.base.Strings.isNullOrEmpty(str)) {
            return TABLE_FIELD_FUZZY_FLAG;
        }
        return TABLE_FIELD_FUZZY_FLAG + str;
    }

    /**
     * add check null and empty.
     *
     * @param str
     * @return
     */
    public static String toRightFuzzyCase(String str) {
        if (com.google.common.base.Strings.isNullOrEmpty(str)) {
            return TABLE_FIELD_FUZZY_FLAG;
        }
        return str + TABLE_FIELD_FUZZY_FLAG;
    }


}
