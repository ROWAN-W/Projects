package edu.uob.Actions;

import edu.uob.Entities.Location;
import edu.uob.Entities.Player;

import java.util.HashMap;

public class Health extends BuiltInCommand{

    private String trigger="health";

    @Override
    public boolean isPossible(Player player, Location location, String command ) {
        // this action has no extra requirement
        return true;
    }

    @Override
    public String execute(Player player, HashMap<String, Player> allPlayers, HashMap<String, Location> locations, String command ) {

        return "Your current health level is "+player.getHealth();
    }
}
