/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package simulador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
//import static simulador.ConfiguracionSistema.configuracion;

/**
 *
 * @author casar
 */
public class SimuladorPrioridad {

      //VARIABLES
     static int tiempoActual=0;
    static int procesosTerminados=0;
    Proceso procesoActual= null;
    static double porcentajeUso=0;   //porcetaje de uso de CPU
    static int tiempoRT=0;       //tiempo de retorno de la tanda
    static int totalSO=0;        //tiempo Total del SO
    static int tiempoCPUdesocupada=0;  //tiempo de CPU desocupada
    static int tiempoNP=0;
    static int tiempoRetorno=0;
    static double tiempoRNT=0;   //tiempo de retorno normalizado de la tanda
    static double tiempoRNP=0;  //aculador del tiempo de retono de cada proceso  
    static int tiempoCPU=0;
    static int tiempoTotalCPU=0;
    static int tiempoC=0;    //contado de tiempos de conmutacion
    static int tiempoF=0;    //contador de tiempos de finalizacion de procesos
  
     
    public SimuladorPrioridad() {
    }
    
    
    
public void simularPrioridad(String politica, ArrayList<Proceso> listaDeProcesos, ConfiguracionSistema configuracion){
    DefaultTableModel modelo = (DefaultTableModel) SimulacionProceso.jTableProces.getModel();
    modelo.setColumnCount(7);
    
    //SE ASUME QUE TODOS LOS PROCESOS HAN LLEGADO Y ESTAN EN ESTADO LISTO
     // Ordenar la lista utilizando el comparador
    Collections.sort(listaDeProcesos, new Ordenamiento()); //LOS ORDENA POR ORDEN DE LLEGADA PRIMERAMENTE
         
    //CONFIGURO LOS TIEMPOS DEL SO
     configuracionDeinicioDelSO(configuracion);
    
    //ANTES DE INICIAR LA EJECUCION, LE ASIGNO A CADA PROCESO EL TIEMPO DE RAFAGA  DE CPU RESTANTE PARA UTILIZARLA DURANTE LA EJECUCION
    inicializarProcesos(); 
    //CARGA LA LISTA A UNA AUXILIAR
    cargarLista(listaDeProcesos, Proceso.listaEjecutando);
    
    
    Thread simulacionThread = new Thread(() -> {
        // Asignar proceso de la cola de listos a ejecutar
           asignarProceso();  
        
        while(procesosTerminados < Proceso.listaDeProcesos.size()){
                     
           
           while(!Proceso.listaEjecutando.isEmpty() && procesoActual.getRafagaCPUrestante() >0){
               //EJECUTA EL PROCESO ACTUAL
                procesoActual.setEstado("En Ejecucion");
                procesoActual.setTiempoCPU(procesoActual.getTiempoCPU() + 1);
            
                esperar(500);
                        
                //ACTUALIZO LAS RAFAGAS DE CPU EJECUTADAS
                procesoActual.setRafagasCPUejecutadas(procesoActual.getRafagasCPUejecutadas()+1);
                    
                          
                //ACTUALIZO LAS RAFAGAS RESTANTE
                procesoActual.setRafagaCPUrestante(procesoActual.getRafagaCPUrestante() - 1);
                System.out.println("RAGAFAS RESTANTES " + procesoActual.getNombre() + procesoActual.getRafagaCPUrestante());
                //VERIFICA SI EXISTEN PROCESOS EN ESPERA 
                
                //CARGO LOS DATOS EN LA TABAL   
                SwingUtilities.invokeLater(() -> {
                    modelo.addRow(new Object[]{tiempoActual,procesoActual.getNombre(), procesoActual.getTiempoArribo(), 
                     procesoActual.getRafagasCPUejecutadas(),procesoActual.getRafagasCPU(), procesoActual.getDuracionRafagaES(), procesoActual.getEstado()});        
                });
   
                verificarProcesosEspera(modelo,tiempoActual);
           
                esperar(500);
                        
              
                //VERIFICO SI HAY PROCESOS BLOQUEADOS POR E/S
                verificarProcesosBloqueados(modelo, tiempoActual);
            
                //VERIFICA SI TIENE RAFAGAS POR EJECUTAR, SI ES ASI, INCREMENTA EL TIEMPO Y VUELVE A EJECUTAR
                if(procesoActual.getRafagaCPUrestante() > 0){     
                     tiempoActual++;
                }
                
                if(procesoActual.getRafagaCPUrestante() == 0){   //SI EJECUTO LAS RAFAGAS DE CPU 
                     
                    procesoActual.setEstado("En Espera");   //CAMBIA EL ESTADO
                    int rafagasCPU = procesoActual.getRafagasCPU();
                    rafagasCPU --;
                    procesoActual.setRafagasCPU(rafagasCPU);
                      
                     if(rafagasCPU >0){
                            if(configuracion.getTiempoConmutacion() > 0){
                                tiempoActual += configuracion.getTiempoConmutacion();       //SE ACTUALIZA EL TIEMPO CON EL TIEMPO DE CONMUTACION
                                tiempoC += configuracion.getTiempoConmutacion();
                            }    
                            
                        //SETO NUEVAMENTE LOS TIEMPOS DE RESTANTE DE RAFAGAS
                         for(Proceso proc: listaDeProcesos){
                            if(proc == procesoActual){
                                procesoActual.setRafagaCPUrestante(proc.getDuracionRafagaCPU());
                               }
                            }
                
                        //VERIFICA SI HAY RAFAGAS DE E/S
                        verificarRafagasES(modelo);
                        tiempoActual++;
                        procesoActual=asignarProcesoConPrioridad(); 
                         
                        esperar(500);
                        
                    }else{  //EJECUTO TODA LAS RAFAGAS DE CPU
                        if(configuracion.getTiempoTerminacionProceso() >0){
                         tiempoActual += configuracion.getTiempoTerminacionProceso();  //INCREMENTA EL TIEMPO DE EJECUCION CON EL TIEMPO DE FINALIZACION DEL PROCESO
                         tiempoF +=configuracion.getTiempoTerminacionProceso();
                        }else{
                          tiempoActual++;
                        }
                        
                        //CAMBIA EL ESTADO Y SETEA EL TIEMPO DE FINALIZACION DEL PROCESO
                        
                        procesoActual.setEstado("Terminado");
                        procesoActual.setTiempoFinalizado(tiempoActual);
                        
                        //CARGA LOS DATOS A LA TABLA
                        SwingUtilities.invokeLater(() -> {
                              modelo.addRow(new Object[]{tiempoActual,procesoActual.getNombre(), procesoActual.getTiempoArribo(), 
                              procesoActual.getRafagasCPUejecutadas(),procesoActual.getRafagasCPU(), procesoActual.getDuracionRafagaES(), procesoActual.getEstado()});        
                             });
                      
                        
                        Proceso.listaDeTerminados.add(procesoActual);
                        Proceso.listaEjecutando.remove(procesoActual);
                        procesosTerminados++;
                   
                        //BUSCO EL PROCESO QUE TENGA EL TIEMPO MAS CORTE DE RAFAGA DE CPU
                        procesoActual=asignarProcesoConPrioridad();
                        
                    }
                
                }
           }
           
           
       }//while
         informacionTanda();
        
    });//hilo de procesos
        
   simulacionThread.start(); // Iniciar el hilo de simulación         
}//metodo    

private void inicializarProcesos() {
        for(Proceso pr: Proceso.listaDeProcesos){
            pr.setRafagaCPUrestante(pr.getDuracionRafagaCPU());       
        }
    }

private void verificarProcesosEspera(DefaultTableModel modelo, int tiempoActual) {    
        for (Proceso p : Proceso.listaEjecutando) {
            if (p != procesoActual && tiempoActual >= p.getTiempoArribo()) {
                     p.setTiempoEspera(p.getTiempoEspera() + 1);
                      //CARGA LOS DATOS A LA TABLA
                     SwingUtilities.invokeLater(() -> {
                        modelo.addRow(new Object[]{tiempoActual,p.getNombre(), p.getTiempoArribo(), 
                        p.getRafagasCPUejecutadas(),p.getRafagasCPU(), p.getDuracionRafagaES(), p.getEstado()});        
                    });
                 
                }
            }
    }

private void verificarProcesosBloqueados(DefaultTableModel modelo, int tiempoActual) {
        if(!Proceso.listaBloqueados.isEmpty()){
             Iterator<Proceso> iteradorBloqueados = Proceso.listaBloqueados.iterator();
              while (iteradorBloqueados.hasNext()) {
                Proceso pBloqueado = iteradorBloqueados.next();
                pBloqueado.setTiempoRestanteES(pBloqueado.getTiempoRestanteES()-1);
                if (pBloqueado.getTiempoRestanteES() == 0) {
                    pBloqueado.setTotalES(pBloqueado.getTotalES() + 1);
                    pBloqueado.setEstado("E/S") ;
                    //CARGO LOS DATOS EN LA TABLE
                    SwingUtilities.invokeLater(() -> {
                        modelo.addRow(new Object[]{tiempoActual,pBloqueado.getNombre(), pBloqueado.getTiempoArribo(), 
                        pBloqueado.getRafagasCPUejecutadas(),pBloqueado.getRafagasCPU(), pBloqueado.getDuracionRafagaES(), pBloqueado.getEstado()});        
                     });
                    
                    //EL PROCESO VUELVE A LA LISTA DE EJECUTANDO ESPERANDO SU TURNO
                    pBloqueado.setEstado("Listo");
                    Proceso.listaEjecutando.add(pBloqueado);
                    iteradorBloqueados.remove();
                }else{
                    //CARGA LOS DATOS A LA TABLA
                   SwingUtilities.invokeLater(() -> {
                        modelo.addRow(new Object[]{tiempoActual,pBloqueado.getNombre(), pBloqueado.getTiempoArribo(), 
                        pBloqueado.getRafagasCPUejecutadas(),pBloqueado.getRafagasCPU(), pBloqueado.getDuracionRafagaES(), pBloqueado.getEstado()});        
                     });
                   pBloqueado.setTotalES(pBloqueado.getTotalES() + 1);
                   
                  
                }
             }
          }
  
    }

private void configuracionDeinicioDelSO(ConfiguracionSistema configuracion) {
        for(Proceso p: Proceso.listaDeProcesos){
            p.setTiempoArribo(p.getTiempoArribo() + configuracion.getTiempoAceptacionNuevoProceso());
            if(configuracion.getTiempoAceptacionNuevoProceso() > 0){
            tiempoNP ++;
            }
        }
    
    }

private void cargarLista(ArrayList<Proceso> listaDeProcesos, ArrayList<Proceso> listaEjecutando) {
        for(Proceso procesos: listaDeProcesos){
                Proceso.listaEjecutando.add(procesos);
          }        
    }

