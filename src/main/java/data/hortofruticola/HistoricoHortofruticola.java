package data.hortofruticola;

import jakarta.persistence.*;
import data.HistoricoProducto;

@Entity
@Table(name = "HistoricoHortofruticola")
@AttributeOverride(name = "id", column = @Column(name = "id_historico_hortofruticola"))
public class HistoricoHortofruticola extends HistoricoProducto {

    @ManyToOne
    @JoinColumn(name = "id_hortofruticola", nullable = false)
    private HortoFruticola hortofruticola;

    public HortoFruticola getHortofruticola() {
        return hortofruticola;
    }

    public void setHortofruticola(HortoFruticola hortofruticola) {
        this.hortofruticola = hortofruticola;
    }

    @Override
    public String toString() {
        return super.toString() + ", hortofruticola=" + hortofruticola;
    }
}
