package com.mmd.feature.tracker.workout

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

object WorkoutNavArgs {
    const val TASK_ID = "taskId"
}

const val WorkoutRouteBase = "workout"
const val WorkoutRoute = "$WorkoutRouteBase/{${WorkoutNavArgs.TASK_ID}}"

fun workoutRouteFor(taskId: Long): String = "$WorkoutRouteBase/$taskId"

fun NavGraphBuilder.workoutScreen(
    onComplete: () -> Unit,
    onCancel: () -> Unit,
) {
    composable(
        route = WorkoutRoute,
        arguments = listOf(navArgument(WorkoutNavArgs.TASK_ID) { type = NavType.LongType }),
    ) {
        WorkoutScreen(onComplete = onComplete, onCancel = onCancel)
    }
}
