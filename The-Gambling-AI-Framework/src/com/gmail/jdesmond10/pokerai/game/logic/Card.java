package com.gmail.jdesmond10.pokerai.game.logic;

/**
 * A Card is a number between 2 and 14. It can also have a value of Unknown, if
 * the card is face-down, a value of Winner, if the other player folded and all
 * that needs to be known is that one hand wins, and Fold if a player folds.
 * 
 * @author Josh
 */
public enum Card {
	Unkown(0), Fold(0), Two(2), Three(3), Four(4), Five(5), Six(6), Seven(7), Eight(
			8), Nine(9), Ten(10), Jack(11), Queen(12), King(13), Ace(14), Winner(
			32767);

	public final int value;

	Card(int i) {
		value = i;
	}

	private static Card getCard(int cardVal) {
		return Card.values()[cardVal + 1];
	}

	/**
	 * 
	 * @return A random card between Two and Ace.
	 */
	public static Card getRandomCard() {
		final double d = Math.random() * 13;
		return Card.getCard((int) d + 1);
	} 

}
