package data.hortofruticola;

import data.StockProducto;
import jakarta.persistence.*;

@Entity
@Table(name = "StockHortofruticola")
@AttributeOverride(name = "id", column = @Column(name = "id_stock_hortofruticola"))
@AssociationOverride(name = "producto", joinColumns = @JoinColumn(name = "id_hortofruticola"))
public class StockHortoFruticola extends StockProducto<HortoFruticola> {
}