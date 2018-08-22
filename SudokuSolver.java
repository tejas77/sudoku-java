
package sudoku;

import java.util.Stack;

/**
 * This class implements a sequential Sudoku Solver, capable of
 * solving any square Sudoku Problem.  To use this class, create an
 * instance of the Sudoku Solver, specifiying the size of the problem
 * to solve, then set the initial values, and, finally, call the
 * <tt>solve</tt> method to solve the problem.  Something along the
 * lines of:
 *
 * <pre>
 *    // a size of 3 corresponds to a problem of 9x9
 *    SudokuSolver solver = new SudokuSolver(3);
 *    solver.setInitialValues(someInitialValues);
 *    solver.solve();
 * </pre>
 *
 * Note that a significant part of the problem solving may occur
 * during the call to the <tt>setInitialValues</tt> method,
 * because this solver uses forward constraint propagation whenever it
 * sets a cell of the puzzle to a certain value.  So, if you intend to
 * measure the time that the solver takes to solve a puzzle, start
 * measuring the time before calling the <tt>setInitialValues</tt>
 * method.
 *
 * After solving the puzzle, you may either obtain the solution as a
 * string, with the method <tt>cellsToString</tt>, or print the
 * solution in a user-readable form, with the <tt>printCells</tt>
 * method.
 */
public class SudokuSolver {
    private final int problemSize;
    private final int numOptions;
    private final Cell[][] cells;

    public SudokuSolver(int problemSize) {
        this.problemSize = problemSize;
        this.numOptions = problemSize * problemSize;
        this.cells = new Cell[numOptions][numOptions];

        // create all cells
        for (int r = 0; r < numOptions; r++) {
            for (int c = 0; c < numOptions; c++) {
                cells[r][c] = new Cell(r, c);
            }
        }
    }

    /**
     * Initialize the problem with the initial known values.  The
     * givens string must be a comma-separated sequence of triplets.
     * Each triplet is represented by three decimal numbers separated
     * by colons, in the form "row:column:value", where row and column
     * are 1-based, starting from the top-left.
     */
    public void setInitialValues(String givens) {
        String[] initials = givens.split("\\s*,\\s*");        
        for (String triplet : initials) {
            if (! triplet.matches("\\d+:\\d+:\\d+")) {
                System.err.println("Error: Wrong format for the initial known values " + triplet);
                System.exit(1);
            }

            // process one
            int colon1 = triplet.indexOf(':');
            int colon2 = triplet.indexOf(':', colon1 + 1);
            int row = Integer.parseInt(triplet.substring(0, colon1));
            int col = Integer.parseInt(triplet.substring(colon1 + 1, colon2));
            int value = Integer.parseInt(triplet.substring(colon2 + 1));

            if (outOfRange(row) || outOfRange(col) || outOfRange(value)) {
                System.err.println("Error: Values out of range for this problem size " + triplet);
                System.exit(1);
            }

            try {
                cells[row-1][col-1].setValue(value, 0);
            } catch (Fail f) {
                undoTentative(0);
                throw new Error("Set of initial knowns is not possible");
            }
        }
    }

    /**
     * Solves the problem.  Returns <tt>true</tt> if it finds a
     * solution, and <tt>false</tt> otherwise.
     */
    public boolean solve() {
        try {
            tryValuesFor(findUnsolvedCell(), 0);
            return true;
        } catch (Fail f) {
            return false;
        }
    }

