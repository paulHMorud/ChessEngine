package chess;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.io.File;

public class GameControls implements Initializable {
    private ArrayList<int[]> markedSquare = new ArrayList<>();
    private Rectangle markedRectangle;
    private Board board;
    private final Color lightColor = Color.CORNSILK;
    private final Color darkColor = Color.BURLYWOOD;
    private static HashMap<Character, String> piecePic = new HashMap<>(); 
    private boolean playBot = false;
    private FileHandler fileHandler = new FileHandler();
    
    @FXML
    private Slider slider;

    private void slider() {
        slider.setMin(3);
        slider.setMax(6);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        slider.setBlockIncrement(1);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

    }

    @FXML
    private GridPane gridpane;

    @Override
    public void initialize(URL url, ResourceBundle resourcebundle) {
        board = new Board(this);
        slider();
        System.out.println(System.getProperty("user.dir"));
        piecePic.put('k', "./src/main/java/chess/bilde_brikker-kopi/black-king.png");
        piecePic.put('K', "./src/main/java/chess/bilde_brikker-kopi/white-king.png");
        piecePic.put('p', "./src/main/java/chess/bilde_brikker-kopi/black-pawn.png");
        piecePic.put('P', "./src/main/java/chess/bilde_brikker-kopi/white-pawn.png");
        piecePic.put('n', "./src/main/java/chess/bilde_brikker-kopi/black-knight.png");
        piecePic.put('N', "./src/main/java/chess/bilde_brikker-kopi/white-knight.png");
        piecePic.put('b', "./src/main/java/chess/bilde_brikker-kopi/black-bishop.png");
        piecePic.put('B', "./src/main/java/chess/bilde_brikker-kopi/white-bishop.png");
        piecePic.put('q', "./src/main/java/chess/bilde_brikker-kopi/black-queen.png");
        piecePic.put('Q', "./src/main/java/chess/bilde_brikker-kopi/white-queen.png");
        piecePic.put('r', "./src/main/java/chess/bilde_brikker-kopi/black-rook.png");
        piecePic.put('R', "./src/main/java/chess/bilde_brikker-kopi/white-rook.png");

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Rectangle rectangle = new Rectangle(75, 75);
                rectangle.setOnMouseClicked(this::onMouseClick);
                rectangle.setStrokeWidth(5);
                if ((row + col) % 2 == 0) {
                    rectangle.setFill(lightColor);
                }
                else {
                    rectangle.setFill(darkColor);
                }
                gridpane.add(rectangle, col, row);

                char c = board.getBoard().get(row).get(col);

                ImageView view = new ImageView();
                view.setFitHeight(60);
                view.setFitWidth(60);
                view.setMouseTransparent(true);
                view.setX(1000);
                if (c != ' ') {
                    try {
                    InputStream stream = new FileInputStream(piecePic.get(c));
                    Image image = new Image(stream); 
                    view.setImage(image);
                    }
                    catch(Exception e) {
                        System.out.println("Problems with piece: " + c + " " + piecePic.get(c));
                        try {
                            String file = new java.io.File(".").getCanonicalPath();
                            System.out.println(file);
                        }
                        catch(Exception a) {
                            System.out.println("Helvete");
                        }
                        

                    }
                }
                gridpane.add(view, col, row);
                
            }
        }
    }

    @FXML
    private void markSquare(Rectangle rect) {
        markedRectangle = rect;
        if (rect.getFill().equals(lightColor)) {
            rect.setFill(Color.CORAL);
        }
        else {
            rect.setFill(Color.CORAL);
        }
    }

    @FXML 
    private void demarkSquare(Rectangle rect) {
        boolean isLightSquare = ((GridPane.getColumnIndex(rect) + GridPane.getRowIndex(rect)) % 2 == 0); 
        markedRectangle = null;
        if (isLightSquare) {
            rect.setFill(lightColor);
        }
        else {
            rect.setFill(darkColor);
        }
    }

    @FXML
    private void writeToFile() {
        fileHandler.writeToFile(board);
    }

    @FXML
    private void restartBoard() {
        Board startBoard = new Board(this);
        for (var node : gridpane.getChildren()) {
            if (node instanceof ImageView) {
                try {
                    int row = GridPane.getRowIndex(node);
                    int col = GridPane.getColumnIndex(node);
                    char piece = startBoard.getPiece(row, col);
                    if (piece == ' ') {
                        ((ImageView)node).setImage(null);
                    }
                    else {
                        InputStream stream = new FileInputStream(piecePic.get(piece));
                        Image image = new Image(stream);
                        ((ImageView)node).setImage(image);
                        stream.close();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                }
            }
    }

    @FXML 
    private void undoMove() {
        ArrayList<int[]> playedMoves = board.getPlayedMoves();
        playedMoves.removeLast();
        Board newBoard = new Board(this);
        restartBoard();
        this.board = newBoard;
        for (int[] move : playedMoves) {
            makeMove(move);
        }

    }

    @FXML 
    private void runSaveState() {
        Board savedBoard = new Board(this);
        restartBoard();
        this.board = savedBoard;
        ArrayList<int[]> playedMoves = fileHandler.getSaveState();
        for (int[] move : playedMoves) {
            makeMove(move);
        }
    }

    @FXML
    private void playBot() {
        this.playBot = !playBot;
    }

    private boolean makeMove(int[] move) {
        return makeMove(move, false);
    }
    @FXML
    private boolean makeMove(int[] move, boolean castles) {
        if (!castles) {
            if (!board.makeMove(move)) {
                return false;
            }
        }

        int fromRow = move[0];
        int fromCol = move[1];
        int toRow = move[2];
        int toCol = move[3];
        char piece = board.getPiece(toRow, toCol);

        if (board.getPiece(toRow, toCol) == 'k' || board.getPiece(toRow, toCol) == 'K') {
            if (Math.abs(fromCol - toCol) > 1) {
                int rookMove[] = null;
                if (move[3] == 6) {
                    rookMove = new int[]{fromRow, 7, fromRow, 5};
                }
                else {
                    rookMove = new int[]{fromRow, 0, fromRow, 3 };
                }
                makeMove(rookMove, true);
            }
        }

        for (var node : gridpane.getChildren()) {
            if (node instanceof ImageView) {
                int row = GridPane.getRowIndex(node);
                int col = GridPane.getColumnIndex(node);

                if (row == fromRow && col == fromCol) {
                    ((ImageView)node).setImage(null);
                    }
                else if (row == toRow && col == toCol) {
                    try {
                        InputStream stream = new FileInputStream(piecePic.get(piece));
                        Image image = new Image(stream);
                        ((ImageView)node).setImage(image);
                        stream.close();
                        }
                    catch(Exception e) {
                        System.out.println("Det ble en feil i funksjonen changeBoard. Med flytting av brikken" + piece);
                        }
                    }
                if(board.lastMoveIsEnPassent) {
                    if (row == fromRow && col == toCol) {
                        ((ImageView)node).setImage(null);
                    }
                }
            }
        }
        if (board.isCheck()) {
            if (board.isCheckMate()) {
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Game Over");
                if (!board.getWhitesTurn()) {
                    dialog.setContentText("Hvit har vunnet");
                }
                else {
                    dialog.setContentText("Svart har vunnet");
                }
                dialog.show();
            }
        }
        return true;
    }

    public char askForPromotionPiece(boolean whitesTurn) {
        ArrayList<Character> pieces = new ArrayList<>();
        ArrayList<ImageView> images = new ArrayList<>();
        HashMap<ImageView, Character> imageMap = new HashMap<>();
        if (whitesTurn) {
            pieces.add('Q'); pieces.add('R'); pieces.add('B'); pieces.add('N');
        }
        else {
            pieces.add('q'); pieces.add('r'); pieces.add('b'); pieces.add('n');
        }
        for (char piece : pieces) {
            try {
                InputStream stream = new FileInputStream(piecePic.get(piece));
                Image image = new Image(stream);
                ImageView view = new ImageView(image);
                images.add(view);
                imageMap.put(view, piece);

            } 
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Feil med askForpromotion");
            }  

        }

        ChoiceDialog<ImageView> choiceDialog = new ChoiceDialog<>(images.get(0), images);
        choiceDialog.setTitle("Velg brikke du vil promotere til");
        choiceDialog.showAndWait();
        return imageMap.get(choiceDialog.getResult());
    }



    @FXML
    private void onMouseClick(MouseEvent event) {
        Rectangle rect = (Rectangle) event.getSource();

        Integer row = GridPane.getRowIndex(rect);
        Integer col = GridPane.getColumnIndex(rect);

        if (markedSquare.isEmpty()) {
            int[] square = {row, col};
            markSquare(rect);
            markedSquare.add(square);
        }
        else {
            int[] move = {markedSquare.getFirst()[0], markedSquare.getFirst()[1], row, col};
            if (makeMove(move)) {
                if (playBot) {
                    ChessComputer moveGenerator = new ChessComputer(board);
                    int[] machineMove = moveGenerator.getMove((int)slider.getValue());
                    makeMove(machineMove);
                }
            }
            demarkSquare(markedRectangle);
            markedSquare.remove(0);
        }
    }


}
