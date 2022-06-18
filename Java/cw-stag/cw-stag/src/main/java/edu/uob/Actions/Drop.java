package edu.uob.Actions;

import edu.uob.Entities.Location;
import edu.uob.Entities.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Drop extends BuiltInCommand {
    private String trigger="drop" ;
   private String potentialTarget;
    public String getTrigger() {
        return trigger;
    }

    @Override
    public  boolean isPossible(Player player, Location location, String command ) {
        //check that there is exactly one artefact in the inventory of said player that's contained in the command
        ArrayList<String> possibleArtefacts=new ArrayList<>();
        player.getInventory().keySet().forEach(key->{
           if(command.contains(" "+key+" ")){
               possibleArtefacts.add(key);
           }
       });
    if(possibleArtefacts.size()==1){
            potentialTarget=possibleArtefacts.get(0);
            return true;
        }
        return false;
    }

    @Override
    public String execute(Player player,HashMap<String,Player> allPlayers, HashMap<String, Location> locations, String command ) {

        Location currentLocation=locations.get(player.getLocation());
        String description =player.getInventory().get(potentialTarget).getDescription();
        currentLocation.putArtefactByName(potentialTarget,player.dropArtefactByName(potentialTarget));

        return "You have dropped "+description+" in "+currentLocation.getName();

    }

}
