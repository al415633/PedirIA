package data;

import jakarta.persistence.*;

@Entity
@Table(name = "Carne")
public class Carne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carne", columnDefinition = "serial")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "categoria", nullable = false, length = 100)
    private String categoria;

    @Column(name = "unidad", nullable = false, length = 20)
    private String unidad;

    @Column(name = "tipo_conserva", nullable = false, length = 50)
    private String tipoConserva;

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getUnidad() {
        return unidad;
    }

    public String getTipoConserva() {
        return tipoConserva;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public void setTipoConserva(String tipoConserva) {
        this.tipoConserva = tipoConserva;
    }
}



