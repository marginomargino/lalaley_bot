package telegram

sealed interface Task {
    data class ForDate(
        val dayOfMonth: Int,
        val monthNumber: Int
    ) : Task

    data object Random : Task
}
