package com.example.whereismyshit.ui
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.whereismyshit.data.Shit
import com.example.whereismyshit.viewmodel.ShitViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ShitViewModel,
    changeStack: (parentStack: List<Shit>) -> Unit,
    navController: NavController
) {
   Column(
    modifier = Modifier
    .fillMaxSize()
    .padding(16.dp)
   ){
       var expanded by rememberSaveable { mutableStateOf(false) }
       var searchResults : List<Shit> = emptyList()
       var textFieldState: TextFieldState = remember{
           TextFieldState("")
           }
       Spacer(
           modifier = Modifier.height(50.dp)
       )
       Box(
           modifier = Modifier
               //.fillMaxSize()
               .semantics { isTraversalGroup = true }
       ) {
           SearchBar(
               modifier = Modifier
                   .align(Alignment.TopCenter)
                   .semantics { traversalIndex = 0f },
               inputField = {
                   SearchBarDefaults.InputField(
                       query = textFieldState.text.toString(),
                       onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                       onSearch = {
                           viewModel.performSearchText(textFieldState.text.toString())
                           //expanded = false
                       },
                       expanded = expanded,
                       onExpandedChange = { expanded = it },
                       placeholder = { Text("Search") }
                   )
               },
               expanded = expanded,
               onExpandedChange = { expanded = it },
           ) {
               // Display search results in a scrollable column
               Column(Modifier.verticalScroll(rememberScrollState())) {
                   viewModel.searchResults.forEach { result ->
                       ShitSearchResultRow(
                           shit = result,

                           goToParentContainer = { parentStack ->
                               if (result.isContainer) {
                                   changeStack(parentStack)
                                   //containerStack.add(shit)
                               }
                               navController.navigate("containers")
                           },
                           getParentStack = {
                               viewModel.getParentStack(it)
                           },
                           getNumOfChildContainers = {
                               viewModel.getNumOfChildContainers(result.id)
                           },

                           getNumOfChildShits = {
                               viewModel.getNumOfChildShits(result.id)
                           }
                       )
                       /*ListItem(
                           headlineContent = { Text(result) },
                           modifier = Modifier
                               .clickable {
                                   textFieldState.edit { replace(0, length, result) }
                                   expanded = false
                               }
                               .fillMaxWidth()
                       )*/
                   }
               }
           }
       }
       Spacer(
           modifier = Modifier.height(50.dp)
       )
       Box(
           modifier = Modifier
               .fillMaxWidth()
               .border(width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(8.dp))
               .padding(10.dp)
               .clickable(onClick = {navController.navigate("containers") }),

           contentAlignment = Alignment.Center
       ) {
           Text(
               text = "Containers",
               style = MaterialTheme.typography.headlineMedium,
               color = MaterialTheme.colorScheme.primary,
               fontSize = 24.sp,
               fontFamily = FontFamily.Monospace
           )
       }
       Spacer(
           modifier = Modifier.height(16.dp)
       )
   }
}