/*
 * Organism
 * Has health, turnsSurvived, and a boolean isSelected that represents whether or not the Organism is selected by
 * the user and information will be shown about that particular organism at a particular moment in the simulation.
 * To access and change these private variables outside of the class, getter and setter methods are included, except for
 * turns survived which only provides a way to increment the turns survived an Organism survived by one.
 */
abstract class Organism {
 private double health;
 private boolean isSelected;
 private int turnsSurvived;
 
 Organism(double health) {
   this.health = health;
   this.isSelected = false;
   this.turnsSurvived = 0;
 }
 
 /*
  * addTurns
  * This method increments the turnsSurvived instance integer variable by 1
  */
 public void addTurns() {
   this.turnsSurvived++;
 }
 
 /*
  * getTurnsSurvived
  * This method returns the turnsSurvived instance variable
  * @return turnsSurvived, an integer holding the number of turns the Organism has survived thus far
  */
 public int getTurnsSurvived() {
   return this.turnsSurvived;
 }
 
 /*
  * setSelected
  * This method sets the isSelected boolean instance variable to the value of the parameter
  * @param isSelected, a boolean whose copy will be used to indicate whether or not the Organism 
  * should or should not be selected
  */
 public void setSelected(boolean isSelected) {
   this.isSelected = isSelected;
 }
 
 /*
  * getIsSelected
  * This method returns isSelected instance variable
  * @return isSelected, a boolean representing whether or not the Organism is selected or not
  */
 public boolean getIsSelected() {
   return this.isSelected;
 }
 
 /*
  * getHealth
  * This method returns the health of the Organism
  * @return health, a double storing the health of the Organism
  */
 public double getHealth() {
   return this.health;
 }
 
 /*
  * setHealth
  * This method changes the health of the Organism to the value of the parameter health
  */
 public void setHealth(double health) {
   this.health = health;
 }
}


   
    