package domain;

/**
 * Calamar Naranja:
 * Persigue al jugador más cercano. Si tiene hielo delante, intenta romperlo
 * tras varios ticks. No atraviesa bloques sólidos.
 *
 * Nota: Para tu regla, el calamar puede pararse sobre fogata/baldosa (activas o no),
 * pero NO atraviesa hielo sin romperlo.
 */
public class CalamarNaranja extends Monster {

    private int breakingTicks = 0;
    private static final int TICKS_TO_BREAK = 3;

    private int targetBlockRow = -1;
    private int targetBlockCol = -1;

    public CalamarNaranja(BadIceCream board, int row, int column) {
        super(board, row, column);
    }

    @Override
    public String getMonsterType() {
        return "CALAMAR";
    }

    /**
     * Tick del calamar:
     * - Selecciona el jugador más cercano.
     * - Decide una dirección (prioriza eje con mayor distancia).
     * - Si hay hielo delante, acumula ticks y lo rompe.
     * - Si la celda destino es pasable, se mueve.
     */
    @Override
    public void act() throws BadIceCreamException {
        Player targetPlayer = findNearestPlayer();
        if (targetPlayer == null) return;

        int pr = targetPlayer.getRow();
        int pc = targetPlayer.getColumn();

        int dr = pr - row;
        int dc = pc - column;

        String dir = null;
        int nr = row;
        int nc = column;

        if (Math.abs(dr) > Math.abs(dc)) {
            if (dr > 0) { dir = "down"; nr = row + 1; }
            else if (dr < 0) { dir = "up"; nr = row - 1; }
        } else {
            if (dc > 0) { dir = "right"; nc = column + 1; }
            else if (dc < 0) { dir = "left"; nc = column - 1; }
        }

        if (dir == null) return;

        changeOfView(dir);

        if (!inBounds(nr, nc)) return;
        if (board.getMonsters()[nr][nc] != null) return;

        Block ahead = board.getBlocks()[nr][nc];

        // Si es hielo: romper con ticks
        if (ahead != null && ahead.isIceBlock()) {
            breakIce(nr, nc);
            return;
        }

        // Si cambió el objetivo (ya no está rompiendo el mismo bloque)
        if (targetBlockRow != nr || targetBlockCol != nc) {
            breakingTicks = 0;
            targetBlockRow = -1;
            targetBlockCol = -1;
        }

        // No atraviesa decoración/otros sólidos; sí pisa fogata/baldosa
        if (!isPassableCellForMonster(nr, nc)) return;

        board.setMonster(row, column, null);
        changePosition(nr, nc);
    }

    /**
     * Rompe un bloque de hielo después de TICKS_TO_BREAK ticks insistiendo.
     *
     * @param blockRow fila del hielo
     * @param blockCol columna del hielo
     * @throws BadIceCreamException si ocurre algún problema del tablero
     */
    public void breakIce(int blockRow, int blockCol) throws BadIceCreamException {
        if (targetBlockRow != blockRow || targetBlockCol != blockCol) {
            breakingTicks = 0;
            targetBlockRow = blockRow;
            targetBlockCol = blockCol;
        }

        breakingTicks++;

        if (breakingTicks >= TICKS_TO_BREAK) {
            Block[][] blocks = board.getBlocks();
            Block b = blocks[blockRow][blockCol];

            if (b != null && b.isIceBlock()) {
                blocks[blockRow][blockCol] = null;
            }

            breakingTicks = 0;
            targetBlockRow = -1;
            targetBlockCol = -1;
        }
    }

    /**
     * Encuentra el jugador más cercano (distancia Manhattan).
     *
     * @return Player más cercano o null si no existen jugadores
     */
    private Player findNearestPlayer() {
        Player p1 = board.getFirstPlayer();
        Player p2 = board.getSecondPlayer();

        if (p1 == null && p2 == null) return null;
        if (p1 == null) return p2;
        if (p2 == null) return p1;

        int d1 = Math.abs(p1.getRow() - row) + Math.abs(p1.getColumn() - column);
        int d2 = Math.abs(p2.getRow() - row) + Math.abs(p2.getColumn() - column);

        return (d1 <= d2) ? p1 : p2;
    }

    private boolean inBounds(int r, int c) {
        return r >= 0 && r < board.getHeight() && c >= 0 && c < board.getWidth();
    }

    /**
     * Regla “pasable” para monstruos:
     * - null => pasable
     * - fogata/baldosa => pasable (activa o no)
     * - decoración u otros sólidos => NO pasable
     */
    private boolean isPassableCellForMonster(int r, int c) {
        Block b = board.getBlocks()[r][c];
        if (b == null) return true;
        if (b.isFogata() || b.isBaldosaCaliente()) return true;
        if (b.isIceBlock()) return false;
        if (b.isDecorationBlock()) return false;
        return false;
    }
}

