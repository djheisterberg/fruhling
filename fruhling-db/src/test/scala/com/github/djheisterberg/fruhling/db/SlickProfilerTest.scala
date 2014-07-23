import scala.slick.driver.JdbcDriver

import org.junit.Assert
import org.junit.Test

package com.github.djheisterberg.fruhling {

  import test.ConfigTestBase

  package db {

    class SlickProfilerTest extends ConfigTestBase {

      @Test
      def testProfile() {
        val driver = SlickProfiler(ConfigTestBase.dataSource)
        Assert.assertNotNull(driver)
      }

      @Test
      def testKnownProfiles() {
        for (profile <- SlickProfiler.KNOWN_PROFILES) {
          val driver = SlickProfiler(profile)
          Assert.assertNotNull(profile, driver)
          Assert.assertNotSame(profile, JdbcDriver, driver)
        }
      }

      @Test
      def testDefault() {
        val profile = "default"
        val driver = SlickProfiler(profile)
        Assert.assertNotNull(profile, driver)
        Assert.assertSame(profile, JdbcDriver, driver)
      }
    }
  }
}
