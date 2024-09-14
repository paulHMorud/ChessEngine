package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChessComputer {
    private Board board;
    private TreeNode root;
    private int count;
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
        this.root = new TreeNode(board, null);
    }

     

    public void addChildren(TreeNode node, int depth) {
        if (depth == -1) {
            return;
        }
        Board NodeBoard = node.getBoard();
        ArrayList<int[]> legalMoves = LegalMoves.legalMoves(NodeBoard, NodeBoard.getWhitesTurn());
        if (depth <= 0) {
            List<int[]> captures = legalMoves.stream().filter((int[] move) -> LegalMoves.isCapture(NodeBoard, move)).toList();
            if (captures.size() == 0) {
                return;
            }
            for (int[] capture : captures) {
                TreeNode child = new TreeNode(new Board(NodeBoard), capture);
                if (child.getBoard().makeMove(capture)) {
                    node.addChild(child);
                    addChildren(child, depth-1);
                }
            }
            return;
        }
        for (int[] move : legalMoves) {
            TreeNode child = new TreeNode(new Board(NodeBoard), move);
            if (child.getBoard().makeMove(move)) {
                node.addChild(child);
            }
        }
        for (TreeNode child : node.getChildren()) {
            addChildren(child, depth - 1);
        }

    }


    public float getBestChild(TreeNode node, int depth, float alpha, float beta) {
        if (depth <= 0) {
            count ++;
            return getStaticValue(node.getBoard());
            //return node.getStaticValue();
        }
        Board NodeBoard = node.getBoard();
        ArrayList<int[]> legalMoves = NodeBoard.getLegalMoves();
        float high = -1000;
        float low = 1000;
        boolean whitesTurn = node.getBoard().getWhitesTurn();

        if (depth <= 0) {
            List<int[]> captures = legalMoves.stream().filter((int[] move) -> LegalMoves.isCapture(NodeBoard, move)).toList();
            if (captures.size() == 0) {
                return node.getStaticValue();
            }
            for (int[] capture : captures) {
                TreeNode child = new TreeNode(new Board(NodeBoard), capture);
                if (child.getBoard().makeMove(capture)) {
                    node.addChild(child);
                    float score = getBestChild(child, depth - 1, alpha, beta);
                    if (whitesTurn && score >= alpha) {
                        alpha = score;                    
                    }
                    else if (!whitesTurn && score <= beta) {
                        beta = score;
                    }
                    if (score < low) {
                        low = score;
                    }
                    if (score > high) {
                        high = score;
                    }
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return node.getValue();
        }

        for (int[] move : legalMoves) {
            TreeNode child = new TreeNode(new Board(NodeBoard), move);
            if (child.getBoard().makeMove(move, false)) {
                node.addChild(child);
                float score = getBestChild(child, depth - 1, alpha, beta);
                if (whitesTurn && score >= alpha) {
                    alpha = score;                    
                }
                else if (!whitesTurn && score <= beta) {
                    beta = score;
                }
                if (score < low) {
                    low = score;
                }
                if (score > high) {
                    high = score;
                }
                if (beta <= alpha) {
                    break;
                }
                }
            }
        if (whitesTurn) {
            node.setValue(high);
            return high;
        }
        else {
            node.setValue(low);
            return low;
        }

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
            count++;
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

    public int[] getBestMoveFast(int depth) {
        float high = -10000;
        float low = 10000;
        int[] bestMove = null;
        for (int[] move : board.getLegalMoves()) {
            Board childBoard = new Board(board);
            if (childBoard.makeMove(move, false)) {
                float score = evaluateChild(childBoard, depth-1, high, low);
                if (board.getWhitesTurn() && score > high) {
                    high = score;
                    bestMove = move;                    
                }
                else if (!board.getWhitesTurn() && score < low) {
                    low = score;
                    bestMove = move;
                }
            }
            
        }
        if (board.getWhitesTurn()) {
            System.out.println("high is: " + high);
            System.out.println("Low is: " + low);
        }
        else {
            System.out.println("high is: " + high);
            System.out.println("Low is: " + low);
        }
        System.out.println(count);
        return bestMove;
    }

    public float getBestChildWithActivity(TreeNode node, int depth, float alpha, float beta) {
        if (depth <= 0) {
            count ++;
            return node.getStaticValueWithActivity();
        }
        Board NodeBoard = node.getBoard();
        ArrayList<int[]> legalMoves = NodeBoard.getLegalMoves();
        float high = -1000;
        float low = 1000;
        boolean whitesTurn = node.getBoard().getWhitesTurn();


        for (int[] move : legalMoves) {
            TreeNode child = new TreeNode(new Board(NodeBoard), move);
            if (child.getBoard().makeMove(move)) {
                node.addChild(child);
                float score = getBestChildWithActivity(child, depth - 1, alpha, beta);
                if (whitesTurn && score >= alpha) {
                    alpha = score;                    
                }
                else if (!whitesTurn && score <= beta) {
                    beta = score;
                }
                if (score < low) {
                    low = score;
                }
                if (score > high) {
                    high = score;
                }
                if (beta <= alpha) {
                    break;
                }
                }
            }
        if (whitesTurn) {
            node.setValue(high);
            return high;
        }
        else {
            node.setValue(low);
            return low;
        }

    }

    public int[] getBestMoveWithActivity(int depth) {
        float bestVal = getBestChildWithActivity(root, depth, -1000, 1000);
        for (TreeNode child : root.getChildren()) {
            if (child.getValue() == bestVal) {
                return child.getMove();
            }
        }

        return root.getChildren().get(0).getMove();
    }


    public int[] getBestMove(int depth) {
        float bestVal = getBestChild(root, depth, -1000, 1000);
        for (TreeNode child : root.getChildren()) {
            if (child.getValue() == bestVal) {
                return child.getMove();
            }
        }

        return root.getChildren().get(0).getMove();
        
    }

    public static void main(String[] args) {
        Board board = new Board((GameControls)null);
        ChessComputer compute = new ChessComputer(board);
        int[] move = compute.getBestMove(3);
        System.out.println("" + move[0] + move[1] + move[2] + move[3]);
    }


}
