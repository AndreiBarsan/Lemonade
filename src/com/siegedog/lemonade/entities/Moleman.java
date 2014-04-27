package com.siegedog.lemonade.entities;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.siegedog.egglib.AnimatedSprite;
import com.siegedog.egglib.Dude;
import com.siegedog.egglib.EggGame;
import com.siegedog.egglib.physics.AABB;
import com.siegedog.egglib.physics.Shape;

public abstract class Moleman extends Dude {

	public enum State {
		Alive,
		Falling
	}
	
	public static final int FALL_SPEED = 90;
	
	protected int maxHealth;
	protected int curHealth;
	
	private State state;
	private float startY;
	
	public Moleman(AnimatedSprite sprite, Shape shape, Vector2 spriteOffset, int hp) {
		super(sprite, shape);
		curHealth = maxHealth = hp;
		
		state = State.Alive;
		physics.interactive = false;
		
		startY = shape.getY();
	}
	
	public void takeDamage(int amount) {
		curHealth -= amount;
		System.out.println(curHealth);
		if(curHealth <= 0) {
			state = State.Falling;
			clearActions();
			
			// TODO: boom here
			
			addAction(Actions.sequence(
					Actions.delay(0.2f),
					Actions.moveTo(getX(), startY, Math.abs(startY - getY()) / FALL_SPEED, Interpolation.bounceOut),
					new Action() {
						public boolean act(float delta) {
							// TODO: poof here
							return true;
						}
					},
					Actions.delay(1.0f),
					new Action() {
						public boolean act(float delta) {
							// TODO: another boom here
							Moleman.this.kill();
							return true;
						}
					}
			));
		}
	}
	
	public boolean isAttackable() {
		// Can only be attacked if alive
		return state == State.Alive;
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		switch(state) {
		case Alive:
			break;
			
		case Falling:
			
			break;
		}
	}

}
