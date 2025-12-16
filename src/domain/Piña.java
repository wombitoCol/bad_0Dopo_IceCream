package domain;

import java.util.Random;

public class Piña extends Fruit {

    private String directionOfView;
    private static final Random random = new Random();

    public Piña(BadIceCream board, int row, int column){
        super(board,row,column);
        score = 20;
        // Dirección inicial aleatoria
        String[] dirs = {"up", "down", "left", "right"};
        directionOfView = dirs[random.nextInt(dirs.length)];
    }

    @Override
    public void act() throws BadIceCreamException {
        // Intenta moverse en la dirección actual,
        // si no puede, rota la dirección y vuelve a intentar
        if (!tryMoveInCurrentDirection()) {
            changeDirectionOfView();
            tryMoveInCurrentDirection();
        }
    }

    private boolean tryMoveInCurrentDirection() throws BadIceCreamException {
        int dr = 0, dc = 0;

        if (directionOfView == null) {
            directionOfView = "up";
        }

        switch (directionOfView) {
            case "up":    dr = -1; dc = 0;  break;
            case "down":  dr = 1;  dc = 0;  break;
            case "left":  dr = 0;  dc = -1; break;
            case "right": dr = 0;  dc = 1;  break;
            default:
                return false;
        }

        int r1 = row + dr;
        int c1 = column + dc;
        int r2 = row + 2*dr;
        int c2 = column + 2*dc;

        Block[][] blocks   = board.getBlocks();
        Fruit[][] fruits   = board.getFruits();
        Monster[][] monsters = board.getMonsters();
        Player[][] players = board.getPlayers();

        int height = blocks.length;
        int width  = blocks[0].length;

        // 1. Si la primera casilla está fuera del tablero → no se mueve
        if (r1 < 0 || r1 >= height || c1 < 0 || c1 >= width) {
            return false;
        }

        // 2. Caso normal: la casilla inmediata está libre de bloque, monstruo y fruta
        //    (puede haber jugador, lo manejaremos luego con checkActions)
        if (blocks[r1][c1] == null &&
            monsters[r1][c1] == null &&
            fruits[r1][c1] == null) {

            changePosition(r1, c1);
            return true;
        }

        // 3. Intentar "saltar" un SOLO bloque:
        //    - La primera casilla tiene bloque
        //    - La segunda casilla (r2,c2) existe
        //    - En (r2,c2) NO hay bloque, ni monstruo, ni fruta
        //      (puede haber jugador)
        if (blocks[r1][c1] != null) {
            // Si la casilla de aterrizaje está fuera → no salta
            if (r2 < 0 || r2 >= height || c2 < 0 || c2 >= width) {
                return false;
            }

            // Si hay otro bloque en r2,c2 → significa que son 2 bloques seguidos o más → NO salta
            if (blocks[r2][c2] != null) {
                return false;
            }

            // Si hay monstruo o fruta en la casilla de aterrizaje → tampoco
            if (monsters[r2][c2] != null || fruits[r2][c2] != null) {
                return false;
            }

            // Aquí SÍ puede saltar el bloque único
            changePosition(r2, c2);
            return true;
        }

        // 4. Si la casilla inmediata no es bloque pero sí tiene monstruo o fruta → no se mueve
        return false;
    }

    private void changeDirectionOfView() throws BadIceCreamException {
        if (directionOfView == null) {
            directionOfView = "up";
            return;
        }

        switch (directionOfView) {
            case "up":
                changeOfView("right");
                break;
            case "right":
                changeOfView("down");
                break;
            case "down":
                changeOfView("left");
                break;
            case "left":
                changeOfView("up");
                break;
            default:
                throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_MOVEMENT);
        }
    }

    public void changeOfView(String direction) throws BadIceCreamException {
        if (direction.equals("down") || direction.equals("up") || 
            direction.equals("left") || direction.equals("right")) {
            directionOfView = direction;
        } else {
            throw new BadIceCreamException(BadIceCreamException.DIRECTION_NO_ALLOWED);
        }
    }

    @Override
    public String getFruitType() {
        return "PIÑA";
    }
}
