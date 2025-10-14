package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import java.util.List;

public interface InventarioDAOInterface<T> {

    public void crear(T registro) throws IllegalArgumentException, IllegalAccessException;

    public void eliminar(T registro) throws IllegalArgumentException, IllegalAccessException;

    public T actualizar(T registro) throws IllegalStateException;

    public T leer(Object id) throws IllegalStateException;

    public List<T> findRange(int first, int max) throws IllegalArgumentException;

   public int count() throws IllegalArgumentException;

}
