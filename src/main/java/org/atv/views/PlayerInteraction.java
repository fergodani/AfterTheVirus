package org.atv.views;

import org.atv.models.cards.Card;
import org.atv.models.cards.ZombieCard;

import java.util.List;

public interface PlayerInteraction {

   public Card selectCard(List<Card> cardsAvailable);
   public int selectOption(String message, int numberOfOptions);
   public void killZombies(List<Card> zombies, int quantity);
   public void discardZombies(List<Card> zombies, int quantity);
}
