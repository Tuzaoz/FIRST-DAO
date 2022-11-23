package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentDaoJDBC implements DepartmentDao {
    private Connection conn;

    public DepartmentDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    public DepartmentDaoJDBC() {
    }

    @Override
    public void insert(Department obj) {
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement("insert into department (Name) " +
                    "VALUES (?) ", statement.RETURN_GENERATED_KEYS);
            statement.setString(1, obj.getName());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected>0){
                ResultSet rs =  statement.getGeneratedKeys();
                if (rs.next()){
                    int id = rs.getInt(1);
                    obj.setId(id);
                } else {
                    throw new DbException("Error: no rows affected");
                }
                DB.closeresultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            DB.closeStatement(statement);
        }
    }

    @Override
    public void update(Department obj) {
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement("update department " +
                    "set Name=? " +
                    "where Id=? ");
            statement.setString(1, obj.getName());
            statement.setInt(2, obj.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DB.closeStatement(statement);
        }

    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement("delete from department where Id=? ");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.prepareStatement("SELECT department.*,department.Name as DepName "
                    + "FROM department "
                    + "WHERE department.Id = ?");
            statement.setInt(1,id);
            rs = statement.executeQuery();
            if (rs.next()) {
                return instantiateDepartment(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            DB.closeStatement(statement);
            DB.closeresultSet(rs);
        }
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement statement = null;
        ResultSet set = null;
        List<Department> departments = new ArrayList<>();
        try {
            statement = conn.prepareStatement(
                    "select *,department.Name as DepName from department ");
            set = statement.executeQuery();
            while (set.next()){
                Department department = instantiateDepartment(set);
                departments.add(department);
            }
            return departments;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DB.closeStatement(statement);
            DB.closeresultSet(set);
        }
    }
    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt("Id"));
        department.setName(rs.getString("DepName"));
        return department;
    }
}
