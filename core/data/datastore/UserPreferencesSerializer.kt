package core.data.datastore

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UserPreferences(
    val persona: String,
    val theme: String,
    val privacy: String
)

object UserPreferencesSerializer {
    fun serialize(prefs: UserPreferences): String = Json.encodeToString(UserPreferences.serializer(), prefs)
    fun deserialize(json: String): UserPreferences = Json.decodeFromString(UserPreferences.serializer(), json)
}
