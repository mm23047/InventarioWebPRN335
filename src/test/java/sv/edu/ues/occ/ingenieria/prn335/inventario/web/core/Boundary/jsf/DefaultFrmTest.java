package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * Pruebas unitarias para DefaultFrm
 * Cubre la lógica de negocio del CRUD base
 */
@ExtendWith(MockitoExtension.class)
class DefaultFrmTest {

    @Mock
    private InventarioDAOInterface<EntidadPrueba> dao;

    @Mock
    private FacesContext facesContext;

    @Mock
    private Application application;

    @Mock
    private ResourceBundle resourceBundle;

    @Mock
    private UIComponent component;

    @Mock
    private SelectEvent<EntidadPrueba> selectEvent;

    private DefaultFrmImpl frm;
    private EntidadPrueba entidadPrueba;

    @BeforeEach
    void setUp() {
        frm = new DefaultFrmImpl(dao, facesContext);
        entidadPrueba = new EntidadPrueba(1L, "Prueba", true);

        // Configurar mocks básicos con lenient() para evitar
        // UnnecessaryStubbingException
        lenient().when(facesContext.getApplication()).thenReturn(application);
        lenient().when(application.getResourceBundle(any(), eq("crud"))).thenReturn(resourceBundle);
        lenient().when(resourceBundle.getString(anyString())).thenReturn("Mensaje de prueba");
    }

    // ==================== PRUEBAS DE INICIALIZACIÓN ====================

    @Test
    void inicializarRegistros_DebeCrearModelo() {
        // Arrange
        lenient().when(dao.count()).thenReturn(10);
        lenient().when(dao.findRange(anyInt(), anyInt())).thenReturn(Arrays.asList(entidadPrueba));

        // Act
        frm.inicializarRegistros();

        // Assert
        assertNotNull(frm.getModelo());
    }

    @Test
    void inicializarRegistros_ModeloDebeContarCorrectamente() {
        // Arrange
        when(dao.count()).thenReturn(15);

        // Act
        frm.inicializarRegistros();

        // Assert
        assertEquals(15, frm.getModelo().count(null));
        verify(dao, times(1)).count();
    }

    @Test
    void inicializarRegistros_ModeloDebeCargarDatos() {
        // Arrange
        List<EntidadPrueba> datos = Arrays.asList(
                new EntidadPrueba(1L, "Test1", true),
                new EntidadPrueba(2L, "Test2", true));
        when(dao.findRange(0, 5)).thenReturn(datos);

        // Act
        frm.inicializarRegistros();
        List<EntidadPrueba> resultado = frm.getModelo().load(0, 5, null, null);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(dao, times(1)).findRange(0, 5);
    }

    // ==================== PRUEBAS DE VALIDACIÓN ====================

    @Test
    void validarnombre_ConNombreNulo_DebeLanzarExcepcion() {
        // Act & Assert
        assertThrows(ValidatorException.class, () -> frm.validarnombre(facesContext, component, null));
    }

    @Test
    void validarnombre_ConNombreVacio_DebeLanzarExcepcion() {
        // Act & Assert
        assertThrows(ValidatorException.class, () -> frm.validarnombre(facesContext, component, ""));
    }

    @Test
    void validarnombre_ConNombreCorto_DebeLanzarExcepcion() {
        // Act & Assert
        assertThrows(ValidatorException.class, () -> frm.validarnombre(facesContext, component, "ab"));
    }

    @Test
    void validarnombre_ConNombreLargo_DebeLanzarExcepcion() {
        // Arrange
        String nombreLargo = "a".repeat(156);

        // Act & Assert
        assertThrows(ValidatorException.class, () -> frm.validarnombre(facesContext, component, nombreLargo));
    }

    @Test
    void validarnombre_ConNombreValido_NoDebeLanzarExcepcion() {
        // Act & Assert
        assertDoesNotThrow(() -> frm.validarnombre(facesContext, component, "Nombre Válido"));
    }

    @Test
    void validarnombre_ConEspacios_DebeValidarLongitudSinEspacios() {
        // Act & Assert
        assertDoesNotThrow(() -> frm.validarnombre(facesContext, component, "   Nombre   "));
    }

    // ==================== PRUEBAS DE SELECCIÓN ====================

    @Test
    void selectionHandler_ConEventoValido_DebeCambiarEstado() {
        // Arrange
        lenient().when(selectEvent.getObject()).thenReturn(entidadPrueba);

        // Act
        frm.selectionHandler(selectEvent);

        // Assert
        assertEquals(ESTADO_CRUD.MODIFICAR, frm.getEstado());
    }

