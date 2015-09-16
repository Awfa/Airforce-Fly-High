# Air Force Fly High

![Air Force Fly High Screenshot](/screenshot.png?raw=true "Screenshot")
An exciting beginning to a game of Air Force Fly High!

Air Force Fly High was a project I made so that I can play [Aeroplane Chess](https://en.wikipedia.org/wiki/Aeroplane_Chess) with my friends over the computer.
To play, on your turn, click to roll the dice. Once the dice is rolled, choices that you can take will be presented to you
through purple dots on the board. Click any of the dots to play your move.

### Rules
  - Every player has 4 planes which start in the hanger
  - To get a plane out of the hanger and onto the runway, a player must roll an even number
  - A player wins when all four of their planes reach home
  - Passing another plane with your plane in a single move sends the other plane back to their hanger
  - When one of your planes land on another of your planes, it becomes a formation (currently no in-game indicator)
  - Formations block other planes from passing it but not bigger formations
  - When a plane lands on the player's color on the outer loop of the board, it has the option to jump to the next colored square
  - When a plane lands on the player's color next to the slide, it has the option to slide
  - If the plane slides, it will take out planes it crosses no matter what formation size

### Set up
To play single player with AI, run [DesktopLauncher](/desktop/src/com/haanthony/desktop/DesktopLauncher.java)

To host a server, run [ServerLauncher](/desktop/src/com/haanthony/desktop/ServerLauncher.java)
To configure how many remote players you want, edit the "NUMBER_OF_REMOTES" field in line 22 in [ServerScreen.java](/core/src/com/haanthony/screens/ServerScreen.java)
The number of remote players determines how many other players you'll play with. Use only in the interval range [0, 3]

To play as a client of a server, run [ClientLauncher](/desktop/src/com/haanthony/desktop/ClientLauncher.java)
To configure which IP address to connect to, edit the "IP_TO_CONNECT_TO" field in line 12 in [ClientScreen.java](/core/src/com/haanthony/screens/ClientScreen.java)

Once all players are connected to the server, the game will automatically start.

### To Do
  - Set up a main menu so that players can choose server options and client options without having to edit and build the source code
  - Add sound effects to the game
  - Add visual indication of airplane formation size
  
### Known Bugs
  - Server launcher is unresponsive until all remote clients connect
  - If more than one choice go to the same destination, one choice dot will block the other
