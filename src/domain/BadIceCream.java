package domain;
import java.util.*;
import java.io.*;

public class BadIceCream implements Serializable {
    
    private int height = 16;
    private int width = 16;
    private int level;
    private int phase;

    private ArrayList<Unit> units;
    private Fruit[][] fruits;
    private Player[][] players;
    private Block[][] blocks;
    private Monster[][] monsters;
 
    private int totalScore;
    private int phaseScore;
    private Fruit currentFruit;
    
    private ArrayList<EntityData> fruitsPhase1 = new ArrayList<>();
    private ArrayList<EntityData> fruitsPhase2 = new ArrayList<>();
    
    private Player player1;
    private Player player2;
    private String selectedPlayer1Flavor = null;
    private String selectedPlayer2Flavor = null;
    private ArrayList<InteractWithPlayer> InteractorsWithPlayer = new ArrayList();    
    /*
     * @level: número de nivel con el que se inicializa el juego
     * Constructor de la clase. Inicializa las estructuras del tablero
     * y establece el nivel y la fase inicial.
     * @return: no retorna nada
     */
    public BadIceCream(int level) throws BadIceCreamException { 
        units = new ArrayList<>();
        phase = 1;
        this.level = level;
        players = new Player[height][width];
        fruits = new Fruit[height][width];
        monsters = new Monster[height][width];
        blocks = new Block[height][width];
        createBoard(height,width,level);
    }
    
    /*
     * @player1Flavor: sabor seleccionado para el jugador 1
     * @player2Flavor: sabor seleccionado para el jugador 2
     * Asigna los sabores elegidos para cada jugador para usarlos al importar/crear el nivel.
     * @return: no retorna nada
     */
    public void setPlayerFlavors(String player1Flavor, String player2Flavor) {
        this.selectedPlayer1Flavor = player1Flavor;
        this.selectedPlayer2Flavor = player2Flavor;
    }

    /*
     * @height: especifica la altura del tablero 
     * @width: especifica la anchura del tablero 
     * @level: indica el nivel 
     * Se encarga de crear (o limpiar) el tablero con las especificaciones dadas,
     * inicializando las matrices de frutas, bloques, jugadores y monstruos en null.
     * @return: no retorna nada
     */
    public void createBoard(int height, int width,int level) throws BadIceCreamException{
        for (int r=0;r<height;r++){
            for (int c=0;c<width;c++){
                fruits[r][c]=null;
                blocks[r][c]=null;
                players[r][c]=null;
                monsters[r][c]=null; 
            }
        }
    } 

    /*
     * @u: unidad que se va a añadir a la lista general de unidades
     * Añade una unidad al arreglo de unidades activas del juego.
     * @return: no retorna nada
     */
    public void addUnit(Unit u){
        units.add(u);
    }
    
    /*
     * Recorre la matriz de frutas y suma el puntaje de todas las frutas
     * actualmente presentes en el tablero para la fase.
     * @return: retorna la suma total de puntajes de las frutas de la fase
     */
    public int calculatePhaseScore() {
        int sum = 0;
        for(int r = 0; r < height; r++) {
            for(int c = 0; c < width; c++) {
                if(fruits[r][c]!= null) {
                    sum += fruits[r][c].getScore();
                }
            }
        }
        return sum;
    }

    /*
     * Reinicia el tablero del juego, eliminando todas las frutas, bloques,
     * jugadores y monstruos, dejando las matrices en null.
     * @return: no retorna nada
     */
    public void reboot() { 
        for(int r=0;r<height;r++){
            for(int c=0; c<width; c++){
                fruits[r][c]=null;
                blocks[r][c]=null;
                players[r][c]=null;
                monsters[r][c]=null;
            }
        }
        InteractorsWithPlayer.clear(); // Limpiar la lista de interactores
    }

    /*
     * @r: fila donde se ubicará el jugador
     * @c: columna donde se ubicará el jugador
     * @p: jugador a asignar en la matriz de jugadores
     * Coloca un jugador en la posición indicada dentro de la matriz de jugadores.
     * @return: no retorna nada
     */
    public void setPlayer(int r, int c, Player p){
        players[r][c] = p;
    }

    /*
     * @r: fila donde se ubicará la fruta
     * @c: columna donde se ubicará la fruta
     * @f: fruta a asignar en la matriz de frutas
     * Coloca una fruta en la posición indicada dentro de la matriz de frutas.
     * @return: no retorna nada
     */
    public void setFruit(int r, int c, Fruit f){
        fruits[r][c] = f;
    }

    /*
     * @r: fila donde se ubicará el bloque
     * @c: columna donde se ubicará el bloque
     * @b: bloque a asignar en la matriz de bloques
     * Coloca un bloque en la posición indicada dentro de la matriz de bloques.
     * @return: no retorna nada
     */
    public void setBlock(int r, int c, Block b){
        blocks[r][c] = b;
    }

    /*
     * @r: fila donde se ubicará el monstruo
     * @c: columna donde se ubicará el monstruo
     * @m: monstruo a asignar en la matriz de monstruos
     * Coloca un monstruo en la posición indicada dentro de la matriz de monstruos.
     * @return: no retorna nada
     */
    public void setMonster(int r, int c, Monster m){
        monsters[r][c] = m;
    }

    /**
     * Checkea después de realizar cada tic de movimiento los posibles casos
     * para el jugador 1: muerte por monstruo, comer fruta, quemarse, etc.
     */
    /*
     * Revisa las interacciones del jugador 1 con monstruos, frutas y bloques
     * en su posición actual (posibles muertes o recogida de frutas).
     * Usa la lista InteractorsWithPlayer para verificar todas las interacciones.
     * @return: no retorna nada
     */
    public void checkActionsForPlayer1() {
        if (player1 == null) return;
        
        // PRIMERO: Verificar bloques peligrosos (fogata activa) directamente
        Block block = blocks[player1.getRow()][player1.getColumn()];
        if (block != null && block.isFogata()) {
            Fogata f = (Fogata)block;
            // Verificar AMBOS: isActive() Y isDangerous() por si acaso
            if (f.isActive() || f.isDangerous()) {
                player1.die();
                units.remove(player1);
                player1 = null;
                return; // Salir porque el jugador murió
            }
        }
        
        // SEGUNDO: Verificar objetos peligrosos usando InteractorsWithPlayer
        for (InteractWithPlayer i : InteractorsWithPlayer) {
            // Verificar si el objeto está en la misma posición que el jugador
            if (i.getRow() == player1.getRow() && i.getColumn() == player1.getColumn()) {
                if (i.isDangerous()) {
                    // Objeto peligroso (monstruo o cactus activo)
                    player1.die();
                    units.remove(player1);
                    player1 = null;
                    return; // Salir porque el jugador murió
                }
            }
        }
        
        // Si el jugador sobrevivió, verificar si hay frutas para recoger
        checkActionsWithFruits(player1);
    }
    
