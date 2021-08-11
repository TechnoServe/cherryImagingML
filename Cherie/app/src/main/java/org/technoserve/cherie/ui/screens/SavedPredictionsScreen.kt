package org.technoserve.cherie.ui.screens

import android.app.Application
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.technoserve.cherie.R
import org.technoserve.cherie.SavedPredictionActivity
import org.technoserve.cherie.database.Prediction
import org.technoserve.cherie.database.PredictionViewModel
import org.technoserve.cherie.database.PredictionViewModelFactory
import org.technoserve.cherie.ui.components.ButtonPrimary
import org.technoserve.cherie.ui.components.ButtonSecondary
import java.util.*
import kotlin.concurrent.schedule

const val DELETED = 204

@ExperimentalFoundationApi
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SavedPredictionsScreen(
    scaffoldState: ScaffoldState,
    homeScope: CoroutineScope
) {

    val context = LocalContext.current
    val predictionViewModel: PredictionViewModel = viewModel(
        factory = PredictionViewModelFactory(context.applicationContext as Application)
    )

    val listItems = predictionViewModel.readAllData.observeAsState(listOf()).value

    var showDeleteDialog = remember { mutableStateOf(false) }
    var showSyncDialog = remember { mutableStateOf(false) }

    val loading = remember { mutableStateOf(true) }
    var previewedPredictions: MutableList<Prediction> = mutableListOf()

    Timer().schedule(1200) {
        loading.value = false
    }

    val showDetails =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == DELETED) {
                val id = result.data?.getLongExtra("ID", 0L)
                homeScope.launch {
                    val snackbarResult =
                        scaffoldState.snackbarHostState.showSnackbar("Prediction Deleted", "Undo")
                    when (snackbarResult) {
                        SnackbarResult.Dismissed -> {
                            previewedPredictions.removeAll { it.id == id }
                        }
                        SnackbarResult.ActionPerformed -> {
                            // Restore Prediction
                            val previewedPrediction = previewedPredictions.find { it.id == id }
                            if (previewedPrediction != null) {
                                predictionViewModel.addPrediction(previewedPrediction)
                            }
                        }
                    }
                }
            }
        }


    val selectedIds = remember { mutableStateListOf<Long>() }


    fun selectOrDeselectAll() {
        if (selectedIds.isEmpty()) {
            val ids = listItems.map { it.id }
            selectedIds.addAll(ids)
        } else {
            selectedIds.removeAll(selectedIds)
        }
    }

    val proceedToPredictionScreen: (id: Long) -> Unit = {
        val previewedPrediction = listItems.find { it2 -> it2.id == it }
        if (previewedPrediction != null) {
            previewedPredictions.add(previewedPrediction)
        }
        val intent = SavedPredictionActivity.newIntent(context, it)
        showDetails.launch(intent)
    }

    val checkboxAction: (id: Long) -> Unit = {
        if (selectedIds.contains(it)) {
            selectedIds.remove(it)
        } else {
            selectedIds.add(it)
        }
    }

    val clickRowItem: (id: Long) -> Unit = {
        // No item selected
        if (selectedIds.isEmpty()) {
            proceedToPredictionScreen(it)
        } else {
            checkboxAction(it)
        }
    }

    val proceedWithDelete: () -> Unit = {
        val toDelete = mutableListOf<Long>()
        toDelete.addAll(selectedIds)
        predictionViewModel.deleteList(toDelete)
        selectedIds.removeAll(selectedIds)
        showDeleteDialog.value = false
    }

    val proceedWithSync: () -> Unit = {
        val toSync = mutableListOf<Long>()
        toSync.addAll(selectedIds)
        selectedIds.removeAll(selectedIds)
        showSyncDialog.value = false

        predictionViewModel.updateSyncListStatus(toSync)
        Log.d("TAG", "Syncing")
        // Dispatch worker
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Saved Predictions",
                        color = Color.White,
                        fontSize = 18.sp,
                    )
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.Black,
                actions = {
                    IconButton(
                        onClick = { if (selectedIds.isNotEmpty()) showSyncDialog.value = true },
                        enabled = (selectedIds.size > 0)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CloudUpload,
                            contentDescription = null,
                            tint = if (selectedIds.size > 0) Color.White else Color(0xAAFFFFFF)
                        )
                    }
                    IconButton(
                        onClick = {
                            if (selectedIds.isNotEmpty()) showDeleteDialog.value = true
                        },
                        enabled = (selectedIds.size > 0)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = if (selectedIds.size > 0) Color.White else Color(0xAAFFFFFF)
                        )
                    }
                }
            )
        }
    ) {
        if (loading.value) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            )
            {
                LinearProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading saved predictions...",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            if (listItems.isEmpty()) {
                NoSavedPredictions()
            } else {
                Box(modifier = Modifier.fillMaxWidth()) {

                    val listState = rememberLazyListState()
                    val scope = rememberCoroutineScope()

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colors.background),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
                    ) {

                        stickyHeader {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 16.dp)
                                    .background(MaterialTheme.colors.background),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    modifier = Modifier.clickable(onClick = { selectOrDeselectAll() }),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Row(
                                        modifier = Modifier.padding(
                                            PaddingValues(all = 16.dp)
                                        )
                                    ) {
                                        Checkbox(
                                            checked = listItems.size == selectedIds.size,
                                            onCheckedChange = { selectOrDeselectAll() }
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(text = "Select All")
                                    }
                                }
                                Row {
                                    if (selectedIds.size == 1) {
                                        Text(text = "${selectedIds.size} item selected")
                                    }
                                    if (selectedIds.size > 1) {
                                        Text(text = "${selectedIds.size} items selected")
                                    }
                                }
                            }
                        }

                        itemsIndexed(items = listItems) { index, item ->
                            PredictionCard(
                                prediction = item,
                                clickRowItem,
                                checkboxAction,
                                selectedIds.contains(item.id)
                            )
                            if (index < listItems.size - 1)
                                Divider(
                                    color = Color.LightGray,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(PaddingValues(horizontal = 52.dp))
                                )
                        }
                    }

                    val showButton = listState.firstVisibleItemIndex > 5

                    AnimatedVisibility(
                        visible = showButton,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    ) {
                        ScrollToTopButton(scrollToTop = {
                            scope.launch {
                                listState.scrollToItem(0)
                            }
                        })
                    }
                }
            }
        }


        if (showDeleteDialog.value) {
            DeleteAllDialogPresenter(showDeleteDialog, onProceedFn = proceedWithDelete)
        }

        if (showSyncDialog.value) {
            SyncAllDialogPresenter(showSyncDialog, onProceedFn = proceedWithSync)
        }
    }
}

