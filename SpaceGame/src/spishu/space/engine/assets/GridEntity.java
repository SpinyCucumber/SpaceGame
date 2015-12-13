package spishu.space.engine.assets;

import org.lwjgl.opengl.GL11;

import spishu.space.engine.assets.World.Entity;
import spishu.space.engine.lib.Framebuffer;
import spishu.space.engine.math.AABB;
import spishu.space.engine.math.Rect;
import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2d;

public class GridEntity extends Entity {

	private static Shape tileShape, texShape = Rect.fromAABB(new AABB(Vec2d.ZERO, new Vec2d(1)));
	private static final int proj;
	
	static {
		proj = GL11.glGenLists(1);
		GL11.glNewList(proj, GL11.GL_COMPILE);
		new AABB(Vec2d.ZERO, new Vec2d(1)).glOrtho();
		GL11.glEndList();
	}
	
	public static class Tile { //Placeholder for now
		
		private int value;

		public Tile(int value) {
			this.value = value;
		}
		
	}
	
	private Framebuffer fbo;

	public GridEntity(World world, Vec2d velocity, Vec2d position, float mass, float rotation, float angVelocity, float restitution,
			float friction, Framebuffer fbo) {
		world.super(velocity, position, mass, rotation, angVelocity, restitution, friction);
		this.fbo = fbo;
	}

	public GridEntity(World world, Vec2d velocity, Vec2d position, float mass, float rotation, float angVelocity, float restitution,
			float friction, Animation[] textures, Tile[][] tiles, Framebuffer fbo) {
		world.super(velocity, position, mass, rotation, angVelocity, restitution, friction);
		//Begin rendering to fbo
		this.fbo = fbo;
		int width = tiles[0].length, height = tiles.length;
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glCallList(proj); //Change matrix
		
		fbo.bind(); //Bind framebuffer
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Tile tile = tiles[x][y];
				
				Shape vertices = tileShape.translate(new Vec2d(x,y)), tex = texShape.transform(tile)
			}
		}
		Framebuffer.unbind();
		
		GL11.glPopMatrix(); //Revert matrix
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
	}
	
	
	
}
