import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.slick.driver.JdbcProfile

package com.github.djheisterberg.fruhling.db {

  trait DAO {
    protected val profile: JdbcProfile

    import profile.backend.Database
    import profile.backend.Session

    protected val database: Database

    protected def futureInSession[A](op: Session => A)(implicit xc: ExecutionContext): Future[A] = Future(database withSession op)

    protected def futureInTransaction[A](op: Session => A)(implicit xc: ExecutionContext): Future[A] = Future(database withTransaction op)
  }
}
