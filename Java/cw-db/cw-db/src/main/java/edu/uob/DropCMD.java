package edu.uob;

public class DropCMD  extends  DBCmd{
    public String query(DBServer Server) throws DBException{
        server=Server;
        dataBase=server.getDataBase();

        if(commandType.equals("DATABASE")){
            server.deleteDB(DBName);
            return null;
        }
        if(commandType.equals("TABLE")){
            checkDB();
            dataBase.deleteTableByName(tableNames.get(0));
            return null;
        }

        throw new DBException("fail to DROP");
    }

    public void checkDB()throws  DBException {
        if(dataBase==null){
            throw new DBException("no database used");
        }
    }
}
