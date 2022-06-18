package edu.uob;

import java.util.ArrayList;

public class AlterCMD  extends DBCmd{
    Table table;
    ArrayList<ArrayList<String>> content;
    public String query(DBServer Server) throws DBException{
        server=Server;
        dataBase=server.getDataBase();
        checkDB();
        controlID();
        table=dataBase.getTableByName(tableNames.get(0));
        content=table.getContent();
        if(commandType.equals("DROP")){
            checkColNames();
            dropCol();
            table.setContent(content);
            table.storeToFile();
            return null;
        }
        else if(commandType.equals("ADD")){
              addCol();
              table.setContent(content);
              table.storeToFile();
              return null;
        }
        return null;

    }

    public void addCol()throws DBException{
        content.get(0).add(colNames.get(0));
        if(content.size()>1){
            for(int i=1;i<content.size();i++){
                content.get(i).add("NULL");
            }
        }
    }

    public void dropCol() throws DBException{
        int index=content.get(0).indexOf(colNames.get(0));
        for(int i=0;i<content.size();i++){
            content.get(i).remove(index);
        }
    }

    public void checkColNames() throws DBException{
        if(!content.get(0).contains(colNames.get(0))){
            throw new DBException(colNames.get(0)+" doesn't exist");
        }
    }

    public void controlID() throws DBException{
        if(colNames.get(0).equals("id")&&commandType.equals("DROP")){
            throw new DBException("can not drop id column");
        }
    }

    public void checkDB()throws  DBException {
        if(dataBase==null){
            throw new DBException("no database used");
        }
    }
}
