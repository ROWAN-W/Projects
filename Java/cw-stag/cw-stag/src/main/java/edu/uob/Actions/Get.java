package edu.uob.Actions;

import edu.uob.Entities.Location;
import edu.uob.Entities.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Get extends BuiltInCommand {
    private String trigger="get";
    private String potentialTarget;

    public String getTrigger() {
        return trigger;
    }



    @Override
    public  boolean isPossible(Player player, Location location,String command  ) {
        // check that there is exactly one artefact in the current location that's contained in the command
        ArrayList<String> possibleArtefacts=new ArrayList<>();
        location.getArtefacts().keySet().forEach(key->{
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
        String artefactDescription=currentLocation.getArtefacts().get(potentialTarget).getDescription();
        player.putArtefactByName(potentialTarget,currentLocation.takeArtefactByName(potentialTarget));
        return "Artefact "+artefactDescription+" is added to your inventory!";
    }
}
