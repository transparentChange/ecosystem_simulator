import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

/* [DisplayEcosim.java]
 * Displays the grid and the organisms on it in colours that are associated with each respective organism.
 * Also displays simulation statistics and a flashing organism with some information accompanying it that can be
 * controlled using arrow keys
 * @author Mangat
 * @author Matthew Sekirin
 */

/*
 * DisplayEcosim
 * Dislays the entire simulation in a jframe, the main part of which is the grid that updates as the organisms move,
 * get eaten, and spawn, for example. The window is often refreshed in order to produce an fading effect of the selected
 * organism, and a large image of the type of selected organism so that the user clearly understands what is hapenning
 * is shown to the right of the grid
 */
class DisplayEcosim { 

  private JFrame frame;
  private double maxX, maxY;
  private int GridToScreenRatio;
  private Organism[][] world;
  private int numCyclesPassed;
  private boolean resetSelected;
  
  DisplayEcosim(Organism[][] w) { 
    this.world = w;
    numCyclesPassed = 0;
    
    resetSelected = true;
    
    maxX = Toolkit.getDefaultToolkit().getScreenSize().width;
    maxY = Toolkit.getDefaultToolkit().getScreenSize().height - 10;
    GridToScreenRatio = (int) ((maxY - 53) / (world.length + 1));  // ratio to fit in screen as square map
    
    System.out.println("Map size: " + world.length + " by " + world[0].length + "\nScreen size: " + maxX + "x" + maxY +
                       " Ratio: " + GridToScreenRatio);
    
    this.frame = new JFrame("Map of World");
    
    EncompassingPanel worldPanel = new EncompassingPanel();
    
    frame.addKeyListener(worldPanel);
    frame.getContentPane().add(BorderLayout.CENTER, worldPanel);
    //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
    frame.setVisible(true);
  }
  
  public void setCloseOperation(StatsWriter writer) {
	 frame.addWindowListener(new java.awt.event.WindowAdapter() {
		 public void windowClosing(java.awt.event.WindowEvent e) {
			writer.exit();
			System.exit(0);
		 }
	 });
  }
  
  
  /*
   * exit
   * This method closes the frame. Meant to be used when one of organism types ceases to exist on the grid
   */
  public void exit() {
    this.frame.dispose();
  }
  
  
  /*
   * refresh
   * This method contains the repaint method, updating the frame, and also increments the number of turns
   * that have passed since the beginning of the simulation
   */
  public void refresh() {
    numCyclesPassed++;
    resetSelected = true;
    frame.repaint();
  }
  
  /*
   * EncompassingPanel
   * Updates the grid, image, and statistics, allowing for user control of the selected organism of interest
   */
  class EncompassingPanel extends JPanel implements KeyListener {
    private Inspector findOrganism;
    private int[] selectedYX;
    
    // colour specifications for the selected organism
    private int[] rgbSelected = new int[3];
    private int[] minRgb = new int[3];
    private final int MAX_RGB_DIFFERENCE = 50;
    
    private boolean someOrganismSelected; // used to find a new organism if existing one has ceased to exist
    
    // images for the selected organism and their display specifics
    private BufferedImage sheepImage, wolfImage, grassImage;
    private int newImageWidth, newImageHeight;
    private int animalImgX;
    private int grassImgX;
    
    private int populationCount;
    
    // position markers for images and statistics
    private final int POPULATION_STATS_X = (int) maxX - 200;
    private final int POPULATION_STATS_MINY = 100;
    private final int IMAGE_Y = 50;
    private int selectedStatsMinY;
    private int selectedStatsX;
    private final int TEXT_DELTA_Y = 30;
    
    EncompassingPanel() { 
      try {
        sheepImage = ImageIO.read(new File("sheepImg.png"));
        wolfImage = ImageIO.read(new File("wolfImg.png"));
        grassImage = ImageIO.read(new File("grassImg.png"));
      } catch (Exception e) {
        e.printStackTrace();
      }
      newImageWidth = (int) (sheepImage.getWidth() * 0.5);
      newImageHeight = (int) (sheepImage.getHeight() * 0.5);
      
      animalImgX = (int) (world[0].length * GridToScreenRatio * 1.05);
      grassImgX = animalImgX * (22 / 21);
      selectedStatsMinY = IMAGE_Y + newImageHeight + TEXT_DELTA_Y;
      selectedStatsX = (int) (animalImgX * 1.05);
      
      populationCount = 0;
      findOrganism = new Inspector(world);
      selectedYX = findOrganism.getCoordinates();
      
      new Timer(15, paintTimer).start();
    } 
    
