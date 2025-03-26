package data.carniceria;

import data.StockProducto;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "StockCarne")
@AttributeOverride(name = "id", column = @Column(name = "id_stock_carne"))
public class StockCarne extends StockProducto {
    @ManyToOne
    @JoinColumn(name = "id_carne", nullable = false)
    private Carne producto;

    public StockCarne() {
    }

    public StockCarne(BigDecimal cantidad, LocalDate fechaVencimiento, LocalDate fechaIngreso, Carne producto) {
        super(cantidad, fechaVencimiento, fechaIngreso);
        this.producto = producto;
    }

    public Carne getProducto() {
        return producto;
    }

    public void setProducto(Carne producto) {
        this.producto = producto;
    }
}

