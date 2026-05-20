package com.android.purebilibili.core.ui.transition

internal enum class VideoSharedTransitionProfile {
    COVER_ONLY,
    COVER_AND_METADATA
}

internal const val VIDEO_SHARED_COVER_ASPECT_RATIO = 16f / 10f
private const val HOME_SOURCE_ROUTE = "home"
private const val HOME_DETAIL_REVEAL_DELAY_MILLIS = 40
private const val HOME_DETAIL_REVEAL_DURATION_MILLIS = 220
private const val HOME_DETAIL_REVEAL_SLIDE_OFFSET_DP = 14
private const val HOME_DETAIL_REVEAL_INITIAL_SCALE = 0.985f

internal data class VideoSharedTransitionOwnership(
    val useCoverSharedBounds: Boolean,
    val useMetadataSharedBounds: Boolean
)

internal data class VideoDetailContentRevealMotion(
    val enabled: Boolean,
    val delayMillis: Int,
    val durationMillis: Int,
    val slideOffsetDp: Int,
    val initialScale: Float
)

internal fun resolveVideoSharedTransitionProfile(): VideoSharedTransitionProfile {
    // Prefer a unified return choreography: cover + key metadata move back together.
    return VideoSharedTransitionProfile.COVER_AND_METADATA
}

internal fun shouldEnableVideoCoverSharedTransition(
    transitionEnabled: Boolean,
    hasSharedTransitionScope: Boolean,
    hasAnimatedVisibilityScope: Boolean
): Boolean {
    return transitionEnabled &&
        hasSharedTransitionScope &&
        hasAnimatedVisibilityScope
}

internal fun shouldEnableVideoMetadataSharedTransition(
    coverSharedEnabled: Boolean,
    isQuickReturnLimited: Boolean,
    useCardContainerSharedBounds: Boolean = false,
    profile: VideoSharedTransitionProfile = resolveVideoSharedTransitionProfile()
): Boolean {
    if (!coverSharedEnabled) return false
    // 卡片容器已经承载整体放大/回收时，标题、UP、统计等不要再各自抢独立 sharedBounds。
    if (useCardContainerSharedBounds) return false
    // Keep metadata linked during quick return to avoid cover-only snapback.
    if (isQuickReturnLimited && profile == VideoSharedTransitionProfile.COVER_ONLY) return false
    return profile == VideoSharedTransitionProfile.COVER_AND_METADATA
}

internal fun resolveVideoSharedTransitionOwnership(
    sourceRoute: String?,
    coverSharedEnabled: Boolean,
    isQuickReturnLimited: Boolean
): VideoSharedTransitionOwnership {
    if (!coverSharedEnabled) {
        return VideoSharedTransitionOwnership(
            useCoverSharedBounds = false,
            useMetadataSharedBounds = false
        )
    }

    val isHomeSource = sourceRoute?.substringBefore("?") == HOME_SOURCE_ROUTE
    return VideoSharedTransitionOwnership(
        useCoverSharedBounds = true,
        // 首页进入详情时封面是主锚点，元数据跟随普通内容入场，避免多个 sharedBounds 抢焦点。
        useMetadataSharedBounds = !isHomeSource &&
            shouldEnableVideoMetadataSharedTransition(
                coverSharedEnabled = true,
                isQuickReturnLimited = isQuickReturnLimited
            )
    )
}

internal fun resolveVideoDetailContentRevealMotion(
    sourceRoute: String?,
    transitionEnabled: Boolean
): VideoDetailContentRevealMotion {
    val isHomeSharedTransition = transitionEnabled &&
        sourceRoute?.substringBefore("?") == HOME_SOURCE_ROUTE
    if (!isHomeSharedTransition) {
        return VideoDetailContentRevealMotion(
            enabled = false,
            delayMillis = 0,
            durationMillis = 0,
            slideOffsetDp = 0,
            initialScale = 1f
        )
    }

    return VideoDetailContentRevealMotion(
        enabled = true,
        delayMillis = HOME_DETAIL_REVEAL_DELAY_MILLIS,
        durationMillis = HOME_DETAIL_REVEAL_DURATION_MILLIS,
        slideOffsetDp = HOME_DETAIL_REVEAL_SLIDE_OFFSET_DP,
        initialScale = HOME_DETAIL_REVEAL_INITIAL_SCALE
    )
}
