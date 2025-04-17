package data;

import jakarta.persistence.Column;

import java.time.LocalDate;

public class OfertaRequest {

    // Datos oferta
    private String ubicacion;
    private LocalDate fechaAlta;
    private LocalDate fechaBaja;
    private Integer cantidad;

    // Datos Stock
    private Long idStock;
    private String tipoStock; // Añadimos el campo para el tipo de stock

    public OfertaRequest() {}

    public OfertaRequest(String ubicacion, LocalDate fechaAlta, LocalDate fechaBaja, Integer cantidad, Long idStock, String tipoStock) {
        this.ubicacion = ubicacion;
        this.fechaAlta = fechaAlta;
        this.fechaBaja = fechaBaja;
        this.cantidad = cantidad;
        this.idStock = idStock;
        this.tipoStock = tipoStock;
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

    public Long getIdStock() {
        return idStock;
    }

    public void setIdStock(Long idStock) {
        this.idStock = idStock;
    }

    public String getTipoStock() {
        return tipoStock;
    }

    public void setTipoStock(String tipoStock) {
        this.tipoStock = tipoStock;
    }

    @Override
    public String toString() {
        return "OfertaRequest{" +
                "ubicacion='" + ubicacion + '\'' +
                ", fechaAlta=" + fechaAlta +
                ", fechaBaja=" + fechaBaja +
                ", cantidad=" + cantidad +
                ", idStock=" + idStock +
                ", tipoStock='" + tipoStock + '\'' +
                '}';
    }
}
