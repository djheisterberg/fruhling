import org.hsqldb.jdbc.JDBCDriver

package com.github.djheisterberg.fruhling.db {

  class SlickProfilerHSQLCheck extends SlickProfilerCheck {
    override def driver = new JDBCDriver
    override def url = "jdbc:hsqldb:mem:profiler-hsql"
    override def expectedProductName = SlickProfiler.HSQL
  }
}