@Composable
fun ScrollToTopButton(scrollToTop: () -> Unit) {
    FloatingActionButton(
        contentColor = MaterialTheme.colors.onSurface,
        backgroundColor = Color.White,
        onClick = { scrollToTop() }
    ) {
        Icon(Icons.Outlined.ArrowUpward, "", tint = MaterialTheme.colors.primary)
    }
}

@Composable
fun NoSavedPredictions() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val cherryIconResource =
            if (isSystemInDarkTheme()) R.drawable.cherry_white else R.drawable.cherry
        Image(
            painter = painterResource(id = cherryIconResource),
            contentDescription = "",
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .height(240.dp)
                .padding(top = 60.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No Saved Predictions",
            color = MaterialTheme.colors.onSurface,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(60.dp))
    }
}


@Composable
fun SyncAllDialogPresenter(
    showSyncDialog: MutableState<Boolean>,
    onProceedFn: () -> Unit
) {

    if(showSyncDialog.value){
        AlertDialog(
            modifier = Modifier.padding(horizontal = 32.dp),
            onDismissRequest = { showSyncDialog.value = false },
            title = { Text(text = "Sync Selected Items") },
            text = {
                Column {
                    Text("Are you sure?")
                    Text("This will upload all selected items")
                }
            },

            confirmButton = {
                TextButton(onClick = { onProceedFn() }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSyncDialog.value = false }) {
                    Text(text = "No")
                }
            }
        )
    }

}

@Composable
fun DeleteAllDialogPresenter(
    showDeleteDialog: MutableState<Boolean>,
    onProceedFn: () -> Unit
) {

    if(showDeleteDialog.value){
        AlertDialog(
            modifier = Modifier.padding(horizontal = 32.dp),
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text(text = "Delete Selected Items") },
            text = {
                Column {
                    Text("Are you sure?")
                    Text("This will delete all selected items")
                }
            },

            confirmButton = {
                TextButton(onClick = { onProceedFn() }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text(text = "No")
                }
            }
        )
    }
}