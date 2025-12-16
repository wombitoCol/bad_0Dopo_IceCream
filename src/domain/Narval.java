package domain;

import java.util.Random;

/**
 * Narval:
 * - Deambula aleatoriamente.
 * - Si detecta un jugador en la misma fila/columna con línea de visión, entra en modo "carga".
 * - En carga, avanza recto sin parar hasta chocar con un muro/bloque sólido.
 * - A diferencia de otros, puede romper bloques de hielo durante la carga.
 *
 * Regla de tu proyecto:
 * - Puede pararse sobre fogata y baldosa caliente incluso activas (como monstruo).
 */
public class Narval extends Monster {

    private static final Random RNG = new Random();

    private boolean isCharging = false;

    public Narval(BadIceCream board, int row, int column) {
        super(board, row, column);
    }

    @Override
    public String getMonsterType() {
        return "NARVAL";
    }

    /**
     * Tick del Narval:
     * 1) Si está cargando, intenta avanzar 1 celda recto:
     *    - Si hay hielo delante, lo rompe.
     *    - Si la celda es sólida (decoración/límite), detiene la carga.
     * 2) Si no está cargando:
     *    - Si ve un jugador en línea (fila/columna) => inicia carga hacia él.
     *    - Si no => deambula (random walk).
     */
    @Override
    public void act() throws BadIceCreamException {
        if (isCharging) {
            doChargeStep();
            return;
        }

        // Si ve un jugador en línea, iniciar carga
        String chaseDir = detectPlayerLineDirection();
        if (chaseDir != null) {
            changeOfView(chaseDir);
            isCharging = true;
            doChargeStep(); // opcional: que avance de una vez
            return;
        }

        // Si no ve jugador: deambular
        wanderOneStep();
    }

    /**
     * Ejecuta 1 paso de la carga (avanza recto).
     * Si choca, detiene la carga.
     */
    private void doChargeStep() throws BadIceCreamException {
        int nr = row;
        int nc = column;

        if ("up".equals(directionOfView)) nr--;
        else if ("down".equals(directionOfView)) nr++;
        else if ("left".equals(directionOfView)) nc--;
        else if ("right".equals(directionOfView)) nc++;
        else {
            isCharging = false;
            return;
        }

        // Fuera de límites => parar carga
        if (!inBounds(nr, nc)) {
            isCharging = false;
            return;
        }

        // Otro monstruo bloquea => parar carga
        if (board.getMonsters()[nr][nc] != null) {
            isCharging = false;
            return;
        }

        Block b = board.getBlocks()[nr][nc];

        // Si es hielo: romperlo (habilidad extra del narval)
        if (b != null && b.isIceBlock()) {
            board.getBlocks()[nr][nc] = null;
            // después de romper, puede ocupar esa celda este mismo tick
            board.setMonster(row, column, null);
            changePosition(nr, nc);
            return;
        }

        // Si es sólido (decoración u otro no pasable), detener carga
        if (!isPassableCellForMonster(nr, nc)) {
            isCharging = false;
            return;
        }

        // Mover
        board.setMonster(row, column, null);
        changePosition(nr, nc);
    }

    /**
     * Movimiento aleatorio simple: intenta avanzar; si no puede, cambia dirección aleatoria.
     */
    private void wanderOneStep() throws BadIceCreamException {
        // 35% cambia dirección al azar
        if (RNG.nextDouble() < 0.35) {
            changeOfView(randomDirection());
        }

        int nr = row;
        int nc = column;

        if ("up".equals(directionOfView)) nr--;
        else if ("down".equals(directionOfView)) nr++;
        else if ("left".equals(directionOfView)) nc--;
        else if ("right".equals(directionOfView)) nc++;
        else changeOfView("down");

        if (!inBounds(nr, nc)) {
            changeOfView(randomDirection());
            return;
        }

        if (board.getMonsters()[nr][nc] != null) {
            changeOfView(randomDirection());
            return;
        }

        // En deambular NO atraviesa hielo (solo lo rompe cuando carga)
        Block b = board.getBlocks()[nr][nc];
        if (b != null && b.isIceBlock()) {
            changeOfView(randomDirection());
            return;
        }

        if (!isPassableCellForMonster(nr, nc)) {
            changeOfView(randomDirection());
            return;
        }

        board.setMonster(row, column, null);
        changePosition(nr, nc);
    }

