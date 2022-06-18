package edu.uob.Actions;


import edu.uob.Entities.Location;
import edu.uob.Entities.Player;

import java.util.HashMap;

public abstract class GameAction
{
   public  abstract  boolean isPossible(Player player, Location location, String command );
   public  abstract  String execute(Player player,HashMap<String,Player> allPlayers, HashMap<String,Location> locations,String command  );
}
