package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import domain.*;

/**
 * Clase principal de la interfaz gráfica del juego Bad Ice Cream.
 * Maneja todas las pantallas del juego: menú inicial, selección de personajes,
 * juego principal, victoria y game over.
 *
 * @author Neco-Arc Team
 * @version 1.0
 */
public class BadIceCreamGUI extends JFrame {

    private JPanel initialPanel;
    private JButton start;
    private JPanel electionModePanel;
    private JButton pvp;

    private JPanel playerSelectionPanel;
    private String player1Flavor = null;
    private String player2Flavor = null;
    private Point player1SelectedPos = null;
    private Point player2SelectedPos = null;

    private JPanel electionLevelPanel;
    private JButton level1;
    private JButton level2;
    private JButton level3;
    private JPanel mainGame;
    private JMenuBar menuBar;
    private JMenu menuOption;
    private JMenuItem exit;
    private JMenuItem news;
    private JMenuItem importLevel;
    private JMenuItem exportLevel;
    private JMenuItem saveGame;
    private JMenuItem openGame;

    private JPanel gameOverPanel;
    private JPanel victoryPanel;

    private BadIceCream juego;
    private int currentLevel = 1;

    private Timer gameTimer;
    private Timer monsterTimer;

    private boolean isMachinePlayer1 = false;
    private boolean isMachinePlayer2 = false;

    /**
     * Constructor principal de la GUI.
     * Inicializa todos los elementos visuales y acciones.
     */
    public BadIceCreamGUI() {
        prepareElements();
        prepareActions();
    }

    /**
     * Carga una imagen desde diferentes ubicaciones posibles.
     * Intenta cargar desde recursos JAR, directorio del proyecto, y carpeta src.
     *
     * @param relativePath Ruta relativa de la imagen
     * @return Image cargada o null si no se encontró
     */
    private Image loadImageUniversal(String relativePath) {
        try {
            java.net.URL imgURL = getClass().getResource(relativePath);
            if (imgURL != null) {
                return new ImageIcon(imgURL).getImage();
            }

            String projectPath = System.getProperty("user.dir");
            String fullPath = projectPath + relativePath.replace("/", java.io.File.separator);
            java.io.File file = new java.io.File(fullPath);
            if (file.exists()) {
                return new ImageIcon(fullPath).getImage();
            }

            fullPath = projectPath + "/src" + relativePath.replace("/", java.io.File.separator);
            file = new java.io.File(fullPath);
            if (file.exists()) {
                return new ImageIcon(fullPath).getImage();
            }

            System.out.println("⚠ No se pudo cargar: " + relativePath);
            return null;

        } catch (Exception e) {
            System.out.println("✗ Error cargando imagen: " + relativePath);
            return null;
        }
    }

    /**
     * Prepara todos los elementos visuales de la ventana principal.
     * Configura el tamaño, título y paneles iniciales.
     */
    private void prepareElements() {
        setTitle("Bad Ice Cream");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        Image tempImage = loadImageUniversal("/images/bad_Ice_Cream_initial_screen.png");

        if (tempImage != null) {
            int width = tempImage.getWidth(null);
            int height = tempImage.getHeight(null);
            setSize(width, height + 50);
        } else {
            setSize(800, 600);
        }

        setLocationRelativeTo(null);

        prepareElementsInitialPanel();
        prepareElementsElectionMode();
        prepareElementsPlayerSelection();
        prepareElementsElectionLevel();
        prepareElementsMenu();
        prepareElementsGameOver();
        prepareElementsVictory();

        setContentPane(initialPanel);
        setVisible(true);
    }

    /**
     * Prepara el panel inicial del juego con el botón PLAY.
     */
    private void prepareElementsInitialPanel() {
        initialPanel = new JPanel() {
            private Image backgroundImage;

            {
                backgroundImage = loadImageUniversal("/images/bad_Ice_Cream_initial_screen.png");
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(135, 206, 250));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        initialPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        start = new JButton("PLAY");
        start.setFont(new Font("Arial", Font.BOLD, 40));
        start.setPreferredSize(new Dimension(200, 80));
        start.setFocusPainted(false);
        start.setBackground(new Color(50, 205, 50));
        start.setForeground(Color.WHITE);
        start.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

        buttonPanel.add(start);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));

        initialPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Prepara el panel de Game Over (derrota).
     * Muestra imagen de derrota y botones de reiniciar/menú.
     */
    private void prepareElementsGameOver() {
        gameOverPanel = new JPanel() {
            private Image gameOverImage;

            {
                gameOverImage = loadImageUniversal("/images/lose.png");
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (gameOverImage != null) {
                    g.drawImage(gameOverImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(139, 0, 0));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 60));
                    g.drawString("PERDISTE", getWidth()/2 - 150, getHeight()/2 - 50);
                    g.setFont(new Font("Arial", Font.BOLD, 30));
                    g.drawString("LOS MONSTRUOS TE COMIERON", getWidth()/2 - 250, getHeight()/2 + 20);
                }
            }
        };

        gameOverPanel.setLayout(null);

        JButton restartButton = new JButton("REINICIAR");
        restartButton.setFont(new Font("Arial", Font.BOLD, 28));
        restartButton.setBackground(new Color(255, 140, 0));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        restartButton.setBounds(390, 520, 220, 70);

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        JButton menuButton = new JButton("MENÚ");
        menuButton.setFont(new Font("Arial", Font.BOLD, 28));
        menuButton.setBackground(new Color(70, 130, 180));
        menuButton.setForeground(Color.WHITE);
        menuButton.setFocusPainted(false);
        menuButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        menuButton.setBounds(670, 520, 220, 70);

        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopGameTimers();
                juego = null;
                setContentPane(initialPanel);
                revalidate();
                repaint();
            }
        });

        gameOverPanel.add(restartButton);
        gameOverPanel.add(menuButton);
    }

    /**
     * Prepara el panel de victoria.
     * Muestra los puntajes de ambos jugadores y quién ganó.
     */
    private void prepareElementsVictory() {
        victoryPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(255, 215, 0),
                        getWidth(), getHeight(), new Color(255, 140, 0)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 80));
                String texto = "¡GANASTE!";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(texto)) / 2;
                g2d.drawString(texto, x, 150);

                if (juego != null) {
                    g2d.setFont(new Font("Arial", Font.BOLD, 40));
                    g2d.setColor(new Color(70, 130, 255));

                    Player p1 = juego.getPlayer1();
                    Player p2 = juego.getPlayer2();

                    int score1 = (p1 != null) ? p1.getScore() : 0;
                    int score2 = (p2 != null) ? p2.getScore() : 0;

                    String p1Score = "JUGADOR 1: " + score1 + " puntos";
                    String p2Score = "JUGADOR 2: " + score2 + " puntos";

                    int x1 = (getWidth() - g2d.getFontMetrics().stringWidth(p1Score)) / 2;
                    int x2 = (getWidth() - g2d.getFontMetrics().stringWidth(p2Score)) / 2;

                    g2d.drawString(p1Score, x1, 250);
                    g2d.drawString(p2Score, x2, 310);

                    String winner = juego.getWinner();
                    g2d.setFont(new Font("Arial", Font.BOLD, 50));
                    g2d.setColor(new Color(255, 215, 0));
                    String winnerText = winner.equals("EMPATE") ? "¡EMPATE!" : "¡GANÓ " + winner + "!";
                    int xWinner = (getWidth() - g2d.getFontMetrics().stringWidth(winnerText)) / 2;
                    g2d.drawString(winnerText, xWinner, 400);
                }

                g2d.setFont(new Font("Arial", Font.PLAIN, 24));
                g2d.setColor(Color.WHITE);
                String mensaje = "¡Completaste el nivel!";
                fm = g2d.getFontMetrics();
                x = (getWidth() - fm.stringWidth(mensaje)) / 2;
                g2d.drawString(mensaje, x, 480);
            }
        };

        victoryPanel.setLayout(null);

        JButton nextLevelButton = new JButton("SIGUIENTE NIVEL");
        nextLevelButton.setFont(new Font("Arial", Font.BOLD, 28));
        nextLevelButton.setBackground(new Color(50, 205, 50));
        nextLevelButton.setForeground(Color.WHITE);
        nextLevelButton.setFocusPainted(false);
        nextLevelButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        nextLevelButton.setBounds(390, 520, 280, 70);

        nextLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentLevel++;
                if (currentLevel > 3) {
                    currentLevel = 1;
                }
                restartGame();
            }
        });

        JButton menuButton = new JButton("MENÚ");
        menuButton.setFont(new Font("Arial", Font.BOLD, 28));
        menuButton.setBackground(new Color(70, 130, 180));
        menuButton.setForeground(Color.WHITE);
        menuButton.setFocusPainted(false);
        menuButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        menuButton.setBounds(720, 520, 220, 70);

        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopGameTimers();
                juego = null;
                setContentPane(initialPanel);
                revalidate();
                repaint();
            }
        });

        victoryPanel.add(nextLevelButton);
        victoryPanel.add(menuButton);
    }

    /**
     * Prepara el panel de selección de modo de juego.
     */
    private void prepareElementsElectionMode() {
        electionModePanel = new JPanel() {
            private Image backgroundImage;

            {
                backgroundImage = loadImageUniversal("/images/default_image_background.png");
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(135, 206, 250));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        electionModePanel.setLayout(null);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBounds(0, 0, 1350, 720);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;

        JLabel titleMode = new JLabel("SELECCIONA EL MODO");
        titleMode.setFont(new Font("Arial", Font.BOLD, 30));
        titleMode.setForeground(Color.WHITE);
        gbc.gridy = 0;
        centerPanel.add(titleMode, gbc);

        // Botón Jugador vs Jugador
        pvp = new JButton("Jugador vs Jugador");
        pvp.setFont(new Font("Arial", Font.BOLD, 20));
        pvp.setPreferredSize(new Dimension(300, 60));
        pvp.setBackground(new Color(255, 140, 0));
        pvp.setForeground(Color.WHITE);
        pvp.setFocusPainted(false);
        gbc.gridy = 1;
        centerPanel.add(pvp, gbc);

        // Botón Jugador vs Máquina
        JButton pvMachine = new JButton("Jugador vs Máquina");
        pvMachine.setFont(new Font("Arial", Font.BOLD, 20));
        pvMachine.setPreferredSize(new Dimension(300, 60));
        pvMachine.setBackground(new Color(50, 205, 50));
        pvMachine.setForeground(Color.WHITE);
        pvMachine.setFocusPainted(false);
        gbc.gridy = 2;
        centerPanel.add(pvMachine, gbc);

        // Botón Máquina vs Máquina
        JButton machineVsMachine = new JButton("Máquina vs Máquina");
        machineVsMachine.setFont(new Font("Arial", Font.BOLD, 20));
        machineVsMachine.setPreferredSize(new Dimension(300, 60));
        machineVsMachine.setBackground(new Color(138, 43, 226));
        machineVsMachine.setForeground(Color.WHITE);
        machineVsMachine.setFocusPainted(false);
        gbc.gridy = 3;
        centerPanel.add(machineVsMachine, gbc);

        // Acción Jugador vs Máquina
        pvMachine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionSelectMode("PvMachine");
            }
        });

        // Acción Máquina vs Máquina
        machineVsMachine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionSelectMode("MachineVsMachine");
            }
        });

        electionModePanel.add(centerPanel);

        JButton backButton = new JButton("Atrás ->");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(70, 70, 70));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBounds(1200, 650, 100, 40);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setContentPane(initialPanel);
                revalidate();
                repaint();
            }
        });

        electionModePanel.add(backButton);
    }

    /**
     * Prepara el panel de selección de sabores de helado para cada jugador.
     */
    private void prepareElementsPlayerSelection() {
        playerSelectionPanel = new JPanel() {
            private Image selectionImage;

            {
                selectionImage = loadImageUniversal("/images/selection_color_image.png");
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setColor(new Color(135, 206, 250));
                g.fillRect(0, 0, getWidth(), getHeight());

                if (selectionImage != null) {
                    int imgWidth = selectionImage.getWidth(null);
                    int imgHeight = selectionImage.getHeight(null);
                    int x = (getWidth() - imgWidth) / 2;
                    int y = (getHeight() - imgHeight) / 2;
                    g.drawImage(selectionImage, x, y, imgWidth, imgHeight, this);

                    putClientProperty("imageX", x);
                    putClientProperty("imageY", y);
                    putClientProperty("imageWidth", imgWidth);
                    putClientProperty("imageHeight", imgHeight);
                }

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (player1SelectedPos != null) {
                    g2d.setColor(new Color(255, 215, 0, 200));
                    g2d.setStroke(new BasicStroke(4));
                    g2d.drawOval(player1SelectedPos.x - 45, player1SelectedPos.y - 45, 90, 90);
                    g2d.setColor(new Color(255, 255, 0, 100));
                    g2d.fillOval(player1SelectedPos.x - 45, player1SelectedPos.y - 45, 90, 90);
                }

                if (player2SelectedPos != null) {
                    g2d.setColor(new Color(255, 215, 0, 200));
                    g2d.setStroke(new BasicStroke(4));
                    g2d.drawOval(player2SelectedPos.x - 45, player2SelectedPos.y - 45, 90, 90);
                    g2d.setColor(new Color(255, 255, 0, 100));
                    g2d.fillOval(player2SelectedPos.x - 45, player2SelectedPos.y - 45, 90, 90);
                }
            }
        };

        playerSelectionPanel.setLayout(null);

        JButton continueButton = new JButton("CONTINUAR");
        continueButton.setFont(new Font("Arial", Font.BOLD, 24));
        continueButton.setBackground(new Color(50, 205, 50));
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);
        continueButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        continueButton.setBounds(575, 600, 200, 60);
        continueButton.setEnabled(false);

        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setContentPane(electionLevelPanel);
                revalidate();
                repaint();
            }
        });

        JButton backButton = new JButton("Atrás ->");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(70, 70, 70));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBounds(1200, 650, 100, 40);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player1Flavor = null;
                player2Flavor = null;
                player1SelectedPos = null;
                player2SelectedPos = null;
                setContentPane(electionModePanel);
                revalidate();
                repaint();
            }
        });

        playerSelectionPanel.putClientProperty("continueButton", continueButton);

        createInvisibleConeButton(360, 280, "chocolate", 1, playerSelectionPanel, continueButton);
        createInvisibleConeButton(455, 280, "vanilla", 1, playerSelectionPanel, continueButton);
        createInvisibleConeButton(550, 280, "strawberry", 1, playerSelectionPanel, continueButton);
        createInvisibleConeButton(800, 280, "chocolate", 2, playerSelectionPanel, continueButton);
        createInvisibleConeButton(895, 280, "vanilla", 2, playerSelectionPanel, continueButton);
        createInvisibleConeButton(990, 280, "strawberry", 2, playerSelectionPanel, continueButton);

        playerSelectionPanel.add(continueButton);
        playerSelectionPanel.add(backButton);
    }

    private void createInvisibleConeButton(int x, int y, String flavor, int player,
                                           JPanel panel, JButton continueBtn) {
        JButton button = new JButton();

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        int size = 80;
        button.setBounds(x - size/2, y - size/2, size, size);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (player == 1) {
                    player1Flavor = flavor;
                    player1SelectedPos = new Point(x, y);
                } else {
                    player2Flavor = flavor;
                    player2SelectedPos = new Point(x, y);
                }

                if (player1Flavor != null && player2Flavor != null) {
                    continueBtn.setEnabled(true);
                }

                panel.repaint();
            }
        });

        panel.add(button);
    }

    /**
     * Prepara el panel de selección de nivel.
     */
    private void prepareElementsElectionLevel() {
        electionLevelPanel = new JPanel() {
            private Image backgroundImage;

            {
                backgroundImage = loadImageUniversal("/images/default_image_background.png");
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(135, 206, 250));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        electionLevelPanel.setLayout(null);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBounds(0, 0, 1350, 720);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;

        JLabel titleLevel = new JLabel("SELECCIONA EL NIVEL");
        titleLevel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLevel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        centerPanel.add(titleLevel, gbc);

        level1 = new JButton("Nivel 1");
        level1.setFont(new Font("Arial", Font.BOLD, 20));
        level1.setPreferredSize(new Dimension(200, 60));
        level1.setBackground(new Color(34, 139, 34));
        level1.setForeground(Color.WHITE);
        level1.setFocusPainted(false);
        gbc.gridy = 1;
        centerPanel.add(level1, gbc);

        level2 = new JButton("Nivel 2");
        level2.setFont(new Font("Arial", Font.BOLD, 20));
        level2.setPreferredSize(new Dimension(200, 60));
        level2.setBackground(new Color(255, 165, 0));
        level2.setForeground(Color.WHITE);
        level2.setFocusPainted(false);
        gbc.gridy = 2;
        centerPanel.add(level2, gbc);

        level3 = new JButton("Nivel 3");
        level3.setFont(new Font("Arial", Font.BOLD, 20));
        level3.setPreferredSize(new Dimension(200, 60));
        level3.setBackground(new Color(178, 34, 34));
        level3.setForeground(Color.WHITE);
        level3.setFocusPainted(false);
        gbc.gridy = 3;
        centerPanel.add(level3, gbc);

        electionLevelPanel.add(centerPanel);

        JButton backButton = new JButton("Atrás ->");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(70, 70, 70));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBounds(1200, 650, 100, 40);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setContentPane(playerSelectionPanel);
                revalidate();
                repaint();
            }
        });

        electionLevelPanel.add(backButton);
    }

    private void prepareElementsMenu() {
        menuBar = new JMenuBar();
        menuOption = new JMenu("Opciones");

        news = new JMenuItem("Noticias");
        exit = new JMenuItem("Salir");
        importLevel = new JMenuItem("Importar Nivel");
        exportLevel = new JMenuItem("Exportar Nivel");
        saveGame = new JMenuItem("Guardar Juego");
        openGame = new JMenuItem("Abrir Juego");

        menuOption.add(news);
        menuOption.add(importLevel);
        menuOption.add(exportLevel);
        menuOption.addSeparator();
        menuOption.add(saveGame);
        menuOption.add(openGame);
        menuOption.addSeparator();
        menuOption.add(exit);

        menuBar.add(menuOption);
        setJMenuBar(menuBar);
    }

    private void prepareActions() {
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionPlay();
            }
        });

        pvp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionSelectMode("PVP");
            }
        });

        level1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionSelectLevel(1);
            }
        });

        level2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionSelectLevel(2);
            }
        });

        level3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionSelectLevel(3);
            }
        });

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionExit();
            }
        });

        importLevel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionImportLevel();
            }
        });

        exportLevel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionExportLevel();
            }
        });

        news.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionNewGame();
            }
        });

        saveGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionSaveGame();
            }
        });

        openGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionOpenGame();
            }
        });
    }

    private void actionPlay() {
        setContentPane(electionModePanel);
        revalidate();
        repaint();
    }
    
    /**
     * Acción de selección de modo de juego.
     *
     * @param mode Modo seleccionado
     */
    private void actionSelectMode(String mode) {
        player1Flavor = null;
        player2Flavor = null;
        player1SelectedPos = null;
        player2SelectedPos = null;

        // ✅ FIX: tus strings reales son "PVP", "PvMachine", "MachineVsMachine"
        if (mode.equals("PVP")) {
            isMachinePlayer1 = false;
            isMachinePlayer2 = false;
        } else if (mode.equals("PvMachine")) {
            isMachinePlayer1 = false;
            isMachinePlayer2 = true;
        } else if (mode.equals("MachineVsMachine")) {
            isMachinePlayer1 = true;
            isMachinePlayer2 = true;
        }

        setContentPane(playerSelectionPanel);
        revalidate();
        repaint();
    }

    private void actionSelectLevel(int level) {

        currentLevel = level;

        stopGameTimers();

        setSize(1280, 720);
        setLocationRelativeTo(null);

        if (mainGame == null) {
            prepareElementsMainGame();
        }

        try {
            juego = new BadIceCream(level);

            if (player1Flavor != null && player2Flavor != null) {
                juego.setPlayerFlavors(player1Flavor, player2Flavor);
            }

            String fileName = "level" + level + ".txt";
            java.io.File f = new java.io.File(System.getProperty("user.dir"), fileName);

            if (f.exists()) {
                juego.importLevel(f.getAbsolutePath());
            }

        } catch (BadIceCreamException e) {
            e.printStackTrace();
        }

        startGameTimers();

        setContentPane(mainGame);
        mainGame.requestFocusInWindow();
        revalidate();
        repaint();
    }

    private void restartGame() {
        stopGameTimers();

        try {
            juego = new BadIceCream(currentLevel);

            if (player1Flavor != null && player2Flavor != null) {
                juego.setPlayerFlavors(player1Flavor, player2Flavor);
            }

            String fileName = "level" + currentLevel + ".txt";
            java.io.File f = new java.io.File(System.getProperty("user.dir"), fileName);
            if (f.exists()) {
                juego.importLevel(f.getAbsolutePath());
            }

        } catch (BadIceCreamException e) {
            e.printStackTrace();
        }

        startGameTimers();

        setContentPane(mainGame);
        mainGame.requestFocusInWindow();
        revalidate();
        repaint();
    }

    private void stopGameTimers() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
        if (monsterTimer != null && monsterTimer.isRunning()) {
            monsterTimer.stop();
        }
    }

    private void startGameTimers() {
        if (gameTimer != null) {
            gameTimer.start();
        }
        if (monsterTimer != null) {
            monsterTimer.start();
        }
    }

    private void actionExit() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que quieres salir?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void actionNewGame() {
        stopGameTimers();
        juego = null;
        setContentPane(initialPanel);
        revalidate();
        repaint();
    }

    private void prepareElementsMainGame() {
        mainGame = new JPanel() {
            private final int CELL_SIZE = 40;
            private final int BOARD_WIDTH = 16 * CELL_SIZE;
            private final int BOARD_HEIGHT = 16 * CELL_SIZE;

            private Image vanillaSprite;
            private Image vanillaSpriteUp;
            private Image vanillaSpriteDown;
            private Image vanillaSpriteLeft;
            private Image vanillaSpriteRight;

            private Image chocolateSprite;
            private Image chocolateSpriteUp;
            private Image chocolateSpriteDown;
            private Image chocolateSpriteLeft;
            private Image chocolateSpriteRight;

            private Image strawberrySprite;

            private Image bananaSprite;
            private Image cherrySprite;
            private Image grapesSprite;
            private Image pineappleSprite;
            private Image cactusSprite;
            private Image cactusActivadoPNG;

            private Image trollSprite;
            private Image trollSpriteUp;
            private Image trollSpriteDown;
            private Image trollSpriteLeft;
            private Image trollSpriteRight;

            private Image calamarSprite;
            private Image iceBlockSprite;
            private Image decorationBlockSprite;
            private Image igluSprite;
            private Image cirnoBackground;

            private Image flowerpotAlertUp;
            private Image flowerpotAlertFront;
            private Image flowerpotAlertLeft;
            private Image flowerpotAlertRight;

            private Image flowerpotSprite;
            private Image flowerpotSpriteUp;
            private Image flowerpotSpriteDown;
            private Image flowerpotSpriteLeft;
            private Image flowerpotSpriteRight;

            private Image baldosaCalienteSprite;
            private Image fogataSprite;

            {
                setPreferredSize(new Dimension(1280, 720));
                loadSprites();

                gameTimer = new Timer(16, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (juego != null) {
                            // Derretir hielo sobre baldosas calientes (2s)
                            juego.tickHotTiles();

                            checkGameStatus();
                            repaint();
                        }
                    }
                });

                monsterTimer = new Timer(500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (juego != null) {
                            moveAllMonsters();

                            if (isMachinePlayer1 && juego.getFirstPlayer() != null) {
                                try {
                                    juego.moveMachinePlayer1();
                                } catch (BadIceCreamException ex) {
                                    ex.printStackTrace();
                                }
                            }

                            if (isMachinePlayer2 && juego.getSecondPlayer() != null) {
                                try {
                                    juego.moveMachinePlayer2();
                                } catch (BadIceCreamException ex) {
                                    ex.printStackTrace();
                                }
                            }

                            try {
                                juego.tickFruits();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
            }

            private void checkGameStatus() {
                Player player1 = juego.getFirstPlayer();
                Player player2 = juego.getSecondPlayer();
                if (player1 == null && player2 == null) {
                    stopGameTimers();
                    showGameOver();
                    return;
                }
                if (player1 != null || player2 != null) {
                    if (juego.isPhaseCleared()) {
                        try {
                            if (juego.getPhase() == 1) {
                                juego.goToNextPhase();
                            } else {
                                stopGameTimers();
                                showVictory();
                            }
                        } catch (BadIceCreamException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            private void moveAllMonsters() {
                if (juego == null) return;

                Monster[][] monsters = juego.getMonsters();
                Player player1 = juego.getFirstPlayer();
                Player player2 = juego.getSecondPlayer();

                java.util.ArrayList<Monster> monsterList = new java.util.ArrayList<>();

                for (int r = 0; r < 16; r++) {
                    for (int c = 0; c < 16; c++) {
                        if (monsters[r][c] != null) {
                            monsterList.add(monsters[r][c]);
                        }
                    }
                }

                for (Monster monster : monsterList) {
                    try {
                        int oldRow = monster.getRow();
                        int oldCol = monster.getColumn();

                        monster.act();

                        if (monster.getRow() != oldRow || monster.getColumn() != oldCol) {
                            monsters[oldRow][oldCol] = null;
                            monsters[monster.getRow()][monster.getColumn()] = monster;
                        }

                    } catch (BadIceCreamException ex) {
                    }
                }

                if (player1 != null) {
                    int playerRow = player1.getRow();
                    int playerCol = player1.getColumn();
                    boolean collision = false;

                    if (monsters[playerRow][playerCol] != null) {
                        collision = true;
                    }

                    if (!collision) {
                        for (Monster monster : monsterList) {
                            if (monster.getRow() == playerRow && monster.getColumn() == playerCol) {
                                collision = true;
                                break;
                            }
                        }
                    }

                    if (collision) {
                        juego.checkActionsForPlayer1();
                    }
                }

                if (player2 != null) {
                    int playerRow = player2.getRow();
                    int playerCol = player2.getColumn();
                    boolean collision = false;

                    if (monsters[playerRow][playerCol] != null) {
                        collision = true;
                    }

                    if (!collision) {
                        for (Monster monster : monsterList) {
                            if (monster.getRow() == playerRow && monster.getColumn() == playerCol) {
                                collision = true;
                                break;
                            }
                        }
                    }

                    if (collision) {
                        juego.checkActionsForPlayer2();
                    }
                }
            }

            private void loadSprites() {
                vanillaSprite = loadImageUniversal("/images/vanilla_sprite.png");
                vanillaSpriteUp = loadImageUniversal("/images/player/vanilla_up.png");
                vanillaSpriteDown = loadImageUniversal("/images/player/vanilla_down.png");
                vanillaSpriteLeft = loadImageUniversal("/images/player/vanilla_left.png");
                vanillaSpriteRight = loadImageUniversal("/images/player/vanilla_right.png");

                chocolateSprite = loadImageUniversal("/images/chocolate_sprite.png");
                chocolateSpriteUp = loadImageUniversal("/images/player/chocolate_up.png");
                chocolateSpriteDown = loadImageUniversal("/images/player/chocolate_down.png");
                chocolateSpriteLeft = loadImageUniversal("/images/player/chocolate_left.png");
                chocolateSpriteRight = loadImageUniversal("/images/player/chocolate_right.png");

                strawberrySprite = loadImageUniversal("/images/strawberry_sprite.png");

                bananaSprite = loadImageUniversal("/images/banana_sprite.png");
                cherrySprite = loadImageUniversal("/images/cherry_sprite.png");
                grapesSprite = loadImageUniversal("/images/grapes_sprite.png");
                pineappleSprite = loadImageUniversal("/images/pinaplee_sprite.png");
                cactusSprite = loadImageUniversal("/images/cactus_sprite.png");

                trollSprite = loadImageUniversal("/images/troll_sprite.png");
                trollSpriteUp = loadImageUniversal("/images/troll_up.png");
                trollSpriteDown = loadImageUniversal("/images/troll_down.png");
                trollSpriteLeft = loadImageUniversal("/images/troll_left.png");
                trollSpriteRight = loadImageUniversal("/images/troll_right.png");

                calamarSprite = loadImageUniversal("/images/calamar_sprite.png");

                flowerpotSpriteUp = loadImageUniversal("/images/macetaatrasapuntodeprenderseatras.png");
                flowerpotSpriteDown = loadImageUniversal("/images/macetafrentederechanoflor.png");
                flowerpotSpriteLeft = loadImageUniversal("/images/macetamovimientoizquierda2.png");
                flowerpotSpriteRight = loadImageUniversal("/images/macetamovimientoderecha2.png");

                flowerpotSprite = flowerpotSpriteDown;

                flowerpotAlertUp = loadImageUniversal("/images/macetaatrasderechaflor.png");
                flowerpotAlertFront = loadImageUniversal("/images/maceta_a_punto_de_transformarsefrente1.png");
                flowerpotAlertLeft = loadImageUniversal("/images/macetamovimientoizquierdaflor2.png");
                flowerpotAlertRight = loadImageUniversal("/images/movimientoderechaflor2.png");

                iceBlockSprite = loadImageUniversal("/images/spriteCuleroDelHielo.png");
                decorationBlockSprite = loadImageUniversal("/images/spriteCuleroDecoracion.png");

                igluSprite = loadImageUniversal("/images/spriteIgluBonito.png");
                cirnoBackground = loadImageUniversal("/images/110624.jpg");

                baldosaCalienteSprite = loadImageUniversal("/images/baldosa_caliente.png");
                fogataSprite = loadImageUniversal("/images/fogata.png");
                cactusActivadoPNG = loadImageUniversal("/images/CactusActivado.png");
            }

            private Image getPlayerSprite(Player player) {
                String flavor = player.getFlavor();
                String direction = player.getDirectionOfView();

                if (direction == null) {
                    direction = "down";
                }

                String spritePath = "/images/player/" + flavor + "_" + direction + ".png";
                Image sprite = loadImageUniversal(spritePath);

                if (sprite == null) {
                    spritePath = "/images/player/" + flavor + "_down.png";
                    sprite = loadImageUniversal(spritePath);
                }

                return sprite;
            }

            private Image getMonsterSprite(Monster monster) {
                String direction = monster.getDirectionOfView();

                if (direction == null) {
                    direction = "down";
                }

                String monsterType = monster.getMonsterType();

                switch (monsterType) {
                    case "TROLL":
                        switch (direction.toLowerCase()) {
                            case "up":
                                return trollSpriteUp != null ? trollSpriteUp : trollSprite;
                            case "down":
                                return trollSpriteDown != null ? trollSpriteDown : trollSprite;
                            case "left":
                                return trollSpriteLeft != null ? trollSpriteLeft : trollSprite;
                            case "right":
                                return trollSpriteRight != null ? trollSpriteRight : trollSprite;
                            default:
                                return trollSpriteDown != null ? trollSpriteDown : trollSprite;
                        }

                    case "CALAMAR":
                        return calamarSprite;

                    case "MACETA":
                        String directiones = monster.getDirectionOfView();

                        if (monster.isInAlertMode()) {
                            switch (directiones) {
                                case "up":
                                    return flowerpotAlertUp != null ? flowerpotAlertUp : flowerpotSpriteUp;
                                case "down":
                                    return flowerpotAlertFront != null ? flowerpotAlertFront : flowerpotSpriteDown;
                                case "left":
                                    return flowerpotAlertLeft != null ? flowerpotAlertLeft : flowerpotSpriteLeft;
                                case "right":
                                    return flowerpotAlertRight != null ? flowerpotAlertRight : flowerpotSpriteRight;
                                default:
                                    return flowerpotAlertFront != null ? flowerpotAlertFront : flowerpotSprite;
                            }
                        } else {
                            switch (directiones) {
                                case "up":
                                    return flowerpotSpriteUp != null ? flowerpotSpriteUp : flowerpotSprite;
                                case "down":
                                    return flowerpotSpriteDown != null ? flowerpotSpriteDown : flowerpotSprite;
                                case "left":
                                    return flowerpotSpriteLeft != null ? flowerpotSpriteLeft : flowerpotSprite;
                                case "right":
                                    return flowerpotSpriteRight != null ? flowerpotSpriteRight : flowerpotSprite;
                                default:
                                    return flowerpotSprite;
                            }
                        }
                    default:
                        return trollSprite;
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (juego == null) return;

                Graphics2D g2d = (Graphics2D) g;

                if (cirnoBackground != null) {
                    g2d.drawImage(cirnoBackground, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setColor(new Color(135, 206, 250));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }

                int offsetX = (getWidth() - BOARD_WIDTH) / 2;
                int offsetY = (getHeight() - BOARD_HEIGHT) / 2;

                g2d.setColor(Color.WHITE);
                g2d.fillRect(offsetX, offsetY, BOARD_WIDTH, BOARD_HEIGHT);

                drawIceBlocks(g2d, offsetX, offsetY);
                drawDecorationBlocks(g2d, offsetX, offsetY);
                drawSpecialBlocks(g2d, offsetX, offsetY);
                drawIglu(g2d, offsetX, offsetY);
                drawFruits(g2d, offsetX, offsetY);
                drawMonsters(g2d, offsetX, offsetY);
                drawPlayers(g2d, offsetX, offsetY);
                drawUI(g2d);
            }

            private void drawIceBlocks(Graphics2D g, int offsetX, int offsetY) {
                Block[][] blocks = juego.getBlocks();

                for (int r = 0; r < 16; r++) {
                    for (int c = 0; c < 16; c++) {
                        Block block = blocks[r][c];
                        if (block != null) {
                            if (block.isIceBlock() || "ICE".equals(block.getBlockType()) || "ICEBLOCK".equals(block.getBlockType())) {
                                int x = offsetX + c * CELL_SIZE;
                                int y = offsetY + r * CELL_SIZE;

                                if (iceBlockSprite != null) {
                                    int spriteSize = (int)(CELL_SIZE * 1.05);
                                    int margin = (CELL_SIZE - spriteSize) / 2;
                                    g.drawImage(iceBlockSprite, x + margin, y + margin, spriteSize, spriteSize, null);
                                } else {
                                    g.setColor(new Color(100, 200, 255));
                                    g.fillRect(offsetX + c * CELL_SIZE, offsetY + r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                                }
                            }
                        }
                    }
                }
            }

            private void drawDecorationBlocks(Graphics2D g, int offsetX, int offsetY) {
                Block[][] blocks = juego.getBlocks();

                for (int r = 0; r < 16; r++) {
                    for (int c = 0; c < 16; c++) {
                        Block block = blocks[r][c];
                        if (block != null && (block.isDecorationBlock() || "DECORATION".equals(block.getBlockType()))) {
                            if (r >= 6 && r <= 9 && c >= 6 && c <= 9) {
                                continue;
                            }

                            int x = offsetX + c * CELL_SIZE;
                            int y = offsetY + r * CELL_SIZE;

                            if (decorationBlockSprite != null) {
                                int spriteSize = (int)(CELL_SIZE * 1);
                                int margin = (CELL_SIZE - spriteSize) / 2;
                                g.drawImage(decorationBlockSprite, x + margin, y + margin, spriteSize, spriteSize, null);
                            }
                        }
                    }
                }
            }

            private void drawSpecialBlocks(Graphics2D g, int offsetX, int offsetY) {
                Block[][] blocks = juego.getBlocks();

                for (int r = 0; r < 16; r++) {
                    for (int c = 0; c < 16; c++) {
                        Block block = blocks[r][c];
                        if (block == null) continue;

                        int x = offsetX + c * CELL_SIZE;
                        int y = offsetY + r * CELL_SIZE;

                        if (block.isBaldosaCaliente()) {
                            if (baldosaCalienteSprite != null) {
                                g.drawImage(baldosaCalienteSprite, x, y, CELL_SIZE, CELL_SIZE, null);
                            } else {
                                g.setColor(new Color(255, 140, 0));
                                g.fillRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                            }
                        } else if (block.isFogata()) {
                            Fogata fogata = (Fogata) block;
                            if (fogata.isActive() && fogataSprite != null) {
                                int size = (int)(CELL_SIZE * 1.3);
                                int margin = (CELL_SIZE - size) / 2;
                                g.drawImage(fogataSprite, x + margin, y + margin, size, size, null);
                            } else if (fogata.isActive()) {
                                g.setColor(new Color(255, 69, 0));
                                g.fillOval(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                            }
                        }
                    }
                }
            }

            private void drawIglu(Graphics2D g, int offsetX, int offsetY) {
                if (igluSprite != null) {
                    int igluAreaSize = (int)(5.5 * CELL_SIZE);
                    int igluAreaX = offsetX + 6 * CELL_SIZE - (int)(0.96 * CELL_SIZE);
                    int igluAreaY = offsetY + 6 * CELL_SIZE - (int)(0.75 * CELL_SIZE);

                    g.drawImage(igluSprite, igluAreaX, igluAreaY, igluAreaSize, igluAreaSize, null);
                }
            }

            private void drawFruits(Graphics2D g, int offsetX, int offsetY) {
                Fruit[][] fruits = juego.getFruits();

                for (int r = 0; r < 16; r++) {
                    for (int c = 0; c < 16; c++) {
                        Fruit fruit = fruits[r][c];
                        if (fruit != null) {
                            int x = offsetX + c * CELL_SIZE;
                            int y = offsetY + r * CELL_SIZE;

                            Image sprite = null;
                            String fruitType = fruit.getFruitType();

                            switch (fruitType) {
                                case "BANANA":
                                    sprite = bananaSprite;
                                    break;
                                case "CEREZA":
                                    sprite = cherrySprite;
                                    break;
                                case "UVA":
                                    sprite = grapesSprite;
                                    break;
                                case "PIÑA":
                                    sprite = pineappleSprite;
                                    break;
                            }

                            if (sprite != null) {
                                int spriteSize = (int)(CELL_SIZE * 1.4);
                                int margin = (CELL_SIZE - spriteSize) / 2;
                                g.drawImage(sprite, x + margin, y + margin, spriteSize, spriteSize, null);
                            }

                            if (fruit.isCactus()) {
                                Cactus cactus = (Cactus) fruit;
                                int spriteSize = (int)(CELL_SIZE * 1.4);
                                int margin = (CELL_SIZE - spriteSize) / 2;
                                if (cactus.isActive()) {
                                    g.drawImage(cactusActivadoPNG, x + margin, y + margin, spriteSize, spriteSize, null);
                                } else {
                                    g.drawImage(cactusSprite, x + margin, y + margin, spriteSize, spriteSize, null);
                                }
                            }
                        }
                    }
                }
            }

            private void drawMonsters(Graphics2D g, int offsetX, int offsetY) {
                Monster[][] monsters = juego.getMonsters();

                for (int r = 0; r < 16; r++) {
                    for (int c = 0; c < 16; c++) {
                        Monster monster = monsters[r][c];
                        if (monster != null) {
                            int x = offsetX + c * CELL_SIZE;
                            int y = offsetY + r * CELL_SIZE;

                            if (monster.getMonsterType().equals("MACETA") && monster.isInAlertMode()) {
                                g.setColor(new Color(255, 220, 0, 80));
                                g.fillOval(x - 5, y - 5, CELL_SIZE + 10, CELL_SIZE + 10);
                            }

                            Image sprite = getMonsterSprite(monster);

                            if (sprite != null) {
                                float scale = getSpriteScale(monster);
                                int spriteSize = (int)(CELL_SIZE * scale);
                                int margin = (CELL_SIZE - spriteSize) / 2;
                                g.drawImage(sprite, x + margin, y + margin, spriteSize, spriteSize, null);
                            }
                        }
                    }
                }
            }

            private float getSpriteScale(Monster monster) {
                String type = monster.getMonsterType();
                String direction = monster.getDirectionOfView();

                switch (type) {
                    case "TROLL":
                        if (direction.equals("left") || direction.equals("right")) return 1.3f;
                        return 1.6f;
                    case "MACETA":
                        if (monster.isInAlertMode()) return 1.7f;
                        return 1.5f;
                    case "CALAMAR":
                        return 1.5f;
                    default:
                        return 1.6f;
                }
            }

            private void drawPlayers(Graphics2D g, int offsetX, int offsetY) {
                Player[][] players = juego.getPlayers();

                for (int r = 0; r < 16; r++) {
                    for (int c = 0; c < 16; c++) {
                        Player player = players[r][c];
                        if (player != null) {
                            int x = offsetX + c * CELL_SIZE;
                            int y = offsetY + r * CELL_SIZE;

                            Image sprite = getPlayerSprite(player);

                            if (sprite != null) {
                                int spriteHeight = (int)(CELL_SIZE * 1.6);
                                int spriteWidth = (int)(CELL_SIZE * 1.2);

                                int marginX = (CELL_SIZE - spriteWidth) / 2;
                                int marginY = (CELL_SIZE - spriteHeight) / 2;

                                g.drawImage(sprite, x + marginX, y + marginY, spriteWidth, spriteHeight, null);
                            }
                        }
                    }
                }
            }
            
            private void drawUI(Graphics2D g) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 24));

                g.setColor(Color.BLACK);
                g.drawString("Level: " + juego.getLevel(), 51, 51);
                g.drawString("Phase: " + juego.getPhase(), 51, 81);

                Player player1 = juego.getPlayer1();
                Player player2 = juego.getPlayer2();

                int score1 = (player1 != null) ? player1.getScore() : 0;
                int score2 = (player2 != null) ? player2.getScore() : 0;

                g.drawString("P1: " + score1, 51, 111);
                g.drawString("P2: " + score2, 51, 141);

                g.setColor(Color.WHITE);
                g.drawString("Level: " + juego.getLevel(), 50, 50);
                g.drawString("Phase: " + juego.getPhase(), 50, 80);
                g.drawString("P1: " + score1, 50, 110);
                g.drawString("P2: " + score2, 50, 140);
            }
        };

        mainGame.setLayout(null);
        mainGame.setFocusable(true);

        mainGame.addKeyListener(new KeyAdapter() {
            private long lastMoveTimeP1 = 0;
            private long lastMoveTimeP2 = 0;
            private long lastActionTimeP1 = 0;
            private long lastActionTimeP2 = 0;
            private final long MOVE_DELAY = 150;
            private final long ACTION_DELAY = 250;

            @Override
            public void keyPressed(KeyEvent e) {
                if (juego != null) {
                    Player player1 = juego.getFirstPlayer();
                    Player player2 = juego.getSecondPlayer();

                    long currentTime = System.currentTimeMillis();
                    int keyCode = e.getKeyCode();

                    if (player1 != null && !isMachinePlayer1) {
                        if (currentTime - lastMoveTimeP1 >= MOVE_DELAY) {
                            int oldRow = player1.getRow();
                            int oldCol = player1.getColumn();

                            if (keyCode == KeyEvent.VK_A) {
                                try {
                                    player1.changeOfView("left");
                                    juego.movePlayer1();
                                    juego.getPlayers()[oldRow][oldCol] = null;
                                    juego.checkActionsForPlayer1();
                                } catch (BadIceCreamException ex) {
                                }
                                lastMoveTimeP1 = currentTime;
                            } else if (keyCode == KeyEvent.VK_D) {
                                try {
                                    player1.changeOfView("right");
                                    juego.movePlayer1();
                                    juego.getPlayers()[oldRow][oldCol] = null;
                                    juego.checkActionsForPlayer1();
                                } catch (BadIceCreamException ex) {
                                }
                                lastMoveTimeP1 = currentTime;
                            } else if (keyCode == KeyEvent.VK_W) {
                                try {
                                    player1.changeOfView("up");
                                    juego.movePlayer1();
                                    juego.getPlayers()[oldRow][oldCol] = null;
                                    juego.checkActionsForPlayer1();
                                } catch (BadIceCreamException ex) {
                                }
                                lastMoveTimeP1 = currentTime;
                            } else if (keyCode == KeyEvent.VK_S) {
                                try {
                                    player1.changeOfView("down");
                                    juego.movePlayer1();
                                    juego.getPlayers()[oldRow][oldCol] = null;
                                    juego.checkActionsForPlayer1();
                                } catch (BadIceCreamException ex) {
                                }
                                lastMoveTimeP1 = currentTime;
                            }
                        }

                        if (keyCode == KeyEvent.VK_SPACE && currentTime - lastActionTimeP1 >= ACTION_DELAY) {
                            try {
                                juego.shootOrBreakIcePlayer1();
                                lastActionTimeP1 = currentTime;
                            } catch (BadIceCreamException ex) {
                            }
                        }
                    }

                    if (player2 != null && !isMachinePlayer2) {
                        if (currentTime - lastMoveTimeP2 >= MOVE_DELAY) {
                            int oldRow = player2.getRow();
                            int oldCol = player2.getColumn();

                            if (keyCode == KeyEvent.VK_LEFT) {
                                try {
                                    player2.changeOfView("left");
                                    juego.movePlayer2();
                                    juego.getPlayers()[oldRow][oldCol] = null;
                                    juego.checkActionsForPlayer2();
                                } catch (BadIceCreamException ex) {
                                }
                                lastMoveTimeP2 = currentTime;
                            } else if (keyCode == KeyEvent.VK_RIGHT) {
                                try {
                                    player2.changeOfView("right");
                                    juego.movePlayer2();
                                    juego.getPlayers()[oldRow][oldCol] = null;
                                    juego.checkActionsForPlayer2();
                                } catch (BadIceCreamException ex) {
                                }
                                lastMoveTimeP2 = currentTime;
                            } else if (keyCode == KeyEvent.VK_UP) {
                                try {
                                    player2.changeOfView("up");
                                    juego.movePlayer2();
                                    juego.getPlayers()[oldRow][oldCol] = null;
                                    juego.checkActionsForPlayer2();
                                } catch (BadIceCreamException ex) {
                                }
                                lastMoveTimeP2 = currentTime;
                            } else if (keyCode == KeyEvent.VK_DOWN) {
                                try {
                                    player2.changeOfView("down");
                                    juego.movePlayer2();
                                    juego.getPlayers()[oldRow][oldCol] = null;
                                    juego.checkActionsForPlayer2();
                                } catch (BadIceCreamException ex) {
                                }
                                lastMoveTimeP2 = currentTime;
                            }
                        }

                        if (keyCode == KeyEvent.VK_ENTER && currentTime - lastActionTimeP2 >= ACTION_DELAY) {
                            try {
                                juego.shootOrBreakIcePlayer2();
                                lastActionTimeP2 = currentTime;
                            } catch (BadIceCreamException ex) {
                            }
                        }
                    }

                    repaint();
                }
            }
        });
    }

    private void showGameOver() {
        setContentPane(gameOverPanel);
        revalidate();
        repaint();
    }

    private void showVictory() {
        setContentPane(victoryPanel);
        revalidate();
        repaint();
    }

    private void actionImportLevel() {
        if (juego == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debes iniciar un juego para importar un nivel",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar nivel para importar");

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos de nivel (*.txt)", "txt"
        );
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new java.io.File("."));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();

            try {
                stopGameTimers();
                juego.importLevel(selectedFile.getAbsolutePath());
                startGameTimers();

                if (mainGame != null) {
                    mainGame.repaint();
                }

                JOptionPane.showMessageDialog(
                        this,
                        "Nivel importado exitosamente desde:\n" + selectedFile.getName(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (BadIceCreamException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error al importar el nivel:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void actionExportLevel() {
        if (juego == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debes iniciar un juego para exportar un nivel",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar nivel como");

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos de nivel (*.txt)", "txt"
        );
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setSelectedFile(new java.io.File("nivel_" + currentLevel + ".txt"));

        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();

            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".txt")) {
                filePath += ".txt";
                selectedFile = new java.io.File(filePath);
            }

            try {
                juego.exportLevel(selectedFile.getAbsolutePath());

                JOptionPane.showMessageDialog(
                        this,
                        "Nivel exportado exitosamente a:\n" + selectedFile.getName(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (BadIceCreamException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error al exportar el nivel:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void actionSaveGame() {
        if (juego == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debes iniciar un juego para guardarlo",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar partida");

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos de partida (*.sav)", "sav"
        );
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setSelectedFile(new java.io.File("partida.sav"));

        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();

            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".sav")) {
                filePath += ".sav";
                selectedFile = new java.io.File(filePath);
            }

            try {
                stopGameTimers();
                juego.saveGame(selectedFile.getAbsolutePath());
                startGameTimers();

                JOptionPane.showMessageDialog(
                        this,
                        "Partida guardada exitosamente en:\n" + selectedFile.getName(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (BadIceCreamException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error al guardar la partida:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void actionOpenGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Abrir partida guardada");

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos de partida (*.sav)", "sav"
        );
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new java.io.File("."));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();

            try {
                stopGameTimers();
                juego = BadIceCream.openGame(selectedFile.getAbsolutePath());

                prepareElementsMainGame();
                setContentPane(mainGame);
                mainGame.requestFocusInWindow();
                startGameTimers();
                revalidate();
                repaint();

                JOptionPane.showMessageDialog(
                        this,
                        "Partida cargada exitosamente desde:\n" + selectedFile.getName(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (BadIceCreamException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error al cargar la partida:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BadIceCreamGUI();
            }
        });
    }
}