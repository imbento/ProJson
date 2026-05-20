import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import projson.*

/**
 * Test suite for the ProJson library.
 *
 * This class validates Phase 1 and Phase 2 functionality:
 * - Conversion of Kotlin objects to JSON
 * - Conversion of collections to JSON arrays
 * - JSON manipulation
 * - Map handling without "$type"
 * - References using @Reference
 * - Property customization using @JsonProperty
 * - Ignored fields using @JsonIgnore
 */
class ProJsonTest {

    /**
     * Sample data class used for testing object serialization.
     */
    data class Date(
        val day: Int,
        val month: Int,
        val year: Int
    )

    /**
     * Sample class used to test references.
     */
    class Task(
        val description: String,
        val deadline: Date?,

        @field:Reference
        val dependencies: List<Task>
    )

    /**
     * Sample class used to test custom property names and ignored fields.
     */
    class CustomTask(
        @field:JsonProperty("desc")
        val description: String,

        @field:JsonIgnore
        val deadline: Date?,

        @field:JsonProperty("deps")
        val dependencies: List<String>
    )

    /**
     * Tests if a Kotlin object is correctly converted into a JSON object.
     */
    @Test
    fun convertsObjectToJson() {
        val date = Date(31, 4, 2026)

        val json = ProJson().toJsonString(date)

        assertTrue(json.contains("\"\$type\": \"Date\""))
        assertTrue(json.contains("\"day\": 31"))
        assertTrue(json.contains("\"month\": 4"))
        assertTrue(json.contains("\"year\": 2026"))
    }

    /**
     * Tests conversion of a Kotlin list into a JSON array.
     */
    @Test
    fun convertsListToJsonArray() {
        val list = listOf("a", null, "b")

        val json = ProJson().toJsonString(list)

        assertTrue(json.startsWith("["))
        assertTrue(json.contains("\"a\""))
        assertTrue(json.contains("null"))
        assertTrue(json.contains("\"b\""))
        assertTrue(json.endsWith("]"))
    }

    /**
     * Tests if JSON objects can be modified after creation.
     */
    @Test
    fun allowsObjectManipulation() {
        val date = Date(31, 4, 2026)

        val json = ProJson().toJson(date) as JsonObject

        json.setProperty("year", 2027)

        assertTrue(json.toString().contains("\"year\":2027"))
    }

    /**
     * Tests conversion of a Map into a JSON object.
     */
    @Test
    fun convertsMapWithoutType() {
        val map = mapOf(
            "name" to "Miguel",
            "age" to 22
        )

        val json = ProJson().toJsonString(map)

        assertTrue(json.contains("\"name\": \"Miguel\""))
        assertTrue(json.contains("\"age\": 22"))
        assertFalse(json.contains("\"\$type\""))
        assertFalse(json.contains("\"\$id\""))
    }

    /**
     * Tests if JSON arrays can be modified after creation.
     */
    @Test
    fun allowsArrayManipulation() {
        val json = ProJson().toJson(listOf("a", "b")) as JsonArray

        json.add("c")

        assertEquals("[\"a\",\"b\",\"c\"]", json.toString())
    }

    /**
     * Tests conversion of primitive Kotlin values into JSON.
     */
    @Test
    fun convertsPrimitiveValuesToJson() {
        assertEquals("\"Miguel\"", ProJson().toJsonString("Miguel"))
        assertEquals("22", ProJson().toJsonString(22))
        assertEquals("true", ProJson().toJsonString(true))
        assertEquals("null", ProJson().toJsonString(null))
    }

    /**
     * Tests adding, modifying and removing properties from a JsonObject.
     */
    @Test
    fun allowsObjectPropertyAddModifyAndRemove() {
        val date = Date(31, 4, 2026)

        val json = ProJson().toJson(date) as JsonObject

        json.setProperty("year", 2027)
        json.setProperty("extra", "test")
        json.removeProperty("month")

        val result = json.toString()

        assertTrue(result.contains("\"year\":2027"))
        assertTrue(result.contains("\"extra\":\"test\""))
        assertFalse(result.contains("\"month\""))
    }

    /**
     * Tests adding, modifying and removing elements from a JsonArray.
     */
    @Test
    fun allowsArrayAddModifyAndRemove() {
        val json = ProJson().toJson(listOf("a", "b", "c")) as JsonArray

        json.add("d")
        json.modifyAt(1, "changed")
        json.removeAt(0)

        assertEquals("[\"changed\",\"c\",\"d\"]", json.toString())
    }

    /**
     * Tests traversal of a JSON tree.
     */
    @Test
    fun traversesJsonTree() {
        val map = mapOf(
            "name" to "Miguel",
            "skills" to listOf("Java", "Kotlin")
        )

        val json = ProJson().toJson(map) as JsonObject

        var count = 0

        json.traverse {
            count++
        }

        assertTrue(count >= 4)
    }

    /**
     * Tests that fields annotated with @Reference are serialized as JSON references.
     */
    @Test
    fun createsReferencesForAnnotatedFields() {
        val t1 = Task("T1", Date(30, 2, 2026), emptyList())
        val t2 = Task("T2", Date(31, 4, 2026), emptyList())
        val t3 = Task("T3", null, listOf(t1, t2))

        val json = ProJson().toJsonString(listOf(t1, t2, t3))

        assertTrue(json.contains("\"\$id\""))
        assertTrue(json.contains("\"\$ref\""))
        assertTrue(json.contains("\"description\": \"T3\""))
    }


    /**
     * Tests that generated references are created for annotated dependencies.
     */
    @Test
    fun referenceIdsMatchGeneratedObjectIds() {
        val t1 = Task("T1", null, emptyList())
        val t2 = Task("T2", null, listOf(t1))

        val json = ProJson().toJsonString(listOf(t1, t2))

        assertTrue(json.contains("\"\$id\""))
        assertTrue(json.contains("\"\$ref\""))
        assertTrue(json.contains("\"description\": \"T1\""))
        assertTrue(json.contains("\"description\": \"T2\""))
    }

    /**
     * Tests that @JsonProperty changes field names in the generated JSON.
     */
    @Test
    fun usesJsonPropertyAnnotationToRenameFields() {
        val task = CustomTask(
            description = "T1",
            deadline = Date(30, 2, 2026),
            dependencies = listOf("A", "B")
        )

        val json = ProJson().toJsonString(task)

        assertTrue(json.contains("\"desc\": \"T1\""))
        assertTrue(json.contains("\"deps\""))

        assertFalse(json.contains("\"description\""))
        assertFalse(json.contains("\"dependencies\""))
    }

    /**
     * Tests that @JsonIgnore removes fields from the generated JSON.
     */
    @Test
    fun usesJsonIgnoreAnnotationToIgnoreFields() {
        val task = CustomTask(
            description = "T1",
            deadline = Date(30, 2, 2026),
            dependencies = emptyList()
        )

        val json = ProJson().toJsonString(task)

        assertFalse(json.contains("\"deadline\""))
        assertFalse(json.contains("\"day\""))
        assertFalse(json.contains("\"month\""))
        assertFalse(json.contains("\"year\""))
    }
}