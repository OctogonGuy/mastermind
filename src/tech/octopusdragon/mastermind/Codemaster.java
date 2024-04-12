package tech.octopusdragon.mastermind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

/**
 * Codemaster class for the game of Master Mind
 * @author Alex
 */
public class Codemaster {
	private boolean repeatingColors;	// Whether or not to accept repeating colors
    private CodeColor[] hiddenPattern;  // The pattern of colors to guess
    private int colors;                 // The number of possible colors
    
    /**
     * Constructor
     * @param repColors Whether or not to accept repeating colors.
     * @param numColors The number of possible colors.
     * @param numHoles The number of holes in the pattern.
     */
    public Codemaster(boolean repColors, int numHoles, int numColors) {
    	repeatingColors = repColors;
        colors = numColors;
        hiddenPattern = new CodeColor[numHoles];
    }
    
    /**
     * The makeHiddenPattern method creates a random pattern of colors for the
     * player to guess.
     */
    public void makeHiddenPattern() {
        Random rand = new Random();
        
        if (repeatingColors) {
	        // Assign each element in the hiddenPattern array a random color within
	        // the range of possible colors.
	        for (int i = 0; i < hiddenPattern.length; i++)
	            hiddenPattern[i] = CodeColor.values()[rand.nextInt(colors)];
        } else {
        	// Make sure not to repeat.
        	ArrayList<CodeColor> colorPool = new ArrayList<CodeColor>();
        	for (int i = 0; i < CodeColor.values().length; i++)
        		colorPool.add(CodeColor.values()[i]);
        	for (int i = 0; i < hiddenPattern.length; i++) {
        		int index = rand.nextInt(colors);
        		hiddenPattern[i] = colorPool.get(index);
        		colorPool.remove(index);
        		colors--;
        	}
        }
    }
    
    /**
     * The getHiddenPattern method returns the hidden pattern.
     * @return The values stored in hiddenPattern.
     */
    public CodeColor[] getHiddenPattern() {
        return hiddenPattern;
    }
    
    /**
     * The checkGuess method check's a player's guess against the codemaster's
     * hidden pattern.
     * @param player The Player object to check.
     * @return An array of
     */
    public KeyColor[] checkGuess(Player player) {
        // Create an array holdling the combination of key peg colors that
        // represent the number of correct placements and colors. An integer
        // will represent how much of the array is filled.
        KeyColor[] keys = new KeyColor[hiddenPattern.length];
        int keysLength = 0;
        
        // Create mutable ArrayLists to compare the player's guess pattern to
        // the codemaster's hidden pattern.
        ArrayList<CodeColor> guessList =
                new ArrayList<>(Arrays.asList(player.getGuessPattern()));
        ArrayList<CodeColor> hiddenList =
                new ArrayList<>(Arrays.asList(hiddenPattern));
        
        // Create Iterator objects to go through each element.
        Iterator<CodeColor> guess = guessList.iterator();
        Iterator<CodeColor> hidden = hiddenList.iterator();
        
        // Check for the existence of a correct color in the correct position.
        while (guess.hasNext())
            if (guess.next().equals(hidden.next())) {
                keys[keysLength] = KeyColor.BLACK;
                guess.remove();
                hidden.remove();
                keysLength++;
            }
        
        // Check for the existence of a correct color in an incorrect position.
        for (CodeColor element : guessList) {
            hidden = hiddenList.iterator();
            while (hidden.hasNext()) {
                if (element.equals(hidden.next())) {
                    keys[keysLength] = KeyColor.WHITE;
                    hidden.remove();
                    keysLength++;
                    break;
                }
            }
        }
        
        // Truncate the null values.
        keys = Arrays.copyOf(keys, keysLength);
        
        // Return the array.
        return keys;
    }
}