package model.dao.impl;

import db.DB;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
                Department department = new Department();
                department.setId(rs.getInt("DepartmentId"));
                department.setName(rs.getString("DepName"));
                return new Seller(rs.getInt("Id"),
                                        rs.getString("Name"),
                                        rs.getNString("Email"),
                                        rs.getDate("BirthDate").toLocalDate(),
                                        rs.getDouble("BaseSalary"),
                                        department);

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
    public List<Seller> findAll() {
        return null;
    }
}
