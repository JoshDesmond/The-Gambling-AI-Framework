/**
 * 
 */
package com.gmail.jdesmond10.pokerai.main;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.gmail.jdesmond10.pokerai.game.Client;
import com.gmail.jdesmond10.pokerai.game.logic.BettingAction;
import com.gmail.jdesmond10.pokerai.game.logic.GameState;
import com.gmail.jdesmond10.pokerai.game.logic.GameStateData;

/**
 * This is the GUI Class of the program. This class can double as a client; if
 * the GUI is acting as a client, it will be printing/displaying the
 * gameStateData it receives via inform(), if it's not a client, however, it
 * will be displaying the gameStateData that the server statically sends to the
 * GUI every update.
 * 
 * @see Main
 * @author Josh
 *
 */
public final class SwingGUI extends JFrame implements Client, ActionListener {
	/*
	 * SwingGUI uses absolute positioning, and isn't resizable. This is simply
	 * due to a lack of time to develop any really GUI interface. Sorry for the
	 * inconvenience this may have cause any of those with smaller resolution
	 * screens.
	 */

	/**
	 * Returns the single instance of a SwingGUI.
	 * 
	 * @return the statically cached instance of GUI
	 */
	protected static SwingGUI getInstance() {
		return singletonInstance;
	}

	/** */
	private static final long serialVersionUID = -3520504145747984186L;
	private GameStateData state;
	/**
	 * True if the GUI is acting as a Client, or a human is playing through the
	 * GUI
	 */
	private boolean isClient;

	private static SwingGUI singletonInstance;

	private String playerOneName;
	private String playerTwoName;

	// JFields
	private JPanel contentPanel;
	private JPanel inputPanel;
	private JButton betButton;
	private JButton foldButton;
	private JTextField amountField;
	private JScrollPane consoleScrollPane;
	private JTextArea console;
	private JTextArea contentText;
	private JButton potSizedBetButton;
	private JButton allInBetButton;
	private JButton callButton;
	private JButton increaseBetButton;

	private JButton decreaseBetButton;
	// Client fields
	private Semaphore semaphore;

	private BettingAction nextAction;

	/**
	 * @param isClient
	 *            True if the SwingGUI should be ready to act as a client/ if a
	 *            human will be playing through the GUI.
	 */
	public SwingGUI(final boolean isClient) {
		// Validate existence of GUI singletonInstance
		if (singletonInstance != null) {
			Main.fail("Second GUI is being created before destroying the old one");
		}
		singletonInstance = this;

		initializeGui(); // Create window and display state.

		if (isClient) { // Attach GUI buttons to listeners.
			this.isClient = true;
			initializeClient();
		}

	}

	@Override
	public void actionPerformed(final ActionEvent e) {

		// Buttons will perform one way if you are a client and another
		// otherwise.
		if (isClient) {
			if (semaphore.availablePermits() > 0) {
				semaphore.drainPermits();
			}

			if (e.getSource() == betButton) {

				try {
					final int nextBetVal = Integer.parseInt(amountField
							.getText());
					setNextAction(new BettingAction(nextBetVal));
					semaphore.release();
				} catch (final NumberFormatException exc) {
					Main.systemPrint("Amount field must be an integer value");
				}

			} else if (e.getSource() == foldButton) {
				nextAction = BettingAction.FOLD;
				semaphore.release();
			}

			/* Helper Bet Buttons */
			else if (e.getSource() == callButton) {
				amountField.setText(String.valueOf(state.getCallAmount()));
			} else if (e.getSource() == potSizedBetButton) {
				amountField.setText(String.valueOf(state.potSize));
			} else if (e.getSource() == allInBetButton) {
				amountField.setText(String.valueOf(state.getMaxBetAmount()));
			}

			else if (e.getSource() == increaseBetButton) {
				try {
					int currentVal = Integer.parseInt(amountField.getText());
					currentVal++;
					// Don't allow the increaseBetButton to increase above the
					// maximum bet value.
					if (state.getMaxBetAmount() >= currentVal) {
						amountField.setText(String.valueOf(currentVal));
					}
				} catch (final NumberFormatException exc) {
					; // do nothing?
				}
			} else if (e.getSource() == decreaseBetButton) {
				try {
					int currentVal = Integer.parseInt(amountField.getText());
					currentVal--;
					// Don't allow decreaseBetButton to decrease below the
					// minimum
					// bet value.
					if (state.getCallAmount() <= currentVal) {
						amountField.setText(String.valueOf(currentVal));
					}
				} catch (final NumberFormatException exc) {
					; // do nothing?
				}
			}

			// Default case
			else {
				Main.systemPrint("Unkown Action Event Source");
			}
		}
		// If the GUI isn't a client, here's how to handle buttons
		else {

		}
	}

