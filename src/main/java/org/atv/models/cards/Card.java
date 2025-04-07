package org.atv.models.cards;

import org.atv.models.actions.Action;

public class Card {

   private String name;
   private String subType;
   private int explorationCost;
   private Action action;


   private boolean prepared;

   public Card(String name, String subType, int explorationCost, Action action) {
      this.name = name;
      this.explorationCost = explorationCost;
      this.action = action;
      this.subType = subType;
      this.prepared = false;
   }

   public Card(String name, String subType, int explorationCost) {
      this.name = name;
      this.subType = subType;
      this.explorationCost = explorationCost;
   }

   public void prepare() {
      this.prepared = true;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getExplorationCost() {
      return explorationCost;
   }

   public void setExplorationCost(int explorationCost) {
      this.explorationCost = explorationCost;
   }

   public Action getAction() {
      return action;
   }

   public void setAction(Action action) {
      this.action = action;
   }

   public boolean isPrepared() {
      return prepared;
   }

   public void setPrepared(boolean prepared) {
      this.prepared = prepared;
   }

   public String getSubType() {
      return subType;
   }

   public void setSubType(String subType) {
      this.subType = subType;
   }
}
