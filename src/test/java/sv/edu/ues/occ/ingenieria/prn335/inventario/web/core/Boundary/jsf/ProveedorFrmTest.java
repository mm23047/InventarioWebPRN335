package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.application.Application;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ProveedorDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Proveedor;

import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * Pruebas unitarias para ProveedorFrm
 * Cubre conversión de IDs (Integer), creación de entidades y selección
 */
@ExtendWith(MockitoExtension.class)
class ProveedorFrmTest {

    @Mock
    private ProveedorDAO dao;

    @Mock
    private FacesContext facesContext;

    @Mock
    private Application application;

    @Mock
    private ResourceBundle resourceBundle;

    @Mock
    private SelectEvent<Proveedor> selectEvent;

    @InjectMocks
    private ProveedorFrm frm;

    private Proveedor proveedorPrueba;

    @BeforeEach
    void setUp() {
        proveedorPrueba = new Proveedor();
        proveedorPrueba.setId(1);
        proveedorPrueba.setNombre("Proveedor Test");
        proveedorPrueba.setRazonSocial("Proveedor Test S.A.");
        proveedorPrueba.setNit("0614-123456-001-1");
        proveedorPrueba.setActivo(true);
        proveedorPrueba.setObservaciones("Observaciones de prueba");

        // Configurar mocks con lenient() para evitar UnnecessaryStubbingException
        lenient().when(facesContext.getApplication()).thenReturn(application);
        lenient().when(application.getResourceBundle(any(), eq("crud"))).thenReturn(resourceBundle);
        lenient().when(resourceBundle.getString(anyString())).thenReturn("Mensaje de prueba");
    }

    // ==================== PRUEBAS DE CREACIÓN DE ENTIDAD ====================

    @Test
    void createNewEntity_DebeCrearProveedorConValoresPorDefecto() {
        // Act
        Proveedor resultado = frm.createNewEntity();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
        assertEquals("", resultado.getNombre());
        assertEquals("", resultado.getRazonSocial());
        assertEquals("", resultado.getNit());
        assertEquals("", resultado.getObservaciones());
    }

    @Test
    void createNewEntity_DebeCrearProveedorActivo() {
        // Act
        Proveedor resultado = frm.createNewEntity();

        // Assert
        assertTrue(resultado.getActivo());
    }

    @Test
    void createNewEntity_DebeInicializarCamposVacios() {
        // Act
        Proveedor resultado = frm.createNewEntity();

        // Assert
        assertNotNull(resultado.getNombre());
        assertNotNull(resultado.getRazonSocial());
        assertNotNull(resultado.getNit());
        assertNotNull(resultado.getObservaciones());
    }

    // ==================== PRUEBAS DE CONVERSIÓN DE ID ====================

    @Test
    void getIdAsText_ConProveedorValido_DebeRetornarIdComoString() {
        // Act
        String resultado = frm.getIdAsText(proveedorPrueba);

        // Assert
        assertNotNull(resultado);
        assertEquals("1", resultado);
    }

    @Test
    void getIdAsText_ConProveedorNulo_DebeRetornarNull() {
        // Act
        String resultado = frm.getIdAsText(null);

        // Assert
        assertNull(resultado);
    }

    @Test
    void getIdAsText_ConProveedorSinId_DebeRetornarNull() {
        // Arrange
        Proveedor proveedorSinId = new Proveedor();

        // Act
        String resultado = frm.getIdAsText(proveedorSinId);

        // Assert
        assertNull(resultado);
    }

    @Test
    void getIdAsText_ConIdCero_DebeRetornarCero() {
        // Arrange
        proveedorPrueba.setId(0);

        // Act
        String resultado = frm.getIdAsText(proveedorPrueba);

        // Assert
        assertEquals("0", resultado);
    }

    @Test
    void getIdAsText_ConIdNegativo_DebeRetornarNumeroNegativo() {
        // Arrange
        proveedorPrueba.setId(-1);

        // Act
        String resultado = frm.getIdAsText(proveedorPrueba);

        // Assert
        assertEquals("-1", resultado);
    }

    // ==================== PRUEBAS DE getIdByText ====================

    @Test
    void getIdByText_ConIdValido_DebeRetornarProveedor() {
        // Arrange
        when(dao.leer(1)).thenReturn(proveedorPrueba);

        // Act
        Proveedor resultado = frm.getIdByText("1");

        // Assert
        assertNotNull(resultado);
        assertEquals(proveedorPrueba.getId(), resultado.getId());
        assertEquals(proveedorPrueba.getNombre(), resultado.getNombre());
    }

    @Test
    void getIdByText_ConIdNulo_DebeRetornarNull() {
        // Act
        Proveedor resultado = frm.getIdByText(null);

        // Assert
        assertNull(resultado);
        verify(dao, never()).leer(any());
    }

    @Test
    void getIdByText_ConIdInvalido_DebeRetornarNull() {
        // Act
        Proveedor resultado = frm.getIdByText("abc");

        // Assert
        assertNull(resultado);
    }

    @Test
    void getIdByText_ConIdVacio_DebeRetornarNull() {
        // Act
        Proveedor resultado = frm.getIdByText("");

        // Assert
        assertNull(resultado);
    }

