package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Cliente;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ClienteDAO
 * Cubre búsquedas por UUID y funcionalidad de autocompletado
 */
@ExtendWith(MockitoExtension.class)
class ClienteDAOTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Cliente> typedQuery;

    @InjectMocks
    private ClienteDAO dao;

    private Cliente cliente1;
    private Cliente cliente2;
    private UUID uuid1;
    private UUID uuid2;

    @BeforeEach
    void setUp() {
        dao.em = entityManager;

        // Crear UUIDs de prueba
        uuid1 = UUID.randomUUID();
        uuid2 = UUID.randomUUID();

        // Crear entidades de prueba
        cliente1 = new Cliente();
        cliente1.setId(uuid1);
        cliente1.setNombre("Juan Pérez");
        cliente1.setDui("12345678-9");

        cliente2 = new Cliente();
        cliente2.setId(uuid2);
        cliente2.setNombre("Juan González");
        cliente2.setDui("98765432-1");
    }

    // ==================== PRUEBAS PARA buscarPorId ====================

    @Test
    void buscarPorId_ConIdValido_DebeRetornarEntidad() {
        // Arrange
        when(entityManager.find(Cliente.class, uuid1)).thenReturn(cliente1);

        // Act
        Cliente resultado = dao.buscarPorId(uuid1);

        // Assert
        assertNotNull(resultado);
        assertEquals(uuid1, resultado.getId());
        assertEquals("Juan Pérez", resultado.getNombre());
        verify(entityManager, times(1)).find(Cliente.class, uuid1);
    }

    @Test
    void buscarPorId_ConIdInexistente_DebeRetornarNull() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(entityManager.find(Cliente.class, idInexistente)).thenReturn(null);

        // Act
        Cliente resultado = dao.buscarPorId(idInexistente);

        // Assert
        assertNull(resultado);
        verify(entityManager, times(1)).find(Cliente.class, idInexistente);
    }

    @Test
    void buscarPorId_ConIdNulo_DebeRetornarNull() {
        // Act
        Cliente resultado = dao.buscarPorId(null);

        // Assert
        assertNull(resultado);
        verify(entityManager, never()).find(any(), any());
    }

    @Test
    void buscarPorId_ConExcepcion_DebeRetornarNull() {
        // Arrange
        when(entityManager.find(Cliente.class, uuid1))
                .thenThrow(new RuntimeException("Error de BD"));

        // Act
        Cliente resultado = dao.buscarPorId(uuid1);

        // Assert
        assertNull(resultado);
    }

    // ==================== PRUEBAS PARA findByNombreLike ====================

    @Test
    void findByNombreLike_ConNombreValido_DebeRetornarLista() {
        // Arrange
        List<Cliente> esperados = Arrays.asList(cliente1, cliente2);
        when(entityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nombre"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(esperados);

        // Act
        List<Cliente> resultado = dao.findByNombreLike("Juan", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(typedQuery).setParameter("nombre", "%JUAN%");
        verify(typedQuery).setFirstResult(0);
        verify(typedQuery).setMaxResults(10);
    }

    @Test
    void findByNombreLike_ConNombreNulo_DebeRetornarListaVacia() {
        // Act
        List<Cliente> resultado = dao.findByNombreLike(null, 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(entityManager, never()).createQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConNombreVacio_DebeRetornarListaVacia() {
        // Act
        List<Cliente> resultado = dao.findByNombreLike("   ", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(entityManager, never()).createQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConFirstNegativo_DebeRetornarListaVacia() {
        // Act
        List<Cliente> resultado = dao.findByNombreLike("Juan", -1, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(entityManager, never()).createQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConMaxCero_DebeRetornarListaVacia() {
        // Act
        List<Cliente> resultado = dao.findByNombreLike("Juan", 0, 0);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(entityManager, never()).createQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConMaxNegativo_DebeRetornarListaVacia() {
        // Act
        List<Cliente> resultado = dao.findByNombreLike("Juan", 0, -1);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(entityManager, never()).createQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConExcepcion_DebeRetornarListaVacia() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Cliente.class)))
                .thenThrow(new RuntimeException("Error de BD"));

        // Act
        List<Cliente> resultado = dao.findByNombreLike("Juan", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByNombreLike_DebeTrimearYConvertirAMayusculas() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nombre"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        // Act
        dao.findByNombreLike("  juan  ", 0, 10);

        // Assert
        verify(typedQuery).setParameter("nombre", "%JUAN%");
    }

    @Test
    void findByNombreLike_DebeOrdenarPorNombreAscendente() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        // Act
        dao.findByNombreLike("Juan", 0, 10);

        // Assert
        verify(entityManager).createQuery(
                "SELECT c FROM Cliente c WHERE UPPER(c.nombre) LIKE :nombre ORDER BY c.nombre ASC",
                Cliente.class);
    }

    // ==================== PRUEBAS GENERALES ====================

    @Test
    void getEntityManager_DebeRetornarEntityManagerConfigurado() {
        // Act
        EntityManager resultado = dao.getEntityManager();

        // Assert
        assertNotNull(resultado);
        assertEquals(entityManager, resultado);
    }

    @Test
    void constructor_DebeInicializarCorrectamente() {
        // Act
        ClienteDAO nuevoDao = new ClienteDAO();

        // Assert
        assertNotNull(nuevoDao);
    }

    @Test
    void heredaDe_InventarioDefaultDataAccess() {
        // Assert
        assertTrue(dao instanceof InventarioDefaultDataAccess);
    }
}
