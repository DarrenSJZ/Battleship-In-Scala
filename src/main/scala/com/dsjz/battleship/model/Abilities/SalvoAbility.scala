package com.dsjz.battleship.model.Abilities

import com.dsjz.battleship.model.Board.Board
import com.dsjz.battleship.model.Player.Player

class SalvoAbility(player: Player) extends UltimateAbility {

  // Method to validate the coordinates
  def isValidSalvo(coords: List[(Int, Int)], board: Board): Boolean = {
    if (coords.size != 2) return false

    val (startX, startY) = coords.head
    val (endX, endY) = coords.last

    // Check if both coordinates are in the same row or column and on opposite edges
    if ((startX == endX && (startY == 0 && endY == board.grid.head.length - 1 || startY == board.grid.head.length - 1 && endY == 0)) ||
      (startY == endY && (startX == 0 && endX == board.grid.length - 1 || startX == board.grid.length - 1 && endX == 0))) {
      true
    } else {
      false
    }
  }

  override def use(coords: List[(Int, Int)], board: Board): Unit = {
    if (!isValidSalvo(coords, board)) {
      println("Invalid Salvo. It must be on the edges of the grid and in the same row or column.")
      return
    }

    println("Using Salvo!")
    val (startX, startY) = coords.head

    if (startX == coords.last._1) {
      // Same column - clear the entire row
      for (y <- board.grid.head.indices) {
        player.basicAttack(startX, y)
      }
    } else {
      // Same row - clear the entire column
      for (x <- board.grid.indices) {
        player.basicAttack(x, startY)
      }
    }
  }
}
