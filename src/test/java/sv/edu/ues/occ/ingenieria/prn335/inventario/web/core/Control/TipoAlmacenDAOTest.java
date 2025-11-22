package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoAlmacen;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TipoAlmacenDAOTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private TipoAlmacenDAO tipoAlmacenDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ==================== TESTS BÁSICOS HEREDADOS ====================

    @Test
    void testCrear_EntidadValida() {
        TipoAlmacen tipoAlmacen = new TipoAlmacen();
        tipoAlmacen.setNombre("Almacén General");
        tipoAlmacen.setActivo(true);

        tipoAlmacenDAO.crear(tipoAlmacen);

        verify(em, times(1)).persist(tipoAlmacen);
    }

    @Test
    void testCrear_EntidadNula() {
        assertThrows(IllegalArgumentException.class, () -> {
            tipoAlmacenDAO.crear(null);
        });
        verify(em, never()).persist(any());
    }

    @Test
    void testActualizar_EntidadValida() {
        TipoAlmacen tipoAlmacen = new TipoAlmacen();
        tipoAlmacen.setId(1);
        tipoAlmacen.setNombre("Almacén Actualizado");

        TipoAlmacen merged = new TipoAlmacen();
        when(em.merge(tipoAlmacen)).thenReturn(merged);

        TipoAlmacen resultado = tipoAlmacenDAO.actualizar(tipoAlmacen);

        assertNotNull(resultado);
        verify(em, times(1)).merge(tipoAlmacen);
    }

    @Test
    void testActualizar_EntidadNula() {
        assertThrows(IllegalArgumentException.class, () -> {
            tipoAlmacenDAO.actualizar(null);
        });
        verify(em, never()).merge(any());
    }

    @Test
    void testEliminar_EntidadValida() {
        TipoAlmacen tipoAlmacen = new TipoAlmacen();
        tipoAlmacen.setId(1);

        when(em.contains(tipoAlmacen)).thenReturn(false);
        when(em.merge(tipoAlmacen)).thenReturn(tipoAlmacen);

        tipoAlmacenDAO.eliminar(tipoAlmacen);

        verify(em, times(1)).merge(tipoAlmacen);
        verify(em, times(1)).remove(tipoAlmacen);
    }

    @Test
    void testEliminar_EntidadNula() {
        assertThrows(IllegalArgumentException.class, () -> {
            tipoAlmacenDAO.eliminar(null);
        });

        verify(em, never()).remove(any());
    }

    @Test
    void testLeer_IdValido() {
        TipoAlmacen tipoAlmacen = new TipoAlmacen();
        tipoAlmacen.setId(1);
        tipoAlmacen.setNombre("Almacén Refrigerado");

        when(em.find(TipoAlmacen.class, 1)).thenReturn(tipoAlmacen);

        TipoAlmacen resultado = tipoAlmacenDAO.leer(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Almacén Refrigerado", resultado.getNombre());
        verify(em, times(1)).find(TipoAlmacen.class, 1);
    }

    @Test
    void testLeer_IdNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            tipoAlmacenDAO.leer(null);
        });
        verify(em, never()).find(any(), any());
    }

    @Test
    void testCount_ConRegistros() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Long> cq = mock(CriteriaQuery.class);
        Root<TipoAlmacen> root = mock(Root.class);
        TypedQuery<Long> query = mock(TypedQuery.class);

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cq);
        when(cq.from(TipoAlmacen.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(cq.select(any())).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(5L);

        int resultado = tipoAlmacenDAO.count();

        assertEquals(5, resultado);
    }

    @Test
    void testCount_SinRegistros() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Long> cq = mock(CriteriaQuery.class);
        Root<TipoAlmacen> root = mock(Root.class);
        TypedQuery<Long> query = mock(TypedQuery.class);

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cq);
        when(cq.from(TipoAlmacen.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(cq.select(any())).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(0L);

        int resultado = tipoAlmacenDAO.count();

        assertEquals(0, resultado);
    }

    @Test
    void testFindRange_RangoValido() {
        TipoAlmacen tipo1 = new TipoAlmacen();
        tipo1.setId(1);
        TipoAlmacen tipo2 = new TipoAlmacen();
        tipo2.setId(2);

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<TipoAlmacen> cq = mock(CriteriaQuery.class);
        Root<TipoAlmacen> root = mock(Root.class);
        TypedQuery<TipoAlmacen> query = mock(TypedQuery.class);

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(TipoAlmacen.class)).thenReturn(cq);
        when(cq.from(TipoAlmacen.class)).thenReturn(root);
        when(cq.select(root)).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(tipo1, tipo2));

        List<TipoAlmacen> resultado = tipoAlmacenDAO.findRange(0, 10);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(10);
    }

    @Test
    void testFindRange_RangoVacio() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<TipoAlmacen> cq = mock(CriteriaQuery.class);
        Root<TipoAlmacen> root = mock(Root.class);
        TypedQuery<TipoAlmacen> query = mock(TypedQuery.class);

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(TipoAlmacen.class)).thenReturn(cq);
        when(cq.from(TipoAlmacen.class)).thenReturn(root);
        when(cq.select(root)).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<TipoAlmacen> resultado = tipoAlmacenDAO.findRange(0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ==================== TESTS ESPECÍFICOS ====================

    @Test
    void testBuscarRegistroPorId_IdValido() {
        TipoAlmacen tipoAlmacen = new TipoAlmacen();
        tipoAlmacen.setId(1);
        tipoAlmacen.setNombre("Almacén Seco");
        tipoAlmacen.setActivo(true);

        when(em.find(TipoAlmacen.class, 1)).thenReturn(tipoAlmacen);

        TipoAlmacen resultado = tipoAlmacenDAO.buscarRegistroPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Almacén Seco", resultado.getNombre());
        assertTrue(resultado.getActivo());
        verify(em, times(1)).find(TipoAlmacen.class, 1);
    }

    @Test
    void testBuscarRegistroPorId_IdNulo() {
        TipoAlmacen resultado = tipoAlmacenDAO.buscarRegistroPorId(null);

        assertNull(resultado);
        verify(em, never()).find(any(), any());
    }

    @Test
    void testBuscarRegistroPorId_IdNoExistente() {
        when(em.find(TipoAlmacen.class, 999)).thenReturn(null);

        TipoAlmacen resultado = tipoAlmacenDAO.buscarRegistroPorId(999);

        assertNull(resultado);
        verify(em, times(1)).find(TipoAlmacen.class, 999);
    }

    @Test
    void testBuscarRegistroPorId_ExcepcionEnBusqueda() {
        when(em.find(TipoAlmacen.class, 1)).thenThrow(new RuntimeException("Error de base de datos"));

        TipoAlmacen resultado = tipoAlmacenDAO.buscarRegistroPorId(1);

        assertNull(resultado);
        verify(em, times(1)).find(TipoAlmacen.class, 1);
    }

    @Test
    void testGetEntityManager() {
        EntityManager resultado = tipoAlmacenDAO.getEntityManager();
        assertNotNull(resultado);
        assertEquals(em, resultado);
    }
}
