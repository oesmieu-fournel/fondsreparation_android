package com.ecosystem.mobile.reparation.dev.repository

import com.ecosystem.mobile.reparation.dev.ui.odata.data.EntityMediaResource
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.HeaderQuery

import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Z_API_SERVICE_ORDER_SRV_Entities

import com.sap.cloud.mobile.odata.*
import com.sap.cloud.mobile.odata.http.HttpHeaders

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

import org.slf4j.LoggerFactory


/**
 * Repository with specific EntitySet as parameter.
 * In other words, each entity set has its own repository and an in-memory store of all the entities
 * of that type.
 * Repository exposed the list of entities as paging data flow
 * @param z_API_SERVICE_ORDER_SRV_Entities OData service
 * @param entitySet entity set associated with this repository
 * @param orderByProperty used to order the collection retrieved from OData service
 */
class Repository(
        private val z_API_SERVICE_ORDER_SRV_Entities: Z_API_SERVICE_ORDER_SRV_Entities,
        private val entitySet: EntitySet,
        private val orderByProperty: Property?) {
    /*
     * Indicate if metadata=full parameter needs to be set during query for the entity set
     * V4 and higher OData version services do not return metadata as part of the result preventing the
     * the construction of download url for use by Glide.
     */
    private var needFullMetadata = false

    /**
     * Return a suitable HttpHeader based on whether full metadata parameter is required
     * @return HttpHeader for query
     */
    private val httpHeaders: HttpHeaders
        get() {
            val httpHeaders: HttpHeaders
            if (needFullMetadata) {
                httpHeaders = HttpHeaders()
                httpHeaders.set("Accept", "application/json;odata.metadata=full")
            } else {
                httpHeaders = HttpHeaders.empty
            }
            return httpHeaders
        }
	
    init {
       
        if (EntityMediaResource.isV4(z_API_SERVICE_ORDER_SRV_Entities.metadata.versionCode) && EntityMediaResource.hasMediaResources(entitySet)) {
            needFullMetadata = true
        }
    }


	suspend fun read(pageSize: Int = 40, page: Int = 0): Flow<List<EntityValue>> = flow {

		//HeaderQuerySet?sap-client=110&sap-language=fr&%24filter=ContactNo+eq+%27500009%27+and+CreationDate+gt+%2720.04.2023%27+and+CreationDate+lt+%2720.07.2023%27

	    val items = suspendCoroutine<List<EntityValue>> { continuation ->
	        var dataQuery = DataQuery().from(entitySet).page(pageSize).skip(page * pageSize)
	        //var dataQuery = DataQuery().from(entitySet)
				.filter(HeaderQuery.contactNo.equal("500009"))
				.filter(HeaderQuery.creationDate.greaterThan("20.04.2023"))
				.filter(HeaderQuery.creationDate.lessThan("20.07.2023"))

	        orderByProperty?.also {
	            dataQuery = dataQuery.orderBy(orderByProperty, SortOrder.ASCENDING)
	        }
	        z_API_SERVICE_ORDER_SRV_Entities.executeQueryAsync(
	            dataQuery,
	            { result ->
	                val entitiesRead = result.entityList.toList()
	                continuation.resume(entitiesRead)
	            },
	            { error ->
	                LOGGER.debug("Error encountered during fetch of Category collection", error)
	                continuation.resumeWithException(error) // will propagate the throwable
	            },
	            httpHeaders
	        )
	    }
	    emit(items)
	}.flowOn(Dispatchers.IO)

	suspend fun read(
	    parent: EntityValue,
	    navPropertyName: String,
	    pageSize: Int = 40,
	    page: Int = 0
	): Flow<List<EntityValue>> = flow {
	    val items = if (!parent.hasKey()) { // skip local parent entity
	        listOf()
	    } else {
	        suspendCoroutine<List<EntityValue>> { continuation ->
	            val navigationProperty = parent.entityType.getProperty(navPropertyName)
	            val dataQuery = DataQuery()
	            if (navigationProperty.isCollection) {
	                dataQuery.page(pageSize).skip(page * pageSize)
	                orderByProperty?.also {
	                    dataQuery.orderBy(
	                        orderByProperty,
	                        SortOrder.ASCENDING
	                    )
	                }
	            }
	            z_API_SERVICE_ORDER_SRV_Entities.loadPropertyAsync(
	                navigationProperty, parent, dataQuery,
	                {
	                    val relatedData = parent.getOptionalValue(navigationProperty)
	                    val entities = mutableListOf<EntityValue>()
	                    when (navigationProperty.dataType.code) {
	                        DataType.ENTITY_VALUE_LIST -> entities.addAll((relatedData as EntityValueList?)!!.toList())
	                        DataType.ENTITY_VALUE -> if (relatedData != null) {
	                            val entity = relatedData as EntityValue
	                            entities.add(entity)
	                        }
	                    }
	                    continuation.resume(entities)
	                },
	                { error ->
	                    LOGGER.debug("Error encountered during fetch of Category collection", error)
	                    continuation.resumeWithException(error)
	                },
		            httpHeaders
	            )
	        }
	    }
	    emit(items)
	}.flowOn(Dispatchers.IO)
	
	sealed class SuspendOperationResult {
	    data class SuspendOperationSuccess(val newEntity: EntityValue? = null) :
	        SuspendOperationResult()

	    data class SuspendOperationFail(val error: Exception) : SuspendOperationResult()
	}

	suspend fun suspendCreate(newEntity: EntityValue, media: StreamBase): SuspendOperationResult {
	    return suspendCoroutine<SuspendOperationResult> { continuation ->
	        if (newEntity.entityType.isMedia) {
	            z_API_SERVICE_ORDER_SRV_Entities.createMediaAsync(newEntity, media,
	                {
	                    val operationSuccess: SuspendOperationResult =
	                        SuspendOperationResult.SuspendOperationSuccess(
	                            newEntity
	                        )
	                    continuation.resume(operationSuccess)
	                },
	                { error ->
	                    LOGGER.debug("Media Linked Entity creation failed:", error)
	                    val operationFail = SuspendOperationResult.SuspendOperationFail(error)
	                    continuation.resume(operationFail)
	                })
	        }
	    }
	}
	
	suspend fun suspendCreate(newEntity: EntityValue): SuspendOperationResult {
	    if (newEntity.entityType.isMedia) {
	        return SuspendOperationResult.SuspendOperationFail(IllegalStateException("Specify media resource for Media Linked Entity"))
	    }
	    return suspendCoroutine<SuspendOperationResult> { continuation ->
	        z_API_SERVICE_ORDER_SRV_Entities.createEntityAsync(newEntity,
	            {
	                val operationSuccess: SuspendOperationResult =
	                    SuspendOperationResult.SuspendOperationSuccess(
	                        newEntity
	                    )
	                continuation.resume(operationSuccess)
	            },
	            { error ->
	                LOGGER.debug("Entity creation failed:", error)
	                val operationFail = SuspendOperationResult.SuspendOperationFail(error)
	                continuation.resume(operationFail)
	            })
	    }
	}

	suspend fun suspendUpdate(updateEntity: EntityValue): SuspendOperationResult {
	    return suspendCoroutine<SuspendOperationResult> { continuation ->
	        z_API_SERVICE_ORDER_SRV_Entities.updateEntityAsync(updateEntity,
	            {
	                val operationSuccess: SuspendOperationResult =
	                    SuspendOperationResult.SuspendOperationSuccess(
	                        updateEntity
	                    )
	                continuation.resume(operationSuccess)
	            },
	            { error ->
	                LOGGER.debug("Entity update failed:", error)
	                val operationFail = SuspendOperationResult.SuspendOperationFail(error)
	                continuation.resume(operationFail)
	            })
	    }
	}

	suspend fun suspendDelete(deleteEntities: List<EntityValue>): SuspendOperationResult {
	    val deleteChangeSet = ChangeSet()
	    for (entityToDelete in deleteEntities) {
	        deleteChangeSet.deleteEntity(entityToDelete)
	    }
	    return suspendCoroutine<SuspendOperationResult> { continuation ->
	        z_API_SERVICE_ORDER_SRV_Entities.applyChangesAsync(deleteChangeSet,
	            {
	                val operationSuccess: SuspendOperationResult =
	                    SuspendOperationResult.SuspendOperationSuccess()
	                continuation.resume(operationSuccess)
	            },
	            { error ->
	                LOGGER.debug("Entity delete failed:", error)
	                val operationFail = SuspendOperationResult.SuspendOperationFail(error)
	                continuation.resume(operationFail)
	            })
	    }
	}



    companion object {
        private val LOGGER = LoggerFactory.getLogger(Repository::class.java)
    }
}
