package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Board implements Iterable<Character> {
    private ArrayList<ArrayList<Character>> board = new ArrayList<>();
    private String fen;
    private boolean whitesTurn;
    private ArrayList<int[]> playedMoves = new ArrayList<>();
    private GameControls gameController = null;
    public boolean lastMoveIsEnPassent = false;
    private ArrayList<int[]> legalMoves = new ArrayList<>();

    public Board(GameControls gameController) {
        this.fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        this.whitesTurn = true;
        if (gameController != null) {
            this.gameController = gameController;
        }
        makeBoard(fen);
        this.legalMoves = LegalMoves.legalMoves(this, whitesTurn);
    }

    public Board(String fen, boolean whitesTurn) {
        this.fen = fen;
        this.whitesTurn = whitesTurn;
        this.playedMoves = new ArrayList<>();
        makeBoard(fen);
        this.legalMoves = LegalMoves.legalMoves(this, whitesTurn);
    }

    public Board(Board board) {
        this.board = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            this.board.add(new ArrayList<Character> (board.getBoard().get(row)));
        }
        this.whitesTurn = board.getWhitesTurn();
        this.playedMoves = new ArrayList<>(board.playedMoves);
        this.legalMoves = LegalMoves.legalMoves(this, whitesTurn);
    }


    public void makeBoard(String fen) {
        board = new ArrayList<>();

        for(int i = 0; i < 8; i++) {
            ArrayList<Character> row = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                row.add(' ');
            }
            board.add(row);
        }

        int pos = 0;
        for (int i = 0; i < fen.length(); i++) {
            char c = fen.charAt(i);
            if (Character.isDigit(c)) {
                pos += Character.getNumericValue(c);
            }
            else {
                if (!(c == '/')) {
                    board.get(pos / 8).set(pos % 8, c);
                    pos ++;
                }
            }
        }
     }

    public ArrayList<ArrayList<Character>> getBoard() {
        return board;
    }

    public ArrayList<int[]> getPlayedMoves() {
        return playedMoves;
    }

    public boolean getWhitesTurn() {
        return whitesTurn;
    }

    public char getPiece(int row, int col) {
        return board.get(row).get(col);
    }

    public void setPiece(int row, int col, char c) {
        board.get(row).set(col, c);
    }

    public int[] getLastMove() {
        return playedMoves.getLast();
    }

    public ArrayList<int[]> getLegalMoves() {
        return legalMoves;
    }

    public boolean makeMove(int[] move) {
        return makeMove(move, true);
    }

    public boolean makeMove(int[] move, boolean checkLegality) {
        if (checkLegality) {
            boolean legal = false;
            if (move == null || move.length != 4) {
                throw new IllegalStateException("Trekket hadde ikke 4 elementer, den hadde: " + move.length);
            }

            for (int[] moves : legalMoves) {
                if (Arrays.equals(moves, move)) {
                    legal = true;
                    break;
                }
            }
            if (!legal) {
                return false;
            }
        }
       

        char movedPiece = getPiece(move[0], move[1]);
        char attackedPiece = getPiece(move[2], move[3]);
        this.setPiece(move[0], move[1], ' ');
        this.setPiece(move[2], move[3], movedPiece);

        this.legalMoves = LegalMoves.legalMoves(this, !whitesTurn);

        if (this.isCheck()) { //Sjekker om det er sjakk
            this.setPiece(move[2], move[3], attackedPiece);
            this.setPiece(move[0], move[1], movedPiece);
            this.legalMoves = LegalMoves.legalMoves(this, whitesTurn);
            return false;
        }

        if (Character.toLowerCase(movedPiece) == 'p' && (move[2] == 7 || move[2] == 0)) { //Håndterer bondepromosjon
            char promotion = 'q';
            if (whitesTurn) {
                promotion = 'Q';
            }
            if (this.gameController != null) {
                promotion = gameController.askForPromotionPiece(whitesTurn);
            }
            pawnPromotion(promotion, move[2], move[3]);
        }

        if (movedPiece == 'k' || movedPiece == 'K') { //Håndterer Rokade
            int[] whiteRightCastle = {7,4,7,6};
            int[] whiteLeftCastle = {7,4,7,2};
            int[] blackRightCastle = {0,4,0,6};
            int[] blackLeftCastle = {0,4,0,2};
            
            int[] rookMove = null;
            if (Arrays.equals(move, whiteRightCastle)) {
                rookMove = new int[]{7, 7, 7, 5};
            }
            else if (Arrays.equals(move, whiteLeftCastle)) {
                rookMove = new int[]{7, 0, 7, 3};
            }
            else if (Arrays.equals(move, blackRightCastle)) {
                rookMove = new int[]{0, 7, 0, 5};
            }
            else if (Arrays.equals(move, blackLeftCastle)) {
                rookMove = new int[]{0, 0, 0, 3};
            }
            if (rookMove != null) {
                char piece = getPiece(rookMove[0], rookMove[1]);
                this.setPiece(rookMove[0], rookMove[1], ' ');
                this.setPiece(rookMove[2], rookMove[3], piece);
                this.legalMoves = LegalMoves.legalMoves(this, !whitesTurn);
            }
        }
        playedMoves.add(move);
        if (lastMoveIsEnPassent()) {
            if (whitesTurn) {
                this.setPiece(move[2]+1, move[3], ' ');
            }
            else {
                this.setPiece(move[2]-1, move[3], ' ');
            }
            this.legalMoves = LegalMoves.legalMoves(this, !whitesTurn);
        }
        this.whitesTurn = !whitesTurn;
        return true;
    }

    public Iterator<Character> iterator() {
        Iterator<Character> it = new BoardIterator(board);
        return it;
    }

    public boolean lastMoveIsEnPassent() {
        if (playedMoves.size() < 3) {
            return false;
        }
        int[] lastMove = playedMoves.getLast();
        int[] lastOpponentMove = playedMoves.get(playedMoves.size()-2);
        if (Character.toLowerCase(this.getPiece(lastMove[2], lastMove[3])) != 'p' || Character.toLowerCase(this.getPiece(lastOpponentMove[2], lastOpponentMove[3])) != 'p') {
            return false;
        }
        if (lastMove[0] == 3 && lastMove[1] != lastMove[3] && lastOpponentMove[0] == 1 && lastOpponentMove[2] == 3 && lastMove[3] == lastOpponentMove[1]) {
            return true;
        }
        if (lastMove[0] == 4 && lastMove[1] != lastMove[3] && lastOpponentMove[0] == 6 && lastOpponentMove[2] == 4 && lastMove[3] == lastOpponentMove[1]) {
            return true;
        }
        return false;
    }

    public boolean isCheck() {
        char king;
        int row = 0;
        int col = 0;
        ArrayList<int[]> legalMoves = LegalMoves.legalMoves(this, !whitesTurn, true);
        if (whitesTurn) {
            king = 'K';
        }
        else {
            king = 'k';
        }
        outerloop:
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board.get(r).get(c) == king) {
                    row = r;
                    col = c;
                    break outerloop;
                }
            }
        }
        for (int[] moves : legalMoves) {
            if (moves[2] == row && moves[3] == col) {
                return true;
            }
        }
        return false;
    }

    public boolean isCheckMate() {
        Board newBoard = new Board((GameControls)null);
        for (int[] move : playedMoves) {
            newBoard.makeMove(move);
        }

        for (int[] move : legalMoves) {
            if (newBoard.makeMove(move)) {
                return false;
            }
        }
        return true;
    }
    
    public void pawnPromotion(char promotion, int row, int col) {
        List<Character> legalPromotions = List.of('q', 'r', 'n', 'b', 'Q', 'R', 'N', 'B');
        if (!legalPromotions.contains(promotion)) {
            throw new IllegalArgumentException("Det har skjedd en feil med pawnPromotion. Med ugyldig char bonden skal promoteres til");
        }
        char piece = board.get(row).get(col);
        if ((row == 7 && Character.isUpperCase(piece)) || (row == 0 && Character.isLowerCase(piece))) {
            throw new IllegalArgumentException("Feil med fargen på bondepromsjonen");
        }

        this.setPiece(row, col, promotion);
    }


    public String makeFen() {
        String fenString = "";
        for (ArrayList<Character> row : board) {
            int counter = 0;
            for (char c : row) {
                if (c == ' ') {
                    counter += 1;
                }
                else {
                    if (counter != 0) {
                        fenString += counter;
                        counter = 0;
                    }
                    fenString += c;
                }
            }
            if (counter != 0) {
                fenString += counter;
            }
            fenString += "/";
        }
        return fenString.substring(0, fenString.length()-1);
        
    }
}