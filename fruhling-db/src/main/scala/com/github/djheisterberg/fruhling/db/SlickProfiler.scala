import java.sql.Connection
import java.sql.DatabaseMetaData
import javax.sql.DataSource

import scala.slick.driver.JdbcProfile

package com.github.djheisterberg.fruhling.db {

  object SlickProfiler {

    val DERBY = "Apache Derby"
    val H2 = "H2"
    val HSQL = "HSQL Database Engine"
    val MYSQL = "MySQL"
    val POSTGRES = "PostgreSQL"
    val ORACLE = "Oracle"

    val KNOWN_PROFILES = List(DERBY, H2, HSQL, MYSQL, POSTGRES, ORACLE)

    private val defaultDriver = "scala.slick.driver.JdbcDriver$"

    private val drivers = Map(
      DERBY -> "scala.slick.driver.DerbyDriver$",
      H2 -> "scala.slick.driver.H2Driver$",
      HSQL -> "scala.slick.driver.HsqldbDriver$",
      MYSQL -> "scala.slick.driver.MySQLDriver$",
      POSTGRES -> "scala.slick.driver.PostgresDriver$",
      ORACLE -> "com.typesafe.slick.driver.oracle.OracleDriver$")

    def apply(dataSource: DataSource): JdbcProfile = {
      new DataSourceOp(dataSource)(apply).get
    }

    def apply(connection: Connection): JdbcProfile = {
      val databaseMetaData = connection.getMetaData()
      apply(databaseMetaData)
    }

    def apply(databaseMetaData: DatabaseMetaData): JdbcProfile = {
      val databaseProductName = databaseMetaData.getDatabaseProductName()
      apply(databaseProductName);
    }

    def apply(databaseProductName: String): JdbcProfile = {
      val driverClassName = drivers.getOrElse(databaseProductName, defaultDriver)
      val driverClass = Class.forName(driverClassName)
      val driver = driverClass.getDeclaredField("MODULE$").get(null)
      driver.asInstanceOf[JdbcProfile]
    }
  }
}
