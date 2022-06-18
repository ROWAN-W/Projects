package edu.uob;

public class GameException extends  Exception{
    private static final long serialVersionUID = 2022L;
    private String message;
    public GameException(String Message){
        message=Message;
    }

    public String getMessage() {
        return message;
    }


    public static class LoadFileException extends GameException {
        private static final long serialVersionUID =20221L;
        public LoadFileException(String Message) {
            super(Message);
        }
    }


}


