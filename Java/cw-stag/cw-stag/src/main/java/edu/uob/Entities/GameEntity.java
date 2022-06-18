package edu.uob.Entities;



public abstract class GameEntity
{

   private String name;
    private String description;
   private String type;


    public GameEntity(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    //a bit of double-dispatch





}
