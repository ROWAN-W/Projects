package edu.uob;

public class DBException extends Exception{
    private static final long serialVersionUID = 9973L;

    String message;
    public DBException(String ErrorMessage){
        message=ErrorMessage;
    }

    public String getMessage(){
        return message;
    }
}
