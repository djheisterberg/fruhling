import com.mysql.jdbc.Driver

package com.github.djheisterberg.fruhling.db {

  class SlickProfilerMySQLCheck extends SlickProfilerCheck {
    override def driver = new Driver
    override def url = "jdbc:mysql://localhost:3306/mysql"
    override def username = "root"
    override def password = "7734hole"
    override def expectedProductName = SlickProfiler.MYSQL
  }
}
