package spishu.space.engine.entity;

import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2;
import spishu.space.engine.phys.World;
import spishu.space.engine.phys.World.Entity;

public class ShapeEntity<T extends Shape> extends Entity<T[]>{
		
	private Shape[] texcoordsShapes;
	
	public ShapeEntity(World world, Vec2 velocity, Vec2 position, float mass,
			T[] bounds) {
		world.super(velocity, position, mass, bounds);
		texcoordsShapes = new Shape[bounds.length];
		for(int i = 0; i < bounds.length; i++){
			Shape shape = bounds[i];
			shape.translate(shape.min().negate());
			
		}
	}
	
	@Override
	public void draw
	
}

