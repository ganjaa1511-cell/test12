package com.remy.airealmcompanion

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.BiasAlignment
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.googlefonts.*
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.*
import org.json.*
import java.io.*
import java.util.UUID

// ─────────────────────────────────────────────────────────────────────────────
// FONTS
// ─────────────────────────────────────────────────────────────────────────────

private val gmsProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)
private val CinzelGF     = GoogleFont("Cinzel")
private val AlegreyaGF   = GoogleFont("Alegreya")
private val CinzelFamily = FontFamily(
    Font(googleFont = CinzelGF, fontProvider = gmsProvider, weight = FontWeight.Normal),
    Font(googleFont = CinzelGF, fontProvider = gmsProvider, weight = FontWeight.SemiBold),
    Font(googleFont = CinzelGF, fontProvider = gmsProvider, weight = FontWeight.Bold),
    Font(googleFont = CinzelGF, fontProvider = gmsProvider, weight = FontWeight.ExtraBold),
)
/** Body serif — warm, readable, codex feel. Falls back to system serif while loading. */
private val AlegreyaFamily = FontFamily(
    Font(googleFont = AlegreyaGF, fontProvider = gmsProvider, weight = FontWeight.Normal),
    Font(googleFont = AlegreyaGF, fontProvider = gmsProvider, weight = FontWeight.Medium),
)

// ─────────────────────────────────────────────────────────────────────────────
// TOKENS
// ─────────────────────────────────────────────────────────────────────────────

private val L0 = Color(0xFF0A0908)   // void
private val L1 = Color(0xFF121110)   // bg — neutral warm charcoal
private val L2 = Color(0xFF1B1917)   // cards
private val L3 = Color(0xFF232020)   // raised
private val L4 = Color(0xFF2D2A28)   // inputs
private val L5 = Color(0xFF3A3633)   // border subtle
private val L6 = Color(0xFF4D4845)   // border visible

private val GOLD_HI   = Color(0xFFE89B6C)   // copper highlight
private val GOLD      = Color(0xFFD17A4E)   // terracotta — primary
private val GOLD_MID  = Color(0xFF9E5A3A)   // copper mid
private val GOLD_DIM  = Color(0xFF3E2418)   // copper tint bg

private val T1 = Color(0xFFF4EEE8)   // headings — warm white
private val T2 = Color(0xFFC0B4A8)   // body — warm grey
private val T3 = Color(0xFF978A7E)   // muted (≥4.5:1)
private val T4 = Color(0xFF6A5F56)   // placeholders

private val TEAL    = Color(0xFF5B9A8B)   // sage-teal — locations
private val TEAL_LO = Color(0xFF1C2E2A)
private val CRIM    = Color(0xFFB04A3A)   // soft terracotta-red
private val CRIM_LO = Color(0xFF2E1512)

// Label palette — 8 distinct warm/cool colours that all work on the dark bg
val LABEL_COLORS = listOf(
    Color(0xFFD9A441), // Ambre
    Color(0xFF3FA0A0), // Sarcelle
    Color(0xFF9B5FB0), // Améthyste
    Color(0xFFC0504D), // Cramoisi
    Color(0xFF4E9E6E), // Mousse
    Color(0xFF8A6A3A), // Bronze
    Color(0xFF5575A0), // Ardoise
    Color(0xFFB0926A), // Sable
    Color(0xFFE08A4C), // Mandarine
    Color(0xFFD96B8E), // Rose poudré
    Color(0xFF6C8FE0), // Bleuet
    Color(0xFF5FB89A), // Jade
    Color(0xFFB5C24A), // Citron vert
    Color(0xFFC85C5C), // Corail
    Color(0xFF9080D0), // Lavande
    Color(0xFF8FA0AE), // Acier
)
val LABEL_COLOR_NAMES = listOf(
    "Ambre","Sarcelle","Améthyste","Cramoisi","Mousse","Bronze","Ardoise","Sable",
    "Mandarine","Rose poudré","Bleuet","Jade","Citron vert","Corail","Lavande","Acier"
)

private fun scheme() = darkColorScheme(
    primary = GOLD, onPrimary = L0, primaryContainer = GOLD_DIM,
    secondary = TEAL, onSecondary = L0,
    background = L1, onBackground = T1,
    surface = L3, onSurface = T1,
    surfaceVariant = L4, onSurfaceVariant = T2,
    outline = L5, outlineVariant = L6, error = CRIM,
)

private val Typo = Typography(
    displayMedium  = TextStyle(fontFamily = CinzelFamily, fontWeight = FontWeight.ExtraBold, fontSize = 46.sp, lineHeight = 48.sp, letterSpacing = (-1).sp),
    headlineLarge  = TextStyle(fontFamily = CinzelFamily, fontWeight = FontWeight.Bold,      fontSize = 30.sp, lineHeight = 34.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontFamily = CinzelFamily, fontWeight = FontWeight.Bold,      fontSize = 22.sp, lineHeight = 28.sp),
    headlineSmall  = TextStyle(fontFamily = CinzelFamily, fontWeight = FontWeight.SemiBold,  fontSize = 18.sp, lineHeight = 24.sp),
    titleLarge     = TextStyle(fontFamily = CinzelFamily, fontWeight = FontWeight.Bold,      fontSize = 16.sp, lineHeight = 22.sp),
    titleMedium    = TextStyle(fontFamily = CinzelFamily, fontWeight = FontWeight.SemiBold,  fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge     = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, letterSpacing = 2.4.sp),
    labelMedium    = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium,   fontSize = 9.sp,  letterSpacing = 2.0.sp),
    labelSmall     = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal,   fontSize = 8.5.sp,  letterSpacing = 1.6.sp),
    bodyLarge      = TextStyle(fontFamily = AlegreyaFamily, fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 25.sp),
    bodyMedium     = TextStyle(fontFamily = AlegreyaFamily, fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall      = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal,   fontSize = 11.sp, lineHeight = 17.sp),
)

// ─────────────────────────────────────────────────────────────────────────────
// DATA MODEL  — added: CampaignLabel, labelIds on Npc & Location
// ─────────────────────────────────────────────────────────────────────────────

data class CampaignLabel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val colorIndex: Int = 0           // index into LABEL_COLORS
)

data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val dateMillis: Long = System.currentTimeMillis(),
    val title: String = "",
    val text: String = ""
)

data class Campaign(
    val id: String = UUID.randomUUID().toString(),
    val name: String, val description: String = "",
    val labels: List<CampaignLabel> = emptyList(),
    val photoUri: String? = null,
    val gallery: List<GalleryPhoto> = emptyList(),
    val npcs: List<Npc> = emptyList(),
    val locations: List<Location> = emptyList(),
    val journal: List<JournalEntry> = emptyList(),
    val theme: String = "default",
    val gauges: List<Gauge> = emptyList(),  // custom per-campaign stat gauges
    val accentColor: Int = -1               // -1 = terracotta défaut, sinon index dans LABEL_COLORS
)

/** Resolve a campaign's signature accent colour (falls back to the default copper/terracotta). */
fun Campaign.accent(): Color =
    if (accentColor in LABEL_COLORS.indices) LABEL_COLORS[accentColor] else GOLD

/** Accent colour of the active campaign context (defaults to GOLD when outside a campaign). */
val LocalAccent = compositionLocalOf { GOLD }

/** A custom gauge defined at campaign level. Numeric (0..100 bar) or leveled (text scale). */
data class Gauge(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val colorIndex: Int = 0,        // index into LABEL_COLORS
    val numeric: Boolean = false    // true = 0..100 number, false = text level
)

/** Ordered text levels for leveled gauges → 0..1 intensity. */
val GAUGE_LEVELS = listOf("None","Low","Moderate","High","Max")
fun gaugeLevelIntensity(value: String): Float {
    val idx = GAUGE_LEVELS.indexOfFirst { it.equals(value, true) }
    return if (idx >= 0) idx / (GAUGE_LEVELS.size - 1f) else 0.4f
}

/** Ambient theme per campaign type — tints the background glow + ember colour. */
enum class CodexTheme(val id: String, val label: String, val glow: Color, val ember: Color) {
    DEFAULT("default", "Codex (cuivre)", Color(0xFF3A2418), Color(0xFFE89B6C)),
    SLICE   ("slice",   "Slice of Life",  Color(0xFF2A2418), Color(0xFFE8C76C)),
    FANTASY ("fantasy", "Fantasy",        Color(0xFF2E2410), Color(0xFFE8B84E)),
    HORROR  ("horror",  "Horreur",        Color(0xFF2A0E0E), Color(0xFFB04A3A)),
    CYBER   ("cyber",   "Cyberpunk",      Color(0xFF101A2E), Color(0xFF5B9AD1)),
    ROMANCE ("romance", "Romance",        Color(0xFF2E1422), Color(0xFFE87CA8));
    companion object {
        fun from(id: String) = entries.firstOrNull { it.id == id } ?: DEFAULT
    }
}

data class GalleryPhoto(
    val id: String = UUID.randomUUID().toString(),
    val path: String,
    val caption: String = ""
)

data class Npc(
    val id: String = UUID.randomUUID().toString(), val campaignId: String,
    val name: String, val role: String = "", val major: String = "", val shortCard: String = "",
    val fullCard: String = "", val relationships: String = "", val secrets: String = "",
    val sceneHistory: String = "", val tags: String = "",
    val dna: Map<String,String> = emptyMap(),     // structured Airealm fields, one line each
    val labelIds: List<String> = emptyList(),
    val photoUris: List<String> = emptyList(),
    val heroFocal: Float = 0.5f,                  // vertical focal point of hero photo (0=haut, 1=bas)
    val gaugeValues: Map<String,String> = emptyMap(),  // gaugeId → value (text level or number)
    val sections: Map<String,String> = emptyMap(),     // free-form parsed sections (title → content), any format
    val pinned: Boolean = false,                       // épinglé en haut de liste
    val collapsedSections: Set<String> = emptySet()    // titres des sections repliées (mémorisé par fiche)
)

/** Canonical Airealm DNA / INERTIA field keys, in display order. */
val DNA_KEYS = listOf(
    "Gender",
    "Looks","Style/Persona","Voice/Humor","Lifestyle/Vices",
    "Insecurity/Drive","Priorities","Boundary","Clique","Texts"
)
val INERTIA_KEYS = listOf("Lens","Vibe","Anchors")

/** Accepted input labels per canonical field. Parser tries each alias; storage stays canonical. */
val FIELD_ALIASES: Map<String, List<String>> = mapOf(
    "Lives"            to listOf("Lives in","Lives","Housing","Residence","Home"),
    "Looks"            to listOf("Looks","Appearance","Physical"),
    "Style/Persona"    to listOf("Style/Persona","Persona","Fashion","Style"),
    "Voice/Humor"      to listOf("Voice/Humor","Voice","Humor","Speech"),
    "Lifestyle/Vices"  to listOf("Lifestyle/Vices","Lifestyle","Vices","Habits"),
    "Insecurity/Drive" to listOf("Insecurity/Drive","Insecurity","Motivation","Drive","Fear"),
    "Priorities"       to listOf("Priorities","Goals","Priority"),
    "Boundary"         to listOf("Boundaries","Boundary","Limits"),
    "Clique"           to listOf("Clique","Social Circle","Faction","Group"),
    "Texts"            to listOf("Texting/SMS","Texting Style","Texting","Messages","Texts","SMS"),
    "Lens"             to listOf("Lens","Perception","View"),
    "Vibe"             to listOf("Vibe","Mood","Energy"),
    "Anchors"          to listOf("Anchors","Anchor")
)
/** Friendly French labels for the field keys. */
val FIELD_LABELS = mapOf(
    "Looks" to "Apparence", "Style/Persona" to "Style · Persona",
    "Voice/Humor" to "Voix · Humour", "Lifestyle/Vices" to "Mode de vie · Vices",
    "Insecurity/Drive" to "Insécurité · Moteur", "Priorities" to "Priorités",
    "Boundary" to "Limites", "Clique" to "Cercle social", "Texts" to "Messages / SMS",
    "Lens" to "Regard porté", "Vibe" to "Ambiance", "Anchors" to "Ancrages",
    "Facets" to "Facettes", "Lives" to "Logement",
    "Gender" to "Sexe · Pronoms"
)

data class Location(
    val id: String = UUID.randomUUID().toString(), val campaignId: String,
    val name: String, val type: String = "", val description: String = "",
    val atmosphere: String = "", val notableFeatures: String = "",
    val linkedNpcs: String = "", val secrets: String = "", val tags: String = "",
    val loc: Map<String,String> = emptyMap(),       // structured location-profile fields
    val labelIds: List<String> = emptyList(),
    val linkedNpcIds: List<String> = emptyList(),   // structured links → NPC fiches
    val photoUris: List<String> = emptyList(),
    val heroFocal: Float = 0.5f
)

/** Location-profile field keys, in display order (mirrors the Airealm building template). */
val LOC_KEYS = listOf(
    "Access","Style","Facade","Entrance",
    "Zone1","Zone2","Zone3","Flow",
    "Lighting","Acoustics","SmellTemp","Vibe",
    "Security","Services","Secrets"
)
val LOC_LABELS = mapOf(
    "Access" to "Accès · Zone",
    "Style" to "Style architectural",
    "Facade" to "Façade · Signes distinctifs",
    "Entrance" to "Approche · Entrée",
    "Zone1" to "Zone 1",
    "Zone2" to "Zone 2",
    "Zone3" to "Zone 3",
    "Flow" to "Circulation · Goulots",
    "Lighting" to "Éclairage",
    "Acoustics" to "Acoustique · Sons",
    "SmellTemp" to "Odeur · Température",
    "Vibe" to "Ambiance dynamique",
    "Security" to "Sécurité",
    "Services" to "Services · Loot",
    "Secrets" to "Lore caché · Secrets"
)

// ─────────────────────────────────────────────────────────────────────────────
// PHOTO STORE  — fixed: I/O runs on Dispatchers.IO via suspend
// ─────────────────────────────────────────────────────────────────────────────

object PhotoStore {
    /**
     * Imports a photo into the app sandbox and returns its private file path.
     * This deliberately copies the original bytes instead of keeping the external
     * content:// URI, so photos survive app restarts and Android permission changes.
     */
    suspend fun save(ctx: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val resolver = ctx.contentResolver
            val dir = File(ctx.filesDir, "airealm_photos").also { it.mkdirs() }
            val ext = when (resolver.getType(uri)?.lowercase()) {
                "image/png" -> "png"
                "image/webp" -> "webp"
                "image/heic", "image/heif" -> "heic"
                else -> "jpg"
            }
            val dst = File(dir, "${UUID.randomUUID()}.$ext")
            resolver.openInputStream(uri)?.use { input ->
                FileOutputStream(dst).use { output -> input.copyTo(output) }
            } ?: return@runCatching null
            if (!dst.exists() || dst.length() <= 0L) return@runCatching null
            dst.absolutePath
        }.getOrNull()
    }

    fun delete(path: String) { runCatching { File(path).delete() } }
}

// ─────────────────────────────────────────────────────────────────────────────
// PERSISTENCE
// ─────────────────────────────────────────────────────────────────────────────

private const val PREFS = "airealm_store"
private const val KEY   = "campaigns_v4"   // bumped — schema has labels now

class LocalStore(ctx: Context) {
    private val p = ctx.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val KEY_BACKUP = "campaigns_backup"

    /** True when the stored data was present but failed to parse (corruption) — UI can warn. */
    var lastLoadCorrupted: Boolean = false
        private set

    fun load(): List<Campaign> {
        val raw = p.getString(KEY, null)
        if (raw.isNullOrBlank()) { lastLoadCorrupted = false; return starter() }  // genuinely empty → starter
        val parsed = runCatching {
            val a = JSONArray(raw)
            buildList { for (i in 0 until a.length()) add(a.getJSONObject(i).toCampaign()) }
        }.getOrNull()
        if (parsed != null) {
            lastLoadCorrupted = false
            // Keep a rolling backup of the last good state (for manual recovery)
            runCatching { p.edit().putString(KEY_BACKUP, raw).apply() }
            return parsed
        }
        // Corruption: DON'T fall back to starter (that would let a save overwrite good data).
        // Try the backup first; if it also fails, surface an empty-but-flagged state.
        lastLoadCorrupted = true
        val backup = p.getString(KEY_BACKUP, null)
        val fromBackup = backup?.let { b -> runCatching {
            val a = JSONArray(b); buildList { for (i in 0 until a.length()) add(a.getJSONObject(i).toCampaign()) }
        }.getOrNull() }
        return fromBackup ?: starter()
    }

    suspend fun save(list: List<Campaign>) = withContext(Dispatchers.IO) {
        val json = JSONArray().also { a -> list.forEach { a.put(it.toJson()) } }.toString()
        // Before overwriting, copy the current good value to backup
        runCatching { p.getString(KEY, null)?.let { cur -> p.edit().putString(KEY_BACKUP, cur).apply() } }
        p.edit().putString(KEY, json).commit()
    }

    var lastOpenedId: String?
        get() = p.getString("lastOpened", null)
        set(v) { p.edit().putString("lastOpened", v).apply() }

    var textScale: Float
        get() = p.getFloat("textScale", 1f)
        set(v) { p.edit().putFloat("textScale", v).apply() }

    var compactCards: Boolean
        get() = p.getBoolean("compactCards", false)
        set(v) { p.edit().putBoolean("compactCards", v).apply() }

    fun toJson(list: List<Campaign>) = JSONArray().also { a -> list.forEach { a.put(it.toJson()) } }.toString(2)
    fun fromJson(s: String) = runCatching {
        val a = JSONArray(s); buildList { for (i in 0 until a.length()) add(a.getJSONObject(i).toCampaign()) }
    }.getOrNull()

    private fun starter() = listOf(Campaign(
        id = "s1", name = "Campagne exemple",
        description = "Supprime ou modifie via les icônes.",
        labels = listOf(
            CampaignLabel(id = "sl-maj", name = "Majeur",    colorIndex = 0),
            CampaignLabel(id = "sl-all", name = "Allié",     colorIndex = 2),
            CampaignLabel(id = "sl-enc", name = "Ennemi",    colorIndex = 3),
            CampaignLabel(id = "sl-mis", name = "Mystère",   colorIndex = 6),
        ),
        npcs = listOf(Npc(id="sn1", campaignId="s1", name="Aldric le Gris",
            role="Archimage · Conseiller du Roi",
            shortCard="Vieux mage impénétrable. Connaît des secrets qui pourraient renverser la couronne.",
            secrets="Il est le père biologique du prince héritier.",
            tags="majeur, ambigu, puissant",
            labelIds = listOf("sl-maj","sl-mis"))),
        locations = listOf(Location(id="sl1", campaignId="s1", name="La Taverne du Corbeau",
            type="Taverne",
            description="Repaire de marchands, d'espions et de voyageurs.",
            atmosphere="Fumée de pipe, alcool tiède, rumeurs à toutes les tables.",
            tags="point de départ, lieu clé",
            labelIds = listOf("sl-maj")))
    ))
}

// ── JSON serialization ────────────────────────────────────────────────────────

private fun JSONObject.toCampaign(): Campaign {
    val id = optString("id").ifBlank { UUID.randomUUID().toString() }
    val labArr = optJSONArray("labels") ?: JSONArray()
    return Campaign(
        id = id, name = optString("name",""), description = optString("description",""),
        theme = optString("theme","default"),
        accentColor = optInt("accentColor", -1),
        gauges = (optJSONArray("gauges") ?: JSONArray()).let { a ->
            buildList { for (i in 0 until a.length()) add(a.getJSONObject(i).toGauge()) }
        },
        photoUri = optString("photoUri","").ifBlank { null },
        labels = buildList { for (i in 0 until labArr.length()) add(labArr.getJSONObject(i).toLabel()) },
        npcs = (optJSONArray("npcs") ?: JSONArray()).let { a ->
            buildList { for (i in 0 until a.length()) add(a.getJSONObject(i).toNpc(id)) }
        },
        locations = (optJSONArray("locations") ?: JSONArray()).let { a ->
            buildList { for (i in 0 until a.length()) add(a.getJSONObject(i).toLoc(id)) }
        },
        journal = (optJSONArray("journal") ?: JSONArray()).let { a ->
            buildList { for (i in 0 until a.length()) add(a.getJSONObject(i).toJournal()) }
        },
        gallery = (optJSONArray("gallery") ?: JSONArray()).let { a ->
            buildList { for (i in 0 until a.length()) add(a.getJSONObject(i).toGalleryPhoto()) }
        }
    )
}

private fun JSONObject.toGalleryPhoto() = GalleryPhoto(
    id = optString("id").ifBlank { UUID.randomUUID().toString() },
    path = optString("path",""), caption = optString("caption","")
)

private fun JSONObject.toJournal() = JournalEntry(
    id = optString("id").ifBlank { UUID.randomUUID().toString() },
    dateMillis = optLong("dateMillis", System.currentTimeMillis()),
    title = optString("title",""), text = optString("text","")
)

private fun JSONObject.toLabel() = CampaignLabel(
    id = optString("id").ifBlank { UUID.randomUUID().toString() },
    name = optString("name",""),
    colorIndex = optInt("colorIndex", 0)
)

private fun JSONObject.toNpc(fb: String) = Npc(
    id = optString("id").ifBlank { UUID.randomUUID().toString() },
    campaignId = optString("campaignId", fb),
    name = optString("name",""), role = optString("role",""), major = optString("major",""),
    heroFocal = optDouble("heroFocal", 0.5).toFloat(),
    shortCard = optString("shortCard",""), fullCard = optString("fullCard",""),
    relationships = optString("relationships",""), secrets = optString("secrets",""),
    sceneHistory = optString("sceneHistory",""), tags = optString("tags",""),
    pinned = optBoolean("pinned", false),
    collapsedSections = (optJSONArray("collapsedSections") ?: JSONArray()).let { a ->
        buildSet { for (i in 0 until a.length()) add(a.getString(i)) }
    },
    dna = (optJSONObject("dna") ?: JSONObject()).let { o ->
        buildMap { o.keys().forEach { k -> put(k, o.optString(k,"")) } }
    },
    gaugeValues = (optJSONObject("gaugeValues") ?: JSONObject()).let { o ->
        buildMap { o.keys().forEach { k -> put(k, o.optString(k,"")) } }
    },
    sections = (optJSONArray("sections") ?: JSONArray()).let { a ->
        // stored as ordered array of {t,c} to preserve order
        linkedMapOf<String,String>().apply {
            for (i in 0 until a.length()) { val o=a.getJSONObject(i); put(o.optString("t",""), o.optString("c","")) }
        }
    },
    labelIds = (optJSONArray("labelIds") ?: JSONArray()).let { a ->
        buildList { for (i in 0 until a.length()) add(a.getString(i)) }
    },
    photoUris = (optJSONArray("photoUris") ?: JSONArray()).let { a ->
        buildList { for (i in 0 until a.length()) add(a.getString(i)) }
    }
)

private fun JSONObject.toLoc(fb: String) = Location(
    id = optString("id").ifBlank { UUID.randomUUID().toString() },
    campaignId = optString("campaignId", fb),
    name = optString("name",""), type = optString("type",""),
    description = optString("description",""), atmosphere = optString("atmosphere",""),
    notableFeatures = optString("notableFeatures",""), linkedNpcs = optString("linkedNpcs",""),
    linkedNpcIds = (optJSONArray("linkedNpcIds") ?: JSONArray()).let { a ->
        buildList { for (i in 0 until a.length()) add(a.getString(i)) }
    },
    secrets = optString("secrets",""), tags = optString("tags",""),
    loc = optJSONObject("loc")?.let { o ->
        buildMap { o.keys().forEach { k -> put(k, o.getString(k)) } }
    } ?: emptyMap(),
    heroFocal = optDouble("heroFocal", 0.5).toFloat(),
    labelIds = (optJSONArray("labelIds") ?: JSONArray()).let { a ->
        buildList { for (i in 0 until a.length()) add(a.getString(i)) }
    },
    photoUris = (optJSONArray("photoUris") ?: JSONArray()).let { a ->
        buildList { for (i in 0 until a.length()) add(a.getString(i)) }
    }
)

private fun Gauge.toJson() = JSONObject().apply {
    put("id",id); put("name",name); put("colorIndex",colorIndex); put("numeric",numeric)
}
private fun JSONObject.toGauge() = Gauge(
    id = optString("id").ifBlank { UUID.randomUUID().toString() },
    name = optString("name",""),
    colorIndex = optInt("colorIndex",0),
    numeric = optBoolean("numeric",false)
)

private fun Campaign.toJson() = JSONObject().apply {
    put("id",id); put("name",name); put("description",description); put("theme",theme)
    put("accentColor", accentColor)
    photoUri?.let { put("photoUri", it) }
    put("labels", JSONArray().also { a -> labels.forEach { a.put(it.toJson()) } })
    put("npcs",   JSONArray().also { a -> npcs.forEach     { a.put(it.toJson()) } })
    put("locations", JSONArray().also { a -> locations.forEach { a.put(it.toJson()) } })
    put("journal", JSONArray().also { a -> journal.forEach { a.put(it.toJson()) } })
    put("gallery", JSONArray().also { a -> gallery.forEach { a.put(it.toJson()) } })
    put("gauges", JSONArray().also { a -> gauges.forEach { a.put(it.toJson()) } })
}

private fun GalleryPhoto.toJson() = JSONObject().apply {
    put("id",id); put("path",path); put("caption",caption)
}

private fun JournalEntry.toJson() = JSONObject().apply {
    put("id",id); put("dateMillis",dateMillis); put("title",title); put("text",text)
}

private fun CampaignLabel.toJson() = JSONObject().apply {
    put("id",id); put("name",name); put("colorIndex",colorIndex)
}

private fun Npc.toJson() = JSONObject().apply {
    put("id",id); put("campaignId",campaignId); put("name",name); put("role",role)
    put("major",major); put("heroFocal", heroFocal.toDouble())
    put("shortCard",shortCard); put("fullCard",fullCard)
    put("relationships",relationships); put("secrets",secrets)
    put("sceneHistory",sceneHistory); put("tags",tags); put("pinned",pinned)
    put("collapsedSections", JSONArray().also { a -> collapsedSections.forEach { a.put(it) } })
    put("dna", JSONObject().also { o -> dna.forEach { (k,v) -> o.put(k,v) } })
    put("gaugeValues", JSONObject().also { o -> gaugeValues.forEach { (k,v) -> o.put(k,v) } })
    put("sections", JSONArray().also { a -> sections.forEach { (t,c) -> a.put(JSONObject().put("t",t).put("c",c)) } })
    put("labelIds",  JSONArray().also { a -> labelIds.forEach  { a.put(it) } })
    put("photoUris", JSONArray().also { a -> photoUris.forEach { a.put(it) } })
}

private fun Location.toJson() = JSONObject().apply {
    put("id",id); put("campaignId",campaignId); put("name",name); put("type",type)
    put("description",description); put("atmosphere",atmosphere)
    put("notableFeatures",notableFeatures); put("linkedNpcs",linkedNpcs)
    put("linkedNpcIds", JSONArray().also { a -> linkedNpcIds.forEach { a.put(it) } })
    put("secrets",secrets); put("tags",tags)
    put("loc", JSONObject().also { o -> loc.forEach { (k,v) -> o.put(k,v) } })
    put("heroFocal", heroFocal.toDouble())
    put("labelIds",  JSONArray().also { a -> labelIds.forEach  { a.put(it) } })
    put("photoUris", JSONArray().also { a -> photoUris.forEach { a.put(it) } })
}

