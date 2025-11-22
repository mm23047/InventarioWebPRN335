package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoUnidadMedida;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.UnidadMedida;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para UnidadMedidaDAO
 * Cubre métodos específicos: countByTipoUnidadMedida, findByTipoUnidadMedida
 */
@ExtendWith(MockitoExtension.class)
class UnidadMedidaDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<UnidadMedida> typedQuery;

    @Mock
    private TypedQuery<Long> longTypedQuery;

    @InjectMocks
    private UnidadMedidaDAO dao;

    private UnidadMedida unidadMedidaPrueba;
    private TipoUnidadMedida tipoUnidadMedida;

    @BeforeEach
    void setUp() {
        tipoUnidadMedida = new TipoUnidadMedida();
        tipoUnidadMedida.setId(1);
        tipoUnidadMedida.setNombre("Longitud");
        tipoUnidadMedida.setActivo(true);

        unidadMedidaPrueba = new UnidadMedida();
        unidadMedidaPrueba.setId(1);
        unidadMedidaPrueba.setExpresionRegular("^\\d+(\\.\\d+)?\\s*m$");
        unidadMedidaPrueba.setComentarios("Metro - unidad de longitud");
        unidadMedidaPrueba.setIdTipoUnidadMedida(tipoUnidadMedida);
        unidadMedidaPrueba.setActivo(true);
    }

    // ==================== PRUEBAS DE countByTipoUnidadMedida ====================

    @Test
    void countByTipoUnidadMedida_ConIdValido_DebeRetornarCantidad() {
        // Arrange
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.setParameter(eq("idTipo"), anyInt())).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(5L);

        // Act
        int resultado = dao.countByTipoUnidadMedida(1);

        // Assert
        assertEquals(5, resultado);
        verify(longTypedQuery).setParameter("idTipo", 1);
    }

    @Test
    void countByTipoUnidadMedida_ConIdNulo_DebeRetornarCero() {
        // Act
        int resultado = dao.countByTipoUnidadMedida(null);

        // Assert
        assertEquals(0, resultado);
        verify(em, never()).createQuery(anyString(), any());
    }

    @Test
    void countByTipoUnidadMedida_ConExcepcion_DebeRetornarCero() {
        // Arrange
        when(em.createQuery(anyString(), eq(Long.class)))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Act
        int resultado = dao.countByTipoUnidadMedida(1);

        // Assert
        assertEquals(0, resultado);
    }

    @Test
    void countByTipoUnidadMedida_ConIdSinRegistros_DebeRetornarCero() {
        // Arrange
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.setParameter(eq("idTipo"), anyInt())).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(0L);

        // Act
        int resultado = dao.countByTipoUnidadMedida(999);

        // Assert
        assertEquals(0, resultado);
    }

    @Test
    void countByTipoUnidadMedida_ConMultiplesRegistros_DebeRetornarTotal() {
        // Arrange
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.setParameter(eq("idTipo"), anyInt())).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(100L);

        // Act
        int resultado = dao.countByTipoUnidadMedida(1);

        // Assert
        assertEquals(100, resultado);
    }

    // ==================== PRUEBAS DE findByTipoUnidadMedida ====================

    @Test
    void findByTipoUnidadMedida_ConIdValido_DebeRetornarLista() {
        // Arrange
        List<UnidadMedida> unidadesEsperadas = Arrays.asList(unidadMedidaPrueba);
        when(em.createQuery(anyString(), eq(UnidadMedida.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("idTipo"), anyInt())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(unidadesEsperadas);

        // Act
        List<UnidadMedida> resultado = dao.findByTipoUnidadMedida(1, 0, 10);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(unidadMedidaPrueba.getComentarios(), resultado.get(0).getComentarios());
        verify(typedQuery).setParameter("idTipo", 1);
    }

    @Test
    void findByTipoUnidadMedida_ConIdNulo_DebeRetornarListaVacia() {
        // Act
        List<UnidadMedida> resultado = dao.findByTipoUnidadMedida(null, 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em, never()).createQuery(anyString(), any());
    }

    @Test
    void findByTipoUnidadMedida_ConPaginacion_DebeConfigurarCorrectamente() {
        // Arrange
        when(em.createQuery(anyString(), eq(UnidadMedida.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("idTipo"), anyInt())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList());

        // Act
        dao.findByTipoUnidadMedida(1, 5, 15);

        // Assert
        verify(typedQuery).setFirstResult(5);
        verify(typedQuery).setMaxResults(15);
    }

    @Test
    void findByTipoUnidadMedida_ConExcepcion_DebeRetornarListaVacia() {
        // Arrange
        when(em.createQuery(anyString(), eq(UnidadMedida.class)))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Act
        List<UnidadMedida> resultado = dao.findByTipoUnidadMedida(1, 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByTipoUnidadMedida_ConIdSinRegistros_DebeRetornarListaVacia() {
        // Arrange
        when(em.createQuery(anyString(), eq(UnidadMedida.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("idTipo"), anyInt())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        // Act
        List<UnidadMedida> resultado = dao.findByTipoUnidadMedida(999, 0, 10);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByTipoUnidadMedida_ConMultiplesResultados_DebeRetornarTodos() {
        // Arrange
        UnidadMedida unidad2 = new UnidadMedida();
        unidad2.setId(2);
        unidad2.setComentarios("Centímetro - unidad de longitud");
        unidad2.setExpresionRegular("^\\d+(\\.\\d+)?\\s*cm$");
        unidad2.setIdTipoUnidadMedida(tipoUnidadMedida);

        UnidadMedida unidad3 = new UnidadMedida();
        unidad3.setId(3);
        unidad3.setComentarios("Kilómetro - unidad de longitud");
        unidad3.setExpresionRegular("^\\d+(\\.\\d+)?\\s*km$");
        unidad3.setIdTipoUnidadMedida(tipoUnidadMedida);

        List<UnidadMedida> unidadesEsperadas = Arrays.asList(unidadMedidaPrueba, unidad2, unidad3);
        when(em.createQuery(anyString(), eq(UnidadMedida.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("idTipo"), anyInt())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(unidadesEsperadas);

        // Act
        List<UnidadMedida> resultado = dao.findByTipoUnidadMedida(1, 0, 10);

        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertTrue(resultado.get(0).getComentarios().contains("Metro"));
        assertTrue(resultado.get(1).getComentarios().contains("Centímetro"));
        assertTrue(resultado.get(2).getComentarios().contains("Kilómetro"));
    }

    @Test
    void findByTipoUnidadMedida_ConPaginacionLimitada_DebeRespetarMax() {
        // Arrange
        when(em.createQuery(anyString(), eq(UnidadMedida.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("idTipo"), anyInt())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(unidadMedidaPrueba));

        // Act
        dao.findByTipoUnidadMedida(1, 0, 1);

        // Assert
        verify(typedQuery).setMaxResults(1);
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

    // ==================== PRUEBAS DE INTEGRACIÓN ====================

    @Test
    void findByTipoUnidadMedida_DebeConstruirQueryCorrectamente() {
        // Arrange
        when(em.createQuery(anyString(), eq(UnidadMedida.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("idTipo"), anyInt())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList());

        // Act
        dao.findByTipoUnidadMedida(1, 0, 10);

        // Assert
        verify(em).createQuery(
                eq("SELECT u FROM UnidadMedida u WHERE u.idTipoUnidadMedida.id = :idTipo ORDER BY u.id"),
                eq(UnidadMedida.class));
    }

    @Test
    void countByTipoUnidadMedida_DebeConstruirQueryCorrectamente() {
        // Arrange
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.setParameter(eq("idTipo"), anyInt())).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(0L);

        // Act
        dao.countByTipoUnidadMedida(1);

        // Assert
        verify(em).createQuery(
                eq("SELECT COUNT(u) FROM UnidadMedida u WHERE u.idTipoUnidadMedida.id = :idTipo"),
                eq(Long.class));
    }
}
