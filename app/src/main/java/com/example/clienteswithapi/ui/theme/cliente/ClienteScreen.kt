package com.example.clienteswithapi.ui.theme.cliente

import com.example.clienteswithapi.R
import com.example.clienteswithapi.data.remote.dto.ClienteDto


import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import android.icu.util.Calendar
import android.util.Log

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.clienteswithapi.util.Resource
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesScreen(
    viewModel: ClienteViewModel = hiltViewModel()
) {
    val clientesResource by viewModel.clientes.collectAsState(initial = Resource.Loading())


    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.isMessageShownFlow.collectLatest {
            if (it) {
                snackbarHostState.showSnackbar(
                    message =viewModel.mensaje,
                    duration = SnackbarDuration.Short
                )

            }
        }

    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Clientes With Api") }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(8.dp)
        ) {
            // Vista de AddPersonas
            AddClientes(viewModel)
            // Llama a la función ClienteListContent que se encargará de mostrar la lista de clientes
            ClienteListContent(clientesResource, viewModel)

        }
    }
}

@Composable
fun ClienteListContent(result: Resource<List<ClienteDto>>, viewModel: ClienteViewModel) {
    when (result) {
        is Resource.Loading -> {

            CircularProgressIndicator()
        }
        is Resource.Success -> {
            val clientes = result.data
            if (clientes != null) {

                Text(
                    text = "Lista (${clientes.size} registros):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(clientes){cliente->
                        ClienteCard(
                            Modifier.padding(dimensionResource(R.dimen.padding_small)),
                            cliente,
                            viewModel
                        )
                    }

                }
            }
        }
        is Resource.Error -> {
            // Mostrar un mensaje de error
            Text(text = "Error: ${result.message}")
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddClientes(
    viewModel: ClienteViewModel
){
    var expanded by remember { mutableStateOf(false) }
    Card {

        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {
            Row(

            ){
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { expanded = !expanded }
                ) {
                    if(expanded) Text(text = "Ocultar") else Text(text = "Agregar")
                }
            }

            if(expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    ExpandedContent(viewModel = viewModel)
                }
            }


        }
    }
}
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpandedContent(viewModel: ClienteViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Nombre") },
            singleLine = true,
            maxLines=1,
            value = viewModel.nombre,
            onValueChange = viewModel::onNombreChanged,
            leadingIcon ={
                Icon(imageVector = Icons.Filled.Person, contentDescription = "person icon")
            },
            isError = viewModel.nombreError,
            trailingIcon = {
                AnimatedVisibility(
                    visible = viewModel.nombreError,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = "")
                }
            } ,
            keyboardOptions = KeyboardOptions(
                keyboardType =  KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
            keyboardActions = KeyboardActions{
                focusManager.moveFocus(FocusDirection.Next)
            }
        )


        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Telefono") },
            singleLine = true,
            maxLines=1,
            value = viewModel.telefono,
            onValueChange = viewModel::onTelefonoChanged,
            leadingIcon ={
                Icon(imageVector = Icons.Filled.Call, contentDescription = "phone icon")
            },
            isError = viewModel.telefonoError,
            trailingIcon = {
                AnimatedVisibility(
                    visible = viewModel.telefonoError,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = "")
                }
            } ,
            keyboardOptions = KeyboardOptions(
                keyboardType =  KeyboardType.Phone,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
            keyboardActions = KeyboardActions{
                focusManager.moveFocus(FocusDirection.Next)
            }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Celular") },
            singleLine = true,
            maxLines=1,
            value = viewModel.celular,
            onValueChange = viewModel::oncelularChanged,
            leadingIcon ={
                Icon(imageVector = Icons.Filled.Call, contentDescription = "phone icon")
            },
            isError = viewModel.celularError,
            trailingIcon = {
                AnimatedVisibility(
                    visible = viewModel.celularError,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = "")
                }
            } ,
            keyboardOptions = KeyboardOptions(
                keyboardType =  KeyboardType.Phone,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
            keyboardActions = KeyboardActions{
                focusManager.moveFocus(FocusDirection.Next)
            }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Email") },
            singleLine = true,
            maxLines=1,
            value = viewModel.email,
            onValueChange = viewModel::onEmailChanged,
            leadingIcon ={
                Icon(imageVector = Icons.Filled.Email, contentDescription = "email icon")
            },
            isError = viewModel.emailError,
            trailingIcon = {
                AnimatedVisibility(
                    visible = viewModel.emailError,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = "")
                }
            } ,
            keyboardOptions = KeyboardOptions(
                keyboardType =  KeyboardType.Email,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
            keyboardActions = KeyboardActions{
                focusManager.moveFocus(FocusDirection.Next)
            }
        )
        val context = LocalContext.current
        DateTextField(viewModel = viewModel, context =context )

        DropdownMenuBox(viewModel)



        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                keyboardController?.hide()
                viewModel.postCliente()
                viewModel.setMessageShown()
            })
        {
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "guardar")
            Text(text = "Guardar")
        }


    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DateTextField(
    viewModel: ClienteViewModel,
    context: Context
) {
    val year: Int
    val month: Int
    val day: Int
    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    var isDatePickerVisible by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Fecha Nacimiento") },
        singleLine = true,
        maxLines=1,
        value = viewModel.fechaNac,
        onValueChange = viewModel::onfechaNacChanged,
        leadingIcon ={
            IconButton(
                onClick = {
                    keyboardController?.hide()
                    isDatePickerVisible = true
                }
            ) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = "email icon")
            }
        },
        isError = viewModel.fechaNacError,
        trailingIcon = {
            AnimatedVisibility(
                visible = viewModel.fechaNacError,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Icon(imageVector = Icons.Default.Warning, contentDescription = "")
            }
        },
        readOnly = true
    )
    if (isDatePickerVisible) {
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                viewModel.fechaNac = "$dayOfMonth/$month/$year"
                isDatePickerVisible = false
            },
            year, month, day
        )
        DisposableEffect(Unit) {
            datePickerDialog.show()
            onDispose {
                datePickerDialog.dismiss()

            }
        }
        datePickerDialog.setOnCancelListener {isDatePickerVisible = false }
        datePickerDialog.setOnDismissListener { isDatePickerVisible = false }

    }



}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    viewModel: ClienteViewModel
) {

    val Opciones = arrayOf("ingeniero", "Contable", "Medico", "Abogado", "Civil")
    var expanded by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)

    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(

                value = viewModel.ocupacion,
                label = { Text(text = "Ocupacion") },
                onValueChange = viewModel::onOcupacionChanged,
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                leadingIcon ={
                    Icon(imageVector = Icons.Filled.Build, contentDescription = "email icon")
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Opciones.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            viewModel.ocupacion = item
                            expanded = false

                        }
                    )
                }
            }
        }
    }
}




