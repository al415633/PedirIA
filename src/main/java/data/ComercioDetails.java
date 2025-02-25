package data;

public class ComercioDetails {

    String email;
    String password;
    String nombre;


    String tipo;
    String diaCompraDeStock;






    @Override
    public String toString() {
        return "ComercioDetails{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", diaCompraDeStock='" + diaCompraDeStock + '\'' +
                '}';
    }




    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
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
