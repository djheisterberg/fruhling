import com.github.djheisterberg.fruhling.db.DataSourceFactory
import com.mchange.v2.c3p0.ComboPooledDataSource
import com.typesafe.config.Config

package com.github.djheisterberg.fruhling.db {

  object C3P0DataSourceFactory extends DataSourceFactory[ComboPooledDataSource] {

    override def apply(config: Config) = {
      val dataSource = new ComboPooledDataSource()
      dataSource.setDriverClass(driver(config))
      dataSource.setJdbcUrl(url(config))
      dataSource.setUser(username(config))
      dataSource.setPassword(password(config))

      initConnections(config) map dataSource.setInitialPoolSize
      minConnections(config) map dataSource.setMinPoolSize
      maxConnections(config) map dataSource.setMaxPoolSize

      dataSource
    }
  }
}
