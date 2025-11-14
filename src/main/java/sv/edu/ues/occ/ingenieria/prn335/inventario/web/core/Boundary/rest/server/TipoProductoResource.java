package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.rest.server;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoProducto;


@Path("tipo_producto")
public class TipoProductoResource extends AbstractResource<TipoProducto> {

    @Inject
    TipoProductoDAO tipoProductoDAO;

    @Override
    protected InventarioDefaultDataAccess<TipoProducto> getDAO() {
        return tipoProductoDAO;
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