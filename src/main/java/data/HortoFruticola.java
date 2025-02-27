package data;

import jakarta.persistence.*;

@Entity
@Table(name = "HortoFruticola")
public class HortoFruticola {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hortofruticola", columnDefinition = "serial")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "unidad", nullable = false, length = 20)
    private String unidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conserva", nullable = false, length = 50)
    private TipoConserva tipoConserva;

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUnidad() {
        return unidad;
    }

    public TipoConserva getTipoConserva() {
        return tipoConserva;
    }

    public void setTipoConserva(TipoConserva tipoConserva) {
        this.tipoConserva = tipoConserva;
    }
}
