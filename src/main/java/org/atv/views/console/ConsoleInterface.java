package org.atv.views.console;

import org.atv.models.Character;
import org.atv.models.Game;
import org.atv.models.cards.Card;
import org.atv.views.PlayerInteraction;
import org.atv.views.UserInterface;

import java.util.Scanner;

public class ConsoleInterface implements UserInterface {

   public static final String ANSI_RESET = "\u001B[0m";
   public static final String ANSI_YELLOW = "\u001B[33m";
   public static final String ANSI_MAGENTA = "\u001B[35m";
   public static final String ANSI_CYAN = "\u001B[36m";
   public static final String ANSI_BOLD = "\u001B[1m";
   public static final String ANSI_RED = "\u001B[31m";
   public static final String ANSI_GREEN = "\u001B[32m";
   public static final String ANSI_BLUE = "\u001B[34m";

   Scanner scanner = new Scanner(System.in);

   PlayerInteraction playerInteraction = new ConsoleInteraction();
   private Game game;

   public ConsoleInterface(Game game) {
      this.game = game;
      this.game.setPlayerInteraction(playerInteraction);
   }

   @Override
   public void updateInfo() {
      System.out.println(ANSI_RED + "Exploration Area:");
      for (Card card : game.getExplorationArea()) {
         System.out.print(card.toStringExploration());
      }
      System.out.println(ANSI_RESET + ANSI_GREEN + "\nZombies in play:");
      for (Card card : game.getZombieArea()) {
         System.out.print(card);
      }
      System.out.println(ANSI_RESET + ANSI_BLUE +  "\nCards in play:");
      for (Card card : game.getPlayerArea()) {
         System.out.print(card.toStringInPlay());
      }
      System.out.println(ANSI_RESET + ANSI_CYAN + "\nYour hand:");
      for (int i = 0; i < game.getPlayerHand().size(); i++) {
         System.out.print(i + ":" + game.getPlayerHand().get(i).toStringHand() + " ");
      }
      System.out.println(ANSI_RESET + "\nArms damaged: " + game.isArmsDamaged());
      System.out.println("Legs damaged: " + game.isLegsDamaged());
   }

   @Override
   public void showMessage(String message) {
      System.out.println(message);
   }

   public PlayerInteraction getPlayerInteraction() {
      return playerInteraction;
   }

   @Override
   public void playAction() {
      System.out.println("Acciones disponibles:");
      System.out.println("1. Jugar carta");
      System.out.println("2. Explorar");
      System.out.println("3. Recuperar");
      System.out.println("4. Preparar carta");
      System.out.println("5. Usar carta");
      System.out.println("6. Pasar");
      if (game.isRifleInPlay()) {
         System.out.println("7. Recargar rifle");
      }

      int action;
      try {
      action = scanner.nextInt();
      } catch (Exception e) {
         System.out.println("Entrada no válida. Por favor, introduce un número.");
         scanner.next(); // Limpiar el buffer
         return;
      }

      switch (action) {
         case 1:
            game.playCard();
            break;
         case 2:
            game.explore();
            break;
         case 3:
            game.retrieve();
            break;
         case 4:
            game.prepareCard();
            break;
         case 5:
            game.useCard();
            break;
         case 6:
            game.pass();
            break;
         case 7:
            game.reloadRifle();
            break;
         default:
            System.out.println("Acción no válida.");
      }
   }

   public void clearConsole() {
    for (int i = 0; i < 50; i++) {
        System.out.println();
    }
}
}
