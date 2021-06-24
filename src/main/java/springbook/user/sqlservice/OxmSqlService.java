//package springbook.user.sqlservice;
//
//import org.springframework.core.io.ClassPathResource;
//import springbook.user.dao.UserDao;
//import springbook.user.sqlservice.jaxb.SqlType;
//import springbook.user.sqlservice.jaxb.Sqlmap;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Unmarshaller;
//import javax.xml.transform.Source;
//import javax.xml.transform.stream.StreamSource;
//import java.io.File;
//import java.io.IOException;
//
//public class OxmSqlService implements SqlService{
//    private final BaseSqlService baseSqlService = new BaseSqlService();
//    private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
//
//    private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
//
//    public void setSqlRegistry(SqlRegistry sqlRegistry) {
//        this.sqlRegistry = sqlRegistry;
//    }
//
//    public void setSqlmap(Resource sqlmap) {
//        this.oxmSqlReader.setSqlmap(sqlmap);
//    }
//
//    @PostConstruct
//    public void loadSql() {
//        this.baseSqlService.setSqlReader(this.oxmSqlReader);
//        this.baseSqlService.setSqlRegistry(this.sqlRegistry);
//
//        this.baseSqlService.loadSql();
//    }
//
//    @Override
//    public String getSql(String key) throws SqlRetrievalFailureException {
//        return this.baseSqlService.getSql(key);
//    }
//
//    private class OxmSqlReader implements SqlReader {
//        private Resource sqlmap = (Resource) new ClassPathResource("sqlmap.xml", UserDao.class);
//
//        public void setSqlmap(Resource sqlmap) {
//            this.sqlmap = sqlmap;
//        }
//
//        @Override
//        public void read(SqlRegistry sqlRegistry) {
//            try {
//                Source source = new StreamSource(sqlmap.getInputStream());
//
//                for(SqlType sql : sqlmap.getSql()) {
//                    sqlRegistry.registerSql(sql.getKey(), sql.getValue());
//                }
//            } catch(JAXBException e) {
//                throw new IllegalArgumentException(this.sqlmap + "을 가져올 수 없습니다", e);
//            }
//        }
//    }
//}
