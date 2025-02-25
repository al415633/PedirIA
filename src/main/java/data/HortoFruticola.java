package data;

import jakarta.persistence.*;

@Entity
@Table(name = "HortoFruticola")
public class HortoFruticola {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hortofruticola")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "categoria", nullable = false, length = 100)
    private String categoria;

    @Column(name = "variedad", nullable = false, length = 100)
    private String variedad;

    @Column(name = "unidad", nullable = false, length = 20)
    private String unidad;

    @Column(name = "origen", nullable = false, length = 100)
    private String origen;

    @Column(name = "tipo_conserva", nullable = false, length = 50)
    private String tipoConserva;

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setVariedad(String variedad) {
        this.variedad = variedad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public void setTipoConserva(String tipoConserva) {
        this.tipoConserva = tipoConserva;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getVariedad() {
        return variedad;
    }

    public String getUnidad() {
        return unidad;
    }

    public String getOrigen() {
        return origen;
    }

    public String getTipoConserva() {
        return tipoConserva;
    }
}
