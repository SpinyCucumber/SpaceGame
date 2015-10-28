package spishu.space.main;

import spishu.space.engine.lib.Framebuffer;
import spishu.space.engine.math.AABB;
import spishu.space.engine.math.Rect;
import spishu.space.engine.math.Vec2d;

/**
 * Class containing data necessary to create a new ShipEntity.
 * @author SpinyCucumber
 *
 */
public class ShipData {
	
	private static final Vec2d tileDim = new Vec2d(100);
	
	private ShipTile[][] tiles;
	int width, height;
	Framebuffer fbo;
	Vec2d dimensions;
	
	public void renderToBuffer() {
		if(fbo != null) fbo.delete();
		fbo = new Framebuffer(1000, 1000);
		fbo.bind();
		Rect tileShape = Rect.fromAABB(new AABB(Vec2d.ZERO, tileDim));
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				tiles[x][y].texture.bind(0);
				tileShape.translate(new Vec2d(x, y).mulDim(tileDim)).texturedQuad();
			}
		};
		Framebuffer.unbind();
	}
	
	public ShipData(ShipTile[][] tiles) {
		this.tiles = tiles;
		width = tiles.length;
		height = tiles[0].length;
		dimensions = new Vec2d(width, height).mulDim(tileDim);
	}
	
}
