package com.gmail.jdesmond10.pokerai.agent;

import com.gmail.jdesmond10.pokerai.game.logic.BettingAction;
import com.gmail.jdesmond10.pokerai.game.logic.Card;
import com.gmail.jdesmond10.pokerai.game.logic.GameStateData;
import com.gmail.jdesmond10.pokerai.main.Main;

/**
 * Main Class of the Poker Agent
 * 
 * @author Tyler Parr
 *
 */
public class ProjectPokerPlayer extends PokerAgent {
	private boolean turn;
	private PPPHelpur pPPHelpur;

	private final int smallBlind = Main.SMALL_BLIND;

	/**
	 * Records/Parses the information about the last {@link GameStateData}
	 * 
	 * @param currentState
	 *            the most recent gameStateData object given to this agent via
	 *            inform
	 * 
	 */
	@Override
	protected final void recordState(final GameStateData currentState) {
		pPPHelpur.update(currentState);
		/*
		 * not sure if we're using this, depends on how we implement roundBets
		 * if(newRound()){ for(int i=roundBets.size()-1;i>=0;i--){
		 * roundBets.remove(i); } }
		 */
	}

	/**
	 * Initialization method for a PokerAgent.
	 * 
	 * @param isPlayerOne
	 *            True if this agent is the first player to act in a game of two
	 *            players.
	 * @return
	 */
	@Override
	public PokerAgent initializeAgent(final boolean isPlayerOne) {
		pPPHelpur = new PPPHelpur(isPlayerOne, 10);
		turn = isPlayerOne;
		return this;
	}

	/*
	 * maximizes difference between expected agent equity and expected opponent
	 * equity
	 */
	@Override
	public BettingAction prompt() {
		double eq = getEquity();
		int best = -1;
		BettingAction jim = BettingAction.FOLD;
		for (int i = getGameState().getCallAmount(); i < getGameState()
				.getMaxBetAmount(); i++) {
			jim = new BettingAction(i);
			if (makeMeMoney(jim) > eq) {
				best = i;
				eq = makeMeMoney(jim);
			}
		}
		if (best == -1)
			return BettingAction.FOLD;
		jim = new BettingAction(best);
		return (jim);
	}

	/**
	 * 
	 * @return evaluates net change of A bettingAction
	 */
	private double makeMeMoney(final BettingAction bet) {

		if ((bet.isFold()) || bet.isCheck())
			return 3;

		if (pPPHelpur.getHistory().size() - 2 < 0
				|| pPPHelpur.getHistory()
				.get(pPPHelpur.getHistory().size() - 2).isHandOver)
			return getEquity() * bet.getAmount()
					- (getTightness() - getCurrentCard().value) / 12
					* bet.getAmount();

		else
			return 0;
	}

	private double getTightness() {

		return pPPHelpur.getTight();
	}

	/**
	 * 
	 * @return raw statistical amount of pot owned
	 */
	private double getEquity() {
		if (turn = false)
			return ((getGameState().playerOneCard.value - 1.5) / 13)
					* (getGameState().playerOneAmountBetSoFar + getGameState().playerTwoAmountBetSoFar);
		else
			return ((getGameState().playerTwoCard.value - 1.5) / 13)
					* (getGameState().playerOneAmountBetSoFar + getGameState().playerTwoAmountBetSoFar);

	}

	/**
	 * 
	 * @return Current card
	 */
	private final Card getCurrentCard() {
		if (turn)
			return getGameState().playerOneCard;
		else
			return getGameState().playerTwoCard;
	}

	@Override
	public String getName() {
		return "ProjectPokerPlayer";
	}
}
