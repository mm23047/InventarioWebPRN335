package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.application.Application;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.UnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoUnidadMedida;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.UnidadMedida;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * Pruebas unitarias para UnidadMedidaFrm
 * Cubre LazyDataModel con filtrado por TipoUnidadMedida y lógica de selección
 */
@ExtendWith(MockitoExtension.class)
class UnidadMedidaFrmTest {

    @Mock
    private UnidadMedidaDAO unidadMedidaDAO;

    @Mock
    private TipoUnidadMedidaDAO tipoUnidadMedidaDAO;

    @Mock
    private FacesContext facesContext;

    @Mock
    private Application application;

    @Mock
    private ResourceBundle resourceBundle;

    @Mock
    private SelectEvent<UnidadMedida> selectEvent;

    private UnidadMedidaFrm frm;

    private UnidadMedida unidadMedidaPrueba;
    private TipoUnidadMedida tipoUnidadMedida;
    private List<TipoUnidadMedida> listaTipos;

    @BeforeEach
    void setUp() {
        // Crear tipo de unidad de medida
        tipoUnidadMedida = new TipoUnidadMedida();
        tipoUnidadMedida.setId(1);
        tipoUnidadMedida.setNombre("Longitud");
        tipoUnidadMedida.setActivo(true);

        TipoUnidadMedida tipo2 = new TipoUnidadMedida();
        tipo2.setId(2);
        tipo2.setNombre("Peso");
        tipo2.setActivo(true);

        listaTipos = Arrays.asList(tipoUnidadMedida, tipo2);

        // Crear unidad de medida de prueba
        unidadMedidaPrueba = new UnidadMedida();
        unidadMedidaPrueba.setId(1);
        unidadMedidaPrueba.setExpresionRegular("^\\d+(\\.\\d+)?\\s*m$");
        unidadMedidaPrueba.setComentarios("Metro - unidad de longitud");
        unidadMedidaPrueba.setIdTipoUnidadMedida(tipoUnidadMedida);
        unidadMedidaPrueba.setActivo(true);

        // Inicializar el frm manualmente
        frm = new UnidadMedidaFrm();
        frm.unidadMedidaDAO = unidadMedidaDAO;
        frm.tipoUnidadMedidaDAO = tipoUnidadMedidaDAO;
        frm.facesContext = facesContext;

        // Configurar mocks con lenient()
        lenient().when(facesContext.getApplication()).thenReturn(application);
        lenient().when(application.getResourceBundle(any(), eq("crud"))).thenReturn(resourceBundle);
        lenient().when(resourceBundle.getString(anyString())).thenReturn("Mensaje de prueba");
    }

    // ==================== PRUEBAS DE CREACIÓN DE ENTIDAD ====================

    @Test
    void createNewEntity_SinTipoActual_DebeCrearConPrimerTipo() {
        // Arrange
        frm.setListaTipoUnidadMedida(listaTipos);

        // Act
        UnidadMedida resultado = frm.createNewEntity();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
        assertNotNull(resultado.getIdTipoUnidadMedida());
        assertEquals(tipoUnidadMedida.getId(), resultado.getIdTipoUnidadMedida().getId());
    }

    @Test
    void createNewEntity_ConTipoActual_DebeAsignarTipoActual() {
        // Arrange
        frm.setTipoUnidadMedidaActual(tipoUnidadMedida);

        // Act
        UnidadMedida resultado = frm.createNewEntity();

        // Assert
        assertNotNull(resultado);
        assertEquals(tipoUnidadMedida, resultado.getIdTipoUnidadMedida());
    }

    @Test
    void createNewEntity_DebeCrearUnidadActiva() {
        // Act
        UnidadMedida resultado = frm.createNewEntity();

        // Assert
        assertTrue(resultado.getActivo());
    }

    @Test
    void createNewEntity_SinListaTipos_NoDebeGenerarExcepcion() {
        // Act
        UnidadMedida resultado = frm.createNewEntity();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
    }

    // ==================== PRUEBAS DE inicializarListas ====================

    @Test
    void inicializarListas_DebeCargarTiposUnidadMedida() {
        // Arrange
        when(tipoUnidadMedidaDAO.findRange(0, Integer.MAX_VALUE)).thenReturn(listaTipos);

        // Act
        frm.inicializarListas();

        // Assert
        assertNotNull(frm.getListaTipoUnidadMedida());
        assertEquals(2, frm.getListaTipoUnidadMedida().size());
    }

