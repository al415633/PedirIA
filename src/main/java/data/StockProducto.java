package data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@MappedSuperclass
public abstract class StockProducto<T extends Producto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_stock", columnDefinition = "serial")
    protected Long id;

    @NotNull
    @Min(0)
    @Column(name = "cantidad", nullable = false, precision = 10, scale = 3)
    protected BigDecimal cantidad;

    @NotNull
    @Column(name = "fecha_vencimiento", nullable = false)
    protected LocalDate fechaVencimiento;

    @NotNull
    @Column(name = "fecha_ingreso", nullable = false)
    protected LocalDate fechaIngreso;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    protected T producto;

    // Constructores
    public StockProducto() {}

    public StockProducto(BigDecimal cantidad, LocalDate fechaVencimiento, LocalDate fechaIngreso, T producto) {
        this.cantidad = cantidad;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaIngreso = fechaIngreso;
        this.producto = producto;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public T getProducto() { return producto; }
    public void setProducto(T producto) { this.producto = producto; }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", fechaVencimiento=" + fechaVencimiento +
                ", fechaIngreso=" + fechaIngreso +
                ", producto=" + producto +
                '}';
    }
}
