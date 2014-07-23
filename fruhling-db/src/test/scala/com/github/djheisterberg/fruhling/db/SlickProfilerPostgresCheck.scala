import org.postgresql.Driver

package com.github.djheisterberg.fruhling.db {

  class SlickProfilerPostgresCheck extends SlickProfilerCheck {
    override def driver = new Driver
    override def url = "jdbc:postgresql://localhost:5433/postgres"
    override def username = "postgres"
    override def password = "7734hole"
    override def expectedProductName = SlickProfiler.POSTGRES
  }
}
