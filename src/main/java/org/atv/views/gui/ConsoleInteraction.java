package org.atv.views.gui;

import org.atv.models.cards.Card;
import org.atv.models.cards.ZombieCard;
import org.atv.views.PlayerInteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleInteraction implements PlayerInteraction {

   private Scanner scanner = new Scanner(System.in);
   @Override
   public Card selectCard(List<Card> cardsAvailable) {
      System.out.println("Selecciona una carta para descartar:");
      for (int i = 0; i < cardsAvailable.size(); i++) {
         System.out.println(i + ": " + cardsAvailable.get(i).getName());
      }

      int eleccion = scanner.nextInt();
      return cardsAvailable.get(eleccion);
   }

   @Override
   public int selectOption(String message, int numberOfOptions) {
      System.out.println(message);
      int option = 0;
      do {
         option = scanner.nextInt();

      } while(option < 1 || option > numberOfOptions);

      return option;
   }

   @Override
   public void killZombies(List<Card> zombies, int quantity) {
      System.out.println("Selecciona ");

      for (int i = 0; i < zombies.size(); i++) {
         System.out.println((i + 1) + ": " + zombies.get(i));
      }

      System.out.println("Introduce los pares (índice número) separados por comas. Ejemplo: 1 2, 3 3, 2 1");
      Scanner scanner = new Scanner(System.in);
      String input = scanner.nextLine(); // Ej: "1 2, 3 3, 2 1"

      String[] pares = input.split(",");
      for (String par : pares) {
         String[] partes = par.trim().split("\\s+");
         if (partes.length == 2) {
            try {
               int indiceCarta = Integer.parseInt(partes[0]) - 1;
               int valor = Integer.parseInt(partes[1]);

               if (indiceCarta >= 0 && indiceCarta < zombies.size()) {
                  ZombieCard carta = (ZombieCard) zombies.get(indiceCarta);
                  carta.setZombiesKilled(carta.getZombiesKilled() + valor);
               } else {
                  System.out.println("Índice fuera de rango: " + (indiceCarta + 1));
               }
            } catch (NumberFormatException e) {
               System.out.println("Error al parsear entrada: " + par);
            }
         } else {
            System.out.println("Formato inválido para el par: " + par);
         }
      }
   }

   @Override
   public void discardZombies(List<Card> zombies, int quantity) {
      System.out.println("Selecciona ");

      for (int i = 0; i < zombies.size(); i++) {
         System.out.println((i + 1) + ": " + zombies.get(i));
      }

      System.out.println("Introduce los pares (índice número) separados por comas. Ejemplo: 1 2, 3 3, 2 1");
      Scanner scanner = new Scanner(System.in);
      String input = scanner.nextLine(); // Ej: "1 2, 3 3, 2 1"

      String[] pares = input.split(",");
      for (String par : pares) {
         String[] partes = par.trim().split("\\s+");
         if (partes.length == 2) {
            try {
               int indiceCarta = Integer.parseInt(partes[0]) - 1;
               int valor = Integer.parseInt(partes[1]);

               if (indiceCarta >= 0 && indiceCarta < zombies.size()) {
                  ZombieCard carta = (ZombieCard) zombies.get(indiceCarta);
                  carta.setZombiesDiscarded(carta.getZombiesDiscarded() + valor);
               } else {
                  System.out.println("Índice fuera de rango: " + (indiceCarta + 1));
               }
            } catch (NumberFormatException e) {
               System.out.println("Error al parsear entrada: " + par);
            }
         } else {
            System.out.println("Formato inválido para el par: " + par);
         }
      }
   }


}
