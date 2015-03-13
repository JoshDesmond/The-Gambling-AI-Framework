package com.gmail.jdesmond10.pokerai.game.logic;

import com.gmail.jdesmond10.pokerai.main.Main;

/**
 * The GameState object contains the full knowledge representation of a game of
 * poker. GameState can, however, generate LimitedGameStates from the
 * perspective of any player.
 * 
 * @author Josh
 */
public class GameState {

	private GameStateData data;

	/**
	 * Initializes a new Game with the given number of players.
	 * 
	 * @param numPlayers
	 *            the number of players in the game. (Must be a value between 2
	 *            and Main.MAX_PLAYERS)
	 **/
	public GameState(final int numPlayers) {
		// Validates numPlayers
		if (numPlayers > Main.MAX_PLAYERS || numPlayers < 2) {
			Main.fail("Creation of a GameState with " + numPlayers
					+ " number of players passed to the constructor. "
					+ "The maximum number of players allowed is "
					+ Main.MAX_PLAYERS);
		}

		data = GameStateData.getInitialGameStateData();
	}

	/**
	 * Applies an action to the current GameState, and then determines if the
	 * betting round is over. The given action applied must be from the current
	 * player to act.
	 * 
	 * @param action
	 *            The Action being made by the current player to act (must be a
	 *            legal action).
	 * @return True if the given action terminates the round of betting (i.e. if
	 *         there are no more possible bets).
	 */
	public boolean applyAction(final BettingAction action) {
		// Validates the action
		if (!isLegal(action)) {
			Main.fail("Illegal Action was submitted by player "
					+ getCurrentPlayer() + ": " + action.toString());
		}

		// Applies the action
		data = data.getResultingGameStateData(action);
		return data.isHandOver;
	}

	/**
	 * Causes players to pay their blinds, then deals out cards and properly
	 * sets all variables. This should be called after {@link #endHand()}.
	 * 
	 * @return True if the nextHand will yield no bets (if a player is all-in on
	 *         just the blinds).
	 */
	public boolean dealNextHand() {
		Main.systemPrint("Dealing Next Hand. The current gameState is "
				+ data.toString());

		data = data.dealNextHand();

		if (data.playerOneStack <= 0 || data.playerTwoStack <= 0)
			return true;
		else
			return false;
	}

	/**
	 * Transfers money over to the winner of the hand. This should only be
	 * called if the betting round is over (whether someone folds or not).
	 */
	public void endHand() {
		Main.systemPrint("Finalizing the hand. The current gameState is "
				+ data.toString());

		data = data.endHand();
	}

	/**
	 * Generates a LimitedGameState that reveals only the cards of the given
	 * player.
	 * 
	 * @param playerNumber
	 *            The Integer Value corresponding to a player (a value between 0
	 *            and 1)
	 * @return the LimitedGameState belonging to playerNumber.
	 */
	public GameStateData generateLimitedGameState(final int playerNumber) {
		// Validates playerNumber
		if (playerNumber < 0 || playerNumber > Main.MAX_PLAYERS - 1) {
			Main.fail("Illegal Value of " + playerNumber + " was passed");
		}

		if (playerNumber == 0)
			return data.generateObfuscatedData(true);
		else
			return data.generateObfuscatedData(false);
	}

	/**
	 * Determines which player is next to act
	 * 
	 * @return the integer value (a value between 0 and 7) of which player is
	 *         next to act)
	 */
	public int getCurrentPlayer() {
		if (data.playerOneToBet)
			return 0;
		return 1;
	}

	public GameStateData getGameStateData() {
		return data;
	}

	/**
	 * Determines if an action is legal for the current player to act
	 * 
	 * @param action
	 *            the BettingAction
	 * @return True if the action is legal
	 */
	private boolean isLegal(final BettingAction action) {
		// Folding is always legal
		if (action.isFold())
			return true;

		return data.isLegalAction(action);
	}

	/**
	 * Determines whether the game is over (i.e if only one player remains).
	 * 
	 * @return True if the game is over.
	 */
	public boolean isTerminalState() {

		return data.isTerminalState();
	}

	@Override
	public String toString() {
		return data.toString();
	}

}
