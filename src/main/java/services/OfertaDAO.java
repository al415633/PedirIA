package services;

import data.AprovechanteDetails;
import data.Oferta;
import data.ProductoOferta;
import data.StockProducto;
import data.carniceria.StockCarne;
import data.hortofruticola.StockHortoFruticola;
import data.pescaderia.StockPescado;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.transaction.Transactional;
import services.carne.StockCarneDAO;
import services.hortofruticola.StockHortoFruticolaDAO;
import services.pescado.StockPescadoDAO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class OfertaDAO {

    @Inject
    protected EntityManager em;

    @Inject
    protected StockCarneDAO stockCarneDAO;

    @Inject
    protected StockPescadoDAO stockPescadoDAO;

    @Inject
    protected StockHortoFruticolaDAO stockHortoFruticolaDAO;

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

    public List<Oferta> obtenerOfertasPorProductoCarnePublicadas(Long idNegocio, Long idProductoCarne) {
        return em.createQuery("""
        SELECT o FROM Oferta o
        JOIN o.productoOferta po
        JOIN po.stockCarne sc
        WHERE o.negocio.id = :idNegocio
          AND sc.producto.id = :idProductoCarne
          AND o.fechaBaja IS NULL
    """, Oferta.class)
                .setParameter("idNegocio", idNegocio)
                .setParameter("idProductoCarne", idProductoCarne)
                .getResultList();
    }

    public List<Oferta> obtenerOfertasPorProductoCarneAceptadas(Long idNegocio, Long idProductoCarne) {
        return em.createQuery("""
        SELECT o FROM Oferta o
        JOIN o.productoOferta po
        JOIN po.stockCarne sc
        WHERE o.negocio.id = :idNegocio
          AND sc.producto.id = :idProductoCarne
          AND o.fechaBaja IS NOT NULL
    """, Oferta.class)
                .setParameter("idNegocio", idNegocio)
                .setParameter("idProductoCarne", idProductoCarne)
                .getResultList();
    }

    public List<Oferta> obtenerOfertasPorProductoPescadoPublicadas(Long idNegocio, Long idPescado) {
        return em.createQuery("""
            SELECT o FROM Oferta o
            JOIN o.productoOferta po
            JOIN po.stockPescado sc
            WHERE o.negocio.id = :idNegocio
              AND sc.producto.id = :idPescado
              AND o.fechaBaja IS NULL
        """, Oferta.class)
                .setParameter("idNegocio", idNegocio)
                .setParameter("idPescado", idPescado)
                .getResultList();
    }
    public List<Oferta> obtenerOfertasPorProductoPescadoAceptadas(Long idNegocio, Long idPescado) {
        return em.createQuery("""
            SELECT o FROM Oferta o
            JOIN o.productoOferta po
            JOIN po.stockPescado sc
            WHERE o.negocio.id = :idNegocio
              AND sc.producto.id = :idPescado
              AND o.fechaBaja IS NOT NULL
        """, Oferta.class)
                .setParameter("idNegocio", idNegocio)
                .setParameter("idPescado", idPescado)
                .getResultList();
    }


    public List<Oferta> obtenerOfertasPorProductoHortofruticolaPublicadas(Long idNegocio, Long idhortofruticola) {
        return em.createQuery("""
            SELECT o FROM Oferta o
            JOIN o.productoOferta po
            JOIN po.stockHortoFruticola sc
            WHERE o.negocio.id = :idNegocio
              AND sc.producto.id = :idhortofruticola
              AND o.fechaBaja IS NULL
        """, Oferta.class)
                .setParameter("idNegocio", idNegocio)
                .setParameter("idhortofruticola", idhortofruticola)
                .getResultList();
    }

    public List<Oferta> obtenerOfertasPorProductoHortofruticolaAceptadas(Long idNegocio, Long idhortofruticola) {
        return em.createQuery("""
            SELECT o FROM Oferta o
            JOIN o.productoOferta po
            JOIN po.stockHortoFruticola sc
            WHERE o.negocio.id = :idNegocio
              AND sc.producto.id = :idhortofruticola
              AND o.fechaBaja IS NOT NULL
        """, Oferta.class)
                .setParameter("idNegocio", idNegocio)
                .setParameter("idhortofruticola", idhortofruticola)
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

    @Transactional
    public <T extends StockProducto<?>> Oferta crearOferta(Oferta oferta, T stock) {
        try {

            // Crear ProductoOferta asociada al stock
            StockProductoDAO<?> stockProductoDAO;
            ProductoOferta productoOferta = new ProductoOferta();
            productoOferta.setOferta(oferta);

            // Según el tipo de stock, asignamos a la relación correspondiente
            if (stock instanceof StockCarne) {
                productoOferta.setStockCarne((StockCarne) stock);
                stockProductoDAO = stockCarneDAO;
            } else if (stock instanceof StockPescado) {
                productoOferta.setStockPescado((StockPescado) stock);
                stockProductoDAO = stockPescadoDAO;
            } else if (stock instanceof StockHortoFruticola) {
                productoOferta.setStockHortoFruticola((StockHortoFruticola) stock);
                stockProductoDAO = stockHortoFruticolaDAO;
            } else {
                throw new IllegalArgumentException("El stock debe ser de tipo StockCarne, StockPescado o StockHortoFruticola.");
            }

            // Persistir la oferta y su ProductoOferta asociado
            em.persist(oferta);
            em.persist(productoOferta);
            try {
                stockProductoDAO.venderStock(stock.getId(), BigDecimal.valueOf(oferta.getCantidad()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("No hay suficiente cantidad para ofertar");
            }


        } catch (Exception e) {
            throw e;
        }
        return oferta;
    }

    public Oferta obtenerOfertaPorId(Long id) {
        return em.find(Oferta.class, id);
    }

    @Transactional
    public boolean eliminarOferta(Long id) {
        try {
            Oferta oferta = em.find(Oferta.class, id);
            if (oferta != null) {
                // Se obtiene el ProductoOferta asociado
                ProductoOferta productoOferta = oferta.getProductoOferta();
                if (productoOferta != null) {
                    BigDecimal cantidadOferta = BigDecimal.valueOf(oferta.getCantidad());
                    StockProducto<?> stockProducto = productoOferta.getStock();
                    if (stockProducto instanceof StockCarne stock) {
                        if (stock.getFechaVencimiento().isAfter(LocalDate.now())) {
                            stock.setCantidad(stock.getCantidad().add(cantidadOferta));
                            em.merge(stock);
                        }
                    } else if (stockProducto instanceof StockPescado stock) {
                        if (stock.getFechaVencimiento().isAfter(LocalDate.now())) {
                            stock.setCantidad(stock.getCantidad().add(cantidadOferta));
                            em.merge(stock);
                        }
                    } else if (stockProducto instanceof StockHortoFruticola stock) {
                        if (stock.getFechaVencimiento().isAfter(LocalDate.now())) {
                            stock.setCantidad(stock.getCantidad().add(cantidadOferta));
                            em.merge(stock);
                        }
                    }
                }
                em.remove(oferta.getProductoOferta());

                // Finalmente se elimina la oferta
                em.remove(oferta);
                return true;
            }
            return false;
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }


    @Transactional
    public Oferta modificarOferta(Oferta oferta) {
        try {
            // Guardar los cambios en la oferta
            return em.merge(oferta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Si ocurre un error, retornar null
        }
    }
}
