package com.meshchat.android.ui.nav

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.meshchat.android.ui.ChatViewModel
import com.meshchat.android.ui.screens.ChatListScreen
import com.meshchat.android.ui.screens.ConversationScreen
import com.meshchat.android.ui.screens.SettingsScreen

// ─── Route constants ──────────────────────────────────────────────────────────

object MeshChatRoutes {
    const val CHAT_LIST    = "chat_list"
    const val CONVERSATION = "conversation/{conversationId}"
    const val SETTINGS     = "settings"

    fun conversationRoute(conversationId: String) =
        "conversation/${conversationId.encodeRouteComponent()}"
}

private fun String.encodeRouteComponent(): String =
    java.net.URLEncoder.encode(this, "UTF-8")

// ─── Nav Host ────────────────────────────────────────────────────────────────

private const val TRANSITION_DURATION_MS = 280

@Composable
fun MeshChatNavHost(
    navController: NavHostController,
    viewModel: ChatViewModel,
) {
    NavHost(
        navController       = navController,
        startDestination    = MeshChatRoutes.CHAT_LIST,
        enterTransition     = {
            slideIntoContainer(
                towards  = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(TRANSITION_DURATION_MS),
            )
        },
        exitTransition      = {
            slideOutOfContainer(
                towards  = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(TRANSITION_DURATION_MS),
            )
        },
        popEnterTransition  = {
            slideIntoContainer(
                towards  = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(TRANSITION_DURATION_MS),
            )
        },
        popExitTransition   = {
            slideOutOfContainer(
                towards  = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(TRANSITION_DURATION_MS),
            )
        },
    ) {
        // ── Chat List (home) ─────────────────────────────────────────────────
        composable(MeshChatRoutes.CHAT_LIST) {
            ChatListScreen(
                viewModel         = viewModel,
                onConversationClick = { conversationId ->
                    navController.navigate(MeshChatRoutes.conversationRoute(conversationId))
                },
                onSettingsClick   = { navController.navigate(MeshChatRoutes.SETTINGS) },
            )
        }

        // ── Conversation ─────────────────────────────────────────────────────
        composable(
            route     = MeshChatRoutes.CONVERSATION,
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType }
            ),
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments
                ?.getString("conversationId")
                ?.let { java.net.URLDecoder.decode(it, "UTF-8") }
                ?: return@composable

            ConversationScreen(
                conversationId = conversationId,
                viewModel      = viewModel,
                onBack         = { navController.popBackStack() },
            )
        }

        // ── Settings ─────────────────────────────────────────────────────────
        composable(MeshChatRoutes.SETTINGS) {
            SettingsScreen(
                viewModel = viewModel,
                onBack    = { navController.popBackStack() },
            )
        }
    }
}
