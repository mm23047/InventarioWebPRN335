package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

@Entity
@Table(name = "compra", schema = "public")
@NamedQueries({
        @NamedQuery(name = "Compra.findByEstado",
                query = "SELECT c FROM Compra c WHERE c.estado = :estado ORDER BY c.fecha DESC"),
        @NamedQuery(name = "Compra.findByProveedor",
                query = "SELECT c FROM Compra c WHERE c.proveedor.id = :idProveedor ORDER BY c.fecha DESC"),
        @NamedQuery(name = "Compra.findByFechaRange",
                query = "SELECT c FROM Compra c WHERE c.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fecha DESC"),
        @NamedQuery(name = "Compra.calcularMontoTotal",
                query = "SELECT SUM(d.cantidad * d.precio) FROM CompraDetalle d WHERE d.idCompra.id = :idCompra AND d.estado != 'ANULADO'")
})
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor") // Esta es la columna real en la BD
    private Proveedor proveedor;

    @Column(name = "fecha")
    private OffsetDateTime fecha;

    @Size(max = 10)
    @Column(name = "estado", length = 10)
    private String estado;

    @Lob
    @Column(name = "observaciones")
    private String observaciones;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public OffsetDateTime getFecha() {
        return fecha;
    }

    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getIdProveedor() {
        return proveedor != null ? proveedor.getId() : null;
    }
}