package data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "StockPescado")
public class StockPescado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_stock_pescado", columnDefinition = "serial")
    private Long id;

    @NotNull
    @Min(0)
    @Column(name = "cantidad", nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidad;

    @NotNull
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @NotNull
    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @ManyToOne
    @JoinColumn(name = "id_pescado", nullable = false)
    private Pescado pescado;

    // Constructores
    public StockPescado() {}

    public StockPescado(BigDecimal cantidad, LocalDate fechaVencimiento, LocalDate fechaIngreso, Pescado pescado) {
        this.cantidad = cantidad;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaIngreso = fechaIngreso;
        this.pescado = pescado;
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

    public Pescado getPescado() {
        return pescado;
    }

    public void setPescado(Pescado pescado) {
        this.pescado = pescado;
    }

    @Override
    public String toString() {
        return "StockPescado{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", fechaVencimiento=" + fechaVencimiento +
                ", fechaIngreso=" + fechaIngreso +
                ", pescado=" + pescado +
                '}';
    }
}
