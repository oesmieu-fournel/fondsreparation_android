package com.ecosystem.mobile.reparation.model.files

data class FilesInformations(val data : List<FileInformation>)
data class FileInformation(val url : String, val fileName : String, val fileType : String, val bytes : ByteArray)