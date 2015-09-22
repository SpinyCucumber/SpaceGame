package spishu.space.main;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.lwjgl.Sys;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.yaml.snakeyaml.Yaml;

import spishu.space.engine.anim.Animation;
import spishu.space.engine.game.Game;
import spishu.space.engine.gl.Camera2d;
import spishu.space.engine.gl.Framebuffer;
import spishu.space.engine.gl.GLSLProgram;
import spishu.space.engine.gl.GLTimer;
import spishu.space.engine.gl.GLWindow;
import spishu.space.engine.math.AABB;
import spishu.space.engine.math.Rectangle;
import spishu.space.engine.math.Vec2;
import spishu.space.engine.phys.World;
import spishu.space.ship.ShipData;
import spishu.space.ship.ShipEntity;
import spishu.space.ship.ShipTile;

/**
 * Main game class, representing an instance of the game.
 * @author SpinyCucumber
 *
 */
public class SpaceGame {

	GLTimer timer;
	GLWindow window;
	World world;
	Map<String, Object> config;
    
    /**
     * Begins running the game.
     */
	@SuppressWarnings("unchecked")
	public void start() {
		
		try {
			
			//Load config and log initialization specs
			config = (Map<String, Object>) new Yaml().load(new FileInputStream("config.yml"));
			
			Game.getLogger().setLevel(Level.parse((String) config.get("logLevel")));
			Game.getLogger().info(String.format("Logger level %s", Game.getLogger().getLevel()));
			Game.getLogger().info(String.format("Loaded config %s", config));
			Game.getLogger().info(String.format("LWJGL Version %s (﻿ ͡° ͜ʖ ͡°) ", Sys.getVersion()));
			
			//Set error callback to logger
			final GLFWErrorCallback errorCallback = new GLFWErrorCallback() {
				public void invoke(int arg0, long arg1) {
					String message = Callbacks.errorCallbackDescriptionString(arg1);
					Game.getLogger().warning(String.format("GLFW error: %s", message));
				}
			};
			GLFW.glfwSetErrorCallback(errorCallback);
	       
	        initGraphics();
	        
	        //Load resources
	        Game.useDefaultLoaders();
	        Game.setSource(getClass());
	        Game.loadResources();
	        
	        //Generate objects
	        final Framebuffer mainFBO = new Framebuffer(window.getWidth(), window.getWidth());
	        final GLSLProgram primShader = (GLSLProgram) Game.getResource("shader%sprim.glsl"), fboShader = (GLSLProgram) Game.getResource("shader%sfbo.glsl");
	        final Camera2d camera = new Camera2d(new Vec2(0, 0), 1, (double) config.get("cameraMoveSpeed"), (double) config.get("cameraZoomSpeed"), window);
	        final boolean useShaders = (Boolean) config.get("useShaders");

	        timer = new GLTimer((double) config.get("timeScale"));
	        world = new World(new Vec2(0, 0), 50.0f, 10);
	        
	        //Log junk
	        Game.getLogger().info(String.format("Initialized timer %s", timer));
	        Game.getLogger().info(String.format("Initialized world %s", world));
	        Game.getLogger().info(String.format("Initialized camera %s", camera));
	        Game.getLogger().info(String.format("Using %d collision detectors", World.getColliders().size()));
	        
	        //Get dimensions to be used in glOrtho
	        Vec2 d = window.getDimensions().invScale(2);
	        AABB worldOrtho = new AABB(new Vec2(-d.x, d.y), new Vec2(d.x, -d.y)), screenOrtho = new AABB(Vec2.ZERO, window.getDimensions());
	        
	        //Add entities. Testing ships.
	        ShipTile tile = new ShipTile((Animation) Game.getResource("texture%stest.anim"));
	        ShipData data = new ShipData(new ShipTile[][]{{tile, tile},{tile, tile}});
	        data.renderToBuffer();
	        new ShipEntity(data, world, new Vec2(0, 0), new Vec2(0, 0), 1, 0, 0, 1);
	        
	        while(!window.shouldClose()) { //Main game loop. Will probably seperate into graphics class.
	        	
	        	//Update stuff
	        	double delta = timer.update();
	        	world.update(delta);
	        	camera.update(delta);
	        	
	        	//Render to fbo
	        	mainFBO.bind(); {
	        		
	        		worldOrtho.glOrtho();
	        		if(useShaders) primShader.use();
		        	camera.transform();
		        	
		            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		            world.draw();
		            
	        	} Framebuffer.unbind();
	        	
	        	GL11.glLoadIdentity();
	        	screenOrtho.glViewport();
	        	screenOrtho.glOrtho();
	        	
	        	//Render fbo to screen
	        	if(useShaders) fboShader.use();
	        	mainFBO.getColorTexture().bind();
	        	Rectangle.fromAABB(screenOrtho).texturedQuad();
	        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	        	
	        	window.setTitle("SWAG LEVEL: " + timer.getTime());
	            window.swapBuffers();
	        	
	        	GLFW.glfwPollEvents();
	        }
	        
	        //Provide statistics
	        Map<String, Object> exitStats = new HashMap<String, Object>();
	        exitStats.put("frames", timer.getFrames());
	        exitStats.put("avgFrameLength", timer.getTime() / timer.getFrames());
	        Game.getLogger().info(String.format("Exiting %s", exitStats));
	        
	        world.delete();
			window.destroy();
			GLFW.glfwTerminate();
			errorCallback.release();
	        
		} catch(Exception e) {

			Game.getLogger().log(Level.SEVERE, "Joshua crashed your program!", e);
			
		}
		
	}
	
	private void initGraphics() {
	    	
		if(GLFW.glfwInit() != GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");
		
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
		
		new SpaceGame().start();
		
	}

}
