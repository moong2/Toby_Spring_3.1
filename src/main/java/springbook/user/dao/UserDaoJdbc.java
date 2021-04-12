package springbook.user.dao;

import com.mysql.cj.exceptions.MysqlErrorNumbers;
import jdk.nashorn.internal.scripts.JD;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class UserDaoJdbc implements UserDao{
    // 1. 생성자를 이용하는 방법
//    private ConnectionMaker connectionMaker;
//
//    public void setConnectionMaker(ConnectionMaker connectionMaker) {
////        DI(의존관계 주입) = Dependency Injection
//        this.connectionMaker = connectionMaker;
//
////        DL(의존관계 검색) = Dependency Lookup
////        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
////        this.connectionMaker = context.getBean("connectionMaker", ConnectionMaker.class);
//    }

    // 2. 이미 존재하는 DataSource를 이용
    private DataSource dataSource;

    // 1) JdbcContext를 이용하는 템플릿/콜백 방식
//    private JdbcContext jdbcContext;
    // 2) spring에서 제공하는 JdbcTemplate을 이용하는 템플릿/콜백 방식
    private JdbcTemplate jdbcTemplate;

    // -1- DataSource를 빈으로 등록
//    public void setDataSource(DataSource dataSource){
//        this.jdbcTemplate = new JdbcTemplate();
//
//        this.jdbcTemplate.setDataSource(dataSource);
//
//        this.dataSource = dataSource;
//    }
    // -2- JdbcTemplate을 빈으로 등록
    public void setJdbcTemplate(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    // jdbcContext로 try/catch/finally 구문 실행
//    private JdbcContext jdbcContext;
//
//    public void setJdbcContext(JdbcContext jdbcContext) {
//        this.jdbcContext = jdbcContext;
//    }

    private RowMapper<User> userMapper =
            new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet resultSet, int i) throws SQLException {
                    User user = new User();
                    user.setId(resultSet.getString("id"));
                    user.setName(resultSet.getString("name"));
                    user.setPassword(resultSet.getString("password"));
                    return user;
                }
            };


    public void add(final User user){
        this.jdbcTemplate.update("INSERT INTO users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
    }

    public User get(String id) {
        // 1)
////        Connection c = connectionMaker.makeConnection();
//        Connection c = dataSource.getConnection();
//
//        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
//        ps.setString(1, id);
//
//        ResultSet rs = ps.executeQuery();
//
//        User user = null;
//        if(rs.next()){
//            user = new User();
//            user.setId(rs.getString("id"));
//            user.setName(rs.getString("name"));
//            user.setPassword(rs.getString("password"));
//        }
//
//        rs.close();
//        ps.close();
//        c.close();
//
//        if(user == null) throw new EmptyResultDataAccessException(1);
//
//        return user;

        // 2) JdbcTemplate의 queryForObject()와 RowMapper 적용
        return this.jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?",
                new Object[]{id}, this.userMapper);
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("SELECT * FROM users ORDER BY id", this.userMapper);
    }

    public void deleteAll() {
        // 1) jdbcContext 사용 방식
//        this.jdbcContext.excuteSql("delete from users");
        // 2) spring에서 제공하는 JdbcTemplate 사용 방식
            // 2-1) JdbcTemplate을 적용
//        this.jdbcTemplate.update(
//                new PreparedStatementCreator() {
//                    @Override
//                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
//                        return connection.prepareStatement("DELETE FROM users");
//                    }
//                }
//        )
            // 2-2) JdbcTemplate의 내장 콜백을 사용하는 방식
        this.jdbcTemplate.update("DELETE FROM users");
    }

    public int getCount() {

        // 1)
//        Connection c = null;
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//
//        try {
//            c = dataSource.getConnection();
//
//            ps = c.prepareStatement("select count(*) from users");
//
//            rs = ps.executeQuery();
//            rs.next();
//            return rs.getInt(1);
//        } catch(SQLException e){
//            throw e;
//        } finally {
//            if(rs != null) {
//                try{
//                    rs.close();
//                } catch(SQLException e){
//
//                }
//            }
//            if(ps != null) {
//                try{
//                    ps.close();
//                }catch(SQLException e){
//
//                }
//            }
//            if(c != null) {
//                try{
//                    c.close();
//                }catch(SQLException e){
//
//                }
//            }
//        }

        // 2) JdbcTemplate 적용
//        return this.jdbcTemplate.query(new PreparedStatementCreator() {
//            @Override
//            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
//                return connection.prepareStatement("SELECT COUNT(*) FROM users");
//            }
//        }, new ResultSetExtractor<Integer>() {
//            @Override
//            public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
//                resultSet.next();
//                return resultSet.getInt(1);
//            }
//        });

        // 3) JdbcTemplate 내장 콜백 사용
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM users");
    }
}
