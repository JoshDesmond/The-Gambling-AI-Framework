package com.gmail.jdesmond10.pokerai.agent;

import com.gmail.jdesmond10.pokerai.game.logic.BettingAction;
import com.gmail.jdesmond10.pokerai.game.logic.Card;
import com.gmail.jdesmond10.pokerai.game.logic.GameStateData;
import com.gmail.jdesmond10.pokerai.main.Main;

/**
 * A SimplePokerAgent will only extend one method, prompt(). SimplePokerAgents
 * are simple reflex agents.
 * 
 * @author Josh Desmond
 *
 */
public abstract class SimplePokerAgent extends PokerAgent {

	private boolean isPlayerOne;

	@Override
	public final PokerAgent initializeAgent(final boolean isPlayerOne) {
		this.isPlayerOne = isPlayerOne;
		return this;
	}

	@Override
	public abstract BettingAction prompt();

	protected final Card getCurrentCard() {
		if (isPlayerOne())
			return getGameState().playerOneCard;
		else
			return getGameState().playerTwoCard;
	}

	/**
	 * @return True if the agent is player one. This is to know which player's
	 *         cards to check.
	 */
	protected final boolean isPlayerOne() {
		return isPlayerOne;
	}

	@Override
	protected final void recordState(final GameStateData currentState) {
		// Do nothing. SimplePokerAgents are only reflex agents.
	}

	/*
	 * Below this comment are utility methods that can be used by all
	 * SimplePokerAgents.
	 */

	/**
	 * 
	 * @return The number of chips you currently have.
	 */
	protected int getMyMoney() {
		if (isPlayerOne())
			return getGameState().playerOneStack;
		else
			return getGameState().playerTwoStack;
	}

	/**
	 * 
	 * @return True if you are short stacked, (or rather, have fewer than 10 big
	 *         blinds).
	 */
	protected boolean isPoor() {
		if (getMyMoney() < Main.BIG_BLIND * 10)
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @return True if you have 1.7* the amount of money you started with.
	 */
	protected boolean isRich() {
		if (getMyMoney() > 1.7 * Main.STARTING_CHIPS)
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @return A calling move (if it is legal).
	 */
	protected BettingAction call() {
		final BettingAction toRet = new BettingAction(getGameState()
				.getCallAmount());
		if (getGameState().isLegalAction(toRet))
			return toRet;
		else {
			Main.importantSystemPrint(String.format(
					"Apparently the calling move %s is considered illegal."
							+ " The current gamestate is %s", toRet.toString(),
					getGameState().toString()));
			return BettingAction.FOLD;
		}

	}

	/**
	 * 
	 * @return A BettingAction that is pretty high-- between an all-in and call.
	 */
	protected BettingAction betHigh() {
		// amount = average of an all-in and call.
		final int amount;
		final int maxBet = getGameState().getMaxBetAmount();
		final int callBet = getGameState().getCallAmount();

		if (maxBet == callBet) {
			amount = maxBet;
		} else if ((maxBet + callBet) % 2 == 1) {
			amount = (maxBet + callBet) / 2 + 1;
		} else if ((maxBet + callBet) % 2 == 0) {
			amount = (maxBet + callBet) / 2;
		} else {
			Main.fail("A number wasn't even or odd");
			amount = 0;
		}

		final BettingAction toRet = new BettingAction(amount);
		if (getGameState().isLegalAction(toRet))
			return toRet;
		else {
			Main.importantSystemPrint(String.format(
					"Apparently the move %s is considered illegal. The "
							+ "current gamestate is %s", toRet.toString(),
					getGameState().toString()));
			return BettingAction.FOLD;
		}
	}

	/**
	 * 
	 * @return A fold, or a check if a check is legal
	 */
	protected BettingAction checkFold() {
		if (getGameState().isLegalAction(new BettingAction(0)))
			return new BettingAction(0);
		else
			return BettingAction.FOLD;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
}
