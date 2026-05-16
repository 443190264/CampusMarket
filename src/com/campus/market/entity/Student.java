package com.campus.market.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生实体 - 对应数据库 student 表
 */
public class Student {
    // ========== 数据库字段 ==========
    private Integer id;               // 主键，自增
    private String studentId;        // 学号，唯一
    private String name;
    private String phone;
    private BigDecimal balance;
    private String password;         // 密码（存储哈希值）
    private String salt;
    private LocalDateTime createTime;
    private boolean isAdmin;         // 是否管理员（true=管理员，false=普通学生）

    public Student() {
    }

    public Student(String studentId, String name, String phone, BigDecimal balance, String password, String salt) {
        this.studentId = studentId;
        this.name = name;
        this.phone = phone;
        this.balance = balance;
        this.password = password;
        this.salt = salt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}