package com.auroraai.app.ui.market

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.auroraai.app.data.AiModel
import com.auroraai.app.data.ModelCatalog
import com.auroraai.app.data.SettingsStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiMarketScreen(settings: SettingsStore, navController: NavHostController) {
    var multiSelectMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf<Set<String>>(settings.combinedModelIds) }
    var keyDialogModel by remember { mutableStateOf<AiModel?>(null) }
    var activeModel by remember { mutableStateOf(settings.activeModelId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (multiSelectMode) "Modelleri Seç (${selectedIds.size})" else "Yapay Zeka Marketi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    if (multiSelectMode) {
                        TextButton(onClick = {
                            settings.combinedModelIds = selectedIds.toMutableSet()
                            settings.combineModeEnabled = selectedIds.size > 1
                            multiSelectMode = false
                        }) { Text("Bitti") }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (multiSelectMode) {
                Text(
                    "Diğer modellere de dokunarak birden fazlasını seç — hepsine aynı anda mesaj gönderilip cevaplar birleştirilecek.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            SectionLabel("Ücretsiz Modeller")
            ModelGrid(
                models = ModelCatalog.freeModels(),
                settings = settings,
                multiSelectMode = multiSelectMode,
                selectedIds = selectedIds,
                activeModel = activeModel,
                onLongPress = { multiSelectMode = true; selectedIds = selectedIds + it.id },
                onTap = { model ->
                    if (multiSelectMode) {
                        selectedIds = if (model.id in selectedIds) selectedIds - model.id else selectedIds + model.id
                    } else if (settings.getKeyFor(model.provider).isBlank()) {
                        keyDialogModel = model
                    } else {
                        settings.activeModelId = model.id
                        activeModel = model.id
                    }
                }
            )

            SectionLabel("Ücretli Modeller (kendi API anahtarınla)")
            ModelGrid(
                models = ModelCatalog.paidModels(),
                settings = settings,
                multiSelectMode = multiSelectMode,
                selectedIds = selectedIds,
                activeModel = activeModel,
                onLongPress = { multiSelectMode = true; selectedIds = selectedIds + it.id },
                onTap = { model ->
                    if (multiSelectMode) {
                        selectedIds = if (model.id in selectedIds) selectedIds - model.id else selectedIds + model.id
                    } else if (settings.getKeyFor(model.provider).isBlank()) {
                        keyDialogModel = model
                    } else {
                        settings.activeModelId = model.id
                        activeModel = model.id
                    }
                }
            )
        }
    }

    keyDialogModel?.let { model ->
        ApiKeyDialog(
            model = model,
            currentKey = settings.getKeyFor(model.provider),
            onDismiss = { keyDialogModel = null },
            onSave = { key ->
                settings.setProviderKey(model.provider, key)
                settings.activeModelId = model.id
                activeModel = model.id
                keyDialogModel = null
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ModelGrid(
    models: List<AiModel>,
    settings: SettingsStore,
    multiSelectMode: Boolean,
    selectedIds: Set<String>,
    activeModel: String,
    onLongPress: (AiModel) -> Unit,
    onTap: (AiModel) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.heightIn(max = 260.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(models) { model ->
            ModelCard(
                model = model,
                isActive = model.id == activeModel,
                isSelected = model.id in selectedIds,
                hasKey = settings.getKeyFor(model.provider).isNotBlank(),
                onTap = { onTap(model) },
                onLongPress = { onLongPress(model) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ModelCard(
    model: AiModel,
    isActive: Boolean,
    isSelected: Boolean,
    hasKey: Boolean,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(6.dp)
            .combinedClickable(onClick = onTap, onLongClick = onLongPress),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(model.colorHex))),
                contentAlignment = Alignment.Center
            ) {
                Text(model.initials, color = Color.White, fontWeight = FontWeight.Bold)
            }
            if (isActive || isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(model.displayName, style = MaterialTheme.typography.labelSmall, maxLines = 2, textAlign = TextAlign.Center)
        if (!hasKey) {
            Text("anahtar gerekli", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
private fun ApiKeyDialog(
    model: AiModel,
    currentKey: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var key by remember { mutableStateOf(currentKey) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${model.displayName} için API Anahtarı") },
        text = {
            Column {
                Text("Sağlayıcı: ${model.provider.displayName}", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = key, onValueChange = { key = it }, label = { Text("API Anahtarı") })
            }
        },
        confirmButton = { TextButton(onClick = { onSave(key) }) { Text("Kaydet ve Kullan") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}
