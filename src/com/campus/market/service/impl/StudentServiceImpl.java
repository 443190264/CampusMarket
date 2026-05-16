package com.campus.market.service.impl;

import com.campus.market.dao.StudentDao;
import com.campus.market.dao.impl.StudentDaoImpl;
import com.campus.market.entity.Student;
import com.campus.market.service.StudentService;
import com.campus.market.util.LogUtil;
import com.campus.market.util.PasswordUtil;
import com.campus.market.exception.BusinessException;

import java.math.BigDecimal;
import java.util.List;

public class StudentServiceImpl implements StudentService {
    private StudentDao studentDao = new StudentDaoImpl();

    @Override
    public boolean register(Student student) {
        Student exist = studentDao.findByStudentId(student.getStudentId());
        if (exist != null) {
            throw new BusinessException("学号已存在：" + student.getStudentId());
        }

        String salt = PasswordUtil.generateSalt();
        String hashedPwd = PasswordUtil.hashPassword(student.getPassword(), salt);
        student.setPassword(hashedPwd);
        student.setSalt(salt);

        if (student.getBalance() == null) {
            student.setBalance(BigDecimal.ZERO);
        }

        boolean result = studentDao.add(student);
        if (result) {
            LogUtil.record(student.getId(), "注册", "学生注册成功，学号=" + student.getStudentId());
        }
        return result;
    }

    @Override
    public Student login(String studentId, String password) {
        Student student = studentDao.findByStudentId(studentId);
        if (student == null) {
            throw new BusinessException("学号不存在：" + studentId);
        }

        boolean valid = PasswordUtil.verifyPassword(password, student.getSalt(), student.getPassword());
        if (!valid) {
            throw new BusinessException("密码错误");
        }

        LogUtil.record(student.getId(), "登录", "登录成功");
        return student;
    }

    @Override
    public boolean modifyInfo(Student student) {
        Student old = studentDao.findById(student.getId());
        if (old == null) {
            throw new BusinessException("学生不存在，ID=" + student.getId());
        }
        boolean result = studentDao.update(student);
        if (result) {
            LogUtil.record(student.getId(), "修改信息", "学号=" + student.getStudentId());
        }
        return result;
    }

    @Override
    public boolean removeStudent(int id) {
        Student s = studentDao.findById(id);
        if (s == null) {
            throw new BusinessException("学生不存在，ID=" + id);
        }
        boolean result = studentDao.deleteById(id);
        if (result) {
            LogUtil.record(id, "删除学生", "学生ID=" + id);
        }
        return result;
    }

    @Override
    public Student getById(int id) {
        return studentDao.findById(id);
    }

    @Override
    public Student getByStudentId(String studentId) {
        return studentDao.findByStudentId(studentId);
    }

    @Override
    public List<Student> searchByName(String keyword) {
        return studentDao.findByNameLike(keyword);
    }

    @Override
    public boolean recharge(int studentId, BigDecimal amount) {
        Student student = studentDao.findById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在，ID=" + studentId);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须为正数，输入：" + amount);
        }
        BigDecimal newBalance = student.getBalance().add(amount);
        student.setBalance(newBalance);
        boolean result = studentDao.update(student);
        if (result) {
            LogUtil.record(studentId, "充值", "充值金额：" + amount + "，新余额：" + newBalance);
        }
        return result;
    }

    @Override
    public boolean adminResetPassword(String studentId, String newPassword) {
        Student student = studentDao.findByStudentId(studentId);
        if (student == null) {
            throw new BusinessException("学号不存在：" + studentId);
        }
        String newSalt = PasswordUtil.generateSalt();
        String newHash = PasswordUtil.hashPassword(newPassword, newSalt);
        student.setSalt(newSalt);
        student.setPassword(newHash);
        boolean result = studentDao.update(student);
        if (result) {
            LogUtil.record(student.getId(), "管理员重置密码", "学号=" + studentId);
        }
        return result;
    }

}