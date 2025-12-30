package com.sudoku.game.advanced_sudoku_game.solver;


import com.sudoku.game.advanced_sudoku_game.core.SudokuBoard;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Provides logical hints (Naked Single, Hidden Single, Locked Candidates, etc.) for a Sudoku board.
 * It does not solve the entire board but identifies the next easiest move.
 */
public class HintEngine {

    private static final int SIZE = 9;

    /**
     * Enumeration to distinguish between different types of hints.
     */
    public static enum HintType {
        PLACEMENT, // E.g., Naked Single, Hidden Single (Places a number)
        ELIMINATION // E.g., Locked Candidates (Removes a candidate)
    }

    /**
     * Data structure to hold the result of a hint search. Immutable.
     */
    public static class Hint {
        public final HintType type;
        public final int row; // Target row for PLACEMENT / Primary unit row index for ELIMINATION (0-8)
        public final int col; // Target col for PLACEMENT / Primary unit col index for ELIMINATION (0-8)
        public final int value; // Value to place / Candidate to lock or eliminate
        public final String technique;
        public final String explanation; // Detailed explanation for the user

        // Constructor for PLACEMENT Hints (Naked/Hidden Singles)
        public Hint(int row, int col, int value, String technique) {
            this.type = HintType.PLACEMENT;
            this.row = row;
            this.col = col;
            this.value = value;
            this.technique = technique;
            this.explanation = String.format("Place %d at R%dC%d.", value, row + 1, col + 1);
        }

        // Constructor for ELIMINATION Hints (Locked Candidates)
        public Hint(int row, int col, int value, String technique, String explanation) {
            this.type = HintType.ELIMINATION;
            this.row = row;
            this.col = col;
            this.value = value;
            this.technique = technique;
            this.explanation = explanation;
        }
    }

    /**
     * Finds the next easiest logical move on the board.
     * @param board The current state of the Sudoku board.
     * @return A Hint object containing the move, or null if no easy hint is found.
     */
    public Hint getNextHint(SudokuBoard board) {
        
        // 1. Check for Naked Singles (Easiest placement)
        Hint nakedSingle = findNakedSingle(board);
        if (nakedSingle != null) {
            return nakedSingle;
        }

        // 2. Check for Hidden Singles (Slightly harder placement)
        Hint hiddenSingle = findHiddenSingle(board);
        if (hiddenSingle != null) {
            return hiddenSingle;
        }
        
        // 3. Locked Candidates (First elimination technique - requires calculating all candidates)
        Hint lockedCandidates = findLockedCandidates(board);
        if (lockedCandidates != null) {
            return lockedCandidates;
        }

        return null; // No easy logical hint found
    }

    // --- Core Logic Implementations ---

    /**
     * Calculates all valid numbers (candidates) that can be placed in a given empty cell.
     * @param board The board state.
     * @param row The row index.
     * @param col The column index.
     * @return A list of valid numbers (1-9).
     */
    private List<Integer> getCandidates(SudokuBoard board, int row, int col) {
        List<Integer> candidates = new ArrayList<>();
        if (board.getValue(row, col) != 0) {
            return candidates; // Not an empty cell
        }

        // For each number 1-9, check if it's a valid move
        for (int num = 1; num <= SIZE; num++) {
            if (canPlaceNumber(board, row, col, num)) {
                candidates.add(num);
            }
        }
        return candidates;
    }
    
