package com.dsjz.battleship.model.Abilities

import com.dsjz.battleship.model.Board.Board

trait UltimateAbility {
  def use(coords: List[(Int, Int)], board: Board): Unit
}