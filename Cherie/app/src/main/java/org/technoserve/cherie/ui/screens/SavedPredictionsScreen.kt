package org.technoserve.cherie.ui.screens

import android.app.Application
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Image
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.technoserve.cherie.R
import org.technoserve.cherie.SavedPredictionActivity
import org.technoserve.cherie.database.Prediction
import org.technoserve.cherie.database.PredictionViewModel
import org.technoserve.cherie.database.PredictionViewModelFactory
import org.technoserve.cherie.viewmodels.SavedPredictionsViewModel
import org.technoserve.cherie.viewmodels.SavedPredictionsViewModelFactory
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

    val showDialog = remember { mutableStateOf(false) }
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

    fun toggleSelectedIds(id: Long){
        if(selectedIds.contains(id)){
            selectedIds.remove(id)
        } else {
            selectedIds.add(id)
        }
    }

    fun selectOrDeselectAll(){
        if(selectedIds.isEmpty()){
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
        if(selectedIds.contains(it)){
            selectedIds.remove(it)
        } else {
            selectedIds.add(it)
        }
    }

    val clickRowItem: (id: Long) -> Unit = {
        // No item selected
        if(selectedIds.isEmpty()){
            proceedToPredictionScreen(it)
        } else {
            checkboxAction(it)
        }
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
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Outlined.CloudUpload,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        if (listItems.isNotEmpty()) showDialog.value = true
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = Color.White
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
                                    if(selectedIds.size == 1){
                                        Text(text = "${selectedIds.size} item selected")
                                    }
                                    if(selectedIds.size > 1){
                                        Text(text = "${selectedIds.size} items selected")
                                    }
                                }
                            }
                        }

                        val listItems2 = mutableListOf<Prediction>().apply {
                            repeat(24) {
                                this.addAll(listItems)
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
                                    modifier = Modifier.padding(PaddingValues(horizontal = 48.dp))
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


        if (showDialog.value) DeleteAllDialogPresenter(showDialog, predictionViewModel)
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
fun DeleteAllDialogPresenter(
    showDialog: MutableState<Boolean>,
    predictionViewModel: PredictionViewModel
) {
    AlertDialog(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = { showDialog.value = false },
        title = {
            Text(text = "Delete All Items", modifier = Modifier.height(72.dp))
        },
        text = {
            Column {
                Text("Are you sure?")
                Text("This will delete all saved predictions")
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .height(72.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { showDialog.value = false },
                    modifier = Modifier
                        .requiredWidth(160.dp)
                        .background(MaterialTheme.colors.background)
                        .border(1.dp, MaterialTheme.colors.primary),
                    shape = RoundedCornerShape(0),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 4.dp,
                        disabledElevation = 0.dp
                    )
                ) {
                    Text(
                        text = "No, Cancel",
                        modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 4.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        predictionViewModel.deleteAllPredictions()
                        showDialog.value = false
                    },
                    modifier = Modifier.requiredWidth(160.dp),
                    shape = RoundedCornerShape(0),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 4.dp,
                        disabledElevation = 0.dp
                    )
                ) {
                    Text(
                        text = "Yes, Proceed",
                        modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 4.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
    )
}