    @Test
    void getIdByText_ConExcepcionEnDAO_DebeRetornarNull() {
        // Arrange
        when(dao.leer(anyInt())).thenThrow(new RuntimeException("Error de base de datos"));

        // Act
        Proveedor resultado = frm.getIdByText("1");

        // Assert
        assertNull(resultado);
    }

    @Test
    void getIdByText_ConIdGrande_DebeConvertirCorrectamente() {
        // Arrange
        proveedorPrueba.setId(999999);
        when(dao.leer(999999)).thenReturn(proveedorPrueba);

        // Act
        Proveedor resultado = frm.getIdByText("999999");

        // Assert
        assertNotNull(resultado);
        assertEquals(999999, resultado.getId());
    }

    @Test
    void getIdByText_DebeUsarIntegerParseInt() {
        // Arrange
        when(dao.leer(42)).thenReturn(proveedorPrueba);

        // Act
        frm.getIdByText("42");

        // Assert
        verify(dao).leer(42);
    }

    // ==================== PRUEBAS DE SELECCIÓN ====================

    @Test
    void seleccionarRegistro_ConEventoValido_DebeCambiarEstado() {
        // Arrange
        lenient().when(selectEvent.getObject()).thenReturn(proveedorPrueba);

        // Act
        frm.seleccionarRegistro(selectEvent);

        // Assert
        assertEquals(ESTADO_CRUD.MODIFICAR, frm.getEstado());
        assertEquals(proveedorPrueba, frm.getRegistro());
    }

    @Test
    void seleccionarRegistro_ConEventoNulo_NoDebeCambiarEstado() {
        // Arrange
        ESTADO_CRUD estadoInicial = frm.getEstado();

        // Act
        frm.seleccionarRegistro(null);

        // Assert
        assertEquals(estadoInicial, frm.getEstado());
    }

    @Test
    void seleccionarRegistro_ConEventoSinObjeto_NoDebeCambiarEstado() {
        // Arrange
        when(selectEvent.getObject()).thenReturn(null);
        ESTADO_CRUD estadoInicial = frm.getEstado();

        // Act
        frm.seleccionarRegistro(selectEvent);

        // Assert
        assertEquals(estadoInicial, frm.getEstado());
    }

    // ==================== PRUEBAS DE buscarRegistroPorId ====================

    @Test
    void buscarRegistroPorId_ConIdValido_DebeRetornarProveedor() {
        // Arrange
        when(dao.leer(1)).thenReturn(proveedorPrueba);

        // Act
        Proveedor resultado = frm.buscarRegistroPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(proveedorPrueba.getId(), resultado.getId());
    }

    @Test
    void buscarRegistroPorId_ConIdNulo_DebeRetornarNull() {
        // Act
        Proveedor resultado = frm.buscarRegistroPorId(null);

        // Assert
        assertNull(resultado);
        verify(dao, never()).leer(any());
    }

    @Test
    void buscarRegistroPorId_ConDAONulo_DebeRetornarNull() {
        // Arrange
        frm = new ProveedorFrm(); // Sin inyección de DAO

        // Act
        Proveedor resultado = frm.buscarRegistroPorId(1);

        // Assert
        assertNull(resultado);
    }

    // ==================== PRUEBAS DE getEntityId ====================

    @Test
    void getEntityId_ConProveedorValido_DebeRetornarId() {
        // Act
        Object resultado = frm.getEntityId(proveedorPrueba);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado);
    }

    @Test
    void getEntityId_ConProveedorNulo_DebeRetornarNull() {
        // Act
        Object resultado = frm.getEntityId(null);

        // Assert
        assertNull(resultado);
    }

    @Test
    void getEntityId_ConProveedorSinId_DebeRetornarNull() {
        // Arrange
        Proveedor proveedorSinId = new Proveedor();

        // Act
        Object resultado = frm.getEntityId(proveedorSinId);

        // Assert
        assertNull(resultado);
    }

    // ==================== PRUEBAS DE getEntityName ====================

    @Test
    void getEntityName_DebeRetornarNombreBean() {
        // Act
        String resultado = frm.getEntityName();

        // Assert
        assertEquals("Proveedor", resultado);
    }

    // ==================== PRUEBAS DE nuevoRegistro ====================

    @Test
    void nuevoRegistro_DebeRetornarNuevaEntidad() {
        // Act
        Proveedor resultado = frm.nuevoRegistro();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
    }

    // ==================== PRUEBAS DE getDao ====================

    @Test
    void getDao_DebeRetornarProveedorDAO() {
        // Act
        var resultado = frm.getDao();

        // Assert
        assertNotNull(resultado);
        assertEquals(dao, resultado);
    }

    // ==================== PRUEBAS DE configurarNuevoRegistro ====================

    @Test
    void configurarNuevoRegistro_NoDebeGenerarExcepcion() {
        // Act & Assert
        assertDoesNotThrow(() -> frm.configurarNuevoRegistro());
    }

    // ==================== PRUEBAS DE CONSTRUCTOR ====================

    @Test
    void constructor_DebeInicializarNombreBean() {
        // Arrange & Act
        ProveedorFrm nuevoFrm = new ProveedorFrm();

        // Assert
        assertNotNull(nuevoFrm.nombreBean);
        assertEquals("Proveedor", nuevoFrm.nombreBean);
    }
}
