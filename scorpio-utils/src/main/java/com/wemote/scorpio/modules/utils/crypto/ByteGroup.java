package com.wemote.scorpio.modules.utils.crypto;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author jayon.xu@gmail.com
 */
public class ByteGroup {

    List<Byte> byteContainer = Lists.newArrayList();

    public byte[] toBytes() {
        byte[] bytes = new byte[byteContainer.size()];
        for (int i = 0; i < byteContainer.size(); i++) {
            bytes[i] = byteContainer.get(i);
        }
        return bytes;
    }

    public ByteGroup addBytes(byte[] bytes) {
        for (byte b : bytes) {
            byteContainer.add(b);
        }
        return this;
    }

    public int size() {
        return byteContainer.size();
    }
}
