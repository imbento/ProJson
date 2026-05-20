import projson.*

data class Date(
    val day: Int,
    val month: Int,
    val year: Int
)

class Task(
    val description: String,
    val deadline: Date?,

    @field:Reference
    val dependencies: List<Task>
)

fun main() {
    val t1 = Task("T1", Date(30, 2, 2026), emptyList())
    val t2 = Task("T2", Date(31, 4, 2026), emptyList())
    val t3 = Task("T3", null, listOf(t1, t2))

    val all = listOf(t1, t2, t3)

    println(ProJson().toJsonString(all))
}