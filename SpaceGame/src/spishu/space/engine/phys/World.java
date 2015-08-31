package spishu.space.engine.phys;

import java.util.ArrayDeque;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spishu.space.engine.entity.ShapeEntity;
import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2;
/**
 * A container that lets entities interact with eachother.
 * Also has gravity and sort-of air-resistance.
 * Uses a static list of 'colliders' to detect collisions.
 * 
 * @author SpinyCucumber
 *
 */
public class World {;
	
	private static Set<Collider> colliders = new HashSet<Collider>();
	
	public static Set<Collider> getColliders() {
		return colliders;
	}
	
	static {
		
		colliders.add(new Collider() {

			@Override
			public CollisionResult collide(Entity ent1, Entity ent2) {
				ShapeEntity<?> e1 = (ShapeEntity<?>) ent1;
				ShapeEntity<?> e2 = (ShapeEntity<?>) ent2;
				if(!e1.getAABB().translateAABB(e1.position).overlapAABB(e2.getAABB().translateAABB(e2.position))) return null;
				Shape s1 = e1.getBounds().rotate((float) Math.toRadians(e1.rotation)).translate(e1.position);
				Shape s2 = e2.getBounds().rotate((float) Math.toRadians(e2.rotation)).translate(e2.position);
				return s1.checkCollision(s2);
			}
			
		});
		
	}
	
	/**
	 * The base entity class. Has velocity, position, mass, rotation, and restitution.
	 * 
	 * @author SpinyCucumber
	 *
	 */
	public class Entity {
		
		public Vec2 velocity, position;
		public float mass, invMass, rotation, angVelocity, restitution;
		
		public void remove() {
			oldEntities.add(this);
		}
		
		protected void update(double delta) {
			//Move
			position = position.add(velocity.scale((float) delta));
			
			//Apply gravity
			velocity = velocity.add(gravity.scale(mass));
			
			//Modify speed
			if(!velocity.equals(Vec2.ZERO)) {
				float speed = velocity.length();
				Vec2 dir = velocity.invScale(speed);
				speed = Math.max(0, speed - slowdown * (float) delta);
				velocity = dir.scale(speed);
			}
			
			//Rotate
			rotation += angVelocity;
			
			//Modify angular speed
			if(angVelocity != 0) {
				float speedAng = Math.abs(angVelocity);
				int dirAng = (int) (angVelocity / speedAng);
				speedAng = Math.max(0, speedAng - angSlowdown * (float) delta);
				angVelocity = dirAng * speedAng;
			}
			
		}
		
		protected void draw() {}
		
		public Entity(Vec2 velocity, Vec2 position, float mass, float rotation, float angVelocity, float restitution) {
			this.velocity = velocity;
			this.position = position;
			this.mass = mass;
			this.rotation = rotation;
			this.angVelocity = angVelocity;
			this.restitution = restitution;
			invMass = mass == 0 ? 0 : 1 / mass;
			newEntities.add(this);
		}
		
	}
	
	private List<Entity> entities = new ArrayList<Entity>();
	private Deque<Entity> newEntities = new ArrayDeque<Entity>(),
			oldEntities = new ArrayDeque<Entity>();
	
	private Vec2 gravity;
	private float slowdown, angSlowdown;
	
	public void update(double delta) {
		
		entities.removeAll(oldEntities);
		entities.addAll(newEntities);
		oldEntities.clear();
		newEntities.clear();
		
		for(int i1 = 0; i1 < entities.size(); i1++) { //Iterate over all pairs of entities
			Entity e1 = entities.get(i1);
			e1.update(delta); //Call entity's update method
			for(int i2 = i1 + 1; i2 < entities.size(); i2++) {
				Entity e2 = entities.get(i2);
				for(Collider collider : colliders) { //Check to see if we can detect a collision
					CollisionResult result;
					try {
						result = collider.collide(e1, e2);
					} catch(ClassCastException e) {
						continue; //This isnt the collider we're looking for
					}
					if(result == null) continue;
					resolveCollision(result, e1, e2);
				}
			}
		}
		
	}

	public void draw() {
		for(Entity entity : entities) entity.draw();
	}
	
	/**
	 * Calls entities' remove method.
	 */
	public void delete() {
		for(Entity entity : entities) entity.remove();
	}

	public World(Vec2 gravity, float slowdown) {
		this.gravity = gravity;
		this.slowdown = slowdown;
		this.angSlowdown = slowdown;
	}	
	
	/**
	 * Newton's equations implemented to the best of my ability.
	 * 
	 * @param result The collision normal and depth
	 * @param e1 Entity 1
	 * @param e2 Entity 2
	 */
	public static void resolveCollision(CollisionResult result, Entity e1, Entity e2) {
		
		float rv = e2.velocity.sub(e1.velocity).dot(result.normal); //The difference in velocities, along the normal.
		
		if(rv > 0) return; //Don't do anything if entities are travelling away from eachother.
		
		float e = Math.min(e1.restitution, e2.restitution); //Get restitution coefficient. Basically the bounciness.
		float j = -(1 + e) * rv; //Get impulse scalar... the impulse is the change in momemtum. Most complicated step.
		j /= e1.invMass + e2.invMass; //Divide by total mass, to yield a ratio.
		
		Vec2 impulse = result.normal.scale(j); //Scale normal to get impulse vector
		e1.velocity = e1.velocity.sub(impulse.scale(e1.invMass)); //Get parts and apply impulse.
		e2.velocity = e2.velocity.add(impulse.scale(e2.invMass));
		
	}
	
}
