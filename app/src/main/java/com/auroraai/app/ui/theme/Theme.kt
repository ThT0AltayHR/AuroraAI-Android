package com.auroraai.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

/**
 * Aurora AI tema paleti.
 *
 * Sıcak terracotta turuncu vurgu rengi + krem/koyu kahve zeminler kullanır.
 * Bu, ilham perisi olan büyük sohbet asistanlarının arayüzlerinde görülen
 * "sıcak, kağıt hissi veren" estetiğe kendi özgün yorumumuzdur — kopya değil,
 * kendi rozet renklerimiz, kendi tipografimiz ve kendi bileşen setimizle.
 */

// --- Vurgu (accent) rengi: sıcak terracotta ---
val AuroraAccent = Color(0xFFD97757)
val AuroraAccentDark = Color(0xFFE38B6C)
val AuroraAccentMuted = Color(0xFFE8C4B0)

// --- Açık tema: krem/kağıt zemin ---
val AuroraLightBackground = Color(0xFFFAF7F2)
val AuroraLightSurface = Color(0xFFFFFFFF)
val AuroraLightSurfaceVariant = Color(0xFFF0EAE1)
val AuroraLightOnBackground = Color(0xFF2B2622)
val AuroraLightOutline = Color(0xFFE2D9CC)

// --- Koyu tema: sıcak, kahverengiye çalan neredeyse siyah ---
val AuroraDarkBackground = Color(0xFF1A1815)
val AuroraDarkSurface = Color(0xFF211E1A)
val AuroraDarkSurfaceVariant = Color(0xFF2C2823)
val AuroraDarkOnBackground = Color(0xFFEDE6DC)
val AuroraDarkOutline = Color(0xFF3A342C)

private val AuroraLightColors = lightColorScheme(
    primary = AuroraAccent,
    onPrimary = Color.White,
    primaryContainer = AuroraAccentMuted,
    onPrimaryContainer = Color(0xFF5C2E1A),
    secondary = Color(0xFF8A7A66),
    background = AuroraLightBackground,
    onBackground = AuroraLightOnBackground,
    surface = AuroraLightSurface,
    onSurface = AuroraLightOnBackground,
    surfaceVariant = AuroraLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF5C554A),
    outline = AuroraLightOutline,
    error = Color(0xFFBA1A1A)
)

private val AuroraDarkColors = darkColorScheme(
    primary = AuroraAccentDark,
    onPrimary = Color(0xFF3A1B0F),
    primaryContainer = Color(0xFF4A2A1C),
    onPrimaryContainer = AuroraAccentMuted,
    secondary = Color(0xFFB7A890),
    background = AuroraDarkBackground,
    onBackground = AuroraDarkOnBackground,
    surface = AuroraDarkSurface,
    onSurface = AuroraDarkOnBackground,
    surfaceVariant = AuroraDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC9BEB0),
    outline = AuroraDarkOutline,
    error = Color(0xFFFFB4AB)
)

val AuroraTypography = Typography(
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        fontFamily = FontFamily.Serif
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        fontFamily = FontFamily.Serif
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        fontFamily = FontFamily.Default
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontFamily = FontFamily.Default
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontFamily = FontFamily.Default
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        fontFamily = FontFamily.Default
    )
)

@Composable
fun AuroraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) AuroraDarkColors else AuroraLightColors
    MaterialTheme(
        colorScheme = colors,
        typography = AuroraTypography,
        content = content
    )
}