    /*
     * Revisa las interacciones del jugador 2 con monstruos, frutas y bloques
     * en su posición actual (posibles muertes o recogida de frutas).
     * Usa la lista InteractorsWithPlayer para verificar todas las interacciones.
     * @return: no retorna nada
     */
    public void checkActionsForPlayer2() {
        if (player2 == null) return;
        
        // PRIMERO: Verificar bloques peligrosos (fogata activa) directamente
        Block block = blocks[player2.getRow()][player2.getColumn()];
        if (block != null && block.isFogata()) {
            Fogata f = (Fogata)block;
            // Verificar AMBOS: isActive() Y isDangerous() por si acaso
            if (f.isActive() || f.isDangerous()) {
                player2.die();
                units.remove(player2);
                player2 = null;
                return; // Salir porque el jugador murió
            }
        }
        
        // SEGUNDO: Verificar objetos peligrosos usando InteractorsWithPlayer
        for (InteractWithPlayer i : InteractorsWithPlayer) {
            // Verificar si el objeto está en la misma posición que el jugador
            if (i.getRow() == player2.getRow() && i.getColumn() == player2.getColumn()) {
                if (i.isDangerous()) {
                    // Objeto peligroso (monstruo o cactus activo)
                    player2.die();
                    units.remove(player2);
                    player2 = null;
                    return; // Salir porque el jugador murió
                }
            }
        }
        
        // Si el jugador sobrevivió, verificar si hay frutas para recoger
        checkActionsWithFruits(player2);
    }
    
    /*
     * @p: jugador sobre el cual se verifican las interacciones
     * Verifica si el jugador se encuentra sobre una fogata activa.
     * Si está activa, el jugador muere y se elimina de la lista de unidades.
     * @return: no retorna nada
     */
    public void checkActionsWithBlocks(Player p) {
        if (p == null) return;
        
        Block block = blocks[p.getRow()][p.getColumn()];
        if (block != null && block.isFogata()) { 
            Fogata f = (Fogata)block;
            if(f.isActive()) {
                p.die();
                units.remove(p);
                
                if (p == player1) {
                    player1 = null;
                } else if (p == player2) {
                    player2 = null;
                }
            }
        }
    }
    
    /*
     * @p: jugador sobre el cual se verifican las interacciones
     * Verifica si el jugador está sobre una fruta. Si es cactus activo, el
     * jugador muere. Si no, se suma el puntaje de la fruta y ésta desaparece.
     * @return: no retorna nada
     */
    public void checkActionsWithFruits(Player p) {
        if (p == null) return;
        
        Fruit f = fruits[p.getRow()][p.getColumn()];
        if(f!= null) {
            if(f.isCactus()) {
                Cactus cactus = (Cactus)f;
                if(cactus.isActive()) {
                    players[p.getRow()][p.getColumn()] = null;
                    units.remove(p);
                    
                    if (p == player1) {
                        player1 = null;
                    } else if (p == player2) {
                        player2 = null;
                    }
                    p.die();
                }
                else {
                    p.setScore(f.getScore());
                    units.remove(f);
                    f.dissapear();
                }
            }
            else {
                p.setScore(f.getScore());
                units.remove(f);
                f.dissapear();
            }
        }
    }
    
    /*
     * @p: jugador sobre el cual se verifican las interacciones
     * Verifica si en la posición actual del jugador hay un monstruo.
     * Si lo hay, el jugador muere y se elimina del tablero y de la lista de unidades.
     * @return: no retorna nada
     */
    public void checkActionsWithMonsters(Player p) {
        if (p == null) return;
        
        int playerRow = p.getRow();
        int playerCol = p.getColumn();
        
        Monster m = monsters[playerRow][playerCol];
        if (m != null) {
            players[playerRow][playerCol] = null;
            
            units.remove(p);
            
            if (p == player1) {
                player1 = null;
                System.out.println("¡Player 1 eliminado por monstruo!");
            } else if (p == player2) {
                player2 = null;
                System.out.println("¡Player 2 eliminado por monstruo!");
            }
        }
    }
    
    /*
     * Llama a la acción de disparar o romper hielo para el jugador 1
     * dependiendo de lo que tenga delante. Si el jugador no existe, no hace nada.
     * @return: no retorna nada
     */
    public void shootOrBreakIcePlayer1() throws BadIceCreamException{
        if(player1 == null) return;
        
        player1.shootOrBreakIce();
    }
    
    /*
     * Llama a la acción de disparar o romper hielo para el jugador 2
     * dependiendo de lo que tenga delante. Si el jugador no existe, no hace nada.
     * @return: no retorna nada
     */
    public void shootOrBreakIcePlayer2() throws BadIceCreamException{
    	if(player2 == null) return;
        
        player2.shootOrBreakIce();
    }
    
    /*
     * @r: fila de referencia del jugador que crea el hielo
     * @c: columna de referencia del jugador que crea el hielo
     * Crea bloques de hielo hacia arriba desde la posición (r,c) hasta encontrar
     * un obstáculo, monstruo, jugador o el borde del tablero. Si hay fogata o
     * baldosa caliente, se marca para restaurarlas al romper el hielo.
     * @return: no retorna nada
     */
    public void createIceUp(int r, int c) {
        int row = r - 1; 

        while(row >= 0) {
            if (blocks[row][c] != null) {
                if (blocks[row][c].isBaldosaCaliente()) {
                    new IceBlock(this,row,c);
                    blocks[row][c].setHasBaldosa(true);
                }
                else if(blocks[row][c].isFogata()) {
                    new IceBlock(this,row,c);
                    blocks[row][c].setHasFogata(true);
                }
                else { break; }
            }
            else if (monsters[row][c] != null || players[row][c] != null) {
                break;
            }
            else {
                new IceBlock(this, row, c);
            }
            row--; 
        }
    }

    /*
     * @r: fila de referencia del jugador que rompe el hielo
     * @c: columna de referencia del jugador que rompe el hielo
     * Rompe bloques de hielo hacia arriba desde la posición (r,c) y, si 
     * esos bloques tenían asociada una fogata o baldosa caliente, los restaura.
     * @return: no retorna nada
     */
    public void breakIceUp(int r, int c) {
        int row = r - 1;

        while (row >= 0) {
            Block b = blocks[row][c];
            if (b == null) {
                break;
            }
            if (!b.isIceBlock()) {
                break;
            }
            boolean hadFogata = b.hasFogata();
            boolean hadBaldosa = b.hasBaldosa();
            blocks[row][c] = null;
            if (hadFogata) {
                new Fogata(this, row, c);
            } else if (hadBaldosa) {
                new BaldosaCaliente(this, row, c);
            }
            row--;
        }
    }
 
