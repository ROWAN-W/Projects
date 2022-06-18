package edu.uob.Actions;

import edu.uob.Entities.Location;
import edu.uob.Entities.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Goto extends BuiltInCommand{
    private String trigger="goto" ;
    private String potentialTarget;
    public String getTrigger() {
        return trigger;
    }


    @Override
    public  boolean isPossible(Player player, Location location,String command  ) {
        //check that there is exactly one path in the current location that is mentioned in the command
        ArrayList<String> possiblePaths=new ArrayList<>();
        location.getPaths().forEach(path->{
            if(command.contains(" "+path+" ")){
                possiblePaths.add(path);
            }
        });
        if(possiblePaths.size()==1){
            potentialTarget=possiblePaths.get(0);
            return true;
        }
        return false;
    }

    @Override
    public String execute(Player player,HashMap<String,Player> allPlayers, HashMap<String, Location> locations, String command ) {
        String oldLocation=player.getLocation();
        String newLocation=potentialTarget;
        player.setLocation(newLocation);

        return "You have moved from "+oldLocation+" to "+newLocation;
    }
}
