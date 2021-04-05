package springbook.user.dao;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
//        Connection c = connectionMaker.makeConnection();
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();

        ps.close();
        c.close();
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
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();
            ps = c.prepareStatement("delete from users");
            ps.executeUpdate();
        } catch(SQLException e) {
            throw e;
        } finally {
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



        ps.close();
        c.close();
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
