package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.rest.server;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ClienteDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Cliente;

import java.util.UUID;

@Path("cliente")
public class ClienteResource extends AbstractResource<Cliente> {

    @Inject
    ClienteDAO clienteDAO;

    @Override
    protected InventarioDefaultDataAccess<Cliente> getDAO() {
        return clienteDAO;
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") UUID id) {
        if (id != null) {
            try {
                Cliente resp = clienteDAO.buscarPorId(id);
                if (resp != null) {
                    return Response.ok(resp).build();
                }
                return Response.status(Response.Status.NOT_FOUND).header("Not-found-id", "Record with id " + id + " not found").build();
            } catch (Exception ex) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Server-exception", "Cannot access db").build();
            }
        }
        return Response.status(422).header("Missing-parameter", "id").build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") UUID id) {
        if (id != null) {
            try {
                Cliente resp = clienteDAO.buscarPorId(id);
                if (resp != null) {
                    clienteDAO.eliminar(resp);
                    return Response.noContent().build();
                }
                return Response.status(Response.Status.NOT_FOUND).header("Not-found-id", "Record with id " + id + " not found").build();
            } catch (Exception ex) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Server-exception", "Cannot access db").build();
            }
        }
        return Response.status(422).header("Missing-parameter", "id").build();
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(Cliente entity, @Context UriInfo uriInfo) {
        if (entity != null && entity.getId() == null) {
            try {
                // Generar un nuevo UUID si no tiene uno asignado
                entity.setId(UUID.randomUUID());
                clienteDAO.crear(entity);
                return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(entity.getId())).build()).build();
            } catch (Exception ex) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Server-exception", "Cannot access db").build();
            }
        }
        return Response.status(422).header("Missing-parameter", "entity must not be null and entity.id be null").build();
    }
}

