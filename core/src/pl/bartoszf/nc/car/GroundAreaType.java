package pl.bartoszf.nc.car;

public class GroundAreaType extends FixtureUserData {

	public float frictionModifier;
	boolean outOfCourse;
	
	public GroundAreaType(float frictionModifier, boolean outOfCourse) {
		super(FixtureUserDataType.FUD_GROUND_AREA);
		
		this.frictionModifier = frictionModifier;
		this.outOfCourse = outOfCourse;
	}

}
