package bouncing_balls;

/**
 * The physics model.
 * 
 * This class is where you should implement your bouncing balls model.
 * 
 * The code has intentionally been kept as simple as possible, but if you wish, you can improve the design.
 * 
 * @author Simon Robillard
 *
 */

import java.util.Random;
import java.util.Scanner;
import java.io.IOException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

class Model {

	double areaWidth, areaHeight;
	
	Ball [] balls;

	double ballsDistance;

	double b1Rx;
	double b2Rx;

	double b1Ry;
	double b2Ry;

	double b1Rvx;
	double b2Rvx;

	double b1Rvy;
	double b2Rvy;

	double b1Rr;
	double b2Rr;

	boolean ballColliding;

	int frame;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;



		b1Ry = randNum();
		b2Ry = randNum();

		b1Rvx = randNum();
		b2Rvx = randNum();

		b1Rvy = randNum();
		b2Rvy = randNum();

		b1Rr = randNum();
		b2Rr = randNum();

		// Initialize the model with a few balls
		

		balls = new Ball[2];

		Scanner keyboard = new Scanner(System.in);
		System.out.println("1: pre");
		System.out.println("else: random");
		int myint = keyboard.nextInt();
		
		if(myint == 1){
			balls[0] = new Ball(width / 3, height * 0.7, 0.7, 0.6, 0.3);
			balls[1] = new Ball(2 * width / 3, height * 0.7, -0.8, 0.6, 0.3);
		}else{


		//--------------------------RANDOM PARAMETERS---------------------------

		balls[0] = new Ball(width / 3, height * b1Ry, b1Rvx, b1Rvy*b1Rr, b1Rr);
		balls[1] = new Ball(2 * width / 3, height * b2Ry, -1*b2Rvx, b2Rvy*b2Rr, b2Rr/2);
		
		//--------------------------------------------------------------------
		
		}
		

		this.ballColliding = false;

		this.frame = 0;
	

	}

	void step(double deltaT) {
		// TODO this method implements one step of simulation with a step deltaT

		this.frame++;

		if((checkNOCollisionBalls())){		
			this.ballColliding = false;	
		}
		
		int i = 0;
		for (Ball b : balls) {
			
			// detect collision with the border
			
			if((b.vx < 0 && b.x < b.radius) || (b.vx > 0 && b.x > areaWidth - b.radius)){
				b.vx *= -1; // change direction of ball
				b.ballOut = true;
			}		
				
			if((b.vy <= 0 && b.y <= b.radius) || (b.vy >= 0 && b.y >= areaHeight - b.radius)){
				b.vy *= -1; // change direction of ball
				b.ballOut = true;
				if(b.y <= b.radius){
					b.y = b.radius;	
				}
				
			}
				
			b.x += deltaT * b.vx;	
			b.vy = gravityChange(deltaT, b.vy, b.y);		
			b.y += deltaT * b.vy;
			

			// compute new position according to the speed of the ball
			

			
			int next = (i+1)%2;
			calcDistance();
			
			System.out.println("------------"+"Frame : "+this.frame+"----------------");
			System.out.println("BallsTouching: " + checkCollisionBalls());
			System.out.println("BallsNotInside: "+ !this.ballColliding);
			System.out.println("BallsCollison: " + (!this.ballColliding && checkCollisionBalls()));

			if(checkCollisionBalls() && !this.ballColliding){

				this.ballColliding = true;
				momentumFunc(i,next,deltaT);

			}
			

		
		}


	}
	
	
	private boolean checkCollisionBalls(){

		double deltaX = balls[0].x-balls[1].x;
		double deltaY = balls[0].y-balls[1].y;

		double collisionDistance = balls[0].radius+balls[1].radius;
		 

		return (deltaX*deltaX + deltaY*deltaY  <= collisionDistance*collisionDistance);	//ballsDistance <= balls[0].radius+balls[1].radius	 
	}

	private boolean checkNOCollisionBalls(){

		double deltaX = balls[0].x-balls[1].x;
		double deltaY = balls[0].y-balls[1].y;

		double collisionDistance = balls[0].radius+balls[1].radius;
		 

		return (deltaX*deltaX + deltaY*deltaY  > collisionDistance*collisionDistance);	//  ballsDistance >= balls[0].radius+balls[1].radius

	}

	private void momentumFunc(int bActive, int bNext, double deltaT){

		double u1 = balls[bActive].vx;
		double u2 = balls[bNext].vx;

		double m1 = balls[bActive].mass;
		double m2 = balls[bNext].mass;
		
		double deltaX = (balls[bActive].x - balls[bNext].x);
		double deltaY = (balls[bActive].y - balls[bNext].y);
		
		double rotAngle = Math.atan(deltaY/deltaX);

		balls[bActive].rotate(-1*rotAngle);
		balls[bNext].rotate(-1*rotAngle);

 		double I = balls[bActive].mass*balls[bActive].vx+balls[bNext].mass*balls[bNext].vx;
  		double R = -(balls[bNext].vx-balls[bActive].vx);    

  		balls[bActive].vx = (I-R*balls[bNext].mass) / (balls[bActive].mass+balls[bNext].mass);
  		balls[bNext].vx = R+balls[bActive].vx;

		//System.out.println((m1*balls[bActive].vx*balls[bActive].vx)/2 + (m2*balls[bNext].vx*balls[bNext].vx)/2 == (m1*u1*u1)/2 + (m2*u2*u2)/2);

		balls[bActive].rotate(rotAngle);
		balls[bNext].rotate(rotAngle);



	}

	private void calcDistance(){

		double deltaX = Math.abs(balls[0].x - balls[1].x);
		double deltaY = Math.abs(balls[0].y - balls[1].y); 

		if(deltaY == 0){						//maybe not needed because the last case takes care of it, and two doubles might never become 0.

			this.ballsDistance = deltaX;

		}else if(deltaX == 0){					//maybe not needed because the last case takes care of it, and two doubles might never become 0.

			this.ballsDistance = deltaY;

		}else{

			this.ballsDistance = Math.sqrt((deltaX*deltaX)+(deltaY*deltaY));	
		}
		 
	}
	
	private double gravityChange(double deltaT, double vy, double y){
		
			double vnew1 = vy - (9.82*deltaT*y);
			return vnew1;
				
	}


	private static double randNum(){
    	double rangeMin = 0.1f;
    	double rangeMax = 0.5f;
    	Random r = new Random();
    	double createdRanNum = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    	return(createdRanNum);
	}

	/**
	 * Simple inner class describing balls.
	 */
	class Ball {
		
		Ball(double x, double y, double vx, double vy, double r) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.radius = r;
			this.mass = r/100;
			this.ballOut = false;
			
			this.r = Math.sqrt(this.vx*this.vx+this.vy*this.vy);
			this.angle = Math.atan(this.vy/this.vx);
		}

		/**
		 * Position, speed, and radius of the ball. You may wish to add other attributes.
		 */
		double x, y, vx, vy, radius, mass, r, angle;
		boolean ballOut;

		private void rectToPolar() {
			this.r = Math.sqrt(this.vx*this.vx+this.vy*this.vy);
			this.angle = Math.atan(this.vy/this.vx);
			if(this.vx<0){
				this.angle += Math.PI;
			} 
		}
	
		private void polarToRect() {
			this.vx = this.r*Math.cos(this.angle);
			this.vy = this.r*Math.sin(this.angle);
		}
	
		private void rotate(double rotateAngle) {
			rectToPolar();
			this.angle += rotateAngle;
			polarToRect();
		}
	}
	
}
