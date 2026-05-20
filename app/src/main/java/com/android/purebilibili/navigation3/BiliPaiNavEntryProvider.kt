package com.android.purebilibili.navigation3

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay

internal fun biliPaiNavEntryProvider(
    sourceMetadata: BiliPaiNavSourceMetadata,
    content: @Composable (BiliPaiNavKey) -> Unit
): (BiliPaiNavKey) -> NavEntry<BiliPaiNavKey> {
    return entryProvider(
        fallback = { key ->
            NavEntry(
                key = key,
                metadata = biliPaiNavEntryMetadata(key, sourceMetadata),
                content = content
            )
        }
    ) {
        entry<BiliPaiNavKey.Home>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Dynamic>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Search>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.SearchTrending>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.TopicDetail>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Settings>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.OpenSourceLicenses>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.AppearanceSettings>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.IconSettings>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.AnimationSettings>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.PlaybackSettings>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.PermissionSettings>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.PluginsSettings>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.BottomBarSettings>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.SettingsShare>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.WebDavBackup>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.TipsSettings>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Login>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Profile>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.History>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Favorite>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.WatchLater>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Onboarding>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Following>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.DownloadList>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.OfflineVideoPlayer>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.LiveList>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.LiveSearch>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.LiveArea>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.LiveAreaDetail>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.LiveFollowing>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Inbox>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.ReplyMe>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.AtMe>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.LikeMe>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.SystemNotice>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Chat>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Partition>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Story>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.AudioMode>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.SeasonSeriesDetail>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Bangumi>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.BangumiPlayer>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.MusicDetail>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.NativeMusic>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.VideoDetail>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.ArticleDetail>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.DynamicDetail>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Space>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Category>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Live>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.BangumiDetail>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Web>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
        entry<BiliPaiNavKey.Unknown>(metadata = { key -> biliPaiNavEntryMetadata(key, sourceMetadata) }, content = content)
    }
}

internal fun biliPaiNavEntryMetadata(
    key: BiliPaiNavKey,
    sourceMetadata: BiliPaiNavSourceMetadata
): Map<String, Any> {
    val transitions = resolveBiliPaiNavEntryRouteTransitions(
        key = key,
        sourceMetadata = sourceMetadata
    )
    return NavDisplay.transitionSpec {
        resolveBiliPaiNavContentTransform(transitions.forward)
    } + NavDisplay.popTransitionSpec {
        resolveBiliPaiNavContentTransform(transitions.pop)
    } + NavDisplay.predictivePopTransitionSpec {
        resolveBiliPaiNavContentTransform(transitions.predictivePop)
    }
}

internal data class BiliPaiNavEntryRouteTransitions(
    val forward: BiliPaiNavRouteTransition,
    val pop: BiliPaiNavRouteTransition,
    val predictivePop: BiliPaiNavRouteTransition
)

internal fun resolveBiliPaiNavEntryRouteTransitions(
    key: BiliPaiNavKey,
    sourceMetadata: BiliPaiNavSourceMetadata
): BiliPaiNavEntryRouteTransitions {
    val sharedReadyVideoPush = key is BiliPaiNavKey.VideoDetail &&
        sourceMetadata.sharedTransitionReady &&
        sourceMetadata.sourceRoute != null &&
        sourceMetadata.sourceRoute == key.sourceRoute &&
        sourceMetadata.sourceKey == "${sourceMetadata.sourceRoute}:${key.bvid}"
    val noOpReturn = key is BiliPaiNavKey.VideoDetail || sourceMetadata.sharedTransitionReady
    val forward = if (sharedReadyVideoPush) {
        BiliPaiNavRouteTransition.NO_OP_SHARED_ELEMENT
    } else {
        BiliPaiNavRouteTransition.FALLBACK
    }
    val pop = if (noOpReturn) {
        BiliPaiNavRouteTransition.NO_OP_SHARED_ELEMENT
    } else {
        BiliPaiNavRouteTransition.FALLBACK
    }
    return BiliPaiNavEntryRouteTransitions(
        forward = forward,
        pop = pop,
        predictivePop = pop
    )
}
