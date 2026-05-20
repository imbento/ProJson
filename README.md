# ProJson

ProJson is a Kotlin library for converting Kotlin objects into JSON.

The project supports JSON generation, object serialization through reflection, JSON manipulation and object references.

## Features

- JSON objects
- JSON arrays
- Primitive JSON values
- Reflection-based serialization
- Pretty printed JSON output
- Object identifiers using `$id`
- Object references using `$ref`
- `@Reference`
- `@JsonProperty`
- `@JsonIgnore`
- JSON tree traversal
- Object and array manipulation

## Basic Example

```kotlin
import projson.*

data class Date(
    val day: Int,
    val month: Int,
    val year: Int
)

fun main() {

    val date = Date(
        31,
        4,
        2026
    )

    println(
        ProJson()
            .toJsonString(date)
    )
}
```

Output:

```json
{
    "$id": "generated-id",
    "$type": "Date",
    "day": 31,
    "month": 4,
    "year": 2026
}
```

## References

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
        Date(30,2,2026),
        emptyList()
    )

val t2=
    Task(
        "T2",
        Date(31,4,2026),
        emptyList()
    )

val t3=
    Task(
        "T3",
        null,
        listOf(t1,t2)
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
[
    {
        "$id":"uuid",
        "$type":"Task",
        "description":"T1"
    },
    {
        "$id":"uuid",
        "$type":"Task",
        "description":"T2"
    },
    {
        "$id":"uuid",
        "$type":"Task",
        "description":"T3",
        "dependencies":[
            {
                "$ref":"uuid"
            },
            {
                "$ref":"uuid"
            }
        ]
    }
]
```

## JsonProperty

```kotlin
class Task(

    @field:JsonProperty(
        "desc"
    )
    val description:String
)
```

Output:

```json
{
    "desc":"T1"
}
```

## JsonIgnore

```kotlin
class Task(

    @field:JsonIgnore
    val deadline:Date?
)
```

The field will not appear in the generated JSON.

## Running Tests

Run:

```bash
gradlew test
```

## Build

Generate JAR:

```bash
gradlew build
```

JAR location:

```text
build/libs/
```

## Author

Miguel Bento
