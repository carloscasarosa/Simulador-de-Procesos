/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package simulador;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

/**
 *
 * @author casar
 */
public class Simulador {

  

    // Constructor
    public Simulador() {
    
    }

   
  public void simulacionElegida(String politica, ArrayList<Proceso> listaDeProcesos, ConfiguracionSistema configuracion) {
    switch(politica) {
            case "FCFS":
                  SimuladorFCFS s= new SimuladorFCFS();
                   s.simularFCFS(politica, listaDeProcesos, configuracion);
                break;
            case "Prioridad":
                  SimuladorPrioridad sp= new SimuladorPrioridad();
                  sp.simularPrioridad(politica, listaDeProcesos, configuracion);
                break;
            case "Round-Robin":
                  SimuladorRR sr= new SimuladorRR();
                  sr.simularRR(politica, listaDeProcesos, configuracion);
                break;
            case "FSJ":
                SimuladorFSJ fs= new SimuladorFSJ();
                fs.simularFSJ(politica, listaDeProcesos, configuracion);
                break;
            case "SRT":
                    SimuladorSRT sim= new SimuladorSRT();
                    sim.simularSRT(politica, listaDeProcesos, configuracion);
                break;
            default:
                // Tratamiento para casos no reconocidos
                break;
        }
        
        
}
       



    
    
    public static void main(String[] args){
        SimuladorJF ven = new SimuladorJF();
        ven.setVisible(true);
    }
    
   
}
