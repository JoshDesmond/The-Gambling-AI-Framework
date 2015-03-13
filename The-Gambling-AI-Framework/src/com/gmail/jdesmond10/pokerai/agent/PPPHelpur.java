package com.gmail.jdesmond10.pokerai.agent;

import java.util.ArrayList;

import com.gmail.jdesmond10.pokerai.game.logic.GameStateData;

/**
 * manages Aggression and Tightness for ProjectPokerPlayer
 * 
 * @author Tyler Parr
 *
 */
public class PPPHelpur {
	private final ArrayList<Double> agg;
	private final ArrayList<Integer> tight;
	private final ArrayList<GameStateData> history;
	// list of previous gameStates

	private final double weight;
	/*
	 * represents how quickly PPPHelpur will "learn" by filling agg and tight
	 * with weight number of .5's or 8's, respectively. This prevents the
	 * program from falling into traps like assuming someone who's folded the
	 * first two turns will only play if they get an ace.
	 */

	private final boolean turn;

	public PPPHelpur(final boolean turn, final int weight) {
		agg = new ArrayList<Double>();
		tight = new ArrayList<Integer>();
		history = new ArrayList<GameStateData>();
		this.weight = weight;
		this.turn = turn;
	}

	// return range of behavior for player
	public void update(final GameStateData currentState) {
		history.add(currentState);

		// adds collected data when a fresh hand begins
		if (currentState.isHandOver) {
			updateAgg();
			updateTight();
		}
	}

	// evaluates opponent aggression over this hand
	private void updateAgg() {
		double val = 0;
		int bets = 0;
		int checks = 0;
		for (int i = history.size() - 2; i > 0 || history.get(i).isHandOver; i--) {
			if (!ourTurn(i) && !newHand(i + 1)) {
				if (history.get(i + 1).lastAction.isCheck()) {
					checks++;
				} else if (!history.get(i + 1).lastAction.isFold()) {
					bets++;
				}
			}
		}
		if (bets + checks != 0) {
			val = bets / (bets + checks);
		}
		agg.add(val);
	}

	private void updateTight() {
		if (turn) {
			if (history.get(history.size() - 1).playerTwoCard.value != 0) {
				tight.add(history.get(history.size() - 1).playerTwoCard.value);
			}
		} else {
			if (history.get(history.size() - 1).playerOneCard.value != 0) {
				tight.add(history.get(history.size() - 1).playerOneCard.value);
			}
		}
		if (history.size() - 2 < 0) {

		} else if (!ourTurn(history.size() - 2)) {
			if (history.get(history.size() - 1).lastAction == null) {
				;
			} else if (history.get(history.size() - 1).lastAction.isFold()) {
				tight.add(12);
			}
		} else {
			if (history.get(history.size() - 2).playerOneToBet) {

			}
		}
	}

	private Boolean ourTurn(final int i) {
		if (turn) {
			if (history.get(i).playerOneToBet)
				return true;
			else
				return false;
		} else {
			if (!history.get(i).playerOneToBet)
				return true;
			else
				return false;
		}
	}

	private Boolean newHand(final int i) {
		if (i < 1 || i >= history.size())
			return false;
		return history.get(i - 1).isHandOver;
	}

	/**
	 * For get algorithms: we are getConf()% sure that player behavior lies
	 * within the range of get plus or minus getVar.
	 */
	public double getAgg() {
		double rtn = 0;
		for (int i = 0; i < agg.size(); i++) {
			rtn += agg.get(i);
		}
		rtn += weight * .5;
		// prevents our value of get tight from shifting too rapidly while
		// tight.size is small
		return rtn / (agg.size() + weight);
	}

	/**
	 * finds slightly modified stdom for collected values
	 * 
	 * @return
	 */
	public double getAggVar() {
		double rtn = 0;
		double temp = 0;
		final double arg = getTight();
		for (int i = 0; i < agg.size(); i++) {
			temp = (arg - agg.get(i));
			rtn += temp * temp;
		}

		temp = (.5 - arg);
		rtn += weight * temp * temp;
		return Math.sqrt(rtn) / Math.sqrt(tight.size() + weight);
	}

	public double getTight() {
		double rtn = 0;
		for (int i = 0; i < tight.size(); i++) {
			rtn += tight.get(i);
		}
		rtn += weight * 8;
		// prevents our value of get tight from shifting too rapidly while
		// tight.size is small
		return rtn / (tight.size() + weight);
	}

	// finds slightly modified stdom for collected values
	public double getTightVar() {
		double rtn = 0;
		double temp = 0;
		final double tite = getTight();
		for (int i = 0; i < tight.size(); i++) {
			temp = (tite - tight.get(i));
			rtn += temp * temp;
		}

		temp = (8 - tite);
		rtn += weight * temp * temp;
		return Math.sqrt(rtn) / Math.sqrt(tight.size() + weight);
	}

	public ArrayList<GameStateData> getHistory() {
		return history;
	}

	@Override
	public String toString() {
		return "PPPHelpur [agg=" + getAgg() + ", aggVar=" + getAggVar()
				+ ", aggConf=" + ", tight=" + tight + ", tightVar="
				+ getTightVar() + ", tightConf=" + "]";
	}
}
