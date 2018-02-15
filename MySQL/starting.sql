/* Commands used to drop existing tables to recreate them if neccesary. */
DROP TABLE players;
DROP TABLE ranks;
/* Commands used to create the initial tables to hold all players and to hold all the players in a ranked list */
CREATE TABLE players (Username VARCHAR(10) PRIMARY KEY, HandsWon INT(6), HandsLost INT(6), HandsPlayed INT(6), AvgScore INT(4));
CREATE TABLE ranks (Rank INT(3) UNSIGNED AUTO_INCREMENT, Username VARCHAR(10), WinPercentage DOUBLE);

