package com.dsjz.battleship.controller

import com.dsjz.battleship.MyApp
import com.dsjz.battleship.model.Player.{Player1, Player2}
import scalafx.scene.control.TextInputDialog
import scalafxml.core.macros.sfxml

@sfxml
class WelcomeController {

  def PlayButtonHandler(): Unit = {
    // Prompt for Player 1's name
    val dialog1 = new TextInputDialog(defaultValue = "") {
      initOwner(MyApp.stage)
      title = "Player 1 Name"
      headerText = "Enter the name for Player 1"
      contentText = "Name:"
    }
    val result1 = dialog1.showAndWait()
    val player1Name = result1.getOrElse("Player 1") // Default to "Player 1" if no name is provided

    // Prompt for Player 2's name
    val dialog2 = new TextInputDialog(defaultValue = "") {
      initOwner(MyApp.stage)
      title = "Player 2 Name"
      headerText = "Enter the name for Player 2"
      contentText = "Name:"
    }
    val result2 = dialog2.showAndWait()
    val player2Name = result2.getOrElse("Player 2") // Default to "Player 2" if no name is provided

    // Create new player instances with the provided names
    val player1 = new Player1(player1Name)
    val player2 = new Player2(player2Name)

    // Set the target boards to each player
    player1.setTargetBoard(player2.board)
    player2.setTargetBoard(player1.board)

    // Show the ship placement screen for Player 1
    MyApp.showShipPlacementP1(player1, player2)
  }

  def showPlayerStatsButtonHandler(): Unit = {
    MyApp.showPlayerStats()
  }

  def showAboutGameButtonHandler(): Unit = {
    MyApp.showAboutGameDialog()
  }

  def ExitButtonHandler(): Unit = {
    sys.exit(0)
  }
}
