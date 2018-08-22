package sudoku;

public class MainSudoku {

	public static void main(String[] args) {
		Sudoku s = new Sudoku();
		s.initialize();
		s.input();
		long start = System.nanoTime();		s.display();
		s.algo();

		System.out.println("Solved in " + ((System.nanoTime() - start) / 1e6) + "ms");	}

}
