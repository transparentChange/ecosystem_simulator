/*
 * OrganismCreator
 * Provides a way of creating organisms on the map. If they are animals, they randomly spawn, if they are grass, they
 * spawn near positions that other current or previous grass has occupied. The health of each organism is by default
 * the arguments passed in upon the creation of a OrganismCreator object, however the health of wolves and sheep
 * may change according to the number of spaces not occupied by an animal, an attempt to balance the animal populations
 * and provide a more realistic simulation. For example, if too many of an organism exist in a certain area, 
 * factors such as increased disease rate or lack of resources lead to worse survival chances. 
 */
class OrganismCreator extends MapManager {
  
  private int grassHealth, sheepHealth, wolfHealth;
  private boolean initialGrassCreated;
  
  /*
   * Spawn places are only added. If a grass is eaten, it is assumed that grass is still more likely to spawn at locations
   * surrounding that spot since it it is assumed that the roots remain in place and may still give rise to new grass
   */
  private boolean[][] grassSpawnPlaces; 
  
  OrganismCreator(int ySize, int xSize, int grassValue, int healthSheep, int healthWolves) {
    super(ySize, xSize);
    
    this.grassHealth = grassValue;
    this.sheepHealth = healthSheep;
    this.wolfHealth = healthWolves;
    
    this.grassSpawnPlaces = new boolean[lenY][lenX];
    this.initialGrassCreated = false; // if some grass was already created, the grass should spawn near other grass
  }
  
  /*
   * organismTypeCreation
   * This method uses other private methods declared in the class to create a certain number of organisms of a given
   * type on the map. Also sets the initialGrassCreated flag to true if it is the first time grass is being created. 
   * @param organismType, a char that acts as an identifier of which type of organism is going to be created. Meant
   * to be either 'g' for grass, 's' for sheep, or 'w' for wolf
   * @param numOrganisms, an integer holding the number of organisms that are meant to be created of the type 
   * represented by organismType
   */
  public void organismTypeCreation(char organismType, int numOrganisms) {
    if (organismType == 'g' || organismType == 's' || organismType == 'w') { // checks that the proper arguments are used
      
      int[] numEmpty = setNumEmpty(organismType); 
      System.out.println(numEmpty[0]); 
      int[] coordinatesYX = new int[2];
      for (int i = 0; i < numOrganisms; i++) {
        coordinatesYX = createPosition(organismType, numEmpty[0]);
        
        if (coordinatesYX != null) { // if some spots are available on the map
          map = createOrganism(organismType, coordinatesYX, numEmpty[1]);
          numEmpty[0]--;
        }
      }
      
      if (organismType == 'g' && !this.initialGrassCreated) {
        this.initialGrassCreated = true;
      }
    } else {
      System.out.println("An invalid organism type has been passed into method organismTypeCreation");
    }
  }
  
  /*
   * setNumEmpty
   * This method finds the number of possible spawning locations depending on the type of organism being created. Also
   * determines the number of either empty spaces or spaces occupied by grass. The results are returned.
   * @param organismType, a char that acts as an identifier of which type of organism is going to be created. Meant
   * to be either 'g' for grass, 's' for sheep, or 'w' for wolf
   * @return numEmpty, the first element of which is the number of possible spawning locations that is either
   * the number of empty spots or the number of true booleans values in the grassSpawnPlaces 2D boolean array, and the
   * second element of which holds the number of spots that are either empty or occupied by grass in the map
   */
  private int[] setNumEmpty(char organismType) {
    int[] numEmpty = new int[2];
    numEmpty[0] = 0;
    numEmpty[1] = 0;
    
    for (int i = 0; i < lenY; i++) {
      for (int j = 0; j < lenX; j++) {
        if ((map[i][j] == null) && (organismType != 'g' || !this.initialGrassCreated || grassSpawnPlaces[i][j])) {
          numEmpty[0]++;
        }
        if (map[i][j] == null || map[i][j] instanceof Grass) {
          numEmpty[1]++;
        }
      }
    }
    
    return numEmpty;
  }
  
