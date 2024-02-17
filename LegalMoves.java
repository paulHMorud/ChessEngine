import java.util.ArrayList;

public class LegalMoves {
    public static ArrayList<int[]> legalMoves(Board board, boolean whitesTurn) {
        ArrayList<int[]> Moves = new ArrayList<>();
        int pos = -1;
        for (char c : board) {
            pos++;
            int row = pos / 8;
            int col = pos % 8;
            if (c != ' ') {
                if (c == 'p' || c == 'P') {
                    Moves.addAll(legalPawnMove(board, row, col, whitesTurn));
                }
                else if (c == 'n' || c == 'N') {
                    Moves.addAll(legalKnightMove(board, row, col, whitesTurn));
                }
                else if (c == 'b' || c == 'B') {
                    Moves.addAll(legalBishopMove(board, row, col, whitesTurn));
                }
                else if (c == 'r' || c == 'R') {
                    Moves.addAll(legalRookMove(board, row, col, whitesTurn));
                }
                else if (c == 'k' || c == 'K') {
                    Moves.addAll(legalKingMove(board, row, col, whitesTurn));
                }
                else if (c == 'q' || c == 'Q') {
                    Moves.addAll(legalQueenMove(board, row, col, whitesTurn));
                }
            }
        }
        return Moves;

    }
    
    private static boolean friendlyPieceOnSquare(Board board, int row, int col, boolean whitesTurn) {
        char c = board.getPiece(row, col);

        if (c ==' ') {
            return false;
        }
        if (whitesTurn && Character.isUpperCase(c)) {
            return true;
        }
        if (!(whitesTurn || Character.isUpperCase(c))) {
            return true;
        }
        return false;
    }

    private static boolean enemyPieceOnSquare(Board board, int row, int col, boolean whitesTurn) {
        char c = board.getPiece(row, col);
       
        if (c ==' ') {
            return false;
        }
        if (whitesTurn && !Character.isUpperCase(c)) {
            return true;
        }
        if (!whitesTurn && Character.isUpperCase(c)) {
            return true;
        }
        return false;
    }

    private static ArrayList<int[]> legalPawnMove(Board board, int row, int col, boolean whitesTurn) {
        ArrayList<int[]> pawnMoves = new ArrayList<>();
        char c = board.getPiece(row, col);
        if (whitesTurn && Character.isUpperCase(c)) {
            if (board.getPiece(row-1, col) == ' ') { //En fram
                int[] move = {row, col, row-1, col};
                pawnMoves.add(move);
            }
            if (row == 6 && board.getPiece(row-2, col) == ' ' && board.getPiece(row-1, col) == ' ') { // To fram
                int[] move = {row, col, row-2, col};
                pawnMoves.add(move);
            }
            if (col < 7 && enemyPieceOnSquare(board, row-1, col+1, whitesTurn)) {//Ta skrått til høyre
                int[] move = {row, col, row-1 , col+1};
                pawnMoves.add(move);
            }
            if (col > 0 && enemyPieceOnSquare(board, row-1, col-1, whitesTurn)) { //Ta skrått til venstre
                int[] move = {row, col, row-1 , col-1};
                pawnMoves.add(move);
            }
            if (row == 3) { //Dette er en passent
                int[] lastMove = board.getLastMove();
                int[] enPassentRight = {1, col+1, 3, col+1};
                int[] enPassentLeft = {1, col-1, 3, col-1};
                if (lastMove.equals(enPassentRight)) {
                    int[] move = {3, col, 2, col+1};
                    pawnMoves.add(move);                 
                }
                else if(lastMove.equals(enPassentLeft)) {
                    int[] move = {3, col, 2, col-1};
                    pawnMoves.add(move);
                }
            }
        }
        else if (!(whitesTurn || Character.isUpperCase(c))) {
            if (board.getPiece(row+1, col) == ' ') {
                int[] move = {row, col, row+1, col};
                pawnMoves.add(move);
            }
            if (row == 1 && board.getPiece(row+2, col) == ' ' && board.getPiece(row+1, col) == ' ') {
                int[] move = {row, col, row+2, col};
                pawnMoves.add(move);
            }
            if (col < 7 && enemyPieceOnSquare(board, row+1, col+1, whitesTurn)) {
                int[] move = {row, col, row-1 , col+1};
                pawnMoves.add(move);
            }
            if (col > 0 && enemyPieceOnSquare(board, row+1, col-1, whitesTurn)) {
                int[] move = {row, col, row+1 , col-1};
                pawnMoves.add(move);
            }
            if (row == 4) { //Dette er en passent
                int[] lastMove = board.getLastMove();
                int[] enPassentRight = {6, col+1, 4, col+1};
                int[] enPassentLeft = {6, col-1, 4, col-1};
                if (lastMove.equals(enPassentRight)) {
                    int[] move = {4, col, 5, col+1};
                    pawnMoves.add(move);                 
                }
                else if(lastMove.equals(enPassentLeft)) {
                    int[] move = {4, col, 5, col-1};
                    pawnMoves.add(move);
                }
            }
        }
        return pawnMoves;
    }

