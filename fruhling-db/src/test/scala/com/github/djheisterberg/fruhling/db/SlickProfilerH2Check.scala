import org.h2.Driver

package com.github.djheisterberg.fruhling.db {

  class SlickProfilerH2Check extends SlickProfilerCheck {
    override def driver = new Driver
    override def url = "jdbc:h2:mem:"
    override def expectedProductName = SlickProfiler.H2
  }
}
