package springbook.user.dao;

import jdk.nashorn.internal.scripts.JD;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.*;

public class UserDao {
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

    private JdbcContext jdbcContext;

    public void setDataSource(DataSource dataSource){
        this.jdbcContext = new JdbcContext();

        this.jdbcContext.setDataSource(dataSource);

        this.dataSource = dataSource;
    }


    // jdbcContext로 try/catch/finally 구문 실행
//    private JdbcContext jdbcContext;
//
//    public void setJdbcContext(JdbcContext jdbcContext) {
//        this.jdbcContext = jdbcContext;
//    }

    public void add(final User user) throws ClassNotFoundException, SQLException {
        this.jdbcContext.excuteSqlAndBindingParameter("insert into users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
//        Connection c = connectionMaker.makeConnection();
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        User user = null;
        if(rs.next()){
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if(user == null) throw new EmptyResultDataAccessException(1);

        return user;
    }

    public void deleteAll() throws SQLException {
        this.jdbcContext.excuteSql("delete from users");
    }

    public int getCount() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = dataSource.getConnection();

            ps = c.prepareStatement("select count(*) from users");

            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch(SQLException e){
            throw e;
        } finally {
            if(rs != null) {
                try{
                    rs.close();
                } catch(SQLException e){

                }
            }
            if(ps != null) {
                try{
                    ps.close();
                }catch(SQLException e){

                }
            }
            if(c != null) {
                try{
                    c.close();
                }catch(SQLException e){

                }
            }
        }
    }
}
