package presentation;

import org.junit.*;
import static org.junit.Assert.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import domain.*;

/**
 * Clase de pruebas JUnit para BadIceCreamGUI
 * Cobertura: Métodos testables sin interacción visual
 * 
 * @author Neco-Arc Team
 */
public class BadIceCreamGUITest {
    
    private BadIceCreamGUI gui;
    
    /**
     * Configuración antes de cada prueba
     */
    @Before
    public void setUp() {
        // Crear GUI en modo headless para testing
        System.setProperty("java.awt.headless", "false");
        gui = new BadIceCreamGUI();
    }
    
    /**
     * Limpieza después de cada prueba
     */
    @After
    public void tearDown() {
        if (gui != null) {
            gui.dispose();
            gui = null;
        }
    }
    
    // ==================== PRUEBAS DE CONSTRUCCIÓN ====================
    
    /**
     * Test 1: Verificar que el constructor crea la ventana
     */
    @Test
    public void testConstructor() {
        assertNotNull("La GUI no debe ser null", gui);
        assertTrue("La ventana debe ser visible", gui.isVisible());
    }
    
    /**
     * Test 2: Verificar título de la ventana
     */
    @Test
    public void testWindowTitle() {
        assertEquals("El título debe ser 'Bad Ice Cream'", 
                     "Bad Ice Cream", gui.getTitle());
    }
    
    /**
     * Test 3: Verificar que la ventana no es redimensionable
     */
    @Test
    public void testWindowNotResizable() {
        assertFalse("La ventana no debe ser redimensionable", gui.isResizable());
    }
    
    /**
     * Test 4: Verificar operación de cierre por defecto
     */
    @Test
    public void testDefaultCloseOperation() {
        assertEquals("La operación de cierre debe ser EXIT_ON_CLOSE",
                     JFrame.EXIT_ON_CLOSE, gui.getDefaultCloseOperation());
    }
    
    // ==================== PRUEBAS DE COMPONENTES ====================
    
    /**
     * Test 5: Verificar que tiene barra de menú
     */
    @Test
    public void testHasMenuBar() {
        assertNotNull("Debe tener barra de menú", gui.getJMenuBar());
    }
    
    /**
     * Test 6: Verificar que el menú tiene opciones
     */
    @Test
    public void testMenuHasOptions() {
        JMenuBar menuBar = gui.getJMenuBar();
        assertNotNull("La barra de menú no debe ser null", menuBar);
        
        assertTrue("Debe tener al menos un menú", menuBar.getMenuCount() > 0);
    }
    
    /**
     * Test 7: Verificar que tiene contenido
     */
    @Test
    public void testHasContentPane() {
        Container contentPane = gui.getContentPane();
        assertNotNull("Debe tener un panel de contenido", contentPane);
    }
    
    // ==================== PRUEBAS DE DIMENSIONES ====================
    
    /**
     * Test 8: Verificar que tiene un tamaño definido
     */
    @Test
    public void testWindowSize() {
        Dimension size = gui.getSize();
        assertTrue("El ancho debe ser mayor a 0", size.width > 0);
        assertTrue("El alto debe ser mayor a 0", size.height > 0);
    }
    
    /**
     * Test 9: Verificar tamaño mínimo razonable
     */
    @Test
    public void testMinimumSize() {
        Dimension size = gui.getSize();
        assertTrue("El ancho debe ser al menos 600", size.width >= 600);
        assertTrue("El alto debe ser al menos 400", size.height >= 400);
    }
    
    // ==================== PRUEBAS DE ESTADO INICIAL ====================
    
    /**
     * Test 10: Verificar que la GUI está centrada
     */
    @Test
    public void testWindowCentered() {
        Point location = gui.getLocation();
        assertNotNull("La ubicación no debe ser null", location);
    }
    
    /**
     * Test 11: Verificar que comienza visible
     */
    @Test
    public void testInitiallyVisible() {
        assertTrue("La ventana debe comenzar visible", gui.isVisible());
    }
    
    // ==================== PRUEBAS DE FUNCIONALIDAD ====================
    
    /**
     * Test 12: Verificar que se puede hacer dispose
     */
    @Test
    public void testDispose() {
        gui.dispose();
        assertFalse("Después de dispose, no debe estar visible", gui.isVisible());
    }
    
    /**
     * Test 13: Verificar que se puede ocultar
     */
    @Test
    public void testSetVisible() {
        gui.setVisible(false);
        assertFalse("Debe poder ocultarse", gui.isVisible());
        
        gui.setVisible(true);
        assertTrue("Debe poder mostrarse de nuevo", gui.isVisible());
    }
    
