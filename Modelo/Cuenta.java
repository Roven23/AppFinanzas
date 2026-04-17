package Modelo;

public class Cuenta {
    private String nombre;
    private double saldo;

    public Cuenta(String nombre, double saldoInicial){
        this.nombre = nombre;
        this.saldo = saldoInicial;
    }

    public void actualizarSaldo(double monto, boolean esIngreso){
        if(esIngreso){this.saldo += monto;}
        else{this.saldo -= monto;}}


    public String getNombre() {return nombre;}
    public double getSaldo(){return saldo;}
    
    @Override
    public String toString(){
        return String.format("%s: $%.2f", nombre, saldo);
    }
}