// ─────────────────────────────────────────────────────────────────────────────
// ENTRY POINT
// ─────────────────────────────────────────────────────────────────────────────

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Dark UI → force light (white) status & nav bar icons
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        setContent {
            LaunchedEffect(Unit) {
                val s = LocalStore(this@MainActivity)
                TextPref.scale = s.textScale
                CompactMode.on = s.compactCards
            }
            val baseDensity = LocalDensity.current
            val scaledDensity = Density(baseDensity.density, baseDensity.fontScale * TextPref.scale)
            CompositionLocalProvider(LocalDensity provides scaledDensity) {
            MaterialTheme(colorScheme = scheme(), typography = Typo) {
                val themeGlow by animateColorAsState(CurrentTheme.theme.glow, tween(800), label="themeGlow")
                Box(Modifier.fillMaxSize().background(L1).drawBehind {
                    // Ambient halo — tinted by the campaign theme
                    drawRect(Brush.radialGradient(
                        listOf(themeGlow.copy(alpha=0.6f), themeGlow.copy(alpha=0.28f), Color.Transparent),
                        center = Offset(size.width*0.5f, size.height*0.08f),
                        radius = size.maxDimension * 0.5f))
                    // Vignette — corners sink into the void
                    drawRect(Brush.radialGradient(
                        listOf(Color.Transparent, L0.copy(alpha = 0.6f)),
                        center = Offset(size.width/2f, size.height*0.40f),
                        radius = size.maxDimension * 0.72f))
                    // Faint gold flecks — parchment grain, deterministic
                    var seed = 0x9E3779B9.toInt()
                    repeat(90) {
                        seed = seed * 1103515245 + 12345
                        val fx = ((seed ushr 16) and 0x7FFF) / 32767f * size.width
                        seed = seed * 1103515245 + 12345
                        val fy = ((seed ushr 16) and 0x7FFF) / 32767f * size.height
                        seed = seed * 1103515245 + 12345
                        val a = 0.02f + ((seed ushr 16) and 0x7FFF) / 32767f * 0.03f
                        drawCircle(GOLD.copy(alpha=a), 0.8f, Offset(fx, fy))
                    }
                }) {
                    EmberDust(Modifier.fillMaxSize())
                    CompanionApp()
                    NoticeHost(Modifier.align(Alignment.BottomCenter))
                }
            }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NAVIGATION
// ─────────────────────────────────────────────────────────────────────────────

sealed class Screen {
    object Campaigns : Screen()
    data class Campaign(val id: String) : Screen()
    data class NpcDetail(val campaignId: String, val npcId: String) : Screen()
    data class LocDetail(val campaignId: String, val locId: String) : Screen()
    data class Relations(val campaignId: String) : Screen()
}

// ─────────────────────────────────────────────────────────────────────────────
// ROOT
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CompanionApp() {
    val ctx   = LocalContext.current
    val store = remember { LocalStore(ctx) }
    val scope = rememberCoroutineScope()
    var campaigns by remember { mutableStateOf(store.load()) }
    var screen    by remember { mutableStateOf<Screen>(Screen.Campaigns) }
    var lastOpenedId by remember { mutableStateOf(store.lastOpenedId) }
    // If stored data couldn't be parsed, warn instead of silently losing it
    LaunchedEffect(Unit) {
        if (store.lastLoadCorrupted)
            Notice.show("Données illisibles au démarrage — une sauvegarde de secours a été utilisée.")
    }
    fun persist(next: List<Campaign>) { campaigns = next; scope.launch { store.save(next) } }
    fun openCampaign(id: String) { store.lastOpenedId = id; lastOpenedId = id; screen = Screen.Campaign(id) }

    // System back gesture navigates up instead of closing the app
    BackHandler(enabled = screen != Screen.Campaigns) {
        screen = when (val s = screen) {
            is Screen.NpcDetail -> Screen.Campaign(s.campaignId)
            is Screen.LocDetail -> Screen.Campaign(s.campaignId)
            is Screen.Relations -> Screen.Campaign(s.campaignId)
            else -> Screen.Campaigns
        }
    }

    // Photo helpers — suspend-safe
    fun addPhoto(uri: Uri, callback: (String) -> Unit) {
        scope.launch {
            val path = PhotoStore.save(ctx, uri)
            if (path != null) callback(path)
            else withContext(Dispatchers.Main) { toast(ctx, "Impossible de lire la photo") }
        }
    }

    val exportL = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            ctx.contentResolver.openOutputStream(uri)?.use { it.write(store.toJson(campaigns).toByteArray()) }
            withContext(Dispatchers.Main) { toast(ctx, "Sauvegarde exportée") }
        }
    }
    val importL = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            val json = ctx.contentResolver.openInputStream(uri)?.bufferedReader()?.readText() ?: return@launch
            withContext(Dispatchers.Main) {
                store.fromJson(json)?.let { persist(it); toast(ctx,"${it.size} campagne(s) importée(s)") }
                    ?: toast(ctx,"Fichier invalide")
            }
        }
    }

    // Ambient theme follows the open campaign (and its sub-screens)
    val activeCampaignId = when (val s = screen) {
        is Screen.Campaign  -> s.id
        is Screen.NpcDetail -> s.campaignId
        is Screen.LocDetail -> s.campaignId
        is Screen.Relations -> s.campaignId
        else -> null
    }
    LaunchedEffect(activeCampaignId, campaigns) {
        CurrentTheme.theme = activeCampaignId
            ?.let { id -> campaigns.firstOrNull { it.id == id } }
            ?.let { CodexTheme.from(it.theme) }
            ?: CodexTheme.DEFAULT
    }
    // Signature accent of the active campaign (animated transition between campaigns)
    val targetAccent = activeCampaignId
        ?.let { id -> campaigns.firstOrNull { it.id == id } }?.accent() ?: GOLD
    val activeAccent by animateColorAsState(targetAccent, tween(500), label="accent")

    CompositionLocalProvider(LocalAccent provides activeAccent) {
    AnimatedContent(targetState = screen, transitionSpec = {
        // Social-premium feel: content fades + gently scales up into place
        (fadeIn(tween(260)) + scaleIn(tween(300, easing = EaseOutCubic), initialScale = 0.97f)
            + slideInHorizontally(tween(280, easing = EaseOutCubic)) { it / 12 })
            .togetherWith(fadeOut(tween(160)) + scaleOut(tween(200), targetScale = 0.99f))
    }, label = "nav") { s ->
        when (s) {
            is Screen.Campaigns -> CampaignListScreen(campaigns,
                onOpen   = { openCampaign(it.id) },
                lastOpened = campaigns.firstOrNull { it.id == lastOpenedId },
                onOpenNpcById = { cid, nid -> screen = Screen.NpcDetail(cid, nid) },
                onOpenLocById = { cid, lid -> screen = Screen.LocDetail(cid, lid) },
                onAdd    = { n -> persist(listOf(Campaign(name = n.ifBlank{"Nouvelle campagne"})) + campaigns) },
                onEdit   = { u -> persist(campaigns.map { if(it.id==u.id) u else it }) },
                onDelete = { c -> persist(campaigns.filter { it.id != c.id }) },
                onExport = { exportL.launch("airealm_backup.json") },
                onImport = { importL.launch(arrayOf("application/json","text/plain","*/*")) }
            )
            is Screen.Campaign -> {
                val c = campaigns.firstOrNull{it.id==s.id} ?: run{screen=Screen.Campaigns;return@AnimatedContent}
                CampaignDetailScreen(c,
                    onBack       = { screen = Screen.Campaigns },
                    onOpenRelations = { screen = Screen.Relations(c.id) },
                    onOpenNpc    = { screen = Screen.NpcDetail(c.id, it.id) },
                    onOpenLoc    = { screen = Screen.LocDetail(c.id, it.id) },
                    onAddNpc     = { n -> val x=Npc(campaignId=c.id,name=n.ifBlank{"Nouveau NPC"}); persist(campaigns.map{if(it.id==c.id)it.copy(npcs=it.npcs+x)else it}) },
                    onDelNpc     = { npc ->
                        persist(campaigns.map{if(it.id==c.id)it.copy(npcs=it.npcs.filter{n->n.id!=npc.id})else it})
                        Notice.showWithAction("« ${npc.name} » supprimé", "Annuler") {
                            persist(campaigns.map{if(it.id==c.id)it.copy(npcs=it.npcs+npc)else it})
                        }
                    },
                    onTogglePinNpc = { npc ->
                        persist(campaigns.map{ if(it.id==c.id) it.copy(npcs=it.npcs.map{ x ->
                            if(x.id==npc.id) x.copy(pinned=!x.pinned) else x }) else it })
                    },
                    onAddLoc     = { n -> val x=Location(campaignId=c.id,name=n.ifBlank{"Nouveau lieu"}); persist(campaigns.map{if(it.id==c.id)it.copy(locations=it.locations+x)else it}) },
                    onDelLoc     = { loc ->
                        persist(campaigns.map{if(it.id==c.id)it.copy(locations=it.locations.filter{l->l.id!=loc.id})else it})
                        Notice.showWithAction("« ${loc.name} » supprimé", "Annuler") {
                            persist(campaigns.map{if(it.id==c.id)it.copy(locations=it.locations+loc)else it})
                        }
                    },
                    onUpdateLabels = { labels -> persist(campaigns.map{if(it.id==c.id)it.copy(labels=labels)else it}) },
                    onImportNpc     = { npc ->
                        // Auto-gauges: keys like "auto:Love" → ensure a campaign gauge exists, remap to its id
                        val autoEntries = npc.gaugeValues.filterKeys { it.startsWith("auto:") }
                        var newGauges = c.gauges
                        val remapped = npc.gaugeValues.toMutableMap()
                        autoEntries.forEach { (k, v) ->
                            val gname = k.removePrefix("auto:")
                            val existing = newGauges.firstOrNull { it.name.equals(gname, true) }
                            val gauge = existing ?: Gauge(name=gname, numeric=true,
                                colorIndex=(newGauges.size) % LABEL_COLORS.size).also { newGauges = newGauges + it }
                            remapped.remove(k)
                            remapped[gauge.id] = v
                        }
                        val x = npc.copy(campaignId = c.id, gaugeValues = remapped)
                        persist(campaigns.map{if(it.id==c.id)it.copy(npcs=it.npcs+x, gauges=newGauges)else it})
                        screen = Screen.NpcDetail(c.id, x.id)
                    },
                    onImportLoc     = { loc ->
                        val x = loc.copy(campaignId = c.id)
                        persist(campaigns.map{if(it.id==c.id)it.copy(locations=it.locations+x)else it})
                        screen = Screen.LocDetail(c.id, x.id)
                    },
                    onAddGalleryPhoto = { uri -> addPhoto(uri){ path -> persist(campaigns.map{if(it.id==c.id)it.copy(gallery=it.gallery+GalleryPhoto(path=path))else it}) } },
                    onUpdateCaption  = { gp -> persist(campaigns.map{if(it.id==c.id)it.copy(gallery=it.gallery.map{g->if(g.id==gp.id)gp else g})else it}) },
                    onDelGalleryPhoto = { gp -> PhotoStore.delete(gp.path); persist(campaigns.map{if(it.id==c.id)it.copy(gallery=it.gallery.filter{g->g.id!=gp.id})else it}) },
                    onAddJournal    = { entry -> persist(campaigns.map{if(it.id==c.id)it.copy(journal=listOf(entry)+it.journal)else it}) },
                    onUpdateJournal = { entry -> persist(campaigns.map{if(it.id==c.id)it.copy(journal=it.journal.map{j->if(j.id==entry.id)entry else j})else it}) },
                    onDelJournal    = { entry -> persist(campaigns.map{if(it.id==c.id)it.copy(journal=it.journal.filter{j->j.id!=entry.id})else it}) }
                )
            }
            is Screen.NpcDetail -> {
                val c   = campaigns.firstOrNull{it.id==s.campaignId} ?: run{screen=Screen.Campaigns;return@AnimatedContent}
                val npc = c.npcs.firstOrNull{it.id==s.npcId} ?: run{screen=Screen.Campaign(s.campaignId);return@AnimatedContent}
                NpcScreen(npc, campaignLabels=c.labels,
                    campaignLocations=c.locations,
                    campaignGauges=c.gauges,
                    onOpenLoc = { loc -> screen = Screen.LocDetail(c.id, loc.id) },
                    onBack    = { screen = Screen.Campaign(s.campaignId) },
                    onSave    = { e -> persist(campaigns.map{cc->if(cc.id==e.campaignId)cc.copy(npcs=cc.npcs.map{if(it.id==e.id)e else it})else cc}); toast(ctx,"Sauvegardé") },
                    onAddPhoto = { uri -> addPhoto(uri) { p -> val u=npc.copy(photoUris=npc.photoUris+p); persist(campaigns.map{cc->if(cc.id==u.campaignId)cc.copy(npcs=cc.npcs.map{if(it.id==u.id)u else it})else cc}) } },
                    onDelPhoto = { path -> PhotoStore.delete(path); val u=npc.copy(photoUris=npc.photoUris.filter{it!=path}); persist(campaigns.map{cc->if(cc.id==u.campaignId)cc.copy(npcs=cc.npcs.map{if(it.id==u.id)u else it})else cc}) }
                )
            }
            is Screen.Relations -> {
                val c = campaigns.firstOrNull{it.id==s.campaignId} ?: run{screen=Screen.Campaigns;return@AnimatedContent}
                RelationsScreen(c,
                    onBack={screen=Screen.Campaign(s.campaignId)},
                    onOpenNpc={npc->screen=Screen.NpcDetail(c.id,npc.id)})
            }
            is Screen.LocDetail -> {
                val c   = campaigns.firstOrNull{it.id==s.campaignId} ?: run{screen=Screen.Campaigns;return@AnimatedContent}
                val loc = c.locations.firstOrNull{it.id==s.locId} ?: run{screen=Screen.Campaign(s.campaignId);return@AnimatedContent}
                LocScreen(loc, campaignLabels=c.labels,
                    campaignNpcs=c.npcs,
                    onOpenNpc = { npc -> screen = Screen.NpcDetail(c.id, npc.id) },
                    onBack    = { screen = Screen.Campaign(s.campaignId) },
                    onSave    = { e -> persist(campaigns.map{cc->if(cc.id==e.campaignId)cc.copy(locations=cc.locations.map{if(it.id==e.id)e else it})else cc}); toast(ctx,"Sauvegardé") },
                    onAddPhoto = { uri -> addPhoto(uri) { p -> val u=loc.copy(photoUris=loc.photoUris+p); persist(campaigns.map{cc->if(cc.id==u.campaignId)cc.copy(locations=cc.locations.map{if(it.id==u.id)u else it})else cc}) } },
                    onDelPhoto = { path -> PhotoStore.delete(path); val u=loc.copy(photoUris=loc.photoUris.filter{it!=path}); persist(campaigns.map{cc->if(cc.id==u.campaignId)cc.copy(locations=cc.locations.map{if(it.id==u.id)u else it})else cc}) }
                )
            }
        }
    }
    }
}

/** In-app themed notice — replaces system toasts for an immersive codex feel. */
/** The ambient theme currently displayed (driven by the open campaign). */
object CurrentTheme { var theme by mutableStateOf(CodexTheme.DEFAULT) }

/** User text-size preference: 0.9 = compact, 1.0 = normal, 1.15 = grand. */
object TextPref { var scale by mutableStateOf(1f) }

/** Compact list cards (dense rows) vs rich banner cards. */
object CompactMode { var on by mutableStateOf(false) }

/** When true, LoreFields render as clean read-only text (mode lecture). */
val LocalReadMode = compositionLocalOf { false }

object Notice {
    var current by mutableStateOf<String?>(null)
    var actionLabel by mutableStateOf<String?>(null)
    var action: (() -> Unit)? = null
    fun show(msg: String) { current = msg; actionLabel = null; action = null }
    fun showWithAction(msg: String, label: String, act: () -> Unit) {
        current = msg; actionLabel = label; action = act
    }
}
@Suppress("UNUSED_PARAMETER")
private fun toast(ctx: Context, msg: String) { Notice.show(msg) }

