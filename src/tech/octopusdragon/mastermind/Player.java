package tech.octopusdragon.mastermind;

/**
 * Player class for the game of Master Mind
 * @author Alex
 */
public class Player {
    private CodeColor[] guessPattern;   // The player's guess
    
    /**
     * Constructor
     * @param numHoles The number of holes in the pattern.
     */
    public Player(int numHoles) {
        guessPattern = new CodeColor[numHoles];
    }
    
    /**
     * The setGuessPattern assigns the specified guess to guessPattern.
     * @param guess The player's guess.
     */
    public void setGuessPattern(CodeColor[] guess) {
        for (int i = 0; i < guess.length; i++)
            guessPattern[i] = guess[i];
    }
    
    /**
     * The getGuessPattern returns the player's guess.
     * @return The values stored int guessPattern.
     */
    public CodeColor[] getGuessPattern() {
        return guessPattern;
    }
}
