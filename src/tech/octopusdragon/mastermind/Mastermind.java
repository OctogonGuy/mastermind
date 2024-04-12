package tech.octopusdragon.mastermind;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.event.*;
import javafx.scene.input.*;

/**
 * This program simulates a game of Master Mind.
 * @author Alex
 */
public class Mastermind extends Application {
    final static int NUM_SLOTS = 4;     			// The default number of peg holes per row
    final static int NUM_COLORS = 6;    			// The default number of peg colors to be used
    final static int NUM_ROWS = 6;      			// The default number of rows of holes
    final static boolean REPEATING_COLORS = false;	// The default value of whether or not to repeat colors
    
    final static double RADIUS = 25.0;  		// The radius of the holes
    final static double GRIDH_SPACING = 10.0;	// The horizontal spacing between grid elements
    final static double GRIDV_SPACING = 8.0;	// The vertical spacing between grid elements
    final static double H_SPACING = 8.0;		// The spacing between hbox elements
    final static double V_SPACING = 20.0;		// The spacing between vbox elements
    final static double PADDING = 30.0; 		// The padding
    
    // Fields
    MenuBar menuBar;
    Menu gameMenu;
    MenuItem newGameItem;
    MenuItem settingsItem;
    MenuItem exitItem;
    Menu helpMenu;
    MenuItem howToPlayItem;
    
    Button backButton;
    Button confirmButton;
    Label messageLabel;
    Label footerText;
    Group[] selectionRow;
    Circle[] hiddenRow;
    Circle[][] codePegs;
    Circle[][] keyPegs;
    HBox selectionBox;
    HBox hiddenBox;
    HBox buttonBox;
    GridPane decodingBoard;
    GridPane[] keyPegGroups;
    BorderPane userInterface;
    
    Player player;
    boolean finished;
    Codemaster codemaster;
    CodeColor[] hiddenPattern;
    int currentRow;
    int currentCol;
    int numSlots;
    int numColors;
    int numRows;
    boolean repeatingColors;

    
    
    public static void main(String[] args) {
        launch();
    }
    
    
    
    @Override
    public void start(Stage primaryStage) {
        // Initialize some fields.
        numSlots = NUM_SLOTS;
        numColors = NUM_COLORS;
        numRows = NUM_ROWS;
        repeatingColors = REPEATING_COLORS;
        
        // Create the menu bar.
        menuBar = new MenuBar();
        gameMenu = new Menu("Game");
        newGameItem = new MenuItem("New Game");
        newGameItem.setOnAction(event -> {
        	newGame(primaryStage);
        });
        gameMenu.getItems().add(newGameItem);
        settingsItem = new MenuItem("Settings");
        settingsItem.setOnAction(event -> {
        	showSettings();
        });
        gameMenu.getItems().add(settingsItem);
        exitItem = new MenuItem("Exit");
        exitItem.setOnAction(event -> {
        	primaryStage.close();
        });
        gameMenu.getItems().add(exitItem);
        menuBar.getMenus().add(gameMenu);
        helpMenu = new Menu("Help");
        howToPlayItem = new MenuItem("How to Play");
        howToPlayItem.setOnAction(event -> {
        	showHowToPlay();
        });
        helpMenu.getItems().add(howToPlayItem);
        menuBar.getMenus().add(helpMenu);
        
        // Create a header to show messages to the player.
        messageLabel = new Label();
        
        // Create the back and confirm buttons
        backButton = new Button("Back");
        backButton.setOnAction(new BackButtonHandler());
        backButton.setDisable(true);
        confirmButton = new Button("Confirm");
        confirmButton.setOnAction(new ConfirmButtonHandler());
        confirmButton.setDisable(true);
        buttonBox = new HBox(H_SPACING, backButton, confirmButton);
        buttonBox.setAlignment(Pos.CENTER);
        
        // Create a footer showing the current configurations.
        footerText = new Label("Repeating colors: " + (repeatingColors ? "Yes" : "No") +
                                     "\tSlots: " + numSlots +
                                     "\tColors: " + numColors);
        HBox footerBox = new HBox(footerText);
        footerBox.getStyleClass().add("background-white");
        
        // Create a BorderPane encompassing the whole user interface.
        userInterface = new BorderPane();
        userInterface.setTop(menuBar);
        userInterface.setBottom(footerBox);
        
        // Create the Scene and display it.
        Scene scene = new Scene(userInterface);
        primaryStage.setScene(scene);
        // Start the game
        newGame(primaryStage);
        // Display the Scene
        primaryStage.setTitle("Mastermind");
        primaryStage.show();
    }
    
    
    
