package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.rest.server;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDefaultDataAccess;

import java.util.List;

/**
 * Clase abstracta base para recursos REST
 * Proporciona implementación común para operaciones CRUD
 * @param <T> Tipo de la entidad
 */
public abstract class AbstractResource<T> {

    /**
     * Método abstracto que debe ser implementado por las clases hijas
     * para proporcionar acceso al DAO específico
     * @return instancia del DAO
     */
    protected abstract InventarioDefaultDataAccess<T> getDAO();

    @APIResponse(
        responseCode = "500",
        description = "Internal server error",
        headers = {@Header(name = "Server-exception", description = "Indicates a server exception occurred during data access", schema = @Schema(type = SchemaType.STRING))}
    )
    @APIResponse(
        responseCode = "422",
        description = "Invalid parameters",
        headers = {@Header(name = "Missing-parameter", description = "Indicates missing or invalid parameters", schema = @Schema(type = SchemaType.STRING))}
    )
    @Operation(summary = "Find entities in a specified range", description = "Returns a list of entities based on the provided range parameters 'first' and 'max'.")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @Min(0)
            @DefaultValue("0")
            @QueryParam("first")
            int first,
            @Max(100)
            @DefaultValue("50")
            @QueryParam("max")
            int max) {
        if (first >= 0 && max > 0 && max <= 100) {
            try {
                InventarioDefaultDataAccess<T> dao = getDAO();
                int total = dao.count();
                List<T> entities = dao.findRange(first, max);
                return Response.ok(entities).header("Total-records", total).build();
            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db").build();
            }
        }
        return Response.status(422)
            .header("Missing-parameter", "first:" + first + ", max:" + max).build();
    }
}

