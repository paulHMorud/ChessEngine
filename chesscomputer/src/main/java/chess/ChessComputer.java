package chess;

import java.util.List;
import java.util.Map;

public class ChessComputer {
    private Board board;
    private int[] bestMove = null;
    private int setDepth;
    private static Map<Character, Integer> pieceValues = Map.of('p', 1, 'r', 5, 'q', 9, 'k', 100, 'n', 3, 'b', 3);
    private final List<Integer> knightSquares = List.of(
        -50,-40,-30,-30,-30,-30,-40,-50,
        -40,-20,  0,  0,  0,  0,-20,-40,
        -30,  0, 10, 15, 15, 10,  0,-30,
        -30,  5, 15, 20, 20, 15,  5,-30,
        -30,  0, 15, 20, 20, 15,  0,-30,
        -30,  5, 10, 15, 15, 10,  5,-30,
        -40,-20,  0,  5,  5,  0,-20,-40,
        -50,-40,-30,-30,-30,-30,-40,-50);

    private final List<Integer> rookSquares = List.of(
        0,  0,  0,  0,  0,  0,  0,  0,
        5, 10, 10, 10, 10, 10, 10,  5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        0,  0,  0,  5,  5,  0,  0,  0
    );

    private final List<Integer> pawnSquares = List.of(
        0,  0,  0,  0,  0,  0,  0,  0,
        50, 50, 50, 50, 50, 50, 50, 50,
        10, 10, 20, 30, 30, 20, 10, 10,
         5,  5, 10, 25, 25, 10,  5,  5,
         0,  0,  0, 20, 20,  0,  0,  0,
         5, -5,-10,  0,  0,-10, -5,  5,
         5, 10, 10,-20,-20, 10, 10,  5,
         0,  0,  0,  0,  0,  0,  0,  0
    );

    private final List<Integer> bishopSquares = List.of(
        -20,-10,-10,-10,-10,-10,-10,-20,
        -10,  0,  0,  0,  0,  0,  0,-10,
        -10,  0,  5, 10, 10,  5,  0,-10,
        -10,  5,  5, 10, 10,  5,  5,-10,
        -10,  0, 10, 10, 10, 10,  0,-10,
        -10, 10, 10, 10, 10, 10, 10,-10,
        -10,  5,  0,  0,  0,  0,  5,-10,
        -20,-10,-10,-10,-10,-10,-10,-20
    );

    private final List<Integer> kingSquares = List.of(
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -20,-30,-30,-40,-40,-30,-30,-20,
        -10,-20,-20,-20,-20,-20,-20,-10,
        20, 20,  0,  0,  0,  0, 20, 20,
        20, 30, 10,  0,  0, 10, 30, 20
    );

    private final List<Integer> queenSquares = List.of(
        -20,-10,-10, -5, -5,-10,-10,-20,
        -10,  0,  0,  0,  0,  0,  0,-10,
        -10,  0,  5,  5,  5,  5,  0,-10,
        -5,  0,  5,  5,  5,  5,  0, -5,
        0,  0,  5,  5,  5,  5,  0, -5,
        -10,  5,  5,  5,  5,  5,  0,-10,
        -10,  0,  5,  0,  0,  0,  0,-10,
        -20,-10,-10, -5, -5,-10,-10,-20
    );

    private float squareBonus(char piece, boolean whitesTurn, int pos) {
        if (!whitesTurn) {
            int row = pos / 8;
            int col = pos % 8;
            pos = (7 - row)* 8 + col;
        }
        if (piece == 'p') {
            return pawnSquares.get(pos).floatValue()/100;
        }
        if (piece == 'r') {
            return rookSquares.get(pos).floatValue()/100;
        }
        if (piece == 'q') {
            return queenSquares.get(pos).floatValue()/100;
        }
        if (piece == 'n') {
            return knightSquares.get(pos).floatValue()/100;
        }
        if (piece == 'b') {
            return bishopSquares.get(pos).floatValue()/100;
        }
        if (piece == 'k') {
            return kingSquares.get(pos).floatValue()/100;
        }
        return 0;

    }


    public ChessComputer(Board board) {
        this.board = board;
    }

    public float getStaticValue(Board board) {
        float val = 0;
        for (int pos = 0; pos < 64; pos++) {
            char piece = board.getPiece(pos / 8, pos % 8);
            if (piece == ' ') {
                continue;
            }
            if (Character.isUpperCase(piece)) {
                piece = Character.toLowerCase(piece);
                val += pieceValues.get(piece);
                val += squareBonus(piece, true, pos);
            }
            else {
                val -= pieceValues.get(piece);
                val -= squareBonus(piece, false, pos);
            }
        }
        return val;
    }

    public int[] getMove(int depth) {
        this.setDepth = depth;
        evaluateChild(board, depth, -1000, 1000);
        return bestMove;
    }

    public float evaluateChild(Board board, int depth, float alpha, float beta) {
        if (depth <= 0) {
            return getStaticValue(board);
        }

        float high = -1000;
        float low = 1000;
        for (int[] move : board.getLegalMoves()) {
            Board nodeBoard = new Board(board);
            if (nodeBoard.makeMove(move, false)) {
                float score = evaluateChild(nodeBoard, depth-1, alpha, beta);
                if (!nodeBoard.getWhitesTurn() && score >= alpha) {
                    alpha = score;
                }
                else if (nodeBoard.getWhitesTurn() && score <= beta) {
                    beta = score;
                }

                if (score < low) {
                    if (depth == setDepth && !board.getWhitesTurn()) {
                        bestMove = move;
                    } 
                    low = score;
                }
                if (score > high) {
                    if (depth == setDepth && board.getWhitesTurn()) {
                        bestMove = move;
                    } 
                    high = score;
                }

                if (beta <= alpha) {
                    break;
                }
            }
        }
        if (board.getWhitesTurn()) {
            return high;
        }
        else {
            return low;
        }
    }


}
