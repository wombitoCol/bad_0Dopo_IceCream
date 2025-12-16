package domain;
import java.io.*;


public interface Unit extends Serializable {
	
    void act() throws BadIceCreamException;

    int getRow();

    int getColumn();

    void changePosition(int newRow, int newColumn);

    public default boolean isPlayer(){return false;}
    public default boolean isMonster(){return false;}
    public default boolean isFruit(){return false;}

}
