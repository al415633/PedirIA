package data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@NamedQueries({
        @NamedQuery(name="Comercio.findAll", query = "SELECT c FROM ComercioDetails c"),
        @NamedQuery(name = "Comercio.findByCorreo", query = "SELECT c FROM ComercioDetails c WHERE c.correo = :correo")
})


public class ComercioDetails {

    @Id
    String correo;
    String password;
    String nombre;


    String tipo;
    String diaCompraDeStock;






    @Override
    public String toString() {
        return "ComercioDetails{" +
                "correo='" + correo + '\'' +
                ", password='" + password + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", diaCompraDeStock='" + diaCompraDeStock + '\'' +
                '}';
    }




    public String getCorreo() {
        return correo;
    }


    public void setCorreo(String email) {
        this.correo = email;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getTipo() {
        return tipo;
    }


    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    public String getDiaCompraDeStock() {
        return diaCompraDeStock;
    }


    public void setDiaCompraDeStock(String diaCompraDeStock) {
        this.diaCompraDeStock = diaCompraDeStock;
    }

}
