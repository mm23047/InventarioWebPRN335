package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.rest.server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ProductoTipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Producto;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.ProductoTipoProducto;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoProducto;

import java.util.List;
import java.util.UUID;

@Path("producto/{idProducto}/tipo_producto")
@Tag(name = "ProductoTipoProducto", description = "Operaciones sobre las relaciones entre productos y tipos de producto")
public class ProductoTipoProductoResource {

    @Inject
    ProductoTipoProductoDAO productoTipoProductoDAO;

    @Inject
    ProductoDAO productoDAO;

    @Inject
    TipoProductoDAO tipoProductoDAO;

    @Operation(
            summary = "Obtener tipos de producto por ID de producto",
            description = "Retorna una lista paginada de los tipos de producto asociados a un producto específico"
    )
    @APIResponse(
            responseCode = "200",
            description = "Lista recuperada exitosamente",
            headers = {@Header(name = "Total-records", description = "Total de registros disponibles", schema = @Schema(type = SchemaType.INTEGER))},
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ProductoTipoProducto.class, type = SchemaType.ARRAY)
            )
    )
    @APIResponse(
            responseCode = "422",
            description = "Parámetros inválidos",
            headers = {@Header(name = "Missing-parameter", description = "Detalles de parámetros inválidos", schema = @Schema(type = SchemaType.STRING))}
    )
    @APIResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            headers = {@Header(name = "Server-exception", description = "Error ocurrido en el servidor", schema = @Schema(type = SchemaType.STRING))}
    )
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @Parameter(description = "ID del producto", required = true)
            @PathParam("idProducto") UUID idProducto,
            @Parameter(description = "Índice del primer registro a recuperar", example = "0")
            @Min(0)
            @DefaultValue("0")
            @QueryParam("first")
            int first,
            @Parameter(description = "Cantidad máxima de registros a recuperar", example = "50")
            @Max(100)
            @DefaultValue("50")
            @QueryParam("max")
            int max) {

        if (idProducto == null) {
            return Response.status(422).header("Missing-parameter", "idProducto is required").build();
        }

        if (first >= 0 && max > 0 && max <= 100) {
            try {
                List<ProductoTipoProducto> salida = productoTipoProductoDAO.findByIdProducto(idProducto, first, max);
                long total = productoTipoProductoDAO.countByIdProducto(idProducto);
                return Response.ok(salida).header("Total-records", total).build();
            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db").build();
            }
        }
        return Response.status(422).header("Missing-parameter", "first:" + first + ", max:" + max).build();
    }

    @Operation(
            summary = "Buscar relación producto-tipo producto por ID",
            description = "Retorna una relación específica entre producto y tipo de producto"
    )
    @APIResponse(
            responseCode = "200",
            description = "Relación encontrada exitosamente",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ProductoTipoProducto.class)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "Relación no encontrada",
            headers = {@Header(name = "Not-found-id", description = "ID de la relación no encontrada", schema = @Schema(type = SchemaType.STRING))}
    )
    @APIResponse(
            responseCode = "422",
            description = "Parámetros inválidos",
            headers = {@Header(name = "Missing-parameter", description = "Parámetros requeridos faltantes", schema = @Schema(type = SchemaType.STRING))}
    )
    @APIResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            headers = {@Header(name = "Server-exception", description = "Error al acceder a la base de datos", schema = @Schema(type = SchemaType.STRING))}
    )
    @GET
    @Path("{idRelacion}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(
            @Parameter(description = "ID del producto", required = true)
            @PathParam("idProducto") UUID idProducto,
            @Parameter(description = "ID de la relación producto-tipo producto", required = true)
            @PathParam("idRelacion") UUID idRelacion) {

        if (idProducto == null || idRelacion == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idProducto and idRelacion are required")
                    .build();
        }

        try {
            ProductoTipoProducto entity = productoTipoProductoDAO.leer(idRelacion);

            if (entity == null) {
                return Response.status(404)
                        .header("Not-found-id", String.valueOf(idRelacion))
                        .build();
            }

            // Verificar que la relación pertenece al producto correcto
            if (!entity.getIdProducto().getId().equals(idProducto)) {
                return Response.status(404)
                        .header("Not-found", "Relation does not belong to this product")
                        .build();
            }

            return Response.ok(entity).build();

        } catch (Exception ex) {
            return Response.status(500)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @Operation(
            summary = "Crear relación producto-tipo producto",
            description = "Crea una nueva asociación entre un producto y un tipo de producto"
    )
    @APIResponse(
            responseCode = "201",
            description = "Relación creada exitosamente",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ProductoTipoProducto.class)
            ),
            headers = {@Header(name = "Location", description = "URI del recurso creado", schema = @Schema(type = SchemaType.STRING))}
    )
    @APIResponse(
            responseCode = "404",
            description = "Producto o TipoProducto no encontrado",
            headers = {@Header(name = "Not-found", description = "Recurso no encontrado", schema = @Schema(type = SchemaType.STRING))}
    )
    @APIResponse(
            responseCode = "422",
            description = "Parámetros inválidos",
            headers = {@Header(name = "Missing-parameter", description = "Parámetros requeridos faltantes", schema = @Schema(type = SchemaType.STRING))}
    )
    @APIResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            headers = {@Header(name = "Server-exception", description = "Error al crear la entidad", schema = @Schema(type = SchemaType.STRING))}
    )
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @Parameter(description = "ID del producto", required = true)
            @PathParam("idProducto") UUID idProducto,
            @Parameter(description = "Datos de la relación producto-tipo producto a crear", required = true)
            ProductoTipoProducto entity,
            @Context UriInfo uriInfo) {

        if (idProducto == null || entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idProducto and entity are required")
                    .build();
        }

        if (entity.getIdTipoProducto() == null || entity.getIdTipoProducto().getId() == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idTipoProducto.id is required")
                    .build();
        }

        try {
            // Verificar que el producto existe
            Producto producto = productoDAO.buscarPorId(idProducto);
            if (producto == null) {
                return Response.status(404)
                        .header("Not-found", "Producto not found")
                        .build();
            }

            // Verificar que el tipo producto existe
            TipoProducto tipo = tipoProductoDAO.buscarRegistroPorId(entity.getIdTipoProducto().getId());
            if (tipo == null) {
                return Response.status(404)
                        .header("Not-found", "TipoProducto not found")
                        .build();
            }

            // Establecer las relaciones y generar UUID
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
            }
            entity.setIdProducto(producto);
            entity.setIdTipoProducto(tipo);

            productoTipoProductoDAO.crear(entity);

            return Response.created(
                    uriInfo.getAbsolutePathBuilder()
                            .path(String.valueOf(entity.getId()))
                            .build()
            ).entity(entity).build();

        } catch (Exception ex) {
            return Response.status(500)
                    .header("Server-exception", "Cannot create entity: " + ex.getMessage())
                    .build();
        }
    }

    @Operation(
            summary = "Eliminar relación producto-tipo producto",
            description = "Elimina una asociación existente entre un producto y un tipo de producto"
    )
    @APIResponse(
            responseCode = "204",
            description = "Relación eliminada exitosamente"
    )
    @APIResponse(
            responseCode = "404",
            description = "Relación no encontrada",
            headers = {@Header(name = "Not-found-id", description = "ID de la relación no encontrada", schema = @Schema(type = SchemaType.STRING))}
    )
    @APIResponse(
            responseCode = "422",
            description = "Parámetros inválidos",
            headers = {@Header(name = "Missing-parameter", description = "Parámetros requeridos faltantes o relación no pertenece al producto", schema = @Schema(type = SchemaType.STRING))}
    )
    @APIResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            headers = {@Header(name = "Server-exception", description = "Error al eliminar la entidad", schema = @Schema(type = SchemaType.STRING))}
    )
    @DELETE
    @Path("{idRelacion}")
    public Response delete(
            @Parameter(description = "ID del producto", required = true)
            @PathParam("idProducto") UUID idProducto,
            @Parameter(description = "ID de la relación a eliminar", required = true)
            @PathParam("idRelacion") UUID idRelacion) {

        if (idProducto == null || idRelacion == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idProducto and idRelacion are required")
                    .build();
        }

        try {
            ProductoTipoProducto entity = productoTipoProductoDAO.leer(idRelacion);

            if (entity == null) {
                return Response.status(404)
                        .header("Not-found-id", String.valueOf(idRelacion))
                        .build();
            }

            // Verificar que la relación pertenece al producto correcto
            if (!entity.getIdProducto().getId().equals(idProducto)) {
                return Response.status(422)
                        .header("Invalid-parameter", "Relation does not belong to this product")
                        .build();
            }

            productoTipoProductoDAO.eliminar(entity);
            return Response.noContent().build();

        } catch (Exception ex) {
            return Response.status(500)
                    .header("Server-exception", "Cannot delete entity: " + ex.getMessage())
                    .build();
        }
    }
}