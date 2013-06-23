package fps3d;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.lwjgl.BufferUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Describes a game level.
 * @author Philip
 *
 */
public class Level {
	//Statics
	private final static float MOVE_FORWARD_SPEED = 0.25f;
	private final static float MOVE_BACKWARD_SPEED = 0.3f;
	private final static float STRAFE_SPEED = 0.3f;

	private float ROTATE_SPEED = 1.0f;
	
	//Level
	private float xStartPos, yStartPos, zStartPos;
	//private float width, length;
	private static int MAX_MONSTERS = 30;
	private Monster[] monsters;
	private boolean[] monstersLiving;
	
	//Light
	private FloatBuffer lightPosition;
	private FloatBuffer whiteLight;
	private FloatBuffer lModelAmbient;	
		
	//Player
	private float xPos, yPos, zPos;
	private float heading = 0.0f;
	private float walkbiasangle;
	private float walkbias;

	private static final int BULLET_INTERVAL = 0;	
	private ArrayList<Bullet> bullets;
	private int MAX_BULLETS;
	private long bulletTime;
	
	/**
	 * Load a level from a file
	 * @param filename File name.
	 * @return Level
	 */
	public static Level LoadFromFile(String filename) {
		if(filename != null) {
			InputStream is = Level.class.getResourceAsStream(filename);
			if(is != null) {  
				try {
			        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			        doc.getDocumentElement().normalize();
			        Element gNode = doc.getDocumentElement();
			        Level level = ReadFromXML(gNode);
			        return level;		        	
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}	
		}
		return null;
	}
	
	/**
	 * Read the level from an XML element.
	 * @param element Element
	 * @return Level
	 */
	private static Level ReadFromXML(Element element) {
		Level l = new Level();
		l.xStartPos = Float.parseFloat(element.getAttribute("xStartPos"));
		l.yStartPos = Float.parseFloat(element.getAttribute("yStartPos"));
		l.zStartPos = Float.parseFloat(element.getAttribute("zStartPos"));
        NodeList nodes = element.getChildNodes();
        for(int n=0; n < nodes.getLength(); n++) {
        	Node nNode = nodes.item(n);
        	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        		Element eElement = (Element) nNode;
        		if(eElement.getTagName().equals("monsters")) {
        			NodeList mList = eElement.getChildNodes();
        			for(int mNode = 0;(mNode < mList.getLength() && mNode < MAX_MONSTERS); mNode++) {
        				if(mList.item(mNode).getNodeType() == Node.ELEMENT_NODE) {
        					l.monsters[mNode] = Monster.ReadFromXML((Element) mList.item(mNode)); 
        				}
        			}
        		}
        	}
        }
		return l;		
	}
	/**
	 * Constructor
	 */
	private Level() {
		monsters = new Monster[MAX_MONSTERS];
		monstersLiving = new boolean[MAX_MONSTERS];
		bullets = new ArrayList<Bullet>(MAX_BULLETS);
	}
	/**
	 * Start the level.
	 */
	protected void start() {
		xPos = xStartPos;
		yPos = yStartPos;
		zPos = zStartPos;
		for(int i = 0; i < monsters.length; i++) {
			if(monsters[i] != null) {
				monsters[i].resetPosition();
				monstersLiving[i] = true;			
			}
		}		
	}
	/**
	 * Render the level.
	 */
	protected void render() {
	    //glEnable(GL_LIGHT0);
	    lightPosition = BufferUtils.createFloatBuffer(4);
		lightPosition.put(1.0f).put(1.0f).put(10.0f).put(0.0f).flip();
		whiteLight = BufferUtils.createFloatBuffer(4);
		whiteLight.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();
		lModelAmbient = BufferUtils.createFloatBuffer(4);
		lModelAmbient.put(0.5f).put(0.5f).put(0.5f).put(1.0f).flip();
	    glRotatef(heading,0f,1.0f,0f);
	    //	glLight(GL_LIGHT0, GL_POSITION, lightPosition);				// sets light position
	    //	glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);				// sets specular light to white
	    //	glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);	    
	    	glTranslatef(-xPos,-walkbias-yPos,-zPos);              // Move Right And Into The Screen
		/**	    drawWall(0.0f,	0.0f,0.0f,5f,0f,50f);
			    drawWall(0.0f,	0.0f,0.0f,5f,50f,0f);	    
			    drawFloor(0.0f,0.0f,0.0f,50f,50f);
			    drawCube(rquad,11.5f,0.0f,-30.0f);
			    drawCube(0,11.5f,6.0f,-30.0f);		**/
		
		renderFloor(0.0f,0.0f,0.0f,50f,50f);
		renderWalls(0.0f,0.0f,0.0f,5f,0f,50f);
		for(int b = 0; b < bullets.size(); b++) {
			if(bullets.get(b) != null) {
				bullets.get(b).render();
			}
		}
		for(int m = 0; m < monsters.length; m++) {
			if(monstersLiving[m]) {
				monsters[m].render();
			}
		}
	}
	/**
	 * Fire a bullet.
	 */
	protected void fireBullet() {
		if(bulletTime - System.nanoTime() >= BULLET_INTERVAL && bullets.size() < MAX_BULLETS) {
			bullets.add(new Bullet(xPos,yPos,zPos,heading));
		}
	}
	/**
	 * Walk forwards.
	 */
	protected void walkForwards() {
	    zPos -= Math.cos(Math.toRadians(heading))*MOVE_FORWARD_SPEED;
	    xPos += Math.sin(Math.toRadians(heading))*MOVE_FORWARD_SPEED;
	    if (walkbiasangle >= 359.0f)               
	    {
	        walkbiasangle = 0.0f;               
	    }
	    else                             
	    {
	         walkbiasangle+=5;                 
	    }
	    walkbias = (float)Math.sin(Math.toRadians(walkbiasangle))/20.0f;     // Causes The Player To Bounce
	}
	/**
	 * Walk backwards.
	 */
	protected void walkBackwards() {
		zPos += Math.cos(Math.toRadians(heading))*MOVE_BACKWARD_SPEED;
		xPos -= Math.sin(Math.toRadians(heading))*MOVE_BACKWARD_SPEED;
	    if (walkbiasangle <= 0.0f)                
	    {
	        walkbiasangle = 359.0f;
	    }
	    else                   
	    {
	         walkbiasangle-=5;                  
	    }
	    walkbias = (float)Math.sin(Math.toRadians(walkbiasangle))/20.0f;     // Causes The Player To Bounce		
	}
	/**
	 * Strafe left.
	 */
	protected void strafeLeft() {
		xPos -= Math.cos(Math.toRadians(heading))*STRAFE_SPEED; //LEFT
		zPos -= Math.sin(Math.toRadians(heading))*STRAFE_SPEED;
	}
	/**
	 * Strafe right.
	 */
	protected void strafeRight() {
		xPos += Math.cos(Math.toRadians(heading))*STRAFE_SPEED; //RIGHT
		zPos += Math.sin(Math.toRadians(heading))*STRAFE_SPEED;
	}
	/**
	 * Turn left.
	 */
	protected void turnLeft() {
		heading -= ROTATE_SPEED;
	}
	/**
	 * Turn right.
	 */
	protected void turnRight() {
		heading += ROTATE_SPEED;
	}
	/**
	 * Update the level.
	 */
	protected void update() {
		
	}
	/**
	 * Check whether finished
	 * @return True if finished.
	 */
	protected boolean isFinished() {
		for(int i = 0; i < monsters.length; i++) {
			if(monstersLiving[i]) {
				return false;
			}
		}
		//TODO: Finish
		return false;
	}
	/**
	 * Render the floor.
	 * @param x X
	 * @param y Y
	 * @param z Z
	 * @param width Width
	 * @param length Length
	 */
	private void renderFloor(float x, float y, float z, float width, float length) {
		glPushMatrix();
		glBegin(GL_QUADS);
			glColor3f(0.0f,0.39f,0.0f);          // Set The Colour To Green
			glVertex3f(x,y,-z);
			glVertex3f(x+width,y,-z);
			glVertex3f(x+width,y,-(z+length));
			glVertex3f(x,y,-(z+length));
		glEnd();                        // Done Drawing The Floor
	    glPopMatrix();
	}
	/**
	 * Render the walls
	 * @param x X
	 * @param y Y
	 * @param z Z
	 * @param height Height
	 * @param width Width
	 * @param length Length
	 */
	private void renderWalls(float x, float y, float z, float height, float width, float length) {
		glPushMatrix();
		glBegin(GL_QUADS);
			glColor3f(0.39f,0.39f,0.39f);          // Set The Colour To Grey
			glVertex3f(x,y,-z);
			glVertex3f(x+width,y,-(z+length));
			glVertex3f(x+width,y+height,-(z+length));
			glVertex3f(x,y+height,-z);
		glEnd();                        // Done Drawing The Floor
	    glPopMatrix();
	}
	
	/**
	 * Restart the level.
	 */
	public void restart() {
		//Reset Monsters
		for(int i = 0; i < monsters.length; i++) {
			if(monsters[i] != null) {
				monsters[i].resetPosition();
				monstersLiving[i] = true;	
			}		
		}
		//Remove Bullets
		bullets = new ArrayList<Bullet>(MAX_BULLETS);
		xPos = xStartPos;
		yPos = yStartPos;
		zPos = zStartPos;
	}
}
