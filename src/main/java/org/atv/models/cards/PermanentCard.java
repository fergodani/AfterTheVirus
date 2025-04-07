package org.atv.models.cards;

import org.atv.models.actions.Action;

public class PermanentCard extends Card{

   private int usageCost;
   private Action prepareAction;
   private Action exitAction;
   private boolean oncePerTurn = false;
   private boolean used = false;

   public PermanentCard(String name, int explorationCost, int usageCost, String subType, Action action) {
      super(name, subType, explorationCost, action);
      this.usageCost = usageCost;
   }

   public PermanentCard(String name, int explorationCost, int usageCost, String subType, boolean oncePerTurn, Action action) {
      super(name, subType, explorationCost, action);
      this.usageCost = usageCost;
      this.oncePerTurn = oncePerTurn;
   }

   public PermanentCard(String name, int explorationCost, int usageCost, String subType) {
      super(name, subType, explorationCost);
      this.usageCost = usageCost;
   }

   public int getUsageCost() {
      return usageCost;
   }

   public void setUsageCost(int usageCost) {
      this.usageCost = usageCost;
   }

   public Action getPrepareAction() {
      return prepareAction;
   }

   public void setPrepareAction(Action prepareAction) {
      this.prepareAction = prepareAction;
   }

   public Action getExitAction() {
      return exitAction;
   }

   public void setExitAction(Action exitAction) {
      this.exitAction = exitAction;
   }

   public boolean isOncePerTurn() {
      return oncePerTurn;
   }

   public void setOncePerTurn(boolean oncePerTurn) {
      this.oncePerTurn = oncePerTurn;
   }

   public boolean isUsed() {
      return used;
   }

   public void setUsed(boolean used) {
      this.used = used;
   }
}
