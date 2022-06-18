package edu.uob.Entities;
//Characters: The various creatures or people involved in game
public class Character extends GameEntity{

    public Character(String name, String description) {
        super(name, description);
        super.setType("character");
    }
}
