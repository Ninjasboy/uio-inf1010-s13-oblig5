package inf1010.oblig3;

/**
 * This class represents a rectangular fragment of a Sudoku board, be it an
 * entire row, column, or a box.
 * 
 * @author armenmi
 * 
 */
class BoardFragment {

	final int left;
	final int top;
	final int right;
	final int bottom;

	final Board board;

	/**
	 * Creates a new board fragment object.
	 * 
	 * @note <code>right</code>,<code>bottom</code> are not part of this board
	 *       fragment.
	 * 
	 * @param left
	 *            Column index of the leftmost square that is part of this board
	 *            fragment.
	 * @param top
	 *            Row index of the topmost square that is part of this board
	 *            fragment.
	 * @param right
	 *            Column index RIGHT OF THE EDGE of the rightmost square that is
	 *            part of this board fragment.
	 * @param bottom
	 *            Row index UNDER THE EDGE of the bottom-most square that is
	 *            part of this board fragment.
	 * @param board
	 *            The board that this fragment belongs to.
	 */
	BoardFragment(int left, int top, int right, int bottom, Board board) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;

		this.board = board;
	}

	/**
	 * Checks whether a given value is valid within Sudoku rules - i.e. it is
	 * not already present across this board fragment.
	 * 
	 * @param value
	 *            A value to check for duplicates.
	 * @return <code>true</code> if the value is valid, <code>false</code>
	 *         otherwise.
	 */
	boolean isValidValue(int value) {
		for(int y = top; y < bottom; y++) {
			for(int x = left; x < right; x++) {
				if(board.square(x, y).value() == value) {
					return false;
				}
			}
		}

		return true;
	}
}
