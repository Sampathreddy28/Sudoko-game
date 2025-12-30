package com.sudoku.game.advanced_sudoku_game;



import com.sudoku.game.advanced_sudoku_game.core.PuzzleGenerator;

import com.sudoku.game.advanced_sudoku_game.core.SudokuBoard;
import com.sudoku.game.advanced_sudoku_game.solver.BacktrackingSolver;






import com.sudoku.game.advanced_sudoku_game.solver.BacktrackingSolver;

/**
 * Standard Java testing class for PuzzleGenerator, using a main method 
 * to avoid external dependencies like JUnit.
 * This class ensures generated puzzles are valid and solvable.
 */
public class PuzzleGeneratorTest {

    private final PuzzleGenerator generator = new PuzzleGenerator();
    private final BacktrackingSolver solver = new BacktrackingSolver();

    // --- Utility Methods ---

    /**
     * Helper method to count empty cells (0s).
     */
    private int countEmptyCells(SudokuBoard board) {
        int count = 0;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board.getValue(r, c) == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Prints the result of a test.
     */
    private void printTestResult(String testName, boolean success, String message) {
        String status = success ? "SUCCESS" : "FAILED";
        System.out.println("[" + status + "] " + testName + ": " + message);
        if (!success) {
            System.out.println("--------------------------------------------------");
        }
    }

    // --- Test Implementations ---

    public void testGenerateEasyPuzzle() {
        String testName = "Easy Puzzle Generation Test";
        int targetRemoval = 40;
        SudokuBoard board = generator.generate(targetRemoval);
        int emptyCells = countEmptyCells(board);
        boolean rangeCheck = emptyCells >= 35 && emptyCells <= 45;
        
        // Assert that the generated puzzle is solvable
        SudokuBoard solveBoard = new SudokuBoard(board.getGrid()); // Solve a copy
        boolean solvable = solver.solve(solveBoard, true);

        if (rangeCheck && solvable) {
            printTestResult(testName, true, "Generated puzzle is solvable with " + emptyCells + " empty cells.");
        } else {
            String message = "";
            if (!rangeCheck) {
                message += "Cell count check failed (expected 35-45, got " + emptyCells + "). ";
            }
            if (!solvable) {
                message += "The generated puzzle was reported as UN-SOLVABLE.";
            }
            printTestResult(testName, false, message);
        }
    }

    public void testGenerateHardPuzzle() {
        String testName = "Hard Puzzle Generation Test";
        int targetRemoval = 60;
        SudokuBoard board = generator.generate(targetRemoval);
        int emptyCells = countEmptyCells(board);
        boolean rangeCheck = emptyCells >= 55 && emptyCells <= 65;
        
        // Assert that the generated puzzle is solvable
        SudokuBoard solveBoard = new SudokuBoard(board.getGrid()); // Solve a copy
        boolean solvable = solver.solve(solveBoard, true);

        if (rangeCheck && solvable) {
            printTestResult(testName, true, "Generated puzzle is solvable with " + emptyCells + " empty cells.");
        } else {
            String message = "";
            if (!rangeCheck) {
                message += "Cell count check failed (expected 55-65, got " + emptyCells + "). ";
            }
            if (!solvable) {
                message += "The generated puzzle was reported as UN-SOLVABLE.";
            }
            printTestResult(testName, false, message);
        }
    }

    public void testSolvabilityOfKnownEvilPuzzle() {
        String testName = "Known Evil Puzzle Solvability Test";
        // A known 'evil' difficulty puzzle
        int[][] evilPuzzle = {
            {8, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 3, 6, 0, 0, 0, 0, 0},
            {0, 7, 0, 0, 9, 0, 2, 0, 0},
            {0, 5, 0, 0, 0, 7, 0, 0, 0},
            {0, 0, 0, 0, 4, 5, 7, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 3, 0},
            {0, 0, 1, 0, 0, 0, 0, 6, 8},
            {0, 0, 8, 5, 0, 0, 0, 1, 0},
            {0, 9, 0, 0, 0, 0, 4, 0, 0}
        };
        
        SudokuBoard board = new SudokuBoard(evilPuzzle);
        boolean solvable = solver.solve(board, true);
        
        if (solvable && board.isComplete() && board.isSolved()) {
            printTestResult(testName, true, "Puzzle solved correctly.");
        } else {
            printTestResult(testName, false, "Puzzle failed to solve or solution was invalid.");
        }
    }
    
    // --- Main Execution Method ---

    public static void main(String[] args) {
        System.out.println("--- Starting Sudoku Puzzle Generator Tests ---");
        PuzzleGeneratorTest tester = new PuzzleGeneratorTest();
        
        tester.testGenerateEasyPuzzle();
        tester.testGenerateHardPuzzle();
        tester.testSolvabilityOfKnownEvilPuzzle();
        
        System.out.println("--- Tests Complete ---");
    }
}