    /*
     * actionPerformed
     * This method decreases all rgb values of the selected organism by a the same factor
     * if they are greater than the pre-determined minimum values
     * @param e, an ActionEvent object
     */
    Action paintTimer = new AbstractAction() {
      public void actionPerformed(java.awt.event.ActionEvent e) {  
        for (int i = 0; i < 3; i++) {
          if (rgbSelected[i] > minRgb[i]) {
            rgbSelected[i] *= 0.98;
          }
        }
        repaint();
      }
    };
    
    /*
     * paintComponent
     * This method calls the methods that draw the grid, images, and statistics, making sure the correct colours
     * and selected organism are chosen. It also finds the total population count.
     * @param g, a Graphics object
     */
    public void paintComponent(Graphics g) {   
      super.paintComponent(g);
      setDoubleBuffered(true); 
      
      drawSelectedStats(g);
      drawPopulationStats(g);
      
      someOrganismSelected = false;
      populationCount = 0;
      
      for (int i = 0; i < world.length; i++) { 
        for (int j = 0; j < world[0].length; j++) { 
          
          Color colorBlock = chooseColor(i, j);
          
          if ((world[i][j] != null) && world[i][j].getIsSelected()) {
            someOrganismSelected = true;
            findOrganism.setCoordinates(i, j);
            
            drawImage(g);
            
            // will execute once every turn or when there is a switch in selected organisms
            if (resetSelected) {
              resetSelected = false;
              
              rgbSelected = setFirstSelectedRGB(colorBlock);
              for (int h = 0; h < 3; h++) {
                minRgb[h] = rgbSelected[h] - MAX_RGB_DIFFERENCE;
              }
            }
          } else {
            drawBlock(colorBlock, g, i, j);
          }
          
          if (world[i][j] != null) {
            populationCount += 1;
          }
        }
      }  
      
      if (!someOrganismSelected) {
        selectedYX = findOrganism.getFirstCoordinates();
      }
      
      if (selectedYX != null) { // avoids null pointer exception, for example, when there are no organisms on the map
        drawBlock(new Color(rgbSelected[0], rgbSelected[1], rgbSelected[2]), g, selectedYX[0], selectedYX[1]); // selected block
      }
      
    }
    
    
    /*
     * drawPopulationStats
     * This method draws the number of turns passed and the population count on a specified position in a specific font
     * @param g, a Graphics object
     */
    private void drawPopulationStats(Graphics g) {
      g.setFont(new Font("Helvetica", Font.PLAIN, 23));
      g.drawString("Turns: " + numCyclesPassed, POPULATION_STATS_X, POPULATION_STATS_MINY);
      g.drawString("Population: " + populationCount, POPULATION_STATS_X, POPULATION_STATS_MINY + TEXT_DELTA_Y);
    }
    
    /*
     * drawSelectedStats
     * This method draws the health, turns survived, and coordinates of the selected Organism on a
     * specified position in a specific font
     * @param g, a Graphics object
     */
    private void drawSelectedStats(Graphics g) {
      selectedYX = findOrganism.getCoordinates();
      if (selectedYX != null && world[selectedYX[0]][selectedYX[1]] != null) { // attempt to avoid any possible null pointer exceptions
        g.setFont(new Font("Helvetica", Font.PLAIN, 20));
        try {
        g.drawString("Health: " + world[selectedYX[0]][selectedYX[1]].getHealth(), selectedStatsX, selectedStatsMinY);
        g.drawString("Turns survived: " + world[selectedYX[0]][selectedYX[1]].getTurnsSurvived(), selectedStatsX, 
                     selectedStatsMinY + TEXT_DELTA_Y);
        } catch(Exception e) {
          ; // in case a null pointer exception does appear
        }
        
        g.drawString("(x, y) is " + selectedYX[1] + " " + selectedYX[0], selectedStatsX, 
                     selectedStatsMinY + 2 * TEXT_DELTA_Y);
      }
    }
    
