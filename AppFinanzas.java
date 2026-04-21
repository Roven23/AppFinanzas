import Modelo.transaccion;
import Modelo.Cuenta;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

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
        ArrayList<String> categorias = new ArrayList<>(Arrays.asList("Comida", "Transporte", "Facultad", "Sueldo", "Gym"));

        
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
            System.out.println("8. Crear Meta de Ahorro");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = teclado.nextInt();
            teclado.nextLine(); 

            switch (opcion) {
                case 1:
                    double totalDisponible = 0;
                    double totalAhorrado = 0;

                    System.out.println("\n========== ESTADO FINANCIERO ==========");
                    
                    System.out.println("\n--- CUENTAS DISPONIBLES ---");
                    for (Cuenta c : misCuentas) {
                        if (!c.getNombre().contains("[AHORRO]")) {
                            System.out.println("- " + c.toString());
                            totalDisponible += c.getSaldo();
                        }
                    }

                    System.out.println("\n--- TUS AHORROS ---");
                    for (Cuenta c : misCuentas) {
                        if (c.getNombre().contains("[AHORRO]")) {
                            System.out.println("- " + c.toString());
                            totalAhorrado += c.getSaldo();
                        }
                    }

                    System.out.println("\n---------------------------------------");
                    System.out.printf("DISPONIBLE TOTAL: $%.2f\n", totalDisponible);
                    System.out.printf("AHORRO TOTAL:     $%.2f\n", totalAhorrado);
                    System.out.printf("PATRIMONIO NETO:  $%.2f\n", (totalDisponible + totalAhorrado));
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
        System.out.println("❌ Error: No tienes cuentas registradas. Crea una en la opción 2.");
        break;
    }

    // 1. Selección de Cuenta
    System.out.println("\n--- SELECCIONE LA CUENTA ---");
    for (int i = 0; i < misCuentas.size(); i++) {
        System.out.println((i + 1) + ". " + misCuentas.get(i).getNombre() + 
                           " (Saldo: $" + misCuentas.get(i).getSaldo() + ")");
    }
    System.out.print("Selección: ");
    int indiceCuenta = teclado.nextInt() - 1;

    if (indiceCuenta < 0 || indiceCuenta >= misCuentas.size()) {
        System.out.println("❌ Selección inválida.");
        break;
    }
    Cuenta cuentaSeleccionada = misCuentas.get(indiceCuenta);

    // 2. SELECCIÓN O CREACIÓN DE CATEGORÍA
    System.out.println("\n--- SELECCIONE CATEGORÍA ---");
    for (int i = 0; i < categorias.size(); i++) {
        System.out.println((i + 1) + ". " + categorias.get(i));
    }
    System.out.println((categorias.size() + 1) + ". [ + Crear nueva categoría ]");
    
    System.out.print("Selección: ");
    int indiceCat = teclado.nextInt() - 1;
    teclado.nextLine(); // Limpiar buffer importante para strings

    String categoriaElegida;
    if (indiceCat == categorias.size()) { // Opción de crear nueva
        System.out.print("Nombre de la nueva categoría: ");
        categoriaElegida = teclado.nextLine();
        categorias.add(categoriaElegida);
        System.out.println("✔ Categoría '" + categoriaElegida + "' añadida.");
    } else if (indiceCat >= 0 && indiceCat < categorias.size()) {
        categoriaElegida = categorias.get(indiceCat);
    } else {
        System.out.println("❌ Opción inválida.");
        break;
    }

    // 3. Monto y Tipo de movimiento
    System.out.print("Monto ($): ");
    double montoMov = teclado.nextDouble();
    
    System.out.print("¿Es un ingreso? (true/false): ");
    boolean esIngresoMov = teclado.nextBoolean();
    teclado.nextLine(); // Limpiar buffer

    // Validación de saldo para gastos
    if (!esIngresoMov && montoMov > cuentaSeleccionada.getSaldo()) {
        System.out.println("⚠️ ¡Alerta! Saldo insuficiente en " + cuentaSeleccionada.getNombre());
        System.out.print("¿Desea realizar el gasto de todos modos? (si/no): ");
        if (teclado.nextLine().equalsIgnoreCase("no")) break;
    }

    // 4. Descripción (Opcional)
    System.out.print("Descripción (Opcional - Enter para saltar): ");
    String descMov = teclado.nextLine();

    // 5. Ejecución y Guardado en Historial
    cuentaSeleccionada.actualizarSaldo(montoMov, esIngresoMov);
    
    // El orden del constructor es: monto, descripción, categoría, esIngreso
    String descConCuenta = "[" + cuentaSeleccionada.getNombre() + "] " + descMov;
    historial.add(new transaccion(montoMov, descConCuenta, categoriaElegida, esIngresoMov));

    System.out.println("✔ Movimiento registrado correctamente en " + cuentaSeleccionada.getNombre());
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
                    mostrarResumenCiclo(obtenerInicioCiclo(), historial, misCuentas);
                    break;

                case 7: // Opción de Transferencia entre cuentas
    if (misCuentas.size() < 2) {
        System.out.println("❌ Necesitas al menos 2 cuentas para realizar una transferencia.");
        break;
    }

    // 1. Elegir Origen
    System.out.println("\n¿De qué cuenta sale el dinero?");
    for (int i = 0; i < misCuentas.size(); i++) {
        System.out.println((i + 1) + ". " + misCuentas.get(i).getNombre() + " ($" + misCuentas.get(i).getSaldo() + ")");
    }
    int origenIdx = teclado.nextInt() - 1;

    // Validar selección de origen
    if (origenIdx < 0 || origenIdx >= misCuentas.size()) {
        System.out.println("❌ Selección inválida.");
        break;
    }

    // 2. Elegir Destino
    System.out.println("\n¿A qué cuenta envía el dinero?");
    for (int i = 0; i < misCuentas.size(); i++) {
        if (i != origenIdx) { // No enviarse a uno mismo
            System.out.println((i + 1) + ". " + misCuentas.get(i).getNombre());
        }
    }
    int destinoIdx = teclado.nextInt() - 1;

    // Validar selección de destino
    if (destinoIdx < 0 || destinoIdx >= misCuentas.size() || destinoIdx == origenIdx) {
        System.out.println("❌ Selección de destino inválida.");
        break;
    }

    // 3. Monto y ejecución
    System.out.print("Cantidad a transferir ($): ");
    double montoTransf = teclado.nextDouble();
    teclado.nextLine(); // Limpiar buffer

    if (montoTransf > 0 && montoTransf <= misCuentas.get(origenIdx).getSaldo()) {
        Cuenta origen = misCuentas.get(origenIdx);
        Cuenta destino = misCuentas.get(destinoIdx);

        // Ejecutar el movimiento en los objetos Cuenta
        origen.actualizarSaldo(montoTransf, false); // Resta del origen
        destino.actualizarSaldo(montoTransf, true);  // Suma al destino
        
        // 4. REGISTRO EN HISTORIAL (Corregido para el nuevo constructor)
        // Orden: monto, descripción, categoría, esIngreso
        String registroDesc = "De " + origen.getNombre() + " a " + destino.getNombre();
        
        historial.add(new transaccion(montoTransf, registroDesc, "TRANSFERENCIA", false)); 
        
        System.out.println("✔ Transferencia exitosa de " + origen.getNombre() + " a " + destino.getNombre());
    } else {
        System.out.println("❌ Saldo insuficiente o monto inválido.");
    }
    break;
                
                case 8: // Nueva opción: Crear Meta de Ahorro
                    System.out.print("\n¿Qué nombre le pondrás a este ahorro? (Ej: Fondo de Emergencia, Viaje): ");
                    String nombreAhorro = teclado.nextLine();
                    
                    // Creamos la cuenta con saldo 0 al inicio
                    Cuenta nuevaMeta = new Cuenta("[AHORRO] " + nombreAhorro, 0.0);
                    misCuentas.add(nuevaMeta);
                    
                    System.out.println("✔ Meta '" + nombreAhorro + "' creada. ¡Ahora puedes transferirle dinero!");
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
        public static void mostrarResumenCiclo(LocalDateTime inicio, ArrayList<transaccion> historial, ArrayList<Cuenta> misCuentas) {
    LocalDateTime fin = inicio.plusMonths(1);
    double ingresosMes = 0;
    double egresosMes = 0;
    double sueldoDetectado = 0; // Variable para el cálculo de porcentajes
    double saldoAhorros = 0;
    double deudaConAhorro = 0;
    
    java.util.Map<String, Double> gastosPorCategoria = new java.util.HashMap<>();

    // 1. Obtener saldo de cuentas marcadas como [AHORRO]
    for (Cuenta c : misCuentas) {
        if (c.getNombre().toUpperCase().contains("[AHORRO]")) {
            saldoAhorros += c.getSaldo();
        }
    }

    // 2. Escanear Historial
    for (transaccion t : historial) {
        if ((t.getFechaHora().isAfter(inicio) || t.getFechaHora().isEqual(inicio)) 
             && t.getFechaHora().isBefore(fin)) {
            
            // Lógica para detectar el Sueldo Automáticamente
            if (t.getCategoria().equalsIgnoreCase("SUELDO") && t.isEsIngreso()) {
                sueldoDetectado += t.getMonto();
            }

            // Ignorar transferencias para el flujo de caja, pero detectar deuda
            // ... dentro del bucle for de transacciones ...

if (t.getCategoria().equalsIgnoreCase("TRANSFERENCIA")) {
    
    // Solo contamos como DEUDA si el dinero SALIÓ de un ahorro hacia otro lado
    // Buscamos que la descripción empiece con "De [AHORRO]"
    if (t.getDescripcion().toUpperCase().contains("DE [AHORRO]")) {
        deudaConAhorro += t.getMonto();
    }
    
    // Las transferencias no afectan el cálculo de ingresos/gastos mensuales
    continue; 
}

            // Sumar ingresos y egresos generales del mes
            if (t.isEsIngreso()) {
                ingresosMes += t.getMonto();
            } else {
                egresosMes += t.getMonto();
                String cat = t.getCategoria();
                gastosPorCategoria.put(cat, gastosPorCategoria.getOrDefault(cat, 0.0) + t.getMonto());
            }
        }
    }

    double sobranteMes = ingresosMes - egresosMes;
    double ahorroMasSobrante = saldoAhorros + sobranteMes;

    // --- INTERFAZ DEL DASHBOARD ---
    System.out.println("\n=========================================");
    System.out.println("   📊 DASHBOARD ESTRATÉGICO AUTOMATIZADO");
    System.out.println("   Periodo: " + inicio.toLocalDate() + " al " + fin.toLocalDate().minusDays(1));
    System.out.println("=========================================");

    System.out.printf(" 💰 SUELDO DETECTADO:    $%.2f\n", sueldoDetectado);
    System.out.printf(" (+) Otros Ingresos:     $%.2f\n", (ingresosMes - sueldoDetectado));
    System.out.printf(" (-) Gastos del Mes:     $%.2f\n", egresosMes);
    System.out.printf(" (=) Sobrante del Ciclo: $%.2f\n", sobranteMes);
    System.out.println(" ---------------------------------------");
    
    System.out.printf(" 🏦 SALDO EN AHORROS:    $%.2f\n", saldoAhorros);
    if (deudaConAhorro > 0) {
        System.out.printf(" ⚠️ DEUDA POR REPORNER: -$%.2f\n", deudaConAhorro);
    }
    System.out.println(" ---------------------------------------");
    System.out.printf(" 💎 PATRIMONIO TOTAL:    $%.2f\n", ahorroMasSobrante);
    System.out.println(" ---------------------------------------");

    // --- GRÁFICO RESPECTO AL SUELDO DETECTADO ---
    if (sueldoDetectado > 0) {
        System.out.println("\n DESGLOSE DE GASTOS (% DEL SUELDO):");
        for (java.util.Map.Entry<String, Double> entry : gastosPorCategoria.entrySet()) {
            double monto = entry.getValue();
            double porcSueldo = (monto / sueldoDetectado) * 100;
            
            int numBarras = (int) (porcSueldo / 2); // 1 bloque por cada 2%
            String barra = "";
            for (int i = 0; i < numBarras; i++) barra += "■";
            
            System.out.printf("%-15s [%-20s] %3.1f%% ($%.2f)\n", 
                              entry.getKey(), barra, porcSueldo, monto);
        }
    } else {
        System.out.println("\n [!] No se detectaron ingresos bajo la categoría 'SUELDO'.");
        System.out.println("     Registre su sueldo para ver los porcentajes.");
    }
    System.out.println("=========================================\n");
}
}