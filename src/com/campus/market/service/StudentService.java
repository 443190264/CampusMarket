package com.campus.market.service;

import com.campus.market.entity.Student;
import java.util.List;
import java.math.BigDecimal;

public interface StudentService {
    boolean register(Student student);
    Student login(String studentId, String password);
    boolean modifyInfo(Student student);
    boolean removeStudent(int id);
    Student getById(int id);
    Student getByStudentId(String studentId);
    List<Student> searchByName(String keyword);
    boolean recharge(int studentId, BigDecimal amount);
    boolean adminResetPassword(String studentId, String newPassword);

}