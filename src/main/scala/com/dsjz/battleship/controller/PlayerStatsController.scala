package com.dsjz.battleship.controller

import com.dsjz.battleship.MyApp
import com.dsjz.battleship.model.Player.{Player1, Player2, PlayerData}
import javafx.beans.value.{ChangeListener, ObservableValue}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Label, TableColumn, TableView}
import scalafx.scene.layout.GridPane
import scalafxml.core.macros.sfxml

@sfxml
class PlayerStatsController(
                             private val playerTable: TableView[PlayerData],
                             private val nameColumn: TableColumn[PlayerData, String],
                             private val shotsFiredLabel1: Label,
                             private val shotsHitLabel1: Label,
                             private val shotsMissedLabel1: Label,
                             private val ultimateUsedLabel1: Label,
                             private val roundsSurvivedLabel1: Label,
                             private val enemyShipsSunkLabel1: Label,
                             private val shotsFiredLabel2: Label,
                             private val shotsHitLabel2: Label,
                             private val shotsMissedLabel2: Label,
                             private val ultimateUsedLabel2: Label,
                             private val roundsSurvivedLabel2: Label,
                             private val enemyShipsSunkLabel2: Label
                           ) {

  // ObservableBuffer to hold the player data
  val playerData = ObservableBuffer[PlayerData]()

  // Initialize the controller
  def initialize(): Unit = {
    // Set up the TableView columns
    nameColumn.cellValueFactory = { _.value.nameProperty }

    // Load player data from the database
    loadPlayerData()

    // Bind the selection model to the stats grid using a ChangeListener
    playerTable.getSelectionModel.selectedItemProperty().addListener(
      new ChangeListener[PlayerData] {
        override def changed(observable: ObservableValue[_ <: PlayerData], oldValue: PlayerData, newValue: PlayerData): Unit = {
          if (newValue != null) {
            updateStats(newValue)
          }
        }
      }
    )

    // Set the items for the playerTable
    playerTable.items = playerData
  }

  // Method to load player data from the database
  private def loadPlayerData(): Unit = {
    // Fetch player data from both Player1 and Player2 tables
    val player1Data = Player1.getAllPlayers
    val player2Data = Player2.getAllPlayers

    // Combine the player data into pairs of Player1 and Player2
    val combinedData = player1Data.zip(player2Data).map { case (p1, p2) =>
      PlayerData(
        id = p1.id,  // Use Player1's ID
        name = s"${p1.name} - ${p2.name}", // Combine the names of both players
        player1ShotsFired = p1.player1ShotsFiredProperty.value,
        player1ShotsHit = p1.player1ShotsHitProperty.value,
        player1ShotsMissed = p1.player1ShotsMissedProperty.value,
        player1UltimateAbilitiesUsed = p1.player1UltimateAbilitiesUsedProperty.value,
        player1RoundsSurvived = p1.player1RoundsSurvivedProperty.value,
        player1EnemyShipsSunk = p1.player1EnemyShipsSunkProperty.value,
        player2ShotsFired = p2.player2ShotsFiredProperty.value,
        player2ShotsHit = p2.player2ShotsHitProperty.value,
        player2ShotsMissed = p2.player2ShotsMissedProperty.value,
        player2UltimateAbilitiesUsed = p2.player2UltimateAbilitiesUsedProperty.value,
        player2RoundsSurvived = p2.player2RoundsSurvivedProperty.value,
        player2EnemyShipsSunk = p2.player2EnemyShipsSunkProperty.value
      )
    }

    // Populate the ObservableBuffer with the combined data
    playerData.clear()
    playerData ++= combinedData
  }

  // Method to update the statistics labels
  private def updateStats(player: PlayerData): Unit = {
    // Update Player 1 Stats
    shotsFiredLabel1.text = player.player1ShotsFiredProperty.value.toString
    shotsHitLabel1.text = player.player1ShotsHitProperty.value.toString
    shotsMissedLabel1.text = player.player1ShotsMissedProperty.value.toString
    ultimateUsedLabel1.text = player.player1UltimateAbilitiesUsedProperty.value.toString
    roundsSurvivedLabel1.text = player.player1RoundsSurvivedProperty.value.toString
    enemyShipsSunkLabel1.text = player.player1EnemyShipsSunkProperty.value.toString

    // Update Player 2 Stats
    shotsFiredLabel2.text = player.player2ShotsFiredProperty.value.toString
    shotsHitLabel2.text = player.player2ShotsHitProperty.value.toString
    shotsMissedLabel2.text = player.player2ShotsMissedProperty.value.toString
    ultimateUsedLabel2.text = player.player2UltimateAbilitiesUsedProperty.value.toString
    roundsSurvivedLabel2.text = player.player2RoundsSurvivedProperty.value.toString
    enemyShipsSunkLabel2.text = player.player2EnemyShipsSunkProperty.value.toString
  }

  def backButtonHandler(): Unit = {
    MyApp.showWelcome()
  }
}
