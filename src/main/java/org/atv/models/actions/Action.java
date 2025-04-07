package org.atv.models.actions;

import org.atv.models.Game;
import org.atv.models.cards.Card;

@FunctionalInterface
public interface Action {
   void execute(Game game, Card card);
}
