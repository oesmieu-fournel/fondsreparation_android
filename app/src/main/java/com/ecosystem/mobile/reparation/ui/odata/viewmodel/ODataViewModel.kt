package com.ecosystem.mobile.reparation.ui.odata.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.ecosystem.mobile.reparation.R
import com.ecosystem.mobile.reparation.repository.Repository
import com.ecosystem.mobile.reparation.repository.RepositoryFactory
import com.ecosystem.mobile.reparation.util.Converter
import com.ecosystem.mobile.reparation.ui.odata.data.EntityPageSource
import com.ecosystem.mobile.reparation.ui.odata.screens.OperationResult
import com.ecosystem.mobile.reparation.ui.odata.screens.FieldUIState
import com.sap.cloud.mobile.odata.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

const val PAGE_SIZE: Int = 20

open class ODataViewModel(
    application: Application,
    val entitySet: EntitySet,
    private val orderByProperty: Property?,
    open val parent: EntityValue? = null,
    private val navigationPropertyName: String? = null,
) : BaseOperationViewModel(application) {
    //    val entitySet: EntitySet get() = _entitySet
    private val repository: Repository =
        RepositoryFactory.getRepository(entitySet, orderByProperty)

    val pagingDataState =
        mutableStateOf<Flow<PagingData<EntityValue>>>(flowOf(PagingData.empty()))

    private val _masterEntity = MutableStateFlow(getDefaultEmptyEntity())
    val masterEntity = _masterEntity.asStateFlow()

    private val _selectItems = MutableStateFlow(listOf<EntityValue>())
    val selectItems = _selectItems.asStateFlow()

    private val pagingSourceFactory =
        InvalidatingPagingSourceFactory<Int, EntityValue> {
            EntityPageSource(
                entitySet = entitySet,
                orderByProperty,
                PAGE_SIZE,
                parent,
                navigationPropertyName
            )
        }

    init {
        pagingDataState.value = retrieveEntities()
    }

    private fun retrieveEntities(): Flow<PagingData<EntityValue>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = true),
        pagingSourceFactory = pagingSourceFactory
    ).flow.cachedIn(viewModelScope)

    fun onEntitySelection(entity: EntityValue) {
        _selectItems.getAndUpdate {
            if (it.contains(entity)) {
                it - entity
            } else {
                it + entity
            }
        }
    }

    //delete selected entities in list screen
    fun deleteSelected() {
        deleteEntities(_selectItems.value)
        resetSelection()
    }

    // delete master entity in details screen
    fun onDeleteEntity() {
        deleteEntities(listOf(masterEntity.value))
    }

    private fun deleteEntities(entities: List<EntityValue>) {
        viewModelScope.launch() {
            operationStart()
            when (val operationResult =
                repository.suspendDelete(entities)) {
                is Repository.SuspendOperationResult.SuspendOperationSuccess -> {
                    refreshEntities()
                    operationFinished(result = OperationResult.OperationSuccess("Delete Success"))
                }
                is Repository.SuspendOperationResult.SuspendOperationFail -> {
                    operationFinished(
                        result = OperationResult.OperationFail(
                            operationResult.error.message ?: "Delete fail"
                        )
                    )
                }
            }
        }
    }

    fun setDefaultMasterEntity() {
        _masterEntity.update { getDefaultEmptyEntity() }
    }

    fun setMasterEntity(entity: EntityValue) {
        _masterEntity.update { entity }
    }

    private fun getDefaultEmptyEntity(): EntityValue {
        return entitySet.entityType.objectFactory!!.create() as EntityValue
    }

    fun refreshEntities() {
        Log.d("operation-refresh", "set refresh entities state")
        pagingSourceFactory.invalidate()
    }

    fun onSaveAction(
        entity: EntityValue,
        propValuePairs: List<Pair<Property, String>>
    ): List<Converter.ConvertResult.ConvertError> {
        val result = Converter.populateEntityWithPropertyValue(entity, propValuePairs)
        if (result.isEmpty()) {
            viewModelScope.launch() {
                operationStart()
                val isCreation = !entity.hasKey()
                when (val operationResult =
                    if (isCreation) {
                        if (entity.entityType.isMedia) {
                            repository.suspendCreate(entity, defaultMediaResource)
                        } else {
                            repository.suspendCreate(entity)
                        }
                    } else {
                        repository.suspendUpdate(entity)
                    }) {
                    is Repository.SuspendOperationResult.SuspendOperationSuccess -> {
                        refreshEntities()
                        operationFinished(result = OperationResult.OperationSuccess("${if (isCreation) "Create" else "Update"} Success"))
                    }
                    is Repository.SuspendOperationResult.SuspendOperationFail -> {
                        operationFinished(
                            result = OperationResult.OperationFail(
                                operationResult.error.message
                                    ?: "${if (isCreation) "Create" else "Update"} Fail"
                            )
                        )
                    }
                }
            }
        }
        return result
    }

    fun resetSelection() {
        _selectItems.value = listOf()
    }

    fun validateField(
        fieldUIState: FieldUIState,
        newValue: String
    ): FieldUIState {
        var newState = fieldUIState
        val property = fieldUIState.property
        if (!property.isNullable && newValue.isEmpty()) { // check if mandatory
            return newState.copy(
                isError = true,
                errorMessage = getApplication<Application>().getString(R.string.mandatory_warning),
                value = newValue
            )
        } else if (newValue.isNotEmpty()) { // check if property type valid input
            val convertResult = Converter.convert(property, newValue)
            if (convertResult is Converter.ConvertResult.ConvertError) {
                return newState.copy(
                    isError = true,
                    errorMessage = getApplication<Application>().getString(R.string.format_error),
                    value = newValue
                )
            }
        }

        //con max length
        val maxLength = property.maxLength
        return if (maxLength > 0 && newValue.length > maxLength) {
            newState.copy(value = newValue.substring(0, maxLength), isError = false)
        } else {
            newState.copy(value = newValue, isError = false)
        }

    }

    //for list view
    open fun getAvatarText(entity: EntityValue?): String {
        val entityPrincipleData =
            orderByProperty?.let { entity?.getOptionalValue(orderByProperty).toString() }
        return if (entityPrincipleData?.isNotEmpty() == true) {
            entityPrincipleData.take(1)
        } else {
            "?"
        }
    }

    open fun getEntityTitle(entity: EntityValue): String {
        val title =
            orderByProperty?.let { entity.getOptionalValue(orderByProperty).toString() }
        return if (title?.isNotEmpty() == true) {
            title
        } else {
            "???"
        }
    }

    private val defaultMediaResource: StreamBase
        get() {
            val inputStream = getApplication<Application>().resources.openRawResource(R.raw.blank)
            val byteStream = ByteStream.fromInput(inputStream)
            byteStream.mediaType = "image/png"
            return byteStream
        }


}
