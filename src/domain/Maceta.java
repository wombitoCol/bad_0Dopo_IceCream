package domain;

/**
 * Maceta:
 * - Alterna entre modo normal y modo alerta.
 * - En alerta persigue más agresivamente (más movimientos por tick).
 * - En normal deambula y a veces “persigue” si detecta jugador en línea.
 *
 * Regla de colisión: como monstruo, puede pararse sobre fogata/baldosa incluso activas.
 */
public class Maceta extends Monster {

    private boolean isChasing;
    private boolean isInAlertMode;

    private static final long ALERT_INTERVAL = 8000;
    private static final long ALERT_DURATION = 5000;
    private static final int ALERT_MOVES_PER_ACT = 2;

    private long lastAlertTime;
    private long alertStartTime;

    public Maceta(BadIceCream board, int row, int column) {
        super(board, row, column);
        isChasing = false;
        isInAlertMode = false;
        lastAlertTime = System.currentTimeMillis();
        alertStartTime = 0;
    }

    public boolean isChasing() {
        return isChasing;
    }

    @Override
    public boolean isInAlertMode() {
        return isInAlertMode;
    }

    @Override
    public String getMonsterType() {
        return "MACETA";
    }

    /**
     * Tick principal:
     * - Activa/desactiva modo alerta por tiempo.
     * - Si está en alerta: ejecuta varios micro-movimientos.
     * - Si no: ejecuta comportamiento normal.
     */
    @Override
    public void act() throws BadIceCreamException {
        long now = System.currentTimeMillis();

        if (!isInAlertMode && (now - lastAlertTime >= ALERT_INTERVAL)) {
            isInAlertMode = true;
            alertStartTime = now;
            lastAlertTime = now;
        }

        if (isInAlertMode && (now - alertStartTime >= ALERT_DURATION)) {
            isInAlertMode = false;
        }

        if (isInAlertMode) {
            for (int i = 0; i < ALERT_MOVES_PER_ACT; i++) {
                actInAlertMode();
            }
        } else {
            actInNormalMode();
        }
    }

    /**
     * En alerta: persigue al jugador más cercano (prioriza eje con mayor distancia).
     * Si no puede avanzar hacia él, elige una dirección válida aleatoria.
     */
    private void actInAlertMode() throws BadIceCreamException {
        Player target = findNearestPlayer();
        if (target == null) {
            actInNormalMode();
            return;
        }

        isChasing = true;

        int tr = target.getRow();
        int tc = target.getColumn();

        int rr = tr - row;
        int cc = tc - column;

        boolean moved = false;

        if (Math.abs(rr) > Math.abs(cc)) {
            if (rr < 0) moved = tryMoveInDirection("up");
            else if (rr > 0) moved = tryMoveInDirection("down");

            if (!moved) {
                if (cc < 0) moved = tryMoveInDirection("left");
                else if (cc > 0) moved = tryMoveInDirection("right");
            }
        } else {
            if (cc < 0) moved = tryMoveInDirection("left");
            else if (cc > 0) moved = tryMoveInDirection("right");

            if (!moved) {
                if (rr < 0) moved = tryMoveInDirection("up");
                else if (rr > 0) moved = tryMoveInDirection("down");
            }
        }

        if (!moved) {
            chooseRandomValidDirection();
        }
    }

    /**
     * Modo normal: deambula; si ve un jugador en línea, marca chasing.
     * Si no puede moverse, cambia dirección.
     */
    private void actInNormalMode() throws BadIceCreamException {
        boolean seesPlayer = detectPlayerInLine();
        isChasing = seesPlayer;

        boolean moved = attemptMoveOneStep();
        if (!moved) {
            chooseRandomValidDirection();
        } else {
            if (seesPlayer) attemptMoveOneStep();
            else if (Math.random() < 0.30) chooseRandomValidDirection();
        }
    }

    /**
     * Busca el jugador más cercano iterando la matriz de players.
     *
     * @return Player más cercano o null si no hay jugadores
     */
    private Player findNearestPlayer() {
        Player[][] players = board.getPlayers();
        Player nearest = null;
        double minDist = Double.MAX_VALUE;

        for (int r = 0; r < board.getHeight(); r++) {
            for (int c = 0; c < board.getWidth(); c++) {
                if (players[r][c] != null) {
                    double d = Math.sqrt(Math.pow(r - row, 2) + Math.pow(c - column, 2));
                    if (d < minDist) {
                        minDist = d;
                        nearest = players[r][c];
                    }
                }
            }
        }
        return nearest;
    }

    /**
     * Intenta moverse una casilla en una dirección dada.
     *
     * @param direction "up/down/left/right"
     * @return true si se movió, false si no
     * @throws BadIceCreamException si la dirección no es válida
     */
    private boolean tryMoveInDirection(String direction) throws BadIceCreamException {
        changeOfView(direction);
        return attemptMoveOneStep();
    }

