package com.dsjz.battleship.controller

import com.dsjz.battleship.MyApp
import com.dsjz.battleship.model.Player.{Player1, Player2}
import com.dsjz.battleship.model.Board.{OutOfBounds, Overlap, PlacementSuccess}
import com.dsjz.battleship.model.Ship.Ship
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.GridPane
import scalafxml.core.macros.sfxml
import scalafx.scene.layout.StackPane
import scalafx.scene.media.AudioClip
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scala.collection.JavaConverters._

@sfxml
class ShipPlacementP1Controller(private val gameGridP1: GridPane,
                                private val P1carrierButton: Button,
                                private val P1battleshipButton: Button,
                                private val P1cruiserButton: Button,
                                private val P1submarineButton: Button,
                                private val P1destroyerButton: Button,
                                private val P1endButton: Button) {


  var player1: Player1 = _
  var player2: Player2 = _


  // Load sound effects
  private val shipPlacedSound: AudioClip = new AudioClip(getClass.getResource("/audio/ShipPlace.mp3").toExternalForm)
  private val illegalPlacementSound: AudioClip = new AudioClip(getClass.getResource("/audio/illegal.mp3").toExternalForm)

  // Track the current ship being placed
  private var currentShip: Option[Ship] = None
  private var startX: Option[Int] = None
  private var startY: Option[Int] = None
  private var endX: Option[Int] = None
  private var endY: Option[Int] = None

  // Initialization method
  def initialize(): Unit = {
    if (player1 == null) {
      throw new IllegalStateException("Player1 instance must be set before using the controller.")
    }

    // Set up event handlers for grid and buttons
    setupGridHandlers()

    // Initialize button states
    P1endButton.setDisable(true) // Disable the end button until all ships are placed

    // Update the view based on the current state of the game
    updateView()

    // Log or output initial state for debugging
//    println("Initialization complete. Current board state:")
//    println(player1.board.toString)
  }

  // Map to track ship buttons and their corresponding Ship objects
  private lazy val shipButtons = Map[Ship, Button](
    player1.ships(0) -> P1carrierButton,
    player1.ships(1) -> P1battleshipButton,
    player1.ships(2) -> P1cruiserButton,
    player1.ships(3) -> P1submarineButton,
    player1.ships(4) -> P1destroyerButton
  )

  // Handler methods for each button
  def P1carrierButtonHandler(): Unit = selectShip(player1.ships(0))
  def P1battleshipButtonHandler(): Unit = selectShip(player1.ships(1))
  def P1cruiserButtonHandler(): Unit = selectShip(player1.ships(2))
  def P1submarineButtonHandler(): Unit = selectShip(player1.ships(3))
  def P1destroyerButtonHandler(): Unit = selectShip(player1.ships(4))

  def P1EndButtonHandler(): Unit = MyApp.showShipPlacementP2(player1, player2)

  // Set the current ship to place and reset coordinates
  private def selectShip(ship: Ship): Unit = {
    currentShip = Some(ship)
    startX = None
    startY = None
    endX = None
    endY = None
    println(s"Selected ${ship.name} for placement.")
  }

  // Set up click handlers for the grid
  private def setupGridHandlers(): Unit = {


    // Convert JavaFX children to a Scala collection
    val buttons = gameGridP1.children.asScala

    // Iterate over the children and set up the click handlers
    for (child <- buttons) {
      val button = new Button(child.asInstanceOf[javafx.scene.control.Button])
      button.getStyleClass.add("grid-button")
      val rowIndex = GridPane.getRowIndex(button)
      val columnIndex = GridPane.getColumnIndex(button)
      button.onMouseClicked = _ => handleGridClick(columnIndex, rowIndex)
    }
  }

  private def indexToCoordinate(x: Int, y: Int): String = {
    val letters = "ABCDEFGHIJ"
    val letter = if (y >= 0 && y < letters.length) letters(y).toString else "?"
    val number = (x + 1).toString
    letter + number
  }

  // Handle clicks on the grid
  private def handleGridClick(x: Int, y: Int): Unit = {
    if (currentShip.isEmpty) {
      println("No ship selected for placement.")
      return
    }

    val currentCoord = indexToCoordinate(x, y)

    if (startX.isEmpty && startY.isEmpty) {
      // First click sets the start coordinates
      startX = Some(x)
      startY = Some(y)
      println(s"Start position set at $currentCoord. Now select the end position.")
    } else {
      // Second click sets the end coordinates
      var tempEndX = x
      var tempEndY = y

      // Ensure startX, startY is the smaller coordinate and tempEndX, tempEndY is the larger coordinate
      if (startX.get > tempEndX) {
        val temp = startX.get
        startX = Some(tempEndX)
        tempEndX = temp
      }
      if (startY.get > tempEndY) {
        val temp = startY.get
        startY = Some(tempEndY)
        tempEndY = temp
      }

      endX = Some(tempEndX)
      endY = Some(tempEndY)

      val startCoord = indexToCoordinate(startX.get, startY.get)
      val endCoord = indexToCoordinate(endX.get, endY.get)

      // Determine the orientation and length of the ship
      val horizontal = startY.get == endY.get // True if y-coordinates are the same (horizontal)
      val vertical = startX.get == endX.get   // True if x-coordinates are the same (vertical)

      if (!horizontal && !vertical) {
        println("Invalid placement: The ship must be placed either horizontally or vertically.")
        return
      }

      val length = if (horizontal) {
        math.abs(endX.get - startX.get) + 1
      } else {
        math.abs(endY.get - startY.get) + 1
      }

      // Update the print statement to reflect the correct orientation
      val orientation = if (horizontal) "Horizontal" else "Vertical"
      println(s"Attempting to place ${currentShip.get.name} from $startCoord to $endCoord. Orientation: $orientation")

      if (length == currentShip.get.length) {
        val placementResult = player1.board.placeShip(startX.get, startY.get, currentShip.get, horizontal)

        placementResult match {
          case PlacementSuccess =>
            println(s"${currentShip.get.name} placed successfully! Coordinates: $startCoord to $endCoord, Orientation: $orientation")
            disableShipButton(currentShip.get) // Disable the button for the placed ship
            checkAllShipsPlaced() // Check if all ships are placed
            updateView()
            shipPlacedSound.play()

          case OutOfBounds =>
            println(s"Failed to place ${currentShip.get.name}. Placement out of bounds.")
            illegalPlacementSound.play()

          case Overlap =>
            println(s"Failed to place ${currentShip.get.name}. Placement overlaps with another ship.")
            illegalPlacementSound.play()
        }
      } else {
        println(s"Invalid length for ${currentShip.get.name}. Please try again.")
        illegalPlacementSound.play()
      }

      // Reset for the next ship
      currentShip = None
      startX = None
      startY = None
      endX = None
      endY = None
    }
  }

  // Disable the button for the placed ship
  private def disableShipButton(ship: Ship): Unit = {
    shipButtons.get(ship).foreach { button =>
      button.setDisable(true)
    }
    checkAllShipsPlaced() // Check if all ships are placed whenever a ship button is disabled
  }

  // Check if all ships have been placed
  private def checkAllShipsPlaced(): Unit = {
    val allPlaced = shipButtons.keys.forall(ship => shipButtons(ship).isDisable)
    P1endButton.setDisable(!allPlaced)
  }

  // Update the view to reflect the current state of the player's board
  def updateView(): Unit = {
    if (startX.isDefined && startY.isDefined && endX.isDefined && endY.isDefined && currentShip.isDefined) {
      val ship = currentShip.get
      val imageUrl = if (startY.get == endY.get) {
        // Horizontal images
        ship.name match {
          case "Carrier" => "images/game/Carrier_horizontal.png"
          case "Battleship" => "images/game/Battleship_horizontal.png"
          case "Cruiser" => "images/game/Cruiser_horizontal.png"
          case "Submarine" => "images/game/Submarine_horizontal.png"
          case "Destroyer" => "images/game/Destroyer_horizontal.png"
        }
      } else {
        // Vertical images
        ship.name match {
          case "Carrier" => "images/game/Carrier_vertical.png"
          case "Battleship" => "images/game/Battleship_vertical.png"
          case "Cruiser" => "images/game/Cruiser_vertical.png"
          case "Submarine" => "images/game/Submarine_vertical.png"
          case "Destroyer" => "images/game/Destroyer_vertical.png"
        }
      }

      val image = new Image(imageUrl)
      val imageView = new ImageView(image)

      // Set the ImageView size based on orientation
      val span = ship.length
      if (startY.get == endY.get) {
        // Horizontal
        imageView.fitWidth = span * 63
        imageView.fitHeight = 63
      } else {
        // Vertical
        imageView.fitWidth = 63
        imageView.fitHeight = span * 63
      }

      imageView.setPreserveRatio(false) // Ensure the image fully fills the specified dimensions

      // Create a frosted background rectangle with a white border
      val background = new Rectangle {
        width = imageView.fitWidth.value
        height = imageView.fitHeight.value
        fill = Color.rgb(0, 0, 200, 0.5)
        stroke = Color.White
        strokeWidth = 1
      }

      // Combine the background and image in a StackPane
      val stackPane = new StackPane()
      stackPane.children.addAll(background, imageView)

      // Add the StackPane to the grid at the correct position
      val x = startX.get
      val y = startY.get
      gameGridP1.add(stackPane, x, y)

      // Set the correct span based on the orientation
      if (startY.get == endY.get) {
        GridPane.setColumnSpan(stackPane, span)
      } else {
        GridPane.setRowSpan(stackPane, span)
      }

//      println(player.board.toString) // For debugging
    }
  }
}
