package spishu.space.main;

import java.util.logging.Level;

import org.lwjgl.Sys;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import spishu.space.engine.anim.Animation;
import spishu.space.engine.gl.Camera;
import spishu.space.engine.gl.Framebuffer;
import spishu.space.engine.gl.GLSLProgram;
import spishu.space.engine.gl.GLWindow;
import spishu.space.engine.math.AABB;
import spishu.space.engine.math.Rectangle;
import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2;
import spishu.space.engine.phys.ShapeEntity;
import spishu.space.engine.phys.World;

/**
 * Main game class, representing an instance of the game.
 * @author SpinyCucumber
 *
 */
public class GameObject {
	
	GLFWErrorCallback errorCallback;
	double time, lastTime, timeScale = 0.5;
	
	GLWindow window;
	Camera camera;
	World world;
	
	public double getTime() {
    	return GLFW.glfwGetTime();
    }
    
	/**
	 * @return Time in seconds between current frame and last frame.
	 */
    public double delta() {
	    time = getTime();
	    double delta = time - lastTime;
    	lastTime = time;
    	return delta;
    }
    
    /**
     * Begins running the game.
     */
	public void start() {
		
		try {
			
			Game.getLogger().info("Starting GameObject");
			Game.getLogger().info(String.format("LWJGL Version %s", Sys.getVersion()));
			
			GLFW.glfwSetErrorCallback(errorCallback = Callbacks.errorCallbackPrint(System.err));
	        if ( GLFW.glfwInit() != GL11.GL_TRUE )
	            throw new IllegalStateException("Unable to initialize GLFW");
	        
	        initGraphics();
	        Game.useDefaultLoaders();
	        Game.loadResources();
	        
	        final Framebuffer mainFBO = new Framebuffer(window.getWidth(), window.getWidth());
	        final GLSLProgram primShader = (GLSLProgram) Game.getResource("shader\\prim.glsl"), fboShader = (GLSLProgram) Game.getResource("shader\\fbo.glsl");
	        world = new World(new Vec2(0, 0), 50.0f, 10);
	        camera = new Camera(new Vec2(0, 0), 1, 4000, 0.99f, window);
	        
	        Game.getLogger().info(String.format("Initialized world %s", world));
	        Game.getLogger().info(String.format("Initialized camera %s", camera));
	        
	        Vec2 d = window.getDimensions().invScale(2);
	        AABB worldOrtho = new AABB(new Vec2(-d.x, d.y), new Vec2(d.x, -d.y)), screenOrtho = new AABB(Vec2.ZERO, window.getDimensions());

	        Animation anim = (Animation) Game.getResource("texture\\test.anim");
	        new ShapeEntity<Shape>(world, new Vec2(200f, 0), new Vec2(-400, 0),
	        		1, 30, 0, 1, Rectangle.fromDimensions(new Vec2(200)), anim);
	        new ShapeEntity<Shape>(world, new Vec2(0, 0), new Vec2(0, 0),
	        		1, 0, 0, 1, Rectangle.fromDimensions(new Vec2(100)), anim);
	        
	        while(!window.shouldClose()) { //Main game loop. Will probably seperate into graphics class.
	        	
	        	double delta = delta() * timeScale;
	        	world.update(delta);
	        	camera.update(delta);
	        	
	        	mainFBO.bind(); {
	        		
	        		worldOrtho.glOrtho();
	        		primShader.use();
		        	camera.transform();
		        	
		            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		            world.draw();
		            
	        	} Framebuffer.unbind();
	        	
	        	GL11.glLoadIdentity();
	        	screenOrtho.glViewport();
	        	screenOrtho.glOrtho();
	        	
	        	fboShader.use();
	        	mainFBO.bindColorTexture();
	        	Rectangle.fromAABB(screenOrtho).texturedQuad();
	        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	        	
	        	window.setTitle("SWAG LEVEL: " + time);
	            window.swapBuffers();
	        	
	        	GLFW.glfwPollEvents();
	        	
	        }
	        
	        Game.getLogger().info("Exiting");
	        
	        world.delete();
			window.destroy();
			GLFW.glfwTerminate();
			errorCallback.release();
	        
		} catch(Exception e) {

			Game.getLogger().log(Level.SEVERE, "Joshua crashed your program!");
			e.printStackTrace();
			
		}
		
	}
	
	 private void initGraphics() {
	    	
        // Configure our window
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
        
        // Create the window
        window = new GLWindow(1000, 700);
        window.setPosition(GLWindow.getScreenDimensions().sub(window.getDimensions()).invScale(2));

        window.makeContext();
        // Enable v-sync
        GLFW.glfwSwapInterval(1);
        
        //Do opengl stuff (create glcontext from thread)
        GLContext.createFromCurrent();
	    
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
	}
	
	public static void main(String[] args) {
		
		new GameObject().start();
		
	}

}
