package com.example.clienteswithapi.data.repository

import com.example.clienteswithapi.data.remote.dto.ClienteDto
import com.example.clienteswithapi.util.Resource
import kotlinx.coroutines.flow.Flow

interface ClienteRepository {

    fun getClientes(): Flow<Resource<List<ClienteDto>>>

    fun getClienteId(id: Int): Flow<Resource<ClienteDto>>

    suspend fun deleteClientes(id: Int)

    suspend fun postCliente(clienteDto: ClienteDto)
}