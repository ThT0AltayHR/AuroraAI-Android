package com.auroraai.app.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.auroraai.app.data.ChatMode
import com.auroraai.app.data.Message
import com.auroraai.app.data.ModelCatalog
import com.auroraai.app.data.Role
import com.auroraai.app.data.SettingsStore

/**
 * Ana sohbet ekranı.
 *
 * Görsel dil: sıcak krem/koyu kahve zemin, terracotta vurgu, asistan mesajları
 * baloncuksuz düz metin (okunabilirlik için), kullanıcı mesajları vurgu renkli
 * baloncuk. Yanıt üretilirken "adımlar" şeklinde küçük, açılır bir durum çipi
 * gösterilir — bir asistanın "düşünüyor / arıyor / yazıyor" aşamalarını
 * şeffafça gösteren kendi özgün bileşenimiz.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(settings: SettingsStore, navController: NavHostController) {
    val vm: ChatViewModel = viewModel(factory = ChatViewModel.factory(settings))
    val messages by vm.messages.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    var input by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf(settings.chatMode) }

    val activeModelName = ModelCatalog.byId(settings.activeModelId)?.displayName ?: "Model seçilmedi"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AuroraMark(size = 20.dp)
                                Spacer(Modifier.width(6.dp))
                                Text("Aurora AI", style = MaterialTheme.typography.titleLarge)
                            }
                            Text(
                                activeModelName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("market") }) {
                            Icon(Icons.Filled.Storefront, contentDescription = "Yapay Zeka Marketi")
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("image") }) {
                            Icon(Icons.Filled.Image, contentDescription = "Görsel Üret")
                        }
                        IconButton(onClick = { navController.navigate("settings") }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Ayarlar")
                        }
                    }
                )
                ModeSelector(current = mode, onSelect = {
                    mode = it
                    settings.chatMode = it
                })
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            }
        },
        bottomBar = {
            ChatInputBar(
                input = input,
                onInputChange = { input = it },
                onSend = {
                    if (input.isNotBlank()) {
                        vm.send(input)
                        input = ""
                    }
                }
            )
        }
    ) { padding ->
        if (messages.isEmpty() && !isLoading) {
            EmptyChatState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 14.dp)
            ) {
                item { Spacer(Modifier.height(4.dp)) }
                items(messages) { msg ->
                    AnimatedMessageEntry { MessageBubble(msg) }
                }
                if (isLoading) {
                    item { AnimatedMessageEntry { TypingStepIndicator() } }
                }
                item { Spacer(Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun AnimatedMessageEntry(content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(260)) + slideInVertically(
            animationSpec = tween(260, easing = FastOutSlowInEasing),
            initialOffsetY = { it / 6 }
        )
    ) { content() }
}

@Composable
private fun EmptyChatState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuroraMark(size = 44.dp, animated = true)
        Spacer(Modifier.height(16.dp))
        Text(
            "Bugün sana nasıl yardımcı olabilirim?",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/** Krem/koyu kahve zeminde hafif nabız atan turuncu logo rozeti. */
@Composable
fun AuroraMark(size: Dp, animated: Boolean = false) {
    val infinite = rememberInfiniteTransition(label = "mark")
    val scale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = if (animated) 1.08f else 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scale"
    )
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                )
            )
    )
}

@Composable
fun ModeSelector(current: ChatMode, onSelect: (ChatMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ChatMode.values().forEach { m ->
            FilterChip(
                selected = current == m,
                onClick = { onSelect(m) },
                label = { Text(m.label, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val isUser = message.role == Role.USER
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (isUser) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        } else {
            // Asistan yanıtı baloncuksuz, düz akan metin olarak gösterilir.
            Column(modifier = Modifier.widthIn(max = 340.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AuroraMark(size = 14.dp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Aurora",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

/**
 * "Araç çağrısı / adım" görünümü — dosya oluşturma, arama gibi işlemleri
 * şeffafça göstermek için kullanılan, genişleyebilir kendi özgün kart
 * tasarımımız. Şu an yanıt üretilirken durum göstermek için kullanılıyor;
 * gerçek araç adımları eklendiğinde doğrudan bağlanabilir.
 */
@Composable
fun TypingStepIndicator() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PulsingDots()
            Spacer(Modifier.width(10.dp))
            Text(
                "Düşünüyor…",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Sırayla zıplayan üç nokta — canlı animasyon hissi için. */
@Composable
fun PulsingDots() {
    val infinite = rememberInfiniteTransition(label = "dots")
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { index ->
            val delay = index * 140
            val alpha by infinite.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = delay, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot$index"
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
            )
        }
    }
}

/** Bir işlemin ("dosya oluşturuluyor", "arama yapılıyor" vb.) küçük ayraç çipi. */
@Composable
fun ToolStepChip(label: String, icon: ImageVector = Icons.Filled.Bolt) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun ChatInputBar(input: String, onInputChange: (String) -> Unit, onSend: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxWidth()) {
        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = onInputChange,
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp, max = 140.dp),
                    placeholder = { Text("Aurora'ya bir şey sor…") },
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(Modifier.width(8.dp))
                FilledIconButton(
                    onClick = onSend,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(46.dp)
                ) {
                    Icon(Icons.Filled.ArrowUpward, contentDescription = "Gönder", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
