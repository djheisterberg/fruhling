import org.apache.derby.jdbc.EmbeddedDriver

package com.github.djheisterberg.fruhling.db {

  class SlickProfilerDerbyCheck extends SlickProfilerCheck {
    override def driver = new EmbeddedDriver
    override def url = "jdbc:derby:profiler-derby;create=true"
    override def expectedProductName = SlickProfiler.DERBY
  }
}
