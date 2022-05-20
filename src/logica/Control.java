/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logica;

import control.exceptions.IllegalOrphanException;
import control.exceptions.NonexistentEntityException;
import entidades.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import persistencia.*;

/**
 *
 * @author Arcke
 */
public class Control {
    
    
    //Ordenes
    public static void registrarOrden(Ordenes orden, List<Detalleorden> detalles) throws Exception{
        OrdenesJpaController oc = new OrdenesJpaController();
        DetalleordenJpaController dc = new DetalleordenJpaController();
        ArticulosJpaController ac = new ArticulosJpaController();
        oc.create(orden);
        if(detalles == null)
            return;
        
        for (Detalleorden detalle : detalles) {
            Articulos articulo = ac.findArticulos(detalle.getIdArticulo().getIdArticulo());           
            articulo.setUnidades(articulo.getUnidades() + detalle.getCantidad());
            ac.edit(articulo);
            dc.create(detalle);                       
        }        
        //oc.create(orden);
    }
    
    public static List<Ordenes> consultarOrdenes(){
        OrdenesJpaController oc = new OrdenesJpaController();
        return oc.findOrdenesEntities();
    }
    
    public static List<Detalleorden> consultarDetallesOrden(Ordenes orden){
        DetalleordenJpaController dc = new DetalleordenJpaController();
        List<Detalleorden> detalles = new ArrayList<>();
        for (Detalleorden detalle : dc.findDetalleordenEntities()) {
            if(detalle.getIdDetalleOrden() == orden.getIdOrden()){
                detalles.add(detalle);
            }
        }
        return detalles;
    }
    
    public static void eliminarProveedor(Proveedores proveedor){
        try {
            ProveedoresJpaController pc = new ProveedoresJpaController();
            pc.destroy(proveedor.getIdProveedor());
        } catch (persistencia.exceptions.IllegalOrphanException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        } catch (persistencia.exceptions.NonexistentEntityException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void eliminarOrden(Ordenes orden) throws  persistencia.exceptions.NonexistentEntityException, Vistas.exceptions.NonexistentEntityException{
        OrdenesJpaController oc = new OrdenesJpaController();
        DetalleordenJpaController dc = new DetalleordenJpaController();
        List<Detalleorden> detalleorden = consultarDetallesOrden(orden);
        for (Detalleorden detalle : detalleorden) {
            dc.destroy(detalle.getIdDetalleOrden());
        }
        oc.destroy(orden.getIdOrden());
    }
    
    //Articulos
    public static void registrarArticulo(Articulos articulos){
        ArticulosJpaController ac = new ArticulosJpaController();
        ac.create(articulos);
        
    }
    
    public static List<Articulos> consultarArticulos(){
        ArticulosJpaController ac = new ArticulosJpaController();
        return ac.findArticulosEntities();
        
    } 
    
    public static Articulos consultarArticulo(String nombre){
        ArticulosJpaController ac = new ArticulosJpaController();
        List<Articulos> articulos = ac.findArticulosEntities();
        for (Articulos articulo : articulos) {
            if(articulo.getNombre().equals(nombre)){
                return articulo;
            }
        }
        return null;
    }
    
    public static Articulos consultarArticulo(int id){
        return new ArticulosJpaController().findArticulos(id);
    }
    
    public static void registrarProveedor(Proveedores proveedor){
        ProveedoresJpaController pc = new ProveedoresJpaController();
        pc.create(proveedor);
    }
    
    public static List<Articulos> consultarArticulosProveedor(Proveedores proveedor){
        ArticulosJpaController ac = new ArticulosJpaController();
        List<Articulos> articulos =  ac.findArticulosEntities();
        List<Articulos> articulosProveedor = new ArrayList<>();
         for (Articulos articulo : articulos) {
             if(articulo.getIdProveedor().equals(proveedor)){
                 articulosProveedor.add(articulo);
             }
         }
        
        
        return articulosProveedor;
        
    }
    
    //Proyecto
    public static List<Proyectos> consultarProyectos(){
        ProyectosJpaController pc = new ProyectosJpaController();
        return pc.findProyectosEntities();
    }
    
    public static Proyectos consultarProyecto(String nombre){
        List<Proyectos> proyectos = consultarProyectos();
        for (Proyectos proyecto : proyectos) {
            if(proyecto.getNombre().equals(nombre)){
                return proyecto;
            }
            
        }
        return null;
    }
    
    public static Proyectos consultarProyecto(int id){
        return new ProyectosJpaController().findProyectos(id);
    }
    
    //Empleados
    public static List<Empleados> consultarEmpleados(){
        EmpleadoJpaController em = new EmpleadoJpaController();
        return em.findEmpleadosEntities();
    }
    
    public static Empleados consultarEmpleadoNombre(String nombre){
        EmpleadoJpaController em = new EmpleadoJpaController();
        List<Empleados> empleados = em.findEmpleadosEntities();
        for (Empleados empleado : empleados) {
            if(empleado.getNombre().equals(nombre)){
                return empleado;
            }
        }
        return null;
    }
    
    //Proveedores
    public static List<Proveedores> consultarProveedores(){
        ProveedoresJpaController pc = new ProveedoresJpaController();
        return pc.findProveedoresEntities();
    }
   
    public static Proveedores consultarProveedordoNombre(String nombre){
        ProveedoresJpaController pc = new ProveedoresJpaController();
        List<Proveedores> proveedores = pc.findProveedoresEntities();
        for (Proveedores proveedor : proveedores) {
            if(proveedor.getNombre().equals(nombre)){
                return proveedor;
            }
        }
        return null;
    }
    
}