    private static ArrayList<int[]> legalKnightMove(Board board, int row, int col, boolean whitesTurn) {
        char c = board.getPiece(row, col);
        ArrayList<int[]> knightMoves = new ArrayList<>();
        if (!((whitesTurn && Character.isUpperCase(c)) || !(whitesTurn || Character.isUpperCase(c)))) {
            return knightMoves;
        }
        if (row > 1 && col < 7 && !friendlyPieceOnSquare(board, row-2, col+1, whitesTurn)) {
            int[] move = {row, col, row-2, col+1};
            knightMoves.add(move);
        }
        if (row > 1 && col > 0 && !friendlyPieceOnSquare(board, row-2, col-1, whitesTurn)) {
            int[] move = {row, col, row-2, col-1};
            knightMoves.add(move);
        }
        if (row > 0 && col < 6 && !friendlyPieceOnSquare(board, row-1, col+2, whitesTurn)) {
            int[] move = {row, col, row-1, col+2};
            knightMoves.add(move);
        }
        if (row > 0 && col > 1 && !friendlyPieceOnSquare(board, row-1, col-2, whitesTurn)) {
            int[] move = {row, col, row-1, col-2};
            knightMoves.add(move);
        }
        if (row < 6 && col > 0 && !friendlyPieceOnSquare(board, row+2, col-1, whitesTurn)) {
            int[] move = {row, col, row+2, col-1};
            knightMoves.add(move);
        }
        if (row < 6 && col < 7 && !friendlyPieceOnSquare(board, row+2, col+1, whitesTurn)) {
            int[] move = {row, col, row+2, col+1};
            knightMoves.add(move);
        }
        if (row < 7 && col > 1 && !friendlyPieceOnSquare(board, row+1, col-2, whitesTurn)) {
            int[] move = {row, col, row+1, col-1};
            knightMoves.add(move);
        }
        if (row < 7 && col < 6 && !friendlyPieceOnSquare(board, row+1, col+2, whitesTurn)) {
            int[] move = {row, col, row+1, col-1};
            knightMoves.add(move);
        }

        return knightMoves;
    }

    private static ArrayList<int[]> legalBishopMove(Board board, int row, int col, boolean whitesTurn) {
        char c = board.getPiece(row, col);
        ArrayList<int[]> bishopMoves = new ArrayList<>();

        if (!((whitesTurn && Character.isUpperCase(c)) || !(whitesTurn || Character.isUpperCase(c)))) {
            return bishopMoves;
        }

        for (int i = 1; i <= Integer.min(row, col); i++) { //Skrått oppover til venstre
            int[] move = {row, col, row-i, col-i};
            if (friendlyPieceOnSquare(board, row-i, col-i, whitesTurn)) {
                break;
            }
            if (enemyPieceOnSquare(board, row-i, col-i, whitesTurn)) {
                bishopMoves.add(move);
                break;
            }
            bishopMoves.add(move);
        }
        for (int i = 1; i  <= Integer.min(7-row, 7-col); i++) { //Skrått nedover til høyre
            int[] move = {row, col, row+i, col+i};
            if (friendlyPieceOnSquare(board, row+i, col+i, whitesTurn)) {
                break;
            }
            if (enemyPieceOnSquare(board, row+i, col+i, whitesTurn)) {
                bishopMoves.add(move);
                break;
            }
            bishopMoves.add(move);
        }
        for (int i = 1; i <= Integer.min(row, 7-col); i++) { //Skrått oppover til høyre
            int[] move = {row, col, row-i, col+i};
            if (friendlyPieceOnSquare(board, row-i, col+i, whitesTurn)) {
                break;
            }
            if (enemyPieceOnSquare(board, row-i, col+i, whitesTurn)) {
                bishopMoves.add(move);
                break;
            }
            bishopMoves.add(move);
        }
        for (int i = 1; i <= Integer.min(7-row, col); i++) { //Skrått nedover til venstre
            int[] move = {row, col, row+i, col-i};
            if (friendlyPieceOnSquare(board, row+i, col-i, whitesTurn)) {
                break;
            }
            if (enemyPieceOnSquare(board, row+i, col-i, whitesTurn)) {
                bishopMoves.add(move);
                break;
            }
            bishopMoves.add(move);
        }

        return bishopMoves;
    }

