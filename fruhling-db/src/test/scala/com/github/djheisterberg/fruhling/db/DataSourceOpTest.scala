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
    val COMMIT = "commit"
    val ROLLBACK = "rollback"
    val CLOSE = "close"

    type ConnectionFactory = (String, Connection => Connection)
    type Verifier = (String, Int, Try[String], Connection) => Unit

    def goodOp(connection: Connection) = GOOD_OP

    def badOp(connection: Connection): String = throw new RuntimeException(BAD_OP)

    def goodDataSource(connection: Connection) = {
      val dataSource = Mockito.mock(classOf[DataSource])
      Mockito.when(dataSource.getConnection()).thenReturn(connection)
      dataSource
    }

    def badDataSource() = {
      val dataSource = Mockito.mock(classOf[DataSource])
      Mockito.doThrow(new SQLException(BAD_DATA_SOURCE)).when(dataSource).getConnection()
      dataSource
    }

    def goodConnection() = Mockito.mock(classOf[Connection])

    def throwOnCommit(connection: Connection) = {
      Mockito.doThrow(new SQLException(COMMIT)).when(connection).commit()
      connection
    }

    def throwOnRollback(connection: Connection) = {
      Mockito.doThrow(new SQLException(ROLLBACK)).when(connection).rollback()
      connection
    }

    def throwOnClose(connection: Connection) = {
      Mockito.doThrow(new SQLException(CLOSE)).when(connection).close()
      connection
    }

    def autoCommit(connection: Connection) = {
      Mockito.when(connection.getAutoCommit()).thenReturn(true);
      connection;
    }

    def closed(connection: Connection) = {
      Mockito.when(connection.isClosed()).thenReturn(true);
      connection
    }

    // Commit must come first.
    val throwers = List[ConnectionFactory](
      ("Commit", throwOnCommit),
      ("OK", identity),
      ("Rollback", throwOnRollback),
      ("Close", throwOnClose))

    val modifiers = List[ConnectionFactory](
      ("", identity),
      ("ac-", autoCommit),
      ("cls-", closed))

    def connections() = for (modifier <- modifiers; thrower <- throwers) yield (modifier._1 + thrower._1, modifier._2 compose thrower._2)

    def verifySuccessGood(label: String, commitCount: Int, t: Try[String], connection: Connection) {
      t match {
        case Success(s) => Assert.assertEquals(label, GOOD_OP, s)
        case Failure(e) => Assert.fail(label + ": " + e)
      }
      verifyGood(commitCount, connection)
    }

    def verifyFailureGood(label: String, commitCount: Int, t: Try[String], connection: Connection) {
      t match {
        case Success(s) => Assert.fail(label)
        case Failure(e) => e match {
          case sqle: SQLException => Assert.assertEquals(label, COMMIT, sqle.getMessage())
          case t: Throwable => Assert.fail(label + ": " + t)
        }
      }
      verifyGood(commitCount, connection)
    }

    def verifyGood(commitCount: Int, connection: Connection) {
      Mockito.verify(connection, Mockito.times(commitCount)).commit()
      Mockito.verify(connection, Mockito.never()).rollback()
    }

    def verifyBad(label: String, rollbackCount: Int, t: Try[String], connection: Connection) {
      t match {
        case s: Success[_] => Assert.fail(label + ": " + s)
        case Failure(e) => e match {
          case re: RuntimeException => Assert.assertEquals(label, BAD_OP, e.getMessage())
          case t: Throwable => Assert.fail(label + ": " + t)
        }
      }
      Mockito.verify(connection, Mockito.never()).commit()
      Mockito.verify(connection, Mockito.times(rollbackCount)).rollback()
    }

    def testOp(cnx: ConnectionFactory, op: Connection => String, verify: Verifier) {
      val connection = cnx._2(goodConnection())
      val dataSource = goodDataSource(connection)
      val commitOrRollbackCount = if (connection.getAutoCommit || connection.isClosed()) 0 else 1
      verify(cnx._1, commitOrRollbackCount, new DataSourceOp(dataSource)(op), connection)
      Mockito.verify(connection, Mockito.times(1)).close()
    }

    @Test
    def testGoodOp() {
      // 1st case is special
      val cnxs = connections()
      testOp(cnxs.head, goodOp, verifyFailureGood)
      for (cnx <- cnxs.tail) testOp(cnx, goodOp, verifySuccessGood)
    }

    @Test
    def testBadOp() {
      for (cnx <- connections()) testOp(cnx, badOp, verifyBad)
    }

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

    @Test
    def testConnections() {
      Assert.assertEquals("Expected 12 connections", 12, connections().size)
    }

    @Test
    def testThrow() {
      def testThrow(assertMsg: String, cnx: ConnectionFactory, op: (Connection) => Any) {
        try {
          op(cnx._2(goodConnection()))
          Assert.fail(assertMsg)
        } catch {
          case sqle: SQLException => Assert.assertEquals(assertMsg, cnx._1, sqle.getMessage())
          case e: Exception => Assert.fail(assertMsg + " : " + e)
        }
      }
      testThrow("Expected SQLException on commit", (COMMIT, throwOnCommit), _.commit())
      testThrow("Expected SQLException on rollback", (ROLLBACK, throwOnRollback), _.rollback())
      testThrow("Expected SQLException on close", (CLOSE, throwOnClose), _.close())
    }
  }
}
