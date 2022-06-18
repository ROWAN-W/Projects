package edu.uob;

import java.util.ArrayList;

public class DeleteCMD extends DBCmd{
    Table table;
    ArrayList<ArrayList<String >> content;
    public String query(DBServer Server) throws DBException{
       server=Server;
       dataBase=server.getDataBase();
       checkDB();
       table=dataBase.getTableByName(tableNames.get(0));
       content=table.getContent();
       if(content.size()<2){
           return null;
       }
       deleteRows();
       table.setContent(content);
       table.storeToFile();
       return null;
    }


    public void deleteRows() throws DBException{

        boolean exist=false;
        ArrayList<ArrayList<String>> test=new ArrayList<>();
        test.add(content.get(0));
        test.add(content.get(0));
        do{
            for(int i=1;i<content.size();i++){
                test.set(1,content.get(i));
                if(condition.checkCondition(test,condition)){
                    exist=true;
                    content.remove(i);
                    break;
                }
                exist=false;
            }
        }while(exist);
    }




    public void checkDB()throws  DBException {
        if(dataBase==null){
            throw new DBException("no database used");
        }
    }
}
