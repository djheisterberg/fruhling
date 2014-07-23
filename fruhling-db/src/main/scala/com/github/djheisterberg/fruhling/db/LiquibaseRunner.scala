import java.sql.Connection

import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.integration.commandline.CommandLineResourceAccessor

package com.github.djheisterberg.fruhling.db {

  object LiquibaseRunner {
    def apply(configXML: String)(connection: Connection) {
      val resourceAccessor = new CommandLineResourceAccessor(getClass.getClassLoader)
      val databaseConnection = new JdbcConnection(connection)
      val liquibase = new Liquibase(configXML, resourceAccessor, databaseConnection)
      liquibase.update(null: String)
      connection.commit()
    }
  }
}
