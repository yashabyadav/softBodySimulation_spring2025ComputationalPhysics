package utils;

import java.awt.Color;
import java.util.ArrayList;

public class colorPalletes {
    public static final ArrayList<Color> WARM = new ArrayList<Color>() {{
        add(new Color(255, 87, 51));    // #FF5733
        add(new Color(255, 195, 0));    // #FFC300
        add(new Color(255, 138, 101));  // #FF8A65
        add(new Color(255, 87, 34));    // #FF5722
    }};

    public static final ArrayList<Color> COOL = new ArrayList<Color>() {{
        add(new Color(33, 150, 243));   // #2196F3
        add(new Color(0, 188, 212));    // #00BCD4
        add(new Color(3, 169, 244));    // #03A9F4
        add(new Color(0, 150, 136));    // #009688
    }};

    public static final ArrayList<Color> Kanagawa = new ArrayList<Color>() {{
        add(new Color(186, 187, 189));  // #BABBBD
        add(new Color(46, 50, 87));     // #2E3257
        add(new Color(98, 125, 154));   // #627D9A
        add(new Color(223, 197, 164));  // #DFC5A4
        add(new Color(255, 254, 247));  // #FFFEF7
    }};
    public static final ArrayList<Color> dataPlot1 = new ArrayList<Color>() {{
        add(new Color(10, 16, 13));     // #0a100d
        add(new Color(185, 186, 163)); // #b9baa3
        add(new Color(214, 213, 201)); // #d6d5c9
        add(new Color(162, 44, 41));   // #a22c29
        add(new Color(144, 41, 35));   // #902923
        add(new Color(253, 240, 213)); // #fdf0d5
        add(new Color(0, 48, 73));     // #003049
        add(new Color(102, 155, 188)); // #669bbc
    }};
    public static final ArrayList<Color> GRAYSCALE = new ArrayList<Color>() {{
        for (int i = 0; i <= 255; i += 25) {
            add(new Color(i, i, i));     // Dynamic grayscale values
        }
    }};

    public static final ArrayList<Color> Color_Azure = new ArrayList<Color>() {{ //https://www.color-hex.com/color-palette/31531
        add(new Color(216, 235, 255));  // #D8EBFF
        add(new Color(137, 196, 255));  // #89C4FF
        add(new Color(0, 128, 255));    // #0080FF
        add(new Color(0, 69, 137));     // #004589
        add(new Color(0, 30, 59));      // #001E3B
    }};

    public static ArrayList<Color> Metro_Colors = new ArrayList<Color>() {{ //https://www.color-hex.com/color-palette/861
        add(new Color(0, 171, 169));    // #00aba9
        add(new Color(255, 0, 151));    // #ff0097
        add(new Color(162, 0, 255));    // #a200ff
        add(new Color(27, 161, 226));   // #1ba1e2
        add(new Color(240, 150, 9));    // #f09609
    }};
    public static Color getColor(ArrayList<Color> palette, int index) {
        if (palette == null || palette.size() == 0) return Color.BLACK;
        return palette.get(index % palette.size());
    }

    public static ArrayList<Color> gradient(Color start, Color end, int steps) {
        ArrayList<Color> gradient = new ArrayList<>();
        for (int i = 0; i < steps; i++) {
            float ratio = (float)i / (steps - 1);
            int red   = (int)(start.getRed()   + ratio * (end.getRed()   - start.getRed()));
            int green = (int)(start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int blue  = (int)(start.getBlue()  + ratio * (end.getBlue()  - start.getBlue()));
            gradient.add(new Color(red, green, blue));
        }
        return gradient;
    }
}