@Composable
fun NoticeHost(modifier: Modifier = Modifier) {
    val msg = Notice.current
    val actionLabel = Notice.actionLabel
    LaunchedEffect(msg) { if (msg != null) { delay(if (Notice.actionLabel != null) 4500 else 2200)
        Notice.current = null; Notice.actionLabel = null; Notice.action = null } }
    AnimatedVisibility(
        visible = msg != null,
        modifier = modifier,
        enter = fadeIn(tween(180)) + slideInVertically(tween(220, easing = EaseOutCubic)) { it / 2 },
        exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { it / 2 }
    ) {
        var shown by remember { mutableStateOf(msg ?: "") }
        if (msg != null) shown = msg
        Row(
            Modifier.padding(horizontal = 24.dp)
                .navigationBarsPadding().padding(bottom = 86.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(L3)
                .border(1.dp, GOLD.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.Check, null, Modifier.size(15.dp), tint = GOLD_HI)
            Text(shown, style = Typo.bodyMedium.copy(color = T1), modifier = Modifier.weight(1f, fill = false))
            if (actionLabel != null) {
                Box(Modifier.padding(start = 4.dp).clip(RoundedCornerShape(8.dp))
                    .background(GOLD.copy(alpha = 0.18f))
                    .clickable { Notice.action?.invoke(); Notice.current = null
                        Notice.actionLabel = null; Notice.action = null }
                    .padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text(actionLabel, style = Typo.labelMedium.copy(color = GOLD_HI))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DESIGN PRIMITIVES
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Deterministic, MUTED accent hue per name — sits in a curated palette of
 * deep, desaturated tones (never the old random saturated red/blue clash).
 */
private val SEAL_HUES = listOf(
    Color(0xFF9E5A3A), // copper
    Color(0xFF5B7A5E), // sage
    Color(0xFF4A6878), // dusty blue
    Color(0xFF8A5560), // dusty rose
    Color(0xFF6A5A82), // muted violet
    Color(0xFF3E7A6E), // teal
    Color(0xFFB07848), // warm ochre
    Color(0xFF566270), // slate
)
/** Turns a raw Airealm header "| 18yo | Freshman | Major: X |" into clean "18yo · Freshman · X". */
/** Turn ALL-CAPS or messy section titles into clean Title Case ("RACE CLASS ROLE" → "Race Class Role"). */
fun prettyTitle(raw: String): String {
    val t = raw.trim()
    // If it's mixed-case already (not all caps), leave it as-is.
    val letters = t.filter { it.isLetter() }
    if (letters.isNotEmpty() && letters != letters.uppercase()) return t
    return t.lowercase().split(" ").joinToString(" ") { w ->
        if (w.isEmpty()) w
        else if (w == "&") w
        else w.replaceFirstChar { it.uppercase() }
    }
}

fun prettyHeader(raw: String): String =
    raw.split("|").map { it.trim() }
        .filter { it.isNotBlank() }
        .joinToString(" · ") { seg ->
            if (seg.contains(":")) seg.substringAfter(":").trim() else seg
        }

private fun sealHue(name: String): Color =
    SEAL_HUES[(name.fold(0){a,c->a*31+c.code} and 0x7FFFFFFF) % SEAL_HUES.size]

/** Subtle dark backdrop: a single muted radial glow over near-black. */
private fun nameGrad(name: String): Brush {
    val hue = sealHue(name)
    return Brush.radialGradient(
        listOf(hue.copy(alpha = 0.32f), hue.copy(alpha = 0.10f), L2),
        center = Offset(0.30f * 1000f, 0.32f * 1000f), radius = 900f
    )
}

/**
 * Engraved monogram — the initial pressed into the surface via twin shadows
 * (dark below-right, faint light above-left). Reads as stamped leather/wax.
 */
@Composable
fun EngravedMonogram(
    letter: String, accent: Color, size: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier, baseAlpha: Float = 0.16f
) {
    Box(modifier) {
        // Dark recess
        Text(letter, style = TextStyle(fontFamily = CinzelFamily, fontWeight = FontWeight.ExtraBold,
            fontSize = size, color = L0.copy(alpha = 0.55f)),
            modifier = Modifier.offset(x = 1.5.dp, y = 1.5.dp))
        // Light catch
        Text(letter, style = TextStyle(fontFamily = CinzelFamily, fontWeight = FontWeight.ExtraBold,
            fontSize = size, color = accent.copy(alpha = baseAlpha * 1.6f)),
            modifier = Modifier.offset(x = (-1).dp, y = (-1).dp))
        // Face
        Text(letter, style = TextStyle(fontFamily = CinzelFamily, fontWeight = FontWeight.ExtraBold,
            fontSize = size, color = accent.copy(alpha = baseAlpha)))
    }
}

/** Press feedback: subtle scale-down, premium feel. Keeps ripple. */
/**
 * A floating surface: deep soft shadow tinted by [glow], a glassy gradient fill,
 * and a hairline top highlight so the surface catches light. Creates real depth.
 */
/** Entrance: content fades in and rises, with an optional delay (ms). Pairs with the hero grow. */
@Composable
fun rememberRiseIn(delayMs: Int = 90): Pair<Float, Float> {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { kotlinx.coroutines.delay(delayMs.toLong()); shown = true }
    val a by animateFloatAsState(if (shown) 1f else 0f, tween(420, easing = EaseOutCubic), label="riseA")
    val ty by animateFloatAsState(if (shown) 0f else 46f, tween(460, easing = EaseOutCubic), label="riseY")
    return a to ty
}

fun Modifier.floatingSurface(
    shape: Shape,
    glow: Color = GOLD,
    elevation: Dp = 14.dp,
    fill: Color = L2
): Modifier = this
    .shadow(elevation, shape, ambientColor = glow.copy(alpha = 0.55f), spotColor = Color.Black)
    .clip(shape)
    .background(Brush.verticalGradient(listOf(
        fill.copy(alpha = 1f),
        fill.copy(alpha = 0.92f)
    )))

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.pressable(onClickLabel: String? = null, onLongClick: (()->Unit)? = null, onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, tween(110), label = "press")
    val haptic = LocalHapticFeedback.current
    this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .combinedClickable(interactionSource = interaction, indication = LocalIndication.current,
            onClickLabel = onClickLabel,
            onLongClick = if (onLongClick != null) {{
                haptic.performHapticFeedback(HapticFeedbackType.LongPress); onLongClick()
            }} else null,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onClick()
            })
}

/** Codex flourish — ─── ◆ ─── */
@Composable
fun Ornament(accent: Color = GOLD, modifier: Modifier = Modifier) {
    Row(modifier.width(120.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).height(1.dp).background(
            Brush.horizontalGradient(listOf(Color.Transparent, accent.copy(alpha=0.45f)))))
        Text("◆", style = TextStyle(fontSize = 9.sp, color = accent.copy(alpha=0.6f)),
            modifier = Modifier.padding(horizontal = 8.dp))
        Box(Modifier.weight(1f).height(1.dp).background(
            Brush.horizontalGradient(listOf(accent.copy(alpha=0.45f), Color.Transparent))))
    }
}

@Composable fun GoldLine(alpha: Float = 0.3f) = Box(
    Modifier.fillMaxWidth().height(1.dp).background(
        Brush.horizontalGradient(listOf(Color.Transparent, GOLD.copy(alpha=alpha), GOLD.copy(alpha=alpha*.5f), Color.Transparent))
    )
)

@Composable
fun StatItemAnim(target: Int, label: String, accent: Color, modifier: Modifier = Modifier) {
    var play by remember { mutableStateOf(false) }
    LaunchedEffect(target) { play = true }
    val animated by animateIntAsState(if (play) target else 0, tween(700, easing=EaseOutCubic), label="countup")
    StatItem(animated.toString(), label, accent, modifier)
}
@Composable
fun StatItem(value: String, label: String, accent: Color, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment=Alignment.CenterHorizontally) {
        Text(value, style=TextStyle(fontFamily=CinzelFamily, fontWeight=FontWeight.Bold,
            fontSize=28.sp, color=accent))
        Text(label.uppercase(), style=Typo.labelSmall.copy(color=T3, letterSpacing=1.5.sp),
            modifier=Modifier.padding(top=2.dp))
    }
}
@Composable
fun StatDivider() {
    Box(Modifier.height(34.dp).width(1.dp).background(
        Brush.verticalGradient(listOf(Color.Transparent, L6, Color.Transparent))))
}
@Composable
fun StatBox(value: String, label: String, accent: Color = GOLD, modifier: Modifier = Modifier) {
    Column(modifier.clip(RoundedCornerShape(10.dp)).background(accent.copy(alpha=0.07f))
        .border(1.dp, accent.copy(alpha=0.14f), RoundedCornerShape(10.dp))
        .padding(horizontal=12.dp, vertical=8.dp),
        horizontalAlignment=Alignment.CenterHorizontally) {
        Text(value, style=Typo.headlineSmall.copy(color=accent, fontSize=22.sp))
        Text(label.uppercase(), style=Typo.labelSmall.copy(color=accent.copy(alpha=0.65f)))
    }
}

// ── Label chip — reusable across list and detail ──────────────────────────────

/** Unified info badge — icon + count in a soft tinted capsule. Same height everywhere. */
@Composable
fun MetaPill(icon: ImageVector, text: String, tint: Color) {
    Row(Modifier.clip(RoundedCornerShape(999.dp))
        .background(tint.copy(alpha=0.10f))
        .border(1.dp, tint.copy(alpha=0.28f), RoundedCornerShape(999.dp))
        .padding(horizontal=8.dp, vertical=4.dp),
        verticalAlignment=Alignment.CenterVertically,
        horizontalArrangement=Arrangement.spacedBy(5.dp)){
        Icon(icon, null, Modifier.size(11.dp), tint=tint)
        Text(text, style=Typo.labelSmall.copy(color=tint, letterSpacing=0.3.sp))
    }
}

/** A social tag capsule. Colour hints at relationship type by keyword. */
@Composable
fun TagCapsule(text: String) {
    val t = text.trim()
    if (t.isBlank()) return
    val low = t.lowercase()
    val color = when {
        listOf("crush","love","amour","attir").any{it in low} -> Color(0xFFD46A8A)   // rose
        listOf("rival","ennemi","enemy","hate").any{it in low} -> CRIM
        listOf("sex","had sex","couché","intim").any{it in low} -> Color(0xFFC25C9E)  // magenta
        listOf("friend","ami","allié","ally").any{it in low} -> TEAL
        listOf("roommate","roomate","coloc","family","famille").any{it in low} -> GOLD_HI
        else -> GOLD
    }
    Row(Modifier.clip(RoundedCornerShape(999.dp))
        .background(color.copy(alpha=0.14f))
        .border(1.dp, color.copy(alpha=0.4f), RoundedCornerShape(999.dp))
        .padding(horizontal=10.dp, vertical=5.dp),
        verticalAlignment=Alignment.CenterVertically){
        Box(Modifier.size(5.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(6.dp))
        Text(t, style=Typo.labelSmall.copy(color=color, letterSpacing=0.5.sp))
    }
}

@Composable
fun LabelChip(label: CampaignLabel, selected: Boolean = true, small: Boolean = false, onClick: (() -> Unit)? = null) {
    val color = LABEL_COLORS.getOrElse(label.colorIndex) { GOLD }
    val haptic = LocalHapticFeedback.current
    val mod = if (onClick != null) Modifier.clickable {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onClick()
    } else Modifier
    Row(
        mod.clip(RoundedCornerShape(999.dp))
            .background(if (selected) color.copy(alpha=0.18f) else color.copy(alpha=0.05f))
            .border(1.dp, if (selected) color.copy(alpha=0.55f) else color.copy(alpha=0.18f), RoundedCornerShape(999.dp))
            .padding(horizontal=if(small) 8.dp else 10.dp, vertical=if(small) 4.dp else 5.dp),
        verticalAlignment=Alignment.CenterVertically,
        horizontalArrangement=Arrangement.spacedBy(5.dp)
    ) {
        if (selected && onClick != null)
            Icon(Icons.Default.Check, null, Modifier.size(if(small) 10.dp else 12.dp), tint = color)
        else
            Box(Modifier.size(5.dp).clip(CircleShape).background(color))
        Text(label.name,
            style=if(small) Typo.labelSmall.copy(color=color) else Typo.labelMedium.copy(color=color))
    }
}

// ── Label manager dialog ──────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LabelManagerDialog(
    labels: List<CampaignLabel>,
    onDismiss: () -> Unit,
    onUpdate: (List<CampaignLabel>) -> Unit
) {
    var current by remember { mutableStateOf(labels) }
    var newName  by remember { mutableStateOf("") }
    var newColor by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = L3,
        title = { Text("Labels de campagne", style = Typo.headlineMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Existing labels
                current.forEach { lbl ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        LabelChip(lbl, small = true)
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { current = current.filter { it.id != lbl.id } },
                            modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, null, Modifier.size(14.dp), tint = CRIM.copy(alpha=0.7f))
                        }
                    }
                }
                if (current.isNotEmpty()) HorizontalDivider(color = L5)
                // Add new label
                Text("Nouveau label", style = Typo.labelMedium.copy(color = T3))
                OutlinedTextField(value = newName, onValueChange = { newName = it },
                    placeholder = { Text("Nom du label…", color=T4, style=Typo.bodyMedium.copy(fontStyle=FontStyle.Italic)) },
                    singleLine = true, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor=GOLD, unfocusedBorderColor=L6,
                        focusedContainerColor=L4, unfocusedContainerColor=L4, cursorColor=GOLD),
                    textStyle = Typo.bodyMedium.copy(color=T1))
                // Color picker row
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LABEL_COLORS.forEachIndexed { i, c ->
                        Box(Modifier.size(28.dp).clip(CircleShape)
                            .background(c.copy(alpha=0.25f))
                            .border(2.dp, if (newColor==i) c else Color.Transparent, CircleShape)
                            .clickable { newColor = i },
                            contentAlignment = Alignment.Center) {
                            Box(Modifier.size(14.dp).clip(CircleShape).background(c))
                        }
                    }
                }
                Button(onClick = {
                    if (newName.isNotBlank()) {
                        current = current + CampaignLabel(name=newName.trim(), colorIndex=newColor)
                        newName = ""
                    }
                }, enabled = newName.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor=GOLD, contentColor=L0)) {
                    Text("Ajouter", style=Typo.labelLarge)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onUpdate(current); onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor=GOLD, contentColor=L0)) {
                Text("Enregistrer")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

// ── Label picker in entity detail ─────────────────────────────────────────────

@Composable
fun LabelPicker(
    allLabels: List<CampaignLabel>,
    selectedIds: List<String>,
    onToggle: (String) -> Unit
) {
    if (allLabels.isEmpty()) {
        Text("Aucun label défini pour cette campagne.",
            style = Typo.bodySmall.copy(color=T4, fontStyle=FontStyle.Italic),
            modifier = Modifier.padding(horizontal=20.dp, vertical=4.dp))
        return
    }
    Column(Modifier.padding(horizontal=20.dp, vertical=6.dp)) {
        Text("LABELS".uppercase(), style=Typo.labelSmall.copy(color=GOLD.copy(alpha=0.55f)))
        Spacer(Modifier.height(8.dp))
        // Wrap chips
        var currentRow by remember { mutableStateOf(0) }
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            allLabels.forEach { lbl ->
                LabelChip(
                    label    = lbl,
                    selected = selectedIds.contains(lbl.id),
                    small    = true,
                    onClick  = { onToggle(lbl.id) }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SHARED UI PRIMITIVES (unchanged from V8)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun LoreSection(
    title: String, icon: ImageVector, accent: Color = GOLD,
    defaultExpanded: Boolean = true, summary: String = "",
    expandedOverride: Boolean? = null, onExpandChange: ((Boolean)->Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var expandedLocal by remember { mutableStateOf(defaultExpanded) }
    val expanded = expandedOverride ?: expandedLocal
    val haptic = LocalHapticFeedback.current
    // Each section floats — soft shadow + glassy fill + accent hairline when open
    Column(Modifier.fillMaxWidth().padding(horizontal=14.dp, vertical=6.dp)
        .floatingSurface(RoundedCornerShape(18.dp), glow=accent, elevation=if(expanded) 16.dp else 8.dp, fill=L2)
        .border(1.dp, if(expanded) accent.copy(alpha=0.28f) else L5.copy(alpha=0.5f), RoundedCornerShape(18.dp))) {
        Row(Modifier.fillMaxWidth()
            .semantics { stateDescription = if (expanded) "Section dépliée" else "Section repliée" }
            .clickable(onClickLabel = if (expanded) "Replier" else "Déplier"){
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                val nv = !expanded
                if (onExpandChange != null) onExpandChange(nv) else expandedLocal = nv
            }
            .padding(start=16.dp,end=14.dp,top=15.dp,bottom=if(expanded||summary.isBlank())11.dp else 13.dp),
            verticalAlignment=Alignment.CenterVertically) {
            // Icon in a tinted round chip
            Box(Modifier.size(28.dp).clip(CircleShape).background(accent.copy(alpha=0.12f)),
                contentAlignment=Alignment.Center){
                Icon(icon, null, Modifier.size(15.dp), tint=accent)
            }
            Spacer(Modifier.width(11.dp))
            Text(title.uppercase(), style=Typo.titleMedium.copy(color=accent, letterSpacing=1.4.sp, fontWeight=FontWeight.SemiBold))
            Spacer(Modifier.weight(1f))
            Icon(if(expanded)Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                null, Modifier.size(20.dp), tint=accent.copy(alpha=0.5f))
        }
        if (!expanded && summary.isNotBlank()) {
            Text(summary, style=Typo.bodySmall.copy(color=T3, fontStyle=FontStyle.Italic),
                maxLines=2, overflow=TextOverflow.Ellipsis,
                modifier=Modifier.padding(start=16.dp,end=16.dp,bottom=14.dp))
        }
        AnimatedVisibility(expanded,
            enter=fadeIn(tween(180))+expandVertically(tween(200,easing=EaseOutCubic)),
            exit=fadeOut(tween(140))+shrinkVertically(tween(160))) {
            Column(Modifier.fillMaxWidth().padding(bottom=8.dp)) { content() }
        }
    }
}

@Composable
fun LoreField(
    label: String, value: String, placeholder: String = "",
    multi: Boolean = false, accent: Color = GOLD, onChange: (String) -> Unit
) {
    val readMode = LocalReadMode.current
    if (readMode) {
        // In read mode, hide empty fields entirely for a clean "play" view
        if (value.isBlank()) return
        Column(Modifier.fillMaxWidth().padding(horizontal=20.dp, vertical=9.dp)) {
            Text(label.uppercase(), style=Typo.labelSmall.copy(color=accent.copy(alpha=0.72f), letterSpacing=0.8.sp))
            Spacer(Modifier.height(4.dp))
            Text(value, style=Typo.bodyLarge.copy(color=T1))
        }
        return
    }
    Column(Modifier.fillMaxWidth().padding(horizontal=20.dp, vertical=9.dp)) {
        Text(label.uppercase(), style=Typo.labelSmall.copy(color=accent.copy(alpha=0.72f), letterSpacing=0.8.sp))
        Spacer(Modifier.height(5.dp))
        BasicTextField(
            value=value, onValueChange=onChange,
            modifier=Modifier.fillMaxWidth(),
            minLines=if(multi)3 else 1, maxLines=if(multi)Int.MAX_VALUE else 1,
            keyboardOptions=KeyboardOptions(capitalization=KeyboardCapitalization.Sentences),
            textStyle=Typo.bodyLarge.copy(color=T1),
            decorationBox={ inner ->
                Column {
                    if (value.isEmpty()) Text(placeholder.ifBlank{"—"},
                        style=Typo.bodyLarge.copy(color=T4,
                            fontStyle=if(placeholder.isNotBlank())FontStyle.Italic else FontStyle.Normal))
                    else inner()
                    Spacer(Modifier.height(7.dp))
                    Box(Modifier.fillMaxWidth().height(1.dp).background(accent.copy(alpha=0.18f)))
                }
            }
        )
    }
}

/** A free-form section with an editable title, editable multiline content, and a delete button. */
@Composable
fun EditableSection(
    title: String, content: String, readMode: Boolean,
    canMoveUp: Boolean = false, canMoveDown: Boolean = false,
    onMoveUp: ()->Unit = {}, onMoveDown: ()->Unit = {},
    onContentChange: (String)->Unit, onTitleChange: (String)->Unit, onDelete: ()->Unit
) {
    if (readMode) {
        if (content.isBlank()) return
        Column(Modifier.fillMaxWidth().padding(horizontal=20.dp, vertical=9.dp)) {
            Text(prettyTitle(title).uppercase(), style=Typo.labelSmall.copy(color=GOLD_MID.copy(alpha=0.65f)))
            Spacer(Modifier.height(4.dp))
            Text(content, style=Typo.bodyLarge.copy(color=T1))
        }
        return
    }
    var localTitle by remember(title) { mutableStateOf(title) }
    Column(Modifier.fillMaxWidth().padding(horizontal=20.dp, vertical=9.dp)) {
        // Title row: editable title + delete
        Row(verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            BasicTextField(
                value=localTitle, onValueChange={ localTitle=it },
                textStyle=Typo.labelSmall.copy(color=GOLD_MID.copy(alpha=0.85f), letterSpacing=1.2.sp),
                cursorBrush=SolidColor(GOLD_MID), singleLine=true,
                modifier=Modifier.weight(1f).onFocusChanged { fs ->
                    if (!fs.isFocused && localTitle.trim() != title) onTitleChange(localTitle.trim())
                },
                decorationBox={ inner ->
                    Box {
                        if (localTitle.isEmpty())
                            Text("TITRE…", style=Typo.labelSmall.copy(color=T4, letterSpacing=1.2.sp))
                        inner()
                    }
                }
            )
            // Reorder arrows
            Box(Modifier.size(28.dp).clip(CircleShape)
                .background(if(canMoveUp) L4 else L3)
                .clickable(enabled=canMoveUp){ onMoveUp() }, contentAlignment=Alignment.Center){
                Icon(Icons.Default.KeyboardArrowUp, "Monter", Modifier.size(17.dp),
                    tint=if(canMoveUp) T2 else T4)
            }
            Box(Modifier.size(28.dp).clip(CircleShape)
                .background(if(canMoveDown) L4 else L3)
                .clickable(enabled=canMoveDown){ onMoveDown() }, contentAlignment=Alignment.Center){
                Icon(Icons.Default.KeyboardArrowDown, "Descendre", Modifier.size(17.dp),
                    tint=if(canMoveDown) T2 else T4)
            }
            Box(Modifier.size(28.dp).clip(CircleShape).background(CRIM_LO)
                .clickable{ onDelete() }, contentAlignment=Alignment.Center){
                Icon(Icons.Default.Close, "Supprimer la section", Modifier.size(15.dp), tint=CRIM)
            }
        }
        Spacer(Modifier.height(5.dp))
        BasicTextField(
            value=content, onValueChange=onContentChange,
            textStyle=Typo.bodyLarge.copy(color=T1, lineHeight=24.sp),
            cursorBrush=SolidColor(GOLD_MID),
            modifier=Modifier.fillMaxWidth(),
            decorationBox={ inner ->
                Box {
                    if (content.isEmpty())
                        Text("—", style=Typo.bodyLarge.copy(color=T4))
                    inner()
                }
            }
        )
        Spacer(Modifier.height(6.dp))
        HorizontalDivider(color=L4)
    }
}

@Composable
fun EntityHero(
    name: String, subtitle: String, firstPhoto: String?,
    accent: Color, onBack: ()->Unit, action: @Composable ()->Unit = {},
    focal: Float = 0.5f, onFocalChange: ((Float)->Unit)? = null,
    onTapPhoto: (()->Unit)? = null
) {
    var heroShown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { heroShown = true }
    val heroAlpha by animateFloatAsState(if (heroShown) 1f else 0f, tween(420), label="heroFade")
    // "Grows into place": hero starts zoomed-in + lifted, then settles — reads as the card expanding
    val heroScale by animateFloatAsState(if (heroShown) 1f else 1.12f,
        spring(dampingRatio = 0.78f, stiffness = 220f), label="heroScale")
    val heroLift by animateFloatAsState(if (heroShown) 0f else -34f,
        tween(460, easing = EaseOutCubic), label="heroLift")
    var adjusting by remember { mutableStateOf(false) }
    var liveFocal by remember(focal) { mutableStateOf(focal) }
    Box(Modifier.fillMaxWidth().height(310.dp).graphicsLayer{
        alpha = heroAlpha
        scaleX = heroScale; scaleY = heroScale
        translationY = heroLift
    }) {
        if (firstPhoto != null) {
            val model = ImageRequest.Builder(LocalContext.current).data(File(firstPhoto)).crossfade(true).build()
            // Blurred ambiance behind
            AsyncImage(
                model=model,
                contentDescription=null,
                contentScale=ContentScale.Crop,
                modifier=Modifier.fillMaxSize().blur(18.dp).alpha(if(adjusting) 0.15f else 0.42f)
            )
            Box(Modifier.fillMaxSize().background(Brush.radialGradient(
                listOf(Color.Transparent, L0.copy(alpha=0.72f)), radius = 760f
            )))
            // Foreground photo — CROP with adjustable vertical focal so the face can be framed
            val dragMod = if (adjusting)
                Modifier.pointerInput(Unit){
                    detectVerticalDragGestures { _, delta ->
                        // drag down → reveal higher part (focal decreases)
                        liveFocal = (liveFocal - delta / 600f).coerceIn(0f, 1f)
                    }
                } else Modifier
            AsyncImage(
                model=model,
                contentDescription=null,
                contentScale=ContentScale.Crop,
                alignment=BiasAlignment(0f, liveFocal*2f - 1f),  // -1=top,0=center,1=bottom
                modifier=Modifier.fillMaxSize().then(dragMod).then(
                    if (onTapPhoto != null && !adjusting)
                        Modifier.clickable(onClickLabel="Ouvrir la photo en grand"){ onTapPhoto() }
                    else Modifier
                )
            )
            // Adjust controls
            if (onFocalChange != null) {
                if (adjusting) {
                    // guide overlay
                    Box(Modifier.fillMaxSize().background(L0.copy(alpha=0.15f)))
                    Box(Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top=48.dp)
                        .clip(RoundedCornerShape(999.dp)).background(L0.copy(alpha=0.65f))
                        .padding(horizontal=14.dp, vertical=8.dp)){
                        Text("Glisse pour cadrer le visage", style=Typo.labelMedium.copy(color=T1))
                    }
                    // Save / cancel bottom
                    Row(Modifier.align(Alignment.BottomCenter).padding(bottom=80.dp),
                        horizontalArrangement=Arrangement.spacedBy(10.dp)){
                        Box(Modifier.clip(RoundedCornerShape(999.dp)).background(L0.copy(alpha=0.7f))
                            .clickable{ liveFocal=focal; adjusting=false }
                            .padding(horizontal=16.dp, vertical=9.dp)){
                            Text("Annuler", style=Typo.labelMedium.copy(color=T2))
                        }
                        Box(Modifier.clip(RoundedCornerShape(999.dp)).background(accent)
                            .clickable{ onFocalChange(liveFocal); adjusting=false }
                            .padding(horizontal=18.dp, vertical=9.dp)){
                            Text("Valider", style=Typo.labelMedium.copy(color=L0))
                        }
                    }
                }
            }
        } else {
            val seal = sealHue(name)
            Box(Modifier.fillMaxSize().background(nameGrad(name)).drawBehind {
                // faint engraved grain
                val dot=seal.copy(alpha=0.05f); val s=22.dp.toPx(); var y=0f
                while(y<size.height){var x=if((y/s).toInt()%2==0)0f else s/2
                    while(x<size.width){drawCircle(dot,0.9f,Offset(x,y));x+=s};y+=s}
            }) {
                EngravedMonogram(name.take(1).uppercase(), seal, 168.sp,
                    Modifier.align(Alignment.Center).offset(y=18.dp), baseAlpha=0.14f)
            }
        }
        Box(Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(L0.copy(alpha=0.58f),Color.Transparent,Color.Transparent,
                L0.copy(alpha=0.86f),L1.copy(alpha=0.99f)))))
        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal=6.dp,vertical=4.dp),
            verticalAlignment=Alignment.CenterVertically,
            horizontalArrangement=Arrangement.SpaceBetween) {
            Box(Modifier.clip(RoundedCornerShape(999.dp)).background(L0.copy(alpha=0.55f))
                .clickable{onBack()}.padding(horizontal=14.dp,vertical=8.dp)) {
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.ArrowBack,null,Modifier.size(15.dp),tint=T1)
                    Text("Retour",style=Typo.labelMedium.copy(color=T1))
                }
            }
            Row(verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(6.dp)){
                if (firstPhoto != null && onFocalChange != null && !adjusting) {
                    Box(Modifier.clip(RoundedCornerShape(999.dp)).background(L0.copy(alpha=0.55f))
                        .clickable{ adjusting=true }.padding(8.dp)){
                        Icon(Icons.Default.Crop, "Cadrer la photo", Modifier.size(17.dp), tint=T1)
                    }
                }
                Box(Modifier.clip(RoundedCornerShape(999.dp)).background(L0.copy(alpha=0.55f))){ action() }
            }
        }
        if (!adjusting) {
            Column(Modifier.align(Alignment.BottomStart).padding(start=22.dp,end=22.dp,bottom=20.dp)) {
                if(subtitle.isNotBlank()) Text(subtitle.uppercase(),
                    style=Typo.labelLarge.copy(color=accent,letterSpacing=2.sp),
                    modifier=Modifier.padding(bottom=5.dp))
                Text(name,style=Typo.headlineLarge.copy(color=T1,shadow=Shadow(L0,Offset(0f,2f),8f)),
                    modifier=Modifier.semantics{heading()})
            }
        }
    }
}

@Composable
fun SaveBar(label: String, accent: Color = GOLD, enabled: Boolean = true, onClick: ()->Unit) {
    val haptic = LocalHapticFeedback.current
    Column(Modifier.fillMaxWidth()) {
        GoldLine(alpha=0.22f)
        Box(Modifier.fillMaxWidth().background(L1.copy(alpha=0.97f)).navigationBarsPadding()
            .padding(horizontal=18.dp,vertical=14.dp)) {
            Button(onClick={haptic.performHapticFeedback(HapticFeedbackType.LongPress); onClick()}, enabled=enabled,
                modifier=Modifier.fillMaxWidth().height(52.dp),
                shape=RoundedCornerShape(14.dp),
                colors=ButtonDefaults.buttonColors(containerColor=accent,contentColor=L0,
                    disabledContainerColor=L4,disabledContentColor=T2.copy(alpha=0.7f)),
                elevation=ButtonDefaults.buttonElevation(0.dp,0.dp)) {
                Icon(Icons.Default.Save,null,Modifier.size(17.dp))
                Spacer(Modifier.width(10.dp))
                Text(label,style=Typo.labelLarge.copy(fontFamily=CinzelFamily,fontSize=14.sp))
            }
        }
    }
}

/**
 * Portrait-friendly photo strip. The original file is copied into app storage;
 * thumbnails use Fit so NPC portraits are never brutally cropped.
 */
