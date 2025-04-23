package data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import data.carniceria.StockCarne;
import data.hortofruticola.StockHortoFruticola;
import data.pescaderia.StockPescado;
import jakarta.persistence.*;

@Entity
@Table(name = "ProductoOferta")
public class ProductoOferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_oferta_producto")
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_oferta", nullable = false, unique = true)
    @JsonBackReference
    private Oferta oferta;

    @ManyToOne
    @JoinColumn(name = "id_stock_carne")
    private StockCarne stockCarne;

    @ManyToOne
    @JoinColumn(name = "id_stock_pescado")
    private StockPescado stockPescado;

    @ManyToOne
    @JoinColumn(name = "id_stock_hortofruticola")
    private StockHortoFruticola stockHortoFruticola;

    public StockProducto<?> getStock() {
        if (stockCarne != null) return stockCarne;
        if (stockPescado != null) return stockPescado;
        if (stockHortoFruticola != null) return stockHortoFruticola;
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Oferta getOferta() {
        return oferta;
    }

    public void setOferta(Oferta oferta) {
        this.oferta = oferta;
    }

    public void setStockCarne(StockCarne stockCarne) {
        this.stockCarne = stockCarne;
    }

    public void setStockPescado(StockPescado stockPescado) {
        this.stockPescado = stockPescado;
    }

    public void setStockHortoFruticola(StockHortoFruticola stockHortoFruticola) {
        this.stockHortoFruticola = stockHortoFruticola;
    }

    @Override
    public String toString() {
        return "ProductoOferta{" +
                "id=" + id +
                ", stockProducto=" + getStock() +
                '}';
    }
}
