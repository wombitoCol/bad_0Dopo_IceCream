package domain;

/**
 * Troll: enemigo que camina en patrón cuadrado.
 * Intenta avanzar en su dirección; si no puede, rota la dirección (up->right->down->left).
 * Mata al jugador por contacto (lo maneja el board/GUI con checkActionsForPlayerX()).
 */
public class Troll extends Monster {

    public Troll(BadIceCream board, int row, int column) {
        super(board, row, column);
    }

    @Override
    public String getMonsterType() {
        return "TROLL";
    }

    /**
     * Ejecuta un tick del troll.
     * Si la celda al frente es válida y pasable para monstruos, se mueve.
     * Si no puede moverse, cambia su dirección (rotación).
     *
     * Reglas de “pasable” para monstruos (según tu requerimiento):
     * - Puede pararse en baldosas calientes y fogatas, estén activas o no.
     * - No puede atravesar bloques sólidos (decoración, hielo, etc.).
     */
    @Override
    public void act() throws BadIceCreamException {
        int nr = row;
        int nc = column;

        if ("up".equals(directionOfView)) nr--;
        else if ("down".equals(directionOfView)) nr++;
        else if ("left".equals(directionOfView)) nc--;
        else if ("right".equals(directionOfView)) nc++;
        else changeDirectionOfView();

        if (canMoveTo(nr, nc)) {
            board.setMonster(row, column, null);
            changePosition(nr, nc);
        } else {
            changeDirectionOfView();
        }
    }

    /**
     * Valida si el troll puede moverse a (r,c).
     *
     * @param r fila destino
     * @param c columna destino
     * @return true si puede moverse, false si no
     */
    private boolean canMoveTo(int r, int c) {
        if (r < 0 || r >= board.getHeight() || c < 0 || c >= board.getWidth()) return false;
        if (board.getMonsters()[r][c] != null) return false;

        Block b = board.getBlocks()[r][c];
        // Pasables para monstruos incluso activas:
        if (b == null) return true;
        if (b.isFogata() || b.isBaldosaCaliente()) return true;

        // Bloques sólidos:
        return false;
    }

    /**
     * Rota la dirección del troll en sentido horario.
     *
     * @throws BadIceCreamException si la dirección actual es inválida
     */
    public void changeDirectionOfView() throws BadIceCreamException {
        if (directionOfView.equals("up")) changeOfView("right");
        else if (directionOfView.equals("right")) changeOfView("down");
        else if (directionOfView.equals("down")) changeOfView("left");
        else if (directionOfView.equals("left")) changeOfView("up");
        else throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_MOVEMENT);
    }
}

