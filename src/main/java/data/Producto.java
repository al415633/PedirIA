package data;

import jakarta.persistence.*;

import java.util.Arrays;

@MappedSuperclass
public abstract class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "serial")
    protected Long id;

    @Column(name = "nombre", nullable = false, length = 250)
    protected String nombre;

    @Column(name = "unidad", nullable = false, length = 20)
    protected String unidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conserva", nullable = false, length = 50)
    protected TipoConserva tipoConserva;

    @Column(name = "id_img", nullable = false)
    protected Integer idImg;

    @Transient
    protected String imagenNombre;
    @Transient
    protected String imagenTipo;
    @Transient
    protected byte[] imagenDatos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUnidad() {
        return unidad;
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

    public Integer getIdImg() {
        return idImg;
    }

    public void setIdImg(Integer idImg) {
        this.idImg = idImg;
    }

    public String getImagenNombre() {
        return imagenNombre;
    }

    public void setImagenNombre(String imagenNombre) {
        this.imagenNombre = imagenNombre;
    }

    public String getImagenTipo() {
        return imagenTipo;
    }

    public void setImagenTipo(String imagenTipo) {
        this.imagenTipo = imagenTipo;
    }

    public byte[] getImagenDatos() {
        return imagenDatos;
    }

    public void setImagenDatos(byte[] imagenDatos) {
        this.imagenDatos = imagenDatos;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", unidad='" + unidad + '\'' +
                ", tipoConserva=" + tipoConserva +
                ", idImg=" + idImg +
                ", imagenNombre='" + imagenNombre + '\'' +
                ", imagenTipo='" + imagenTipo + '\'' +
                ", imagenDatos=" + Arrays.toString(imagenDatos) +
                '}';
    }
}
