package springbook.user.dao;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import springbook.user.domain.User;

import java.sql.SQLException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class UserDaoTest {
    @Test
    public void addAndGet() throws ClassNotFoundException, SQLException {
        // 1. DaoFactory를 이용한 애플리케이션 컨텍스트
//        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

        // 2. XML을 이용한 애플리케이션 컨텍스트
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        UserDao dao = context.getBean("userDao", UserDao.class);

        직접_생성한_DaoFactory_오브젝트_동등성_비교();
        스프링컨텍스트로부터_가져온_오브젝트_동등성_비교();

        User user = new User();
        user.setId("moong2");
        user.setName("박뭉");
        user.setPassword("I'm_moong2");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());

        assertThat(user2.getName(), is(user.getName()));
        assertThat(user2.getPassword(), is(user.getPassword()));
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