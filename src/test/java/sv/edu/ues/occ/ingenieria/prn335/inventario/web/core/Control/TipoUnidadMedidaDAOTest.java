package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoUnidadMedida;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para TipoUnidadMedidaDAO
 * Valida los métodos específicos del DAO además de la funcionalidad heredada
 */
@ExtendWith(MockitoExtension.class)
class TipoUnidadMedidaDAOTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TipoUnidadMedidaDAO dao;

    private TipoUnidadMedida tipoUnidadMedida;

    @BeforeEach
    void setUp() {
        // Configurar el EntityManager mockeado en el DAO
        dao.em = entityManager;

        // Crear entidad de prueba
        tipoUnidadMedida = new TipoUnidadMedida();
        tipoUnidadMedida.setId(1);
        tipoUnidadMedida.setNombre("Kilogramo");
        tipoUnidadMedida.setUnidadBase("kg");
        tipoUnidadMedida.setActivo(true);
    }

    // ==================== PRUEBAS PARA buscarRegistroPorId ====================

    @Test
    void buscarRegistroPorId_ConIdValido_DebeRetornarEntidad() {
        // Arrange
        when(entityManager.find(TipoUnidadMedida.class, 1)).thenReturn(tipoUnidadMedida);

        // Act
        TipoUnidadMedida resultado = dao.buscarRegistroPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Kilogramo", resultado.getNombre());
        verify(entityManager, times(1)).find(TipoUnidadMedida.class, 1);
    }

    @Test
    void buscarRegistroPorId_ConIdInexistente_DebeRetornarNull() {
        // Arrange
        when(entityManager.find(TipoUnidadMedida.class, 999)).thenReturn(null);

        // Act
        TipoUnidadMedida resultado = dao.buscarRegistroPorId(999);

        // Assert
        assertNull(resultado);
        verify(entityManager, times(1)).find(TipoUnidadMedida.class, 999);
    }

    @Test
    void buscarRegistroPorId_ConIdNulo_DebeRetornarNull() {
        // Act
        TipoUnidadMedida resultado = dao.buscarRegistroPorId(null);

        // Assert
        assertNull(resultado);
        verify(entityManager, never()).find(any(), any());
    }

    @Test
    void buscarRegistroPorId_ConExcepcion_DebeRetornarNull() {
        // Arrange
        when(entityManager.find(TipoUnidadMedida.class, 1))
                .thenThrow(new RuntimeException("Error en la base de datos"));

        // Act
        TipoUnidadMedida resultado = dao.buscarRegistroPorId(1);

        // Assert
        assertNull(resultado);
        verify(entityManager, times(1)).find(TipoUnidadMedida.class, 1);
    }

    // ==================== PRUEBAS PARA getEntityManager ====================

    @Test
    void getEntityManager_DebeRetornarEntityManagerConfigurado() {
        // Act
        EntityManager resultado = dao.getEntityManager();

        // Assert
        assertNotNull(resultado);
        assertEquals(entityManager, resultado);
    }

    // ==================== PRUEBAS DE CONSTRUCTOR ====================

    @Test
    void constructor_DebeInicializarCorrectamente() {
        // Act
        TipoUnidadMedidaDAO nuevoDao = new TipoUnidadMedidaDAO();

        // Assert
        assertNotNull(nuevoDao);
    }

    // ==================== PRUEBAS DE INTEGRACIÓN CON CLASE BASE
    // ====================

    @Test
    void heredaDe_InventarioDefaultDataAccess() {
        // Assert
        assertTrue(dao instanceof InventarioDefaultDataAccess);
    }
}
