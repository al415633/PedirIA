package data.carniceria;

import data.StockProducto;
import jakarta.persistence.*;

@Entity
@Table(name = "StockCarne")
@AttributeOverride(name = "id", column = @Column(name = "id_stock_carne"))
@AssociationOverride(name = "producto", joinColumns = @JoinColumn(name = "id_carne"))
public class StockCarne extends StockProducto<Carne> {
}

