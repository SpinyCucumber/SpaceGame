package spishu.space.main;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.File;

import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import spishu.space.engine.computer.chip8.Chip;

import spishu.space.engine.anim.Animation;
import spishu.space.engine.anim.TextureLineup;
import spishu.space.engine.computer.chip8.Chip;
import spishu.space.engine.entity.SingleShapeEntity;
import spishu.space.engine.gl.Framebuffer;
import spishu.space.engine.gl.GLWindow;
import spishu.space.engine.gl.Texture;
import spishu.space.engine.math.Rectangle;
import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2;
import spishu.space.engine.phys.World;

public class EmulatorTest { //It all starts here
	
	GLFWErrorCallback errorCallback;
	boolean running;
	double time, lastTime;
	
	GLWindow window;
	World world;
	
	Chip chip = new Chip();
	private byte[] display;
	private int displen;
	
	public double getTime() {
    	return glfwGetTime();
    }
    
    public double delta() {
	    time = getTime();
	    try {
	    	return time - lastTime;
	    } finally {
    		lastTime = time;
    	}
    }
	
	void setRunning(boolean running) {
		this.running = running;
	}

	public void start() {
		
	
		try {
			
			System.out.println("Hello LWJGL " + Sys.getVersion() + "!");
			
			glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
	        if ( glfwInit() != GL11.GL_TRUE )
	            throw new IllegalStateException("Unable to initialize GLFW");
	        
	        initGraphics();        
	        
			Framebuffer fb = new Framebuffer(640, 320);
			
			chip.init();
			
			chip.loadProgram("C:/Users/Carl/Downloads/pong2.c8");
	        	        
	       while(!window.shouldClose()) {
		        
				chip.run();
	    	   
				display = chip.display;
				displen = display.length;
		        
				fb.bind();
				
	            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				
				GL11.glPointSize(10f);
	            
				GL11.glBegin(GL_POINTS);
				
            
		        for(int i = 0; i < displen; i++) {
					if(display[i] == 0){
						/**
						int x = (i % 64);
						int y = (int)Math.floor(i / 64);
						batch.draw(b2, (x * 10), (y * 10), 10, 10);
						
						*/
					
					} else{
						int x = (i % 64);
						int y = (int)Math.floor(i / 64);
						
						GL11.glVertex2f(x, y);
						

					}
					}
		        
				GL11.glEnd();
		        
		        Framebuffer.unbind();
		        fb.bindColorTexture();
		        window.fullscreenQuad();
	        	
	        	delta();
       	
	        	glLoadIdentity();
	            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	        	window.setTitle("SWAG LEVEL: " + time);
	            
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

        window.makeContext();
        // Enable v-sync
        glfwSwapInterval(1);
        
        //Do opengl stuff (create glcontext from thread)
        GLContext.createFromCurrent();
        
        glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, window.getWidth(), window.getHeight(), 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
	    
		glEnable(GL_TEXTURE_2D);
		
	}
	
	public static void main(String[] args) {
		
		new EmulatorTest().start();
		
	}

}
