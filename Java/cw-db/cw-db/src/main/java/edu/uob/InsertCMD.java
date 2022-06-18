package edu.uob;

import java.util.ArrayList;

public class InsertCMD extends DBCmd{
    Table table;
    ArrayList<ArrayList<String>> array;
    @Override
    public String query(DBServer Server) throws DBException{
        server=Server;
        dataBase=server.getDataBase();
        checkDB();
        table=dataBase.getTableByName(tableNames.get(0));
        array=table.getContent();
        insert();
        return null;
    }
    public void checkDB()throws  DBException {
        if(dataBase==null){
            throw new DBException("no database used");
        }
    }


    public void insert() throws DBException{
        if(values.size()==array.get(0).size()){
             insertWithID();
             return;
        }
        else if(values.size()==array.get(0).size()-1){
            values.add(0,String.valueOf(table.getMaxID()+1));
            insertWithID();
            return;
        }
        throw new DBException("fail to insert");

    }

    public void insertWithID() throws DBException{
        table.addRow(values);
        table.storeToFile();
    }
}
