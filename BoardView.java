package com.sudoku.gui;

import com.sudoku.game.advanced_sudoku_game.core.SudokuBoard;
import com.sudoku.game.advanced_sudoku_game.solver.HintEngine.Hint;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 * The main visual component (View) for the Sudoku board. 
 * It contains 81 SudokuCell objects arranged in a GridPane.
 */
public class BoardView extends BorderPane {
    private static final int SIZE = 9;
    final SudokuCell[][] cells = new SudokuCell[SIZE][SIZE];
    private final GameController controller;
    private final GridPane grid;

    public BoardView(GameController controller) {
        this.controller = controller;
        this.grid = createGridPane();
        setCenter(this.grid);
        setPadding(new Insets(20));
    }

    /**
     * Initializes the nested GridPane structure for the 9x9 board with 3x3 box separation.
     * @return The constructed GridPane.
     */
    private GridPane createGridPane() {
        GridPane mainGrid = new GridPane();
        mainGrid.setAlignment(Pos.CENTER);
        mainGrid.setHgap(3); // Gap between 3x3 boxes
        mainGrid.setVgap(3); // Gap between 3x3 boxes
        mainGrid.setStyle("-fx-background-color: #333333; -fx-padding: 3; -fx-background-radius: 5;");

        for (int blockRow = 0; blockRow < 3; blockRow++) {
            for (int blockCol = 0; blockCol < 3; blockCol++) {
                // Create a 3x3 inner grid (the box)
                GridPane innerGrid = new GridPane();
                innerGrid.setStyle("-fx-border-color: #bbbbbb; -fx-border-width: 1; -fx-background-color: white;");
                
                for (int cellRow = 0; cellRow < 3; cellRow++) {
                    for (int cellCol = 0; cellCol < 3; cellCol++) {
                        int row = blockRow * 3 + cellRow;
                        int col = blockCol * 3 + cellCol;
                        
                        // Create and store the custom cell
                        SudokuCell cell = new SudokuCell(row, col, controller);
                        cells[row][col] = cell;

                        // Add light borders to separate cells within the 3x3 box
                        cell.setStyle("-fx-border-color: #dddddd; -fx-border-width: 0.5;"); 
                        
                        innerGrid.add(cell, cellCol, cellRow);
                    }
                }
                
                mainGrid.add(innerGrid, blockCol, blockRow);
            }
        }
        return mainGrid;
    }

    /**
     * Refreshes all cells in the view based on the current state of the SudokuBoard model.
     * @param model The current SudokuBoard data model.
     */
    public void updateBoard(SudokuBoard model) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int value = model.getValue(r, c);
                boolean isInitial = model.isInitial(r, c);
                
                cells[r][c].setValue(value, isInitial);
                
                // Also check and apply error styling immediately after update if needed
                if (value != 0 && !model.isValidMove(r, c, value)) {
                    cells[r][c].setErrorStyle();
                } else if (value != 0) {
                    cells[r][c].setValidStyle(); // Or back to default user style
                }
            }
        }
    }
    
    /**
     * Applies a specific style to a cell, useful for highlighting hints.
     */
    public void highlightCell(int r, int c, String style) {
         cells[r][c].setStyle(style);
    }

	

	public void refreshValidationStyles(SudokuBoard board) {
		// TODO Auto-generated method stub
		
	}

	public void clearHighlights() {
		// TODO Auto-generated method stub
		
	}

	public void highlightHint(Hint hint) {
		// TODO Auto-generated method stub
		
	}
}