package com.dsjz.battleship.model.Abilities

import com.dsjz.battleship.model.Board.Board
import com.dsjz.battleship.model.Player.Player

class CarpetBombAbility(player: Player) extends UltimateAbility {
  override def use(coords: List[(Int, Int)], board: Board): Unit = {
    println("Using Carpet Bomb!")

    // Ensure only one coordinate is provided
    val (centerX, centerY) = coords.head

    // Define the range for the 3x3 grid centered on (centerX, centerY)
    val startX = math.max(0, centerX - 1)
    val endX = math.min(board.grid.length - 1, centerX + 1)
    val startY = math.max(0, centerY - 1)
    val endY = math.min(board.grid.head.length - 1, centerY + 1)

    // Iterate over the 3x3 grid and perform basic attacks on each cell
    for (x <- startX to endX) {
      for (y <- startY to endY) {
        player.basicAttack(x, y)
      }
    }
  }
}
