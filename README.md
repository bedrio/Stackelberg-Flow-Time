# Stackelberg-Flow-Time
An implementation of the paper "Stackelberg strategy for routing flow over time" in Java. The JGraphT library was used for graph structure and algorithms.

## How to Run
To run this project in the correct environment, download the latest Java JDK and the latest Maven version. Here is a guide on how to download and setup [Maven](https://phoenixnap.com/kb/install-maven-windows). **It's imperative to have the correct environment or the program will not run.**

Once the environment is set up, go to Main.java and run the main method. There are some control variables at the top to modify the program
- `visualize`: toggle whether a visual should be outputted or not
- `simpleDebug`: toggles whether print statements should be outputted while running or not
- `numberOfPlayers`: determines how many players will be within the game
- `numberOfEpochs`: determines after how many epochs the program will terminate at, if not all players have reached the end
- `extraPlayers`: how many more players to enter **per** epoch. If this is set to 2, then 2 more players will enter every epoch
- `graphNumber`: determines which graph file to use (1 - 4)

**All 3 group members contributed to this project implementation, and the commit history does not refelct which player programmed a section.** Since not everyone was well-versed with Git, Beder Rifai, handled version control
