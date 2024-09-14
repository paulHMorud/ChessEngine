# Chess Game with Engine

This is a self made chess game and engine built in Java, featuring a graphical user interface (GUI) using JavaFX. The project is structured as a Maven build. While most aspects of chess are implemented, some functionalities are either incomplete or buggy:
- **Draws**: Draws are not included.
- **Pawn Promotion**: The functionality exists but may not work as expected in all cases.

## Features
- Chess game with most of the functionality.
- Enginge written using minmax treesearch.
- JavaFX-based GUI.
- Maven-based project structure for easy building and running.

## Prerequisites

- **Java**: Ensure that Java 21 or later is installed on your system. You can verify your Java version by running:
```
java -version
```
- **Maven**: Maven is prefered to build and run the project. You can check if Maven is installed with:
```
mvn -version
```
## Compile and run
```
git clone https://github.com/paulHMorud/ChessEngine.git
cd chesscomputer
mvn clean javafx:run
```
