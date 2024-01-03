package com.ecosystem.mobile.reparation.dev.ui.odata.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.sap.cloud.mobile.flows.compose.db.UserSecureStoreDelegate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class EntitySetViewModel(application: Application) : BaseOperationViewModel(application) {

    private val _isMultipleUserMode = MutableStateFlow<Boolean>(false)
    val isMultipleUserMode = _isMultipleUserMode.asStateFlow()

    init {
        viewModelScope.launch {
            _isMultipleUserMode.update {
                UserSecureStoreDelegate.getInstance().getRuntimeMultipleUserMode()
            }
        }
    }

}
