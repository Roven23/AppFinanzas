import Modelo.transaccion;
import Modelo.Cuenta;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class AppFinanzas {
    private static int diaPago = 27;

    public static LocalDateTime obtenerInicioCiclo() {
        LocalDateTime ahora = LocalDateTime.now();
        if (ahora.getDayOfMonth() >= diaPago) {
            // Si ya pasamos el día de pago, el ciclo empezó este mes
            return ahora.withDayOfMonth(diaPago).withHour(0).withMinute(0);
        } else {
            // Si no, el ciclo empezó el mes pasado
            return ahora.minusMonths(1).withDayOfMonth(diaPago).withHour(0).withMinute(0);
        }
    }
    public static void main(String[] args) {
        
        Scanner teclado = new Scanner(System.in);
        ArrayList<transaccion> historial = new ArrayList<>();
        ArrayList<Cuenta> misCuentas = new ArrayList<>();

        
        misCuentas.add(new Cuenta("EFECTIVO", 0.0));

        int opcion;

        do {
            System.out.println("\n========== GESTOR DE BILLETERAS ==========");
            System.out.println("1. Ver saldos totales");
            System.out.println("2. Agregar nueva billetera (Ej: Produbanco)");
            System.out.println("3. Registrar Movimiento (Ingreso/Egreso)");
            System.out.println("4. Ver historial de transacciones");
            System.out.println("5. Configurar día de pago (Actual: " + diaPago + ")");
            System.out.println("6. Ver resumen del ciclo actual");
            System.out.println("7. Transferencia entre cuentas");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = teclado.nextInt();
            teclado.nextLine(); 

            switch (opcion) {
                case 1:
                    System.out.println("\n--- TUS CUENTAS ---");
                    for (Cuenta c : misCuentas) {
                        System.out.println("- " + c.toString());
                    }
                    break;

                case 2:
                    //  AGREGAR X BILLETERA NUEVA
                    System.out.print("\nNombre de la nueva cuenta/banco: ");
                    String nombreCuenta = teclado.nextLine();
                    System.out.print("Saldo inicial en dólares: ");
                    double saldoIni = teclado.nextDouble();
                    
                    misCuentas.add(new Cuenta(nombreCuenta, saldoIni));
                    System.out.println("Billetera '" + nombreCuenta + "' agregada con éxito.");
                    break;

                case 3: // Registro de Movimiento
                    if (misCuentas.isEmpty()) {
                        System.out.println("Error: No tienes cuentas registradas. Crea una en la opción 2.");
                        break;
                    }

                    // 1. Pregunta de dónde sale o a dónde entra el dinero
                    System.out.println("\n--- SELECCIONE LA CUENTA ---");
                    for (int i = 0; i < misCuentas.size(); i++) {
                        System.out.println((i + 1) + ". " + misCuentas.get(i).getNombre() + 
                                        " (Saldo: $" + misCuentas.get(i).getSaldo() + ")");
                    }
                    System.out.print("Selección: ");
                    int indiceCuenta = teclado.nextInt() - 1;

                    // Valida que la cuenta exista
                    if (indiceCuenta < 0 || indiceCuenta >= misCuentas.size()) {
                        System.out.println("Selección inválida.");
                        break;
                    }
                    Cuenta cuentaSeleccionada = misCuentas.get(indiceCuenta);

                    // 2. Pide los datos del movimiento
                    System.out.print("Monto ($): ");
                    double montoMov = teclado.nextDouble();
                    teclado.nextLine(); // Limpiar buffer

                    System.out.print("¿Es un ingreso? (true/false): ");
                    boolean esIngresoMov = teclado.nextBoolean();
                    teclado.nextLine(); // Limpiar buffer

                    // VALIDACIÓN CRÍTICA: Si es egreso, ¿hay suficiente dinero?
                    if (!esIngresoMov && montoMov > cuentaSeleccionada.getSaldo()) {
                        System.out.println("¡Alerta! Saldo insuficiente en " + cuentaSeleccionada.getNombre());
                        System.out.print("¿Desea realizar el gasto de todos modos? (si/no): ");
                        String confirmar = teclado.nextLine();
                        if (confirmar.equalsIgnoreCase("no")) break;
                    }

                    System.out.print("Descripción: ");
                    String descMov = teclado.nextLine();

                    // 3. Ejecuta actualización
                    cuentaSeleccionada.actualizarSaldo(montoMov, esIngresoMov);
                    
                    // 4. Guarda en el historial incluyendo el nombre de la cuenta
                    String descFinal = "[" + cuentaSeleccionada.getNombre() + "] " + descMov;
                    historial.add(new transaccion(montoMov, descFinal, esIngresoMov));

                    System.out.println("Movimiento registrado correctamente en " + cuentaSeleccionada.getNombre());
                    break;

                case 4:
                    System.out.println("\n--- HISTORIAL COMPLETO ---");
                    for (transaccion t : historial) {
                        System.out.println(t.toString());
                    }
                    break;
                
                case 5:
                    System.out.print("Ingrese su nuevo día de pago (1-28): ");
                    int nuevoDia = teclado.nextInt();
                    if (nuevoDia >= 1 && nuevoDia <= 28) { // Evitamos problemas con febrero
                        diaPago = nuevoDia;
                        System.out.println("Día de pago actualizado al " + diaPago);
                    } else {
                        System.out.println("Día inválido (use 1-28 para evitar errores en febrero).");
                    }
                    break;

                case 6:
                    mostrarResumenCiclo(obtenerInicioCiclo(), historial);
                    break;

                case 7: // Opción de Transferencia entre cuentas
                    if (misCuentas.size() < 2) {
                        System.out.println("Necesitas al menos 2 cuentas para realizar una transferencia.");
                        break;
                    }

                    // 1. Elegir Origen
                    System.out.println("\n¿De qué cuenta sale el dinero?");
                    for (int i = 0; i < misCuentas.size(); i++) {
                        System.out.println((i + 1) + ". " + misCuentas.get(i).getNombre());
                    }
                    int origenIdx = teclado.nextInt() - 1;

                    // 2. Elegir Destino
                    System.out.println("\n¿A qué cuenta envía el dinero?");
                    for (int i = 0; i < misCuentas.size(); i++) {
                        if (i != origenIdx) { // No enviarse a uno mismo
                            System.out.println((i + 1) + ". " + misCuentas.get(i).getNombre());
                        }
                    }
                    int destinoIdx = teclado.nextInt() - 1;

                    // 3. Monto y ejecución
                    System.out.print("Cantidad a transferir ($): ");
                    double montoTransf = teclado.nextDouble();
                    teclado.nextLine(); 

                    if (montoTransf > 0 && montoTransf <= misCuentas.get(origenIdx).getSaldo()) {
                        Cuenta origen = misCuentas.get(origenIdx);
                        Cuenta destino = misCuentas.get(destinoIdx);

                        // Ejecutar el movimiento
                        origen.actualizarSaldo(montoTransf, false); // Resta del origen
                        destino.actualizarSaldo(montoTransf, true);  // Suma al destino

                        // Registrar en el historial para que no se pierda el rastro
                        String registroDesc = "Transferencia: " + origen.getNombre() + " -> " + destino.getNombre();
                        historial.add(new transaccion(montoTransf, registroDesc, false)); 
                        
                        System.out.println("Transferencia exitosa de " + origen.getNombre() + " a " + destino.getNombre());
                    } else {
                        System.out.println("Saldo insuficiente o monto inválido.");
                    }
                    break;

                case 0:
                    System.out.println("Cerrando aplicación...");
                    break;

                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcion != 0);

        teclado.close();
    }
    public static void mostrarResumenCiclo(LocalDateTime inicio, ArrayList<transaccion> historial) {
        LocalDateTime fin = inicio.plusMonths(1); // El corte es exactamente un mes después
        double ingresos = 0;
        double egresos = 0;

        System.out.println("\n--- REPORTE PROFESIONAL DEL CICLO ---");
        System.out.println("Desde: " + inicio.toLocalDate() + " hasta: " + fin.toLocalDate().minusDays(1));

        for (transaccion t : historial) {
            // Lógica de frontera: Incluye el inicio, excluye el inicio del siguiente ciclo
            if ((t.getFechaHora().isAfter(inicio) || t.getFechaHora().isEqual(inicio)) 
                 && t.getFechaHora().isBefore(fin)) {
                
                System.out.println(t.toString());
                if (t.isEsIngreso()) ingresos += t.getMonto();
                else egresos += t.getMonto();
            }
        }

        System.out.println("-------------------------------------");
        System.out.printf("INGRESOS DEL CICLO: $%.2f\n", ingresos);
        System.out.printf("GASTOS DEL CICLO:   $%.2f\n", egresos);
        System.out.printf("AHORRO NETO:        $%.2f\n", (ingresos - egresos));
        System.out.println("-------------------------------------");
    }
}