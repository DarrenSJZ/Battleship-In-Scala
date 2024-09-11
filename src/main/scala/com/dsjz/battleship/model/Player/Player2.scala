package com.dsjz.battleship.model.Player

import com.dsjz.battleship.model.Abilities.CarpetBombAbility
import com.dsjz.battleship.model.Board.Board
import com.dsjz.battleship.model.Ship._
import scalafx.beans.property.ObjectProperty
import scalikejdbc._
import com.dsjz.battleship.util.Database
import scala.util.{Try, Success, Failure}

class Player2(name: String) extends Player(name) {

  override var id: ObjectProperty[Int] = ObjectProperty(0)
  override val board: Board = new Board
  private var _targetBoard: Board = _

  def setTargetBoard(board: Board): Unit = {
    _targetBoard = board
  }

  override def targetBoard: Board = _targetBoard
  override val ships: List[Ship] = List(
    new Carrier(),
    new Battleship(),
    new Cruiser(),
    new Submarine(),
    new Destroyer()
  )

  override val ultimateAbility: CarpetBombAbility = new CarpetBombAbility(this)

  def save(): Try[Unit] = {
    if (id.value == 0) {
      Try(DB autoCommit { implicit session =>
        val generatedId = sql"""
          insert into player2 (name, shotsFired, shotsHit, shotsMissed, ultimateAbilitiesUsed,
          roundsSurvived, enemyShipsSunk)
          values (${name}, ${shotsFired}, ${shotsHit}, ${shotsMissed}, ${ultimateAbilitiesUsed},
          ${roundsSurvived}, ${enemyShipsSunk})
        """.updateAndReturnGeneratedKey().apply()

        id.value = generatedId.toInt
      })
    } else {
      update()
    }
  }

  def update(): Try[Unit] = {
    Try(DB autoCommit { implicit session =>
      sql"""
        update player2 set
          name = ${name},
          shotsFired = ${shotsFired},
          shotsHit = ${shotsHit},
          shotsMissed = ${shotsMissed},
          ultimateAbilitiesUsed = ${ultimateAbilitiesUsed},
          roundsSurvived = ${roundsSurvived},
          enemyShipsSunk = ${enemyShipsSunk}
        where id = ${id.value}
      """.update.apply()
    })
  }

  def delete(): Try[Int] = {
    Try(DB autoCommit { implicit session =>
      sql"""
        delete from player2 where id = ${id.value}
      """.update.apply()
    })
  }
}

object Player2 extends Database {
  def initializeTable(): Unit = {
    DB autoCommit { implicit session =>
      sql"""
        create table player2 (
          id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
          name varchar(255),
          shotsFired int,
          shotsHit int,
          shotsMissed int,
          ultimateAbilitiesUsed int,
          roundsSurvived int,
          enemyShipsSunk int
        )
      """.execute.apply()
    }
  }

  def getAllPlayers: List[PlayerData] = {
    DB readOnly { implicit session =>
      sql"select * from player2".map(rs => {
        PlayerData(
          id = rs.int("id"),
          name = rs.string("name"),
          player1ShotsFired = 0, // Initialize Player 1 stats as 0 for now
          player1ShotsHit = 0,
          player1ShotsMissed = 0,
          player1UltimateAbilitiesUsed = 0,
          player1RoundsSurvived = 0,
          player1EnemyShipsSunk = 0,
          player2ShotsFired = rs.int("shotsFired"),
          player2ShotsHit = rs.int("shotsHit"),
          player2ShotsMissed = rs.int("shotsMissed"),
          player2UltimateAbilitiesUsed = rs.int("ultimateAbilitiesUsed"),
          player2RoundsSurvived = rs.int("roundsSurvived"),
          player2EnemyShipsSunk = rs.int("enemyShipsSunk")
        )
      }).list.apply()
    }
  }
}
