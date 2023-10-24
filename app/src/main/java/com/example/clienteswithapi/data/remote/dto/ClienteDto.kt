package com.example.clienteswithapi.data.remote.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class ClienteDto(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("nombres")
    var nombres: String = "",
    @SerializedName("telefono")
    var telefono: String = "",
    @SerializedName("celular")
    var celular: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("fechaNac")
    var fechaNac: String = "",
    @SerializedName("Ocupacion")
    var Ocupacion: String = ""
)


