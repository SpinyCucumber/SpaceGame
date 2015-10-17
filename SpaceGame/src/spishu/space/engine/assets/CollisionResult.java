package spishu.space.engine.assets;

import spishu.space.engine.math.Vec2;

public class CollisionResult {
	
	public Vec2 normal;
	public float depth;
	
	public CollisionResult(Vec2 normal, float depth) {
		this.normal = normal;
		this.depth = depth;
	}
	
}
