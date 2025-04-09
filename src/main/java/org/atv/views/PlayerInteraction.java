package org.atv.views;

import org.atv.models.cards.Card;
import org.atv.models.cards.ZombieCard;

import java.util.List;

public interface PlayerInteraction {

   Card selectCard(List<Card> cardsAvailable);
   int selectOption(String message, int numberOfOptions);
   void killZombies(List<Card> zombies, int quantity);
   void discardZombies(List<Card> zombies, int quantity);
   void showMessage(String message);

}
