package spishu.space.main;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.File;

import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import spishu.space.engine.anim.Animation;
import spishu.space.engine.anim.TextureLineup;
import spishu.space.engine.entity.SingleShapeEntity;
import spishu.space.engine.gl.GLWindow;
import spishu.space.engine.gl.Texture;
import spishu.space.engine.math.Rectangle;
import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2;
import spishu.space.engine.phys.World;

public class GameObject { //It all starts here
	
	GLFWErrorCallback errorCallback;
	double time, lastTime;
	
	GLWindow window;
	World world;
	
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

	public void start() {
		
		try {
			
			System.out.println("Hello LWJGL " + Sys.getVersion() + "!");
			
			glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
	        if ( glfwInit() != GL11.GL_TRUE )
	            throw new IllegalStateException("Unable to initialize GLFW");
	        
	        initGraphics();
	        
	        world = new World(new Vec2(0, 0));
	        
	        Animation anim = new TextureLineup(0, Texture.fromFile(new File("res/texture/ComputerCraft.png")));
	        new SingleShapeEntity<Shape>(world, new Vec2(0.1f, 0), window.getDimensions().invScale(2), 0,
	        		0, 0.1f, Rectangle.fromHalfDimension(new Vec2(0), new Vec2(100)), anim);
			
	        while(!window.shouldClose()) {
	        	
	        	world.update(delta());

	            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	        	window.setTitle("SWAG LEVEL: " + time);
	            world.draw();
	            
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
		
		new GameObject().start();
		
	}

}
