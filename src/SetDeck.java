/**
 * Makes a deck. Much cool such wow very whoa
 * Makes a deck of SetCards
 * @author Connor Wong
 */
import java.util.ArrayList;
public class SetDeck {

    private ArrayList<SetCard> deck;

    public SetDeck() {
        init();
        makeDeck();
    }

    public void init() {
        deck = new ArrayList<SetCard>();
    }

    public ArrayList<SetCard> getDeck() {
        return deck;
    }

    private void makeDeck() {
        for(SetCard.Number number : SetCard.Number.class.getEnumConstants()) {
            for(SetCard.Color color : SetCard.Color.class.getEnumConstants()) {
                for(SetCard.Shade shade : SetCard.Shade.class.getEnumConstants()) {
                    for(SetCard.Shape shape : SetCard.Shape.class.getEnumConstants()) {
                        deck.add(new SetCard(number, color, shade, shape));
                    }
                }
            }
        }
    }
}