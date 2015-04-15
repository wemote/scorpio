package com.wemote.scorpio.modules.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * User: jayon.xu@gmail.com
 */
public class JSONs {

    /**
     * 转换对象为字节Json格式
     *
     * @param object
     * @return
     * @throws IOException
     */
    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    /**
     * 转换对象为字符Json格式
     *
     * @param object
     * @return
     * @throws IOException
     */
    public static String convertObjectToJsonString(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(object);
    }

    /**
     * 比较两个Json串是否相等
     *
     * @param jsonStr1
     * @param jsonStr2
     * @return
     */
    public static boolean compareJson(String jsonStr1, String jsonStr2) {
        if (jsonStr1 == null && jsonStr2 == null) {
            return true;
        }

        if (jsonStr1 != null && jsonStr1.equals(jsonStr2)) {
            return true;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode1 = mapper.readTree(jsonStr1);
            JsonNode jsonNode2 = mapper.readTree(jsonStr2);
            return jsonNode1.equals(jsonNode2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取Json串指定字段的值
     *
     * @param jsonStr
     * @param fieldName
     * @return
     */
    public static JsonNode getFieldValueFromJson(String jsonStr, String fieldName) {
        if (jsonStr == null || jsonStr.trim().length() == 0) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(jsonStr);
            return jsonNode.findValue(fieldName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 构建JsonNode
     *
     * @param jsonStr
     * @return
     */
    public static JsonNode buildJsonNode(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().length() == 0) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 构建JsonNode
     *
     * @param jsonBytes
     * @return
     */
    public static JsonNode buildJsonNode(byte[] jsonBytes) {
        if (jsonBytes == null || jsonBytes.length == 0) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(jsonBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取Json文件
     *
     * @param path
     * @param theRoot
     * @return
     */
    public static Object readJsonFile(String path, Class<?> theRoot) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input = loader.getResourceAsStream(path);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Object o = mapper.readValue(input, theRoot);
            return o;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取Json文件输出字节组
     *
     * @param path
     * @return
     */
    public static byte[] readJsonFileAsBytes(String path) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input = loader.getResourceAsStream(path);
        byte[] content = new byte[2048];
        try {
            input.read(content);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 读取Json文件输出字符串
     *
     * @param path
     * @return
     */
    public static String readJsonFileAsString(String path) {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        String content = "";
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                content += line;
            }

            reader.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

}
