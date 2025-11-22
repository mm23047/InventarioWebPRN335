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
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Almacen;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoAlmacen;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AlmacenDAOTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private AlmacenDAO almacenDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ==================== TESTS BÁSICOS CRUD ====================

    @Test
    void testCrear_EntidadValida() {
        Almacen almacen = new Almacen();
        almacen.setActivo(true);
        almacen.setObservaciones("Almacén central");

        almacenDAO.crear(almacen);

        verify(em, times(1)).persist(almacen);
    }

    @Test
    void testCrear_EntidadNula() {
        assertThrows(IllegalArgumentException.class, () -> {
            almacenDAO.crear(null);
        });
        verify(em, never()).persist(any());
    }

    @Test
    void testActualizar_EntidadValida() {
        Almacen almacen = new Almacen();
        almacen.setId(1);
        almacen.setActivo(false);
        almacen.setObservaciones("Almacén desactivado");

        Almacen merged = new Almacen();
        when(em.merge(almacen)).thenReturn(merged);

        Almacen resultado = almacenDAO.actualizar(almacen);

        assertNotNull(resultado);
        verify(em, times(1)).merge(almacen);
    }

    @Test
    void testActualizar_EntidadNula() {
        assertThrows(IllegalArgumentException.class, () -> {
            almacenDAO.actualizar(null);
        });
        verify(em, never()).merge(any());
    }

    @Test
    void testEliminar_EntidadValida() {
        Almacen almacen = new Almacen();
        almacen.setId(1);

        when(em.contains(almacen)).thenReturn(false);
        when(em.merge(almacen)).thenReturn(almacen);

        almacenDAO.eliminar(almacen);

        verify(em, times(1)).merge(almacen);
        verify(em, times(1)).remove(almacen);
    }

    @Test
    void testEliminar_EntidadNula() {
        assertThrows(IllegalArgumentException.class, () -> {
            almacenDAO.eliminar(null);
        });

        verify(em, never()).remove(any());
    }

    @Test
    void testLeer_IdValido() {
        TipoAlmacen tipoAlmacen = new TipoAlmacen();
        tipoAlmacen.setId(1);
        tipoAlmacen.setNombre("Tipo General");

        Almacen almacen = new Almacen();
        almacen.setId(1);
        almacen.setIdTipoAlmacen(tipoAlmacen);
        almacen.setActivo(true);
        almacen.setObservaciones("Almacén principal");

        when(em.find(Almacen.class, 1)).thenReturn(almacen);

        Almacen resultado = almacenDAO.leer(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Almacén principal", resultado.getObservaciones());
        assertTrue(resultado.getActivo());
        assertNotNull(resultado.getIdTipoAlmacen());
        assertEquals("Tipo General", resultado.getIdTipoAlmacen().getNombre());
        verify(em, times(1)).find(Almacen.class, 1);
    }

    @Test
    void testLeer_IdNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            almacenDAO.leer(null);
        });
        verify(em, never()).find(any(), any());
    }

    @Test
    void testLeer_IdNoExistente() {
        when(em.find(Almacen.class, 999)).thenReturn(null);

        Almacen resultado = almacenDAO.leer(999);

        assertNull(resultado);
        verify(em, times(1)).find(Almacen.class, 999);
    }

    @Test
    void testCount_ConRegistros() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Long> cq = mock(CriteriaQuery.class);
        Root<Almacen> root = mock(Root.class);
        TypedQuery<Long> query = mock(TypedQuery.class);

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cq);
        when(cq.from(Almacen.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(cq.select(any())).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(10L);

        int resultado = almacenDAO.count();

        assertEquals(10, resultado);
    }

    @Test
    void testCount_SinRegistros() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Long> cq = mock(CriteriaQuery.class);
        Root<Almacen> root = mock(Root.class);
        TypedQuery<Long> query = mock(TypedQuery.class);

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(cq);
        when(cq.from(Almacen.class)).thenReturn(root);
        when(cb.count(root)).thenReturn(null);
        when(cq.select(any())).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(0L);

        int resultado = almacenDAO.count();

        assertEquals(0, resultado);
    }

    @Test
    void testFindRange_RangoValido() {
        TipoAlmacen tipo = new TipoAlmacen();
        tipo.setId(1);
        tipo.setNombre("General");

        Almacen almacen1 = new Almacen();
        almacen1.setId(1);
        almacen1.setIdTipoAlmacen(tipo);
        almacen1.setActivo(true);

        Almacen almacen2 = new Almacen();
        almacen2.setId(2);
        almacen2.setIdTipoAlmacen(tipo);
        almacen2.setActivo(true);

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Almacen> cq = mock(CriteriaQuery.class);
        Root<Almacen> root = mock(Root.class);
        TypedQuery<Almacen> query = mock(TypedQuery.class);

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Almacen.class)).thenReturn(cq);
        when(cq.from(Almacen.class)).thenReturn(root);
        when(cq.select(root)).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(almacen1, almacen2));

        List<Almacen> resultado = almacenDAO.findRange(0, 10);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(10);
    }

    @Test
    void testFindRange_RangoVacio() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Almacen> cq = mock(CriteriaQuery.class);
        Root<Almacen> root = mock(Root.class);
        TypedQuery<Almacen> query = mock(TypedQuery.class);

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Almacen.class)).thenReturn(cq);
        when(cq.from(Almacen.class)).thenReturn(root);
        when(cq.select(root)).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Almacen> resultado = almacenDAO.findRange(0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindRange_ParametrosNegativos() {
        assertThrows(IllegalArgumentException.class, () -> {
            almacenDAO.findRange(-1, -1);
        });
    }

    @Test
    void testGetEntityManager() {
        EntityManager resultado = almacenDAO.getEntityManager();
        assertNotNull(resultado);
        assertEquals(em, resultado);
    }
}
