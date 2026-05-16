package com.campus.market.dao;

import com.campus.market.entity.Student;
import java.util.List;

/**
 * 学生数据访问接口 - 定义对 student 表的操作
 */
public interface StudentDao {

    // 增加学生
    boolean add(Student student);

    // 根据id删除学生
    boolean deleteById(int id);

    // 更新学生信息
    boolean update(Student student);

    // 根据id查询学生
    Student findById(int id);

    // 根据学号查询学生
    Student findByStudentId(String studentId);

    // 查询所有学生
    List<Student> findAll();

    // 根据姓名模糊查询
    List<Student> findByNameLike(String keyword);

    // 根据学号和密码查询
    Student findByStudentIdAndPassword(String studentId, String password);
}