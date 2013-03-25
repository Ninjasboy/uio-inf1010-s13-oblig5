/**
 * Wraps the board solving process in an object that is suitable for running in
 * a separate thread context.
 * 
 * @author armenmi
 * 
 */
class Solver implements Runnable {

	final Board board;

	Solver(Board board) {
		this.board = board;
	}

	/**
	 * Thread startup routine.
	 * 
	 * Solves the Sudoku board.
	 * 
	 */
	public void run() {
		board.solve();
	}
}
