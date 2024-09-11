package com.dsjz.battleship.model.Board

import com.dsjz.battleship.model.Ship.Ship
import scala.collection.mutable

sealed trait CellContent
case class ShipCell(shipId: Int) extends CellContent
case object HitCell extends CellContent
case object MissCell extends CellContent

sealed trait PlacementResult
case object PlacementSuccess extends PlacementResult
case object OutOfBounds extends PlacementResult
case object Overlap extends PlacementResult

class Board {
  // Grid representing the board, with Option[CellContent] to store cell contents
  val grid: Array[Array[Option[CellContent]]] = Array.fill(10, 10)(None)

  // Map to track ship IDs and their corresponding Ship objects
  val ships: mutable.Map[Int, Ship] = mutable.Map()

  // Initialize the grid
  initializeGrid()

  protected def initializeGrid(): Unit = {
    for (i <- grid.indices; j <- grid(i).indices) {
      grid(i)(j) = None
    }
  }

  def placeShip(x: Int, y: Int, ship: Ship, horizontal: Boolean): PlacementResult = {
    val endX = if (horizontal) x + ship.length - 1 else x
    val endY = if (horizontal) y else y + ship.length - 1

    // Check if the entire ship is within bounds
    if (!isWithinBounds(x, y) || !isWithinBounds(endX, endY)) return OutOfBounds

    // Check if any of the cells are already occupied (overlap)
    if (checkOverlap(x, y, ship.length, horizontal)) return Overlap

    // Assign a unique ID to the ship and place it on the grid
    val shipId = ships.size + 1

    // Set the ship's positions
    ship.setPositions(x, y, horizontal)

    for (i <- 0 until ship.length) {
      val placeX = if (horizontal) x + i else x
      val placeY = if (horizontal) y else y + i
      grid(placeX)(placeY) = Some(ShipCell(shipId))
    }
    ships(shipId) = ship
    PlacementSuccess
  }
  // Method to print the names of the ships that have been sunk
  def printSunkShips(): Unit = {
    val sunkShips = ships.values.filter(_.isSunk)
    if (sunkShips.nonEmpty) {
      println("Ships that have been sunk:")
      sunkShips.foreach(ship => println(s"- ${ship.name}"))
    } else {
      println("No ships have been sunk yet.")
    }
  }

  // Method to check if any of the cells in the ship's path are occupied
  private def checkOverlap(x: Int, y: Int, length: Int, horizontal: Boolean): Boolean = {
    for (i <- 0 until length) {
      val checkX = if (horizontal) x + i else x
      val checkY = if (horizontal) y else y + i
      if (grid(checkX)(checkY).isDefined) return true
    }
    false
  }

  // Reset the board
  def resetBoard(): Unit = {
    initializeGrid()

    // Reset the status of each ship
    ships.values.foreach(_.resetStatus())

    ships.clear()
  }

  // Register a hit on the grid
  def registerHit(x: Int, y: Int): Boolean = {
    grid(x)(y) match {
      case Some(ShipCell(shipId)) =>
        val ship = ships(shipId)
        ship.takeHit()
        grid(x)(y) = Some(HitCell)
        println(s"Ship hit at (${indexToCoordinate(x, y)})")
        true
      case _ =>
        println(s"No ship at (${indexToCoordinate(x, y)})")
        false
    }
  }

  // Register a miss on the grid
  def registerMiss(x: Int, y: Int): Boolean = {
    if (isWithinBounds(x, y) && grid(x)(y).isEmpty) {
      grid(x)(y) = Some(MissCell)
      println(s"Miss at (${indexToCoordinate(x, y)})")
      true
    } else {
      false
    }
  }

  // Check the state of a cell
  def checkCell(x: Int, y: Int): Option[CellContent] = grid(x)(y)

  // Check if the current cell is empty
  def isEmpty(x: Int, y: Int): Boolean = grid(x)(y).isEmpty

  // Check if coordinates are within board bounds
  def isWithinBounds(x: Int, y: Int): Boolean = {
    x >= 0 && x < 10 && y >= 0 && y < 10
  }

  // Check if all ships have been sunk
  def allShipsSunk(): Boolean = ships.values.forall(_.isSunk)

  // Convert grid index to board coordinate
  private def indexToCoordinate(x: Int, y: Int): String = {
    val letters = "ABCDEFGHIJ"
    val letter = if (y >= 0 && y < letters.length) letters(y).toString else "?"
    val number = (x + 1).toString
    letter + number
  }

  // Provide a string representation of the board
  override def toString: String = {
    // Process the grid by rows (y-coordinate first)
    grid.transpose.indices.map { y =>
      grid.indices.map { x =>
        grid(x)(y) match {
          case None => "."
          case Some(ShipCell(_)) => "S"
          case Some(HitCell) => "X"
          case Some(MissCell) => "O"
          case _ => "?"
        }
      }.mkString(" ")
    }.mkString("\n")
  }
}
