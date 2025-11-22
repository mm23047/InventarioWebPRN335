package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Producto;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProductoDAO
 * Cubre métodos específicos: buscarPorId (con UUID), findByNombreLike
 */
@ExtendWith(MockitoExtension.class)
class ProductoDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Producto> typedQuery;

    @InjectMocks
    private ProductoDAO dao;

    private Producto productoPrueba;
    private UUID uuidPrueba;

    @BeforeEach
    void setUp() {
        uuidPrueba = UUID.randomUUID();
        productoPrueba = new Producto();
        productoPrueba.setId(uuidPrueba);
        productoPrueba.setNombreProducto("Laptop Dell");
        productoPrueba.setReferenciaExterna("LAP-001");
        productoPrueba.setComentarios("Laptop Dell Inspiron 15");
        productoPrueba.setActivo(true);
    }

    // ==================== PRUEBAS DE buscarPorId ====================

    @Test
    void buscarPorId_ConUUIDValido_DebeRetornarProducto() {
        // Arrange
        when(em.find(Producto.class, uuidPrueba)).thenReturn(productoPrueba);

        // Act
        Producto resultado = dao.buscarPorId(uuidPrueba);

        // Assert
        assertNotNull(resultado);
        assertEquals(uuidPrueba, resultado.getId());
        assertEquals("Laptop Dell", resultado.getNombreProducto());
    }

    @Test
    void buscarPorId_ConUUIDNulo_DebeRetornarNull() {
        // Act
        Producto resultado = dao.buscarPorId(null);

        // Assert
        assertNull(resultado);
        verify(em, never()).find(any(), any());
    }

    @Test
    void buscarPorId_ConUUIDInexistente_DebeRetornarNull() {
        // Arrange
        UUID uuidInexistente = UUID.randomUUID();
        when(em.find(Producto.class, uuidInexistente)).thenReturn(null);

        // Act
        Producto resultado = dao.buscarPorId(uuidInexistente);

        // Assert
        assertNull(resultado);
    }

    @Test
    void buscarPorId_ConExcepcion_DebeRetornarNull() {
        // Arrange
        when(em.find(Producto.class, uuidPrueba))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Act
        Producto resultado = dao.buscarPorId(uuidPrueba);

        // Assert
        assertNull(resultado);
    }

    @Test
    void buscarPorId_DebeUsarEntityManagerFind() {
        // Arrange
        when(em.find(Producto.class, uuidPrueba)).thenReturn(productoPrueba);

        // Act
        dao.buscarPorId(uuidPrueba);

        // Assert
        verify(em).find(Producto.class, uuidPrueba);
    }

    // ==================== PRUEBAS DE findByNombreLike ====================

    @Test
    void findByNombreLike_ConNombreValido_DebeRetornarLista() {
        // Arrange
        List<Producto> productosEsperados = Arrays.asList(productoPrueba);
        when(em.createNamedQuery("Producto.findByNombreLike", Producto.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nombre"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(productosEsperados);

        // Act
        List<Producto> resultado = dao.findByNombreLike("Laptop", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Laptop Dell", resultado.get(0).getNombreProducto());
        verify(typedQuery).setParameter("nombre", "%LAPTOP%");
    }

    @Test
    void findByNombreLike_ConNombreConEspacios_DebeTrimear() {
        // Arrange
        when(em.createNamedQuery("Producto.findByNombreLike", Producto.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nombre"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(productoPrueba));

        // Act
        dao.findByNombreLike("  Laptop  ", 0, 10);

        // Assert
        verify(typedQuery).setParameter("nombre", "%LAPTOP%");
    }

    @Test
    void findByNombreLike_ConNombreMinusculas_DebeConvertirAMayusculas() {
        // Arrange
        when(em.createNamedQuery("Producto.findByNombreLike", Producto.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nombre"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(productoPrueba));

        // Act
        dao.findByNombreLike("laptop dell", 0, 10);

        // Assert
        verify(typedQuery).setParameter("nombre", "%LAPTOP DELL%");
    }

    @Test
    void findByNombreLike_ConNombreNulo_DebeRetornarListaVacia() {
        // Act
        List<Producto> resultado = dao.findByNombreLike(null, 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em, never()).createNamedQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConNombreVacio_DebeRetornarListaVacia() {
        // Act
        List<Producto> resultado = dao.findByNombreLike("", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em, never()).createNamedQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConNombreBlank_DebeRetornarListaVacia() {
        // Act
        List<Producto> resultado = dao.findByNombreLike("   ", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em, never()).createNamedQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConFirstNegativo_DebeRetornarListaVacia() {
        // Act
        List<Producto> resultado = dao.findByNombreLike("Laptop", -1, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em, never()).createNamedQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConFirstMayorQueMax_DebeRetornarListaVacia() {
        // Act
        List<Producto> resultado = dao.findByNombreLike("Laptop", 20, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em, never()).createNamedQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConFirstIgualAMax_DebePermitirBusqueda() {
        // Arrange - first == max es válido en la implementación real
        when(em.createNamedQuery("Producto.findByNombreLike", Producto.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nombre"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(productoPrueba));

        // Act
        List<Producto> resultado = dao.findByNombreLike("Laptop", 10, 10);

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("Producto.findByNombreLike", Producto.class);
    }

    @Test
    void findByNombreLike_ConExcepcion_DebeRetornarListaVacia() {
        // Arrange
        when(em.createNamedQuery("Producto.findByNombreLike", Producto.class))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Act
        List<Producto> resultado = dao.findByNombreLike("Laptop", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByNombreLike_ConPaginacion_DebeConfigurarCorrectamente() {
        // Arrange
        when(em.createNamedQuery("Producto.findByNombreLike", Producto.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nombre"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(productoPrueba));

        // Act
        dao.findByNombreLike("Laptop", 5, 15);

        // Assert
        verify(typedQuery).setFirstResult(5);
        verify(typedQuery).setMaxResults(15);
    }

    @Test
    void findByNombreLike_ConMultiplesResultados_DebeRetornarTodos() {
        // Arrange
        Producto producto2 = new Producto();
        producto2.setId(UUID.randomUUID());
        producto2.setNombreProducto("Laptop HP");
        producto2.setReferenciaExterna("LAP-002");

        Producto producto3 = new Producto();
        producto3.setId(UUID.randomUUID());
        producto3.setNombreProducto("Laptop Lenovo");
        producto3.setReferenciaExterna("LAP-003");

        List<Producto> productosEsperados = Arrays.asList(productoPrueba, producto2, producto3);
        when(em.createNamedQuery("Producto.findByNombreLike", Producto.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nombre"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(productosEsperados);

        // Act
        List<Producto> resultado = dao.findByNombreLike("Laptop", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals("Laptop Dell", resultado.get(0).getNombreProducto());
        assertEquals("Laptop HP", resultado.get(1).getNombreProducto());
        assertEquals("Laptop Lenovo", resultado.get(2).getNombreProducto());
    }

    @Test
    void findByNombreLike_ConBusquedaParcial_DebeEncontrarCoincidencias() {
        // Arrange
        when(em.createNamedQuery("Producto.findByNombreLike", Producto.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nombre"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(productoPrueba));

        // Act
        dao.findByNombreLike("Dell", 0, 10);

        // Assert
        verify(typedQuery).setParameter("nombre", "%DELL%");
    }

    @Test
    void findByNombreLike_ConCaracteresEspeciales_DebeProcessarCorrectamente() {
        // Arrange
        when(em.createNamedQuery("Producto.findByNombreLike", Producto.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nombre"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList());

        // Act
        dao.findByNombreLike("Laptop-2024", 0, 10);

        // Assert
        verify(typedQuery).setParameter("nombre", "%LAPTOP-2024%");
    }

    // ==================== PRUEBAS DE getEntityManager ====================

    @Test
    void getEntityManager_DebeRetornarEntityManager() {
        // Act
        EntityManager resultado = dao.getEntityManager();

        // Assert
        assertNotNull(resultado);
        assertEquals(em, resultado);
    }

    // ==================== PRUEBAS DE VALIDACIÓN DE PARÁMETROS ====================

    @Test
    void findByNombreLike_ConFirstCero_DebePermitirBusqueda() {
        // Arrange
        when(em.createNamedQuery("Producto.findByNombreLike", Producto.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nombre"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(productoPrueba));

        // Act
        List<Producto> resultado = dao.findByNombreLike("Laptop", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    void findByNombreLike_ConMaxUno_DebeLimitarResultados() {
        // Arrange
        when(em.createNamedQuery("Producto.findByNombreLike", Producto.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nombre"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(productoPrueba));

        // Act
        dao.findByNombreLike("Laptop", 0, 1);

        // Assert
        verify(typedQuery).setMaxResults(1);
    }
}
