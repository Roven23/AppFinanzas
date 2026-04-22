package Servicio;

import Modelo.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

public class FinanzasService {

    // 1. METODOS DE INTERFAZ BASICA
    public static void mostrarMenu() {
        System.out.println("\n========= GESTION FINANCIERA =========");
        System.out.println("1. Ver Saldos de Cuentas");
        System.out.println("2. Crear Nueva Cuenta");
        System.out.println("3. Registrar Movimiento (Ingreso/Egreso)");
        System.out.println("4. Ver Historial General");
        System.out.println("5. Configurar Dia de Pago");
        System.out.println("6. Ver Dashboard del Ciclo");
        System.out.println("7. Transferencias (Metas de Ahorro)");
        System.out.println("0. Salir y Guardar");
        System.out.println("======================================");
        System.out.print("Seleccione una opcion: ");
    }

    public static ArrayList<Categoria> cargarCategoriasIniciales() {
        ArrayList<Categoria> lista = new ArrayList<>();
        lista.add(new Categoria("SUELDO", true));
        lista.add(new Categoria("COMIDA", false));
        lista.add(new Categoria("TRANSPORTE", false));
        lista.add(new Categoria("SERVICIOS", false));
        return lista;
    }

    // 2. GESTION DE CUENTAS Y MOVIMIENTOS
    public static void mostrarSaldos(ArrayList<Cuenta> cuentas) {
        if (cuentas.isEmpty()) {
            System.out.println("No hay cuentas registradas.");
            return;
        }
        System.out.println("\n--- ESTADO DE CUENTAS ---");
        for (Cuenta c : cuentas) {
            System.out.println("- " + c.getNombre() + ": $" + c.getSaldo());
        }
    }

    public static void crearCuenta(ArrayList<Cuenta> cuentas, Scanner teclado) {
        System.out.print("Nombre de la cuenta (ej: Pichincha, [AHORRO]): ");
        String nombre = teclado.nextLine();
        System.out.print("Saldo inicial: ");
        double saldo = Double.parseDouble(teclado.nextLine());
        cuentas.add(new Cuenta(nombre, saldo));
        System.out.println("Cuenta creada exitosamente.");
    }

    public static void registrarMovimiento(ArrayList<Cuenta> cuentas, ArrayList<Categoria> categorias, ArrayList<transaccion> historial, Scanner teclado) {
        if (cuentas.isEmpty()) {
            System.out.println("Error: Debe crear una cuenta primero.");
            return;
        }

        // Seleccion de Cuenta
        System.out.println("\n--- SELECCIONE CUENTA ---");
        for (int i = 0; i < cuentas.size(); i++) {
            System.out.println((i + 1) + ". " + cuentas.get(i).getNombre());
        }
        int idxCuenta = Integer.parseInt(teclado.nextLine()) - 1;
        Cuenta cuentaSel = cuentas.get(idxCuenta);

        // Seleccion de Categoria
        System.out.println("\n--- SELECCIONE CATEGORIA ---");
        for (int i = 0; i < categorias.size(); i++) {
            System.out.println((i + 1) + ". " + categorias.get(i).getNombre());
        }
        System.out.println((categorias.size() + 1) + ". [ + Crear nueva ]");
        int idxCat = Integer.parseInt(teclado.nextLine()) - 1;

        Categoria catSel;
        if (idxCat == categorias.size()) {
            System.out.print("Nombre nueva categoria: ");
            String n = teclado.nextLine();
            System.out.print("Es de ingreso? (true/false): ");
            boolean t = Boolean.parseBoolean(teclado.nextLine());
            catSel = new Categoria(n, t);
            categorias.add(catSel);
        } else {
            catSel = categorias.get(idxCat);
        }

        System.out.print("Monto: ");
        double monto = Double.parseDouble(teclado.nextLine());
        System.out.print("Descripcion (Opcional): ");
        String desc = teclado.nextLine();

        // Ejecucion
        cuentaSel.actualizarSaldo(monto, catSel.isEsIngreso());
        String descFull = "[" + cuentaSel.getNombre() + "] " + desc;
        historial.add(new transaccion(monto, descFull, catSel.getNombre(), catSel.isEsIngreso()));
        System.out.println("Movimiento registrado.");
    }

    public static void mostrarHistorialGeneral(ArrayList<transaccion> historial) {
        if (historial.isEmpty()) {
            System.out.println("Historial vacio.");
            return;
        }
        System.out.println("\n--- HISTORIAL DE TRANSACCIONES ---");
        for (transaccion t : historial) {
            System.out.println(t.getFechaHora().toLocalDate() + " | " + t.getCategoria() + " | " + t.getDescripcion() + " | $" + t.getMonto());
        }
    }

