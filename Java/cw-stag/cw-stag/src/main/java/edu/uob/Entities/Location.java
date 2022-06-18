package edu.uob.Entities;

import java.util.ArrayList;
import java.util.HashMap;

//Locations: Rooms or places within the game
public class Location extends GameEntity {

    private HashMap<String,Furniture> furnitures;
    private HashMap<String,Character> characters;
    private HashMap<String,Artefact> artefacts;
    private  ArrayList<String> paths;

    public ArrayList<String> getPaths() {
        return paths;
    }

    public Location(String Name, String Description) {
          super(Name,Description);
          furnitures =new HashMap<>();
          characters=new HashMap<>();
          artefacts=new HashMap<>();
          paths=new ArrayList<>();
          super.setType("location");
    }

    public HashMap<String, Artefact> getArtefacts() {
        return artefacts;
    }

    public GameEntity takeEntityByName(String name){
        if(furnitures.keySet().contains(name)){
            return furnitures.remove(name);
        }
        if(artefacts.keySet().contains(name)){
            return  artefacts.remove(name);
        }
        if(characters.keySet().contains(name)){
            return characters.remove(name);
        }
        return null;
    }

    public Artefact takeArtefactByName(String name){

        return artefacts.remove(name) ;
    }

    public  void putArtefactByName(String name,Artefact artefact){
        artefacts.put(name, artefact);
    }

    public void putEntityByName(String name,GameEntity entity){
        switch (entity.getType()){
            case "artefact":
                artefacts.put(name,(Artefact) entity);
                break;
            case "furniture":
                furnitures.put(name,(Furniture) entity);
                break;
            case "character":
                characters.put(name,(Character) entity);
                break;
            default:
                return;
        }

    }


    public ArrayList<String> listAllEntities(){
        ArrayList<String> allEntities=new ArrayList<>();
        furnitures.keySet().forEach(key->{
            allEntities.add(key);
        });
        artefacts.keySet().forEach(key->{
            allEntities.add(key);
        });
        characters.keySet().forEach(key->{
            allEntities.add(key);
        });
        return allEntities;
    }

    public String toString(){
        return  "This location contains:\n"+ "Furnitures: "+printAllFurnitures()+"\n"+"Artefacts: "+printAllArtefacts()+
                "\n"+"Characters: "+printAllCharacters()+"\n"+ "Paths to: "+printAllPaths()+"\n";
    }

    public String printAllFurnitures(){
        String furnitureList="";
        for (String key: furnitures.keySet()) {
            furnitureList=furnitureList+key+" ("+furnitures.get(key).getDescription()+")"+"; ";
        }
        return  furnitureList;
    }

    public String printAllArtefacts(){
        String artefactList="";
        for (String key: artefacts.keySet()) {
            artefactList=artefactList+key+" ("+artefacts.get(key).getDescription()+")"+"; ";
        }
        return  artefactList;
    }

    public String printAllCharacters(){
        String characterList="";
        for (String key: characters.keySet()) {
            characterList=characterList+key+" ("+characters.get(key).getDescription()+")"+"; ";
        }
        return  characterList;
    }

    public String printAllPaths(){
        String pathList="";
        for(String path: paths){
            pathList=pathList+path+"; ";
        }
        return pathList;
    }

    public void addFurniture(String name,Furniture furniture){
        furnitures.put(name,furniture);
    }

    public void addCharacter(String name,Character character){
        characters.put(name,character);
    }

    public void addArtifact(String name,Artefact artefact){
        artefacts.put(name,artefact);
    }

    public void addPath(String toLocation){
        paths.add(toLocation);
    }

}
