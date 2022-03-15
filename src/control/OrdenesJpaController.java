/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import control.exceptions.IllegalOrphanException;
import control.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Empleado;
import entidades.Proveedores;
import entidades.Detalleorden;
import entidades.Ordenes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Arcke
 */
public class OrdenesJpaController implements Serializable {

    public OrdenesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ordenes ordenes) {
        if (ordenes.getDetalleordenCollection() == null) {
            ordenes.setDetalleordenCollection(new ArrayList<Detalleorden>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empleado idEmpleado = ordenes.getIdEmpleado();
            if (idEmpleado != null) {
                idEmpleado = em.getReference(idEmpleado.getClass(), idEmpleado.getIdEmpleado());
                ordenes.setIdEmpleado(idEmpleado);
            }
            Proveedores idProveedor = ordenes.getIdProveedor();
            if (idProveedor != null) {
                idProveedor = em.getReference(idProveedor.getClass(), idProveedor.getIdProveedor());
                ordenes.setIdProveedor(idProveedor);
            }
            Collection<Detalleorden> attachedDetalleordenCollection = new ArrayList<Detalleorden>();
            for (Detalleorden detalleordenCollectionDetalleordenToAttach : ordenes.getDetalleordenCollection()) {
                detalleordenCollectionDetalleordenToAttach = em.getReference(detalleordenCollectionDetalleordenToAttach.getClass(), detalleordenCollectionDetalleordenToAttach.getIdDetalleOrden());
                attachedDetalleordenCollection.add(detalleordenCollectionDetalleordenToAttach);
            }
            ordenes.setDetalleordenCollection(attachedDetalleordenCollection);
            em.persist(ordenes);
            if (idEmpleado != null) {
                idEmpleado.getOrdenesCollection().add(ordenes);
                idEmpleado = em.merge(idEmpleado);
            }
            if (idProveedor != null) {
                idProveedor.getOrdenesCollection().add(ordenes);
                idProveedor = em.merge(idProveedor);
            }
            for (Detalleorden detalleordenCollectionDetalleorden : ordenes.getDetalleordenCollection()) {
                Ordenes oldIdOrdenOfDetalleordenCollectionDetalleorden = detalleordenCollectionDetalleorden.getIdOrden();
                detalleordenCollectionDetalleorden.setIdOrden(ordenes);
                detalleordenCollectionDetalleorden = em.merge(detalleordenCollectionDetalleorden);
                if (oldIdOrdenOfDetalleordenCollectionDetalleorden != null) {
                    oldIdOrdenOfDetalleordenCollectionDetalleorden.getDetalleordenCollection().remove(detalleordenCollectionDetalleorden);
                    oldIdOrdenOfDetalleordenCollectionDetalleorden = em.merge(oldIdOrdenOfDetalleordenCollectionDetalleorden);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ordenes ordenes) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ordenes persistentOrdenes = em.find(Ordenes.class, ordenes.getIdOrden());
            Empleado idEmpleadoOld = persistentOrdenes.getIdEmpleado();
            Empleado idEmpleadoNew = ordenes.getIdEmpleado();
            Proveedores idProveedorOld = persistentOrdenes.getIdProveedor();
            Proveedores idProveedorNew = ordenes.getIdProveedor();
            Collection<Detalleorden> detalleordenCollectionOld = persistentOrdenes.getDetalleordenCollection();
            Collection<Detalleorden> detalleordenCollectionNew = ordenes.getDetalleordenCollection();
            List<String> illegalOrphanMessages = null;
            for (Detalleorden detalleordenCollectionOldDetalleorden : detalleordenCollectionOld) {
                if (!detalleordenCollectionNew.contains(detalleordenCollectionOldDetalleorden)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detalleorden " + detalleordenCollectionOldDetalleorden + " since its idOrden field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idEmpleadoNew != null) {
                idEmpleadoNew = em.getReference(idEmpleadoNew.getClass(), idEmpleadoNew.getIdEmpleado());
                ordenes.setIdEmpleado(idEmpleadoNew);
            }
            if (idProveedorNew != null) {
                idProveedorNew = em.getReference(idProveedorNew.getClass(), idProveedorNew.getIdProveedor());
                ordenes.setIdProveedor(idProveedorNew);
            }
            Collection<Detalleorden> attachedDetalleordenCollectionNew = new ArrayList<Detalleorden>();
            for (Detalleorden detalleordenCollectionNewDetalleordenToAttach : detalleordenCollectionNew) {
                detalleordenCollectionNewDetalleordenToAttach = em.getReference(detalleordenCollectionNewDetalleordenToAttach.getClass(), detalleordenCollectionNewDetalleordenToAttach.getIdDetalleOrden());
                attachedDetalleordenCollectionNew.add(detalleordenCollectionNewDetalleordenToAttach);
            }
            detalleordenCollectionNew = attachedDetalleordenCollectionNew;
            ordenes.setDetalleordenCollection(detalleordenCollectionNew);
            ordenes = em.merge(ordenes);
            if (idEmpleadoOld != null && !idEmpleadoOld.equals(idEmpleadoNew)) {
                idEmpleadoOld.getOrdenesCollection().remove(ordenes);
                idEmpleadoOld = em.merge(idEmpleadoOld);
            }
            if (idEmpleadoNew != null && !idEmpleadoNew.equals(idEmpleadoOld)) {
                idEmpleadoNew.getOrdenesCollection().add(ordenes);
                idEmpleadoNew = em.merge(idEmpleadoNew);
            }
            if (idProveedorOld != null && !idProveedorOld.equals(idProveedorNew)) {
                idProveedorOld.getOrdenesCollection().remove(ordenes);
                idProveedorOld = em.merge(idProveedorOld);
            }
            if (idProveedorNew != null && !idProveedorNew.equals(idProveedorOld)) {
                idProveedorNew.getOrdenesCollection().add(ordenes);
                idProveedorNew = em.merge(idProveedorNew);
            }
            for (Detalleorden detalleordenCollectionNewDetalleorden : detalleordenCollectionNew) {
                if (!detalleordenCollectionOld.contains(detalleordenCollectionNewDetalleorden)) {
                    Ordenes oldIdOrdenOfDetalleordenCollectionNewDetalleorden = detalleordenCollectionNewDetalleorden.getIdOrden();
                    detalleordenCollectionNewDetalleorden.setIdOrden(ordenes);
                    detalleordenCollectionNewDetalleorden = em.merge(detalleordenCollectionNewDetalleorden);
                    if (oldIdOrdenOfDetalleordenCollectionNewDetalleorden != null && !oldIdOrdenOfDetalleordenCollectionNewDetalleorden.equals(ordenes)) {
                        oldIdOrdenOfDetalleordenCollectionNewDetalleorden.getDetalleordenCollection().remove(detalleordenCollectionNewDetalleorden);
                        oldIdOrdenOfDetalleordenCollectionNewDetalleorden = em.merge(oldIdOrdenOfDetalleordenCollectionNewDetalleorden);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ordenes.getIdOrden();
                if (findOrdenes(id) == null) {
                    throw new NonexistentEntityException("The ordenes with id " + id + " no longer exists.");
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
            Ordenes ordenes;
            try {
                ordenes = em.getReference(Ordenes.class, id);
                ordenes.getIdOrden();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ordenes with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Detalleorden> detalleordenCollectionOrphanCheck = ordenes.getDetalleordenCollection();
            for (Detalleorden detalleordenCollectionOrphanCheckDetalleorden : detalleordenCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ordenes (" + ordenes + ") cannot be destroyed since the Detalleorden " + detalleordenCollectionOrphanCheckDetalleorden + " in its detalleordenCollection field has a non-nullable idOrden field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Empleado idEmpleado = ordenes.getIdEmpleado();
            if (idEmpleado != null) {
                idEmpleado.getOrdenesCollection().remove(ordenes);
                idEmpleado = em.merge(idEmpleado);
            }
            Proveedores idProveedor = ordenes.getIdProveedor();
            if (idProveedor != null) {
                idProveedor.getOrdenesCollection().remove(ordenes);
                idProveedor = em.merge(idProveedor);
            }
            em.remove(ordenes);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ordenes> findOrdenesEntities() {
        return findOrdenesEntities(true, -1, -1);
    }

    public List<Ordenes> findOrdenesEntities(int maxResults, int firstResult) {
        return findOrdenesEntities(false, maxResults, firstResult);
    }

    private List<Ordenes> findOrdenesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ordenes.class));
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

    public Ordenes findOrdenes(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ordenes.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrdenesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ordenes> rt = cq.from(Ordenes.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
