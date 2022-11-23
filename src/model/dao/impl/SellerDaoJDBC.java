package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao {
    private final Connection conn;

    public SellerDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Seller obj) {
       PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement("insert into seller (Name, Email, BirthDate, BaseSalary, DepartmentId) " +
                    "VALUES (?, ?, ?, ?, ?) ", statement.RETURN_GENERATED_KEYS);
            statement.setString(1, obj.getName());
            statement.setString(2, obj.getemail());
            statement.setDate(3, Date.valueOf(obj.getBirthDate()));
            statement.setDouble(4,obj.getBaseSalary());
            statement.setInt(5,obj.getDepartment().getId());
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
    public void update(Seller obj) {
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement("update seller " +
                    "set Name=?, Email=?, BirthDate=?, BaseSalary=?, DepartmentId=? " +
                    "where Id=? ");
            statement.setString(1, obj.getName());
            statement.setString(2, obj.getemail());
            statement.setDate(3, Date.valueOf(obj.getBirthDate()));
            statement.setDouble(4,obj.getBaseSalary());
            statement.setInt(5,obj.getDepartment().getId());
            statement.setInt(6,obj.getId());
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
            statement = conn.prepareStatement("delete from seller where Id=?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.prepareStatement("SELECT seller.*,department.Name as DepName "
                    + "FROM seller INNER JOIN department "
                    + "ON seller.DepartmentId = department.Id "
                    + "WHERE seller.Id = ?");
            statement.setInt(1,id);
            rs = statement.executeQuery();
            if (rs.next()) {
                Department department = instantiateDepartment(rs);
                return  instantiateSeller(rs,department);
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
    public List<Seller> findByDepartmentId(Department department) {
        PreparedStatement statement = null;
        ResultSet set = null;
        List<Seller> sellers = new ArrayList<>();
        try {
            statement = conn.prepareStatement(
                    "select seller.* , department.Name as DepName "
                    + "FROM seller inner join department "
                    + "on seller.DepartmentId = department.Id "
                    + "where DepartmentId = ? "
                    + "ORDER BY Name");
            statement.setInt(1, department.getId());
            set = statement.executeQuery();
            Map<Integer,Department> departmentMap = new HashMap<>();
                while (set.next()){
                    Department dep = departmentMap.get(set.getInt("DepartmentId"));
                    if (dep == null) {
                        dep = instantiateDepartment(set);
                        departmentMap.put(dep.getId(),dep);
                    }
                    Seller seller = instantiateSeller(set,dep);
                    sellers.add(seller);
                }
                return sellers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DB.closeStatement(statement);
            DB.closeresultSet(set);
        }
    }

    private Seller instantiateSeller(ResultSet rs, Department department) throws SQLException {
        return new Seller(rs.getInt("Id"),
                rs.getString("Name"),
                rs.getNString("Email"),
                rs.getDate("BirthDate").toLocalDate(),
                rs.getDouble("BaseSalary"),
                department);

    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt("DepartmentId"));
        department.setName(rs.getString("DepName"));
        return department;
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement statement = null;
        ResultSet set = null;
        List<Seller> sellers = new ArrayList<>();
        try {
            statement = conn.prepareStatement(
                    "select seller.* , department.Name as DepName "
                            + "FROM seller inner join department "
                            + "on seller.DepartmentId = department.Id "
                            + "ORDER BY Name");
            set = statement.executeQuery();
            Map<Integer,Department> departmentMap = new HashMap<>();
            while (set.next()){
                Department dep = departmentMap.get(set.getInt("DepartmentId"));
                if (dep == null) {
                    dep = instantiateDepartment(set);
                    departmentMap.put(dep.getId(),dep);
                }
                Seller seller = instantiateSeller(set,dep);
                sellers.add(seller);
            }
            return sellers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DB.closeStatement(statement);
            DB.closeresultSet(set);
        }

    }
}
