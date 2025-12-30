package com.sudoku.game.advanced_sudoku_game.core;




/**
 * Represents the 9x9 Sudoku game board and contains the core validation logic.
 * The board state is stored in a 2D array.
 */
public class SudokuBoard {
    private static final int SIZE = 9;
    
    // The current state of the grid, including user input (0 means empty)
    private final int[][] grid;
    
    // The initial state of the grid (givens), used to prevent users from modifying original numbers
    private final int[][] initialGrid;

    /**
     * Constructs a new SudokuBoard from an initial puzzle configuration.
     * @param puzzle The 9x9 array representing the initial puzzle (0 for empty cells).
     */
    public SudokuBoard(int[][] puzzle) {
        this.grid = new int[SIZE][SIZE];
        this.initialGrid = new int[SIZE][SIZE];
        
        // Deep copy the initial puzzle into both arrays
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(puzzle[i], 0, this.grid[i], 0, SIZE);
            System.arraycopy(puzzle[i], 0, this.initialGrid[i], 0, SIZE);
        }
    }

    /**
     * Gets the value at the specified cell.
     * @param row Row index (0-8)
     * @param col Column index (0-8)
     * @return The value (1-9) or 0 if empty.
     */
    public int getValue(int row, int col) {
        return grid[row][col];
    }

    /**
     * Sets the value at the specified cell. This is typically called by the GameController.
     * @param row Row index (0-8)
     * @param col Column index (0-8)
     * @param value The value to set (1-9 or 0 to clear).
     */
    public void setValue(int row, int col, int value) {
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
            this.grid[row][col] = value;
        }
    }

    /**
     * Checks if a cell contains an initial 'given' value from the puzzle setup.
     * @param row Row index (0-8)
     * @param col Column index (0-8)
     * @return True if the cell is part of the initial puzzle, false otherwise.
     */
    public boolean isInitial(int row, int col) {
        return initialGrid[row][col] != 0;
    }

    /**
     * Validates if placing 'num' at (row, col) is valid according to Sudoku rules 
     * (row, column, and 3x3 box).
     * @param row Row index (0-8)
     * @param col Column index (0-8)
     * @param num The number to check (1-9).
     * @return True if the move is valid, false otherwise.
     */
    public boolean isValidMove(int row, int col, int num) {
        if (num < 1 || num > 9) return false;

        // 1. Check Row
        for (int c = 0; c < SIZE; c++) {
            if (c != col && grid[row][c] == num) {
                return false;
            }
        }

        // 2. Check Column
        for (int r = 0; r < SIZE; r++) {
            if (r != row && grid[r][col] == num) {
                return false;
            }
        }

        // 3. Check 3x3 Box
        int startRow = row - row % 3;
        int startCol = col - col % 3;

        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if (r != row && c != col && grid[r][c] == num) {
                    return false;
                }
            }
        }

        return true;
    }
    
    /**
     * Checks if all cells on the board are filled (non-zero).
     * @return True if the board is complete, false otherwise.
     */
    public boolean isComplete() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (grid[r][c] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the board is completely filled AND is a valid solution.
     * This iterates over all cells and uses isValidMove to ensure no conflicts exist.
     * @return True if the board is a correctly solved puzzle, false otherwise.
     */
    public boolean isSolved() {
        if (!isComplete()) {
            return false;
        }
        
        // Check every cell for validity against its neighbors
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                // Check if the number placed at (r, c) is valid within its own row, column, and block
                if (!isValidMove(r, c, grid[r][c])) {
                    return false;
                }
            }
        }
        return true;
    }

    // Getter for the entire grid (used by solver/view)
    public int[][] getGrid() {
        return grid;
    }
    
    // Getter for the initial grid (used by generator)
    public int[][] getInitialGrid() {
        return initialGrid;
    }
}