package services;
import data.Usuario;
import data.ComercioDetails;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ComercioDAOJPA implements ComercioDao{

    @PersistenceContext
    EntityManager em;

    public List<Usuario> getComercios() {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.tipo = 'negocio'", Usuario.class).getResultList();
    }

    public Usuario getComercioPorCorreo(String correo) {
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.correo = :correo AND u.tipo = 'negocio'", Usuario.class)
                    .setParameter("correo", correo)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean existeCorreo(String correo) {
        return getComercioPorCorreo(correo) != null;
    }

    @Transactional
    public Usuario crearNegocio(Usuario usuario, ComercioDetails negocio) {
        em.persist(usuario);
        em.flush(); // Asegura que el ID se genera antes de usarlo en el negocio

        negocio.setIdNegocio(usuario.getId_usuario());
        negocio.setUsuario(usuario);
        em.persist(negocio);

        usuario.setNegocio(negocio);
        return usuario;
    }

    @Transactional
    public boolean actualizarNegocio(Usuario usuario, ComercioDetails negocio) {
        Usuario usuarioExistente = getComercioPorCorreo(usuario.getCorreo());
        if (usuarioExistente == null) return false;

        usuarioExistente.setPassword(usuario.getPassword());
        usuarioExistente.setTipo(usuario.getTipo());

        ComercioDetails negocioExistente = usuarioExistente.getNegocio();
        if (negocioExistente != null) {
            negocioExistente.setNombre(negocio.getNombre());
            negocioExistente.setDiaCompraDeStock(negocio.getDiaCompraDeStock());
        } else {
            negocio.setIdNegocio(usuarioExistente.getId_usuario());
            negocio.setUsuario(usuarioExistente);
            em.persist(negocio);
            usuarioExistente.setNegocio(negocio);
        }
        return true;
    }

    @Transactional
    public boolean eliminarNegocio(String correo) {
        Usuario usuario = getComercioPorCorreo(correo);
        if (usuario == null) return false;

        if (usuario.getNegocio() != null) {
            em.remove(usuario.getNegocio());
        }
        em.remove(usuario);
        return true;
    }
}
