package org.atv.utils;

import org.atv.models.Character;
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

   private static CardFactory cardFactory;
   private Stack<Card> explorationDeck = new Stack<>();
   private Stack<Card> zombieDeck = new Stack<>();

   private CardFactory() {
      // Prevent instantiation
   }

   public static CardFactory getInstance() {
      if (cardFactory != null) {
         return cardFactory;
      }
      return new CardFactory();
   }

   public void createExplorationDeck() {
      // Car
      PermanentCard permanentCard = new PermanentCard("Car", 2, 2, "Vehicle", (game, self) -> {
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
      explorationDeck.add(permanentCard);

      // MC
      permanentCard = new PermanentCard("MC", 1, 1, "Vehicle", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self); // Descartar la propia carta
         Card cardSelected = game.getPlayerInteraction().selectCard(game.getZombieArea());
         game.getZombieArea().remove(cardSelected);
         game.getPlayerDiscard().add(cardSelected);
      });
      permanentCard.setPrepareAction(((game, card) -> {
         if (game.discard(((PermanentCard) card).getPrepareCost())) {
            Card cardSelected = game.getPlayerInteraction().selectCard(game.getZombieArea());
            game.getZombieArea().remove(cardSelected);
            game.getPlayerDiscard().add(cardSelected);
            ((PermanentCard) card).setPrepared(true);
         }

      }));
      explorationDeck.add(permanentCard);

      // Blockbuster
      permanentCard = new PermanentCard("Blockbuster", 2, 3, "Tramp", (game, self) -> {
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
         if (game.discard(((PermanentCard) self).getPrepareCost())) {
            for (Card card : game.getPlayerArea()) {
               if (card.getName().equals("Trapper Skill") && ((PermanentCard)card).isPrepared()) {
                  game.kill(1);
               }
            }
            ((PermanentCard) self).setPrepared(true);
         }
      }));
      explorationDeck.add(permanentCard);

      // Crossfire
      permanentCard = new PermanentCard("Crossfire", 2, 1, "Tramp", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         int preparedPerson = 0;
         for (Card card : game.getPlayerArea()) {
            if (card.getSubType().equals("Person") && ((PermanentCard)card).isPrepared()
                    || (card instanceof ZombieCard && ((ZombieCard) card).isSurvivor())) {
               preparedPerson++;
            }
         }
         game.kill(preparedPerson);
      });
      permanentCard.setPrepareAction(((game, self) -> {
         if (game.discard(((PermanentCard) self).getPrepareCost())) {
            for (Card card : game.getPlayerArea()) {
               if (card.getName().equals("Trapper Skill") && ((PermanentCard)card).isPrepared()) {
                  game.kill(1);
               }
            }
            ((PermanentCard) self).setPrepared(true);
         }
      }));
      explorationDeck.add(permanentCard);

      // Cure
      permanentCard = new PermanentCard("Cure", 2, 1, "Medical Equipment", (game, self) -> {
         game.destroy(self);
         ZombieCard cardSelected = (ZombieCard) game.getPlayerInteraction().selectCard(game.getZombieArea());
         cardSelected.setSurvivor(true);
         game.getPlayerArea().add(cardSelected);
      });
      explorationDeck.add(permanentCard);

      // Antidote
      permanentCard = new PermanentCard("Antidote", 1, 0, "Medical Equipment", (game, self) -> {
         game.destroy(self);
         game.setDefends(game.getDefends() + 1);
      });
      explorationDeck.add(permanentCard);

      // Dog
      permanentCard = new PermanentCard("Dog", 2, 0, "Animal", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         game.kill(1);
      });
      explorationDeck.add(permanentCard);

      // Entrenchment
      permanentCard = new PermanentCard("Entrenchment", 2, 1, "Tramp", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         game.kill(2);
      });
      permanentCard.setPrepareAction(((game, self) -> {
         if (game.discard(((PermanentCard) self).getPrepareCost())) {
            for (Card card : game.getPlayerArea()) {
               if (card.getName().equals("Trapper Skill") && ((PermanentCard)card).isPrepared()) {
                  game.kill(1);
               }
            }
            ((PermanentCard) self).setPrepared(true);
         }
      }));
      explorationDeck.add(permanentCard);

      // Food
      permanentCard = new PermanentCard("Food", 1, 0, "Equip", (game, self) -> {
         if (game.getZombieArea().isEmpty()) {
            int option = game.getPlayerInteraction().selectOption(
                    "�D�nde quieres poner la carta?\n1. Parte superior del mazo de �rea\n2. Parte inferior del mazo de �rea",
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
                       "�Qu� parte quieres curar?\n1. Brazos\n2. Piernas",
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
      explorationDeck.add(permanentCard);

      // Med Kit
      permanentCard = new PermanentCard("Med Kit", 1, 1, "Medical Equipment", (game, self) -> {
         if (game.getZombieArea().isEmpty()) {
            game.destroy(self);
            game.setArmsDamaged(false);
            game.setLegsDamaged(false);
         }
      });

      // Gasoline
      permanentCard = new PermanentCard("Gasoline", 2, 1, "Tramp", (game, self) -> {
         game.destroy(self);
         for (Card card : game.getPlayerArea()) {
            if (card.getSubType().equals("Tramp")
                    || card.getSubType().equals("Vehicle")
                    || card.getSubType().equals("Shooting")
                    || card.getSubType().equals("Striking")) {
               ((PermanentCard)card).prepare(game);
            }
         }
      });
      permanentCard.setPrepareAction(((game, self) -> {
         if (game.discard(((PermanentCard) self).getPrepareCost())) {
            for (Card card : game.getPlayerArea()) {
               if (card.getName().equals("Trapper Skill") && ((PermanentCard)card).isPrepared()) {
                  game.kill(1);
               }
            }
            ((PermanentCard) self).setPrepared(true);
         }
      }));
      explorationDeck.add(permanentCard);

      // Guide
      permanentCard = new PermanentCard("Guide", 2, 0, "Person", (game, self) -> {
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
      explorationDeck.add(permanentCard);

      // Lab
      permanentCard = new PermanentCard("Lab", 2, 1, "Facility", (game, self) -> {
      });
      permanentCard.setPrepareAction(((game, card) -> {
         if (game.discard("Safe House")) {
            game.setCanRetrieveMedicalEquipment(true);
            ((PermanentCard) card).setPrepared(true);
         }
      }));
      permanentCard.setExitAction(((game, card) -> {
         game.setCanRetrieveMedicalEquipment(false);
      }));
      explorationDeck.add(permanentCard);

      // Leather Jacket
      permanentCard = new PermanentCard("Leather Jacket", 2, 0, "Armor", (game, self) -> {
         game.destroy(self);
         game.setDefends(game.getDefends() + 2);
      });
      explorationDeck.add(permanentCard);

      // Perimeter Trap
      permanentCard = new PermanentCard("Perimeter Trap", 2, 1, "Trap", (game, self) -> {
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
      explorationDeck.add(permanentCard);

      // Pub
      permanentCard = new PermanentCard("Pub", 2, 1, "Facility", (game, self) -> {
      });
      permanentCard.setPrepareAction((game, card) -> {
         if(game.discard("Survivor")) {
            ((PermanentCard) card).setPrepared(true);
         }
      });
      explorationDeck.add(permanentCard);

      // Scout
      permanentCard = new PermanentCard("Scout", 2, 0, "Scout", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         Card cardSelected = game.getPlayerInteraction().selectCard(game.getExplorationArea());
         game.getPlayerDiscard().add(cardSelected);
         cardSelected = game.getPlayerInteraction().selectCard(game.getExplorationArea());
         game.getPlayerDiscard().add(cardSelected);
      });
      explorationDeck.add(permanentCard);

      // Sure Aim
      permanentCard = new PermanentCard("Sure Aim", 1, 3, "Training", (game, self) -> {
      });
      explorationDeck.add(permanentCard);

      // Weapon Skill
      permanentCard = new PermanentCard("Weapon Skill", 1, 2, "Training", (game, self) -> {
      });
      explorationDeck.add(permanentCard);

      // Trapper Skill
      permanentCard = new PermanentCard("Trapper Skill", 1, 1, "Training", (game, self) -> {
      });
      explorationDeck.add(permanentCard);

      // Tunnel
      permanentCard = new PermanentCard("Tunnel", 1, 1, "Facility", (game, self) -> {
      });
      permanentCard.setPrepareAction(((game, card) -> {
         if (game.discard("Safe House")) {
            ((PermanentCard) card).setPrepared(true);
         }
      }));
      explorationDeck.add(permanentCard);

      // Chainsaw
      permanentCard = new PermanentCard("Chainsaw", 2, 2, "Striking", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         game.kill(6);
      });
      explorationDeck.add(permanentCard);

      // Concussion Grenade
      permanentCard = new PermanentCard("Concussion Grenade", 2, 1, "Striking", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         game.discardZombie(5);

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Sure Aim") && ((PermanentCard) card).isPrepared()) {
               game.kill(1);
            }
         }
      });
      permanentCard.setPrepareAction(((game, self) -> {
         if (game.discard(((PermanentCard) self).getPrepareCost())) {
            for (Card card : game.getPlayerArea()) {
               if (card.getName().equals("Weapon Skill") && ((PermanentCard)card).isPrepared()) {
                  game.kill(1);
               }
            }
            ((PermanentCard) self).setPrepared(true);
         }
      }));
      explorationDeck.add(permanentCard);

      // Crowbar
      permanentCard = new PermanentCard("Crowbar", 1, 1, "Striking", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         game.discardZombie(1);

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Sure Aim") && ((PermanentCard) card).isPrepared()) {
               game.kill(1);
            }
         }
      });
      permanentCard.setPrepareAction(((game, self) -> {
         if (game.discard(((PermanentCard) self).getPrepareCost())) {
            game.discardZombie(1);

            for (Card card : game.getPlayerArea()) {
               if (card.getName().equals("Weapon Skill") && ((PermanentCard)card).isPrepared()) {
                  game.kill(1);
               }
            }
            ((PermanentCard) self).setPrepared(true);
         }

      }));
      explorationDeck.add(permanentCard);

      // Flamethrower
      permanentCard = new PermanentCard("Flamethrower", 3, 1, "Shooting", (game, self) -> {
         game.getPlayerArea().remove(self);
         game.getPlayerDiscard().add(self);
         game.kill(3);

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Sure Aim") && ((PermanentCard) card).isPrepared()) {
               game.kill(1);
            }
         }
      });
      permanentCard.setPrepareAction(((game, self) -> {
         if (game.discard(((PermanentCard) self).getPrepareCost())) {

            for (Card card : game.getPlayerArea()) {
               if (card.getName().equals("Weapon Skill") && ((PermanentCard)card).isPrepared()) {
                  game.kill(1);
               }
            }
            ((PermanentCard) self).setPrepared(true);
         }
      }));
      explorationDeck.add(permanentCard);

      // Machete
      permanentCard = new PermanentCard("Machete", 1, 0, "Striking", true, (game, self) -> {
         if (((PermanentCard)self).isUsed()) {
            game.getPlayerInteraction().showMessage("Solo se puede usar una vez");
            return;
         }
         game.getPlayerInteraction().showMessage("Descarta una carta:");
         Card cardSelected = game.getPlayerInteraction().selectCard(game.getPlayerHand());
         game.getPlayerHand().remove(cardSelected);
         game.getPlayerDiscard().add(cardSelected);
         game.kill(1);
         ((PermanentCard) self).setUsed(true);

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Sure Aim") && ((PermanentCard) card).isPrepared()) {
               game.kill(1);
            }
         }
      });
      permanentCard.setPrepareAction(((game, self) -> {
         if (game.discard(((PermanentCard) self).getPrepareCost())) {

            for (Card card : game.getPlayerArea()) {
               if (card.getName().equals("Weapon Skill") && ((PermanentCard)card).isPrepared()) {
                  game.kill(1);
               }
            }
            ((PermanentCard) self).setPrepared(true);
         }
      }));
      explorationDeck.add(permanentCard);

      // Minigun
      permanentCard = new ShootingCard("Minigun", 2, 0, "Shooting", true, true, (game, self) -> {
         Card ammo = ((ShootingCard) self).shoot();
         game.getPlayerDiscard().add(ammo);
         game.kill(2);

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Sure Aim") && ((PermanentCard) card).isPrepared()) {
               game.kill(1);
            }
         }
      });
      permanentCard.setPrepareAction(((game, self) -> {
         if (game.discard(((PermanentCard) self).getPrepareCost())) {

            for (Card card : game.getPlayerArea()) {
               if (card.getName().equals("Weapon Skill") && ((PermanentCard)card).isPrepared()) {
                  game.kill(1);
               }
            }
            ((PermanentCard) self).setPrepared(true);
         }
      }));
      explorationDeck.add(permanentCard);

      // Pistol
      permanentCard = new ShootingCard("Pistol", 2, 1, "Shooting", true, (game, self) -> {
         if (((PermanentCard)self).isUsed()) {
            game.getPlayerInteraction().showMessage("Solo se puede usar una vez");
            return;
         }
         if (((ShootingCard) self).getAmmo().isEmpty()) {
            game.getPlayerArea().remove(self);
            game.getPlayerDiscard().add(self);
         } else {
            int option = game.getPlayerInteraction().selectOption(
                    "�Qu� quieres hacer?\n1. Descartar munici�n\n2. Descartar la Pistola",
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
         ((PermanentCard) self).setUsed(true);

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Sure Aim") && ((PermanentCard) card).isPrepared()) {
               game.kill(1);
            }
         }
      }, true);
      permanentCard.setPrepareAction(((game, self) -> {
         if (game.discard(((PermanentCard) self).getPrepareCost())) {

            for (Card card : game.getPlayerArea()) {
               if (card.getName().equals("Weapon Skill") && ((PermanentCard)card).isPrepared()) {
                  game.kill(1);
               }
            }
            ((PermanentCard) self).setPrepared(true);
         }
      }));
      explorationDeck.add(permanentCard);

      // Rifle
      permanentCard = new ShootingCard("Rifle", 2, 1, "Shooting", true, false, (game, self) -> {
         if (((PermanentCard)self).isUsed()) {
            game.getPlayerInteraction().showMessage("Solo se puede usar una vez");
            return;
         }
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
                 "�D�nde quieres matar el Zombie?\n1. Zombie en juego\n2. Pila de descartes",
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
         ((PermanentCard) self).setUsed(true);

         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Sure Aim") && ((PermanentCard) card).isPrepared()){
               game.kill(1);
            }
         }

      });
      permanentCard.setPrepareAction(((game, self) -> {
         if (game.discard(((PermanentCard) self).getPrepareCost())) {

            for (Card card : game.getPlayerArea()) {
               if (card.getName().equals("Weapon Skill") && ((PermanentCard)card).isPrepared()) {
                  game.kill(1);
               }
            }
            ((PermanentCard) self).setPrepared(true);
         }
      }));
      explorationDeck.add(permanentCard);

      // Shotgun
      permanentCard = new ShootingCard("Shotgun", 1, 0, "Shooting", true, true, (game, self) -> {
         if (((ShootingCard) self).getAmmo().isEmpty()) {
            game.destroy(self);
         } else {
            int option = game.getPlayerInteraction().selectOption(
                    "�Qu� quieres hacer?\n1. Descartar munici�n\n2. Destruir la Escopeta",
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
            if (card.getName().equals("Sure Aim") && ((PermanentCard) card).isPrepared()) {
               game.kill(1);
            }
         }
      });
      permanentCard.setPrepareAction(((game, self) -> {
         // TODO: las armas que se preparan con munici�n
         for (Card card : game.getPlayerArea()) {
            if (card.getName().equals("Weapon Skill") && ((PermanentCard) card).isPrepared()) {
               game.kill(1);
            }
         }
      }));
      explorationDeck.add(permanentCard);

      // Magazine
      permanentCard = new PermanentCard("Magazine", 2, 0, " - ", (game, self) -> {
         List<Card> weaponsPrepared = game.getPlayerArea()
                 .stream().filter((card -> card instanceof ShootingCard && ((ShootingCard)card).isPrepared()))
                 .collect(Collectors.toList());
         StringBuilder message = new StringBuilder("�Qu� arma quieres cargar?\n");
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
      explorationDeck.add(permanentCard);

      // Raid
      EventCard eventCard = new EventCard("Raid", 2, (game, self) -> {
         Card cardSelected = game.getPlayerInteraction().selectCard(game.getExplorationArea());
         game.getPlayerDiscard().add(cardSelected);
         game.getExplorationArea().remove(cardSelected);
      });
      explorationDeck.add(eventCard);

      // Run
      eventCard = new EventCard("Run", 1, (game, self) -> {
         Card cardSelected = game.getPlayerInteraction().selectCard(game.getZombieArea());
         game.getZombieArea().remove(cardSelected);
         game.getPlayerDiscard().add(cardSelected);
      });
      explorationDeck.add(eventCard);

      // Safe house
      eventCard = new EventCard("Safe house", 2, (game, self) -> {
         int totalRescued = 0;
         List<Card> cardsToRemove = new ArrayList<>();
         for (Card card : game.getPlayerArea()) {
            if (card instanceof ZombieCard) {
               if (((ZombieCard) card).isSurvivor()) {
                  cardsToRemove.add(card);
                  game.getPlayerDiscard().add(card);
                  totalRescued++;
               }
            } else if (card instanceof PermanentCard) {
               if (card.getName().equals("Survivor") && ((PermanentCard) card).isPrepared()) {
                  cardsToRemove.add(card);
                  game.getPlayerDiscard().add(card);
                  totalRescued++;
               } else if (card.getName().equals("VIP")  && ((PermanentCard) card).isPrepared()) {
                  game.destroy(card);
                  totalRescued += 3;
               } else if (card.getName().equals("Pub")  && ((PermanentCard) card).isPrepared()) {
                  if (game.isArmsDamaged() && !game.isLegsDamaged()) {
                     game.setArmsDamaged(false);
                  } else if (!game.isArmsDamaged() && game.isLegsDamaged()) {
                     game.setLegsDamaged(false);
                  } else {
                     int option = game.getPlayerInteraction().selectOption(
                             "�Qu� parte quieres curar?\n1. Brazos\n2. Piernas",
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
         }
         game.getPlayerArea().removeAll(cardsToRemove);
         game.incrementSurvivorsRescued(totalRescued);
      });
      explorationDeck.add(eventCard);

      explorationDeck.add(new PermanentCard("Survivor", 1, 1, "Person", (game, self) -> {}));
      explorationDeck.add(new PermanentCard("Survivor", 1, 1, "Person", (game, self) -> {}));
      explorationDeck.add(new PermanentCard("Survivor", 1, 1, "Person", (game, self) -> {}));
      explorationDeck.add(new PermanentCard("VIP", 3, 1, "Person", (game, self) -> {}));

   }

   public void createZombieDeck() {

      for (int i = 0; i < 2; i++) {
         zombieDeck.push(new ZombieCard(4, ((game, card) -> {
         })));
      }
      for (int i = 0; i < 3; i++) {
         zombieDeck.push(new ZombieCard(3, ((game, card) -> {
         })));
      }
      for (int i = 0; i < 4; i++) {
         zombieDeck.push(new ZombieCard(2, ((game, card) -> {
         })));
      }
      for (int i = 0; i < 5; i++) {
         zombieDeck.push(new ZombieCard(1, ((game, card) -> {
         })));
      }

   }

   public Stack<Card> createPlayerDeck(Character character) {
      Stack<Card> playerDeck = new Stack<>();
      switch (character) {
         case ADAM: {
            for (Card card: this.explorationDeck) {
               if (card.getName().equals("Survivor")
                     || card.getName().equals("Run")
                     || card.getName().equals("Raid")
                     || card.getName().equals("Safe house")
                     || card.getName().equals("Machete")
                     || card.getName().equals("Dog")) {
                  playerDeck.push(card);
               }
            }
            break;
         }
         case JENNIE: {
            for (Card card: this.explorationDeck) {
               if (card.getName().equals("Survivor")
                       || card.getName().equals("Run")
                       || card.getName().equals("Pistol")
                       || card.getName().equals("Safe house")
                       || card.getName().equals("Weapon Skill")
                       || card.getName().equals("Scout")) {
                  playerDeck.push(card);
               }
            }
            break;
         }
         case ROBERT: {
            int runCount = 0;
            int survivorCount = 0;
            for (Card card: this.explorationDeck) {
               if (card.getName().equals("Crowbar")
                       || card.getName().equals("Safe house")
                       || card.getName().equals("Guide")
                       || card.getName().equals("Crossfire")
                       || card.getName().equals("Leather Jacket")
                       || card.getName().equals("Food")) {
                  playerDeck.push(card);
               } else if (card.getName().equals("Run")) {
                  playerDeck.push(card);
                  runCount++;
                  if (runCount == 2) {
                     break;
                  }
               } else if (card.getName().equals("Survivor")) {
                  playerDeck.push(card);
                  survivorCount++;
                  if (survivorCount == 2) {
                     break;
                  }
               }
            }
            break;
         }
         case RUTH: {
            for (Card card: this.explorationDeck) {
               if (card.getName().equals("Survivor")
                       || card.getName().equals("Shotgun")
                       || card.getName().equals("Raid")
                       || card.getName().equals("Magazine")
                       || card.getName().equals("Trapper Skill")
                       || card.getName().equals("Blockbuster")
                       || card.getName().equals("Entrenchment")
                       || card.getName().equals("Pub")
                       || card.getName().equals("Safe house")) {
                  playerDeck.push(card);
               }
            }
            break;
         }
         default:
            throw new IllegalArgumentException("Unknown character: " + character);
      }
      this.explorationDeck.removeAll(playerDeck);
      playerDeck.push(this.zombieDeck.pop());
      return playerDeck;
   }

   public Stack<Card> getExplorationDeck() {
      return explorationDeck;
   }

   public Stack<Card> getZombieDeck() {
      return zombieDeck;
   }


}
