package services;

import data.AprovechanteDetails;
import data.Usuario;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
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

    @Transactional
    public Usuario crearAprovechante(Usuario usuario, AprovechanteDetails aprovechante) {
        em.persist(usuario);
        em.flush(); // Asegura que el ID se genera antes de usarlo en el negocio

        aprovechante.setIdAprovechante(usuario.getId_usuario());
        aprovechante.setUsuario(usuario);

        em.persist(aprovechante);
        //aprovechante.setTipo_aprovechante(usuario.getTipo());
        usuario.setTipo("aprovechante");
        usuario.setAprovechante(aprovechante);
        return usuario;
    }

    @Transactional
    public boolean actualizarAprovechante(Usuario usuario, AprovechanteDetails aprovechante) {
        Usuario usuarioExistente = getAprovechantePorCorreo(usuario.getCorreo());
        if (usuarioExistente == null) return false;

        usuarioExistente.setPassword(usuario.getPassword());
        usuarioExistente.setTipo(usuario.getTipo());

        AprovechanteDetails aprovechanteExistente = usuarioExistente.getAprovechante();
        if (aprovechanteExistente != null) {
            aprovechanteExistente.setCondiciones(aprovechante.getCondiciones());
            aprovechanteExistente.setCondiciones2(aprovechante.getCondiciones2());
        } else {
            aprovechanteExistente.setIdAprovechante(usuarioExistente.getId_usuario());
            aprovechanteExistente.setUsuario(usuarioExistente);
            em.persist(aprovechante);
            usuarioExistente.setAprovechante(aprovechante);
        }
        return true;
    }

    @Transactional
    public boolean eliminarAprovechante(String correo) {
        Usuario usuario = getAprovechantePorCorreo(correo);
        System.out.println("llegooooooooœ");
        System.out.println(correo);
        if (usuario == null) return false;

        usuario.setFechaBaja(LocalDateTime.now());
        em.merge(usuario);
        return true;
    }

    @Override
    public Usuario verificarCredenciales(String correo, String password) {
        try {
            List<Usuario> usuarios = em.createQuery(
                            "SELECT u FROM Usuario u WHERE u.correo = :correo AND u.fecha_baja IS NULL",
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
