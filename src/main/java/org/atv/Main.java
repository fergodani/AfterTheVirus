package org.atv;

import org.atv.controllers.GameController;
import org.atv.models.Game;
import org.atv.views.PlayerInteraction;
import org.atv.views.UserInterface;
import org.atv.views.console.ConsoleInteraction;
import org.atv.views.console.ConsoleInterface;

public class Main {
   public static void main(String[] args) {
      Game game = new Game();
      UserInterface userInterface = new ConsoleInterface(game);
      GameController gameController = new GameController(game, userInterface);
      gameController.start();
   }
}