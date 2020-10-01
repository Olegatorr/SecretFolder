package ua.coursework.secretfolder.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class md5Calculator {

    public String md5Apache(String st) {
        String md5Hex = DigestUtils.md5Hex(st);
        return md5Hex;
    }

}
