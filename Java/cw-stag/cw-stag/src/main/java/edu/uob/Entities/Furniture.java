package edu.uob.Entities;
//Furniture: Physical things that are an integral part of a location
//these can NOT be collected by the player
public class Furniture extends GameEntity {

    public Furniture(String name, String description) {
        super(name, description);
        super.setType("furniture");
    }


}
