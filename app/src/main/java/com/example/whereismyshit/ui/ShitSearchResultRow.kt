package com.example.whereismyshit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
fun ShitSearchResultRow(
    shit: Shit,
    getParentStack:suspend (shit:Shit) -> List<Shit>,
    goToParentContainer: (parentContainerStack: List<Shit>) -> Unit,
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
    var parentStack by remember(shit.id) {
        mutableStateOf<List<Shit>>(emptyList())
    }

    LaunchedEffect(shit.id) {
        parentStack = getParentStack(shit)
    }

    val childContainers by getNumOfChildContainers(shit.id)
        .collectAsState(initial = 0)
        /*collectAsState() takes a Flow and turns its latest value into
        Compose State, so the UI automatically updates whenever the Flow emits a new value.*/

    val childShits by getNumOfChildShits(shit.id)
        .collectAsState(initial = 0)

    val parentPath = parentStack.joinToString("/") {
        it.name
    }

    val fullPath =
        if (parentPath.isEmpty()) {
            shit.name
        } else {
            "$parentPath/${shit.name}"
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row (
            modifier = Modifier
            .fillMaxWidth()
        ){
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = fullPath,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = if (shit.isContainer) {
                        Modifier.clickable {
                            goToParentContainer(parentStack)
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
                Column {
                    if (shit.isContainer) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Child Shit Containers: $childContainers")
                        Text("Child Shits: $childShits")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
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
}