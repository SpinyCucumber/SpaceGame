package spishu.space.main;

import org.lwjgl.opengl.GL11;

import spishu.space.engine.assets.Animation;
import spishu.space.engine.assets.Camera2d;
import spishu.space.engine.assets.World;
import spishu.space.engine.game.Game;
import spishu.space.engine.game.GameObject;
import spishu.space.engine.lib.Framebuffer;
import spishu.space.engine.lib.GLSLProgram;
import spishu.space.engine.math.AABB;
import spishu.space.engine.math.Rectangle;
import spishu.space.engine.math.Vec2;
import spishu.space.ship.ShipData;
import spishu.space.ship.ShipEntity;
import spishu.space.ship.ShipTile;

/**
 * Main game class, representing an instance of the game.
 * @author SpinyCucumber
 *
 */
public class SpaceGame extends GameObject {

	World world;
	Camera2d camera;
	Framebuffer mainFBO;
	GLSLProgram primShader, fboShader;
	AABB worldOrtho, screenOrtho;
	
	private boolean useShaders;
	
	public static void main(String[] args) {
		
		new SpaceGame().start();
		
	}

	@Override
	protected void mainLoop(double delta) throws Exception {
		
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
    	mainFBO.getColorTex().bind();
    	Rectangle.fromAABB(screenOrtho).texturedQuad();
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    	
    	window.setTitle("SWAG LEVEL: " + timer.getTime());
    	
	}

	@Override
	protected void onExit() {
		world.delete();
		primShader.delete();
		fboShader.delete();
		mainFBO.delete();
	}

	@Override
	protected void onStart() throws Exception {
		
		//Load resources
		Game.useDefaultLoaders();
        Game.setSource(getClass());
        Game.loadResources();
        
        //Generate objects
        mainFBO = new Framebuffer(window.getWidth(), window.getWidth());
        primShader = (GLSLProgram) Game.getResource("shader%sprim.glsl");
        fboShader = (GLSLProgram) Game.getResource("shader%sfbo.glsl");
        camera = new Camera2d(new Vec2(0, 0), 1, (double) config.get("cameraMoveSpeed"), (double) config.get("cameraZoomSpeed"), window);
        useShaders = (Boolean) config.get("useShaders");
        world = new World(new Vec2(0, 0), 50.0f, 10);
        
        //Log junk
        Game.info("Initialized world %s", world);
        Game.info("Initialized camera %s", camera);
        Game.info("Using %d collision detectors", World.getColliders().size());
        
        //Get dimensions to be used in glOrtho
        Vec2 d = window.getDim().invScale(2);
        worldOrtho = new AABB(new Vec2(-d.x, d.y), new Vec2(d.x, -d.y));
        screenOrtho = new AABB(Vec2.ZERO, window.getDim());
        
        //Add entities. Testing ships.
        ShipTile tile = new ShipTile((Animation) Game.getResource("texture%stest.anim"));
        ShipData data = new ShipData(new ShipTile[][]{{tile, tile},{tile, tile}});
        data.renderToBuffer();
        new ShipEntity(data, world, new Vec2(0, 0), new Vec2(0, 0), 1, 0, 0, 1);
		
	}

}
