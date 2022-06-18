package edu.uob;

import javax.naming.Name;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;

/** This class implements the DB server. */
public final class DBServer {

  private static final char END_OF_TRANSMISSION = 4;
  DataBase dataBase;
  String rootPath;
  File rootFile;


  public static void main(String[] args) throws IOException {
    new DBServer(Paths.get(".").toAbsolutePath().toFile()).blockingListenOn(8888);
  }

  /**
   * KEEP this signature (i.e. {@code edu.uob.DBServer(File)}) otherwise we won't be able to mark
   * your submission correctly.
   *
   * <p>You MUST use the supplied {@code databaseDirectory} and only create/modify files in that
   * directory; it is an error to access files outside that directory.
   *
   * @param databaseDirectory The directory to use for storing any persistent database files such
   *     that starting a new instance of the server with the same directory will restore all
   *     databases. You may assume *exclusive* ownership of this directory for the lifetime of this
   *     server instance.
   */
  public DBServer(File databaseDirectory) {
    // TODO implement your server logic here
    rootPath=databaseDirectory.getPath();

  }

  public void useDataBase(String name) throws DBException{
    dataBase=new DataBase(rootPath,name);
    dataBase.loadFromDir();
  }

  public void createDB(String name) throws DBException{
    if(checkExistDB(name)) throw new DBException("database already exists");
    dataBase=new DataBase(rootPath,name);
    dataBase.createDBDirectory();
  }

  public boolean checkExistDB(String Name) throws DBException{
    rootFile=new File(rootPath);
    File[] list=rootFile.listFiles();
    for(int i=0;i<list.length;i++){
      if(list[i].isDirectory()&&list[i].getName().equals(Name)){
        return true;
      }
    }
    return false;
  }


  public void deleteDB (String Name) throws DBException{
    if(!checkExistDB(Name)){
      throw new DBException(Name+" doesn't exist");
    }
    DataBase tobeDelete=new DataBase(rootPath,Name);
    tobeDelete.loadFromDir();
    tobeDelete.deleteAllTables();
    tobeDelete.deleteDir();

    if(dataBase!=null&&dataBase.getName().equals(Name)){
      dataBase=null;
    }
  }


  public DataBase getDataBase() {
    return dataBase;
  }


  /**
   * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
   * able to mark your submission correctly.
   *
   * <p>This method handles all incoming DB commands and carry out the corresponding actions.
   */
  public String handleCommand(String command) {
    // TODO implement your server logic here

    DBParser p=new DBParser(command);
    DBCmd Cmd;

    try{
     Cmd=p.parse();
    }catch (DBException e){
      return "[ERROR]" +e.getMessage();
    }
    String reply;
    try{
      reply=Cmd.query(this);
    } catch (DBException e){
      return "[ERROR]" +e.getMessage();
    }
    if(reply==null){
      return "[OK]";
    }
    else{
      return "[OK]"+System.lineSeparator()+reply;
    }

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
          System.err.println("Server encountered a non-fatal IO error:");
          e.printStackTrace();
          System.err.println("Continuing...");
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

      System.out.println("Connection established: " + serverSocket.getInetAddress());
      while (!Thread.interrupted()) {
        String incomingCommand = reader.readLine();
        System.out.println("Received message: " + incomingCommand);
        String result = handleCommand(incomingCommand);
        writer.write(result);
        writer.write("\n" + END_OF_TRANSMISSION + "\n");
        writer.flush();
      }
    }
  }
}
