package org.atv.views.console;

import org.atv.models.cards.Card;
import org.atv.models.cards.ZombieCard;
import org.atv.views.PlayerInteraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ConsoleInteraction implements PlayerInteraction {

   private Scanner scanner = new Scanner(System.in);
   @Override
   public Card selectCard(List<Card> cardsAvailable) {
      System.out.println("Seleccione una carta:");
      for (int i = 0; i < cardsAvailable.size(); i++) {
         System.out.println(i + ": " + cardsAvailable.get(i).getName());
      }

      int eleccion = scanner.nextInt();
      return cardsAvailable.get(eleccion);
   }

   @Override
   public List<Card> selectCards(List<Card> cardsAvailable) {
      System.out.println("Seleccione las cartas (separadas por un espacio):");
      for (int i = 0; i < cardsAvailable.size(); i++) {
         System.out.println((i+1) + ": " + cardsAvailable.get(i).getName());
      }
      scanner.nextLine();
      String input = scanner.nextLine();
      String[] indexes = input.split(" ");
      List<Integer> indexesParsed = new ArrayList<>();
      List<Card> cards = new ArrayList<>();
      int index;
      for (int i = 0; i < indexes.length; i++) {
         index = Integer.parseInt(indexes[i]) - 1;
         if (index < 0 || index >= cardsAvailable.size() || indexesParsed.contains(index)) {
            continue;
         }
         indexesParsed.add(index);
         cards.add(cardsAvailable.get(Integer.parseInt(indexes[i]) - 1));
      }
      return cards;
   }

   @Override
   public int selectOption(String message, int numberOfOptions) {
      System.out.println(message);
      int option = 0;
      do {
         try {
            option = scanner.nextInt();
         } catch (Exception e) {
            System.out.println("Entrada inválida. Por favor, introduce un número.");
            scanner.next(); // Limpiar el buffer
         }

      } while(option < 1 || option > numberOfOptions);

      return option;
   }

   @Override
   public void killZombies(List<Card> zombies, int quantity) {
      if (zombies.isEmpty()) {
         System.out.println("No hay zombies restantes.");
         return;
      }
      System.out.println("Selecciona ");

      for (int i = 0; i < zombies.size(); i++) {
         System.out.println((i + 1) + ": " + zombies.get(i));
      }

      System.out.println("Introduce los pares (índice número) separados por comas. Ejemplo: 1 2, 3 3, 2 1");

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
                  carta.setZombiesLeft(carta.getZombiesLeft() - valor);
               } else {
                  System.out.println("Índice fuera de rango: " + (indiceCarta + 1));
                  killZombies(zombies, quantity);
               }
            } catch (NumberFormatException e) {
               System.out.println("Error al parsear entrada: " + par);
               killZombies(zombies, quantity);
            }
         } else {
            System.out.println("Formato inválido para el par: " + par);
            killZombies(zombies, quantity);
         }
      }
   }

   @Override
   public void discardZombies(List<Card> zombies, int quantity) {
      if (zombies.isEmpty()) {
         System.out.println("No hay zombies restantes.");
         return;
      }

      System.out.println("Selecciona ");

      for (int i = 0; i < zombies.size(); i++) {
         System.out.println((i + 1) + ": " + zombies.get(i));
      }

      System.out.println("Introduce los pares (índice número) separados por comas. Ejemplo: 1 2, 3 3, 2 1");

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

   @Override
   public void showMessage(String message) {
      System.out.println(message);
   }




}
