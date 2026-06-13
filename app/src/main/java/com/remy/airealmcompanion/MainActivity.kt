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
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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

private val L0 = Color(0xFF080604)
private val L1 = Color(0xFF0D0B08)
private val L2 = Color(0xFF161210)
private val L3 = Color(0xFF1E1A16)
private val L4 = Color(0xFF27221C)
private val L5 = Color(0xFF332D25)
private val L6 = Color(0xFF453D30)

private val GOLD_HI   = Color(0xFFEEC85A)
private val GOLD      = Color(0xFFCCA040)
private val GOLD_MID  = Color(0xFF886820)
private val GOLD_DIM  = Color(0xFF3A2C0C)

private val T1 = Color(0xFFF2EAD8)
private val T2 = Color(0xFFB8A882)
private val T3 = Color(0xFF7A6A4A)
private val T4 = Color(0xFF4A3C24)

private val TEAL    = Color(0xFF3D8888)
private val TEAL_LO = Color(0xFF183030)
private val CRIM    = Color(0xFF8B2424)
private val CRIM_LO = Color(0xFF280A0A)

// Label palette — 8 distinct warm/cool colours that all work on the dark bg
val LABEL_COLORS = listOf(
    Color(0xFFCCA040), // Ambre
    Color(0xFF3D8888), // Sarcelle
    Color(0xFF7A5088), // Améthyste
    Color(0xFF8B4040), // Cramoisi
    Color(0xFF3A6A50), // Mousse
    Color(0xFF6A5030), // Bronze
    Color(0xFF405070), // Ardoise
    Color(0xFF706040), // Sable
)
val LABEL_COLOR_NAMES = listOf("Ambre","Sarcelle","Améthyste","Cramoisi","Mousse","Bronze","Ardoise","Sable")

private fun scheme() = darkColorScheme(
    primary = GOLD, onPrimary = L0, primaryContainer = GOLD_DIM,
    secondary = TEAL, onSecondary = L0,
    background = L1, onBackground = T1,
    surface = L3, onSurface = T1,
    surfaceVariant = L4, onSurfaceVariant = T2,
    outline = L5, outlineVariant = L6, error = CRIM,
)

private val Typo = Typography(
    displayMedium  = TextStyle(fontFamily = CinzelFamily, fontWeight = FontWeight.ExtraBold, fontSize = 40.sp, lineHeight = 44.sp, letterSpacing = (-0.5).sp),
    headlineLarge  = TextStyle(fontFamily = CinzelFamily, fontWeight = FontWeight.Bold,      fontSize = 26.sp, lineHeight = 32.sp),
    headlineMedium = TextStyle(fontFamily = CinzelFamily, fontWeight = FontWeight.Bold,      fontSize = 21.sp, lineHeight = 27.sp),
    headlineSmall  = TextStyle(fontFamily = CinzelFamily, fontWeight = FontWeight.SemiBold,  fontSize = 18.sp, lineHeight = 24.sp),
    titleLarge     = TextStyle(fontFamily = CinzelFamily, fontWeight = FontWeight.Bold,      fontSize = 16.sp, lineHeight = 22.sp),
    titleMedium    = TextStyle(fontFamily = CinzelFamily, fontWeight = FontWeight.SemiBold,  fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge     = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, letterSpacing = 1.8.sp),
    labelMedium    = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium,   fontSize = 10.sp, letterSpacing = 1.6.sp),
    labelSmall     = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal,   fontSize = 10.sp, letterSpacing = 1.3.sp),
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
    val npcs: List<Npc> = emptyList(),
    val locations: List<Location> = emptyList(),
    val journal: List<JournalEntry> = emptyList()
)

data class Npc(
    val id: String = UUID.randomUUID().toString(), val campaignId: String,
    val name: String, val role: String = "", val shortCard: String = "",
    val fullCard: String = "", val relationships: String = "", val secrets: String = "",
    val sceneHistory: String = "", val tags: String = "",
    val dna: Map<String,String> = emptyMap(),     // structured Airealm fields, one line each
    val labelIds: List<String> = emptyList(),
    val photoUris: List<String> = emptyList()
)

/** Canonical Airealm DNA / INERTIA field keys, in display order. */
val DNA_KEYS = listOf(
    "Looks","Style/Persona","Voice/Humor","Lifestyle/Vices",
    "Insecurity/Drive","Priorities","Boundary","Clique","Texts"
)
val INERTIA_KEYS = listOf("Lens","Vibe","Anchors")
/** Friendly French labels for the field keys. */
val FIELD_LABELS = mapOf(
    "Looks" to "Apparence", "Style/Persona" to "Style · Persona",
    "Voice/Humor" to "Voix · Humour", "Lifestyle/Vices" to "Mode de vie · Vices",
    "Insecurity/Drive" to "Insécurité · Moteur", "Priorities" to "Priorités",
    "Boundary" to "Limites", "Clique" to "Cercle social", "Texts" to "Messages / SMS",
    "Lens" to "Regard porté", "Vibe" to "Ambiance", "Anchors" to "Ancrages",
    "Facets" to "Facettes", "Lives" to "Logement"
)

