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
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.AlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoAlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Almacen;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoAlmacen;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AlmacenFrmTest {

    @Mock
    private FacesContext facesContext;

    @Mock
    private Application application;

    @Mock
    private AlmacenDAO almacenDAO;

    @Mock
    private TipoAlmacenDAO tipoAlmacenDAO;

    @InjectMocks
    private AlmacenFrm almacenFrm;

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
        AlmacenFrm frm = new AlmacenFrm();
        assertEquals("Almacén", frm.getNombreBean());
    }

    @Test
    void testGetDao() {
        assertEquals(almacenDAO, almacenFrm.getDao());
    }

    // ==================== TESTS DE INICIALIZAR LISTAS ====================

    @Test
    void testInicializarListas_ConDatos() {
        TipoAlmacen tipo1 = new TipoAlmacen();
        tipo1.setId(1);
        tipo1.setNombre("Almacén General");
        tipo1.setActivo(true);

        TipoAlmacen tipo2 = new TipoAlmacen();
        tipo2.setId(2);
        tipo2.setNombre("Almacén Refrigerado");
        tipo2.setActivo(true);

        when(tipoAlmacenDAO.findRange(0, Integer.MAX_VALUE)).thenReturn(Arrays.asList(tipo1, tipo2));

        almacenFrm.inicializarListas();

        assertNotNull(almacenFrm.getListaTipoAlmacen());
        assertEquals(2, almacenFrm.getListaTipoAlmacen().size());
        verify(tipoAlmacenDAO, times(1)).findRange(0, Integer.MAX_VALUE);
    }

    @Test
    void testInicializarListas_SinDatos() {
        when(tipoAlmacenDAO.findRange(0, Integer.MAX_VALUE)).thenReturn(Collections.emptyList());

        almacenFrm.inicializarListas();

        assertNotNull(almacenFrm.getListaTipoAlmacen());
        assertTrue(almacenFrm.getListaTipoAlmacen().isEmpty());
    }

    @Test
    void testInicializarListas_ConExcepcion() {
        when(tipoAlmacenDAO.findRange(0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("Error de base de datos"));

        almacenFrm.inicializarListas();

        assertNotNull(almacenFrm.getListaTipoAlmacen());
        assertTrue(almacenFrm.getListaTipoAlmacen().isEmpty());
    }

    // ==================== TESTS DE CREACIÓN ====================

    @Test
    void testCreateNewEntity_ConListaTipoAlmacenActivos() {
        TipoAlmacen tipoActivo1 = new TipoAlmacen();
        tipoActivo1.setId(1);
        tipoActivo1.setNombre("Tipo Activo 1");
        tipoActivo1.setActivo(true);

        TipoAlmacen tipoInactivo = new TipoAlmacen();
        tipoInactivo.setId(2);
        tipoInactivo.setNombre("Tipo Inactivo");
        tipoInactivo.setActivo(false);

        TipoAlmacen tipoActivo2 = new TipoAlmacen();
        tipoActivo2.setId(3);
        tipoActivo2.setNombre("Tipo Activo 2");
        tipoActivo2.setActivo(true);

        almacenFrm.setListaTipoAlmacen(Arrays.asList(tipoActivo1, tipoInactivo, tipoActivo2));

        Almacen resultado = almacenFrm.createNewEntity();

        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
        assertNotNull(resultado.getIdTipoAlmacen());
        assertEquals(tipoActivo1, resultado.getIdTipoAlmacen());
    }

    @Test
    void testCreateNewEntity_ConListaVacia() {
        almacenFrm.setListaTipoAlmacen(Collections.emptyList());

        Almacen resultado = almacenFrm.createNewEntity();

        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
        assertNull(resultado.getIdTipoAlmacen());
    }

    @Test
    void testCreateNewEntity_ConListaNula() {
        almacenFrm.setListaTipoAlmacen(null);

        Almacen resultado = almacenFrm.createNewEntity();

        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
        assertNull(resultado.getIdTipoAlmacen());
    }

    @Test
    void testCreateNewEntity_SoloTiposInactivos() {
        TipoAlmacen tipoInactivo = new TipoAlmacen();
        tipoInactivo.setId(1);
        tipoInactivo.setNombre("Tipo Inactivo");
        tipoInactivo.setActivo(false);

        almacenFrm.setListaTipoAlmacen(Collections.singletonList(tipoInactivo));

        Almacen resultado = almacenFrm.createNewEntity();

        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
        assertNull(resultado.getIdTipoAlmacen());
    }

    // ==================== TESTS DE CONVERSIÓN DE ID ====================

    @Test
    void testGetIdAsText_ConAlmacenValido() {
        Almacen almacen = new Almacen();
        almacen.setId(1);

        String resultado = almacenFrm.getIdAsText(almacen);

        assertNotNull(resultado);
        assertEquals("1", resultado);
    }

    @Test
    void testGetIdAsText_ConAlmacenNulo() {
        String resultado = almacenFrm.getIdAsText(null);
        assertNull(resultado);
    }

    @Test
    void testGetIdAsText_ConIdNulo() {
        Almacen almacen = new Almacen();
        almacen.setId(null);

        String resultado = almacenFrm.getIdAsText(almacen);

        assertNull(resultado);
    }

    @Test
    void testGetIdByText_ConModeloYDatos() {
        // El método getIdByText busca en modelo.getWrappedData() que es complejo de
        // mockear
        // En su lugar, verificamos que retorna null cuando el wrapped data está vacío
        when(almacenDAO.count()).thenReturn(0);
        when(almacenDAO.findRange(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        almacenFrm.inicializarRegistros();

        Almacen resultado = almacenFrm.getIdByText("1");

        // Esperamos null porque el modelo no tiene wrapped data
        assertNull(resultado);
    }

    @Test
    void testGetIdByText_ConIdNulo() {
        Almacen resultado = almacenFrm.getIdByText(null);
        assertNull(resultado);
    }

    @Test
    void testGetIdByText_ConIdInvalido() {
        when(almacenDAO.count()).thenReturn(1);
        almacenFrm.inicializarRegistros();

        Almacen resultado = almacenFrm.getIdByText("no-es-un-numero");

        assertNull(resultado);
    }

    // ==================== TESTS DE BÚSQUEDA ====================

    @Test
    void testBuscarRegistroPorId_ConIdValido() {
        Almacen almacen = new Almacen();
        almacen.setId(1);
        almacen.setActivo(true);

        when(almacenDAO.leer(1)).thenReturn(almacen);

        Almacen resultado = almacenFrm.buscarRegistroPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        verify(almacenDAO, times(1)).leer(1);
    }

    @Test
    void testBuscarRegistroPorId_ConIdNulo() {
        Almacen resultado = almacenFrm.buscarRegistroPorId(null);
        assertNull(resultado);
        verify(almacenDAO, never()).leer(any());
    }

    @Test
    void testBuscarRegistroPorId_ConDAONulo() {
        almacenFrm = new AlmacenFrm();

        Almacen resultado = almacenFrm.buscarRegistroPorId(1);

        assertNull(resultado);
    }

    // ==================== TESTS DE SELECCIÓN ====================

    @Test
    void testSeleccionarRegistro_ConEventoValido() {
        Almacen almacen = new Almacen();
        almacen.setId(1);
        almacen.setActivo(true);

        SelectEvent<Almacen> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(almacen);

        almacenFrm.seleccionarRegistro(event);

        assertEquals(almacen, almacenFrm.getRegistro());
        assertEquals(ESTADO_CRUD.MODIFICAR, almacenFrm.getEstado());
    }

    @Test
    void testSeleccionarRegistro_ConEventoNulo() {
        Almacen registroAnterior = new Almacen();
        almacenFrm.setRegistro(registroAnterior);

        almacenFrm.seleccionarRegistro(null);

        assertEquals(registroAnterior, almacenFrm.getRegistro());
    }

    @Test
    void testSeleccionarRegistro_ConObjetoNulo() {
        SelectEvent<Almacen> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(null);

        almacenFrm.seleccionarRegistro(event);

        assertNotEquals(ESTADO_CRUD.MODIFICAR, almacenFrm.getEstado());
    }

    // ==================== TESTS DE TIPO ALMACEN SELECCIONADO ====================

    @Test
    void testGetIdTipoAlmacenSeleccionado_ConTipoAsignado() {
        TipoAlmacen tipo = new TipoAlmacen();
        tipo.setId(1);

        Almacen almacen = new Almacen();
        almacen.setIdTipoAlmacen(tipo);

        almacenFrm.setRegistro(almacen);

        Integer resultado = almacenFrm.getIdTipoAlmacenSeleccionado();

        assertNotNull(resultado);
        assertEquals(1, resultado);
    }

    @Test
    void testGetIdTipoAlmacenSeleccionado_SinRegistro() {
        almacenFrm.setRegistro(null);

        Integer resultado = almacenFrm.getIdTipoAlmacenSeleccionado();

        assertNull(resultado);
    }

    @Test
    void testGetIdTipoAlmacenSeleccionado_SinTipoAsignado() {
        Almacen almacen = new Almacen();
        almacen.setIdTipoAlmacen(null);

        almacenFrm.setRegistro(almacen);

        Integer resultado = almacenFrm.getIdTipoAlmacenSeleccionado();

        assertNull(resultado);
    }

    @Test
    void testSetIdTipoAlmacenSeleccionado_ConTipoActivo() {
        TipoAlmacen tipo1 = new TipoAlmacen();
        tipo1.setId(1);
        tipo1.setActivo(true);

        TipoAlmacen tipo2 = new TipoAlmacen();
        tipo2.setId(2);
        tipo2.setActivo(true);

        almacenFrm.setListaTipoAlmacen(Arrays.asList(tipo1, tipo2));

        Almacen almacen = new Almacen();
        almacenFrm.setRegistro(almacen);

        almacenFrm.setIdTipoAlmacenSeleccionado(2);

        assertNotNull(almacen.getIdTipoAlmacen());
        assertEquals(2, almacen.getIdTipoAlmacen().getId());
    }

    @Test
    void testSetIdTipoAlmacenSeleccionado_ConTipoInactivo() {
        TipoAlmacen tipoInactivo = new TipoAlmacen();
        tipoInactivo.setId(1);
        tipoInactivo.setActivo(false);

        almacenFrm.setListaTipoAlmacen(Collections.singletonList(tipoInactivo));

        Almacen almacen = new Almacen();
        almacenFrm.setRegistro(almacen);

        almacenFrm.setIdTipoAlmacenSeleccionado(1);

        assertNull(almacen.getIdTipoAlmacen());
    }

    @Test
    void testSetIdTipoAlmacenSeleccionado_ConIdNulo() {
        Almacen almacen = new Almacen();
        almacenFrm.setRegistro(almacen);

        almacenFrm.setIdTipoAlmacenSeleccionado(null);

        assertNull(almacen.getIdTipoAlmacen());
    }

    @Test
    void testSetIdTipoAlmacenSeleccionado_SinRegistro() {
        almacenFrm.setRegistro(null);

        // No debe lanzar excepción
        assertDoesNotThrow(() -> almacenFrm.setIdTipoAlmacenSeleccionado(1));
    }

    @Test
    void testSetIdTipoAlmacenSeleccionado_SinLista() {
        almacenFrm.setListaTipoAlmacen(null);
        Almacen almacen = new Almacen();
        almacenFrm.setRegistro(almacen);

        // No debe lanzar excepción
        assertDoesNotThrow(() -> almacenFrm.setIdTipoAlmacenSeleccionado(1));
    }

    // ==================== TESTS DE LISTA TIPO ALMACEN ACTIVOS ====================

    @Test
    void testGetListaTipoAlmacenActivos_ConActivosEInactivos() {
        TipoAlmacen tipo1 = new TipoAlmacen();
        tipo1.setId(1);
        tipo1.setActivo(true);

        TipoAlmacen tipo2 = new TipoAlmacen();
        tipo2.setId(2);
        tipo2.setActivo(false);

        TipoAlmacen tipo3 = new TipoAlmacen();
        tipo3.setId(3);
        tipo3.setActivo(true);

        almacenFrm.setListaTipoAlmacen(Arrays.asList(tipo1, tipo2, tipo3));

        List<TipoAlmacen> resultado = almacenFrm.getListaTipoAlmacenActivos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(t -> Boolean.TRUE.equals(t.getActivo())));
    }

    @Test
    void testGetListaTipoAlmacenActivos_ConListaNula() {
        almacenFrm.setListaTipoAlmacen(null);

        List<TipoAlmacen> resultado = almacenFrm.getListaTipoAlmacenActivos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testGetListaTipoAlmacenActivos_SoloInactivos() {
        TipoAlmacen tipoInactivo = new TipoAlmacen();
        tipoInactivo.setId(1);
        tipoInactivo.setActivo(false);

        almacenFrm.setListaTipoAlmacen(Collections.singletonList(tipoInactivo));

        List<TipoAlmacen> resultado = almacenFrm.getListaTipoAlmacenActivos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ==================== TESTS ADICIONALES ====================

    @Test
    void testGetEntityId_ConAlmacenValido() {
        Almacen almacen = new Almacen();
        almacen.setId(1);

        Object resultado = almacenFrm.getEntityId(almacen);

        assertNotNull(resultado);
        assertEquals(1, resultado);
    }

    @Test
    void testGetEntityId_ConAlmacenNulo() {
        Object resultado = almacenFrm.getEntityId(null);
        assertNull(resultado);
    }

    @Test
    void testGetEntityName() {
        String resultado = almacenFrm.getEntityName();
        assertEquals("Almacén", resultado);
    }

    @Test
    void testNuevoRegistro() {
        Almacen resultado = almacenFrm.nuevoRegistro();
        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
    }

    @Test
    void testInicializarRegistros() {
        when(almacenDAO.count()).thenReturn(3);
        when(almacenDAO.findRange(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        almacenFrm.inicializarRegistros();

        LazyDataModel<Almacen> modelo = almacenFrm.getModelo();
        assertNotNull(modelo);

        // Simular carga de datos
        modelo.load(0, 5, Collections.emptyMap(), Collections.emptyMap());

        verify(almacenDAO, atLeastOnce()).findRange(anyInt(), anyInt());
    }
}
