package data;

import jakarta.persistence.*;

import java.util.Arrays;

@Entity
@Table(name = "Pescado")
@SqlResultSetMapping(
        name = "PescadoMapping",
        entities = {
                @EntityResult(
                        entityClass = Pescado.class,
                        fields = {
                                @FieldResult(name = "id", column = "id_pescado"),
                                @FieldResult(name = "nombre", column = "nombre"),
                                @FieldResult(name = "unidad", column = "unidad"),
                                @FieldResult(name = "tipoConserva", column = "tipo_conserva"),
                                @FieldResult(name = "idImg", column = "id_img")
                        }
                )
        },
        columns = {
                @ColumnResult(name = "imagenNombre", type = String.class),
                @ColumnResult(name = "imagenTipo", type = String.class),
                @ColumnResult(name = "imagenDatos", type = byte[].class)
        }
)
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

    @Column(name = "id_img", nullable = false)
    private Integer idImg;

    @Transient
    private String imagenNombre;
    @Transient
    private String imagenTipo;
    @Transient
    private byte[] imagenDatos;

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
        return "Pescado{" +
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
