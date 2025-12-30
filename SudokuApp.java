package com.sudoku.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * The main JavaFX Application class. Sets up the primary stage, scene, 
 * and wires the GameController and BoardView together.
 */
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main JavaFX Application class. Sets up the primary stage, scene, 
 * and wires the GameController and BoardView together.
 */
public class SudokuApp extends Application {
    
    private GameController controller;
    
    @Override
    public void start(Stage primaryStage) {
        // 1. Initialize Controller and View
        controller = new GameController();
        BoardView boardView = new BoardView(controller);
        controller.setView(boardView); // Link the view back to the controller

        // 2. Create UI Elements (Header and Controls)
        Label headerLabel = new Label("Advanced Sudoku Solver (DL4J Ready)");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // --- Difficulty Buttons ---
        // Easy: 40 removals
        Button newGameEasyButton = createButton("Easy", () -> controller.newGame(40), "#007bff"); 
        // Medium: 50 removals
        Button newGameMediumButton = createButton("Medium", () -> controller.newGame(50), "#ffc107"); 
        // Hard: 60 removals
        Button newGameHardButton = createButton("Hard", () -> controller.newGame(60), "#dc3545"); 

        // 3. Layout Controls
        HBox difficultyBox = new HBox(15, newGameEasyButton, newGameMediumButton, newGameHardButton);
        difficultyBox.setAlignment(Pos.CENTER);
        difficultyBox.setPadding(new Insets(10, 10, 0, 10)); // Top padding

        // --- Action Buttons (Hint, Solve, AI Solve) ---
        Button hintButton = createButton("Get Hint (Logic)", controller::getHint, "#17a2b8");
        Button solveButton = createButton("Solve (Backtracking)", controller::solveBoard, "#6c757d");
        Button aiSolveButton = createButton("Solve with AI (DL4J)", controller::solveWithAI, "#28a745");
        
        // --- New: Solution Check Button ---
        Button checkSolutionButton = createButton("Check Solution", controller::checkSolution, "#4a9c4a");

        HBox actionBox1 = new HBox(15, hintButton, solveButton, aiSolveButton);
        actionBox1.setAlignment(Pos.CENTER);
        actionBox1.setPadding(new Insets(10, 10, 0, 10));
        
        HBox actionBox2 = new HBox(15, checkSolutionButton);
        actionBox2.setAlignment(Pos.CENTER);
        actionBox2.setPadding(new Insets(0, 10, 10, 10));

        // Combine all controls into a VBox for the bottom section
        VBox bottomContent = new VBox(10, difficultyBox, actionBox1, actionBox2);
        bottomContent.setAlignment(Pos.CENTER);
        bottomContent.setPadding(new Insets(10));


        // 4. Assemble the Root Layout (BorderPane)
        BorderPane root = new BorderPane();
        root.setTop(headerLabel);
        BorderPane.setAlignment(headerLabel, Pos.CENTER);
        BorderPane.setMargin(headerLabel, new Insets(20, 0, 10, 0));
        
        root.setCenter(boardView);
        root.setBottom(bottomContent); // Use the combined VBox content
        
        // 5. Setup Stage and Show
        Scene scene = new Scene(root, 700, 850); // Adjusted height for new button row
        primaryStage.setTitle("Advanced Sudoku Solver");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Helper function to create styled buttons with a custom color.
     */
    private Button createButton(String text, Runnable action, String color) {
        Button button = new Button(text);
        button.setPrefWidth(150);
        button.setPrefHeight(40);
        button.setOnAction(e -> action.run());
        // Use the passed color for styling
        button.setStyle("-fx-font-weight: bold; -fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 5;");
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}