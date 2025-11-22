package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Proveedor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProveedorDAO
 * Cubre métodos de búsqueda específicos: findByNombreLike, findByActivos,
 * buscarRegistroPorId
 */
@ExtendWith(MockitoExtension.class)
class ProveedorDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Proveedor> typedQuery;

    @InjectMocks
    private ProveedorDAO dao;

    private Proveedor proveedorPrueba;

    @BeforeEach
    void setUp() {
        proveedorPrueba = new Proveedor();
        proveedorPrueba.setId(1);
        proveedorPrueba.setNombre("Proveedor Test");
        proveedorPrueba.setRazonSocial("Proveedor Test S.A.");
        proveedorPrueba.setNit("0614-123456-001-1");
        proveedorPrueba.setActivo(true);
    }

    // ==================== PRUEBAS DE findByNombreLike ====================

    @Test
    void findByNombreLike_ConNombreValido_DebeRetornarLista() {
        // Arrange
        List<Proveedor> proveedoresEsperados = Arrays.asList(proveedorPrueba);
        when(em.createNamedQuery("Proveedor.findByNombreLike", Proveedor.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nombre"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(proveedoresEsperados);

        // Act
        List<Proveedor> resultado = dao.findByNombreLike("Test", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(proveedorPrueba.getNombre(), resultado.get(0).getNombre());
        verify(typedQuery).setParameter("nombre", "%TEST%");
    }

    @Test
    void findByNombreLike_ConNombreConEspacios_DebeTrimear() {
        // Arrange
        when(em.createNamedQuery("Proveedor.findByNombreLike", Proveedor.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nombre"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(proveedorPrueba));

        // Act
        dao.findByNombreLike("  Test  ", 0, 10);

        // Assert
        verify(typedQuery).setParameter("nombre", "%TEST%");
    }

    @Test
    void findByNombreLike_ConNombreNulo_DebeRetornarListaVacia() {
        // Act
        List<Proveedor> resultado = dao.findByNombreLike(null, 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em, never()).createNamedQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConNombreVacio_DebeRetornarListaVacia() {
        // Act
        List<Proveedor> resultado = dao.findByNombreLike("", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em, never()).createNamedQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConNombreBlank_DebeRetornarListaVacia() {
        // Act
        List<Proveedor> resultado = dao.findByNombreLike("   ", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em, never()).createNamedQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConFirstNegativo_DebeRetornarListaVacia() {
        // Act
        List<Proveedor> resultado = dao.findByNombreLike("Test", -1, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em, never()).createNamedQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConFirstMayorQueMax_DebeRetornarListaVacia() {
        // Act
        List<Proveedor> resultado = dao.findByNombreLike("Test", 20, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em, never()).createNamedQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConExcepcion_DebeRetornarListaVacia() {
        // Arrange
        when(em.createNamedQuery("Proveedor.findByNombreLike", Proveedor.class))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Act
        List<Proveedor> resultado = dao.findByNombreLike("Test", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByNombreLike_ConPaginacion_DebeConfigurarCorrectamente() {
        // Arrange
        when(em.createNamedQuery("Proveedor.findByNombreLike", Proveedor.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nombre"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(proveedorPrueba));

        // Act
        dao.findByNombreLike("Test", 5, 15);

        // Assert
        verify(typedQuery).setFirstResult(5);
        verify(typedQuery).setMaxResults(15);
    }

    // ==================== PRUEBAS DE findByActivos ====================

    @Test
    void findByActivos_DebeRetornarProveedoresActivos() {
        // Arrange
        List<Proveedor> proveedoresActivos = Arrays.asList(proveedorPrueba);
        when(em.createNamedQuery("Proveedor.findByActivos", Proveedor.class)).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(proveedoresActivos);

        // Act
        List<Proveedor> resultado = dao.findByActivos(0, 10);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getActivo());
    }

    @Test
    void findByActivos_ConPaginacion_DebeConfigurarCorrectamente() {
        // Arrange
        when(em.createNamedQuery("Proveedor.findByActivos", Proveedor.class)).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList());

        // Act
        dao.findByActivos(10, 20);

        // Assert
        verify(typedQuery).setFirstResult(10);
        verify(typedQuery).setMaxResults(20);
    }

    @Test
    void findByActivos_ConExcepcion_DebeRetornarListaVacia() {
        // Arrange
        when(em.createNamedQuery("Proveedor.findByActivos", Proveedor.class))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Act
        List<Proveedor> resultado = dao.findByActivos(0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ==================== PRUEBAS DE buscarRegistroPorId ====================

    @Test
    void buscarRegistroPorId_ConIdValido_DebeRetornarProveedor() {
        // Arrange
        when(em.find(Proveedor.class, 1)).thenReturn(proveedorPrueba);

        // Act
        Proveedor resultado = dao.buscarRegistroPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(proveedorPrueba.getId(), resultado.getId());
        assertEquals(proveedorPrueba.getNombre(), resultado.getNombre());
    }

    @Test
    void buscarRegistroPorId_ConIdNulo_DebeRetornarNull() {
        // Act
        Proveedor resultado = dao.buscarRegistroPorId(null);

        // Assert
        assertNull(resultado);
        verify(em, never()).find(any(), any());
    }

    @Test
    void buscarRegistroPorId_ConIdInexistente_DebeRetornarNull() {
        // Arrange
        when(em.find(Proveedor.class, 999)).thenReturn(null);

        // Act
        Proveedor resultado = dao.buscarRegistroPorId(999);

        // Assert
        assertNull(resultado);
    }

    @Test
    void buscarRegistroPorId_ConExcepcion_DebeRetornarNull() {
        // Arrange
        when(em.find(Proveedor.class, 1)).thenThrow(new RuntimeException("Error de base de datos"));

        // Act
        Proveedor resultado = dao.buscarRegistroPorId(1);

        // Assert
        assertNull(resultado);
    }

    // ==================== PRUEBAS DE leer ====================

    @Test
    void leer_ConIdValido_DebeRetornarProveedor() {
        // Arrange
        when(em.find(Proveedor.class, 1)).thenReturn(proveedorPrueba);

        // Act
        Proveedor resultado = dao.leer(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(proveedorPrueba.getId(), resultado.getId());
    }

    @Test
    void leer_ConIdNulo_DebeRetornarNull() {
        // Arrange
        when(em.find(Proveedor.class, null)).thenReturn(null);

        // Act
        Proveedor resultado = dao.leer(null);

        // Assert
        assertNull(resultado);
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
}
