package data.carniceria;

import data.HistoricoProducto;
import jakarta.persistence.*;

@Entity
@Table(name = "HistoricoCarne")
@AttributeOverride(name = "id", column = @Column(name = "id_historico_carne"))
public class HistoricoCarne extends HistoricoProducto {

    @ManyToOne
    @JoinColumn(name = "id_carne", nullable = false)
    private Carne producto;

    public Carne getCarne() {
        return producto;
    }

    public void setCarne(Carne carne) {
        this.producto = carne;
    }

    @Override
    public String toString() {
        return super.toString() + ", carne=" + producto;
    }
}
