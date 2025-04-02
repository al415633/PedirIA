package services;

import data.AprovechanteDetails;
import data.Oferta;
import data.ProductoOferta;
import data.StockProducto;
import data.carniceria.StockCarne;
import data.hortofruticola.StockHortoFruticola;
import data.pescaderia.StockPescado;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.List;

public class OfertaDAO {

    @Inject
    protected EntityManager em;

    // Obtiene todas las ofertas
    public List<Oferta> obtenerOfertas() {
        return em.createQuery("SELECT o FROM Oferta o", Oferta.class)
                .getResultList();
    }

    // Obtiene todas las ofertas creadas por un negocio/comercio
    public List<Oferta> obtenerOfertasPorNegocio(Long idNegocio) {
        return em.createQuery("SELECT o FROM Oferta o WHERE o.negocio.id = :idNegocio", Oferta.class)
                .setParameter("idNegocio", idNegocio)
                .getResultList();
    }

    // Actualiza una oferta para asignarle un aprovechante y una fecha_baja
    public Oferta aceptarOferta(Oferta oferta, Long idAprovechante) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            AprovechanteDetails aprovechante = em.find(AprovechanteDetails.class, idAprovechante);
            if (aprovechante == null) {
                throw new IllegalArgumentException("No se encontró el aprovechante con id: " + idAprovechante);
            }
            oferta.setAprovechante(aprovechante);
            oferta.setFechaBaja(LocalDate.now());
            Oferta updatedOferta = em.merge(oferta);
            transaction.commit();
            return updatedOferta;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return null;
        }
    }

    public <T extends StockProducto<?>> Oferta crearOferta(Oferta oferta, T stock) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Crear ProductoOferta asociada al stock
            ProductoOferta productoOferta = new ProductoOferta();
            productoOferta.setOferta(oferta);

            // Según el tipo de stock, asignamos a la relación correspondiente
            if (stock instanceof StockCarne) {
                productoOferta.setStockCarne((StockCarne) stock);
            } else if (stock instanceof StockPescado) {
                productoOferta.setStockPescado((StockPescado) stock);
            } else if (stock instanceof StockHortoFruticola) {
                productoOferta.setStockHortoFruticola((StockHortoFruticola) stock);
            } else {
                throw new IllegalArgumentException("El stock debe ser de tipo StockCarne, StockPescado o StockHortoFruticola.");
            }

            // Persistir la oferta y su ProductoOferta asociado
            em.persist(oferta);
            em.persist(productoOferta);

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
        return oferta;
    }

    public Oferta obtenerOfertaPorId(Long id) {
        return em.find(Oferta.class, id);
    }

    public boolean eliminarOferta(Long id) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            Oferta oferta = em.find(Oferta.class, id);
            if (oferta != null) {
                em.remove(oferta);
                transaction.commit();
                return true;
            }

            transaction.rollback();
            return false;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        }
    }
}
