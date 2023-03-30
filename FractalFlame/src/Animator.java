
public class Animator {
	private double velocity, acceleration, initial;
	final static double MAX_INITIAL_ACCELERATION = 0.00005;
	
	
	public Animator(double initialAcceleration, double initialVelocity) {
		acceleration = initialAcceleration;
		velocity = initialVelocity;
		initial = initialAcceleration;
	}
	
	public Animator(double initialAcceleration) {
		this(initialAcceleration,0);
	}

	
	public Animator() {
		this(Math.random()*MAX_INITIAL_ACCELERATION);
		initial = MAX_INITIAL_ACCELERATION;
	}
	
	public double animate(double lastValue) {
		velocity+=acceleration;
		double MAX_ACCELERATION_CHANGE = acceleration/10;
		acceleration+=(2*MAX_ACCELERATION_CHANGE)*(Math.random()-0.5);
		return (lastValue+velocity);
	}
	
	public void reverse() {
		acceleration = -acceleration*Math.random();
		velocity = -velocity;
	}
}
