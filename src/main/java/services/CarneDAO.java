package services;

import data.Carne;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class CarneDAO {
    @Inject
    EntityManager em;

    // Crea una nueva Carne. Se inserta primero la imagen y se asigna el ID resultante.
    @Transactional
    public Carne create(Carne carne) {
        // Verifica si hay datos de imagen para insertar
        if (carne.getImagenNombre() != null && carne.getImagenDatos() != null) {
            // Inserción en la tabla ImagenesCarnes y obtención del id generado.
            Query q = em.createNativeQuery(
                    "INSERT INTO ImagenesCarnes (nombre, tipo, datos) VALUES (?, ?, ?) RETURNING id_img"
            );
            q.setParameter(1, carne.getImagenNombre());
            q.setParameter(2, carne.getImagenTipo());
            q.setParameter(3, carne.getImagenDatos());
            Integer generatedImageId = (Integer) q.getSingleResult();
            carne.setIdImg(generatedImageId);
        }
        em.persist(carne);
        return carne;
    }

    // Recupera una Carne haciendo JOIN entre las tablas Carne e ImagenesCarnes
    public Carne retrieve(Long id) {
        // Se usa un mapeo de resultados (SqlResultSetMapping) llamado "CarneMapping" para transformar el resultado en una instancia de Carne.
        Query q = em.createNativeQuery(
                "SELECT c.id_carne, c.nombre, c.unidad, c.tipo_conserva, c.id_img, " +
                        "i.nombre AS \"imagenNombre\", i.tipo AS \"imagenTipo\", i.datos AS \"imagenDatos\" " +
                        "FROM Carne c " +
                        "JOIN ImagenesCarnes i ON c.id_img = i.id_img " +
                        "WHERE c.id_carne = ?",
                "CarneMapping"
        );
        q.setParameter(1, id);

        Object[] result = (Object[]) q.getSingleResult();

        Carne carne = (Carne) result[0]; // La entidad Carne
        String imagenNombre = (String) result[1];
        String imagenTipo = (String) result[2];
        byte[] imagenDatos = (byte[]) result[3];
        carne.setImagenNombre(imagenNombre);
        carne.setImagenTipo(imagenTipo);
        carne.setImagenDatos(imagenDatos);

        return carne;
    }

    // Actualiza Carne y, si se han modificado los datos de la imagen, actualiza también la tabla ImagenesCarnes.
    @Transactional
    public Carne update(Carne carne) {
        Carne existing = em.find(Carne.class, carne.getId());
        if (existing == null) {
            return null;
        }
        existing.setNombre(carne.getNombre());
        existing.setUnidad(carne.getUnidad());
        existing.setTipoConserva(carne.getTipoConserva());

        // Si se suministran nuevos datos de imagen, actualiza la tabla ImagenesCarnes.
        if (carne.getImagenNombre() != null && carne.getImagenDatos() != null) {
            Query q = em.createNativeQuery(
                    "UPDATE ImagenesCarnes SET nombre = ?, tipo = ?, datos = ? WHERE id_img = ?"
            );
            q.setParameter(1, carne.getImagenNombre());
            q.setParameter(2, carne.getImagenTipo());
            q.setParameter(3, carne.getImagenDatos());
            q.setParameter(4, existing.getIdImg());
            q.executeUpdate();
        }
        em.merge(existing);
        return existing;
    }

    // Elimina Carne y su imagen asociada.
    @Transactional
    public Carne delete(Long id) {
        Carne existing = em.find(Carne.class, id);
        if (existing == null) {
            return null;
        }
        // Elimina la carne
        em.remove(existing);
        
        // Elimina la imagen
        Query q = em.createNativeQuery("DELETE FROM ImagenesCarnes WHERE id_img = ?");
        q.setParameter(1, existing.getIdImg());
        q.executeUpdate();

        return existing;
    }

    // Recupera todas las carnes realizando un JOIN con la tabla de imágenes.
    public Collection<Carne> getAll() {
        Query q = em.createNativeQuery(
                "SELECT c.id_carne, c.nombre, c.unidad, c.tipo_conserva, c.id_img, " +
                        "i.nombre AS imagenNombre, i.tipo AS imagenTipo, i.datos AS imagenDatos " +
                        "FROM Carne c " +
                        "JOIN ImagenesCarnes i ON c.id_img = i.id_img",
                "CarneMapping"
        );
        List<Carne> list = q.getResultList();
        return list;
    }
}
