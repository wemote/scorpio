package com.wemote.scorpio.modules.utils;

import com.google.common.collect.Range;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: jayon.xu@gmail.com
 */
public class EmojiUtils {

    private static Logger logger = LoggerFactory.getLogger(EmojiUtils.class);

    static Range<Character> utf84ByteRange = Range.closed("\u1F601".charAt(0), "\u1F64F".charAt(0));

    //把超过utf-8字节编码的字符去掉
    //参见: http://cenalulu.github.io/linux/character-encoding/
    //所谓Emoji就是一种在Unicode位于\u1F601-\u1F64F区段的字符
    public static String replaceUtf84byte(String str, char replaceChar) {

        if (StringUtils.isBlank(str)) {
            return StringUtils.EMPTY;
        }

        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isSurrogate(chars[i])) {
                logger.warn(chars[i] + "char is surrogate code unit, utf-8 is 4 bytes, replace it with " + replaceChar);
                chars[i] = replaceChar; //替换成空格
            } else if (utf84ByteRange.contains(chars[i])) {
                logger.warn(chars[i] + "char is in \\u1F601-\\u1F64F, utf-8 is 4 bytes, replace it with " + replaceChar);
                chars[i] = replaceChar; //替换成空格
            }
        }

        return new String(chars);
    }

    public static String replaceUtf84byteWithBlank(String str) {
        return replaceUtf84byte(str, ' ');
    }

}