data class Location(
    val id: String = UUID.randomUUID().toString(), val campaignId: String,
    val name: String, val type: String = "", val description: String = "",
    val atmosphere: String = "", val notableFeatures: String = "",
    val linkedNpcs: String = "", val secrets: String = "", val tags: String = "",
    val labelIds: List<String> = emptyList(),
    val linkedNpcIds: List<String> = emptyList(),   // structured links → NPC fiches
    val photoUris: List<String> = emptyList()
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

    fun load(): List<Campaign> = p.getString(KEY, null)?.let { raw ->
        runCatching {
            val a = JSONArray(raw)
            buildList { for (i in 0 until a.length()) add(a.getJSONObject(i).toCampaign()) }
        }.getOrNull()
    } ?: starter()

    suspend fun save(list: List<Campaign>) = withContext(Dispatchers.IO) {
        p.edit().putString(KEY, JSONArray().also { a -> list.forEach { a.put(it.toJson()) } }.toString()).commit()
    }

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
        labels = buildList { for (i in 0 until labArr.length()) add(labArr.getJSONObject(i).toLabel()) },
        npcs = (optJSONArray("npcs") ?: JSONArray()).let { a ->
            buildList { for (i in 0 until a.length()) add(a.getJSONObject(i).toNpc(id)) }
        },
        locations = (optJSONArray("locations") ?: JSONArray()).let { a ->
            buildList { for (i in 0 until a.length()) add(a.getJSONObject(i).toLoc(id)) }
        },
        journal = (optJSONArray("journal") ?: JSONArray()).let { a ->
            buildList { for (i in 0 until a.length()) add(a.getJSONObject(i).toJournal()) }
        }
    )
}

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
    name = optString("name",""), role = optString("role",""),
    shortCard = optString("shortCard",""), fullCard = optString("fullCard",""),
    relationships = optString("relationships",""), secrets = optString("secrets",""),
    sceneHistory = optString("sceneHistory",""), tags = optString("tags",""),
    dna = (optJSONObject("dna") ?: JSONObject()).let { o ->
        buildMap { o.keys().forEach { k -> put(k, o.optString(k,"")) } }
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
    labelIds = (optJSONArray("labelIds") ?: JSONArray()).let { a ->
        buildList { for (i in 0 until a.length()) add(a.getString(i)) }
    },
    photoUris = (optJSONArray("photoUris") ?: JSONArray()).let { a ->
        buildList { for (i in 0 until a.length()) add(a.getString(i)) }
    }
)

private fun Campaign.toJson() = JSONObject().apply {
    put("id",id); put("name",name); put("description",description)
    put("labels", JSONArray().also { a -> labels.forEach { a.put(it.toJson()) } })
    put("npcs",   JSONArray().also { a -> npcs.forEach     { a.put(it.toJson()) } })
    put("locations", JSONArray().also { a -> locations.forEach { a.put(it.toJson()) } })
    put("journal", JSONArray().also { a -> journal.forEach { a.put(it.toJson()) } })
}

private fun JournalEntry.toJson() = JSONObject().apply {
    put("id",id); put("dateMillis",dateMillis); put("title",title); put("text",text)
}

private fun CampaignLabel.toJson() = JSONObject().apply {
    put("id",id); put("name",name); put("colorIndex",colorIndex)
}

private fun Npc.toJson() = JSONObject().apply {
    put("id",id); put("campaignId",campaignId); put("name",name); put("role",role)
    put("shortCard",shortCard); put("fullCard",fullCard)
    put("relationships",relationships); put("secrets",secrets)
    put("sceneHistory",sceneHistory); put("tags",tags)
    put("dna", JSONObject().also { o -> dna.forEach { (k,v) -> o.put(k,v) } })
    put("labelIds",  JSONArray().also { a -> labelIds.forEach  { a.put(it) } })
    put("photoUris", JSONArray().also { a -> photoUris.forEach { a.put(it) } })
}

