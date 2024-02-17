import java.util.ArrayList;
import java.util.Iterator;

public class Board implements Iterable<Character> {
    private ArrayList<ArrayList<Character>> board;
    private String fen;
    private boolean whitesTurn;
    private ArrayList<int[]> playedMoves;

    public Board() {
        this.fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        this.whitesTurn = true;
        this.playedMoves = new ArrayList<>();
        makeBoard(fen);
        
    }

    public Board(String fen, boolean whitesTurn) {
        this.fen = fen;
        this.whitesTurn = whitesTurn;
        this.playedMoves = new ArrayList<>();
        makeBoard(fen);
    }

    private void makeBoard(String fen) {
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

    public char getPiece(int row, int col) {
        return board.get(row).get(col);
    }

    public void setPiece(int row, int col, char c) {
        board.get(row).set(col, c);
    }

    public int[] getLastMove() {
        return playedMoves.getLast();
    }


    public boolean makeMove(int[] move) {
        ArrayList<int[]> legalMoves = LegalMoves.legalMoves(this, whitesTurn);

        if (move.length != 4) {
            throw new IllegalStateException("Trekket hadde ikke 4 elementer, den hadde: " + move.length);
        }

        if (!legalMoves.contains(move)) {
            return false;
        }
        
        char c = getPiece(move[0], move[1]);
        System.out.println(c);
        this.setPiece(move[0], move[1], ' ');
        this.setPiece(move[2], move[3], c);
        playedMoves.add(move);
        return true;
    }

    public Iterator<Character> iterator() {
        Iterator<Character> it = new BoardIterator(board);
        return it;
    }


    public static void main(String[] args) {
        Board board = new Board();
        System.out.println(board.getBoard());
        int[] move = {6, 4, 4, 4};
        board.makeMove(move);
        System.out.println(board.getBoard());
        for(char c : board) {
            if (c != ' ') {
                System.out.println(c);
            }
        }
     }
}
