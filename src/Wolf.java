/*
 * Wolf
 * This class presents the framework for a Wolf. It contains a single method that deals with the change in the
 * wolf's position on the 2D Organism map array depending on what it is surrounded by.
 */

class Wolf extends Animal {
  
  Wolf(double health, boolean isFemale) {
    super(health, isFemale);
  }
  
  /*
   * movement
   * This method updates the 2D Organism array, map, simulating a Wolf at the map spot with coordinates
   * specified by the parameters moving. Methods from the Animal class are called, many in order to update health and 
   * selection upon collision, when an organism is "eaten". A wolf will not eat a sheep if the wolf's 
   * @param oldYPos and oldXPos, two integers that hold the y and x coordinates of a position on the mapPortion
   * array (meant to be occupied by a Wolf, otherwise the method will print a message to console and its main
   * contents will not execute)
   * @param map, a 2D Organism array representing the relevant portion of the map that will be changed
   * @return map, the updated map after movement is complete
   */
  @Override
  public Organism[][] movement(int oldYPos, int oldXPos, int[] newPosition, Organism[][] map) {
    if (map[oldYPos][oldXPos] != null && map[oldYPos][oldXPos] instanceof Wolf) { // attempt to force proper implementation
      if (!((Animal) map[oldYPos][oldXPos]).hasMoved()) {
        Organism organismCollided = map[newPosition[0]][newPosition[1]]; // for clarity
        
        ((Animal) map[oldYPos][oldXPos]).setHasMoved(true);
        
        if (organismCollided instanceof Grass || organismCollided == null) {
          if (organismCollided instanceof Grass && organismCollided.getIsSelected()) {
            switchSelection(organismCollided);
          }
          
          simpleMovement(map, newPosition, oldYPos, oldXPos);
        } else if (organismCollided instanceof Sheep && map[oldYPos][oldXPos].getHealth() < HEALTH_WHEN_FULL) {
          if (organismCollided.getIsSelected()) {
            switchSelection(organismCollided);
          }
          
          ((Animal) map[oldYPos][oldXPos]).changeHealth(((Sheep) organismCollided).getNutrionnalValue());
          
          simpleMovement(map, newPosition, oldYPos, oldXPos);
        } else if ((organismCollided instanceof Wolf) 
                     && !((Wolf) organismCollided).getIsFemale() == ((Animal) map[oldYPos][oldXPos]).getIsFemale()
                     && ((newPosition[0] != oldYPos) || (newPosition[1] != oldXPos))) { // fighting
          if (((Animal) map[oldYPos][oldXPos]).compareTo(organismCollided) <= 0) {
            ((Animal) map[oldYPos][oldXPos]).changeHealth(-10);
          } else {
            ((Animal) map[newPosition[0]][newPosition[1]]).changeHealth(-10);
          }
        }
        
        ((Animal) map[newPosition[0]][newPosition[1]]).changeHealth(-1); // energy required to move
      }
    } else {
      System.out.println("Invalid argument passed into method movement(int oldYPos, int oldXPos, Organism[][] map). " +
                         "The oldYPos and oldXPos must be represent a coordinate on the map array that is occupied by " + 
                         "a Wolf.");
    }
    
    return map;
  }
  
}