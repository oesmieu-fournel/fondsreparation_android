package com.ecosystem.mobile.reparation.repository

import com.ecosystem.mobile.reparation.ui.odata.data.EntityMediaResource
import com.sap.cloud.android.odata.z_api_produits_marques_srv_entities.Products
import com.sap.cloud.android.odata.z_api_produits_marques_srv_entities.Z_API_PRODUITS_MARQUES_SRV_Entities
import com.sap.cloud.android.odata.z_api_produits_marques_srv_entities.Z_API_PRODUITS_MARQUES_SRV_EntitiesMetadata
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Header
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.HeaderQuery
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Z_API_SERVICE_ORDER_SRV_Entities
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Z_API_SERVICE_ORDER_SRV_EntitiesMetadata
import com.sap.cloud.android.odata.z_business_partner_srv_entities.Contact
import com.sap.cloud.android.odata.z_business_partner_srv_entities.Repairer
import com.sap.cloud.android.odata.z_business_partner_srv_entities.Z_BUSINESS_PARTNER_SRV_Entities
import com.sap.cloud.android.odata.z_business_partner_srv_entities.Z_BUSINESS_PARTNER_SRV_EntitiesMetadata
import com.sap.cloud.mobile.odata.DataQuery
import com.sap.cloud.mobile.odata.EntitySet
import com.sap.cloud.mobile.odata.EntityValue
import com.sap.cloud.mobile.odata.FromJSON
import com.sap.cloud.mobile.odata.SortOrder
import com.sap.cloud.mobile.odata.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.slf4j.LoggerFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CustomRepository(
    private val z_API_SERVICE_ORDER_SRV_Entities: Z_API_SERVICE_ORDER_SRV_Entities,
    private val z_BUSINESS_PARTNER_SRV_Entities: Z_BUSINESS_PARTNER_SRV_Entities,
    private val z_API_PRODUITS_MARQUES_SRV_Entities: Z_API_PRODUITS_MARQUES_SRV_Entities,
) {


    suspend fun readHeaderList(
        contactNo: String,
        pageSize: Int = 40,
        page: Int = 0
    ): Flow<List<EntityValue>> = flow {

        //HeaderQuerySet?sap-client=110&sap-language=fr&%24filter=ContactNo+eq+%27500009%27+and+CreationDate+gt+%2720.04.2023%27+and+CreationDate+lt+%2720.07.2023%27
        val orderByProperty = HeaderQuery.creationDate

        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val strDateEnd: String = formatter.format(calendar.time)
        calendar.add(Calendar.MONTH, -3)
        val strDateBegin: String = formatter.format(calendar.time)
        val items = suspendCoroutine<List<EntityValue>> { continuation ->
            var dataQuery =
                DataQuery().from(Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.headerQuerySet)
                    .page(pageSize).skip(page * pageSize)
                    .filter(HeaderQuery.contactNo.equal(contactNo))
                    .filter(HeaderQuery.creationDate.greaterThan(strDateBegin))
                    .filter(HeaderQuery.creationDate.lessThan(strDateEnd))
                    .orderBy(orderByProperty, SortOrder.DESCENDING)

            orderByProperty?.also {
                dataQuery = dataQuery.orderBy(orderByProperty, SortOrder.DESCENDING)
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
                getHttpHeaders(Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.headerQuerySet)
            )
        }
        emit(items)
    }.flowOn(Dispatchers.IO)

    suspend fun readHeader(serviceOrderNumber: String): Flow<EntityValue> = flow {

        //HeaderQuerySet?sap-client=110&sap-language=fr&%24filter=ContactNo+eq+%27500009%27+and+CreationDate+gt+%2720.04.2023%27+and+CreationDate+lt+%2720.07.2023%27

        val items = suspendCoroutine { continuation ->
            var dataQuery =
                DataQuery().from(Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.headerSet)
                    .withKey(Header.key(serviceOrderNumber))
                    .expand(
                        Header.items,
                        Header.partners,
                        Header.customers,
                        Header.pricing,
                        Header.attachments
                    )
            z_API_SERVICE_ORDER_SRV_Entities.executeQueryAsync(
                dataQuery,
                { result ->
                    val entity = result.entityList[0]
                    continuation.resume(entity)
                },
                { error ->
                    LOGGER.debug("Error encountered during fetch of Category collection", error)
                    continuation.resumeWithException(error) // will propagate the throwable
                },
                getHttpHeaders(Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.headerQuerySet)
            )
        }
        emit(items)
    }.flowOn(Dispatchers.IO)


    suspend fun updateHeaderStatus(serviceOrderNumber: String, status : String): Repository.SuspendOperationResult
    {
        return suspendCoroutine { continuation ->
            z_API_SERVICE_ORDER_SRV_Entities.updateStatusAsync(
                status,
                serviceOrderNumber,
                null,
                { headerEntity ->
                    val operationSuccess: Repository.SuspendOperationResult =
                    Repository.SuspendOperationResult.SuspendOperationSuccess(
                        headerEntity
                    )
                    continuation.resume(operationSuccess)
                },
                { error ->
                    LOGGER.debug("Error encountered during fetch of Category collection", error)
                    val operationFail =
                    Repository.SuspendOperationResult.SuspendOperationFail(error)
                    continuation.resume(operationFail) // will propagate the throwable
                },
            )
        }
    }

    suspend fun readPartner(contactNo: String): Flow<EntityValue> = flow {
        ///Z_BUSINESS_PARTNER_SRV/ContactSet('contactNo')?sap-client=110&sap-language=fr&%24expand=ToRepairer/ToRepairPlaces,ToRepairer/ToSubContractor
        val items = suspendCoroutine { continuation ->
            var dataQuery =
                DataQuery().from(Z_BUSINESS_PARTNER_SRV_EntitiesMetadata.EntitySets.contactSet)
                    .withKey(Contact.key(contactNo))
                    .expand(Contact.toRepairer)
                    .expand(Contact.toRepairer.path(Repairer.toRepairPlaces))
                    .expand(Contact.toRepairer.path(Repairer.toSubContractor))

            z_BUSINESS_PARTNER_SRV_Entities.executeQueryAsync(
                dataQuery,
                { result ->
                    val entity = result.entityList[0]
                    continuation.resume(entity)
                },
                { error ->
                    LOGGER.debug("Error encountered during fetch of Category collection", error)
                    continuation.resumeWithException(error) // will propagate the throwable
                },
                getHttpHeaders(Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.headerQuerySet)
            )
        }
        emit(items)
    }.flowOn(Dispatchers.IO)


    suspend fun readProducts(): Flow<List<EntityValue>> = flow {
        // /sap/opu/odata/sap/Z_API_PRODUITS_MARQUES_SRV/ProductSet/?sap-client=110&sap-language=fr&%24expand=ToDefect%2CToBrand
        val items = suspendCoroutine { continuation ->
            var dataQuery =
                DataQuery().from(Z_API_PRODUITS_MARQUES_SRV_EntitiesMetadata.EntitySets.productSet)
                    .expand(Products.toDefect)
                    .expand(Products.toBrand)
                    .expand(Products.toAmount)

            z_API_PRODUITS_MARQUES_SRV_Entities.executeQueryAsync(
                dataQuery,
                { result ->
                    val entitiesRead = result.entityList.toList()
                    continuation.resume(entitiesRead)
                },
                { error ->
                    LOGGER.debug("Error encountered during fetch of Category collection", error)
                    val operationFail =
                    Repository.SuspendOperationResult.SuspendOperationFail(error)
                    continuation.resumeWithException(error) // will propagate the throwable
                },
                getHttpHeaders(Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.headerQuerySet)
            )
        }
        emit(items)
    }.flowOn(Dispatchers.IO)

    suspend fun suspendCreateUpdateHeader(entity: String): Repository.SuspendOperationResult {

        val query = FromJSON.entity(entity)
            .from(Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.headerSet)

        val headerEntity =
            z_API_SERVICE_ORDER_SRV_Entities.getHeader(query)

        headerEntity.isNew = true
        headerEntity.partners = headerEntity.partners.map {
            it.isNew = true
            it
        }.toMutableList()

        headerEntity.customers = headerEntity.customers.map {
            it.isNew = true
            it
        }.toMutableList()

        headerEntity.items = headerEntity.items.map {
            it.isNew = true
            it
        }.toMutableList()

        headerEntity.pricing = headerEntity.pricing.map {
            it.isNew = true
            it
        }.toMutableList()

        headerEntity.attachments = headerEntity.attachments.map {
            it.isNew = true
            it
        }.toMutableList()

        return suspendCoroutine { continuation ->
            z_API_SERVICE_ORDER_SRV_Entities.createEntityAsync(headerEntity,
                {
                    val operationSuccess: Repository.SuspendOperationResult =
                        Repository.SuspendOperationResult.SuspendOperationSuccess(
                            headerEntity
                        )
                    continuation.resume(operationSuccess)
                },
                { error ->
                    LOGGER.debug("Entity creation failed:", error)
                    val operationFail =
                        Repository.SuspendOperationResult.SuspendOperationFail(error)
                    continuation.resume(operationFail)
                })
        }
    }

    private fun needFullMetadata(entitySet: EntitySet): Boolean {
        return EntityMediaResource.isV4(z_API_SERVICE_ORDER_SRV_Entities.metadata.versionCode) && EntityMediaResource.hasMediaResources(
            entitySet
        )
    }

    /*suspend fun putS3Object(bucketName: String, objectKey: String, objectPath: String) {

       *//* val metadataVal = mutableMapOf<String, String>()
        metadataVal["myVal"] = "test"*//*

        val request = PutObjectRequest {
            bucket = bucketName
            key = objectKey
            //metadata = metadataVal
            body = File(objectPath).asByteStream()
        }

        S3Client { region = "us-east-1" }.use { s3 ->
            val response = s3.putObject(request)
            println("Tag information is ${response.eTag}")
        }
    }*/

    private fun getHttpHeaders(entitySet: EntitySet): HttpHeaders {
        val httpHeaders: HttpHeaders
        if (needFullMetadata(entitySet)) {
            httpHeaders = HttpHeaders()
            httpHeaders.set("Accept", "application/json;odata.metadata=full")
        } else {
            httpHeaders = HttpHeaders.empty
        }
        return httpHeaders
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CustomRepository::class.java)
    }

}