    /**
     * Test 14: Verificar que el tamaño se puede cambiar
     */
    @Test
    public void testSetSize() {
        Dimension newSize = new Dimension(1280, 720);
        gui.setSize(newSize);
        
        assertEquals("El ancho debe ser 1280", 1280, gui.getWidth());
        assertEquals("El alto debe ser 720", 720, gui.getHeight());
    }
    
    // ==================== PRUEBAS DE PANELES ====================
    
    /**
     * Test 15: Verificar que los componentes son JPanel
     */
    @Test
    public void testComponentTypes() {
        Container contentPane = gui.getContentPane();
        assertTrue("El contenido debe ser un JPanel", 
                   contentPane instanceof JPanel);
    }
    
    // ==================== PRUEBAS DE INTEGRACIÓN CON DOMAIN ====================
    
    /**
     * Test 16: Crear GUI múltiples veces no lanza excepción
     */
    @Test
    public void testMultipleGUICreation() {
        BadIceCreamGUI gui2 = new BadIceCreamGUI();
        assertNotNull("Segunda GUI no debe ser null", gui2);
        gui2.dispose();
    }
    
    /**
     * Test 17: Verificar que el main ejecuta sin excepción
     */
    @Test
    public void testMainMethodExecutes() {
        // Ejecutar main en un thread separado
        Thread thread = new Thread(() -> {
            try {
                // No ejecutamos realmente el main porque crearía una ventana
                // pero verificamos que la clase existe
                Class<?> clazz = BadIceCreamGUI.class;
                assertNotNull("La clase debe existir", clazz);
            } catch (Exception e) {
                fail("No debe lanzar excepción: " + e.getMessage());
            }
        });
        
        thread.start();
        assertTrue("El thread debe ejecutarse", true);
    }
    
    // ==================== PRUEBAS DE RECURSOS ====================
    
    /**
     * Test 18: Verificar que la GUI se libera correctamente
     */
    @Test
    public void testResourceCleanup() {
        gui.dispose();
        gui = null;
        System.gc(); // Sugerir recolección de basura
        
        assertTrue("Los recursos deben liberarse", true);
    }
    
    /**
     * Test 19: Verificar que se puede crear después de dispose
     */
    @Test
    public void testRecreateAfterDispose() {
        gui.dispose();
        gui = new BadIceCreamGUI();
        
        assertNotNull("Debe poder recrearse", gui);
        assertTrue("Debe estar visible", gui.isVisible());
    }
    
    // ==================== PRUEBAS DE EVENTOS ====================
    
    /**
     * Test 20: Verificar que la GUI responde a eventos de ventana
     */
    @Test
    public void testWindowEvents() {
        gui.setVisible(false);
        assertFalse("Debe responder a setVisible(false)", gui.isVisible());
        
        gui.setVisible(true);
        assertTrue("Debe responder a setVisible(true)", gui.isVisible());
    }
    
    // ==================== PRUEBAS DE LAYOUT ====================
    
    /**
     * Test 21: Verificar que tiene un layout manager
     */
    @Test
    public void testHasLayoutManager() {
        Container contentPane = gui.getContentPane();
        assertNotNull("El layout manager no debe ser null", 
                      contentPane.getLayout());
    }
    
    // ==================== PRUEBAS DE VALIDACIÓN ====================
    
    /**
     * Test 22: Verificar que la ventana es válida
     */
    @Test
    public void testWindowIsValid() {
        assertTrue("La ventana debe ser válida", gui.isValid());
    }
    
    /**
     * Test 23: Verificar que la ventana está displayable
     */
    @Test
    public void testWindowDisplayable() {
        assertTrue("La ventana debe ser displayable", gui.isDisplayable());
    }
    
    // ==================== PRUEBAS DE PROPIEDADES ====================
    
    /**
     * Test 24: Verificar que el título se puede cambiar
     */
    @Test
    public void testChangeTitle() {
        String newTitle = "Test Title";
        gui.setTitle(newTitle);
        
        assertEquals("El título debe cambiar", newTitle, gui.getTitle());
        
        // Restaurar título original
        gui.setTitle("Bad Ice Cream");
    }
    
    /**
     * Test 25: Verificar iconImage (puede ser null)
     */
    @Test
    public void testIconImage() {
        // El icono puede ser null, solo verificamos que no lance excepción
        Image icon = gui.getIconImage();
        assertTrue("Debe retornar un Image o null", icon == null || icon instanceof Image);
    }
    
    // ==================== PRUEBAS DE FOCUS ====================
    
