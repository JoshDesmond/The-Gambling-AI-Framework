package com.gmail.jdesmond10.pokerai.agent;

import com.gmail.jdesmond10.pokerai.game.logic.BettingAction;
import com.gmail.jdesmond10.pokerai.game.logic.GameStateData;
import com.gmail.jdesmond10.pokerai.main.Main;

/**
 * An AI that simply always bets 10 chips or folds. This is really just a mock
 * PokerAgent for testing.
 * 
 * @author Josh
 */
public final class TestClient extends PokerAgent {

	private BettingAction bet;

	@Override
	public PokerAgent initializeAgent(final boolean isPlayerOne) {
		bet = new BettingAction(10);
		return this;
	}

	@Override
	/**
	 * Bets 10 if it's legal. Otherwise Folds.
	 */
	public BettingAction prompt() {
		if (getGameState().isLegalAction(bet))
			return bet;
		else if (getGameState().isLegalAction(
				new BettingAction(getGameState().getCallAmount())))
			return new BettingAction(getGameState().getCallAmount());
		else
			return (BettingAction.FOLD);

	}

	@Override
	protected void recordState(final GameStateData currentState) {
		// Validate the currentState.
		if (!currentState.isHandOver) {

			if (currentState.getCallAmount() > currentState.getMaxBetAmount()) {
				Main.fail(String.format(
						"The maximum bet amount %s is lower than the amount to call %s"
								+ "\nThe current game state is:\n	%s",
						currentState.getMaxBetAmount(),
						currentState.getCallAmount(), currentState.toString()));
			}
		}
	}

	@Override
	public String getName() {
		return "%%%%%%"; // Don't be using this class for things outside of
							// projectTesting.
	}
}
