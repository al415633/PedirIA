package data;

import jakarta.persistence.*;

@Entity
@Table(name = "Pescado")
public class Pescado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pescado", columnDefinition = "serial")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 250)
    private String nombre;

    @Column(name = "unidad", nullable = false, length = 20)
    private String unidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conserva", nullable = false, length = 50)
    private TipoConserva tipoConserva;

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public TipoConserva getTipoConserva() {
        return tipoConserva;
    }

    public void setTipoConserva(TipoConserva tipoConserva) {
        this.tipoConserva = tipoConserva;
    }

    @Override
    public String toString() {
        return "Pescado{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", unidad='" + unidad + '\'' +
                ", tipoConserva=" + tipoConserva +
                '}';
    }
}
