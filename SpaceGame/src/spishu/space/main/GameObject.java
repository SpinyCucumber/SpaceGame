package spishu.space.main;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;

import java.io.File;

import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import spishu.space.engine.anim.Animation;
import spishu.space.engine.anim.TextureLineup;
import spishu.space.engine.gl.Camera;
import spishu.space.engine.gl.Framebuffer;
import spishu.space.engine.gl.GLSLProgram;
import spishu.space.engine.gl.GLWindow;
import spishu.space.engine.gl.Texture;
import spishu.space.engine.math.AABB;
import spishu.space.engine.math.Rectangle;
import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2;
import spishu.space.engine.phys.World;

public class GameObject {
	
	GLFWErrorCallback errorCallback;
	double time, lastTime, timeScale = 0.2;
	
	GLWindow window;
	Framebuffer mainFBO;
	GLSLProgram fboShader, primShader;
	Camera camera;
	World world;
	
	public double getTime() {
    	return glfwGetTime();
    }
    
    public double delta() {
	    time = getTime();
	    double delta = time - lastTime;
    	lastTime = time;
    	return delta;
    }

	public void start() {
		
		try {
			
			System.out.println("Hello LWJGL " + Sys.getVersion() + "!");
			
			glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
	        if ( glfwInit() != GL11.GL_TRUE )
	            throw new IllegalStateException("Unable to initialize GLFW");
	        
	        initGraphics();
	        
	        mainFBO = new Framebuffer(window.getWidth(), window.getWidth());
	        primShader = GLSLProgram.fromVertexFragmentPair("res/shader/prim.vs", "res/shader/prim.fs");
	        fboShader = GLSLProgram.fromVertexFragmentPair("res/shader/fbo.vs", "res/shader/fbo.fs");
	        world = new World(new Vec2(0, 0), 50.0f, 10);
	        camera = new Camera(new Vec2(0, 0), 1, 4000, 0.99f, window);
	        
	        Vec2 d = window.getDimensions().invScale(2);
	        AABB worldOrtho = new AABB(new Vec2(-d.x, d.y), new Vec2(d.x, -d.y).scale(2)), screenOrtho = new AABB(Vec2.ZERO, window.getDimensions());
	        
	        Animation anim = new TextureLineup(0, Texture.fromFile(new File("res/texture/ComputerCraft.png")));
	        
	        new ShapeEntity<Shape>(world, new Vec2(200f, 0), new Vec2(-400, 0),
	        		1, 30, 0, 1, Rectangle.fromDimensions(new Vec2(200)), anim);
	        new ShapeEntity<Shape>(world, new Vec2(0, 0), new Vec2(0, 0),
	        		1, 0, 0, 1, Rectangle.fromDimensions(new Vec2(100)), anim);
			
	        while(!window.shouldClose()) {
	        	
	        	double delta = delta() * timeScale;
	        	world.update(delta);
	        	camera.update(delta);
	        	
	        	mainFBO.bind(); {
	        		
	        		worldOrtho.glOrtho();
	        		primShader.use();
		        	camera.transform();
		        	
		            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		            world.draw();
		            
	        	} Framebuffer.unbind();
	        	
	        	glLoadIdentity();
	        	screenOrtho.glViewport();
	        	screenOrtho.glOrtho();
	        	
	        	fboShader.use();
	        	mainFBO.bindColorTexture();
	        	Rectangle.fromAABB(screenOrtho).texturedQuad();
	        	Texture.unbind();
	        	
	        	window.setTitle("SWAG LEVEL: " + time);
	            window.swapBuffers();
	        	
	        	glfwPollEvents();
	        	
	        }
	        
		} catch(Exception e) {

			e.printStackTrace();
			
		} finally {
			
			world.delete();
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
	    
		glEnable(GL_TEXTURE_2D);
		
	}
	
	public static void main(String[] args) {
		
		new GameObject().start();
		
	}

}
