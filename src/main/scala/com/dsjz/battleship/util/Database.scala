package com.dsjz.battleship.util

import com.dsjz.battleship.model.Player.{Player1, Player2}
import scalikejdbc._

trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
  val dbURL = "jdbc:derby:battleshipDB;create=true;"
  Class.forName(derbyDriverClassname)
  ConnectionPool.singleton(dbURL, "dsjz", "battleship") //username / password

  implicit val session: AutoSession.type = AutoSession
}

object Database extends Database {
  def setupDB() = {
    if (!hasDBInitialize) {
      Player1.initializeTable()
      Player2.initializeTable()
    }

    def hasDBInitialize: Boolean = {
      val player1TableExists = DB getTable "player1" match {
        case Some(_) => true
        case None => false
      }

      val player2TableExists = DB getTable "player2" match {
        case Some(_) => true
        case None => false
      }

      player1TableExists && player2TableExists
    }
  }
}
