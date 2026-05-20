# ProJson

ProJson is a Kotlin library for converting Kotlin objects into JSON using reflection.

The library supports:

- JSON objects
- JSON arrays
- Primitive values
- Reflection-based serialization
- Object identifiers (`$id`)
- Object references (`$ref`)
- Pretty printed output
- JSON manipulation
- Custom annotations

---

# Installation

Clone the repository:

```bash
git clone https://github.com/YOUR_USERNAME/ProJson.git
```

Open the project using IntelliJ IDEA.

---

# Project Structure

```text
src/main/kotlin/projson/

    ProJson.kt
    JsonValue.kt
    JsonObject.kt
    JsonArray.kt
    JsonPrimitive.kt
    JsonReference.kt

    Reference.kt
    JsonProperty.kt
    JsonIgnore.kt
```

---

# Basic Usage

Create a Kotlin object:

```kotlin
import projson.*

data class Date(
    val day:Int,
    val month:Int,
    val year:Int
)
```

Convert into JSON:

```kotlin
fun main(){

    val date=
        Date(
            31,
            4,
            2026
        )

    val json=
        ProJson()
            .toJsonString(date)

    println(json)
}
```

Output:

```json
{
    "$id":"generated-id",
    "$type":"Date",
    "day":31,
    "month":4,
    "year":2026
}
```

---

# Primitive Values

ProJson automatically converts primitive values.

```kotlin
println(
    ProJson()
        .toJsonString(
            "Miguel"
        )
)

println(
    ProJson()
        .toJsonString(
            22
        )
)

println(
    ProJson()
        .toJsonString(
            true
        )
)
```

Output:

```json
"Miguel"

22

true
```

---

# Working with Arrays

Collections automatically become JSON arrays.

```kotlin
val list=
    listOf(
        "Java",
        null,
        "Kotlin"
    )

println(
    ProJson()
        .toJsonString(
            list
        )
)
```

Output:

```json
[
    "Java",
    null,
    "Kotlin"
]
```

---

# Working with Maps

Maps become JSON objects.

```kotlin
val map=
    mapOf(
        "name" to "Miguel",
        "age" to 22
    )

println(
    ProJson()
        .toJsonString(map)
)
```

Output:

```json
{
    "name":"Miguel",
    "age":22
}
```

---

# JsonObject Manipulation

Objects can be modified after creation.

```kotlin
val date=
    Date(
        31,
        4,
        2026
    )

val json=
    ProJson()
        .toJson(date)
            as JsonObject

json.setProperty(
    "year",
    2027
)

json.removeProperty(
    "month"
)

println(json)
```

Output:

```json
{
    "$id":"id",
    "$type":"Date",
    "day":31,
    "year":2027
}
```

---

# JsonArray Manipulation

Arrays support modifications.

```kotlin
val array=
    ProJson()
        .toJson(
            listOf(
                "a",
                "b"
            )
        ) as JsonArray

array.add("c")

array.modifyAt(
    1,
    "changed"
)

array.removeAt(0)

println(array)
```

Output:

```json
["changed","c"]
```

---

# Traversing JSON Trees

ProJson supports recursive traversal.

```kotlin
json.traverse {

    println(it)

}
```

This visits:

- objects
- arrays
- primitive values
- nested nodes

---

# References

Fields annotated with `@Reference`
generate object references.

```kotlin
class Task(

    val description:String,

    val deadline:Date?,

    @field:Reference
    val dependencies:
        List<Task>
)
```

Example:

```kotlin
val t1=
    Task(
        "T1",
        Date(
            30,
            2,
            2026
        ),
        emptyList()
    )

val t2=
    Task(
        "T2",
        Date(
            31,
            4,
            2026
        ),
        emptyList()
    )

val t3=
    Task(
        "T3",
        null,
        listOf(
            t1,
            t2
        )
    )

println(
    ProJson()
        .toJsonString(
            listOf(
                t1,
                t2,
                t3
            )
        )
)
```

Output:

```json
{
    "$ref":"generated-id"
}
```

---

# JsonProperty

Allows custom property names.

```kotlin
class Task(

    @field:JsonProperty(
        "desc"
    )

    val description:
        String
)
```

Output:

```json
{
    "desc":"T1"
}
```

---

# JsonIgnore

Ignores fields.

```kotlin
class Task(

    @field:JsonIgnore
    val deadline:Date?
)
```

The field will not appear.

---

# Running Tests

Run all tests:

```bash
gradlew test
```

---

# Build

Generate JAR:

```bash
gradlew build
```

Output location:

```text
build/libs/
```

---

# Author

Miguel Bento
