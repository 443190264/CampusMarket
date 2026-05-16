package com.campus.market.dao.impl;

import com.campus.market.dao.StudentDao;
import com.campus.market.entity.Student;
import com.campus.market.exception.SystemException;
import com.campus.market.util.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class StudentDaoImpl implements StudentDao {

    @Override
    public boolean add(Student student) {
        String sql = "INSERT INTO student(student_id, name, phone, balance, password, salt) VALUES(?,?,?,?,?,?)";
        List<Object> params = List.of(
                student.getStudentId(),
                student.getName(),
                student.getPhone(),
                student.getBalance() == null ? BigDecimal.ZERO : student.getBalance(),
                student.getPassword(),
                student.getSalt()
        );
        int generatedId = JdbcTemplate.executeInsert(sql, params);
        if (generatedId > 0) {
            student.setId(generatedId);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM student WHERE id = ?";
        return JdbcTemplate.executeUpdate(sql, List.of(id)) > 0;
    }

    @Override
    public boolean update(Student student) {
        String sql = "UPDATE student SET student_id=?, name=?, phone=?, balance=?, password=?, salt=?, is_admin=? WHERE id=?";
        List<Object> params = List.of(
                student.getStudentId(),
                student.getName(),
                student.getPhone(),
                student.getBalance(),
                student.getPassword(),
                student.getSalt(),
                student.isAdmin() ? 1 : 0,
                student.getId()
        );
        return JdbcTemplate.executeUpdate(sql, params) > 0;
    }

    @Override
    public Student findById(int id) {
        String sql = "SELECT * FROM student WHERE id = ?";
        return JdbcTemplate.queryForObject(sql, List.of(id), rs -> {
            try {
                return mapRowToStudent(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public Student findByStudentId(String studentId) {
        String sql = "SELECT * FROM student WHERE student_id = ?";
        return JdbcTemplate.queryForObject(sql, List.of(studentId), rs -> {
            try {
                return mapRowToStudent(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public Student findByStudentIdAndPassword(String studentId, String password) {
        String sql = "SELECT * FROM student WHERE student_id = ? AND password = ?";
        return JdbcTemplate.queryForObject(sql, List.of(studentId, password), rs -> {
            try {
                return mapRowToStudent(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public List<Student> findAll() {
        String sql = "SELECT * FROM student";
        return JdbcTemplate.queryForList(sql, List.of(), rs -> {
            try {
                return mapRowToStudent(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    @Override
    public List<Student> findByNameLike(String keyword) {
        String sql = "SELECT * FROM student WHERE name LIKE ?";
        return JdbcTemplate.queryForList(sql, List.of("%" + keyword + "%"), rs -> {
            try {
                return mapRowToStudent(rs);
            } catch (SQLException e) {
                throw new SystemException("结果集映射失败", e);
            }
        });
    }

    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setStudentId(rs.getString("student_id"));
        student.setName(rs.getString("name"));
        student.setPhone(rs.getString("phone"));
        student.setBalance(rs.getBigDecimal("balance"));
        student.setPassword(rs.getString("password"));
        student.setSalt(rs.getString("salt"));
        student.setAdmin(rs.getInt("is_admin") == 1);
        Timestamp ts = rs.getTimestamp("create_time");
        if (ts != null) {
            student.setCreateTime(ts.toLocalDateTime());
        }
        return student;
    }
}