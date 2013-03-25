/**
 * Models a single row of squares on a Sudoku board.
 * 
 * Fra oppgaveteksten: "rad er en vannrett (fra venstre mot høyre på brettet)
 * rekke med n ruter. "
 * 
 * @author armenmi
 * 
 */
class Row extends BoardFragment {

	Row(int index, Board board) {
		super(0, index, board.dimension(), index + 1, board);
	}
}
