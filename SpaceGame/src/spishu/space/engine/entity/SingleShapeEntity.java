package spishu.space.engine.entity;

import org.lwjgl.opengl.GL11;

import spishu.space.engine.anim.Animation;
import spishu.space.engine.gl.DisplayList;
import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2;
import spishu.space.engine.phys.World;
import spishu.space.engine.phys.World.Entity;

public class SingleShapeEntity<T extends Shape> extends Entity<T> { //Simple implementation of entity
		
	private Shape texcoordsShapes;
	private Animation texture;
	private DisplayList list;
	
	public SingleShapeEntity(World world, Vec2 velocity, Vec2 position, float mass,
			T bounds, Animation texture) {
		world.super(velocity, position, mass, bounds);
		this.texture = texture;
		Shape shape = bounds;
		shape = shape.translate(shape.min().negate());
		texcoordsShapes = shape.divDim(shape.max());
		genList();
	}
	
	private void genList() {
		list = new DisplayList();
		GL11.glBegin(GL11.GL_POLYGON);
		for(int i = 0; i < bounds.vertices.length; i++){
			texcoordsShapes.vertices[i].glTexCoord();
			bounds.vertices[i].glVertex();
		}
		GL11.glEnd();
		DisplayList.end();
	}
	
	@Override
	public void draw(){
		texture.bind();
		GL11.glRotatef(rotation, 0, 0, 1);
		position.glTranslate();
		list.call();
		GL11.glLoadIdentity();
	}
	
	@Override
	public void update(double delta) {
		super.update(delta);
		texture.update(delta);
	}
	
}

