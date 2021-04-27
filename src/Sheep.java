/*
 * Sheep
 * This class defines what a Sheep is. It contains methods that deal with the change in sheep's position on the 
 * 2D Organism map array depending on what it is surrounded by. It also contains a sheepNutrionValue field that
 * represents the health that a wolf will gain by eating the sheep
 */
class Sheep extends Animal {
  
  private int sheepNutritionValue = (int) (super.getHealth() * 0.25); 
  
  Sheep(double health, boolean isFemale) {
    super(health, isFemale);
    
    sheepNutritionValue = (int) (super.getHealth() * 0.5); // determined once upon instantiation
  }
  
  /*
   * getNutrionalValue
   * This method returns the nutrionnal value of the sheep for a wolf stored in the SHEEP_NUTRIONNAL_VALUE constant
   */
  public int getNutrionnalValue() {
    return sheepNutritionValue;
  }
  
  /*
   * createBiasedPosition
   * This method will return the coordinates on mapPortion corresponding to a new position on the map if grass or 
   * a wolf is found directly beside the position defined by oldYPos and oldXPos parameters, null otherwise. The new
   * coordinates will be equal to those of the grass if grass if found first, or will be one spot in the direction
   * away from the wolf relative to oldYPos and oldXPos if a wolf is found first. If the position of the sheep
   * is on the border and the wolf is next to the sheep, this method has no effect and it becomes possible that the 
   * sheep will suicide out of desperation
   * @param oldYPos and oldXPos, two integers that hold the y and x coordinates of a position on the mapPortion
   * array (meant to be occupied by a Sheep, otherwise the method will print a message to console and its main
   * contents will not execute)
   * @param mapPortion, a 2D Organism array that is a copy of the map in a restricted area around the
   * (oldXPos, oldYPos) coordinate
   * @param notMovePos, an integer array containing the coordinates of the position on the map that should not
   * be made equal to the coordinates returned by this method
   * @return newPosition, an array holding the new coordinates
   */
  public int[] createBiasedPosition(int oldYPos, int oldXPos, Organism[][] mapPortion, int[] notMovePos) {
    if (mapPortion[oldYPos][oldXPos] != null && mapPortion[oldYPos][oldXPos] instanceof Sheep) { // attempt to force proper implementation
      int[] newPosition = new int[2];
      newPosition[0] = oldYPos;
      newPosition[1] = oldXPos;
      
      // avoids null checking
      if (notMovePos == null) {
        notMovePos = new int[2];
        notMovePos[0] = -1;
        notMovePos[1] = -1;
      }
      
      if (oldYPos + 1 < mapPortion.length && ((oldYPos + 1 != notMovePos[0]) || (oldXPos != notMovePos[1])) 
            && (mapPortion[oldYPos + 1][oldXPos] != null)) { 
        
        // check position below of (oldXPos, oldYPos)
        if (mapPortion[oldYPos + 1][oldXPos] instanceof Wolf && (oldYPos - 1 >= 0)) {
          newPosition[0] -= 1;
          return newPosition;
        } else if (mapPortion[oldYPos + 1][oldXPos] instanceof Grass) {
          newPosition[0] += 1;
          return newPosition;
        }
      } else if ((oldYPos - 1 >= 0) && ((oldYPos - 1 != notMovePos[0]) || (oldXPos != notMovePos[1])) 
                   && (mapPortion[oldYPos - 1][oldXPos] != null)) {
        
        // check position above of (oldXPos, oldYPos)
        if (mapPortion[oldYPos - 1][oldXPos] instanceof Wolf && (oldYPos + 1 < mapPortion.length)) {
          newPosition[0] += 1;
          return newPosition;
        } else if (mapPortion[oldYPos - 1][oldXPos] instanceof Grass) {
          newPosition[0] -= 1;
          return newPosition;
        } 
      } else if ((oldXPos + 1 < mapPortion[0].length) && ((oldYPos != notMovePos[0]) || (oldXPos + 1 != notMovePos[1])) 
                   && (mapPortion[oldYPos][oldXPos + 1] != null)) {
        
        // check the position to the right of (oldXPos, oldYPos)
        if (mapPortion[oldYPos][oldXPos + 1] instanceof Wolf && (oldXPos - 1 >= 0)) {
          newPosition[1] -= 1;
          return newPosition;
        } else if (mapPortion[oldYPos][oldXPos + 1] instanceof Grass) {
          newPosition[1] += 1;
          return newPosition;
        }
      } else if ((oldXPos - 1 >= 0) && ((oldYPos != notMovePos[0]) || (oldXPos - 1 != notMovePos[1])) 
                   && (mapPortion[oldYPos][oldXPos - 1] != null)) {
        
        // check the position to the left of (oldXPos, oldYPos)
        if (mapPortion[oldYPos][oldXPos - 1] instanceof Wolf && (oldXPos + 1 < mapPortion[0].length)) {
          newPosition[1] += 1;
          return newPosition;
        } else if (mapPortion[oldYPos][oldXPos - 1] instanceof Grass) {
          newPosition[1] -= 1;
          return newPosition;
        }
      }
    } else {
      System.out.println("Invalid arguments passed into method createBiasedPosition(int oldYPos, int oldXPos, " + 
                          "Organism[][] mapPortion, int[] notMovePos). The oldYPos and oldXPos must represent a "
                           + "coordinate on the map (3rd argument) that is occupied by a Sheep.");
    }   
    
    return null;
  }
  
  /*
   * movement
   * This method updates the 2D Organism array, map, simulating a Sheep at the map spot with coordinates
   * specified by the parameters moving. Methods from the Animal class are called, many in order to update health and 
   * selection upon collision, when an organism is "eaten". 
   * @param oldYPos and oldXPos, two integers that hold the y and x coordinates of a position on the mapPortion
   * array (meant to be occupied by a Sheep, otherwise the method will print a message to console and its main
   * contents will not execute)
   * @param map, a 2D Organism array representing the relevant portion of the map that will be changed
   * @return map, the updated map after movement is complete
   */
  @Override
  public Organism[][] movement(int oldYPos, int oldXPos, int[] newPosition, Organism[][] map) {
    if (map[oldYPos][oldXPos] != null && map[oldYPos][oldXPos] instanceof Sheep) { // attempt to force proper implementation
      if (!((Animal) map[oldYPos][oldXPos]).hasMoved()) {
        Organism organismCollided = map[newPosition[0]][newPosition[1]]; // for clarity
        
        ((Animal) map[oldYPos][oldXPos]).setHasMoved(true);
        
        if (organismCollided == null || organismCollided instanceof Grass) {
          if (organismCollided instanceof Grass && organismCollided.getIsSelected()) {
            switchSelection(organismCollided);
          }
          
          simpleMovement(map, newPosition, oldYPos, oldXPos);
          
          if (organismCollided instanceof Grass) {
            changeHealth(organismCollided.getHealth());
          }
          
          ((Animal) map[newPosition[0]][newPosition[1]]).changeHealth(-1); // enery required to move
          
        } else if (organismCollided instanceof Wolf) {
          if (getIsSelected()) {
            organismCollided.setSelected(true);
          }
          ((Animal) organismCollided).changeHealth(20);
          map[oldYPos][oldXPos] = null;
        } // if organismCollided is a sheep, nothing happens---breeding changes are taken care of by OrganismManager
      }
    } else {
      System.out.println("Invalid arguments passed into method movement(int oldYPos, int oldXPos, Organism[][] map). " +
                         "The oldYPos and oldXPos must represent a coordinate on the map (3rd argument) " + 
                         "that is occupied by a Sheep.");
    }                     
    
    return map;
  }
  
}