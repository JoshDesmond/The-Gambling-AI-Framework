package com.gmail.jdesmond10.pokerai.agent;

import com.gmail.jdesmond10.pokerai.game.Client;
import com.gmail.jdesmond10.pokerai.game.logic.BettingAction;
import com.gmail.jdesmond10.pokerai.game.logic.GameStateData;

/**
 * A client that is controlled by an AI Agent. Note that any subclass of
 * PokerAgent has access to the field {@code gameState} via the getGameState()
 * method.
 * 
 * @author Josh
 */
public abstract class PokerAgent implements Client {

	/**
	 * Because gameState is immutable, I think it should be okay to have
	 * subclasses given access to gameState.
	 */
	private GameStateData gameState;

	/**
	 * Returns the currently known GameStateData object.
	 * 
	 * @return
	 */
	protected final GameStateData getGameState() {
		return gameState;
	}

	@Override
	public final void inform(final GameStateData currentState) {
		recordState(currentState);

		gameState = currentState;
	}

	/**
	 * Initialization method for a PokerAgent.
	 * 
	 * @param isPlayerOne
	 *            True if this agent is the first player to act in a game of two
	 *            players.
	 * @return The instance of the initialized agent
	 */
	public abstract PokerAgent initializeAgent(boolean isPlayerOne);

	@Override
	public abstract BettingAction prompt();

	/**
	 * Records and Parses the information about the last {@link GameStateData}
	 * 
	 * @param currentState
	 *            the most recent gameStateData object given to this agent via
	 *            inform
	 * 
	 */
	protected abstract void recordState(GameStateData currentState);
}