@Composable
fun PhotoStrip(uris: List<String>, onAdd: ()->Unit, onDel: (String)->Unit) {
    var toDelete by remember { mutableStateOf<String?>(null) }
    var viewIndex by remember { mutableStateOf<Int?>(null) }
    viewIndex?.let { idx ->
        PhotoZoomViewer(uris, idx, onClose={viewIndex=null})
    }
    toDelete?.let { p ->
        AlertDialog(onDismissRequest={toDelete=null}, containerColor=L3,
            title={Text("Supprimer la photo ?",style=Typo.headlineSmall)},
            confirmButton={TextButton(onClick={onDel(p);toDelete=null}){Text("Supprimer",color=CRIM)}},
            dismissButton={TextButton(onClick={toDelete=null}){Text("Annuler")}})
    }
    LazyRow(Modifier.fillMaxWidth(),
        contentPadding=PaddingValues(horizontal=18.dp,vertical=12.dp),
        horizontalArrangement=Arrangement.spacedBy(10.dp)) {
        item {
            // Add button — 3:2 ratio
            Column(Modifier.width(88.dp).height(118.dp).clip(RoundedCornerShape(14.dp))
                .background(L4).border(1.dp,GOLD.copy(alpha=0.22f),RoundedCornerShape(10.dp))
                .clickable{onAdd()},
                horizontalAlignment=Alignment.CenterHorizontally,
                verticalArrangement=Arrangement.Center) {
                Icon(Icons.Default.AddAPhoto,null,Modifier.size(20.dp),tint=GOLD)
                Spacer(Modifier.height(3.dp))
                Text("Ajouter",style=Typo.labelSmall.copy(color=GOLD.copy(alpha=0.75f)))
            }
        }
        itemsIndexed(uris) { idx, path ->
            // 3:2 tile — Fit so the whole photo is visible
            Box(Modifier.width(88.dp).height(118.dp).clip(RoundedCornerShape(14.dp))
                .background(L0)) {
                AsyncImage(
                    model=ImageRequest.Builder(LocalContext.current).data(File(path)).crossfade(true).build(),
                    contentDescription="Voir la photo en grand",
                    contentScale=ContentScale.Fit,   // ← no crop, whole photo visible
                    modifier=Modifier.fillMaxSize().clickable{ viewIndex = idx }
                )
                Box(Modifier.align(Alignment.TopEnd).size(40.dp)
                    .clickable(onClickLabel="Supprimer la photo"){toDelete=path},
                    contentAlignment=Alignment.Center) {
                    Box(Modifier.size(26.dp).clip(CircleShape)
                        .background(L0.copy(alpha=0.8f))
                        .border(1.dp,T1.copy(alpha=0.25f),CircleShape),
                        contentAlignment=Alignment.Center) {
                        Icon(Icons.Default.Close,"Supprimer la photo",Modifier.size(13.dp),tint=T1)
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmDelete(title: String, body: String, onConfirm: ()->Unit, onDismiss: ()->Unit) {
    val haptic = LocalHapticFeedback.current
    AlertDialog(onDismissRequest=onDismiss, containerColor=L3,
        title={Text(title,style=Typo.headlineSmall)},
        text={Text(body,style=Typo.bodyMedium.copy(color=T2))},
        confirmButton={Button(onClick={haptic.performHapticFeedback(HapticFeedbackType.LongPress); onConfirm()},
            colors=ButtonDefaults.buttonColors(containerColor=CRIM,contentColor=T1),
            shape=RoundedCornerShape(10.dp)){Text("Supprimer")}},
        dismissButton={TextButton(onClick=onDismiss){Text("Annuler",color=T2)}})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSheet(label: String, hint: String, accent: Color, onDismiss: ()->Unit, onAdd: (String)->Unit) {
    var name by remember { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest=onDismiss,
        sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true),
        containerColor=L3,
        dragHandle={Box(Modifier.padding(vertical=10.dp).width(32.dp).height(3.dp)
            .clip(RoundedCornerShape(2.dp)).background(L6))}) {
        Column(Modifier.fillMaxWidth().padding(horizontal=22.dp).navigationBarsPadding().imePadding()) {
            Text(label,style=Typo.headlineMedium.copy(color=T1))
            Spacer(Modifier.height(18.dp))
            OutlinedTextField(value=name,onValueChange={name=it},
                placeholder={Text(hint,color=T3,style=Typo.bodyLarge.copy(fontStyle=FontStyle.Italic))},
                modifier=Modifier.fillMaxWidth(),singleLine=true,shape=RoundedCornerShape(12.dp),
                colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=accent,unfocusedBorderColor=L6,
                    focusedContainerColor=L4,unfocusedContainerColor=L4,cursorColor=accent),
                textStyle=Typo.bodyLarge.copy(color=T1))
            Spacer(Modifier.height(14.dp))
            Button(onClick={onAdd(name.trim());onDismiss()},enabled=name.isNotBlank(),
                modifier=Modifier.fillMaxWidth().height(50.dp),shape=RoundedCornerShape(12.dp),
                colors=ButtonDefaults.buttonColors(containerColor=accent,contentColor=L0)) {
                Text("Créer",style=Typo.labelLarge.copy(fontFamily=CinzelFamily,fontSize=14.sp))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CAMPAIGN LIST
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignListScreen(
    campaigns: List<Campaign>,
    onOpen: (Campaign)->Unit,
    lastOpened: Campaign? = null,
    onOpenNpcById: (String,String)->Unit, onOpenLocById: (String,String)->Unit,
    onAdd: (String)->Unit,
    onEdit: (Campaign)->Unit, onDelete: (Campaign)->Unit,
    onExport: ()->Unit, onImport: ()->Unit
) {
    var showAdd     by remember { mutableStateOf(false) }
    var showOptions by remember { mutableStateOf(false) }
    var query       by remember { mutableStateOf("") }
    var scope       by remember { mutableStateOf(SearchScope.ALL) }
    var editTarget  by remember { mutableStateOf<Campaign?>(null) }
    var delTarget   by remember { mutableStateOf<Campaign?>(null) }

    if (showAdd) AddSheet("Nouvelle campagne","Nom de la campagne…",GOLD,{showAdd=false}){onAdd(it)}
    editTarget?.let{c-> CampaignEditDialog(c,{editTarget=null}){onEdit(it);editTarget=null}}
    delTarget?.let{c-> ConfirmDelete("Supprimer ?",
        "« ${c.name} » et ses ${c.npcs.size+c.locations.size} entrées seront supprimées.",
        {onDelete(c);delTarget=null},{delTarget=null})}

    if (showOptions) AlertDialog(onDismissRequest={showOptions=false}, containerColor=L3,
        title={Text("Options",style=Typo.headlineSmall)},
        text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){
            // Taille du texte
            Text("TAILLE DU TEXTE", style=Typo.labelMedium.copy(color=GOLD_MID))
            val ctxStore = LocalContext.current
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)){
                listOf("Petit" to 0.9f, "Normal" to 1f, "Grand" to 1.15f).forEach { (lab, sc) ->
                    val sel = kotlin.math.abs(TextPref.scale - sc) < 0.01f
                    Box(Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                        .background(if(sel) GOLD.copy(alpha=0.18f) else L4)
                        .border(1.dp, if(sel) GOLD else L5, RoundedCornerShape(10.dp))
                        .clickable{ TextPref.scale = sc; LocalStore(ctxStore).textScale = sc }
                        .padding(vertical=10.dp), contentAlignment=Alignment.Center){
                        Text(lab, style=Typo.labelLarge.copy(color=if(sel) GOLD else T2))
                    }
                }
            }
            HorizontalDivider(color=L5, modifier=Modifier.padding(vertical=4.dp))
            // Compact cards toggle
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .clickable{ val nv=!CompactMode.on; CompactMode.on=nv; LocalStore(ctxStore).compactCards=nv }
                .padding(horizontal=4.dp, vertical=8.dp),
                verticalAlignment=Alignment.CenterVertically){
                Icon(Icons.Default.ViewAgenda, null, Modifier.size(18.dp), tint=GOLD)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)){
                    Text("Cartes compactes", style=Typo.bodyMedium.copy(color=T1))
                    Text("Liste dense, vignettes au lieu de grandes photos",
                        style=Typo.labelSmall.copy(color=T4))
                }
                Switch(checked=CompactMode.on, onCheckedChange={ nv-> CompactMode.on=nv; LocalStore(ctxStore).compactCards=nv },
                    colors=SwitchDefaults.colors(checkedThumbColor=GOLD, checkedTrackColor=GOLD_DIM,
                        uncheckedThumbColor=T4, uncheckedTrackColor=L4))
            }
            HorizontalDivider(color=L5, modifier=Modifier.padding(vertical=4.dp))
            TextButton(onClick={onImport();showOptions=false},modifier=Modifier.fillMaxWidth()){
                Icon(Icons.Default.FileUpload,null,Modifier.size(16.dp),tint=GOLD)
                Spacer(Modifier.width(10.dp)); Text("Importer",style=Typo.bodyMedium.copy(color=T1))}
            TextButton(onClick={onExport();showOptions=false},modifier=Modifier.fillMaxWidth()){
                Icon(Icons.Default.FileDownload,null,Modifier.size(16.dp),tint=GOLD)
                Spacer(Modifier.width(10.dp)); Text("Exporter",style=Typo.bodyMedium.copy(color=T1))}}},
        confirmButton={TextButton(onClick={showOptions=false}){Text("Fermer")}})

    val campaignListState = rememberLazyListState()
    val fabExpanded by remember { derivedStateOf { campaignListState.firstVisibleItemIndex == 0 } }
    Scaffold(containerColor=Color.Transparent,
        floatingActionButton={
            ExtendedFloatingActionButton(onClick={showAdd=true},
                expanded=fabExpanded,
                containerColor=GOLD,contentColor=L0,
                modifier=Modifier.navigationBarsPadding()
                    .shadow(16.dp, RoundedCornerShape(18.dp), ambientColor=GOLD, spotColor=L0),
                icon={Icon(Icons.Default.Add,null,Modifier.size(20.dp))},
                text={Text("Campagne",style=Typo.labelLarge.copy(fontFamily=CinzelFamily,fontSize=13.sp))})
        }) { pads ->
        LazyColumn(Modifier.fillMaxSize(), state=campaignListState,
            contentPadding=PaddingValues(bottom=pads.calculateBottomPadding()+16.dp)) {
            item {
                Box(Modifier.fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(L3,L2,L1)))
                    .statusBarsPadding()
                    .padding(start=22.dp,end=16.dp,top=20.dp,bottom=18.dp)) {
                    Column {
                        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Top) {
                            Column(Modifier.weight(1f)) {
                                // Eyebrow with flanking dots
                                Row(verticalAlignment=Alignment.CenterVertically){
                                    Box(Modifier.size(3.dp).clip(CircleShape).background(GOLD_MID))
                                    Spacer(Modifier.width(7.dp))
                                    Text("AIREALM",style=Typo.labelMedium.copy(color=GOLD_MID,letterSpacing=5.sp))
                                }
                                Spacer(Modifier.height(4.dp))
                                // Title with soft glow behind
                                Box(contentAlignment=Alignment.CenterStart){
                                    // Breathing halo — now a real glowing aura behind the title
                                    val breathe = rememberInfiniteTransition(label="halo")
                                    val haloA by breathe.animateFloat(
                                        initialValue=0.30f, targetValue=0.62f,
                                        animationSpec=infiniteRepeatable(
                                            tween(4200, easing=FastOutSlowInEasing),
                                            repeatMode=RepeatMode.Reverse), label="haloA")
                                    // soft radial bloom
                                    Box(Modifier.matchParentSize().offset(y=2.dp).blur(38.dp)
                                        .background(Brush.radialGradient(
                                            listOf(GOLD.copy(alpha=haloA*0.5f), Color.Transparent))))
                                    // glow copy of the text
                                    Text("Codex",style=Typo.displayMedium.copy(
                                        color=GOLD_HI.copy(alpha=haloA), fontSize=44.sp),
                                        modifier=Modifier.blur(20.dp))
                                    Text("Codex",style=Typo.displayMedium.copy(color=GOLD_HI, fontSize=44.sp))
                                }
                                Spacer(Modifier.height(8.dp))
                                // Double-rule ornament ──◆──
                                Row(Modifier.width(170.dp), verticalAlignment=Alignment.CenterVertically) {
                                    Box(Modifier.weight(1f).height(1.5.dp).background(
                                        Brush.horizontalGradient(listOf(GOLD, GOLD.copy(alpha=0f)))))
                                    Text("◆", style=TextStyle(fontSize=8.sp, color=GOLD),
                                        modifier=Modifier.padding(horizontal=8.dp))
                                    Box(Modifier.weight(1f).height(1.dp).background(
                                        Brush.horizontalGradient(listOf(GOLD.copy(alpha=0.4f), GOLD.copy(alpha=0f)))))
                                }
                            }
                            IconButton(onClick={showOptions=true}){
                                Icon(Icons.Default.MoreVert,"Options",tint=T3)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically) {
                            StatItemAnim(campaigns.size,"Campagnes",GOLD,Modifier.weight(1f))
                            StatDivider()
                            StatItemAnim(campaigns.sumOf{it.npcs.size},"Personnages",GOLD_HI,Modifier.weight(1f))
                            StatDivider()
                            StatItemAnim(campaigns.sumOf{it.locations.size},"Lieux",TEAL,Modifier.weight(1f))
                        }
                    }
                }
                GoldLine(alpha=0.25f)
            }
            if (campaigns.isEmpty()) item {
                Box(Modifier.fillMaxWidth().padding(64.dp),contentAlignment=Alignment.Center){
                    Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)){
                        Icon(Icons.Outlined.MenuBook,null,Modifier.size(44.dp),tint=T4)
                        Text("Ton Codex est vierge",style=Typo.headlineSmall.copy(color=T2))
                        Text("Appuie sur + pour commencer",style=Typo.bodySmall.copy(color=T4))
                    }
                }
            }
            // ── Global search (NPCs + Lieux seulement) ────────────────────────
            item {
                SearchField(query, { query = it },
                    Modifier.padding(horizontal=14.dp).padding(top=10.dp, bottom=4.dp))
            }

            if (query.isBlank()) {
                // "Reprendre" — quick resume of last opened campaign
                if (lastOpened != null && campaigns.size > 1) {
                    item(key="continue") {
                        ContinueCard(lastOpened, Modifier.animateItem()){ onOpen(lastOpened) }
                    }
                }
                items(campaigns,key={it.id}){c->
                    Box(Modifier.animateItem()) {
                        CampaignCard(c,{onOpen(c)},{editTarget=c},{delTarget=c})
                    }
                }
            } else {
                // Scope filter chips
                item {
                    Row(Modifier.fillMaxWidth().padding(horizontal=14.dp,vertical=4.dp),
                        horizontalArrangement=Arrangement.spacedBy(8.dp)){
                        ScopeChip("Tout", scope==SearchScope.ALL){scope=SearchScope.ALL}
                        ScopeChip("Personnages", scope==SearchScope.NPC, GOLD){scope=SearchScope.NPC}
                        ScopeChip("Lieux", scope==SearchScope.LOCATION, TEAL){scope=SearchScope.LOCATION}
                    }
                }
                val allHits = buildSearchHits(campaigns, query)
                val hits = when(scope){
                    SearchScope.ALL -> allHits
                    SearchScope.NPC -> allHits.filter{it.kind==HitKind.NPC}
                    SearchScope.LOCATION -> allHits.filter{it.kind==HitKind.LOCATION}
                }
                if (hits.isEmpty()) item {
                    Box(Modifier.fillMaxWidth().padding(40.dp),contentAlignment=Alignment.Center){
                        Text("Aucun résultat pour « $query »",
                            style=Typo.bodyMedium.copy(color=T3,fontStyle=FontStyle.Italic))
                    }
                }
                items(hits, key={it.key}){ hit ->
                    Box(Modifier.animateItem()){
                        SearchResultRow(hit) {
                            when (hit.kind) {
                                HitKind.NPC      -> onOpenNpcById(hit.campaignId, hit.entityId)
                                HitKind.LOCATION -> onOpenLocById(hit.campaignId, hit.entityId)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CampaignCard(c: Campaign, onOpen:()->Unit, onEdit:()->Unit, onDelete:()->Unit) {
    val seal = sealHue(c.name)
    var menuOpen by remember { mutableStateOf(false) }
    // COVER-STYLE campaign card: big title over art, narrative subtitle, metadata below
    Box(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=9.dp)
        .floatingSurface(RoundedCornerShape(22.dp), glow=seal, elevation=16.dp, fill=L2)
        .border(1.dp, Brush.verticalGradient(listOf(seal.copy(alpha=0.30f), L5.copy(alpha=0.4f))), RoundedCornerShape(22.dp))
        .pressable(onClickLabel="Ouvrir la campagne"){onOpen()}) {
        Column {
            // ── Cover area: photo/monogram + title overlaid ──────────────────
            Box(Modifier.fillMaxWidth().height(150.dp)
                .clip(RoundedCornerShape(topStart=22.dp, topEnd=22.dp))) {
                if(c.photoUri!=null){
                    AsyncImage(model=ImageRequest.Builder(LocalContext.current).data(File(c.photoUri)).crossfade(true).build(),
                        contentDescription=null, contentScale=ContentScale.Crop, modifier=Modifier.fillMaxSize())
                } else {
                    Box(Modifier.fillMaxSize().background(nameGrad(c.name))){
                        EngravedMonogram(c.name.take(1).uppercase(), seal, 110.sp,
                            Modifier.align(Alignment.CenterEnd).padding(end=18.dp), baseAlpha=0.13f)
                    }
                }
                // scrim for legibility
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(
                    0.35f to Color.Transparent, 1f to L0.copy(alpha=0.92f))))
                // title + narrative subtitle, bottom-left
                Column(Modifier.align(Alignment.BottomStart).padding(start=20.dp, end=56.dp, bottom=16.dp)){
                    Text(c.name, style=Typo.headlineMedium.copy(color=Color.White,
                        shadow=Shadow(L0, Offset(0f,2f), 12f)), maxLines=1, overflow=TextOverflow.Ellipsis)
                    if(c.description.isNotBlank())
                        Text(c.description, style=Typo.bodySmall.copy(color=T2.copy(alpha=0.9f),
                            fontStyle=FontStyle.Italic), maxLines=1, overflow=TextOverflow.Ellipsis,
                            modifier=Modifier.padding(top=3.dp))
                }
                // discreet ⋮ menu, top-right
                Box(Modifier.align(Alignment.TopEnd).padding(8.dp)){
                    Box(Modifier.size(36.dp).clip(CircleShape).background(L0.copy(alpha=0.4f))
                        .clickable{ menuOpen=true }, contentAlignment=Alignment.Center){
                        Icon(Icons.Default.MoreVert, "Options", Modifier.size(18.dp), tint=T1)
                    }
                    DropdownMenu(expanded=menuOpen, onDismissRequest={menuOpen=false},
                        containerColor=L3){
                        DropdownMenuItem(text={Text("Modifier", color=T1)},
                            leadingIcon={Icon(Icons.Default.Edit,null,Modifier.size(18.dp),tint=T2)},
                            onClick={menuOpen=false; onEdit()})
                        DropdownMenuItem(text={Text("Supprimer", color=CRIM)},
                            leadingIcon={Icon(Icons.Default.Delete,null,Modifier.size(18.dp),tint=CRIM)},
                            onClick={menuOpen=false; onDelete()})
                    }
                }
            }
            // ── Metadata strip below ─────────────────────────────────────────
            Row(Modifier.fillMaxWidth().padding(start=20.dp,end=20.dp,top=12.dp,bottom=14.dp),
                horizontalArrangement=Arrangement.spacedBy(7.dp)){
                MetaPill(Icons.Default.Person, "${c.npcs.size}", GOLD)
                MetaPill(Icons.Default.Place, "${c.locations.size}", TEAL)
                if(c.labels.isNotEmpty())
                    MetaPill(Icons.Default.Label, "${c.labels.size}", T3)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CampaignEditDialog(c: Campaign, onDismiss:()->Unit, onSave:(Campaign)->Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var name by remember{mutableStateOf(c.name)}
    var desc by remember{mutableStateOf(c.description)}
    var photo by remember{mutableStateOf(c.photoUri)}
    var theme by remember{mutableStateOf(c.theme)}
    var accentCol by remember{mutableStateOf(c.accentColor)}
    var gauges by remember{mutableStateOf(c.gauges)}
    var showGauges by remember{mutableStateOf(false)}
    if (showGauges) GaugeManagerDialog(gauges, {showGauges=false}) { gauges = it }
    val photoL = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch {
            val path = PhotoStore.save(ctx, uri)
            if (path != null) { photo?.let { PhotoStore.delete(it) }; photo = path }
            else withContext(Dispatchers.Main){ toast(ctx,"Impossible de lire la photo") }
        }
    }
    AlertDialog(onDismissRequest=onDismiss,containerColor=L3,
        title={Text("Modifier",style=Typo.headlineMedium)},
        text={Column(verticalArrangement=Arrangement.spacedBy(10.dp)){
            // Photo de couverture
            Box(Modifier.fillMaxWidth().height(96.dp).clip(RoundedCornerShape(12.dp))
                .background(L4).border(1.dp,GOLD.copy(alpha=0.25f),RoundedCornerShape(12.dp))
                .clickable{photoL.launch("image/*")}, contentAlignment=Alignment.Center){
                if(photo!=null){
                    AsyncImage(model=ImageRequest.Builder(ctx).data(File(photo!!)).crossfade(true).build(),
                        contentDescription=null, contentScale=ContentScale.Crop, modifier=Modifier.fillMaxSize())
                    Box(Modifier.fillMaxSize().background(L0.copy(alpha=0.25f)))
                    Box(Modifier.align(Alignment.TopEnd).padding(6.dp).size(28.dp).clip(CircleShape)
                        .background(L0.copy(alpha=0.6f)).clickable{photo?.let{PhotoStore.delete(it)};photo=null},
                        contentAlignment=Alignment.Center){
                        Icon(Icons.Default.Close,"Retirer la photo",Modifier.size(15.dp),tint=T1)
                    }
                } else {
                    Column(horizontalAlignment=Alignment.CenterHorizontally){
                        Icon(Icons.Default.AddAPhoto,null,Modifier.size(22.dp),tint=GOLD)
                        Spacer(Modifier.height(4.dp))
                        Text("Photo de couverture",style=Typo.labelSmall.copy(color=GOLD.copy(alpha=0.8f)))
                    }
                }
            }
            OutlinedTextField(value=name,onValueChange={name=it},label={Text("Nom")},singleLine=true,modifier=Modifier.fillMaxWidth(),
                colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=GOLD,cursorColor=GOLD,focusedContainerColor=L4,unfocusedContainerColor=L4))
            OutlinedTextField(value=desc,onValueChange={desc=it},label={Text("Description")},minLines=2,modifier=Modifier.fillMaxWidth(),
                colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=GOLD,cursorColor=GOLD,focusedContainerColor=L4,unfocusedContainerColor=L4))
            // Ambient theme picker
            Text("AMBIANCE", style=Typo.labelMedium.copy(color=GOLD_MID), modifier=Modifier.padding(top=2.dp))
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement=Arrangement.spacedBy(8.dp)){
                CodexTheme.entries.forEach { t ->
                    val sel = theme == t.id
                    Row(Modifier.clip(RoundedCornerShape(999.dp))
                        .background(if(sel) t.ember.copy(alpha=0.20f) else L4)
                        .border(1.dp, if(sel) t.ember else L5, RoundedCornerShape(999.dp))
                        .clickable{ theme = t.id }.padding(horizontal=12.dp, vertical=7.dp),
                        verticalAlignment=Alignment.CenterVertically,
                        horizontalArrangement=Arrangement.spacedBy(6.dp)){
                        Box(Modifier.size(10.dp).clip(CircleShape).background(t.ember))
                        Text(t.label, style=Typo.labelMedium.copy(color=if(sel) T1 else T3))
                    }
                }
            }
            // Couleur d'accent signature
            Text("COULEUR D'ACCENT", style=Typo.labelMedium.copy(color=GOLD_MID))
            FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp), verticalArrangement=Arrangement.spacedBy(8.dp)){
                // Default (terracotta) swatch = index -1
                Box(Modifier.size(30.dp).clip(CircleShape).background(GOLD.copy(alpha=0.25f))
                    .border(2.dp, if(accentCol==-1) GOLD else Color.Transparent, CircleShape)
                    .clickable{ accentCol = -1 }, contentAlignment=Alignment.Center){
                    Box(Modifier.size(15.dp).clip(CircleShape).background(GOLD))
                }
                LABEL_COLORS.forEachIndexed { i, col ->
                    Box(Modifier.size(30.dp).clip(CircleShape).background(col.copy(alpha=0.25f))
                        .border(2.dp, if(accentCol==i) col else Color.Transparent, CircleShape)
                        .clickable{ accentCol = i }, contentAlignment=Alignment.Center){
                        Box(Modifier.size(15.dp).clip(CircleShape).background(col))
                    }
                }
            }
            // Gauges manager entry
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(L4)
                .border(1.dp, L5, RoundedCornerShape(10.dp))
                .clickable{ showGauges=true }.padding(horizontal=12.dp, vertical=10.dp),
                verticalAlignment=Alignment.CenterVertically){
                Icon(Icons.Default.Tune, null, Modifier.size(18.dp), tint=GOLD)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)){
                    Text("Jauges personnalisées", style=Typo.labelLarge.copy(color=T1))
                    Text(if(gauges.isEmpty()) "Aucune — appuie pour créer"
                         else "${gauges.size} jauge${if(gauges.size>1)"s" else ""}",
                         style=Typo.labelSmall.copy(color=T3))
                }
                Icon(Icons.Default.ChevronRight, null, Modifier.size(20.dp), tint=T4)
            }
        }},
        confirmButton={Button(onClick={onSave(c.copy(name=name.ifBlank{c.name},description=desc,photoUri=photo,theme=theme,gauges=gauges,accentColor=accentCol))},
            colors=ButtonDefaults.buttonColors(containerColor=GOLD,contentColor=L0)){Text("Sauver")}},
        dismissButton={TextButton(onClick=onDismiss){Text("Annuler")}})
}

// ─────────────────────────────────────────────────────────────────────────────
// CAMPAIGN DETAIL — tabs with label filter
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignDetailScreen(
    c: Campaign, onBack:()->Unit,
    onOpenRelations:()->Unit = {},
    onOpenNpc:(Npc)->Unit, onOpenLoc:(Location)->Unit,
    onAddNpc:(String)->Unit, onDelNpc:(Npc)->Unit, onTogglePinNpc:(Npc)->Unit = {},
    onAddLoc:(String)->Unit, onDelLoc:(Location)->Unit,
    onUpdateLabels:(List<CampaignLabel>)->Unit,
    onImportNpc:(Npc)->Unit, onImportLoc:(Location)->Unit,
    onAddJournal:(JournalEntry)->Unit, onUpdateJournal:(JournalEntry)->Unit, onDelJournal:(JournalEntry)->Unit,
    onAddGalleryPhoto:(Uri)->Unit, onUpdateCaption:(GalleryPhoto)->Unit, onDelGalleryPhoto:(GalleryPhoto)->Unit
) {
    val ctx = LocalContext.current
    var tab          by remember{mutableIntStateOf(0)}
    val listStates = listOf(rememberLazyListState(), rememberLazyListState(),
                            rememberLazyListState(), rememberLazyListState())
    var showAddNpc     by remember{mutableStateOf(false)}
    var showAddLoc     by remember{mutableStateOf(false)}
    var showAddJournal by remember{mutableStateOf(false)}
    var showImport     by remember{mutableStateOf(false)}
    var showNpcChoice  by remember{mutableStateOf(false)}
    var showLocChoice  by remember{mutableStateOf(false)}
    var showImportLoc  by remember{mutableStateOf(false)}
    var editJournal    by remember{mutableStateOf<JournalEntry?>(null)}
    var showLabels   by remember{mutableStateOf(false)}
    var activeFilter by remember{mutableStateOf<String?>(null)}  // null = all
    var sortMode by remember{mutableStateOf(0)}  // 0=nom, 1=année, 2=clique

    if(showAddNpc) AddSheet("Nouveau personnage","Nom…",GOLD,{showAddNpc=false}){onAddNpc(it)}
    if(showAddLoc) AddSheet("Nouveau lieu","Nom…",TEAL,{showAddLoc=false}){onAddLoc(it)}
    if(showAddJournal) JournalSheet(null,{showAddJournal=false}){onAddJournal(it)}
    if(showImport) ImportSheet({showImport=false}, onImport={onImportNpc(it)}, onImportLoc={onImportLoc(it)})
    if(showNpcChoice) NpcChoiceSheet(
        onDismiss={showNpcChoice=false},
        onImport={showNpcChoice=false; showImport=true},
        onBlank={showNpcChoice=false; showAddNpc=true})
    if(showLocChoice) LocChoiceSheet(
        onDismiss={showLocChoice=false},
        onImport={showLocChoice=false; showImportLoc=true},
        onBlank={showLocChoice=false; showAddLoc=true})
    if(showImportLoc) ImportLocSheet({showImportLoc=false}){onImportLoc(it)}
    editJournal?.let{ entry -> JournalSheet(entry,{editJournal=null}){onUpdateJournal(it)} }
    if(showLabels) LabelManagerDialog(c.labels,{showLabels=false},onUpdateLabels)

    Scaffold(containerColor=Color.Transparent,
        floatingActionButton={
            val galleryPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){ it?.let(onAddGalleryPhoto) }
            val fabExpanded by remember { derivedStateOf {
                listStates[tab].firstVisibleItemIndex == 0 && listStates[tab].firstVisibleItemScrollOffset < 40
            } }
            ExtendedFloatingActionButton(
                onClick={when(tab){0->showNpcChoice=true;1->showLocChoice=true;2->showAddJournal=true;else->galleryPicker.launch("image/*")}},
                expanded=fabExpanded,
                containerColor=when(tab){0->GOLD;1->TEAL;2->GOLD_MID;else->TEAL},contentColor=L0,
                modifier=Modifier.navigationBarsPadding()
                    .shadow(14.dp, RoundedCornerShape(18.dp), ambientColor=GOLD, spotColor=L0),
                icon={Icon(when(tab){0->Icons.Default.PersonAdd;1->Icons.Default.AddLocation;2->Icons.Default.HistoryEdu;else->Icons.Default.AddPhotoAlternate},null,Modifier.size(20.dp))},
                text={Text(when(tab){0->"Personnage";1->"Lieu";2->"Entrée";else->"Photo"},style=Typo.labelLarge.copy(fontFamily=CinzelFamily,fontSize=13.sp))})
        }) { pads ->
        Column(Modifier.fillMaxSize()) {
            // Header
            Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(L3,L2))).statusBarsPadding()) {
                Column {
                    Row(Modifier.fillMaxWidth().padding(start=6.dp,end=8.dp,top=8.dp,bottom=4.dp),
                        verticalAlignment=Alignment.CenterVertically) {
                        IconButton(onClick=onBack){Icon(Icons.Default.ArrowBack,"Retour",tint=GOLD,modifier=Modifier.size(22.dp))}
                        Column(Modifier.weight(1f)){
                            Text(c.name,style=Typo.headlineMedium.copy(color=T1),maxLines=1,overflow=TextOverflow.Ellipsis)
                            if(c.description.isNotBlank()) Text(c.description,style=Typo.bodySmall.copy(color=T3),maxLines=1,overflow=TextOverflow.Ellipsis)
                        }
                        // Relations dashboard
                        if (c.npcs.isNotEmpty())
                            IconButton(onClick=onOpenRelations){
                                Icon(Icons.Default.Diversity3,"Tableau des relations",tint=T3,modifier=Modifier.size(20.dp))
                            }
                        // Import an Airealm NPC card
                        IconButton(onClick={showImport=true}){
                            Icon(Icons.Default.Download,"Importer une fiche Airealm",tint=T3,modifier=Modifier.size(19.dp))
                        }
                        // Copy full campaign summary for Airealm
                        IconButton(onClick={
                            val clip=ClipData.newPlainText("Campagne",campaignFullExport(c))
                            (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                            toast(ctx,"Campagne copiée")
                        }){
                            Icon(Icons.Default.ContentCopy,"Copier toute la campagne",tint=T3,modifier=Modifier.size(18.dp))
                        }
                        // Labels manage button
                        IconButton(onClick={showLabels=true}){
                            Icon(Icons.Default.Label,"Labels",tint=if(c.labels.isNotEmpty())GOLD else T3,modifier=Modifier.size(20.dp))
                        }
                    }
                    // Label filter bar — only shown when there are labels
                    if (c.labels.isNotEmpty()) {
                        LazyRow(Modifier.fillMaxWidth(),
                            contentPadding=PaddingValues(horizontal=14.dp,vertical=6.dp),
                            horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                            // "Tous" chip
                            item {
                                Box(Modifier.clip(RoundedCornerShape(999.dp))
                                    .background(if(activeFilter==null)GOLD.copy(alpha=0.18f) else L4)
                                    .border(1.dp,if(activeFilter==null)GOLD.copy(alpha=0.55f) else L5,RoundedCornerShape(999.dp))
                                    .clickable{activeFilter=null}
                                    .padding(horizontal=10.dp,vertical=4.dp)) {
                                    Text("Tous",style=Typo.labelMedium.copy(color=if(activeFilter==null)GOLD else T3))
                                }
                            }
                            items(c.labels){lbl->
                                LabelChip(lbl,selected=activeFilter==lbl.id,small=true,onClick={activeFilter=if(activeFilter==lbl.id)null else lbl.id})
                            }
                        }
                        GoldLine(0.12f)
                    }
                    TabRow(selectedTabIndex=tab,containerColor=Color.Transparent,contentColor=GOLD,
                        indicator={tabs->Box(Modifier.tabIndicatorOffset(tabs[tab]).height(2.dp)
                            .background(Brush.horizontalGradient(listOf(GOLD.copy(alpha=0f),GOLD_HI,GOLD.copy(alpha=0f)))))},
                        divider={GoldLine(0.18f)}) {
                        listOf("Personnages" to c.npcs.size,"Lieux" to c.locations.size,"Journal" to c.journal.size,"Galerie" to c.gallery.size)
                            .forEachIndexed{i,(lbl,cnt)->
                                Tab(selected=tab==i,onClick={tab=i}){
                                    Text("$lbl · $cnt",
                                        style=Typo.labelLarge.copy(fontFamily=CinzelFamily,fontSize=12.sp,
                                            color=if(tab==i)GOLD_HI else T3),
                                        modifier=Modifier.padding(vertical=14.dp))
                                }
                            }
                    }
                }
            }
            Box(Modifier.weight(1f)){
                // helpers : année + clique extraits des champs
                fun yearRank(npc: Npc): Int {
                    val r = (npc.role + " " + npc.major).lowercase()
                    return when {
                        "freshman" in r -> 1; "sophomore" in r -> 2
                        "junior" in r -> 3; "senior" in r -> 4
                        "grad" in r || "phd" in r -> 5; else -> 9
                    }
                }
                fun cliqueOf(npc: Npc) = (npc.dna["Clique"] ?: "").trim()
                val baseNpcs = if(activeFilter==null) c.npcs else c.npcs.filter{activeFilter in it.labelIds}
                val filteredNpcs = when(sortMode){
                    1 -> baseNpcs.sortedWith(compareBy({yearRank(it)},{it.name.lowercase()}))
                    2 -> baseNpcs.sortedWith(compareBy({cliqueOf(it).ifBlank{"zzz"}.lowercase()},{it.name.lowercase()}))
                    else -> baseNpcs.sortedBy{it.name.lowercase()}
                }
                val filteredLocs  = if(activeFilter==null) c.locations else c.locations.filter{activeFilter in it.labelIds}
                when(tab){
                    0 -> Column(Modifier.fillMaxSize()){
                        // Sort bar
                        var sortMenu by remember { mutableStateOf(false) }
                        val sortLabels = listOf("Nom","Année","Clique")
                        Row(Modifier.fillMaxWidth().padding(start=18.dp,end=14.dp,top=6.dp,bottom=2.dp),
                            verticalAlignment=Alignment.CenterVertically){
                            Text("${filteredNpcs.size} personnage${if(filteredNpcs.size>1)"s" else ""}",
                                style=Typo.labelMedium.copy(color=T4), modifier=Modifier.weight(1f))
                            Box{
                                Row(Modifier.clip(RoundedCornerShape(999.dp)).background(L3)
                                    .clickable{sortMenu=true}.padding(horizontal=12.dp,vertical=6.dp),
                                    verticalAlignment=Alignment.CenterVertically,
                                    horizontalArrangement=Arrangement.spacedBy(5.dp)){
                                    Icon(Icons.Default.SwapVert,null,Modifier.size(15.dp),tint=GOLD)
                                    Text(sortLabels[sortMode],style=Typo.labelMedium.copy(color=T2))
                                }
                                DropdownMenu(expanded=sortMenu,onDismissRequest={sortMenu=false},containerColor=L3){
                                    sortLabels.forEachIndexed{ idx, lab ->
                                        DropdownMenuItem(
                                            text={Text(lab,color=if(sortMode==idx)GOLD else T1)},
                                            leadingIcon={if(sortMode==idx)Icon(Icons.Default.Check,null,Modifier.size(16.dp),tint=GOLD)},
                                            onClick={sortMode=idx;sortMenu=false})
                                    }
                                }
                            }
                        }
                        EntityList(filteredNpcs,
                        emptyTitle=if(activeFilter!=null)"Aucun résultat" else "Aucun personnage",
                        emptyHint=if(activeFilter!=null)"Aucun NPC avec ce label" else "Appuie sur + pour ajouter",
                        emptyIcon=Icons.Outlined.Person, accent=GOLD,
                        getKey={it.id},getName={it.name},getSub={it.role},
                        getPreview={prettyHeader(it.shortCard)},getTags={it.tags},
                        getLabels={ids->c.labels.filter{l->l.id in ids}},
                        getLabelIds={it.labelIds},
                        getPhoto={it.photoUris.firstOrNull()}, getFocal={it.heroFocal},
                        getPinned={it.pinned}, onTogglePin={onTogglePinNpc(it)},
                        onOpen={onOpenNpc(it)},onDel={onDelNpc(it)},
                        bottomPad=pads.calculateBottomPadding(), listState=listStates[0])
                    }
                    1 -> EntityList(filteredLocs.sortedBy{it.name.lowercase()}, typeIcon=Icons.Default.Place,
                        emptyTitle=if(activeFilter!=null)"Aucun résultat" else "Aucun lieu",
                        emptyHint=if(activeFilter!=null)"Aucun lieu avec ce label" else "Appuie sur + pour ajouter",
                        emptyIcon=Icons.Outlined.Place, accent=TEAL,
                        getKey={it.id},getName={it.name},getSub={it.type},
                        getPreview={it.description},getTags={it.tags},
                        getLabels={ids->c.labels.filter{l->l.id in ids}},
                        getLabelIds={it.labelIds},
                        getPhoto={it.photoUris.firstOrNull()},
                        onOpen={onOpenLoc(it)},onDel={onDelLoc(it)},
                        bottomPad=pads.calculateBottomPadding(), listState=listStates[1])
                    2 -> JournalTab(c.journal,
                        onEdit={editJournal=it}, onDel=onDelJournal,
                        bottomPad=pads.calculateBottomPadding())
                    3 -> GalleryTab(c.gallery,
                        onUpdateCaption=onUpdateCaption, onDel=onDelGalleryPhoto,
                        bottomPad=pads.calculateBottomPadding())
                }
            }
        }
    }
}

