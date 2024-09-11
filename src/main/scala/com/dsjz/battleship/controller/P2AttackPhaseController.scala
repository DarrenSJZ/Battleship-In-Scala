package com.dsjz.battleship.controller

import com.dsjz.battleship.MyApp
import com.dsjz.battleship.model.Board.{HitCell, MissCell, ShipCell}
import com.dsjz.battleship.model.Player.{Player, Player1, Player2}
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control.{Alert, Button, ButtonType, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{AnchorPane, GridPane, StackPane, VBox}
import scalafx.scene.media.AudioClip
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.stage.{Modality, Stage, StageStyle}
import scalafxml.core.macros.sfxml

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

@sfxml
class P2AttackPhaseController(private val P2AttackPhase: AnchorPane,
                              private val P2AttackGrid: GridPane,
                              private val P2UltimateButton: Button) {

  var player1: Player1 = _
  var player2: Player2 = _

  private var ultimateMode: Boolean = false
  private var ultimateCoords: List[(Int, Int)] = List()

  // Load sound effects
  private val hitSound: AudioClip = new AudioClip(getClass.getResource("/audio/hit.mp3").toExternalForm)
  private val missSound: AudioClip = new AudioClip(getClass.getResource("/audio/miss.mp3").toExternalForm)
  private val ultimateAudio: AudioClip = new AudioClip(getClass.getResource("/audio/Player2UltimateAudio.mp3").toExternalForm)

  // Initialization method
  def initialize(): Unit = {
    if (player1 == null || player2 == null) {
      throw new IllegalStateException("Player1 and Player2 instances must be set before using the controller.")
    }
    P2AttackPhase.setUserData(this)
    // Disable the Ultimate Button initially
    updateUltimateButtonState()

    // Set up event handlers for grid and buttons
    setupGridHandlers()

    // Update the view to match the board
    updateView()
  }

  // Method to set up click handlers for the grid
  private def setupGridHandlers(): Unit = {
    val buttons = P2AttackGrid.children.asScala
    for (child <- buttons) {
      val button = new Button(child.asInstanceOf[javafx.scene.control.Button])
      button.getStyleClass.add("grid-button")
      val rowIndex = GridPane.getRowIndex(button)
      val columnIndex = GridPane.getColumnIndex(button)
      button.onMouseClicked = _ => handleGridClick(columnIndex, rowIndex)
    }
  }

  // Method to handle clicks on the grid
  private def handleGridClick(x: Int, y: Int): Unit = {

    if (ultimateMode) {
      handleUltimateClick(x, y)
    } else {
      handleBasicClick(x, y)
    }

    // Update ultimate button state after each click
    updateUltimateButtonState()
  }

  // Handle a basic attack click
  private def handleBasicClick(x: Int, y: Int): Unit = {
    val result = player2.basicAttack(x, y)
    player2.updateEnemyShipsSunk(player1) // Update the count of enemy ships sunk
    player1.board.printSunkShips()

    if (result) {
      hitSound.play()
      println(s"${player2.name} HIT ${player1.name}'s ship!")
      updateView()
      if (!checkForWinner()) {
        println("Continue attacking...")
      }
    } else {
      missSound.play()
      println(s"${player2.name} MISSED ${player1.name}'s ship!")
      endRound()
    }
  }

  // Handle a click during ultimate mode
  private def handleUltimateClick(x: Int, y: Int): Unit = {
    ultimateCoords :+= (x, y)
    if (ultimateCoords.size == 1) {  // Assuming Player 2's ultimate needs only 1 coordinate
      disableAllGridButtons()
      player2.useUltimateAbility(ultimateCoords)
      ultimateAudio.play()
      updateView()

      Future {
        Thread.sleep(5000)
      }.onComplete { _ =>
        Platform.runLater {
          if (!checkForWinner()) {
            endRound()
          }
        }
      }
      ultimateMode = false
    }
  }

  // Update the ultimate button state based on player2's status
  private def updateUltimateButtonState(): Unit = {
    player2.checkUltimateConditions()
    if (player2.isUltimateAvailable) {
      P2UltimateButton.setDisable(false)  // Enable the button if available
    } else {
      P2UltimateButton.setDisable(true)  // Disable the button if not available
    }
  }

  // Method to disable all buttons on the grid
  private def disableAllGridButtons(): Unit = {
    P2AttackGrid.children.asScala.foreach {
      case button: javafx.scene.control.Button => button.setDisable(true)
      case pane: javafx.scene.layout.Pane =>
        pane.getChildren.asScala.foreach {
          case nestedButton: javafx.scene.control.Button => nestedButton.setDisable(true)
          case _ => // Ignore non-button elements
        }
      case _ => // Ignore other types of nodes
    }
  }

  // Method to check if a player has won the game
  private def checkForWinner(): Boolean = {
    if (player1.board.allShipsSunk()) {
      showVictoryDialog("Player 2")
      true
    } else {
      false
    }
  }

  // Method to handle the grid map button click
  def P2GridMapButtonHandler(): Unit = {
    showPlayerGrid(player2)
  }

  // Method to handle the Ultimate Button click
  def P2UltimateButtonHandler(): Unit = {
    ultimateMode = true
    ultimateCoords = List()
    println("Player 2 activated Carpet Bomb mode! Select a point on the grid.")
  }

  // Method to display the player's grid
  private def showPlayerGrid(player: Player): Unit = {
    val gridPane = new GridPane()
    val letters = "ABCDEFGHIJ"
    for (i <- 0 until 10) {
      gridPane.add(new Label(letters(i).toString), 0, i + 1)
      gridPane.add(new Label((i + 1).toString), i + 1, 0)
    }

    for (x <- 0 until 10) {
      for (y <- 0 until 10) {
        val cellStatus = player.board.checkCell(x, y)
        val stackPane = new StackPane()
        val background = new Rectangle {
          width = 63.0
          height = 63.0
          fill = Color.Transparent
          stroke = Color.Black
          strokeWidth = 1
        }
        stackPane.children.add(background)

        cellStatus match {
          case Some(HitCell) =>
            val hitMarker = new Rectangle {
              width = 63.0
              height = 63.0
              fill = Color.Red
              stroke = Color.White
              strokeWidth = 1
            }
            stackPane.children.add(hitMarker)

          case Some(MissCell) =>
            val missMarker = new Rectangle {
              width = 63.0
              height = 63.0
              fill = Color.Gray
              stroke = Color.Black
              strokeWidth = 1
            }
            stackPane.children.add(missMarker)

          case Some(ShipCell(_)) =>
            val shipBackground = new Rectangle {
              width = 63.0
              height = 63.0
              fill = Color.Blue
              stroke = Color.White
              strokeWidth = 1
            }
            stackPane.children.add(shipBackground)

          case None => // No specific content for empty cells, background remains transparent
        }

        gridPane.add(stackPane, x + 1, y + 1)
      }
    }

    player.board.ships.values.foreach { ship =>
      val startPos = ship.positions.head
      val shipImage = new Image(if (ship.isHorizontal) {
        s"/images/game/${ship.name}_horizontal.png"
      } else {
        s"/images/game/${ship.name}_vertical.png"
      })
      val imageView = new ImageView(shipImage) {
        fitWidth = if (ship.isHorizontal) 63.0 * ship.length else 63.0
        fitHeight = if (!ship.isHorizontal) 63.0 * ship.length else 63.0
        preserveRatio = false
      }
      val shipPane = new StackPane()
      shipPane.children.add(imageView)

      gridPane.add(shipPane, startPos._1 + 1, startPos._2 + 1)
      if (ship.isHorizontal) {
        GridPane.setColumnSpan(shipPane, ship.length)
      } else {
        GridPane.setRowSpan(shipPane, ship.length)
      }
    }

    val dialogContent = new VBox {
      children.add(gridPane)
      alignment = scalafx.geometry.Pos.Center
    }

    val dialogStage = new Stage() {
      title = "Your Grid"
      initModality(Modality.APPLICATION_MODAL)
      initStyle(StageStyle.UTILITY)
      scene = new Scene(dialogContent, 660, 680)
      resizable = false
    }

    dialogStage.showAndWait()
  }

  // Method to update the view after an action
  private def updateView(): Unit = {
    for (x <- 0 until 10) {
      for (y <- 0 until 10) {
        val cellStatus = player1.board.checkCell(x, y)
        val marker: Option[Rectangle] = cellStatus match {
          case Some(HitCell) =>
            Some(new Rectangle {
              width = 63.0
              height = 63.0
              fill = Color.Red
              opacity = 0.6
            })

          case Some(MissCell) =>
            Some(new Rectangle {
              width = 63.0
              height = 63.0
              fill = Color.Gray
              opacity = 0.6
            })

          case _ => None
        }

        marker.foreach { m =>
          val existingPaneOption = P2AttackGrid.children.asScala.collectFirst {
            case pane: StackPane if GridPane.getRowIndex(pane) == y && GridPane.getColumnIndex(pane) == x =>
              pane
          }

          if (existingPaneOption.isDefined) {
            existingPaneOption.get.getChildren.add(m)
          } else {
            val stackPane = new StackPane()
            stackPane.getChildren.add(m)
            P2AttackGrid.add(stackPane, x, y)
          }
        }
      }
    }
  }

  def handleForfeit(): Unit = {
    val alert = new Alert(Alert.AlertType.Confirmation) {
      initOwner(MyApp.stage)
      title = "Forfeit"
      headerText = "Are you sure you want to forfeit?"
      contentText = "If you forfeit, Player 1 will win the match."
    }

    // Show the alert and get the result
    val result = alert.showAndWait()

    // Check if the result is OK
    result match {
      case Some(ButtonType.OK) =>
        showVictoryDialog("Player 1")
      case _ =>
      // Do nothing if the user cancels
    }
  }

  // Method to show the victory dialog
  private def showVictoryDialog(winner: String): Unit = {
    player1.save() match {
      case Success(_) => println(s"${player1.getClass.getSimpleName} stats saved successfully!")
      case Failure(e) => println(s"Failed to save ${player1.getClass.getSimpleName} stats: ${e.getMessage}")
    }

    player2.save() match {
      case Success(_) => println(s"${player2.getClass.getSimpleName} stats saved successfully!")
      case Failure(e) => println(s"Failed to save ${player2.getClass.getSimpleName} stats: ${e.getMessage}")
    }

    val alert = new Alert(Alert.AlertType.Information) {
      initOwner(MyApp.stage)
      title = "Game Ended"
      headerText = "Victory!"
      contentText = s"$winner wins!"
    }
    resetGame()
    alert.showAndWait()
    MyApp.showWelcome()
  }

  // Method to reset the game after it ends
  private def resetGame(): Unit = {
    player1.resetPlayer()
    player2.resetPlayer()
  }

  // Method to handle the end of a round
  private def endRound(): Unit = {
    player1.surviveRound()
    player2.surviveRound()

    updateUltimateButtonState()

    MyApp.showP1AttackPhase(player1, player2)
  }
}
