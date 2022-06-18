package edu.uob;

import java.util.ArrayList;

public class CreateCMD extends DBCmd {
    Table table;

    public String query(DBServer Server) throws DBException{
        server=Server;
        dataBase=server.getDataBase();

        if(commandType.equals("DATABASE")){
            server.createDB(DBName);
            return null;
        }
        if(commandType.equals("TABLE")){
            checkDB();
            CreateTable();
            return null;
        }

        throw new DBException("fail to CREATE");
    }

    public void CreateTable() throws DBException{
        table=dataBase.CreatTable(tableNames.get(0));
        if(colNames==null){
            throw new DBException("no column name specified");
        }
        if(!colNames.contains("id")){
            colNames.add(0,"id");
        }
        table.addRow(colNames);
        table.storeToFile();
    }

    public void checkDB()throws  DBException {
        if(dataBase==null){
            throw new DBException("no database used");
        }
    }



}
