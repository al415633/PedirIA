package data.pescaderia;

import jakarta.persistence.*;
import data.HistoricoProducto;

@Entity
@Table(name = "HistoricoPescado")
@AttributeOverride(name = "id", column = @Column(name = "id_historico_pescado"))
public class HistoricoPescado extends HistoricoProducto {

    @ManyToOne
    @JoinColumn(name = "id_pescado", nullable = false)
    private Pescado producto;

    public Pescado getPescado() {
        return producto;
    }

    public void setPescado(Pescado pescado) {
        this.producto = pescado;
    }

    @Override
    public String toString() {
        return super.toString() + ", pescado=" + producto;
    }
}

