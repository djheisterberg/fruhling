import com.typesafe.config.Config
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

package com.github.djheisterberg.fruhling.db {

  object HikariCPDataSourceFactory extends DataSourceFactory[HikariDataSource] {

    override def apply(config: Config) = {
      val hikariConfig = new HikariConfig()
      hikariConfig.setDriverClassName(driver(config))
      hikariConfig.addDataSourceProperty("jdbcUrl", url(config))
      hikariConfig.addDataSourceProperty("user", username(config))
      hikariConfig.addDataSourceProperty("password", password(config))

      minConnections(config) map { i => hikariConfig.addDataSourceProperty("minimumIdle", i.toString) }
      maxConnections(config) map { i => hikariConfig.addDataSourceProperty("maximumPoolSize", i.toString) }

      val dataSource = new HikariDataSource(hikariConfig)
      dataSource
    }
  }
}
