package github.July_Summer.AncientBook.util;

public class ColorUtil {

    public static final String[] colors = new String[] {"1", "4", "c", "9", "6", "d", "e", "5", "2", "f", "a", "7", "b", "8", "3", "0", "l", "k", "n", "m"};
    
    public static String stripColor(String input)
    {
        for(String color : colors)
              input = input.replace("¡ì" + color, "");
        
        return input;
    }
    
}