    @Test
    void selectionHandler_ConEventoNulo_NoDebeCambiarEstado() {
        // Arrange
        ESTADO_CRUD estadoInicial = frm.getEstado();

        // Act
        frm.selectionHandler(null);

        // Assert
        assertEquals(estadoInicial, frm.getEstado());
    }

    @Test
    void seleccionarRegistro_ConEventoValido_DebeAsignarRegistroYEstado() {
        // Arrange
        when(selectEvent.getObject()).thenReturn(entidadPrueba);

        // Act
        frm.seleccionarRegistro(selectEvent);

        // Assert
        assertEquals(entidadPrueba, frm.getRegistro());
        assertEquals(ESTADO_CRUD.MODIFICAR, frm.getEstado());
    }

    // ==================== PRUEBAS DE ESTADO ====================

    @Test
    void limpiarFormulario_DebeResetearEstadoYRegistro() {
        // Arrange
        frm.setRegistro(entidadPrueba);
        frm.setEstado(ESTADO_CRUD.MODIFICAR);

        // Act
        frm.limpiarFormulario();

        // Assert
        assertNull(frm.getRegistro());
        assertEquals(ESTADO_CRUD.NADA, frm.getEstado());
    }

    @Test
    void getEstado_PorDefecto_DebeSerNADA() {
        // Act
        ESTADO_CRUD estado = frm.getEstado();

        // Assert
        assertEquals(ESTADO_CRUD.NADA, estado);
    }

    // ==================== PRUEBAS DE GETTERS Y SETTERS ====================

    @Test
    void setPageSize_DebeAsignarValor() {
        // Act
        frm.setPageSize(10);

        // Assert
        assertEquals(10, frm.getPageSize());
    }

    @Test
    void getRegistrosPorPagina_DebeRetornarPageSize() {
        // Arrange
        frm.setPageSize(15);

        // Act
        int resultado = frm.getRegistrosPorPagina();

        // Assert
        assertEquals(15, resultado);
    }

    @Test
    void getNombreBean_DebeRetornarNombreConfigurado() {
        // Act
        String nombre = frm.getNombreBean();

        // Assert
        assertEquals("Entidad de Prueba", nombre);
    }

    // ==================== CLASE DE PRUEBA ====================

    /**
     * Implementación concreta de DefaultFrm para pruebas
     */
    static class DefaultFrmImpl extends DefaultFrm<EntidadPrueba> {
        private final InventarioDAOInterface<EntidadPrueba> dao;
        private final FacesContext facesContext;

        public DefaultFrmImpl(InventarioDAOInterface<EntidadPrueba> dao, FacesContext facesContext) {
            this.dao = dao;
            this.facesContext = facesContext;
            this.nombreBean = "Entidad de Prueba";
        }

        @Override
        protected FacesContext getFacesContext() {
            return facesContext;
        }

        @Override
        protected InventarioDAOInterface<EntidadPrueba> getDao() {
            return dao;
        }

        @Override
        protected EntidadPrueba nuevoRegistro() {
            return new EntidadPrueba(null, "", true);
        }

        @Override
        protected EntidadPrueba buscarRegistroPorId(Object id) {
            return dao.leer(id);
        }

        @Override
        protected String getIdAsText(EntidadPrueba r) {
            return r != null && r.getId() != null ? r.getId().toString() : null;
        }

        @Override
        protected EntidadPrueba getIdByText(String id) {
            return id != null ? dao.leer(Long.parseLong(id)) : null;
        }

        @Override
        protected EntidadPrueba createNewEntity() {
            return new EntidadPrueba(null, "", true);
        }

        @Override
        protected Object getEntityId(EntidadPrueba entity) {
            return entity != null ? entity.getId() : null;
        }

        @Override
        protected String getEntityName() {
            return nombreBean;
        }
    }

    /**
     * Entidad simple para pruebas
     */
    static class EntidadPrueba {
        private Long id;
        private String nombre;
        private Boolean activo;

        public EntidadPrueba(Long id, String nombre, Boolean activo) {
            this.id = id;
            this.nombre = nombre;
            this.activo = activo;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public Boolean getActivo() {
            return activo;
        }

        public void setActivo(Boolean activo) {
            this.activo = activo;
        }
    }
}
