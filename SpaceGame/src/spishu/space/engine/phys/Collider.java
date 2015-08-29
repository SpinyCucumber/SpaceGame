package spishu.space.engine.phys;

import spishu.space.engine.phys.World.Entity;

public interface Collider {
	
	CollisionResult collide(Entity ent1, Entity ent2);
	
}
