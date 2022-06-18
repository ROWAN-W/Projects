package edu.uob.Actions;

import edu.uob.Entities.GameEntity;
import edu.uob.Entities.Location;
import edu.uob.Entities.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class GameSpecificAction extends GameAction{
  private ArrayList<String>  triggers;
 private ArrayList<String>  subjects;
  private ArrayList<String> consumed;
  private ArrayList<String> produced;
  private String narration;

  public GameSpecificAction(ArrayList<String> Triggers,ArrayList<String> Subjects,ArrayList<String> Consumed,ArrayList<String> Produced,String Narration){
    triggers=Triggers;
    subjects=Subjects;
    consumed=Consumed;
    produced=Produced;
    narration=Narration;
  }

  @Override
  public boolean isPossible(Player player, Location location,String command ) {
    //a valid action command MUST contain a trigger word/phrase and AT LEAST ONE subject
    //the action is only valid if ALL subject entities are available to the player.
    //the entity to either be in the inventory of the player invoking the action or for that entity to be in the room/location where the action is being performed.

    boolean isPossible=false;
   for(String subject:subjects){
      if(command.contains(" "+subject+" ")){
        isPossible=true;
      }
   }

    for(String subject:subjects){
      if(player.getInventory().get(subject)==null && !location.listAllEntities().contains(subject) && !player.getLocation().equals(subject)){
        isPossible=false;
      }
    }
    return isPossible;
  }



  @Override
  public String execute(Player player,HashMap<String,Player> allPlayers, HashMap<String, Location> locations, String command ) {
   if(consumed(player,locations)&&produced(player,locations)){
       return narration+"\n"+checkHealth(player,locations.get(player.getLocation()));
   }
    return "Unable to execute this command. Probably because one of the subject is in another user's inventory";
  }

  private String checkHealth(Player player,Location thisLocation){
      if(player.getHealth()==0){
          player.getInventory().keySet().forEach(key->{
              thisLocation.putEntityByName(key,player.dropArtefactByName(key));
          });
          player.setLocation(player.getInitialLocation());
          player.setHealth(3);
          return "you died and lost all of your items, you must return to the start of the game";
      }
      return "";
  }

  private boolean produced(Player player,HashMap<String, Location> locations){
      boolean succeed=true;
      for(String entityName:produced){
          if(!produceOneEntity(entityName,player,locations)){
              succeed=false;
          }
      }
      return succeed;
  }



    private boolean produceOneEntity(String entityName,Player player,HashMap<String, Location> locations){

        if(producePath(entityName, locations.get(player.getLocation()),locations)) {
            return true;
        }
        if(produceHealth(entityName,player)){
            return true;
        }

        if(moveEntity(player.getLocation(),entityName,player,locations)){
            return true;
        }
        return  false;
    }

  private boolean consumed(Player player,HashMap<String, Location> locations){
      boolean succeed=true;

      for(String entityName:consumed){
          if(!consumeOneEntity(entityName,player,locations)){
              succeed=false;
          }
      }
      return succeed;

  }

  private boolean consumeHealth(String entityName,Player player){
      if(entityName.equals("health")){
          player.deBuff();
          return true;
      }
      return  false;
  }

    private boolean produceHealth(String entityName,Player player){
        if(entityName.equals("health")){
            player.buff();
            return true;
        }
        return  false;
    }


    private boolean moveEntity(String destination,String entityName,Player player,HashMap<String, Location> locations){
      GameEntity retrieved=retrieveFromAllLocation(entityName,locations);
      Location toLocation=locations.get(destination);
      if(toLocation==null){
          return false;
      }
      if(retrieved!=null){
          toLocation.putEntityByName(entityName,retrieved);
          return true;
      }
      retrieved=player.getInventory().remove(entityName);
      if(retrieved!=null){
          toLocation.putEntityByName(entityName,retrieved);
          return true;
      }
      return false;
  }


  private boolean consumeOneEntity(String entityName,Player player,HashMap<String, Location> locations){

      if(consumePath(entityName, locations.get(player.getLocation()))) {
      return true;
      }
      if(consumeHealth(entityName,player)){
          return true;
      }

     if(moveEntity("storeroom",entityName,player,locations)){
         return true;
     }
      return  false;
  }




  private boolean consumePath(String name,Location thisLocation){

      if(thisLocation.getPaths().contains(name)){
          thisLocation.getPaths().remove(thisLocation.getPaths().indexOf(name));
          return true;
      }
      return false;
  }

    private boolean producePath(String name,Location thisLocation,HashMap<String, Location> locations){

       if(locations.keySet().contains(name)){
           thisLocation.addPath(name);
           return true;
       }
        return false;
    }

  private GameEntity retrieveFromAllLocation(String name,HashMap<String, Location> locations){
        for(String key:locations.keySet()) {
            GameEntity retrieved = locations.get(key).takeEntityByName(name);
            if (retrieved != null) {
                return retrieved;
            }
        }
        return null;
  }


}
