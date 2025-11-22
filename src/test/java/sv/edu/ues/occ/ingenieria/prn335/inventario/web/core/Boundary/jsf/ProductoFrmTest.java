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
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.ESTADO_CRUD;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Producto;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductoFrmTest {

    @Mock
    private FacesContext facesContext;

    @Mock
    private Application application;

    @Mock
    private ProductoDAO productoDAO;

    @Mock
    private ProductoTipoProductoFrm ptpFrm;

    @InjectMocks
    private ProductoFrm productoFrm;

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
        ProductoFrm frm = new ProductoFrm();
        assertEquals("Producto", frm.getNombreBean());
    }

    @Test
    void testGetDao() {
        assertEquals(productoDAO, productoFrm.getDao());
    }

    // ==================== TESTS DE CONVERSIÓN DE ID ====================

    @Test
    void testGetIdAsText_ConProductoValido() {
        UUID id = UUID.randomUUID();
        Producto producto = new Producto();
        producto.setId(id);

        String resultado = productoFrm.getIdAsText(producto);

        assertNotNull(resultado);
        assertEquals(id.toString(), resultado);
    }

    @Test
    void testGetIdAsText_ConProductoNulo() {
        String resultado = productoFrm.getIdAsText(null);
        assertNull(resultado);
    }

    @Test
    void testGetIdAsText_ConIdNulo() {
        Producto producto = new Producto();
        producto.setId(null);

        String resultado = productoFrm.getIdAsText(producto);

        assertNull(resultado);
    }

    @Test
    void testGetIdByText_ConIdValido() {
        UUID id = UUID.randomUUID();
        Producto producto = new Producto();
        producto.setId(id);

        when(productoDAO.leer(id)).thenReturn(producto);

        Producto resultado = productoFrm.getIdByText(id.toString());

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        verify(productoDAO, times(1)).leer(id);
    }

    @Test
    void testGetIdByText_ConIdNulo() {
        Producto resultado = productoFrm.getIdByText(null);
        assertNull(resultado);
        verify(productoDAO, never()).leer(any());
    }

    @Test
    void testGetIdByText_ConIdInvalido() {
        String idInvalido = "no-es-un-uuid";

        Producto resultado = productoFrm.getIdByText(idInvalido);

        assertNull(resultado);
        verify(productoDAO, never()).leer(any());
    }

    @Test
    void testGetIdByText_ConExcepcion() {
        UUID id = UUID.randomUUID();
        when(productoDAO.leer(id)).thenThrow(new RuntimeException("Error de base de datos"));

        Producto resultado = productoFrm.getIdByText(id.toString());

        assertNull(resultado);
    }

    // ==================== TESTS DE CREACIÓN ====================

    @Test
    void testCreateNewEntity() {
        Producto resultado = productoFrm.createNewEntity();

        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertTrue(resultado.getActivo());
        assertEquals("", resultado.getNombreProducto());
        assertEquals("", resultado.getReferenciaExterna());
        assertEquals("", resultado.getComentarios());
    }

    @Test
    void testNuevoRegistro() {
        Producto resultado = productoFrm.nuevoRegistro();

        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertTrue(resultado.getActivo());
    }

    // ==================== TESTS DE BÚSQUEDA ====================

    @Test
    void testBuscarRegistroPorId_ConIdValido() {
        UUID id = UUID.randomUUID();
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombreProducto("Producto Test");

        when(productoDAO.leer(id)).thenReturn(producto);

        Producto resultado = productoFrm.buscarRegistroPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Producto Test", resultado.getNombreProducto());
        verify(productoDAO, times(1)).leer(id);
    }

    @Test
    void testBuscarRegistroPorId_ConIdNulo() {
        Producto resultado = productoFrm.buscarRegistroPorId(null);
        assertNull(resultado);
        verify(productoDAO, never()).leer(any());
    }

    @Test
    void testBuscarRegistroPorId_ConDAONulo() {
        productoFrm = new ProductoFrm();
        // No se inyecta productoDAO, por lo que será null

        Producto resultado = productoFrm.buscarRegistroPorId(UUID.randomUUID());

        assertNull(resultado);
    }

    // ==================== TESTS DE SELECCIÓN ====================

    @Test
    void testSeleccionarRegistro_ConEventoValido() {
        UUID id = UUID.randomUUID();
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombreProducto("Producto Seleccionado");

        SelectEvent<Producto> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(producto);

        productoFrm.seleccionarRegistro(event);

        assertEquals(producto, productoFrm.getRegistro());
        assertEquals(ESTADO_CRUD.MODIFICAR, productoFrm.getEstado());
        verify(ptpFrm, times(1)).setIdProducto(id);
    }

    @Test
    void testSeleccionarRegistro_ConEventoNulo() {
        Producto registroAnterior = new Producto();
        productoFrm.setRegistro(registroAnterior);

        productoFrm.seleccionarRegistro(null);

        assertEquals(registroAnterior, productoFrm.getRegistro());
        verify(ptpFrm, never()).setIdProducto(any());
    }

    @Test
    void testSeleccionarRegistro_ConObjetoNulo() {
        SelectEvent<Producto> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(null);

        productoFrm.seleccionarRegistro(event);

        verify(ptpFrm, never()).setIdProducto(any());
    }

    // ==================== TESTS DE ENTITY ID ====================

    @Test
    void testGetEntityId_ConProductoValido() {
        UUID id = UUID.randomUUID();
        Producto producto = new Producto();
        producto.setId(id);

        Object resultado = productoFrm.getEntityId(producto);

        assertNotNull(resultado);
        assertEquals(id, resultado);
    }

    @Test
    void testGetEntityId_ConProductoNulo() {
        Object resultado = productoFrm.getEntityId(null);
        assertNull(resultado);
    }

    // ==================== TESTS DE LAZY DATA MODEL ====================

    @Test
    void testInicializarRegistros() {
        when(productoDAO.count()).thenReturn(5);
        when(productoDAO.findRange(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        productoFrm.inicializarRegistros();

        LazyDataModel<Producto> modelo = productoFrm.getModelo();
        assertNotNull(modelo);

        // Simular carga de datos
        modelo.load(0, 5, Collections.emptyMap(), Collections.emptyMap());

        verify(productoDAO, atLeastOnce()).findRange(anyInt(), anyInt());
    }

    @Test
    void testLazyDataModel_GetRowKey() {
        UUID id = UUID.randomUUID();
        Producto producto = new Producto();
        producto.setId(id);

        when(productoDAO.count()).thenReturn(1);
        productoFrm.inicializarRegistros();

        String rowKey = productoFrm.getModelo().getRowKey(producto);

        assertEquals(id.toString(), rowKey);
    }

    @Test
    void testLazyDataModel_GetRowData() {
        UUID id = UUID.randomUUID();
        Producto producto = new Producto();
        producto.setId(id);

        when(productoDAO.count()).thenReturn(1);
        when(productoDAO.leer(id)).thenReturn(producto);
        productoFrm.inicializarRegistros();

        Producto resultado = productoFrm.getModelo().getRowData(id.toString());

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
    }

    // ==================== TESTS DE INTEGRACIÓN CON ProductoTipoProductoFrm
    // ====================

    @Test
    void testGetPtpFrm_ConRegistroValido() {
        UUID id = UUID.randomUUID();
        Producto producto = new Producto();
        producto.setId(id);
        productoFrm.setRegistro(producto);

        ProductoTipoProductoFrm resultado = productoFrm.getPtpFrm();

        assertNotNull(resultado);
        verify(ptpFrm, times(1)).setIdProducto(id);
    }

    @Test
    void testGetPtpFrm_ConRegistroNulo() {
        productoFrm.setRegistro(null);

        ProductoTipoProductoFrm resultado = productoFrm.getPtpFrm();

        assertNotNull(resultado);
        verify(ptpFrm, never()).setIdProducto(any());
    }

    @Test
    void testGetPtpFrm_ConIdNulo() {
        Producto producto = new Producto();
        producto.setId(null);
        productoFrm.setRegistro(producto);

        ProductoTipoProductoFrm resultado = productoFrm.getPtpFrm();

        assertNotNull(resultado);
        verify(ptpFrm, never()).setIdProducto(any());
    }

    // ==================== TESTS ADICIONALES ====================

    @Test
    void testGetEntityName() {
        String resultado = productoFrm.getEntityName();
        assertEquals("Producto", resultado);
    }

    @Test
    void testConfigurarNuevoRegistro() {
        // Este método está vacío pero debe existir
        assertDoesNotThrow(() -> productoFrm.configurarNuevoRegistro());
    }
}
