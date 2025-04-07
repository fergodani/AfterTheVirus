package org.atv.models;

import org.atv.models.cards.Card;
import org.atv.models.cards.ZombieCard;
import org.atv.views.PlayerInteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Game {

   private Stack<Card> playerDeck = new Stack<>();
   private Stack<Card> zombieDeck = new Stack<>();
   private Stack<Card> explorationDeck = new Stack<>();
   private List<Card> playerArea = new ArrayList<>();
   private List<Card> zombieArea = new ArrayList<>();
   private List<Card> explorationArea = new ArrayList<>();
   private List<Card> playerDiscard = new ArrayList<>();
   private List<Card> playerHand = new ArrayList<>();
   private List<Card> cardsDestroyed = new ArrayList<>();
   private final PlayerInteraction playerInteraction;
   private int defends = 0;

   private int round;
   private int survivorsRescued;
   private boolean canRetrieveMedicalEquipment = false;
   private boolean armsDamaged = false;
   private boolean legsDamaged = false;

   public Game(PlayerInteraction playerInteraction) {
      this.playerInteraction = playerInteraction;
   }

   public void init() {

   }

   public void kill(ZombieCard card) {
      this.zombieArea.remove(card);
      this.zombieDeck.push(card);
   }

   public void kill(int quantity) {
      this.playerInteraction.killZombies(zombieArea, quantity);
   }

   public void discardZombie(int quantity) {
      this.playerInteraction.discardZombies(zombieArea, quantity);
   }

   public void destroy(Card card) {
      this.playerArea.remove(card);
      this.cardsDestroyed.add(card);
   }

   public List<Card> getZombiesInDiscard() {
      return this.playerDiscard.stream()
              .filter((card) -> card instanceof ZombieCard)
              .collect(Collectors.toList());
   }

   public void incrementSurvivorsRescued(int value) {
      this.survivorsRescued += value;
   }

   public Stack<Card> getPlayerDeck() {
      return playerDeck;
   }

   public void setPlayerDeck(Stack<Card> playerDeck) {
      this.playerDeck = playerDeck;
   }

   public Stack<Card> getZombieDeck() {
      return zombieDeck;
   }

   public void setZombieDeck(Stack<Card> zombieDeck) {
      this.zombieDeck = zombieDeck;
   }

   public Stack<Card> getExplorationDeck() {
      return explorationDeck;
   }

   public void setExplorationDeck(Stack<Card> explorationDeck) {
      this.explorationDeck = explorationDeck;
   }

   public List<Card> getPlayerArea() {
      return playerArea;
   }

   public void setPlayerArea(List<Card> playerArea) {
      this.playerArea = playerArea;
   }

   public List<Card> getZombieArea() {
      return zombieArea;
   }

   public void setZombieArea(List<Card> zombieArea) {
      this.zombieArea = zombieArea;
   }

   public List<Card> getExplorationArea() {
      return explorationArea;
   }

   public void setExplorationArea(List<Card> explorationArea) {
      this.explorationArea = explorationArea;
   }

   public List<Card> getPlayerDiscard() {
      return playerDiscard;
   }

   public void setPlayerDiscard(List<Card> playerDiscard) {
      this.playerDiscard = playerDiscard;
   }

   public List<Card> getPlayerHand() {
      return playerHand;
   }

   public void setPlayerHand(List<Card> playerHand) {
      this.playerHand = playerHand;
   }

   public List<Card> getCardsDestroyed() {
      return cardsDestroyed;
   }

   public void setCardsDestroyed(List<Card> cardsDestroyed) {
      this.cardsDestroyed = cardsDestroyed;
   }

   public int getRound() {
      return round;
   }

   public void setRound(int round) {
      this.round = round;
   }

   public int getSurvivorsRescued() {
      return survivorsRescued;
   }

   public void setSurvivorsRescued(int survivorsRescued) {
      this.survivorsRescued = survivorsRescued;
   }

   public PlayerInteraction getPlayerInteraction() {
      return playerInteraction;
   }

   public int getDefends() {
      return defends;
   }

   public void setDefends(int defends) {
      this.defends = defends;
   }

   public boolean isCanRetrieveMedicalEquipment() {
      return canRetrieveMedicalEquipment;
   }

   public void setCanRetrieveMedicalEquipment(boolean canRetrieveMedicalEquipment) {
      this.canRetrieveMedicalEquipment = canRetrieveMedicalEquipment;
   }

   public boolean isArmsDamaged() {
      return armsDamaged;
   }

   public void setArmsDamaged(boolean armsDamaged) {
      this.armsDamaged = armsDamaged;
   }

   public boolean isLegsDamaged() {
      return legsDamaged;
   }

   public void setLegsDamaged(boolean legsDamaged) {
      this.legsDamaged = legsDamaged;
   }
}
