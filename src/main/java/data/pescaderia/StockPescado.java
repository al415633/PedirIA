package data.pescaderia;

import data.StockProducto;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "StockPescado")
@AttributeOverride(name = "id", column = @Column(name = "id_stock_pescado"))
public class StockPescado extends StockProducto {
    @ManyToOne
    @JoinColumn(name = "id_pescado", nullable = false)
    private Pescado producto;

    public StockPescado() {
    }

    public StockPescado(BigDecimal cantidad, LocalDate fechaVencimiento, LocalDate fechaIngreso, Pescado producto) {
        super(cantidad, fechaVencimiento, fechaIngreso);
        this.producto = producto;
    }

    @Override
    public Pescado getProducto() {
        return producto;
    }

    public void setProducto(Pescado producto) {
        this.producto = producto;
    }

}
