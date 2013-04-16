/**
 * A rectangular fragment of a Sudoku board, be it an entire row, column, or a
 * box.
 */
class BoardFragment
{
	/** Index of the leftmost column in this fragment. */
	final public int left;

	/** Index of the topmost row in this fragment. */
	final public int top;

	/**
	 * Index of the column immediately succeeding rightmost column in this
	 * fragment.
	 * 
	 * I.e. the column at the index specified by this variable is NOT included
	 * in this fragment.
	 */
	final public int right;

	/**
	 * Index of the row immediately succeeding bottommost row in this fragment.
	 * 
	 * I.e. the row at the index specified by this variable is NOT included
	 * in this fragment.
	 * */
	final public int bottom;

	/** The board that this fragment is part of. */
	final public Board board;

	/**
	 * Create new board fragment object using specified edges.
	 * 
	 * @param left Index of the leftmost column in the new fragment.
	 * @param top Index of the topmost row in the new fragment.
	 * @param right Index of the column succeeding rightmost column in the new
	 *        fragment.
	 * @param bottom Index of the row succeeding bottommost row in the new
	 *        fragment.
	 * @param board The board that the fragment is part of.
	 */
	BoardFragment(int left, int top, int right, int bottom, Board board)
	{
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;

		this.board = board;
	}

	/**
	 * Check whether the specified value is a valid square value as far as
	 * Sudoku rules are concerned. This naturally involves checking whether the
	 * value is not duplicate across the row, column and the box that this
	 * square lies on.
	 * 
	 * @param value The value to check for validity.
	 * @return <code>true</code> if the value is accepted, <code>false</code>
	 *         otherwise.
	 */
	boolean isValidValue(int value)
	{
		for(int y = top; y < bottom; y++)
		{
			for(int x = left; x < right; x++)
			{
				if(board.value(x, y) == value)
				{
					return false;
				}
			}
		}

		return true;
	}
}
