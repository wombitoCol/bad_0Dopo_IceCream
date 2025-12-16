package domain;

public class BadIceCreamException extends Exception{
    public static final String DIRECTION_NO_ALLOWED = "Esta dirección no esta definida";
    public static final String CANNOT_EXECUTE_MOVEMENT = "No se puede ejecutar el movimiento";
    public static final String CANNOT_EXECUTE_ACTION = "No se puede ejecutar esta acción";
    public static final String PLAYER_ON_PLAYER = "No se puede colocar un jugador sobre otro jugador";
    public static final String PLAYER_ON_MONSTER = "No se puede colocar un jugador sobre un monstruo";
    public static final String MONSTER_ON_MONSTER = "No se puede colocar un monstruo sobre otro monstruo";
    public static final String UNIT_ON_ICE_BLOCK = "No se puede colocar una unidad sobre un bloque de hielo";
    public static final String UNIT_ON_DECORATION_BLOCK = "No se puede colocar una unidad sobre un bloque decorativo";
    public static final String INVALID_LEVEL_FORMAT = "Formato de nivel inválido";
    public static final String FILE_NOT_FOUND = "Archivo no encontrado";
    public static final String INVALID_POSITION = "Posición inválida en el tablero";
    public BadIceCreamException(String message){
        super(message);
    }
}
