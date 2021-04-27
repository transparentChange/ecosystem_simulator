/*
 * Inspector
 * This class finds new positions of an Organism based on a copy of the 2D map containing all the Organisms. The new
 * Organism is either in a certain direction away from an existing point on the map or is found independent of other
 * points as the closest Organism to the center of the map
 */
class Inspector {
  private Organism[][] map;
  private int[] coordinatesYX = new int[2];
  private int[] tempCoordinates = new int[2];
  
  Inspector(Organism[][] map) {
    this.map = map;
    this.coordinatesYX = getFirstCoordinates();
  } 
  
  /*
   * getCoordinates
   * This method returns the field coordinatesYX so that the values it holds can be inspected outside of the class
   * @return coordinatesYX, an integer array holding the coordinates of an Organism on the map
   */
  public int[] getCoordinates() {
    return coordinatesYX;
  }
  
  /*
   * setCoordinates
   * This method sets the coordinatesYX array to a certain y coordinate and x coordinate so that the coordinates it 
   * holds can be changed outside of the class
   * @param yPos and xPos, the y and x coordinates on the 2D Organism map that will be copied to coordinates[0]
   * and coordinates[1] respectively
   */
  public void setCoordinates(int yPos, int xPos) {
    this.coordinatesYX[0] = yPos;
    this.coordinatesYX[1] = xPos;
  }
         
  /*
   * getFirstCoordinates
   * This method returns the coordinates of the closest organism to the center of map, a 2D Organism array holding all
   * the existing organisms
   * @return coordinatesYX, an integer array of width two holding the y coordinate and x coordinate of the organism
   * that was found, unless no organism exists and null is returned
   */
  public int[] getFirstCoordinates() {
    this.coordinatesYX[0] = map.length / 2;
    this.coordinatesYX[1] = map[0].length / 2;
    int side = 1;
    final char[] MOVE = {'u', 'r', 'd', 'l'};
    
    // iterates through the map in a spiral-like motion until an organism has been found or the whole map has been traversed
    while (side <= map.length + 1) {
      for (int i = 0; i < 4; i++) {
        for (int j = 0; j < side; j++) {
          if ((coordinatesYX[0] >= 0 && coordinatesYX[0] < map.length) 
                && (coordinatesYX[1] >= 0 && coordinatesYX[1] < map[0].length) 
                && (map[coordinatesYX[0]][coordinatesYX[1]] != null)) {
            map[coordinatesYX[0]][coordinatesYX[1]].setSelected(true);
            return coordinatesYX;
          } else {
            if (MOVE[i] == 'u') {
              coordinatesYX[0] -= 1;
            } else if (MOVE[i] == 'r') {
              coordinatesYX[1] += 1;
            } else if (MOVE[i] == 'd') {
              coordinatesYX[0] += 1;
            } else if (MOVE[i] == 'l') {
              coordinatesYX[1] -= 1;
            }
          }
        }
        if ((MOVE[i] == 'r') || (MOVE[i] == 'l')) {
          side++;
        }
      }
    }

    coordinatesYX = null;
    return coordinatesYX;
  }
  
  /*
   * getNextCoordinatesHorizontal
   * This method finds the next non-empty position on the map that is either left or right of the position stored
   * in coordinatesYX in a sort of triangle-shaped scope.
   * @param c, a char representing which direction the next position is located relative to coordinatesYX. 'l' 
   * represents left, and 'r' represents right
   * @return coordinatesYX, an integer array holding the position of the newly found organism 
   
   * Could also use the same method for both vertical and horizontal movement with one using the implementation
   * of the other: rotating a quarter of a cycle, moving using the existing method, 
   * rotating a copy back and checking whether or not the space is empty or occupied. 
   * While appearing to be more elegant, this approach is less easy to understand by others reading the code
   * and decreases effectiveness due to the extra rotation involved
   */ 
  public int[] getNextCoordinatesHorizontal(char c) {
    int upScope = coordinatesYX[0];
    int downScope = map.length - coordinatesYX[0];
    
    int deltaX = 0;
    int xBorderDistance = 0;
    if (c == 'l') {
      deltaX = -1;
      xBorderDistance = coordinatesYX[1];
    } else if (c == 'r') {
      deltaX = 1;
      xBorderDistance = map[0].length - 1 - coordinatesYX[1];
    } else {
      System.out.println("An improper argument was passed into the method getNextCoordinatesHorizontal(char c)");
    }
    
    while (Math.abs(deltaX) <= xBorderDistance) { 
      for (int k = 0; ((k < upScope || k < downScope) && k <= 1.25 * Math.abs(deltaX)); k++) { 
        if ((k < upScope) && (k < downScope)) { 
          tempCoordinates = newPosition(k, deltaX);
          if (tempCoordinates != null) {
            return tempCoordinates;
          } else {
            tempCoordinates = newPosition(-k, deltaX);
            if (tempCoordinates != null) {
              return tempCoordinates;
            }
          }
        } else if (k < upScope) {
          tempCoordinates = newPosition(-k, deltaX);
            if (tempCoordinates != null) {
              return tempCoordinates;
            }
        } else if (k < downScope) {
          tempCoordinates = newPosition(k, deltaX);
          if (tempCoordinates != null) {
            return tempCoordinates;
          }
        }
      }
      
      deltaX = increment(deltaX);
    }
    
    return coordinatesYX;
  }
  
