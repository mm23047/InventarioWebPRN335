package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.application.Application;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoProducto;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * Pruebas unitarias para TipoProductoFrm
 * Cubre lógica jerárquica y construcción de árboles
 */
@ExtendWith(MockitoExtension.class)
class TipoProductoFrmTest {

    @Mock
    private TipoProductoDAO tPDAO;

    @Mock
    private TipoProductoCaracteristicaFrm tpcFrm;

    @Mock
    private FacesContext facesContext;

    @Mock
    private Application application;

    @Mock
    private ResourceBundle resourceBundle;

    @InjectMocks
    private TipoProductoFrm frm;

    private TipoProducto raiz;
    private TipoProducto hijo1;
    private TipoProducto hijo2;
    private TipoProducto nieto1;

    @BeforeEach
    void setUp() {
        // Configurar jerarquía de tipos de producto
        raiz = new TipoProducto();
        raiz.setId(1L);
        raiz.setNombre("Electrónica");
        raiz.setActivo(true);
        raiz.setIdTipoProductoPadre(null);

        hijo1 = new TipoProducto();
        hijo1.setId(2L);
        hijo1.setNombre("Computadoras");
        hijo1.setActivo(true);
        hijo1.setIdTipoProductoPadre(raiz);

        hijo2 = new TipoProducto();
        hijo2.setId(3L);
        hijo2.setNombre("Teléfonos");
        hijo2.setActivo(true);
        hijo2.setIdTipoProductoPadre(raiz);

        nieto1 = new TipoProducto();
        nieto1.setId(4L);
        nieto1.setNombre("Laptops");
        nieto1.setActivo(true);
        nieto1.setIdTipoProductoPadre(hijo1);

        // Configurar mocks con lenient() para evitar UnnecessaryStubbingException
        lenient().when(facesContext.getApplication()).thenReturn(application);
        lenient().when(application.getResourceBundle(any(), eq("crud"))).thenReturn(resourceBundle);
        lenient().when(resourceBundle.getString(anyString())).thenReturn("Mensaje de prueba");
    }

    // ==================== PRUEBAS DE CONSTRUCTOR ====================

    @Test
    void constructor_DebeAsignarNombreBean() {
        // Act
        TipoProductoFrm nuevoFrm = new TipoProductoFrm();

        // Assert
        assertEquals("Tipo de Producto", nuevoFrm.getNombreBean());
    }

    // ==================== PRUEBAS DE ÁRBOL JERÁRQUICO ====================

    @Test
    void getRoot_PrimeraVez_DebeConstruirArbol() {
        // Arrange
        List<TipoProducto> todos = Arrays.asList(raiz, hijo1, hijo2, nieto1);
        when(tPDAO.findAll()).thenReturn(todos);

        // Act
        TreeNode root = frm.getRoot();

        // Assert
        assertNotNull(root);
        verify(tPDAO, times(1)).findAll();
    }

    @Test
    void getRoot_ConJerarquia_DebeCrearEstructuraCorrecta() {
        // Arrange
        List<TipoProducto> todos = Arrays.asList(raiz, hijo1);
        when(tPDAO.findAll()).thenReturn(todos);

        // Act
        TreeNode root = frm.getRoot();

        // Assert
        assertNotNull(root);
        assertFalse(root.getChildren().isEmpty());
    }

    @Test
    void getRoot_ConExcepcion_DebeRetornarArbolVacio() {
        // Arrange
        when(tPDAO.findAll()).thenThrow(new RuntimeException("Error de BD"));

        // Act
        TreeNode root = frm.getRoot();

        // Assert
        assertNotNull(root);
        assertTrue(root.getChildren().isEmpty());
    }

    // ==================== PRUEBAS DE OPCIONES PADRE ====================

    @Test
    void getOpcionesPadreJerarquicas_DebeIncluirOpcionSinPadre() {
        // Arrange
        when(tPDAO.findAll()).thenReturn(Arrays.asList(raiz));

        // Act
        List<SelectItem> opciones = frm.getOpcionesPadreJerarquicas();

        // Assert
        assertNotNull(opciones);
        assertFalse(opciones.isEmpty());
        assertEquals(null, opciones.get(0).getValue());
        assertTrue(opciones.get(0).getLabel().contains("SIN PADRE"));
    }

    @Test
    void getOpcionesPadreJerarquicas_ConHijos_DebeIncluirJerarquia() {
        // Arrange
        when(tPDAO.findAll()).thenReturn(Arrays.asList(raiz, hijo1, nieto1));

        // Act
        List<SelectItem> opciones = frm.getOpcionesPadreJerarquicas();

        // Assert
        assertNotNull(opciones);
        assertTrue(opciones.size() > 1);
    }

    @Test
    void getOpcionesPadreJerarquicas_ConTiposInactivos_DebeMarcarlos() {
        // Arrange
        hijo1.setActivo(false);
        when(tPDAO.findAll()).thenReturn(Arrays.asList(raiz, hijo1));

        // Act
        List<SelectItem> opciones = frm.getOpcionesPadreJerarquicas();

        // Assert
        assertNotNull(opciones);
        boolean tieneInactivo = opciones.stream()
                .anyMatch(item -> item.getLabel().contains("INACTIVO"));
        assertTrue(tieneInactivo);
    }

    // ==================== PRUEBAS DE VERIFICACIÓN DE HIJOS ====================

