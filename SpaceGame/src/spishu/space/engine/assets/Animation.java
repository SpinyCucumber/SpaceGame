package spishu.space.engine.assets;

import spishu.space.engine.math.Vec2d;

/**
 * Multi-framed  animation, has 2 implementations.
 * @author SpinyCucumber
 *
 */
public interface Animation {
	
	Vec2d getTexCoord(Vec2d texCoord);
	Vec2d getTextureDim();
	void bind();
	void update(double delta);
	Animation clone();
	
}
