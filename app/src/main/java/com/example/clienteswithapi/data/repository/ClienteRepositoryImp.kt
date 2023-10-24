package com.example.clienteswithapi.data.repository

import com.example.clienteswithapi.data.remote.ClientesApi
import com.example.clienteswithapi.data.remote.dto.ClienteDto
import com.example.clienteswithapi.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ClienteRepositoryImp @Inject constructor(
    private val api: ClientesApi
): ClienteRepository {
    override fun getClientes(): Flow<Resource<List<ClienteDto>>> = flow {
        try {
            emit(Resource.Loading()) //indicar que estamos cargando

            val cliente = api.getClientes() //descarga los cliente de internet, se supone quedemora algo

            emit(Resource.Success(cliente)) //indicar que se cargo correctamente
        } catch (e: HttpException) {
            //error general HTTP
            emit(Resource.Error(e.message ?: "Error HTTP GENERAL"))
        } catch (e: IOException) {
            //debe verificar tu conexion a internet
            emit(Resource.Error(e.message ?: "verificar tu conexion a internet"))
        }
    }

    override  fun getClienteId(id: Int): Flow<Resource<ClienteDto>> = flow {
        try {
            emit(Resource.Loading())

            val clientes =
                api.getClienteId(id)

            emit(Resource.Success(clientes))
        } catch (e: HttpException) {
            //error general HTTP
            emit(Resource.Error(e.message ?: "Error HTTP GENERAL"))
        } catch (e: IOException) {
            //debe verificar tu conexion a internet
            emit(Resource.Error(e.message ?: "verificar tu conexion a internet"))
        }
    }


    override suspend fun deleteClientes(id: Int) {
        api.deleteClientes(id)
    }
    override suspend fun postCliente(clienteDto: ClienteDto) {

        api.postCliente(clienteDto)

    }


}