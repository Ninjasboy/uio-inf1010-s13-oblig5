import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A view of a board, part of the GUI.
 * 
 * Displays a board and navigates its solutions. The board acts a a model for
 * the frame.
 */
class BoardFrame extends JFrame implements ActionListener
{
	/** Size of the margin for the action buttons */
	static final int BUTTONS_MARGIN_SIZE = 50;

	/** Whether this frame is put into interactive debugging mode. */
	final public boolean mode;

	/** The board displayed in this frame. */
	final public Board board;

	/** Title of this frame. */
	final public String title;

	/** The solution buffer that this view displays. */
	final private SolutionBuffer solutionBuffer;

	/** The text fields that paint the board squares. */
	final private JTextField[][] squareTextFields;

	/** Action buttons */
	final JButton nextSolutionButton,
		prevSolutionButton/* , saveSolutionsButton */;

	/** Only when debug mode is set. */
	private JButton stepButton;

	/** Font for the non-user-modifiable board squares. */
	private Font staticSquareTextFont;

	/** Currently displayed solution ID. */
	private int solutionID;

	/** When board is being solved, this tracks last updated square view. */
	private JTextField lastTextField;

	/**
	 * Creates a board frame that is linked to a board solution buffer.
	 * 
	 * References the parent GUI to call its methods when necessary (an
	 * alternative to event propagation.)
	 * 
	 * @param solutionBuffer A solution buffer object that this frame will
	 *        display and navigate.
	 * @param oblig3 A parent GUI to use for event propagation.
	 */
	BoardFrame(Board board, SolutionBuffer solutionBuffer, String title,
		boolean mode)
	{
		this.title = title;
		this.board = board;
		this.solutionBuffer = solutionBuffer;
		this.mode = mode;

		Font defaultFont = UIManager.getFont("TextField.font"); //$NON-NLS-1$

		staticSquareTextFont =
			new Font(defaultFont.getName(), defaultFont.getStyle() | Font.BOLD
				| Font.ITALIC, defaultFont.getSize());

		squareTextFields = new JTextField[board.dimension][board.dimension];

		setPreferredSize(new Dimension(board.dimension * defaultSquareSize(), BUTTONS_MARGIN_SIZE
			+ board.dimension * defaultSquareSize()));

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel buttonsPanel = newButtonPanel();
		JPanel boardPanel = createBoardView();

		getContentPane().add(buttonsPanel, BorderLayout.NORTH);
		getContentPane().add(boardPanel, BorderLayout.CENTER);

		pack();

		nextSolutionButton = addNewButton("Next solution", buttonsPanel);
		prevSolutionButton = addNewButton("Previous solution", buttonsPanel);

		// saveSolutionsButton = addNewButton("Save solutions", buttonsPanel);

		setVisible(true);
	}

	/**
	 * Swing event callback method, is called when a <code>JButton</code> is
	 * clicked.
	 */
	@Override public void actionPerformed(ActionEvent event)
	{
		Object eventSource = event.getSource();

		if(eventSource == nextSolutionButton)
		{
			showBoardSolution(++solutionID);
		}
		else if(eventSource == prevSolutionButton)
		{
			showBoardSolution(--solutionID);
		}
	}

