package Modelo;
import java.time.LocalDateTime;
import java.io.Serializable;

public class transaccion implements Serializable {
    private static final long serialVersionUID = 1L; // Para la serialización
    private double monto;
    private String descripcion;
    private String categoria;
    private LocalDateTime fechaHora;
    private boolean esIngreso; // true si es ingreso, false si es egreso
    private String nombreCuenta;

    public transaccion(double monto, String descripcion, String categoria, boolean esIngreso, String nombreCuenta) {
        this.monto = monto;
        this.descripcion = (descripcion == null || descripcion.trim().isEmpty()) ? "---" : descripcion;
        this.categoria = categoria;
        this.esIngreso = esIngreso;
        this.nombreCuenta = nombreCuenta;
        
        this.fechaHora = LocalDateTime.now(); //toma la fecha y hora automaticamente
    }

    // Getters
    public double getMonto() { return monto; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public boolean isEsIngreso() { return esIngreso; }
    public String getCategoria() { return categoria; }  
    public String getNombreCuenta() { return nombreCuenta; }

    @Override
public String toString() {
    // Definimos si es un (+) o un (-) para que visualmente se entienda rápido
    String simbolo = esIngreso ? "[+]" : "[-]";
    
    
    String fechaFormateada = fechaHora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm"));

    return String.format("%s %s | %-12s | %-15s | $%.2f" + " | %s", 

            simbolo, 
            fechaFormateada, 
            categoria.toUpperCase(), // La categoría en mayúsculas para que resalte
            descripcion, 
            monto,
            nombreCuenta);
}
}