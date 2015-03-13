# The-Gambling-AI-Framework
	
Poker is, and forever will be an unsolved game both for computers and humans. Playing poker at a high level requires extraordinary levels of pattern recognition, an understanding of basic game theory concepts, and large depth of background knowledge. Our goal is to write a Java Framework with which to build gambling AI's that can play a game very similar to poker: an invented game that combines the elements of betting/gambling, chance, and multiple players. Thus, by simplifying the game of poker down to the simplest version we can, we can write a “first-draft”, so to speak, of a poker playing agent. Then we can complicate the game, and use our skeleton of an agent to play more complicated forms of poker.

## Game Description:

We will be using the invented game of No-Limit Sit N’ Go One Card Draw (Multi-Deck). The rules are as follows: 
Two players may play at a time (Temporary restriction(?)). Each player starts with a certain number of chips (100BB). One player is the big blind, and another is the small blind; the blinds rotate after each hand, and cost 1BB and 1/2BB.
Each player receives a card with a value between two and fourteen. These cards are face down for your opponents, so you may only view your own cards. Betting begins left of the big blind (small blind in the case of two players). A player may, during their turn, either call, check, raise, or fold. The rules of betting are the same as Texas-Hold’em (rules that specify, for example, when the betting phase is over).
The objective for each player is simple: Be the last player remaining, or win the most chips- That is, there is no reward for coming in second place in the case of more than two players; You either win or you lose.



## Goals:
Program a poker-like game that:
- Has an offline style server -> client system, where agents are clients, and a human can be a client as well.
- Has an interface that allows humans to visualize the game and play against AI’s.
- Allows some level of visualization of a game between two agents as well

