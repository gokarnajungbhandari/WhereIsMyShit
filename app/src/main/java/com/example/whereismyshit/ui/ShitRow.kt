package com.example.whereismyshit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.whereismyshit.data.Shit
import kotlinx.coroutines.flow.Flow
import androidx.compose.runtime.collectAsState
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
/*
@Composable — tells Compose this function creates UI.
Shit: Shit — the particular Shit this row should display.
Row { } — arranges the UI elements horizontally.
Text(...) — displays the Shit's information.
onEdit / onDelete — functions passed into ShitRow telling it what should happen when the buttons are clicked.
*/
@Composable
fun ShitRow(
    shit: Shit,
    parentStack:List<Shit> = emptyList(),
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    goToContainer: () -> Unit,
    getNumOfChildContainers: (Int) -> Flow<Int>,
    getNumOfChildShits: (Int) -> Flow<Int>
) {
    /*
    If the last argument to a function is another function (a lambda), Kotlin allows:

        someFunction(argument, {
            // function
        })

    to be written more cleanly as:

        someFunction(argument) {
            // function
        }
    */
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }
    val childContainers by getNumOfChildContainers(shit.id)
        .collectAsState(initial = 0)
        /*collectAsState() takes a Flow and turns its latest value into
        Compose State, so the UI automatically updates whenever the Flow emits a new value.*/

    val childShits by getNumOfChildShits(shit.id)
        .collectAsState(initial = 0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = shit.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = if (shit.isContainer) {
                        Modifier.clickable {
                            goToContainer()
                        }
                    } else {
                        Modifier
                    }
                )
                Column(
                    Modifier.padding(5.dp)
                ) {
                    if (shit.isContainer) {
                        Text(text = "Container")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {

                    Button(onClick = onEdit) {
                        Text("Edit")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = {
                        showDeleteDialog = true
                    }) {
                        Text("Delete")
                    }
                }
            }
            shit.imagePath?.let { imagePath ->

                AsyncImage(
                    model = imagePath,
                    contentDescription = shit.name,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(
                    modifier = Modifier.width(16.dp)
                )
            }
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },

            title = {
                Text("Delete ${shit.name}?")
            },

            text = {
                Column {
                    Text("Are you sure you want to delete this?")

                    if (shit.isContainer) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Child Shit Containers: $childContainers")
                        Text("Child Shits: $childShits")
                    }
                }
            },

            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },

            dismissButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}