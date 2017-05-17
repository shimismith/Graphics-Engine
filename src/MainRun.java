import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
/** The class runs all the functions of the game.
 * It renders the world and draws graphics on the screen.
 * It also controls all the networking features of the game.
 * @author shimismith
 *@since March 28, 2018
 */
public class MainRun extends JPanel{

private static JFrame frame;

/**width of screen.*/
  public static final int width = 1080;
  /**height of screen.*/
  public static final int height = 720;
  /**all the pixels that are being drawn.*/
  public static int pixels[];
  /**BufferedImage for the screen.*/
  static BufferedImage image;
  private static Render ren;
  
  private static Point[][] mesh;

  /** Loads all the images necessary for the game, sets up the networking, builds the level, opens a window and starts the game loop. 
   * @param args
   */
 public static void main(String[] args) {
	 
  image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);  //creates a buffered image for the screen


  ren = new Render();

  
 //LOAD THE MESH
  try {
		mesh = ObjParser.readFile("teapot.obj");
		System.out.println("FILE LOAD SUCCESFULL");
	} catch (Exception e) {
		  System.out.println("FILE LOAD FAILED");
		e.printStackTrace();
	}

  frame = new JFrame("Window");
  frame.setSize(width, height);
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //there are custom close operations
  frame.setResizable(false);

  MainRun jp = new MainRun();
  frame.add(jp);

  frame.setVisible(true);


  jp.mainGameLoop();  //start the game loop

 }
  
  /** This method has a loop that loops continuously throughout the game with a 16 millisecond delay.
   * It carries out game play functions.
   * This includes moving the player and moving the rectangles in the world relative to the player.
   * It also adds the damage done to the other player and sends and receives the most up to date player object to the other computer.
   */
 public void mainGameLoop() {
  for(;;){
	  repaint();

	   try {
	    Thread.sleep(16);
	   } catch (Exception e) {
	   }
  }

 }
 
 double rotateY = 0;
 
  /**Draws all the graphics to the screen.
   */
  @Override
  public void paintComponent(Graphics g){
	
    super.paintComponent(g);
    
      pixels= new int[MainRun.width*MainRun.height];

      ren.resetZBuffer();
      
      //clears the buffered image in an efficient way
      Graphics2D g2d = image.createGraphics();
      g2d.setBackground(new Color(0, 0, 0, 0));
      g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
     
//     RENDERS THE MESH 
//       ren.renderMesh(mesh, rotateY);
//       rotateY += 0.09;
      
      Point corners[] = {new Point(779.8407517664359, 170.6659937386956), new Point(779.8896675191816, 170.73168797953963), new Point(786.5115239165985, 170.66647076860178)};
      ren.rasterizeTriangle(corners);

      
      image.setRGB(0, 0, width, height, pixels, 0, width);
      g.drawImage(image, 0, 0, this);  //draws screen
      
    
  }
    
  
}