    /**
     * Detecta si hay jugador en la misma fila/columna con línea despejada.
     * Devuelve la dirección hacia ese jugador si existe, si no null.
     *
     * Nota: Fogata/baldosa NO bloquean visión; hielo/decoración sí.
     *
     * @return "up/down/left/right" o null
     */
    private String detectPlayerLineDirection() {
        Player p1 = board.getFirstPlayer();
        Player p2 = board.getSecondPlayer();

        // Revisar ambos, quedarse con el primero que cumpla
        String d1 = directionToPlayerIfVisible(p1);
        if (d1 != null) return d1;

        String d2 = directionToPlayerIfVisible(p2);
        return d2;
    }

    private String directionToPlayerIfVisible(Player p) {
        if (p == null) return null;

        int pr = p.getRow();
        int pc = p.getColumn();

        // misma fila
        if (pr == row) {
            if (pc > column && clearLine(row, column + 1, row, pc, "right")) return "right";
            if (pc < column && clearLine(row, column - 1, row, pc, "left")) return "left";
        }

        // misma columna
        if (pc == column) {
            if (pr > row && clearLine(row + 1, column, pr, column, "down")) return "down";
            if (pr < row && clearLine(row - 1, column, pr, column, "up")) return "up";
        }

        return null;
    }

    /**
     * Verifica que no haya bloques sólidos entre dos puntos en línea recta.
     */
    private boolean clearLine(int sr, int sc, int tr, int tc, String dir) {
        Block[][] blocks = board.getBlocks();

        if ("right".equals(dir)) {
            for (int c = sc; c <= tc; c++) {
                if (isSolidVisionBlock(blocks[sr][c])) return false;
            }
        } else if ("left".equals(dir)) {
            for (int c = sc; c >= tc; c--) {
                if (isSolidVisionBlock(blocks[sr][c])) return false;
            }
        } else if ("down".equals(dir)) {
            for (int r = sr; r <= tr; r++) {
                if (isSolidVisionBlock(blocks[r][sc])) return false;
            }
        } else if ("up".equals(dir)) {
            for (int r = sr; r >= tr; r--) {
                if (isSolidVisionBlock(blocks[r][sc])) return false;
            }
        }

        return true;
    }

    private boolean isSolidVisionBlock(Block b) {
        if (b == null) return false;
        // fogata y baldosa NO bloquean visión en tu regla
        if (b.isFogata() || b.isBaldosaCaliente()) return false;
        // hielo y decoración sí bloquean
        return true;
    }

    private boolean inBounds(int r, int c) {
        return r >= 0 && r < board.getHeight() && c >= 0 && c < board.getWidth();
    }

    /**
     * Regla de pasabilidad para monstruos:
     * - null => pasable
     * - fogata/baldosa => pasable (aunque esté activa)
     * - decoración => no
     * - hielo => no (salvo en carga, que se rompe aparte)
     */
    private boolean isPassableCellForMonster(int r, int c) {
        Block b = board.getBlocks()[r][c];
        if (b == null) return true;
        if (b.isFogata() || b.isBaldosaCaliente()) return true;
        if (b.isDecorationBlock()) return false;
        if (b.isIceBlock()) return false;
        return false;
    }

    private String randomDirection() {
        int x = RNG.nextInt(4);
        if (x == 0) return "up";
        if (x == 1) return "down";
        if (x == 2) return "left";
        return "right";
    }
}
