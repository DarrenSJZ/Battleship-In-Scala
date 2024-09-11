package com.dsjz.battleship.model.Player

import scalafx.beans.property._

case class PlayerData(
                       id: Int,
                       name: String,
                       player1ShotsFired: Int,
                       player1ShotsHit: Int,
                       player1ShotsMissed: Int,
                       player1UltimateAbilitiesUsed: Int,
                       player1RoundsSurvived: Int,
                       player1EnemyShipsSunk: Int,
                       player2ShotsFired: Int,
                       player2ShotsHit: Int,
                       player2ShotsMissed: Int,
                       player2UltimateAbilitiesUsed: Int,
                       player2RoundsSurvived: Int,
                       player2EnemyShipsSunk: Int
                     ) {
  val nameProperty = new StringProperty(this, "name", name)

  // Player 1 properties
  val player1ShotsFiredProperty = new IntegerProperty(this, "player1ShotsFired", player1ShotsFired)
  val player1ShotsHitProperty = new IntegerProperty(this, "player1ShotsHit", player1ShotsHit)
  val player1ShotsMissedProperty = new IntegerProperty(this, "player1ShotsMissed", player1ShotsMissed)
  val player1UltimateAbilitiesUsedProperty = new IntegerProperty(this, "player1UltimateAbilitiesUsed", player1UltimateAbilitiesUsed)
  val player1RoundsSurvivedProperty = new IntegerProperty(this, "player1RoundsSurvived", player1RoundsSurvived)
  val player1EnemyShipsSunkProperty = new IntegerProperty(this, "player1EnemyShipsSunk", player1EnemyShipsSunk)

  // Player 2 properties
  val player2ShotsFiredProperty = new IntegerProperty(this, "player2ShotsFired", player2ShotsFired)
  val player2ShotsHitProperty = new IntegerProperty(this, "player2ShotsHit", player2ShotsHit)
  val player2ShotsMissedProperty = new IntegerProperty(this, "player2ShotsMissed", player2ShotsMissed)
  val player2UltimateAbilitiesUsedProperty = new IntegerProperty(this, "player2UltimateAbilitiesUsed", player2UltimateAbilitiesUsed)
  val player2RoundsSurvivedProperty = new IntegerProperty(this, "player2RoundsSurvived", player2RoundsSurvived)
  val player2EnemyShipsSunkProperty = new IntegerProperty(this, "player2EnemyShipsSunk", player2EnemyShipsSunk)
}