    /*
     * @r: fila de referencia del jugador que crea el hielo
     * @c: columna de referencia del jugador que crea el hielo
     * Crea bloques de hielo hacia abajo desde la posición (r,c) respetando
     * bordes, monstruos, jugadores y bloques especiales (fogata/baldosa).
     * @return: no retorna nada
     */
    public void createIceDown(int r, int c) {
        int row = r + 1;  

        while (row < height) {
            if (blocks[row][c] != null) {
                if (blocks[row][c].isBaldosaCaliente()) {
                    new IceBlock(this, row, c);
                    blocks[row][c].setHasBaldosa(true);
                } else if (blocks[row][c].isFogata()) {
                    new IceBlock(this, row, c);
                    blocks[row][c].setHasFogata(true);
                } else {
                    break;
                }
            } else if (monsters[row][c] != null || players[row][c] != null) {
                break;
            } else {
                new IceBlock(this, row, c);
            }
            row++;
        }
    }

    /*
     * @r: fila de referencia del jugador que rompe el hielo
     * @c: columna de referencia del jugador que rompe el hielo
     * Rompe bloques de hielo hacia abajo desde la posición (r,c) y restaura
     * fogatas o baldosas calientes que estuvieran cubiertas.
     * @return: no retorna nada
     */
    public void breakIceDown(int r, int c) {
        int row = r + 1;
        while (row < height) {
            Block b = blocks[row][c];
            if (b == null) {
                break;
            }
            if (!b.isIceBlock()) {
                break;
            }
            boolean hadFogata = b.hasFogata();
            boolean hadBaldosa = b.hasBaldosa();
            blocks[row][c] = null;
            if (hadFogata) {
                new Fogata(this, row, c);
            } else if (hadBaldosa) {
                new BaldosaCaliente(this, row, c);
            }
            row++;
        }
    }

    /*
     * @r: fila de referencia del jugador que crea el hielo
     * @c: columna de referencia del jugador que crea el hielo
     * Crea bloques de hielo hacia la izquierda desde la posición (r,c) hasta
     * chocar con un obstáculo, monstruo, jugador o el borde del tablero.
     * @return: no retorna nada
     */
    public void createIceLeft(int r, int c) {
        int col = c - 1; 

        while (col >= 0) {
            if (blocks[r][col] != null) {
                if (blocks[r][col].isBaldosaCaliente()) {
                    new IceBlock(this, r, col);
                    blocks[r][col].setHasBaldosa(true);
                } else if (blocks[r][col].isFogata()) {
                    new IceBlock(this, r, col);
                    blocks[r][col].setHasFogata(true);
                } else {
                    break;
                }
            } else if (monsters[r][col] != null || players[r][col] != null) {
                break;
            } else {
                new IceBlock(this, r, col);
            }
            col--;
        }
    }

    /*
     * @r: fila de referencia del jugador que rompe el hielo
     * @c: columna de referencia del jugador que rompe el hielo
     * Rompe bloques de hielo hacia la izquierda desde la posición (r,c)
     * y restaura fogatas o baldosas calientes cubiertas.
     * @return: no retorna nada
     */
    public void breakIceLeft(int r, int c) {
        int col = c - 1;
        while (col >= 0) {
            Block b = blocks[r][col];
            if (b == null) {
                break;
            }
            if (!b.isIceBlock()) {
                break;
            }
            boolean hadFogata = b.hasFogata();
            boolean hadBaldosa = b.hasBaldosa();
            blocks[r][col] = null;
            if (hadFogata) {
                new Fogata(this, r, col);
            } else if (hadBaldosa) {
                new BaldosaCaliente(this, r, col);
            }
            col--;
        }
    }

    /*
     * @r: fila de referencia del jugador que crea el hielo
     * @c: columna de referencia del jugador que crea el hielo
     * Crea bloques de hielo hacia la derecha desde la posición (r,c) hasta
     * chocar con un obstáculo, monstruo, jugador o el borde del tablero.
     * @return: no retorna nada
     */
    public void createIceRight(int r, int c) {
        int col = c + 1; 

        while (col < width) {
            if (blocks[r][col] != null) {
                if (blocks[r][col].isBaldosaCaliente()) {
                    new IceBlock(this, r, col);
                    blocks[r][col].setHasBaldosa(true);
                } else if (blocks[r][col].isFogata()) {
                    new IceBlock(this, r, col);
                    blocks[r][col].setHasFogata(true);
                } else {
                    break;
                }
            } else if (monsters[r][col] != null || players[r][col] != null) {
                break;
            } else {
                new IceBlock(this, r, col);
            }
            col++;
        }
    } 

    /*
     * @r: fila de referencia del jugador que rompe el hielo
     * @c: columna de referencia del jugador que rompe el hielo
     * Rompe bloques de hielo hacia la derecha desde la posición (r,c)
     * y restaura fogatas o baldosas calientes cubiertas.
     * @return: no retorna nada
     */
    public void breakIceRight(int r, int c) {
        int col = c + 1;
        while (col < width) {
            Block b = blocks[r][col];
            if (b == null) {
                break;
            }
            if (!b.isIceBlock()) {
                break;
            }
            boolean hadFogata = b.hasFogata();
            boolean hadBaldosa = b.hasBaldosa();
            blocks[r][col] = null;
            if (hadFogata) {
                new Fogata(this, r, col);
            } else if (hadBaldosa) {
                new BaldosaCaliente(this, r, col);
            }
            col++;
        }
    }

    /*
     * Marca el final del nivel para el jugador 1
     * eliminándolo de la referencia principal.
     * @return: no retorna nada
     */
    public void levelOver() {player1 = null;}

    private boolean inBounds(int r, int c) {
        return r >= 0 && r < height && c >= 0 && c < width;
    }

    /**
     * Regla de pisabilidad para el jugador:
     * - null: sí
     * - iceblock / decoration: no
     * - baldosa caliente: sí
     * - fogata: sí SOLO si está inactiva
     */
    private boolean canPlayerStepOn(int r, int c) {
        if (!inBounds(r, c)) return false;

        Block b = blocks[r][c];
        if (b == null) return true;

        if (b.isIceBlock()) return false;
        if (b.isDecorationBlock()) return false;

        if (b.isBaldosaCaliente()) return true;

        if (b.isFogata()) {
            Fogata f = (Fogata) b;
            return !f.isActive(); // solo si NO está activa
        }
        return false;
    }

    /*
     * Mueve al jugador 1 una casilla en la dirección en la que está mirando,
     * siempre que la casilla sea válida (sin bloque sólido ni otro jugador).
     * Si el movimiento no se puede ejecutar, lanza una excepción.
     * @return: no retorna nada
     */
    public void movePlayer1() throws BadIceCreamException {
        if (player1 == null) return;

        int r = player1.getRow();
        int c = player1.getColumn();

        String dir = player1.getDirectionOfView();
        if (dir == null) {
            throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_MOVEMENT);
        }

        int nr = r;
        int nc = c;

