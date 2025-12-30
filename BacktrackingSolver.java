package com.sudoku.game.advanced_sudoku_game.solver;



import com.sudoku.game.advanced_sudoku_game.core.SudokuBoard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements the standard recursive backtracking algorithm for solving a Sudoku board.
 */
public class BacktrackingSolver {
    
    private static final int SIZE = 9;

    /**
     * Main method to solve a Sudoku board in place.
     * @param board The SudokuBoard object to solve.
     * @param randomize If true, shuffles the candidate numbers (used by generator).
     * @return True if a solution was found, false otherwise.
     */
    public boolean solve(SudokuBoard board, boolean randomize) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                
                // Find the next empty cell
                if (board.getValue(row, col) == 0) {
                    
                    List<Integer> numbers = new ArrayList<>();
                    for (int n = 1; n <= 9; n++) {
                        numbers.add(n);
                    }
                    
                    if (randomize) {
                        Collections.shuffle(numbers); // Randomize candidates for unique board generation
                    }

                    for (int num : numbers) {
                        // Check if the current number is valid at this position
                        if (board.isValidMove(row, col, num)) {
                            board.setValue(row, col, num); // Place the number
                            
                            if (solve(board, randomize)) {
                                return true; // Found solution
                            }
                            
                            board.setValue(row, col, 0); // Backtrack
                        }
                    }
                    return false; // No number worked in this cell
                }
            }
        }
        return true; // Entire board is filled
    }
    
    /**
     * Counts the number of possible solutions for a given Sudoku board.
     * This is crucial for the PuzzleGenerator to ensure uniqueness.
     * @param board The SudokuBoard instance to check.
     * @return The number of unique solutions found.
     */
    public int countSolutions(SudokuBoard board) {
        int count = 0;
        
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                
                if (board.getValue(row, col) == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (board.isValidMove(row, col, num)) {
                            board.setValue(row, col, num);
                            count += countSolutions(board);
                            board.setValue(row, col, 0); // Backtrack
                        }
                    }
                    return count; 
                }
            }
        }
        return 1; // Solution found (base case)
    }
}