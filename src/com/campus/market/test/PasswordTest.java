package com.campus.market.test;

import com.campus.market.util.PasswordUtil;

public class PasswordTest {
    public static void main(String[] args) {
        String plain = "admin123";
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(plain, salt);

        boolean ok = PasswordUtil.verifyPassword(plain, salt, hash);
        System.out.println(ok ? "加密验证通过" : "加密验证失败");

        boolean fail = PasswordUtil.verifyPassword("wrong", salt, hash);
        System.out.println(!fail ? "错误密码正确拒绝" : "错误密码验证异常");
    }
}