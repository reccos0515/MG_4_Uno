# Flow of Game
## Start Game
### Deck Table Created
#### Filled set of UNO cards, then randomized
### Game Table Created
#### Game.Deck filled with Deck table
#### Players in lobby are assigned to random Game.seats
#### Game.Direction chose at random
#### Game.Turn chooses player in random seat
#### Game.Chair[each].Player.Hand populated with 7 cards from the deck
##### Cards drawn are removed from the deck
#### Deck[0] is moved from the deck to discard pile
##### Card moved to discard pile is removed from deck
#
### First Turn started
#### Player selects card from hand to play in discard pile
##### Card removed from player.hand
##### Card added to discardPile[0]
##### hand and discard pile is updated on everyones client
##### Game.direction is updated if card played is reverse
##### Game.turn is changed to next player according to Game.direction
#### Player has to Draw
##### deck[0] is moved from deck to player.hand
###### card is removed from deck
###### card is added to player.hand
##### server updates all clients to show correct # of cards in player.hand and in deck
### Game is Won
#### Game.winner has player.wins updated to add 1
#### All players have their player.played updated to add 1
#### Drop game and deck tables