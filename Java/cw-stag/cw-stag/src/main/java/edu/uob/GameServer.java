package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import java.nio.file.Paths;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;
import edu.uob.Actions.*;
import edu.uob.Entities.*;
import edu.uob.Entities.Character;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/** This class implements the STAG server. */
public final class GameServer {

    private String firstLocationName;
    private HashMap<String,Location> locations;
    private HashMap<String,Player> players;
    private HashMap<String,BuiltInCommand> builtInActions;
    private TreeMap<String, HashSet<GameAction>> specificActions;

    private static final char END_OF_TRANSMISSION = 4;

    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    private void loadBuiltInActions(){
        Get get=new Get();
        builtInActions.put("get",get);
        Look look = new Look();
        builtInActions.put("look",look);
        Goto aGoto = new Goto();
        builtInActions.put("goto",aGoto);
        Drop drop = new Drop();
        builtInActions.put("drop",drop);
        Inventory inventory = new Inventory();
        builtInActions.put("inventory",inventory);
        builtInActions.put("inv",inventory);
        Health health=new Health();
        builtInActions.put("health",health);
    }

    private void addHashSet(String trigger,GameSpecificAction action){
        if(specificActions.get(trigger)!=null){
            specificActions.get(trigger).add(action);
        }
        else{
            HashSet<GameAction> hashSet=new HashSet<>();
            hashSet.add(action);
            specificActions.put(trigger,hashSet);
        }
    }

    private void loadArtefacts(Location location,Graph artefacts){
        artefacts.getNodes(false).forEach(node -> {
            String name=node.getId().getId();
            Artefact artefact =new Artefact(name,node.getAttribute("description"));
            location.addArtifact(name,artefact);
        });
    }

    private void loadCharacters(Location location,Graph characters){
        characters.getNodes(false).forEach(node -> {
            String name=node.getId().getId();
            Character character=new Character(name,node.getAttribute("description"));
            location.addCharacter(name,character);
        });
        //System.out.println(location.listAllCharacters());
    }

    private void loadFurniture(Location location,Graph furniture){
        furniture.getNodes(false).forEach(node -> {
            String name=node.getId().getId();
            Furniture furniture1=new Furniture(name,node.getAttribute("description"));
            location.addFurniture(name,furniture1);
        });
        //System.out.println(location.listAllFurnitures());
    }


    private void loadLocationEntities(Location location, ArrayList<Graph> entities){
        entities.forEach(graph -> {
            String entityType=graph.getId().getId();
            //System.out.println(entityType);

            switch (entityType){
                case "artefacts":
                    loadArtefacts(location,graph);
                    break;
                case "furniture":
                    loadFurniture(location,graph);
                    break;
                case "characters":
                    loadCharacters(location,graph);
                    break;
                default:
                    return;
            }

        });


    }


    private void loadLocations(ArrayList<Graph> Locations){
        Locations.forEach((location)->{
            Node locationDetails = location.getNodes(false).get(0);
            String  locationName = locationDetails.getId().getId();
            String description =locationDetails.getAttribute("description").toString();
            Location newLocation=new Location(locationName,description);
            loadLocationEntities(newLocation,location.getSubgraphs());
            locations.put(locationName,newLocation);
        });
        if(!locations.keySet().contains("storeroom")){
            Location storeroom=new Location("storeroom","storeroom");
            locations.put("storeroom",storeroom);
        }

    }


    public void checkEmptyEntity(ArrayList<Graph> entities) throws GameException{
        if(entities.size()==0){
            throw new GameException.LoadFileException("the entities file is empty");
        }
    }

    public void loadPaths(ArrayList<Edge> paths){
        paths.forEach(path->{
            Node fromLocation = path.getSource().getNode();
            String fromName = fromLocation.getId().getId();
            Node toLocation = path.getTarget().getNode();
            String toName = toLocation.getId().getId();
            locations.get(fromName).addPath(toName);
        });
    }



