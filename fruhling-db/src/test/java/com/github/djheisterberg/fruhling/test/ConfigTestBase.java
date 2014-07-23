package com.github.djheisterberg.fruhling.test;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.djheisterberg.fruhling.db.C3P0DataSourceFactory$;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigTestBase {

    protected static Config config;
    protected static DataSource dataSource;

    @BeforeClass
    public static void beforeClass() {
        config = ConfigFactory.load();
        Config dbConfig = config.getConfig("db");
        dataSource = C3P0DataSourceFactory$.MODULE$.apply(dbConfig);
    }

    @Test
    public void testSetup() {
        Assert.assertNotNull(dataSource);
    }
}
