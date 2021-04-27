/*
 * MapManager
 * Stores a 2D array containing organisms and a method that finds the coordinates of the boundaries around
 * a given x and y coordinate inside that array
 */
class MapManager {
  protected static Organism[][] map;
  protected static int lenY, lenX;
  private final int LEN_RESTRICTED = 3;
  
  MapManager(int ySize, int xSize) {
    map = new Organism[ySize][xSize];
    lenY = ySize;
    lenX = xSize;
  }
  
  /*
   * getMap
   * This method returns the map 2D array stored as a static field in the class
   * @return map, the array storing all the organisms shown on the grid
   */
  public Organism[][] getMap() {
    return map;
  }
  
  /*
   * createBoundaries
   * This method stores and returns the minimum and maximum horizontal and vertical coordinates within the map dimensions
   * a certain distance (half of LEN_RESTRICTED) around the coordinate passed in as a parameter  
   * @param yPos and xPos, integers containing vertical and horizontal coordinates (meant to be a point on the map)
   * @return bounds, a 2D array holding the value of: the minimum y value, the maximum y value, the x minimum value,
   * and the x maximum value, in that order, of the area around the coordinate passed in as a parameter
   */
  public int[] createBoundaries(int yPos, int xPos) {
    int[] bounds = new int[4];
    bounds[0] = Math.max(yPos - (LEN_RESTRICTED / 2), 0);
    bounds[1] = Math.min(yPos + (LEN_RESTRICTED / 2), lenY - 1);
    bounds[2] = Math.max(xPos - (LEN_RESTRICTED / 2), 0);
    bounds[3] = Math.min(xPos + (LEN_RESTRICTED / 2), lenX - 1);

    return bounds;
  }
}

/*
 * OrganismManager
 * Controls the movement of organisms around the map, keeping track of how many times each organism has moved. Also
 * contains an OrganismCreator object, creating new organisms when necessary. If one of the three types of 
 * organisms dies out, this is indicated by the existOrganisms boolean field.
 */
class OrganismManager extends MapManager {

  private boolean[] existOrganisms;
  private OrganismCreator creator;
  private final int GRASS_PER_TURN = (int) (lenY * lenX * 0.004) + 1;
  
  OrganismManager(int ySize, int xSize, int grassValue, int healthSheep, int healthWolves, int[] numOrganismsInitial) {
    super(ySize, xSize);
    creator = new OrganismCreator(ySize, xSize, grassValue, healthSheep, healthWolves);
    
    existOrganisms = new boolean[3];
    for (int i = 0; i < 3; i++) {
      existOrganisms[i] = true;
    }  
    
    // creation of organisms at the beginning of the simulation
    creator.organismTypeCreation('g', numOrganismsInitial[0]);
    creator.organismTypeCreation('s', numOrganismsInitial[1]);
    creator.organismTypeCreation('w', numOrganismsInitial[2]);
  }
  
  /*
   * continueSimulation
   * This method determines if all organisms are present based on the existOrganism array that holds boolean values
   * for the presence of each type of organism
   * @return true if all organisms are present, false otherwise
   */
  public boolean continueSimulation() {
    if (existOrganisms[0])  {
      return true;
    } else {
      return false;
    }
  }
  
  /*
   * movementCycle
   * This method represents on cycle of movement. It calls the movement method for an organism with health greater
   * than 0 that has not already moved, incrementing the number of cycles it has survived while doing so, 
   * and sets components of the existsOrganisms array to true if an organism of a certain type exists in the entire map, 
   * false otherwise. After all movement is complete, new grass spawn, the number of which is proportional to the 
   * map size.  
   */
  public void movementCycle() {
    for (int i = 0; i < 3; i++) {
      existOrganisms[i] = false;
    }
    existOrganisms[0] = true;
    existOrganisms[1] = true;
    for (int i = 0; i < lenY; i++) {
      for (int j = 0; j < lenX; j++) {
        if (map[i][j] != null && map[i][j] instanceof Animal) {
          if (map[i][j] instanceof Wolf) {
            existOrganisms[0] = true;
          } else if (map[i][j] instanceof Sheep) {
            existOrganisms[1] = true;
          }
          
          if (!((Animal) map[i][j]).hasMoved()) {
            map[i][j].addTurns();
            
            if (map[i][j].getHealth() <= 0) {
              map[i][j] = null;
            } else {
              moveAnimal(i, j, null);
            }
          }
        } else if ((map[i][j] != null) && (map[i][j] instanceof Grass)) {
          map[i][j].addTurns();
          existOrganisms[2] = true;
        }
      }
    }
    
    creator.organismTypeCreation('g', GRASS_PER_TURN);
    
    reset(); // for the next turn, all animals will indicate that they haven't moved 
  }
  
