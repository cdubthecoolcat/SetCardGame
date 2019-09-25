/**
 * for testing
 *
 * @author Connor Wong
 */

import java.lang.*;

public class Test {
    public static void main(String[] args) {
        /*SetCard test = new SetCard(SetCard.Number.ONE, SetCard.Color.RED, SetCard.Shade.SOLID, SetCard.Shape.DIAMOND);
        ArrayList<SetCard> testArray = new ArrayList<SetCard>();
        testArray.add(new SetCard(SetCard.Number.ONE, SetCard.Color.RED, SetCard.Shade.SOLID, SetCard.Shape.DIAMOND));
        testArray.add(new SetCard(SetCard.Number.TWO, SetCard.Color.GREEN, SetCard.Shade.STRIPED, SetCard.Shape.SQUIGGLE));
        testArray.add(new SetCard(SetCard.Number.THREE, SetCard.Color.PURPLE, SetCard.Shade.OPEN, SetCard.Shape.OVAL));
        SetCardGameModel test1 = new SetCardGameModel();
        SetDeck d = new SetDeck();
        System.out.println(test1.isSet(testArray));

        System.out.println(test);
        for(SetCard a : d.getDeck()) {
            System.out.println(a);
            System.out.println();
        }
        System.out.println(d.getDeck().size());*/
        SetCardGameModel test = new SetCardGameModel();
        System.out.println(test.getBoard());
        //System.out.println(test.findPossibleSets());
        System.out.println(test.getCount());
    }
}