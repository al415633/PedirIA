package data;

import data.Usuario;
import jakarta.persistence.*;

@Entity
@Table(name = "negocio")
public class ComercioDetails {

    @Id
    @Column(name = "id_negocio")
    private Long idNegocio; //El mismo ID que el de Usuario

    @Column(nullable = false)
    private String nombre;

    @Column(name = "dia")
    private String diaCompraDeStock;

    @OneToOne
    @MapsId // Indica que usa el mismo ID que Usuario
    @JoinColumn(name = "id_negocio")
    private Usuario usuario;

    @Override
    public String toString() {
        return "ComercioDetails{" +
                "idNegocio=" + idNegocio +
                ", nombre='" + nombre + '\'' +
                ", diaCompraDeStock='" + diaCompraDeStock + '\'' +
                ", usuario=" + usuario +
                '}';
    }

    public Long getIdNegocio() {
        return idNegocio;
    }

    public void setIdNegocio(Long idNegocio) {
        this.idNegocio = idNegocio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDiaCompraDeStock() {
        return diaCompraDeStock;
    }

    public void setDiaCompraDeStock(String diaCompraDeStock) {
        this.diaCompraDeStock = diaCompraDeStock;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
