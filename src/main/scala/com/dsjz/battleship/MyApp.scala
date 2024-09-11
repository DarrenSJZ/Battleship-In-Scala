package com.dsjz.battleship

import com.dsjz.battleship.controller._
import com.dsjz.battleship.model.Player.{Player1, Player2}
import com.dsjz.battleship.util.Database
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

object MyApp extends JFXApp {
  Database.setupDB()

  // Load the root layout
  val rootResource = getClass.getResource("view/RootLayout.fxml")
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load()
  val roots = loader.getRoot[jfxs.layout.BorderPane]

  val controller = loader.getController[RootLayoutController#Controller]
  controller.initialize()

  stage = new PrimaryStage {
    title = "Battleship"
    icons += new Image(getClass.getResourceAsStream("/images/application/BattleshipLogo.png"))
    scene = new Scene(1280, 720) {
      root = roots
    }
    resizable = false
  }

  // Show Welcome screen
  def showWelcome(): Unit = {
    val resource = getClass.getResource("view/Welcome.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(root)
  }

  // Show Ship Placement for Player 1
  def showShipPlacementP1(player1: Player1, player2: Player2): Unit = {
    val resource = getClass.getResource("view/ShipPlacementP1.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(root)

    // Retrieve the controller and set the player
    val controller = loader.getController[ShipPlacementP1Controller#Controller]
    controller.player1 = player1
    controller.player2 = player2
    controller.initialize()
  }

  // Show Ship Placement for Player 2
  def showShipPlacementP2(player1: Player1, player2: Player2): Unit = {
    val resource = getClass.getResource("view/ShipPlacementP2.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(root)

    // Retrieve the controller and set the player
    val controller = loader.getController[ShipPlacementP2Controller#Controller]
    controller.player1 = player1
    controller.player2 = player2
    controller.initialize()
  }

  // Show Player 1 Attack Phase
  def showP1AttackPhase(player1: Player1, player2: Player2): Unit = {
    val resource = getClass.getResource("view/P1AttackPhase.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(root)

    // Retrieve the controller and initialize the player
    val controller = loader.getController[P1AttackPhaseController#Controller]
    controller.player1 = player1
    controller.player2 = player2
    controller.initialize()
  }

  // Show Player 2 Attack Phase
  def showP2AttackPhase(player1: Player1, player2: Player2): Unit = {
    val resource = getClass.getResource("view/P2AttackPhase.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(root)

    // Retrieve the controller and initialize the player
    val controller = loader.getController[P2AttackPhaseController#Controller]
    controller.player1 = player1
    controller.player2 = player2
    controller.initialize()
  }

  def showPlayerStats(): Unit = {
    val resource = getClass.getResource("view/PlayerStats.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(root)

    val controller = loader.getController[PlayerStatsController#Controller]
    controller.initialize
  }

  import javafx.fxml.FXMLLoader
  import javafx.scene.Scene
  import javafx.scene.layout.AnchorPane
  import javafx.stage.{Modality, Stage, StageStyle}

  def showAboutGameDialog(): Unit = {
    // Load the FXML with a proper resolver
    val resource = getClass.getResource("/com/dsjz/battleship/view/AboutGame.fxml")

    val loader = new FXMLLoader(resource)
    // Load the JavaFX AnchorPane directly
    val root = loader.load().asInstanceOf[AnchorPane]

    // Create a new Stage (dialog)
    val dialogStage = new Stage() {
      initModality(Modality.APPLICATION_MODAL) // Make it a modal dialog
      initStyle(StageStyle.UTILITY) // Set the stage style to utility
      setTitle("About Game")
      setScene(new Scene(root))
      setResizable(false) // Disable resizing the dialog
    }

    // Show the dialog and wait for it to close before returning
    dialogStage.showAndWait()
  }

  // Start by showing the Welcome screen
  showWelcome()
}
