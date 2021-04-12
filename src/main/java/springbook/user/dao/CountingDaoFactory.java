package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class CountingDaoFactory {
    @Bean
    public UserDaoJdbc userDao() {

        UserDaoJdbc userDao = new UserDaoJdbc();

        // 1. ConnectionMaker 인터페이스 사용
//        userDao.setConnectionMaker(connectionMaker());

        // 2. DataSource 사용
        userDao.setJdbcTemplate(dataSource());

        return userDao;
    }

    // 1. connectionMaker 인터페이스 이용
//    @Bean
//    public ConnectionMaker connectionMaker() {
//        return new DConnectionMaker();
////        return new NConnectionMaker();
//    }

    // 2. DataSource 이용
    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/spring_study");
        dataSource.setUsername("root");
        dataSource.setPassword("min20617");

        return dataSource;
    }

    @Bean
    public ConnectionMaker realConnectionMaker(){
        return new DConnectionMaker();
    }
}