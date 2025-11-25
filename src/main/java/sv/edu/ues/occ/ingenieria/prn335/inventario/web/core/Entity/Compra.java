package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

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
    @SequenceGenerator(name = "compra_seq_gen", sequenceName = "compra_id_compra_seq", schema = "public", allocationSize = 1 ) // CRÍTICO: debe ser 1
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "compra_seq_gen")
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

    // ========== RELACIÓN BIDIRECCIONAL CON CASCADE ==========
    @OneToMany(mappedBy = "idCompra", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CompraDetalle> compraDetalleList;
    //

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

    // Getter y Setter para la lista de detalles
    public List<CompraDetalle> getCompraDetalleList() {
        return compraDetalleList;
    }

    public void setCompraDetalleList(List<CompraDetalle> compraDetalleList) {
        this.compraDetalleList = compraDetalleList;
    }

    @Transient
    private BigDecimal montoTotal;
    public BigDecimal getMontoTotal() {
        return montoTotal;
    }


    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
}