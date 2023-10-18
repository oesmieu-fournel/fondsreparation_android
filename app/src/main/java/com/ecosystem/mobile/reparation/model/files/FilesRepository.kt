package com.ecosystem.mobile.reparation.model.files

interface FilesRepository {

    suspend fun uploadFiles(serviceOrder: String, filesInformations: FilesInformations) : FilesInformations
}