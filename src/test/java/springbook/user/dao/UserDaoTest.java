package springbook.user.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

//1. Spring을 이용한 Test
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserDaoTest {
//    @Autowired
//    private ApplicationContext context;
    @Autowired
    private UserDao dao;
    @Autowired
    private DataSource dataSource;

    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp() {
        // 1. DaoFactory를 이용한 애플리케이션 컨텍스트
//        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

//         2. XML을 이용한 애플리케이션 컨텍스트
//        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

//        this.dao = context.getBean("userDao", UserDao.class);

        // 컨테이너 없는 DI 테스트
//        dao = new UserDaoJdbc();
//        DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost/testdb", "root", "min20617", true);
//        ((UserDaoJdbc)dao).setDataSource(dataSource);

        this.user1 = new User("moong2", "박뭉", "I'm_moong2");
        this.user2 = new User("chicken", "치킨", "bhc");
        this.user3 = new User("pizza", "피자", "domino");
    }

    @Test
    public void addAndGet() {

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
        UserDao dao2 = factory.userDao();
        UserDao dao1 = factory.userDao();

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

    @Test
    public void getAll() throws ClassNotFoundException, SQLException{
        dao.deleteAll();

        List<User> users0 = dao.getAll();
        assertThat(users0.size(), is(0));

        dao.add(user1);
        List<User> users1 = dao.getAll();
        assertThat(users1.size(), is(1));
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertThat(users2.size(), is(2));
        checkSameUser(user2, users2.get(0));
        checkSameUser(user1, users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        assertThat(users3.size(), is(3));
        checkSameUser(user2, users3.get(0));
        checkSameUser(user1, users3.get(1));
        checkSameUser(user3, users3.get(2));
    }
    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user2.getName(), is(user2.getName()));
        assertThat(user3.getPassword(), is(user3.getPassword()));
    }

//    아래 두 테스트는 NoClassDefFoundError로 인해서 실행되지 않음

    @Test(expected = DataAccessException.class)
    public void duplicateKey() {
        dao.deleteAll();

        dao.add(user1);
        dao.add(user1);
    }

    @Test
    public void sqlExceptionTranslate() {
        dao.deleteAll();

        try{
            dao.add(user1);
            dao.add(user1);
        }
        catch(DuplicateKeyException ex){
            SQLException sqlEx = (SQLException)ex.getRootCause();
            SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
            assertThat(set.translate(null, null, sqlEx), is(DuplicateKeyException.class));
        }
    }
}