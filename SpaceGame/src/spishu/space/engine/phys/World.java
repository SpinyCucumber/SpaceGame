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

	public class Entity<T> {
		
		public Vec2 velocity, position;
		public float mass, invMass;
		public T bounds;
		
		public void remove() {
			oldEntities.add(this);
		}
		
		protected void update() {
			position = position.add(velocity);
			velocity = velocity.add(gravity);
		}
		
		protected void draw() {}
		
		public Entity(Vec2 velocity, Vec2 position, float mass, T bounds) {
			this.velocity = velocity;
			this.position = position;
			this.mass = mass;
			invMass = mass == 0 ? 0 : 1 / mass;
			this.bounds = bounds;
			newEntities.add(this);
		}
		
	}
	
	private List<Entity<?>> entities = new ArrayList<Entity<?>>();
	private Deque<Entity<?>> newEntities = new ArrayDeque<Entity<?>>(),
			oldEntities = new ArrayDeque<Entity<?>>();
	
	private Vec2 gravity;
	
	public void update(int delta) {
		entities.removeAll(oldEntities);
		entities.addAll(newEntities);
	}

	public World(Vec2 gravity) {
		this.gravity = gravity;
	}
	
}
