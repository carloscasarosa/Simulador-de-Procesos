/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package simulador;

import java.util.ArrayList;

/**
 *
 * @author casar
 */
public class Proceso {

 
    private String nombre;
    private int quantum;
    private int tiempoArribo;
    private int totalES;
    private int rafagasCPU;
    private int rafagasCPUejecutadas;
    private int duracionRafagaCPU;
    private int rafagaCPUrestante;            
    private int tiempoRestanteES;             
    private int duracionRafagaES;
    private int prioridadExterna;
    private String estado;
    private int tiempoCPU;
    private int inicioEjecucion;
    private int tiempoEspera;
    private int tiempoFinalizacionES;
    private int tiempoInicioES;
    private int tiempoFinalizado;
    static ArrayList<Proceso> listaDeProcesos= new ArrayList<>();
    static ArrayList<Proceso> listaDeTerminados= new ArrayList<>();
    static ArrayList<Proceso> listaEjecutando =new ArrayList<>();
    static ArrayList<Proceso> listaBloqueados =new ArrayList<>();
    

    // Constructor
    public Proceso(String nombre, int tiempoArribo, int rafagasCPU, int duracionRafagaCPU, int duracionRafagaES, int prioridadExterna) {
        this.nombre = nombre;
        this.tiempoArribo = tiempoArribo;
        this.rafagasCPU = rafagasCPU;
        this.duracionRafagaCPU = duracionRafagaCPU;
        this.duracionRafagaES = duracionRafagaES;
        this.prioridadExterna = prioridadExterna;
        this.estado = "Listo";
        this.tiempoCPU =0;  //ESTO TIENE QUE ESTAN EN CONFIGURACION 
        this.rafagasCPUejecutadas=0;
}

    public int getTiempoFinalizado() {
        return tiempoFinalizado;
    }

    public void setTiempoFinalizado(int tiempoFinalizado) {
        this.tiempoFinalizado = tiempoFinalizado;
    }
     public int getTiempoInicioES() {
        return tiempoInicioES;
    }

    public void setTiempoInicioES(int tiempoInicioES) {
        this.tiempoInicioES = tiempoInicioES;
    }
    
        public int getInicioEjecucion() {
        return inicioEjecucion;
    }

    public void setInicioEjecucion(int inicioEjecucion) {
        this.inicioEjecucion = inicioEjecucion;
    }
    
  
       public int getTotalES() {
        return totalES;
    }

    public void setTotalES(int tiempoES) {
        this.totalES = tiempoES;
    }
    
     public int getTiempoFinalizacionES() {
        return tiempoFinalizacionES;
    }

    public void setTiempoFinalizacionES(int tiempoFinalizacionES) {
        this.tiempoFinalizacionES = tiempoFinalizacionES;
    }
    
  public int getTiempoEspera() {
        return tiempoEspera;
    }

    public void setTiempoEspera(int tiempoEspera) {
        this.tiempoEspera = tiempoEspera;
    }

   
     public int getRafagasCPUejecutadas() {
        return rafagasCPUejecutadas;
    }

    public void setRafagasCPUejecutadas(int rafagasCPUejecutadas) {
        this.rafagasCPUejecutadas = rafagasCPUejecutadas;
    }
    
    
        public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getTiempoCPU() {
        return tiempoCPU;
    }

    public void setTiempoCPU(int tiempoCPU) {
        this.tiempoCPU = tiempoCPU;
    }
    

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTiempoArribo() {
        return tiempoArribo;
    }

    public void setTiempoArribo(int tiempoArribo) {
        this.tiempoArribo = tiempoArribo;
    }

    public int getRafagasCPU() {
        return rafagasCPU;
    }

    public void setRafagasCPU(int rafagasCPU) {
        this.rafagasCPU = rafagasCPU;
    }

    public String getEstado() {
        return estado;
    }
    
    
      public int getRafagaCPUrestante() {
        return rafagaCPUrestante;
    }

    public void setRafagaCPUrestante(int ragafaCPUrestante) {
        this.rafagaCPUrestante = ragafaCPUrestante;
    }
    

    public int getDuracionRafagaCPU() {
        return duracionRafagaCPU;
    }

    public void setDuracionRafagaCPU(int duracionRafagaCPU) {
        this.duracionRafagaCPU = duracionRafagaCPU;
    }

    public int getDuracionRafagaES() {
        return duracionRafagaES;
    }

    public void setDuracionRafagaES(int duracionRafagaES) {
        this.duracionRafagaES = duracionRafagaES;
    }
    public int getTiempoRestanteES() {
        return tiempoRestanteES;
    }

    public void setTiempoRestanteES(int tiempoRestanteES) {
        this.tiempoRestanteES = tiempoRestanteES;
    }
    
    public int getPrioridadExterna() {
        return prioridadExterna;
    }

    public void setPrioridadExterna(int prioridadExterna) {
        this.prioridadExterna = prioridadExterna;
    }
      public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }
    
    
     public static ArrayList<Proceso> getProcesos(){
        return listaDeProcesos;
     }
 
       public static ArrayList<Proceso> getListaDeTerminados() {
        return listaDeTerminados;
    }
     
     public static ArrayList<Proceso> getListaEjecutando() {
        return listaEjecutando;
    }
    
     public static ArrayList<Proceso> getListaBloqueados() {
        return listaBloqueados;
    }
    
    
    public static Proceso nuevoProceso(String nombre, int tiempoArribo, int rafagasCPU,int duracionRafagaCPU, int duracionRafagaES, int prioridadExterna){
        Proceso proces = new Proceso (nombre, tiempoArribo,rafagasCPU,duracionRafagaCPU,duracionRafagaES, prioridadExterna);
        listaDeProcesos.add(proces);
        return proces;
    }

  
@Override
    public String toString() {
        return "Nombre: " + this.nombre + ", Tiempo de Arribo: " + this.tiempoArribo + "rafagasCPU: " + this.rafagasCPU + "duracionRafagaCPU: " +
               this.duracionRafagaCPU + " duracionRafagaES: " + this.duracionRafagaES + "prioridad: " + this.prioridadExterna + "Estado: " + this.estado;
    }     
    
    
}
