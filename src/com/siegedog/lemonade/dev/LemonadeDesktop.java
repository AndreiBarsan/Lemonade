package com.siegedog.lemonade.dev;

import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;
import com.badlogic.gdx.utils.BufferUtils;
import com.siegedog.egglib.Resources;
import com.siegedog.lemonade.LemonadeGame;

public class LemonadeDesktop {
	
	static org.lwjgl.input.Cursor emptyCursor;
	
	/**
	 * Copyright github.com/mattdesl
	 */
	public static void setHWCursorVisible(boolean visible) throws LWJGLException {
		if (Gdx.app.getType() != ApplicationType.Desktop && Gdx.app instanceof LwjglApplication) {
			return;
		}
		if (emptyCursor == null) {
			if (Mouse.isCreated()) {
				int min = org.lwjgl.input.Cursor.getMinCursorSize();
				IntBuffer tmp = BufferUtils.newIntBuffer(min * min);
				emptyCursor = new org.lwjgl.input.Cursor(min, min, min / 2, min / 2, 1, tmp, null);
			} else {
				throw new LWJGLException("Could not create empty cursor before Mouse object is created");
			}
		}
		if (Mouse.isInsideWindow()) {
			Mouse.setNativeCursor(visible ? null : emptyCursor);
		}
	}
	
	public static void main (String[] args) throws LWJGLException {
		try {
			System.out.println("Packing stuff...");
			Settings settings = new TexturePacker2.Settings();
			settings.maxWidth = 4096;
			settings.maxHeight = 4096;
			TexturePacker2.processIfModified(settings, Resources.texRoot, Resources.texRoot + "atlas/", "pack.atlas");
		} catch (Exception e) {
			System.out.println("Horrible error when packing resources...");
			e.printStackTrace();
			
			System.out.println("Still soldiering on!");
		}
		
    	LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    	config.fullscreen = false;
    	config.resizable = false;
    	config.width = 1024;
    	config.height = 768;
    	config.samples = 1;
    	config.title = "LD29 - Project Lemonade";
    	config.useGL20 = false;
        new LwjglApplication(new LemonadeGame(), config);
    }
}
