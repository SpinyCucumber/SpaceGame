package spishu.space.engine.assets;

import spishu.space.engine.assets.World.Entity;

public interface Collider {
	
	CollisionResult collide(Entity ent1, Entity ent2);
	
}
