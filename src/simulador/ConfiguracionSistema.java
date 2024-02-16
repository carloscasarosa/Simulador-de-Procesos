/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package simulador;

/**
 *
 * @author casar
 */
public class ConfiguracionSistema {

    static Object configuracion;

    private int tiempoAceptacionNuevoProceso;
    private int tiempoTerminacionProceso;
    private int tiempoConmutacion;
    private int quantum;
    private int tiempoActual;

  
    
    
    // Constructor
    public ConfiguracionSistema(int tiempoAceptacionNuevoProceso, int tiempoTerminacionProceso, int tiempoConmutacion, int quantum) {
        this.tiempoAceptacionNuevoProceso = tiempoAceptacionNuevoProceso;
        this.tiempoTerminacionProceso = tiempoTerminacionProceso;
        this.tiempoConmutacion = tiempoConmutacion;
        this.quantum = quantum;
        tiempoActual=0;
    }

    public int getTiempoActual() {
        return tiempoActual;
    }

    public void setTiempoActual(int tiempoActual) {
        this.tiempoActual = tiempoActual;
    }  
   
    public int getTiempoAceptacionNuevoProceso() {
        return tiempoAceptacionNuevoProceso;
    }

    public void setTiempoAceptacionNuevoProceso(int tiempoAceptacionNuevoProceso) {
        this.tiempoAceptacionNuevoProceso = tiempoAceptacionNuevoProceso;
    }

    public int getTiempoTerminacionProceso() {
        return tiempoTerminacionProceso;
    }

    public void setTiempoTerminacionProceso(int tiempoTerminacionProceso) {
        this.tiempoTerminacionProceso = tiempoTerminacionProceso;
    }

    public int getTiempoConmutacion() {
        return tiempoConmutacion;
    }

    public void setTiempoConmutacion(int tiempoConmutacion) {
        this.tiempoConmutacion = tiempoConmutacion;
    }

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    public static ConfiguracionSistema nuevaConfiguracion(int tiempoAceptacion, int tiempoTerminacion, int tiempoConmutacion, int quantum) {
        return new ConfiguracionSistema(tiempoAceptacion, tiempoTerminacion, tiempoConmutacion, quantum);
    }
}
