package fps3d;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class FPS3D extends Applet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Canvas display_parent;
	/** Thread which runs the main game loop */
	Thread gameThread;
	/** is the game loop running */
	boolean running = false;

	private Level level;
	



	/**
	 * Creates the LWJGL display context
	 */
	protected void startLWJGL() {
		gameThread = new Thread() {
			public void run() {
				running = true;
				try {
					Display.setParent(display_parent);
					Display.create();
					initGL();
				} catch (LWJGLException e) {
					e.printStackTrace();
					return;
				}
				
				level = Level.LoadFromFile("level.xml");
				gameLoop();
			}
		};
		gameThread.start();
	}
	/**
	 * Tell game loop to stop running, after which the LWJGL Display will 
	 * be destroyed. The main thread will wait for the Display.destroy().
	 */
	protected void stopLWJGL() {
		running = false;
		try {
			gameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		
	}

	public void stop() {
		
	}
	
	/**
	 * Applet Destroy method will remove the canvas, 
	 * before canvas is destroyed it will notify stopLWJGL()
	 * to stop the main game loop and to destroy the Display
	 */
	public void destroy() {
		remove(display_parent);
		super.destroy();
	}
	
	public void init() {
		setLayout(new BorderLayout());
		try {
			display_parent = new Canvas() {
				private static final long serialVersionUID = 1L;
				public final void addNotify() {
					super.addNotify();
					startLWJGL();
				}
				public final void removeNotify() {
					stopLWJGL();
					super.removeNotify();
				}
			};
			display_parent.setSize(getWidth(),getHeight());
			add(display_parent);
			display_parent.setFocusable(true);
			display_parent.requestFocus();
			display_parent.setIgnoreRepaint(true);
			setVisible(true);
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException("Unable to create display");
		}
	}

	protected void initGL() {
		// OpenGL
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(45.0f,((float) getWidth()/(float) getHeight()),0.1f,1000.0f);
		glMatrixMode(GL_MODELVIEW);	
		// Keyboard and Mouse stuff
		Mouse.setGrabbed(false);
	}
	public void gameLoop() {
		level.start();
		while(running) {
			
			gameLogic();
			gameRender();
		}
		
		Display.destroy();
	}
	private void pollInput() {
        if (Mouse.isButtonDown(0)) {
		}
        if(level != null) {
			if (Keyboard.isKeyDown(KeyboardLayout.WALKFORWARD)) {
				level.walkForwards();
			}
			if (Keyboard.isKeyDown(KeyboardLayout.WALKBACKWARD)) {
				level.walkBackwards();
			}
			if (Keyboard.isKeyDown(KeyboardLayout.STRAFELEFT)) {
				level.strafeLeft();
			}
			if (Keyboard.isKeyDown(KeyboardLayout.STRAFERIGHT)) {
				level.strafeRight();
			}
			if (Keyboard.isKeyDown(KeyboardLayout.TURNLEFT)) {
				level.turnLeft();
			}
			if (Keyboard.isKeyDown(KeyboardLayout.TURNRIGHT)) {
				level.turnRight();
			}
			if (Keyboard.isKeyDown(KeyboardLayout.FIRE)) {
				level.fireBullet();
			}
        }
		
		while (Keyboard.next()) {
		    if (Keyboard.getEventKeyState()) {
		    }
		}
    }
	private void gameLogic() {		
		if(level.isFinished()) {
			System.out.println("Restarting");
			level.restart();
		}
		
		pollInput();
	}
	private void gameRender() {
		glLoadIdentity();
		// Clear the screen and depth buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);	
	    glClearDepth(1.0f);                         // Depth Buffer Setup
	    glEnable(GL_DEPTH_TEST); 
	    glEnable(GL_BLEND);
	    glDepthFunc(GL_LEQUAL);
	    //glEnable(GL_LIGHTING);
	    glEnable(GL_COLOR_MATERIAL);
	    glColorMaterial ( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE ) ;
	    level.render();
		Display.sync(60);
		Display.update();
	}

}