    private void loadEntities(File entitiesFile) throws GameException.LoadFileException{
        try {
            Parser parser = new Parser();

            FileReader reader = new FileReader(entitiesFile);
            parser.parse(reader);
            checkEmptyEntity(parser.getGraphs());

            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();

            // The locations will always be in the first subgraph
            ArrayList<Graph> locations = sections.get(0).getSubgraphs();
            checkEmptyEntity(locations);

            //get the first location name
            Graph firstLocation = locations.get(0);
            Node locationDetails = firstLocation.getNodes(false).get(0);
            firstLocationName = locationDetails.getId().getId();
            loadLocations(locations);

            // The paths will always be in the second subgraph
            ArrayList<Edge> paths = sections.get(1).getEdges();
            loadPaths(paths);

        } catch (FileNotFoundException fnfe) {
            throw new GameException.LoadFileException("Entity file not found");
        } catch (ParseException pe) {
            throw new GameException.LoadFileException("Can not parse the entity file");
        }catch (GameException gme){
            throw new GameException.LoadFileException(gme.getMessage());
        }

    }


    private ArrayList<String> getTriggers(Element triggers){
        ArrayList<String> triggerList=new ArrayList<>();
        //System.out.println(triggers.getElementsByTagName("keyword").getLength());
        for(int i=0;i<triggers.getElementsByTagName("keyword").getLength();i++){
            triggerList.add(triggers.getElementsByTagName("keyword").item(i).getTextContent());
        }
        return  triggerList;
    }

    private ArrayList<String> getActionEntities(Element element){
        ArrayList<String> entityList=new ArrayList<>();

        for(int i=0;i<element.getElementsByTagName("entity").getLength();i++){
            entityList.add(element.getElementsByTagName("entity").item(i).getTextContent());
        }
        return  entityList;
    }


    private void loadOneAction(Element action){

        // Get triggers
        Element triggers = (Element)action.getElementsByTagName("triggers").item(0);
        ArrayList<String> triggerList=getTriggers(triggers);
        //Get subjects
        Element subjects=(Element)action.getElementsByTagName("subjects").item(0);
        ArrayList<String> subjectList=getActionEntities(subjects);

        //Get consumed
        Element consumed=(Element)action.getElementsByTagName("consumed").item(0);
        ArrayList<String> consumedList=getActionEntities(consumed);

        //Get produced
        Element produced=(Element)action.getElementsByTagName("produced").item(0);
        ArrayList<String> producedList=getActionEntities(produced);

        //Get narration
        String narration=action.getElementsByTagName("narration").item(0).getTextContent();

        GameSpecificAction newAction=new GameSpecificAction(triggerList,subjectList,consumedList,producedList,narration);
        triggerList.forEach(trigger->{
            addHashSet(trigger,newAction);
        });

    }