	/**
	 * Builds a representation of the gameState
	 * 
	 * @return a String representing Player one's chips and cards and state
	 *         information
	 */
	private String buildTextP1() {
		String ret;
		ret = String.format("\n%s \nStack: %s" + "\nCard: %s"
				+ "\nAmount Bet: %s", getPlayerOneName(), state.playerOneStack,
				state.playerOneCard, state.playerOneAmountBetSoFar);
		if (state.playerOneHasButton) {
			ret += "\nDEALER\n";
		} else {
			ret += "\n\n";
		}

		return ret;
	}

	/**
	 * Builds a representation of the gameState
	 * 
	 * @return a String representing Player two's chips and cards and state
	 *         information
	 */
	private String buildTextP2() {
		String ret;
		ret = String.format("\n%s\nStack: %s" + "\nCard: %s"
				+ "\nAmount Bet: %s", getPlayerTwoName(), state.playerTwoStack,
				state.playerTwoCard, state.playerTwoAmountBetSoFar);
		if (!state.playerOneHasButton) {
			ret += "\nDEALER\n";
		} else {
			ret += "\n\n";
		}
		return ret;
	}

	private String getPlayerOneName() {

		return playerOneName;
	}

	/**
	 * Sets the name of the first player. Use Client.getname()
	 */
	public void setPlayerOneName(final String name) {
		playerOneName = name;
	}

	/**
	 * Sets the name of the second player. Use Client.getName()
	 */
	public void setPlayerTwoName(final String name) {
		playerTwoName = name;
	}

	private String getPlayerTwoName() {

		return playerTwoName;
	}

	/**
	 * Returns the text for the current pot size
	 * 
	 * @return
	 */
	private String buildTextStack() {
		String ret;
		ret = String.format("The current pot is: %s,", state.potSize);
		ret += "\n";
		return ret;
	}

	/**
	 * 
	 * @return Current/Next Action. It is safe to assume this action is legal,
	 *         assuming only the action listeners are setting this.
	 */
	private BettingAction getNextAction() {
		if (nextAction == null) {
			Main.fail("no next action");
		}

		return nextAction;
	}

	@Override
	public void inform(final GameStateData currentState) {
		// Validate the state of GUI
		if (!isClient) {
			Main.fail("GUI is not a client, but inform() was called");
		}
		// Update the current GameStateData object
		state = currentState;
		render();
	}

	/**
	 * Initializes any client specific fields. This is only called by the
	 * constructor if the GUI is to be a client. This is called after
	 * initializeGUI.
	 */
	private void initializeClient() {
		// Validate. This is a little extreme; But hey, it doesn't hurt.
		if (!isClient) {
			Main.fail("Illegal calling of initializeClient");
		}

		semaphore = new Semaphore(0);
	}

