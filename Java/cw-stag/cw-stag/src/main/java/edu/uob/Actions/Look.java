package edu.uob.Actions;

import edu.uob.Entities.Location;
import edu.uob.Entities.Player;

import java.util.HashMap;

public class Look extends BuiltInCommand{
    private String trigger="look" ;


    public String getTrigger() {
        return trigger;
    }

    @Override
    public boolean isPossible(Player player, Location location, String command  ){
        // this action has no extra requirement
        return true;
    }

    private  String listOtherPlayers(String location, HashMap<String,Player> otherPlayers){
        String otherPlayerList="";
        for (String key:otherPlayers.keySet()) {
            if(otherPlayers.get(key).getLocation().equals(location)){
                otherPlayerList=otherPlayerList+key+"; ";
            }
        }
        return otherPlayerList;
    }

    private HashMap<String,Player> getOtherPlayers(Player you, HashMap<String,Player> allPlayers){
        HashMap<String,Player> otherPlayers=new HashMap<>();
        allPlayers.keySet().forEach(key->{
            if(allPlayers.get(key)!=you){
                otherPlayers.put(key,allPlayers.get(key));
            }
        });
        return otherPlayers;
    }

    @Override
    public String execute(Player player,HashMap<String,Player> allPlayers, HashMap<String, Location> locations, String command ) {
        String reply="";
        String currentLocation=player.getLocation();
        reply= "You location is "+currentLocation+ ", which is "+locations.get(currentLocation).getDescription()+"\n";
        reply=reply+locations.get(currentLocation).toString();
        HashMap<String,Player> otherPlayers=getOtherPlayers(player,allPlayers);
        reply=reply+"There are other players in this location: "+listOtherPlayers(currentLocation,otherPlayers);
        return reply;
    }


}
