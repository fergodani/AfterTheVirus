package org.atv.utils;

import org.atv.models.Game;
import org.atv.models.actions.*;
import org.atv.models.cards.*;
import org.omg.CORBA.ShortSeqHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class CardFactory {

   public static List<Card> createExplorationDeck() {
      List<Card> cards = new ArrayList<>();

      // Car
      PermanentCard permanentCard = new PermanentCard("Car", 1, 1, "Vehicle", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         Card cardSelected = game.getPlayerInteraction().selectCard(game.getZombieArea());
         game.kill((ZombieCard) cardSelected);

         List<Card> zombiesToDiscard = new ArrayList<>(game.getZombieArea());
         for (Card card : zombiesToDiscard) {
            game.getZombieArea().remove(card);
            game.getPlayerDiscard().add(card);
         }
      });
      cards.add(permanentCard);

      // MC
      permanentCard = new PermanentCard("MC", 1, 1, "Vehicle", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self); // Descartar la propia carta
         Card cardSelected = game.getPlayerInteraction().selectCard(game.getZombieArea());
         game.getZombieArea().remove(cardSelected);
         game.getPlayerDiscard().add(cardSelected);
      });
      permanentCard.setPrepareAction(((game, card) -> {
         Card cardSelected = game.getPlayerInteraction().selectCard(game.getZombieArea());
         game.getZombieArea().remove(cardSelected);
         game.getPlayerDiscard().add(cardSelected);
      }));
      cards.add(permanentCard);

      // Blockbuster
      permanentCard = new PermanentCard("Blockbuster", 1, 1, "Tramp", (game, self) -> {
         game.destroy(self);
         for (Card zombie : game.getZombieArea()) {
            game.kill((ZombieCard) zombie);
         }
         for (Card card : game.getPlayerDiscard()) {
            if (card instanceof ZombieCard) {
               game.kill((ZombieCard) card);
            }
         }
      });
      permanentCard.setPrepareAction(((game, self) -> {
         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Trapper Skill")) {
               game.kill(1);
            }
         }
      }));
      cards.add(permanentCard);

      // Crossfire
      permanentCard = new PermanentCard("Crossfire", 1, 1, "Tramp", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         int preparedPerson = 0;
         for (Card card : game.getPlayerArea()) {
            if (card.getSubType().equals("Person") && card.isPrepared()
                    || (card instanceof ZombieCard && ((ZombieCard) card).isSurvivor())) {
               preparedPerson++;
            }
         }
         game.kill(preparedPerson);
      });
      permanentCard.setPrepareAction(((game, self) -> {
         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Trapper Skill")) {
               game.kill(1);
            }
         }
      }));
      cards.add(permanentCard);

      // Cure
      permanentCard = new PermanentCard("Cure", 1, 1, "Medical Equipment", (game, self) -> {
         game.destroy(self);
         ZombieCard cardSelected = (ZombieCard) game.getPlayerInteraction().selectCard(game.getZombieArea());
         cardSelected.setSurvivor(true);
         game.getPlayerArea().add(cardSelected);
      });
      cards.add(permanentCard);

      // Antidote
      permanentCard = new PermanentCard("Antidote", 1, 1, "Medical Equipment", (game, self) -> {
         game.destroy(self);
         game.setDefends(game.getDefends() + 1);
      });
      cards.add(permanentCard);

      // Dog
      permanentCard = new PermanentCard("Dog", 1, 1, "Animal", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         game.kill(1);
      });
      cards.add(permanentCard);

      // Entrechment
      permanentCard = new PermanentCard("Entrechment", 1, 1, "Tramp", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         game.kill(2);
      });
      permanentCard.setPrepareAction(((game, self) -> {
         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Trapper Skill")) {
               game.kill(1);
            }
         }
      }));
      cards.add(permanentCard);

      // Food
      permanentCard = new PermanentCard("Food", 1, 1, "Equip", (game, self) -> {
         if (game.getZombieArea().isEmpty()) {
            int option = game.getPlayerInteraction().selectOption(
                    "¿Dónde quieres poner la carta?\n1. Parte superior del mazo de área\n2. Parte inferior del mazo de área",
                    2
            );
            if (option == 1) {
               game.getExplorationDeck().push(self);
            } else {
               game.getExplorationDeck().insertElementAt(self, game.getExplorationDeck().size() - 1);
            }

            if (!game.isArmsDamaged() && !game.isLegsDamaged()) {
               return;
            }

            if (game.isArmsDamaged() && !game.isLegsDamaged()) {
               game.setArmsDamaged(false);
            } else if (!game.isArmsDamaged() && game.isLegsDamaged()) {
               game.setLegsDamaged(false);
            } else {
               option = game.getPlayerInteraction().selectOption(
                       "¿Qué parte quieres curar?\n1. Brazos\n2. Piernas",
                       2
               );
               if (option == 1) {
                  game.setArmsDamaged(false);
               } else {
                  game.setLegsDamaged(false);
               }
            }
         }
      });
      cards.add(permanentCard);

      // Gasoline
      permanentCard = new PermanentCard("Gasoline", 1, 1, "Tramp", (game, self) -> {
         game.destroy(self);
         for (Card card : game.getPlayerArea()) {
            if (card.getSubType().equals("Tramp")
                    || card.getSubType().equals("Vehicle")
                    || card.getSubType().equals("Shooting")
                    || card.getSubType().equals("Striking")) {
               card.prepare();
            }
         }
      });
      permanentCard.setPrepareAction(((game, self) -> {
         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Trapper Skill")) {
               game.kill(1);
            }
         }
      }));
      cards.add(permanentCard);

      // Guide
      permanentCard = new PermanentCard("Guide", 1, 1, "Person", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         int killsLeft = 2;
         for (int i = 2; i > 0; i--) {
            for (Card card : game.getPlayerDiscard()) {
               if (killsLeft == 0) return;
               if (card instanceof ZombieCard && ((ZombieCard) card).getValue() == i
                       && killsLeft >= i) {
                  game.kill((ZombieCard) card);
                  killsLeft -= i;
               }
            }
         }
      });
      cards.add(permanentCard);

      // Lab
      permanentCard = new PermanentCard("Lab", 1, 1, "Facility", (game, self) -> {
      });
      permanentCard.setPrepareAction(((game, card) -> {
         game.setCanRetrieveMedicalEquipment(true);
      }));
      permanentCard.setExitAction(((game, card) -> {
         game.setCanRetrieveMedicalEquipment(false);
      }));
      cards.add(permanentCard);

      // Leather Jacket
      permanentCard = new PermanentCard("Leather Jacket", 1, 1, "Armor", (game, self) -> {
         game.destroy(self);
         game.setDefends(game.getDefends() + 2);
      });
      cards.add(permanentCard);

      // Perimeter Trap
      permanentCard = new PermanentCard("Perimeter Trap", 1, 1, "Trap", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         int killsLeft = 6;
         for (int i = 4; i > 0; i--) {
            for (Card card : game.getPlayerDiscard()) {
               if (killsLeft == 0) return;
               if (card instanceof ZombieCard && ((ZombieCard) card).getValue() == i
                       && killsLeft >= i) {
                  game.kill((ZombieCard) card);
                  killsLeft -= i;
               }
            }
         }
      });
      cards.add(permanentCard);

      // Pub
      permanentCard = new PermanentCard("Pub", 1, 1, "Facility", (game, self) -> {
      });
      cards.add(permanentCard);

      // Scout
      permanentCard = new PermanentCard("Scout", 1, 1, "Scout", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         Card cardSelected = game.getPlayerInteraction().selectCard(game.getExplorationArea());
         game.getPlayerDiscard().add(cardSelected);
         cardSelected = game.getPlayerInteraction().selectCard(game.getExplorationArea());
         game.getPlayerDiscard().add(cardSelected);
      });
      cards.add(permanentCard);

      // Sure Aim
      permanentCard = new PermanentCard("Sure Aim", 1, 1, "Training", (game, self) -> {
      });
      cards.add(permanentCard);

      // Weapon Skill
      permanentCard = new PermanentCard("Weapon Skill", 1, 1, "Training", (game, self) -> {
      });
      cards.add(permanentCard);

      // Trapper Skill
      permanentCard = new PermanentCard("Trapper Skill", 1, 1, "Training", (game, self) -> {
      });
      cards.add(permanentCard);

      // Tunnel
      permanentCard = new PermanentCard("Tunnel", 1, 1, "Facility", (game, self) -> {
      });
      cards.add(permanentCard);

      // Chainsaw
      permanentCard = new PermanentCard("Chainsaw", 1, 1, "Striking", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         game.kill(6);
      });
      cards.add(permanentCard);

      // Concussion Grenade
      permanentCard = new PermanentCard("Concussion Grenade", 1, 1, "Striking", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         game.discardZombie(5);

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Sure Aim")) {
               game.kill(1);
            }
         }
      });
      permanentCard.setPrepareAction(((game, self) -> {
         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Weapon Skill")) {
               game.kill(1);
            }
         }
      }));
      cards.add(permanentCard);

      // Crowbar
      permanentCard = new PermanentCard("Crowbar", 1, 1, "Striking", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         game.discardZombie(1);

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Sure Aim")) {
               game.kill(1);
            }
         }
      });
      permanentCard.setPrepareAction(((game, self) -> {
         game.discardZombie(1);

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Weapon Skill")) {
               game.kill(1);
            }
         }
      }));
      cards.add(permanentCard);

      // Flamethrower
      permanentCard = new PermanentCard("Flamethrower", 1, 1, "Shooting", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         game.kill(3);

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Sure Aim")) {
               game.kill(1);
            }
         }
      });
      permanentCard.setPrepareAction(((game, self) -> {
         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Weapon Skill")) {
               game.kill(1);
            }
         }
      }));
      cards.add(permanentCard);

      // Machete
      permanentCard = new PermanentCard("Machete", 1, 1, "Striking", true, (game, self) -> {
         Card cardSelected = game.getPlayerInteraction().selectCard(game.getPlayerHand());
         game.getPlayerHand().remove(self);
         game.getPlayerDiscard().add(self);
         game.kill(1);

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Sure Aim")) {
               game.kill(1);
            }
         }
      });
      permanentCard.setPrepareAction(((game, self) -> {
         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Weapon Skill")) {
               game.kill(1);
            }
         }
      }));
      cards.add(permanentCard);

      // Minigun
      permanentCard = new ShootingCard("Minigun", 1, 1, "Shooting", true, true, (game, self) -> {
         Card ammo = ((ShootingCard) self).shoot();
         game.getPlayerDiscard().add(ammo);
         game.kill(2);

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Sure Aim")) {
               game.kill(1);
            }
         }
      });
      permanentCard.setPrepareAction(((game, self) -> {
         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Weapon Skill")) {
               game.kill(1);
            }
         }
      }));
      cards.add(permanentCard);

      // Pistol
      permanentCard = new ShootingCard("Pistol", 1, 1, "Shooting", true, (game, self) -> {
         if (((ShootingCard) self).getAmmo().isEmpty()) {
            game.getPlayerArea().remove(self);
            game.getPlayerDiscard().add(self);
         } else {
            int option = game.getPlayerInteraction().selectOption(
                    "¿Qué quieres hacer?\n1. Descartar munición\n2. Descartar la Pistola",
                    2
            );
            if (option == 1) {
               Card ammo = ((ShootingCard) self).shoot();
               game.getPlayerDiscard().add(ammo);
            } else {
               game.getPlayerArea().remove(self);
               game.getPlayerDiscard().add(self);
            }
         }
         game.kill(1);

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Sure Aim")) {
               game.kill(1);
            }
         }
      }, true);
      permanentCard.setPrepareAction(((game, self) -> {
         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Weapon Skill")) {
               game.kill(1);
            }
         }
      }));
      cards.add(permanentCard);

      // Rifle
      permanentCard = new ShootingCard("Rifle", 1, 1, "Shooting", true, false, (game, self) -> {
         Card ammo = ((ShootingCard) self).shoot();
         game.getPlayerDiscard().add(ammo);

         if (game.getZombieArea().isEmpty() && game.getZombiesInDiscard().isEmpty()) {
            return;
         }

         if (!game.getZombieArea().isEmpty() && game.getZombiesInDiscard().isEmpty()) {
            game.kill(1);
         } else if (game.getZombieArea().isEmpty() && !game.getZombiesInDiscard().isEmpty()) {
            for (Card card : game.getPlayerDiscard()) {
               if (card instanceof ZombieCard && ((ZombieCard) card).getValue() == 1) {
                  game.kill((ZombieCard) card);
                  return;
               }
            }
         }
         int option = game.getPlayerInteraction().selectOption(
                 "¿Dónde quieres matar el Zombie?\n1. Zombie en juego\n2. Pila de descartes",
                 2
         );
         if (option == 1) {
            game.kill(1);
         } else {
            for (Card card : game.getPlayerDiscard()) {
               if (card instanceof ZombieCard && ((ZombieCard) card).getValue() == 1) {
                  game.kill((ZombieCard) card);
                  return;
               }
            }
         }

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Sure Aim")) {
               game.kill(1);
            }
         }

      });
      permanentCard.setPrepareAction(((game, self) -> {
         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Weapon Skill")) {
               game.kill(1);
            }
         }
      }));
      cards.add(permanentCard);

      // Shotgun
      permanentCard = new ShootingCard("Shotgun", 1, 1, "Shooting", true, true, (game, self) -> {
         if (((ShootingCard) self).getAmmo().isEmpty()) {
            game.destroy(self);
         } else {
            int option = game.getPlayerInteraction().selectOption(
                    "¿Qué quieres hacer?\n1. Descartar munición\n2. Destruir la Escopeta",
                    2
            );
            if (option == 1) {
               Card ammo = ((ShootingCard) self).shoot();
               game.getPlayerDiscard().add(ammo);
            } else {
               game.destroy(self);
            }
         }
         game.kill(1);

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Sure Aim")) {
               game.kill(1);
            }
         }
      });
      permanentCard.setPrepareAction(((game, self) -> {
         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Weapon Skill")) {
               game.kill(1);
            }
         }
      }));
      cards.add(permanentCard);

      // Magazine
      permanentCard = new PermanentCard("Magazine", 1, 1, " - ", (game, self) -> {
         List<Card> weaponsPrepared = game.getPlayerArea()
                 .stream().filter((card -> card instanceof ShootingCard && card.isPrepared()))
                 .collect(Collectors.toList());
         StringBuilder message = new StringBuilder("¿Qué arma quieres cargar?\n");
         for (Card card : weaponsPrepared) {
            message.append("1. ").append(card.getName()).append("\n");
         }
         int option = game.getPlayerInteraction().selectOption(
                 message.toString(),
                 weaponsPrepared.size()
         );
         ShootingCard cardSelected = (ShootingCard)weaponsPrepared.get(option - 1);
         cardSelected.addAmmo(self);
      });
      cards.add(permanentCard);

      // Raid
      EventCard eventCard = new EventCard("Raid", 1, (game, self) -> {
         Card cardSelected = game.getPlayerInteraction().selectCard(game.getExplorationArea());
         game.getPlayerDiscard().add(cardSelected);
      });
      cards.add(eventCard);

      // Run
      eventCard = new EventCard("Run", 1, (game, self) -> {
         Card cardSelected = game.getPlayerInteraction().selectCard(game.getZombieArea());
         game.getZombieArea().remove(cardSelected);
         game.getPlayerDiscard().add(cardSelected);
      });
      cards.add(eventCard);

      // Safe house
      eventCard = new EventCard("Safe house", 1, (game, self) -> {
         int totalRescued = 0;
         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Survivor") && card.isPrepared()) {
               game.getPlayerArea().remove(card);
               game.getPlayerDiscard().add(card);
               totalRescued++;
            } else if ((card instanceof ZombieCard && ((ZombieCard) card).isSurvivor())) {
               ((ZombieCard) card).setSurvivor(false);
               game.getPlayerArea().remove(card);
               game.getPlayerDiscard().add(card);
            } else if (card.getName().equals("VIP")) {
               game.destroy(card);
               totalRescued += 3;
            } else if (card.getName().equals("Pub")) {
               if (game.isArmsDamaged() && !game.isLegsDamaged()) {
                  game.setArmsDamaged(false);
               } else if (!game.isArmsDamaged() && game.isLegsDamaged()) {
                  game.setLegsDamaged(false);
               } else {
                  int option = game.getPlayerInteraction().selectOption(
                          "¿Qué parte quieres curar?\n1. Brazos\n2. Piernas",
                          2
                  );
                  if (option == 1) {
                     game.setArmsDamaged(false);
                  } else {
                     game.setLegsDamaged(false);
                  }
               }
            }
         }
         game.incrementSurvivorsRescued(totalRescued);
      });
      cards.add(eventCard);

      cards.add(new Card("Survivor", "Person", 0));
      cards.add(new Card("Survivor", "Person", 0));
      cards.add(new Card("Survivor", "Person", 0));
      cards.add(new Card("VIP", "Person", 0));


      return null;
   }

   public static Stack<Card> createZombieCards() {
      Stack<Card> cards = new Stack<>();

      for (int i = 0; i < 2; i++) {
         cards.push(new ZombieCard(4, ((game, card) -> {
         })));
      }
      for (int i = 0; i < 3; i++) {
         cards.push(new ZombieCard(3, ((game, card) -> {
         })));
      }
      for (int i = 0; i < 4; i++) {
         cards.push(new ZombieCard(2, ((game, card) -> {
         })));
      }
      for (int i = 0; i < 5; i++) {
         cards.push(new ZombieCard(1, ((game, card) -> {
         })));
      }

      return cards;
   }

   public static List<Card> createPlayerDeck() {
      return null;
   }


}