  /*
   * moveAnimal
   * This method combines several methods contained in the Animal, Wolf and Sheep classes to allow an organism to move 
   * while only having access to a portion of the entire map. If the animal we are moving collides with an animal, 
   * the BreedingOnCollision method is called and a new animal may be created if certain conditions are met. If the
   * animal that has been collided with has not already moved, the forceMovement method is called that forces that animal
   * to react, be it by remaining stationary or moving to a new spot
   * @param i and j, integers storing the y and x coordinates respectively of the organism being moved 
   * @param notMovePosition, an integer array storing the coordinates of a point that will be avoided upon movement
   */
  private void moveAnimal(int i, int j, int[] notMovePosition) {
    int[] smallOldYX = new int[2];
    smallOldYX[0] = i;
    smallOldYX[1] = j;
    
    // transition to the restricted portion of the map
    int[] bounds = createBoundaries(i, j);
    smallOldYX = scaleCoordinates(smallOldYX, bounds[0], bounds[2]);
    Organism[][] mapPortion = getRestrictedMap(bounds); 
    
    int[] newPosition = ((Animal) mapPortion[smallOldYX[0]][smallOldYX[1]]).findNextPosition(smallOldYX[0], smallOldYX[1],
                                                                                             mapPortion, notMovePosition);
    //int[] newPosition = ((Animal) mapPortion[smallOldYX[0]][smallOldYX[1]]).getNewPosition();
    
    /*
     * breeding may occur even if one of the animals hasn't moved. Thus while the animals "move at once", breeding
     * occurs slightly more often than may be expected, reflecting animals' willingness to reproduce in real life
     */
    breedingOnCollision(mapPortion, smallOldYX, newPosition);
    
    if (((newPosition[0] != smallOldYX[0]) || (newPosition[1] != smallOldYX[1])) 
          && (mapPortion[newPosition[0]][newPosition[1]] != null) 
          && (mapPortion[newPosition[0]][newPosition[1]] instanceof Animal)
          && !((Animal) mapPortion[newPosition[0]][newPosition[1]]).hasMoved()) {
      
      // reverse the scaling down to since the old portion of the map no longer applies
      bounds[0] = -bounds[0];
      bounds[2] = -bounds[2];
      newPosition = scaleCoordinates(newPosition, bounds[0], bounds[2]);
      
      forceMovement(i, j, newPosition);
    } else if ((newPosition[0] != smallOldYX[0]) || (newPosition[1] != smallOldYX[1])) {
      mapPortion = ((Animal) mapPortion[smallOldYX[0]][smallOldYX[1]]).movement(smallOldYX[0], smallOldYX[1], newPosition, mapPortion); 
      addToMap(bounds, mapPortion);
    } else { // if the next position is no different than the previous
      ((Animal) map[i][j]).setHasMoved(true);
    }
  }
  
  /*
   * scaleCoordinates
   * This method converts the coordinates of a point on one 2D array to the coordinates of a point on another 2D array
   * @param posYX, an integer array holding the coordinates of the point that will be scaled
   * @param newOriginY and newOriginX, integers that represent the vertical and horizontal coordinates of the origin
   * of the smaller of the two 2D arrays relative to the other 2D array
   * @return posXY, the scaled coordinate
   */
  private int[] scaleCoordinates(int[] posYX, int newOriginY, int newOriginX) {
    posYX[0] -= newOriginY;
    posYX[1] -= newOriginX;
    
    return posYX;
  }
  
