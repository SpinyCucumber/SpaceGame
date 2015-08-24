package spishu.space.main;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GLContext;

public class Graphics extends Thread {
	
	GameObject gameInstance;
    
    private int width, height;
    
    // The window handle, might make wrapper
    long window;
    
    public Graphics(GameObject gameInstance, int width, int height) {
		this.gameInstance = gameInstance;
		this.width = width;
		this.height = height;
	}
    
    @Override
	public void run() {
        	
        init();
        
        while (glfwWindowShouldClose(window) == GL_FALSE) {
        	
        	glLoadIdentity();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        	glfwSetWindowTitle(window, "SWAG LEVEL: " + gameInstance.time);
            gameInstance.world.draw();
            
            glfwSwapBuffers(window);
        	
        	glfwPollEvents();
            
        }
        
        glfwDestroyWindow(window);
        gameInstance.running = false;
        
    }
 
    private void init() {
    	
        // Configure our window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
 
        // Create the window
        window = glfwCreateWindow(width, height, "", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
            window,
            (GLFWvidmode.width(vidmode) - width) / 2,
            (GLFWvidmode.height(vidmode) - height) / 2
        );

        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        
        //Do opengl stuff (create glcontext from thread)
        GLContext.createFromCurrent();
        
        glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, width, height, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
        
    }
	
}