private fun Location.toJson() = JSONObject().apply {
    put("id",id); put("campaignId",campaignId); put("name",name); put("type",type)
    put("description",description); put("atmosphere",atmosphere)
    put("notableFeatures",notableFeatures); put("linkedNpcs",linkedNpcs)
    put("linkedNpcIds", JSONArray().also { a -> linkedNpcIds.forEach { a.put(it) } })
    put("secrets",secrets); put("tags",tags)
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
            MaterialTheme(colorScheme = scheme(), typography = Typo) {
                Box(Modifier.fillMaxSize().background(L1).drawBehind {
                    // Subtle warm vignette — corners sink into the void
                    drawRect(Brush.radialGradient(
                        listOf(Color.Transparent, L0.copy(alpha = 0.55f)),
                        center = Offset(size.width/2f, size.height*0.38f),
                        radius = size.maxDimension * 0.72f))
                }) {
                    CompanionApp()
                    NoticeHost(Modifier.align(Alignment.BottomCenter))
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
    fun persist(next: List<Campaign>) { campaigns = next; scope.launch { store.save(next) } }

    // System back gesture navigates up instead of closing the app
    BackHandler(enabled = screen != Screen.Campaigns) {
        screen = when (val s = screen) {
            is Screen.NpcDetail -> Screen.Campaign(s.campaignId)
            is Screen.LocDetail -> Screen.Campaign(s.campaignId)
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

    AnimatedContent(targetState = screen, transitionSpec = {
        (fadeIn(tween(200)) + slideInHorizontally(tween(240, easing = EaseOutCubic)) { it / 10 })
            .togetherWith(fadeOut(tween(160)) + slideOutHorizontally(tween(200)) { -it / 10 })
    }, label = "nav") { s ->
        when (s) {
            is Screen.Campaigns -> CampaignListScreen(campaigns,
                onOpen   = { screen = Screen.Campaign(it.id) },
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
                    onOpenNpc    = { screen = Screen.NpcDetail(c.id, it.id) },
                    onOpenLoc    = { screen = Screen.LocDetail(c.id, it.id) },
                    onAddNpc     = { n -> val x=Npc(campaignId=c.id,name=n.ifBlank{"Nouveau NPC"}); persist(campaigns.map{if(it.id==c.id)it.copy(npcs=it.npcs+x)else it}) },
                    onDelNpc     = { npc -> npc.photoUris.forEach{PhotoStore.delete(it)}; persist(campaigns.map{if(it.id==c.id)it.copy(npcs=it.npcs.filter{n->n.id!=npc.id})else it}) },
                    onAddLoc     = { n -> val x=Location(campaignId=c.id,name=n.ifBlank{"Nouveau lieu"}); persist(campaigns.map{if(it.id==c.id)it.copy(locations=it.locations+x)else it}) },
                    onDelLoc     = { loc -> loc.photoUris.forEach{PhotoStore.delete(it)}; persist(campaigns.map{if(it.id==c.id)it.copy(locations=it.locations.filter{l->l.id!=loc.id})else it}) },
                    onUpdateLabels = { labels -> persist(campaigns.map{if(it.id==c.id)it.copy(labels=labels)else it}) },
                    onImportNpc     = { npc ->
                        val x = npc.copy(campaignId = c.id)
                        persist(campaigns.map{if(it.id==c.id)it.copy(npcs=it.npcs+x)else it})
                        screen = Screen.NpcDetail(c.id, x.id)
                    },
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
                    onOpenLoc = { loc -> screen = Screen.LocDetail(c.id, loc.id) },
                    onBack    = { screen = Screen.Campaign(s.campaignId) },
                    onSave    = { e -> persist(campaigns.map{cc->if(cc.id==e.campaignId)cc.copy(npcs=cc.npcs.map{if(it.id==e.id)e else it})else cc}); toast(ctx,"Sauvegardé") },
                    onAddPhoto = { uri -> addPhoto(uri) { p -> val u=npc.copy(photoUris=npc.photoUris+p); persist(campaigns.map{cc->if(cc.id==u.campaignId)cc.copy(npcs=cc.npcs.map{if(it.id==u.id)u else it})else cc}) } },
                    onDelPhoto = { path -> PhotoStore.delete(path); val u=npc.copy(photoUris=npc.photoUris.filter{it!=path}); persist(campaigns.map{cc->if(cc.id==u.campaignId)cc.copy(npcs=cc.npcs.map{if(it.id==u.id)u else it})else cc}) }
                )
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

/** In-app themed notice — replaces system toasts for an immersive codex feel. */
object Notice { var current by mutableStateOf<String?>(null) }
@Suppress("UNUSED_PARAMETER")
private fun toast(ctx: Context, msg: String) { Notice.current = msg }

@Composable
fun NoticeHost(modifier: Modifier = Modifier) {
    val msg = Notice.current
    LaunchedEffect(msg) { if (msg != null) { delay(2200); Notice.current = null } }
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
            Text(shown, style = Typo.bodyMedium.copy(color = T1))
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
    Color(0xFF6B4A2A), // bronze
    Color(0xFF4A5A3A), // moss
    Color(0xFF3A5260), // slate-blue
    Color(0xFF5A3A42), // wine
    Color(0xFF50466A), // amethyst
    Color(0xFF2E5A52), // pine-teal
    Color(0xFF6A5230), // ochre
    Color(0xFF455060), // steel
)
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
@Composable
fun Modifier.pressable(onClickLabel: String? = null, onClick: () -> Unit): Modifier {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.975f else 1f, tween(110), label = "press")
    return this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .clickable(interactionSource = interaction, indication = LocalIndication.current,
            onClickLabel = onClickLabel, onClick = onClick)
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
            .padding(horizontal=if(small) 7.dp else 10.dp, vertical=if(small) 3.dp else 5.dp),
        verticalAlignment=Alignment.CenterVertically,
        horizontalArrangement=Arrangement.spacedBy(4.dp)
    ) {
        if (selected && onClick != null)
            Icon(Icons.Default.Check, null, Modifier.size(if(small) 10.dp else 12.dp), tint = color)
        else
            Box(Modifier.size(6.dp).clip(CircleShape).background(color))
        Text(label.name,
            style=if(small) Typo.labelSmall.copy(color=color) else Typo.labelMedium.copy(color=color))
    }
}

// ── Label manager dialog ──────────────────────────────────────────────────────

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
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(LABEL_COLORS.size) { i ->
                        val c = LABEL_COLORS[i]
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
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(defaultExpanded) }
    val haptic = LocalHapticFeedback.current
    Column(Modifier.fillMaxWidth()) {
        GoldLine(alpha=0.18f)
        Row(Modifier.fillMaxWidth()
            .semantics { stateDescription = if (expanded) "Section dépliée" else "Section repliée" }
            .clickable(onClickLabel = if (expanded) "Replier" else "Déplier"){
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); expanded=!expanded
            }
            .padding(start=20.dp,end=16.dp,top=14.dp,bottom=if(expanded||summary.isBlank())4.dp else 10.dp),
            verticalAlignment=Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(14.dp), tint=accent)
            Spacer(Modifier.width(9.dp))
            Text(title.uppercase(), style=Typo.labelLarge.copy(color=accent))
            Spacer(Modifier.weight(1f))
            Icon(if(expanded)Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                null, Modifier.size(18.dp), tint=accent.copy(alpha=0.45f))
        }
        if (!expanded && summary.isNotBlank()) {
            Text(summary, style=Typo.bodySmall.copy(color=T3, fontStyle=FontStyle.Italic),
                maxLines=2, overflow=TextOverflow.Ellipsis,
                modifier=Modifier.padding(start=43.dp,end=20.dp,bottom=10.dp))
        }
        AnimatedVisibility(expanded,
            enter=fadeIn(tween(180))+expandVertically(tween(200,easing=EaseOutCubic)),
            exit=fadeOut(tween(140))+shrinkVertically(tween(160))) {
            Column(Modifier.fillMaxWidth()) { content() }
        }
    }
}

@Composable
fun LoreField(
    label: String, value: String, placeholder: String = "",
    multi: Boolean = false, accent: Color = GOLD, onChange: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(horizontal=20.dp, vertical=7.dp)) {
        Text(label.uppercase(), style=Typo.labelSmall.copy(color=accent.copy(alpha=0.55f)))
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

@Composable
fun EntityHero(
    name: String, subtitle: String, firstPhoto: String?,
    accent: Color, onBack: ()->Unit, action: @Composable ()->Unit = {}
) {
    Box(Modifier.fillMaxWidth().height(310.dp)) {
        if (firstPhoto != null) {
            val model = ImageRequest.Builder(LocalContext.current).data(File(firstPhoto)).crossfade(true).build()
            AsyncImage(
                model=model,
                contentDescription=null,
                contentScale=ContentScale.Crop,
                modifier=Modifier.fillMaxSize().blur(18.dp).alpha(0.42f)
            )
            Box(Modifier.fillMaxSize().background(Brush.radialGradient(
                listOf(Color.Transparent, L0.copy(alpha=0.72f)), radius = 760f
            )))
            AsyncImage(
                model=model,
                contentDescription=null,
                contentScale=ContentScale.Fit,
                modifier=Modifier.fillMaxSize().padding(top=30.dp, bottom=70.dp, start=18.dp, end=18.dp)
            )
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
            Brush.verticalGradient(listOf(L0.copy(alpha=0.55f),Color.Transparent,Color.Transparent,
                L0.copy(alpha=0.75f),L1.copy(alpha=0.97f)))))
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
            Box(Modifier.clip(RoundedCornerShape(999.dp)).background(L0.copy(alpha=0.55f))){ action() }
        }
        Column(Modifier.align(Alignment.BottomStart).padding(horizontal=22.dp,bottom=20.dp)) {
            if(subtitle.isNotBlank()) Text(subtitle.uppercase(),
                style=Typo.labelLarge.copy(color=accent,letterSpacing=2.sp),
                modifier=Modifier.padding(bottom=5.dp))
            Text(name,style=Typo.headlineLarge.copy(color=T1,shadow=Shadow(L0,Offset(0f,2f),8f)),
                modifier=Modifier.semantics{heading()})
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
                    disabledContainerColor=L4,disabledContentColor=T4),
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
        items(uris) { path ->
            // 3:2 tile — Fit so the whole photo is visible
            Box(Modifier.width(88.dp).height(118.dp).clip(RoundedCornerShape(14.dp))
                .background(L0)) {
                AsyncImage(
                    model=ImageRequest.Builder(LocalContext.current).data(File(path)).crossfade(true).build(),
                    contentDescription=null,
                    contentScale=ContentScale.Fit,   // ← no crop, whole photo visible
                    modifier=Modifier.fillMaxSize()
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
    onOpenNpcById: (String,String)->Unit, onOpenLocById: (String,String)->Unit,
    onAdd: (String)->Unit,
    onEdit: (Campaign)->Unit, onDelete: (Campaign)->Unit,
    onExport: ()->Unit, onImport: ()->Unit
) {
    var showAdd     by remember { mutableStateOf(false) }
    var showOptions by remember { mutableStateOf(false) }
    var query       by remember { mutableStateOf("") }
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
            TextButton(onClick={onImport();showOptions=false},modifier=Modifier.fillMaxWidth()){
                Icon(Icons.Default.FileUpload,null,Modifier.size(16.dp),tint=GOLD)
                Spacer(Modifier.width(10.dp)); Text("Importer",style=Typo.bodyMedium.copy(color=T1))}
            TextButton(onClick={onExport();showOptions=false},modifier=Modifier.fillMaxWidth()){
                Icon(Icons.Default.FileDownload,null,Modifier.size(16.dp),tint=GOLD)
                Spacer(Modifier.width(10.dp)); Text("Exporter",style=Typo.bodyMedium.copy(color=T1))}}},
        confirmButton={TextButton(onClick={showOptions=false}){Text("Fermer")}})

    Scaffold(containerColor=Color.Transparent,
        floatingActionButton={
            ExtendedFloatingActionButton(onClick={showAdd=true},
                containerColor=GOLD,contentColor=L0,
                modifier=Modifier.navigationBarsPadding(),
                icon={Icon(Icons.Default.Add,null,Modifier.size(20.dp))},
                text={Text("Campagne",style=Typo.labelLarge.copy(fontFamily=CinzelFamily,fontSize=13.sp))},
                shape=RoundedCornerShape(16.dp))
        }) { pads ->
        LazyColumn(Modifier.fillMaxSize(),
            contentPadding=PaddingValues(bottom=pads.calculateBottomPadding()+16.dp)) {
            item {
                Box(Modifier.fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(L3,L2,L1)))
                    .statusBarsPadding()
                    .padding(start=22.dp,end=16.dp,top=20.dp,bottom=18.dp)) {
                    Column {
                        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Top) {
                            Column(Modifier.weight(1f)) {
                                Text("AIREALM",style=Typo.labelMedium.copy(color=GOLD_MID,letterSpacing=4.sp))
                                Spacer(Modifier.height(2.dp))
                                Text("Codex",style=Typo.displayMedium.copy(color=GOLD_HI))
                        // decorative gold rule
                        Row(Modifier.padding(top=6.dp).width(150.dp), verticalAlignment=Alignment.CenterVertically) {
                            Box(Modifier.weight(1f).height(1.dp).background(
                                Brush.horizontalGradient(listOf(GOLD, GOLD.copy(alpha=0f)))))
                            Text("◆", style=TextStyle(fontSize=7.sp, color=GOLD.copy(alpha=0.7f)),
                                modifier=Modifier.padding(horizontal=6.dp))
                            Box(Modifier.weight(1f).height(1.dp).background(
                                Brush.horizontalGradient(listOf(GOLD.copy(alpha=0.3f), GOLD.copy(alpha=0f)))))
                        }
                            }
                            IconButton(onClick={showOptions=true}){
                                Icon(Icons.Default.MoreVert,"Options",tint=T3)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically) {
                            StatItem(campaigns.size.toString(),"Campagnes",GOLD,Modifier.weight(1f))
                            StatDivider()
                            StatItem(campaigns.sumOf{it.npcs.size}.toString(),"Personnages",GOLD_HI,Modifier.weight(1f))
                            StatDivider()
                            StatItem(campaigns.sumOf{it.locations.size}.toString(),"Lieux",TEAL,Modifier.weight(1f))
                        }
                    }
                }
                GoldLine(alpha=0.25f)
            }
            if (campaigns.isEmpty()) item {
                Box(Modifier.fillMaxWidth().padding(64.dp),contentAlignment=Alignment.Center){
                    Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)){
                        Icon(Icons.Outlined.MenuBook,null,Modifier.size(44.dp),tint=T4)
                        Text("Aucune campagne",style=Typo.headlineSmall.copy(color=T3))
                        Text("Appuie sur + pour commencer",style=Typo.bodySmall.copy(color=T4))
                    }
                }
            }
            // ── Global search field ──────────────────────────────────────────
            item {
                SearchField(query, { query = it },
                    Modifier.padding(horizontal=14.dp).padding(top=10.dp, bottom=4.dp))
            }

            if (query.isBlank()) {
                items(campaigns,key={it.id}){c->
                    Box(Modifier.animateItem()) {
                        CampaignCard(c,{onOpen(c)},{editTarget=c},{delTarget=c})
                    }
                }
            } else {
                val hits = buildSearchHits(campaigns, query)
                if (hits.isEmpty()) item {
                    Box(Modifier.fillMaxWidth().padding(40.dp),contentAlignment=Alignment.Center){
                        Text("Aucun résultat pour « $query »",
                            style=Typo.bodyMedium.copy(color=T3,fontStyle=FontStyle.Italic))
                    }
                }
                items(hits, key={it.key}){ hit ->
                    SearchResultRow(hit) {
                        when (hit.kind) {
                            HitKind.NPC      -> onOpenNpcById(hit.campaignId, hit.entityId)
                            HitKind.LOCATION -> onOpenLocById(hit.campaignId, hit.entityId)
                            HitKind.JOURNAL  -> onOpen(campaigns.first{it.id==hit.campaignId})
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
    Box(Modifier.fillMaxWidth().padding(horizontal=14.dp,vertical=7.dp)
        .shadow(10.dp, RoundedCornerShape(16.dp), ambientColor=seal, spotColor=L0)
        .clip(RoundedCornerShape(16.dp)).pressable(onClickLabel="Ouvrir la campagne"){onOpen()}) {
        Box(Modifier.fillMaxWidth().height(78.dp).background(nameGrad(c.name))) {
            EngravedMonogram(c.name.take(1).uppercase(), seal, 92.sp,
                Modifier.align(Alignment.CenterEnd).padding(end=14.dp), baseAlpha=0.18f)
            // top light edge
            Box(Modifier.fillMaxWidth().height(1.dp).align(Alignment.TopCenter).background(
                Brush.horizontalGradient(listOf(Color.Transparent, seal.copy(alpha=0.5f), Color.Transparent))))
            Box(Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color.Transparent,L2.copy(alpha=0.85f)))))
        }
        Column(Modifier.fillMaxWidth().padding(top=58.dp)
            .clip(RoundedCornerShape(bottomStart=16.dp,bottomEnd=16.dp))
            .background(L2).border(1.dp,L5,RoundedCornerShape(bottomStart=16.dp,bottomEnd=16.dp))) {
            Row(Modifier.fillMaxWidth()) {
                Box(Modifier.width(3.dp).height(56.dp).background(
                    Brush.verticalGradient(listOf(GOLD.copy(alpha=0f),GOLD.copy(alpha=0.7f),GOLD.copy(alpha=0f)))))
                Column(Modifier.weight(1f).padding(start=12.dp,end=8.dp,top=10.dp,bottom=10.dp)) {
                    Text(c.name,style=Typo.titleLarge.copy(color=T1),maxLines=1,overflow=TextOverflow.Ellipsis)
                    if(c.description.isNotBlank())
                        Text(c.description,style=Typo.bodySmall.copy(color=T3),maxLines=1,overflow=TextOverflow.Ellipsis,modifier=Modifier.padding(top=2.dp))
                    Row(Modifier.padding(top=6.dp),horizontalArrangement=Arrangement.spacedBy(10.dp)){
                        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)){
                            Icon(Icons.Default.Person,null,Modifier.size(11.dp),tint=GOLD_MID)
                            Text("${c.npcs.size} NPC",style=Typo.labelSmall.copy(color=GOLD_MID))
                        }
                        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)){
                            Icon(Icons.Default.Place,null,Modifier.size(11.dp),tint=TEAL)
                            Text("${c.locations.size} lieu",style=Typo.labelSmall.copy(color=TEAL))
                        }
                        if(c.labels.isNotEmpty()){
                            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)){
                                Icon(Icons.Default.Label,null,Modifier.size(11.dp),tint=T3)
                                Text("${c.labels.size} label${if(c.labels.size>1)"s" else ""}",style=Typo.labelSmall.copy(color=T3))
                            }
                        }
                    }
                }
                Column(horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.padding(end=4.dp,top=4.dp)){
                    IconButton(onClick=onEdit,Modifier.size(44.dp)){Icon(Icons.Default.Edit,"Modifier la campagne",Modifier.size(18.dp),tint=T3)}
                    IconButton(onClick=onDelete,Modifier.size(44.dp)){Icon(Icons.Default.Delete,"Supprimer la campagne",Modifier.size(18.dp),tint=CRIM.copy(alpha=0.8f))}
                }
            }
        }
    }
}