@Composable
fun <T> EntityList(
    items: List<T>, emptyTitle: String, emptyHint: String, emptyIcon: ImageVector,
    accent: Color, getKey:(T)->String, getName:(T)->String, getSub:(T)->String,
    getPreview:(T)->String, getTags:(T)->String,
    getLabels:(List<String>)->List<CampaignLabel>, getLabelIds:(T)->List<String>,
    getPhoto:(T)->String?, onOpen:(T)->Unit, onDel:(T)->Unit, bottomPad: Dp,
    typeIcon: ImageVector = Icons.Default.Person, getFocal:(T)->Float = { 0.5f },
    getPinned:(T)->Boolean = { false }, onTogglePin:(T)->Unit = {},
    listState: LazyListState = rememberLazyListState()
) {
    var delTarget by remember{mutableStateOf<T?>(null)}
    delTarget?.let{t-> ConfirmDelete("Supprimer ?","« ${getName(t)} » sera supprimé.",
        {onDel(t);delTarget=null},{delTarget=null})}

    if(items.isEmpty()){
        Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){
            Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)){
                Ornament(accent)
                Icon(emptyIcon,null,Modifier.size(44.dp),tint=T4)
                Text(emptyTitle,style=Typo.headlineSmall.copy(color=T3))
                Text(emptyHint,style=Typo.bodySmall.copy(color=T4))
                Ornament(accent)
            }
        }
    } else {
        LazyColumn(Modifier.fillMaxSize(), state=listState,
            contentPadding=PaddingValues(top=8.dp,bottom=bottomPad+104.dp),
            verticalArrangement=Arrangement.spacedBy(8.dp)){
            itemsIndexed(items, key={_,it->getKey(it)}){ index, item ->
                val labels = getLabels(getLabelIds(item))
                CascadeIn(index) {
                    Box(Modifier.animateItem()) {
                        EntityCard(getName(item),getSub(item),getPreview(item),getTags(item),
                            labels,getPhoto(item),accent,typeIcon,getFocal(item),{onOpen(item)},{delTarget=item},
                            pinned=getPinned(item), onTogglePin={onTogglePin(item)})
                    }
                }
            }
        }
    }
}

