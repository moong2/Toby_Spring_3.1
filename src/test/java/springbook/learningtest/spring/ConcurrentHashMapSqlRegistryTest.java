package springbook.learningtest.spring;

import org.junit.Before;
import org.junit.Test;
import springbook.issuetracker.sqlService.UpdatableSqlRegistry;
import springbook.user.sqlservice.updatable.ConcurrentHashMapSqlRegistry;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{
    protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
        return new ConcurrentHashMapSqlRegistry();
    }
}
