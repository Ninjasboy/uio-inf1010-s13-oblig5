/**
 * The box that is a sub-segment of a Sudoku board.
 * 
 * Fra oppgaveteksten: "boks er ﬂere vannrette og loddrette ruter, markert med
 * tykkere strek i oppgavene; i 9x9-eksemplet er en boks på 3x3 ruter, mens i
 * Aftenpostens lynsudoku består en boks av 2x3 ruter. "
 * 
 * @author armenmi
 */
class Box extends BoardFragment {

	Box(int left, int top, int right, int bottom, Board board) {
		super(left, top, right, bottom, board);
	}
}
