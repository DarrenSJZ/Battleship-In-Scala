# ScalaFX Battleship Game

![Scala](https://img.shields.io/badge/Scala-2.12.19-red?style=flat&logo=scala)
![SBT](https://img.shields.io/badge/SBT-1.9.7-blue?style=flat&logo=sbt)
![Java](https://img.shields.io/badge/Java-8-007396?style=flat&logo=java)
![FXML](https://img.shields.io/badge/FXML-007396?style=flat&logo=java&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat&logo=css3)

## Project Description
This is a ScalaFX implementation of the classic Battleship game, where two players place ships on a grid and attempt to sink each other's fleets through strategic attacks. The game features dynamic ship placement, turn-based attacks, ultimate abilities, and customizable game rules.

## Features
- **Player vs Player**: A two-player mode where each player takes turns placing ships and attacking.
- **Ultimate Abilities**:
  - Player 1: **Salvo** – Attack multiple positions on the grid.
  - Player 2: **Carpet Bomb** – Attack a line of cells on the opponent's grid.
- **Game Phases**: Ship Placement, Attack Phase.
- **Score Calculation**: Tracks shots hit, missed, and ultimate ability usage to compute final scores.
- **Database Integration**: Player stats are stored and retrieved using ScalikeJDBC.
- **Customizable Grid**: Ships can be placed programmatically by clicking on the grid.
- **Endgame Dialog**: Notifies the winner with a message box.
- **Forfeit Option**: Players can forfeit during their turn.

## Installation Instructions

1. **Clone the repository**:
   ```bash
   git clone https://github.com/DarrenSJZ/Battleship-In-Scala.git
   cd Battleship-in-Scala
   ```

3. **Setup the environment**:
   - Install Scala (version 2.12.19 recommended)
   - Install SBT (version 1.9.7 recommended)
   - Java 8 is required as this project uses JavaFX.

4. **Run the project**:
   sbt run

5. **Database Setup**:
   - This game uses ScalikeJDBC for player statistics.

## Usage Instructions

1. **Starting a Game**:
   - Players start by placing their ships on the grid by selecting a ship and choosing a start and end point.
   - After all ships are placed, the game proceeds to the attack phase.

2. **Attack Phase**:
   - Players take turns clicking on the opponent’s grid to fire shots.
   - If a player hits a ship, they can continue attacking. A miss ends their turn.
   - Ultimate abilities can be activated after hitting or missing certain numbers of cells.

3. **Forfeit**:
   - A player can forfeit by selecting the "Forfeit" option from the menu.

## Technology Stack
- **Programming Language**: Scala 2.12.19
- **Framework**: ScalaFX 8.0.192-R14
- **Database**: Apache Derby / H2 (ScalikeJDBC)
- **Build Tool**: SBT 1.9.7
- **JavaFX**: Java 8 for UI components
- **FXML**: Used for defining UI components
- **CSS3**: Used for styling UI components

## Screenshots

_To Be Included_

## Contributors
- **Siew Jun Zhen** – [GitHub](https://github.com/yourusername)

## License
![License](https://img.shields.io/badge/License-MIT-green.svg)
---

## Future Enhancements
- **AI Opponent**: Add a computer player mode.
- **Online Multiplayer**: Play against remote opponents.
- **Custom Game Modes**: Introduce new rules and challenges for more strategic gameplay.
- **UI Improvements**: Enhance the user interface for a more polished experience.
- **Dynamic Difficulty**: Implement AI that adapts based on player performance.
