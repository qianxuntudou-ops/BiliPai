package com.android.purebilibili.feature.video.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.purebilibili.core.store.SettingsManager
import com.android.purebilibili.core.util.EasterEggs
import com.android.purebilibili.feature.video.ui.feedback.resolveTripleActionFeedbackMessage
import com.android.purebilibili.feature.video.ui.feedback.resolveTripleActionVisualState
import com.android.purebilibili.feature.video.usecase.TripleActionResult
import com.android.purebilibili.feature.video.usecase.VideoInteractionUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VideoEngagementSeed(
    val isLoggedIn: Boolean = false,
    val isVip: Boolean = false,
    val isFollowing: Boolean = false,
    val isFavorited: Boolean = false,
    val isLiked: Boolean = false,
    val coinCount: Int = 0,
    val isInWatchLater: Boolean = false,
    val followingMids: Set<Long> = emptySet()
)

data class VideoEngagementUiState(
    val subject: VideoSubjectSnapshot? = null,
    val isLoggedIn: Boolean = false,
    val isVip: Boolean = false,
    val isFollowing: Boolean = false,
    val isFavorited: Boolean = false,
    val isLiked: Boolean = false,
    val coinCount: Int = 0,
    val isInWatchLater: Boolean = false,
    val followingMids: Set<Long> = emptySet(),
    val coinDialogVisible: Boolean = false,
    val likeBurstVisible: Boolean = false,
    val tripleCelebrationVisible: Boolean = false
)

sealed interface VideoEngagementEvent {
    data class Message(val text: String) : VideoEngagementEvent
    data class OpenFollowGroups(val mid: Long) : VideoEngagementEvent
    data class LoadVideo(val bvid: String) : VideoEngagementEvent
}

interface VideoEngagementActions {
    suspend fun toggleFollow(mid: Long, currentlyFollowing: Boolean): Result<Boolean>
    suspend fun toggleLike(aid: Long, currentlyLiked: Boolean, bvid: String): Result<Boolean>
    suspend fun toggleFavorite(aid: Long, currentlyFavorited: Boolean, bvid: String): Result<Boolean>
    suspend fun toggleWatchLater(aid: Long, currentlyInWatchLater: Boolean, bvid: String): Result<Boolean>
    suspend fun doCoin(aid: Long, count: Int, alsoLike: Boolean, bvid: String): Result<Boolean>
    suspend fun doTripleAction(aid: Long): Result<TripleActionResult>
}

private class DefaultVideoEngagementActions(
    private val useCase: VideoInteractionUseCase = VideoInteractionUseCase()
) : VideoEngagementActions {
    override suspend fun toggleFollow(mid: Long, currentlyFollowing: Boolean) =
        useCase.toggleFollow(mid, currentlyFollowing)

    override suspend fun toggleLike(aid: Long, currentlyLiked: Boolean, bvid: String) =
        useCase.toggleLike(aid, currentlyLiked, bvid)

    override suspend fun toggleFavorite(aid: Long, currentlyFavorited: Boolean, bvid: String) =
        useCase.toggleFavorite(aid, currentlyFavorited, bvid)

    override suspend fun toggleWatchLater(aid: Long, currentlyInWatchLater: Boolean, bvid: String) =
        useCase.toggleWatchLater(aid, currentlyInWatchLater, bvid)

    override suspend fun doCoin(aid: Long, count: Int, alsoLike: Boolean, bvid: String) =
        useCase.doCoin(aid, count, alsoLike, bvid)

    override suspend fun doTripleAction(aid: Long) = useCase.doTripleAction(aid)
}

