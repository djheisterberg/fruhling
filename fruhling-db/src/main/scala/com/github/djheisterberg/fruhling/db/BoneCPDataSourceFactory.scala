import com.jolbox.bonecp.BoneCPConfig
import com.jolbox.bonecp.BoneCPDataSource
import com.typesafe.config.Config

package com.github.djheisterberg.fruhling.db {

  object BoneCPDataSourceFactory extends DataSourceFactory[BoneCPDataSource] {

    override def apply(config: Config) = {
      registerDriver(config)

      val boneCPConfig = new BoneCPConfig()
      boneCPConfig.setJdbcUrl(url(config))
      boneCPConfig.setUsername(username(config))
      boneCPConfig.setPassword(password(config))

      minConnections(config) map boneCPConfig.setMinConnectionsPerPartition
      maxConnections(config) map boneCPConfig.setMaxConnectionsPerPartition

      val dataSource = new BoneCPDataSource(boneCPConfig);
      dataSource
    }
  }
}
  