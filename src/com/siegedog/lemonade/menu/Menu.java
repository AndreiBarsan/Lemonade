package com.siegedog.lemonade.menu;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;

public class Menu extends InputAdapter {
	public List<MenuEntry> entries;
	
	private int index = 0;
	private BitmapFont font;
	private int entryHeight;
	private int width;

	private boolean active = false;
	
	/**
	 * Entry 0 is selected by default.
	 */
	public Menu(Vector2 position, BitmapFont font, int width, int entryHeight, List<MenuEntry> entries) {
		this.entries = entries;
		this.font = font;
		this.width = width;
		this.entryHeight = entryHeight;
		
		Vector2 cpos = new Vector2(position);
		
		for(MenuEntry me : entries) {
			me.init(font, new Vector2(cpos), width, 0.5f, 0.5f);
			cpos.y -= entryHeight;
		}
		
		entries.get(0).selected();
	}

	@Override
	public synchronized boolean keyUp(int keycode) {
		//System.out.println("keyUP: active = " + active + "; thread = " + Thread.currentThread() + " this = " + this);
		if(! active) {
			System.out.println("Menu inactive; dropping event");
			return false;
		}
		
		System.out.println("Handling event");
		
		switch(keycode) {
		case Keys.UP:
			entries.get(index).deselected();
			
			index -= 1;
			if(index < 0) {
				index = entries.size() - 1;
			}
			
			entries.get(index).selected();
			
			return true;
			
		case Keys.DOWN:
			entries.get(index).deselected();	
			
			index += 1;
			if(index >= entries.size()) {
				index = 0;
			}
			
			entries.get(index).selected();
			
			return true;
			
		case Keys.A:
		case Keys.SPACE:
		case Keys.ENTER:
			entries.get(index).activated();
			return true;
		}
		
		return false;			
	}
	
	protected void show() {
		for(MenuEntry me : entries) {
			me.show();
		}
	}
	 
	protected void hide() {
		for(MenuEntry me : entries) {
			me.hide();
		}
	}
	
	public synchronized void activate() {
		active = true;
		Gdx.input.setInputProcessor(this);
		show();
	}
	
	public synchronized void deactivate() {
		//System.out.println("Deactivated menu: Thread = " + Thread.currentThread() + " this = " + this);
		active = false;
		// System.out.println(active);
		Gdx.input.setInputProcessor(null);
		hide();
	}
}