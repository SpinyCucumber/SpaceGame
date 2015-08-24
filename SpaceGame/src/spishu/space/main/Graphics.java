package spishu.space.main;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GLContext;

import spishu.space.engine.gl.GLWindow;

public class Graphics extends Thread {
	
	GameObject gameInstance;    
    GLWindow window;
    
    public Graphics(GameObject gameInstance) {
		this.gameInstance = gameInstance;
	}
    
    @Override
	public void run() {
        	
        init();
        
        while (window.shouldClose()) {
        	
        	glLoadIdentity();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        	window.setTitle("SWAG LEVEL: " + gameInstance.time);
            gameInstance.world.draw();
            
            window.swapBuffers();
        	
        	glfwPollEvents();
            
        }
        
        window.destroy();
        gameInstance.running = false;
        
    }
 
    private void init() {
    	
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
		glOrtho(0, window.width, window.height, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
        
    }
	
}