  /*
   * createPosition
   * This method chooses a random position limited to the empty spots available on the map that can serve as 
   * the initial position for the new organism being created
   * @param organismType, a char that acts as an identifier of which type of organism is going to be created. Meant
   * to be either 'g' for grass, 's' for sheep, or 'w' for wolf
   * @param numSpotsAvailable, the number of spots available whose coordinates can be stored in the coordinates variable
   * @return coordinates, an integer array containing two elements: the y and x coordinates of the new 
   * random position on the map
   */
  private int[] createPosition(char organismType, int numSpotsAvailable) {
    int count = 0;
    
    // random number will be at least one less than the number of empty spots, hence one is added
    int randNum = (int) (Math.random() * numSpotsAvailable) + 1;
    
    int xPos = 0;
    int yPos = 0;
    int[] coordinates = null;
     
    while ((count != randNum) && (yPos < lenY)) {
      xPos = 0;
      while ((count != randNum) && (xPos < lenX)) {
        if ((map[yPos][xPos] == null) && (organismType != 'g' || !this.initialGrassCreated || grassSpawnPlaces[yPos][xPos])) {
          count++;
        }
        xPos++;
      }
      yPos++;
    }
    
    if (count == 0 && coordinates == null) { // avoid negative values if no spots available
      return coordinates; 
    } else if (randNum != 0) {
      coordinates = new int[2];
      coordinates[0] = yPos - 1;
      coordinates[1] = xPos - 1;
    } 
    
    return coordinates;
  }
  
  /*
   * createOrganism
   * This method instantiates an object of type Grass, Sheep or Wolf, storing it in a location defined by the parameter
   * coordinatesYX on the map. Necessary health adjustments and random assignment of sex for animals is used.
   * @param organismType, a char that acts as an identifier of which type of organism is going to be created. Meant
   * to be either 'g' for grass, 's' for sheep, or 'w' for wolf
   * @param coordinatesYX, an integer array containing the y and x coordinates, in that order, of the position on the 
   * map where the new organism will be stored in 
   * @param numGrassOrEmpty, the number of spots on the 2D Organism map that are empty or are occupied by grass
   * @return map, the updated map containing all the organisms, including the one that was created within this method
   */
  private Organism[][] createOrganism(char organismType, int[] coordinatesYX, int numGrassOrEmpty) {
    if (organismType == 'g') {
      map[coordinatesYX[0]][coordinatesYX[1]] = new Grass(grassHealth);
      addSpawnLocations(coordinatesYX);
    } else if ((organismType == 's') || (organismType == 'w')) {
      int newSheepHealth = sheepHealth;
      int newWolfHealth = wolfHealth;
      if ((numGrassOrEmpty < lenX * lenY * 0.89) && (organismType == 's')) { // a lot of animals, sheep health decreases
        newSheepHealth = (int) (sheepHealth - 0.3 * (Math.max(lenX * lenY * 0.89 - numGrassOrEmpty, 0)));
      } else if (numGrassOrEmpty > lenX * lenY * 0.955) { // not a lot of animals, wolf health increases
        newWolfHealth = (int) (wolfHealth + 0.15 * (Math.min(numGrassOrEmpty - lenX * lenY * 0.955, 67)));
      } 
      int rand = (int) (Math.random() * 2); // determines sex of animal
      if (organismType == 's') {
        if (rand == 0) {
          map[coordinatesYX[0]][coordinatesYX[1]] = new Sheep(newSheepHealth, true); 
        } else {
          map[coordinatesYX[0]][coordinatesYX[1]] = new Sheep(newSheepHealth, false);
        }
      } else {
        if (rand == 0) {
          map[coordinatesYX[0]][coordinatesYX[1]] = new Wolf(newWolfHealth, true);
        } else {
          map[coordinatesYX[0]][coordinatesYX[1]] = new Wolf(newWolfHealth, false);
        }
      }
    }
    
    return map;
  }
  
  /*
   * addSpawnLocations
   * This method sets the 2D grassSpawnPlaces boolean array to true around a coordinate passed in as a parameter if 
   * it is not already so. The area changed is bounded by the bounds integer array created by the createBoundaries method
   * @param coordinatesYX, an integer array containing the y and x coordinates, in that order, of a point on the map
   */
  private void addSpawnLocations(int[] coordinatesYX) {
    int[] bounds = createBoundaries(coordinatesYX[0], coordinatesYX[1]);
    for (int i = bounds[0]; i <= bounds[1]; i++) {
      for (int j = bounds[2]; j <= bounds[3]; j++) {
        if (!grassSpawnPlaces[i][j]) {
          grassSpawnPlaces[i][j] = true;
        }
      }
    }
  }
}