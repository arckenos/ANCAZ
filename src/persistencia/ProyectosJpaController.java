/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistencia;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Empleados;
import entidades.Ordenes;
import entidades.Proyectos;
import java.util.ArrayList;
import java.util.Collection;
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
public class ProyectosJpaController implements Serializable {

    public ProyectosJpaController() {
        this.emf = Persistence.createEntityManagerFactory("InventariosANCAZPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Proyectos proyectos) {
        if (proyectos.getOrdenesCollection() == null) {
            proyectos.setOrdenesCollection(new ArrayList<Ordenes>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empleados idEncargado = proyectos.getIdEncargado();
            if (idEncargado != null) {
                idEncargado = em.getReference(idEncargado.getClass(), idEncargado.getIdEmpleado());
                proyectos.setIdEncargado(idEncargado);
            }
            Collection<Ordenes> attachedOrdenesCollection = new ArrayList<Ordenes>();
            for (Ordenes ordenesCollectionOrdenesToAttach : proyectos.getOrdenesCollection()) {
                ordenesCollectionOrdenesToAttach = em.getReference(ordenesCollectionOrdenesToAttach.getClass(), ordenesCollectionOrdenesToAttach.getIdOrden());
                attachedOrdenesCollection.add(ordenesCollectionOrdenesToAttach);
            }
            proyectos.setOrdenesCollection(attachedOrdenesCollection);
            em.persist(proyectos);
            if (idEncargado != null) {
                idEncargado.getProyectosCollection().add(proyectos);
                idEncargado = em.merge(idEncargado);
            }
            for (Ordenes ordenesCollectionOrdenes : proyectos.getOrdenesCollection()) {
                Proyectos oldIdProyectoOfOrdenesCollectionOrdenes = ordenesCollectionOrdenes.getIdProyecto();
                ordenesCollectionOrdenes.setIdProyecto(proyectos);
                ordenesCollectionOrdenes = em.merge(ordenesCollectionOrdenes);
                if (oldIdProyectoOfOrdenesCollectionOrdenes != null) {
                    oldIdProyectoOfOrdenesCollectionOrdenes.getOrdenesCollection().remove(ordenesCollectionOrdenes);
                    oldIdProyectoOfOrdenesCollectionOrdenes = em.merge(oldIdProyectoOfOrdenesCollectionOrdenes);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Proyectos proyectos) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proyectos persistentProyectos = em.find(Proyectos.class, proyectos.getIdProyecto());
            Empleados idEncargadoOld = persistentProyectos.getIdEncargado();
            Empleados idEncargadoNew = proyectos.getIdEncargado();
            Collection<Ordenes> ordenesCollectionOld = persistentProyectos.getOrdenesCollection();
            Collection<Ordenes> ordenesCollectionNew = proyectos.getOrdenesCollection();
            List<String> illegalOrphanMessages = null;
            for (Ordenes ordenesCollectionOldOrdenes : ordenesCollectionOld) {
                if (!ordenesCollectionNew.contains(ordenesCollectionOldOrdenes)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ordenes " + ordenesCollectionOldOrdenes + " since its idProyecto field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idEncargadoNew != null) {
                idEncargadoNew = em.getReference(idEncargadoNew.getClass(), idEncargadoNew.getIdEmpleado());
                proyectos.setIdEncargado(idEncargadoNew);
            }
            Collection<Ordenes> attachedOrdenesCollectionNew = new ArrayList<Ordenes>();
            for (Ordenes ordenesCollectionNewOrdenesToAttach : ordenesCollectionNew) {
                ordenesCollectionNewOrdenesToAttach = em.getReference(ordenesCollectionNewOrdenesToAttach.getClass(), ordenesCollectionNewOrdenesToAttach.getIdOrden());
                attachedOrdenesCollectionNew.add(ordenesCollectionNewOrdenesToAttach);
            }
            ordenesCollectionNew = attachedOrdenesCollectionNew;
            proyectos.setOrdenesCollection(ordenesCollectionNew);
            proyectos = em.merge(proyectos);
            if (idEncargadoOld != null && !idEncargadoOld.equals(idEncargadoNew)) {
                idEncargadoOld.getProyectosCollection().remove(proyectos);
                idEncargadoOld = em.merge(idEncargadoOld);
            }
            if (idEncargadoNew != null && !idEncargadoNew.equals(idEncargadoOld)) {
                idEncargadoNew.getProyectosCollection().add(proyectos);
                idEncargadoNew = em.merge(idEncargadoNew);
            }
            for (Ordenes ordenesCollectionNewOrdenes : ordenesCollectionNew) {
                if (!ordenesCollectionOld.contains(ordenesCollectionNewOrdenes)) {
                    Proyectos oldIdProyectoOfOrdenesCollectionNewOrdenes = ordenesCollectionNewOrdenes.getIdProyecto();
                    ordenesCollectionNewOrdenes.setIdProyecto(proyectos);
                    ordenesCollectionNewOrdenes = em.merge(ordenesCollectionNewOrdenes);
                    if (oldIdProyectoOfOrdenesCollectionNewOrdenes != null && !oldIdProyectoOfOrdenesCollectionNewOrdenes.equals(proyectos)) {
                        oldIdProyectoOfOrdenesCollectionNewOrdenes.getOrdenesCollection().remove(ordenesCollectionNewOrdenes);
                        oldIdProyectoOfOrdenesCollectionNewOrdenes = em.merge(oldIdProyectoOfOrdenesCollectionNewOrdenes);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = proyectos.getIdProyecto();
                if (findProyectos(id) == null) {
                    throw new NonexistentEntityException("The proyectos with id " + id + " no longer exists.");
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
            Proyectos proyectos;
            try {
                proyectos = em.getReference(Proyectos.class, id);
                proyectos.getIdProyecto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The proyectos with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Ordenes> ordenesCollectionOrphanCheck = proyectos.getOrdenesCollection();
            for (Ordenes ordenesCollectionOrphanCheckOrdenes : ordenesCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Proyectos (" + proyectos + ") cannot be destroyed since the Ordenes " + ordenesCollectionOrphanCheckOrdenes + " in its ordenesCollection field has a non-nullable idProyecto field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Empleados idEncargado = proyectos.getIdEncargado();
            if (idEncargado != null) {
                idEncargado.getProyectosCollection().remove(proyectos);
                idEncargado = em.merge(idEncargado);
            }
            em.remove(proyectos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Proyectos> findProyectosEntities() {
        return findProyectosEntities(true, -1, -1);
    }

    public List<Proyectos> findProyectosEntities(int maxResults, int firstResult) {
        return findProyectosEntities(false, maxResults, firstResult);
    }

    private List<Proyectos> findProyectosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Proyectos.class));
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

    public Proyectos findProyectos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Proyectos.class, id);
        } finally {
            em.close();
        }
    }

    public int getProyectosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Proyectos> rt = cq.from(Proyectos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
