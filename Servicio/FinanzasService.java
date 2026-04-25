package Servicio;

import Modelo.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

public class FinanzasService {

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
    
    private static double leerMontoSeguro(Scanner teclado) {
        while (true) {
            try {
                String entrada = teclado.nextLine().replace(',', '.').trim();
                double monto = Double.parseDouble(entrada);
                if (monto < 0) {
                    System.out.print("Error: El monto no puede ser negativo. Ingrese nuevamente: ");
                    continue;
                } return monto;    
            } catch (NumberFormatException e) {
                System.out.print("Error: Ingrese un valor numerico ");
            }
        }
    }

    public static ArrayList<Categoria> cargarCategoriasIniciales() {
        ArrayList<Categoria> lista = new ArrayList<>();
        lista.add(new Categoria("SUELDO", true));
        lista.add(new Categoria("COMIDA", false));
        lista.add(new Categoria("TRANSPORTE", false));
        lista.add(new Categoria("SERVICIOS", false));
        return lista;
    }

    public static void crearCuenta(ArrayList<Cuenta> cuentas, Scanner teclado) {

        System.out.print("Ingrese el nombre de la cuenta: ");
        String nombre = teclado.nextLine().trim();

        if(nombre.isEmpty()) {
            System.out.println("Error: El nombre de la cuenta no puede estar vacio.");
            return;
        }

        for (Cuenta c: cuentas ){
            if (c.getNombre().equalsIgnoreCase(nombre)) {
                System.out.println("Error: Ya existe una cuenta con ese nombre.");
                return;
            } 
        }
        
        System.out.print("Saldo inicial: ");
        double saldo = leerMontoSeguro(teclado);
        cuentas.add(new Cuenta(nombre, saldo));
        System.out.println("Cuenta " + nombre +"creada exitosamente.");
    }

    public static void mostrarSaldos(ArrayList<Cuenta> cuentas) {
        if (cuentas.isEmpty()) {
            System.out.println("No hay cuentas registradas.");
            return;
        }
        System.out.println("\n--- ESTADO DE CUENTAS ---");
        for (Cuenta c : cuentas) {
            System.out.printf("- %20s: $%.2f\n", c.getNombre(), c.getSaldo());
        }
    }

    public static void registrarMovimiento(ArrayList<Cuenta> cuentas, ArrayList<Categoria> categorias, ArrayList<transaccion> historial, Scanner teclado) {
        
        if (cuentas.isEmpty()) {
            System.out.println("Error: Cree una cuenta primero.");
            return;
        }
        Cuenta cuentaSel = null;
        try {
        System.out.println("\n--- SELECCIONE CUENTA ---");
        for (int i = 0; i < cuentas.size(); i++) {
            System.out.printf("%d. %s ($%.2f)\n", (i + 1), cuentas.get(i).getNombre(), cuentas.get(i).getSaldo());
        }
        int idxCuenta = Integer.parseInt(teclado.nextLine()) - 1;
        if(idxCuenta < 0 || idxCuenta >= cuentas.size()) {
            System.out.println("Error: Seleccione una cuenta valida.");
            return;
        }
        cuentaSel = cuentas.get(idxCuenta);
        } catch (NumberFormatException e) {
                System.out.print("Error: Ingrese un valor numerico ");
                return;
            }


            //no te permite si no hay cuenta creaa
//eleccion e cuenta (cuenta no valia, no es numero)
//lo mismo en categoria
//categoria ya existe
//valiar que solo escriba true o false
//valiar monto valio
//poner el movimiento en qeu cuenta se hace
        System.out.println("\n--- SELECCIONE CATEGORIA ---");
        for (int i = 0; i < categorias.size(); i++) {
            System.out.println((i + 1) + ". " + categorias.get(i).getNombre());}
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
            catSel = categorias.get(idxCat);}
        System.out.print("Monto: ");
        double monto = leerMontoSeguro(teclado);
        System.out.print("Descripcion (Opcional): ");
        String desc = teclado.nextLine();
        cuentaSel.actualizarSaldo(monto, catSel.isEsIngreso());
        String descFull = "[" + cuentaSel.getNombre() + "] " + desc;
        historial.add(new transaccion(monto, descFull, catSel.getNombre(), catSel.isEsIngreso()));
        System.out.printf("Movimiento de $%.2f registrado.\n", monto);
    }

    public static void mostrarHistorialGeneral(ArrayList<transaccion> historial) {
        if (historial.isEmpty()) {
            System.out.println("Historial vacio.");
            return;
        }
        System.out.println("\n--- HISTORIAL DE TRANSACCIONES ---");
        System.out.printf("%-12s | %-12s | %-25s | %-10s%.2f\n", "FECHA", "CATEGORIA", "DESCRIPCION", "MONTO");
        System.out.println("---------------------------------------------------------------------------");
        
        for (transaccion t : historial) {
            String signo = t.isEsIngreso() ? "+" : "-";
            System.out.printf("%-12s | %-12s | %-25s | %s$%.2f\n", 
                t.getFechaHora().toLocalDate(), 
                t.getCategoria(), 
                t.getDescripcion(), 
                signo, 
                t.getMonto());
        }
    }

    public static void realizarTransferencia(ArrayList<Cuenta> cuentas, ArrayList<transaccion> historial, Scanner teclado) {
        if (cuentas.size() < 2) {
            System.out.println("Necesita al menos 2 cuentas para transferir.");
            return;
        }

        System.out.println("\n--- ORIGEN DEL DINERO ---");
        for (int i = 0; i < cuentas.size(); i++) {
            System.out.printf("%d. %s (Saldo: $%.2f)\n", (i + 1), cuentas.get(i).getNombre(), cuentas.get(i).getSaldo());
        }
        System.out.print("Seleccione cuenta de origen: ");
        int or = Integer.parseInt(teclado.nextLine()) - 1;

        System.out.println("\n--- DESTINO DEL DINERO ---");
        for (int i = 0; i < cuentas.size(); i++) {
            if (i == or) continue; // No transferir a la misma cuenta
            System.out.printf("%d. %s (Saldo: $%.2f)\n", (i + 1), cuentas.get(i).getNombre(), cuentas.get(i).getSaldo());
        }
        System.out.print("Seleccione cuenta de destino: ");
        int des = Integer.parseInt(teclado.nextLine()) - 1;

        System.out.print("Monto a transferir: ");
        double m = leerMontoSeguro(teclado);

        if (m <= cuentas.get(or).getSaldo()) {
            cuentas.get(or).actualizarSaldo(m, false);
            cuentas.get(des).actualizarSaldo(m, true);

            String d = "De " + cuentas.get(or).getNombre() + " a " + cuentas.get(des).getNombre();
            // Se registra como egreso del origen para el historial tecnico
            historial.add(new transaccion(m, d, "TRANSFERENCIA", false));
            System.out.printf("Transferencia de $%.2f realizada con exito.\n", m);
        } else {
            System.out.println("Error: Saldo insuficiente en la cuenta de origen.");
        }
    }
   
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
            if (c.getNombre().toUpperCase().contains("AHORRO")) saldoAhorros += c.getSaldo();
        }

        for (transaccion t : historial) {
            if (!t.getFechaHora().isBefore(inicio) && t.getFechaHora().isBefore(fin)) {
                if (t.getCategoria().equalsIgnoreCase("SUELDO") && t.isEsIngreso()) sueldoDet += t.getMonto();
                
                if (t.getCategoria().equalsIgnoreCase("TRANSFERENCIA")) {
                    if (t.getDescripcion().toUpperCase().contains("DE AHORRO")) deudaAhorro += t.getMonto();
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