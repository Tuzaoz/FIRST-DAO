package model.dao;

import db.DB;
import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {
    public static SellerDao createSellerDao() throws ClassNotFoundException {
        return new SellerDaoJDBC(DB.getConnection());
    }
}
