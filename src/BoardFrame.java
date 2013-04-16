import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A Swing frame that displays a Sudoku board.
 * 
 * Displays a board and navigates its solutions. The board acts a a model for
 * the frame.
 */
class BoardFrame extends JFrame implements ActionListener
{
	/**
	 * Event listener that is notified of events originating in and related to
	 * this frame.
	 */
	interface EventListener
	{
		/**
		 * Step button has been clicked. The step button is part of unassisted
		 * debugging.
		 */
		void onStepButtonClicked();
	}

	/** Size of the margin for the action buttons */
	static final int BUTTONS_MARGIN_SIZE = 50;

	/** Background painting color for immutable board squares. */
	static final Color staticSquareBgColor = new Color(0xd0d0d0);

	/** Whether this frame is put into interactive debugging mode. */
	final public Application.Debugger debug;

	/** The board displayed in this frame. */
	final public Board board;

	/** Title of this frame. */
	final public String title;

	/** The solution buffer that this view displays. */
	final private SolutionBuffer solutionBuffer;

	/** The text fields that paint the board squares. */
	final private JTextField[][] squareTextFields;

	/** Action buttons */
	final JButton nextSolutionButton, prevSolutionButton,
		stepButton/* , saveSolutionsButton */;

	/** Currently displayed solution ID. */
	private int solutionID;

	/** When board is being solved, this tracks last updated square view. */
	private JTextField lastTextField;

	/**
	 * The event listener object that is notified of events. See
	 * <code>EventListener</code> class.
	 */
	private EventListener eventListener;

	/**
	 * Create a Swing frame that displays the board and its solutions.
	 * 
	 * The board and the solution buffer are used together.
	 * 
	 * @param board The Sudoku board model to display and navigate.
	 * @param solutionBuffer The solution buffer object that this frame will
	 *        display and navigate.
	 * @param debug The object to use for debugging purposes.
	 * @param eventListener The event listener to notify of events. See
	 *        <code>EventListener</code> class.
	 */
	BoardFrame(Board board, SolutionBuffer solutionBuffer, String title,
		Application.Debugger debug, EventListener eventListener)
	{
		this.title = title;
		this.board = board;
		this.solutionBuffer = solutionBuffer;
		this.debug = debug;
		this.eventListener = eventListener;

		squareTextFields = new JTextField[board.dimension][board.dimension];

		setPreferredSize(new Dimension(board.dimension * defaultSquareSize(), BUTTONS_MARGIN_SIZE
			+ board.dimension * defaultSquareSize()));

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		final JPanel buttonsPanel = new JPanel();

		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

		final JPanel boardPanel = createBoardView();

		getContentPane().add(buttonsPanel, BorderLayout.NORTH);
		getContentPane().add(boardPanel, BorderLayout.CENTER);

		pack();

		nextSolutionButton = addNewButton("Next solution", buttonsPanel);
		prevSolutionButton = addNewButton("Previous solution", buttonsPanel);

		stepButton =
			debug.useStepping ? addNewButton("Step", buttonsPanel) : null;
	}

	/**
	 * Called by Swing framework when a <code>JButton</code> is clicked.
	 */
	@Override public void actionPerformed(ActionEvent event)
	{
		final Object eventSource = event.getSource();

		if(eventSource == nextSolutionButton)
		{
			showBoardSolution(++solutionID);
		}
		else if(eventSource == prevSolutionButton)
		{
			showBoardSolution(--solutionID);
		}
		else if(eventSource == stepButton)
		{
			eventListener.onStepButtonClicked();
		}
	}

