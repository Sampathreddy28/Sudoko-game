package com.sudoku.gui;

import com.sudoku.ai.DL4JSudokuModel;
import com.sudoku.game.advanced_sudoku_game.core.PuzzleGenerator;
import com.sudoku.game.advanced_sudoku_game.core.SudokuBoard;
import com.sudoku.game.advanced_sudoku_game.solver.BacktrackingSolver;
import com.sudoku.game.advanced_sudoku_game.solver.HintEngine;
import javafx.scene.control.Alert;


/**
 * The Controller in the MVC pattern. Mediates between the SudokuBoard (Model) 
 * and the BoardView (View). Handles game flow, user input, solving, and hints.
 */
public class GameController {
    
    private SudokuBoard board;
    private BoardView view;
    
    // Core Logic Dependencies
    private final PuzzleGenerator generator;
    private final BacktrackingSolver solver;
    private final HintEngine hintEngine;
    private final DL4JSudokuModel aiModel; // Placeholder/Integration point

    public GameController() {
        this.generator = new PuzzleGenerator();
        this.solver = new BacktrackingSolver();
        this.hintEngine = new HintEngine();
        this.aiModel = new DL4JSudokuModel(); // Initialize AI (will use in later steps)
        
        // Start a default easy game on initialization
        newGame(40); 
    }

    /**
     * Sets the view instance. Called once by the SudokuApp during initialization.
     */
    public void setView(BoardView view) {
        this.view = view;
        this.view.updateBoard(board);
    }

    /**
     * Starts a new game by generating a puzzle and updating the view.
     * @param difficulty The approximate number of cells to remove (e.g., 40 for easy).
     */
    public void newGame(int difficulty) {
        // Generate a new puzzle and update the model
        this.board = generator.generate(difficulty);
        if (view != null) {
            view.updateBoard(board);
        }
    }

    /**
     * Handles user input from a SudokuCell.
     * Updates the model and applies immediate validation styling to the cell.
     * @param row Row index.
     * @param col Column index.
     * @param value The input value (1-9) or 0 to clear.
     */
    public void handleInput(int row, int col, int value) {
        // 1. Update the Model
        board.setValue(row, col, value);
        
        // 2. Full validation refresh is often needed for cross-cell error highlighting
        if (view != null) {
             view.clearHighlights(); // Clear any previous hint highlighting on interaction
             view.refreshValidationStyles(board);
        }
    }
    
    /**
     * Uses the BacktrackingSolver to find the complete solution for the current board.
     */
    public void solveBoard() {
        // Create a temporary board copy so the user's current input isn't lost if we fail
        SudokuBoard solveBoard = new SudokuBoard(board.getGrid());
        
        boolean solved = solver.solve(solveBoard, false);
        
        if (solved) {
            // If solved, replace the current board with the solution
            this.board = solveBoard;
            view.updateBoard(board);
            showFeedback("Success", "Board Solved", "The backtracking solver successfully completed the puzzle!");
        } else {
            // Handle error
            System.err.println("The current board configuration is unsolvable.");
            showFeedback("Error", "Unsolvable Board", "The current puzzle state cannot be solved.");
        }
    }
    
    /**
     * Finds and applies the next logical hint to the board.
     */
    public void getHint() {
        HintEngine.Hint hint = hintEngine.getNextHint(board);
        
        if (hint != null) {
            
            if (view != null) {
                view.clearHighlights(); // Clear any previous hint highlighting
            }
            
            // Handle Placement Hints (Naked Single, Hidden Single)
            if (hint.type == HintEngine.HintType.PLACEMENT) {
                // 1. Highlight the cells involved in the placement hint
                if (view != null) {
                    view.highlightHint(hint);
                }
                
                System.out.println("Placement Hint: Place " + hint.value + " at (" + hint.row + ", " + hint.col + ") using " + hint.technique);
                showFeedback("Hint", hint.technique + " Found", 
                             "Place " + hint.value + " at row " + (hint.row + 1) + ", column " + (hint.col + 1) + ". " + 
                             "Cells involved in the hint are highlighted in pale yellow, and the target cell is orange.");
                             
            } 
            // Handle Elimination Hints (Locked Candidates)
            else if (hint.type == HintEngine.HintType.ELIMINATION) {
                System.out.println("Elimination Hint: " + hint.explanation);
                showFeedback("Advanced Hint", hint.technique + " Found", 
                             hint.explanation + 
                             "\n\nSince this is an elimination step, it will not place a number directly, but it simplifies the puzzle!");
            }

            // The user must manually input the value or candidates must be managed externally
        } else {
            if (view != null) {
                view.clearHighlights(); // Clear if previous hint was showing
            }
            System.out.println("No easy logical hint found. Try the AI solver!");
            showFeedback("Info", "No Easy Hint", "The current puzzle requires advanced logical techniques or the AI solver.");
        }
    }

    /**
     * Executes the AI-based solver (currently using the backtracking solver as a temporary substitute).
     */
    public void solveWithAI() {
        System.out.println("Starting AI inference for board solving...");
        
        // NOTE: In a real app, this would call a sophisticated AI model. 
        // For now, we rely on the internal backtracking solver as a robust fallback.
        SudokuBoard solvedBoard = new SudokuBoard(board.getGrid());
        solver.solve(solvedBoard, false); 
        
        this.board = solvedBoard;
        
        if (view != null) {
            view.updateBoard(board);
        }
        
        System.out.println("AI Solve Complete (using Backtracking fallback).");
        showFeedback("Success", "AI Solve Complete", "The board was solved using the AI integration (currently using a reliable backtracking fallback).");
    }
    
    /**
     * Checks the current board state against the Sudoku completion rules.
     */
    public void checkSolution() {
        if (!board.isComplete()) {
            System.out.println("Solution Check: Board is incomplete. Keep going!");
            showFeedback("Keep Playing", "Board Incomplete", "Not all cells are filled yet! Keep solving.");
        } else if (board.isSolved()) {
            System.out.println("Solution Check: Congratulations! The board is solved correctly!");
            showFeedback("Congratulations!", "Puzzle Solved!", "You have correctly solved the Sudoku puzzle!");
        } else {
            System.out.println("Solution Check: Board is complete, but contains errors.");
            view.refreshValidationStyles(board); // Re-apply styles on check
            showFeedback("Try Again", "Errors Found", "The board is complete, but there are errors. Check your row, column, and block constraints!");
        }
        if (view != null) {
            view.clearHighlights();
        }
    }

    /**
     * Helper method to display simple feedback alerts to the user.
     */
    private void showFeedback(String title, String header, String content) {
        Alert.AlertType type;
        switch (title) {
            case "Success":
            case "Congratulations!":
                type = Alert.AlertType.INFORMATION;
                break;
            case "Error":
            case "Try Again":
                type = Alert.AlertType.ERROR;
                break;
            default:
                type = Alert.AlertType.INFORMATION;
                break;
        }
        
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Getter for the view (used by SudokuApp for layout)
    public BoardView getView() {
        return view;
    }
    
    // Getter for the board (used by SudokuCell for self-validation check on input)
    public SudokuBoard getBoard() {
        return board;
    }
}