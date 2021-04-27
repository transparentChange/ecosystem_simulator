/*
 * Animal
 * This class defines what an Animal is in the context of the program. It provides general-purpose methods for movement
 * and for actions that are related to its health, such as breeding and comparing the health of two Organisms. 
 */
abstract class Animal extends Organism implements Reproducible, Comparable<Organism> {
  private boolean moved;
  public final int HEALTH_WHEN_FULL = 250; // cannot eat when this health is already surpassed
  private final static int MIN_HEALTH_TO_BREED = 20;
  private final static int BREEDING_HEALTH_DECREASE = 10;
  private boolean isFemale; // no setter methods defined since sex should not be changed outside of the class
  
  Animal(double health, boolean isFemale) {
    super(health);
    this.moved = false;
    this.isFemale = isFemale;
  }
  
  /*
   * canBreed
   * This method determines if the Animal is able to breed, such that its health is greater than the integer constant
   * MIN_HEALTH_TO_BREED
   * @return true if the Animal's health is large enough for it to breed, false otherwise
   */
  public boolean canBreed() {
    if (super.getHealth() > MIN_HEALTH_TO_BREED) {
      return true;
    } else {
      return false;
    }
  }
  
  /*
   * getIsFemale
   * This method returns the isFemale boolean instance variable
   * @return isFemale, a boolean that indicates whether or not the Organism is female
   */
  public boolean getIsFemale() {
    return this.isFemale;
  }
  
  /*
   * changeHealth
   * This method changes the current health of the Organism by the value of the parameter change
   * @param change, a double value by which the health of the Organism will be changed (increase if positive,
   * decrease if negative)
   */
  public void changeHealth(double change) {
    if (this.getHealth() + change < HEALTH_WHEN_FULL) {
      this.setHealth(this.getHealth() + change);
    }
  }
  
  /*
   * decreaseHealthOnBreeding
   * This method decreases the instance variable health by the integer constant BREEDING_HEALTH_DECREASE
   */
  public void decreaseHealthOnBreeding() {
    this.setHealth(this.getHealth() - BREEDING_HEALTH_DECREASE);
  }
  
  /*
   * setHasMoved
   * This method sets the moved instance boolean variable to the value passed in as a parameter
   * @param hasMoved, a boolean value that will be copied to the moved boolean variable, representing if an animal
   * has already moved during a particular turn
   */
  public void setHasMoved(boolean hasMoved) {
    this.moved = hasMoved;
  }
  
  /*
   * hasMoved
   * This method returns the moved boolean instance variable
   * @return moved, a boolean value that represents if an animals has already moved during a particular turn
   */
  public boolean hasMoved() {
    return this.moved;
  }
  
  /*
   * compareTo
   * This method compares the health of the current Organism to the Organism passed in as a parameter
   * @param objToCompare, the Organism whose health is going to be compared to
   * @return 1 if the current Organism's health is greater than the healtf of objToCompare's health,
   * 0 if they are the same, and -1 otherwise
   */
  @Override
  public int compareTo(Organism objToCompare) {
    if (this.getHealth() < objToCompare.getHealth()) {
      return 1;
    } else if (this.getHealth() == objToCompare.getHealth()) {
      return 0;
    } else {
      return -1;
    }
  }       
  
  /*
   * findNextPosition
   * This method determines the next position of an animal after movement in a random fasion unless the organism
   * is a sheep and the createBiasedPosition does not return null
   * @param oldYPos and oldXPos, two integers that contain the coordinates of the position of the organism before movement
   * @param mapPortion, a 2D Organism array that is a portion of the entire map that the Organism has access to
   * @param notMovePos, an integer array that may hold a position onto which the animal cannot move upon, so these values
   * cannot be placed in the newPosition integer array
   */
  public int[] findNextPosition(int oldYPos, int oldXPos, Organism[][] mapPortion, int[] notMovePos) {
    int[] newPosition = null;
    if (mapPortion[oldYPos][oldXPos] instanceof Sheep) {
      newPosition = ((Sheep) mapPortion[oldYPos][oldXPos]).createBiasedPosition(oldYPos, oldXPos, mapPortion, notMovePos);
    }
    if (newPosition == null) { // if there are no grass or wolves directly beside the sheep 
      newPosition = new int[2];
      do {
        newPosition[0] = (int) (Math.floor(Math.random() * (5 / 3.0) - (1 / 3.0))) + oldYPos;
        if ((newPosition[0] == oldYPos - 1) || (newPosition[0] == oldYPos + 1)) {
          newPosition[1] = oldXPos;
        } else {
          newPosition[1] = (int) Math.floor(Math.random() * 3 - 1) + oldXPos;
        }
      } while ((newPosition[0] >= mapPortion.length) || (newPosition[1] >= mapPortion[0].length) 
                 || (newPosition[0] < 0) || (newPosition[1] < 0) 
                 || ((notMovePos != null) && (newPosition[0] == notMovePos[0]) && (newPosition[1] == notMovePos[1])));
    }
    
    return newPosition;
  }
  
  abstract Organism[][] movement(int oldYPos, int oldXPos, int[] newPosition, Organism[][] nextMap); // forces each animal to have implementation for movement
  
  /*
   * simpleMovement
   * This method changes the Organism map 2D array such that the position on the map defined by the oldYPos and oldXPos
   * coordinates is copied to the position on the map whose coordinates are defined by the newPosition array
   * and then made null
   * @param map, a 2D Organism array representing the relevant portion of the map that will be changed
   * @param newPosition, an integer array holding the y and x coordinates, in that order, of the position on the map that
   * will be copied to
   * @param oldYPos and oldXPos, integers that hold the y and x coordinates of the position on the map that will
   * be copied from and made null
   */
  public void simpleMovement(Organism[][] map, int[] newPosition, int oldYPos, int oldXPos) {
    map[newPosition[0]][newPosition[1]] = this;
    map[oldYPos][oldXPos] = null;
  }
  
  /*
   * switchSelection
   * This method calls the current Organism's setSelected method, passing in true to it, and the organismEaten's
   * setSelected method, passing false into it
   * @param organismEaten, the organism to that should have been previously selected
   */
  public void switchSelection(Organism organismEaten) {
    organismEaten.setSelected(false);
    this.setSelected(true);
  }
}