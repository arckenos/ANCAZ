/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistencia;

import Vistas.exceptions.IllegalOrphanException;
import Vistas.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Ordenes;
import java.util.ArrayList;
import java.util.Collection;
import entidades.Articulos;
import entidades.Proveedores;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Arcke
 */
public class ProveedoresJpaController implements Serializable {

    public ProveedoresJpaController() {
        this.emf = Persistence.createEntityManagerFactory("InventariosANCAZPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Proveedores proveedores) {
        if (proveedores.getOrdenesCollection() == null) {
            proveedores.setOrdenesCollection(new ArrayList<Ordenes>());
        }
        if (proveedores.getArticulosCollection() == null) {
            proveedores.setArticulosCollection(new ArrayList<Articulos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Ordenes> attachedOrdenesCollection = new ArrayList<Ordenes>();
            for (Ordenes ordenesCollectionOrdenesToAttach : proveedores.getOrdenesCollection()) {
                ordenesCollectionOrdenesToAttach = em.getReference(ordenesCollectionOrdenesToAttach.getClass(), ordenesCollectionOrdenesToAttach.getIdOrden());
                attachedOrdenesCollection.add(ordenesCollectionOrdenesToAttach);
            }
            proveedores.setOrdenesCollection(attachedOrdenesCollection);
            Collection<Articulos> attachedArticulosCollection = new ArrayList<Articulos>();
            for (Articulos articulosCollectionArticulosToAttach : proveedores.getArticulosCollection()) {
                articulosCollectionArticulosToAttach = em.getReference(articulosCollectionArticulosToAttach.getClass(), articulosCollectionArticulosToAttach.getIdArticulo());
                attachedArticulosCollection.add(articulosCollectionArticulosToAttach);
            }
            proveedores.setArticulosCollection(attachedArticulosCollection);
            em.persist(proveedores);
            for (Ordenes ordenesCollectionOrdenes : proveedores.getOrdenesCollection()) {
                Proveedores oldIdProveedorOfOrdenesCollectionOrdenes = ordenesCollectionOrdenes.getIdProveedor();
                ordenesCollectionOrdenes.setIdProveedor(proveedores);
                ordenesCollectionOrdenes = em.merge(ordenesCollectionOrdenes);
                if (oldIdProveedorOfOrdenesCollectionOrdenes != null) {
                    oldIdProveedorOfOrdenesCollectionOrdenes.getOrdenesCollection().remove(ordenesCollectionOrdenes);
                    oldIdProveedorOfOrdenesCollectionOrdenes = em.merge(oldIdProveedorOfOrdenesCollectionOrdenes);
                }
            }
            for (Articulos articulosCollectionArticulos : proveedores.getArticulosCollection()) {
                Proveedores oldIdProveedorOfArticulosCollectionArticulos = articulosCollectionArticulos.getIdProveedor();
                articulosCollectionArticulos.setIdProveedor(proveedores);
                articulosCollectionArticulos = em.merge(articulosCollectionArticulos);
                if (oldIdProveedorOfArticulosCollectionArticulos != null) {
                    oldIdProveedorOfArticulosCollectionArticulos.getArticulosCollection().remove(articulosCollectionArticulos);
                    oldIdProveedorOfArticulosCollectionArticulos = em.merge(oldIdProveedorOfArticulosCollectionArticulos);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Proveedores proveedores) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proveedores persistentProveedores = em.find(Proveedores.class, proveedores.getIdProveedor());
            Collection<Ordenes> ordenesCollectionOld = persistentProveedores.getOrdenesCollection();
            Collection<Ordenes> ordenesCollectionNew = proveedores.getOrdenesCollection();
            Collection<Articulos> articulosCollectionOld = persistentProveedores.getArticulosCollection();
            Collection<Articulos> articulosCollectionNew = proveedores.getArticulosCollection();
            List<String> illegalOrphanMessages = null;
            for (Ordenes ordenesCollectionOldOrdenes : ordenesCollectionOld) {
                if (!ordenesCollectionNew.contains(ordenesCollectionOldOrdenes)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ordenes " + ordenesCollectionOldOrdenes + " since its idProveedor field is not nullable.");
                }
            }
            for (Articulos articulosCollectionOldArticulos : articulosCollectionOld) {
                if (!articulosCollectionNew.contains(articulosCollectionOldArticulos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Articulos " + articulosCollectionOldArticulos + " since its idProveedor field is not nullable.");
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
            proveedores.setOrdenesCollection(ordenesCollectionNew);
            Collection<Articulos> attachedArticulosCollectionNew = new ArrayList<Articulos>();
            for (Articulos articulosCollectionNewArticulosToAttach : articulosCollectionNew) {
                articulosCollectionNewArticulosToAttach = em.getReference(articulosCollectionNewArticulosToAttach.getClass(), articulosCollectionNewArticulosToAttach.getIdArticulo());
                attachedArticulosCollectionNew.add(articulosCollectionNewArticulosToAttach);
            }
            articulosCollectionNew = attachedArticulosCollectionNew;
            proveedores.setArticulosCollection(articulosCollectionNew);
            proveedores = em.merge(proveedores);
            for (Ordenes ordenesCollectionNewOrdenes : ordenesCollectionNew) {
                if (!ordenesCollectionOld.contains(ordenesCollectionNewOrdenes)) {
                    Proveedores oldIdProveedorOfOrdenesCollectionNewOrdenes = ordenesCollectionNewOrdenes.getIdProveedor();
                    ordenesCollectionNewOrdenes.setIdProveedor(proveedores);
                    ordenesCollectionNewOrdenes = em.merge(ordenesCollectionNewOrdenes);
                    if (oldIdProveedorOfOrdenesCollectionNewOrdenes != null && !oldIdProveedorOfOrdenesCollectionNewOrdenes.equals(proveedores)) {
                        oldIdProveedorOfOrdenesCollectionNewOrdenes.getOrdenesCollection().remove(ordenesCollectionNewOrdenes);
                        oldIdProveedorOfOrdenesCollectionNewOrdenes = em.merge(oldIdProveedorOfOrdenesCollectionNewOrdenes);
                    }
                }
            }
            for (Articulos articulosCollectionNewArticulos : articulosCollectionNew) {
                if (!articulosCollectionOld.contains(articulosCollectionNewArticulos)) {
                    Proveedores oldIdProveedorOfArticulosCollectionNewArticulos = articulosCollectionNewArticulos.getIdProveedor();
                    articulosCollectionNewArticulos.setIdProveedor(proveedores);
                    articulosCollectionNewArticulos = em.merge(articulosCollectionNewArticulos);
                    if (oldIdProveedorOfArticulosCollectionNewArticulos != null && !oldIdProveedorOfArticulosCollectionNewArticulos.equals(proveedores)) {
                        oldIdProveedorOfArticulosCollectionNewArticulos.getArticulosCollection().remove(articulosCollectionNewArticulos);
                        oldIdProveedorOfArticulosCollectionNewArticulos = em.merge(oldIdProveedorOfArticulosCollectionNewArticulos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = proveedores.getIdProveedor();
                if (findProveedores(id) == null) {
                    throw new NonexistentEntityException("The proveedores with id " + id + " no longer exists.");
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
            Proveedores proveedores;
            try {
                proveedores = em.getReference(Proveedores.class, id);
                proveedores.getIdProveedor();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The proveedores with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Ordenes> ordenesCollectionOrphanCheck = proveedores.getOrdenesCollection();
            for (Ordenes ordenesCollectionOrphanCheckOrdenes : ordenesCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Proveedores (" + proveedores + ") cannot be destroyed since the Ordenes " + ordenesCollectionOrphanCheckOrdenes + " in its ordenesCollection field has a non-nullable idProveedor field.");
            }
            Collection<Articulos> articulosCollectionOrphanCheck = proveedores.getArticulosCollection();
            for (Articulos articulosCollectionOrphanCheckArticulos : articulosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Proveedores (" + proveedores + ") cannot be destroyed since the Articulos " + articulosCollectionOrphanCheckArticulos + " in its articulosCollection field has a non-nullable idProveedor field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(proveedores);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Proveedores> findProveedoresEntities() {
        return findProveedoresEntities(true, -1, -1);
    }

    public List<Proveedores> findProveedoresEntities(int maxResults, int firstResult) {
        return findProveedoresEntities(false, maxResults, firstResult);
    }

    private List<Proveedores> findProveedoresEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Proveedores.class));
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

    public Proveedores findProveedores(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Proveedores.class, id);
        } finally {
            em.close();
        }
    }

    public int getProveedoresCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Proveedores> rt = cq.from(Proveedores.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
