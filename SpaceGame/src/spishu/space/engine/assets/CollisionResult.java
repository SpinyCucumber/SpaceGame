package spishu.space.engine.assets;

import spishu.space.engine.math.Vec2d;

public class CollisionResult {
	
	private Vec2d normal;
	private float depth;
	
	public Vec2d getNormal() {
		return normal;
	}

	public float getDepth() {
		return depth;
	}

	public CollisionResult(Vec2d normal, float depth) {
		this.normal = normal;
		this.depth = depth;
	}
	
}
