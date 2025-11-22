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
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ClienteDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Cliente;

import java.util.ResourceBundle;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * Pruebas unitarias para ClienteFrm
 * Cubre manejo de UUID y lógica específica de clientes
 */
@ExtendWith(MockitoExtension.class)
class ClienteFrmTest {

    @Mock
    private ClienteDAO clienteDAO;

    @Mock
    private FacesContext facesContext;

    @Mock
    private Application application;

    @Mock
    private ResourceBundle resourceBundle;

    @InjectMocks
    private ClienteFrm frm;

    private Cliente cliente;
    private UUID uuid;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();

        cliente = new Cliente();
        cliente.setId(uuid);
        cliente.setNombre("Juan Pérez");
        cliente.setDui("123456789");
        cliente.setNit("12345678901234");
        cliente.setActivo(true);

        // Configurar mocks con lenient() para evitar UnnecessaryStubbingException
        lenient().when(facesContext.getApplication()).thenReturn(application);
        lenient().when(application.getResourceBundle(any(), eq("crud"))).thenReturn(resourceBundle);
        lenient().when(resourceBundle.getString(anyString())).thenReturn("Mensaje de prueba");
    }

    // ==================== PRUEBAS DE CONSTRUCTOR ====================

    @Test
    void constructor_DebeAsignarNombreBean() {
        // Act
        ClienteFrm nuevoFrm = new ClienteFrm();

        // Assert
        assertEquals("Cliente", nuevoFrm.getNombreBean());
    }

    // ==================== PRUEBAS DE CONVERSIÓN ID ====================

    @Test
    void getIdAsText_ConClienteValido_DebeRetornarUUIDComoString() {
        // Act
        String resultado = frm.getIdAsText(cliente);

        // Assert
        assertNotNull(resultado);
        assertEquals(uuid.toString(), resultado);
    }

    @Test
    void getIdAsText_ConClienteNulo_DebeRetornarNull() {
        // Act
        String resultado = frm.getIdAsText(null);

        // Assert
        assertNull(resultado);
    }

    @Test
    void getIdAsText_ConClienteSinId_DebeRetornarNull() {
        // Arrange
        Cliente clienteSinId = new Cliente();

        // Act
        String resultado = frm.getIdAsText(clienteSinId);

        // Assert
        assertNull(resultado);
    }

    @Test
    void getIdByText_ConUUIDValido_DebeRetornarCliente() {
        // Arrange
        when(clienteDAO.leer(uuid)).thenReturn(cliente);

        // Act
        Cliente resultado = frm.getIdByText(uuid.toString());

        // Assert
        assertNotNull(resultado);
        assertEquals(uuid, resultado.getId());
        verify(clienteDAO, times(1)).leer(uuid);
    }

    @Test
    void getIdByText_ConIdNulo_DebeRetornarNull() {
        // Act
        Cliente resultado = frm.getIdByText(null);

        // Assert
        assertNull(resultado);
        verify(clienteDAO, never()).leer(any());
    }

    @Test
    void getIdByText_ConUUIDInvalido_DebeRetornarNull() {
        // Act
        Cliente resultado = frm.getIdByText("uuid-invalido");

        // Assert
        assertNull(resultado);
    }

    // ==================== PRUEBAS DE CREACIÓN DE ENTIDAD ====================

    @Test
    void nuevoRegistro_DebeCrearClienteConUUID() {
        // Act
        Cliente nuevo = frm.nuevoRegistro();

        // Assert
        assertNotNull(nuevo);
        assertNotNull(nuevo.getId());
    }

    @Test
    void createNewEntity_DebeInicializarCampos() {
        // Act
        Cliente nuevo = frm.createNewEntity();

        // Assert
        assertAll("nuevo cliente",
                () -> assertNotNull(nuevo.getId()),
                () -> assertTrue(nuevo.getActivo()),
                () -> assertEquals("", nuevo.getNombre()),
                () -> assertEquals("", nuevo.getDui()),
                () -> assertEquals("", nuevo.getNit()));
    }

    @Test
    void createNewEntity_DebeGenerarUUIDUnico() {
        // Act
        Cliente cliente1 = frm.createNewEntity();
        Cliente cliente2 = frm.createNewEntity();

        // Assert
        assertNotEquals(cliente1.getId(), cliente2.getId());
    }

    // ==================== PRUEBAS DE BÚSQUEDA ====================

    @Test
    void buscarRegistroPorId_ConIdValido_DebeRetornarCliente() {
        // Arrange
        when(clienteDAO.leer(uuid)).thenReturn(cliente);

        // Act
        Cliente resultado = frm.buscarRegistroPorId(uuid);

        // Assert
        assertNotNull(resultado);
        assertEquals(uuid, resultado.getId());
        verify(clienteDAO, times(1)).leer(uuid);
    }

    @Test
    void buscarRegistroPorId_ConIdNulo_DebeRetornarNull() {
        // Act
        Cliente resultado = frm.buscarRegistroPorId(null);

        // Assert
        assertNull(resultado);
        verify(clienteDAO, never()).leer(any());
    }

    @Test
    void buscarRegistroPorId_ConDAONulo_DebeRetornarNull() {
        // Arrange
        frm.clienteDAO = null;

        // Act
        Cliente resultado = frm.buscarRegistroPorId(uuid);

        // Assert
        assertNull(resultado);
    }

    // ==================== PRUEBAS DE SELECCIÓN ====================

    @Test
    void seleccionarRegistro_ConEventoValido_DebeAsignarRegistroYEstado() {
        // Arrange
        SelectEvent<Cliente> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(cliente);

        // Act
        frm.seleccionarRegistro(event);

        // Assert
        assertEquals(cliente, frm.getRegistro());
        assertEquals(ESTADO_CRUD.MODIFICAR, frm.getEstado());
    }

    @Test
    void seleccionarRegistro_ConEventoNulo_NoDebeModificarEstado() {
        // Arrange
        ESTADO_CRUD estadoInicial = frm.getEstado();

        // Act
        frm.seleccionarRegistro(null);

        // Assert
        assertEquals(estadoInicial, frm.getEstado());
    }

    @Test
    void seleccionarRegistro_ConObjetoNulo_NoDebeAsignarRegistro() {
        // Arrange
        SelectEvent<Cliente> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(null);

        // Act
        frm.seleccionarRegistro(event);

        // Assert
        assertNull(frm.getRegistro());
    }

    // ==================== PRUEBAS DE ENTITY ID ====================

    @Test
    void getEntityId_ConClienteValido_DebeRetornarUUID() {
        // Act
        Object resultado = frm.getEntityId(cliente);

        // Assert
        assertNotNull(resultado);
        assertEquals(uuid, resultado);
    }

    @Test
    void getEntityId_ConClienteNulo_DebeRetornarNull() {
        // Act
        Object resultado = frm.getEntityId(null);

        // Assert
        assertNull(resultado);
    }

    // ==================== PRUEBAS DE GETTERS ====================

    @Test
    void getDao_DebeRetornarClienteDAO() {
        // Act
        var dao = frm.getDao();

        // Assert
        assertNotNull(dao);
        assertEquals(clienteDAO, dao);
    }

    @Test
    void getEntityName_DebeRetornarNombreBean() {
        // Act
        String nombre = frm.getEntityName();

        // Assert
        assertEquals("Cliente", nombre);
    }

    // Test removido: getFacesContext requiere ambiente JSF completo que no está
    // disponible en pruebas unitarias

    // ==================== PRUEBAS DE INTEGRACIÓN ====================

    @Test
    void cicloCompleto_CrearYSeleccionar() {
        // Arrange - Crear nuevo cliente
        Cliente nuevo = frm.createNewEntity();

        // Act - Seleccionar el cliente creado
        SelectEvent<Cliente> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(nuevo);
        frm.seleccionarRegistro(event);

        // Assert
        assertEquals(nuevo, frm.getRegistro());
        assertEquals(ESTADO_CRUD.MODIFICAR, frm.getEstado());
        assertNotNull(frm.getEntityId(nuevo));
    }

    @Test
    void validacionDatos_ClienteCompleto() {
        // Arrange & Act
        Cliente nuevo = frm.createNewEntity();
        nuevo.setNombre("María González");
        nuevo.setDui("987654321");
        nuevo.setNit("98765432109876");

        // Assert
        assertAll("cliente completo",
                () -> assertNotNull(nuevo.getId()),
                () -> assertTrue(nuevo.getActivo()),
                () -> assertEquals("María González", nuevo.getNombre()),
                () -> assertEquals("987654321", nuevo.getDui()),
                () -> assertEquals("98765432109876", nuevo.getNit()));
    }
}