        switch (dir) {
            case "left":
                nc = c - 1;
                break;
            case "right":
                nc = c + 1;
                break;
            case "up":
                nr = r - 1;
                break;
            case "down":
                nr = r + 1;
                break;
            default:
                throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_MOVEMENT);
        }

        if (!inBounds(nr, nc)) {
            throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_MOVEMENT);
        }

        if (players[nr][nc] != null) {
            throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_MOVEMENT);
        }

        if (!canPlayerStepOn(nr, nc)) {
            throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_MOVEMENT);
        }

        players[r][c] = null;
        player1.changePosition(nr, nc);
        players[nr][nc] = player1;
    }


    /*
     * Mueve al jugador 2 una casilla en la dirección en la que está mirando,
     * siempre que la casilla sea válida (sin bloque sólido ni otro jugador).
     * Si el movimiento no se puede ejecutar, lanza una excepción.
     * @return: no retorna nada
     */
    public void movePlayer2() throws BadIceCreamException {
        if (player2 == null) return;

        int r = player2.getRow();
        int c = player2.getColumn();

        String dir = player2.getDirectionOfView();
        if (dir == null) {
            throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_MOVEMENT);
        }

        int nr = r;
        int nc = c;

        switch (dir) {
            case "left":
                nc = c - 1;
                break;
            case "right":
                nc = c + 1;
                break;
            case "up":
                nr = r - 1;
                break;
            case "down":
                nr = r + 1;
                break;
            default:
                throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_MOVEMENT);
        }

        if (!inBounds(nr, nc)) {
            throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_MOVEMENT);
        }

        if (players[nr][nc] != null) {
            throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_MOVEMENT);
        }

        if (!canPlayerStepOn(nr, nc)) {
            throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_MOVEMENT);
        }

        players[r][c] = null;
        player2.changePosition(nr, nc);
        players[nr][nc] = player2;
    }

    /*
     * Retorna la referencia al primer jugador del juego.
     * @return: jugador 1 o null si no existe
     */
    public Player getFirstPlayer() {
        return player1;
    }
    
    /*
     * Retorna la referencia al segundo jugador del juego.
     * @return: jugador 2 o null si no existe
     */
    public Player getSecondPlayer() {
        return player2;
    }
    
    /*
     * Retorna la matriz de bloques del tablero.
     * @return: matriz de bloques blocks[][]
     */
    public Block[][] getBlocks() {
        return blocks;
    }

    /*
     * Retorna la matriz de frutas del tablero.
     * @return: matriz de frutas fruits[][]
     */
    public Fruit[][] getFruits() {
        return fruits;
    }

    /*
     * Retorna la matriz de jugadores del tablero.
     * @return: matriz de jugadores players[][]
     */
    public Player[][] getPlayers() {
        return players;
    }

    /*
     * Retorna la matriz de monstruos del tablero.
     * @return: matriz de monstruos monsters[][]
     */
    public Monster[][] getMonsters() {
        return monsters;
    }

    /*
     * Retorna el número de nivel actual.
     * @return: valor entero del nivel
     */
    public int getLevel() {
        return level;
    }

    /*
     * Retorna el número de fase actual del nivel.
     * @return: valor entero de la fase
     */
    public int getPhase() {
        return phase;
    }
    
    /*
     * Retorna la altura (número de filas) del tablero.
     * @return: valor entero de la altura
     */
    public int getHeight() {
        return height;
    }
    
    /*
     * Retorna la anchura (número de columnas) del tablero.
     * @return: valor entero de la anchura
     */
    public int getWidth() {
        return width;
    }
    
    /*
     * Recorre todas las frutas del tablero, las guarda en una lista
     * y llama a su método act() para que ejecuten su comportamiento.
     * Al final revisa acciones de los jugadores tras mover las frutas.
     * @return: no retorna nada
     */
    public void tickFruits() {
        ArrayList<Fruit> fruitsList = new ArrayList<>();

        for (int r = 0; r < height; r++) { 
            for (int c = 0; c < width; c++) {
                if (fruits[r][c] != null) {
                    fruitsList.add(fruits[r][c]);
                }
            }
        }
        for (Fruit fruit : fruitsList) {
            try {
                fruit.act();
            } catch (BadIceCreamException e) {              
            }
        }
        checkActionsForPlayer1();
        checkActionsForPlayer2();
    }
    
    /*
     * Recorre todos los bloques del tablero y verifica si son bloques de hielo
     * que deberían derretirse por estar sobre una baldosa caliente.
     * Si se deben derretir, se elimina el hielo y se restaura la baldosa caliente.
     * @return: no retorna nada
     */
    public void tickHotTiles() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                Block b = blocks[r][c];
                if (b != null && b.isIceBlock() && b.shouldMeltIceOverBaldosa()) {
                    blocks[r][c] = null;
                    new BaldosaCaliente(this, r, c);
                }
            }
        }
    }
    
    /*
     * @phaseNumber: número de fase para la cual se desean crear las frutas
     * Crea en el tablero las frutas correspondientes a la fase indicada
     * (leyendo de las listas fruitsPhase1 o fruitsPhase2).
     * @return: no retorna nada
     */
    private void spawnFruitsForPhase(int phaseNumber) throws BadIceCreamException {
        ArrayList<EntityData> lista;

        if (phaseNumber == 1) {
            lista = fruitsPhase1;
        } else if (phaseNumber == 2) {
            lista = fruitsPhase2;
        } else {
            return; // por ahora solo 2 fases
        }

        for (EntityData data : lista) {
            validatePositionForFruit(data.row, data.col);
            crearFruta(data.tipo, data.row, data.col);
        }
    }
    
    /*
     * @filename: nombre o ruta del archivo donde se guardará el nivel
     * Exporta el estado actual del nivel (modo de juego, jugadores,
     * monstruos, bloques y frutas) a un archivo de texto.
     * @return: no retorna nada
     */
    public void exportLevel(String filename) throws BadIceCreamException {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filename));
            
            writer.println("jugador vs jugador");
            
            if (player1 != null) {
                String tipo1 = determinarTipoJugador(player1);
                writer.println(tipo1 + " " + player1.getRow() + " " + player1.getColumn());
            } else {
                writer.println("vainilla 10 7"); 
            }
            
            if (player2 != null) {
                String tipo2 = determinarTipoJugador(player2);
                writer.println(tipo2 + " " + player2.getRow() + " " + player2.getColumn());
            } else {
                writer.println("chocolate 10 8"); 
            }
            
            // MONSTRUOS
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    if (monsters[r][c] != null) {
                        Monster monster = monsters[r][c];
                        String tipoMonstruo = determinarTipoMonstruo(monster);
                        writer.println("monster " + tipoMonstruo + " " + r + " " + c);
                    }
                }
            }

            // BLOQUES
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    if (blocks[r][c] != null) {
                        Block block = blocks[r][c];
                        String tipoBloque = determinarTipoBloque(block);
                        writer.println("block " + tipoBloque + " " + r + " " + c);
                    }
                }
            }

            // FRUTAS
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    if (fruits[r][c] != null) {
                        Fruit fruit = fruits[r][c];
                        String tipoFruta = determinarTipoFruta(fruit);
                        writer.println("fruit " + tipoFruta + " " + r + " " + c);
                    }
                }
            }
            
            writer.close();
            
        } catch (IOException e) {
            throw new BadIceCreamException("Error al escribir el archivo: " + e.getMessage());
        }
    }

    /*
     * @player: jugador para el cual se quiere determinar el tipo
     * Determina el tipo textual de jugador (vainilla o chocolate) para exportarlo.
     * @return: cadena con el tipo de jugador
     */
    private String determinarTipoJugador(Player player) {
        if (player == player1) {
            return "vainilla";
        } else {
            return "chocolate";
        }
    }

    /*
     * @monster: monstruo del cual se quiere obtener el tipo
     * Devuelve el tipo del monstruo en minúsculas para guardarlo en el archivo.
     * @return: cadena con el tipo de monstruo
     */
    private String determinarTipoMonstruo(Monster monster) {
        return monster.getMonsterType().toLowerCase();
    }

    /*
     * @block: bloque del cual se quiere obtener el tipo
     * Determina el tipo de bloque a partir de su blockType y lo traduce
     * a la palabra usada en el archivo de niveles.
     * @return: cadena con el tipo de bloque
     */
    private String determinarTipoBloque(Block block) {
        String tipo = block.getBlockType();
        
        if ("ICE".equals(tipo)) {
            return "iceblock";
        } else if ("DECORATION".equals(tipo)) {
            return "decoration";
        } else if ("FOGATA".equals(tipo)) {
            return "fogata";
        } else if ("BALDOSA_CALIENTE".equals(tipo)) {
            return "baldosa";
        }
        return "iceblock";
    }

    /*
     * @fruit: fruta de la cual se quiere obtener el tipo
     * Determina el tipo de fruta a partir de su fruitType y lo traduce
     * a la palabra usada en el archivo de niveles.
     * @return: cadena con el tipo de fruta
     */
    private String determinarTipoFruta(Fruit fruit) {
        String tipo = fruit.getFruitType();
        
        if ("BANANA".equals(tipo)) {
            return "platano";
        } else if ("GRAPE".equals(tipo)) {
            return "uva";
        } else if ("CACTUS".equals(tipo)) {
            return "cactus";
        } else if ("CHERRY".equals(tipo)) {
            return "cereza";
        } else if ("PINEAPPLE".equals(tipo)) {
            return "pina";
        }
        return "platano";
    }
    /*
     * @filename: nombre o ruta del archivo a importar
     * Importa un nivel desde un archivo de texto, leyendo modo de juego,
     * posiciones de jugadores, monstruos, bloques y frutas, y llenando
     * las estructuras internas del tablero.
     * @return: no retorna nada
     */
    public void importLevel(String filename) throws BadIceCreamException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            // Limpia el tablero y estructuras internas
            clearBoardForImport();

            // Leer modo de juego
            String mode = reader.readLine();
            if (mode == null || !mode.trim().equals("jugador vs jugador")) {
                reader.close();
                throw new BadIceCreamException(BadIceCreamException.INVALID_LEVEL_FORMAT);
            }

            // ---- JUGADOR 1 ----
            String player1Line = reader.readLine();
            if (player1Line == null) {
                reader.close();
                throw new BadIceCreamException(BadIceCreamException.INVALID_LEVEL_FORMAT);
            }
            String[] player1Data = player1Line.trim().split("\\s+");
            if (player1Data.length != 3) {
                reader.close();
                throw new BadIceCreamException(BadIceCreamException.INVALID_LEVEL_FORMAT);
            }
            String tipoJugador1 = player1Data[0].toLowerCase();
            int p1Row = Integer.parseInt(player1Data[1]);
            int p1Col = Integer.parseInt(player1Data[2]);

            // ---- JUGADOR 2 ----
            String player2Line = reader.readLine();
            if (player2Line == null) {
                reader.close();
                throw new BadIceCreamException(BadIceCreamException.INVALID_LEVEL_FORMAT);
            }
            String[] player2Data = player2Line.trim().split("\\s+");
            if (player2Data.length != 3) {
                reader.close();
                throw new BadIceCreamException(BadIceCreamException.INVALID_LEVEL_FORMAT);
            }
            String tipoJugador2 = player2Data[0].toLowerCase();
            int p2Row = Integer.parseInt(player2Data[1]);
            int p2Col = Integer.parseInt(player2Data[2]);

            // Validar tipos de jugador
            if (!esTipoJugadorValido(tipoJugador1) || !esTipoJugadorValido(tipoJugador2)) {
                reader.close();
                throw new BadIceCreamException("Tipo de jugador inválido. Use: vainilla, chocolate o fresa");
            }

            // Validar posiciones de jugadores
            validatePositionForPlayer(p1Row, p1Col);
            validatePositionForPlayer(p2Row, p2Col);

            // No pueden estar en la misma casilla
            if (p1Row == p2Row && p1Col == p2Col) {
                reader.close();
                throw new BadIceCreamException(BadIceCreamException.PLAYER_ON_PLAYER);
            }

            // Crear jugadores usando los sabores seleccionados si existen, sino usar los del archivo
            String flavor1 = selectedPlayer1Flavor != null ? selectedPlayer1Flavor : tipoJugador1;
            String flavor2 = selectedPlayer2Flavor != null ? selectedPlayer2Flavor : tipoJugador2;
            player1 = new Player(this, p1Row, p1Col, flavor1);
            player2 = new Player(this, p2Row, p2Col, flavor2);
            players[p1Row][p1Col] = player1;
            players[p2Row][p2Col] = player2;

            // Listas temporales para monstruos y bloques
            ArrayList<EntityData> monstruosData = new ArrayList<>();
            ArrayList<EntityData> bloquesData = new ArrayList<>();

            // Listas de instancia para frutas por fase
            fruitsPhase1.clear();
            fruitsPhase2.clear();
            int currentPhaseFile = 1; // por defecto, todo va a fase 1 si no se indica lo contrario

            // Leer el resto del archivo
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");

                // Línea que define la fase: "phase 1" o "phase 2"
                if (parts[0].equalsIgnoreCase("phase")) {
                    if (parts.length != 2) {
                        reader.close();
                        throw new BadIceCreamException(BadIceCreamException.INVALID_LEVEL_FORMAT);
                    }
                    try {
                        currentPhaseFile = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        reader.close();
                        throw new BadIceCreamException(BadIceCreamException.INVALID_LEVEL_FORMAT);
                    }
                    continue; // pasar a la siguiente línea
                }

                // El resto deben ser siempre 4 campos: categoria tipo row col
                if (parts.length != 4) {
                    reader.close();
                    throw new BadIceCreamException(BadIceCreamException.INVALID_LEVEL_FORMAT);
                }

                String categoria = parts[0].toLowerCase();
                String tipo = parts[1].toLowerCase();
                int row = Integer.parseInt(parts[2]);
                int col = Integer.parseInt(parts[3]);

                switch (categoria) {
                    case "monster":
                        if (!esTipoMonstruoValido(tipo)) {
                            reader.close();
                            throw new BadIceCreamException("Tipo de monstruo inválido. Use: troll, calamar o maceta");
                        }
                        monstruosData.add(new EntityData(tipo, row, col));
                        break;

                    case "block":
                        if (!esTipoBloqueValido(tipo)) {
                            reader.close();
                            throw new BadIceCreamException("Tipo de bloque inválido.");
                        }
                        bloquesData.add(new EntityData(tipo, row, col));
                        break;

                    case "fruit":
                        if (!esTipoFrutaValida(tipo)) {
                            reader.close();
                            throw new BadIceCreamException("Tipo de fruta inválido.");
                        }
                        // Enviar la fruta a la lista de la fase actual
                        if (currentPhaseFile == 1) {
                            fruitsPhase1.add(new EntityData(tipo, row, col));
                        } else if (currentPhaseFile == 2) {
                            fruitsPhase2.add(new EntityData(tipo, row, col));
                        } else {
                            // Si quisieras soportar más fases, extiendes aquí
                            fruitsPhase1.add(new EntityData(tipo, row, col)); // fallback simple
                        }
                        break;

                    default:
                        reader.close();
                        throw new BadIceCreamException(BadIceCreamException.INVALID_LEVEL_FORMAT);
                }
            }

            // Crear bloques
            for (EntityData data : bloquesData) {
                validatePositionForBlock(data.row, data.col);
                Block block = crearBloque(data.tipo, data.row, data.col);
                blocks[data.row][data.col] = block;
                InteractorsWithPlayer.add(block); // Agregar a la lista de interactores
            }

            // Crear monstruos
            for (EntityData data : monstruosData) {
                validatePositionForMonster(data.row, data.col);
                Monster monster = crearMonstruo(data.tipo, data.row, data.col);
                monsters[data.row][data.col] = monster;
                InteractorsWithPlayer.add(monster); // Agregar a la lista de interactores
            }

            // Crear frutas SOLO de la fase 1 al iniciar el nivel
            spawnFruitsForPhase(1);

            reader.close();

        } catch (FileNotFoundException e) {
            throw new BadIceCreamException(BadIceCreamException.FILE_NOT_FOUND);
        } catch (IOException e) {
            throw new BadIceCreamException("Error al leer el archivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new BadIceCreamException(BadIceCreamException.INVALID_LEVEL_FORMAT);
        }
    }

    /*
     * @tipo: texto del tipo de jugador leído del archivo
     * Verifica si el tipo de jugador es uno de los permitidos (vainilla, chocolate o fresa).
     * @return: true si es válido, false en caso contrario
     */
    private boolean esTipoJugadorValido(String tipo) {
        return tipo.equals("vainilla") || tipo.equals("chocolate") || tipo.equals("fresa");
    }

    /*
     * @tipo: texto del tipo de monstruo leído del archivo
     * Verifica si el tipo de monstruo es uno de los permitidos (troll, calamar, maceta).
     * @return: true si es válido, false en caso contrario
     */
    private boolean esTipoMonstruoValido(String tipo) {
        return tipo.equals("troll") || tipo.equals("calamar") || tipo.equals("maceta");
    }

    /*
     * @tipo: texto del tipo de bloque leído del archivo
     * Verifica si el tipo de bloque es uno de los permitidos
     * (iceblock, decoration, fogata, baldosa).
     * @return: true si es válido, false en caso contrario
     */
    private boolean esTipoBloqueValido(String tipo) {
        return tipo.equals("iceblock") || tipo.equals("decoration") 
               || tipo.equals("fogata") || tipo.equals("baldosa");
    }

    /*
     * @tipo: texto del tipo de fruta leído del archivo
     * Verifica si el tipo de fruta es uno de los permitidos
     * (platano, uva, cactus, cereza, pina).
     * @return: true si es válido, false en caso contrario
     */
    private boolean esTipoFrutaValida(String tipo) {
        return tipo.equals("platano") || tipo.equals("uva") || 
               tipo.equals("cactus") || tipo.equals("cereza") || 
               tipo.equals("pina");
    }

    /*
     * @tipo: tipo de monstruo en texto ("troll", "calamar", "maceta")
     * @row: fila donde se creará el monstruo
     * @col: columna donde se creará el monstruo
     * Crea y retorna una instancia del monstruo correspondiente al tipo dado.
     * @return: instancia de Monster creada
     */
    private Monster crearMonstruo(String tipo, int row, int col) throws BadIceCreamException {
        switch (tipo.toLowerCase()) {
            case "troll":
                return new Troll(this, row, col);
            case "calamar":
                return new CalamarNaranja(this, row, col);
            case "maceta":
                return new Maceta(this, row, col);
            default:
                return new Troll(this, row, col); 
        }
    }

    /*
     * @tipo: tipo de bloque en texto ("iceblock", "decoration", "fogata", "baldosa")
     * @row: fila donde se creará el bloque
     * @col: columna donde se creará el bloque
     * Crea y retorna una instancia del bloque correspondiente al tipo dado.
     * @return: instancia de Block creada
     */
    private Block crearBloque(String tipo, int row, int col) throws BadIceCreamException {
        switch (tipo.toLowerCase()) {
            case "iceblock":
                return new IceBlock(this, row, col);
            case "decoration":
                return new DecorationBlock(this, row, col);
            case "fogata":
                return new Fogata(this, row, col);
            case "baldosa":
                return new BaldosaCaliente(this, row, col);
            default:
                return new IceBlock(this, row, col);
        }
    }

    /*
     * @tipo: tipo de fruta en texto ("platano", "uva", "cactus", "cereza", "pina")
     * @row: fila donde se creará la fruta
     * @col: columna donde se creará la fruta
     * Crea y retorna una instancia de la fruta correspondiente al tipo dado.
     * @return: instancia de Fruit creada
     */
    private Fruit crearFruta(String tipo, int row, int col) throws BadIceCreamException {
        Fruit fruit;
        switch (tipo.toLowerCase()) {
            case "platano":
                fruit = new Platano(this, row, col);
                break;
            case "uva":
                fruit = new Uva(this, row, col);
                break;
            case "cactus":
                fruit = new Cactus(this, row, col);
                break;
            case "cereza":
                fruit = new Cereza(this, row, col);
                break;
            case "pina":
                fruit = new Piña(this, row, col);
                break;
            default:
                fruit = new Platano(this, row, col);
        }
        fruits[row][col] = fruit; // Asignar a la matriz
        InteractorsWithPlayer.add(fruit); // Agregar a la lista de interactores
        return fruit;
    }

    /*
     * Clase interna que almacena información básica de una entidad (tipo y posición)
     * usada para construir monstruos, bloques o frutas después de leer el archivo.
     */
    private static class EntityData implements Serializable{
        String tipo;
        int row;
        int col;
        
        /*
         * @tipo: tipo de entidad (monster, block, fruit)
         * @row: fila de la entidad
         * @col: columna de la entidad
         * Constructor que guarda los datos de una entidad leída del archivo.
         * @return: no retorna nada
         */
        EntityData(String tipo, int row, int col) {
            this.tipo = tipo;
            this.row = row;
            this.col = col; 
        }
    }

    /*
     * @row: fila a validar para colocar un jugador
     * @col: columna a validar para colocar un jugador
     * Verifica que la posición esté dentro del tablero y que no esté ocupada por
     * bloques no permitidos, otro jugador o un monstruo.
     * @return: no retorna nada, pero lanza excepción si la posición es inválida
     */
    private void validatePositionForPlayer(int row, int col) throws BadIceCreamException {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            throw new BadIceCreamException(BadIceCreamException.INVALID_POSITION);
        }

        if (blocks[row][col] != null) {
            if (blocks[row][col].isIceBlock()) {
                throw new BadIceCreamException(BadIceCreamException.UNIT_ON_ICE_BLOCK);
            }
            if (blocks[row][col].isDecorationBlock()) {
                throw new BadIceCreamException(BadIceCreamException.UNIT_ON_DECORATION_BLOCK);
            }
        }

        if (players[row][col] != null) {
            throw new BadIceCreamException(BadIceCreamException.PLAYER_ON_PLAYER);
        }
        if (monsters[row][col] != null) {
            throw new BadIceCreamException(BadIceCreamException.PLAYER_ON_MONSTER);
        }
    }

    /*
     * @row: fila a validar para colocar un monstruo
     * @col: columna a validar para colocar un monstruo
     * Verifica que la posición esté dentro del tablero y libre de jugadores,
     * otros monstruos o bloques no permitidos.
     * @return: no retorna nada, pero lanza excepción si la posición es inválida
     */
    private void validatePositionForMonster(int row, int col) throws BadIceCreamException {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            throw new BadIceCreamException(BadIceCreamException.INVALID_POSITION);
        }

        if (blocks[row][col] != null) {
            if (blocks[row][col].isIceBlock()) {
                throw new BadIceCreamException(BadIceCreamException.UNIT_ON_ICE_BLOCK);
            }
            if (blocks[row][col].isDecorationBlock()) {
                throw new BadIceCreamException(BadIceCreamException.UNIT_ON_DECORATION_BLOCK);
            }
        }

        if (players[row][col] != null) {
            throw new BadIceCreamException(BadIceCreamException.PLAYER_ON_MONSTER);
        }
        if (monsters[row][col] != null) {
            throw new BadIceCreamException(BadIceCreamException.MONSTER_ON_MONSTER);
        }
    }

    /*
     * @row: fila a validar para colocar un bloque
     * @col: columna a validar para colocar un bloque
     * Valida que la posición esté dentro de los límites y que no haya ya
     * un bloque, jugador o monstruo en esa casilla.
     * @return: no retorna nada, pero lanza excepción si la posición es inválida
     */
    private void validatePositionForBlock(int row, int col) throws BadIceCreamException {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            throw new BadIceCreamException(BadIceCreamException.INVALID_POSITION);
        }
        if (blocks[row][col] != null) {
            throw new BadIceCreamException(BadIceCreamException.MONSTER_ON_MONSTER);
        }
        if (players[row][col] != null || monsters[row][col] != null) {
            throw new BadIceCreamException(BadIceCreamException.INVALID_POSITION);
        }
    }

    /*
     * @row: fila a validar para colocar una fruta
     * @col: columna a validar para colocar una fruta
     * Verifica que la posición esté dentro del tablero, sin frutas previas,
     * sin bloques de hielo/decoración, ni jugadores o monstruos.
     * @return: no retorna nada, pero lanza excepción si la posición es inválida
     */
    private void validatePositionForFruit(int row, int col) throws BadIceCreamException {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            throw new BadIceCreamException(BadIceCreamException.INVALID_POSITION);
        }
        if (fruits[row][col] != null) {
            throw new BadIceCreamException(BadIceCreamException.INVALID_POSITION);
        }
        if (blocks[row][col] != null) {
            if (blocks[row][col].isIceBlock() || blocks[row][col].isDecorationBlock()) {
                throw new BadIceCreamException(BadIceCreamException.UNIT_ON_ICE_BLOCK);
            }
        }
        if (players[row][col] != null || monsters[row][col] != null) {
            throw new BadIceCreamException(BadIceCreamException.INVALID_POSITION);
        }
    }

    /*
     * Limpia por completo el tablero antes de importar un nivel nuevo,
     * borrando jugadores, monstruos, frutas y bloques, reiniciando listas
     * de unidades y de frutas por fase, y dejando la fase en 1.
     * @return: no retorna nada
     */
    private void clearBoardForImport() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                players[r][c] = null;
                monsters[r][c] = null;
                fruits[r][c] = null;
                blocks[r][c] = null;
            }
        }
        
        player1 = null;
        player2 = null;
        
        units.clear();
        InteractorsWithPlayer.clear(); // Limpiar la lista de interactores

        fruitsPhase1.clear();
        fruitsPhase2.clear(); 
        phase = 1;  
    }

    /*
     * Verifica si en el tablero ya no quedan frutas (todas las posiciones
     * de la matriz de frutas son null), es decir, si la fase se ha completado.
     * @return: true si la fase está limpia de frutas, false en caso contrario
     */
    public boolean isPhaseCleared() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (fruits[r][c] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * Avanza de la fase 1 a la fase 2 si todas las frutas de la fase 1
     * ya han sido recogidas. Crea las frutas correspondientes a la fase 2.
     * @return: no retorna nada
     */
    public void goToNextPhase() throws BadIceCreamException {
        if (phase == 1 && isPhaseCleared()) {
            phase = 2;
            spawnFruitsForPhase(2);
        }
    } 
    
    public String getWinner() {
        if (player1 == null && player2 == null) {
            return "EMPATE";
        }
        if (player1 == null) {
            return "JUGADOR 2";
        }
        if (player2 == null) {
            return "JUGADOR 1";
        }
        
        int score1 = player1.getScore();
        int score2 = player2.getScore();
        
        if (score1 > score2) {
            return "JUGADOR 1";
        } else if (score2 > score1) {
            return "JUGADOR 2";
        } else {
            return "EMPATE";
        }
    }
    
    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer1(Player p) {
        this.player1 = p;
    }

    public void setPlayer2(Player p) {
        this.player2 = p;
    }
    
    /**
     * Guarda el estado completo del juego usando serialización
     */
    public void saveGame(String filepath) throws BadIceCreamException {
        try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            throw new BadIceCreamException("Error al guardar el juego: " + e.getMessage());
        }
    }
    
    /**
     * Carga un juego guardado previamente
     */
    public static BadIceCream openGame(String filepath) throws BadIceCreamException {
        try {
            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            BadIceCream game = (BadIceCream) in.readObject();
            in.close();
            fileIn.close();
            return game;
        } catch (IOException e) {
            throw new BadIceCreamException("Error al abrir el juego: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new BadIceCreamException("Archivo de juego corrupto o inválido");
        }
    }
    

    /*
     * @row: fila de referencia (normalmente la del jugador)
     * @col: columna de referencia (normalmente la del jugador)
     * Busca la fruta más cercana en el tablero usando distancia Manhattan.
     * @return: arreglo {fila, columna} de la fruta más cercana o null si no hay frutas
     */
    private int[] findNearestFruit(int row, int col) {
        int bestDist = Integer.MAX_VALUE;
        int[] bestPos = null;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (fruits[r][c] != null) {
                    int d = Math.abs(r - row) + Math.abs(c - col);
                    if (d < bestDist) {
                        bestDist = d;
                        bestPos = new int[]{r, c};
                    }
                }
            }
        }
        return bestPos;
    }
    
    /*
     * @p: jugador que se quiere mover
     * @newRow: nueva fila a la que se quiere mover
     * @newCol: nueva columna a la que se quiere mover
     * Verifica si la casilla (newRow,newCol) es válida para que el jugador p se mueva:
     * - Está dentro del tablero
     * - No hay otro jugador
     * - No hay monstruo
     * - No hay bloque sólido (permite nulo y fogata)
     * @return: true si el movimiento es válido, false en caso contrario
     */
    private boolean canMoveTo(Player p, int newRow, int newCol) {
        if (p == null) return false;
        
        if (newRow < 0 || newRow >= height || newCol < 0 || newCol >= width) {
            return false;
        }
        
        if (players[newRow][newCol] != null && players[newRow][newCol] != p) {
            return false;
        }
        
        if (monsters[newRow][newCol] != null) {
            return false;
        }
        
        Block b = blocks[newRow][newCol];
        if (b != null && !b.isFogata()) {
            return false;
        }
        return true;
    }
    
    /*
     * Mueve automáticamente al jugador controlado por la máquina (player1).
     * Estrategia:
     *  1. Busca la fruta más cercana.
     *  2. Evalúa los 4 movimientos posibles (arriba, abajo, izquierda, derecha).
     *  3. Escoge el movimiento válido que más acerque a la fruta.
     *  4. Si no hay frutas o no hay movimiento que acerque, intenta un movimiento válido aleatorio.
     * Después de moverse se revisan las acciones habituales (monstruos, frutas, bloques).
     * @return: no retorna nada
     */
    public void moveMachinePlayer1() throws BadIceCreamException {
        if (player1 == null) return;
        Player p = player1;
        int currentRow = p.getRow();
        int currentCol = p.getColumn();
        
        int[] target = findNearestFruit(currentRow, currentCol);
        
        int[][] directions = {
            {-1, 0}, 
            {1, 0},  
            {0, -1}, 
            {0, 1}   
        };
        
        int bestRow = currentRow;
        int bestCol = currentCol;
        boolean foundBetter = false;
        
        if (target != null) {
            int targetRow = target[0];
            int targetCol = target[1];
            int bestDist = Math.abs(targetRow - currentRow) + Math.abs(targetCol - currentCol);
            
            for (int[] dir : directions) {
                int nr = currentRow + dir[0];
                int nc = currentCol + dir[1];
                if (canMoveTo(p, nr, nc)) {
                    int d = Math.abs(targetRow - nr) + Math.abs(targetCol - nc);
                    if (d < bestDist) {
                        bestDist = d;
                        bestRow = nr;
                        bestCol = nc;
                        foundBetter = true;
                    }
                }
            }
        }
        
        if (!foundBetter) {
            java.util.ArrayList<int[]> validMoves = new java.util.ArrayList<>();
            for (int[] dir : directions) {
                int nr = currentRow + dir[0];
                int nc = currentCol + dir[1];
                if (canMoveTo(p, nr, nc)) {
                    validMoves.add(new int[]{nr, nc});
                }
            }
            if (!validMoves.isEmpty()) {
                java.util.Random rand = new java.util.Random();
                int[] chosen = validMoves.get(rand.nextInt(validMoves.size()));
                bestRow = chosen[0];
                bestCol = chosen[1];
            } else {
                return;
            }
        }
        
        players[currentRow][currentCol] = null;
        
        if (bestRow < currentRow) p.changeOfView("up");
        else if (bestRow > currentRow) p.changeOfView("down");
        else if (bestCol < currentCol) p.changeOfView("left");
        else if (bestCol > currentCol) p.changeOfView("right");
        
        p.changePosition(bestRow, bestCol);
        players[bestRow][bestCol] = p;
        
        checkActionsForPlayer1();
    }
     
    /*
     * Mueve automáticamente al jugador controlado por la máquina (player2).
     * Estrategia:
     *  1. Busca la fruta más cercana.
     *  2. Evalúa los 4 movimientos posibles (arriba, abajo, izquierda, derecha).
     *  3. Escoge el movimiento válido que más acerque a la fruta.
     *  4. Si no hay frutas o no hay movimiento que acerque, intenta un movimiento válido aleatorio.
     * Después de moverse se revisan las acciones habituales (monstruos, frutas, bloques).
     * @return: no retorna nada
     */
    public void moveMachinePlayer2() throws BadIceCreamException {
        if (player2 == null) return;
        Player p = player2;
        int currentRow = p.getRow();
        int currentCol = p.getColumn();
        
        int[] target = findNearestFruit(currentRow, currentCol);
        
        int[][] directions = {
            {-1, 0}, 
            {1, 0},  
            {0, -1}, 
            {0, 1}   
        };
        
        int bestRow = currentRow;
        int bestCol = currentCol;
        boolean foundBetter = false;
        
        if (target != null) {
            int targetRow = target[0];
            int targetCol = target[1];
            int bestDist = Math.abs(targetRow - currentRow) + Math.abs(targetCol - currentCol);
            
            for (int[] dir : directions) {
                int nr = currentRow + dir[0];
                int nc = currentCol + dir[1];
                if (canMoveTo(p, nr, nc)) {
                    int d = Math.abs(targetRow - nr) + Math.abs(targetCol - nc);
                    if (d < bestDist) {
                        bestDist = d;
                        bestRow = nr;
                        bestCol = nc;
                        foundBetter = true;
                    }
                }
            }
        }
        
        if (!foundBetter) {
            java.util.ArrayList<int[]> validMoves = new java.util.ArrayList<>();
            for (int[] dir : directions) {
                int nr = currentRow + dir[0];
                int nc = currentCol + dir[1];
                if (canMoveTo(p, nr, nc)) {
                    validMoves.add(new int[]{nr, nc});
                }
            }
            if (!validMoves.isEmpty()) {
                java.util.Random rand = new java.util.Random();
                int[] chosen = validMoves.get(rand.nextInt(validMoves.size()));
                bestRow = chosen[0];
                bestCol = chosen[1];
            } else {
                return;
            }
        }
        
        players[currentRow][currentCol] = null;
        
        if (bestRow < currentRow) p.changeOfView("up");
        else if (bestRow > currentRow) p.changeOfView("down");
        else if (bestCol < currentCol) p.changeOfView("left");
        else if (bestCol > currentCol) p.changeOfView("right");
        
        p.changePosition(bestRow, bestCol);
        players[bestRow][bestCol] = p;
        
        checkActionsForPlayer2();
    }
    
}