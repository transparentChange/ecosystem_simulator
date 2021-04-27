import java.util.Scanner;

/*
 * [UserInterface.java]
 * Simulates an ecosystem containing sheep, wolves and grass based on initial conditions provided by the user.
 * Wolves eat sheep, sheep eat grass. Wolves move randomly, as do sheep unless they are directly beside a grass
 * or a wolf and they are not on the border of the map. New organisms appear when two animals of the same gender collide
 * or, for grass, when a cycle passes
 * @author Matthew Sekirin
 * May 11, 2019
 */

/* Extra features
 
 Graphics: In DisplayEcosim class
  - a graphical "darkening" effect on the selected organism so that it clearly seen (depending on screen size, 
    visibility may decrease after about 200x200 grid size)
  - display of an image corresponding to the type of selected organism (wolf, sheep, or grass)
  - display of selected organism statistics: health, turns survived, and coordinates
  - display of general information: turns passed and current organism count
  
 Movement
  - everything moves at once
     => OrganismManager class
 
 Animals
  - an animal is only given access to a small portion of the map directly around it.
    => MapManager, OrganismManager classes
  - animals of different sexes exist, such that only males and females colliding may result in a new animal being created
    and only wolves of the same gender fight each other
    => Animal, OrganismCreator classes, for example
  - sheep may run away from wolves and towards grass
    => details in Sheep class
 
 Creation of more organisms: in OrganismCreator class 
  - Grass spawns near where other grass exists or previously existed
    => OrganismCreator class
  - The health of new organisms may be influenced by the number of empty spaces or number of spaces occupied by grass.
    Reasoning explained directly above the OrganismCreator class
 
 Entirety of Inspector class 
  
  Notes:
   - the DisplayEcosim class is not properly designed in the author's opinion, which is a result of a lack of 
     knowledge in Java graphics syntax and design principles at the time of creation
   - some points that led to the decision to have everything move at once: 
      - There is minimal priority given to certain organisms based on their type or based on their position in the array,
        meaning consistency is improved and reality is reflected more accurately than if it had been otherwise. We are
        not so bold as to claim that everything perfectly moves at once due to the special relationship between sheep
        and wolves that makes the events that occur dependent on the move order. This is fine, however. *
      - the scale of the simulation is meant to be large, so if an animal moves it takes a while to do so and means
      that other animals have also moved in that time period, unless they remain in the same position
      - it is more difficult than the traditionnal approach
   - the factors use to adjust health when deemed suitable for wolves and sheep (see "Creation of more organisms" above)
     are chosen by trial and error to create a better balance and have nothing to do with either 
     the mathematical modelling of predator-prey relationships or real-life expirimental data
     
     * Although it may appear that if sheep move later than wolves sheep have the advantage, because they would be better able to 
     assess the situation, but this is compensated by the fact that sheep could have been eaten by wolves 
     that turn before it had a chance to react. The important part is that animals do not hinder the movement of other
     animals of the same type before one of the animals already had a chance to move that turn
*/
  

/*
 * UserInterface
 * This class get user input on the specifications of the ecosystem and contains the loop each iteration of which
 * represents one "turn". The user input is somewhat validated.
 */
public class UserInterface {
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    
    int[] numOrganismsInitial = new int[3];
    int xSize, ySize;
    int mapArea;
    int grassValue;
    int healthSheep, healthWolves;
    
    System.out.println("Welcome to the Ecosystem Simulation! Change parameters about grass, sheep and wolves and see " +
                         "what happens."); // welcoming message 
    
    // User input
    String viewDefault = null;
    do {
      if (viewDefault != null) {
        System.out.print("Please enter valid input. ");
      }
      System.out.println("Would you like to view the simulation with example parameters? (y/n): ");

      viewDefault = input.nextLine();
      
    } while ((!viewDefault.equals("y")) && (!viewDefault.equals("Y")) && (!viewDefault.equals("n")) && (!viewDefault.equals("N")));
    
