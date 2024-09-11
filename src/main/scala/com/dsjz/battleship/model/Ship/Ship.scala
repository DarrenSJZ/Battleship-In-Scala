package com.dsjz.battleship.model.Ship

class Ship(val name: String, val length: Int) {
  private var hitPoints: Int = length
  private var _positions: List[(Int, Int)] = List() // List of coordinates the ship occupies

  // Get the positions the ship occupies
  def positions: List[(Int, Int)] = _positions

  // Set the positions for the ship
  def setPositions(startX: Int, startY: Int, horizontal: Boolean): Unit = {
    _positions = if (horizontal) {
      (0 until length).map(i => (startX + i, startY)).toList
    } else {
      (0 until length).map(i => (startX, startY + i)).toList
    }
  }

  // Determine if the ship is placed horizontally
  def isHorizontal: Boolean = _positions.map(_._1).distinct.size > 1

  // Take a hit, reducing hit points
  def takeHit(): Unit = {
    if (hitPoints > 0) {
      hitPoints -= 1
    }
  }

  // Check if the ship is sunk
  def isSunk: Boolean = hitPoints == 0

  // Reset the ship's status
  def resetStatus(): Unit = {
    hitPoints = length
    _positions = List() // Clear positions when resetting the ship
  }

  override def toString: String = s"$name(length: $length, hitPoints: $hitPoints, positions: $positions)"
}

// Subclasses representing different types of ships
class Carrier extends Ship("Carrier", 5)
class Battleship extends Ship("Battleship", 4)
class Cruiser extends Ship("Cruiser", 3)
class Submarine extends Ship("Submarine", 3)
class Destroyer extends Ship("Destroyer", 2)
