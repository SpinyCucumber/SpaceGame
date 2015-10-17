package spishu.space.engine.assets;

import spishu.space.engine.math.Vec2d;

public class CollisionResult {
	
	public Vec2d normal;
	public float depth;
	
	public CollisionResult(Vec2d normal, float depth) {
		this.normal = normal;
		this.depth = depth;
	}
	
}
