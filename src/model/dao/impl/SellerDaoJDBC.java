package model.dao.impl;

import db.DB;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao {
    private Connection conn;

    public SellerDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Seller obj) {

    }

    @Override
    public void update(Seller obj) {

    }

    @Override
    public void deleteById(Seller obj) {

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
