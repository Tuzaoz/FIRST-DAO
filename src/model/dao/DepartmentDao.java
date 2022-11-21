package model.dao;

import model.entities.Department;

import java.util.List;

public interface DepartmentDao {
    void insert(Department obj);
    void update(Department obj);
    void deleteById(Department obj);
    Department findById(Integer id);
    List<Department> findAll();
}
