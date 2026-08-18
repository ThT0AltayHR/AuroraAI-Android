package com.auroraai.app.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.auroraai.app.auth.GoogleAuthHelper
import com.auroraai.app.data.SettingsStore
import com.auroraai.app.ui.AuroraMark
import kotlinx.coroutines.launch

private val usageTypes = listOf("Geliştirici", "Bireysel kullanım", "Sadece sohbet etmek için")
private val referralSources = listOf("Arkadaş tavsiyesi", "Sosyal medya", "Google araması", "Diğer")

@Composable
fun OnboardingScreen(settings: SettingsStore, navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var step by remember { mutableStateOf(0) }
    var googleName by remember { mutableStateOf(settings.googleDisplayName) }
    var name by remember { mutableStateOf(settings.userName) }
    var selectedUsage by remember { mutableStateOf("") }
    var selectedReferral by remember { mutableStateOf("") }
    var signingIn by remember { mutableStateOf(false) }

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(28.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OnboardingProgress(step = step, total = 4)
            Spacer(Modifier.height(28.dp))

            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn(tween(280)) togetherWith fadeOut(tween(160)) },
                label = "onboarding-step"
            ) { currentStep ->
                when (currentStep) {
                    0 -> GoogleSignInStep(
                        signingIn = signingIn,
                        onGoogleClick = {
                            scope.launch {
                                signingIn = true
                                val user = GoogleAuthHelper.signIn(context)
                                if (user != null) {
                                    googleName = user.displayName
                                    settings.googleDisplayName = user.displayName
                                    settings.googleEmail = user.email
                                    name = user.displayName
                                }
                                signingIn = false
                                step = 1
                            }
                        },
                        onSkip = { step = 1 }
                    )

                    1 -> NameStep(
                        name = name,
                        onNameChange = { name = it },
                        onContinue = { step = 2 }
                    )

                    2 -> ChoiceStep(
                        title = "Bu uygulamayı ne için kullanacaksın?",
                        options = usageTypes,
                        selected = selectedUsage,
                        onSelect = { selectedUsage = it },
                        onContinue = { step = 3 }
                    )

                    3 -> ChoiceStep(
                        title = "Bizi nereden duydun?",
                        options = referralSources,
                        selected = selectedReferral,
                        onSelect = { selectedReferral = it },
                        buttonLabel = "Başla",
                        onContinue = {
                            settings.userName = name
                            settings.userType = selectedUsage
                            settings.referralSource = selectedReferral
                            settings.isOnboardingComplete = true
                            navController.navigate("chat") { popUpTo(0) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingProgress(step: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { index ->
            val active = index <= step
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .weight(1f)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (active) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
    }
}

/** Özel tasarlanmış Google ile Giriş ekranı — Aurora AI kimliğiyle uyumlu. */
@Composable
private fun GoogleSignInStep(signingIn: Boolean, onGoogleClick: () -> Unit, onSkip: () -> Unit) {
    Column(horizontalAlignment = Alignment.Start) {
        AuroraMark(size = 56.dp, animated = true)
        Spacer(Modifier.height(20.dp))
        Text("Aurora AI'ye Hoş Geldin", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Sohbetlerin cihazlar arasında senin olsun diye Google hesabınla giriş yapabilirsin. İstersen bu adımı atlayabilirsin.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(28.dp))

        OutlinedButton(
            onClick = onGoogleClick,
            enabled = !signingIn,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            if (signingIn) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(10.dp))
                Text("Bağlanıyor…")
            } else {
                GoogleGlyph()
                Spacer(Modifier.width(10.dp))
                Text("Google ile Giriş Yap", fontWeight = FontWeight.Medium)
            }
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) { Text("Şimdilik atla") }
    }
}

/** Google logosunun renkli "G" harfini andıran, telif hakkı içermeyen basit bir rozet. */
@Composable
private fun GoogleGlyph() {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text("G", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun NameStep(name: String, onNameChange: (String) -> Unit, onContinue: () -> Unit) {
    Column {
        Text("Sana nasıl hitap edelim?", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("İsmin") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
        )
        Spacer(Modifier.height(24.dp))
        PrimaryStepButton("Devam Et", enabled = name.isNotBlank(), onClick = onContinue)
    }
}

@Composable
private fun ChoiceStep(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    buttonLabel: String = "Devam Et",
    onContinue: () -> Unit
) {
    Column {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(18.dp))
        options.forEach { option ->
            OptionRow(option, selected == option) { onSelect(option) }
        }
        Spacer(Modifier.height(24.dp))
        PrimaryStepButton(buttonLabel, enabled = selected.isNotBlank(), onClick = onContinue)
    }
}

@Composable
private fun PrimaryStepButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) { Text(label, fontWeight = FontWeight.Medium) }
}

@Composable
private fun OptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label)
            if (selected) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
