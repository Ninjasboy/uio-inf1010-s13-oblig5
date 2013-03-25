/**
 * Model of a single column on a Sudoku board.
 * 
 * Fra oppgaveteksten: "kolonne er en loddrett (ovenfra og nedover) rekke med n
 * ruter. "
 * 
 * @author armenmi
 * 
 */
class Column extends BoardFragment
{

	Column(int index, Board board)
	{
		super(index, 0, index + 1, board.dimension(), board);
	}
}