	/**
	 * Create a Swing panel that draws a traditional Sudoku board.
	 * 
	 * @return The newly created Swing panel.
	 */
	private JPanel createBoardView()
	{
		final JPanel squareTextFieldsPanel = new JPanel();

		squareTextFieldsPanel
			.setLayout(new GridLayout(board.dimension, board.dimension));
		squareTextFieldsPanel.setAlignmentX(CENTER_ALIGNMENT);
		squareTextFieldsPanel.setAlignmentY(CENTER_ALIGNMENT);

		setPreferredSize(new Dimension(board.dimension * defaultSquareSize(), board.dimension
			* defaultSquareSize()));

		for(int y = 0; y < board.dimension; y++)
		{
			final int topBorderSize =
				(y % board.boxHeight == 0 && y != 0) ? 4 : 1;

			for(int x = 0; x < board.dimension; x++)
			{
				final int leftBorderSize =
					(x % board.boxWidth == 0 && x != 0) ? 4 : 1;

				final JTextField squareTextField = new JTextField();

				squareTextField.setEditable(false);
				squareTextField.setBackground(Color.WHITE);
				squareTextField.setBorder(BorderFactory.createMatteBorder(
					topBorderSize, leftBorderSize, 1, 1, Color.BLACK));
				squareTextField.setHorizontalAlignment(SwingConstants.CENTER);
				squareTextField
					.setPreferredSize(new Dimension(defaultSquareSize(), defaultSquareSize()));

				final Square square = board.square(x, y);

				if(square instanceof StaticSquare)
				{
					squareTextField.setBackground(staticSquareBgColor);
				}

				squareTextFields[y][x] = squareTextField;

				squareTextFieldsPanel.add(squareTextField);
			}
		}

		return squareTextFieldsPanel;
	}
	
	/**
	 * Update the view of a square using specified value and background color.
	 * 
	 * @param colIndex Index of the column of the square.
	 * @param rowIndex Index of the row of the square.
	 * @param value Value to redraw the view with.
	 * @param color Background color to paint the view with.
	 */
	void redrawSquare(int colIndex, int rowIndex, int value, Color color)
	{
		if(lastTextField != null)
		{
			lastTextField.setBackground(Color.WHITE);
		}

		JTextField textField = squareTextFields[rowIndex][colIndex];

		textField.setText(squareText(value)); //$NON-NLS-1$
		textField.setBackground(color);

		lastTextField = textField;
	}
	
	/**
	 * Display a solution specified by its ID.
	 * 
	 * Also updates the title of the frame accordingly. Remembers the last
	 * displayed solution.
	 * 
	 * @param solutionID An ID of the solution to display.
	 */
	void showBoardSolution(int showSolutionID)
	{
		final int[][] boardValueArray = solutionBuffer.get(showSolutionID);

		for(int y = 0; y < board.dimension; y++)
		{
			for(int x = 0; x < board.dimension; x++)
			{
				squareTextFields[y][x]
					.setText(squareText(boardValueArray[y][x]));
			}
		}

		solutionID = showSolutionID;

		final int solutionCount = solutionBuffer.size();

		nextSolutionButton.setEnabled(solutionID + 1 < solutionCount);
		prevSolutionButton.setEnabled(solutionID > 0);

		setTitle(title + " " + (solutionID + 1) + "/" + solutionCount); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Add new action button.
	 * 
	 * @param label Label of the new button to create.
	 * @param buttonsPanel Panel to attach the new button to.
	 * 
	 * @return The newly created button.
	 */
	private JButton addNewButton(String label, JPanel buttonsPanel)
	{
		final JButton button = new JButton(label);

		button.addActionListener(this);
		button.setEnabled(false);

		buttonsPanel.add(button);

		return button;
	}

	/**
	 * Obtain a comfortable size of displayed board square.
	 * 
	 * @return Calculated size of the board square side.
	 */
	private int defaultSquareSize()
	{
		return (board.dimension < 9) ? 75 : 50;
	}
	
	/**
	 * Obtain visual text string to display as square value.
	 * 
	 * @param value Value to display.
	 * @return Text string to use as displayed text.
	 */
	private String squareText(int value)
	{
		return (value != 0) ? String.valueOf(board.charFromSquareValue(value))
			: "";
	}	
}
