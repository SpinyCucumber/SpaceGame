package spishu.space.engine.assets;

import org.lwjgl.opengl.GL11;

import spishu.space.engine.assets.World.Entity;
import spishu.space.engine.math.AABB;
import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2d;

/**
 * A simple implementation of entity with basic interactivity.
 * 
 * @author SpinyCucumber
 *
 * @param <T> The shape subclass.
 * 
 */
public class ShapeEntity extends Entity {

	private Animation texture;
	private int list;
	protected Shape bounds;
	private AABB aabb;
	
	
	
	public ShapeEntity(World world, Vec2d velocity, Vec2d position, float mass, float rotation, float angVelocity, float restitution,
			Animation texture, int list, Shape bounds, AABB aabb) {
		world.super(velocity, position, mass, rotation, angVelocity, restitution);
		this.texture = texture;
		this.list = list;
		this.bounds = bounds;
		this.aabb = aabb;
	}
	
	//TODO add more copy methods
	public ShapeEntity copy(ShapeEntity o) {
		return new ShapeEntity(o.getWorld(), o.velocity, o.position, o.mass, o.rotation, o.angVelocity, o.restitution,
				o.texture.copy(), o.list, o.bounds.copy(), o.aabb);
	}

	/**
	 * Creates ShapeEntity and adds it to world. Must call world.update() to register.
	 * @param world World to add to
	 * @param velocity
	 * @param position
	 * @param mass
	 * @param rotation
	 * @param angVelocity
	 * @param restitution
	 * @param bounds
	 * @param texture
	 */
	public ShapeEntity(World world, Vec2d velocity, Vec2d position, float mass, float rotation, float angVelocity,
			float restitution, Shape bounds, Animation texture) {
		
		world.super(velocity, position, mass, rotation, angVelocity, restitution);
		this.texture = texture;
		this.bounds = bounds;
		
		//Generate AABB for all possible rotations
		float apoth = bounds.getVertices().get(0).length();
		for(Vec2d vertex : bounds.getVertices()) {
			float length = vertex.length();
			if(length > apoth) apoth = length;
		}
		Vec2d v = new Vec2d(apoth);
		aabb = new AABB(v.negate(), v);
		
		//Generate texcoord shape by scaling bounds down.
		Shape origin = bounds.translate(bounds.min().negate()), texcoordShapes = origin.divDim(origin.max());
		
		//Generate displaylist
		list = GL11.glGenLists(1);
		GL11.glNewList(list, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_POLYGON);
		Shape.draw(origin, texcoordShapes);
		GL11.glEnd();
		GL11.glEndList();
		
	}
	
	public Shape getBounds() {
		return bounds;
	}

	public AABB getAABB() {
		return aabb;
	}
	
	@Override
	public void remove() {
		super.remove();
		GL11.glDeleteLists(list, 1);
	}

	@Override
	public String toString() {
		return "ShapeEntity [texture=" + texture + ", list=" + list
				+ ", bounds=" + bounds + ", velocity=" + velocity
				+ ", position=" + position + ", mass=" + mass + ", rotation="
				+ rotation + ", angVelocity=" + angVelocity + ", restitution="
				+ restitution + "]";
	}

	@Override
	/**
	 * Binds texture, transforms matrix, and calls list.
	 */
	public void draw(){
		
		GL11.glPushMatrix();
		
		position.glTranslate();
		//Rectangle.fromAABB(aabb).glLineLoop(); //For debug purposes. Will probably be controlled through some static field.
		texture.bind();
		GL11.glRotatef(rotation, 0, 0, 1);
		GL11.glCallList(list);
		
		GL11.glPopMatrix();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
	}
	
	@Override
	public void update(double delta) {
		super.update(delta);
		texture.update(delta);
	}
	
}

