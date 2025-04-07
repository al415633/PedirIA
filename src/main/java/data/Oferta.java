package data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Oferta")
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_oferta")
    private Long id;

    @Column(name = "ubicacion", nullable = false)
    private String ubicacion;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "id_aprovechante")
    private AprovechanteDetails aprovechante;

    @ManyToOne
    @JoinColumn(name = "id_negocio", nullable = false)
    private ComercioDetails negocio;

    @OneToOne(mappedBy = "oferta", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProductoOferta productoOferta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public LocalDate getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(LocalDate fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public AprovechanteDetails getAprovechante() {
        return aprovechante;
    }

    public void setAprovechante(AprovechanteDetails aprovechante) {
        this.aprovechante = aprovechante;
    }

    public ComercioDetails getNegocio() {
        return negocio;
    }

    public void setNegocio(ComercioDetails negocio) {
        this.negocio = negocio;
    }

    public ProductoOferta getProductoOferta() {
        return productoOferta;
    }

    public void setProductoOferta(ProductoOferta productoOferta) {
        this.productoOferta = productoOferta;
    }

    @Override
    public String toString() {
        return "Oferta{" +
                "id=" + id +
                ", ubicacion='" + ubicacion + '\'' +
                ", fechaAlta=" + fechaAlta +
                ", fechaBaja=" + fechaBaja +
                ", cantidad=" + cantidad +
                ", aprovechante=" + aprovechante +
                ", negocio=" + negocio +
                ", productoOferta=" + productoOferta +
                '}';
    }
}
