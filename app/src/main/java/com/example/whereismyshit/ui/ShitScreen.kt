package com.example.whereismyshit.ui
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.whereismyshit.data.Shit
import com.example.whereismyshit.viewmodel.ShitViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun ShitScreen(
    viewModel: ShitViewModel,
    navController: NavController,
    removeLastContainerFromStack: ()-> Unit,
    addContainerToStack: (shit: Shit) -> Unit,
    getLastContainerOrNullFromStack: () -> Shit?
) {
    var name by remember { mutableStateOf("") } //mutableStateOf() makes compose track state of name
                                                        // remember makes compose remember the previous state of
                                                        // name and doesnot redo the assignment when the function
                                                        // is recomposed. name is not a String but mutableState<String>
                                                        // and the value is accessed by name.value but using "by" lets
                                                        // you use name directly insead of dealing with .value
    var isContainer by remember { mutableStateOf(false) }
    var shitBeingEdited by remember {
        mutableStateOf<Shit?>(null)
    }

    // Keeps track of which containers we have entered
    /*val containerStack = remember {
        mutableStateListOf<Shit>()
    }*/

    // The container we are currently inside
    val currentContainer = getLastContainerOrNullFromStack()//containerStack.lastOrNull()

    // Decide which Flow we should display
    val shitFlow = remember(currentContainer?.id) {
        if (currentContainer == null) {
            viewModel.allPrimaryShits
        } else {
            viewModel.getAllShitInsideAContainer(
                currentContainer.id
            )
        }
    }

    // Convert the Flow into Compose state
    val shitList by shitFlow.collectAsState(
        initial = emptyList()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        // -------------------------
        // HEADER / BACK BUTTON
        // -------------------------

        Column(
            Modifier.fillMaxWidth()
        ) {
            Text(
                text = currentContainer?.name ?: "ALL OF MY SHIT",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.width(20.dp)
            )

            Button(
                onClick = {
                    if (currentContainer != null) {
                        removeLastContainerFromStack()
                        /*containerStack.removeAt(
                            containerStack.lastIndex
                        )*/
                    } else {
                        navController.navigate("home")
                    }
                }
            ) {
                Text("Back")
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // -------------------------
        // INPUT
        // -------------------------
        Column(
            modifier = Modifier
                .border(width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(8.dp))
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            TextField(
                value = name,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {
                    name = it
                },
                label = {
                    Text("Name")
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isContainer,
                    onCheckedChange = {
                        isContainer = it
                    }
                )

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Text(
                    text = "Container",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val editing = shitBeingEdited

                        if (editing == null) {
                            viewModel.addShit(
                                name = name,
                                isContainer = isContainer,

                                // THIS IS IMPORTANT
                                parentId = currentContainer?.id
                            )
                        } else {
                            viewModel.updateShit(
                                editing.copy(
                                    name = name,
                                    isContainer = isContainer,

                                    // THIS IS IMPORTANT
                                    parentId = currentContainer?.id
                                )
                            )

                            shitBeingEdited = null
                        }

                        name = ""
                        isContainer = false
                    }
                }
            ) {
                Text("Add")
            }
        }
        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // -------------------------
        // LIST
        // -------------------------

        LazyColumn {

            items(shitList) { shit ->

                ShitRow(
                    shit = shit,

                    goToContainer = {
                        if (shit.isContainer) {
                            addContainerToStack(shit)
                            //containerStack.add(shit)
                        }
                    },

                    onEdit = {
                        shitBeingEdited = shit
                        name = shit.name
                        isContainer = shit.isContainer
                    },

                    onDelete = {
                        viewModel.deleteShit(shit)
                    },

                    getNumOfChildContainers = {
                        viewModel.getNumOfChildContainers(shit.id)
                    },

                    getNumOfChildShits = {
                        viewModel.getNumOfChildShits(shit.id)
                    }
                )
            }
        }
    }
}