    /**
     * Checks if a number can be placed at a location without conflicts in its unit.
     */
    private boolean canPlaceNumber(SudokuBoard board, int row, int col, int num) {
        // Check Row
        for (int c = 0; c < SIZE; c++) {
            if (c != col && board.getValue(row, c) == num) return false;
        }

        // Check Column
        for (int r = 0; r < SIZE; r++) {
            if (r != row && board.getValue(r, col) == num) return false;
        }

        // Check 3x3 Box
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if (r != row && c != col && board.getValue(r, c) == num) return false;
            }
        }

        return true;
    }
    
    /**
     * Finds a Naked Single: a cell that has only one possible valid number.
     */
    private Hint findNakedSingle(SudokuBoard board) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board.getValue(r, c) == 0) {
                    List<Integer> candidates = getCandidates(board, r, c);
                    
                    if (candidates.size() == 1) {
                        return new Hint(r, c, candidates.get(0), "Naked Single");
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds a Hidden Single: a number that can only be placed in one cell within a 
     * row, column, or 3x3 block.
     */
    private Hint findHiddenSingle(SudokuBoard board) {
        // Check Rows
        for (int r = 0; r < SIZE; r++) {
            Hint hint = findHiddenSingleInUnit(board, r, -1, "Row");
            if (hint != null) return hint;
        }

        // Check Columns
        for (int c = 0; c < SIZE; c++) {
            Hint hint = findHiddenSingleInUnit(board, -1, c, "Column");
            if (hint != null) return hint;
        }

        // Check Blocks
        for (int blockRow = 0; blockRow < 3; blockRow++) {
            for (int blockCol = 0; blockCol < 3; blockCol++) {
                Hint hint = findHiddenSingleInUnit(board, blockRow * 3, blockCol * 3, "Block");
                if (hint != null) return hint;
            }
        }

        return null;
    }

    /**
     * Helper to find a Hidden Single within a single unit (Row, Column, or Block).
     */
    private Hint findHiddenSingleInUnit(SudokuBoard board, int r, int c, String unitType) {
        // Determine the iteration range based on unit type
        List<int[]> cellsInUnit = new ArrayList<>();
        
        if (unitType.equals("Row")) {
            IntStream.range(0, 9).forEach(col -> cellsInUnit.add(new int[]{r, col}));
        } else if (unitType.equals("Column")) {
            IntStream.range(0, 9).forEach(row -> cellsInUnit.add(new int[]{row, c}));
        } else { // Block
            int startR = r; // Already block start row
            int startC = c; // Already block start col
            for (int row = startR; row < startR + 3; row++) {
                for (int col = startC; col < startC + 3; col++) {
                    cellsInUnit.add(new int[]{row, col});
                }
            }
        }

        // Loop through all possible numbers (1-9)
        for (int num = 1; num <= SIZE; num++) {
            // Create a final copy for use in the lambda expression
            final int currentNum = num; 
            
            int lastPossibleRow = -1;
            int lastPossibleCol = -1;
            int count = 0;
            
            // Check if the number is already present in this unit
            boolean numIsPresent = cellsInUnit.stream()
                .anyMatch(pos -> board.getValue(pos[0], pos[1]) == currentNum);
            if (numIsPresent) continue; 

            // Check every empty cell in the unit for candidacy
            for (int[] pos : cellsInUnit) {
                int row = pos[0];
                int col = pos[1];
                
                if (board.getValue(row, col) == 0 && canPlaceNumber(board, row, col, currentNum)) {
                    count++;
                    lastPossibleRow = row;
                    lastPossibleCol = col;
                }
            }

            // If the number can only be placed in exactly ONE empty cell in this unit
            if (count == 1) {
                return new Hint(lastPossibleRow, lastPossibleCol, currentNum, "Hidden Single (" + unitType + ")");
            }
        }
        return null;
    }
    
    /**
     * Finds Locked Candidates (Pointing or Claiming).
     * This is an elimination technique.
     */
    private Hint findLockedCandidates(SudokuBoard board) {
        // Locked Candidates relies on candidate calculation.
        
        // --- LOCKED CANDIDATES (POINTING: Block -> Row/Col) ---
        // If a candidate is confined to a single row (or column) within a block, 
        // eliminate it from that row (or column) outside the block.
        
        for (int br = 0; br < 3; br++) {
            for (int bc = 0; bc < 3; bc++) {
                int rStart = br * 3;
                int cStart = bc * 3;

                // Loop through all possible candidates (1-9)
                for (int num = 1; num <= 9; num++) {
                    // Create a final copy for use in the lambda expression
                    final int currentNum = num; 
                    
                    List<int[]> candidateCellsInBlock = new ArrayList<>();
                    for (int r = rStart; r < rStart + 3; r++) {
                        for (int c = cStart; c < cStart + 3; c++) {
                            if (board.getValue(r, c) == 0 && getCandidates(board, r, c).contains(currentNum)) {
                                candidateCellsInBlock.add(new int[]{r, c});
                            }
                        }
                    }
                    
                    if (candidateCellsInBlock.size() < 2) continue;
                    
                    // Check for Pointing (Confined to a single row?)
                    int confinedRow = candidateCellsInBlock.get(0)[0];
                    boolean isRowConfined = candidateCellsInBlock.stream().allMatch(p -> p[0] == confinedRow);

                    if (isRowConfined) {
                        final int finalConfinedRow = confinedRow; // Make confinedRow final for the lambda
                        // Potential elimination in the confinedRow outside the block
                        boolean eliminationPossible = IntStream.range(0, 9)
                            .filter(c -> c < cStart || c >= cStart + 3) // Cells outside the block
                            .anyMatch(c -> board.getValue(finalConfinedRow, c) == 0 && getCandidates(board, finalConfinedRow, c).contains(currentNum));

                        if (eliminationPossible) {
                            String explanation = String.format(
                                "Locked Candidates (Pointing): Candidate %d is confined to Row %d in Block R%d-R%d C%d-C%d. " +
                                "Therefore, %d can be eliminated from all other candidate cells in Row %d outside this block.",
                                currentNum, finalConfinedRow + 1, rStart + 1, rStart + 3, cStart + 1, cStart + 3, currentNum, finalConfinedRow + 1
                            );
                            return new Hint(finalConfinedRow, -1, currentNum, "Locked Candidates (Pointing)", explanation);
                        }
                    }
                    
                    // Check for Pointing (Confined to a single column?)
                    int confinedCol = candidateCellsInBlock.get(0)[1];
                    boolean isColConfined = candidateCellsInBlock.stream().allMatch(p -> p[1] == confinedCol);
                    
                    if (isColConfined) {
                        final int finalConfinedCol = confinedCol; // Make confinedCol final for the lambda
                        // Potential elimination in the confinedCol outside the block
                        boolean eliminationPossible = IntStream.range(0, 9)
                            .filter(r -> r < rStart || r >= rStart + 3) // Cells outside the block
                            .anyMatch(r -> board.getValue(r, finalConfinedCol) == 0 && getCandidates(board, r, finalConfinedCol).contains(currentNum));

                        if (eliminationPossible) {
                            String explanation = String.format(
                                "Locked Candidates (Pointing): Candidate %d is confined to Column %d in Block R%d-R%d C%d-C%d. " +
                                "Therefore, %d can be eliminated from all other candidate cells in Column %d outside this block.",
                                currentNum, finalConfinedCol + 1, rStart + 1, rStart + 3, cStart + 1, cStart + 3, currentNum, finalConfinedCol + 1
                            );
                            return new Hint(-1, finalConfinedCol, currentNum, "Locked Candidates (Pointing)", explanation);
                        }
                    }
                }
            }
        }
        
        // --- LOCKED CANDIDATES (CLAIMING: Row/Col -> Block) ---
        
        // Check Rows for Claiming
        for (int r = 0; r < 9; r++) {
            final int finalR = r; // Make R final for the lambda
            // Loop through all possible candidates (1-9)
            for (int num = 1; num <= 9; num++) {
                final int currentNum = num; // Make num final for the lambda
                List<int[]> candidateCellsInRow = IntStream.range(0, 9)
                    .filter(c -> board.getValue(finalR, c) == 0 && getCandidates(board, finalR, c).contains(currentNum))
                    .mapToObj(c -> new int[]{finalR, c})
                    .collect(Collectors.toList());
                
                if (candidateCellsInRow.size() < 2) continue;

                // Check if candidates are confined to a single block-column (c/3)
                int blockColIndex = candidateCellsInRow.get(0)[1] / 3;
                boolean isBlockConfined = candidateCellsInRow.stream().allMatch(p -> (p[1] / 3) == blockColIndex);

                if (isBlockConfined) {
                    // Locked Candidates (Claiming) found: 'num' is claimed by this block
                    int br = finalR / 3;
                    int bc = blockColIndex;
                    int rStart = br * 3;
                    int cStart = bc * 3;
                    
                    // Potential elimination within the block outside the row
                    boolean eliminationPossible = false;
                    for (int row = rStart; row < rStart + 3; row++) {
                        if (row != finalR) { // Cells in the block, but not in the current row
                            for (int col = cStart; col < cStart + 3; col++) {
                                if (board.getValue(row, col) == 0 && getCandidates(board, row, col).contains(currentNum)) {
                                    eliminationPossible = true;
                                    break;
                                }
                            }
                        }
                        if (eliminationPossible) break;
                    }

                    if (eliminationPossible) {
                        String explanation = String.format(
                            "Locked Candidates (Claiming): Candidate %d is confined to Row %d in Block C%d. " +
                            "Therefore, %d can be eliminated from the other candidate cells in Block R%d-R%d C%d-C%d that are not in Row %d.",
                            currentNum, finalR + 1, blockColIndex + 1, currentNum, rStart + 1, rStart + 3, cStart + 1, cStart + 3, finalR + 1
                        );
                        return new Hint(finalR, -1, currentNum, "Locked Candidates (Claiming)", explanation);
                    }
                }
            }
        }
        
        // Check Columns for Claiming
        for (int c = 0; c < 9; c++) {
            final int finalC = c; // Make C final for the lambda
            // Loop through all possible candidates (1-9)
            for (int num = 1; num <= 9; num++) {
                final int currentNum = num; // Make num final for the lambda
                List<int[]> candidateCellsInCol = IntStream.range(0, 9)
                    .filter(r -> board.getValue(r, finalC) == 0 && getCandidates(board, r, finalC).contains(currentNum))
                    .mapToObj(r -> new int[]{r, finalC})
                    .collect(Collectors.toList());
                
                if (candidateCellsInCol.size() < 2) continue;

                // Check if candidates are confined to a single block-row (r/3)
                int blockRowIndex = candidateCellsInCol.get(0)[0] / 3;
                boolean isBlockConfined = candidateCellsInCol.stream().allMatch(p -> (p[0] / 3) == blockRowIndex);

                if (isBlockConfined) {
                    // Locked Candidates (Claiming) found: 'num' is claimed by this block
                    int br = blockRowIndex;
                    int bc = finalC / 3;
                    int rStart = br * 3;
                    int cStart = bc * 3;
                    
                    // Potential elimination within the block outside the column
                    boolean eliminationPossible = false;
                    for (int row = rStart; row < rStart + 3; row++) {
                        for (int col = cStart; col < cStart + 3; col++) {
                            if (col != finalC) { // Cells in the block, but not in the current column
                                if (board.getValue(row, col) == 0 && getCandidates(board, row, col).contains(currentNum)) {
                                    eliminationPossible = true;
                                    break;
                                }
                            }
                        }
                        if (eliminationPossible) break;
                    }

                    if (eliminationPossible) {
                        String explanation = String.format(
                            "Locked Candidates (Claiming): Candidate %d is confined to Column %d in Block R%d. " +
                            "Therefore, %d can be eliminated from the other candidate cells in Block R%d-R%d C%d-C%d that are not in Column %d.",
                            currentNum, finalC + 1, blockRowIndex + 1, currentNum, rStart + 1, rStart + 3, cStart + 1, cStart + 3, finalC + 1
                        );
                        return new Hint(-1, finalC, currentNum, "Locked Candidates (Claiming)", explanation);
                    }
                }
            }
        }
        
        return null;
    }
}