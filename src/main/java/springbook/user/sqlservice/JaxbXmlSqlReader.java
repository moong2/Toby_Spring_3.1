package springbook.user.sqlservice;

import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class JaxbXmlSqlReader implements SqlReader{
    @Override
    public void read(SqlRegistry sqlRegistry) {
        String contextPath = Sqlmap.class.getPackage().getName();
        try {
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = context.createUnmarshaller();
//			InputStream is = UserDao.class.getResourceAsStream(sqlmapFile);
            Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(new File("sqlmap.xml"));
            for(SqlType sql : sqlmap.getSql()) {
                sqlRegistry.registerSql(sql.getKey(), sql.getValue());
            }
        } catch(JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
