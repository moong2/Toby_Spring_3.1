package springbook.user.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.mail.MailSender;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserServiceImpl userServiceImpl;
    @Autowired
    UserDao userDao;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    MailSender mailSender;

    List<User> users;

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("moong0", "박뭉영", "p1", "clapmean@gmail.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, Timestamp.valueOf(LocalDateTime.now())),
                new User("moong1", "박뭉일", "p2", "clapmean@gmail.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, Timestamp.valueOf(LocalDateTime.now())),
                new User("moong2", "박뭉이", "p3", "clapmean@gmail.com", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1, Timestamp.valueOf(LocalDateTime.now())),
                new User("moong3", "박뭉삼", "p4", "clapmean@gmail.com", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, Timestamp.valueOf(LocalDateTime.now())),
                new User("moong4", "박뭉사", "p5", "clapmean@gmail.com", Level.GOLD, 100, 100, Timestamp.valueOf(LocalDateTime.now()))
        );
        userServiceImpl.userLevelUpgradePolicy = new UserLevelUpgradeEvent();
        userServiceImpl.setMailSender(mailSender);
    }

    @Test
    @DirtiesContext
    public void upgradeLevels() throws Exception{
        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userService.upgradeLevels();

        checkLevel(users.get(0), false);
        checkLevel(users.get(1), true);
        checkLevel(users.get(2), false);
        checkLevel(users.get(3), true);
        checkLevel(users.get(4), false);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(3).getEmail()));
    }
    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<String>();

        public List<String> getRequests() {
            return requests;
        }

        @Override
        public void send(SimpleMailMessage simpleMailMessage) throws MailException {
            requests.add(simpleMailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage[] simpleMailMessages) throws MailException {

        }
    }
    public void checkLevel(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if(upgraded) {
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        }
        else {
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
        }
    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
    }

    @Test
    public void upgradeAllOrNothing() throws Exception{
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.userLevelUpgradePolicy = new UserLevelUpgradeEvent();
        testUserService.setMailSender(mailSender);

        UserServiceTx txUserService = new UserServiceTx();
        txUserService.setTransactionManager(transactionManager);
        txUserService.setUserService(testUserService);

        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        try {
            txUserService.upgradeLevels();
            fail("TestUserServiceException expected"); }
        catch(TestUserServiceException e) {
        }

        checkLevel(users.get(1), false);
    }
    static class TestUserService extends UserServiceImpl {
        private String id;

        private TestUserService(String id) {this.id = id;}

        @Override
        protected void upgradeLevel(User user) {
            if(user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }
    static class TestUserServiceException extends RuntimeException{}
}
