package spishu.space.emulator.chip8;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPointSize;

import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import spishu.space.engine.gl.Framebuffer;
import spishu.space.engine.gl.GLWindow;
import spishu.space.engine.math.AABB;
import spishu.space.engine.math.Rectangle;
import spishu.space.engine.math.Vec2;

public class EmulatorTest {
	
	GLFWErrorCallback errorCallback;
	
	GLWindow window;
	
	Chip chip = new Chip();
	private byte[] display;
	private int displen;

	public void start() {
		
	
		try {
			
			System.out.println("Hello LWJGL " + Sys.getVersion() + "!");
			
			glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
	        if ( glfwInit() != GL11.GL_TRUE )
	            throw new IllegalStateException("Unable to initialize GLFW");
	        
	        initGraphics();
	        
			Framebuffer fbo = new Framebuffer(64, 32);
			AABB fbOrtho = new AABB(new Vec2(0, 32), new Vec2(64, 0)),
					screenOrtho = new AABB(Vec2.ZERO, window.getDimensions());
			System.out.println(fbOrtho + System.lineSeparator() + screenOrtho);
			
			chip.init();
			chip.loadProgram("res/prog/pong2.c8");
			
	        while(!window.shouldClose()) {
		        
				chip.run();
	    	   
				display = chip.display;
				displen = display.length;
				
				fbo.bind(); {
					
					fbOrtho.glOrtho();
					
					glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
					glBegin(GL_POINTS);
	            
			        for(int i = 0; i < displen; i++) {
			        	
						if(display[i] == 0) {
							/**
							int x = (i % 64);
							int y = (int)Math.floor(i / 64);
							batch.draw(b2, (x * 10), (y * 10), 10, 10);
							
							*/
						
						} else {
	
							int x = (i % 64);
							int y = (int)Math.floor(i / 64);
							
							GL11.glVertex2f(x, y);
							
						}
						
					}
			        
					GL11.glEnd();
				
				} Framebuffer.unbind();

				screenOrtho.glViewport();
				screenOrtho.glOrtho();
		        
		        fbo.bindColorTexture();
		        Rectangle.fromAABB(screenOrtho).texturedQuad();
		        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	            
	            window.swapBuffers();
	        	
	        	glfwPollEvents();
	        	
	        }
	        
		} catch(Exception e) {

			e.printStackTrace();
			
		} finally {
			
			window.destroy();
			glfwTerminate();
			errorCallback.release();
			
		}
		
	}
	
	 private void initGraphics() {
	    	
        // Configure our window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
 
        // Create the window
        window = new GLWindow(1000, 700);
        window.setPosition(GLWindow.getScreenDimensions().sub(window.getDimensions()).invScale(2));
        window.setTitle("Chip8 Emulator");
        window.makeContext();
        // Enable v-sync
        glfwSwapInterval(1);
        
        //Do opengl stuff (create glcontext from thread)
        GLContext.createFromCurrent();
	    
		glEnable(GL_TEXTURE_2D);
		glPointSize(1);
		
	}
	
	public static void main(String[] args) {
		
		new EmulatorTest().start();
		
	}

}
