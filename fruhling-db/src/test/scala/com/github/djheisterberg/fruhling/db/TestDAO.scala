import scala.concurrent.Future

package com.github.djheisterberg.fruhling.db {

  trait TestDAO extends DAO {
    import profile.simple._

    class FruhlingTable(tag: Tag) extends Table[FruhlingEntity](tag, "FRUHLING") {
      def key = column[String]("KEY_COL", O.PrimaryKey)
      def parentKey = column[String]("PAR_COL")
      def value = column[String]("VAL_COL")

      def * = (key, parentKey, value) <> (FruhlingEntity.tupled, FruhlingEntity.unapply)
    }

    val fruhlingTable = TableQuery[FruhlingTable]

    def createFruhling(fruhling: FruhlingEntity): Future[Unit]

    def getFruhling(key: String): Future[Option[FruhlingEntity]]

    def updateFruhling(fruhling: FruhlingEntity): Future[Int]

    def deleteFruhling(key: String): Future[Int]
  }
}