 private static void informacionTanda(){
    DefaultTableModel modelo = (DefaultTableModel) SimulacionProceso.jTableInfoProcesos.getModel();
    modelo.setColumnCount(4);
    for(Proceso procesos: Proceso.listaDeTerminados){
         tiempoRetorno = (procesos.getTiempoFinalizado()- procesos.getTiempoArribo());
         tiempoRNP= (double) tiempoRetorno /procesos.getRafagasCPUejecutadas();
                
         modelo.addRow(new Object[]{procesos.getNombre(), tiempoRetorno, tiempoRNP, procesos.getTiempoEspera()});
        }
     
    informacionSistema();
}

private static void informacionSistema(){
    DefaultTableModel modelo2 = (DefaultTableModel) SimulacionProceso.jTableTanda.getModel();
    modelo2.setColumnCount(6);
 
    //OBTENGO LA INFORMACION DE  LA TANDA
    for(Proceso procesos: Proceso.listaDeTerminados){
         if(procesos.getTiempoFinalizado() > tiempoRT){  //BUSCO EL VALOR MAS ALTO DE FINALIZACION
              tiempoRT=procesos.getTiempoFinalizado();
          }       
         
         tiempoRetorno = (procesos.getTiempoFinalizado()- procesos.getTiempoArribo());
         tiempoCPU= procesos.getRafagasCPUejecutadas();
         tiempoTotalCPU += tiempoCPU;
    }
    tiempoCPUdesocupada=0;
    porcentajeUso= ((double) tiempoTotalCPU/tiempoRT)*100;
    tiempoRNT= (double) tiempoRT/Proceso.listaDeTerminados.size();
       
    //calculo el tiempo total del SO
    totalSO= tiempoNP + tiempoC + tiempoF; 
 
    modelo2.addRow(new Object[]{tiempoRT, tiempoRNT,tiempoCPUdesocupada,totalSO,tiempoTotalCPU, porcentajeUso});
}

private void asignarProceso() { 
 if (procesoActual == null && !Proceso.listaEjecutando.isEmpty()) {
   procesoActual = Proceso.listaEjecutando.get(0);
   tiempoActual= procesoActual.getTiempoArribo();
 } 
}
     
private Proceso asignarProcesoConPrioridad() {
   
    boolean llena=false;
    //PRIMERO SE FIJA SI LLEGARON TODOS LOS PROCESOS
    for(Proceso p :Proceso.listaEjecutando){
        if(tiempoActual >= p.getTiempoArribo()){
          llena=true;
        }else
          llena=false;
    }
    
    if(llena){
        procesoActual = Collections.max(Proceso.listaEjecutando, Comparator.comparingInt(p -> p.getPrioridadExterna()));
    }else{
        for(Proceso p :Proceso.listaEjecutando){
           if(tiempoActual == p.getTiempoArribo()){
                procesoActual= p;
            }
         }     
    }
    return procesoActual;
}   
  
private void esperar(int milisegundos) {
    try {
        Thread.sleep(milisegundos);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}

private void verificarRafagasES(DefaultTableModel modelo) {
   
    if (procesoActual.getDuracionRafagaES() >0){ 
         //CAMBIA EL ESTADO 
         procesoActual.setEstado("E/S");
               
         //SETEO LAS RAFAGAS DE E/S
        for(Proceso pr: Proceso.listaEjecutando){
            if(pr == procesoActual){ 
                pr.setTiempoRestanteES(pr.getDuracionRafagaES());                              
             }
        }   
        
        Proceso.listaBloqueados.add(procesoActual);
        Proceso.listaEjecutando.remove(procesoActual);
                        
        //SI ES EL UTLIMO PROCESO HACE E/S HASTA FINALIZAR 
        if(Proceso.listaEjecutando.isEmpty() && !Proceso.listaBloqueados.isEmpty()){
            while(!Proceso.listaBloqueados.isEmpty()){
                tiempoActual ++; 
                verificarProcesosBloqueados(modelo, tiempoActual);         
            }
        }
                        
    }
      
}

}