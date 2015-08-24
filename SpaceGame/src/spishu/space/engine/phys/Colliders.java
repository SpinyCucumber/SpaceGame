package spishu.space.engine.phys;

import spishu.space.engine.phys.World.Entity;

public final class Colliders {
	
	public interface Collider {
		
		CollisionResult collide(Entity<?> ent1, Entity<?> ent2);
		
	}
		
}
