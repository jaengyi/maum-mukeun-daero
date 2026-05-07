package com.mmd.feature.tracker.completion

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

object CompletionNavArgs {
    const val TASK_ID = "taskId"
}

const val CompletionRouteBase = "completion"
const val CompletionRoute = "$CompletionRouteBase/{${CompletionNavArgs.TASK_ID}}"

fun completionRouteFor(taskId: Long): String = "$CompletionRouteBase/$taskId"

fun NavGraphBuilder.completionScreen(
    onConfirm: () -> Unit,
) {
    composable(
        route = CompletionRoute,
        arguments = listOf(navArgument(CompletionNavArgs.TASK_ID) { type = NavType.LongType }),
    ) {
        CompletionScreen(onConfirm = onConfirm)
    }
}
