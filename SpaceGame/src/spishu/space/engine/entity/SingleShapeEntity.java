package spishu.space.engine.entity;

import org.lwjgl.opengl.GL11;

import spishu.space.engine.anim.Animation;
import spishu.space.engine.gl.DisplayList;
import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2;
import spishu.space.engine.phys.World;
import spishu.space.engine.phys.World.Entity;

public class SingleShapeEntity<T extends Shape> extends Entity { //Simple implementation of entity

	private Animation texture;
	private DisplayList list;
	private T bounds;
	
	public SingleShapeEntity(World world, Vec2 velocity, Vec2 position, float mass, float rotation, float angVelocity,
			T bounds, Animation texture) {
		world.super(velocity, position, mass, rotation, angVelocity);
		this.texture = texture;
		this.bounds = bounds;
		Shape texcoordsShapes = bounds.translate(bounds.min().negate()).divDim(bounds.max());
		list = new DisplayList();
		GL11.glBegin(GL11.GL_POLYGON);
		for(int i = 0; i < bounds.vertices.length; i++){
			texcoordsShapes.vertices[i].glTexCoord();
			bounds.vertices[i].glVertex();
		}
		GL11.glEnd();
		DisplayList.end();
	}
	
	public T getBounds() {
		return bounds;
	}

	@Override
	public void draw(){
		texture.bind();
		position.glTranslate();
		GL11.glRotatef(rotation, 0, 0, 1);
		list.call();
		GL11.glLoadIdentity();
	}
	
	@Override
	public void update(double delta) {
		super.update(delta);
		texture.update(delta);
	}
	
}

