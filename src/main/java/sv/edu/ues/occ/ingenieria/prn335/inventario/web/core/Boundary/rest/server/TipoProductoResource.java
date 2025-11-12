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
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoProducto;


@Path("tipo_producto")
public class TipoProductoResource {

    @Inject
    TipoProductoDAO tipoProductoDAO;

    @APIResponse(
        responseCode = "500",
        description = "Internal server error",
         headers = {@Header(name = "Server-exception", description = "Indicates a server exception occurred during data access", schema = @Schema(type = SchemaType.STRING))
        }

        )


    @APIResponse(
        responseCode = "422",
        description = "Invalid parameters",
        headers = {@Header(name = "Missing parameter", description = "Indicates missing or invalid parameters", schema = @Schema(type = SchemaType.STRING))
        }
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
                int total = tipoProductoDAO.count();
                return  Response.ok(tipoProductoDAO.findRange(first,max)).header("Total-records", total).build();
            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Server-exception","Cannot access db").build();
            }
        }
         return Response.status(422).header("Missing-parameter", "first:" + first + ", max:" + max).build();
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") Long id) {
        if(id != null){
            try {
             TipoProducto resp = tipoProductoDAO.buscarRegistroPorId(id);
             if(resp != null){
                 return Response.ok(resp).build();
             }
                return Response.status(Response.Status.NOT_FOUND).header("Not-found-id", "Record with id "+id+" not found").build();
            } catch (Exception ex) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Server-exception","Cannot access db").build();
            }

        }
        return Response.status(422).header("Missing-parameter", "id").build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id){
        if(id != null){
            try {
                TipoProducto resp = tipoProductoDAO.buscarRegistroPorId(id);
                if(resp != null){
                    tipoProductoDAO.eliminar(resp);
                    return Response.noContent().build();
                }
                return Response.status(Response.Status.NOT_FOUND).header("Not-found-id", "Record with id "+id+" not found").build();
            } catch (Exception ex) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Server-exception","Cannot access db").build();
            }

        }
        return Response.status(422).header("Missing-parameter", "id").build();
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(TipoProducto entity, @Context UriInfo uriInfo){
        if(entity != null && entity.getId() == null){
            try {
                if(entity.getIdTipoProductoPadre() !=null && entity.getIdTipoProductoPadre().getId() != null){
                    TipoProducto padre = tipoProductoDAO.buscarRegistroPorId(entity.getIdTipoProductoPadre().getId());
                    if (padre == null) {
                        return Response.status(422).header("Missing-parameter", "If parent is assigned, must not be null ad exists in the db").build();
                    }
                    entity.setIdTipoProductoPadre(padre);
                }
                tipoProductoDAO.crear(entity);
                return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(entity.getId())).build()).build();
            } catch (Exception ex) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Server-exception","Cannot access db").build();
            }

        }
        return Response.status(422).header("Missing-parameter", "entity must not be null and entity.id be null").build();
    }

}