@Composable
fun CampaignEditDialog(c: Campaign, onDismiss:()->Unit, onSave:(Campaign)->Unit) {
    var name by remember{mutableStateOf(c.name)}
    var desc by remember{mutableStateOf(c.description)}
    AlertDialog(onDismissRequest=onDismiss,containerColor=L3,
        title={Text("Modifier",style=Typo.headlineMedium)},
        text={Column(verticalArrangement=Arrangement.spacedBy(10.dp)){
            OutlinedTextField(value=name,onValueChange={name=it},label={Text("Nom")},singleLine=true,modifier=Modifier.fillMaxWidth(),
                colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=GOLD,cursorColor=GOLD,focusedContainerColor=L4,unfocusedContainerColor=L4))
            OutlinedTextField(value=desc,onValueChange={desc=it},label={Text("Description")},minLines=2,modifier=Modifier.fillMaxWidth(),
                colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=GOLD,cursorColor=GOLD,focusedContainerColor=L4,unfocusedContainerColor=L4))
        }},
        confirmButton={Button(onClick={onSave(c.copy(name=name.ifBlank{c.name},description=desc))},
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
    onOpenNpc:(Npc)->Unit, onOpenLoc:(Location)->Unit,
    onAddNpc:(String)->Unit, onDelNpc:(Npc)->Unit,
    onAddLoc:(String)->Unit, onDelLoc:(Location)->Unit,
    onUpdateLabels:(List<CampaignLabel>)->Unit,
    onImportNpc:(Npc)->Unit,
    onAddJournal:(JournalEntry)->Unit, onUpdateJournal:(JournalEntry)->Unit, onDelJournal:(JournalEntry)->Unit
) {
    val ctx = LocalContext.current
    var tab          by remember{mutableIntStateOf(0)}
    var showAddNpc     by remember{mutableStateOf(false)}
    var showAddLoc     by remember{mutableStateOf(false)}
    var showAddJournal by remember{mutableStateOf(false)}
    var showImport     by remember{mutableStateOf(false)}
    var editJournal    by remember{mutableStateOf<JournalEntry?>(null)}
    var showLabels   by remember{mutableStateOf(false)}
    var activeFilter by remember{mutableStateOf<String?>(null)}  // null = all

    if(showAddNpc) AddSheet("Nouveau personnage","Nom…",GOLD,{showAddNpc=false}){onAddNpc(it)}
    if(showAddLoc) AddSheet("Nouveau lieu","Nom…",TEAL,{showAddLoc=false}){onAddLoc(it)}
    if(showAddJournal) JournalSheet(null,{showAddJournal=false}){onAddJournal(it)}
    if(showImport) ImportSheet({showImport=false}){onImportNpc(it)}
    editJournal?.let{ entry -> JournalSheet(entry,{editJournal=null}){onUpdateJournal(it)} }
    if(showLabels) LabelManagerDialog(c.labels,{showLabels=false},onUpdateLabels)

    Scaffold(containerColor=Color.Transparent,
        floatingActionButton={
            ExtendedFloatingActionButton(
                onClick={when(tab){0->showAddNpc=true;1->showAddLoc=true;else->showAddJournal=true}},
                containerColor=when(tab){0->GOLD;1->TEAL;else->GOLD_MID},contentColor=L0,
                modifier=Modifier.navigationBarsPadding(),
                icon={Icon(when(tab){0->Icons.Default.PersonAdd;1->Icons.Default.AddLocation;else->Icons.Default.HistoryEdu},null,Modifier.size(20.dp))},
                text={Text(when(tab){0->"Personnage";1->"Lieu";else->"Entrée"},style=Typo.labelLarge.copy(fontFamily=CinzelFamily,fontSize=13.sp))},
                shape=RoundedCornerShape(16.dp))
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
                        listOf("Personnages" to c.npcs.size,"Lieux" to c.locations.size,"Journal" to c.journal.size)
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
                val filteredNpcs  = if(activeFilter==null) c.npcs else c.npcs.filter{activeFilter in it.labelIds}
                val filteredLocs  = if(activeFilter==null) c.locations else c.locations.filter{activeFilter in it.labelIds}
                when(tab){
                    0 -> EntityList(filteredNpcs.sortedBy{it.name.lowercase()},
                        emptyTitle=if(activeFilter!=null)"Aucun résultat" else "Aucun personnage",
                        emptyHint=if(activeFilter!=null)"Aucun NPC avec ce label" else "Appuie sur + pour ajouter",
                        emptyIcon=Icons.Outlined.Person, accent=GOLD,
                        getKey={it.id},getName={it.name},getSub={it.role},
                        getPreview={it.shortCard},getTags={it.tags},
                        getLabels={ids->c.labels.filter{l->l.id in ids}},
                        getLabelIds={it.labelIds},
                        getPhoto={it.photoUris.firstOrNull()},
                        onOpen={onOpenNpc(it)},onDel={onDelNpc(it)},
                        bottomPad=pads.calculateBottomPadding())
                    1 -> EntityList(filteredLocs.sortedBy{it.name.lowercase()},
                        emptyTitle=if(activeFilter!=null)"Aucun résultat" else "Aucun lieu",
                        emptyHint=if(activeFilter!=null)"Aucun lieu avec ce label" else "Appuie sur + pour ajouter",
                        emptyIcon=Icons.Outlined.Place, accent=TEAL,
                        getKey={it.id},getName={it.name},getSub={it.type},
                        getPreview={it.description},getTags={it.tags},
                        getLabels={ids->c.labels.filter{l->l.id in ids}},
                        getLabelIds={it.labelIds},
                        getPhoto={it.photoUris.firstOrNull()},
                        onOpen={onOpenLoc(it)},onDel={onDelLoc(it)},
                        bottomPad=pads.calculateBottomPadding())
                    2 -> JournalTab(c.journal,
                        onEdit={editJournal=it}, onDel=onDelJournal,
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
    getPhoto:(T)->String?, onOpen:(T)->Unit, onDel:(T)->Unit, bottomPad: Dp
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
        LazyColumn(Modifier.fillMaxSize(),
            contentPadding=PaddingValues(top=8.dp,bottom=bottomPad+88.dp),
            verticalArrangement=Arrangement.spacedBy(8.dp)){
            items(items,key={getKey(it)}){ item ->
                val labels = getLabels(getLabelIds(item))
                Box(Modifier.animateItem()) {
                    EntityCard(getName(item),getSub(item),getPreview(item),getTags(item),
                        labels,getPhoto(item),accent,{onOpen(item)},{delTarget=item})
                }
            }
        }
    }
}

@Composable
fun EntityCard(
    name: String, sub: String, preview: String, tags: String,
    labels: List<CampaignLabel>, photoUri: String?,
    accent: Color, onClick:()->Unit, onDel:()->Unit
) {
    Box(Modifier.fillMaxWidth().padding(horizontal=14.dp, vertical=2.dp)
        .shadow(8.dp, RoundedCornerShape(14.dp), ambientColor=sealHue(name), spotColor=L0)
        .clip(RoundedCornerShape(14.dp)).pressable(onClickLabel="Ouvrir"){onClick()}) {
        Column {
            // Banner
            Box(Modifier.fillMaxWidth().height(118.dp)) {
                if(photoUri!=null){
                    val model = ImageRequest.Builder(LocalContext.current).data(File(photoUri)).crossfade(true).build()
                    AsyncImage(model=model, contentDescription=null, contentScale=ContentScale.Crop,
                        modifier=Modifier.fillMaxSize().blur(12.dp).alpha(0.28f))
                    Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(L0.copy(alpha=0.60f), Color.Transparent, L0.copy(alpha=0.72f)))))
                    AsyncImage(model=model, contentDescription=null, contentScale=ContentScale.Fit,
                        modifier=Modifier.fillMaxSize().padding(vertical=6.dp))
                } else {
                    val seal = sealHue(name)
                    Box(Modifier.fillMaxSize().background(nameGrad(name))){
                        Box(Modifier.fillMaxWidth().height(1.dp).align(Alignment.TopCenter).background(
                            Brush.horizontalGradient(listOf(Color.Transparent, seal.copy(alpha=0.5f), Color.Transparent))))
                        EngravedMonogram(name.take(1).uppercase(), seal, 96.sp,
                            Modifier.align(Alignment.CenterEnd).padding(end=16.dp), baseAlpha=0.2f)
                    }
                }
                Box(Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color.Transparent,L2.copy(alpha=0.85f)))))
            }
            // Body
            Row(Modifier.fillMaxWidth().background(L2)
                .border(1.dp,L5,RoundedCornerShape(bottomStart=14.dp,bottomEnd=14.dp))
                .clip(RoundedCornerShape(bottomStart=14.dp,bottomEnd=14.dp)),
                verticalAlignment=Alignment.Top){
                Box(Modifier.width(3.dp).height(60.dp).background(
                    Brush.verticalGradient(listOf(accent.copy(alpha=0f),accent.copy(alpha=0.7f),accent.copy(alpha=0f)))))
                Column(Modifier.weight(1f).padding(start=12.dp,top=10.dp,bottom=12.dp,end=4.dp)){
                    Text(name,style=Typo.titleLarge.copy(color=T1),maxLines=1,overflow=TextOverflow.Ellipsis)
                    if(sub.isNotBlank()) Text(sub.uppercase(),style=Typo.labelSmall.copy(color=accent,letterSpacing=1.5.sp),modifier=Modifier.padding(top=2.dp))
                    if(preview.isNotBlank()) Text(preview,style=Typo.bodySmall.copy(color=T2),maxLines=2,overflow=TextOverflow.Ellipsis,modifier=Modifier.padding(top=5.dp))
                    // Labels row
                    if(labels.isNotEmpty()){
                        LazyRow(Modifier.padding(top=7.dp),horizontalArrangement=Arrangement.spacedBy(5.dp)){
                            items(labels){lbl-> LabelChip(lbl,small=true)}
                        }
                    } else if(tags.isNotBlank()){
                        // Fallback to text tags when no structured labels
                        Text(tags,style=Typo.labelSmall.copy(color=T3),modifier=Modifier.padding(top=6.dp))
                    }
                }
                IconButton(onClick=onDel,Modifier.padding(top=2.dp).size(44.dp)){
                    Icon(Icons.Default.Delete,"Supprimer",Modifier.size(18.dp),tint=CRIM.copy(alpha=0.7f))
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

    Column(Modifier.fillMaxSize().imePadding()) {
        EntityHero(name=e.name.ifBlank{"NPC"},subtitle=e.role,
            firstPhoto=npc.photoUris.firstOrNull(),accent=GOLD,onBack=onBack,
            action={IconButton(onClick={
                val clip=ClipData.newPlainText("NPC",export())
                (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                toast(ctx,"Résumé copié")
            }){Icon(Icons.Default.ContentCopy,"Copier le résumé",Modifier.size(17.dp),tint=T1)}})

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            PhotoStrip(npc.photoUris,{photoL.launch(arrayOf("image/*"))},onDelPhoto)
            LoreSection("Identité",Icons.Default.Person,
                summary=e.name.ifBlank{"—"}+if(e.role.isNotBlank())" · ${e.role}"else""){
                LoreField("Nom",e.name,"Nom du personnage"){e=e.copy(name=it)}
                LoreField("Rôle · Faction · Statut",e.role,"ex. Garde royal, faction Ombre"){e=e.copy(role=it)}
                LoreField("Tags libres",e.tags,"ex. allié, majeur, antagoniste"){e=e.copy(tags=it)}
                // Label picker
                if(campaignLabels.isNotEmpty()){
                    Spacer(Modifier.height(4.dp))
                    LabelPicker(campaignLabels,e.labelIds){id->
                        e=e.copy(labelIds=if(id in e.labelIds)e.labelIds-id else e.labelIds+id)}
                }
            }
            LoreSection("Cartes Airealm",Icons.Default.Description,
                summary=e.shortCard.take(80).ifBlank{"Non renseigné"}){
                LoreField("Version courte",e.shortCard,"Résumé compact à coller dans Airealm",multi=true){e=e.copy(shortCard=it)}
                LoreField("Version complète",e.fullCard,"Description détaillée — souvent longue",multi=true){e=e.copy(fullCard=it)}
            }
            // ── Structured DNA fields, one line each ───────────────────────
            LoreSection("ADN du personnage",Icons.Default.Fingerprint,
                summary=dnaSummary(e.dna, DNA_KEYS + "Lives")){
                fun field(key: String, multi: Boolean = false) {
                    LoreField(FIELD_LABELS[key] ?: key, e.dna[key] ?: "",
                        "—", multi=multi){ v ->
                        e = e.copy(dna = e.dna.toMutableMap().apply {
                            if (v.isBlank()) remove(key) else put(key, v) })
                    }
                }
                field("Lives")
                DNA_KEYS.forEach { field(it, multi = it in listOf("Looks","Style/Persona","Voice/Humor","Texts")) }
            }
            // ── INERTIA — relation lens fields ─────────────────────────────
            LoreSection("Inertie · Relation",Icons.Default.Sync, accent=TEAL,
                summary=dnaSummary(e.dna, INERTIA_KEYS + "Facets")){
                fun ifield(key: String, multi: Boolean = false) {
                    LoreField(FIELD_LABELS[key] ?: key, e.dna[key] ?: "",
                        "—", multi=multi, accent=TEAL){ v ->
                        e = e.copy(dna = e.dna.toMutableMap().apply {
                            if (v.isBlank()) remove(key) else put(key, v) })
                    }
                }
                INERTIA_KEYS.forEach { ifield(it, multi = it == "Anchors") }
                ifield("Facets", multi = true)
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
        SaveBar("Sauver le personnage", enabled = e != npc){onSave(e.copy(photoUris = npc.photoUris))}
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
        EntityHero(name=e.name.ifBlank{"Lieu"},subtitle=e.type,
            firstPhoto=loc.photoUris.firstOrNull(),accent=TEAL,onBack=onBack,
            action={IconButton(onClick={
                val clip=ClipData.newPlainText("Lieu",export())
                (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                toast(ctx,"Résumé copié")
            }){Icon(Icons.Default.ContentCopy,"Copier le résumé",Modifier.size(17.dp),tint=T1)}})

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
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

enum class HitKind { NPC, LOCATION, JOURNAL }

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
        for (n in c.npcs) if (listOf(n.name,n.role,n.tags,n.shortCard,n.fullCard,n.relationships,n.sceneHistory).any{it.hit()})
            hits += SearchHit(HitKind.NPC, c.id, c.name, n.id, n.name, n.role,
                snippetOf(n.shortCard,n.fullCard,n.relationships,n.sceneHistory,n.tags))
        for (l in c.locations) if (listOf(l.name,l.type,l.tags,l.description,l.atmosphere,l.notableFeatures).any{it.hit()})
            hits += SearchHit(HitKind.LOCATION, c.id, c.name, l.id, l.name, l.type,
                snippetOf(l.description,l.atmosphere,l.notableFeatures,l.tags))
        for (j in c.journal) if (j.title.hit() || j.text.hit())
            hits += SearchHit(HitKind.JOURNAL, c.id, c.name, j.id, j.title.ifBlank{"Entrée de journal"}, c.name,
                snippetOf(j.text, j.title))
    }
    return hits.take(40)
}

@Composable
fun SearchField(query: String, onChange: (String)->Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = query, onValueChange = onChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text("Rechercher partout…", color = T4,
            style = Typo.bodyMedium.copy(fontStyle = FontStyle.Italic)) },
        leadingIcon = { Icon(Icons.Default.Search, null, Modifier.size(18.dp), tint = GOLD_MID) },
        trailingIcon = {
            if (query.isNotBlank()) IconButton(onClick = { onChange("") }) {
                Icon(Icons.Default.Close, "Effacer la recherche", Modifier.size(16.dp), tint = T3)
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GOLD, unfocusedBorderColor = L5,
            focusedContainerColor = L3, unfocusedContainerColor = L2, cursorColor = GOLD),
        textStyle = Typo.bodyMedium.copy(color = T1)
    )
}

@Composable
fun SearchResultRow(hit: SearchHit, onClick: ()->Unit) {
    val (icon, accent) = when (hit.kind) {
        HitKind.NPC      -> Icons.Default.Person to GOLD
        HitKind.LOCATION -> Icons.Default.Place to TEAL
        HitKind.JOURNAL  -> Icons.Default.HistoryEdu to GOLD_MID
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
        contentPadding=PaddingValues(top=10.dp, bottom=bottomPad+88.dp),
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
                    disabledContainerColor=L4,disabledContentColor=T4)) {
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
fun parseAirealmCard(name: String, raw: String): Npc {
    val lines = raw.lines().map { it.trim() }.filter { it.isNotBlank() }
    fun grab(key: String): String = lines.firstOrNull {
        it.startsWith("$key:", ignoreCase = true) ||
        it.substringAfter(' ', it).startsWith("$key:", ignoreCase = true)  // tolerate emoji prefix
    }?.substringAfter(":")?.trim() ?: ""

    // ── Header ────────────────────────────────────────────────────────────
    val header = lines.firstOrNull { it.startsWith("|") } ?: ""
    val parts  = header.split("|").map { it.trim() }.filter { it.isNotBlank() }
    fun part(prefix: String) = parts.firstOrNull { it.startsWith(prefix, ignoreCase = true) }
    val major  = part("Major")?.substringAfter(":")?.trim() ?: ""
    val traits = part("Traits")?.substringAfter(":")?.trim() ?: ""
    val lives  = part("Lives") ?: ""
    val ageYear = parts.filter { p ->
        listOf("Major","Traits","Lives").none { p.startsWith(it, ignoreCase = true) }
    }.joinToString(" · ")

    val role = listOf(ageYear, major).filter { it.isNotBlank() }.joinToString(" · ")

    // ── DNA + INERTIA → structured map, one entry per field ────────────────
    val dnaMap = linkedMapOf<String,String>()
    if (lives.isNotBlank()) dnaMap["Lives"] = lives
    (DNA_KEYS + INERTIA_KEYS).forEach { k -> grab(k).ifBlank { null }?.let { dnaMap[k] = it } }
    val facetLine = lines.firstOrNull { it.contains("Attraction") || it.contains("Comfort") }
    if (!facetLine.isNullOrBlank()) dnaMap["Facets"] = facetLine

    // ── INERTIA target → relationships (free text) ─────────────────────────
    val vsTarget = Regex("vs\\s+([^)]+)\\)").find(raw)?.groupValues?.get(1)?.trim() ?: ""
    val relationships = if (vsTarget.isNotBlank()) "Vis-à-vis de $vsTarget" else ""

    val tags = listOf(traits, grab("Clique")).filter { it.isNotBlank() }.joinToString(", ")
    val parsedSignals = listOf(header, dnaMap.isNotEmpty().toString().takeIf{dnaMap.isNotEmpty()} ?: "").count { it.isNotBlank() }
    return Npc(
        campaignId = "",
        name = name.trim(),
        role = role,
        shortCard = header,
        fullCard = if (dnaMap.isEmpty() && header.isBlank()) raw.trim() else "",
        relationships = relationships,
        tags = tags,
        dna = dnaMap
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportSheet(onDismiss: ()->Unit, onImport: (Npc)->Unit) {
    var name by remember { mutableStateOf("") }
    var raw  by remember { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = L3,
        dragHandle = { Box(Modifier.padding(vertical=10.dp).width(32.dp).height(3.dp)
            .clip(RoundedCornerShape(2.dp)).background(L6)) }) {
        Column(Modifier.fillMaxWidth().padding(horizontal=22.dp)
            .navigationBarsPadding().imePadding()
            .verticalScroll(rememberScrollState())) {
            Text("Importer une fiche Airealm", style = Typo.headlineMedium.copy(color = T1))
            Text("Colle une NPC Card — les champs se remplissent automatiquement.",
                style = Typo.bodySmall.copy(color = T3), modifier = Modifier.padding(top = 4.dp))
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
                onImport(parseAirealmCard(name, raw)); onDismiss()
            }, enabled = name.isNotBlank() && raw.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GOLD, contentColor = L0,
                    disabledContainerColor = L4, disabledContentColor = T4)) {
                Icon(Icons.Default.Download, null, Modifier.size(17.dp))
                Spacer(Modifier.width(8.dp))
                Text("Importer la fiche", style = Typo.labelLarge.copy(fontFamily = CinzelFamily, fontSize = 14.sp))
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
