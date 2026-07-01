package com.android.purebilibili.core.ui.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.android.purebilibili.navigation3.BiliPaiNavKey
import com.android.purebilibili.navigation3.BiliPaiNavRouteTransition

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun Modifier.videoCardShellSharedBoundsOrEmpty(
    enabled: Boolean,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    bvid: String,
    sourceRoute: String?,
    motionSpec: VideoSharedTransitionMotionSpec,
    clipShape: Shape
): Modifier {
    if (!enabled || sharedTransitionScope == null || animatedVisibilityScope == null || bvid.isBlank()) {
        return this
    }
    return then(
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(
                    key = videoCoverSharedElementKey(
                        bvid = bvid,
                        sourceRoute = sourceRoute
                    )
                ),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = { _, _ ->
                    if (motionSpec.enabled) {
                        tween(
                            durationMillis = motionSpec.durationMillis,
                            easing = motionSpec.easing
                        )
                    } else {
                        com.android.purebilibili.core.ui.motion.AppMotionTokens.spatialSpec()
                    }
                },
                clipInOverlayDuringTransition = OverlayClip(clipShape)
            )
        }
    )
}

@Composable
internal fun VideoSharedTransitionBackdropHost(
    cardTransitionEnabled: Boolean,
    sharedElementRouteTransition: Boolean,
    entryKey: BiliPaiNavKey,
    topKey: BiliPaiNavKey?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val navTransition = LocalNavAnimatedContentScope.current.transition
    val transitionInProgress = navTransition.currentState != navTransition.targetState ||
        navTransition.isRunning
    val entryInvolvesVideoDetail = topKey is BiliPaiNavKey.VideoDetail
    val entryIsUnderlyingSource = entryKey !is BiliPaiNavKey.VideoDetail && entryKey != topKey
    val frame = remember(
        cardTransitionEnabled,
        sharedElementRouteTransition,
        transitionInProgress,
        entryInvolvesVideoDetail,
        entryIsUnderlyingSource
    ) {
        resolveVideoSharedTransitionBackdropFrame(
            cardTransitionEnabled = cardTransitionEnabled,
            sharedElementRouteTransition = sharedElementRouteTransition,
            transitionInProgress = transitionInProgress,
            entryInvolvesVideoDetail = entryInvolvesVideoDetail,
            entryIsUnderlyingSource = entryIsUnderlyingSource
        )
    }
    VideoSharedTransitionBackdropDecoration(
        frame = frame,
        modifier = modifier,
        content = content
    )
}

@Composable
internal fun VideoSharedTransitionBackdropDecoration(
    frame: VideoSharedTransitionBackdropFrame,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (frame.enabled && frame.blurRadiusDp > 0f) {
                        Modifier.blur(frame.blurRadiusDp.dp)
                    } else {
                        Modifier
                    }
                )
        ) {
            content()
        }
        if (frame.enabled && frame.scrimAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = frame.scrimAlpha))
            )
        }
    }
}

internal fun isVideoSharedElementRouteTransition(
    routeTransition: BiliPaiNavRouteTransition
): Boolean {
    return routeTransition == BiliPaiNavRouteTransition.NO_OP_SHARED_ELEMENT
}