import scala.slick.driver.JdbcProfile

package com.github.djheisterberg.fruhling.db {

  trait DAO {
    protected val profile: JdbcProfile
  }
}
