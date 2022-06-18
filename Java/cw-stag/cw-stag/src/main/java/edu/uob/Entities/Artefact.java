package edu.uob.Entities;
//Artefacts: Physical things within the game that can be collected by the player
public class Artefact extends GameEntity {

    public Artefact(String name, String description) {
        super(name, description);
        super.setType("artefact");
    }

}
