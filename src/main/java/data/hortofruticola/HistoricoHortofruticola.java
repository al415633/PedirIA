package data.hortofruticola;

import jakarta.persistence.*;
import data.HistoricoProducto;

@Entity
@Table(name = "HistoricoHortofruticola")
@AttributeOverride(name = "id", column = @Column(name = "id_historico_hortofruticola"))
@AssociationOverride(name = "producto", joinColumns = @JoinColumn(name = "id_hortofruticola"))
public class HistoricoHortofruticola extends HistoricoProducto<HortoFruticola> {
}
