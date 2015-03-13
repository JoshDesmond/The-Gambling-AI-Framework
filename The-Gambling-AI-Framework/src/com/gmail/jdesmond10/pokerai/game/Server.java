package com.gmail.jdesmond10.pokerai.game;

import java.util.List;

import com.gmail.jdesmond10.pokerai.agent.PokerAgent;
import com.gmail.jdesmond10.pokerai.game.logic.BettingAction;
import com.gmail.jdesmond10.pokerai.game.logic.GameState;
import com.gmail.jdesmond10.pokerai.main.Main;

/**
 * A Server is a representation of a poker table. This class informs clients of
 * the current game state after each action, and prompts clients to act when it
 * is their turn. Server contains the true gameState object, and ensures that
 * all actions by clients are legal.
 * 
 * @see Client
 * @see PokerAgent
 * @author Josh
 */
public class Server implements Runnable {
	/** List of clients */
	private final List<Client> players;
	/** Absolute state of the game. */
	private final GameState state;
	/** True if run() is looping. */
	private boolean running;
	/**
	 * True if run() has been run() before, in order that run() can also serve
	 * as a resume()
	 */
	private boolean firstLoop;
	/** True if this class should inform Main of GameState updates */
	private final boolean informMain;

	private int handCount;
	private int actionCount;

	/**
	 * Creates a new game with the given List of clients. There must be two or
	 * more clients, and they must be unique instances of Clients.
	 * 
	 * @param players
	 *            The list of clients playing. The order of players is
	 *            permanent, and will coincide with the seating order.
	 * @param informMain
	 *            True if the Server should inform Main of the gameState (False
	 *            if the Server is not being run from GUI, essentially).
	 */
	public Server(final List<Client> players, final boolean informMain) {
		// Validates players
		if (players.size() != 2) {
			Main.fail("Creation of a Server with " + players.size()
					+ " clients passed to the constructor.");
		}

		this.informMain = informMain;
		this.players = players;
		state = new GameState(players.size());
		running = true;
		firstLoop = true;
	}

	private void endGame() {
		String winningPlayer;
		if (state.getGameStateData().playerOneStack > 2) {
			winningPlayer = players.get(0).getName();
		} else {
			winningPlayer = players.get(1).getName();
		}

		if (informMain) {
			Main.consolePrint(String.format(
					"GAME OVER: There were %s total hands and %s total actions"
							+ "\nPlayer %s won", handCount, actionCount,
							winningPlayer));
			Main.gameOver();
		}
		this.stop();

	}

	/**
	 * Informs each player of the current game state by iterating through
	 * players and generating LimitedGameStates. Also informs the Main class of
	 * the new state.
	 */
	private void informPlayers() {
		for (int i = 0; i < players.size(); i++) {
			players.get(i).inform(state.generateLimitedGameState(i));
		}

		if (informMain) {
			Main.updateState(state);
		}

		Main.systemPrint(state.toString());
	}

	/**
	 * Really only here for a test method I'm writing.
	 * 
	 * @return True if the server is still running a game.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Executes a game of poker until the game is over/all players but one are
	 * eliminated. run() should also function as a resume() function.
	 */
	@Override
	public void run() {
		handCount = 0;
		actionCount = 0;

		if (informMain) {
			Main.updateState(state);
		}
		// If it's the first time being run, initialize the first hand.
		if (firstLoop) {
			informPlayers();
			firstLoop = false;
		}

		// Check after each action if the game should has been terminated
		while (running == true) {
			/*
			 * The game loop works as follows:
			 * 
			 * 1: Prompt the current player to act to act.
			 * 
			 * 2: Update the current GameState by applying the given Action
			 * 
			 * 3: Check if the game is over.
			 * 
			 * 4: Inform each client of their resulting limitedGameState (after
			 * the action was applied)
			 */

			// increase actionCount
			actionCount++;

			// Prompts current player
			final BettingAction nextAction = players.get(
					state.getCurrentPlayer()).prompt();

			Main.systemPrint(String.format("Player %s %s",
					state.getCurrentPlayer(), nextAction));

			// Updates state, and checks if that was the last bet to be made.
			if (state.applyAction(nextAction)) { // TEMP MODULARIZE THIS!
				// Increases handCount;
				handCount++;
				/*
				 * What should be done next is to inform each player of the
				 * other players final action, and again what the state is of
				 * the next hand.
				 */
				state.endHand();
				informPlayers(); // First inform

				if (Main.shouldPause()) {
					try {
						Thread.sleep(100);
					} catch (final InterruptedException e) {
						Main.fail("Interrupted Exception, " + e.getMessage());
					}
				}

				// At the end of every hand, checks to see if a player wins the
				// entire sit and go
				if (state.isTerminalState()) {
					endGame();
					break;
				}

				// Deal the next hand and then again inform players of the
				// new hand (second inform). If the next hand results in one of
				// the two players being all in, then run the next hand code
				// again.
				while (state.dealNextHand()) {
					handCount++;
					state.endHand();
					informPlayers();
					if (Main.shouldPause()) {
						try {
							Thread.sleep(100);
						} catch (final InterruptedException e) {
							Main.fail("Interrupted Exception, "
									+ e.getMessage());
						}
					}
					if (state.isTerminalState()) {
						endGame();
						break;
					}
				}
			}

			informPlayers();
			if (Main.shouldPause()) {
				try {
					Thread.sleep(30);
				} catch (final InterruptedException e) {
					Main.fail("Interrupted Exception, " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Stops further execution of the running game.
	 */
	public void stop() {
		running = false;
	}
}
