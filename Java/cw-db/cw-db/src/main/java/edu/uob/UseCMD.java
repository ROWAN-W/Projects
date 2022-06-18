package edu.uob;

public class UseCMD extends DBCmd{

    @Override
    public String query(DBServer Server) throws DBException {
        server = Server;
        server.useDataBase(DBName);
        dataBase = server.getDataBase();
        return null;
    }
}