  /*
   * breedingOnCollision
   * This method calls creator's (an object of type OrganismCreator) method that creates a new organism and places 
   * it on the grid of the same type as the collided organisms if either both collided animals are wolves or sheep
   * and they are of opposite sexes and they are both able to breed as defined by the canBreed method. 
   * If these conditions are met, the health of each "parent" is decreased. 
   * @param mapPortion, a 2D Organism array representing the portion of the map the organism initiating the collision can see
   * @param movingYX, an integer array containing the coordinates one of the collided organisms on the mapPortion array
   * @param collidedYX, same as movingYX except for the other organism
   */
  private void breedingOnCollision(Organism[][] mapPortion, int[] movingYX, int[] collidedYX) {
    if (((collidedYX[0] != movingYX[0]) || (collidedYX[1] != movingYX[1])) // if the new position is different from the previous one
          && (mapPortion[collidedYX[0]][collidedYX[1]] != null) 
          && (mapPortion[collidedYX[0]][collidedYX[1]] instanceof Animal)) {
      Animal animalMoving = (Animal) mapPortion[movingYX[0]][movingYX[1]]; // for clarity and conciseness
      Animal animalCollidedWith = (Animal) mapPortion[collidedYX[0]][collidedYX[1]]; // for clarity and consiseness
      
      if ((animalMoving.getIsFemale() != animalCollidedWith.getIsFemale()) && (animalMoving.canBreed()) 
            && (animalCollidedWith.canBreed()) && (((animalMoving instanceof Wolf) && (animalCollidedWith instanceof Wolf))
              || ((animalMoving instanceof Sheep) && (animalCollidedWith instanceof Sheep)))) {
        
        ((Animal) mapPortion[collidedYX[0]][collidedYX[1]]).decreaseHealthOnBreeding();
        ((Animal) mapPortion[movingYX[0]][movingYX[1]]).decreaseHealthOnBreeding();
        
        if ((animalMoving instanceof Wolf) && (animalCollidedWith instanceof Wolf)) {
          creator.organismTypeCreation('w', 1);
        } else {
          creator.organismTypeCreation('s', 1);
        }
      }
    }
  }
  
  /*
   * forceMovement
   * This method recursively calls the moveAnimal method, moving the animal that is collided with, as well
   * as the animal initiating the collision if the former remains in the same position. If not, the animal
   * initiating the collision moves to the vacated position via the simpleMovement method defined in the Animal class
   * @param oldYPos and oldXPos, integers storing the coordinates of the animal initiating the collision on the large map
   * @param coordinatesCollided, an integer array storing the coordinates of the animal that is being collided with
   */
  private void forceMovement(int oldYPos, int oldXPos, int[] coordinatesCollided) { 
    int[] notMovePos = new int[2];
    notMovePos[0] = oldYPos;
    notMovePos[1] = oldXPos;
    
    ((Animal) map[coordinatesCollided[0]][coordinatesCollided[1]]).setHasMoved(true); // prevents recursion/stack overflow
    
    moveAnimal(coordinatesCollided[0], coordinatesCollided[1], notMovePos);
    
    if (map[coordinatesCollided[0]][coordinatesCollided[1]] == null) {
      ((Animal) map[oldYPos][oldXPos]).simpleMovement(map, coordinatesCollided, oldYPos, oldXPos); // better to scale back down?
    } else {
      /*
       * argument coordinates collided will ensure that the collided animal will not try to move back to the position
       * the animal initiating the collision previously took up
       */
      moveAnimal(oldYPos, oldXPos, coordinatesCollided);
    }
  }
  
  /*
   * getRestrictedMap
   * This method creates and returns a 2D array that is the exact copy of a rectangular (in out case, square) portion
   * of the map 2D array limited by the parameter, bounds
   * @param bounds, an integer 2D array holding the value of: the minimum y value, the maximum y value, 
   * the x minimum value, and the x maximum value, in that order, of a portion of the map 2D array
   * @return mapPortion, an integer 2D array that is a copy of a portion of the map 2D array
   */
  private Organism[][] getRestrictedMap(int[] bounds) {
    Organism[][] mapPortion = new Organism[bounds[1] - bounds[0] + 1][bounds[3] - bounds[2] + 1];
    for (int i = bounds[0]; i <= bounds[1]; i++) {
      for (int j = bounds[2]; j <= bounds[3]; j++) {
        // scales down the coordinates similarly to the scaleCoordinates method
        mapPortion[i - bounds[0]][j - bounds[2]] = map[i][j]; 
      }
    }
    return mapPortion;
  }
  
  /*
   * addToMap
   * This method copies a 2D array of Organisms to the map
   * @param bounds, an integer 2D array holding the value of: the minimum y value, the maximum y value, 
   * the x minimum value, and the x maximum value, in that order, of a portion of the map 2D array
   * @param mapPortion, an integer 2D array that is a copy of a portion of the map 2D array
   */
  private void addToMap(int[] bounds, Organism[][] mapPortion) {
    for (int i = bounds[0]; i <= bounds[1]; i++) {
      for (int j = bounds[2]; j <= bounds[3]; j++) {
        map[i][j] = mapPortion[i - bounds[0]][j - bounds[2]];
      }
    }
  }
  
  /*
   * reset
   * This method sets all existing Animal's moved boolean field to false if it is true via the hasMoved method
   * in the Animal class
   */
  private void reset() {
    for (int i = 0; i < lenY; i++) {
      for (int j = 0; j < lenX; j++) {
        if (map[i][j] instanceof Animal && ((Animal) map[i][j]).hasMoved()) {
          ((Animal) map[i][j]).setHasMoved(false);
        }
      }
    }
  }
}
  