@Composable
fun EntityCard(
    name: String, sub: String, preview: String, tags: String,
    labels: List<CampaignLabel>, photoUri: String?,
    accent: Color, typeIcon: ImageVector = Icons.Default.Person,
    focal: Float = 0.5f, onClick:()->Unit, onDel:()->Unit,
    pinned: Boolean = false, onTogglePin:()->Unit = {}
) {
    val seal = sealHue(name)
    var quickMenu by remember { mutableStateOf(false) }
    val tagListForBody = tags.split(',').map{it.trim()}.filter{it.isNotBlank()}
    val hasBody = preview.isNotBlank() || labels.isNotEmpty() || tagListForBody.isNotEmpty()

    // ── COMPACT MODE: dense row (thumbnail + name/sub) ───────────────────────
    if (CompactMode.on) {
        Box(Modifier.fillMaxWidth().padding(horizontal=16.dp, vertical=5.dp)
            .clip(RoundedCornerShape(14.dp))
            .floatingSurface(RoundedCornerShape(14.dp), glow=seal, elevation=8.dp, fill=L2)
            .border(1.dp, L5.copy(alpha=0.5f), RoundedCornerShape(14.dp))
            .pressable(onClickLabel="Ouvrir", onLongClick={ quickMenu=true }){onClick()}) {
            DropdownMenu(expanded=quickMenu, onDismissRequest={quickMenu=false}, containerColor=L3){
                DropdownMenuItem(text={Text("Ouvrir", color=T1)},
                    leadingIcon={Icon(Icons.Default.OpenInFull,null,Modifier.size(18.dp),tint=T2)},
                    onClick={quickMenu=false; onClick()})
                DropdownMenuItem(text={Text(if(pinned)"Désépingler" else "Épingler", color=T1)},
                    leadingIcon={Icon(if(pinned)Icons.Default.PushPin else Icons.Outlined.PushPin,null,Modifier.size(18.dp),tint=GOLD)},
                    onClick={quickMenu=false; onTogglePin()})
                DropdownMenuItem(text={Text("Supprimer", color=CRIM)},
                    leadingIcon={Icon(Icons.Default.Delete,null,Modifier.size(18.dp),tint=CRIM)},
                    onClick={quickMenu=false; onDel()})
            }
            Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment=Alignment.CenterVertically,
                horizontalArrangement=Arrangement.spacedBy(12.dp)){
                Box(Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).background(nameGrad(name)),
                    contentAlignment=Alignment.Center){
                    if(photoUri!=null) AsyncImage(
                        model=ImageRequest.Builder(LocalContext.current).data(File(photoUri)).crossfade(true).build(),
                        contentDescription=null, contentScale=ContentScale.Crop,
                        alignment=BiasAlignment(0f, focal*2f - 1f), modifier=Modifier.fillMaxSize())
                    else Text(name.take(1).uppercase(), style=Typo.titleLarge.copy(color=seal))
                }
                Column(Modifier.weight(1f)){
                    Row(verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(5.dp)){
                        if(pinned) Icon(Icons.Default.PushPin,null,Modifier.size(13.dp),tint=GOLD)
                        Text(name, style=Typo.titleMedium.copy(color=T1), maxLines=1, overflow=TextOverflow.Ellipsis)
                    }
                    if(sub.isNotBlank())
                        Text(sub, style=Typo.bodySmall.copy(color=T3), maxLines=1, overflow=TextOverflow.Ellipsis)
                }
                Icon(typeIcon, null, Modifier.size(16.dp), tint=accent.copy(alpha=0.7f))
            }
        }
        return
    }
    Box(Modifier.fillMaxWidth().padding(horizontal=16.dp, vertical=9.dp)
        .floatingSurface(RoundedCornerShape(22.dp), glow=seal, elevation=16.dp, fill=L2)
        .border(1.dp, Brush.verticalGradient(listOf(seal.copy(alpha=0.30f), L5.copy(alpha=0.4f))), RoundedCornerShape(22.dp))
        .pressable(onClickLabel="Ouvrir", onLongClick={ quickMenu=true }){onClick()}) {
        DropdownMenu(expanded=quickMenu, onDismissRequest={quickMenu=false}, containerColor=L3){
            DropdownMenuItem(text={Text("Ouvrir", color=T1)},
                leadingIcon={Icon(Icons.Default.OpenInFull,null,Modifier.size(18.dp),tint=T2)},
                onClick={quickMenu=false; onClick()})
            DropdownMenuItem(text={Text(if(pinned)"Désépingler" else "Épingler", color=T1)},
                leadingIcon={Icon(if(pinned)Icons.Default.PushPin else Icons.Outlined.PushPin,null,Modifier.size(18.dp),tint=GOLD)},
                onClick={quickMenu=false; onTogglePin()})
            DropdownMenuItem(text={Text("Supprimer", color=CRIM)},
                leadingIcon={Icon(Icons.Default.Delete,null,Modifier.size(18.dp),tint=CRIM)},
                onClick={quickMenu=false; onDel()})
        }
        Column {
            // ── Framed banner — photo fills, name overlaid at bottom ──────────
            Box(Modifier.fillMaxWidth().height(176.dp)
                .clip(if (hasBody) RoundedCornerShape(topStart=22.dp, topEnd=22.dp)
                      else RoundedCornerShape(22.dp))) {
                if(photoUri!=null){
                    AsyncImage(
                        model=ImageRequest.Builder(LocalContext.current).data(File(photoUri)).crossfade(true).build(),
                        contentDescription=null, contentScale=ContentScale.Crop,
                        alignment=BiasAlignment(0f, focal*2f - 1f),
                        modifier=Modifier.fillMaxSize())
                } else {
                    // Richer placeholder: soft coloured halo + very subtle grain + avatar chip + label
                    Box(Modifier.fillMaxSize().background(nameGrad(name)).drawBehind{
                        val g=seal.copy(alpha=0.05f); val s=20.dp.toPx(); var y=0f
                        while(y<size.height){var x=if((y/s).toInt()%2==0)0f else s/2
                            while(x<size.width){drawCircle(g,0.8f,Offset(x,y));x+=s};y+=s}
                    }){
                        EngravedMonogram(name.take(1).uppercase(), seal, 120.sp,
                            Modifier.align(Alignment.Center), baseAlpha=0.20f)
                        // "Aucune photo" hint, bottom-right
                        Row(Modifier.align(Alignment.BottomEnd).padding(12.dp),
                            verticalAlignment=Alignment.CenterVertically,
                            horizontalArrangement=Arrangement.spacedBy(4.dp)){
                            Icon(Icons.Default.AddAPhoto, null, Modifier.size(11.dp), tint=T4)
                            Text("Aucune photo", style=Typo.labelSmall.copy(color=T4))
                        }
                    }
                }
                // Bottom scrim so the name is always readable over any photo
                Box(Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        0.40f to Color.Transparent,
                        1f to L0.copy(alpha=0.90f))))
                // Type-coloured pill top-left — visual identity per type
                Row(Modifier.align(Alignment.TopStart).padding(12.dp)
                    .clip(RoundedCornerShape(999.dp)).background(L0.copy(alpha=0.62f))
                    .border(1.dp, accent.copy(alpha=0.45f), RoundedCornerShape(999.dp))
                    .padding(horizontal=10.dp, vertical=5.dp),
                    verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(5.dp)){
                    Icon(typeIcon, null, Modifier.size(12.dp), tint=accent)
                    if(sub.isNotBlank())
                        Text(sub.uppercase(), style=Typo.labelSmall.copy(color=T1, letterSpacing=1.2.sp),
                            maxLines=1, overflow=TextOverflow.Ellipsis)
                }
                // Name, bottom-left
                Text(name, style=Typo.headlineSmall.copy(color=Color.White,
                    shadow=Shadow(L0, Offset(0f,2f), 12f)),
                    maxLines=1, overflow=TextOverflow.Ellipsis,
                    modifier=Modifier.align(Alignment.BottomStart).padding(start=18.dp, end=64.dp, bottom=14.dp))
                // Pin badge (top-right, left of delete) when pinned
                if (pinned) {
                    Box(Modifier.align(Alignment.TopEnd).padding(top=8.dp, end=54.dp).size(40.dp)
                        .clip(CircleShape).background(GOLD.copy(alpha=0.85f)),
                        contentAlignment=Alignment.Center){
                        Icon(Icons.Default.PushPin,"Épinglé",Modifier.size(16.dp),tint=L0)
                    }
                }
                // Delete — top right
                Box(Modifier.align(Alignment.TopEnd).padding(8.dp).size(40.dp)
                    .clip(CircleShape).background(L0.copy(alpha=0.5f))
                    .clickable(onClickLabel="Supprimer"){onDel()}, contentAlignment=Alignment.Center){
                    Icon(Icons.Default.Delete,"Supprimer",Modifier.size(17.dp),tint=T1)
                }
            }
            // ── Body — preview + labels, generous padding ─────────────────────
            if (preview.isNotBlank() || labels.isNotEmpty() || tags.isNotBlank()) {
                Column(Modifier.fillMaxWidth().padding(start=18.dp, end=18.dp, top=12.dp, bottom=14.dp)) {
                    if(preview.isNotBlank())
                        Text(preview, style=Typo.bodyMedium.copy(color=T2),
                            maxLines=2, overflow=TextOverflow.Ellipsis)
                    // Labels custom ET tags coexistent — défilement horizontal, labels d'abord
                    val tagList = tags.split(',').map{it.trim()}.filter{it.isNotBlank()}.take(6)
                    if(labels.isNotEmpty() || tagList.isNotEmpty()){
                        LazyRow(Modifier.padding(top=if(preview.isNotBlank()) 10.dp else 0.dp),
                            horizontalArrangement=Arrangement.spacedBy(6.dp)){
                            items(labels){lbl-> LabelChip(lbl,small=true)}
                            items(tagList){ tg -> TagCapsule(tg) }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NPC DETAIL
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NpcScreen(
    npc: Npc, campaignLabels: List<CampaignLabel>,
    campaignLocations: List<Location> = emptyList(),
    campaignGauges: List<Gauge> = emptyList(),
    onOpenLoc: (Location)->Unit = {},
    onBack:()->Unit, onSave:(Npc)->Unit,
    onAddPhoto:(Uri)->Unit, onDelPhoto:(String)->Unit
) {
    val ctx = LocalContext.current
    var e by remember(npc.id){mutableStateOf(npc)}
    val photoL = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){ uri ->
        uri?.let {
            runCatching { ctx.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            onAddPhoto(it)
        }
    }

    fun export() = buildString {
        appendLine("NPC: ${e.name}")
        if(e.role.isNotBlank())          appendLine("Rôle: ${e.role}")
        if(e.major.isNotBlank())         appendLine("Filière: ${e.major}")
        if(e.shortCard.isNotBlank())     appendLine("Carte: ${e.shortCard}")
        if(e.fullCard.isNotBlank())      appendLine(e.fullCard)
        (DNA_KEYS + "Lives" + INERTIA_KEYS + "Facets").forEach { k ->
            e.dna[k]?.takeIf{it.isNotBlank()}?.let { appendLine("${FIELD_LABELS[k] ?: k}: $it") }
        }
        if(e.relationships.isNotBlank()) appendLine("Relations: ${e.relationships}")
        if(e.tags.isNotBlank())          appendLine("Tags: ${e.tags}")
        if(e.labelIds.isNotEmpty())      appendLine("Labels: ${campaignLabels.filter{it.id in e.labelIds}.joinToString{it.name}}")
        if(e.secrets.isNotBlank())       appendLine("Secrets: ${e.secrets}")
        if(e.sceneHistory.isNotBlank())  appendLine("Historique: ${e.sceneHistory}")
    }.trim()

    var readMode by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().imePadding()) {
        var heroViewer by remember { mutableStateOf(false) }
        if (heroViewer) PhotoZoomViewer(npc.photoUris, 0, onClose={heroViewer=false})
        EntityHero(name=e.name.ifBlank{"NPC"},
            subtitle=listOf(e.role,e.major).filter{it.isNotBlank()}.joinToString(" · "),
            firstPhoto=npc.photoUris.firstOrNull(),accent=LocalAccent.current,onBack=onBack,
            focal=e.heroFocal, onFocalChange={ e=e.copy(heroFocal=it) },
            onTapPhoto=if(npc.photoUris.isNotEmpty()){{heroViewer=true}}else null,
            action={
                Row(verticalAlignment=Alignment.CenterVertically){
                    IconButton(onClick={ readMode = !readMode }){
                        Icon(if(readMode) Icons.Default.Edit else Icons.Default.MenuBook,
                            if(readMode) "Mode édition" else "Mode lecture",
                            Modifier.size(18.dp), tint=if(readMode) GOLD_HI else T1)
                    }
                    CopyButton{
                        val clip=ClipData.newPlainText("NPC",export())
                        (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                    }
                }
            })

        val (riseA, riseY) = rememberRiseIn()
        CompositionLocalProvider(LocalReadMode provides readMode) {
        Column(Modifier.weight(1f).graphicsLayer{ alpha = riseA; translationY = riseY }
            .verticalScroll(rememberScrollState())) {
            if (!readMode) PhotoStrip(npc.photoUris,{photoL.launch(arrayOf("image/*"))},onDelPhoto)
            // Remember collapsed/expanded state per section, per fiche
            fun isExpanded(t: String) = t !in e.collapsedSections
            fun setExpanded(t: String, v: Boolean) {
                e = e.copy(collapsedSections = e.collapsedSections.toMutableSet().apply {
                    if (v) remove(t) else add(t) })
            }
            LoreSection("Identité",Icons.Default.Person,
                expandedOverride=isExpanded("Identité"), onExpandChange={setExpanded("Identité",it)},
                summary=e.name.ifBlank{"—"}+if(e.role.isNotBlank())" · ${e.role}"else""){
                LoreField("Nom",e.name,"Nom du personnage"){e=e.copy(name=it)}
                LoreField("Âge · Année · Statut",e.role,"ex. 18yo · Freshman"){e=e.copy(role=it)}
                LoreField("Sexe · Pronoms",e.dna["Gender"] ?: "","ex. Female (She/Her)"){ v ->
                    e=e.copy(dna=e.dna.toMutableMap().apply{ if(v.isBlank())remove("Gender")else put("Gender",v) })}
                LoreField("Filière · Major",e.major,"ex. English Literature"){e=e.copy(major=it)}
                LoreField("Tags libres",e.tags,"ex. allié, majeur, antagoniste"){e=e.copy(tags=it)}
                // Label picker
                if(campaignLabels.isNotEmpty()){
                    Spacer(Modifier.height(8.dp))
                    LabelPicker(campaignLabels,e.labelIds){id->
                        e=e.copy(labelIds=if(id in e.labelIds)e.labelIds-id else e.labelIds+id)}
                    Spacer(Modifier.height(4.dp))
                }
            }
            LoreSection("Cartes Airealm",Icons.Default.Description,
                expandedOverride=isExpanded("Cartes Airealm"), onExpandChange={setExpanded("Cartes Airealm",it)},
                summary=e.shortCard.take(80).ifBlank{"Non renseigné"}){
                LoreField("Version courte",e.shortCard,"Résumé compact à coller dans Airealm",multi=true){e=e.copy(shortCard=it)}
                LoreField("Version complète",e.fullCard,"Description détaillée — souvent longue",multi=true){e=e.copy(fullCard=it)}
            }
            // ── Structured DNA fields, one line each ───────────────────────
            // ── Custom campaign gauges ──────────────────────────────────────
            if (campaignGauges.isNotEmpty()) {
                LoreSection("Jauges",Icons.Default.Tune, accent=GOLD,
                    expandedOverride=isExpanded("Jauges"), onExpandChange={setExpanded("Jauges",it)},
                    summary="${campaignGauges.size} stat${if(campaignGauges.size>1)"s" else ""} de campagne"){
                    val readMode = LocalReadMode.current
                    campaignGauges.forEach { g ->
                        val col = LABEL_COLORS[g.colorIndex % LABEL_COLORS.size]
                        val raw = e.gaugeValues[g.id] ?: ""
                        val intensity = if (g.numeric) (raw.toFloatOrNull() ?: 0f)/100f
                                        else gaugeLevelIntensity(raw)
                        Column(Modifier.fillMaxWidth().padding(horizontal=20.dp, vertical=8.dp)){
                            Row(verticalAlignment=Alignment.CenterVertically){
                                Box(Modifier.size(9.dp).clip(CircleShape).background(col))
                                Spacer(Modifier.width(7.dp))
                                Text(g.name.uppercase(), style=Typo.labelSmall.copy(color=col.copy(alpha=0.85f)))
                                Spacer(Modifier.weight(1f))
                                Text(if(raw.isBlank()) "—" else raw + if(g.numeric) "/100" else "",
                                    style=Typo.labelMedium.copy(color=T2))
                            }
                            Spacer(Modifier.height(6.dp))
                            // gauge bar (animated width for smooth feedback)
                            val animFill by animateFloatAsState(intensity.coerceIn(0f,1f),
                                spring(dampingRatio=0.7f, stiffness=180f), label="gaugeFill")
                            Box(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(999.dp)).background(L4)){
                                Box(Modifier.fillMaxHeight().fillMaxWidth(animFill)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Brush.horizontalGradient(listOf(col.copy(alpha=0.7f),col))))
                            }
                            // editor (hidden in read mode)
                            if (!readMode) {
                                Spacer(Modifier.height(8.dp))
                                if (g.numeric) {
                                    Slider(value=(raw.toFloatOrNull() ?: 0f),
                                        onValueChange={ v -> e = e.copy(gaugeValues = e.gaugeValues.toMutableMap().apply{
                                            put(g.id, v.toInt().toString()) }) },
                                        valueRange=0f..100f,
                                        colors=SliderDefaults.colors(thumbColor=col, activeTrackColor=col,
                                            inactiveTrackColor=L5))
                                } else {
                                    Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                                        GAUGE_LEVELS.forEach { lvl ->
                                            val sel = raw.equals(lvl, true)
                                            Box(Modifier.clip(RoundedCornerShape(999.dp))
                                                .background(if(sel) col.copy(alpha=0.22f) else L4)
                                                .border(1.dp, if(sel) col else L5, RoundedCornerShape(999.dp))
                                                .clickable{ e = e.copy(gaugeValues = e.gaugeValues.toMutableMap().apply{
                                                    if(sel) remove(g.id) else put(g.id, lvl) }) }
                                                .padding(horizontal=10.dp, vertical=5.dp)){
                                                Text(lvl, style=Typo.labelSmall.copy(color=if(sel) col else T3))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            LoreSection("ADN du personnage",Icons.Default.Fingerprint,
                expandedOverride=isExpanded("ADN du personnage"), onExpandChange={setExpanded("ADN du personnage",it)},
                summary=dnaSummary(e.dna, DNA_KEYS.filter{it!="Gender"} + "Lives")){
                @Composable
                fun field(key: String, multi: Boolean = false) {
                    LoreField(FIELD_LABELS[key] ?: key, e.dna[key] ?: "",
                        "—", multi=multi){ v ->
                        e = e.copy(dna = e.dna.toMutableMap().apply {
                            if (v.isBlank()) remove(key) else put(key, v) })
                    }
                }
                field("Lives")
                DNA_KEYS.filter { it != "Gender" }.forEach { field(it, multi = it in listOf("Looks","Style/Persona","Voice/Humor","Texts")) }
            }
            // ── INERTIA — relation lens fields ─────────────────────────────
            LoreSection("Inertie · Relation",Icons.Default.Sync, accent=TEAL,
                expandedOverride=isExpanded("Inertie · Relation"), onExpandChange={setExpanded("Inertie · Relation",it)},
                summary=dnaSummary(e.dna, INERTIA_KEYS + "Facets")){
                @Composable
                fun ifield(key: String, multi: Boolean = false) {
                    LoreField(FIELD_LABELS[key] ?: key, e.dna[key] ?: "",
                        "—", multi=multi, accent=TEAL){ v ->
                        e = e.copy(dna = e.dna.toMutableMap().apply {
                            if (v.isBlank()) remove(key) else put(key, v) })
                    }
                }
                INERTIA_KEYS.forEach { ifield(it, multi = it == "Anchors") }
                // Visual facet gauges (read-only) above the editable text
                val facetGauges = parseFacets(e.dna["Facets"] ?: "")
                if (facetGauges.isNotEmpty()) {
                    Column(Modifier.fillMaxWidth().padding(top=6.dp,bottom=4.dp),
                        verticalArrangement=Arrangement.spacedBy(5.dp)){
                        facetGauges.forEach { (label, value, intensity) ->
                            val col = facetColor(label)
                            Row(verticalAlignment=Alignment.CenterVertically){
                                Text(label,style=Typo.labelMedium.copy(color=T3),modifier=Modifier.width(92.dp))
                                Box(Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(999.dp)).background(L4)){
                                    Box(Modifier.fillMaxHeight().fillMaxWidth(intensity.coerceIn(0.04f,1f))
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Brush.horizontalGradient(listOf(col.copy(alpha=0.7f),col))))
                                }
                                Text(value,style=Typo.labelSmall.copy(color=T2),
                                    modifier=Modifier.width(70.dp).padding(start=8.dp))
                            }
                        }
                    }
                }
                ifield("Facets", multi = true)
            }
            // ── Free-form sections — always visible, fully manual (add/rename/delete) ──
            val readModeSec = LocalReadMode.current
            LoreSection("Sections",Icons.Default.Article, accent=GOLD_MID,
                expandedOverride=isExpanded("Sections"), onExpandChange={setExpanded("Sections",it)},
                summary=if(e.sections.isEmpty()) "Ajoute tes propres champs"
                        else "${e.sections.size} section${if(e.sections.size>1)"s" else ""}"){
                val secList = e.sections.entries.toList()
                fun moveSection(from: Int, to: Int) {
                    if (to < 0 || to >= secList.size) return
                    val keys = secList.map { it.key }.toMutableList()
                    val k = keys.removeAt(from); keys.add(to, k)
                    val rebuilt = LinkedHashMap<String,String>()
                    keys.forEach { key -> rebuilt[key] = e.sections[key] ?: "" }
                    e = e.copy(sections = rebuilt)
                }
                secList.forEachIndexed { idx, entry ->
                    EditableSection(
                        title = entry.key, content = entry.value, readMode = readModeSec,
                        canMoveUp = idx > 0, canMoveDown = idx < secList.size - 1,
                        onMoveUp = { moveSection(idx, idx - 1) },
                        onMoveDown = { moveSection(idx, idx + 1) },
                        onContentChange = { v ->
                            e = e.copy(sections = LinkedHashMap(e.sections).apply { put(entry.key, v) })
                        },
                        onTitleChange = { newTitle ->
                            if (newTitle.isNotBlank() && newTitle != entry.key) {
                                // rebuild preserving order, rename this key
                                val rebuilt = LinkedHashMap<String,String>()
                                e.sections.forEach { (k,v) -> rebuilt[if(k==entry.key) newTitle else k] = v }
                                e = e.copy(sections = rebuilt)
                            }
                        },
                        onDelete = {
                            e = e.copy(sections = LinkedHashMap(e.sections).apply { remove(entry.key) })
                        }
                    )
                }
                if (!readModeSec) {
                    var newTitle by remember { mutableStateOf("") }
                    Row(Modifier.fillMaxWidth().padding(horizontal=20.dp, vertical=10.dp),
                        verticalAlignment=Alignment.CenterVertically,
                        horizontalArrangement=Arrangement.spacedBy(8.dp)){
                        OutlinedTextField(value=newTitle, onValueChange={newTitle=it}, singleLine=true,
                            modifier=Modifier.weight(1f),
                            placeholder={Text("Titre d'une nouvelle section…", color=T4,
                                style=Typo.bodySmall.copy(fontStyle=FontStyle.Italic))},
                            textStyle=Typo.bodyMedium.copy(color=T1),
                            colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=GOLD_MID,
                                unfocusedBorderColor=L6, focusedContainerColor=L4, unfocusedContainerColor=L4,
                                cursorColor=GOLD_MID))
                        Box(Modifier.clip(RoundedCornerShape(10.dp))
                            .background(if(newTitle.isBlank()) L4 else GOLD_MID.copy(alpha=0.22f))
                            .border(1.dp, if(newTitle.isBlank()) L5 else GOLD_MID, RoundedCornerShape(10.dp))
                            .clickable(enabled=newTitle.isNotBlank()){
                                if(newTitle.isNotBlank() && !e.sections.containsKey(newTitle.trim())){
                                    e = e.copy(sections = LinkedHashMap(e.sections).apply { put(newTitle.trim(), "") })
                                    newTitle = ""
                                }
                            }.padding(horizontal=14.dp, vertical=12.dp)){
                            Icon(Icons.Default.Add, "Ajouter la section", Modifier.size(18.dp),
                                tint=if(newTitle.isBlank()) T4 else GOLD_MID)
                        }
                    }
                }
            }
            LoreSection("Contexte",Icons.Default.Groups,
                summary=if(e.relationships.isNotBlank())e.relationships.take(60) else "Aucune relation renseignée"){
                LoreField("Relations",e.relationships,"Avec les PJ, autres NPCs, tensions…",multi=true){e=e.copy(relationships=it)}
                LoreField("Historique des scènes",e.sceneHistory,"Dernières scènes, dettes, promesses…",multi=true){e=e.copy(sceneHistory=it)}
            }
            // Reverse links — locations referencing this NPC
            val appearsIn = campaignLocations.filter { npc.id in it.linkedNpcIds }
            if (appearsIn.isNotEmpty()) {
                LoreSection("Apparaît dans",Icons.Default.Place,accent=TEAL,
                    summary=appearsIn.joinToString{it.name}){
                    Column(Modifier.padding(horizontal=20.dp,vertical=6.dp),
                        verticalArrangement=Arrangement.spacedBy(6.dp)){
                        appearsIn.forEach { loc ->
                            LinkRow(loc.name, loc.type, TEAL) { onOpenLoc(loc) }
                        }
                    }
                }
            }
            LoreSection("Secrets",Icons.Default.Lock,accent=CRIM,
                defaultExpanded=e.secrets.isNotBlank(),
                summary="${if(e.secrets.isNotBlank())"1 secret · " else "Aucun secret · "}toucher pour ${if(e.secrets.isNotBlank())"révéler" else "ajouter"}"){
                Box(Modifier.fillMaxWidth().padding(horizontal=18.dp,vertical=6.dp)
                    .clip(RoundedCornerShape(10.dp)).background(CRIM_LO)
                    .border(1.dp,CRIM.copy(alpha=0.3f),RoundedCornerShape(10.dp))){
                    LoreField("Secrets connus / inconnus",e.secrets,
                        "Ce que le joueur sait · ce que le NPC cache…",multi=true,accent=CRIM){e=e.copy(secrets=it)}
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        }
        if (!readMode) SaveBar("Sauver le personnage", accent=LocalAccent.current, enabled = e != npc){onSave(e.copy(photoUris = npc.photoUris))}
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LOCATION DETAIL
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun LocScreen(
    loc: Location, campaignLabels: List<CampaignLabel>,
    campaignNpcs: List<Npc> = emptyList(),
    onOpenNpc: (Npc)->Unit = {},
    onBack:()->Unit, onSave:(Location)->Unit,
    onAddPhoto:(Uri)->Unit, onDelPhoto:(String)->Unit
) {
    val ctx = LocalContext.current
    var e by remember(loc.id){mutableStateOf(loc)}
    val photoL = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){ uri ->
        uri?.let {
            runCatching { ctx.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            onAddPhoto(it)
        }
    }

    fun export() = buildString {
        appendLine("LIEU: ${e.name}")
        if(e.type.isNotBlank())            appendLine("Type: ${e.type}")
        if(e.description.isNotBlank())     appendLine("Description: ${e.description}")
        if(e.atmosphere.isNotBlank())      appendLine("Atmosphère: ${e.atmosphere}")
        if(e.notableFeatures.isNotBlank()) appendLine("Éléments: ${e.notableFeatures}")
        if(e.linkedNpcs.isNotBlank())      appendLine("NPCs liés: ${e.linkedNpcs}")
        if(e.tags.isNotBlank())            appendLine("Tags: ${e.tags}")
        if(e.labelIds.isNotEmpty())        appendLine("Labels: ${campaignLabels.filter{it.id in e.labelIds}.joinToString{it.name}}")
        if(e.secrets.isNotBlank())         appendLine("Secrets: ${e.secrets}")
    }.trim()

    Column(Modifier.fillMaxSize().imePadding()) {
        var heroViewer by remember { mutableStateOf(false) }
        if (heroViewer) PhotoZoomViewer(loc.photoUris, 0, onClose={heroViewer=false})
        EntityHero(name=e.name.ifBlank{"Lieu"},subtitle=e.type,
            firstPhoto=loc.photoUris.firstOrNull(),accent=TEAL,onBack=onBack,
            onTapPhoto=if(loc.photoUris.isNotEmpty()){{heroViewer=true}}else null,
            action={CopyButton(accent=TEAL){
                val clip=ClipData.newPlainText("Lieu",export())
                (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
            }})

        val (riseA, riseY) = rememberRiseIn()
        Column(Modifier.weight(1f).graphicsLayer{ alpha = riseA; translationY = riseY }
            .verticalScroll(rememberScrollState())) {
            PhotoStrip(loc.photoUris,{photoL.launch(arrayOf("image/*"))},onDelPhoto)
            LoreSection("Identité",Icons.Default.Place,accent=TEAL,
                summary=e.name.ifBlank{"—"}+if(e.type.isNotBlank())" · ${e.type}"else""){
                LoreField("Nom",e.name,"Nom du lieu",accent=TEAL){e=e.copy(name=it)}
                LoreField("Type",e.type,"Taverne / Donjon / Cité / Forêt…",accent=TEAL){e=e.copy(type=it)}
                LoreField("Tags libres",e.tags,"ex. lieu clé, dangereux, point de départ",accent=TEAL){e=e.copy(tags=it)}
                if(campaignLabels.isNotEmpty()){
                    Spacer(Modifier.height(4.dp))
                    LabelPicker(campaignLabels,e.labelIds){id->
                        e=e.copy(labelIds=if(id in e.labelIds)e.labelIds-id else e.labelIds+id)}
                }
            }

            // ── Structured location-profile fields (from the building template) ──
            @Composable
            fun lfield(key: String, multi: Boolean = true) {
                LoreField(LOC_LABELS[key] ?: key, e.loc[key] ?: "", "—", multi=multi, accent=TEAL){ v ->
                    e = e.copy(loc = e.loc.toMutableMap().apply {
                        if (v.isBlank()) remove(key) else put(key, v) })
                }
            }
            val hasProfile = e.loc.isNotEmpty()
            if (hasProfile || e.loc.keys.any { it in LOC_KEYS }) {
                LoreSection("Architecture",Icons.Default.Landscape,accent=TEAL,
                    summary=e.loc["Style"]?.take(60) ?: "Façade, entrée, approche"){
                    lfield("Access", multi=false)
                    lfield("Style")
                    lfield("Facade")
                    lfield("Entrance")
                }
                LoreSection("Espaces & circulation",Icons.Default.Groups,accent=TEAL,
                    summary=e.loc["Zone1"]?.take(60) ?: "Zones et flux internes"){
                    lfield("Zone1")
                    lfield("Zone2")
                    lfield("Zone3")
                    lfield("Flow")
                }
                LoreSection("Ambiance sensorielle",Icons.Default.Fingerprint,accent=TEAL,
                    summary=e.loc["Vibe"]?.take(60) ?: "Lumière, sons, odeurs"){
                    lfield("Lighting")
                    lfield("Acoustics")
                    lfield("SmellTemp")
                    lfield("Vibe")
                }
                LoreSection("Mécaniques",Icons.Default.Lock,accent=TEAL,
                    summary=e.loc["Security"]?.take(60) ?: "Sécurité, services"){
                    lfield("Security")
                    lfield("Services")
                }
            }

            LoreSection("Description",Icons.Default.Landscape,accent=TEAL,
                summary=e.description.take(80).ifBlank{"Non renseignée"}){
                LoreField("Description générale",e.description,"Ce qu'on voit en arrivant…",multi=true,accent=TEAL){e=e.copy(description=it)}
                LoreField("Atmosphère · Ambiance",e.atmosphere,"Sons, odeurs, lumière, tension…",multi=true,accent=TEAL){e=e.copy(atmosphere=it)}
                LoreField("Éléments notables",e.notableFeatures,"Objets, zones, PNJs présents…",multi=true,accent=TEAL){e=e.copy(notableFeatures=it)}
            }
            LoreSection("Connexions",Icons.Default.Link,accent=TEAL,
                summary=campaignNpcs.filter{it.id in e.linkedNpcIds}.joinToString{it.name}.ifBlank{"Aucun NPC lié"}){
                // Structured links — tap chip to toggle
                if (campaignNpcs.isNotEmpty()) {
                    Column(Modifier.padding(horizontal=20.dp,vertical=4.dp)) {
                        Text("NPCS LIÉS — TOUCHER POUR LIER",style=Typo.labelSmall.copy(color=TEAL.copy(alpha=0.6f)))
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement=Arrangement.spacedBy(8.dp)){
                            campaignNpcs.forEach { npc ->
                                val sel = npc.id in e.linkedNpcIds
                                Row(Modifier.clip(RoundedCornerShape(999.dp))
                                    .background(if(sel)TEAL.copy(alpha=0.18f) else L4)
                                    .border(1.dp,if(sel)TEAL.copy(alpha=0.55f) else L5,RoundedCornerShape(999.dp))
                                    .clickable(onClickLabel=if(sel)"Délier ${npc.name}" else "Lier ${npc.name}"){
                                        e=e.copy(linkedNpcIds=if(sel)e.linkedNpcIds-npc.id else e.linkedNpcIds+npc.id)
                                    }
                                    .padding(horizontal=10.dp,vertical=5.dp),
                                    verticalAlignment=Alignment.CenterVertically,
                                    horizontalArrangement=Arrangement.spacedBy(5.dp)){
                                    if(sel) Icon(Icons.Default.Check,null,Modifier.size(11.dp),tint=TEAL)
                                    Text(npc.name,style=Typo.labelMedium.copy(color=if(sel)TEAL else T3))
                                }
                            }
                        }
                    }
                    // Quick-open linked fiches
                    val linked = campaignNpcs.filter{it.id in e.linkedNpcIds}
                    if (linked.isNotEmpty()) {
                        Column(Modifier.padding(horizontal=20.dp,vertical=8.dp),
                            verticalArrangement=Arrangement.spacedBy(6.dp)){
                            Text("OUVRIR LA FICHE",style=Typo.labelSmall.copy(color=TEAL.copy(alpha=0.6f)))
                            linked.forEach { npc -> LinkRow(npc.name, npc.role, GOLD) { onOpenNpc(npc) } }
                        }
                    }
                }
                LoreField("Notes libres",e.linkedNpcs,"ex. le garde Brom y passe chaque soir…",multi=true,accent=TEAL){e=e.copy(linkedNpcs=it)}
            }
            LoreSection("Secrets",Icons.Default.Lock,accent=CRIM,
                defaultExpanded=e.secrets.isNotBlank(),
                summary="${if(e.secrets.isNotBlank())"1 secret · " else "Aucun secret · "}toucher pour ${if(e.secrets.isNotBlank())"révéler" else "ajouter"}"){
                Box(Modifier.fillMaxWidth().padding(horizontal=18.dp,vertical=6.dp)
                    .clip(RoundedCornerShape(10.dp)).background(CRIM_LO)
                    .border(1.dp,CRIM.copy(alpha=0.3f),RoundedCornerShape(10.dp))){
                    LoreField("Secrets de ce lieu",e.secrets,
                        "Passage secret, malédiction cachée, vrai propriétaire…",multi=true,accent=CRIM){e=e.copy(secrets=it)}
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        SaveBar("Sauver le lieu",accent=TEAL, enabled = e != loc){onSave(e.copy(photoUris = loc.photoUris))}
    }
}

private fun Modifier.tabIndicatorOffset(pos: TabPosition) =
    fillMaxWidth().wrapContentSize(Alignment.BottomStart).offset(x=pos.left).width(pos.width)


// ─────────────────────────────────────────────────────────────────────────────
// SEARCH
// ─────────────────────────────────────────────────────────────────────────────

enum class HitKind { NPC, LOCATION }
enum class SearchScope { ALL, NPC, LOCATION }

data class SearchHit(
    val kind: HitKind, val campaignId: String, val campaignName: String,
    val entityId: String, val name: String, val sub: String, val snippet: String
) { val key get() = "$kind-$entityId" }

fun buildSearchHits(campaigns: List<Campaign>, rawQuery: String): List<SearchHit> {
    val q = rawQuery.trim()
    if (q.length < 2) return emptyList()
    fun String.hit() = contains(q, ignoreCase = true)
    fun snippetOf(vararg fields: String): String =
        fields.firstOrNull { it.hit() }?.let { f ->
            val i = f.indexOf(q, ignoreCase = true).coerceAtLeast(0)
            val start = (i - 24).coerceAtLeast(0)
            (if (start > 0) "…" else "") + f.substring(start, (i + q.length + 36).coerceAtMost(f.length))
        } ?: ""
    val hits = mutableListOf<SearchHit>()
    for (c in campaigns) {
        // Match on NAME only — predictable, no content noise
        for (n in c.npcs) if (n.name.hit())
            hits += SearchHit(HitKind.NPC, c.id, c.name, n.id, n.name, n.role, "")
        for (l in c.locations) if (l.name.hit())
            hits += SearchHit(HitKind.LOCATION, c.id, c.name, l.id, l.name, l.type, "")
    }
    // Names starting with the query rank first
    return hits.sortedBy { if (it.name.startsWith(q, ignoreCase=true)) 0 else 1 }.take(40)
}

@Composable
fun SearchField(query: String, onChange: (String)->Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = query, onValueChange = onChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text("Rechercher un personnage, un lieu…", color = T4,
            style = Typo.bodySmall.copy(fontStyle = FontStyle.Italic)) },
        leadingIcon = { Icon(Icons.Default.Search, null, Modifier.size(16.dp), tint = GOLD_MID) },
        trailingIcon = {
            if (query.isNotBlank()) Box(Modifier.padding(end=4.dp).size(26.dp).clip(CircleShape)
                .background(L5).clickable{ onChange("") }, contentAlignment=Alignment.Center){
                Icon(Icons.Default.Close, "Effacer la recherche", Modifier.size(13.dp), tint = T2)
            }
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GOLD.copy(alpha=0.7f), unfocusedBorderColor = L5.copy(alpha=0.6f),
            focusedContainerColor = L3, unfocusedContainerColor = L2.copy(alpha=0.6f), cursorColor = GOLD),
        textStyle = Typo.bodyMedium.copy(color = T1)
    )
}

@Composable
fun SearchResultRow(hit: SearchHit, onClick: ()->Unit) {
    val (icon, accent) = when (hit.kind) {
        HitKind.NPC      -> Icons.Default.Person to GOLD
        HitKind.LOCATION -> Icons.Default.Place to TEAL
    }
    Row(Modifier.fillMaxWidth().padding(horizontal=14.dp, vertical=3.dp)
        .clip(RoundedCornerShape(12.dp)).background(L2)
        .border(1.dp, L5, RoundedCornerShape(12.dp))
        .pressable(onClickLabel="Ouvrir"){onClick()}
        .padding(horizontal=14.dp, vertical=12.dp),
        verticalAlignment=Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(16.dp), tint=accent)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                Text(hit.name, style=Typo.titleMedium.copy(color=T1), maxLines=1, overflow=TextOverflow.Ellipsis)
                if (hit.sub.isNotBlank()) Text("· ${hit.sub}", style=Typo.labelSmall.copy(color=accent), maxLines=1)
            }
            if (hit.snippet.isNotBlank())
                Text(hit.snippet, style=Typo.bodySmall.copy(color=T2), maxLines=2, overflow=TextOverflow.Ellipsis)
            Text(hit.campaignName, style=Typo.labelSmall.copy(color=T4))
        }
        Icon(Icons.Default.ChevronRight, null, Modifier.size(16.dp), tint=T4)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LINK ROW — navigable reference between fiches
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun LinkRow(name: String, sub: String, accent: Color, onClick: ()->Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
        .background(accent.copy(alpha=0.06f))
        .border(1.dp, accent.copy(alpha=0.2f), RoundedCornerShape(10.dp))
        .pressable(onClickLabel="Ouvrir $name"){onClick()}
        .padding(horizontal=12.dp, vertical=10.dp),
        verticalAlignment=Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(name, style=Typo.titleMedium.copy(color=T1))
            if (sub.isNotBlank()) Text(sub, style=Typo.labelSmall.copy(color=accent))
        }
        Icon(Icons.Default.ArrowForward, null, Modifier.size(14.dp), tint=accent)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// JOURNAL
// ─────────────────────────────────────────────────────────────────────────────

private val journalDateFmt = SimpleDateFormat("d MMMM yyyy", Locale.FRENCH)

@Composable
fun JournalTab(entries: List<JournalEntry>, onEdit:(JournalEntry)->Unit, onDel:(JournalEntry)->Unit, bottomPad: Dp) {
    var delTarget by remember { mutableStateOf<JournalEntry?>(null) }
    delTarget?.let { j -> ConfirmDelete("Supprimer ?","Cette entrée du journal sera supprimée.",
        {onDel(j);delTarget=null},{delTarget=null}) }

    if (entries.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment=Alignment.Center) {
            Column(horizontalAlignment=Alignment.CenterHorizontally, verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Ornament(GOLD_MID)
                Icon(Icons.Outlined.HistoryEdu, null, Modifier.size(44.dp), tint=T4)
                Text("Journal vide", style=Typo.headlineSmall.copy(color=T3))
                Text("Consigne le résumé de tes sessions Airealm", style=Typo.bodySmall.copy(color=T4))
                Ornament(GOLD_MID)
            }
        }
        return
    }
    LazyColumn(Modifier.fillMaxSize(),
        contentPadding=PaddingValues(top=10.dp, bottom=bottomPad+104.dp),
        verticalArrangement=Arrangement.spacedBy(8.dp)) {
        items(entries.sortedByDescending{it.dateMillis}, key={it.id}) { j ->
            JournalCard(j, onEdit={onEdit(j)}, onDelete={delTarget=j},
                modifier=Modifier.animateItem())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalSheet(existing: JournalEntry?, onDismiss:()->Unit, onSave:(JournalEntry)->Unit) {
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var text  by remember { mutableStateOf(existing?.text ?: "") }
    ModalBottomSheet(onDismissRequest=onDismiss,
        sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true),
        containerColor=L3,
        dragHandle={Box(Modifier.padding(vertical=10.dp).width(32.dp).height(3.dp)
            .clip(RoundedCornerShape(2.dp)).background(L6))}) {
        Column(Modifier.fillMaxWidth().padding(horizontal=22.dp).navigationBarsPadding().imePadding()
            .verticalScroll(rememberScrollState())) {
            Text(if(existing==null)"Nouvelle entrée" else "Modifier l'entrée",
                style=Typo.headlineMedium.copy(color=T1))
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value=title, onValueChange={title=it},
                placeholder={Text("Titre — ex. Session 12 : la trahison",color=T4,
                    style=Typo.bodyMedium.copy(fontStyle=FontStyle.Italic))},
                modifier=Modifier.fillMaxWidth(), singleLine=true, shape=RoundedCornerShape(12.dp),
                colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=GOLD,unfocusedBorderColor=L6,
                    focusedContainerColor=L4,unfocusedContainerColor=L4,cursorColor=GOLD),
                textStyle=Typo.bodyLarge.copy(color=T1))
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value=text, onValueChange={text=it},
                placeholder={Text("Ce qui s'est passé, décisions, conséquences…",color=T4,
                    style=Typo.bodyMedium.copy(fontStyle=FontStyle.Italic))},
                modifier=Modifier.fillMaxWidth(), minLines=6, shape=RoundedCornerShape(12.dp),
                colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=GOLD,unfocusedBorderColor=L6,
                    focusedContainerColor=L4,unfocusedContainerColor=L4,cursorColor=GOLD),
                textStyle=Typo.bodyLarge.copy(color=T1))
            Spacer(Modifier.height(14.dp))
            Button(onClick={
                onSave(existing?.copy(title=title.trim(),text=text.trim())
                    ?: JournalEntry(title=title.trim(),text=text.trim()))
                onDismiss()
            }, enabled=title.isNotBlank()||text.isNotBlank(),
                modifier=Modifier.fillMaxWidth().height(50.dp), shape=RoundedCornerShape(12.dp),
                colors=ButtonDefaults.buttonColors(containerColor=GOLD,contentColor=L0,
                    disabledContainerColor=L4,disabledContentColor=T2.copy(alpha=0.7f))) {
                Text(if(existing==null)"Ajouter au journal" else "Enregistrer",
                    style=Typo.labelLarge.copy(fontFamily=CinzelFamily,fontSize=14.sp))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// FULL CAMPAIGN EXPORT — complete context for Airealm
// ─────────────────────────────────────────────────────────────────────────────

fun campaignFullExport(c: Campaign): String = buildString {
    appendLine("CAMPAGNE: ${c.name}")
    if (c.description.isNotBlank()) appendLine(c.description)
    if (c.npcs.isNotEmpty()) {
        appendLine(); appendLine("── PERSONNAGES ──")
        c.npcs.forEach { n ->
            appendLine("• ${n.name}${if(n.role.isNotBlank())" — ${n.role}" else ""}")
            if (n.shortCard.isNotBlank()) appendLine("  ${n.shortCard}")
            if (n.relationships.isNotBlank()) appendLine("  Relations: ${n.relationships}")
            if (n.secrets.isNotBlank()) appendLine("  Secrets: ${n.secrets}")
        }
    }
    if (c.locations.isNotEmpty()) {
        appendLine(); appendLine("── LIEUX ──")
        c.locations.forEach { l ->
            appendLine("• ${l.name}${if(l.type.isNotBlank())" (${l.type})" else ""}")
            if (l.description.isNotBlank()) appendLine("  ${l.description}")
            val linked = c.npcs.filter { it.id in l.linkedNpcIds }
            if (linked.isNotEmpty()) appendLine("  NPCs présents: ${linked.joinToString{it.name}}")
        }
    }
    if (c.journal.isNotEmpty()) {
        appendLine(); appendLine("── JOURNAL ──")
        c.journal.sortedBy{it.dateMillis}.forEach { j ->
            appendLine("[${journalDateFmt.format(Date(j.dateMillis))}] ${j.title}")
            if (j.text.isNotBlank()) appendLine(j.text)
        }
    }
}.trim()


// ─────────────────────────────────────────────────────────────────────────────
// AIREALM CARD IMPORT — parses the standard NPC Card format into a fiche
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Parses an Airealm NPC card pasted as text. Tolerant line-based parser:
 *  - Header  : | 18yo | Freshman | Major: … | Traits: … | Lives in …
 *  - DNA     : Looks: / Style/Persona: / Voice/Humor: / Lifestyle/Vices: /
 *              Insecurity/Drive: / Priorities: / Boundary: / Clique: / Texts:
 *  - INERTIA : (vs X)  Lens: / Vibe: / Anchors:
 *  - FACETS  : emoji stat line(s)
 * Anything unrecognised is preserved in the full card so no data is lost.
 */
/**
 * Parses an Airealm "LOCATION PROFILE" building template into a structured Location.
 * Maps each template bullet to a LOC_KEYS field.
 */
/** Heuristic: does this pasted text look like a LOCATION profile rather than an NPC card? */
fun looksLikeLocation(raw: String): Boolean {
    val t = raw.lowercase()
    val locHits = listOf("location profile","architectural style","zone/district","access level",
        "spatial layout","chokepoint","sensory","soundscape","internal zones","facade","approach & entrance")
        .count { t.contains(it) }
    val npcHits = listOf("dna","inertia","facets","attraction:","intimacy:","major:","freshman","clique","persona")
        .count { t.contains(it) }
    return locHits > npcHits
}

fun parseLocationCard(name: String, raw: String): Location {
    // Keep ORIGINAL lines (we need indentation to detect sub-levels), drop only empties.
    val rawLines = raw.lines().filter { it.isNotBlank() }

    // Field label patterns — tolerant: any of these substrings opens that field.
    // Order matters only for display; matching scans all keys per line.
    val patterns: List<Pair<String, List<String>>> = listOf(
        "Style"    to listOf("architectural style", "style:"),
        "Facade"   to listOf("facade", "distinguishing feature", "distinguishing"),
        "Entrance" to listOf("approach & entrance", "the approach", "approach", "entrance"),
        "Zone1"    to listOf("zone 1", "zone1"),
        "Zone2"    to listOf("zone 2", "zone2"),
        "Zone3"    to listOf("zone 3", "zone3"),
        "Flow"     to listOf("flow & chokepoint", "flow", "chokepoint"),
        "Lighting" to listOf("lighting"),
        "Acoustics" to listOf("acoustics", "soundscape"),
        "SmellTemp" to listOf("smell & temperature", "smell", "temperature"),
        "Vibe"     to listOf("dynamic vibe", "vibe"),
        "Security" to listOf("security & enforcement", "security", "enforcement"),
        "Services" to listOf("available services", "services", "loot"),
        "Secrets"  to listOf("environmental storytelling", "hidden lore", "secret")
    )
    // Section headers to ignore (the "🗺️ 2. SPATIAL LAYOUT" style lines)
    fun isSectionHeader(l: String): Boolean {
        val c = l.trimStart { !it.isLetterOrDigit() }.trim()
        return Regex("^[0-9]+\\.\\s").containsMatchIn(c) ||
               c.uppercase() == c && c.length > 6 && !c.contains(":")
    }
    // Strip a leading bullet/emoji/whitespace for label detection
    fun clean(l: String) = l.trimStart { it == '•' || it == '·' || it == '-' || it == '*' || it == ' ' || it == '\t' }

    // Detect which field (if any) a line opens; returns key + the text after the label colon.
    fun detect(l: String): Pair<String,String>? {
        val c = clean(l).lowercase()
        for ((key, pats) in patterns) {
            for (pat in pats) {
                val idx = c.indexOf(pat)
                // label must be near the start (tolerate small prefixes) and be followed by ':'
                if (idx in 0..4) {
                    val cl = clean(l)
                    val colon = cl.indexOf(':')
                    if (colon >= 0) return key to cl.substring(colon + 1).trim()
                    return key to ""
                }
            }
        }
        return null
    }

    val locMap = linkedMapOf<String, StringBuilder>()

    // ── Header line: "Type: … | Zone/District: … | Access Level: …" ──────────
    val header = rawLines.firstOrNull { it.contains("Type:", true) && it.contains("|") } ?: ""
    val hParts = header.split("|").map { it.trim() }.filter { it.isNotBlank() }
    fun hseg(vararg ks: String) = hParts.firstOrNull { p -> ks.any { p.startsWith(it, true) } }
        ?.substringAfter(":")?.trim() ?: ""
    val type = hseg("Type")
    val zone = hseg("Zone", "District")
    val access = hseg("Access")
    if (zone.isNotBlank() || access.isNotBlank())
        locMap["Access"] = StringBuilder(listOf(zone, access).filter { it.isNotBlank() }.joinToString(" · "))

    // ── Accumulate: each field grabs everything until the next field opens ───
    var current: String? = null
    for (l in rawLines) {
        if (l === header) { current = null; continue }
        val hit = detect(l)
        if (hit != null) {
            val (key, firstText) = hit
            current = key
            val sb = locMap.getOrPut(key) { StringBuilder() }
            if (firstText.isNotBlank()) { if (sb.isNotBlank()) sb.append("\n"); sb.append(firstText) }
        } else if (isSectionHeader(l)) {
            current = null   // a new big section — stop appending to previous field
        } else if (current != null) {
            // continuation / sub-level line → append (preserve sub-indent as "  • ")
            val txt = l.trim()
            if (txt.isNotBlank()) {
                val sb = locMap.getValue(current)
                if (sb.isNotBlank()) sb.append("\n")
                // keep a light bullet for sub-levels that had leading spaces
                val indented = l.length - l.trimStart(' ', '\t').length >= 2
                sb.append(if (indented) "• $txt" else txt)
            }
        }
    }

    val finalMap = locMap.mapValues { it.value.toString().trim() }.filterValues { it.isNotBlank() }
    return Location(
        campaignId = "",
        name = name.trim(),
        type = type,
        loc = finalMap
    )
}

/**
 * Universal parser — splits ANY card format into ordered [title → content] sections.
 * Recognises: "LABEL: value", "ALL-CAPS HEADER" then content on following lines,
 * and "Titlecase Header" lines that act as section titles. Everything is preserved.
 */
/** Short relation codes → readable gauge names (Kestera-style "L 63 | R 68 | Af 58 | I 43"). */
val REL_CODE_MAP = mapOf(
    "l" to "Love", "r" to "Respect", "af" to "Affinity", "i" to "Intimacy",
    "t" to "Trust", "a" to "Attraction", "c" to "Comfort", "s" to "Suspicion",
    "f" to "Fear", "j" to "Jealousy", "aff" to "Affinity", "tr" to "Trust"
)
/** Detect "Code Number" pairs separated by | or , — returns ordered (gaugeName, 0..100). */
fun parseRelationCodes(raw: String): List<Pair<String,Int>> {
    val out = mutableListOf<Pair<String,Int>>()
    val rx = Regex("^\\s*([A-Za-z]{1,3})\\s*[:=]?\\s*(\\d{1,3})\\s*$")
    for (seg in raw.split('|', ',')) {
        val m = rx.find(seg.trim()) ?: continue
        val code = m.groupValues[1].lowercase()
        val value = m.groupValues[2].toIntOrNull()?.coerceIn(0,100) ?: continue
        val gname = REL_CODE_MAP[code] ?: m.groupValues[1].uppercase()
        out.add(gname to value)
    }
    return out
}

fun parseGenericCard(name: String, raw: String): Npc {
    val rawLines = raw.lines()
    val sections = linkedMapOf<String,String>()
    var currentTitle: String? = null
    val buf = StringBuilder()

    fun flush() {
        if (currentTitle != null) {
            val body = buf.toString().trim()
            if (body.isNotEmpty()) {
                val existing = sections[currentTitle!!]
                // Merge duplicate section titles instead of overwriting (e.g. INVENTORY ×3)
                sections[currentTitle!!] = if (existing.isNullOrBlank()) body else "$existing\n$body"
            }
        }
        buf.setLength(0)
    }

    // Heuristics for "this line is a section header"
    fun isHeader(line: String): Pair<String,String>? {
        val t = line.trim()
        if (t.isEmpty()) return null
        // 1) "Label: value" on one line (value may be empty → header only)
        val colon = t.indexOf(':')
        if (colon in 1..40) {
            val label = t.substring(0, colon).trim()
            val value = t.substring(colon + 1).trim()
            // label must look like a label: short, mostly letters/spaces/&/-/
            if (label.length <= 40 && label.count { it.isLetter() } >= 2 &&
                label.none { it.isDigit() } && !label.contains("  ")) {
                return label to value
            }
        }
        // 2) ALL-CAPS header line (no colon), e.g. "APPEARANCE & AGE", "BACKGROUND"
        val letters = t.filter { it.isLetter() }
        if (t.length in 2..48 && letters.length >= 2 &&
            letters == letters.uppercase() && t == t.uppercase() &&
            !t.endsWith(".") && t.count { it == ' ' } <= 6) {
            return t to ""
        }
        // 3) Single-word Titlecase header, e.g. "Name", "Relationships", "Nature"
        if (t.length in 2..24 && !t.contains(' ') && t.all { it.isLetter() } &&
            t[0].isUpperCase()) {
            return t to ""
        }
        return null
    }

    for (line in rawLines) {
        val header = isHeader(line)
        if (header != null) {
            flush()
            currentTitle = header.first
            if (header.second.isNotEmpty()) buf.append(header.second)
        } else {
            if (currentTitle == null) {
                // preamble before any header → "Notes"
                if (line.isNotBlank()) { currentTitle = "Notes"; buf.append(line.trim()) }
            } else {
                if (buf.isNotEmpty()) buf.append("\n")
                buf.append(line.trim())
            }
        }
    }
    flush()

    // Auto-detect numeric relation codes (e.g. "Relationships" section: "L 63 | R 68 | Af 58 | I 43")
    val autoGauges = linkedMapOf<String,String>()
    sections.values.forEach { body ->
        parseRelationCodes(body).forEach { (gname, value) ->
            autoGauges["auto:$gname"] = value.toString()
        }
    }

    return Npc(campaignId = "", name = name.trim(), sections = sections, gaugeValues = autoGauges)
}

fun parseAirealmCard(name: String, raw: String): Npc {
    val lines = raw.lines().map { it.trim() }.filter { it.isNotBlank() }
    fun grab(key: String): String = lines.firstOrNull {
        it.startsWith("$key:", ignoreCase = true) ||
        it.substringAfter(' ', it).startsWith("$key:", ignoreCase = true)  // tolerate emoji prefix
    }?.substringAfter(":")?.trim() ?: ""
    // Tries every alias for a canonical field, returns the first non-blank match.
    fun grabAliased(canonical: String): String {
        val aliases = FIELD_ALIASES[canonical] ?: listOf(canonical)
        for (a in aliases) { val v = grab(a); if (v.isNotBlank()) return v }
        return ""
    }

    // ── Header (avec OU sans | initial) : Âge | Sexe (Pronoms) | Année | Filière | Traits: … ─────
    // On prend la 1ère ligne contenant des séparateurs "|" (le header), pas forcément en début de ligne.
    val header = lines.firstOrNull { it.count { c -> c == '|' } >= 2 } ?: ""
    val parts  = header.split("|").map { it.trim() }.filter { it.isNotBlank() }
    // Segment "Major: X" / "Traits: …" → valeur après ':'
    fun seg(prefix: String) = parts.firstOrNull { it.startsWith(prefix, ignoreCase = true) }
        ?.substringAfter(":")?.trim() ?: ""
    val traits = seg("Traits")
    val lives  = parts.firstOrNull { it.startsWith("Lives", ignoreCase = true) } ?: ""
    // Sexe · Pronoms : segment avec pronoms entre parenthèses, ou mot-clé de genre.
    val genderRegex = Regex("\\((she|he|they|him|her|them)[^)]*\\)", RegexOption.IGNORE_CASE)
    val genderKw = Regex("\\b(female|male|non-?binary|trans|genderfluid|agender|woman|man)\\b", RegexOption.IGNORE_CASE)
    val gender = parts.firstOrNull { p ->
        val pl = p.lowercase()
        listOf("Major","Traits","Lives").none { pl.startsWith(it.lowercase()) } &&
        (genderRegex.containsMatchIn(p) || genderKw.containsMatchIn(p))
    } ?: ""
    // Major/Filière : soit "Major: X" explicite, soit le segment positionnel restant
    // (ni âge, ni sexe, ni année, ni traits/lives). On garde les segments "neutres".
    val neutralSegs = parts.filter { p ->
        val pl = p.lowercase()
        listOf("major","traits","lives").none { pl.startsWith(it) } &&
        p != gender && !genderRegex.containsMatchIn(p)
    }
    // Année = segment qui ressemble à un niveau d'étude ; le reste = âge / filière
    val yearWords = listOf("freshman","sophomore","junior","senior","graduate","grad","phd","year")
    val yearSeg = neutralSegs.firstOrNull { s -> yearWords.any { s.lowercase().contains(it) } } ?: ""
    val ageSeg  = neutralSegs.firstOrNull { s -> s.any { it.isDigit() } && s != yearSeg } ?: ""
    // Major explicite sinon dernier segment neutre non utilisé (âge/année)
    val major = seg("Major").ifBlank {
        neutralSegs.filter { it != yearSeg && it != ageSeg }.lastOrNull() ?: ""
    }
    // Rôle = âge · année (les infos de statut), sans la filière
    val role = listOf(ageSeg, yearSeg).filter { it.isNotBlank() }.joinToString(" · ")
        .ifBlank { neutralSegs.filter { it != major }.joinToString(" · ") }

    // ── DNA + INERTIA → structured map, one entry per field ────────────────
    val dnaMap = linkedMapOf<String,String>()
    if (gender.isNotBlank()) dnaMap["Gender"] = gender
    // Lives/Housing handled via alias too (header "Lives in …" OR a "Housing:" DNA line)
    val livesValue = lives.ifBlank { grabAliased("Lives") }
    if (livesValue.isNotBlank()) dnaMap["Lives"] = livesValue
    (DNA_KEYS + INERTIA_KEYS).forEach { k ->
        if (k != "Gender" && k != "Lives")
            grabAliased(k).ifBlank { null }?.let { dnaMap[k] = it }
    }
    // Facets : détection élargie — toute ligne contenant un de ces marqueurs de stat
    val facetMarkers = listOf("Attraction","Intimacy","Affinity","Trust","Comfort","Respect","Suspicion","Jealousy")
    // Facets span TWO lines on Airealm cards — collect every line that holds ≥2 markers
    val facetLines = lines.filter { l -> facetMarkers.count { l.contains(it, ignoreCase = true) } >= 2 }
    if (facetLines.isNotEmpty()) dnaMap["Facets"] = facetLines.joinToString("\n")

    // Relations laissé VIDE à l'import (l'utilisateur le remplit lui-même)
    val relationships = ""

    val tags = listOf(traits, grab("Clique")).filter { it.isNotBlank() }.joinToString(", ")
    return Npc(
        campaignId = "",
        name = name.trim(),
        role = role,
        major = major,
        shortCard = header,
        fullCard = if (dnaMap.isEmpty() && header.isBlank()) raw.trim() else "",
        relationships = relationships,
        tags = tags,
        dna = dnaMap
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportSheet(onDismiss: ()->Unit, onImport: (Npc)->Unit, onImportLoc: ((Location)->Unit)? = null) {
    var name by remember { mutableStateOf("") }
    var raw  by remember { mutableStateOf("") }
    var profile by remember { mutableStateOf(0) }  // 0 = Airealm, 1 = Générique
    val isLoc = remember(raw) { profile == 0 && raw.isNotBlank() && onImportLoc != null && looksLikeLocation(raw) }
    ModalBottomSheet(onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = L3,
        dragHandle = { Box(Modifier.padding(vertical=10.dp).width(32.dp).height(3.dp)
            .clip(RoundedCornerShape(2.dp)).background(L6)) }) {
        Column(Modifier.fillMaxWidth().padding(horizontal=22.dp)
            .navigationBarsPadding().imePadding()
            .verticalScroll(rememberScrollState())) {
            Text("Importer une fiche", style = Typo.headlineMedium.copy(color = T1))
            Text("Choisis le format, colle la carte — les champs se remplissent.",
                style = Typo.bodySmall.copy(color = T3), modifier = Modifier.padding(top = 4.dp))
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Airealm" to 0, "Générique / Autre" to 1).forEach { (lab, idx) ->
                    val sel = profile == idx
                    Box(Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                        .background(if(sel) GOLD.copy(alpha=0.18f) else L4)
                        .border(1.dp, if(sel) GOLD else L5, RoundedCornerShape(10.dp))
                        .clickable{ profile = idx }.padding(vertical=10.dp),
                        contentAlignment=Alignment.Center){
                        Text(lab, style=Typo.labelLarge.copy(color=if(sel) GOLD else T2))
                    }
                }
            }
            if (raw.isNotBlank()) {
                val chipColor = if (isLoc) TEAL else GOLD
                Row(Modifier.padding(top=10.dp).clip(RoundedCornerShape(999.dp))
                    .background(chipColor.copy(alpha=0.14f))
                    .border(1.dp, chipColor.copy(alpha=0.4f), RoundedCornerShape(999.dp))
                    .padding(horizontal=12.dp, vertical=6.dp),
                    verticalAlignment=Alignment.CenterVertically,
                    horizontalArrangement=Arrangement.spacedBy(6.dp)){
                    Icon(if(isLoc) Icons.Default.Place else Icons.Default.Person, null,
                        Modifier.size(13.dp), tint=chipColor)
                    Text(if(isLoc) "Lieu détecté" else "Personnage détecté",
                        style=Typo.labelMedium.copy(color=chipColor))
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = name, onValueChange = { name = it },
                placeholder = { Text("Nom du personnage…", color = T4,
                    style = Typo.bodyMedium.copy(fontStyle = FontStyle.Italic)) },
                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor=GOLD, unfocusedBorderColor=L6,
                    focusedContainerColor=L4, unfocusedContainerColor=L4, cursorColor=GOLD),
                textStyle = Typo.bodyLarge.copy(color = T1))
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value = raw, onValueChange = { raw = it },
                placeholder = { Text("| 18yo | Freshman | Major: … \n🧬 1. DNA\nLooks: …",
                    color = T4, style = Typo.bodySmall.copy(fontStyle = FontStyle.Italic)) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp, max = 280.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor=GOLD, unfocusedBorderColor=L6,
                    focusedContainerColor=L4, unfocusedContainerColor=L4, cursorColor=GOLD),
                textStyle = Typo.bodySmall.copy(color = T1, fontFamily = FontFamily.Monospace))
            Spacer(Modifier.height(14.dp))
            Button(onClick = {
                when {
                    isLoc && onImportLoc != null -> onImportLoc(parseLocationCard(name, raw))
                    profile == 1                 -> onImport(parseGenericCard(name, raw))
                    else                         -> onImport(parseAirealmCard(name, raw))
                }
                onDismiss()
            }, enabled = name.isNotBlank() && raw.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if(isLoc) TEAL else GOLD, contentColor = L0,
                    disabledContainerColor = L4, disabledContentColor = T4)) {
                Icon(Icons.Default.Download, null, Modifier.size(17.dp))
                Spacer(Modifier.width(8.dp))
                Text(if(isLoc) "Importer le lieu" else "Importer la fiche",
                    style = Typo.labelLarge.copy(fontFamily = CinzelFamily, fontSize = 14.sp))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// JOURNAL CARD — collapsible accordion entry
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun JournalCard(j: JournalEntry, onEdit: ()->Unit, onDelete: ()->Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    Column(modifier.fillMaxWidth().padding(horizontal=14.dp)
        .clip(RoundedCornerShape(14.dp)).background(L2)
        .border(1.dp, if (expanded) GOLD.copy(alpha=0.3f) else L5, RoundedCornerShape(14.dp))) {
        // Header — always visible, tap to toggle
        Row(Modifier.fillMaxWidth()
            .semantics { stateDescription = if (expanded) "Entrée dépliée" else "Entrée repliée" }
            .clickable(onClickLabel = if (expanded) "Replier" else "Déplier") {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); expanded = !expanded
            }
            .padding(start=16.dp, end=8.dp, top=14.dp, bottom=14.dp),
            verticalAlignment=Alignment.CenterVertically) {
            Box(Modifier.size(7.dp).clip(CircleShape).background(GOLD_MID))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(journalDateFmt.format(Date(j.dateMillis)).uppercase(),
                    style=Typo.labelSmall.copy(color=GOLD_MID))
                Text(j.title.ifBlank { "Entrée sans titre" },
                    style=Typo.titleLarge.copy(color=T1),
                    maxLines = if (expanded) Int.MAX_VALUE else 1, overflow=TextOverflow.Ellipsis,
                    modifier=Modifier.padding(top=2.dp).semantics{heading()})
                // Preview snippet when collapsed
                if (!expanded && j.text.isNotBlank())
                    Text(j.text, style=Typo.bodySmall.copy(color=T3),
                        maxLines=1, overflow=TextOverflow.Ellipsis, modifier=Modifier.padding(top=3.dp))
            }
            Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                null, Modifier.size(20.dp), tint=GOLD_MID.copy(alpha=0.7f))
        }
        AnimatedVisibility(expanded,
            enter=fadeIn(tween(180))+expandVertically(tween(200, easing=EaseOutCubic)),
            exit=fadeOut(tween(140))+shrinkVertically(tween(160))) {
            Column(Modifier.fillMaxWidth().padding(start=16.dp, end=16.dp, bottom=14.dp)) {
                GoldLine(0.15f)
                if (j.text.isNotBlank())
                    Text(j.text, style=Typo.bodyMedium.copy(color=T2),
                        modifier=Modifier.padding(top=12.dp))
                Row(Modifier.fillMaxWidth().padding(top=12.dp),
                    horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick=onEdit, modifier=Modifier.weight(1f),
                        border=BorderStroke(1.dp, L6), shape=RoundedCornerShape(10.dp),
                        colors=ButtonDefaults.outlinedButtonColors(contentColor=T2)) {
                        Icon(Icons.Default.Edit, null, Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp)); Text("Modifier", style=Typo.bodySmall)
                    }
                    OutlinedButton(onClick=onDelete, modifier=Modifier.weight(1f),
                        border=BorderStroke(1.dp, CRIM.copy(alpha=0.4f)), shape=RoundedCornerShape(10.dp),
                        colors=ButtonDefaults.outlinedButtonColors(contentColor=CRIM)) {
                        Icon(Icons.Default.Delete, null, Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp)); Text("Supprimer", style=Typo.bodySmall)
                    }
                }
            }
        }
    }
}

/** Builds a short collapsed-state summary from selected dna keys. */
fun dnaSummary(dna: Map<String,String>, keys: List<String>): String {
    val present = keys.count { !dna[it].isNullOrBlank() }
    return if (present == 0) "Non renseigné"
        else "$present champ${if (present>1) "s" else ""} renseigné${if (present>1) "s" else ""}"
}


// ─────────────────────────────────────────────────────────────────────────────
// SEARCH SCOPE CHIP
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ScopeChip(label: String, selected: Boolean, accent: Color = GOLD, onClick: ()->Unit) {
    val haptic = LocalHapticFeedback.current
    Box(Modifier.clip(RoundedCornerShape(999.dp))
        .background(if(selected) accent.copy(alpha=0.18f) else L3)
        .border(1.dp, if(selected) accent.copy(alpha=0.55f) else L5, RoundedCornerShape(999.dp))
        .clickable{ haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onClick() }
        .padding(horizontal=14.dp, vertical=7.dp)){
        Text(label, style=Typo.labelMedium.copy(color=if(selected) accent else T3))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GALLERY — grid + fullscreen zoom/swipe viewer + captions
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GalleryTab(
    photos: List<GalleryPhoto>,
    onUpdateCaption: (GalleryPhoto)->Unit,
    onDel: (GalleryPhoto)->Unit,
    bottomPad: Dp
) {
    var viewerIndex by remember { mutableStateOf<Int?>(null) }

    if (photos.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment=Alignment.Center){
            Column(horizontalAlignment=Alignment.CenterHorizontally, verticalArrangement=Arrangement.spacedBy(8.dp)){
                Ornament(TEAL)
                Icon(Icons.Outlined.PhotoLibrary, null, Modifier.size(44.dp), tint=T4)
                Text("Galerie vide", style=Typo.headlineSmall.copy(color=T3))
                Text("Ajoute des photos d'ambiance ou de scènes", style=Typo.bodySmall.copy(color=T4))
                Ornament(TEAL)
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start=12.dp, end=12.dp, top=12.dp, bottom=bottomPad+104.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            gridItems(photos, key={it.id}) { photo ->
                val idx = photos.indexOf(photo)
                Column(Modifier.animateItem()) {
                    Box(Modifier.fillMaxWidth().aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp)).background(L2)
                        .border(1.dp, L5, RoundedCornerShape(12.dp))
                        .pressable(onClickLabel="Agrandir"){ viewerIndex = idx }){
                        AsyncImage(
                            model=ImageRequest.Builder(LocalContext.current).data(File(photo.path)).crossfade(true).build(),
                            contentDescription=photo.caption.ifBlank{"Photo"},
                            contentScale=ContentScale.Crop, modifier=Modifier.fillMaxSize())
                        if (photo.caption.isNotBlank()) {
                            Box(Modifier.align(Alignment.BottomStart).fillMaxWidth()
                                .background(Brush.verticalGradient(listOf(Color.Transparent, L0.copy(alpha=0.85f))))
                                .padding(horizontal=8.dp, vertical=6.dp)){
                                Text(photo.caption, style=Typo.labelSmall.copy(color=T1),
                                    maxLines=2, overflow=TextOverflow.Ellipsis,
                                    modifier=Modifier.align(Alignment.BottomStart))
                            }
                        }
                    }
                }
            }
        }
    }

    // Fullscreen viewer
    viewerIndex?.let { startIdx ->
        GalleryViewer(photos, startIdx,
            onClose={ viewerIndex=null },
            onUpdateCaption=onUpdateCaption, onDel={ gp ->
                onDel(gp)
                viewerIndex = null
            })
    }
}

@Composable
fun GalleryViewer(
    photos: List<GalleryPhoto>, startIndex: Int,
    onClose: ()->Unit, onUpdateCaption: (GalleryPhoto)->Unit, onDel: (GalleryPhoto)->Unit
) {
    val pagerState = rememberPagerState(initialPage=startIndex.coerceIn(0, (photos.size-1).coerceAtLeast(0))){ photos.size }
    var editingCaption by remember { mutableStateOf<GalleryPhoto?>(null) }
    var delTarget by remember { mutableStateOf<GalleryPhoto?>(null) }

    editingCaption?.let { gp ->
        var text by remember { mutableStateOf(gp.caption) }
        AlertDialog(onDismissRequest={editingCaption=null}, containerColor=L3,
            title={Text("Légende", style=Typo.headlineSmall)},
            text={ OutlinedTextField(value=text, onValueChange={text=it},
                placeholder={Text("Qui / quoi sur cette photo…", color=T4, style=Typo.bodyMedium.copy(fontStyle=FontStyle.Italic))},
                modifier=Modifier.fillMaxWidth(), minLines=2, shape=RoundedCornerShape(10.dp),
                colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=GOLD, unfocusedBorderColor=L6,
                    focusedContainerColor=L4, unfocusedContainerColor=L4, cursorColor=GOLD),
                textStyle=Typo.bodyMedium.copy(color=T1)) },
            confirmButton={Button(onClick={onUpdateCaption(gp.copy(caption=text.trim())); editingCaption=null},
                colors=ButtonDefaults.buttonColors(containerColor=GOLD, contentColor=L0)){Text("Enregistrer")}},
            dismissButton={TextButton(onClick={editingCaption=null}){Text("Annuler")}})
    }
    delTarget?.let { gp ->
        ConfirmDelete("Supprimer la photo ?","Cette photo sera retirée de la galerie.",
            {onDel(gp); delTarget=null}, {delTarget=null})
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest=onClose,
        properties=androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth=false)
    ) {
        Box(Modifier.fillMaxSize().background(Color(0xF0000000))){
            HorizontalPager(state=pagerState, modifier=Modifier.fillMaxSize()){ page ->
                val photo = photos[page]
                var scale by remember { mutableStateOf(1f) }
                var offX by remember { mutableStateOf(0f) }
                var offY by remember { mutableStateOf(0f) }
                Box(Modifier.fillMaxSize()
                    .pointerInput(Unit){
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 4f)
                            if (scale > 1f) { offX += pan.x; offY += pan.y }
                            else { offX = 0f; offY = 0f }
                        }
                    }
                    .pointerInput(Unit){ detectTapGestures(onDoubleTap={
                        if (scale > 1f){ scale=1f; offX=0f; offY=0f } else scale=2.5f
                    })},
                    contentAlignment=Alignment.Center){
                    AsyncImage(
                        model=ImageRequest.Builder(LocalContext.current).data(File(photo.path)).crossfade(true).build(),
                        contentDescription=photo.caption.ifBlank{"Photo"},
                        contentScale=ContentScale.Fit,
                        modifier=Modifier.fillMaxSize().graphicsLayer(
                            scaleX=scale, scaleY=scale, translationX=offX, translationY=offY))
                }
            }
            // Top bar
            Row(Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal=10.dp, vertical=8.dp),
                verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.SpaceBetween){
                Box(Modifier.clip(CircleShape).background(L0.copy(alpha=0.5f)).clickable{onClose()}.padding(8.dp)){
                    Icon(Icons.Default.Close,"Fermer",Modifier.size(22.dp),tint=T1)
                }
                Text("${pagerState.currentPage+1} / ${photos.size}", style=Typo.labelMedium.copy(color=T1))
                Row {
                    val cur = photos.getOrNull(pagerState.currentPage)
                    Box(Modifier.clip(CircleShape).background(L0.copy(alpha=0.5f)).clickable{ cur?.let{editingCaption=it} }.padding(8.dp)){
                        Icon(Icons.Default.Edit,"Légende",Modifier.size(20.dp),tint=T1)
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.clip(CircleShape).background(L0.copy(alpha=0.5f)).clickable{ cur?.let{delTarget=it} }.padding(8.dp)){
                        Icon(Icons.Default.Delete,"Supprimer",Modifier.size(20.dp),tint=CRIM)
                    }
                }
            }
            // Caption bar
            val cur = photos.getOrNull(pagerState.currentPage)
            if (cur != null && cur.caption.isNotBlank()) {
                Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, L0.copy(alpha=0.9f))))
                    .navigationBarsPadding().padding(horizontal=24.dp, vertical=20.dp)){
                    Text(cur.caption, style=Typo.bodyLarge.copy(color=T1),
                        modifier=Modifier.align(Alignment.BottomCenter))
                }
            }
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// MICRO-ANIMATIONS
// ─────────────────────────────────────────────────────────────────────────────

/** Staggered entrance: fades + rises, delayed by item index for a cascade effect. */
@Composable
fun CascadeIn(index: Int, content: @Composable ()->Unit) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay((index.coerceAtMost(10) * 55).toLong())  // slightly longer, more visible stagger
        shown = true
    }
    val alpha by animateFloatAsState(if (shown) 1f else 0f, tween(360), label="cascadeAlpha")
    // Rise + gentle scale "pop" with a soft spring for a premium settle
    val ty by animateFloatAsState(if (shown) 0f else 38f,
        spring(dampingRatio = 0.74f, stiffness = 230f), label="cascadeY")
    val scale by animateFloatAsState(if (shown) 1f else 0.94f,
        spring(dampingRatio = 0.7f, stiffness = 260f), label="cascadeScale")
    Box(Modifier.graphicsLayer {
        this.alpha = alpha; translationY = ty
        scaleX = scale; scaleY = scale
        transformOrigin = TransformOrigin(0.5f, 0.2f)
    }) { content() }
}


// ─────────────────────────────────────────────────────────────────────────────
// NPC CREATION CHOICE — import an Airealm card, or create blank
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NpcChoiceSheet(onDismiss: ()->Unit, onImport: ()->Unit, onBlank: ()->Unit) {
    ModalBottomSheet(onDismissRequest=onDismiss,
        sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true),
        containerColor=L3,
        dragHandle={Box(Modifier.padding(vertical=10.dp).width(32.dp).height(3.dp)
            .clip(RoundedCornerShape(2.dp)).background(L6))}) {
        Column(Modifier.fillMaxWidth().padding(horizontal=18.dp).navigationBarsPadding().padding(bottom=12.dp)) {
            Text("Nouveau personnage", style=Typo.headlineMedium.copy(color=T1))
            Spacer(Modifier.height(4.dp))
            Text("Importe une carte Airealm pour remplir la fiche automatiquement, ou pars d'une fiche vierge.",
                style=Typo.bodySmall.copy(color=T3))
            Spacer(Modifier.height(18.dp))
            // Primary: import
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .background(GOLD.copy(alpha=0.14f))
                .border(1.dp, GOLD.copy(alpha=0.4f), RoundedCornerShape(14.dp))
                .pressable(onClickLabel="Importer une carte"){onImport()}
                .padding(16.dp),
                verticalAlignment=Alignment.CenterVertically){
                Box(Modifier.size(40.dp).clip(CircleShape).background(GOLD.copy(alpha=0.18f)),
                    contentAlignment=Alignment.Center){
                    Icon(Icons.Default.Download, null, Modifier.size(20.dp), tint=GOLD_HI)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)){
                    Text("Importer une carte Airealm", style=Typo.titleMedium.copy(color=T1))
                    Text("Colle la carte, les champs se remplissent", style=Typo.bodySmall.copy(color=T3))
                }
                Icon(Icons.Default.ChevronRight, null, Modifier.size(20.dp), tint=GOLD.copy(alpha=0.6f))
            }
            Spacer(Modifier.height(10.dp))
            // Secondary: blank
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .background(L4)
                .border(1.dp, L5, RoundedCornerShape(14.dp))
                .pressable(onClickLabel="Créer vierge"){onBlank()}
                .padding(16.dp),
                verticalAlignment=Alignment.CenterVertically){
                Box(Modifier.size(40.dp).clip(CircleShape).background(L5),
                    contentAlignment=Alignment.Center){
                    Icon(Icons.Default.PersonAdd, null, Modifier.size(20.dp), tint=T2)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)){
                    Text("Créer une fiche vierge", style=Typo.titleMedium.copy(color=T1))
                    Text("Tout remplir à la main", style=Typo.bodySmall.copy(color=T3))
                }
                Icon(Icons.Default.ChevronRight, null, Modifier.size(20.dp), tint=T4)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// "REPRENDRE" — quick-resume card for the last opened campaign
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ContinueCard(c: Campaign, modifier: Modifier = Modifier, onOpen: ()->Unit) {
    val seal = sealHue(c.name)
    Column(modifier.fillMaxWidth().padding(horizontal=16.dp, vertical=8.dp)) {
        // eyebrow
        Row(verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(6.dp),
            modifier=Modifier.padding(start=4.dp, bottom=8.dp)){
            Icon(Icons.Default.History, null, Modifier.size(12.dp), tint=GOLD_MID)
            Text("REPRENDRE", style=Typo.labelMedium.copy(color=GOLD_MID))
        }
        Box(Modifier.fillMaxWidth().height(96.dp)
            .floatingSurface(RoundedCornerShape(18.dp), glow=seal, elevation=16.dp, fill=L2)
            .border(1.dp, Brush.horizontalGradient(listOf(seal.copy(alpha=0.4f), L5.copy(alpha=0.3f))),
                RoundedCornerShape(18.dp))
            .pressable(onClickLabel="Reprendre ${c.name}"){onOpen()}){
            // cover photo or monogram
            if (c.photoUri != null) {
                AsyncImage(
                    model=ImageRequest.Builder(LocalContext.current).data(File(c.photoUri)).crossfade(true).build(),
                    contentDescription=null, contentScale=ContentScale.Crop,
                    modifier=Modifier.fillMaxSize().clip(RoundedCornerShape(18.dp)).alpha(0.5f))
                Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(
                    listOf(L2.copy(alpha=0.95f), L2.copy(alpha=0.55f)))))
            } else {
                EngravedMonogram(c.name.take(1).uppercase(), seal, 80.sp,
                    Modifier.align(Alignment.CenterEnd).padding(end=20.dp), baseAlpha=0.16f)
            }
            Row(Modifier.fillMaxSize().padding(horizontal=20.dp),
                verticalAlignment=Alignment.CenterVertically){
                Column(Modifier.weight(1f)){
                    Text(c.name, style=Typo.headlineSmall.copy(color=T1), maxLines=1, overflow=TextOverflow.Ellipsis)
                    Text("${c.npcs.size} personnages · ${c.locations.size} lieu${if(c.locations.size>1)"x" else ""}",
                        style=Typo.bodySmall.copy(color=T3), modifier=Modifier.padding(top=2.dp))
                }
                Box(Modifier.size(40.dp).clip(CircleShape).background(seal.copy(alpha=0.18f)),
                    contentAlignment=Alignment.Center){
                    Icon(Icons.Default.PlayArrow, "Reprendre", Modifier.size(20.dp), tint=GOLD_HI)
                }
            }
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// EMBER DUST — slow rising copper motes. Very subtle, premium, not a screensaver.
// ─────────────────────────────────────────────────────────────────────────────

private class Ember(
    val x: Float, val size: Float, val speed: Float, val phase: Float, val drift: Float, val maxA: Float
)

@Composable
fun EmberDust(modifier: Modifier = Modifier) {
    val embers = remember {
        val rnd = java.util.Random(42)
        List(34) {
            Ember(
                x = rnd.nextFloat(),
                size = 2.5f + rnd.nextFloat() * 4.5f,
                speed = 0.10f + rnd.nextFloat() * 0.16f,
                phase = rnd.nextFloat(),
                drift = (rnd.nextFloat() - 0.5f) * 0.06f,
                maxA = 0.30f + rnd.nextFloat() * 0.45f
            )
        }
    }
    val emberColor by animateColorAsState(CurrentTheme.theme.ember, tween(800), label="emberCol")
    val t = rememberInfiniteTransition(label="ember")
    val clock by t.animateFloat(0f, 1f,
        infiniteRepeatable(tween(28000, easing=LinearEasing), RepeatMode.Restart), label="emberClock")
    // subtle twinkle so embers pulse as they rise
    val tw by t.animateFloat(0f, 6.28f,
        infiniteRepeatable(tween(3200, easing=LinearEasing), RepeatMode.Restart), label="twinkle")
    Canvas(modifier) {
        embers.forEach { e ->
            val p = (e.phase + clock / e.speed) % 1f
            val y = (1f - p) * size.height * 1.05f - size.height*0.02f
            val x = (e.x + e.drift * kotlin.math.sin(p * 6.28f)) * size.width
            val edge = (kotlin.math.min(p, 1f - p) * 3.5f).coerceIn(0f, 1f)
            val twinkle = 0.75f + 0.25f * kotlin.math.sin(tw + e.phase * 6.28f)
            val a = e.maxA * edge * twinkle
            if (a > 0.004f) {
                // soft outer glow
                drawCircle(emberColor.copy(alpha = a * 0.35f), e.size * 2.6f, Offset(x, y))
                // bright core
                drawCircle(emberColor.copy(alpha = a), e.size, Offset(x, y))
            }
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// LOCATION import — choice sheet + paste-template sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocChoiceSheet(onDismiss: ()->Unit, onImport: ()->Unit, onBlank: ()->Unit) {
    ModalBottomSheet(onDismissRequest=onDismiss,
        sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true),
        containerColor=L3,
        dragHandle={Box(Modifier.padding(vertical=10.dp).width(32.dp).height(3.dp)
            .clip(RoundedCornerShape(2.dp)).background(L6))}) {
        Column(Modifier.fillMaxWidth().padding(horizontal=18.dp).navigationBarsPadding().padding(bottom=12.dp)) {
            Text("Nouveau lieu", style=Typo.headlineMedium.copy(color=T1))
            Spacer(Modifier.height(4.dp))
            Text("Importe un profil de lieu Airealm pour remplir la fiche, ou pars d'une fiche vierge.",
                style=Typo.bodySmall.copy(color=T3))
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .background(TEAL.copy(alpha=0.14f))
                .border(1.dp, TEAL.copy(alpha=0.4f), RoundedCornerShape(14.dp))
                .pressable(onClickLabel="Importer un profil"){onImport()}
                .padding(16.dp), verticalAlignment=Alignment.CenterVertically){
                Box(Modifier.size(40.dp).clip(CircleShape).background(TEAL.copy(alpha=0.18f)),
                    contentAlignment=Alignment.Center){
                    Icon(Icons.Default.Download, null, Modifier.size(20.dp), tint=TEAL)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)){
                    Text("Importer un profil de lieu", style=Typo.titleMedium.copy(color=T1))
                    Text("Colle le template, les champs se remplissent", style=Typo.bodySmall.copy(color=T3))
                }
                Icon(Icons.Default.ChevronRight, null, Modifier.size(20.dp), tint=TEAL.copy(alpha=0.6f))
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .background(L4).border(1.dp, L5, RoundedCornerShape(14.dp))
                .pressable(onClickLabel="Créer vierge"){onBlank()}
                .padding(16.dp), verticalAlignment=Alignment.CenterVertically){
                Box(Modifier.size(40.dp).clip(CircleShape).background(L5),
                    contentAlignment=Alignment.Center){
                    Icon(Icons.Default.AddLocation, null, Modifier.size(20.dp), tint=T2)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)){
                    Text("Créer une fiche vierge", style=Typo.titleMedium.copy(color=T1))
                    Text("Tout remplir à la main", style=Typo.bodySmall.copy(color=T3))
                }
                Icon(Icons.Default.ChevronRight, null, Modifier.size(20.dp), tint=T4)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportLocSheet(onDismiss: ()->Unit, onImport: (Location)->Unit) {
    var name by remember { mutableStateOf("") }
    var raw  by remember { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = L3,
        dragHandle = { Box(Modifier.padding(vertical=10.dp).width(32.dp).height(3.dp)
            .clip(RoundedCornerShape(2.dp)).background(L6)) }) {
        Column(Modifier.fillMaxWidth().padding(horizontal=22.dp)
            .navigationBarsPadding().imePadding().verticalScroll(rememberScrollState())) {
            Text("Importer un profil de lieu", style = Typo.headlineMedium.copy(color = T1))
            Text("Colle un LOCATION PROFILE — les champs se remplissent automatiquement.",
                style = Typo.bodySmall.copy(color = T3), modifier = Modifier.padding(top = 4.dp))
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = name, onValueChange = { name = it },
                placeholder = { Text("Nom du lieu…", color = T4,
                    style = Typo.bodyMedium.copy(fontStyle = FontStyle.Italic)) },
                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor=TEAL, unfocusedBorderColor=L6,
                    focusedContainerColor=L4, unfocusedContainerColor=L4, cursorColor=TEAL),
                textStyle = Typo.bodyLarge.copy(color = T1))
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value = raw, onValueChange = { raw = it },
                placeholder = { Text("Type: … | Zone: … | Access: …\n• Architectural Style: …",
                    color = T4, style = Typo.bodySmall.copy(fontStyle = FontStyle.Italic)) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp, max = 280.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor=TEAL, unfocusedBorderColor=L6,
                    focusedContainerColor=L4, unfocusedContainerColor=L4, cursorColor=TEAL),
                textStyle = Typo.bodySmall.copy(color = T1, fontFamily = FontFamily.Monospace))
            Spacer(Modifier.height(14.dp))
            Button(onClick = { onImport(parseLocationCard(name, raw)); onDismiss() },
                enabled = name.isNotBlank() && raw.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TEAL, contentColor = L0,
                    disabledContainerColor = L4, disabledContentColor = T4)) {
                Icon(Icons.Default.Download, null, Modifier.size(17.dp))
                Spacer(Modifier.width(8.dp))
                Text("Importer le lieu", style = Typo.labelLarge.copy(fontFamily = CinzelFamily, fontSize = 14.sp))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// PHOTO ZOOM VIEWER — fullscreen pinch-zoom + swipe between a fiche's photos
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun PhotoZoomViewer(uris: List<String>, startIndex: Int, onClose: ()->Unit) {
    if (uris.isEmpty()) { onClose(); return }
    val pagerState = rememberPagerState(
        initialPage = startIndex.coerceIn(0, uris.size-1)){ uris.size }
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onClose,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        // Drag-to-dismiss state (vertical)
        var dragY by remember { mutableStateOf(0f) }
        val dismissProgress = (kotlin.math.abs(dragY) / 600f).coerceIn(0f, 1f)
        val bgAlpha = 1f - dismissProgress * 0.6f
        Box(Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(
                Color(0xFF0A0908).copy(alpha=bgAlpha),
                Color.Black.copy(alpha=bgAlpha),
                Color(0xFF0A0908).copy(alpha=bgAlpha)
            )))) {
            HorizontalPager(state=pagerState, modifier=Modifier.fillMaxSize()) { page ->
                var scale by remember { mutableStateOf(1f) }
                var offX by remember { mutableStateOf(0f) }
                var offY by remember { mutableStateOf(0f) }
                Box(Modifier.fillMaxSize()
                    .pointerInput(Unit){
                        detectTapGestures(onDoubleTap = {
                            if (scale > 1f) { scale = 1f; offX = 0f; offY = 0f } else scale = 2.5f
                        })
                    }
                    .pointerInput(Unit){
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 4f)
                            if (scale > 1f) { offX += pan.x; offY += pan.y }
                            else { offX = 0f; offY = 0f }
                        }
                    }
                    // vertical drag-to-dismiss only when not zoomed
                    .pointerInput(scale){
                        if (scale <= 1f) {
                            detectVerticalDragGestures(
                                onDragEnd = { if (kotlin.math.abs(dragY) > 240f) onClose() else dragY = 0f }
                            ){ _, d -> dragY += d }
                        }
                    }, contentAlignment=Alignment.Center){
                    AsyncImage(
                        model=ImageRequest.Builder(LocalContext.current).data(File(uris[page])).crossfade(true).build(),
                        contentDescription=null, contentScale=ContentScale.Fit,
                        modifier=Modifier.fillMaxSize().padding(vertical=12.dp).graphicsLayer{
                            scaleX=scale; scaleY=scale
                            translationX=offX; translationY=offY + dragY
                            alpha = 1f - dismissProgress * 0.4f
                        })
                }
            }
            // close button
            Box(Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(14.dp)
                .size(40.dp).clip(CircleShape).background(Color.Black.copy(alpha=0.5f))
                .clickable{ onClose() }, contentAlignment=Alignment.Center){
                Icon(Icons.Default.Close, "Fermer", Modifier.size(20.dp), tint=Color.White)
            }
            // page indicator
            if (uris.size > 1) {
                Box(Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom=20.dp)
                    .clip(RoundedCornerShape(999.dp)).background(Color.Black.copy(alpha=0.5f))
                    .padding(horizontal=14.dp, vertical=6.dp)){
                    Text("${pagerState.currentPage+1} / ${uris.size}",
                        style=Typo.labelMedium.copy(color=Color.White))
                }
            }
        }
    }
}