class VideoEngagementViewModel(
    private val actions: VideoEngagementActions = DefaultVideoEngagementActions()
) : ViewModel() {
    private val _uiState = MutableStateFlow(VideoEngagementUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<VideoEngagementEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    private var appContext: Context? = null

    fun initWithContext(context: Context) {
        appContext = context.applicationContext
    }

    fun bindSubject(subject: VideoSubjectSnapshot, seed: VideoEngagementSeed) {
        if (!shouldRebindVideoDomain(_uiState.value.subject, subject)) {
            sync(seed)
            return
        }
        _uiState.value = VideoEngagementUiState(
            subject = subject,
            isLoggedIn = seed.isLoggedIn,
            isVip = seed.isVip,
            isFollowing = seed.isFollowing,
            isFavorited = seed.isFavorited,
            isLiked = seed.isLiked,
            coinCount = seed.coinCount,
            isInWatchLater = seed.isInWatchLater,
            followingMids = seed.followingMids
        )
    }

    fun sync(seed: VideoEngagementSeed) {
        _uiState.update { current ->
            current.copy(
                isLoggedIn = seed.isLoggedIn,
                isVip = seed.isVip,
                isFollowing = seed.isFollowing,
                isFavorited = seed.isFavorited,
                isLiked = seed.isLiked,
                coinCount = seed.coinCount,
                isInWatchLater = seed.isInWatchLater,
                followingMids = seed.followingMids
            )
        }
    }

    fun setCoinDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(coinDialogVisible = visible) }
    }

    fun openCoinDialog() {
        if (_uiState.value.coinCount >= 2) {
            emitMessage("已投满2个硬币")
            return
        }
        setCoinDialogVisible(true)
    }

    fun toggleFollow(mid: Long? = null, currentlyFollowing: Boolean? = null) {
        val state = _uiState.value
        val targetMid = mid ?: state.subject?.ownerMid ?: return
        val wasFollowing = currentlyFollowing ?: state.isFollowing
        viewModelScope.launch {
            actions.toggleFollow(targetMid, wasFollowing)
                .onSuccess { following ->
                    _uiState.update { current ->
                        if (current.subject?.generation != state.subject?.generation) return@update current
                        val followingMids = current.followingMids.toMutableSet().apply {
                            if (following) add(targetMid) else remove(targetMid)
                        }
                        current.copy(
                            isFollowing = if (current.subject?.ownerMid == targetMid) following else current.isFollowing,
                            followingMids = followingMids
                        )
                    }
                    emitMessage(if (following) "关注成功" else "已取消关注")
                    if (following) _events.send(VideoEngagementEvent.OpenFollowGroups(targetMid))
                }
                .onFailure { emitMessage(it.message ?: "操作失败") }
        }
    }

    fun toggleLike(
        aid: Long? = null,
        bvid: String? = null,
        currentlyLiked: Boolean? = null,
        onResult: ((Boolean) -> Unit)? = null
    ) {
        val state = _uiState.value
        val targetAid = aid ?: state.subject?.aid ?: return
        val targetBvid = bvid ?: state.subject?.bvid ?: return
        val wasLiked = currentlyLiked ?: state.isLiked
        viewModelScope.launch {
            actions.toggleLike(targetAid, wasLiked, targetBvid)
                .onSuccess { liked ->
                    _uiState.update { current ->
                        if (current.subject?.aid != targetAid || current.subject.bvid != targetBvid) current
                        else current.copy(isLiked = liked, likeBurstVisible = liked)
                    }
                    onResult?.invoke(liked)
                    val easterEggEnabled = appContext?.let(SettingsManager::isEasterEggEnabledSync) == true
                    emitMessage(
                        if (liked && easterEggEnabled) EasterEggs.getLikeMessage()
                        else if (liked) "已点赞" else "已取消点赞"
                    )
                }
                .onFailure { emitMessage(it.message ?: "操作失败") }
        }
    }

    fun toggleFavorite() {
        val state = _uiState.value
        val subject = state.subject ?: return
        viewModelScope.launch {
            actions.toggleFavorite(subject.aid, state.isFavorited, subject.bvid)
                .onSuccess { favorited ->
                    _uiState.update { current ->
                        if (current.subject?.generation == subject.generation) {
                            current.copy(isFavorited = favorited)
                        } else current
                    }
                    emitMessage(if (favorited) "已收藏" else "已取消收藏")
                }
                .onFailure { emitMessage(it.message ?: "收藏操作失败") }
        }
    }

    fun toggleWatchLater() {
        val state = _uiState.value
        val subject = state.subject ?: return
        viewModelScope.launch {
            actions.toggleWatchLater(subject.aid, state.isInWatchLater, subject.bvid)
                .onSuccess { inWatchLater ->
                    _uiState.update { current ->
                        if (current.subject?.generation == subject.generation) {
                            current.copy(isInWatchLater = inWatchLater)
                        } else current
                    }
                    emitMessage(if (inWatchLater) "已添加到稍后再看" else "已从稍后再看移除")
                }
                .onFailure { emitMessage(it.message ?: "操作失败") }
        }
    }

    fun doCoin(count: Int, alsoLike: Boolean) {
        val state = _uiState.value
        val subject = state.subject ?: return
        setCoinDialogVisible(false)
        viewModelScope.launch {
            actions.doCoin(subject.aid, count, alsoLike, subject.bvid)
                .onSuccess {
                    _uiState.update { current ->
                        if (current.subject?.generation != subject.generation) current
                        else current.copy(
                            coinCount = minOf(current.coinCount + count, 2),
                            isLiked = current.isLiked || alsoLike
                        )
                    }
                    val easterEggEnabled = appContext?.let(SettingsManager::isEasterEggEnabledSync) == true
                    emitMessage(if (easterEggEnabled) EasterEggs.getCoinMessage() else "投币成功")
                }
                .onFailure { emitMessage(it.message ?: "投币失败") }
        }
    }

    fun doTripleAction(
        aid: Long? = null,
        bvid: String? = null,
        currentLiked: Boolean? = null,
        currentCoinCount: Int? = null,
        currentFavorited: Boolean? = null,
        onResult: ((TripleActionResult) -> Unit)? = null
    ) {
        val state = _uiState.value
        val targetAid = aid ?: state.subject?.aid ?: return
        val targetBvid = bvid ?: state.subject?.bvid ?: return
        viewModelScope.launch {
            emitMessage("正在三连")
            actions.doTripleAction(targetAid)
                .onSuccess { result ->
                    val visual = resolveTripleActionVisualState(
                        currentLiked = currentLiked ?: state.isLiked,
                        currentCoinCount = currentCoinCount ?: state.coinCount,
                        currentFavorited = currentFavorited ?: state.isFavorited,
                        likeSuccess = result.likeSuccess,
                        coinSuccess = result.coinSuccess,
                        coinFailureMessage = result.coinMessage,
                        favoriteSuccess = result.favoriteSuccess
                    )
                    _uiState.update { current ->
                        if (current.subject?.aid != targetAid || current.subject.bvid != targetBvid) current
                        else current.copy(
                            isLiked = visual.isLiked,
                            coinCount = visual.coinCount,
                            isFavorited = visual.isFavorited,
                            tripleCelebrationVisible = result.allSuccess
                        )
                    }
                    onResult?.invoke(result)
                    emitMessage(
                        resolveTripleActionFeedbackMessage(
                            result.likeSuccess,
                            result.coinSuccess,
                            result.favoriteSuccess,
                            result.coinMessage
                        )
                    )
                    val context = appContext
                    if (result.allSuccess && context != null && SettingsManager.getTripleJumpEnabled(context).first()) {
                        delay(2_000L)
                        _events.send(VideoEngagementEvent.LoadVideo("BV1JsK5eyEuB"))
                    }
                }
                .onFailure { emitMessage(it.message ?: "三连失败") }
        }
    }

    fun dismissLikeBurst() {
        _uiState.update { it.copy(likeBurstVisible = false) }
    }

    fun dismissTripleCelebration() {
        _uiState.update { it.copy(tripleCelebrationVisible = false) }
    }

    internal fun emitMessage(message: String) {
        viewModelScope.launch { _events.send(VideoEngagementEvent.Message(message)) }
    }
}

internal fun VideoPlaybackUiState.Success.toEngagementSeed(): VideoEngagementSeed =
    VideoEngagementSeed(
        isLoggedIn = isLoggedIn,
        isVip = isVip,
        isFollowing = isFollowing,
        isFavorited = isFavorited,
        isLiked = isLiked,
        coinCount = coinCount,
        isInWatchLater = isInWatchLater,
        followingMids = followingMids
    )
