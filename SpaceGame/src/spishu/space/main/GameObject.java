package spishu.space.main;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;

import spishu.space.engine.math.Vec2;
import spishu.space.engine.phys.World;

public class GameObject { //It all starts here
	
	GLFWErrorCallback errorCallback;
	boolean running;
	double time, lastTime;
	
	Graphics graphics;
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
	
	void setRunning(boolean running) {
		this.running = running;
	}

	public void start() {
		
		try {
			
			System.out.println("Hello LWJGL " + Sys.getVersion() + "!");
			
			glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
	        if ( glfwInit() != GL11.GL_TRUE )
	            throw new IllegalStateException("Unable to initialize GLFW");
	        
	        world = new World(new Vec2(0, 0));
	        
	        graphics = new Graphics(this);
			graphics.start();
	        
			running = true;
			
	        while(running) {
	        	
	        	world.update(delta());
	        	
	        }
	        
	        graphics.join();
	        
		} catch(Exception e) {

			e.printStackTrace();
			
		} finally {
			
			glfwTerminate();
			errorCallback.release();
			
		}
		
	}
	
	public static void main(String[] args) {
		
		new GameObject().start();
		
	}

}
