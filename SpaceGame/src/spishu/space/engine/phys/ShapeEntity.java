package spishu.space.engine.phys;

import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2;
import spishu.space.engine.phys.World.Entity;

public class ShapeEntity<T extends Shape> extends Entity<T>{

	private Shape texcoords;
	
	public ShapeEntity(World world, Vec2 velocity, Vec2 position, float mass,
			T bounds) {
		world.super(velocity, position, mass, bounds);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void draw

}
