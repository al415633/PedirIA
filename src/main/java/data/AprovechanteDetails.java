package data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import data.Usuario;
import jakarta.persistence.*;

@Entity
@Table(name = "aprovechante")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "idAprovechante"
)
public class AprovechanteDetails {

    @Id
    @Column(name = "id_usuario")
    private Long idAprovechante; //El mismo ID que el de Usuario

    @Column(nullable = false)
    private String condiciones;

    @Column(nullable = false)
    private String condiciones2;


    @Column(name = "tipo_aprov", nullable = false)
    private String tipo_aprovechante;

    @OneToOne
    @MapsId // Indica que usa el mismo ID que Usuario
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Override
    public String toString() {
        return "AprovechanteDetails{" +
                "idAprovechante=" + idAprovechante +
                ", correo='" + usuario.getCorreo() + '\'' +
                ", condiciones='" + condiciones + '\'' +
                ", condiciones2='" + condiciones2 + '\'' +
                ", tipo_aprovechante='" + tipo_aprovechante + '\'' +
                '}';
    }

    public Long getIdAprovechante() {
        return idAprovechante;
    }

    public void setIdAprovechante(Long idAprovechante) {
        this.idAprovechante = idAprovechante;
    }

    public String getCondiciones() {
        return condiciones;
    }

    public void setCondiciones(String condiciones) {
        this.condiciones = condiciones;
    }

    public String getCondiciones2() {
        return condiciones2;
    }

    public void setCondiciones2(String condiciones2) {
        this.condiciones2 = condiciones2;
    }

    public String getTipo_aprovechante() {
        return tipo_aprovechante;
    }

    public void setTipo_aprovechante(String tipo_aprovechante) {
        this.tipo_aprovechante = tipo_aprovechante;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }


}
