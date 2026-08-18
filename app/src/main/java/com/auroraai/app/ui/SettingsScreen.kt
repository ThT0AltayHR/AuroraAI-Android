package com.auroraai.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.auroraai.app.data.AiModel
import com.auroraai.app.data.ModelCatalog
import com.auroraai.app.data.Provider
import com.auroraai.app.data.SettingsStore

/**
 * Ayarlar ekranı — bölüm bazlı, ince kenarlıklı kartlarla "seçkin" bir görünüm.
 *
 * SettingsStore'un gerçek API'sini (sağlayıcı başına anahtar, aktif model
 * kimliği, görsel model kimliği) kullanır.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(settings: SettingsStore, navController: NavHostController) {
    var providerKeys by remember { mutableStateOf(settings.getProviderKeys()) }
    var activeModelId by remember { mutableStateOf(settings.activeModelId) }
    var imageModelId by remember { mutableStateOf(settings.imageModelId) }
    var voiceKey by remember { mutableStateOf(settings.realisticVoiceKey) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (settings.googleEmail.isNotBlank()) {
                SettingsCard(title = "Google Hesabı", icon = Icons.Filled.AccountCircle) {
                    Text(settings.googleDisplayName, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        settings.googleEmail,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            SettingsCard(title = "API Anahtarları", icon = Icons.Filled.VpnKey) {
                Text(
                    "Her sağlayıcı için kendi API anahtarını gir. Ücretsiz anahtarları OpenRouter veya Groq üzerinden alabilirsin.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                Provider.values().forEach { provider ->
                    var visible by remember { mutableStateOf(false) }
                    val currentValue = providerKeys[provider.name] ?: ""
                    OutlinedTextField(
                        value = currentValue,
                        onValueChange = {
                            settings.setProviderKey(provider, it)
                            providerKeys = settings.getProviderKeys()
                        },
                        label = { Text(provider.displayName) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { visible = !visible }) {
                                Icon(if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                    )
                }
            }

            SettingsCard(title = "Aktif Sohbet Modeli", icon = Icons.Filled.Psychology) {
                ModelDropdown(
                    models = ModelCatalog.all.filter {
                        it.id != "dall-e-3" && it.id != "stabilityai/sdxl" && it.id != "black-forest-labs/flux-schnell"
                    },
                    selectedId = activeModelId,
                    onSelect = {
                        activeModelId = it
                        settings.activeModelId = it
                    }
                )
            }

            SettingsCard(title = "Görsel Üretim Modeli", icon = Icons.Filled.Image) {
                ModelDropdown(
                    models = listOf(
                        ModelCatalog.byId("stabilityai/sdxl"),
                        ModelCatalog.byId("black-forest-labs/flux-schnell"),
                        ModelCatalog.byId("dall-e-3")
                    ).filterNotNull(),
                    selectedId = imageModelId,
                    onSelect = {
                        imageModelId = it
                        settings.imageModelId = it
                    }
                )
            }

            SettingsCard(title = "Sesli Sohbet API Anahtarı (opsiyonel)", icon = Icons.Filled.Mic) {
                OutlinedTextField(
                    value = voiceKey,
                    onValueChange = {
                        voiceKey = it
                        settings.realisticVoiceKey = it
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingsCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModelDropdown(models: List<AiModel>, selectedId: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selected = models.firstOrNull { it.id == selectedId } ?: models.firstOrNull()

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected?.displayName ?: "Model seç",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            models.forEach { model ->
                DropdownMenuItem(
                    text = { Text("${model.displayName}${if (model.isFree) "  ·  ücretsiz" else ""}") },
                    onClick = {
                        onSelect(model.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
