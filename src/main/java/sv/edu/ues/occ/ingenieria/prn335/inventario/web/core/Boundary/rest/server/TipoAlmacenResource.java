package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.rest.server;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoAlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoAlmacen;

@Path("tipo_almacen")
public class TipoAlmacenResource extends AbstractResource<TipoAlmacen> {

    @Inject
    TipoAlmacenDAO tipoAlmacenDAO;

    @Override
    protected InventarioDefaultDataAccess<TipoAlmacen> getDAO() {
        return tipoAlmacenDAO;
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response findById(@PathParam("id") Integer id) {
        if (id != null) {
            try {
                TipoAlmacen resp = tipoAlmacenDAO.buscarRegistroPorId(id);
                if (resp != null) {
                    return Response.ok(resp).build();
                }
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record with id " + id + " not found").build();
            } catch (Exception ex) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db").build();
            }
        }
        return Response.status(422).header("Missing-parameter", "id").build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Integer id) {
        if (id != null) {
            try {
                TipoAlmacen resp = tipoAlmacenDAO.buscarRegistroPorId(id);
                if (resp != null) {
                    tipoAlmacenDAO.eliminar(resp);
                    return Response.noContent().build();
                }
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record with id " + id + " not found").build();
            } catch (Exception ex) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db").build();
            }
        }
        return Response.status(422).header("Missing-parameter", "id").build();
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response create(TipoAlmacen entity, @Context UriInfo uriInfo) {
        if (entity != null && entity.getId() == null) {
            try {
                tipoAlmacenDAO.crear(entity);
                return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(entity.getId())).build())
                        .build();
            } catch (Exception ex) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db").build();
            }
        }
        return Response.status(422).header("Missing-parameter", "entity must not be null and entity.id be null")
                .build();
    }

    @PUT
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response update(@PathParam("id") Integer id, TipoAlmacen entity) {
        if (id != null && entity != null) {
            try {
                TipoAlmacen existing = tipoAlmacenDAO.buscarRegistroPorId(id);
                if (existing != null) {
                    entity.setId(id);
                    tipoAlmacenDAO.actualizar(entity);
                    return Response.ok(entity).build();
                }
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record with id " + id + " not found").build();
            } catch (Exception ex) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db").build();
            }
        }
        return Response.status(422).header("Missing-parameter", "id and entity must not be null").build();
    }
}
