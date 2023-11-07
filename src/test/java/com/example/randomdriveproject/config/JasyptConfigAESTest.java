//package com.example.randomdriveproject.config;
//
//import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class JasyptConfigAESTest {
//    @Test
//    void stringEncryptor() {
//        String url = "jdbc:mysql://random-drive.cvyfc8cxu04e.ap-northeast-2.rds.amazonaws.com:3306/randomdrive";
//        String username = "randomdrive";
//        String password = "123456789a";
//        String restApiKey = "793210a131c7da0427e26618e55cec5a";
//        String jsApiKey = "c87b1c2e1d4bcdc3401e35160e8e7d95";
//        String jwtSecretKey = "7Iqk7YyM66W07YOA7L2U65Sp7YG065+9U3ByaW5n6rCV7J2Y7Yqc7YSw7LWc7JuQ67mI7J6F64uI64ukLg==";
//
//
//        System.out.println(jasyptEncoding(url));
//        System.out.println(jasyptEncoding(username));
//        System.out.println(jasyptEncoding(password));
//        System.out.println(jasyptEncoding(restApiKey));
//        System.out.println(jasyptEncoding(jsApiKey));
//        System.out.println(jasyptEncoding(jwtSecretKey));
//    }
//
//    public String jasyptEncoding(String value) {
//
//        String key = "gdh-password-randomdrive";
//        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
//        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
//        pbeEnc.setPassword(key);
//        return pbeEnc.encrypt(value);
//    }
//
//}