import javax.sql.DataSource

import com.typesafe.config.ConfigFactory

import org.junit.Assert
import org.junit.Test

package com.github.djheisterberg.fruhling.db {

  class DataSourceFactoryTest {

    val knownFactoryClasses = List(
      BoneCPDataSourceFactory.getClass,
      C3P0DataSourceFactory.getClass,
      CommonsDBCPDataSourceFactory.getClass,
      HikariCPDataSourceFactory.getClass)

    val knownFactories = List(
      BoneCPDataSourceFactory,
      C3P0DataSourceFactory,
      CommonsDBCPDataSourceFactory,
      HikariCPDataSourceFactory)

    @Test
    def testApplyString() {
      for (factoryClass <- knownFactoryClasses) {
        val className = factoryClass.getName
        val dataSourceFactory = DataSourceFactory(className)
        Assert.assertNotNull(className, dataSourceFactory)
      }
    }

    @Test
    def testApplyClass() {
      for (factoryClass <- knownFactoryClasses) {
        val className = factoryClass.getName
        val dataSourceFactory = DataSourceFactory(factoryClass.asInstanceOf[Class[DataSourceFactory[DataSource]]])
        Assert.assertNotNull(className, dataSourceFactory)
      }
    }

    @Test
    def testKnownFactories() {
      val config = ConfigFactory.load().getConfig("db")
      for (factory <- knownFactories) {
        val dataSource = factory(config)
        Assert.assertNotNull(factory.getClass.getName, dataSource)
      }
    }
  }
}