    /**
     * The showSettings method brings up a new window with settings.
     */
    public void showSettings() {
    	Stage secondaryStage = new Stage();
    	
    	// Create Label and ComboBox for setting repeating colors.
    	Label repeatingColorsLabel = new Label("Repeating colors");
    	ComboBox<String> repeatingColorsComboBox = new ComboBox<String>();
    	repeatingColorsComboBox.getItems().addAll("Yes", "No");
    	repeatingColorsComboBox.setValue(repeatingColors ? "Yes" : "No");
    	
    	// Create Label and Spinner for setting slots.
    	Label slotsLabel = new Label("Number of slots");
    	Spinner<Integer> slotsSpinner = new Spinner<Integer>();
    	SpinnerValueFactory<Integer> slots =
    			new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 6, numSlots);
    	slotsSpinner.setValueFactory(slots);
    	slotsSpinner.setPrefWidth(60.0);
    	
    	// Create Label and Spinner for setting colors.
    	Label colorsLabel = new Label("Number of colors");
    	Spinner<Integer> colorsSpinner = new Spinner<Integer>();
    	SpinnerValueFactory<Integer> colors =
    			new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 8, numColors);
    	colorsSpinner.setValueFactory(colors);
    	colorsSpinner.setPrefWidth(60.0);
    	
    	// Create a warning Label
    	Label warningLabel = new Label("*New values for settings will only " +
    			"take effect once you start a new game.");
    	warningLabel.setWrapText(true);
    	warningLabel.setPrefWidth(400);
    	
    	// Create a cancel Button and a save changes Button
    	Button cancelButton = new Button("Cancel");
    	cancelButton.setOnAction(event -> {
    		secondaryStage.close();
    	});
    	Button saveChangesButton = new Button("Confirm");
    	saveChangesButton.setOnAction(event -> {
    		// Set repeating colors
    		if (repeatingColorsComboBox.getValue().equals("Yes"))
    			repeatingColors = true;
    		else
    			repeatingColors = false;
    		// Set number of slots
    		numSlots = slotsSpinner.getValue();
    		// Set number of colors
    		numColors = colorsSpinner.getValue();
    		// Make sure no error
    		if (numColors < numSlots && repeatingColors == false)
    			repeatingColors = true;
    		secondaryStage.close();
    	});
    	HBox buttonBox = new HBox(H_SPACING, cancelButton, saveChangesButton);
    	buttonBox.setAlignment(Pos.CENTER);
    	
    	// Add the elements to a VBox
    	VBox vbox = new VBox(V_SPACING, repeatingColorsLabel, repeatingColorsComboBox,
    			slotsLabel, slotsSpinner, colorsLabel, colorsSpinner,
    			warningLabel, buttonBox);
    	vbox.setAlignment(Pos.CENTER_LEFT);
    	vbox.setPadding(new Insets(PADDING));
    	vbox.setStyle("-fx-background-color: lightgray");
    	
    	// Create the scene and display it.
    	Scene scene = new Scene(vbox);
    	secondaryStage.setScene(scene);
    	secondaryStage.setTitle("Settings");
    	secondaryStage.show();
    }
    
    
    
    /**
     * The showHowToPlay method brings up a new window with instructions of how
     * to play Mastermind.
     */
    public void showHowToPlay() {
    	Stage secondaryStage = new Stage();
    	
    	// Create the Label with the instructions.
    	Label howToPlay = new Label("\tMastermind is a code-breaking game " +
    				"in which, typically, two players take turns placing pegs " +
    				"in rows of holes on a decoding board, trying to guess a " +
    				"hidden pattern of pegs.\n\n\tIn this single-player digital " +
    				"version, a hidden pattern of colored pegs will be created " +
    				"at the start of the game. You have six rows to guess the " +
    				"pattern by clicking the colored buttons at the bottom to " +
    				"put a peg of the corresponding color in the next slot of " +
    				"the current row. After you click the Confirm button, the " +
    				"computer will check your guess and place a series of key " +
    				"pegs to the right of your code pegs. Every white peg you " +
    				"see to the right represents a peg that has the correct " +
    				"color but is in the incorrect position. A black peg " +
    				"represents a correct-colored peg in the correct position. If you " +
    				"guess the pattern correctly, or if you reach the last row " +
    				"without guessing the pattern correctly, the hidden " +
    				"pattern will be revealed, and the game will end.\n\n\tYou can " +
    				"start a new game by clicking the New Game button under " +
    				"the Game menu.\n\n\tYou can also change various settings, such " +
    				"as whether or not to include repeating colors in the " +
    				"pattern, the number of slots in the pattern, and the " +
    				"number of colors. If this is your first time playing, I " +
    				"would recommend you start out with the default settings, " +
    				"with no repeating colors, 4 slots, and 6 colors. If you " +
    				"want more of a challenge, try turning repeating colors " +
    				"off. If you want to up the difficulty even more, try " +
    				"increasing the number of colors.");
    	howToPlay.setWrapText(true);
    	howToPlay.setPrefWidth(600);
    	
    	// Create an OK button to close the window.
    	Button okButton = new Button("OK");
    	okButton.setOnAction(event -> {
    		secondaryStage.close();
    	});
    	
    	// Put the TextArea and the Button in a VBox.
    	VBox vbox = new VBox(V_SPACING, howToPlay, okButton);
    	vbox.setAlignment(Pos.CENTER);
    	vbox.setPadding(new Insets(PADDING));
        vbox.setStyle("-fx-background-color: lightgray");
    	
    	// Create the Scene and display it.
    	Scene scene = new Scene(vbox);
    	secondaryStage.setScene(scene);
    	secondaryStage.setTitle("How to Play");
    	secondaryStage.show();
    }
    
    
    
    /**
     * The constructButton method constructs a simple graphic
     * of a button for the selection row.
     * @param scene The scene
     * @param color The color of the button to make.
     */
    public Group constructButton(Scene scene, CodeColor codeColor) {
    	Color buttonColor;
    	
    	// Constants for the Circles.
		double BODY_RADIUS = RADIUS * .9 ;
		double INNER_FRAME_RADIUS = BODY_RADIUS + 5.0;
		double OUTER_FRAME_RADIUS = BODY_RADIUS + 10.0;
		
		// Determine the color of buttonColor
		switch (codeColor) {
	        case RED:
	            buttonColor = Color.RED;
	            break;
	        case BLUE:
	            buttonColor = Color.BLUE;
	            break;
	        case GREEN:
	            buttonColor = Color.GREEN;
	            break;
	        case YELLOW:
	            buttonColor = Color.YELLOW;
	            break;
	        case PURPLE:
	            buttonColor = Color.PURPLE;
	            break;
	        case ORANGE:
	            buttonColor = Color.ORANGE;
	            break;
	        case CYAN:
	        	buttonColor = Color.CYAN;
	        	break;
	        default:
	        	buttonColor = Color.MAGENTA;
	    }
		
		// Create the frame for the button.
		Circle outerFrame = new Circle(OUTER_FRAME_RADIUS, Color.GRAY);
		
		Circle innerFrame = new Circle(INNER_FRAME_RADIUS, Color.GRAY);
		
		// Add inner shadow effects to the frames.
		InnerShadow outerFrameShadow = new InnerShadow();
		outerFrameShadow.setColor(new Color(0.0, 0.0, 0.0, 0.5));
		outerFrameShadow.setOffsetX(OUTER_FRAME_RADIUS / 3);
		outerFrameShadow.setOffsetY(-OUTER_FRAME_RADIUS / 3);
		outerFrameShadow.setRadius(OUTER_FRAME_RADIUS);
		outerFrame.setEffect(outerFrameShadow);
		
		InnerShadow innerFrameShadow = new InnerShadow();
		innerFrameShadow.setOffsetX(-INNER_FRAME_RADIUS / 3);
		innerFrameShadow.setColor(new Color(0.0, 0.0, 0.0, 0.5));
		innerFrameShadow.setOffsetY(INNER_FRAME_RADIUS / 3);
		innerFrameShadow.setRadius(INNER_FRAME_RADIUS);
		innerFrame.setEffect(innerFrameShadow);
		
		// Create the body of the button.
		Circle body = new Circle(BODY_RADIUS, buttonColor);
		
		// Add inner shadow effects for when the button is pressed and released.
		InnerShadow releasedShadow = new InnerShadow();
		releasedShadow.setColor(new Color(0.0, 0.0, 0.0, 0.5));
		releasedShadow.setOffsetX(BODY_RADIUS / 3);
		releasedShadow.setOffsetY(-BODY_RADIUS / 3);
		releasedShadow.setRadius(BODY_RADIUS);
		body.setEffect(releasedShadow);
		
		InnerShadow pressedShadow = new InnerShadow();
		pressedShadow.setColor(new Color(0.0, 0.0, 0.0, 0.5));
		pressedShadow.setOffsetX(-BODY_RADIUS / 3);
		pressedShadow.setOffsetY(BODY_RADIUS / 3);
		pressedShadow.setRadius(BODY_RADIUS);
		
		// Add the button components to a group
		Group button = new Group(outerFrame, innerFrame, body);
		
		// Register event handlers to the button.
		button.setOnMouseEntered(event ->
		{
			scene.setCursor(Cursor.HAND);
		});
		
		button.setOnMouseExited(event ->
		{
			scene.setCursor(Cursor.DEFAULT);
		});
		
		button.setOnMousePressed(event ->
		{
			body.setEffect(pressedShadow);
		});
		
		button.setOnMouseReleased(event ->
		{
			body.setEffect(releasedShadow);
		});
		
		// Return the button components
		return button;
    }
    
    
    
    /**
     * This constructPeg method constructs a simple graphic
     * of a code peg for the decoding board and hidden pattern.
     * @param color The color of the code peg to make.
     */
    public Circle constructPeg(CodeColor codeColor) {
    	Color pegColor;
		
		// Determine the color of buttonColor
		switch (codeColor) {
	        case RED:
	            pegColor = Color.RED;
	            break;
	        case BLUE:
	            pegColor = Color.BLUE;
	            break;
	        case GREEN:
	            pegColor = Color.GREEN;
	            break;
	        case YELLOW:
	            pegColor = Color.YELLOW;
	            break;
	        case PURPLE:
	            pegColor = Color.PURPLE;
	            break;
	        case ORANGE:
	            pegColor = Color.ORANGE;
	            break;
	        case CYAN:
	        	pegColor = Color.CYAN;
	        	break;
	        default:
	        	pegColor = Color.MAGENTA;
	    }
		
		// Create the body of the peg.
		Circle peg = new Circle(RADIUS, pegColor);
		
		// Add inner shadow effects.
		InnerShadow shadow = new InnerShadow();
		shadow.setColor(new Color(0.0, 0.0, 0.0, 0.5));
		shadow.setOffsetX(RADIUS / 3);
		shadow.setOffsetY(-RADIUS / 3);
		shadow.setRadius(RADIUS);
		peg.setEffect(shadow);
		
		// Return the button components
		return peg;
    }
    
    
    
    /**
     * This constructPeg method constructs a simple graphic
     * of a key peg for the decoding board and hidden pattern.
     * @param color The color of the key peg to make.
     */
    public Circle constructPeg(KeyColor keyColor) {
    	Color pegColor;
		
		// Determine the color of buttonColor
		switch (keyColor) {
	        case BLACK:
	            pegColor = Color.BLACK;
	            break;
	        default:
	            pegColor = Color.WHITE;
	    }
		
		// Create the body of the peg.
		Circle peg = new Circle(RADIUS, pegColor);
		
		// Add inner shadow effects.
		InnerShadow shadow = new InnerShadow();
		shadow.setOffsetX(RADIUS / 3);
		shadow.setOffsetY(-RADIUS / 3);
		shadow.setRadius(RADIUS);
		peg.setEffect(shadow);
		
		// Return the button components
		return peg;
    }
    
    
    
    /**
     * This overloaded version of the fillWithColor method fills the specified
     * array of Circles with the specified pattern of code peg colors.
     * @param circles The array of Circles to color.
     * @param pattern The CodeColor pattern to fill with.
     */
    public void fillWithColor(Circle[] circles, CodeColor[] pattern) {
        for (int i = 0; i < circles.length; i++) {
            switch (pattern[i]) {
                case RED:
                    circles[i].setFill(Color.RED);
                    break;
                case BLUE:
                    circles[i].setFill(Color.BLUE);
                    break;
                case GREEN:
                    circles[i].setFill(Color.GREEN);
                    break;
                case YELLOW:
                    circles[i].setFill(Color.YELLOW);
                    break;
                case PURPLE:
                    circles[i].setFill(Color.PURPLE);
                    break;
                case ORANGE:
                    circles[i].setFill(Color.ORANGE);
                    break;
                case CYAN:
                	circles[i].setFill(Color.CYAN);
                	break;
                default:
                	circles[i].setFill(Color.MAGENTA);
            }
        }
    }
    
    
    
    /**
     * This overloaded version of the fillWithColor method fills the specified
     * array of Circles with the specified pattern of key peg colors.
     * @param circles The array of Circles to color.
     * @param pattern The KeyColor pattern to fill with.
     */
    public void fillWithColor(Circle[] circles, KeyColor[] pattern) {
        for (int i = 0; i < pattern.length; i++) {
            switch (pattern[i]) {
                case BLACK:
                    circles[i].setFill(Color.BLACK);
                    break;
                default:
                    circles[i].setFill(Color.WHITE);
            }
    		InnerShadow shadow = new InnerShadow();
    		shadow.setColor(new Color(0.0, 0.0, 0.0, 0.5));
    		shadow.setOffsetX(RADIUS / 9);
    		shadow.setOffsetY(-RADIUS / 9);
    		shadow.setRadius(RADIUS / 3);
    		circles[i].setEffect(shadow);
        }
    }
    
    
    
    /**
     * The toCodeColor method converts the colors of an array of Circles to an
     * array of CodeColors.
     * @param circles The array of Circles.
     * @return The pattern of CodeColors.
     */
    public CodeColor[] toCodeColor(Circle[] circles) {
        CodeColor[] guess = new CodeColor[numSlots];
        for (int i = 0; i < circles.length; i++) {
            if (circles[i].getFill().equals(Color.RED))
                guess[i] = CodeColor.RED;
            else if (circles[i].getFill().equals(Color.BLUE))
                guess[i] = CodeColor.BLUE;
            else if (circles[i].getFill().equals(Color.GREEN))
                guess[i] = CodeColor.GREEN;
            else if (circles[i].getFill().equals(Color.YELLOW))
                guess[i] = CodeColor.YELLOW;
            else if (circles[i].getFill().equals(Color.PURPLE))
                guess[i] = CodeColor.PURPLE;
            else if (circles[i].getFill().equals(Color.ORANGE))
                guess[i] = CodeColor.ORANGE;
            else if (circles[i].getFill().equals(Color.CYAN))
                guess[i] = CodeColor.CYAN;
            else
            	guess[i] = CodeColor.MAGENTA;
        }
        
        return guess;
    }
    
    
    
    /**
     * The newGame method starts a new game.
     */
    public void newGame(Stage stage) {
        // Reset the player.
        player = new Player(numSlots);
        currentRow = currentCol = 0;
        finished = false;
        
        // Get a code from a codemaster.
        codemaster = new Codemaster(repeatingColors, numSlots, numColors);
        codemaster.makeHiddenPattern();
        hiddenPattern = codemaster.getHiddenPattern();
        
        // Reset the text of the header and footer.
        messageLabel.setText("");
        footerText.setText("Repeating colors: " + (repeatingColors ? "Yes" : "No") +
	                         "\tSlots: " + numSlots +
	                         "\tColors: " + numColors);
        
        // Create GridPanes containing key holes for each row.
        keyPegs = new Circle[numRows][numSlots];
        keyPegGroups = new GridPane[numRows];
        for (int i = 0; i < keyPegGroups.length; i++) {
            keyPegGroups[i] = new GridPane();
            keyPegGroups[i].setAlignment(Pos.CENTER);
            keyPegGroups[i].setHgap(GRIDH_SPACING / 2);
            keyPegGroups[i].setVgap(GRIDV_SPACING / 2);
            for (int j = 0; j < numSlots; j++) {
                keyPegs[i][j] = new Circle(RADIUS / 3, Color.GRAY);
	            InnerShadow shadow = new InnerShadow();
	    		shadow.setColor(new Color(0.0, 0.0, 0.0, 0.5));
	    		shadow.setOffsetX(-RADIUS / 15);
	    		shadow.setOffsetY(RADIUS / 15);
	    		shadow.setRadius(RADIUS / 9);
	    		keyPegs[i][j].setEffect(shadow);
                keyPegGroups[i].add(keyPegs[i][j], j, 0);
            }
        }
        
        // Create a GridPane containing the code holes and key groups.
        codePegs = new Circle[numRows][numSlots];
        decodingBoard = new GridPane();
        decodingBoard.setHgap(GRIDH_SPACING);
        decodingBoard.setVgap(GRIDV_SPACING);
        for (int i = 0; i < numRows; i++) {
        	for (int j = 0; j < numSlots; j++) {
                codePegs[i][j] = new Circle(RADIUS, Color.GRAY);
	            InnerShadow shadow = new InnerShadow();
	    		shadow.setColor(new Color(0.0, 0.0, 0.0, 0.5));
	    		shadow.setOffsetX(-RADIUS / 5);
	    		shadow.setOffsetY(RADIUS / 5);
	    		shadow.setRadius(RADIUS / 3);
	    		codePegs[i][j].setEffect(shadow);
                decodingBoard.add(codePegs[i][j], j, i);
            }
            decodingBoard.add(keyPegGroups[i], numSlots, i);
        }
        decodingBoard.setAlignment(Pos.CENTER);
        
        // Create the hidden row.
        hiddenRow = new Circle[numSlots];
        hiddenBox = new HBox(H_SPACING);
        for (int i = 0; i < numSlots; i++) {
            hiddenRow[i] = constructPeg(hiddenPattern[i]);
            hiddenBox.getChildren().add(hiddenRow[i]);
        }
        hiddenBox.setAlignment(Pos.CENTER);
        hiddenBox.setStyle("-fx-border-color: black;");
        for (Circle c : hiddenRow)
            c.setVisible(false);

        // Create the color selection row.
        selectionRow = new Group[numColors];
        selectionBox = new HBox(H_SPACING);
        for (int i = 0; i < numColors; i++) {
            selectionRow[i] = constructButton(stage.getScene(), CodeColor.values()[i]);
            selectionBox.getChildren().add(selectionRow[i]);
        }
        selectionBox.setAlignment(Pos.CENTER);
        for (Group b : selectionRow)
            b.setOnMouseClicked(new ColorClickHandler());
        
        // Create a VBox containing all of the parts of the decoding board.
        VBox table = new VBox(V_SPACING);
        table.getChildren().add(messageLabel);
        table.getChildren().add(decodingBoard);
        table.getChildren().add(hiddenBox);
        table.getChildren().add(selectionBox);
        table.getChildren().add(buttonBox);
        table.setAlignment(Pos.CENTER);
        table.setPadding(new Insets(PADDING));
        table.setStyle("-fx-background-color: lightgray");
        userInterface.setCenter(table);
        stage.sizeToScene();
    }
    
    
    
    /**
     * Event handler class for color click
     */
    public class ColorClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
        	// Check to see if the player can go forward a row.
            if (!finished && currentCol < numSlots) {
            	
	            // Change the color of the current hole.
	            codePegs[currentRow][currentCol].setFill(((Circle)((Group)event.getSource()).getChildren().get(2)).getFill());
	            
	            // Add inner shadow effects.
	    		InnerShadow shadow = new InnerShadow();
	    		shadow.setColor(new Color(0.0, 0.0, 0.0, 0.5));
	    		shadow.setOffsetX(RADIUS / 3);
	    		shadow.setOffsetY(-RADIUS / 3);
	    		shadow.setRadius(RADIUS);
	    		codePegs[currentRow][currentCol].setEffect(shadow);
	            
	            // Increment the current column.
	            currentCol++;
	            
	            // Enable the back button
	            backButton.setDisable(false);
            }
            
            // If the player is in the last column, enable the confirm button.
            if (currentCol == numSlots)
            	confirmButton.setDisable(false);
        }
    }
    
    
    
    /**
     * Event handler for back button
     */
    public class BackButtonHandler implements EventHandler<ActionEvent> {
    	@Override
    	public void handle(ActionEvent event) {
    		// Check to see if the player can go back a row.
    		if (currentCol != 0) {
    			
	            // Decrement the current column.
	            currentCol--;
	            
	    		// Change the color of the current back to gray and set the shadow.
	            codePegs[currentRow][currentCol].setFill(Color.GRAY);
	            InnerShadow shadow = new InnerShadow();
	    		shadow.setColor(new Color(0.0, 0.0, 0.0, 0.5));
	    		shadow.setOffsetX(-RADIUS / 5);
	    		shadow.setOffsetY(RADIUS / 5);
	    		shadow.setRadius(RADIUS / 3);
	    		codePegs[currentRow][currentCol].setEffect(shadow);
	            
	            // Disable the confirm button
	            confirmButton.setDisable(true);
    		}
    		
    		// If the player is in the first column, disable the back button.
    		if (currentCol == 0)
    			backButton.setDisable(true);
    	}
    }
    
    
    
    /**
     * Event handler for confirm button
     */
    public class ConfirmButtonHandler implements EventHandler<ActionEvent> {
    	@Override
    	public void handle(ActionEvent event) {
    		// Check to see if the row has been completed.
            if (currentCol == numSlots) {
                KeyColor[] feedbackPattern;
                
            	// Set the player's guess.
                player.setGuessPattern(toCodeColor(codePegs[currentRow]));
            	
                currentCol = 0;
                // Check the guess against the hidden pattern.
                feedbackPattern = codemaster.checkGuess(player);
                fillWithColor(keyPegs[currentRow], feedbackPattern);
                
                // Increment the current row, resetting the column to 0.
                currentRow++;
                currentCol = 0;
                
                // Check to see if the player has won or lost the game.
                if (feedbackPattern.length == numSlots &&
                        feedbackPattern[numSlots - 1].equals(KeyColor.BLACK)) {
                    finished = true;
                    messageLabel.setText("Congratulations! You WON!");
                } else if (currentRow == numRows) {
                	finished = true;
                	messageLabel.setText("You lost. Better luck next time.");
                }
                
                // End the game if the player finished the game.
                if (finished)
                    for (Circle c : hiddenRow)
                        c.setVisible(true);
                
                // Disable the confirm button.
                confirmButton.setDisable(true);
                
                // Disable the back button.
                backButton.setDisable(true);
            }
    	}
    }
}