    if ((viewDefault.equals("y")) || (viewDefault.equals("Y"))) {
      xSize = 15;
      ySize = 15;
      numOrganismsInitial[0] = 10;
      numOrganismsInitial[1] = 10; 
      numOrganismsInitial[2] = 0;
      /*
      xSize = 100;
      ySize = 100;
      numOrganismsInitial[0] = 300; // 3000
      
      //numOrganismsInitial[1] = 300; 
      //numOrganismsInitial[2] = 135;
      
      numOrganismsInitial[1] = 1000; 
      numOrganismsInitial[2] = 0;
      */
      grassValue = 10;
      healthSheep = 70;
      healthWolves = 70;
      
      System.out.println("Default initial parameters:\nMap width: " + xSize + "\nMap height: " + ySize 
                           + "\nNumber of grass: " + numOrganismsInitial[0] 
                           + "\nNumber of sheep: " + numOrganismsInitial[1]
                           + "\nNumber of wolves: " + numOrganismsInitial[2]
                           + "\nNutrional value of grass " + grassValue 
                           + "\nHealth of sheep " + healthSheep
                           + "\nHealth of wolves " + healthWolves + "\n");
    } else {
      System.out.println("Enter the desired width of the map: ");
      xSize = input.nextInt();
      
      System.out.println("Enter the desired height of the map: ");
      ySize = input.nextInt();
      
      mapArea = xSize * ySize;
      if (mapArea >= 9E4) {
        System.out.println("Careful, the map may not show due to the large dimensions.\n");
      }
      
      int totalNumOrganisms = 0;
      do {
        System.out.println("Enter the number of grass you would like to start with: ");
        numOrganismsInitial[0] = input.nextInt();
        
        System.out.println("Enter the number of sheep you would like to start with: ");
        numOrganismsInitial[1] = input.nextInt();
        
        System.out.println("Enter the number of wolves you would like to start with: ");
        numOrganismsInitial[2] = input.nextInt();
        
        for (int i = 0; i < 3; i++) {
          totalNumOrganisms += numOrganismsInitial[i];
        }
        
        if ((numOrganismsInitial[0] < 0) || (numOrganismsInitial[1] < 0) || (numOrganismsInitial[2] < 0)) {
          System.out.println("The number of any organism on the map must be positive. Please try again.");
        } else if (totalNumOrganisms > mapArea) {
          System.out.println("Too many organisms have been entered for the map size specified.");
        }
          
      } while ((numOrganismsInitial[0] < 0) || (numOrganismsInitial[1] < 0) || (numOrganismsInitial[2] < 0)
               || (totalNumOrganisms > mapArea));
      
               
      do {         
        System.out.println("Enter the nutritional value of grass: ");
        grassValue = input.nextInt();
        
        System.out.println("Enter the health of sheep: ");
        healthSheep = input.nextInt();
        
        System.out.println("Enter the health of wolves: ");
        healthWolves = input.nextInt();
        
        if ((numOrganismsInitial[0] < 0) || (numOrganismsInitial[1] < 0) || (numOrganismsInitial[2] < 0)) {
          System.out.println("The health of any organism on the map must be positive. Please try again.");
        }
      } while ((grassValue < 0) || (healthSheep < 0) || (healthWolves < 0)); 
    }
    
    // initialize the object environment which manages the overall program, and the display object, display 
    OrganismManager environment = new OrganismManager(ySize, xSize, grassValue, healthSheep, healthWolves, numOrganismsInitial);
    DisplayEcosim display = new DisplayEcosim(environment.getMap());
    StatsWriter populationWriter = new StatsWriter();
    
    // the simulation itself
    display.setCloseOperation(populationWriter);
    while (environment.continueSimulation()) {
      display.refresh();
      
      populationWriter.writeOut(environment.getMap());
      environment.movementCycle();
      
      // small delay
      try { 
        Thread.sleep(400); // 30, 100
      } catch(Exception e) {
        e.printStackTrace();
      };
    }
    
    System.out.println("\nOne of the organisms has ceased to exist. Goodbye.");
    
    display.exit();
    input.close();
  }
}