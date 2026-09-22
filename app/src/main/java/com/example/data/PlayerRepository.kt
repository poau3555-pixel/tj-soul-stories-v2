package com.example.data

import android.content.Context
import androidx.room.Room
import com.example.model.Achievement
import com.example.model.BoosterType
import com.example.model.DEFAULT_ACHIEVEMENTS
import com.example.model.DEFAULT_MISSIONS
import com.example.model.MascotOutfit
import com.example.model.Mission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class PlayerProfile(
    val lives: Int = 5,
    val maxLives: Int = 5,
    val nextLifeTimestamp: Long = 0L,
    val coins: Int = 500,
    val lollipopHammers: Int = 3,
    val freeSwitches: Int = 3,
    val colorBombs: Int = 2,
    val extraMoves: Int = 2,
    val sugarLightning: Int = 2,
    val highestUnlockedLevel: Int = 1,
    val totalStars: Int = 0,
    val lastDailySpinDate: String = "",
    val dailyStreak: Int = 1,
    val lastRewardClaimDate: String = "",
    val claimedRewardDays: List<Int> = emptyList(),
    val currentOutfit: MascotOutfit = MascotOutfit.CLASSIC_EXPLORER,
    val unlockedOutfits: List<String> = listOf(MascotOutfit.CLASSIC_EXPLORER.name),
    val musicEnabled: Boolean = true,
    val soundFxEnabled: Boolean = true
)

class PlayerRepository(context: Context) {