@Composable
fun ClienteCard(
    modifier: Modifier,
    Cliente : ClienteDto,
    viewModel: ClienteViewModel
) {
    Log.d("MiApp", Cliente.toString())
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small))
            ) {
                Text(
                    text = Cliente.nombres,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
                )
                Spacer(Modifier.weight(1f))
                ItemButton(
                    expanded = expanded,
                    onClick = { expanded = !expanded },
                )
            }
            if (expanded) {
               ClienteInfo(
                    Cliente, modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.padding_medium),
                        top = dimensionResource(R.dimen.padding_small),
                        bottom = dimensionResource(R.dimen.padding_medium),
                        end = dimensionResource(R.dimen.padding_medium)
                    ),
                    viewModel
                )
            }

        }
    }




}
@Composable
fun ClienteInfo(
    Cliente: ClienteDto,
    modifier: Modifier = Modifier,
    viewModel: ClienteViewModel
) {
    Column(
        modifier = modifier
    ) {
        Row() {
            Text(
                text = stringResource(R.string.Telefono),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = Cliente.telefono,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(Modifier.height(2.dp))
        Row() {
            Text(
                text = stringResource(R.string.Celular),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = Cliente.celular,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.height(2.dp))

        Row() {
            Text(
                text = stringResource(R.string.Email),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = Cliente.email,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(Modifier.height(2.dp))

        Row() {
            Text(
                text = stringResource(R.string.FechaNacimiento),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = Cliente.fechaNac,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Row() {
            Text(
                text = stringResource(R.string.Ocupacion),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = Cliente.Ocupacion,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(Modifier.weight(2f))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                Cliente.id?.let { viewModel.deleteCliente(it) }


            }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "guardar")
                Text(text = "Eliminar")

            }

        }

    }
}
@Composable
private fun ItemButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}