  /*
   * getNextCoordinatesVertical
   * This method finds the next non-empty position on the map that is either above or below of the position stored
   * in coordinatesYX in a sort of triangle-shaped scope.
   * @param c, a char representing which direction the next position is located relative to coordinatesYX. 'u' 
   * represents up, and 'd' represents down
   * @return coordinatesYX, an integer array holding the position of the newly found organism 
   */
  public int[] getNextCoordinatesVertical(char c) {
    int rightScope = map[0].length - coordinatesYX[1]; 
    int leftScope = coordinatesYX[1];
    
    int deltaY = 0;
    int yBorderDistance = 0;
    if (c == 'u') {
      deltaY = -1;
      yBorderDistance = coordinatesYX[0];
    } else if (c == 'd') {
      deltaY = 1;
      yBorderDistance = map.length - 1 - coordinatesYX[0];
    } else {
      System.out.println("An improper argument was passed into the method getNextCoordinatesVertical(char c)");
    }
    
    while (Math.abs(deltaY) <= yBorderDistance) { 
      for (int k = 0; ((k < rightScope || k < leftScope) && k <= 1.25 * Math.abs(deltaY)); k++) {
        if ((k < rightScope) && (k < leftScope)) {
          tempCoordinates = newPosition(deltaY, k);
          if (tempCoordinates != null) { 
            return tempCoordinates;
          } else {
            tempCoordinates = newPosition(deltaY, -k);
            if (tempCoordinates != null) {
              return tempCoordinates;
            }
          }
        } else if (k < leftScope) {
          if (map[coordinatesYX[0] + deltaY][coordinatesYX[1] - k] != null) {
            tempCoordinates = newPosition(deltaY, -k);
            if (tempCoordinates != null) {
              return tempCoordinates;
            }
          }
        } else if (k < rightScope) {
          if (map[coordinatesYX[0] + deltaY][coordinatesYX[1] + k] != null) {
            tempCoordinates = newPosition(deltaY, k);
            if (tempCoordinates != null) {
              return tempCoordinates;
            }
          }
        }
      }
      
      deltaY = increment(deltaY);
    }
    return coordinatesYX;
  }

  /*
   * newPosition
   * This method determines if the change in x and y specified by the parameters from the orginal coordinates
   * in the integer array coordinatesYX yields a spot on the map that is occupied by an Organism, and returns
   * the coordinates of that position if it does, otherwise returning null
   * @param yChange and xChange, the change in x and y being tested
   * @return coordinatesYX, the new position if that spot on the map is not empty
   * @return null, if the spot on the map determines by coordinatesYX, yChange and xChange is empty
   */
  private int[] newPosition(int yChange, int xChange) {
    if (map[coordinatesYX[0] + yChange][coordinatesYX[1] + xChange] != null) {
      coordinatesYX[0] += yChange;
      coordinatesYX[1] += xChange;
    
      return coordinatesYX;
    } else {
      return null;
    }
  }
  
  /*
   * increment
   * This method increases the counter parameter by one if it is positive and decreases it by one if it is negative, 
   * and returns its value following this operation
   * @param counter, an integer meant to be have its absolute value increased by one
   * @return counter, the updated counter
   */
  private int increment(int counter) {
    if (counter > 0) {
      counter++;
    } else if (counter < 0) {
      counter--;
    }
    
    return counter;
  }
}