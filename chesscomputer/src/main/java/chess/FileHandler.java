package chess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class FileHandler {
    public ArrayList<int[]> getSaveState() {
        String fileName = "./src/main/java/chess/SaveState.txt";
        ArrayList<int[]> listOfMoves = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            reader.readLine();
            String playedMoves = reader.readLine();
            String[] list = playedMoves.split(", ");
            for (String moves : list) {
                int nr1 = Character.getNumericValue(moves.charAt(0));
                int nr2 = Character.getNumericValue(moves.charAt(1));
                int nr3 = Character.getNumericValue(moves.charAt(2));
                int nr4 = Character.getNumericValue(moves.charAt(3));
                int[] move = {nr1, nr2, nr3, nr4};
                listOfMoves.add(move);
            }
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return listOfMoves;
    }

    public void writeToFile(Board board) {
        String fenString = board.makeFen();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/java/chess/SaveState.txt"));
            writer.append(fenString);
            writer.append('\n');
            for (int[] move : board.getPlayedMoves()) {
                String str = "" + move[0] + move[1] + move[2] + move[3];
                writer.append(str);
                writer.append(", ");

            }
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
