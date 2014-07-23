import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

import scala.util.Failure
import scala.util.Success
import scala.util.Try

package com.github.djheisterberg.fruhling.db {

  class DataSourceOp(dataSource: DataSource) {
    def apply[T](op: Connection => T): Try[T] = {
      try {
        val connection = dataSource.getConnection()
        try {
          Success(op(connection))
        } catch {
          case e: Exception => Failure(e)
        } finally {
          try {
            connection.rollback()
          } catch {
            case e: Exception =>
          } finally {
            try {
              connection.close()
            } catch {
              case e: Exception =>
            }
          }
        }
      } catch { case e: Exception => Failure(e) }
    }
  }
}
