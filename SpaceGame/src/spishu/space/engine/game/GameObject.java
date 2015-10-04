package spishu.space.engine.game;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.lwjgl.Sys;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.yaml.snakeyaml.Yaml;

import spishu.space.engine.gl.GLTimer;
import spishu.space.engine.gl.GLWindow;

/**
 * Abstract game class. Pretty much just a bundling of useful objects
 * and some abstract methods. Used to create demos (and games) easily and quickly.
 * @author SpinyCucumber
 *
 */
public abstract class GameObject {
	
	protected GLTimer timer;
	protected GLWindow window;
	protected Map<String, Object> config;
	
	/**
	 * Runs every frame, along with window updates, etc.
	 * @throws Exception
	 */
	protected abstract void mainLoop(double delta) throws Exception;
	
	/**
	 * Called when game finishes.
	 */
	protected void onExit() {}
	
	/**
	 * Called when game starts.
	 */
	protected void onStart() throws Exception {}
	
	/**
	 * Initializes objects and starts main loop.
	 */
	@SuppressWarnings("unchecked")
	public void start() {
			
		try { //Catch intialization errors
		
			//Load configuration and log initialization status.
			config = (Map<String, Object>) new Yaml().load(new FileInputStream("config.yml"));
			
			Level level = Level.parse((String) config.get("logLevel"));
			Game.getLogger().setLevel(level);
			for(Handler handler : Game.getLogger().getHandlers()) handler.setLevel(level);
			Game.info("Starting GameObject %s", this.getClass());
			Game.info("Logger level %s", Game.getLogger().getLevel());
			Game.info("Loaded config %s", config);
			Game.info("LWJGL Version %s (��� ���� ���� ����) ", Sys.getVersion());
			
			//Set glfw errorcallback to logger.
			final GLFWErrorCallback errorCallback = new GLFWErrorCallback() {
				public void invoke(int arg0, long arg1) {
					String message = Callbacks.errorCallbackDescriptionString(arg1);
					Game.getLogger().warning(String.format("GLFW error: %s", message));
				}
			};
			GLFW.glfwSetErrorCallback(errorCallback);
			
			initGraphics();
			timer = new GLTimer((Double) config.get("timeScale"));
			
			//Log initialized components
			Game.info("Initialized window %s", window);
			Game.info("Initialized timer %s", timer);
			
			onStart();
			Game.log(Level.FINE, "Entering main loop");
			
			try {
				
				while(!window.shouldClose()) { //Main loop
					
					mainLoop(timer.update()); //Abstract loop
					
					window.swapBuffers();
					GLFW.glfwPollEvents();
					
				}
				
			} catch(Exception e) { //Runtime exception.
				
				Game.getLogger().log(Level.SEVERE, "Joshua crashed your program!", e);
				
			} finally {
				//Provide statistics
		        Map<String, Object> exitStats = new HashMap<String, Object>();
		        exitStats.put("frames", timer.getFrames());
		        exitStats.put("avgFrameLength", timer.getTime() / timer.getFrames());
		        exitStats.put("avgFPS", timer.getFrames() / timer.getTime());
		        Game.info("Exiting %s", exitStats);
		        
		        onExit();
				window.destroy();
				errorCallback.release();
				GLFW.glfwTerminate();
				
			}
			
		} catch(Exception e) { //Exceptions while initializing
			
			System.err.println("Error initializing:");
			e.printStackTrace(); //Do not use logger; could have been problem.
			
		}
		
	}
	
	/**
	 * Encapsulated for future access. (When changing display options or something)
	 */
	protected void initGraphics() {
		
		//Initialize glfw
		if(GLFW.glfwInit() != GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");
		
        // Configure our window
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
        
        // Create the window
        window = new GLWindow((Integer) config.get("windowWidth"), (Integer) config.get("windowHeight"));
        window.setPosition(GLWindow.getScreenDimensions().sub(window.getDim()).invScale(2));
        window.setTitle(this.getClass().getSimpleName());
        window.makeContext();
        
        // Enable v-sync if requested
        if((Boolean) config.get("vsync")) GLFW.glfwSwapInterval(1);
        
        //Do opengl stuff (create glcontext from thread)
        GLContext.createFromCurrent();
        
	}
	
}