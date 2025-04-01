package data.pescaderia;

import jakarta.persistence.*;
import data.HistoricoProducto;

@Entity
@Table(name = "HistoricoPescado")
@AttributeOverride(name = "id", column = @Column(name = "id_historico_pescado"))
@AssociationOverride(name = "producto", joinColumns = @JoinColumn(name = "id_pescado"))
public class HistoricoPescado extends HistoricoProducto<Pescado> {
}

