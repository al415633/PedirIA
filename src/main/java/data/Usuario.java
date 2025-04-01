package data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Se autoincrementa
    private Long id_usuario;


    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String tipo; //Valores: Negocio o Aprovechante

/*    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    private ComercioDetails negocio;*/

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private ComercioDetails negocio;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private AprovechanteDetails aprovechante;

    @Override
    public String toString() {
        return "Usuario{" +
                "id_usuario=" + id_usuario +
                ", correo='" + correo + '\'' +
                ", password='" + password + '\'' +
                ", tipo='" + tipo + '\'' +

                '}';
    }

    public Long getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Long id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public ComercioDetails getNegocio() {
        return negocio;
    }

    public void setNegocio(ComercioDetails negocio) {
        this.negocio = negocio;
    }

    public AprovechanteDetails getAprovechante() {
        return aprovechante;
    }

    public void setAprovechante(AprovechanteDetails aprovechante) {
        this.aprovechante = aprovechante;
    }
}
