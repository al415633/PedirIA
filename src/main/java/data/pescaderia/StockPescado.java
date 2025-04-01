package data.pescaderia;

import data.StockProducto;
import jakarta.persistence.*;

@Entity
@Table(name = "StockPescado")
@AttributeOverride(name = "id", column = @Column(name = "id_stock_pescado"))
@AssociationOverride(name = "producto", joinColumns = @JoinColumn(name = "id_pescado"))
public class StockPescado extends StockProducto<Pescado> {}