    @Test
    void inicializarListas_ConExcepcion_DebeAsignarListaVacia() {
        // Arrange
        when(tipoUnidadMedidaDAO.findRange(anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Act
        frm.inicializarListas();

        // Assert
        assertNotNull(frm.getListaTipoUnidadMedida());
        assertTrue(frm.getListaTipoUnidadMedida().isEmpty());
    }

    // ==================== PRUEBAS DE CONVERSIÓN DE ID ====================

    @Test
    void getIdAsText_ConUnidadValida_DebeRetornarIdComoString() {
        // Act
        String resultado = frm.getIdAsText(unidadMedidaPrueba);

        // Assert
        assertNotNull(resultado);
        assertEquals("1", resultado);
    }

    @Test
    void getIdAsText_ConUnidadNula_DebeRetornarNull() {
        // Act
        String resultado = frm.getIdAsText(null);

        // Assert
        assertNull(resultado);
    }

    @Test
    void getIdAsText_ConUnidadSinId_DebeRetornarNull() {
        // Arrange
        UnidadMedida unidadSinId = new UnidadMedida();

        // Act
        String resultado = frm.getIdAsText(unidadSinId);

        // Assert
        assertNull(resultado);
    }

    // ==================== PRUEBAS DE getIdByText ====================

    @Test
    void getIdByText_ConIdNulo_DebeRetornarNull() {
        // Act
        UnidadMedida resultado = frm.getIdByText(null);

        // Assert
        assertNull(resultado);
    }

    @Test
    void getIdByText_ConModeloNulo_DebeRetornarNull() {
        // Act
        UnidadMedida resultado = frm.getIdByText("1");

        // Assert
        assertNull(resultado);
    }

    @Test
    void getIdByText_ConIdInvalido_DebeRetornarNull() {
        // Act
        UnidadMedida resultado = frm.getIdByText("abc");

        // Assert
        assertNull(resultado);
    }

    // ==================== PRUEBAS DE SELECCIÓN ====================

    @Test
    void seleccionarRegistro_ConEventoValido_DebeCambiarEstado() {
        // Arrange
        lenient().when(selectEvent.getObject()).thenReturn(unidadMedidaPrueba);

        // Act
        frm.seleccionarRegistro(selectEvent);

        // Assert
        assertEquals(ESTADO_CRUD.MODIFICAR, frm.getEstado());
        assertEquals(unidadMedidaPrueba, frm.getRegistro());
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

    // ==================== PRUEBAS DE getIdTipoUnidadMedidaSeleccionado
    // ====================

    @Test
    void getIdTipoUnidadMedidaSeleccionado_ConRegistroConTipo_DebeRetornarId() {
        // Arrange
        frm.registro = unidadMedidaPrueba;

        // Act
        Integer resultado = frm.getIdTipoUnidadMedidaSeleccionado();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado);
    }

    @Test
    void getIdTipoUnidadMedidaSeleccionado_ConRegistroNulo_DebeRetornarNull() {
        // Arrange
        frm.registro = null;

        // Act
        Integer resultado = frm.getIdTipoUnidadMedidaSeleccionado();

        // Assert
        assertNull(resultado);
    }

    @Test
    void getIdTipoUnidadMedidaSeleccionado_ConRegistroSinTipo_DebeRetornarNull() {
        // Arrange
        UnidadMedida unidadSinTipo = new UnidadMedida();
        frm.registro = unidadSinTipo;

        // Act
        Integer resultado = frm.getIdTipoUnidadMedidaSeleccionado();

        // Assert
        assertNull(resultado);
    }

    // ==================== PRUEBAS DE setIdTipoUnidadMedidaSeleccionado
    // ====================

    @Test
    void setIdTipoUnidadMedidaSeleccionado_ConIdValido_DebeAsignarTipo() {
        // Arrange
        frm.registro = unidadMedidaPrueba;
        frm.setListaTipoUnidadMedida(listaTipos);

        // Act
        frm.setIdTipoUnidadMedidaSeleccionado(2);

        // Assert
        assertNotNull(frm.registro.getIdTipoUnidadMedida());
        assertEquals(2, frm.registro.getIdTipoUnidadMedida().getId());
    }

    @Test
    void setIdTipoUnidadMedidaSeleccionado_ConIdNulo_NoDebeGenerarExcepcion() {
        // Arrange
        frm.registro = unidadMedidaPrueba;

        // Act & Assert
        assertDoesNotThrow(() -> frm.setIdTipoUnidadMedidaSeleccionado(null));
    }

    @Test
    void setIdTipoUnidadMedidaSeleccionado_ConRegistroNulo_NoDebeGenerarExcepcion() {
        // Arrange
        frm.registro = null;

        // Act & Assert
        assertDoesNotThrow(() -> frm.setIdTipoUnidadMedidaSeleccionado(1));
    }

    @Test
    void setIdTipoUnidadMedidaSeleccionado_ConListaNula_NoDebeGenerarExcepcion() {
        // Arrange
        frm.registro = unidadMedidaPrueba;
        frm.setListaTipoUnidadMedida(null);

        // Act & Assert
        assertDoesNotThrow(() -> frm.setIdTipoUnidadMedidaSeleccionado(1));
    }

    @Test
    void setIdTipoUnidadMedidaSeleccionado_ConIdInexistente_DebeAsignarNull() {
        // Arrange
        frm.registro = unidadMedidaPrueba;
        frm.setListaTipoUnidadMedida(listaTipos);

        // Act
        frm.setIdTipoUnidadMedidaSeleccionado(999);

        // Assert
        assertNull(frm.registro.getIdTipoUnidadMedida());
    }

    // ==================== PRUEBAS DE setTipoUnidadMedidaActual
    // ====================

    @Test
    void setTipoUnidadMedidaActual_DebeAsignarTipo() {
        // Arrange
        lenient().when(unidadMedidaDAO.countByTipoUnidadMedida(anyInt())).thenReturn(5);

        // Act
        frm.setTipoUnidadMedidaActual(tipoUnidadMedida);

        // Assert
        assertEquals(tipoUnidadMedida, frm.getTipoUnidadMedidaActual());
    }

    @Test
    void setTipoUnidadMedidaActual_DebeReiniciarEstado() {
        // Arrange
        frm.estado = ESTADO_CRUD.MODIFICAR;
        frm.registro = unidadMedidaPrueba;
        lenient().when(unidadMedidaDAO.countByTipoUnidadMedida(anyInt())).thenReturn(5);

        // Act
        frm.setTipoUnidadMedidaActual(tipoUnidadMedida);

        // Assert
        assertEquals(ESTADO_CRUD.NADA, frm.getEstado());
        assertNull(frm.getRegistro());
    }

    // ==================== PRUEBAS DE reiniciarEstado ====================

    @Test
    void reiniciarEstado_DebeResetearEstadoYRegistro() {
        // Arrange
        frm.estado = ESTADO_CRUD.MODIFICAR;
        frm.registro = unidadMedidaPrueba;
        lenient().when(unidadMedidaDAO.countByTipoUnidadMedida(anyInt())).thenReturn(5);

        // Act
        frm.reiniciarEstado();

        // Assert
        assertEquals(ESTADO_CRUD.NADA, frm.getEstado());
        assertNull(frm.getRegistro());
    }

    // ==================== PRUEBAS DE buscarRegistroPorId ====================

    @Test
    void buscarRegistroPorId_ConIdValido_DebeRetornarUnidad() {
        // Arrange
        when(unidadMedidaDAO.leer(1)).thenReturn(unidadMedidaPrueba);

        // Act
        UnidadMedida resultado = frm.buscarRegistroPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(unidadMedidaPrueba.getId(), resultado.getId());
    }

    @Test
    void buscarRegistroPorId_ConIdNulo_DebeRetornarNull() {
        // Act
        UnidadMedida resultado = frm.buscarRegistroPorId(null);

        // Assert
        assertNull(resultado);
        verify(unidadMedidaDAO, never()).leer(any());
    }

    @Test
    void buscarRegistroPorId_ConDAONulo_DebeRetornarNull() {
        // Arrange
        frm.unidadMedidaDAO = null;

        // Act
        UnidadMedida resultado = frm.buscarRegistroPorId(1);

        // Assert
        assertNull(resultado);
    }

    // ==================== PRUEBAS DE getEntityId ====================

    @Test
    void getEntityId_ConUnidadValida_DebeRetornarId() {
        // Act
        Object resultado = frm.getEntityId(unidadMedidaPrueba);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado);
    }

    @Test
    void getEntityId_ConUnidadNula_DebeRetornarNull() {
        // Act
        Object resultado = frm.getEntityId(null);

        // Assert
        assertNull(resultado);
    }

    // ==================== PRUEBAS DE getEntityName ====================

    @Test
    void getEntityName_DebeRetornarNombreBean() {
        // Act
        String resultado = frm.getEntityName();

        // Assert
        assertEquals("Unidad de Medida", resultado);
    }

    // ==================== PRUEBAS DE nuevoRegistro ====================

    @Test
    void nuevoRegistro_DebeRetornarNuevaEntidad() {
        // Act
        UnidadMedida resultado = frm.nuevoRegistro();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
    }

    // ==================== PRUEBAS DE getDao ====================

    @Test
    void getDao_DebeRetornarUnidadMedidaDAO() {
        // Act
        var resultado = frm.getDao();

        // Assert
        assertNotNull(resultado);
        assertEquals(unidadMedidaDAO, resultado);
    }

    // ==================== PRUEBAS DE getModelo ====================

    @Test
    void getModelo_DebeRetornarLazyDataModel() {
        // Act
        LazyDataModel<UnidadMedida> resultado = frm.getModelo();

        // Assert - Puede ser null si no se ha inicializado
        // Solo verificamos que no genera excepción
        assertDoesNotThrow(() -> frm.getModelo());
    }

    // ==================== PRUEBAS DE CONSTRUCTOR ====================

    @Test
    void constructor_DebeInicializarNombreBean() {
        // Arrange & Act
        UnidadMedidaFrm nuevoFrm = new UnidadMedidaFrm();

        // Assert
        assertNotNull(nuevoFrm.nombreBean);
        assertEquals("Unidad de Medida", nuevoFrm.nombreBean);
    }
}
