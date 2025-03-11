package services;

import data.AprovechanteDetails;
import data.ComercioDetails;
import data.Usuario;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class AprovechanteDAOJPA implements AprovechanteDao{

    @PersistenceContext
    EntityManager em;


    @Override
    public List<Usuario> getAprovechantes() {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.tipo = 'aprovechante'", Usuario.class).getResultList();

    }

    @Override
    public Usuario getAprovechantePorCorreo(String correo) {
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.correo = :correo AND u.tipo = 'aprovechante'", Usuario.class)
                    .setParameter("correo", correo)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean existeCorreo(String correo) {
        return getAprovechantePorCorreo(correo) != null;
    }

    @Override
    public Usuario crearAprovechante(Usuario usuario, AprovechanteDetails aprovechante) {
/*        em.persist(usuario);
        em.flush(); // Asegura que el ID se genera antes de usarlo en el negocio

        aprovechante.setIdAprovechante(usuario.getId_usuario());
        aprovechante.setUsuario(usuario);

        em.persist(aprovechante);
        aprovechante.setTipo_aprovechante(usuario.getTipo());
        usuario.setTipo("aprovechante");
        usuario.setNegocio(aprovechante);*/
    }

    @Override
    public boolean actualizarAprovechante(Usuario usuario, AprovechanteDetails aprovechante) {
        Usuario usuarioExistente = getAprovechantePorCorreo(usuario.getCorreo());
/*        if (usuarioExistente == null) return false;

        usuarioExistente.setPassword(usuario.getPassword());
        usuarioExistente.setTipo(usuario.getTipo());

        AprovechanteDetails negocioExistente = usuarioExistente.getNegocio();
        if (negocioExistente != null) {
            negocioExistente.setNombre(aprovechante.getNombre());
            negocioExistente.setDiaCompraDeStock(negocio.getDiaCompraDeStock());
        } else {
            negocio.setIdNegocio(usuarioExistente.getId_usuario());
            negocio.setUsuario(usuarioExistente);
            em.persist(negocio);
            usuarioExistente.setNegocio(negocio);
        }
        return true;*/
    }

    @Override
    public boolean eliminarAprovechante(String correo) {
        Usuario usuario = getAprovechantePorCorreo(correo);
        if (usuario == null) return false;

        if (usuario.getNegocio() != null) {
            em.remove(usuario.getNegocio());
        }
        em.remove(usuario);
        return true;
    }

    @Override
    public Usuario verificarCredenciales(String correo, String password) {
        try {
            List<Usuario> usuarios = em.createQuery(
                            "SELECT u FROM Usuario u WHERE u.correo = :correo",
                            Usuario.class)
                    .setParameter("correo", correo)
                    .getResultList();

            if (usuarios.isEmpty()) {
                System.out.println("No se encontró ningún usuario con ese correo.");
                return null;
            }

            Usuario usuario = usuarios.get(0); // Tomamos el primer usuario encontrado

            // Verificar la contraseña con Bcrypt
            if (BcryptUtil.matches(password, usuario.getPassword())) {
                return usuario; // Contraseña correcta
            } else {
                System.out.println("Contraseña incorrecta.");
                return null; // Contraseña incorrecta
            }
        } catch (Exception e) {
            e.printStackTrace(); // Imprime el error real
            return null;
        }
    }
}
