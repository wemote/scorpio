package com.wemote.scorpio.modules.common.io;

import com.google.common.base.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件sha1工具类
 *
 * @author Echelon
 */
public class FileSha1Utils {

    /**
     * 对文件进行sha1计算
     *
     * @param inStream 输入流
     * @return sha1结果
     */
    public static String getFileSha1(InputStream inStream) {
        byte[] buffer = new byte[1024 * 1024 * 2]; //2M缓存
        int len = 0;
        try {
            MessageDigest sha1Digest = MessageDigest.getInstance("SHA-1");
            while ((len = inStream.read(buffer)) > 0) {
                sha1Digest.update(buffer, 0, len);
            }
            return byte2Hex(sha1Digest.digest());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getChecksum(String payload) {
        if (Strings.isNullOrEmpty(payload)) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(payload.getBytes());
            return byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getChecksum(byte[] payload) {
        if (payload == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(payload);
            return byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String byte2Hex(byte[] digest) {
        StringBuilder strBlr = new StringBuilder();
        String tempStr = "";

        for (int i = 0; i < digest.length; i++) {
            tempStr = (Integer.toHexString(digest[i] & 0xff));
            if (tempStr.length() == 1) {
                strBlr.append("0");
            }
            strBlr.append(tempStr);
        }
        return strBlr.toString().toLowerCase();
    }
}
