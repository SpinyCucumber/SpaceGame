package spishu.space.engine.entity;

import org.lwjgl.opengl.GL11;

import spishu.space.engine.anim.Animation;
import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2;
import spishu.space.engine.phys.World;
import spishu.space.engine.phys.World.Entity;

public class SingleShapeEntity<T extends Shape> extends Entity<T>{
		
	private Shape texcoordsShapes;
	
	private Animation texture;
	
	public SingleShapeEntity(World world, Vec2 velocity, Vec2 position, float mass,
			T bounds, Animation texture) {
		world.super(velocity, position, mass, bounds);
		this.texture = texture;
		Shape shape = bounds;
		shape = shape.translate(shape.min().negate());
		texcoordsShapes = shape.divDim(shape.max());
	}
	
	@Override
	public void draw(){
		texture.bind();
			GL11.glBegin(GL11.GL_POLYGON);
			for(int i = 0; i < bounds.vertices.length; i++){
				bounds.vertices[i].glVertex();
				texcoordsShapes.vertices[i].glTexCoord();
			}
			GL11.glEnd();
	}
	
	@Override
	public void update(double delta){
		texture.update(delta);
	}
	
}