    public static void realizarTransferencia(ArrayList<Cuenta> cuentas, ArrayList<transaccion> historial, Scanner teclado) {
        if (cuentas.size() < 2) {
            System.out.println("Necesita al menos 2 cuentas para transferir.");
            return;
        }
        System.out.println("Seleccione origen (1-" + cuentas.size() + "): ");
        int or = Integer.parseInt(teclado.nextLine()) - 1;
        System.out.println("Seleccione destino (1-" + cuentas.size() + "): ");
        int des = Integer.parseInt(teclado.nextLine()) - 1;
        System.out.print("Monto a transferir: ");
        double m = Double.parseDouble(teclado.nextLine());

        cuentas.get(or).actualizarSaldo(m, false);
        cuentas.get(des).actualizarSaldo(m, true);

        String d = "De " + cuentas.get(or).getNombre() + " a " + cuentas.get(des).getNombre();
        historial.add(new transaccion(m, d, "TRANSFERENCIA", false));
        System.out.println("Transferencia realizada.");
    }

    // 3. LOGICA DE CALENDARIO Y CICLO
    public static int configurarDiaPago(Scanner teclado) {
        java.time.YearMonth mesActual = java.time.YearMonth.now();
        int diasMax = mesActual.lengthOfMonth();
        int dia;
        do {
            System.out.print("Ingrese dia de pago (1-" + diasMax + "): ");
            dia = Integer.parseInt(teclado.nextLine());
        } while (dia < 1 || dia > diasMax);
        return dia;
    }

    public static LocalDateTime obtenerInicioCiclo(int diaPago) {
        LocalDateTime ahora = LocalDateTime.now();
        java.time.YearMonth mesAct = java.time.YearMonth.now();
        int diaAju = Math.min(diaPago, mesAct.lengthOfMonth());
        
        if (ahora.getDayOfMonth() >= diaAju) {
            return ahora.withDayOfMonth(diaAju).withHour(0).withMinute(0).withSecond(0);
        } else {
            java.time.YearMonth mesPas = mesAct.minusMonths(1);
            int diaAjuPas = Math.min(diaPago, mesPas.lengthOfMonth());
            return ahora.minusMonths(1).withDayOfMonth(diaAjuPas).withHour(0).withMinute(0).withSecond(0);
        }
    }

    // 4. LOGICA DE DASHBOARD ANALITICO
    public static void mostrarResumenCiclo(LocalDateTime inicio, ArrayList<transaccion> historial, ArrayList<Cuenta> misCuentas) {
        LocalDateTime fin = inicio.plusMonths(1);
        double ingresosMes = 0, egresosMes = 0, sueldoDet = 0, saldoAhorros = 0, deudaAhorro = 0;
        Map<String, Double> gastosCat = new HashMap<>();

        for (Cuenta c : misCuentas) {
            if (c.getNombre().toUpperCase().contains("[AHORRO]")) saldoAhorros += c.getSaldo();
        }

        for (transaccion t : historial) {
            if (!t.getFechaHora().isBefore(inicio) && t.getFechaHora().isBefore(fin)) {
                if (t.getCategoria().equalsIgnoreCase("SUELDO") && t.isEsIngreso()) sueldoDet += t.getMonto();
                
                if (t.getCategoria().equalsIgnoreCase("TRANSFERENCIA")) {
                    if (t.getDescripcion().toUpperCase().contains("DE [AHORRO]")) deudaAhorro += t.getMonto();
                    continue;
                }

                if (t.isEsIngreso()) {
                    ingresosMes += t.getMonto();
                } else {
                    egresosMes += t.getMonto();
                    gastosCat.put(t.getCategoria(), gastosCat.getOrDefault(t.getCategoria(), 0.0) + t.getMonto());
                }
            }
        }

        System.out.println("\n=========================================");
        System.out.println("   DASHBOARD ESTRATEGICO AUTOMATIZADO");
        System.out.println("   Periodo: " + inicio.toLocalDate() + " al " + fin.toLocalDate().minusDays(1));
        System.out.println("=========================================");
        System.out.printf(" SUELDO DETECTADO:     $%.2f\n", sueldoDet);
        System.out.printf(" (+) Otros Ingresos:   $%.2f\n", (ingresosMes - sueldoDet));
        System.out.printf(" (-) Gastos del Mes:   $%.2f\n", egresosMes);
        System.out.printf(" (=) Sobrante Ciclo:   $%.2f\n", (ingresosMes - egresosMes));
        System.out.println(" ---------------------------------------");
        System.out.printf(" SALDO EN AHORROS:     $%.2f\n", saldoAhorros);
        if (deudaAhorro > 0) System.out.printf(" DEUDA POR REPONER:   -$%.2f\n", deudaAhorro);
        System.out.printf(" PATRIMONIO TOTAL:     $%.2f\n", (saldoAhorros + (ingresosMes - egresosMes)));
        System.out.println(" ---------------------------------------");

        if (sueldoDet > 0) {
            System.out.println("\n DESGLOSE DE GASTOS (% DEL SUELDO):");
            for (Map.Entry<String, Double> entry : gastosCat.entrySet()) {
                double porc = (entry.getValue() / sueldoDet) * 100;
                String barra = "";
                for (int i = 0; i < (int)(porc/2); i++) barra += "#";
                System.out.printf("%-15s [%-20s] %3.1f%% ($%.2f)\n", entry.getKey(), barra, porc, entry.getValue());
            }
        }
        System.out.println("=========================================\n");
    }
}