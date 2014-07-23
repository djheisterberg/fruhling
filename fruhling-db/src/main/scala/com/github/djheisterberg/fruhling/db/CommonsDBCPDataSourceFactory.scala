import com.github.djheisterberg.fruhling.db.DataSourceFactory
import com.typesafe.config.Config

import org.apache.commons.dbcp2.BasicDataSource

package com.github.djheisterberg.fruhling.db {

  object CommonsDBCPDataSourceFactory extends DataSourceFactory[BasicDataSource] {

    override def apply(config: Config) = {
      val dataSource = new BasicDataSource()
      dataSource.setDriverClassName(driver(config))
      dataSource.setUrl(url(config));
      dataSource.setUsername(username(config));
      dataSource.setPassword(password(config));

      initConnections(config) map dataSource.setInitialSize
      minConnections(config) map dataSource.setMinIdle
      maxConnections(config) map dataSource.setMaxTotal
      dataSource;
    }
  }
}
