package spishu.space.ship;

import spishu.space.engine.lib.Framebuffer;
import spishu.space.engine.math.AABB;
import spishu.space.engine.math.Rectangle;
import spishu.space.engine.math.Vec2;

/**
 * Class containing data necessary to create a new ShipEntity.
 * @author SpinyCucumber
 *
 */
public class ShipData {
	
	private static final Vec2 tileDim = new Vec2(100);
	
	private ShipTile[][] tiles;
	int width, height;
	Framebuffer fbo;
	Vec2 dimensions;
	
	public void renderToBuffer() {
		if(fbo != null) fbo.delete();
		fbo = new Framebuffer(1000, 1000);
		fbo.bind();
		Rectangle tileShape = Rectangle.fromAABB(new AABB(Vec2.ZERO, tileDim));
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				tiles[x][y].texture.bind(0);
				tileShape.translate(new Vec2(x, y).mulDim(tileDim)).texturedQuad();
			}
		};
		Framebuffer.unbind();
	}
	
	public ShipData(ShipTile[][] tiles) {
		this.tiles = tiles;
		width = tiles.length;
		height = tiles[0].length;
		dimensions = new Vec2(width, height).mulDim(tileDim);
	}
	
}
