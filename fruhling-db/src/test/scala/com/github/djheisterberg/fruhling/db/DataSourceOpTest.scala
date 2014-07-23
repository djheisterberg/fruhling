import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

import scala.util.Failure
import scala.util.Success
import scala.util.Try

package com.github.djheisterberg.fruhling.db {

  class DataSourceOpTest {

    val GOOD_OP = "goodOp"
    val BAD_OP = "badOp"
    val BAD_DATA_SOURCE = "badDataSource"

    def goodOp(connection: Connection): String = GOOD_OP

    def badOp(connection: Connection): String = throw new RuntimeException(BAD_OP)

    def goodConnection(): Connection = {
      val connection = Mockito.mock(classOf[Connection])
      connection
    }

    def rollbackConnection(): Connection = {
      val connection = goodConnection()
      Mockito.doThrow(new SQLException()).when(connection).rollback()
      connection
    }

    def closeConnection(): Connection = {
      val connection = goodConnection()
      Mockito.doThrow(new SQLException()).when(connection).close()
      connection
    }

    def labeledConnections() = Map("OK" -> goodConnection(), "Rollback" -> rollbackConnection(), "Close" -> closeConnection())

    def goodDataSource(connection: Connection): DataSource = {
      val dataSource = Mockito.mock(classOf[DataSource])
      Mockito.when(dataSource.getConnection()).thenReturn(connection)
      dataSource
    }

    def badDataSource(): DataSource = {
      val dataSource = Mockito.mock(classOf[DataSource])
      Mockito.doThrow(new SQLException(BAD_DATA_SOURCE)).when(dataSource).getConnection()
      dataSource
    }

    def verifyGood(label: String, t: Try[String]) = t match {
      case Success(s) => Assert.assertEquals(label, GOOD_OP, s)
      case Failure(e) => Assert.fail(label + ": " + e.getMessage())
    }

    def verifyBad(label: String, t: Try[String]) = t match {
      case s: Success[_] => Assert.fail(label + ": " + s.toString())
      case Failure(e) => e match {
        case re: RuntimeException => Assert.assertEquals(label, BAD_OP, e.getMessage())
        case t: Throwable => Assert.fail(label + ": " + t.getMessage())
      }
    }

    def testOp(op: Connection => String, verify: (String, Try[String]) => Unit)(labeledConnection: (String, Connection)) {
      val (label, connection) = labeledConnection
      val dataSource = goodDataSource(connection)
      verify(label, new DataSourceOp(dataSource)(op))
      Mockito.verify(connection).rollback()
      Mockito.verify(connection).close()
    }

    @Test
    def testGoodOp() { labeledConnections map testOp(goodOp, verifyGood) }

    @Test
    def testBadOp() { labeledConnections map testOp(badOp, verifyBad) }

    @Test
    def testBadDataSource() {
      val dataSource = badDataSource()
      new DataSourceOp(dataSource)(goodOp) match {
        case s: Success[_] => Assert.fail(s.toString())
        case Failure(e) => e match {
          case sqle: SQLException => Assert.assertEquals(BAD_DATA_SOURCE, sqle.getMessage())
          case t: Throwable => Assert.fail(t.getMessage())
        }
      }
    }
  }
}