/** Copy icon that briefly turns into a green check + haptic — visual confirmation. */
@Composable
fun CopyButton(accent: Color = GOLD, onCopy: ()->Unit) {
    var copied by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(copied) { if (copied) { delay(1400); copied = false } }
    IconButton(onClick={ onCopy(); haptic.performHapticFeedback(HapticFeedbackType.LongPress); copied = true }) {
        AnimatedContent(targetState=copied, label="copy",
            transitionSpec={ (fadeIn(tween(150))+scaleIn(tween(150), initialScale=0.6f))
                .togetherWith(fadeOut(tween(120))) }) { done ->
            if (done) Icon(Icons.Default.Check, "Copié", Modifier.size(17.dp), tint=Color(0xFF6FCF97))
            else Icon(Icons.Default.ContentCopy, "Copier le résumé", Modifier.size(17.dp), tint=T1)
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// RELATIONS DASHBOARD — social overview: each NPC's key facets as coloured gauges
// ─────────────────────────────────────────────────────────────────────────────

/** Ordered facet levels → a 0..1 intensity for gauges. */
private val FACET_LEVELS = mapOf(
    "none" to 0.0f, "aucun" to 0.0f, "aucune" to 0.0f,
    "unverified" to 0.1f, "minimal" to 0.2f, "low" to 0.25f, "faible" to 0.25f,
    "guarded" to 0.35f, "neutral" to 0.4f, "neutre" to 0.4f,
    "medium" to 0.55f, "moderate" to 0.6f, "modéré" to 0.6f, "modere" to 0.6f,
    "high" to 0.8f, "élevé" to 0.8f, "eleve" to 0.8f, "strong" to 0.85f,
    "intense" to 0.95f, "max" to 1.0f, "total" to 1.0f
)

/** Parse "❤️Attraction: Unverified | 💞Intimacy: None | …" into ordered (label,value,intensity). */
fun parseFacets(raw: String): List<Triple<String,String,Float>> {
    if (raw.isBlank()) return emptyList()
    return raw.split("|","\n").mapNotNull { seg ->
        // Drop any leading non-letter chars (emojis, bullets, spaces) — emojis can't be Char literals
        val s = seg.trim().trimStart { !it.isLetter() }
        if (!s.contains(":")) return@mapNotNull null
        val label = s.substringBefore(":").trim().filter { it.isLetter() || it == ' ' }.trim()
        val value = s.substringAfter(":").trim()
        if (label.isBlank() || value.isBlank()) return@mapNotNull null
        val intensity = FACET_LEVELS[value.lowercase()] ?: 0.45f
        Triple(label, value, intensity)
    }
}

/** Colour for a facet by name (warm = affinity/attraction, cool = trust, red = suspicion/jealousy). */
fun facetColor(label: String): Color = when {
    label.contains("Attraction", true) || label.contains("Intimacy", true) -> Color(0xFFE87CA8)
    label.contains("Affinity", true) || label.contains("Comfort", true)    -> Color(0xFFE89B6C)
    label.contains("Trust", true) || label.contains("Respect", true)       -> Color(0xFF5B9A8B)
    label.contains("Suspicion", true) || label.contains("Jealousy", true)  -> Color(0xFFB04A3A)
    else -> GOLD
}

@Composable
fun RelationsScreen(c: Campaign, onBack: ()->Unit, onOpenNpc: (Npc)->Unit) {
    // NPCs that have facet data, sorted by overall "closeness" (avg of positive facets)
    val withFacets = c.npcs.map { it to parseFacets(it.dna["Facets"] ?: "") }
        .filter { it.second.isNotEmpty() || it.first.gaugeValues.isNotEmpty() }
    Column(Modifier.fillMaxSize().imePadding()) {
        // header
        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(start=8.dp,end=16.dp,top=8.dp,bottom=8.dp),
            verticalAlignment=Alignment.CenterVertically){
            Box(Modifier.clip(RoundedCornerShape(999.dp)).background(L2)
                .pressable(onClickLabel="Retour"){onBack()}.padding(horizontal=14.dp,vertical=8.dp)){
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(6.dp)){
                    Icon(Icons.Default.ArrowBack,null,Modifier.size(18.dp),tint=GOLD)
                    Text("Retour",style=Typo.labelLarge.copy(color=T1))
                }
            }
        }
        Column(Modifier.padding(start=20.dp,end=20.dp,bottom=12.dp)){
            Text("RELATIONS",style=Typo.labelLarge.copy(color=GOLD_MID))
            Text("Tableau social",style=Typo.headlineLarge.copy(color=T1))
        }
        if (withFacets.isEmpty()) {
            Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){
                Column(horizontalAlignment=Alignment.CenterHorizontally){
                    Icon(Icons.Default.Diversity3,null,Modifier.size(40.dp),tint=T4)
                    Spacer(Modifier.height(10.dp))
                    Text("Aucune relation renseignée",style=Typo.bodyMedium.copy(color=T3))
                    Text("Les Facets et jauges des personnages s'affichent ici.",
                        style=Typo.bodySmall.copy(color=T4),modifier=Modifier.padding(top=4.dp))
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize(),
                contentPadding=PaddingValues(start=16.dp,end=16.dp,bottom=40.dp),
                verticalArrangement=Arrangement.spacedBy(10.dp)){
                items(withFacets, key={it.first.id}){ (npc, facets) ->
                    val seal = sealHue(npc.name)
                    Column(Modifier.fillMaxWidth()
                        .floatingSurface(RoundedCornerShape(18.dp), glow=seal, elevation=8.dp, fill=L2)
                        .border(1.dp, L5.copy(alpha=0.5f), RoundedCornerShape(18.dp))
                        .pressable(onClickLabel="Ouvrir ${npc.name}"){onOpenNpc(npc)}
                        .padding(16.dp)){
                        // name row
                        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)){
                            Box(Modifier.size(36.dp).clip(CircleShape).background(nameGrad(npc.name)),
                                contentAlignment=Alignment.Center){
                                if(npc.photoUris.isNotEmpty())
                                    AsyncImage(model=ImageRequest.Builder(LocalContext.current).data(File(npc.photoUris.first())).crossfade(true).build(),
                                        contentDescription=null,contentScale=ContentScale.Crop,modifier=Modifier.fillMaxSize())
                                else Text(npc.name.take(1).uppercase(),style=Typo.labelLarge.copy(color=seal))
                            }
                            Text(npc.name,style=Typo.titleLarge.copy(color=T1))
                        }
                        Spacer(Modifier.height(12.dp))
                        // facet gauges (only the 4 main ones to stay readable)
                        val main = facets.filter { f -> listOf("Attraction","Trust","Affinity","Comfort","Respect")
                            .any { f.first.contains(it, true) } }.take(5)
                        main.forEach { (label, value, intensity) ->
                            val col = facetColor(label)
                            Row(Modifier.fillMaxWidth().padding(vertical=3.dp),verticalAlignment=Alignment.CenterVertically){
                                Text(label,style=Typo.labelMedium.copy(color=T3),modifier=Modifier.width(96.dp))
                                Box(Modifier.weight(1f).height(7.dp).clip(RoundedCornerShape(999.dp)).background(L4)){
                                    Box(Modifier.fillMaxHeight().fillMaxWidth(intensity.coerceIn(0.04f,1f))
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Brush.horizontalGradient(listOf(col.copy(alpha=0.7f),col))))
                                }
                                Text(value,style=Typo.labelSmall.copy(color=T2),
                                    modifier=Modifier.width(74.dp).padding(start=8.dp))
                            }
                        }
                        // Custom campaign gauges for this NPC
                        c.gauges.filter { npc.gaugeValues[it.id]?.isNotBlank() == true }.forEach { g ->
                            val raw = npc.gaugeValues[g.id] ?: ""
                            val col = LABEL_COLORS[g.colorIndex % LABEL_COLORS.size]
                            val intensity = if (g.numeric) (raw.toFloatOrNull() ?: 0f)/100f
                                            else gaugeLevelIntensity(raw)
                            Row(Modifier.fillMaxWidth().padding(vertical=3.dp),verticalAlignment=Alignment.CenterVertically){
                                Text(g.name,style=Typo.labelMedium.copy(color=T3),modifier=Modifier.width(96.dp))
                                Box(Modifier.weight(1f).height(7.dp).clip(RoundedCornerShape(999.dp)).background(L4)){
                                    Box(Modifier.fillMaxHeight().fillMaxWidth(intensity.coerceIn(0.04f,1f))
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Brush.horizontalGradient(listOf(col.copy(alpha=0.7f),col))))
                                }
                                Text(if(g.numeric) "$raw/100" else raw,style=Typo.labelSmall.copy(color=T2),
                                    modifier=Modifier.width(74.dp).padding(start=8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// GAUGE MANAGER — create/edit per-campaign custom gauges (stats)
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GaugeManagerDialog(initial: List<Gauge>, onDismiss: ()->Unit, onSave: (List<Gauge>)->Unit) {
    var gauges by remember { mutableStateOf(initial) }
    var newName by remember { mutableStateOf("") }
    var newColor by remember { mutableStateOf(0) }
    var newNumeric by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest=onDismiss, containerColor=L3,
        title={Text("Jauges personnalisées", style=Typo.headlineSmall)},
        text={Column(Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement=Arrangement.spacedBy(8.dp)){
            Text("Crée des stats propres à cette campagne (Peur, Sanity, Attraction…).",
                style=Typo.bodySmall.copy(color=T3))
            // Existing gauges
            gauges.forEach { g ->
                Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically){
                    Box(Modifier.size(14.dp).clip(CircleShape).background(LABEL_COLORS[g.colorIndex % LABEL_COLORS.size]))
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)){
                        Text(g.name, style=Typo.bodyMedium.copy(color=T1))
                        Text(if(g.numeric) "Numérique (0–100)" else "Niveaux (None→Max)",
                            style=Typo.labelSmall.copy(color=T4))
                    }
                    IconButton(onClick={ gauges = gauges.filter { it.id != g.id } }){
                        Icon(Icons.Default.Close, "Supprimer", Modifier.size(16.dp), tint=CRIM)
                    }
                }
            }
            if (gauges.isNotEmpty()) HorizontalDivider(color=L5)
            // New gauge form
            Text("Nouvelle jauge", style=Typo.labelMedium.copy(color=T3))
            OutlinedTextField(value=newName, onValueChange={newName=it}, singleLine=true,
                placeholder={Text("Nom (ex. Peur)", color=T4, style=Typo.bodyMedium.copy(fontStyle=FontStyle.Italic))},
                modifier=Modifier.fillMaxWidth(),
                colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=GOLD, unfocusedBorderColor=L6,
                    focusedContainerColor=L4, unfocusedContainerColor=L4, cursorColor=GOLD),
                textStyle=Typo.bodyMedium.copy(color=T1))
            // Type toggle
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)){
                listOf("Niveaux" to false, "Numérique" to true).forEach { (lab, num) ->
                    val sel = newNumeric == num
                    Box(Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                        .background(if(sel) GOLD.copy(alpha=0.18f) else L4)
                        .border(1.dp, if(sel) GOLD else L5, RoundedCornerShape(10.dp))
                        .clickable{ newNumeric = num }.padding(vertical=9.dp),
                        contentAlignment=Alignment.Center){
                        Text(lab, style=Typo.labelMedium.copy(color=if(sel) GOLD else T2))
                    }
                }
            }
            // Colour picker
            FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp), verticalArrangement=Arrangement.spacedBy(8.dp)){
                LABEL_COLORS.forEachIndexed { i, col ->
                    Box(Modifier.size(26.dp).clip(CircleShape).background(col.copy(alpha=0.25f))
                        .border(2.dp, if(newColor==i) col else Color.Transparent, CircleShape)
                        .clickable{ newColor=i }, contentAlignment=Alignment.Center){
                        Box(Modifier.size(13.dp).clip(CircleShape).background(col))
                    }
                }
            }
            Button(onClick={
                if(newName.isNotBlank()){
                    gauges = gauges + Gauge(name=newName.trim(), colorIndex=newColor, numeric=newNumeric)
                    newName=""; newNumeric=false
                }
            }, enabled=newName.isNotBlank(), modifier=Modifier.fillMaxWidth(),
                colors=ButtonDefaults.buttonColors(containerColor=GOLD, contentColor=L0,
                    disabledContainerColor=L4, disabledContentColor=T4)){
                Icon(Icons.Default.Add, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp))
                Text("Ajouter la jauge")
            }
        }},
        confirmButton={Button(onClick={onSave(gauges); onDismiss()},
            colors=ButtonDefaults.buttonColors(containerColor=GOLD, contentColor=L0)){Text("Terminé")}},
        dismissButton={TextButton(onClick=onDismiss){Text("Annuler", color=T3)}})
}
