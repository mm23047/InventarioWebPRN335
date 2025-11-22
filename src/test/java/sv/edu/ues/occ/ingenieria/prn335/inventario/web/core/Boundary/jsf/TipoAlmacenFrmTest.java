package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.ESTADO_CRUD;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoAlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoAlmacen;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TipoAlmacenFrmTest {

    @Mock
    private FacesContext facesContext;

    @Mock
    private Application application;

    @Mock
    private TipoAlmacenDAO tipoAlmacenDAO;

    @InjectMocks
    private TipoAlmacenFrm tipoAlmacenFrm;

    private ResourceBundle resourceBundle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        resourceBundle = ResourceBundle.getBundle("crud", Locale.getDefault());

        lenient().when(facesContext.getApplication()).thenReturn(application);
        lenient().when(application.getResourceBundle(facesContext, "msg")).thenReturn(resourceBundle);
    }

    // ==================== TESTS DE INICIALIZACIÓN ====================

    @Test
    void testConstructor() {
        TipoAlmacenFrm frm = new TipoAlmacenFrm();
        assertEquals("Tipo de Almacén", frm.getNombreBean());
    }

    @Test
    void testGetDao() {
        assertEquals(tipoAlmacenDAO, tipoAlmacenFrm.getDao());
    }

    // ==================== TESTS DE CONVERSIÓN DE ID ====================

    @Test
    void testGetIdAsText_ConTipoAlmacenValido() {
        TipoAlmacen tipo = new TipoAlmacen();
        tipo.setId(1);

        String resultado = tipoAlmacenFrm.getIdAsText(tipo);

        assertNotNull(resultado);
        assertEquals("1", resultado);
    }

    @Test
    void testGetIdAsText_ConTipoAlmacenNulo() {
        String resultado = tipoAlmacenFrm.getIdAsText(null);
        assertNull(resultado);
    }

    @Test
    void testGetIdAsText_ConIdNulo() {
        TipoAlmacen tipo = new TipoAlmacen();
        tipo.setId(null);

        String resultado = tipoAlmacenFrm.getIdAsText(tipo);

        assertNull(resultado);
    }

    @Test
    void testGetIdByText_ConIdValido() {
        TipoAlmacen tipo = new TipoAlmacen();
        tipo.setId(1);
        tipo.setNombre("Almacén Refrigerado");

        when(tipoAlmacenDAO.leer(1)).thenReturn(tipo);

        TipoAlmacen resultado = tipoAlmacenFrm.getIdByText("1");

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Almacén Refrigerado", resultado.getNombre());
        verify(tipoAlmacenDAO, times(1)).leer(1);
    }

    @Test
    void testGetIdByText_ConIdNulo() {
        TipoAlmacen resultado = tipoAlmacenFrm.getIdByText(null);
        assertNull(resultado);
        verify(tipoAlmacenDAO, never()).leer(any());
    }

    @Test
    void testGetIdByText_ConIdInvalido() {
        String idInvalido = "no-es-un-numero";

        TipoAlmacen resultado = tipoAlmacenFrm.getIdByText(idInvalido);

        assertNull(resultado);
        verify(tipoAlmacenDAO, never()).leer(any());
    }

    @Test
    void testGetIdByText_ConExcepcion() {
        when(tipoAlmacenDAO.leer(1)).thenThrow(new RuntimeException("Error de base de datos"));

        TipoAlmacen resultado = tipoAlmacenFrm.getIdByText("1");

        assertNull(resultado);
    }

    // ==================== TESTS DE CREACIÓN ====================

    @Test
    void testCreateNewEntity() {
        TipoAlmacen resultado = tipoAlmacenFrm.createNewEntity();

        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
        assertEquals("", resultado.getNombre());
        assertEquals("", resultado.getObsevaciones());
    }

    @Test
    void testCreateNewEntity_VerificarAtributoObsevaciones() {
        // Verificar que se usa "obsevaciones" (con typo) según la entidad
        TipoAlmacen resultado = tipoAlmacenFrm.createNewEntity();

        assertNotNull(resultado);
        // El método getObsevaciones() debe existir (con el typo)
        assertDoesNotThrow(() -> resultado.getObsevaciones());
    }

    @Test
    void testNuevoRegistro() {
        TipoAlmacen resultado = tipoAlmacenFrm.nuevoRegistro();

        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
    }

    // ==================== TESTS DE BÚSQUEDA ====================

    @Test
    void testBuscarRegistroPorId_ConIdValido() {
        TipoAlmacen tipo = new TipoAlmacen();
        tipo.setId(1);
        tipo.setNombre("Almacén Seco");

        when(tipoAlmacenDAO.leer(1)).thenReturn(tipo);

        TipoAlmacen resultado = tipoAlmacenFrm.buscarRegistroPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Almacén Seco", resultado.getNombre());
        verify(tipoAlmacenDAO, times(1)).leer(1);
    }

    @Test
    void testBuscarRegistroPorId_ConIdNulo() {
        TipoAlmacen resultado = tipoAlmacenFrm.buscarRegistroPorId(null);
        assertNull(resultado);
        verify(tipoAlmacenDAO, never()).leer(any());
    }

    @Test
    void testBuscarRegistroPorId_ConDAONulo() {
        tipoAlmacenFrm = new TipoAlmacenFrm();
        // No se inyecta tipoAlmacenDAO, por lo que será null

        TipoAlmacen resultado = tipoAlmacenFrm.buscarRegistroPorId(1);

        assertNull(resultado);
    }

    // ==================== TESTS DE SELECCIÓN ====================

    @Test
    void testSeleccionarRegistro_ConEventoValido() {
        TipoAlmacen tipo = new TipoAlmacen();
        tipo.setId(1);
        tipo.setNombre("Almacén General");

        SelectEvent<TipoAlmacen> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(tipo);

        tipoAlmacenFrm.seleccionarRegistro(event);

        assertEquals(tipo, tipoAlmacenFrm.getRegistro());
        assertEquals(ESTADO_CRUD.MODIFICAR, tipoAlmacenFrm.getEstado());
    }

    @Test
    void testSeleccionarRegistro_ConEventoNulo() {
        TipoAlmacen registroAnterior = new TipoAlmacen();
        tipoAlmacenFrm.setRegistro(registroAnterior);

        tipoAlmacenFrm.seleccionarRegistro(null);

        assertEquals(registroAnterior, tipoAlmacenFrm.getRegistro());
    }

    @Test
    void testSeleccionarRegistro_ConObjetoNulo() {
        SelectEvent<TipoAlmacen> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(null);

        tipoAlmacenFrm.seleccionarRegistro(event);

        assertNotEquals(ESTADO_CRUD.MODIFICAR, tipoAlmacenFrm.getEstado());
    }

    // ==================== TESTS DE ENTITY ID ====================

    @Test
    void testGetEntityId_ConTipoAlmacenValido() {
        TipoAlmacen tipo = new TipoAlmacen();
        tipo.setId(1);

        Object resultado = tipoAlmacenFrm.getEntityId(tipo);

        assertNotNull(resultado);
        assertEquals(1, resultado);
    }

    @Test
    void testGetEntityId_ConTipoAlmacenNulo() {
        Object resultado = tipoAlmacenFrm.getEntityId(null);
        assertNull(resultado);
    }

    // ==================== TESTS DE LAZY DATA MODEL ====================

    @Test
    void testInicializarRegistros() {
        when(tipoAlmacenDAO.count()).thenReturn(4);
        when(tipoAlmacenDAO.findRange(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        tipoAlmacenFrm.inicializarRegistros();

        LazyDataModel<TipoAlmacen> modelo = tipoAlmacenFrm.getModelo();
        assertNotNull(modelo);

        // Simular carga de datos
        modelo.load(0, 5, Collections.emptyMap(), Collections.emptyMap());

        verify(tipoAlmacenDAO, atLeastOnce()).findRange(anyInt(), anyInt());
    }

    @Test
    void testLazyDataModel_GetRowKey() {
        TipoAlmacen tipo = new TipoAlmacen();
        tipo.setId(1);

        when(tipoAlmacenDAO.count()).thenReturn(1);
        tipoAlmacenFrm.inicializarRegistros();

        String rowKey = tipoAlmacenFrm.getModelo().getRowKey(tipo);

        assertEquals("1", rowKey);
    }

    @Test
    void testLazyDataModel_GetRowData() {
        TipoAlmacen tipo = new TipoAlmacen();
        tipo.setId(1);

        when(tipoAlmacenDAO.count()).thenReturn(1);
        when(tipoAlmacenDAO.leer(1)).thenReturn(tipo);
        tipoAlmacenFrm.inicializarRegistros();

        TipoAlmacen resultado = tipoAlmacenFrm.getModelo().getRowData("1");

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
    }

    @Test
    void testLazyDataModel_Load_ConDatos() {
        TipoAlmacen tipo1 = new TipoAlmacen();
        tipo1.setId(1);
        tipo1.setNombre("Tipo 1");

        TipoAlmacen tipo2 = new TipoAlmacen();
        tipo2.setId(2);
        tipo2.setNombre("Tipo 2");

        when(tipoAlmacenDAO.count()).thenReturn(2);
        when(tipoAlmacenDAO.findRange(0, 5)).thenReturn(Arrays.asList(tipo1, tipo2));

        tipoAlmacenFrm.inicializarRegistros();
        List<TipoAlmacen> resultado = tipoAlmacenFrm.getModelo().load(0, 5, Collections.emptyMap(),
                Collections.emptyMap());

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(tipoAlmacenDAO, times(1)).findRange(0, 5);
    }

    // ==================== TESTS ADICIONALES ====================

    @Test
    void testGetEntityName() {
        String resultado = tipoAlmacenFrm.getEntityName();
        assertEquals("Tipo de Almacén", resultado);
    }

    @Test
    void testConfigurarNuevoRegistro() {
        // Este método está vacío pero debe existir
        assertDoesNotThrow(() -> tipoAlmacenFrm.configurarNuevoRegistro());
    }

    @Test
    void testCreateNewEntity_ValoresDefecto() {
        TipoAlmacen nuevo = tipoAlmacenFrm.createNewEntity();

        assertTrue(nuevo.getActivo());
        assertNotNull(nuevo.getNombre());
        assertNotNull(nuevo.getObsevaciones());
        assertTrue(nuevo.getNombre().isEmpty());
        assertTrue(nuevo.getObsevaciones().isEmpty());
    }
}
