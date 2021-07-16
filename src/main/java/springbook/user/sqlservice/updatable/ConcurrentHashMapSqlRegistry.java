package springbook.user.sqlservice.updatable;

import org.springframework.jdbc.object.UpdatableSqlQuery;
import springbook.issuetracker.sqlService.UpdatableSqlRegistry;
import springbook.user.sqlservice.SqlUpdateFailureException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapSqlRegistry implements UpdatableSqlRegistry {
    private Map<String, String> sqlMap = new ConcurrentHashMap<String, String>();

    @Override
    public void updateSql(String key, String sql) throws SqlUpdateFailureException {
        if(sqlMap.get(key) == null) {
            throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다");
        }

        sqlMap.put(key, sql);
    }

    @Override
    public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException {
        for(Map.Entry<String, String> entry : sqlmap.entrySet()) {
            updateSql(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void registerSql(String key, String sql) {
        sqlMap.put(key, sql);
    }

    @Override
    public String findSql(String key) throws RuntimeException {
        String sql = sqlMap.get(key);
        if(sql == null) throw new RuntimeException(key + "를 이용해서 SQL을 찾을 수 없습니다");
        else return sql;
    }
}