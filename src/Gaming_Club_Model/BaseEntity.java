package Gaming_Club_Model;

public abstract class BaseEntity {
    protected String id;
    protected String name;

    public BaseEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract boolean validate();

    public String getBasicInfo(){
        return String.format("ID: %s , Name: %s", id, name);
    }

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
}