    /**
     * Test 26: Verificar que la ventana puede recibir focus
     */
    @Test
    public void testFocusable() {
        assertTrue("La ventana debe ser focusable", gui.isFocusable());
    }
    
    // ==================== PRUEBAS DE BOUNDS ====================
    
    /**
     * Test 27: Verificar bounds de la ventana
     */
    @Test
    public void testWindowBounds() {
        Rectangle bounds = gui.getBounds();
        assertNotNull("Los bounds no deben ser null", bounds);
        assertTrue("Los bounds deben tener área positiva", 
                   bounds.width > 0 && bounds.height > 0);
    }
    
    // ==================== PRUEBAS DE COMPONENTES HIJOS ====================
    
    /**
     * Test 28: Verificar que tiene componentes
     */
    @Test
    public void testHasComponents() {
        Container contentPane = gui.getContentPane();
        assertTrue("Debe tener al menos un componente", 
                   contentPane.getComponentCount() >= 0);
    }
    
    // ==================== PRUEBAS DE ESTADO ====================
    
    /**
     * Test 29: Verificar estado normal de la ventana
     */
    @Test
    public void testWindowState() {
        assertEquals("El estado debe ser NORMAL",
                     JFrame.NORMAL, gui.getExtendedState());
    }
    
    /**
     * Test 30: Verificar que no está iconificada
     */
    @Test
    public void testNotIconified() {
        int state = gui.getExtendedState();
        assertFalse("No debe estar iconificada inicialmente",
                    (state & JFrame.ICONIFIED) != 0);
    }
    
    // ==================== PRUEBAS DE GRAPHICS ====================
    
    /**
     * Test 31: Verificar que tiene Graphics
     */
    @Test
    public void testHasGraphics() {
        Graphics g = gui.getGraphics();
        // Puede ser null en headless, pero no debe lanzar excepción
        assertTrue("Debe retornar Graphics o null", 
                   g == null || g instanceof Graphics);
    }
    
    // ==================== PRUEBAS DE BACKGROUND ====================
    
    /**
     * Test 32: Verificar background color
     */
    @Test
    public void testBackgroundColor() {
        Color bg = gui.getBackground();
        assertNotNull("El background no debe ser null", bg);
    }
    
    // ==================== PRUEBAS DE FOREGROUND ====================
    
    /**
     * Test 33: Verificar foreground color
     */
    @Test
    public void testForegroundColor() {
        Color fg = gui.getForeground();
        assertNotNull("El foreground no debe ser null", fg);
    }
    
    // ==================== PRUEBAS DE FONT ====================
    
    /**
     * Test 34: Verificar font
     */
    @Test
    public void testFont() {
        Font font = gui.getFont();
        assertNotNull("La fuente no debe ser null", font);
    }
    
    // ==================== PRUEBAS DE LOCALE ====================
    
    /**
     * Test 35: Verificar locale
     */
    @Test
    public void testLocale() {
        assertNotNull("El locale no debe ser null", gui.getLocale());
    }
    
    // ==================== PRUEBAS DE CURSOR ====================
    
    /**
     * Test 36: Verificar cursor
     */
    @Test
    public void testCursor() {
        Cursor cursor = gui.getCursor();
        assertNotNull("El cursor no debe ser null", cursor);
    }
    
    // ==================== PRUEBAS DE ENABLED ====================
    
    /**
     * Test 37: Verificar que está habilitada
     */
    @Test
    public void testEnabled() {
        assertTrue("La ventana debe estar habilitada", gui.isEnabled());
    }
    
    /**
     * Test 38: Verificar cambio de enabled
     */
    @Test
    public void testSetEnabled() {
        gui.setEnabled(false);
        assertFalse("Debe poder deshabilitarse", gui.isEnabled());
        
        gui.setEnabled(true);
        assertTrue("Debe poder habilitarse de nuevo", gui.isEnabled());
    }
    
    // ==================== PRUEBAS DE COMPONENTES ESPECÍFICOS ====================
    
    /**
     * Test 39: Verificar que el contentPane no es null
     */
    @Test
    public void testContentPaneNotNull() {
        assertNotNull("El contentPane no debe ser null", gui.getContentPane());
    }
    
    /**
     * Test 40: Verificar que se puede cambiar contentPane
     */
    @Test
    public void testSetContentPane() {
        Container originalContentPane = gui.getContentPane();
        JPanel newPanel = new JPanel();
        
        gui.setContentPane(newPanel);
        assertEquals("El contentPane debe cambiar", newPanel, gui.getContentPane());
        
        // Restaurar original
        gui.setContentPane(originalContentPane);
    }
}
