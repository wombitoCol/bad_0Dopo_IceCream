package domain;
import java.io.*;


public interface InteractWithPlayer extends Serializable {
	
    boolean isDangerous();

    int getRow();

    int getColumn();

}