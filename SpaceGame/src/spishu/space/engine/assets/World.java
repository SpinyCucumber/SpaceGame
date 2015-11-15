package spishu.space.engine.assets;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import spishu.space.engine.game.Game;
import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2d;
import spishu.space.main.ShipEntity;
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
	
	public static void addCollider(Collider collider) {
		colliders.add(collider);
	}
	
	static {
		
		//Add collision detectors
		colliders.add(new Collider() {

			@Override
			public CollisionResult collide(Entity ent1, Entity ent2) {
				ShapeEntity e1 = (ShapeEntity) ent1;
				ShapeEntity e2 = (ShapeEntity) ent2;
				if(!e1.getAABB().translate(e1.position).overlap(e2.getAABB().translate(e2.position))) return null;
				Shape s1 = e1.getBounds().rotate((float) Math.toRadians(e1.rotation)).translate(e1.position);
				Shape s2 = e2.getBounds().rotate((float) Math.toRadians(e2.rotation)).translate(e2.position);
				return s1.checkCollision(s2);
			}
			
		});
		
		colliders.add(new Collider() {

			@Override
			public CollisionResult collide(Entity ent1, Entity ent2) {
				ShipEntity e1 = (ShipEntity) ent1;
				ShipEntity e2 = (ShipEntity) ent2;
				Shape s1 = e1.getBounds().rotate((float) Math.toRadians(e1.rotation)).translate(e1.position);
				Shape s2 = e2.getBounds().rotate((float) Math.toRadians(e2.rotation)).translate(e2.position);
				return s1.checkCollision(s2);
			}
			
		});
		
	}
	
	/**
	 * The base entity class. Has velocity, position, mass, rotation, and restitution.
	 * Floats are used because there may be a large amount of entities.
	 * @author SpinyCucumber
	 *
	 */
	public class Entity {
		
		protected Vec2d velocity, position;
		protected float mass, invMass, rotation, angVelocity, restitution, friction;
		
		public void remove() {
			oldEntities.add(this);
		}
		
		public void spawn() {
			newEntities.add(this);
		}
		
		public Vec2d getVelocity() {
			return velocity;
		}

		public void setVelocity(Vec2d velocity) {
			this.velocity = velocity;
		}

		public Vec2d getPosition() {
			return position;
		}

		public void setPosition(Vec2d position) {
			this.position = position;
		}

		public float getRotation() {
			return rotation;
		}

		public void setRotation(float rotation) {
			this.rotation = rotation;
		}

		public float getAngVelocity() {
			return angVelocity;
		}

		public void setAngVelocity(float angVelocity) {
			this.angVelocity = angVelocity;
		}

		protected void update(double delta) {
			//Move
			position = position.add(velocity.scale((float) delta));
			
			//Apply gravity
			velocity = velocity.add(gravity.scale(mass*(float)delta));
			
			//Modify speed
			if(!velocity.equals(Vec2d.ZERO)) {
				float speed = velocity.length();
				Vec2d dir = velocity.div(speed);
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
		
		public World getWorld() {
			return World.this;
		}
		
		/**
		 * Called when world is drawn; by default does nothing.
		 */
		protected void draw() {}
		
		protected void onCollision(Entity o, CollisionResult result) {}
		
		@Override
		public String toString() {
			return "Entity [velocity=" + velocity + ", position=" + position
					+ ", mass=" + mass + ", rotation=" + rotation
					+ ", angVelocity=" + angVelocity + ", restitution="
					+ restitution + "]";
		}

		public Entity(Vec2d velocity, Vec2d position, float mass, float rotation, float angVelocity, float restitution, float friction) {
			this.velocity = velocity;
			this.position = position;
			this.mass = mass;
			this.rotation = rotation;
			this.angVelocity = angVelocity;
			this.restitution = restitution;
			this.friction = friction;
			invMass = mass == 0 ? 0 : 1 / mass;
		}
		
	}
	
	private List<Entity> entities = new ArrayList<Entity>();
	private Deque<Entity> newEntities = new ArrayDeque<Entity>(),
			oldEntities = new ArrayDeque<Entity>();
	
	private Vec2d gravity;
	private float slowdown, angSlowdown;
	
	/**
	 * Updates entities, applies physics, and removes old entities.
	 * @param delta
	 */
	public void update(double delta) {
		
		for(Entity entity : newEntities) Game.log(Level.FINE, "New entity: %s", entity);
		entities.removeAll(oldEntities);
		entities.addAll(newEntities);
			
		oldEntities.clear();
		newEntities.clear();
		
		for(Entity ent : entities) ent.update(delta);
		for(int i1 = 0; i1 < entities.size(); i1++) { //Iterate over all pairs of entities
			Entity e1 = entities.get(i1);
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
					Game.log(Level.FINE, "Collision between entity %s and entity %s %s", e1, e2, result);
					resolveCollision(result, e1, e2);
				}
			}
		}
		
	}
	
	/**
	 * Renders the world
	 */
	public void draw() {
		for(Entity entity : entities) entity.draw();
	}
	
	/**
	 * Calls entities' remove method.
	 */
	public void delete() {
		for(Entity entity : entities) entity.remove();
	}
	
	/**
	 * @param gravity Vector added to entities' velocities each frame
	 * @param slowdown Amount subtracted from entities' speed each frame
	 * @param angSlowdown Slowdown of angular speed
	 */
	public World(Vec2d gravity, float slowdown, float angSlowdown) {
		this.gravity = gravity;
		this.slowdown = slowdown;
		this.angSlowdown = angSlowdown;
	}	

	@Override
	public String toString() {
		return "World [gravity=" + gravity + ", slowdown=" + slowdown
				+ ", angSlowdown=" + angSlowdown + "]";
	}

	/**
	 * Newton's equations implemented to the best of my ability.
	 * 
	 * @param result The collision normal and depth
	 * @param e1 Entity 1
	 * @param e2 Entity 2
	 */
	public static void resolveCollision(CollisionResult result, Entity e1, Entity e2) {
		
		Vec2d rv = e2.velocity.sub(e1.velocity);
		float r = rv.dot(result.getNormal()); //The relative velocity, along the normal.
		
		if(r > 0) return; //Don't do anything if entities are travelling away from eachother.
		e1.onCollision(e2, result);
		e2.onCollision(e1, result);
		
		float t = (e1.invMass+e2.invMass);
		float e = Math.min(e1.restitution, e2.restitution); //Get restitution coefficient. Basically the bounciness.
		float j = ((1 + e) * -r)/t; //Get impulse scalar... the impulse is the change in momemtum.
		float j1 = -j*e1.invMass, j2 = j*e2.invMass;

		e1.velocity = e1.velocity.add(result.getNormal().scale(j1)); //Get parts and apply impulse.
		e2.velocity = e2.velocity.add(result.getNormal().scale(j2));
		
		// Re-calculate relative velocity after normal impulse
		rv = e2.velocity.sub(e1.velocity);
		 
		// Solve for the tangent vector
		Vec2d tangent = result.getNormal().perp().scale(rv.dot(result.getNormal()) > 0 ? 1 : -1);
		 
		// Solve for magnitude to apply along the friction vector
		j = -rv.dot(tangent)/t;
		j *= (e1.friction+e2.friction)/2;
		j1 = -j*e1.invMass;
		j2 = j*e2.invMass;
		
		e1.velocity = e1.velocity.add(tangent.scale(j1)); //Get parts and apply impulse.
		e2.velocity = e2.velocity.add(tangent.scale(j2));
		
		j = result.getDepth()/t;
		j1 = -j*e1.invMass;
		j2 = j*e2.invMass;
		
		e1.position = e1.position.add(result.getNormal().scale(j1));
		e2.position = e2.position.add(result.getNormal().scale(j2));
		
	}
	
}
