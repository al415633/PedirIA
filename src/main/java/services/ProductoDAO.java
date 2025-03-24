package services;

import data.Producto;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.util.Collection;
import java.util.List;

public abstract class ProductoDAO<T extends Producto> {

    @Inject
    protected EntityManager em;

    private final Class<T> entityType;
    private final String tableName;
    private final String imageTableName;
    private final String idColumn;
    private final String mappingName;

    public ProductoDAO(Class<T> entityType, String tableName, String imageTableName, String idColumn, String mappingName) {
        this.entityType = entityType;
        this.tableName = tableName;
        this.imageTableName = imageTableName;
        this.idColumn = idColumn;
        this.mappingName = mappingName;
    }

    @Transactional
    public T create(T producto) {
        if (producto.getImagenNombre() != null && producto.getImagenDatos() != null) {
            Query q = em.createNativeQuery(
                    "INSERT INTO " + imageTableName + " (nombre, tipo, datos) VALUES (?, ?, ?) RETURNING id_img"
            );
            q.setParameter(1, producto.getImagenNombre());
            q.setParameter(2, producto.getImagenTipo());
            q.setParameter(3, producto.getImagenDatos());
            Integer generatedImageId = (Integer) q.getSingleResult();
            producto.setIdImg(generatedImageId);
        }
        em.persist(producto);
        return producto;
    }

    public T retrieve(Long id) {
        Query q = em.createNativeQuery(
                "SELECT c." + idColumn + ", c.nombre, c.unidad, c.tipo_conserva, c.id_img, " +
                        "i.nombre AS imagenNombre, i.tipo AS imagenTipo, i.datos AS imagenDatos " +
                        "FROM " + tableName + " c " +
                        "JOIN " + imageTableName + " i ON c.id_img = i.id_img " +
                        "WHERE c." + idColumn + " = ?",
                mappingName
        );
        q.setParameter(1, id);

        Object[] result = (Object[]) q.getSingleResult();

        T producto = (T) result[0];
        producto.setImagenNombre((String) result[1]);
        producto.setImagenTipo((String) result[2]);
        producto.setImagenDatos((byte[]) result[3]);

        return producto;
    }

    @Transactional
    public T update(T producto) {
        T existing = em.find(entityType, producto.getId());
        if (existing == null) {
            return null;
        }
        existing.setNombre(producto.getNombre());
        existing.setUnidad(producto.getUnidad());
        existing.setTipoConserva(producto.getTipoConserva());

        if (producto.getImagenNombre() != null && producto.getImagenDatos() != null) {
            Query q = em.createNativeQuery(
                    "UPDATE " + imageTableName + " SET nombre = ?, tipo = ?, datos = ? WHERE id_img = ?"
            );
            q.setParameter(1, producto.getImagenNombre());
            q.setParameter(2, producto.getImagenTipo());
            q.setParameter(3, producto.getImagenDatos());
            q.setParameter(4, existing.getIdImg());
            q.executeUpdate();
        }
        em.merge(existing);
        return existing;
    }

    @Transactional
    public T delete(Long id) {
        T existing = em.find(entityType, id);
        if (existing == null) {
            return null;
        }
        em.remove(existing);

        Query q = em.createNativeQuery("DELETE FROM " + imageTableName + " WHERE id_img = ?");
        q.setParameter(1, existing.getIdImg());
        q.executeUpdate();

        return existing;
    }

    public Collection<T> getAll() {
        Query q = em.createNativeQuery(
                "SELECT c." + idColumn + ", c.nombre, c.unidad, c.tipo_conserva, c.id_img, " +
                        "i.nombre AS imagenNombre, i.tipo AS imagenTipo, i.datos AS imagenDatos " +
                        "FROM " + tableName + " c " +
                        "JOIN " + imageTableName + " i ON c.id_img = i.id_img",
                mappingName
        );
        return q.getResultList();
    }

    public boolean existeProducto(String nombre, String unidad) {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE LOWER(nombre) = LOWER(?) AND LOWER(unidad) = LOWER(?)";

        Query query = em.createNativeQuery(sql);
        query.setParameter(1, nombre.trim().toLowerCase());
        query.setParameter(2, unidad.trim().toLowerCase());

        Long count = ((Number) query.getSingleResult()).longValue();
        return count > 0;
    }

    public Collection<T> getAllByUsuario(Long idNegocio) {
        Query q = em.createNativeQuery(
                "SELECT c." + idColumn + ", c.nombre, c.unidad, c.tipo_conserva, c.id_img, " +
                        "i.nombre AS imagenNombre, i.tipo AS imagenTipo, i.datos AS imagenDatos " +
                        "FROM " + tableName + " c " +
                        "JOIN " + imageTableName + " i ON c.id_img = i.id_img " +
                        "WHERE c.id_negocio = ?",
                mappingName
        );
        q.setParameter(1, idNegocio);
        return q.getResultList();
    }
}
