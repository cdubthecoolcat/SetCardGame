/**
 * A card contains a number, a color, a shade, and a shape
 *
 * @author Connor Wong
 */

public class SetCard {
    private Number number;
    private Color color;
    private Shade shade;
    private Shape shape;

    final static String NUM_STRING = "Number: ";
    final static String COL_STRING = "\nColor: ";
    final static String SHADE_STRING = "\nShading: ";
    final static String SHAPE_STRING = "\nShape: ";

    public enum Number {
        ONE,
        TWO,
        THREE
    }

    public enum Shade {
        SOLID,
        STRIPED,
        OPEN
    }

    public enum Color {
        RED,
        GREEN,
        PURPLE
    }

    public enum Shape {
        DIAMOND,
        SQUIGGLE,
        OVAL
    }

    public SetCard(Number num, Color col, Shade shading, Shape form) {
        number = num;
        color = col;
        shade = shading;
        shape = form;
    }

    public Number getNumber() {
        return number;
    }

    public Color getColor() {
        return color;
    }

    public Shade getShade() {
        return shade;
    }

    public Shape getShape() {
        return shape;
    }

    public String toString() {
        String output = "";
        output += NUM_STRING + number;
        output += COL_STRING + color;
        output += SHADE_STRING + shade;
        output += SHAPE_STRING + shape;
        return output;
    }
}