import javax.sql.DataSource

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.slick.driver.JdbcProfile

package com.github.djheisterberg.fruhling.db {

  class TestDAOImpl(dataSource: DataSource, override protected val profile: JdbcProfile, implicit private val executionContext: ExecutionContext) extends TestDAO {

    import profile.backend.Session
    import profile.simple._

    override protected val database = Database.forDataSource(dataSource)

    private def _byId(key: Column[String]) = fruhlingTable.filter(_.key === key)
    private val byId = Compiled(_byId _)

    override def createFruhling(fruhling: FruhlingEntity): Future[Unit] =
      futureInTransaction { implicit session => fruhlingTable += fruhling }

    override def getFruhling(key: String): Future[Option[FruhlingEntity]] =
      futureInSession { implicit session => byId(key).firstOption }

    override def updateFruhling(fruhling: FruhlingEntity): Future[Int] =
      futureInTransaction { implicit session => byId(fruhling.key).update(fruhling) }

    override def deleteFruhling(key: String): Future[Int] =
      futureInTransaction { implicit session => byId(key).delete }
  }
}
