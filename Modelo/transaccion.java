package Modelo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class transaccion {
    private double monto;
    private String descripcion;
    private LocalDateTime fechaHora;
    private boolean esIngreso; // true si es ingreso, false si es egreso

    public transaccion(double monto, String descripcion, boolean esIngreso) {
        this.monto = monto;
        this.descripcion = descripcion;
        this.esIngreso = esIngreso;
        
        this.fechaHora = LocalDateTime.now(); //toma la fecha y hora automaticamente
    }

    // Getters
    public double getMonto() { return monto; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public boolean isEsIngreso() { return esIngreso; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String tipo = esIngreso ? "[INGRESO]" : "[EGRESO]";
        // String.format("%.2f") asegura los dos decimales al mostrar el dato
        return String.format("%s %s - %s: $%.2f", 
                fechaHora.format(formatter), tipo, descripcion, monto);
    }
}