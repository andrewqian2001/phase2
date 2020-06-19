import java.io.Serializable;

public abstract class DatabaseItem implements Serializable {
    private static int id = 0;
    private int thisId;
    public DatabaseItem(){
        this.thisId = id;
        id++;
    }
    public int getId(){
        return thisId;
    }

}
