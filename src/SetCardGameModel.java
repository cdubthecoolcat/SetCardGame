import java.io.*;
import java.util.*;
import java.lang.*;
import java.lang.Math;

/**
 * The main logic of set
 * @author Connor Wong
 */
public class SetCardGameModel {
    private boolean isSet;
    private ArrayList<Integer> randomList;
    private ArrayList<SetCard> board;
    private ArrayList<ArrayList<SetCard>> setsFound;
    private ArrayList<ArrayList<SetCard>> possibleSets;
    private int count;

    final static int DECK_SIZE = 81;
    final static int BOARD_SIZE = 12;
    final static int SET_SIZE = 3;

    final static String NUM_IMAGE_ONE = "images/1";
    final static String NUM_IMAGE_TWO = "images/2";
    final static String NUM_IMAGE_THREE = "images/3";
    final static String COL_IMAGE_GREEN = "G";
    final static String COL_IMAGE_PURPLE = "P";
    final static String COL_IMAGE_RED = "R";
    final static String SHADE_IMAGE_OPEN = "O";
    final static String SHADE_IMAGE_SOLID = "S";
    final static String SHADE_IMAGE_STRIPED = "St";
    final static String SHAPE_IMAGE_DIAMOND = "D.png\n";
    final static String SHAPE_IMAGE_OVAL = "O.png\n";
    final static String SHAPE_IMAGE_SQUIGGLE = "S.png\n";


    public SetCardGameModel() {
        init();
        makeNumberList();
        setBoard();
        writeFile();
        boardHasSet();
    }

    public void init() {
        isSet = false;
        board = new ArrayList<>();
        setsFound = new ArrayList<>();
        possibleSets = new ArrayList<>();
        count = 0;
    }
    
    public ArrayList<SetCard> getBoard() {
        return board;
    }
    public int getCount() {
        return count;
    }

    public void makeNumberList() {
        randomList = new ArrayList<>();
        for(int i = 0; i < DECK_SIZE; i++) {
            randomList.add(i);
        }
    }

    public void setBoard() {
        int i = 0;
        SetDeck deck = new SetDeck();
        for (int j = 0; j < BOARD_SIZE; j++) {
            int rand = (int) (Math.random() * (deck.getDeck().size() - i));
            board.add(deck.getDeck().get(randomList.get(rand)));
            randomList.remove(rand);
            i++;
        }
    }

    //check if all cards have the same number of shapes
    public boolean isSameNumber(ArrayList<SetCard> select) {
        return select.get(0).getNumber() == select.get(1).getNumber()
                && select.get(0).getNumber() == select.get(2).getNumber();
    }
    //check if all cards have all different number shapes
    public boolean areDifferentNumbers(ArrayList<SetCard> select) {
        boolean first = select.get(0).getNumber() != select.get(1).getNumber();
        boolean second = select.get(1).getNumber() != select.get(2).getNumber();
        boolean third = select.get(0).getNumber() != select.get(2).getNumber();
        return first && second && third;
    }
    //check if all cards are the same shape
    public boolean isSameShape(ArrayList<SetCard> select) {
        return select.get(0).getShape() == select.get(1).getShape()
                && select.get(0).getShape() == select.get(2).getShape();
    }
    //check if all cards have all different shapes
    public boolean areDifferentShapes(ArrayList<SetCard> select) {
        boolean first = select.get(0).getShape() != select.get(1).getShape();
        boolean second = select.get(1).getShape() != select.get(2).getShape();
        boolean third = select.get(0).getShape() != select.get(2).getShape();
        return first && second && third;
    }
    //check if all cards have the same shading
    public boolean isSameShading(ArrayList<SetCard> select) {
        return select.get(0).getShade() == select.get(1).getShade()
                && select.get(0).getShade() == select.get(2).getShade();
    }
    //check if all cards have different shading
    public boolean areDifferentShading(ArrayList<SetCard> select) {
        boolean first = select.get(0).getShade() != select.get(1).getShade();
        boolean second = select.get(1).getShade() != select.get(2).getShade();
        boolean third = select.get(0).getShade() != select.get(2).getShade();
        return first && second && third;
    }
    //check if all cards have same color
    public boolean isSameColor(ArrayList<SetCard> select) {
        return select.get(0).getColor() == select.get(1).getColor()
                && select.get(0).getColor() == select.get(2).getColor();
    }
    //check if all cards have different color
    public boolean areDifferentColors(ArrayList<SetCard> select) {
        boolean first = select.get(0).getColor() != select.get(1).getColor();
        boolean second = select.get(1).getColor() != select.get(2).getColor();
        boolean third = select.get(0).getColor() != select.get(2).getColor();
        return first && second && third;
    }

