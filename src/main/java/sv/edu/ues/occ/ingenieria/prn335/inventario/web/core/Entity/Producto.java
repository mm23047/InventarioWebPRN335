package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "producto", schema = "public")
public class Producto {
    @Id
    @Column(name = "id_producto", nullable = false)
    private UUID id;




    @Size(max = 155)
    @Pattern(regexp = "^[a-zA-ZÁÉÍÓÚáéíóúÑñ]+( [a-zA-ZÁÉÍÓÚáéíóúÑñ]+)*$",
            message = "El nombre del producto solo puede contener letras y espacios internos, sin espacios al inicio o final")
    @Column(name = "nombre_producto", length = 155)
    private String nombreProducto;






    @Lob
    @Column(name = "referencia_externa")
    private String referenciaExterna;

    @Column(name = "activo")
    private Boolean activo;

    @Lob
    @Column(name = "comentarios")
    private String comentarios;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getReferenciaExterna() {
        return referenciaExterna;
    }

    public void setReferenciaExterna(String referenciaExterna) {
        this.referenciaExterna = referenciaExterna;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

}