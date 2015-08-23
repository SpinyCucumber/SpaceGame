package spishu.space.engine.phys;

import spishu.space.engine.math.Vec2;

public final class Colliders {
	
	public interface Collider {
		
		CollisionResult collide(Vec2 pos1, Vec2 pos2, Object bounds1, Object bounds2);
		
	}
		
}