    //checks if selected cards form a set
    public boolean isSet(ArrayList<SetCard> select) {
        if(select.size() != SET_SIZE) {
            return false;
        }
        boolean isNumbers = isSameNumber(select) || areDifferentNumbers(select);
        boolean isColors = isSameColor(select) || areDifferentColors(select);
        boolean isShading = isSameShading(select) || areDifferentShading(select);
        boolean isShapes = isSameShape(select) || areDifferentShapes(select);
        isSet = isNumbers && isColors && isShading && isShapes;
        return isSet;
    }

    public boolean boardHasSet() {
        boolean hasSet = false;
        int BOARD_SIZE = board.size();
        for(int i = 0; i < BOARD_SIZE; i++) {
            for(int j = i + 1; j < BOARD_SIZE; j++) {
                for(int k = j + 1; k < BOARD_SIZE; k++) {
                    ArrayList<SetCard> test = new ArrayList<SetCard>();
                    test.add(board.get(i));
                    test.add(board.get(j));
                    test.add(board.get(k));
                    if(isSet(test)) {
                        possibleSets.add(test);
                        hasSet = true;
                        count++;
                    }
                }
            }
        }
        return hasSet;
    }

    public void writeFile() {
        try {
            File file = new File("cardNames.tmp");
            if(file.exists())
                file.delete();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter writeCards = new BufferedWriter(fw);
            SetCard tempCard;
            for(int i = 0; i < board.size(); i++) {
                tempCard = board.get(i);
                if(tempCard.getNumber() == SetCard.Number.ONE) {
                    writeCards.write(NUM_IMAGE_ONE);
                }
                else if(tempCard.getNumber() == SetCard.Number.TWO) {
                    writeCards.write(NUM_IMAGE_TWO);
                }
                else {
                    writeCards.write(NUM_IMAGE_THREE);
                }
                if(tempCard.getColor() == SetCard.Color.GREEN) {
                    writeCards.write(COL_IMAGE_GREEN);
                }
                else if(tempCard.getColor() == SetCard.Color.PURPLE) {
                    writeCards.write(COL_IMAGE_PURPLE);
                }
                else {
                    writeCards.write(COL_IMAGE_RED);
                }
                if(tempCard.getShade() == SetCard.Shade.OPEN) {
                    writeCards.write(SHADE_IMAGE_OPEN);
                }
                else if(tempCard.getShade() == SetCard.Shade.SOLID) {
                    writeCards.write(SHADE_IMAGE_SOLID);
                }
                else {
                    writeCards.write(SHADE_IMAGE_STRIPED);
                }
                if(tempCard.getShape() == SetCard.Shape.DIAMOND) {
                    writeCards.write(SHAPE_IMAGE_DIAMOND);
                }
                else if(tempCard.getShape() == SetCard.Shape.OVAL) {
                    writeCards.write(SHAPE_IMAGE_OVAL);
                }
                else {
                    writeCards.write(SHAPE_IMAGE_SQUIGGLE);
                }
            }
            writeCards.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void updateSetsFound(ArrayList<SetCard> currentSet) {
        setsFound.add(currentSet);
    }

    public ArrayList<ArrayList<SetCard>> getPossibleSets() {
        return possibleSets;
    }

    public boolean wasFound(ArrayList<SetCard> currentSet) {
        boolean wasFound = false;
        for(int i = 0; i < setsFound.size(); i++) {
            if(setsFound.get(i).contains(currentSet.get(0)) && setsFound.get(i).contains(currentSet.get(1))
                    && setsFound.get(i).contains(currentSet.get(2))) {
                wasFound = true;
            }
        }
        return wasFound;
    }

    public void reset() {
        isSet = false;
        randomList = new ArrayList<>();
        board = new ArrayList<>();
        setsFound = new ArrayList<>();
        possibleSets = new ArrayList<>();
        count = 0;
        makeNumberList();
        setBoard();
        writeFile();
        boardHasSet();
    }
}