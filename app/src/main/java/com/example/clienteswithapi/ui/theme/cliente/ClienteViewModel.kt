package com.example.clienteswithapi.ui.theme.cliente

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clienteswithapi.data.remote.dto.ClienteDto
import com.example.clienteswithapi.data.repository.ClienteRepositoryImp
import com.example.clienteswithapi.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClienteListState(
    val isLoading: Boolean = false,
    val clientes: List<ClienteDto> = emptyList(),
    val error: String = ""
)

data class ClienteState(
    val isLoading: Boolean = false,
    val cliente: ClienteDto? = null,
    val error: String = ""
)
@HiltViewModel
class ClienteViewModel @Inject constructor(
    private val Repository: ClienteRepositoryImp
) : ViewModel()  {
    var nombre by mutableStateOf("")
    var nombreError by mutableStateOf(false)

    var telefono by mutableStateOf("")
    var telefonoError by mutableStateOf(false)

    var celular by mutableStateOf("")
    var celularError by mutableStateOf(false)

    var email by mutableStateOf("")
    var emailError by mutableStateOf(false)

    var fechaNac by mutableStateOf("")
    var fechaNacError by mutableStateOf(false)

    var ocupacion by mutableStateOf("")
    var ocupacionError by mutableStateOf(false)

    private val _isMessageShown = MutableSharedFlow<Boolean>()
    val isMessageShownFlow = _isMessageShown.asSharedFlow()
    var mensaje by mutableStateOf("")

    fun setMessageShown() {
        viewModelScope.launch {
            _isMessageShown.emit(true)
        }
    }
    fun onNombreChanged(valor: String) {
        nombre = valor
        nombreError = valor.isBlank();
    }
    fun onTelefonoChanged(valor: String) {
        telefono= valor
        telefonoError = valor.isBlank();
    }

    fun oncelularChanged(valor: String) {
        celular= valor
        celularError = valor.isBlank();
    }
    fun onEmailChanged(valor: String) {
        email= valor
        emailError = valor.isBlank();
    }


    fun onfechaNacChanged(valor: String) {
        fechaNac= valor
        fechaNacError = valor.isBlank();
    }
    fun onOcupacionChanged(valor: String) {
        ocupacion= valor
        ocupacionError = valor.isBlank();
    }
     fun Validar():Boolean{
         onNombreChanged(nombre)
         onTelefonoChanged(telefono)
         oncelularChanged(celular)
         onEmailChanged(email)
         onfechaNacChanged(fechaNac)
         onOcupacionChanged(ocupacion)

         return ocupacionError && nombreError && fechaNacError && emailError && telefonoError && celularError
    }


    var clientes: StateFlow<Resource<List<ClienteDto>>> = Repository.getClientes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Resource.Loading()
    )
    fun updateClientes() {

    }


    var uiState = MutableStateFlow(ClienteListState())
        private set
    var uiStateCliente = MutableStateFlow(ClienteState())
        private set


    init {
       Repository.getClientes().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    uiState.update { it.copy(isLoading = true) }
                }

                is Resource.Success -> {
                    uiState.update {
                        it.copy(clientes = result.data ?: emptyList())
                    }
                }

                is Resource.Error -> {
                    uiState.update { it.copy(error = result.message ?: "Error desconocido") }
                }
            }
        }.launchIn(viewModelScope)
    }


    fun postCliente() {
        viewModelScope.launch {

            if (!Validar()) {
                println("Guardando cliente...")
                val clienteDto = ClienteDto(
                    id=0,
                    nombres = nombre.toString(),
                    telefono = telefono.toString(),
                    celular = celular.toString(),
                    email = email.toString(),
                    fechaNac = fechaNac.toString(),
                    Ocupacion = ocupacion.toString()
                )
                Repository.postCliente(clienteDto)
                clientes = Repository.getClientes().stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = Resource.Loading()
                )
                mensaje="guardado correctamente"
                limpiar()




            } else {
                mensaje="error"
            }
        }
    }

    fun deleteCliente(clienteId: Int) {
        viewModelScope.launch {
          Repository.deleteClientes(clienteId)

        }
    }

    fun limpiar() {
        nombre=""
        telefono=""
        celular=""
        email=""
        fechaNac=""
        ocupacion=""
    }



}