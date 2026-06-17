package com.android.purebilibili.feature.home

internal const val HOME_HERO_CAROUSEL_MAX_ITEMS = 8
internal const val HOME_HERO_CAROUSEL_SIDE_PEEK_DP = 0f

internal data class HomeHeroCarouselCardTransform(
    val rotationY: Float,
    val rotationZ: Float,
    val scale: Float,
    val alpha: Float,
    val cameraDistanceMultiplier: Float,
    val translationXFraction: Float,
    val pivotFractionX: Float,
    val zIndex: Float,
    val contentParallaxFraction: Float,
    val contentScale: Float,
    val edgeShadeAlpha: Float,
    val edgeShadeStartFromLeft: Boolean,
    val shadowElevationFraction: Float
)

internal fun <T> selectHomeHeroCarouselItems(
    items: List<T>,
    maxItems: Int = HOME_HERO_CAROUSEL_MAX_ITEMS
): List<T> {
    if (maxItems <= 0) return emptyList()
    return items.take(maxItems)
}

internal fun <T, K> excludeHomeHeroCarouselItems(
    items: List<T>,
    carouselItems: List<T>,
    keySelector: (T) -> K
): List<T> {
    if (carouselItems.isEmpty()) return items
    val carouselKeys = carouselItems.mapTo(mutableSetOf(), keySelector)
    return items.filterNot { keySelector(it) in carouselKeys }
}

internal fun shouldShowHomeHeroCarousel(
    enabled: Boolean,
    category: HomeCategory,
    itemCount: Int
): Boolean {
    return enabled && category == HomeCategory.RECOMMEND && itemCount > 0
}

internal fun resolveHomeHeroCarouselCardTransform(
    pageOffset: Float
): HomeHeroCarouselCardTransform {
    val clampedOffset = pageOffset.coerceIn(-1f, 1f)
    val distance = kotlin.math.abs(clampedOffset)
    val inFlightDistance = distance * (1f - distance)
    val pivotFractionX = when {
        clampedOffset < -0.001f -> 0f
        clampedOffset > 0.001f -> 1f
        else -> 0.5f
    }
    return HomeHeroCarouselCardTransform(
        rotationY = -clampedOffset * 66f,
        rotationZ = clampedOffset * inFlightDistance * 14f,
        scale = 1f - distance * 0.12f,
        alpha = 1f - distance * 0.12f,
        cameraDistanceMultiplier = 8f,
        translationXFraction = clampedOffset * inFlightDistance * 0.56f,
        pivotFractionX = pivotFractionX,
        zIndex = 1f - distance + inFlightDistance * 2.4f,
        contentParallaxFraction = -clampedOffset * 0.08f,
        contentScale = 1f + distance * 0.06f,
        edgeShadeAlpha = distance * 0.28f,
        edgeShadeStartFromLeft = clampedOffset < 0f,
        shadowElevationFraction = distance * 0.8f
    )
}

internal fun resolveHomeHeroCarouselPreviewAlpha(
    hasRenderedFirstFrame: Boolean
): Float = if (hasRenderedFirstFrame) 1f else 0f
