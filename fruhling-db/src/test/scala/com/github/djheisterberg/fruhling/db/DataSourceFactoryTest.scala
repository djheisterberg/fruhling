import javax.sql.DataSource

import org.junit.Assert
import org.junit.Test

package com.github.djheisterberg.fruhling.db {

  class DataSourceFactoryTest {

    val knownFactories = List(
      BoneCPDataSourceFactory.getClass,
      C3P0DataSourceFactory.getClass,
      CommonsDBCPDataSourceFactory.getClass,
      HikariCPDataSourceFactory.getClass)

    @Test
    def testApplyString() {
      for (factoryClass <- knownFactories) {
        val className = factoryClass.getName
        val dataSourceFactory = DataSourceFactory(className)
        Assert.assertNotNull(className, dataSourceFactory)
      }
    }

    @Test
    def testApplyClass() {
      for (factoryClass <- knownFactories) {
        val className = factoryClass.getName
        val dataSourceFactory = DataSourceFactory(factoryClass.asInstanceOf[Class[DataSourceFactory[DataSource]]])
        Assert.assertNotNull(className, dataSourceFactory)
      }
    }
  }
}
