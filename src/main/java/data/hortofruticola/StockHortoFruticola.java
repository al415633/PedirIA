package data.hortofruticola;

import data.StockProducto;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "StockPescado")
@AttributeOverride(name = "id", column = @Column(name = "id_stock_hortofruticola"))
public class StockHortoFruticola extends StockProducto {
    @ManyToOne
    @JoinColumn(name = "id_carne", nullable = false)
    private HortoFruticola producto;

    public StockHortoFruticola() {
    }

    public StockHortoFruticola(BigDecimal cantidad, LocalDate fechaVencimiento, LocalDate fechaIngreso, HortoFruticola producto) {
        super(cantidad, fechaVencimiento, fechaIngreso);
        this.producto = producto;
    }

    public HortoFruticola getProducto() {
        return producto;
    }

    public void setProducto(HortoFruticola producto) {
        this.producto = producto;
    }
}