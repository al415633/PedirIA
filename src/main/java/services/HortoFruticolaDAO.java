package services;

import data.Carne;
import data.HortoFruticola;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class HortoFruticolaDAO {
    @Inject
    EntityManager em;

    @Transactional
    public HortoFruticola create(HortoFruticola hortoFruticola) {
        // Verifica si hay datos de imagen para insertar
        if (hortoFruticola.getImagenNombre() != null && hortoFruticola.getImagenDatos() != null) {
            // Inserción en la tabla ImagenesHortofruticolas y obtención del id generado.
            Query q = em.createNativeQuery(
                    "INSERT INTO ImagenesHortofruticolas (nombre, tipo, datos) VALUES (?, ?, ?) RETURNING id_img"
            );
            q.setParameter(1, hortoFruticola.getImagenNombre());
            q.setParameter(2, hortoFruticola.getImagenTipo());
            q.setParameter(3, hortoFruticola.getImagenDatos());
            Integer generatedImageId = (Integer) q.getSingleResult();
            hortoFruticola.setIdImg(generatedImageId);
        }
        em.persist(hortoFruticola);
        return hortoFruticola;
    }

    // Recupera una Carne haciendo JOIN entre las tablas Carne e ImagenesCarnes
    public HortoFruticola retrieve(Long id) {
        // Se usa un mapeo de resultados (SqlResultSetMapping) llamado "CarneMapping" para transformar el resultado en una instancia de Carne.
        Query q = em.createNativeQuery(
                "SELECT h.id_hortofruticola, h.nombre, h.unidad, h.tipo_conserva, h.id_img, " +
                        "i.nombre AS \"imagenNombre\", i.tipo AS \"imagenTipo\", i.datos AS \"imagenDatos\" " +
                        "FROM Hortofruticola h " +
                        "JOIN ImagenesHortofruticolas i ON h.id_img = i.id_img " +
                        "WHERE h.id_hortofruticola = ?",
                "HortofruticolaMapping"
        );
        q.setParameter(1, id);

        Object[] result = (Object[]) q.getSingleResult();

        HortoFruticola hortofruticola = (HortoFruticola) result[0]; // La entidad Carne
        String imagenNombre = (String) result[1];
        String imagenTipo = (String) result[2];
        byte[] imagenDatos = (byte[]) result[3];

        hortofruticola.setImagenNombre(imagenNombre);
        hortofruticola.setImagenTipo(imagenTipo);
        hortofruticola.setImagenDatos(imagenDatos);

        return hortofruticola;
    }

    // Actualiza Carne y, si se han modificado los datos de la imagen, actualiza también la tabla ImagenesCarnes.
    @Transactional
    public HortoFruticola update(HortoFruticola hortofruticola) {
        HortoFruticola existing = em.find(HortoFruticola.class, hortofruticola.getId());
        if (existing == null) {
            return null;
        }
        existing.setNombre(hortofruticola.getNombre());
        existing.setUnidad(hortofruticola.getUnidad());
        existing.setTipoConserva(hortofruticola.getTipoConserva());

        // Si se suministran nuevos datos de imagen, actualiza la tabla ImagenesCarnes.
        if (hortofruticola.getImagenNombre() != null && hortofruticola.getImagenDatos() != null) {
            Query q = em.createNativeQuery(
                    "UPDATE ImagenesHortofruticolas SET nombre = ?, tipo = ?, datos = ? WHERE id_img = ?"
            );
            q.setParameter(1, hortofruticola.getImagenNombre());
            q.setParameter(2, hortofruticola.getImagenTipo());
            q.setParameter(3, hortofruticola.getImagenDatos());
            q.setParameter(4, existing.getIdImg());
            q.executeUpdate();
        }
        em.merge(existing);
        return existing;
    }

    // Elimina Carne y su imagen asociada.
    @Transactional
    public HortoFruticola delete(Long id) {
        HortoFruticola existing = em.find(HortoFruticola.class, id);
        if (existing == null) {
            return null;
        }
        // Elimina la carne
        em.remove(existing);

        // Elimina la imagen
        Query q = em.createNativeQuery("DELETE FROM ImagenesHortofruticolas WHERE id_img = ?");
        q.setParameter(1, existing.getIdImg());
        q.executeUpdate();

        return existing;
    }

    // Recupera todas las carnes realizando un JOIN con la tabla de imágenes.
    public Collection<HortoFruticola> getAll() {
        Query q = em.createNativeQuery(
                "SELECT h.id_hortofruticola, h.nombre, h.unidad, h.tipo_conserva, h.id_img, " +
                        "i.nombre AS imagenNombre, i.tipo AS imagenTipo, i.datos AS imagenDatos " +
                        "FROM Hortofruticola h " +
                        "JOIN ImagenesHortofruticolas i ON h.id_img = i.id_img",
                "HortofruticolaMapping"
        );
        List<HortoFruticola> list = q.getResultList();
        return list;
    }

    public boolean existeHortofruticola(String nombre, String unidad) {
        Long count = em.createQuery(
                        "SELECT COUNT(h) FROM HortoFruticola h WHERE LOWER(h.nombre) = LOWER(:nombre) AND LOWER(h.unidad) = LOWER(:unidad)",
                        Long.class)
                .setParameter("nombre", nombre.trim().toLowerCase())
                .setParameter("unidad", unidad.trim().toLowerCase())
                .getSingleResult();
        return count > 0;
    }

    public Collection<HortoFruticola> getAllByUsuario(Long idNegocio) {
        Query q = em.createNativeQuery(
                "SELECT h.id_hortofruticola, h.nombre, h.unidad, h.tipo_conserva, h.id_img, " +
                        "i.nombre AS imagenNombre, i.tipo AS imagenTipo, i.datos AS imagenDatos " +
                        "FROM Hortofruticola h " +
                        "JOIN ImagenesHortofruticolas i ON h.id_img = i.id_img " +
                        "JOIN Negocio n ON n.id_negocio = h.id_negocio " +
                        "WHERE h.id_negocio = ?",
                "HortofruticolaMapping"
        );
        q.setParameter(1, idNegocio);
        return q.getResultList();
    }
}

