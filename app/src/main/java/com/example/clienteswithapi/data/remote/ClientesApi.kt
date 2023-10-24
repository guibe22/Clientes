package com.example.clienteswithapi.data.remote

import com.example.clienteswithapi.data.remote.dto.ClienteDto
import retrofit2.Response
import retrofit2.http.*
interface ClientesApi{
    @GET("/Clientes")
    suspend fun getClientes(): List<ClienteDto>

    @GET("/clientes/{id}")
    suspend fun getClienteId(@Path("id") id: Int): ClienteDto

    @POST("/clientes")
    suspend fun postCliente(@Body Cliente: ClienteDto): Response<ClienteDto>


    @DELETE("/clientes/{id}")
    suspend fun deleteClientes(@Path("id") id: Int): Response<Unit>
}