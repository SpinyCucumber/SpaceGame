package spishu.space.main;

import java.io.FileInputStream;
import java.util.Map;
import java.util.logging.Level;

import org.lwjgl.Sys;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.yaml.snakeyaml.Yaml;

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
	World world;
	Map<String, Object> config;
	
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
	@SuppressWarnings("unchecked")
	public void start() {
		
		try {
			
			Game.getLogger().info("Starting GameObject");
			Game.getLogger().info(String.format("LWJGL Version %s", Sys.getVersion()));
			
			//Load config
			config = (Map<String, Object>) new Yaml().load(new FileInputStream("config.yml"));
			Game.getLogger().info(String.format("Loaded config %s", config));
			
			//Initiate graphics
			GLFW.glfwSetErrorCallback(errorCallback = Callbacks.errorCallbackPrint(System.err));
	        if ( GLFW.glfwInit() != GL11.GL_TRUE )
	            throw new IllegalStateException("Unable to initialize GLFW");
	        initGraphics();
	        
	        //Load resources
	        Game.useDefaultLoaders();
	        Game.loadResources();
	        
	        //Generate graphical objects
	        final Framebuffer mainFBO = new Framebuffer(window.getWidth(), window.getWidth());
	        final GLSLProgram primShader = (GLSLProgram) Game.getResource("shader%sprim.glsl"), fboShader = (GLSLProgram) Game.getResource("shader%sfbo.glsl");
	        final Camera camera = new Camera(new Vec2(0, 0), 1, 4000, 0.99f, window);
	        final boolean useShaders = (Boolean) config.get("useShaders");
	        world = new World(new Vec2(0, 0), 50.0f, 10);
	        
	        Game.getLogger().info(String.format("Initialized world %s", world));
	        Game.getLogger().info(String.format("Initialized camera %s", camera));
	        
	        //Get dimensions to be used in glOrtho
	        Vec2 d = window.getDimensions().invScale(2);
	        AABB worldOrtho = new AABB(new Vec2(-d.x, d.y), new Vec2(d.x, -d.y)), screenOrtho = new AABB(Vec2.ZERO, window.getDimensions());
	        
	        //Add entities
	        Animation anim = (Animation) Game.getResource("texture%stest.anim");
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
	        		if(useShaders) primShader.use();
		        	camera.transform();
		        	
		            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		            world.draw();
		            
	        	} EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
	        	
	        	GL11.glLoadIdentity();
	        	screenOrtho.glViewport();
	        	screenOrtho.glOrtho();
	        	
	        	if(useShaders) fboShader.use();
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
        window = new GLWindow((Integer) config.get("windowWidth"), (Integer) config.get("windowHeight"));
        window.setPosition(GLWindow.getScreenDimensions().sub(window.getDimensions()).invScale(2));

        window.makeContext();
        // Enable v-sync
        if((Boolean) config.get("vsync")) GLFW.glfwSwapInterval(1);
        
        //Do opengl stuff (create glcontext from thread)
        GLContext.createFromCurrent();
	    
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
	}
	
	public static void main(String[] args) {
		
		new GameObject().start();
		
	}

}