    private val prefs = context.getSharedPreferences("tj_sugar_quest_prefs", Context.MODE_PRIVATE)
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "tj_sugar_quest_db"
    ).fallbackToDestructiveMigration().build()

    private val levelDao = db.levelProgressDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _playerProfile = MutableStateFlow(loadProfile())
    val playerProfile: StateFlow<PlayerProfile> = _playerProfile.asStateFlow()

    private val _missions = MutableStateFlow(loadMissions())
    val missions: StateFlow<List<Mission>> = _missions.asStateFlow()

    private val _achievements = MutableStateFlow(loadAchievements())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    val levelProgressFlow = levelDao.getAllProgress()

    init {
        scope.launch {
            // Seed initial 25 levels if database empty
            val current = levelProgressFlow.first()
            if (current.isEmpty()) {
                val initialList = (1..25).map { level ->
                    LevelProgress(
                        levelNumber = level,
                        isUnlocked = (level == 1),
                        isCompleted = false,
                        stars = 0,
                        highScore = 0
                    )
                }
                levelDao.insertAll(initialList)
            }
            refreshLives()
            updateStatsFromProgress()
        }
    }

    private fun loadProfile(): PlayerProfile {
        val lives = prefs.getInt("lives", 5)
        val coins = prefs.getInt("coins", 600)
        val hammers = prefs.getInt("hammers", 3)
        val switches = prefs.getInt("switches", 3)
        val colorBombs = prefs.getInt("color_bombs", 2)
        val extraMoves = prefs.getInt("extra_moves", 2)
        val lightning = prefs.getInt("sugar_lightning", 2)
        val lastSpin = prefs.getString("last_spin", "") ?: ""
        val nextLifeTime = prefs.getLong("next_life_time", 0L)
        val streak = prefs.getInt("daily_streak", 1)
        val lastClaimDate = prefs.getString("last_claim_date", "") ?: ""
        val claimedDaysStr = prefs.getString("claimed_days", "") ?: ""
        val claimedDays = if (claimedDaysStr.isEmpty()) emptyList() else claimedDaysStr.split(",").mapNotNull { it.toIntOrNull() }
        val outfitName = prefs.getString("current_outfit", MascotOutfit.CLASSIC_EXPLORER.name) ?: MascotOutfit.CLASSIC_EXPLORER.name
        val currentOutfit = try { MascotOutfit.valueOf(outfitName) } catch (e: Exception) { MascotOutfit.CLASSIC_EXPLORER }
        val unlockedOutfitsStr = prefs.getString("unlocked_outfits", MascotOutfit.CLASSIC_EXPLORER.name) ?: MascotOutfit.CLASSIC_EXPLORER.name
        val unlockedOutfits = unlockedOutfitsStr.split(",").filter { it.isNotEmpty() }
        val music = prefs.getBoolean("music_enabled", true)
        val sfx = prefs.getBoolean("sfx_enabled", true)

        return PlayerProfile(
            lives = lives,
            coins = coins,
            lollipopHammers = hammers,
            freeSwitches = switches,
            colorBombs = colorBombs,
            extraMoves = extraMoves,
            sugarLightning = lightning,
            lastDailySpinDate = lastSpin,
            nextLifeTimestamp = nextLifeTime,
            dailyStreak = streak,
            lastRewardClaimDate = lastClaimDate,
            claimedRewardDays = claimedDays,
            currentOutfit = currentOutfit,
            unlockedOutfits = unlockedOutfits,
            musicEnabled = music,
            soundFxEnabled = sfx
        )
    }

    private fun saveProfile(profile: PlayerProfile) {
        prefs.edit()
            .putInt("lives", profile.lives)
            .putInt("coins", profile.coins)
            .putInt("hammers", profile.lollipopHammers)
            .putInt("switches", profile.freeSwitches)
            .putInt("color_bombs", profile.colorBombs)
            .putInt("extra_moves", profile.extraMoves)
            .putInt("sugar_lightning", profile.sugarLightning)
            .putString("last_spin", profile.lastDailySpinDate)
            .putLong("next_life_time", profile.nextLifeTimestamp)
            .putInt("daily_streak", profile.dailyStreak)
            .putString("last_claim_date", profile.lastRewardClaimDate)
            .putString("claimed_days", profile.claimedRewardDays.joinToString(","))
            .putString("current_outfit", profile.currentOutfit.name)
            .putString("unlocked_outfits", profile.unlockedOutfits.joinToString(","))
            .putBoolean("music_enabled", profile.musicEnabled)
            .putBoolean("sfx_enabled", profile.soundFxEnabled)
            .apply()
        _playerProfile.value = profile
    }

    private fun loadMissions(): List<Mission> {
        return DEFAULT_MISSIONS.map { mission ->
            val prog = prefs.getInt("mission_prog_${mission.id}", mission.currentProgress)
            val claimed = prefs.getBoolean("mission_claimed_${mission.id}", false)
            mission.copy(currentProgress = prog, isClaimed = claimed)
        }
    }

    private fun saveMissions(missions: List<Mission>) {
        val editor = prefs.edit()
        missions.forEach {
            editor.putInt("mission_prog_${it.id}", it.currentProgress)
            editor.putBoolean("mission_claimed_${it.id}", it.isClaimed)
        }
        editor.apply()
        _missions.value = missions
    }

    private fun loadAchievements(): List<Achievement> {
        return DEFAULT_ACHIEVEMENTS.map { ach ->
            val curr = prefs.getInt("ach_curr_${ach.id}", ach.current)
            val claimed = prefs.getBoolean("ach_claimed_${ach.id}", false)
            ach.copy(current = curr, isClaimed = claimed)
        }
    }

    private fun saveAchievements(achs: List<Achievement>) {
        val editor = prefs.edit()
        achs.forEach {
            editor.putInt("ach_curr_${it.id}", it.current)
            editor.putBoolean("ach_claimed_${it.id}", it.isClaimed)
        }
        editor.apply()
        _achievements.value = achs
    }

    fun toggleMusic(): Boolean {
        val current = _playerProfile.value
        val newVal = !current.musicEnabled
        saveProfile(current.copy(musicEnabled = newVal))
        return newVal
    }

    fun toggleSoundFx(): Boolean {
        val current = _playerProfile.value
        val newVal = !current.soundFxEnabled
        saveProfile(current.copy(soundFxEnabled = newVal))
        return newVal
    }

    fun consumeLife(): Boolean {
        refreshLives()
        val current = _playerProfile.value
        if (current.lives <= 0) return false

        val newLives = current.lives - 1
        val nextTime = if (newLives < 5 && current.nextLifeTimestamp <= System.currentTimeMillis()) {
            System.currentTimeMillis() + 15 * 60 * 1000
        } else {
            current.nextLifeTimestamp
        }

        saveProfile(current.copy(lives = newLives, nextLifeTimestamp = nextTime))
        return true
    }

    fun refillLives() {
        val current = _playerProfile.value
        if (current.coins >= 100) {
            saveProfile(current.copy(lives = 5, coins = current.coins - 100, nextLifeTimestamp = 0L))
        }
    }

    fun addCoins(amount: Int) {
        val current = _playerProfile.value
        saveProfile(current.copy(coins = current.coins + amount))
    }

    fun useBooster(type: BoosterType): Boolean {
        val current = _playerProfile.value
        return when (type) {
            BoosterType.LOLLIPOP_HAMMER -> {
                if (current.lollipopHammers > 0) {
                    saveProfile(current.copy(lollipopHammers = current.lollipopHammers - 1))
                    true
                } else if (current.coins >= type.costCoins) {
                    saveProfile(current.copy(coins = current.coins - type.costCoins))
                    true
                } else false
            }
            BoosterType.FREE_SWITCH -> {
                if (current.freeSwitches > 0) {
                    saveProfile(current.copy(freeSwitches = current.freeSwitches - 1))
                    true
                } else if (current.coins >= type.costCoins) {
                    saveProfile(current.copy(coins = current.coins - type.costCoins))
                    true
                } else false
            }
            BoosterType.COLOR_BOMB_START -> {
                if (current.colorBombs > 0) {
                    saveProfile(current.copy(colorBombs = current.colorBombs - 1))
                    true
                } else if (current.coins >= type.costCoins) {
                    saveProfile(current.copy(coins = current.coins - type.costCoins))
                    true
                } else false
            }
            BoosterType.SUGAR_LIGHTNING -> {
                if (current.sugarLightning > 0) {
                    saveProfile(current.copy(sugarLightning = current.sugarLightning - 1))
                    true
                } else if (current.coins >= type.costCoins) {
                    saveProfile(current.copy(coins = current.coins - type.costCoins))
                    true
                } else false
            }
            BoosterType.STRIPED_WRAPPED_START -> {
                if (current.coins >= type.costCoins) {
                    saveProfile(current.copy(coins = current.coins - type.costCoins))
                    true
                } else false
            }
            BoosterType.EXTRA_MOVES -> {
                if (current.extraMoves > 0) {
                    saveProfile(current.copy(extraMoves = current.extraMoves - 1))
                    true
                } else if (current.coins >= type.costCoins) {
                    saveProfile(current.copy(coins = current.coins - type.costCoins))
                    true
                } else false
            }
        }
    }

    fun buyBoosterPack(type: BoosterType, count: Int, cost: Int): Boolean {
        val current = _playerProfile.value
        if (current.coins < cost) return false

        val updated = when (type) {
            BoosterType.LOLLIPOP_HAMMER -> current.copy(lollipopHammers = current.lollipopHammers + count, coins = current.coins - cost)
            BoosterType.FREE_SWITCH -> current.copy(freeSwitches = current.freeSwitches + count, coins = current.coins - cost)
            BoosterType.COLOR_BOMB_START -> current.copy(colorBombs = current.colorBombs + count, coins = current.coins - cost)
            BoosterType.SUGAR_LIGHTNING -> current.copy(sugarLightning = current.sugarLightning + count, coins = current.coins - cost)
            BoosterType.EXTRA_MOVES -> current.copy(extraMoves = current.extraMoves + count, coins = current.coins - cost)
            else -> current.copy(coins = current.coins - cost)
        }
        saveProfile(updated)
        return true
    }

    fun buyOutfit(outfit: MascotOutfit): Boolean {
        val current = _playerProfile.value
        if (current.unlockedOutfits.contains(outfit.name)) return true
        if (current.coins < outfit.costCoins) return false

        saveProfile(
            current.copy(
                coins = current.coins - outfit.costCoins,
                unlockedOutfits = current.unlockedOutfits + outfit.name,
                currentOutfit = outfit
            )
        )
        return true
    }

    fun equipOutfit(outfit: MascotOutfit) {
        val current = _playerProfile.value
        if (current.unlockedOutfits.contains(outfit.name)) {
            saveProfile(current.copy(currentOutfit = outfit))
        }
    }

    fun claimDailyRewardDay(day: Int, rewardCoins: Int, booster: BoosterType?, boosterCount: Int) {
        val current = _playerProfile.value
        var hammers = current.lollipopHammers
        var switches = current.freeSwitches
        var bombs = current.colorBombs
        var lightning = current.sugarLightning
        var moves = current.extraMoves

        when (booster) {
            BoosterType.LOLLIPOP_HAMMER -> hammers += boosterCount
            BoosterType.FREE_SWITCH -> switches += boosterCount
            BoosterType.COLOR_BOMB_START -> bombs += boosterCount
            BoosterType.SUGAR_LIGHTNING -> lightning += boosterCount
            BoosterType.EXTRA_MOVES -> moves += boosterCount
            else -> {}
        }

        val updatedClaimed = (current.claimedRewardDays + day).distinct()
        val nextStreak = if (day >= 7) 1 else (current.dailyStreak + 1)
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())

        saveProfile(
            current.copy(
                coins = current.coins + rewardCoins,
                lollipopHammers = hammers,
                freeSwitches = switches,
                colorBombs = bombs,
                sugarLightning = lightning,
                extraMoves = moves,
                claimedRewardDays = updatedClaimed,
                dailyStreak = nextStreak,
                lastRewardClaimDate = todayStr
            )
        )
    }

    fun claimMission(id: String) {
        val currentList = _missions.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1 && currentList[index].isComplete && !currentList[index].isClaimed) {
            val mission = currentList[index]
            currentList[index] = mission.copy(isClaimed = true)
            saveMissions(currentList)
            addCoins(mission.rewardCoins)
        }
    }

    fun claimAchievement(id: String) {
        val currentList = _achievements.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1 && currentList[index].isUnlocked && !currentList[index].isClaimed) {
            val ach = currentList[index]
            currentList[index] = ach.copy(isClaimed = true)
            saveAchievements(currentList)
            addCoins(ach.rewardCoins)
        }
    }

    fun recordLevelWin(levelNumber: Int, score: Int, stars: Int) {
        scope.launch {
            val existing = levelDao.getProgressForLevel(levelNumber)
            val updatedStars = maxOf(existing?.stars ?: 0, stars)
            val updatedHighScore = maxOf(existing?.highScore ?: 0, score)

            levelDao.insertOrUpdate(
                LevelProgress(
                    levelNumber = levelNumber,
                    isUnlocked = true,
                    isCompleted = true,
                    stars = updatedStars,
                    highScore = updatedHighScore
                )
            )

            // Unlock next level
            val nextLevel = levelNumber + 1
            if (nextLevel <= 25) {
                val nextProg = levelDao.getProgressForLevel(nextLevel)
                if (nextProg == null || !nextProg.isUnlocked) {
                    levelDao.insertOrUpdate(
                        LevelProgress(
                            levelNumber = nextLevel,
                            isUnlocked = true,
                            isCompleted = false,
                            stars = 0,
                            highScore = 0
                        )
                    )
                }
            }

            // Reward coins for winning
            addCoins(50 * stars)
            updateStatsFromProgress()
            trackProgressEvents(score = score, levelCompleted = levelNumber)
        }
    }

    private suspend fun updateStatsFromProgress() {
        val all = levelDao.getAllProgressList()
        val totalStars = all.sumOf { it.stars }
        val maxUnlocked = all.filter { it.isUnlocked }.maxOfOrNull { it.levelNumber } ?: 1

        val current = _playerProfile.value
        saveProfile(current.copy(totalStars = totalStars, highestUnlockedLevel = maxUnlocked))

        // Update Achievements
        val currentAchs = _achievements.value.toMutableList()
        // a1: First level
        if (all.any { it.isCompleted }) currentAchs[0] = currentAchs[0].copy(current = 1)
        // a2: World 1 (5 levels)
        val w1Done = all.filter { it.levelNumber in 1..5 && it.isCompleted }.size
        currentAchs[1] = currentAchs[1].copy(current = w1Done)
        // a5: 20 Stars
        currentAchs[4] = currentAchs[4].copy(current = totalStars)
        // a6: Reach Episode 5
        currentAchs[5] = currentAchs[5].copy(current = maxUnlocked)
        saveAchievements(currentAchs)
    }

    fun trackProgressEvents(redCandies: Int = 0, specialsCreated: Int = 0, jelliesCleared: Int = 0, score: Int = 0, levelCompleted: Int = 0) {
        val currentMissions = _missions.value.toMutableList()
        if (redCandies > 0) currentMissions[0] = currentMissions[0].copy(currentProgress = currentMissions[0].currentProgress + redCandies)
        if (specialsCreated > 0) currentMissions[1] = currentMissions[1].copy(currentProgress = currentMissions[1].currentProgress + specialsCreated)
        if (jelliesCleared > 0) currentMissions[2] = currentMissions[2].copy(currentProgress = currentMissions[2].currentProgress + jelliesCleared)
        if (score > 0) currentMissions[4] = currentMissions[4].copy(currentProgress = currentMissions[4].currentProgress + score)
        saveMissions(currentMissions)

        if (specialsCreated > 0) {
            val achs = _achievements.value.toMutableList()
            achs[2] = achs[2].copy(current = achs[2].current + specialsCreated)
            achs[3] = achs[3].copy(current = achs[3].current + 1)
            saveAchievements(achs)
        }
    }

    fun claimDailySpinReward(rewardCoins: Int, booster: BoosterType?) {
        val current = _playerProfile.value
        var hammers = current.lollipopHammers
        var switches = current.freeSwitches
        var colorBombs = current.colorBombs
        var lightning = current.sugarLightning
        var extraMoves = current.extraMoves

        when (booster) {
            BoosterType.LOLLIPOP_HAMMER -> hammers += 1
            BoosterType.FREE_SWITCH -> switches += 1
            BoosterType.COLOR_BOMB_START -> colorBombs += 1
            BoosterType.SUGAR_LIGHTNING -> lightning += 1
            BoosterType.EXTRA_MOVES -> extraMoves += 1
            else -> {}
        }

        saveProfile(
            current.copy(
                coins = current.coins + rewardCoins,
                lollipopHammers = hammers,
                freeSwitches = switches,
                colorBombs = colorBombs,
                sugarLightning = lightning,
                extraMoves = extraMoves,
                lastDailySpinDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
            )
        )
    }

    fun refreshLives() {
        val current = _playerProfile.value
        if (current.lives >= 5) return

        val now = System.currentTimeMillis()
        if (current.nextLifeTimestamp in 1..now) {
            val elapsed = now - current.nextLifeTimestamp
            val livesGained = 1 + (elapsed / (15 * 60 * 1000)).toInt()
            val newLives = minOf(5, current.lives + livesGained)
            val newNextTime = if (newLives < 5) {
                now + (15 * 60 * 1000 - (elapsed % (15 * 60 * 1000)))
            } else {
                0L
            }
            saveProfile(current.copy(lives = newLives, nextLifeTimestamp = newNextTime))
        }
    }

    fun resetAllProgress() {
        scope.launch {
            levelDao.resetAll()
            val initialList = (1..25).map { level ->
                LevelProgress(
                    levelNumber = level,
                    isUnlocked = (level == 1),
                    isCompleted = false,
                    stars = 0,
                    highScore = 0
                )
            }
            levelDao.insertAll(initialList)
            saveProfile(
                PlayerProfile(
                    lives = 5,
                    coins = 600,
                    lollipopHammers = 3,
                    freeSwitches = 3,
                    colorBombs = 2,
                    extraMoves = 2,
                    sugarLightning = 2,
                    highestUnlockedLevel = 1,
                    totalStars = 0
                )
            )
            saveMissions(DEFAULT_MISSIONS)
            saveAchievements(DEFAULT_ACHIEVEMENTS)
        }
    }
}