    /**
     * Return the contents of each cell, separated by spaces.
     */
    public String cellsToString() {
        StringBuilder buf = new StringBuilder();
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                buf.append(cell.getContentsAsString());
                buf.append(' ');
            }
        }
        return buf.toString();
    }

    /**
     * Print the contents of the cells in a user-readable form.
     */
    public void printCells() {
        for (Cell[] row : cells) {
            System.out.println();
            for (Cell cell : row) {
                cell.print();
            }
            System.out.println();
        }
    }


    // non-public methods below

    void undoTentative(int tentative) {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.undoTentative(tentative);
            }
        }
    }

    void tryValuesFor(Cell cell, int tentative) throws Fail {
        // if no unsolved cell exists, then we found the solution
        if (cell == null) {
            return;
        }

        int first = cell.getFirstChoice();

	try {
	    tryOneValue(cell, first, tentative + 1);
	} catch (Fail f) {
            undoTentative(tentative + 1);
	    cell.removeChoice(first, tentative);
            tryValuesFor(cell, tentative);
	}
    }
    
    void tryOneValue(Cell cell, int num, int tentative) throws Fail {
	cell.setValue(num, tentative);
        tryValuesFor(findUnsolvedCell(), tentative);
    }


    Cell findUnsolvedCell() {
        for (Cell[] row : cells) {
            for (Cell c : row) {
                if (c.countChoices() > 1) {
		    return c;
		}
	    }
	}

        return null;
    }

    private boolean outOfRange(int value) {
        return (value < 1) || (value > numOptions);
    }

    private int toRegion(int cellCoord) {
        return cellCoord / problemSize;
    }

    void removeChoiceFromNeighbors(int row, int col, int choice, int tentative) throws Fail {
        int regionRow = toRegion(row);
        int regionCol = toRegion(col);

        for (int r = 0; r < numOptions; r++) {
            for (int c = 0; c < numOptions; c++) {
                if (! ((row == r) && (col == c))) {
                    if ((row == r) || (col == c) || 
                        ((toRegion(r) == regionRow) && (toRegion(c) == regionCol))) {
                        cells[r][c].removeChoice(choice, tentative);
                    }
                }
            }
        }
    }


    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java SudokuSolver <problemSize> <knownValues>");
            System.exit(1);
        }

        int problemSize = Integer.parseInt(args[0]);
        SudokuSolver solver = new SudokuSolver(problemSize);
        try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	long start = System.nanoTime();
        solver.setInitialValues(args[1]);
    //	System.out.println("Solved in " + ((System.nanoTime() - start) / 1e6) + "ms");

        solver.solve();

        System.out.println("Solved in " + ((System.nanoTime() - start) / 1e6) + "ms");
        solver.printCells();
    }

    static class Choice {
        static class Value {
            boolean possible;
            int tentative;
            
            Value(boolean possible, int tentative) {
                this.possible = possible;
                this.tentative = tentative;
            }
        }

        private static final Value INITIAL = new Value(true, -1);

        private Value current = INITIAL;
        private final Stack<Value> previousValues = new Stack<Value>();

        public boolean isPossible() {
            return current.possible;
        }

        public void setPossible(boolean possible, int tentative) {
            previousValues.push(current);
            this.current = new Value(possible, tentative);
        }

        public void undoTentative(int tentative) {
            if (current.tentative == tentative) {
                current = previousValues.pop();
            }
        }
    }

    class Cell {
	private final int row;
	private final int col;

        private final Choice[] choices = new Choice[numOptions];

        Cell(int row, int col) {
	    this.row = row;
	    this.col = col;

	    for (int i = 0; i < numOptions; i++) {
		choices[i] = new Choice();
	    }
        }

     void undoTentative(int tentative) {
         for (Choice c : choices) {
                c.undoTentative(tentative);
         }
     }

	boolean hasChoice(int num) {
	    return choices[num-1].isPossible();
	}

	void setChoice(int num, boolean possible, int tentative) {
	    choices[num-1].setPossible(possible, tentative);
	}

	void removeChoice(int num, int tentative) throws Fail {
	    if (hasChoice(num)) {
		setChoice(num, false, tentative);
		checkChoices(tentative);
	    }
	}

	void checkChoices(int tentative) throws Fail {
	    int numChoices = countChoices();
	    if (numChoices == 0) {
		throw new Fail();
	    } else if (numChoices == 1) {
		removeChoiceFromNeighbors(row, col, getFirstChoice(), tentative);
	    }
	}

        void setValue(int newValue, int tentative) throws Fail {
	    if (! hasChoice(newValue)) {
		throw new Fail();
	    }

	    for (int i = 1; i <= choices.length; i++) {
		setChoice(i, i == newValue, tentative);
	    }
	    checkChoices(tentative);
        }

        int getFirstChoice() {
	    for (int i = 1; i <= choices.length; i++) {
		if (hasChoice(i)) {
		    return i;
		}
	    }
	    return 0;
        }

        int countChoices() {
            int total = 0;

            for (Choice c : choices) {
                if (c.isPossible()) {
                    total++;
                }
            }

	    return total;
        }

        void print() {
            if (countChoices() == 1) {
                System.out.printf(" %2d ", getFirstChoice());
            } else {
                System.out.print("  ? ");
            }
        }

        String getContentsAsString() {
            return (countChoices() == 1) ? String.valueOf(getFirstChoice()) : "?";
        }
    }

    @SuppressWarnings("serial")
	static class Fail extends Exception {}
}