package com.dsjz.battleship.model.Player

import com.dsjz.battleship.model.Abilities.UltimateAbility
import com.dsjz.battleship.model.Board.{Board, PlacementSuccess}
import com.dsjz.battleship.model.Ship.Ship
import scalafx.beans.property.{ObjectProperty, StringProperty}

abstract class Player(val name: String) {

  // Player Database ID
  var id: ObjectProperty[Int]

  // Player's board
  val board: Board

  // The target board for the player to attack
  def targetBoard: Board

  // Abstract method to provide the player's ships
  def ships: List[Ship]

  // The ultimate ability for the player
  def ultimateAbility: UltimateAbility

  // Statistics
  var shotsFired: Int = 0
  var shotsHit: Int = 0
  var shotsMissed: Int = 0
  var ultimateAbilitiesUsed: Int = 0
  var roundsSurvived: Int = 0
  var enemyShipsSunk: Int = 0
  var consecutiveMisses: Int = 0  // Counter for consecutive misses

  // Flag to track if ultimate is available
  var isUltimateAvailable: Boolean = false

  // Flags to ensure each condition only triggers once
  private var hasHitFive: Boolean = false
  private var hasMissedFive: Boolean = false
  private var hasFiredTwenty: Boolean = false
  private var hasSurvivedFiveRounds: Boolean = false
  private var hasSunkThreeShips: Boolean = false
  private var hasSunkThreeEnemyShips: Boolean = false
  private var hasConsecutiveMissedFive: Boolean = false  // New flag for consecutive misses

  // Method to update the number of enemy ships sunk
  def updateEnemyShipsSunk(opponent: Player): Unit = {
    enemyShipsSunk = opponent.ships.count(_.isSunk)
  }

  // Method to check if ultimate conditions are met
  def checkUltimateConditions(): Unit = {
    if (!hasHitFive && shotsHit >= 5) {
      isUltimateAvailable = true
      hasHitFive = true
    } else if (!hasFiredTwenty && shotsFired >= 20) {
      isUltimateAvailable = true
      hasFiredTwenty = true
    } else if (!hasSurvivedFiveRounds && roundsSurvived >= 5) {
      isUltimateAvailable = true
      hasSurvivedFiveRounds = true
    } else if (!hasSunkThreeShips && ships.count(_.isSunk) == 3) {
      isUltimateAvailable = true
      hasSunkThreeShips = true
    } else if (!hasSunkThreeEnemyShips && enemyShipsSunk == 3) {
      isUltimateAvailable = true
      hasSunkThreeEnemyShips = true
    } else if (!hasConsecutiveMissedFive && consecutiveMisses >= 5) {
      isUltimateAvailable = true
      hasConsecutiveMissedFive = true
    }
  }

  // Method for a basic attack
  final def basicAttack(targetX: Int, targetY: Int): Boolean = {
    shotsFired += 1
    val hit = targetBoard.checkCell(targetX, targetY) match {
      case Some(_) =>
        if (targetBoard.registerHit(targetX, targetY)) {
          shotsHit += 1
          consecutiveMisses = 0  // Reset consecutive misses on a hit
          true
        } else {
          shotsMissed += 1
          consecutiveMisses += 1
          false
        }
      case None =>
        targetBoard.registerMiss(targetX, targetY)
        shotsMissed += 1
        consecutiveMisses += 1
        false
    }
    checkUltimateConditions()  // Check conditions after every attack
    hit
  }

  // Method to use the ultimate ability
  final def useUltimateAbility(coords: List[(Int, Int)]): Unit = {
    ultimateAbilitiesUsed += 1
    ultimateAbility.use(coords, targetBoard)
    isUltimateAvailable = false  // Disable ultimate after use
  }

  // Method to calculate the player's score
  final def calculateScore(): Int = {
    shotsHit * 10 - shotsMissed * 2 + (100 * ultimateAbilitiesUsed)
  }

  // Method to place a ship on the player's board
  final def placeShip(x: Int, y: Int, ship: Ship, horizontal: Boolean): Boolean = {
    board.placeShip(x, y, ship, horizontal) match {
      case PlacementSuccess =>
        true
      case _ =>
        false
    }
  }

  // Method to increment the rounds survived
  final def surviveRound(): Unit = {
    roundsSurvived += 1
    checkUltimateConditions()  // Check conditions after each round
  }

  // Method to reset the player's state
  final def resetPlayer(): Unit = {
    shotsFired = 0
    shotsHit = 0
    shotsMissed = 0
    ultimateAbilitiesUsed = 0
    roundsSurvived = 0
    enemyShipsSunk = 0
    consecutiveMisses = 0
    isUltimateAvailable = false

    // Reset the flags
    hasHitFive = false
    hasMissedFive = false
    hasFiredTwenty = false
    hasSurvivedFiveRounds = false
    hasSunkThreeShips = false
    hasSunkThreeEnemyShips = false
    hasConsecutiveMissedFive = false  // Reset consecutive misses flag

    board.resetBoard()  // Reset the player's board as well
  }
}
