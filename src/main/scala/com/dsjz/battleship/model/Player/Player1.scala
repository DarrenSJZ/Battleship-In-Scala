package com.dsjz.battleship.model.Player

import com.dsjz.battleship.model.Abilities.SalvoAbility
import com.dsjz.battleship.model.Board.Board
import com.dsjz.battleship.model.Ship._
import scalafx.beans.property.ObjectProperty
import scalikejdbc._
import com.dsjz.battleship.util.Database
import scala.util.{Try, Success, Failure}

class Player1(name: String) extends Player(name) {

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

  override val ultimateAbility: SalvoAbility = new SalvoAbility(this)

  def save(): Try[Unit] = {
    if (id.value == 0) {
      Try(DB autoCommit { implicit session =>
        val generatedId = sql"""
          insert into player1 (name, shotsFired, shotsHit, shotsMissed, ultimateAbilitiesUsed,
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
        update player1 set
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
        delete from player1 where id = ${id.value}
      """.update.apply()
    })
  }
}

object Player1 extends Database {
  def initializeTable(): Unit = {
    DB autoCommit { implicit session =>
      sql"""
        create table player1 (
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
      sql"select * from player1".map(rs => {
        PlayerData(
          id = rs.int("id"),
          name = rs.string("name"),
          player1ShotsFired = rs.int("shotsFired"),
          player1ShotsHit = rs.int("shotsHit"),
          player1ShotsMissed = rs.int("shotsMissed"),
          player1UltimateAbilitiesUsed = rs.int("ultimateAbilitiesUsed"),
          player1RoundsSurvived = rs.int("roundsSurvived"),
          player1EnemyShipsSunk = rs.int("enemyShipsSunk"),
          player2ShotsFired = 0, // Initialize Player 2 stats as 0 for now
          player2ShotsHit = 0,
          player2ShotsMissed = 0,
          player2UltimateAbilitiesUsed = 0,
          player2RoundsSurvived = 0,
          player2EnemyShipsSunk = 0
        )
      }).list.apply()
    }
  }
}