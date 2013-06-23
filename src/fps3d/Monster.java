package fps3d;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import org.w3c.dom.Element;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;

/**
 * Describes a monster in the game.
 * @author Philip
 *
 */
public class Monster extends Entity {
	protected void render() {
		glPushMatrix();
		glTranslatef(xPos,yPos,zPos);
//	    glRotatef(rotate,1.0f,1.0f,1.0f);
	    glBegin(GL_QUADS);
		    glColor3f(0.0f,1.0f,0.0f);          // Set The Color To Green
		    glVertex3f( 1.0f, 1.0f,-1.0f);          // Top Right Of The Quad (Top)
		    glVertex3f(-1.0f, 1.0f,-1.0f);          // Top Left Of The Quad (Top)
		    glVertex3f(-1.0f, 1.0f, 1.0f);          // Bottom Left Of The Quad (Top)
		    glVertex3f( 1.0f, 1.0f, 1.0f);          // Bottom Right Of The Quad (Top)

		    glColor3f(1.0f,0.5f,0.0f);          // Set The Color To Orange
		    glVertex3f( 1.0f,-1.0f, 1.0f);          // Top Right Of The Quad (Bottom)
		    glVertex3f(-1.0f,-1.0f, 1.0f);          // Top Left Of The Quad (Bottom)
		    glVertex3f(-1.0f,-1.0f,-1.0f);          // Bottom Left Of The Quad (Bottom)
		    glVertex3f( 1.0f,-1.0f,-1.0f);          // Bottom Right Of The Quad (Bottom)

		    glColor3f(1.0f,0.0f,0.0f);          // Set The Color To Red
		    glVertex3f( 1.0f, 1.0f, 1.0f);          // Top Right Of The Quad (Front)
		    glVertex3f(-1.0f, 1.0f, 1.0f);          // Top Left Of The Quad (Front)
		    glVertex3f(-1.0f,-1.0f, 1.0f);          // Bottom Left Of The Quad (Front)
		    glVertex3f( 1.0f,-1.0f, 1.0f);          // Bottom Right Of The Quad (Front)

		    glColor3f(1.0f,1.0f,0.0f);          // Set The Color To Yellow
		    glVertex3f( 1.0f,-1.0f,-1.0f);          // Bottom Left Of The Quad (Back)
		    glVertex3f(-1.0f,-1.0f,-1.0f);          // Bottom Right Of The Quad (Back)
		    glVertex3f(-1.0f, 1.0f,-1.0f);          // Top Right Of The Quad (Back)
		    glVertex3f( 1.0f, 1.0f,-1.0f);          // Top Left Of The Quad (Back)
		    
		    glColor3f(0.0f,0.0f,1.0f);          // Set The Color To Blue
		    glVertex3f(-1.0f, 1.0f, 1.0f);          // Top Right Of The Quad (Left)
		    glVertex3f(-1.0f, 1.0f,-1.0f);          // Top Left Of The Quad (Left)
		    glVertex3f(-1.0f,-1.0f,-1.0f);          // Bottom Left Of The Quad (Left)
		    glVertex3f(-1.0f,-1.0f, 1.0f);          // Bottom Right Of The Quad (Left)

	        glColor3f(1.0f,0.0f,1.0f);          // Set The Color To Violet
	        glVertex3f( 1.0f, 1.0f,-1.0f);          // Top Right Of The Quad (Right)
	        glVertex3f( 1.0f, 1.0f, 1.0f);          // Top Left Of The Quad (Right)
	        glVertex3f( 1.0f,-1.0f, 1.0f);          // Bottom Left Of The Quad (Right)
	        glVertex3f( 1.0f,-1.0f,-1.0f);          // Bottom Right Of The Quad (Right)
	    glEnd();                        // Done Drawing The Quad
	    glPopMatrix();
	}
	
	@Override
	void update() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Reads a monster from an XML element
	 * @param element Element to read from
	 * @return Monster
	 */
	public static Monster ReadFromXML(Element element) {
		Monster m = new Monster();
		m.xStartPos = Float.parseFloat(element.getAttribute("xStartPos"));
		m.yStartPos = Float.parseFloat(element.getAttribute("yStartPos"));
		m.zStartPos = Float.parseFloat(element.getAttribute("zStartPos"));
		return m;
	}

}
