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
 * Part of the GUI. Displays a board and navigates its solution(s).
 */
class BoardFrame extends JFrame implements ActionListener
{

	/** Some defaults. */
	static final int SQUARE_SIZE = 50;
	static final int BUTTONS_MARGIN_SIZE = 50;

	final public boolean mode;
	final public Board board;
	final public String title;
	
	/** The solution buffer model that this view displays. */
	private SolutionBuffer solutionBuffer;
	/** An array of the text fields that paint the board squares. */
	private JTextField[][] squareTextFields;
	private JButton nextSolutionButton,
			prevSolutionButton, saveSolutionsButton;
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
	BoardFrame(Board board, SolutionBuffer solutionBuffer, String title, boolean mode)
	{
		this.title = title;
		this.board = board;
		this.solutionBuffer = solutionBuffer;
		this.mode = mode;

		Font defaultFont = UIManager.getFont("TextField.font"); //$NON-NLS-1$

		staticSquareTextFont = new Font(defaultFont.getName(),
				defaultFont.getStyle() | Font.BOLD | Font.ITALIC,
				defaultFont.getSize());

		squareTextFields =
				new JTextField[board.dimension][board.dimension];

		setPreferredSize(new Dimension(board.dimension * SQUARE_SIZE,
				BUTTONS_MARGIN_SIZE + board.dimension * SQUARE_SIZE));

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel buttonsPanel = createButtons();
		JPanel boardPanel = createBoardView();

		getContentPane().add(buttonsPanel, BorderLayout.NORTH);
		getContentPane().add(boardPanel, BorderLayout.CENTER);

		pack();

		setVisible(true);

		if(solutionBuffer.size() > 0)
		{
			showBoardSolution(0);
		}
	}

	/**
	 * Swing event callback method.
	 * 
	 * Is called when a <code>JButton</code> is clicked.
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
		else if(eventSource == saveSolutionsButton)
		{
			throw new RuntimeException("Not implemented.");
		}
		else if(eventSource == stepButton)
		{
			/** For use with IDE-unassisted debugging. */
			throw new RuntimeException("Not implemented.");
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
	 * Updates square as if it is being actively updated by board solving
	 * process.
	 * 
	 * Is used as part of IDE-unassisted debugging when the board is being
	 * solved.
	 * 
	 * @param square The square whose view to update.
	 */
	void updateAsCurrentSquare(DynamicSquare square)
	{
		updateSquare(square, square.getValue(), Color.YELLOW);
	}

	void updateCurrent(int x, int y, int value)
	{
		updateSquare(x, y, value, Color.YELLOW);
	}
	
	/**
	 * Updates the view of the specified square, using given value and
	 * background color.
	 * 
	 * Tracks state by remembering last updated square, and restores it to
	 * default upon painting another square.
	 * 
	 * @param square Square whose view to update.
	 * @param value Value to display for the square (i.e. may differ from actual
	 *        square value)
	 * @param color Background color of the square view to paint.
	 */
	void updateSquare(DynamicSquare square, int value, Color color)
	{
		updateSquare(square.colIndex, square.rowIndex, value, color);
	}

	void updateSquare(int x, int y, int value, Color color)
	{
		if(lastTextField != null)
		{
			lastTextField.setBackground(Color.WHITE);
		}

		JTextField textField =
				squareTextFields[y][x];

		textField.setText(squareText(value)); //$NON-NLS-1$
		textField.setBackground(color);

		lastTextField = textField;
	}
	
	public String squareText(int value)
	{
		return (value != 0) ? String.valueOf(board.charFromSquareValue(value)) : "";
	}
	
	/**
	 * Creates the view of the board, with typical Sudoku visual style.
	 * 
	 * @return The <code>JPanel</code> object that has been created.
	 */
	private JPanel createBoardView()
	{
		JPanel squareTextFieldsPanel = new JPanel();

		squareTextFieldsPanel.setLayout(new GridLayout(board.dimension,
				board.dimension));
		squareTextFieldsPanel.setAlignmentX(CENTER_ALIGNMENT);
		squareTextFieldsPanel.setAlignmentY(CENTER_ALIGNMENT);

		/** Why is there a Dimension taking a Dimension?? */
		setPreferredSize(new Dimension(new Dimension(board.dimension
				* SQUARE_SIZE, board.dimension * SQUARE_SIZE)));

		for(int y = 0; y < board.dimension; y++)
		{

			/** finn ut om denne raden trenger en tykker linje pÃ¥ toppen: */
			int topBorderSize = (y % board.boxHeight == 0 && y != 0) ? 4
					: 1;

			for(int x = 0; x < board.dimension; x++)
			{
				/**
				 * finn ut om denne ruten er en del av en kolonne som skal ha en
				 * tykkere linje til venstre:
				 */
				int leftBorderSize =
						(x % board.boxWidth == 0 && x != 0) ? 4
								: 1;

				JTextField squareTextField = new JTextField();

				squareTextField.setEditable(false);
				squareTextField.setBackground(Color.WHITE);
				squareTextField.setBorder(BorderFactory.createMatteBorder(
						topBorderSize,
						leftBorderSize,
						1,
						1,
						Color.black));
				squareTextField.setHorizontalAlignment(SwingConstants.CENTER);
				squareTextField.setPreferredSize(new Dimension(SQUARE_SIZE,
						SQUARE_SIZE));

				int value = board.square(x, y).getValue();

				if(value != 0)
				{
					squareTextField.setFont(staticSquareTextFont);
					squareTextField.setText(squareText(value));
				}

				squareTextFields[y][x] = squareTextField;

				squareTextFieldsPanel.add(squareTextField);
			}
		}

		return squareTextFieldsPanel;
	}

	/**
	 * Creates a panel with some buttons.
	 * 
	 * @return a pointer to the created panel.
	 */
	private JPanel createButtons()
	{

		JPanel buttonsPanel = new JPanel();

		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

		/**
		 * Oppgaven sier:
		 * 
		 * "NŒr alle l¿sninger er funnet skal main metoden lage et grafisk
		 * brukergrensesnitt..."
		 * 
		 * SŒ det er ikke n¿dvendig med finn l¿sninger- knapp.
		 */
		/*
		 * findSolutionsButton = new JButton("Finn lÃ¸sning(er)");
		 * findSolutionsButton.addActionListener(this);
		 * buttonsPanel.add(findSolutionsButton);
		 */

		nextSolutionButton = new JButton("Next solution");
		nextSolutionButton.addActionListener(this);
		nextSolutionButton.setEnabled(false);
		buttonsPanel.add(nextSolutionButton);

		prevSolutionButton = new JButton("Previous solution");
		prevSolutionButton.addActionListener(this);
		prevSolutionButton.setEnabled(false);
		buttonsPanel.add(prevSolutionButton);

		saveSolutionsButton = new JButton("Save solutions");
		saveSolutionsButton.addActionListener(this);
		buttonsPanel.add(saveSolutionsButton);

		if(mode)
		{
			stepButton = new JButton("Step");
			stepButton.addActionListener(this);
			buttonsPanel.add(stepButton);
		}

		// JButton nextSquareButton = new JButton("Neste rute");
		// nextSquareButton.addActionListener(this);
		// buttonsPanel.add(nextSquareButton);

		return buttonsPanel;
	}
}
