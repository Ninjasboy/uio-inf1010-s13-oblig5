/**
 * A boxed group of squares on a Sudoku board.
 */
class Box extends BoardFragment
{
	/**
	 * Create a box.
	 * 
	 * @param left Index of the leftmost column this box includes.
	 * @param top Index of the topmost row this box includes.
	 * @param right Index of the column succeeding rightmost column of this box.
	 * @param bottom Index of the row succeeding bottommost row of this box.
	 * @param board The board that this box belongs to.
	 */
	Box(int left, int top, int right, int bottom, Board board)
	{
		super(left, top, right, bottom, board);
	}
}
