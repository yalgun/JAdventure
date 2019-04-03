package com.jadventure.game.entities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.jadventure.game.DeathException;
import com.jadventure.game.GameBeans;
import com.jadventure.game.QueueProvider;
import com.jadventure.game.items.Item;
import com.jadventure.game.items.ItemStack;
import com.jadventure.game.items.Storage;
import com.jadventure.game.menus.BattleMenu;
import com.jadventure.game.monsters.Monster;
import com.jadventure.game.navigation.Coordinate;
import com.jadventure.game.navigation.ILocation;
import com.jadventure.game.navigation.LocationType;
import com.jadventure.game.repository.ItemRepository;
import com.jadventure.game.repository.LocationRepository;
public class Pet extends Entity{
	
	 private String petType;
	 private boolean hasPet;
	 private int petDamage;
	 private int petEnergy;
	 private int petHealth;
	 private String petName;
	 
	 
	 public Pet(String petType, boolean hasPet, int petDamage, int petEnergy, int petHealth, String petName) {
		 this.petType = petType;
		 this.hasPet = hasPet;
		 this.petDamage = petDamage;
		 this.petEnergy = petEnergy;
		 this.petHealth = petHealth;
		 this.petName = petName;
	 }
	 
	 
	 
	 public void setPetType(String petString) {
	    	this.petType = petString;
	 }
	 public String getPetType() {
	    	return petType;
	 }
	 public void setPetDamage(int petDamage) {
	    	this.petDamage = petDamage;
	 }
	 public int getPetDamage() {
	    	return petDamage;
	 }
	 public void setHasPet(boolean hasPet) {
	    	this.hasPet = hasPet;
	 }
	 public boolean getHasPet() {
	    	return hasPet;
	 }
	 public void setPetEnergy(int petEnergy) {
	    	if(petEnergy > 10) {
	    		petEnergy = 10;
	    	}else if(petEnergy < 0) {
	    		petEnergy = 0;
	    	}
	    	this.petEnergy = petEnergy;
	 }
	 public int getPetEnergy() {
	    	return petEnergy;
	 }
	 
	 public void setPetHealth(int petHealth) {
		 this.petHealth = petHealth;
		 if(this.petHealth < 0) {
			 setHasPet(false);
		 }
	 }
	 public int getPetHealth() {
		 return petHealth;
	 }
	 public void setPetName(String petName) {
		 this.petName = petName;
	 }
	 public String getPetName() {
		 return petName;
	 }

}
