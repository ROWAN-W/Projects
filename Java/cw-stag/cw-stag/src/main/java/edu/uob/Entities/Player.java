package edu.uob.Entities;

import java.util.HashMap;

//Players: A special kind of character that represents the user in the game
public class Player {
  private HashMap<String,Artefact> inventory;
  private String initialLocation;
  private String location;
  private int health;

  private String name;


    public Player(String name,String Location) {
        this.name=name;
        inventory=new HashMap<>();
        initialLocation=Location;
        location=initialLocation;
        health=3;
    }

    public String getInitialLocation() {
        return initialLocation;
    }

    public String getLocation() {
        return location;
    }

    public HashMap<String, Artefact> getInventory() {
        return inventory;
    }

    public void putArtefactByName(String name, Artefact artefact){
        inventory.put(name,artefact);
    }

    public Artefact dropArtefactByName(String name){
       return inventory.remove(name);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void buff(){
        if(this.health<3){this.health++;}
    }

    public void deBuff(){
        this.health--;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
