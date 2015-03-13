package com.gmail.jdesmond10.pokerai.agent;

import com.gmail.jdesmond10.pokerai.game.logic.BettingAction;

/**
 * The Rock waits for an Ace or a King to play. It is the tightest, and is
 * pretty aggressive when betting.
 *
 * @author Josh Desmond
 */
public class TheRock extends SimplePokerAgent {

	@Override
	public BettingAction prompt() {

		// Play loose if you're poor or rich.
		if (isPoor() || isRich()) {
			if (getCurrentCard().value >= 11)
				return betHigh();
			else if (getCurrentCard().value > 7
					&& getGameState().getCallAmount() < 5)
				return call();
			else
				return checkFold();
		}

		// Otherwise play like a rock.
		if (getCurrentCard().value >= 13)
			return betHigh();
		else if (getCurrentCard().value > 11
				&& getGameState().getCallAmount() < 5)
			return call();
		else
			return checkFold();
	}

}