    private static ArrayList<int[]> legalRookMove(Board board, int row, int col, boolean whitesTurn) {
        char c = board.getPiece(row, col);
        ArrayList<int[]> rookMoves = new ArrayList<>();

        if (!((whitesTurn && Character.isUpperCase(c)) || !(whitesTurn || Character.isUpperCase(c)))) {
            return rookMoves;
        }
        for (int i = 1; i <= col; i++) { //Rett til venstre
            int[] move = {row, col, row, col-i};
            if (friendlyPieceOnSquare(board, row, col-i, whitesTurn)) {
                break;
            }
            if (enemyPieceOnSquare(board, row, col-i, whitesTurn)) {
                rookMoves.add(move);
                break;
            }
            rookMoves.add(move);
        }
        for (int i = 1; i <= 7 - col; i++) { //Rett til høyre
            int[] move = {row, col, row, col+i};
            if (friendlyPieceOnSquare(board, row, col+i, whitesTurn)) {
                break;
            }
            if (enemyPieceOnSquare(board, row, col+i, whitesTurn)) {
                rookMoves.add(move);
                break;
            }
            rookMoves.add(move);
        }
        for (int i = 1; i <= row; i++) { //Oppover
            int[] move = {row, col, row-i, col};
            if (friendlyPieceOnSquare(board, row-i, col, whitesTurn)) {
                break;
            }
            if (enemyPieceOnSquare(board, row-i, col, whitesTurn)) {
                rookMoves.add(move);
                break;
            }
            rookMoves.add(move);
        }
        for(int i = 1; i <= 7-row; i++) { //Nedover
            int[] move = {row, col, row+i, col};
            if (friendlyPieceOnSquare(board, row+i, col, whitesTurn)) {
                break;
            }
            if (enemyPieceOnSquare(board, row+i, col, whitesTurn)) {
                rookMoves.add(move);
                break;
            }
            rookMoves.add(move);
        }
        
        return rookMoves;
    }

    private static ArrayList<int[]> legalKingMove(Board board, int row, int col, boolean whitesTurn) {
        ArrayList<int[]> kingMoves = new ArrayList<>();
        char c = board.getPiece(row, col);

        if (!((whitesTurn && Character.isUpperCase(c)) || !(whitesTurn || Character.isUpperCase(c)))) {
            return kingMoves;
        }

        if (row > 0) { //Oppover
            if (!friendlyPieceOnSquare(board, row-1, col, whitesTurn)) {
                int[] move = {row, col, row-1, col};
                kingMoves.add(move);
            }
        }
        if (row < 7) { //Nedover
            if (!friendlyPieceOnSquare(board, row+1, col, whitesTurn)) {
                int[] move = {row, col, row+1, col};
                kingMoves.add(move);
            }
        }
        if (row > 0 && col > 0) { //Skrått opp til venstre
            if (!friendlyPieceOnSquare(board, row-1, col-1, whitesTurn)) {
                int[] move = {row, col, row-1, col-1};
                kingMoves.add(move);
            }
        }
        if (row > 0 && col < 7) { //Skrått opp til høyre
            if (!friendlyPieceOnSquare(board, row-1, col+1, whitesTurn)) {
                int[] move = {row, col, row-1, col+1};
                kingMoves.add(move);
            }
        }
        if (row < 7 && col > 0) { //Skrått ned til venstre
            if (!friendlyPieceOnSquare(board, row+1, col-1, whitesTurn)) {
                int[] move = {row, col, row+1, col-1};
                kingMoves.add(move);
            }
        }
        if (row < 7 && col < 7) { //Skrått ned til høyre
            if (!friendlyPieceOnSquare(board, row+1, col+1, whitesTurn)) {
                int[] move = {row, col, row+1, col+1};
                kingMoves.add(move);
            }
        }
        if (col > 0) {
            if (!friendlyPieceOnSquare(board, row, col-1, whitesTurn)) {
                int[] move = {row, col, row, col-1};
                kingMoves.add(move);
            }
        }
        if (col < 7) {
            if (!friendlyPieceOnSquare(board, row, col+1, whitesTurn)) {
                int[] move = {row, col, row, col+1};
                kingMoves.add(move);
            }
        }
        return kingMoves;
    }
    

    private static ArrayList<int[]> legalQueenMove(Board board, int row, int col, boolean whitesTurn) {
        ArrayList<int[]> queenMoves = new ArrayList<>();
        queenMoves.addAll(legalBishopMove(board, row, col, whitesTurn));
        queenMoves.addAll(legalRookMove(board, row, col, whitesTurn));
        return queenMoves;
    }

    public static void main(String[] args) {
        Board board = new Board();
        ArrayList<int[]> a = LegalMoves.legalMoves(board, true);
        System.out.println(a.size());
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/2PP1PPP/RNBQKBNR";
        Board board2 = new Board(fen, true);
        ArrayList<int[]> b = LegalMoves.legalMoves(board2, true);
        System.out.println(b.size());
        for (int[] i : b) {
            for (int n : i) {
                System.out.println(n);
            }
            System.out.println("New Move");
        }
    }
}
