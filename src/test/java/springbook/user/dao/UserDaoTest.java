package springbook.user.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import springbook.user.domain.User;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao dao = context.getBean("userDao", UserDao.class);

        직접_생성한_DaoFactory_오브젝트_동등성_비교();
        스프링컨텍스트로부터_가져온_오브젝트_동등성_비교();

        User user = new User();
        user.setId("moong2");
        user.setName("박뭉");
        user.setPassword("iwant");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());

        System.out.println(user2.getId() + " 조회 성공");
    }

    public static void 직접_생성한_DaoFactory_오브젝트_동등성_비교() {
        DaoFactory factory = new DaoFactory();
        UserDao dao1 = factory.userDao();
        UserDao dao2 = factory.userDao();

        System.out.println(dao1);
        System.out.println(dao2);
    }

    public static void 스프링컨텍스트로부터_가져온_오브젝트_동등성_비교() {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao dao1 = context.getBean("userDao", UserDao.class);
        UserDao dao2 = context.getBean("userDao", UserDao.class);

        System.out.println(dao1);
        System.out.println(dao2);
    }
}