package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "venta", schema = "public")
public class Venta {
    @Id
    @Column(name = "id_venta", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente idCliente;

    @Column(name = "fecha")
    private OffsetDateTime fecha;

    @Lob
    @Column(name = "observaciones")
    private String observaciones;

<<<<<<< Updated upstream
=======
    @Transient
    private BigDecimal total;

    @OneToMany(mappedBy = "idVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaDetalle> detalles = new ArrayList<>();

    // --- Getters y Setters ---
>>>>>>> Stashed changes
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Cliente getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Cliente idCliente) {
        this.idCliente = idCliente;
    }

    public OffsetDateTime getFecha() {
        return fecha;
    }

    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

<<<<<<< Updated upstream
=======
    public BigDecimal getTotal() {
        if (detalles != null && !detalles.isEmpty()) {
            return detalles.stream()
                    .map(d -> d.getPrecio().multiply(d.getCantidad()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<VentaDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<VentaDetalle> detalles) {
        this.detalles = detalles;
    }

    public void agregarDetalle(VentaDetalle detalle) {
        detalle.setIdVenta(this);
        detalles.add(detalle);
    }

    public void quitarDetalle(VentaDetalle detalle) {
        detalles.remove(detalle);
        detalle.setIdVenta(null);
    }

    public void calcularTotal() {
        // No hacer nada - el total se calcula en getTotal()
    }
>>>>>>> Stashed changes
}