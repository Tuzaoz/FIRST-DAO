import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println("==== Teste findById ====");
        SellerDao sellerDao = DaoFactory.createSellerDao();
        Seller seller = sellerDao.findById(3);
        System.out.println(seller);
        System.out.println();
        System.out.println("==== Teste findByDepartmentId ====");
        Department department = new Department(2,null);
        List<Seller> sellers = sellerDao.findByDepartmentId(department);
        for (Seller s :sellers
             ) {
            System.out.println(s);
        }
        System.out.println();
        System.out.println("==== Teste findAll ====");
        List<Seller> sellers2 = sellerDao.findAll();
        for (Seller s :sellers2
        ) {
            System.out.println(s);
        }
        System.out.println();
        System.out.println("==== Teste Insert ====");
        Seller seller1 = new Seller(null,"Tuzão","tuzão@gmail.com", LocalDate.now(), 4000.0, department);
        sellerDao.insert(seller1);
        System.out.println("Inserted. new id: "+ seller1.getId());

    }
}