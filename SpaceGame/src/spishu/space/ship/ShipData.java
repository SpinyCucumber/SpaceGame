package spishu.space.ship;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import spishu.space.engine.gl.Framebuffer;
import spishu.space.engine.math.AABB;
import spishu.space.engine.math.Rectangle;
import spishu.space.engine.math.Vec2;

/**
 * Class containing data necessary to create a new ShipEntity.
 * @author SpinyCucumber
 *
 */
public class ShipData {
	
	private static final Vec2 tileDim = new Vec2(10);
	
	private ShipTile[][] tiles;
	private int width, height;
	Framebuffer fbo;
	
	public void renderToBuffer() {
		fbo.bind();
		GL11.glBegin(GL11.GL_QUADS);
		Rectangle tileShape = Rectangle.fromAABB(new AABB(Vec2.ZERO, tileDim));
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				tiles[x][y].texture.bind(0);
				tileShape.translate(new Vec2(x, y)).drawVertices();
			}
		}
		GL11.glEnd();
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
	}
	
	public ShipData(ShipTile[][] tiles) {
		this.tiles = tiles;
		width = tiles.length;
		height = tiles[0].length;
	}
	
}
