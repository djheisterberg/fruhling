import scala.concurrent.Await
import scala.concurrent.Awaitable
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.util.Failure
import scala.util.Success

import com.github.djheisterberg.fruhling.db.DataSourceOp
import com.github.djheisterberg.fruhling.db.HikariCPDataSourceFactory
import com.github.djheisterberg.fruhling.db.LiquibaseRunner
import com.github.djheisterberg.fruhling.db.SlickProfiler

import com.typesafe.config.ConfigFactory

import org.junit.Assert
import org.junit.Test

package com.github.djheisterberg.fruhling.db {

  class TestDAOImplTest {

    private val DB_CONFIG_PATH = "db"
    private val DDL_PATH = "fruhling-ddl.xml"

    private implicit val waitTime = 2.seconds

    private def sync[A](op: => Awaitable[A])(implicit wait: Duration): A = {
      Await.result(op, wait)
    }

    private def getDAO(): TestDAO = {
      val config = ConfigFactory.load()
      val dbConfig = config.getConfig(DB_CONFIG_PATH)
      val dataSource = HikariCPDataSourceFactory(dbConfig)

      val dataSourceOp = new DataSourceOp(dataSource)
      dataSourceOp(LiquibaseRunner(DDL_PATH))
      val profile = dataSourceOp(SlickProfiler.apply).get

      val dao = new TestDAOImpl(dataSource, profile, ExecutionContext.global)
      dao
    }

    @Test
    def testCRUD() {
      val dao = getDAO()

      val f1 = FruhlingEntity("1", "1", "one")
      val f2a = FruhlingEntity("2", "1", "twoA")
      val f2b = FruhlingEntity("2", "1", "twoB")

      sync(dao.createFruhling(f1))

      sync(dao.createFruhling(f2a))
      val f2aX = sync(dao.getFruhling(f2a.key)).get
      Assert.assertEquals("get f2a", f2a, f2aX)

      val nUpdate = sync(dao.updateFruhling(f2b))
      Assert.assertEquals("1 update", 1, nUpdate)
      val f2bX = sync(dao.getFruhling(f2b.key)).get
      Assert.assertEquals("get f2b", f2b, f2bX)

      val nDelete = sync(dao.deleteFruhling(f1.key))
      Assert.assertEquals("1 delete", 1, nDelete)
      val f1X = sync(dao.getFruhling(f1.key))
      Assert.assertSame("no f1", None, f1X)
      val f2X = sync(dao.getFruhling(f2a.key))
      Assert.assertSame("no f2", None, f2X)
    }
  }
}
