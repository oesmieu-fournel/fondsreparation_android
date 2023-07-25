package com.ecosystem.mobile.reparation.repository

import com.ecosystem.mobile.reparation.service.SAPServiceManager
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets
import com.sap.cloud.mobile.odata.EntitySet
import com.sap.cloud.mobile.odata.Property

import java.util.WeakHashMap

/*
 * Repository factory to construct repository for an entity set
 */
object RepositoryFactory
/**
 * Construct a RepositoryFactory instance. There should only be one repository factory and used
 * throughout the life of the application to avoid caching entities multiple times.
 */
{
    private val repositories: WeakHashMap<String, Repository> = WeakHashMap()

    /**
     * Construct or return an existing repository for the specified entity set
     * @param entitySet - entity set for which the repository is to be returned
     * @param orderByProperty - if specified, collection will be sorted ascending with this property
     * @return a repository for the entity set
     */
    fun getRepository(entitySet: EntitySet, orderByProperty: Property?): Repository {
        val z_API_SERVICE_ORDER_SRV_Entities = SAPServiceManager.z_API_SERVICE_ORDER_SRV_Entities!!
        val key = entitySet.localName
        var repository: Repository? = repositories[key]
        if (repository == null) {
            repository = when (key) {
                EntitySets.attachmentSet.localName -> Repository(z_API_SERVICE_ORDER_SRV_Entities, EntitySets.attachmentSet, orderByProperty)
                EntitySets.customerSet.localName -> Repository(z_API_SERVICE_ORDER_SRV_Entities, EntitySets.customerSet, orderByProperty)
                EntitySets.headerContactSet.localName -> Repository(z_API_SERVICE_ORDER_SRV_Entities, EntitySets.headerContactSet, orderByProperty)
                EntitySets.headerQuerySet.localName -> Repository(z_API_SERVICE_ORDER_SRV_Entities, EntitySets.headerQuerySet, orderByProperty)
                EntitySets.headerSet.localName -> Repository(z_API_SERVICE_ORDER_SRV_Entities, EntitySets.headerSet, orderByProperty)
                EntitySets.itemSet.localName -> Repository(z_API_SERVICE_ORDER_SRV_Entities, EntitySets.itemSet, orderByProperty)
                EntitySets.partnerSet.localName -> Repository(z_API_SERVICE_ORDER_SRV_Entities, EntitySets.partnerSet, orderByProperty)
                EntitySets.pricingSet.localName -> Repository(z_API_SERVICE_ORDER_SRV_Entities, EntitySets.pricingSet, orderByProperty)
                else -> throw AssertionError("Fatal error, entity set[$key] missing in generated code")
            }
            repositories[key] = repository
        }
        return repository
    }

    /**
     * Get rid of all cached repositories
     */
    fun reset() {
        repositories.clear()
    }
}
