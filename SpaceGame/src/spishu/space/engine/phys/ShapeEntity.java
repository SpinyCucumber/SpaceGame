package spishu.space.engine.phys;

import org.lwjgl.opengl.GL11;

import spishu.space.engine.anim.Animation;
import spishu.space.engine.math.AABB;
import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2;
import spishu.space.engine.phys.World.Entity;

/**
 * A simple implementation of entity with basic interactivity.
 * 
 * @author SpinyCucumber
 *
 * @param <T> The shape subclass.
 * 
 */
public class ShapeEntity<T extends Shape> extends Entity {

	private Animation texture;
	private int list;
	private T bounds;
	private AABB aabb;
	
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
	public ShapeEntity(World world, Vec2 velocity, Vec2 position, float mass, float rotation, float angVelocity,
			float restitution, T bounds, Animation texture) {
		
		world.super(velocity, position, mass, rotation, angVelocity, restitution);
		this.texture = texture;
		this.bounds = bounds;
		
		//Generate AABB for all possible rotations
		float apoth = bounds.vertices[0].length();
		for(int i = 1; i < bounds.vertices.length; i++) {
			float length = bounds.vertices[i].length();
			if(length > apoth) apoth = length;
		}
		Vec2 v = new Vec2(apoth);
		aabb = new AABB(v.negate(), v);
		
		//Generate texcoord shape by scaling bounds down.
		Shape origin = bounds.translate(bounds.min().negate()), texcoordsShapes = origin.divDim(origin.max());
		
		//Generate displaylist
		list = GL11.glGenLists(1);
		GL11.glNewList(list, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_POLYGON);
		for(int i = 0; i < bounds.vertices.length; i++){
			texcoordsShapes.vertices[i].glTexCoord();
			bounds.vertices[i].glVertex();
		}
		GL11.glEnd();
		GL11.glEndList();
		
	}
	
	public T getBounds() {
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
	 * Binds texture, translates, and calls list.
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

