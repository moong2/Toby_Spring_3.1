package springbook.user.service;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import springbook.mail.MailSender;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

public class UserServiceImpl implements UserService{
    UserDao userDao;
    UserLevelUpgradePolicy userLevelUpgradePolicy;
    private MailSender mailSender;
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    public void setMailSender(MailSender mailSender){
        this.mailSender = mailSender;
    }

    public void upgradeLevels(){
        List<User> users = userDao.getAll();

        for(User user : users) {
            if(userLevelUpgradePolicy.canUpgradeLevel(user)) {
                upgradeLevel(user);
                userDao.update(user);
            }
        }
    }
    protected void upgradeLevel(User user){
        userLevelUpgradePolicy.upgradeLevel(user);
        sendUpgradeEmail(user);
    }
    private void sendUpgradeEmail(User user){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());

        this.mailSender.send(mailMessage);
    }

    public void add(User user) {
        if(user.getLevel() == null) {user.setLevel(Level.BASIC);}
        userDao.add(user);
    }
}