    private void loadActions(File actionsFile) throws GameException{
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(actionsFile);
            Element root = document.getDocumentElement();
            NodeList actions = root.getChildNodes();
            //only the odd items are actually actions - 1, 3, 5 etc.
            for(int i=1;i<actions.getLength();i+=2){
                Element action = (Element)actions.item(i);
                loadOneAction(action);
            }

        } catch(ParserConfigurationException pce) {
            throw new GameException.LoadFileException("ParserConfigurationException was thrown when attempting to read basic actions file");
        } catch(SAXException saxe) {
            throw new GameException.LoadFileException("SAXException was thrown when attempting to read basic actions file");
        } catch(IOException ioe) {
            throw new GameException.LoadFileException("IOException was thrown when attempting to read basic actions file");
        }
    }

    private Player getPlayer(String playerName){
        if(players.get(playerName)!=null){
            return  players.get(playerName);
        }
        else {
            Player player=new Player(playerName,firstLocationName);
            players.put(playerName,player);
            return player;
        }
    }


    /**
    * KEEP this signature (i.e. {@code edu.uob.GameServer(File, File)}) otherwise we won't be able to mark
    * your submission correctly.
    *
    * <p>You MUST use the supplied {@code entitiesFile} and {@code actionsFile}
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    *
    */
    public GameServer(File entitiesFile, File actionsFile) {
        // TODO implement your server logic here
        players=new HashMap<>();
        locations=new HashMap<>();
        builtInActions=new HashMap<>();
        specificActions = new TreeMap<String, HashSet<GameAction>>();
        loadBuiltInActions();
        try {
            loadEntities(entitiesFile);
            loadActions(actionsFile);
        }catch (GameException gme){
            throw  new RuntimeException(gme.getMessage());
        }

        //create default player
        Player defaultPlayer=new Player("default",firstLocationName);
        //add default player to map
        players.put("default",defaultPlayer);
    }

    private Location getPlayersLocation(Player player)
    {
        return locations.get(player.getLocation());

    }

    //check that there is not duplicated action
    private void addUniqueAction(ArrayList<GameAction> actionList, GameAction action){
        for(GameAction existingAction:actionList){
            if(action==existingAction){
                return;
            }
        }
        actionList.add(action);
    }

    //add possible built-in actions to the list
    private void checkBuiltInActions(ArrayList<GameAction> possibleActions, String command, Player player){
        builtInActions.keySet().forEach(key->{
            if(command.contains(" "+key+" ")&&builtInActions.get(key).isPossible(player,getPlayersLocation(player),command)){
                //possibleActions.add(builtInActions.get(key));
                addUniqueAction(possibleActions,builtInActions.get(key));
            }
        });
    }

    //add possible specific actions to the list
    private void checkSpecificActions(ArrayList<GameAction> possibleActions, String command, Player player){
        specificActions.keySet().forEach(key->{
            if(command.contains(" "+key+" ")){
                for(GameAction action:specificActions.get(key)){
                    if(action.isPossible(player,getPlayersLocation(player),command)){
                        // possibleActions.add(action);
                        addUniqueAction(possibleActions,action);
                    }
                }
            }
        });
    }


    private String executeAction(ArrayList<GameAction> possibleActions,Player player, HashMap<String,Location> locations,String command ){
        if(possibleActions.size()==1){
            return possibleActions.get(0).execute(player,players,locations,command);
        }
        else {return "no proper action or more that one possible action";}
    }

    private  ArrayList<String> toArrayList(String[] array){
        ArrayList<String> arrayList=new ArrayList<>();
        for(String word:array){
            arrayList.add(word);
        }
        return arrayList;
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.GameServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming game commands and carries out the corresponding actions.
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here

        String[] tokenList=command.split(":");
        ArrayList<GameAction> possibleActions=new ArrayList<>();
        Player currentPlayer;
        //ArrayList<String> cleanCommand;
        String cleanCommand;
        if(tokenList.length==2){
            currentPlayer=getPlayer(tokenList[0]);
            //cleanCommand=toArrayList(tokenList[1].toLowerCase().split(" "));
            cleanCommand=" "+tokenList[1].toLowerCase()+" ";
        }
        else {
            currentPlayer=players.get("default");
            //cleanCommand=toArrayList(tokenList[0].toLowerCase().split(" "));
            cleanCommand=" "+tokenList[0].toLowerCase()+" ";
        }
        checkBuiltInActions(possibleActions,cleanCommand,currentPlayer);
        checkSpecificActions(possibleActions,cleanCommand,currentPlayer);

        String reply= executeAction(possibleActions,currentPlayer,locations,cleanCommand);
        if(reply==null){
            return "error when executing the command";
        }
        return reply;
    }

    //  === Methods below are there to facilitate server related operations. ===

    /**
    * Starts a *blocking* socket server listening for new connections. This method blocks until the
    * current thread is interrupted.
    *
    * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
    * you want to.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Handles an incoming connection from the socket server.
    *
    * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
    * * you want to.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();

            }
        }
    }
}