	/**
	 * Initializes the GUI. This is called by the constructor only once.
	 */
	private void initializeGui() {
		this.setSize(1280, 800);
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(null); // Use absolute positioning

		// Initialize JComponents
		contentPanel = new JPanel();
		inputPanel = new JPanel();
		betButton = new JButton("Bet");
		foldButton = new JButton("Fold");
		amountField = new JTextField("Amount");
		console = new JTextArea();
		consoleScrollPane = new JScrollPane(console);
		contentText = new JTextArea();
		potSizedBetButton = new JButton("Pot");
		allInBetButton = new JButton("All-in");
		callButton = new JButton("Call");
		increaseBetButton = new JButton("+1");
		decreaseBetButton = new JButton("-1");

		/*
		 * The following brackets are here for aesthetic value ONLY. Do not
		 * think that these are doing anything to the code because they really
		 * aren't.
		 */
		{ /* Panel Initialization */
			contentPanel.setSize(1280, 600);
			contentPanel.setBounds(0, 0, 1280, 600);
			inputPanel.setSize(1280, 200);
			inputPanel.setBounds(0, 600, 1280, 200);

			contentPanel.setLayout(null);
			inputPanel.setLayout(null);
		}

		{ /* Button Initialization */
			betButton.addActionListener(this);
			foldButton.addActionListener(this);
			// Position and size
			betButton.setSize(300, 150);
			foldButton.setSize(300, 150);
			betButton.setBounds(5, 0, betButton.getWidth(),
					betButton.getHeight());
			foldButton.setBounds(310, 0, foldButton.getWidth(),
					foldButton.getHeight());
		}
		{ /* TextField Initialization */
			amountField.setSize(200, 50);
			amountField.setBounds(620, 0, amountField.getWidth(),
					amountField.getHeight());
			amountField.setFont(new Font("Arial", 22, 22));
		}
		{ /* Helper Button Initializations */
			callButton.setSize(60, 50);
			potSizedBetButton.setSize(60, 50);
			allInBetButton.setSize(62, 50);

			callButton.setBounds(620, 60, callButton.getWidth(),
					callButton.getHeight());
			potSizedBetButton.setBounds(690, 60, potSizedBetButton.getWidth(),
					potSizedBetButton.getHeight());
			allInBetButton.setBounds(758, 60, allInBetButton.getWidth(),
					allInBetButton.getHeight());

			callButton.addActionListener(this);
			potSizedBetButton.addActionListener(this);
			allInBetButton.addActionListener(this);

			increaseBetButton.setSize(95, 30);
			decreaseBetButton.setSize(increaseBetButton.getWidth(),
					increaseBetButton.getHeight());

			increaseBetButton.setBounds(620, 120, increaseBetButton.getWidth(),
					increaseBetButton.getHeight());
			decreaseBetButton.setBounds(725, 120, decreaseBetButton.getWidth(),
					decreaseBetButton.getHeight());

			increaseBetButton.addActionListener(this);
			decreaseBetButton.addActionListener(this);

		}
		{ /* Console Initialization */
			// consoleScrollPane.setSize(430, 150);
			// consoleScrollPane.setBounds(830, 0, consoleScrollPane.getWidth(),
			// consoleScrollPane.getHeight());

			console.setSize(430, 150);
			console.setBounds(830, 0, console.getWidth(), console.getHeight());
			console.setEditable(false);
			console.setLineWrap(true);
			console.setText("This is the console.");

			consoleScrollPane = new JScrollPane(console);
			consoleScrollPane.setSize(430, 150);
			consoleScrollPane.setBounds(830, 0, consoleScrollPane.getWidth(),
					consoleScrollPane.getHeight());
			consoleScrollPane
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		}
		{ /* Content Text Initialization */
			contentText.setSize(1000, 550);
			contentText.setBounds(10, 10, contentText.getWidth(),
					contentText.getHeight());
			contentText.setEditable(false);
			contentText.setFont(new Font("Arial", 30, 22));
			contentText.setText("Initializing Server...");
			// SUGGESTION Display the console text of the tests through
			// contentText in order to have a pretty display of all tests
			// passing while the user waits for it to load.
		}

		// Add stuff to other stuff
		this.add(contentPanel);
		this.add(inputPanel);
		inputPanel.add(betButton);
		inputPanel.add(foldButton);
		inputPanel.add(amountField);
		inputPanel.add(consoleScrollPane);
		contentPanel.add(contentText);
		inputPanel.add(callButton);
		inputPanel.add(potSizedBetButton);
		inputPanel.add(allInBetButton);
		inputPanel.add(increaseBetButton);
		inputPanel.add(decreaseBetButton);

		// Set visibility to true
		inputPanel.setVisible(true);
		contentPanel.setVisible(true);
		this.setVisible(true);
	}

	/**
	 * 
	 * @return true if the GUI is acting as a client
	 */
	public boolean isClient() {
		return isClient;
	}

	@Override
	public BettingAction prompt() {
		// Validate the state of GUI
		if (!isClient) {
			Main.fail("GUI is not a client, but prompt() was called");
		}

		// Disregard any moves that were submitted before now.
		semaphore.drainPermits();

		do {

			// Wait until a move is submitted.
			try {
				semaphore.acquire();
			} catch (final InterruptedException e) {
				e.printStackTrace();
				Main.fail("Interrupted");
			}

			if (!state.isLegalAction(getNextAction())) {
				Main.systemPrint(String
						.format("The move submitted %s is an illegal "
								+ "move. Please submit another betting action.",
								getNextAction().toString()));
			}

			// If the move was illegal, try it again.
		} while (!state.isLegalAction(getNextAction()));

		return getNextAction();
	}

	@Override
	public String getName() {
		return "You";
	}

	/**
	 * Draws the current state, or {@link GameStateData} to the contentPanel.
	 * This should be called after every update to state.
	 */
	private void render() {
		contentText.setText(buildTextStack() + buildTextP1() + buildTextP2());
	}

	/**
	 * Only legal actions should ever be set, as getNextAction will be called
	 * from inform without actually validating it.
	 * 
	 * @param b
	 *            the action to set as the next betting action.
	 */
	private void setNextAction(final BettingAction b) {
		nextAction = b;
	}

	/**
	 * Informs the GUI of the current state of the game. This data is ignored if
	 * GUI is acting as a Client.
	 * 
	 * @param state
	 *            Current GameState.
	 */
	public void updateState(final GameState state) {
		// Validate given state
		if (state == null) {
			Main.fail("Null state passed to GUI as the gameState");
		}
		// Validate GUI
		if (isClient) {
			; // Do nothing
		} else {
			this.state = state.getGameStateData();
			render();
		}
	}

	protected void printToConsole(final String message) {
		console.setText(console.getText() + "\n" + message);
	}

	protected void setClient(final boolean isClient) {
		semaphore = new Semaphore(0);
		this.isClient = isClient;
	}
}
