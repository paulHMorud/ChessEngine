package chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TreeNode {
    private Board board;
    private ArrayList<TreeNode> children = new ArrayList<>();
    private TreeNode parent = null;
    private static Map<Character, Integer> pieceValues = Map.of('p', -1, 'r', -5, 'q', -9, 'k', -100, 'n', -3, 'b', -3);
    private int[] move;
    private float value;

    public TreeNode(Board board, int[] move) {
        this.board = board;
        this.move = move;
    }

    public int[] getMove() {
        return move;
    }

    public Board getBoard() {
        return board;
    }
    public ArrayList<TreeNode> getChildren() {
        return children;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void addChild(TreeNode node) {
        children.add(node);
        node.setParent(this);
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setParent(TreeNode node) {
        parent = node;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getStaticValue() {
        float val = 0;
        for (char piece : board) {
            if (piece == ' ') {
                continue;
            }
            if (Character.isUpperCase(piece)) {
                val -= pieceValues.get(Character.toLowerCase(piece));
            }
            else {
                val += pieceValues.get(piece);
            }
        }
        if (board.isCheck()) {
            if (board.isCheckMate()) {
                if (board.getWhitesTurn()) {
                    val -= 1000;
                }
                else {
                    val += 1000;
                }
            }
        }
        this.value = val;
        return val;
    }
    public float getStaticValueWithActivity() {
        float val = 0;
        for (char piece : board) {
            if (piece == ' ') {
                continue;
            }
            if (Character.isUpperCase(piece)) {
                val -= pieceValues.get(Character.toLowerCase(piece));
            }
            else {
                val += pieceValues.get(piece);
            }
        }
        float ammontOfMoves = LegalMoves.legalMoves(board, true).size() - LegalMoves.legalMoves(board, false).size();
        val += ammontOfMoves/10;
        if (board.isCheck()) {
            if (board.isCheckMate()) {
                if (board.getWhitesTurn()) {
                    val -= 1000;
                }
                else {
                    val += 1000;
                }
            }
        }
        this.value = val;
        return val;
    }
}

