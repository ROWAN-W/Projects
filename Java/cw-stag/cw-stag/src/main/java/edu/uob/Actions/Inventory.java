package edu.uob.Actions;

import edu.uob.Entities.Artefact;
import edu.uob.Entities.Location;
import edu.uob.Entities.Player;

import java.util.HashMap;

public class Inventory extends BuiltInCommand{
   private String trigger="inventory";

    public String getTrigger() {
        return trigger;
    }


    @Override
    public boolean isPossible(Player player, Location location, String command  ) {
        //this action has no extra condition
        return true;
    }



    @Override
    public String execute(Player player,HashMap<String,Player> allPlayers, HashMap<String, Location> locations,String command ) {

        HashMap<String,Artefact> inventory=player.getInventory();
        String description="";

        for(String key:inventory.keySet()){
           description=description+key+" ("+inventory.get(key).getDescription()+")"+"; ";
        }
        return  "Your inventory contains : "+description;
    }
}
