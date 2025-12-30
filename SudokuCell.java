package com.sudoku.gui;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


/**
 * A custom JavaFX TextField representing a single cell in the Sudoku grid.
 * It enforces input rules and holds its board coordinates.
 */
public class SudokuCell extends TextField {
    private final int row;
    private final int col;
    private final GameController controller;
    private boolean isInitial = false;

    public SudokuCell(int row, int col, GameController controller) {
        this.row = row;
        this.col = col;
        this.controller = controller;

        // Basic styling and alignment
        setFont(Font.font("Arial", FontWeight.BOLD, 20));
        setAlignment(javafx.geometry.Pos.CENTER);
        setPrefSize(50, 50);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Add event handling for input
        this.addEventFilter(KeyEvent.KEY_TYPED, this::handleInput);
    }

    /**
     * Handles key input: only allows single digits (1-9) or clearing (0/empty).
     * @param event The key event triggered by the user.
     */
    private void handleInput(KeyEvent event) {
        if (isInitial) {
            // Cannot modify initial given numbers
            event.consume();
            return;
        }

        String input = event.getCharacter();
        if (input.matches("[1-9]")) {
            // If a valid digit is typed, set the text and process the move
            setText(input);
            controller.handleInput(row, col, Integer.parseInt(input));
            event.consume(); // Consume the event to prevent the default text field behavior
        } else if (input.matches("[\b\u007F]")) { // Backspace or Delete
            // Allow clearing the cell
            setText("");
            controller.handleInput(row, col, 0);
        } else {
            // Block all other keys (letters, symbols, etc.)
            event.consume();
        }
    }

    /**
     * Sets the cell value and styles it based on whether it is an initial 'given' number.
     * @param value The number (1-9) or 0 (empty).
     * @param initial True if the number is part of the original puzzle.
     */
    public void setValue(int value, boolean initial) {
        this.isInitial = initial;
        setText(value == 0 ? "" : String.valueOf(value));

        if (initial) {
            // Given numbers are styled differently and cannot be edited
            setStyle("-fx-control-inner-background: #eeeeee; -fx-text-fill: #333333; -fx-font-weight: bold;");
            setEditable(false);
        } else {
            // User input style
            setStyle("-fx-control-inner-background: white; -fx-text-fill: #0055aa; -fx-font-weight: normal;");
            setEditable(true);
        }
    }
    
    /**
     * Styles the cell to indicate a valid move (correct color).
     */
    public void setValidStyle() {
        if (!isInitial) {
            setStyle("-fx-control-inner-background: #e6ffec; -fx-text-fill: #008000; -fx-font-weight: normal;"); // Light green for valid user input
        }
    }

    /**
     * Styles the cell to indicate an invalid move (error color).
     */
    public void setErrorStyle() {
        if (!isInitial) {
            setStyle("-fx-control-inner-background: #ffcccc; -fx-text-fill: #cc0000; -fx-font-weight: normal;"); // Light red for invalid input
        }
    }
}