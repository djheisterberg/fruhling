import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import org.slf4j.LoggerFactory

package com.github.djheisterberg.fruhling.db {

  object DataSourceOp {
    private[db] val logger = LoggerFactory.getLogger(classOf[DataSourceOp])
  }

  class DataSourceOp(dataSource: DataSource) {
    def apply[T](op: Connection => T): Try[T] = {
      try {
        val connection = dataSource.getConnection()
        try {
          try {
            val t = op(connection)
            try {
              commit(connection)
              Success(t)
            } catch {
              case e: Exception => Failure(e)
            }
          } catch {
            case e: Exception => {
              try {
                rollback(connection)
              } catch {
                case e: Exception => DataSourceOp.logger.warn("Ignored exception in rollback", e)
              }
              Failure(e)
            }
          }
        } finally {
          try {
            connection.close()
          } catch {
            case e: Exception => DataSourceOp.logger.warn("Ignored exception in close", e)
          }
        }
      } catch { case e: Exception => Failure(e) }
    }

    private def commit(connection: Connection) = if (!connection.getAutoCommit() && !connection.isClosed()) connection.commit()

    private def rollback(connection: Connection) = if (!connection.getAutoCommit() && !connection.isClosed()) connection.rollback()
  }
}
