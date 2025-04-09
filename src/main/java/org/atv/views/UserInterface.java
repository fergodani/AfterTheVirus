package org.atv.views;

import org.atv.models.Character;

public interface UserInterface {

   void updateInfo();
   void showMessage(String message);

   PlayerInteraction getPlayerInteraction();
   void playAction();
}
