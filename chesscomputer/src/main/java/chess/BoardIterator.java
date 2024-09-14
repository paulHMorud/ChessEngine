package chess;

import java.util.ArrayList;
import java.util.Iterator;

public class BoardIterator implements Iterator<Character> {
    private ArrayList<ArrayList<Character>> board;
    private int row, col;
    public BoardIterator(ArrayList<ArrayList<Character>> board) {
        this.board = board;
        this.row = 0;
        this.col = -1; 
    }

    public boolean hasNext() {
        if (row == 7 && col == 7) {
            return false;
        }
        return true;
    }

    public Character next() {
        if (!this.hasNext()) {
            throw new IllegalArgumentException("Iteratoren har gått for langt");
        }
        if (col == 7) {
            col = 0;
            row++;
        }
        else {
            col++;
        }

        return board.get(row).get(col);
    }


}