	/**
	 * Updates the view with the solution specified by its ID.
	 * 
	 * Also updates the title of the frame accordingly. Remembers the last
	 * displayed solution by updating state of the frame.
	 * 
	 * @param solutionID An ID of the solution to retrieve from solution buffer.
	 */
	void showBoardSolution(int showSolutionID)
	{
		int[][] boardData = solutionBuffer.get(showSolutionID);

		for(int y = 0; y < board.dimension; y++)
		{
			for(int x = 0; x < board.dimension; x++)
			{
				squareTextFields[y][x].setBackground(Color.WHITE);
				squareTextFields[y][x].setText(squareText(boardData[y][x]));
			}
		}

		solutionID = showSolutionID;

		int solutionCount = solutionBuffer.size();

		nextSolutionButton.setEnabled(solutionID + 1 < solutionCount);
		prevSolutionButton.setEnabled(solutionID > 0);

		setTitle(title + " " + (solutionID + 1) + "/" + solutionCount); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Repaint square to indicate it is being 'solved' by the solving process.
	 * 
	 * This is part of IDE-unassisted debugging.
	 * 
	 * @param square The square whose view to update.
	 */
	void updateAsCurrentSquare(DynamicSquare square)
	{
		updateSquare(square, square.value, Color.YELLOW);
	}

	void updateCurrent(int x, int y, int value)
	{
		updateSquare(x, y, value, Color.YELLOW);
	}

	/**
	 * Repaint a square with a given value and background color.
	 * 
	 * Tracks state by remembering last repainted square, and restores it to
	 * default upon painting another square.
	 * 
	 * @param square Square to repaint.
	 * @param value Value to display for the square (may differ from actual
	 *        square value)
	 * @param color Background color of the square.
	 */
	void updateSquare(DynamicSquare square, int value, Color color)
	{
		updateSquare(square.column.index(), square.row.index(), value, color);
	}

	void updateSquare(int x, int y, int value, Color color)
	{
		if(lastTextField != null)
		{
			lastTextField.setBackground(Color.WHITE);
		}

		JTextField textField = squareTextFields[y][x];

		textField.setText(squareText(value)); //$NON-NLS-1$
		textField.setBackground(color);

		lastTextField = textField;
	}

	public String squareText(int value)
	{
		return (value != 0) ? String.valueOf(board.charFromSquareValue(value))
			: "";
	}

	/**
	 * Create the view of the board, with typical Sudoku visual style.
	 * 
	 * @return The <code>JPanel</code> object that has been created.
	 */
	private JPanel createBoardView()
	{
		JPanel squareTextFieldsPanel = new JPanel();

		squareTextFieldsPanel
			.setLayout(new GridLayout(board.dimension, board.dimension));
		squareTextFieldsPanel.setAlignmentX(CENTER_ALIGNMENT);
		squareTextFieldsPanel.setAlignmentY(CENTER_ALIGNMENT);

		setPreferredSize(new Dimension(board.dimension * defaultSquareSize(), board.dimension
			* defaultSquareSize()));

		for(int y = 0; y < board.dimension; y++)
		{
			int topBorderSize = (y % board.boxHeight == 0 && y != 0) ? 4 : 1;

			for(int x = 0; x < board.dimension; x++)
			{
				int leftBorderSize =
					(x % board.boxWidth == 0 && x != 0) ? 4 : 1;

				JTextField squareTextField = new JTextField();

				squareTextField.setEditable(false);
				squareTextField.setBackground(Color.WHITE);
				squareTextField.setBorder(BorderFactory.createMatteBorder(
					topBorderSize, leftBorderSize, 1, 1, Color.BLACK));
				squareTextField.setHorizontalAlignment(SwingConstants.CENTER);
				squareTextField
					.setPreferredSize(new Dimension(defaultSquareSize(), defaultSquareSize()));

				Square square = board.square(x, y);

				if(square instanceof StaticSquare)
				{
					squareTextField.setBackground(Color.GRAY);
					squareTextField.setFont(staticSquareTextFont);
					squareTextField.setText(squareText(square.value()));
				}

				squareTextFields[y][x] = squareTextField;

				squareTextFieldsPanel.add(squareTextField);
			}
		}

		return squareTextFieldsPanel;
	}

	/**
	 * Create a button panel.
	 * 
	 * @return Reference to the newly created panel.
	 */
	private JPanel newButtonPanel()
	{
		final JPanel buttonsPanel = new JPanel();

		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

		return buttonsPanel;
	}

	/**
	 * Add new action button.
	 * 
	 * @param label Label of the new button to create.
	 * @param buttonsPanel Panel to attach the new button to.
	 * @return Reference to the newly created button.
	 */
	private JButton addNewButton(String label, JPanel buttonsPanel)
	{
		JButton button = new JButton(label);

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
	int defaultSquareSize()
	{
		return (board.dimension < 9) ? 75 : 50;
	}
}