    @Test
    void tieneHijos_ConHijos_DebeRetornarTrue() {
        // Arrange
        when(tPDAO.findAll()).thenReturn(Arrays.asList(raiz, hijo1));

        // Act
        boolean resultado = frm.tieneHijos(1L);

        // Assert
        assertTrue(resultado);
    }

    @Test
    void tieneHijos_SinHijos_DebeRetornarFalse() {
        // Arrange
        when(tPDAO.findAll()).thenReturn(Arrays.asList(raiz, hijo1));

        // Act
        boolean resultado = frm.tieneHijos(2L);

        // Assert
        assertFalse(resultado);
    }

    @Test
    void tieneHijos_ConIdNulo_DebeRetornarFalse() {
        // Act
        boolean resultado = frm.tieneHijos(null);

        // Assert
        assertFalse(resultado);
    }

    @Test
    void tieneHijos_ConExcepcion_DebeRetornarFalse() {
        // Arrange
        when(tPDAO.findAll()).thenThrow(new RuntimeException("Error de BD"));

        // Act
        boolean resultado = frm.tieneHijos(1L);

        // Assert
        assertFalse(resultado);
    }

    // ==================== PRUEBAS DE ESTILOS ====================

    @Test
    void getEstiloNodo_ConTipoActivo_DebeRetornarEstiloVacio() {
        // Arrange
        raiz.setActivo(true);

        // Act
        String estilo = frm.getEstiloNodo(raiz);

        // Assert
        assertEquals("", estilo);
    }

    @Test
    void getEstiloNodo_ConTipoInactivo_DebeRetornarEstiloTachado() {
        // Arrange
        raiz.setActivo(false);

        // Act
        String estilo = frm.getEstiloNodo(raiz);

        // Assert
        assertTrue(estilo.contains("line-through"));
        assertTrue(estilo.contains("italic"));
    }

    @Test
    void getEstiloNodo_ConTipoNulo_DebeRetornarEstiloVacio() {
        // Act
        String estilo = frm.getEstiloNodo(null);

        // Assert
        assertEquals("", estilo);
    }

    @Test
    void getEstiloEstado_ConTipoActivo_DebeRetornarEstiloVerde() {
        // Arrange
        raiz.setActivo(true);

        // Act
        String estilo = frm.getEstiloEstado(raiz);

        // Assert
        assertTrue(estilo.contains("#00aa00"));
        assertTrue(estilo.contains("bold"));
    }

    @Test
    void getEstiloEstado_ConTipoInactivo_DebeRetornarEstiloRojo() {
        // Arrange
        raiz.setActivo(false);

        // Act
        String estilo = frm.getEstiloEstado(raiz);

        // Assert
        assertTrue(estilo.contains("#cc0000"));
        assertTrue(estilo.contains("bold"));
    }

    @Test
    void getEstiloEstado_ConTipoNulo_DebeRetornarEstiloVacio() {
        // Act
        String estilo = frm.getEstiloEstado(null);

        // Assert
        assertEquals("", estilo);
    }

    // ==================== PRUEBAS DE SELECCIÓN EN ÁRBOL ====================

    @Test
    void onTreeRowSelect_ConNodoValido_DebeAsignarRegistro() {
        // Arrange
        TreeNode nodo = new DefaultTreeNode(raiz);
        NodeSelectEvent event = mock(NodeSelectEvent.class);
        when(event.getTreeNode()).thenReturn(nodo);

        // Act
        frm.onTreeRowSelect(event);

        // Assert
        assertEquals(raiz, frm.getRegistro());
        assertEquals(ESTADO_CRUD.MODIFICAR, frm.getEstado());
    }

    @Test
    void onTreeRowSelect_ConPadre_DebeSincronizarPadreSeleccionado() {
        // Arrange
        TreeNode nodo = new DefaultTreeNode(hijo1);
        NodeSelectEvent event = mock(NodeSelectEvent.class);
        when(event.getTreeNode()).thenReturn(nodo);

        // Act
        frm.onTreeRowSelect(event);

        // Assert
        assertEquals(1L, frm.getPadreSeleccionadoId());
    }

    @Test
    void onTreeRowSelect_SinPadre_DebePadreSeleccionadoNulo() {
        // Arrange
        TreeNode nodo = new DefaultTreeNode(raiz);
        NodeSelectEvent event = mock(NodeSelectEvent.class);
        when(event.getTreeNode()).thenReturn(nodo);

        // Act
        frm.onTreeRowSelect(event);

        // Assert
        assertNull(frm.getPadreSeleccionadoId());
    }

    @Test
    void onTreeRowSelect_ConEventoNulo_NoDebeModificarEstado() {
        // Arrange
        ESTADO_CRUD estadoInicial = frm.getEstado();

        // Act
        frm.onTreeRowSelect(null);

        // Assert
        assertEquals(estadoInicial, frm.getEstado());
    }

    // ==================== PRUEBAS DE SETTERS Y GETTERS ====================

    @Test
    void setPadreSeleccionadoId_DebeAsignarValor() {
        // Act
        frm.setPadreSeleccionadoId(5L);

        // Assert
        assertEquals(5L, frm.getPadreSeleccionadoId());
    }

    @Test
    void setSelectedNode_DebeAsignarValor() {
        // Arrange
        TreeNode nodo = new DefaultTreeNode(raiz);

        // Act
        frm.setSelectedNode(nodo);

        // Assert
        assertEquals(nodo, frm.getSelectedNode());
    }
}