    /**
     * Detecta si existe un jugador en línea recta en la dirección actual,
     * deteniéndose con bloque sólido (pero NO debe bloquear por fogata/baldosa).
     *
     * @return true si ve jugador, false si no
     */
    private boolean detectPlayerInLine() {
        Player[][] players = board.getPlayers();
        Block[][] blocks = board.getBlocks();

        if ("up".equals(directionOfView)) {
            for (int r = row - 1; r >= 0; r--) {
                if (isSolidVisionBlock(blocks[r][column])) break;
                if (players[r][column] != null) return true;
            }
        } else if ("down".equals(directionOfView)) {
            for (int r = row + 1; r < board.getHeight(); r++) {
                if (isSolidVisionBlock(blocks[r][column])) break;
                if (players[r][column] != null) return true;
            }
        } else if ("left".equals(directionOfView)) {
            for (int c = column - 1; c >= 0; c--) {
                if (isSolidVisionBlock(blocks[row][c])) break;
                if (players[row][c] != null) return true;
            }
        } else if ("right".equals(directionOfView)) {
            for (int c = column + 1; c < board.getWidth(); c++) {
                if (isSolidVisionBlock(blocks[row][c])) break;
                if (players[row][c] != null) return true;
            }
        }

        return false;
    }

    /**
     * Intenta moverse 1 paso en la dirección actual.
     *
     * @return true si se movió, false si no
     */
    private boolean attemptMoveOneStep() {
        int nr = row;
        int nc = column;

        if ("up".equals(directionOfView)) nr--;
        else if ("down".equals(directionOfView)) nr++;
        else if ("left".equals(directionOfView)) nc--;
        else if ("right".equals(directionOfView)) nc++;
        else return false;

        if (canMoveTo(nr, nc)) {
            board.setMonster(row, column, null);
            changePosition(nr, nc);
            return true;
        }
        return false;
    }

    /**
     * Elige una dirección aleatoria entre las que sean válidas.
     *
     * @throws BadIceCreamException si ocurre un error al cambiar dirección
     */
    private void chooseRandomValidDirection() throws BadIceCreamException {
        String[] dirs = {"up", "down", "left", "right"};
        java.util.ArrayList<String> valid = new java.util.ArrayList<>();

        for (String d : dirs) if (isDirectionValid(d)) valid.add(d);

        if (!valid.isEmpty()) {
            String pick = valid.get((int) (Math.random() * valid.size()));
            changeOfView(pick);
        } else {
            changeOfView(getOppositeDirection());
        }
    }

    /**
     * Verifica si una dirección tiene una celda destino válida.
     *
     * @param d dirección
     * @return true si podría moverse, false si no
     */
    private boolean isDirectionValid(String d) {
        int nr = row, nc = column;

        if ("up".equals(d)) nr--;
        else if ("down".equals(d)) nr++;
        else if ("left".equals(d)) nc--;
        else if ("right".equals(d)) nc++;

        return canMoveTo(nr, nc);
    }

    /**
     * Retorna la dirección opuesta a la actual.
     *
     * @return dirección opuesta
     */
    private String getOppositeDirection() {
        if ("up".equals(directionOfView)) return "down";
        if ("down".equals(directionOfView)) return "up";
        if ("left".equals(directionOfView)) return "right";
        if ("right".equals(directionOfView)) return "left";
        return "down";
    }

    /**
     * Regla de movimiento para la Maceta:
     * - Dentro de bounds
     * - Sin otro monstruo
     * - Bloques: permite fogata y baldosa caliente (activas o no), pero no hielo ni decoración.
     *
     * @param r fila destino
     * @param c columna destino
     * @return true si puede moverse
     */
    private boolean canMoveTo(int r, int c) {
        if (r < 0 || r >= board.getHeight() || c < 0 || c >= board.getWidth()) return false;
        if (board.getMonsters()[r][c] != null) return false;

        Block b = board.getBlocks()[r][c];
        if (b == null) return true;
        if (b.isFogata() || b.isBaldosaCaliente()) return true;

        // sólidos
        if (b.isIceBlock()) return false;
        if (b.isDecorationBlock()) return false;

        return false;
    }

    /**
     * Define qué bloque “corta visión” (para detectar jugador).
     * Fogata y baldosa caliente NO deben cortar visión en tu regla de pasabilidad,
     * pero hielo/decoración sí.
     */
    private boolean isSolidVisionBlock(Block b) {
        if (b == null) return false;
        if (b.isFogata() || b.isBaldosaCaliente()) return false;
        return true;
    }
}