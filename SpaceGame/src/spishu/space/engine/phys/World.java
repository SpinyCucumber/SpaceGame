package spishu.space.engine.phys;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spishu.space.engine.math.Vec2;
import spishu.space.engine.phys.Colliders.Collider;

public class World {;
	
	private static Set<Collider> colliders = new HashSet<Collider>();
	
	public static Set<Collider> getColliders() {
		return colliders;
	}

	public class Entity {
		
		public Vec2 velocity, position;
		public float mass, invMass, rotation, angVelocity;
		
		public void remove() {
			oldEntities.add(this);
		}
		
		protected void update(double delta) {
			position = position.add(velocity.scale((float) delta));
			velocity = velocity.add(gravity.scale((float) delta));
			rotation += angVelocity;
		}
		
		protected void draw() {}
		
		public Entity(Vec2 velocity, Vec2 position, float mass, float rotation, float angVelocity) {
			this.velocity = velocity;
			this.position = position;
			this.mass = mass;
			this.rotation = rotation;
			this.angVelocity = angVelocity;
			invMass = mass == 0 ? 0 : 1 / mass;
			newEntities.add(this);
		}
		
	}
	
	private List<Entity> entities = new ArrayList<Entity>();
	private Deque<Entity> newEntities = new ArrayDeque<Entity>(),
			oldEntities = new ArrayDeque<Entity>();
	
	private Vec2 gravity;
	
	public void update(double d) {
		entities.removeAll(oldEntities);
		entities.addAll(newEntities);
		for(Entity entity : entities) entity.update(d);
	}

	public void draw() {
		for(Entity entity : entities) entity.draw();
	}

	public World(Vec2 gravity) {
		this.gravity = gravity;
	}
	
}
