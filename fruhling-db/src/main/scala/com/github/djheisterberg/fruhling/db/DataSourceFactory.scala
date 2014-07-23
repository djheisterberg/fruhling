import com.typesafe.config.Config

import java.sql.Driver
import java.sql.DriverManager
import javax.sql.DataSource

package com.github.djheisterberg.fruhling.db {

  object DataSourceFactory {

    val DRIVER = "driver"
    val URL = "url"
    val USERNAME = "username"
    val PASSWORD = "password"
    val INIT_CONNECTIONS = "initConnections"
    val MIN_CONNECTIONS = "minConnections"
    val MAX_CONNECTIONS = "maxConnections"

    def apply[D <: DataSource](className: String): DataSourceFactory[D] = {
      val klass = Class.forName(className).asInstanceOf[Class[DataSourceFactory[D]]]
      apply[D](klass)
    }

    def apply[D <: DataSource](klass: Class[DataSourceFactory[D]]): DataSourceFactory[D] = {
      val dataSourceFactory = klass.getDeclaredField("MODULE$").get(null).asInstanceOf[DataSourceFactory[D]]
      dataSourceFactory
    }
  }

  trait DataSourceFactory[+D <: DataSource] {

    def driver(config: Config): String = config.getString(DataSourceFactory.DRIVER)
    def url(config: Config): String = config.getString(DataSourceFactory.URL)
    def username(config: Config): String = config.getString(DataSourceFactory.USERNAME)
    def password(config: Config): String = config.getString(DataSourceFactory.PASSWORD)

    def getIntOption(config: Config, key: String): Option[Int] =
      if (config.hasPath(key)) Some(config.getInt(key)) else None
    def initConnections(config: Config): Option[Int] = getIntOption(config, DataSourceFactory.INIT_CONNECTIONS)
    def minConnections(config: Config): Option[Int] = getIntOption(config, DataSourceFactory.MIN_CONNECTIONS)
    def maxConnections(config: Config): Option[Int] = getIntOption(config, DataSourceFactory.MAX_CONNECTIONS)

    def registerDriver(config: Config) {
      val driverClass = Class.forName(config.getString(DataSourceFactory.DRIVER)).asInstanceOf[Class[Driver]]
      val driver = driverClass.newInstance()
      DriverManager.registerDriver(driver)
    }

    def apply(config: Config): D
  }
}
