package spishu.space.engine.phys;

import spishu.space.engine.entity.ShapeEntity;
import spishu.space.engine.phys.World.Collider;
import spishu.space.engine.phys.World.Entity;


public final class Colliders {
		
	static {
		
		World.getColliders().add(new Collider() {

			@Override
			public CollisionResult collide(Entity ent1, Entity ent2) {
				ShapeEntity<?> s1 = (ShapeEntity<?>) ent1;
				ShapeEntity<?> s2 = (ShapeEntity<?>) ent2;
				return s1.getBounds().checkCollision(s2.getBounds());
			}
			
		});
		
	}
	
}