    /*
     * drawBlock
     * This method draws a block at a given position and of a given colour, and draws an outline of the block around it
     * on the grid.
     * @param c, a Color holding the colour of the block that will be drawn
     * @param g, a Graphics object
     * @param yMapPos and xMapPos, integers that hold the position of the organism in the world 2D array being drawn
     */
    private void drawBlock(Color c, Graphics g, int yMapPos, int xMapPos) {
      g.setColor(c);
      g.fillRect(xMapPos * GridToScreenRatio + 10, yMapPos * GridToScreenRatio + 10, GridToScreenRatio, GridToScreenRatio);
      g.setColor(new Color(51, 25, 0));
      g.drawRect(xMapPos * GridToScreenRatio + 10, yMapPos * GridToScreenRatio + 10, GridToScreenRatio, GridToScreenRatio);
    }
    
    /*
     * chooseColor
     * This method chooses a colour corresponding to the organism whose x and y coordinates are passed in as parameters
     * and returns it
     * @param yPos and xPos, integers that hold the position of the organism in question in the 2D world array
     * @return c, the colour chosen
     */
    private Color chooseColor(int yPos, int xPos) {
      Color c;
      if (world[yPos][xPos] instanceof Grass) {
        c = new Color(96, 128, 56);
      } else if (world[yPos][xPos] instanceof Sheep) {
        c = new Color(192, 192, 192);
      } else if (world[yPos][xPos] instanceof Wolf) {
        c = new Color(50, 50, 50);
      } else {
        c = new Color(88, 66, 37);
      }
      
      return c;
    }
    
    /*
     * setFirstSelectedRGB
     * This method copies the rgb of the colour passed in as a parameter to the selected organism rgb array, and 
     * returns this array to make this side effect clear
     * @param c, the Color being copied
     * @return rgbSelected, an array that takes on the same values as the c's rgb
     */
    private int[] setFirstSelectedRGB(Color c) {
      rgbSelected[0] = c.getRed();
      rgbSelected[1] = c.getGreen();
      rgbSelected[2] = c.getBlue();
      
      return rgbSelected;
    }
    
    /*
     * drawImage
     * This method draws a sheep, wolf or grass image at a given location based on the type of the organism at 
     * the position specified by the selectedYX array on the 2D world array
     * @param g, a Graphics object
     */
    public void drawImage(Graphics g) {
      if (world[selectedYX[0]][selectedYX[1]] instanceof Sheep) {
        g.drawImage(sheepImage, animalImgX, IMAGE_Y, newImageWidth, newImageHeight, this);
      } else if (world[selectedYX[0]][selectedYX[1]] instanceof Wolf) {
        g.drawImage(wolfImage, animalImgX, IMAGE_Y, newImageWidth, newImageHeight, this);
      } else if (world[selectedYX[0]][selectedYX[1]] instanceof Grass) {
        g.drawImage(grassImage, grassImgX, IMAGE_Y, newImageWidth, newImageHeight, this);
      } 
    }
    
    /*
     * keyReleased
     * This method calls the appropriate method used to change findOrganism's coordinates according to which 
     * arrow key is pressed and indicates this change by passing in true to the new organism's setSelected method
     * and false to the previously selected organism's setSelected method
     * @param e, a KeyEvent object
     */
    @Override
    public void keyReleased(KeyEvent e) { 
      if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_DOWN 
            || e.getKeyCode() == KeyEvent.VK_LEFT && findOrganism.getCoordinates() != null) { 
        world[selectedYX[0]][selectedYX[1]].setSelected(false);
        resetSelected = true; // makes sure the previous colour is not shown after the selected organism is changed
        
        if (e.getKeyCode() == KeyEvent.VK_UP) {
          findOrganism.getNextCoordinatesVertical('u');
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
          findOrganism.getNextCoordinatesHorizontal('r'); // store as boolean       
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
          findOrganism.getNextCoordinatesVertical('d');
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
          findOrganism.getNextCoordinatesHorizontal('l');
        }
        
        selectedYX = findOrganism.getCoordinates();
        world[selectedYX[0]][selectedYX[1]].setSelected(true);
      }
    }
    
    // the following methods are included to satisfy the KeyListener interface 
    @Override
    public void keyTyped(KeyEvent e) {
    }      
    
    @Override
    public void keyPressed(KeyEvent e) {  
    }
  } // end of EncompassingPanel
} // end of DisplayEcosim