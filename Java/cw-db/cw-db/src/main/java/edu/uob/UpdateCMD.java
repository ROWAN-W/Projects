package edu.uob;

import java.util.ArrayList;

public class UpdateCMD extends DBCmd {
   Table table;
   ArrayList<ArrayList<String>> content;
    public String query(DBServer Server) throws DBException{
       server=Server;
       dataBase=server.getDataBase();
       checkDB();
       table=dataBase.getTableByName(tableNames.get(0));
       content=table.getContent();
        checkCols();
        if(content.size()<2){
            return "Nothing to update";
       }
        checkCondition();
        table.setContent(content);
        table.storeToFile();
        return null;
    }

    public void checkCondition() throws DBException{
        for(int i=1;i<content.size();i++){
            ArrayList<ArrayList<String>> test=new ArrayList<>();
            test.add(content.get(0));
            test.add(content.get(i));
            if(condition.checkCondition(test,condition)){
               update(i);
            }
        }
    }

    public void update(int row)throws  DBException{
          for(int i=0;i< colNames.size();i++){
              int index=content.get(0).indexOf(colNames.get(0));
              content.get(row).set(index,values.get(i));
          }
    }

    public void checkCols()throws DBException{
        for(int i=0;i<colNames.size();i++){
            if(!content.get(0).contains(colNames.get(i))) {
                throw new DBException("Unknown column "+colNames.get(i));
            }
        }
    }

    public void checkDB()throws  DBException {
        if(dataBase==null){
            throw new DBException("no database used");
        }
    }


}
