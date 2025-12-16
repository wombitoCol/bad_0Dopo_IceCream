package domain;

import java.io.*;

/**
 * Clase abstracta base para todos los monstruos del juego.
 * Un monstruo ocupa una celda del tablero, tiene dirección de vista y ejecuta
 * un comportamiento por tick mediante act().
 *
 * Reglas generales:
 * - Se registra automáticamente en el tablero al construirse.
 * - Los monstruos normalmente no comparten celda con otros monstruos.
 *
 * @author Neco-Arc Team
 * @version 1.0
 */
public abstract class Monster implements Unit, Serializable, InteractWithPlayer {

    protected int row;
    protected int column;
    protected BadIceCream board;
    protected String directionOfView;

    /**
     * Crea un monstruo y lo registra en el tablero en la posición dada.
     *
     * @param board Tablero del juego
     * @param row   Fila inicial
     * @param column Columna inicial
     */
    public Monster(BadIceCream board, int row, int column) {
        this.board = board;
        this.row = row;
        this.column = column;
        this.board.setMonster(row, column, this);
        this.board.addUnit(this);
        directionOfView = "down";
    }

    @Override
    public boolean isDangerous(){
        return true;
    }

    /**
     * Retorna el tipo de monstruo (para render/UI y lógica).
     *
     * @return String identificador del monstruo (ej: "TROLL", "CALAMAR", "NARVAL")
     */
    public abstract String getMonsterType();

    /**
     * Retorna la fila actual del monstruo.
     *
     * @return fila (row)
     */
    @Override
    public int getRow() {
        return row;
    }

    /**
     * Retorna la columna actual del monstruo.
     *
     * @return columna (column)
     */
    @Override
    public int getColumn() {
        return column;
    }

    /**
     * Cambia la posición del monstruo dentro del tablero.
     * Nota: este método también actualiza la referencia en la matriz de monstruos del tablero.
     *
     * @param newRow nueva fila
     * @param newColumn nueva columna
     */
    @Override
    public void changePosition(int newRow, int newColumn) {
        row = newRow;
        column = newColumn;
        this.board.setMonster(row, column, this);
    }

    /**
     * Indica que esta unidad es un monstruo.
     *
     * @return true siempre
     */
    @Override
    public boolean isMonster() {
        return true;
    }

    /**
     * Cambia la dirección en la que mira el monstruo.
     *
     * @param direction "up", "down", "left" o "right"
     * @throws BadIceCreamException si la dirección no es válida
     */
    public void changeOfView(String direction) throws BadIceCreamException {
        if (direction.equals("down") || direction.equals("up") ||
                direction.equals("left") || direction.equals("right")) {
            directionOfView = direction;
        } else {
            throw new BadIceCreamException(BadIceCreamException.DIRECTION_NO_ALLOWED);
        }
    }

    /**
     * Acción por tick del monstruo.
     * Cada subclase debe implementar su comportamiento aquí.
     *
     * @throws BadIceCreamException si ocurre un movimiento/acción inválida
     */
    @Override
    public void act() throws BadIceCreamException { }

    /**
     * Retorna la dirección actual del monstruo.
     *
     * @return dirección de vista
     */
    public String getDirectionOfView() {
        return directionOfView;
    }

    /**
     * Indica si el monstruo está en modo alerta (para animación/IA).
     * Por defecto es false; subclases como Maceta pueden sobreescribirlo.
     *
     * @return true si está en alerta, si no false
     */
    public boolean isInAlertMode() {
        return false;
    }
}
