package spishu.space.engine.assets;

import spishu.space.engine.math.Vec2d;

/**
 * Multi-framed  animation, has 2 implementations.
 * @author SpinyCucumber
 *
 */
public interface Animation {

	Vec2d getTextureDim();
	void bind();
	Animation clone();
	
	default void update(double delta) {}
	default Vec2d getTexCoord(Vec2d texCoord) { return texCoord; }
	
}
