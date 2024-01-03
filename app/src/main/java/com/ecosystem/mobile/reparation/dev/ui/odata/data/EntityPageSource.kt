package com.ecosystem.mobile.reparation.dev.ui.odata.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ecosystem.mobile.reparation.dev.repository.RepositoryFactory
import com.sap.cloud.mobile.odata.EntitySet
import com.sap.cloud.mobile.odata.EntityValue
import com.sap.cloud.mobile.odata.Property
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory

class EntityPageSource(
    private val entitySet: EntitySet,
    private val orderBy: Property?,
    private val pageSize: Int,
    private val parentEntity: EntityValue? = null,
    private val navPropName: String? = null,
) : PagingSource<Int, EntityValue>() {

    private val repository by lazy {
        RepositoryFactory.getRepository(entitySet, orderBy)
    }

    override fun getRefreshKey(state: PagingState<Int, EntityValue>): Int? {
        val refreshKey = state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }

        LOGGER.debug("Get refresh key $refreshKey")
        return refreshKey
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EntityValue> {
        val nextPageNo = params.key ?: 0
        var items = listOf<EntityValue>()
        if (navPropName == null) {
            loadEntities(pageSize, nextPageNo)
        } else {
            loadNavigatedEntities(pageSize, nextPageNo, parentEntity!!, navPropName)
        }.collect {
            items = it
        }
        LOGGER.debug("load with params $params")
        return LoadResult.Page(
            data = items,
            prevKey = if (nextPageNo <= 0) null else nextPageNo - 1,
            nextKey = if (items.size < pageSize) null else {
                val pages = items.size / pageSize
                val remainder = items.size % pageSize
                if (remainder > 0) null else {
                    nextPageNo + pages
                }
            }
        )
    }

    private suspend fun loadEntities(
        pageSize: Int,
        page: Int
    ): Flow<List<EntityValue>> {
        return repository.read(
            pageSize = pageSize, page = page
        )
    }

    private suspend fun loadNavigatedEntities(
        pageSize: Int,
        page: Int,
        parent: EntityValue,
        navProperty: String
    ): Flow<List<EntityValue>> {
        return repository.read(
            pageSize = pageSize, page = page, navPropertyName = navProperty, parent = parent
        )
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(EntityPageSource::class.java)
    }

}

