package data.carniceria;

import data.HistoricoProducto;
import jakarta.persistence.*;

@Entity
@Table(name = "HistoricoCarne")
@AttributeOverride(name = "id", column = @Column(name = "id_historico_carne"))
@AssociationOverride(name = "producto", joinColumns = @JoinColumn(name = "id_carne"))
public class HistoricoCarne extends HistoricoProducto<Carne> { }
