package springbook.user.dao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import springbook.user.domain.User;

import java.sql.SQLException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class UserDaoTest {
    private UserDao dao;
    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp() {
        // 1. DaoFactory를 이용한 애플리케이션 컨텍스트
//        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

        // 2. XML을 이용한 애플리케이션 컨텍스트
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        this.dao = context.getBean("userDao", UserDao.class);

        this.user1 = new User("moong2", "박뭉", "I'm_moong2");
        this.user2 = new User("chicken", "치킨", "bhc");
        this.user3 = new User("pizza", "피자", "domino");
    }

    @Test
    public void addAndGet() throws ClassNotFoundException, SQLException {

        직접_생성한_DaoFactory_오브젝트_동등성_비교();
        스프링컨텍스트로부터_가져온_오브젝트_동등성_비교();

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        User userget1 = dao.get(user1.getId());
        assertThat(userget1.getName(), is(user1.getName()));
        assertThat(userget1.getPassword(), is(user1.getPassword()));

        User userget2 = dao.get(user2.getId());
        assertThat(userget2.getName(), is(user2.getName()));
        assertThat(userget2.getPassword(), is(user2.getPassword()));
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

    @Test
    public void count() throws ClassNotFoundException, SQLException {

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        assertThat(dao.getCount(), is(1));

        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        dao.add(user3);
        assertThat(dao.getCount(), is(3));
    }

    @Test(expected= EmptyResultDataAccessException.class)
    public void getUserFailure() throws ClassNotFoundException, SQLException {

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.get("unknown_id");
    }
}