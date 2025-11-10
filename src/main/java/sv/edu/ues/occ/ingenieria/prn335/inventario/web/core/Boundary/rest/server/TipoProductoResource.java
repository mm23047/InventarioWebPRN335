package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.rest.server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoProducto;


@Path("tipo_producto")
public class TipoProductoResource {

    @Inject
    TipoProductoDAO tipoProductoDAO;

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

}