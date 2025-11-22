package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoProducto;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para TipoProductoDAO
 * Cubre los métodos específicos incluyendo búsquedas personalizadas
 */
@ExtendWith(MockitoExtension.class)
class TipoProductoDAOTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<TipoProducto> typedQuery;

    @Mock
    private TypedQuery<TipoProducto> namedQuery;

    @InjectMocks
    private TipoProductoDAO dao;

    private TipoProducto tipoProducto1;
    private TipoProducto tipoProducto2;

    @BeforeEach
    void setUp() {
        dao.em = entityManager;

        // Crear entidades de prueba
        tipoProducto1 = new TipoProducto();
        tipoProducto1.setId(1L);
        tipoProducto1.setNombre("Electrónica");
        tipoProducto1.setActivo(true);

        tipoProducto2 = new TipoProducto();
        tipoProducto2.setId(2L);
        tipoProducto2.setNombre("Electrodomésticos");
        tipoProducto2.setActivo(true);
    }

    // ==================== PRUEBAS PARA buscarRegistroPorId ====================

    @Test
    void buscarRegistroPorId_ConIdValido_DebeRetornarEntidad() {
        // Arrange
        when(entityManager.find(TipoProducto.class, 1L)).thenReturn(tipoProducto1);

        // Act
        TipoProducto resultado = dao.buscarRegistroPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Electrónica", resultado.getNombre());
        verify(entityManager, times(1)).find(TipoProducto.class, 1L);
    }

    @Test
    void buscarRegistroPorId_ConIdInexistente_DebeRetornarNull() {
        // Arrange
        when(entityManager.find(TipoProducto.class, 999L)).thenReturn(null);

        // Act
        TipoProducto resultado = dao.buscarRegistroPorId(999L);

        // Assert
        assertNull(resultado);
        verify(entityManager, times(1)).find(TipoProducto.class, 999L);
    }

    @Test
    void buscarRegistroPorId_ConIdNulo_DebeRetornarNull() {
        // Act
        TipoProducto resultado = dao.buscarRegistroPorId(null);

        // Assert
        assertNull(resultado);
        verify(entityManager, never()).find(any(), any());
    }

    @Test
    void buscarRegistroPorId_ConExcepcion_DebeRetornarNull() {
        // Arrange
        when(entityManager.find(TipoProducto.class, 1L))
                .thenThrow(new RuntimeException("Error de BD"));

        // Act
        TipoProducto resultado = dao.buscarRegistroPorId(1L);

        // Assert
        assertNull(resultado);
    }

    // ==================== PRUEBAS PARA findByNombreLike ====================

    @Test
    void findByNombreLike_ConNombreValido_DebeRetornarLista() {
        // Arrange
        List<TipoProducto> esperados = Arrays.asList(tipoProducto1, tipoProducto2);
        when(entityManager.createNamedQuery("TipoProducto.findByNombreLike", TipoProducto.class))
                .thenReturn(namedQuery);
        when(namedQuery.setParameter(eq("nombre"), anyString())).thenReturn(namedQuery);
        when(namedQuery.setFirstResult(anyInt())).thenReturn(namedQuery);
        when(namedQuery.setMaxResults(anyInt())).thenReturn(namedQuery);
        when(namedQuery.getResultList()).thenReturn(esperados);

        // Act
        List<TipoProducto> resultado = dao.findByNombreLike("Electr", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(namedQuery).setParameter("nombre", "%ELECTR%");
        verify(namedQuery).setFirstResult(0);
        verify(namedQuery).setMaxResults(10);
    }

    @Test
    void findByNombreLike_ConNombreNulo_DebeRetornarListaVacia() {
        // Act
        List<TipoProducto> resultado = dao.findByNombreLike(null, 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(entityManager, never()).createNamedQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConNombreVacio_DebeRetornarListaVacia() {
        // Act
        List<TipoProducto> resultado = dao.findByNombreLike("   ", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(entityManager, never()).createNamedQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConFirstNegativo_DebeRetornarListaVacia() {
        // Act
        List<TipoProducto> resultado = dao.findByNombreLike("Electr", -1, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(entityManager, never()).createNamedQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConFirstMayorQueMax_DebeRetornarListaVacia() {
        // Act
        List<TipoProducto> resultado = dao.findByNombreLike("Electr", 20, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(entityManager, never()).createNamedQuery(anyString(), any());
    }

    @Test
    void findByNombreLike_ConExcepcion_DebeRetornarListaVacia() {
        // Arrange
        when(entityManager.createNamedQuery("TipoProducto.findByNombreLike", TipoProducto.class))
                .thenThrow(new RuntimeException("Error de BD"));

        // Act
        List<TipoProducto> resultado = dao.findByNombreLike("Electr", 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByNombreLike_DebeTrimearYConvertirAMayusculas() {
        // Arrange
        when(entityManager.createNamedQuery("TipoProducto.findByNombreLike", TipoProducto.class))
                .thenReturn(namedQuery);
        when(namedQuery.setParameter(eq("nombre"), anyString())).thenReturn(namedQuery);
        when(namedQuery.setFirstResult(anyInt())).thenReturn(namedQuery);
        when(namedQuery.setMaxResults(anyInt())).thenReturn(namedQuery);
        when(namedQuery.getResultList()).thenReturn(List.of());

        // Act
        dao.findByNombreLike("  electr  ", 0, 10);

        // Assert
        verify(namedQuery).setParameter("nombre", "%ELECTR%");
    }

    // ==================== PRUEBAS PARA findAll ====================

    @Test
    void findAll_DebeRetornarTodasLasEntidades() {
        // Arrange
        List<TipoProducto> esperados = Arrays.asList(tipoProducto1, tipoProducto2);
        when(entityManager.createQuery(anyString(), eq(TipoProducto.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(esperados);

        // Act
        List<TipoProducto> resultado = dao.findAll();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(entityManager).createQuery(
                "SELECT t FROM TipoProducto t ORDER BY t.nombre",
                TipoProducto.class);
    }

    @Test
    void findAll_ConExcepcion_DebeRetornarListaVacia() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(TipoProducto.class)))
                .thenThrow(new RuntimeException("Error de BD"));

        // Act
        List<TipoProducto> resultado = dao.findAll();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
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
        TipoProductoDAO nuevoDao = new TipoProductoDAO();

        // Assert
        assertNotNull(nuevoDao);
    }

    @Test
    void heredaDe_InventarioDefaultDataAccess() {
        // Assert
        assertTrue(dao instanceof InventarioDefaultDataAccess);
    }
}
