package services;

import data.Pescado;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class PescadoDAO {
    @Inject
    EntityManager em;

    @Transactional
    public Pescado create(Pescado pescado) {
        // Verifica si hay datos de imagen para insertar
        if (pescado.getImagenNombre() != null && pescado.getImagenDatos() != null) {
            // Inserción en la tabla ImagenesCarnes y obtención del id generado.
            Query q = em.createNativeQuery(
                    "INSERT INTO ImagenesPescados (nombre, tipo, datos) VALUES (?, ?, ?) RETURNING id_img"
            );
            q.setParameter(1, pescado.getImagenNombre());
            q.setParameter(2, pescado.getImagenTipo());
            q.setParameter(3, pescado.getImagenDatos());
            Integer generatedImageId = (Integer) q.getSingleResult();
            pescado.setIdImg(generatedImageId);
        }
        em.persist(pescado);
        return pescado;
    }

    public Pescado retrieve(Long id) {
        Query q = em.createNativeQuery(
                "SELECT c.id_pescado, c.nombre, c.unidad, c.tipo_conserva, c.id_img, " +
                        "i.nombre AS \"imagenNombre\", i.tipo AS \"imagenTipo\", i.datos AS \"imagenDatos\" " +
                        "FROM Pescado c " +
                        "JOIN ImagenesPescados i ON c.id_img = i.id_img " +
                        "WHERE c.id_pescado = ?",
                "PescadoMapping"
        );
        q.setParameter(1, id);

        Object[] result = (Object[]) q.getSingleResult();

        Pescado pescado = (Pescado) result[0]; // La entidad Carne
        String imagenNombre = (String) result[1];
        String imagenTipo = (String) result[2];
        byte[] imagenDatos = (byte[]) result[3];

        pescado.setImagenNombre(imagenNombre);
        pescado.setImagenTipo(imagenTipo);
        pescado.setImagenDatos(imagenDatos);

        return pescado;
    }

    @Transactional
    public Pescado update(Pescado pescado) {
        Pescado existing = em.find(Pescado.class, pescado.getId());
        if (existing == null) {
            return null;
        }
        existing.setNombre(pescado.getNombre());
        existing.setUnidad(pescado.getUnidad());
        existing.setTipoConserva(pescado.getTipoConserva());

        // Si se suministran nuevos datos de imagen, actualiza la tabla ImagenesCarnes.
        if (pescado.getImagenNombre() != null && pescado.getImagenDatos() != null) {
            Query q = em.createNativeQuery(
                    "UPDATE ImagenesPescados SET nombre = ?, tipo = ?, datos = ? WHERE id_img = ?"
            );
            q.setParameter(1, pescado.getImagenNombre());
            q.setParameter(2, pescado.getImagenTipo());
            q.setParameter(3, pescado.getImagenDatos());
            q.setParameter(4, existing.getIdImg());
            q.executeUpdate();
        }
        em.merge(existing);
        return existing;
    }

    @Transactional
    public Pescado delete(Long id) {
        Pescado existing = em.find(Pescado.class, id);
        if (existing == null) {
            return null;
        }
        // Elimina la carne
        em.remove(existing);

        // Elimina la imagen
        Query q = em.createNativeQuery("DELETE FROM ImagenesPescados WHERE id_img = ?");
        q.setParameter(1, existing.getIdImg());
        q.executeUpdate();

        return existing;
    }

    public Collection<Pescado> getAll() {
        Query q = em.createNativeQuery(
                "SELECT c.id_pescado, c.nombre, c.unidad, c.tipo_conserva, c.id_img, " +
                        "i.nombre AS imagenNombre, i.tipo AS imagenTipo, i.datos AS imagenDatos " +
                        "FROM Pescado c " +
                        "JOIN ImagenesPescados i ON c.id_img = i.id_img",
                "PescadoMapping"
        );
        List<Pescado> list = q.getResultList();
        return list;
    }

    public boolean existePescado(String nombre, String unidad) {
        Long count = em.createQuery(
                        "SELECT COUNT(c) FROM Pescado c WHERE LOWER(c.nombre) = LOWER(:nombre) AND LOWER(c.unidad) = LOWER(:unidad)",
                        Long.class)
                .setParameter("nombre", nombre.trim().toLowerCase())
                .setParameter("unidad", unidad.trim().toLowerCase())
                .getSingleResult();
        return count > 0;
    }

    public Collection<Pescado> getAllByUsuario(Long idNegocio) {
        Query q = em.createNativeQuery(
                "SELECT c.id_pescado, c.nombre, c.unidad, c.tipo_conserva, c.id_img, " +
                        "i.nombre AS imagenNombre, i.tipo AS imagenTipo, i.datos AS imagenDatos " +
                        "FROM Pescado c " +
                        "JOIN ImagenesPescados i ON c.id_img = i.id_img " +
                        "WHERE c.id_negocio = ?",
                "PescadoMapping"
        );
        q.setParameter(1, idNegocio);
        return q.getResultList();
    }

}
