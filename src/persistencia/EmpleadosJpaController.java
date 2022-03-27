/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistencia;

import entidades.Empleados;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Ordenes;
import java.util.ArrayList;
import java.util.Collection;
import entidades.Proyectos;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import persistencia.exceptions.IllegalOrphanException;
import persistencia.exceptions.NonexistentEntityException;

/**
 *
 * @author Arcke
 */
public class EmpleadosJpaController implements Serializable {

    public EmpleadosJpaController() {
        this.emf = Persistence.createEntityManagerFactory("InventariosANCAZPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Empleados empleados) {
        if (empleados.getOrdenesCollection() == null) {
            empleados.setOrdenesCollection(new ArrayList<Ordenes>());
        }
        if (empleados.getProyectosCollection() == null) {
            empleados.setProyectosCollection(new ArrayList<Proyectos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Ordenes> attachedOrdenesCollection = new ArrayList<Ordenes>();
            for (Ordenes ordenesCollectionOrdenesToAttach : empleados.getOrdenesCollection()) {
                ordenesCollectionOrdenesToAttach = em.getReference(ordenesCollectionOrdenesToAttach.getClass(), ordenesCollectionOrdenesToAttach.getIdOrden());
                attachedOrdenesCollection.add(ordenesCollectionOrdenesToAttach);
            }
            empleados.setOrdenesCollection(attachedOrdenesCollection);
            Collection<Proyectos> attachedProyectosCollection = new ArrayList<Proyectos>();
            for (Proyectos proyectosCollectionProyectosToAttach : empleados.getProyectosCollection()) {
                proyectosCollectionProyectosToAttach = em.getReference(proyectosCollectionProyectosToAttach.getClass(), proyectosCollectionProyectosToAttach.getIdProyecto());
                attachedProyectosCollection.add(proyectosCollectionProyectosToAttach);
            }
            empleados.setProyectosCollection(attachedProyectosCollection);
            em.persist(empleados);
            for (Ordenes ordenesCollectionOrdenes : empleados.getOrdenesCollection()) {
                Empleados oldIdEmpleadoOfOrdenesCollectionOrdenes = ordenesCollectionOrdenes.getIdEmpleado();
                ordenesCollectionOrdenes.setIdEmpleado(empleados);
                ordenesCollectionOrdenes = em.merge(ordenesCollectionOrdenes);
                if (oldIdEmpleadoOfOrdenesCollectionOrdenes != null) {
                    oldIdEmpleadoOfOrdenesCollectionOrdenes.getOrdenesCollection().remove(ordenesCollectionOrdenes);
                    oldIdEmpleadoOfOrdenesCollectionOrdenes = em.merge(oldIdEmpleadoOfOrdenesCollectionOrdenes);
                }
            }
            for (Proyectos proyectosCollectionProyectos : empleados.getProyectosCollection()) {
                Empleados oldIdEncargadoOfProyectosCollectionProyectos = proyectosCollectionProyectos.getIdEncargado();
                proyectosCollectionProyectos.setIdEncargado(empleados);
                proyectosCollectionProyectos = em.merge(proyectosCollectionProyectos);
                if (oldIdEncargadoOfProyectosCollectionProyectos != null) {
                    oldIdEncargadoOfProyectosCollectionProyectos.getProyectosCollection().remove(proyectosCollectionProyectos);
                    oldIdEncargadoOfProyectosCollectionProyectos = em.merge(oldIdEncargadoOfProyectosCollectionProyectos);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Empleados empleados) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empleados persistentEmpleados = em.find(Empleados.class, empleados.getIdEmpleado());
            Collection<Ordenes> ordenesCollectionOld = persistentEmpleados.getOrdenesCollection();
            Collection<Ordenes> ordenesCollectionNew = empleados.getOrdenesCollection();
            Collection<Proyectos> proyectosCollectionOld = persistentEmpleados.getProyectosCollection();
            Collection<Proyectos> proyectosCollectionNew = empleados.getProyectosCollection();
            List<String> illegalOrphanMessages = null;
            for (Ordenes ordenesCollectionOldOrdenes : ordenesCollectionOld) {
                if (!ordenesCollectionNew.contains(ordenesCollectionOldOrdenes)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ordenes " + ordenesCollectionOldOrdenes + " since its idEmpleado field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Ordenes> attachedOrdenesCollectionNew = new ArrayList<Ordenes>();
            for (Ordenes ordenesCollectionNewOrdenesToAttach : ordenesCollectionNew) {
                ordenesCollectionNewOrdenesToAttach = em.getReference(ordenesCollectionNewOrdenesToAttach.getClass(), ordenesCollectionNewOrdenesToAttach.getIdOrden());
                attachedOrdenesCollectionNew.add(ordenesCollectionNewOrdenesToAttach);
            }
            ordenesCollectionNew = attachedOrdenesCollectionNew;
            empleados.setOrdenesCollection(ordenesCollectionNew);
            Collection<Proyectos> attachedProyectosCollectionNew = new ArrayList<Proyectos>();
            for (Proyectos proyectosCollectionNewProyectosToAttach : proyectosCollectionNew) {
                proyectosCollectionNewProyectosToAttach = em.getReference(proyectosCollectionNewProyectosToAttach.getClass(), proyectosCollectionNewProyectosToAttach.getIdProyecto());
                attachedProyectosCollectionNew.add(proyectosCollectionNewProyectosToAttach);
            }
            proyectosCollectionNew = attachedProyectosCollectionNew;
            empleados.setProyectosCollection(proyectosCollectionNew);
            empleados = em.merge(empleados);
            for (Ordenes ordenesCollectionNewOrdenes : ordenesCollectionNew) {
                if (!ordenesCollectionOld.contains(ordenesCollectionNewOrdenes)) {
                    Empleados oldIdEmpleadoOfOrdenesCollectionNewOrdenes = ordenesCollectionNewOrdenes.getIdEmpleado();
                    ordenesCollectionNewOrdenes.setIdEmpleado(empleados);
                    ordenesCollectionNewOrdenes = em.merge(ordenesCollectionNewOrdenes);
                    if (oldIdEmpleadoOfOrdenesCollectionNewOrdenes != null && !oldIdEmpleadoOfOrdenesCollectionNewOrdenes.equals(empleados)) {
                        oldIdEmpleadoOfOrdenesCollectionNewOrdenes.getOrdenesCollection().remove(ordenesCollectionNewOrdenes);
                        oldIdEmpleadoOfOrdenesCollectionNewOrdenes = em.merge(oldIdEmpleadoOfOrdenesCollectionNewOrdenes);
                    }
                }
            }
            for (Proyectos proyectosCollectionOldProyectos : proyectosCollectionOld) {
                if (!proyectosCollectionNew.contains(proyectosCollectionOldProyectos)) {
                    proyectosCollectionOldProyectos.setIdEncargado(null);
                    proyectosCollectionOldProyectos = em.merge(proyectosCollectionOldProyectos);
                }
            }
            for (Proyectos proyectosCollectionNewProyectos : proyectosCollectionNew) {
                if (!proyectosCollectionOld.contains(proyectosCollectionNewProyectos)) {
                    Empleados oldIdEncargadoOfProyectosCollectionNewProyectos = proyectosCollectionNewProyectos.getIdEncargado();
                    proyectosCollectionNewProyectos.setIdEncargado(empleados);
                    proyectosCollectionNewProyectos = em.merge(proyectosCollectionNewProyectos);
                    if (oldIdEncargadoOfProyectosCollectionNewProyectos != null && !oldIdEncargadoOfProyectosCollectionNewProyectos.equals(empleados)) {
                        oldIdEncargadoOfProyectosCollectionNewProyectos.getProyectosCollection().remove(proyectosCollectionNewProyectos);
                        oldIdEncargadoOfProyectosCollectionNewProyectos = em.merge(oldIdEncargadoOfProyectosCollectionNewProyectos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = empleados.getIdEmpleado();
                if (findEmpleados(id) == null) {
                    throw new NonexistentEntityException("The empleados with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empleados empleados;
            try {
                empleados = em.getReference(Empleados.class, id);
                empleados.getIdEmpleado();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The empleados with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Ordenes> ordenesCollectionOrphanCheck = empleados.getOrdenesCollection();
            for (Ordenes ordenesCollectionOrphanCheckOrdenes : ordenesCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Empleados (" + empleados + ") cannot be destroyed since the Ordenes " + ordenesCollectionOrphanCheckOrdenes + " in its ordenesCollection field has a non-nullable idEmpleado field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Proyectos> proyectosCollection = empleados.getProyectosCollection();
            for (Proyectos proyectosCollectionProyectos : proyectosCollection) {
                proyectosCollectionProyectos.setIdEncargado(null);
                proyectosCollectionProyectos = em.merge(proyectosCollectionProyectos);
            }
            em.remove(empleados);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Empleados> findEmpleadosEntities() {
        return findEmpleadosEntities(true, -1, -1);
    }

    public List<Empleados> findEmpleadosEntities(int maxResults, int firstResult) {
        return findEmpleadosEntities(false, maxResults, firstResult);
    }

    private List<Empleados> findEmpleadosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Empleados.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Empleados findEmpleados(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Empleados.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmpleadosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Empleados> rt = cq.from(Empleados.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
