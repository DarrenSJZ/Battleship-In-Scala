package com.dsjz.battleship.controller

import com.dsjz.battleship.MyApp
import javafx.collections.ListChangeListener
import javafx.scene.Node
import javafx.scene.layout.AnchorPane
import scalafx.scene.control.{MenuBar, MenuItem}
import scalafx.scene.layout.BorderPane
import scalafxml.core.macros.sfxml

@sfxml
class RootLayoutController(private val menuBar: MenuBar,
                           private val rootLayout: BorderPane,
                           private val forfeitMenuItem: MenuItem) {

  def initialize(): Unit = {
    rootLayout.getChildren.addListener(new ListChangeListener[javafx.scene.Node] {
      override def onChanged(c: ListChangeListener.Change[_ <: Node]): Unit = {
        if (rootLayout.getChildren.size > 1) {
          val contentPane = rootLayout.getChildren.get(1)
          contentPane match {
            case pane: AnchorPane =>
              pane.getId match {
                case "welcome" => menuBar.visible = false
                case "P1AttackPhase" | "P2AttackPhase" =>
                  menuBar.visible = true
                  forfeitMenuItem.disable = false

                case _ =>
                  menuBar.visible = true
                  forfeitMenuItem.disable = true

              }
            case _ =>
          }
        }
      }
    })
  }

  def showForfeitDialogMenuItemHandler(): Unit = {
    if (rootLayout.getChildren.size > 1) {
      val contentPane = rootLayout.getChildren.get(1)
      contentPane match {
        case pane: AnchorPane =>
          pane.getId match {
            case "P1AttackPhase" =>
              if (pane.getUserData != null) {
                val controller = pane.getUserData.asInstanceOf[P1AttackPhaseController#Controller]
                controller.handleForfeit()
              }

            case "P2AttackPhase" =>
              if (pane.getUserData != null) {
                val controller = pane.getUserData.asInstanceOf[P2AttackPhaseController#Controller]
                controller.handleForfeit()
              }

            case _ => // Handle other cases or do nothing
          }

        case _ => // Handle other types of nodes or do nothing
      }
    }
  }



  def showAboutGameMenuItemHandler(): Unit = {
    MyApp.showAboutGameDialog()
  }

  def menuItemExitHandler(): Unit = {
    sys.exit(0)
  }
}
