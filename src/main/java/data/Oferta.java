package data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Arrays;

public class Oferta {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id_oferta;

    @NotNull
    @Column(name = "lugar", nullable = false, length = 250)
    protected String lugar;

    @Column(name = "id_aprovechante", nullable = true)
    protected Long id_aprovechante;

    @NotNull
    @Column(name = "id_negocio", nullable = false)
    protected Long id_negocio;

    @NotNull
    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fecha_alta;
    @Column(name = "fecha_baja", nullable = true)
    private LocalDate fecha_baja;

    @NotNull
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fecha_vencimiento;


    @NotNull
    @Column(name = "cantidad", nullable = false)
    protected Integer cantidad;

    @NotNull
    @Column(name = "id_producto", nullable = false)
    protected Long id_producto;

    @NotNull
    @Column(name = "tipo_producto", nullable = false, length = 100)
    protected String tipo_producto;


    public void setId_oferta(Long id_oferta) {
        this.id_oferta = id_oferta;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public void setId_aprovechante(Long id_aprovechante) {
        this.id_aprovechante = id_aprovechante;
    }

    public void setId_negocio(Long id_negocio) {
        this.id_negocio = id_negocio;
    }

    public void setFecha_alta(LocalDate fecha_alta) {
        this.fecha_alta = fecha_alta;
    }

    public void setFecha_baja(LocalDate fecha_baja) {
        this.fecha_baja = fecha_baja;
    }

    public void setFecha_vencimiento(LocalDate fecha_vencimiento) {
        this.fecha_vencimiento = fecha_vencimiento;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public void setId_producto(Long id_producto) {
        this.id_producto = id_producto;
    }

    public void setTipo_producto(String tipo_producto) {
        this.tipo_producto = tipo_producto;
    }


    public Long getId_oferta() {
        return id_oferta;
    }

    public String getLugar() {
        return lugar;
    }

    public Long getId_aprovechante() {
        return id_aprovechante;
    }

    public Long getId_negocio() {
        return id_negocio;
    }

    public LocalDate getFecha_alta() {
        return fecha_alta;
    }

    public LocalDate getFecha_baja() {
        return fecha_baja;
    }

    public LocalDate getFecha_vencimiento() {
        return fecha_vencimiento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public Long getId_producto() {
        return id_producto;
    }

    public String getTipo_producto() {
        return tipo_producto;
    }

    @Override
    public String toString() {
        return "Oferta{" +
                "id_oferta=" + id_oferta +
                ", lugar='" + lugar + '\'' +
                ", id_aprovechante=" + id_aprovechante +
                ", id_negocio=" + id_negocio +
                ", fecha_alta=" + fecha_alta +
                ", fecha_baja=" + fecha_baja +
                ", fecha_vencimiento=" + fecha_vencimiento +
                ", cantidad=" + cantidad +
                ", id_producto=" + id_producto +
                ", tipo_producto='" + tipo_producto + '\'' +
                '}';
    }
}
