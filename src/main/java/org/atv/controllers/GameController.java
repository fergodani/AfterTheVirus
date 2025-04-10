package org.atv.controllers;

import org.atv.models.Character;
import org.atv.models.Game;
import org.atv.views.UserInterface;

public class GameController {

   Game game;
   UserInterface userInterface;

   public GameController(Game game, UserInterface userInterface) {
      this.game = game;
      this.userInterface = userInterface;
   }

   public void start() {
      int option = this.userInterface.getPlayerInteraction().selectOption("Select a character: \n1. Ruth\n2. Adam\n3. Robert\n4. Jennie", 4);
      Character character = Character.values()[option - 1];
      game.init(character);

      while (true) {
         this.userInterface.updateInfo();
         this.userInterface.playAction();
         if (game.isGameOver()) {
            this.userInterface.getPlayerInteraction().showMessage("Game Over! You survived " + game.getWave() + " waves with " + game.getSurvivorsRescued() + " survivors rescued.");
            break;
         }
      }
   }
}
