package com.sudoku.game.advanced_sudoku_game;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * The main application class for the Advanced Sudoku Game.
 * This class sets up the primary JavaFX window and the 9x9 Sudoku grid.
 */
public class App extends Application {

    // Global constant for Sudoku size
    private static final int SIZE = 9;

    /**
     * Creates and styles the 9x9 Sudoku grid using GridPane.
     * @return The GridPane containing all 81 input fields.
     */
    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        
        // Styling the overall grid
        grid.setStyle("-fx-border-color: #333333; -fx-border-width: 3; -fx-background-color: #f7f7f7;");

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                // Create a TextField for each cell
                TextField cell = new TextField();
                
                // Configure appearance and behavior
                cell.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                cell.setAlignment(Pos.CENTER);
                cell.setPrefSize(50, 50); // Fixed size for the cell
                cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Allows resizing within GridPane
                
                // Add padding for cell separation
                String style = "-fx-padding: 0;"; 
                
                // Add thick borders to delineate the 3x3 sub-grids (Sudoku regions)
                // Top border for the 0th, 3rd, and 6th row
                if (row % 3 == 0) {
                    style += "-fx-border-top: 2px solid #333333;";
                }
                // Left border for the 0th, 3rd, and 6th column
                if (col % 3 == 0) {
                    style += "-fx-border-left: 2px solid #333333;";
                }
                // Add a light border to separate the single cells
                style += "-fx-border-color: #aaaaaa; -fx-border-width: 0.5; -fx-border-style: solid; -fx-border-radius: 0;";

                // Correct the style for the first row/column to prevent double borders on the outside edge
                if (row == 0) {
                     style = style.replace("-fx-border-top: 2px solid #333333;", "-fx-border-top: 0;");
                }
                if (col == 0) {
                     style = style.replace("-fx-border-left: 2px solid #333333;", "-fx-border-left: 0;");
                }
                
                // The outer grid border applied above takes care of the boundary.
                // Apply the finalized style
                cell.setStyle(style);
                
                // Add the cell to the grid
                grid.add(cell, col, row);
            }
        }
        
        return grid;
    }


    // --- Core JavaFX Setup ---
    
    @Override
    public void start(Stage primaryStage) {
        // 1. Create the Sudoku Grid UI
        GridPane sudokuGrid = createGrid();
        
        // Use a BorderPane as the root to allow for easy placement of the grid (center)
        // and future control buttons (bottom) or headers (top).
        BorderPane root = new BorderPane();
        root.setCenter(sudokuGrid);
        
        // 2. Create the Scene
        // Give the scene a bit more size to accommodate the grid comfortably
        Scene scene = new Scene(root, 650, 700); 

        // 3. Set up the Stage (the main window frame)
        primaryStage.setTitle("Advanced Sudoku Solver (DL4J)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main method to launch the application.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}