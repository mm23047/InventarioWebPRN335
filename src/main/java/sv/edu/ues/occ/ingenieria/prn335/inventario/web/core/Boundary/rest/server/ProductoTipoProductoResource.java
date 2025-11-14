package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.rest.server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ProductoTipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.ProductoTipoProducto;

import java.util.List;
import java.util.UUID;

@Path("producto/{idProducto}/tipo_producto")
public class ProductoTipoProductoResource {

    @Inject
    ProductoTipoProductoDAO productoTipoProductoDAO;

    @APIResponse(
            responseCode = "500",
            description = "Internal server error",
            headers = {@Header(name = "Server-exception", description = "Server error occurred", schema = @Schema(type = SchemaType.STRING))}
    )
    @APIResponse(
            responseCode = "422",
            description = "Invalid parameters",
            headers = {@Header(name = "Missing-parameter", description = "Invalid parameters", schema = @Schema(type = SchemaType.STRING))}
    )
    @Operation(summary = "Find producto tipo producto by product ID", description = "Returns a list of product types for a specific product")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @PathParam("idProducto") UUID idProducto,
            @Min(0)
            @DefaultValue("0")
            @QueryParam("first")
            int first,
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
}
