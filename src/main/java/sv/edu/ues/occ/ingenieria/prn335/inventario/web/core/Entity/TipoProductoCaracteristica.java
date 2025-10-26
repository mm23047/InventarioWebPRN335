package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;



@Entity
@Table(name = "tipo_producto_caracteristica", schema = "public")
@NamedQueries({
        // NUEVOS NAMED QUERIES para el formulario de características
        @NamedQuery(name = "TipoProductoCaracteristica.findByTipoProductoId",
                query = "SELECT tpc FROM TipoProductoCaracteristica tpc WHERE tpc.idTipoProducto.id = :idTipoProducto"),

        @NamedQuery(name = "TipoProductoCaracteristica.countByTipoProductoId",
                query = "SELECT COUNT(tpc) FROM TipoProductoCaracteristica tpc WHERE tpc.idTipoProducto.id = :idTipoProducto"),

        // Mantener los existentes SIN MODIFICAR
        @NamedQuery(name = "TipoProductoCaracteristica.findByTipoProducto",
                query = "SELECT tpc FROM TipoProductoCaracteristica tpc WHERE tpc.idTipoProducto.idTipoProductoPadre= :idTipoProducto"),

        @NamedQuery(name = "TipoProductoCaracteristica.countByTipoProducto",
                query = "SELECT COUNT(tpc.id) FROM TipoProductoCaracteristica tpc WHERE tpc.idTipoProducto.idTipoProductoPadre = :idTipoProducto"),

        @NamedQuery(name = "TipoProductoCaracteristica.findObligatoriasByTipoProducto",
                query = "SELECT tpc FROM TipoProductoCaracteristica tpc WHERE tpc.idTipoProducto.idTipoProductoPadre = :idTipoProducto AND tpc.obligatorio = true"),

        @NamedQuery(name = "TipoProductoCaracteristica.countObligatoriasTipoProducto",
                query = "SELECT COUNT(tpc.id) FROM TipoProductoCaracteristica tpc WHERE tpc.idTipoProducto.idTipoProductoPadre = :idTipoProducto AND tpc.obligatorio = true")
})
public class TipoProductoCaracteristica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_producto_caracteristica", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caracteristica")
    private Caracteristica idCaracteristica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_producto")
    private TipoProducto idTipoProducto;

    @Column(name = "obligatorio")
    private Boolean obligatorio;

    @Column(name = "fecha_creacion")
    private OffsetDateTime fechaCreacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Caracteristica getIdCaracteristica() {
        return idCaracteristica;
    }

    public void setIdCaracteristica(Caracteristica idCaracteristica) {
        this.idCaracteristica = idCaracteristica;
    }

    public TipoProducto getIdTipoProducto() {
        return idTipoProducto;
    }

    public void setIdTipoProducto(TipoProducto idTipoProducto) {
        this.idTipoProducto = idTipoProducto;
    }

    public Boolean getObligatorio() {
        return obligatorio;
    }

    public void setObligatorio(Boolean obligatorio) {
        this.obligatorio = obligatorio;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

}