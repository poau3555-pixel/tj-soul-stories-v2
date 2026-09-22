/**
 * TJ SUGAR QUEST - Core Game Engine
 * Browser & Mobile Web Edition (HTML5, CSS3, JavaScript)
 * Fully offline, no external assets or servers needed.
 */

(function () {
  'use strict';

  // =========================================================
  // AUDIO SYNTHESIZER (Web Audio API)
  // =========================================================
  class SoundEngine {
    constructor() {
      this.ctx = null;
      this.sfxEnabled = true;
      this.musicEnabled = true;
      this.bgmTimer = null;
      this.initialized = false;
    }

    init() {
      if (this.initialized && this.ctx) {
        if (this.ctx.state === 'suspended') {
          this.ctx.resume().catch(() => {});
        }
        return;
      }
      try {
        const AudioContext = window.AudioContext || window.webkitAudioContext;
        if (AudioContext) {
          this.ctx = new AudioContext();
          if (this.ctx.state === 'suspended') {
            this.ctx.resume().catch(() => {});
          }
          this.initialized = true;
          if (this.musicEnabled) {
            this.startBgm();
          }
        }
      } catch (e) {
        console.warn('Audio init error:', e);
      }
    }

    playTone(freq, duration = 0.15, type = 'sine', gainVal = 0.22, endFreq = null) {
      if (!this.sfxEnabled) return;
      this.init();
      if (!this.ctx) return;

      try {
        const now = this.ctx.currentTime;
        const osc = this.ctx.createOscillator();
        const gain = this.ctx.createGain();

        osc.type = type;
        osc.frequency.setValueAtTime(Math.max(20, freq), now);
        if (endFreq) {
          osc.frequency.exponentialRampToValueAtTime(Math.max(20, endFreq), now + duration);
        }

        gain.gain.setValueAtTime(gainVal, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + duration);

        osc.connect(gain);
        gain.connect(this.ctx.destination);

        osc.start(now);
        osc.stop(now + duration);
      } catch (e) {}
    }

    playClick() {
      this.playTone(800, 0.04, 'triangle', 0.18, 420);
    }

    playSwap() {
      this.playTone(340, 0.08, 'sine', 0.22, 540);
    }

    playPop(comboIndex = 1) {
      if (!this.sfxEnabled) return;
      this.init();
      const base = 440 * Math.pow(1.09, Math.min(14, comboIndex));
      this.playTone(base, 0.14, 'triangle', 0.3, base * 1.35);
      if (comboIndex >= 2) {
        setTimeout(() => this.playTone(base * 1.5, 0.09, 'sine', 0.18), 30);
      }
    }

    playSpecialCreated() {
      if (!this.sfxEnabled) return;
      this.init();
      const notes = [523.25, 659.25, 783.99, 1046.50];
      notes.forEach((f, i) => {
        setTimeout(() => this.playTone(f, 0.14, 'sine', 0.26), i * 45);
      });
    }

    playExplosion() {
      if (!this.sfxEnabled) return;
      this.init();
      this.playTone(220, 0.36, 'sawtooth', 0.32, 35);
      setTimeout(() => this.playTone(130, 0.28, 'triangle', 0.28, 28), 40);
    }

    playLaser() {
      if (!this.sfxEnabled) return;
      this.init();
      this.playTone(1280, 0.18, 'sawtooth', 0.34, 160);
      setTimeout(() => this.playTone(780, 0.14, 'sine', 0.24, 110), 35);
    }

    playBomb() {
      if (!this.sfxEnabled) return;
      this.init();
      this.playTone(240, 0.38, 'sawtooth', 0.38, 28);
      setTimeout(() => this.playTone(105, 0.42, 'triangle', 0.34, 22), 45);
    }

    playZap() {
      if (!this.sfxEnabled) return;
      this.init();
      this.playTone(980, 0.12, 'sawtooth', 0.28, 320);
    }

    playChime(noteFreq = 880) {
      if (!this.sfxEnabled) return;
      this.init();
      this.playTone(noteFreq, 0.22, 'sine', 0.26);
      setTimeout(() => this.playTone(noteFreq * 1.5, 0.16, 'sine', 0.16), 40);
    }

    playComboFanfare(comboLevel = 1) {
      if (!this.sfxEnabled) return;
      this.init();
      const chords = [
        [523, 659, 783],            // Sweet!
        [587, 739, 880],            // Tasty!
        [659, 830, 987, 1174],      // Delicious!
        [783, 987, 1174, 1318, 1567] // Sugar Rush!
      ];
      const chord = chords[Math.min(comboLevel - 1, chords.length - 1)];
      chord.forEach((freq, idx) => {
        setTimeout(() => this.playTone(freq, 0.24, 'sine', 0.25), idx * 60);
      });
    }

    playWin() {
      if (!this.sfxEnabled) return;
      this.init();
      const victoryNotes = [523.25, 659.25, 783.99, 1046.50, 1318.51];
      victoryNotes.forEach((f, idx) => {
        setTimeout(() => this.playTone(f, 0.28, 'sine', 0.28), idx * 90);
      });
    }

    playGameOver() {
      if (!this.sfxEnabled) return;
      this.init();
      const sadNotes = [587.33, 523.25, 466.16, 415.30];
      sadNotes.forEach((f, idx) => {
        setTimeout(() => this.playTone(f, 0.32, 'sawtooth', 0.22), idx * 110);
      });
    }

    startBgm() {
      if (!this.musicEnabled) return;
      if (this.bgmTimer) clearInterval(this.bgmTimer);

      const pentatonic = [261.63, 293.66, 329.63, 392.00, 440.00, 523.25];
      this.bgmTimer = setInterval(() => {
        if (!this.musicEnabled) return;
        this.init();
        if (Math.random() > 0.3) {
          const note = pentatonic[Math.floor(Math.random() * pentatonic.length)];
          this.playTone(note, 0.45, 'sine', 0.04);
        }
      }, 550);
    }

    stopBgm() {
      if (this.bgmTimer) {
        clearInterval(this.bgmTimer);
        this.bgmTimer = null;
      }
    }
  }

  const sound = new SoundEngine();

  // =========================================================
  // STORAGE & PLAYER PROFILE
  // =========================================================
  const STORAGE_KEY = 'TJSugarQuest_v2_save';

  const defaultProfile = {
    highScore: 0,
    coins: 500,
    lives: 5,
    maxLives: 5,
    lastLifeTime: Date.now(),
    unlockedLevel: 1,
    levelStars: {},
    levelHighScores: {},
    lastDailyClaim: 0,
    dailyStreak: 0,
    lastDailyDate: '',
    claimedAchievements: {},
    unlockedRecipes: { 10: false, 20: false, 30: false, 40: false, 50: false, 60: false, 70: false, 80: false, 90: false, 100: false },
    stats: {
      totalLevelsWon: 0,
      totalJelliesCleared: 0,
      totalFrostingBroken: 0,
      totalSpecialsCreated: 0,
      maxCombo: 0,
      dailyChallengesCompleted: 0,
      highestSingleScore: 0
    },
    boosters: {
      hammer: 3,
      swap: 3,
      bomb: 2,
      rainbow: 2,
      moves: 2
    },
    settings: {
      sfx: true,
      music: true,
      haptic: true
    }
  };

  function loadProfile() {
    try {
      // Check both current and v1 save for backward compatibility
      const raw = localStorage.getItem(STORAGE_KEY) || localStorage.getItem('TJSugarQuest_v1_save');
      if (raw) {
        const parsed = JSON.parse(raw);
        return {
          highScore: typeof parsed.highScore === 'number' ? parsed.highScore : 0,
          coins: typeof parsed.coins === 'number' ? parsed.coins : defaultProfile.coins,
          lives: 5,
          maxLives: defaultProfile.maxLives,
          lastLifeTime: typeof parsed.lastLifeTime === 'number' ? parsed.lastLifeTime : Date.now(),
          unlockedLevel: typeof parsed.unlockedLevel === 'number' ? parsed.unlockedLevel : 1,
          levelStars: parsed.levelStars && typeof parsed.levelStars === 'object' ? parsed.levelStars : {},
          levelHighScores: parsed.levelHighScores && typeof parsed.levelHighScores === 'object' ? parsed.levelHighScores : {},
          lastDailyClaim: typeof parsed.lastDailyClaim === 'number' ? parsed.lastDailyClaim : 0,
          dailyStreak: typeof parsed.dailyStreak === 'number' ? parsed.dailyStreak : 0,
          lastDailyDate: typeof parsed.lastDailyDate === 'string' ? parsed.lastDailyDate : '',
          claimedAchievements: parsed.claimedAchievements && typeof parsed.claimedAchievements === 'object' ? parsed.claimedAchievements : {},
          unlockedRecipes: parsed.unlockedRecipes && typeof parsed.unlockedRecipes === 'object' ? parsed.unlockedRecipes : {},
          stats: Object.assign({}, defaultProfile.stats, parsed.stats || {}),
          boosters: Object.assign({}, defaultProfile.boosters, parsed.boosters || {}),
          settings: Object.assign({}, defaultProfile.settings, parsed.settings || {})
        };
      }
    } catch (e) {
      console.warn('Could not load localStorage, using defaults', e);
    }
    return JSON.parse(JSON.stringify(defaultProfile));
  }

  let profile = loadProfile();

  function saveProfile() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(profile));
    } catch (e) {
      console.warn('Could not save to localStorage', e);
    }
  }

  // Session-based life system: No automatic regeneration timers.
  // Starting a new game session restores 5 lives. Winning does not consume lives; losing consumes 1 life.
  function checkLifeRegen() {
    // Kept for backward compatibility without auto-timer
  }

  // =========================================================
  // 100 DYNAMIC LEVELS (Across 10 Sweet Worlds)
  // =========================================================
  const LEVELS = [
  {
    "level": 1,
    "world": "Candy Town",
    "worldNum": 1,
    "name": "Sugar Starter",
    "moves": 20,
    "goalType": "score",
    "layout": "border",
    "isMilestone": false,
    "starScores": [
      1900,
      2800,
      4100
    ],
    "targetScore": 1900,
    "description": "Match candies and score 1,900 points!"
  },
  {
    "level": 2,
    "world": "Candy Town",
    "worldNum": 1,
    "name": "Jelly Jubilee",
    "moves": 20,
    "goalType": "jelly",
    "layout": "default",
    "isMilestone": false,
    "starScores": [
      2300,
      3400,
      5000
    ],
    "targetScore": 2300,
    "targetJellies": 10,
    "description": "Clear 10 sweet jellies across the board!"
  },
  {
    "level": 3,
    "world": "Candy Town",
    "worldNum": 1,
    "name": "Candy Harvest",
    "moves": 20,
    "goalType": "collect",
    "layout": "pillars",
    "isMilestone": false,
    "starScores": [
      2800,
      4200,
      6100
    ],
    "targetScore": 2800,
    "collectCandies": {
      "red": 12,
      "green": 12
    },
    "description": "Collect 12 Strawberries and 12 Apples!"
  },
  {
    "level": 4,
    "world": "Candy Town",
    "worldNum": 1,
    "name": "Strawberry Glaze",
    "moves": 20,
    "goalType": "frosting",
    "layout": "center-cross",
    "isMilestone": false,
    "starScores": [
      3300,
      4900,
      7200
    ],
    "targetScore": 3300,
    "targetFrosting": 10,
    "description": "Crack 10 frosting blockers with adjacent matches!"
  },
  {
    "level": 5,
    "world": "Candy Town",
    "worldNum": 1,
    "name": "Town Jellies",
    "moves": 20,
    "goalType": "jelly",
    "layout": "diamonds",
    "isMilestone": false,
    "starScores": [
      3800,
      5700,
      8300
    ],
    "targetScore": 3800,
    "targetJellies": 10,
    "description": "Clear 10 sweet jellies across the board!"
  },
  {
    "level": 6,
    "world": "Candy Town",
    "worldNum": 1,
    "name": "Apple Orchard",
    "moves": 20,
    "goalType": "collect",
    "layout": "donut",
    "isMilestone": false,
    "starScores": [
      4300,
      6400,
      9400
    ],
    "targetScore": 4300,
    "collectCandies": {
      "red": 13,
      "green": 13
    },
    "description": "Collect 13 Strawberries and 13 Apples!"
  },
  {
    "level": 7,
    "world": "Candy Town",
    "worldNum": 1,
    "name": "Sugar Plaza",
    "moves": 20,
    "goalType": "score",
    "layout": "checker",
    "isMilestone": false,
    "starScores": [
      4800,
      7200,
      10500
    ],
    "targetScore": 4800,
    "description": "Match candies and score 4,800 points!"
  },
  {
    "level": 8,
    "world": "Candy Town",
    "worldNum": 1,
    "name": "Jelly Avenue",
    "moves": 20,
    "goalType": "jelly",
    "layout": "four-corners",
    "isMilestone": false,
    "starScores": [
      5300,
      7900,
      11600
    ],
    "targetScore": 5300,
    "targetJellies": 11,
    "description": "Clear 11 sweet jellies across the board!"
  },
  {
    "level": 9,
    "world": "Candy Town",
    "worldNum": 1,
    "name": "Confection Cross",
    "moves": 21,
    "goalType": "specials",
    "layout": "border",
    "isMilestone": false,
    "starScores": [
      5800,
      8700,
      12700
    ],
    "targetScore": 5800,
    "targetSpecials": 3,
    "description": "Craft 3 Special Candies (Striped, Bomb or Rainbow)!"
  },
  {
    "level": 10,
    "world": "Candy Town",
    "worldNum": 1,
    "name": "The Great Sugar Gate",
    "moves": 24,
    "goalType": "hybrid",
    "layout": "default",
    "isMilestone": true,
    "starScores": [
      6400,
      9600,
      14000
    ],
    "targetScore": 6400,
    "targetJellies": 13,
    "targetFrosting": 11,
    "description": "Clear 13 jellies and break 11 frosting blocks!"
  },
  {
    "level": 11,
    "world": "Lemon Lake",
    "worldNum": 2,
    "name": "Citrus Shore",
    "moves": 21,
    "goalType": "score",
    "layout": "checker",
    "isMilestone": false,
    "starScores": [
      6900,
      10300,
      15100
    ],
    "targetScore": 6900,
    "description": "Match candies and score 6,900 points!"
  },
  {
    "level": 12,
    "world": "Lemon Lake",
    "worldNum": 2,
    "name": "Frosting Bay",
    "moves": 21,
    "goalType": "frosting",
    "layout": "four-corners",
    "isMilestone": false,
    "starScores": [
      7400,
      11100,
      16200
    ],
    "targetScore": 7400,
    "targetFrosting": 11,
    "description": "Crack 11 frosting blockers with adjacent matches!"
  },
  {
    "level": 13,
    "world": "Lemon Lake",
    "worldNum": 2,
    "name": "Lemon Sparkle",
    "moves": 21,
    "goalType": "collect",
    "layout": "border",
    "isMilestone": false,
    "starScores": [
      8000,
      12000,
      17600
    ],
    "targetScore": 8000,
    "collectCandies": {
      "yellow": 14,
      "purple": 14
    },
    "description": "Collect 14 Lemons and 14 Grapes!"
  },
  {
    "level": 14,
    "world": "Lemon Lake",
    "worldNum": 2,
    "name": "Sunny Rapids",
    "moves": 21,
    "goalType": "jelly",
    "layout": "default",
    "isMilestone": false,
    "starScores": [
      8500,
      12700,
      18700
    ],
    "targetScore": 8500,
    "targetJellies": 12,
    "description": "Clear 12 sweet jellies across the board!"
  },
  {
    "level": 15,
    "world": "Lemon Lake",
    "worldNum": 2,
    "name": "Zesty Reef",
    "moves": 21,
    "goalType": "frosting",
    "layout": "pillars",
    "isMilestone": false,
    "starScores": [
      9100,
      13600,
      20000
    ],
    "targetScore": 9100,
    "targetFrosting": 12,
    "description": "Crack 12 frosting blockers with adjacent matches!"
  },
  {
    "level": 16,
    "world": "Lemon Lake",
    "worldNum": 2,
    "name": "Citron Splash",
    "moves": 21,
    "goalType": "collect",
    "layout": "center-cross",
    "isMilestone": false,
    "starScores": [
      9600,
      14400,
      21100
    ],
    "targetScore": 9600,
    "collectCandies": {
      "yellow": 15,
      "purple": 15
    },
    "description": "Collect 15 Lemons and 15 Grapes!"
  },
  {
    "level": 17,
    "world": "Lemon Lake",
    "worldNum": 2,
    "name": "Lemonade Fall",
    "moves": 22,
    "goalType": "score",
    "layout": "diamonds",
    "isMilestone": false,
    "starScores": [
      10200,
      15300,
      22400
    ],
    "targetScore": 10200,
    "description": "Match candies and score 10,200 points!"
  },
  {
    "level": 18,
    "world": "Lemon Lake",
    "worldNum": 2,
    "name": "Frosting Pier",
    "moves": 22,
    "goalType": "frosting",
    "layout": "donut",
    "isMilestone": false,
    "starScores": [
      10800,
      16200,
      23700
    ],
    "targetScore": 10800,
    "targetFrosting": 12,
    "description": "Crack 12 frosting blockers with adjacent matches!"
  },
  {
    "level": 19,
    "world": "Lemon Lake",
    "worldNum": 2,
    "name": "Tangerine Tide",
    "moves": 22,
    "goalType": "specials",
    "layout": "checker",
    "isMilestone": false,
    "starScores": [
      11300,
      16900,
      24800
    ],
    "targetScore": 11300,
    "targetSpecials": 4,
    "description": "Craft 4 Special Candies (Striped, Bomb or Rainbow)!"
  },
  {
    "level": 20,
    "world": "Lemon Lake",
    "worldNum": 2,
    "name": "Citrus Citadel",
    "moves": 25,
    "goalType": "hybrid",
    "layout": "four-corners",
    "isMilestone": true,
    "starScores": [
      11900,
      17800,
      26100
    ],
    "targetScore": 11900,
    "targetJellies": 15,
    "targetFrosting": 12,
    "description": "Clear 15 jellies and break 12 frosting blocks!"
  },
  {
    "level": 21,
    "world": "Chocolate Mountain",
    "worldNum": 3,
    "name": "Cocoa Trail",
    "moves": 22,
    "goalType": "score",
    "layout": "diamonds",
    "isMilestone": false,
    "starScores": [
      12500,
      18700,
      27500
    ],
    "targetScore": 12500,
    "description": "Match candies and score 12,500 points!"
  },
  {
    "level": 22,
    "world": "Chocolate Mountain",
    "worldNum": 3,
    "name": "Dark Truffle Slope",
    "moves": 22,
    "goalType": "jelly",
    "layout": "donut",
    "isMilestone": false,
    "starScores": [
      13100,
      19600,
      28800
    ],
    "targetScore": 13100,
    "targetJellies": 13,
    "description": "Clear 13 sweet jellies across the board!"
  },
  {
    "level": 23,
    "world": "Chocolate Mountain",
    "worldNum": 3,
    "name": "Mountain Berries",
    "moves": 22,
    "goalType": "collect",
    "layout": "checker",
    "isMilestone": false,
    "starScores": [
      13600,
      20400,
      29900
    ],
    "targetScore": 13600,
    "collectCandies": {
      "blue": 16,
      "orange": 16
    },
    "description": "Collect 16 Blueberries and 16 Oranges!"
  },
  {
    "level": 24,
    "world": "Chocolate Mountain",
    "worldNum": 3,
    "name": "Choco Avalanche",
    "moves": 22,
    "goalType": "frosting",
    "layout": "four-corners",
    "isMilestone": false,
    "starScores": [
      14200,
      21300,
      31200
    ],
    "targetScore": 14200,
    "targetFrosting": 13,
    "description": "Crack 13 frosting blockers with adjacent matches!"
  },
  {
    "level": 25,
    "world": "Chocolate Mountain",
    "worldNum": 3,
    "name": "Fudge Ridge",
    "moves": 23,
    "goalType": "jelly",
    "layout": "border",
    "isMilestone": false,
    "starScores": [
      14800,
      22200,
      32500
    ],
    "targetScore": 14800,
    "targetJellies": 14,
    "description": "Clear 14 sweet jellies across the board!"
  },
  {
    "level": 26,
    "world": "Chocolate Mountain",
    "worldNum": 3,
    "name": "Grape Crag",
    "moves": 23,
    "goalType": "collect",
    "layout": "default",
    "isMilestone": false,
    "starScores": [
      15400,
      23100,
      33800
    ],
    "targetScore": 15400,
    "collectCandies": {
      "blue": 17,
      "orange": 17
    },
    "description": "Collect 17 Blueberries and 17 Oranges!"
  },
  {
    "level": 27,
    "world": "Chocolate Mountain",
    "worldNum": 3,
    "name": "Jelly Canyon",
    "moves": 23,
    "goalType": "score",
    "layout": "pillars",
    "isMilestone": false,
    "starScores": [
      16000,
      24000,
      35200
    ],
    "targetScore": 16000,
    "description": "Match candies and score 16,000 points!"
  },
  {
    "level": 28,
    "world": "Chocolate Mountain",
    "worldNum": 3,
    "name": "Ganache Cavern",
    "moves": 23,
    "goalType": "jelly",
    "layout": "center-cross",
    "isMilestone": false,
    "starScores": [
      16600,
      24900,
      36500
    ],
    "targetScore": 16600,
    "targetJellies": 15,
    "description": "Clear 15 sweet jellies across the board!"
  },
  {
    "level": 29,
    "world": "Chocolate Mountain",
    "worldNum": 3,
    "name": "Cocoa Blast",
    "moves": 23,
    "goalType": "specials",
    "layout": "diamonds",
    "isMilestone": false,
    "starScores": [
      17200,
      25800,
      37800
    ],
    "targetScore": 17200,
    "targetSpecials": 5,
    "description": "Craft 5 Special Candies (Striped, Bomb or Rainbow)!"
  },
  {
    "level": 30,
    "world": "Chocolate Mountain",
    "worldNum": 3,
    "name": "Cocoa Caldera",
    "moves": 26,
    "goalType": "hybrid",
    "layout": "donut",
    "isMilestone": true,
    "starScores": [
      17800,
      26700,
      39100
    ],
    "targetScore": 17800,
    "targetJellies": 16,
    "targetFrosting": 14,
    "description": "Clear 16 jellies and break 14 frosting blocks!"
  },
  {
    "level": 31,
    "world": "Cookie Forest",
    "worldNum": 4,
    "name": "Gingerbread Path",
    "moves": 23,
    "goalType": "score",
    "layout": "pillars",
    "isMilestone": false,
    "starScores": [
      18400,
      27600,
      40400
    ],
    "targetScore": 18400,
    "description": "Match candies and score 18,400 points!"
  },
  {
    "level": 32,
    "world": "Cookie Forest",
    "worldNum": 4,
    "name": "Crunchy Thicket",
    "moves": 23,
    "goalType": "frosting",
    "layout": "center-cross",
    "isMilestone": false,
    "starScores": [
      19000,
      28500,
      41800
    ],
    "targetScore": 19000,
    "targetFrosting": 15,
    "description": "Crack 15 frosting blockers with adjacent matches!"
  },
  {
    "level": 33,
    "world": "Cookie Forest",
    "worldNum": 4,
    "name": "Forest Harvest",
    "moves": 23,
    "goalType": "collect",
    "layout": "diamonds",
    "isMilestone": false,
    "starScores": [
      19600,
      29400,
      43100
    ],
    "targetScore": 19600,
    "collectCandies": {
      "red": 18,
      "green": 18
    },
    "description": "Collect 18 Strawberries and 18 Apples!"
  },
  {
    "level": 34,
    "world": "Cookie Forest",
    "worldNum": 4,
    "name": "Waffle Grove",
    "moves": 24,
    "goalType": "jelly",
    "layout": "donut",
    "isMilestone": false,
    "starScores": [
      20200,
      30300,
      44400
    ],
    "targetScore": 20200,
    "targetJellies": 16,
    "description": "Clear 16 sweet jellies across the board!"
  },
  {
    "level": 35,
    "world": "Cookie Forest",
    "worldNum": 4,
    "name": "Shortbread Meadow",
    "moves": 24,
    "goalType": "frosting",
    "layout": "checker",
    "isMilestone": false,
    "starScores": [
      20800,
      31200,
      45700
    ],
    "targetScore": 20800,
    "targetFrosting": 15,
    "description": "Crack 15 frosting blockers with adjacent matches!"
  },
  {
    "level": 36,
    "world": "Cookie Forest",
    "worldNum": 4,
    "name": "Cookie Canopy",
    "moves": 24,
    "goalType": "collect",
    "layout": "four-corners",
    "isMilestone": false,
    "starScores": [
      21400,
      32100,
      47000
    ],
    "targetScore": 21400,
    "collectCandies": {
      "red": 19,
      "green": 19
    },
    "description": "Collect 19 Strawberries and 19 Apples!"
  },
  {
    "level": 37,
    "world": "Cookie Forest",
    "worldNum": 4,
    "name": "Sugar Bark Trail",
    "moves": 24,
    "goalType": "score",
    "layout": "border",
    "isMilestone": false,
    "starScores": [
      22100,
      33100,
      48600
    ],
    "targetScore": 22100,
    "description": "Match candies and score 22,100 points!"
  },
  {
    "level": 38,
    "world": "Cookie Forest",
    "worldNum": 4,
    "name": "Macaron Glade",
    "moves": 24,
    "goalType": "frosting",
    "layout": "default",
    "isMilestone": false,
    "starScores": [
      22700,
      34000,
      49900
    ],
    "targetScore": 22700,
    "targetFrosting": 16,
    "description": "Crack 16 frosting blockers with adjacent matches!"
  },
  {
    "level": 39,
    "world": "Cookie Forest",
    "worldNum": 4,
    "name": "Crispy Hollow",
    "moves": 24,
    "goalType": "specials",
    "layout": "pillars",
    "isMilestone": false,
    "starScores": [
      23300,
      34900,
      51200
    ],
    "targetScore": 23300,
    "targetSpecials": 6,
    "description": "Craft 6 Special Candies (Striped, Bomb or Rainbow)!"
  },
  {
    "level": 40,
    "world": "Cookie Forest",
    "worldNum": 4,
    "name": "Ancient Cookie Tree",
    "moves": 27,
    "goalType": "hybrid",
    "layout": "center-cross",
    "isMilestone": true,
    "starScores": [
      23900,
      35800,
      52500
    ],
    "targetScore": 23900,
    "targetJellies": 18,
    "targetFrosting": 15,
    "description": "Clear 18 jellies and break 15 frosting blocks!"
  },
  {
    "level": 41,
    "world": "Rainbow Meadow",
    "worldNum": 5,
    "name": "Prism Blossom",
    "moves": 24,
    "goalType": "score",
    "layout": "border",
    "isMilestone": false,
    "starScores": [
      24600,
      36900,
      54100
    ],
    "targetScore": 24600,
    "description": "Match candies and score 24,600 points!"
  },
  {
    "level": 42,
    "world": "Rainbow Meadow",
    "worldNum": 5,
    "name": "Color Cascade",
    "moves": 25,
    "goalType": "jelly",
    "layout": "default",
    "isMilestone": false,
    "starScores": [
      25200,
      37800,
      55400
    ],
    "targetScore": 25200,
    "targetJellies": 17,
    "description": "Clear 17 sweet jellies across the board!"
  },
  {
    "level": 43,
    "world": "Rainbow Meadow",
    "worldNum": 5,
    "name": "Rainbow Swirl",
    "moves": 25,
    "goalType": "collect",
    "layout": "pillars",
    "isMilestone": false,
    "starScores": [
      25800,
      38700,
      56700
    ],
    "targetScore": 25800,
    "collectCandies": {
      "yellow": 20,
      "purple": 20
    },
    "description": "Collect 20 Lemons and 20 Grapes!"
  },
  {
    "level": 44,
    "world": "Rainbow Meadow",
    "worldNum": 5,
    "name": "Spectrum Valley",
    "moves": 25,
    "goalType": "frosting",
    "layout": "center-cross",
    "isMilestone": false,
    "starScores": [
      26400,
      39600,
      58000
    ],
    "targetScore": 26400,
    "targetFrosting": 17,
    "description": "Crack 17 frosting blockers with adjacent matches!"
  },
  {
    "level": 45,
    "world": "Rainbow Meadow",
    "worldNum": 5,
    "name": "Chroma Frost",
    "moves": 25,
    "goalType": "jelly",
    "layout": "diamonds",
    "isMilestone": false,
    "starScores": [
      27100,
      40600,
      59600
    ],
    "targetScore": 27100,
    "targetJellies": 18,
    "description": "Clear 18 sweet jellies across the board!"
  },
  {
    "level": 46,
    "world": "Rainbow Meadow",
    "worldNum": 5,
    "name": "Iris Garden",
    "moves": 25,
    "goalType": "collect",
    "layout": "donut",
    "isMilestone": false,
    "starScores": [
      27700,
      41500,
      60900
    ],
    "targetScore": 27700,
    "collectCandies": {
      "yellow": 21,
      "purple": 21
    },
    "description": "Collect 21 Lemons and 21 Grapes!"
  },
  {
    "level": 47,
    "world": "Rainbow Meadow",
    "worldNum": 5,
    "name": "Rainbow Brook",
    "moves": 25,
    "goalType": "score",
    "layout": "checker",
    "isMilestone": false,
    "starScores": [
      28400,
      42600,
      62400
    ],
    "targetScore": 28400,
    "description": "Match candies and score 28,400 points!"
  },
  {
    "level": 48,
    "world": "Rainbow Meadow",
    "worldNum": 5,
    "name": "Kaleidoscope",
    "moves": 25,
    "goalType": "jelly",
    "layout": "four-corners",
    "isMilestone": false,
    "starScores": [
      29000,
      43500,
      63800
    ],
    "targetScore": 29000,
    "targetJellies": 18,
    "description": "Clear 18 sweet jellies across the board!"
  },
  {
    "level": 49,
    "world": "Rainbow Meadow",
    "worldNum": 5,
    "name": "Prism Storm",
    "moves": 25,
    "goalType": "specials",
    "layout": "border",
    "isMilestone": false,
    "starScores": [
      29600,
      44400,
      65100
    ],
    "targetScore": 29600,
    "targetSpecials": 6,
    "description": "Craft 6 Special Candies (Striped, Bomb or Rainbow)!"
  },
  {
    "level": 50,
    "world": "Rainbow Meadow",
    "worldNum": 5,
    "name": "Prism Palace",
    "moves": 29,
    "goalType": "hybrid",
    "layout": "default",
    "isMilestone": true,
    "starScores": [
      30300,
      45400,
      66600
    ],
    "targetScore": 30300,
    "targetJellies": 20,
    "targetFrosting": 17,
    "description": "Clear 20 jellies and break 17 frosting blocks!"
  },
  {
    "level": 51,
    "world": "Cotton Candy Peaks",
    "worldNum": 6,
    "name": "Fluffy Foothills",
    "moves": 26,
    "goalType": "score",
    "layout": "checker",
    "isMilestone": false,
    "starScores": [
      30900,
      46300,
      67900
    ],
    "targetScore": 30900,
    "description": "Match candies and score 30,900 points!"
  },
  {
    "level": 52,
    "world": "Cotton Candy Peaks",
    "worldNum": 6,
    "name": "Sugar Mist",
    "moves": 26,
    "goalType": "frosting",
    "layout": "four-corners",
    "isMilestone": false,
    "starScores": [
      31600,
      47400,
      69500
    ],
    "targetScore": 31600,
    "targetFrosting": 18,
    "description": "Crack 18 frosting blockers with adjacent matches!"
  },
  {
    "level": 53,
    "world": "Cotton Candy Peaks",
    "worldNum": 6,
    "name": "Pink Spun Cloud",
    "moves": 26,
    "goalType": "collect",
    "layout": "border",
    "isMilestone": false,
    "starScores": [
      32200,
      48300,
      70800
    ],
    "targetScore": 32200,
    "collectCandies": {
      "blue": 22,
      "orange": 22
    },
    "description": "Collect 22 Blueberries and 22 Oranges!"
  },
  {
    "level": 54,
    "world": "Cotton Candy Peaks",
    "worldNum": 6,
    "name": "Breeze Crest",
    "moves": 26,
    "goalType": "jelly",
    "layout": "default",
    "isMilestone": false,
    "starScores": [
      32900,
      49300,
      72300
    ],
    "targetScore": 32900,
    "targetJellies": 19,
    "description": "Clear 19 sweet jellies across the board!"
  },
  {
    "level": 55,
    "world": "Cotton Candy Peaks",
    "worldNum": 6,
    "name": "Cotton Loft",
    "moves": 26,
    "goalType": "frosting",
    "layout": "pillars",
    "isMilestone": false,
    "starScores": [
      33500,
      50200,
      73700
    ],
    "targetScore": 33500,
    "targetFrosting": 18,
    "description": "Crack 18 frosting blockers with adjacent matches!"
  },
  {
    "level": 56,
    "world": "Cotton Candy Peaks",
    "worldNum": 6,
    "name": "Zephyr Glaze",
    "moves": 26,
    "goalType": "collect",
    "layout": "center-cross",
    "isMilestone": false,
    "starScores": [
      34200,
      51300,
      75200
    ],
    "targetScore": 34200,
    "collectCandies": {
      "blue": 23,
      "orange": 23
    },
    "description": "Collect 23 Blueberries and 23 Oranges!"
  },
  {
    "level": 57,
    "world": "Cotton Candy Peaks",
    "worldNum": 6,
    "name": "Altitude Harvest",
    "moves": 26,
    "goalType": "score",
    "layout": "diamonds",
    "isMilestone": false,
    "starScores": [
      34800,
      52200,
      76500
    ],
    "targetScore": 34800,
    "description": "Match candies and score 34,800 points!"
  },
  {
    "level": 58,
    "world": "Cotton Candy Peaks",
    "worldNum": 6,
    "name": "Cumulus Frost",
    "moves": 26,
    "goalType": "frosting",
    "layout": "donut",
    "isMilestone": false,
    "starScores": [
      35500,
      53200,
      78100
    ],
    "targetScore": 35500,
    "targetFrosting": 19,
    "description": "Crack 19 frosting blockers with adjacent matches!"
  },
  {
    "level": 59,
    "world": "Cotton Candy Peaks",
    "worldNum": 6,
    "name": "Cotton Whirlwind",
    "moves": 27,
    "goalType": "specials",
    "layout": "checker",
    "isMilestone": false,
    "starScores": [
      36200,
      54300,
      79600
    ],
    "targetScore": 36200,
    "targetSpecials": 7,
    "description": "Craft 7 Special Candies (Striped, Bomb or Rainbow)!"
  },
  {
    "level": 60,
    "world": "Cotton Candy Peaks",
    "worldNum": 6,
    "name": "Cotton Cloud Spire",
    "moves": 30,
    "goalType": "hybrid",
    "layout": "four-corners",
    "isMilestone": true,
    "starScores": [
      36800,
      55200,
      80900
    ],
    "targetScore": 36800,
    "targetJellies": 21,
    "targetFrosting": 18,
    "description": "Clear 21 jellies and break 18 frosting blocks!"
  },
  {
    "level": 61,
    "world": "Caramel Canyon",
    "worldNum": 7,
    "name": "Toffee Gorge",
    "moves": 27,
    "goalType": "score",
    "layout": "diamonds",
    "isMilestone": false,
    "starScores": [
      37500,
      56200,
      82500
    ],
    "targetScore": 37500,
    "description": "Match candies and score 37,500 points!"
  },
  {
    "level": 62,
    "world": "Caramel Canyon",
    "worldNum": 7,
    "name": "Caramel Crevasse",
    "moves": 27,
    "goalType": "jelly",
    "layout": "donut",
    "isMilestone": false,
    "starScores": [
      38200,
      57300,
      84000
    ],
    "targetScore": 38200,
    "targetJellies": 21,
    "description": "Clear 21 sweet jellies across the board!"
  },
  {
    "level": 63,
    "world": "Caramel Canyon",
    "worldNum": 7,
    "name": "Butterscotch Bluff",
    "moves": 27,
    "goalType": "collect",
    "layout": "checker",
    "isMilestone": false,
    "starScores": [
      38800,
      58200,
      85300
    ],
    "targetScore": 38800,
    "collectCandies": {
      "red": 24,
      "green": 24
    },
    "description": "Collect 24 Strawberries and 24 Apples!"
  },
  {
    "level": 64,
    "world": "Caramel Canyon",
    "worldNum": 7,
    "name": "Sticky Chasm",
    "moves": 27,
    "goalType": "frosting",
    "layout": "four-corners",
    "isMilestone": false,
    "starScores": [
      39500,
      59200,
      86900
    ],
    "targetScore": 39500,
    "targetFrosting": 20,
    "description": "Crack 20 frosting blockers with adjacent matches!"
  },
  {
    "level": 65,
    "world": "Caramel Canyon",
    "worldNum": 7,
    "name": "Amber Rapids",
    "moves": 27,
    "goalType": "jelly",
    "layout": "border",
    "isMilestone": false,
    "starScores": [
      40200,
      60300,
      88400
    ],
    "targetScore": 40200,
    "targetJellies": 21,
    "description": "Clear 21 sweet jellies across the board!"
  },
  {
    "level": 66,
    "world": "Caramel Canyon",
    "worldNum": 7,
    "name": "Toffee Terraces",
    "moves": 27,
    "goalType": "collect",
    "layout": "default",
    "isMilestone": false,
    "starScores": [
      40800,
      61200,
      89700
    ],
    "targetScore": 40800,
    "collectCandies": {
      "red": 25,
      "green": 25
    },
    "description": "Collect 25 Strawberries and 25 Apples!"
  },
  {
    "level": 67,
    "world": "Caramel Canyon",
    "worldNum": 7,
    "name": "Golden Caramel",
    "moves": 28,
    "goalType": "score",
    "layout": "pillars",
    "isMilestone": false,
    "starScores": [
      41500,
      62200,
      91300
    ],
    "targetScore": 41500,
    "description": "Match candies and score 41,500 points!"
  },
  {
    "level": 68,
    "world": "Caramel Canyon",
    "worldNum": 7,
    "name": "Molasses Ravine",
    "moves": 28,
    "goalType": "jelly",
    "layout": "center-cross",
    "isMilestone": false,
    "starScores": [
      42200,
      63300,
      92800
    ],
    "targetScore": 42200,
    "targetJellies": 22,
    "description": "Clear 22 sweet jellies across the board!"
  },
  {
    "level": 69,
    "world": "Caramel Canyon",
    "worldNum": 7,
    "name": "Caramel Cascade",
    "moves": 28,
    "goalType": "specials",
    "layout": "diamonds",
    "isMilestone": false,
    "starScores": [
      42900,
      64300,
      94300
    ],
    "targetScore": 42900,
    "targetSpecials": 8,
    "description": "Craft 8 Special Candies (Striped, Bomb or Rainbow)!"
  },
  {
    "level": 70,
    "world": "Caramel Canyon",
    "worldNum": 7,
    "name": "Caramel Falls",
    "moves": 31,
    "goalType": "hybrid",
    "layout": "donut",
    "isMilestone": true,
    "starScores": [
      43500,
      65200,
      95700
    ],
    "targetScore": 43500,
    "targetJellies": 23,
    "targetFrosting": 19,
    "description": "Clear 23 jellies and break 19 frosting blocks!"
  },
  {
    "level": 71,
    "world": "Gummy Lagoon",
    "worldNum": 8,
    "name": "Gummy Shore",
    "moves": 28,
    "goalType": "score",
    "layout": "pillars",
    "isMilestone": false,
    "starScores": [
      44200,
      66300,
      97200
    ],
    "targetScore": 44200,
    "description": "Match candies and score 44,200 points!"
  },
  {
    "level": 72,
    "world": "Gummy Lagoon",
    "worldNum": 8,
    "name": "Gelatin Bay",
    "moves": 28,
    "goalType": "frosting",
    "layout": "center-cross",
    "isMilestone": false,
    "starScores": [
      44900,
      67300,
      98700
    ],
    "targetScore": 44900,
    "targetFrosting": 21,
    "description": "Crack 21 frosting blockers with adjacent matches!"
  },
  {
    "level": 73,
    "world": "Gummy Lagoon",
    "worldNum": 8,
    "name": "Chewy Coral",
    "moves": 28,
    "goalType": "collect",
    "layout": "diamonds",
    "isMilestone": false,
    "starScores": [
      45600,
      68400,
      100300
    ],
    "targetScore": 45600,
    "collectCandies": {
      "yellow": 26,
      "purple": 26
    },
    "description": "Collect 26 Lemons and 26 Grapes!"
  },
  {
    "level": 74,
    "world": "Gummy Lagoon",
    "worldNum": 8,
    "name": "Gummy Tidepool",
    "moves": 28,
    "goalType": "jelly",
    "layout": "donut",
    "isMilestone": false,
    "starScores": [
      46300,
      69400,
      101800
    ],
    "targetScore": 46300,
    "targetJellies": 23,
    "description": "Clear 23 sweet jellies across the board!"
  },
  {
    "level": 75,
    "world": "Gummy Lagoon",
    "worldNum": 8,
    "name": "Bouncy Reef",
    "moves": 29,
    "goalType": "frosting",
    "layout": "checker",
    "isMilestone": false,
    "starScores": [
      46900,
      70300,
      103100
    ],
    "targetScore": 46900,
    "targetFrosting": 22,
    "description": "Crack 22 frosting blockers with adjacent matches!"
  },
  {
    "level": 76,
    "world": "Gummy Lagoon",
    "worldNum": 8,
    "name": "Jellyfish Cove",
    "moves": 29,
    "goalType": "collect",
    "layout": "four-corners",
    "isMilestone": false,
    "starScores": [
      47600,
      71400,
      104700
    ],
    "targetScore": 47600,
    "collectCandies": {
      "yellow": 27,
      "purple": 27
    },
    "description": "Collect 27 Lemons and 27 Grapes!"
  },
  {
    "level": 77,
    "world": "Gummy Lagoon",
    "worldNum": 8,
    "name": "Berry Gummy Trench",
    "moves": 29,
    "goalType": "score",
    "layout": "border",
    "isMilestone": false,
    "starScores": [
      48300,
      72400,
      106200
    ],
    "targetScore": 48300,
    "description": "Match candies and score 48,300 points!"
  },
  {
    "level": 78,
    "world": "Gummy Lagoon",
    "worldNum": 8,
    "name": "Gumdrop Atoll",
    "moves": 29,
    "goalType": "frosting",
    "layout": "default",
    "isMilestone": false,
    "starScores": [
      49000,
      73500,
      107800
    ],
    "targetScore": 49000,
    "targetFrosting": 22,
    "description": "Crack 22 frosting blockers with adjacent matches!"
  },
  {
    "level": 79,
    "world": "Gummy Lagoon",
    "worldNum": 8,
    "name": "Taffy Whirlpool",
    "moves": 29,
    "goalType": "specials",
    "layout": "pillars",
    "isMilestone": false,
    "starScores": [
      49700,
      74500,
      109300
    ],
    "targetScore": 49700,
    "targetSpecials": 9,
    "description": "Craft 9 Special Candies (Striped, Bomb or Rainbow)!"
  },
  {
    "level": 80,
    "world": "Gummy Lagoon",
    "worldNum": 8,
    "name": "Gummy Kraken",
    "moves": 32,
    "goalType": "hybrid",
    "layout": "center-cross",
    "isMilestone": true,
    "starScores": [
      50400,
      75600,
      110800
    ],
    "targetScore": 50400,
    "targetJellies": 24,
    "targetFrosting": 21,
    "description": "Clear 24 jellies and break 21 frosting blocks!"
  },
  {
    "level": 81,
    "world": "Starlight Bakery",
    "worldNum": 9,
    "name": "Cosmic Dough",
    "moves": 29,
    "goalType": "score",
    "layout": "border",
    "isMilestone": false,
    "starScores": [
      51100,
      76600,
      112400
    ],
    "targetScore": 51100,
    "description": "Match candies and score 51,100 points!"
  },
  {
    "level": 82,
    "world": "Starlight Bakery",
    "worldNum": 9,
    "name": "Nebula Frosting",
    "moves": 29,
    "goalType": "jelly",
    "layout": "default",
    "isMilestone": false,
    "starScores": [
      51800,
      77700,
      113900
    ],
    "targetScore": 51800,
    "targetJellies": 24,
    "description": "Clear 24 sweet jellies across the board!"
  },
  {
    "level": 83,
    "world": "Starlight Bakery",
    "worldNum": 9,
    "name": "Stardust Sprinkles",
    "moves": 29,
    "goalType": "collect",
    "layout": "pillars",
    "isMilestone": false,
    "starScores": [
      52500,
      78700,
      115500
    ],
    "targetScore": 52500,
    "collectCandies": {
      "blue": 28,
      "orange": 28
    },
    "description": "Collect 28 Blueberries and 28 Oranges!"
  },
  {
    "level": 84,
    "world": "Starlight Bakery",
    "worldNum": 9,
    "name": "Supernova Glaze",
    "moves": 30,
    "goalType": "frosting",
    "layout": "center-cross",
    "isMilestone": false,
    "starScores": [
      53200,
      79800,
      117000
    ],
    "targetScore": 53200,
    "targetFrosting": 23,
    "description": "Crack 23 frosting blockers with adjacent matches!"
  },
  {
    "level": 85,
    "world": "Starlight Bakery",
    "worldNum": 9,
    "name": "Astral Scone",
    "moves": 30,
    "goalType": "jelly",
    "layout": "diamonds",
    "isMilestone": false,
    "starScores": [
      53900,
      80800,
      118500
    ],
    "targetScore": 53900,
    "targetJellies": 25,
    "description": "Clear 25 sweet jellies across the board!"
  },
  {
    "level": 86,
    "world": "Starlight Bakery",
    "worldNum": 9,
    "name": "Galaxy Cupcake",
    "moves": 30,
    "goalType": "collect",
    "layout": "donut",
    "isMilestone": false,
    "starScores": [
      54600,
      81900,
      120100
    ],
    "targetScore": 54600,
    "collectCandies": {
      "blue": 29,
      "orange": 29
    },
    "description": "Collect 29 Blueberries and 29 Oranges!"
  },
  {
    "level": 87,
    "world": "Starlight Bakery",
    "worldNum": 9,
    "name": "Meteor Crumb",
    "moves": 30,
    "goalType": "score",
    "layout": "checker",
    "isMilestone": false,
    "starScores": [
      55300,
      82900,
      121600
    ],
    "targetScore": 55300,
    "description": "Match candies and score 55,300 points!"
  },
  {
    "level": 88,
    "world": "Starlight Bakery",
    "worldNum": 9,
    "name": "Constellation Tart",
    "moves": 30,
    "goalType": "jelly",
    "layout": "four-corners",
    "isMilestone": false,
    "starScores": [
      56000,
      84000,
      123200
    ],
    "targetScore": 56000,
    "targetJellies": 25,
    "description": "Clear 25 sweet jellies across the board!"
  },
  {
    "level": 89,
    "world": "Starlight Bakery",
    "worldNum": 9,
    "name": "Orbit Souffl\u00e9",
    "moves": 30,
    "goalType": "specials",
    "layout": "border",
    "isMilestone": false,
    "starScores": [
      56700,
      85000,
      124700
    ],
    "targetScore": 56700,
    "targetSpecials": 10,
    "description": "Craft 10 Special Candies (Striped, Bomb or Rainbow)!"
  },
  {
    "level": 90,
    "world": "Starlight Bakery",
    "worldNum": 9,
    "name": "Celestial Oven",
    "moves": 33,
    "goalType": "hybrid",
    "layout": "default",
    "isMilestone": true,
    "starScores": [
      57400,
      86100,
      126200
    ],
    "targetScore": 57400,
    "targetJellies": 26,
    "targetFrosting": 22,
    "description": "Clear 26 jellies and break 22 frosting blocks!"
  },
  {
    "level": 91,
    "world": "Sugar Kingdom",
    "worldNum": 10,
    "name": "Royal Courtyard",
    "moves": 30,
    "goalType": "score",
    "layout": "checker",
    "isMilestone": false,
    "starScores": [
      58100,
      87100,
      127800
    ],
    "targetScore": 58100,
    "description": "Match candies and score 58,100 points!"
  },
  {
    "level": 92,
    "world": "Sugar Kingdom",
    "worldNum": 10,
    "name": "Imperial Icing",
    "moves": 31,
    "goalType": "frosting",
    "layout": "four-corners",
    "isMilestone": false,
    "starScores": [
      58800,
      88200,
      129300
    ],
    "targetScore": 58800,
    "targetFrosting": 24,
    "description": "Crack 24 frosting blockers with adjacent matches!"
  },
  {
    "level": 93,
    "world": "Sugar Kingdom",
    "worldNum": 10,
    "name": "King's Harvest",
    "moves": 31,
    "goalType": "collect",
    "layout": "border",
    "isMilestone": false,
    "starScores": [
      59500,
      89200,
      130900
    ],
    "targetScore": 59500,
    "collectCandies": {
      "red": 30,
      "green": 30
    },
    "description": "Collect 30 Strawberries and 30 Apples!"
  },
  {
    "level": 94,
    "world": "Sugar Kingdom",
    "worldNum": 10,
    "name": "Palace Tapestry",
    "moves": 31,
    "goalType": "jelly",
    "layout": "default",
    "isMilestone": false,
    "starScores": [
      60200,
      90300,
      132400
    ],
    "targetScore": 60200,
    "targetJellies": 26,
    "description": "Clear 26 sweet jellies across the board!"
  },
  {
    "level": 95,
    "world": "Sugar Kingdom",
    "worldNum": 10,
    "name": "Crown Jewels",
    "moves": 31,
    "goalType": "frosting",
    "layout": "pillars",
    "isMilestone": false,
    "starScores": [
      60900,
      91300,
      133900
    ],
    "targetScore": 60900,
    "targetFrosting": 25,
    "description": "Crack 25 frosting blockers with adjacent matches!"
  },
  {
    "level": 96,
    "world": "Sugar Kingdom",
    "worldNum": 10,
    "name": "Throne of Sugar",
    "moves": 31,
    "goalType": "collect",
    "layout": "center-cross",
    "isMilestone": false,
    "starScores": [
      61600,
      92400,
      135500
    ],
    "targetScore": 61600,
    "collectCandies": {
      "red": 31,
      "green": 31
    },
    "description": "Collect 31 Strawberries and 31 Apples!"
  },
  {
    "level": 97,
    "world": "Sugar Kingdom",
    "worldNum": 10,
    "name": "Ch\u00e2teau Bonbon",
    "moves": 31,
    "goalType": "score",
    "layout": "diamonds",
    "isMilestone": false,
    "starScores": [
      62400,
      93600,
      137200
    ],
    "targetScore": 62400,
    "description": "Match candies and score 62,400 points!"
  },
  {
    "level": 98,
    "world": "Sugar Kingdom",
    "worldNum": 10,
    "name": "Royal Confectionery",
    "moves": 31,
    "goalType": "frosting",
    "layout": "donut",
    "isMilestone": false,
    "starScores": [
      63100,
      94600,
      138800
    ],
    "targetScore": 63100,
    "targetFrosting": 25,
    "description": "Crack 25 frosting blockers with adjacent matches!"
  },
  {
    "level": 99,
    "world": "Sugar Kingdom",
    "worldNum": 10,
    "name": "Sovereign Cascade",
    "moves": 31,
    "goalType": "specials",
    "layout": "checker",
    "isMilestone": false,
    "starScores": [
      63800,
      95700,
      140300
    ],
    "targetScore": 63800,
    "targetSpecials": 10,
    "description": "Craft 10 Special Candies (Striped, Bomb or Rainbow)!"
  },
  {
    "level": 100,
    "world": "Sugar Kingdom",
    "worldNum": 10,
    "name": "The Grand Sugar Crown Finale",
    "moves": 35,
    "goalType": "hybrid",
    "layout": "four-corners",
    "isMilestone": true,
    "starScores": [
      64500,
      96700,
      141900
    ],
    "targetScore": 64500,
    "targetJellies": 28,
    "targetFrosting": 24,
    "description": "Clear 28 jellies & 24 frosting, and score 64,500 pts in the Grand Finale!"
  }
];

  // 6 CANDY TYPES
  const CANDY_TYPES = [
    { id: 'red', emoji: '🍓', class: 'candy-red', name: 'Strawberry' },
    { id: 'orange', emoji: '🍊', class: 'candy-orange', name: 'Orange' },
    { id: 'yellow', emoji: '🍋', class: 'candy-yellow', name: 'Lemon' },
    { id: 'green', emoji: '🍏', class: 'candy-green', name: 'Apple' },
    { id: 'blue', emoji: '🫐', class: 'candy-blue', name: 'Blueberry' },
    { id: 'purple', emoji: '🍇', class: 'candy-purple', name: 'Grape' }
  ];

  // =========================================================
  // DOM REFERENCES
  // =========================================================
  const dom = {
    // Screens
    screenMainMenu: document.getElementById('screen-main-menu'),
    screenLevels: document.getElementById('screen-levels'),
    screenGame: document.getElementById('screen-game'),

    // HUD
    hudLives: document.getElementById('hud-lives'),
    hudCoins: document.getElementById('hud-coins'),
    hudLivesBtn: document.getElementById('hud-lives-btn'),
    hudCoinsBtn: document.getElementById('hud-coins-btn'),
    soundBtn: document.getElementById('sound-btn'),
    soundIcon: document.getElementById('sound-icon'),
    menuHighScore: document.getElementById('menu-high-score'),

    // Menu Buttons
    btnPlayQuick: document.getElementById('btn-play-quick'),
    btnPlayText: document.getElementById('btn-play-text'),
    btnOpenLevels: document.getElementById('btn-open-levels'),
    btnOpenDaily: document.getElementById('btn-open-daily'),
    btnOpenAchievements: document.getElementById('btn-open-achievements'),
    btnOpenCollection: document.getElementById('btn-open-collection'),
    btnOpenHowToPlay: document.getElementById('btn-open-how-to-play'),
    btnOpenShop: document.getElementById('btn-open-shop'),
    btnOpenSettings: document.getElementById('btn-open-settings'),
    mascotBubble: document.getElementById('mascot-bubble'),

    // Levels Screen
    levelsContainer: document.getElementById('levels-container'),
    levelsBackBtn: document.getElementById('levels-back-btn'),
    levelsTotalStars: document.getElementById('levels-total-stars'),
    worldTabsContainer: document.getElementById('world-tabs-container'),

    // Game Board & HUD
    gameBoard: document.getElementById('game-board'),
    floatingScoresLayer: document.getElementById('floating-scores-layer'),
    gameLevelName: document.getElementById('game-level-name'),
    gameMoves: document.getElementById('game-moves'),
    gameGoalText: document.getElementById('goal-text'),
    gameGoalIcon: document.getElementById('goal-icon'),
    gameScore: document.getElementById('game-score'),
    scoreProgressFill: document.getElementById('score-progress-fill'),
    starSlots: [
      document.getElementById('star-1'),
      document.getElementById('star-2'),
      document.getElementById('star-3')
    ],
    comboBanner: document.getElementById('combo-banner'),
    gamePauseBtn: document.getElementById('game-pause-btn'),
    gameRestartQuickBtn: document.getElementById('game-restart-quick-btn'),

    // Boosters Tray
    boosterHammer: document.getElementById('booster-hammer'),
    boosterSwap: document.getElementById('booster-swap'),
    boosterBomb: document.getElementById('booster-bomb'),
    boosterRainbow: document.getElementById('booster-rainbow'),
    boosterMoves: document.getElementById('booster-moves'),
    countHammer: document.getElementById('count-hammer'),
    countSwap: document.getElementById('count-swap'),
    countBomb: document.getElementById('count-bomb'),
    countRainbow: document.getElementById('count-rainbow'),
    countMoves: document.getElementById('count-moves'),

    // Modals
    modalHowToPlay: document.getElementById('modal-how-to-play'),
    modalSettings: document.getElementById('modal-settings'),
    modalShop: document.getElementById('modal-shop'),
    modalPause: document.getElementById('modal-pause'),
    modalLevelComplete: document.getElementById('modal-level-complete'),
    modalGameOver: document.getElementById('modal-game-over'),
    modalDailyChallenge: document.getElementById('modal-daily-challenge'),
    modalAchievements: document.getElementById('modal-achievements'),
    modalCollection: document.getElementById('modal-collection'),

    // Daily Challenge elements
    dailyDateText: document.getElementById('daily-date-tag') || document.getElementById('daily-date-text'),
    dailyStreakBadge: document.getElementById('daily-streak-badge'),
    dailyModBadge: document.getElementById('daily-modifier-pill') || document.getElementById('daily-mod-badge'),
    dailyModDesc: document.getElementById('daily-puzzle-desc') || document.getElementById('daily-mod-desc'),
    dailyRewardBadge: document.querySelector('.daily-reward-box strong') || document.getElementById('daily-reward-badge'),
    btnStartDaily: document.getElementById('btn-start-daily'),

    // Achievements elements
    achievementsList: document.getElementById('achievements-list'),
    achievementsProgressSummary: document.getElementById('achievements-count') || document.getElementById('achievements-progress-summary'),

    // Collection elements
    recipesList: document.getElementById('recipes-grid') || document.getElementById('recipes-list'),
    recipesMasteredCount: document.getElementById('collection-count') || document.getElementById('recipes-mastered-count'),

    // Settings
    settingSfxToggle: document.getElementById('setting-sfx-toggle'),
    settingMusicToggle: document.getElementById('setting-music-toggle'),
    settingHapticToggle: document.getElementById('setting-haptic-toggle'),
    btnResetData: document.getElementById('btn-reset-data'),

    // Shop
    shopCoinsVal: document.getElementById('shop-coins-val'),
    btnClaimFreeCoins: document.getElementById('btn-claim-free-coins'),

    // Pause Modal Buttons
    pauseBtnResume: document.getElementById('pause-btn-resume'),
    pauseBtnRestart: document.getElementById('pause-btn-restart'),
    pauseBtnSound: document.getElementById('pause-btn-sound'),
    pauseSoundStatus: document.getElementById('pause-sound-status'),
    pauseBtnMenu: document.getElementById('pause-btn-menu'),

    // Win Modal
    winLevelTitle: document.getElementById('win-level-title'),
    winFinalScore: document.getElementById('win-final-score'),
    winCoinsEarned: document.getElementById('win-coins-earned'),
    winMovesBonus: document.getElementById('win-moves-bonus'),
    winStars: [
      document.getElementById('win-star-1'),
      document.getElementById('win-star-2'),
      document.getElementById('win-star-3')
    ],
    winBtnNext: document.getElementById('win-btn-next'),
    winBtnReplay: document.getElementById('win-btn-replay'),
    winBtnMenu: document.getElementById('win-btn-menu'),

    // Game Over Modal
    gameoverScore: document.getElementById('gameover-score'),
    gameoverTarget: document.getElementById('gameover-target'),
    gameoverBtnExtraMoves: document.getElementById('gameover-btn-extra-moves'),
    gameoverBtnRetry: document.getElementById('gameover-btn-retry'),
    gameoverBtnMenu: document.getElementById('gameover-btn-menu'),

    // Toast
    toast: document.getElementById('toast')
  };

  // =========================================================
  // TOAST & UTILITIES
  // =========================================================
  let toastTimer = null;
  function showToast(msg) {
    if (toastTimer) clearTimeout(toastTimer);
    dom.toast.textContent = msg;
    dom.toast.classList.add('show');
    toastTimer = setTimeout(() => {
      dom.toast.classList.remove('show');
    }, 2400);
  }

  function triggerHaptic(duration = 20) {
    if (profile.settings.haptic && navigator.vibrate) {
      try {
        navigator.vibrate(duration);
      } catch (e) {}
    }
  }

  function updateGlobalHUD() {
    // Lives display (5/5)
    if (dom.hudLives) dom.hudLives.textContent = `${profile.lives} / 5`;
    if (dom.hudCoins) dom.hudCoins.textContent = profile.coins.toLocaleString();
    if (dom.menuHighScore) dom.menuHighScore.textContent = profile.highScore.toLocaleString();

    const displayLvl = Math.min(profile.unlockedLevel, LEVELS.length);
    if (dom.btnPlayText) {
      dom.btnPlayText.textContent = profile.unlockedLevel > LEVELS.length
        ? 'PLAY LEVEL 100 👑'
        : `PLAY LEVEL ${displayLvl}`;
    }

    // Booster counts
    if (dom.countHammer) dom.countHammer.textContent = profile.boosters.hammer;
    if (dom.countSwap) dom.countSwap.textContent = profile.boosters.swap;
    if (dom.countBomb) dom.countBomb.textContent = profile.boosters.bomb;
    if (dom.countRainbow) dom.countRainbow.textContent = profile.boosters.rainbow;
    if (dom.countMoves) dom.countMoves.textContent = profile.boosters.moves || 0;

    // Shop coins
    if (dom.shopCoinsVal) dom.shopCoinsVal.textContent = profile.coins.toLocaleString();

    // Check Daily Free Bonus button status
    const oneDay = 24 * 60 * 60 * 1000;
    const canClaim = (Date.now() - (profile.lastDailyClaim || 0)) > oneDay;
    if (dom.btnClaimFreeCoins) {
      if (canClaim) {
        dom.btnClaimFreeCoins.disabled = false;
        dom.btnClaimFreeCoins.textContent = 'CLAIM +100 🪙';
      } else {
        dom.btnClaimFreeCoins.disabled = true;
        dom.btnClaimFreeCoins.textContent = 'CLAIMED TODAY';
      }
    }

    // Sound icons
    sound.sfxEnabled = profile.settings.sfx;
    sound.musicEnabled = profile.settings.music;
    dom.soundIcon.textContent = profile.settings.sfx ? '🔊' : '🔇';
    dom.pauseSoundStatus.textContent = profile.settings.sfx ? 'ON' : 'OFF';

    // Mascot bubble greeting
    const quotes = [
      `Ready to crush it in Level ${displayLvl}?`,
      `Sweet combos give big score bonuses!`,
      `Need a boost? Check out the Lollipop Hammer in the Shop!`,
      `Match 4 candies to forge Striped Lasers!`,
      `Match 5 in a line to create a Rainbow Core!`,
      `Swap a Striped and a Bomb for a mega blast!`
    ];
    dom.mascotBubble.textContent = `✨ Pip: ${quotes[displayLvl % quotes.length]}`;
  }

  // =========================================================
  // SCREEN SWITCHING & MODALS
  // =========================================================
  const GameState = {
    MENU: 'MENU',
    LEVELS: 'LEVELS',
    PLAYING: 'PLAYING',
    PAUSED: 'PAUSED',
    GAMEOVER: 'GAMEOVER',
    VICTORY: 'VICTORY'
  };
  let currentGameState = GameState.MENU;

  function switchScreen(targetScreen) {
    if (!targetScreen) return;
    [dom.screenMainMenu, dom.screenLevels, dom.screenGame].forEach(s => {
      if (s && s.classList) s.classList.remove('active');
    });
    targetScreen.classList.add('active');
    if (targetScreen === dom.screenGame) {
      currentGameState = GameState.PLAYING;
    } else if (targetScreen === dom.screenMainMenu) {
      currentGameState = GameState.MENU;
    } else if (targetScreen === dom.screenLevels) {
      currentGameState = GameState.LEVELS;
    }
    sound.playClick();
  }

  function startPlayLevel(levelNum) {
    try {
      sound.init();
      // Ensure player has lives to play
      if (typeof profile.lives !== 'number' || isNaN(profile.lives) || profile.lives <= 0) {
        profile.lives = 5;
        saveProfile();
        updateGlobalHUD();
      }
      const lvl = (typeof levelNum === 'number' && levelNum >= 1) ? levelNum : (profile.unlockedLevel || 1);
      const safeLvl = Math.max(1, Math.min(lvl, LEVELS.length));
      setupLevel(safeLvl - 1);
      switchScreen(dom.screenGame);
      currentGameState = GameState.PLAYING;
    } catch (e) {
      console.error('[SugarQuest] Failed to start level:', e);
      switchScreen(dom.screenGame);
      currentGameState = GameState.PLAYING;
    }
  }

  function openModal(modal) {
    if (!modal) return;
    modal.classList.add('active');
    if (modal === dom.modalPause) currentGameState = GameState.PAUSED;
    else if (modal === dom.modalGameOver) currentGameState = GameState.GAMEOVER;
    else if (modal === dom.modalLevelComplete) currentGameState = GameState.VICTORY;
    sound.playClick();
  }

  function closeModal(modal) {
    if (!modal) return;
    modal.classList.remove('active');
    if (dom.screenGame && dom.screenGame.classList.contains('active')) {
      currentGameState = GameState.PLAYING;
    } else if (dom.screenLevels && dom.screenLevels.classList.contains('active')) {
      currentGameState = GameState.LEVELS;
    } else {
      currentGameState = GameState.MENU;
    }
  }

  // Bind close buttons with [data-close]
  document.querySelectorAll('[data-close]').forEach(btn => {
    btn.addEventListener('click', () => {
      const modalId = btn.getAttribute('data-close');
      const targetModal = document.getElementById(modalId);
      if (targetModal) closeModal(targetModal);
      sound.playClick();
    });
  });

  // =========================================================
  // MATCH-3 GAME BOARD ENGINE
  // =========================================================
  const ROWS = 8;
  const COLS = 8;

  let currentLevelData = LEVELS[0];
  let board = [];
  let score = 0;
  let movesLeft = 20;
  let earnedStars = 0;
  const MoveState = {
    IDLE: 'IDLE',
    SWAPPING: 'SWAPPING',
    CHECKING_MATCHES: 'CHECKING_MATCHES',
    CLEARING: 'CLEARING',
    FALLING: 'FALLING',
    REFILLING: 'REFILLING',
    CHECKING_CASCADE: 'CHECKING_CASCADE',
    SWAP_BACK: 'SWAP_BACK'
  };
  let currentMoveState = MoveState.IDLE;
  let isProcessing = false;
  let inputLocked = false;
  const activeAnimationTimers = new Set();

  function safeWait(ms) {
    return new Promise(resolve => {
      const id = setTimeout(() => {
        activeAnimationTimers.delete(id);
        resolve();
      }, ms);
      activeAnimationTimers.add(id);
    });
  }

  function clearAllAnimationTimers() {
    activeAnimationTimers.forEach(id => clearTimeout(id));
    activeAnimationTimers.clear();
  }

  let activeBooster = null; // 'hammer' | 'swap' | 'bomb' | 'rainbow'
  let selectedTile = null;
  let swapFirstTile = null;
  let hintTimer = null;
  let moveWatchdogTimer = null;
  let currentComboCount = 0;

  // Level goal progress counters
  let remainingJellies = 0;
  let remainingFrosting = 0;
  let collectedCandies = {};
  let createdSpecials = 0;

  class Tile {
    constructor(row, col) {
      this.row = row;
      this.col = col;
      this.candy = null; // 'red', 'yellow', etc.
      this.special = null; // null | 'striped_h' | 'striped_v' | 'bomb' | 'rainbow'
      this.isJelly = false;
      this.hasFrosting = false;
      this.el = null;
      this.candyEl = null;
    }
  }

  function initBoardLayout() {
    dom.gameBoard.innerHTML = '';
    dom.gameBoard.style.gridTemplateColumns = `repeat(${COLS}, 1fr)`;
    dom.gameBoard.style.gridTemplateRows = `repeat(${ROWS}, 1fr)`;

    board = [];
    for (let r = 0; r < ROWS; r++) {
      board[r] = [];
      for (let c = 0; c < COLS; c++) {
        const tile = new Tile(r, c);

        const slot = document.createElement('div');
        slot.className = 'tile-slot';
        slot.dataset.row = r;
        slot.dataset.col = c;
        dom.gameBoard.appendChild(slot);
        tile.el = slot;

        board[r][c] = tile;
      }
    }
  }

  // Screen Shake Feedback
  function triggerScreenShake(intensity = 'mild') {
    const target = dom.gameBoard;
    if (!target) return;
    target.classList.remove('shake-mild', 'shake-medium', 'shake-strong');
    void target.offsetWidth;
    target.classList.add(`shake-${intensity}`);
    const dur = intensity === 'strong' ? 350 : (intensity === 'medium' ? 280 : 200);
    setTimeout(() => {
      target.classList.remove(`shake-${intensity}`);
    }, dur);
  }

  function safeGetRect(el) {
    if (el && typeof el.getBoundingClientRect === 'function') {
      try {
        const r = el.getBoundingClientRect();
        if (r && (r.width > 0 || r.height > 0 || r.left !== undefined)) return r;
      } catch (_) {}
    }
    return { left: 0, top: 0, width: 44, height: 44, right: 44, bottom: 44 };
  }

  // Visual Blast Effects (Shockwaves, Glossy Shards, Sparkling Particles & Laser Beams)
  function spawnCandyBlast(row, col, candyType, specialType = null, comboLevel = 1) {
    if (!dom.floatingScoresLayer || !board[row] || !board[row][col]) return;
    const tile = board[row][col];
    if (!tile.el) return;

    const tileRect = safeGetRect(tile.el);
    const boardRect = safeGetRect(dom.gameBoard);
    const cx = tileRect.left - boardRect.left + (tileRect.width || 44) / 2;
    const cy = tileRect.top - boardRect.top + (tileRect.height || 44) / 2;

    const candyColors = {
      red: '#FF1744',
      orange: '#FF6D00',
      yellow: '#FFD600',
      green: '#00E676',
      blue: '#00B0FF',
      purple: '#D500F9',
      rainbow: '#FFFFFF'
    };
    const color = candyColors[candyType] || '#FFD54F';

    // 1. Concentric Expanding Energy Shockwaves (Outer colored glow + Inner white blast)
    const ring = document.createElement('div');
    ring.className = 'energy-shockwave';
    ring.style.left = `${cx}px`;
    ring.style.top = `${cy}px`;
    const ringScale = specialType ? 1.5 : (comboLevel >= 3 ? 1.3 : 1.0);
    const ringSize = Math.max(34, tileRect.width * ringScale);
    ring.style.width = `${ringSize}px`;
    ring.style.height = `${ringSize}px`;
    ring.style.setProperty('--ring-color', color);
    dom.floatingScoresLayer.appendChild(ring);
    setTimeout(() => { if (ring.parentNode) ring.remove(); }, 460);

    const innerRing = document.createElement('div');
    innerRing.className = 'energy-shockwave-inner';
    innerRing.style.left = `${cx}px`;
    innerRing.style.top = `${cy}px`;
    innerRing.style.width = `${ringSize * 0.75}px`;
    innerRing.style.height = `${ringSize * 0.75}px`;
    dom.floatingScoresLayer.appendChild(innerRing);
    setTimeout(() => { if (innerRing.parentNode) innerRing.remove(); }, 360);

    // 2. Glossy Candy Shard Particles (Diamond crystal pieces that twirl)
    const numShards = specialType ? 8 : (comboLevel >= 3 ? 7 : 4);
    for (let i = 0; i < numShards; i++) {
      const shard = document.createElement('div');
      shard.className = 'blast-shard';
      const angle = (Math.PI * 2 / numShards) * i + (Math.random() - 0.5) * 0.6;
      const dist = (specialType ? 50 : 34) + Math.random() * 26 + (comboLevel * 4);
      const dx = Math.cos(angle) * dist;
      const dy = Math.sin(angle) * dist;
      const size = Math.floor(7 + Math.random() * 7);

      shard.style.left = `${cx}px`;
      shard.style.top = `${cy}px`;
      shard.style.width = `${size}px`;
      shard.style.height = `${size}px`;
      shard.style.backgroundColor = (candyType === 'rainbow' || Math.random() > 0.55) ? '#FFFFFF' : color;
      shard.style.color = color;
      shard.style.setProperty('--dx', `${dx}px`);
      shard.style.setProperty('--dy', `${dy}px`);

      dom.floatingScoresLayer.appendChild(shard);
      setTimeout(() => { if (shard.parentNode) shard.remove(); }, 700);
    }

    // 3. Bright Sugar Particles (6-12 colorful spheres)
    const numParticles = specialType ? 12 : (comboLevel >= 3 ? 10 : 6);
    for (let i = 0; i < numParticles; i++) {
      const p = document.createElement('div');
      p.className = 'blast-particle';
      const angle = (Math.PI * 2 / numParticles) * i + (Math.random() - 0.5) * 0.5;
      const dist = (specialType ? 45 : 28) + Math.random() * 25 + (comboLevel * 3);
      const dx = Math.cos(angle) * dist;
      const dy = Math.sin(angle) * dist;
      const size = Math.floor(5 + Math.random() * 7);

      p.style.left = `${cx}px`;
      p.style.top = `${cy}px`;
      p.style.width = `${size}px`;
      p.style.height = `${size}px`;
      p.style.borderRadius = '50%';
      p.style.backgroundColor = (candyType === 'rainbow' || Math.random() > 0.6) ? '#FFFFFF' : color;
      p.style.boxShadow = `0 0 8px ${color}`;
      p.style.setProperty('--dx', `${dx}px`);
      p.style.setProperty('--dy', `${dy}px`);

      dom.floatingScoresLayer.appendChild(p);
      setTimeout(() => { if (p.parentNode) p.remove(); }, 680);
    }

    // 4. Sparkle stars (✨, ⭐, 🌟, 💫)
    const sparkleCount = specialType ? 2 : (comboLevel >= 2 || Math.random() > 0.35 ? 1 : 0);
    const sparkleIcons = ['✨', '⭐', '🌟', '💫'];
    for (let s = 0; s < sparkleCount; s++) {
      const sp = document.createElement('div');
      sp.className = 'blast-sparkle';
      sp.textContent = sparkleIcons[Math.floor(Math.random() * sparkleIcons.length)];
      sp.style.left = `${cx}px`;
      sp.style.top = `${cy}px`;
      const angle = Math.random() * Math.PI * 2;
      const dist = 30 + Math.random() * 32;
      sp.style.setProperty('--dx', `${Math.cos(angle) * dist}px`);
      sp.style.setProperty('--dy', `${Math.sin(angle) * dist}px`);
      dom.floatingScoresLayer.appendChild(sp);
      setTimeout(() => { if (sp.parentNode) sp.remove(); }, 750);
    }
  }

  function spawnLaserBeam(index, isHorizontal) {
    if (!dom.floatingScoresLayer || !board[0] || !board[0][0]) return;
    const laser = document.createElement('div');
    laser.className = isHorizontal ? 'laser-beam-h' : 'laser-beam-v';
    const boardRect = safeGetRect(dom.gameBoard);

    if (isHorizontal && board[index] && board[index][0] && board[index][0].el) {
      const tRect = safeGetRect(board[index][0].el);
      const cy = tRect.top - boardRect.top + (tRect.height || 44) / 2;
      laser.style.top = `${cy}px`;
    } else if (!isHorizontal && board[0] && board[0][index] && board[0][index].el) {
      const tRect = safeGetRect(board[0][index].el);
      const cx = tRect.left - boardRect.left + (tRect.width || 44) / 2;
      laser.style.left = `${cx}px`;
    }
    dom.floatingScoresLayer.appendChild(laser);
    setTimeout(() => { if (laser.parentNode) laser.remove(); }, 400);
  }

  // Electric Sugar Beam for Rainbow Core targeting
  function spawnSugarBeam(fromRow, fromCol, toRow, toCol) {
    if (!dom.floatingScoresLayer || !board[fromRow] || !board[toRow]) return;
    const fTile = board[fromRow][fromCol];
    const tTile = board[toRow][toCol];
    if (!fTile || !tTile || !fTile.el || !tTile.el) return;

    const boardRect = safeGetRect(dom.gameBoard);
    const fRect = safeGetRect(fTile.el);
    const tRect = safeGetRect(tTile.el);

    const x1 = fRect.left - boardRect.left + (fRect.width || 44) / 2;
    const y1 = fRect.top - boardRect.top + (fRect.height || 44) / 2;
    const x2 = tRect.left - boardRect.left + (tRect.width || 44) / 2;
    const y2 = tRect.top - boardRect.top + (tRect.height || 44) / 2;

    const dx = x2 - x1;
    const dy = y2 - y1;
    const length = Math.sqrt(dx * dx + dy * dy) || 1;
    const angle = Math.atan2(dy, dx) * 180 / Math.PI;

    const beam = document.createElement('div');
    beam.className = 'sugar-laser-beam';
    beam.style.left = `${x1}px`;
    beam.style.top = `${y1}px`;
    beam.style.width = `${length}px`;
    beam.style.transform = `rotate(${angle}deg)`;

    dom.floatingScoresLayer.appendChild(beam);
    setTimeout(() => { if (beam.parentNode) beam.remove(); }, 340);
  }

  // Floating scores feedback layer
  function showFloatingScore(pts, row, col, isBonus = false) {
    if (!dom.floatingScoresLayer) return;
    const el = document.createElement('div');
    el.className = isBonus ? 'floating-score bonus-score' : 'floating-score';
    el.textContent = `+${pts}`;

    if (row !== undefined && col !== undefined && board[row] && board[row][col] && board[row][col].el) {
      const tileRect = safeGetRect(board[row][col].el);
      const boardRect = safeGetRect(dom.gameBoard);
      const bW = boardRect.width || 360;
      const bH = boardRect.height || 360;
      const x = Math.max(15, Math.min(bW - 35, tileRect.left - boardRect.left + (tileRect.width || 44) / 2));
      const y = Math.max(10, Math.min(bH - 25, tileRect.top - boardRect.top + (tileRect.height || 44) / 2));
      el.style.left = `${x}px`;
      el.style.top = `${y}px`;
    } else {
      el.style.left = '50%';
      el.style.top = '45%';
    }

    dom.floatingScoresLayer.appendChild(el);
    setTimeout(() => {
      if (el.parentNode) el.parentNode.removeChild(el);
    }, 800);
  }

  // Layout pattern placement for obstacles
  function applyObstacleLayout(layout, type, targetCount) {
    const coords = [];
    if (layout === 'center-cross') {
      for (let r = 1; r < ROWS - 1; r++) {
        for (let c = 1; c < COLS - 1; c++) {
          if (r === 3 || r === 4 || c === 3 || c === 4) coords.push([r, c]);
        }
      }
    } else if (layout === 'checker') {
      for (let r = 1; r < ROWS - 1; r++) {
        for (let c = 1; c < COLS - 1; c++) {
          if ((r + c) % 2 === 0) coords.push([r, c]);
        }
      }
    } else if (layout === 'border') {
      for (let r = 0; r < ROWS; r++) {
        for (let c = 0; c < COLS; c++) {
          if (r === 0 || r === ROWS - 1 || c === 0 || c === COLS - 1) coords.push([r, c]);
        }
      }
    } else if (layout === 'donut') {
      for (let r = 1; r < ROWS - 1; r++) {
        for (let c = 1; c < COLS - 1; c++) {
          if (r === 1 || r === ROWS - 2 || c === 1 || c === COLS - 2) coords.push([r, c]);
        }
      }
    } else if (layout === 'pillars') {
      for (let r = 1; r < ROWS - 1; r++) {
        coords.push([r, 2]);
        coords.push([r, 5]);
      }
    } else if (layout === 'four-corners') {
      for (let r of [1, 2, 5, 6]) {
        for (let c of [1, 2, 5, 6]) {
          coords.push([r, c]);
        }
      }
    } else if (layout === 'diamonds') {
      for (let r = 1; r < ROWS - 1; r++) {
        for (let c = 1; c < COLS - 1; c++) {
          const dist = Math.abs(r - 3.5) + Math.abs(c - 3.5);
          if (dist <= 3) coords.push([r, c]);
        }
      }
    } else {
      // Default: inside 6x6 grid
      for (let r = 1; r < ROWS - 1; r++) {
        for (let c = 1; c < COLS - 1; c++) coords.push([r, c]);
      }
    }

    let placed = 0;
    for (const [r, c] of coords) {
      if (placed >= targetCount) break;
      const tile = board[r][c];
      if (type === 'jelly' && !tile.isJelly && !tile.hasFrosting) {
        tile.isJelly = true;
        tile.el.classList.add('jelly-tile');
        placed++;
      } else if (type === 'frosting' && !tile.hasFrosting) {
        tile.hasFrosting = true;
        tile.el.classList.add('frosting-tile');
        placed++;
      }
    }
    return placed;
  }

  function setupLevel(lvlIndex) {
    const safeIdx = Math.max(0, Math.min(lvlIndex, LEVELS.length - 1));
    currentLevelData = LEVELS[safeIdx];
    score = 0;
    movesLeft = currentLevelData.moves;
    earnedStars = 0;
    clearAllAnimationTimers();
    currentMoveState = MoveState.IDLE;
    isProcessing = false;
    inputLocked = false;
    if (moveWatchdogTimer) {
      clearTimeout(moveWatchdogTimer);
      moveWatchdogTimer = null;
    }
    resetInputState();
    activeBooster = null;
    selectedTile = null;
    swapFirstTile = null;
    currentComboCount = 0;

    // Reset booster buttons UI
    document.querySelectorAll('.booster-btn').forEach(b => b.classList.remove('active'));

    // Reset goals
    remainingJellies = 0;
    remainingFrosting = 0;
    collectedCandies = {};
    createdSpecials = 0;

    dom.gameLevelName.textContent = `Level ${currentLevelData.level}`;
    dom.gameMoves.textContent = movesLeft;
    dom.gameMoves.classList.remove('low-moves');
    dom.gameScore.textContent = '0';
    dom.scoreProgressFill.style.width = '0%';
    dom.starSlots.forEach(s => { if (s && s.classList) s.classList.remove('earned'); });

    initBoardLayout();

    // Configure level obstacles with handcrafted layout patterns
    const layout = currentLevelData.layout || 'default';
    if (currentLevelData.goalType === 'jelly') {
      const targetCount = currentLevelData.targetJellies || 12;
      remainingJellies = applyObstacleLayout(layout, 'jelly', targetCount);
    } else if (currentLevelData.goalType === 'frosting') {
      const targetCount = currentLevelData.targetFrosting || 12;
      remainingFrosting = applyObstacleLayout(layout, 'frosting', targetCount);
    } else if (currentLevelData.goalType === 'hybrid') {
      const jTarget = currentLevelData.targetJellies || 12;
      const fTarget = currentLevelData.targetFrosting || 10;
      remainingFrosting = applyObstacleLayout(layout, 'frosting', fTarget);
      remainingJellies = applyObstacleLayout(layout === 'border' ? 'center-cross' : 'border', 'jelly', jTarget);
    }

    // Populate candies avoiding initial match-3
    fillInitialBoard();
    if (!hasPossibleMoves()) {
      shuffleBoard();
    }
    verifyAndRepairBoard();
    renderBoard();
    updateObjectiveDisplay();
    resetHintTimer();
  }

  function fillInitialBoard() {
    for (let r = 0; r < ROWS; r++) {
      for (let c = 0; c < COLS; c++) {
        let type;
        let attempts = 0;
        do {
          type = CANDY_TYPES[Math.floor(Math.random() * CANDY_TYPES.length)].id;
          attempts++;
        } while (
          attempts < 40 &&
          ((c >= 2 && board[r][c - 1].candy === type && board[r][c - 2].candy === type) ||
           (r >= 2 && board[r - 1][c].candy === type && board[r - 2][c].candy === type))
        );
        board[r][c].candy = type;
        board[r][c].special = null;
      }
    }
  }

  function renderBoard() {
    for (let r = 0; r < ROWS; r++) {
      for (let c = 0; c < COLS; c++) {
        const tile = board[r][c];
        tile.el.innerHTML = '';

        if (tile.candy) {
          const candyObj = CANDY_TYPES.find(ct => ct.id === tile.candy);
          const candyEl = document.createElement('div');
          candyEl.className = `candy ${candyObj ? candyObj.class : ''}`;

          if (tile.special === 'striped_h') candyEl.classList.add('special-striped-h');
          if (tile.special === 'striped_v') candyEl.classList.add('special-striped-v');
          if (tile.special === 'bomb') candyEl.classList.add('special-bomb');
          if (tile.special === 'rainbow') candyEl.classList.add('special-rainbow');

          candyEl.textContent = tile.special === 'rainbow' ? '🌟' : (candyObj ? candyObj.emoji : '🍬');
          tile.el.appendChild(candyEl);
          tile.candyEl = candyEl;
        } else {
          tile.candyEl = null;
        }

        // Frosting & Jelly visual states
        if (tile.hasFrosting) {
          tile.el.classList.add('frosting-tile');
        } else {
          tile.el.classList.remove('frosting-tile');
        }

        if (tile.isJelly) {
          tile.el.classList.add('jelly-tile');
        } else {
          tile.el.classList.remove('jelly-tile');
        }
      }
    }
  }

  function updateObjectiveDisplay() {
    if (currentLevelData.goalType === 'score') {
      dom.gameGoalIcon.textContent = '🎯';
      dom.gameGoalText.textContent = `${currentLevelData.targetScore.toLocaleString()} pts`;
    } else if (currentLevelData.goalType === 'jelly') {
      dom.gameGoalIcon.textContent = '🍧';
      dom.gameGoalText.textContent = `${remainingJellies} Left`;
    } else if (currentLevelData.goalType === 'frosting') {
      dom.gameGoalIcon.textContent = '🧊';
      dom.gameGoalText.textContent = `${remainingFrosting} Left`;
    } else if (currentLevelData.goalType === 'collect') {
      const keys = Object.keys(currentLevelData.collectCandies);
      dom.gameGoalIcon.textContent = '🧺';
      dom.gameGoalText.textContent = keys.map(k => {
        const needed = Math.max(0, currentLevelData.collectCandies[k] - (collectedCandies[k] || 0));
        const candyObj = CANDY_TYPES.find(c => c.id === k);
        return `${candyObj ? candyObj.emoji : ''} ${needed}`;
      }).join(' ');
    } else if (currentLevelData.goalType === 'specials') {
      dom.gameGoalIcon.textContent = '⚡';
      dom.gameGoalText.textContent = `${Math.max(0, currentLevelData.targetSpecials - createdSpecials)} Left`;
    } else if (currentLevelData.goalType === 'hybrid') {
      dom.gameGoalIcon.textContent = currentLevelData.isMilestone ? '👑' : '⭐';
      const parts = [];
      if (currentLevelData.targetJellies) parts.push(`🍧${remainingJellies}`);
      if (currentLevelData.targetFrosting) parts.push(`🧊${remainingFrosting}`);
      if (currentLevelData.collectCandies) {
        Object.keys(currentLevelData.collectCandies).forEach(k => {
          const needed = Math.max(0, currentLevelData.collectCandies[k] - (collectedCandies[k] || 0));
          const candyObj = CANDY_TYPES.find(c => c.id === k);
          parts.push(`${candyObj ? candyObj.emoji : ''}${needed}`);
        });
      }
      if (currentLevelData.targetSpecials) {
        parts.push(`⚡${Math.max(0, currentLevelData.targetSpecials - createdSpecials)}`);
      }
      if (currentLevelData.targetScore && !currentLevelData.targetJellies && !currentLevelData.targetFrosting) {
        parts.push(`🎯${score}/${currentLevelData.targetScore}`);
      }
      dom.gameGoalText.textContent = parts.join(' ');
    }
  }

  // =========================================================
  // INTERACTION: TOUCH & SWIPE & CLICK CONTROLLER (SAFE STATE MACHINE)
  // =========================================================
  let touchStartX = 0;
  let touchStartY = 0;
  let touchStartTile = null;
  let touchSwiped = false;
  let activeTouchId = null;
  let lastTouchEndTime = 0;

  let mouseDownTile = null;
  let mouseStartX = 0;
  let mouseStartY = 0;
  let mouseSwiped = false;
  let isMouseDown = false;

  function resetInputState() {
    touchStartX = 0;
    touchStartY = 0;
    touchStartTile = null;
    touchSwiped = false;
    activeTouchId = null;

    mouseDownTile = null;
    mouseStartX = 0;
    mouseStartY = 0;
    mouseSwiped = false;
    isMouseDown = false;
  }

  function getTileFromEventTarget(target, clientX, clientY) {
    let slot = target && target.closest ? target.closest('.tile-slot') : null;
    if (!slot && clientX !== undefined && clientY !== undefined && document.elementFromPoint) {
      const el = document.elementFromPoint(clientX, clientY);
      slot = el ? el.closest('.tile-slot') : null;
    }
    if (slot && slot.dataset.row !== undefined && slot.dataset.col !== undefined) {
      const r = parseInt(slot.dataset.row, 10);
      const c = parseInt(slot.dataset.col, 10);
      if (r >= 0 && r < ROWS && c >= 0 && c < COLS && board[r] && board[r][c]) {
        return board[r][c];
      }
    }
    return null;
  }

  // Prevent accidental page scrolling on mobile while touching the game board
  dom.gameBoard.addEventListener('touchstart', (e) => {
    // If a move or cascade is resolving, ignore new swipes
    if (currentMoveState !== MoveState.IDLE || isProcessing || inputLocked) return;

    const touch = (e.changedTouches && e.changedTouches[0]) || (e.touches && e.touches[0]);
    if (!touch) return;

    activeTouchId = touch.identifier;
    touchStartX = touch.clientX;
    touchStartY = touch.clientY;
    touchSwiped = false;
    touchStartTile = getTileFromEventTarget(e.target, touch.clientX, touch.clientY);
  }, { passive: false });

  dom.gameBoard.addEventListener('touchmove', (e) => {
    // Crucial: prevent mobile browser scroll/pull-down bounce
    if (e.cancelable) e.preventDefault();

    if (currentMoveState !== MoveState.IDLE || isProcessing || inputLocked || !touchStartTile || touchSwiped) return;

    let touch = null;
    if (e.changedTouches) {
      for (let i = 0; i < e.changedTouches.length; i++) {
        if (activeTouchId === null || e.changedTouches[i].identifier === activeTouchId) {
          touch = e.changedTouches[i];
          break;
        }
      }
    }
    if (!touch && e.touches) touch = e.touches[0];
    if (!touch) return;

    const dx = touch.clientX - touchStartX;
    const dy = touch.clientY - touchStartY;
    const absDx = Math.abs(dx);
    const absDy = Math.abs(dy);

    // Responsive 20px swipe threshold
    if (Math.max(absDx, absDy) >= 20) {
      touchSwiped = true;
      let tr = touchStartTile.row;
      let tc = touchStartTile.col;

      if (absDx > absDy) {
        tc += (dx > 0 ? 1 : -1);
      } else {
        tr += (dy > 0 ? 1 : -1);
      }

      const sourceTile = touchStartTile;
      touchStartTile = null;

      // Handle off-board swipes near edges & corners gracefully without freezing
      if (tr < 0 || tr >= ROWS || tc < 0 || tc >= COLS) {
        clearSelection();
        triggerHaptic(5);
        return;
      }

      clearSelection();
      handlePlayerSwap(sourceTile, board[tr][tc]);
    }
  }, { passive: false });

  function handleTouchEnd(e) {
    lastTouchEndTime = Date.now();
    const wasSwiped = touchSwiped;
    const tappedTile = touchStartTile;

    // Unconditionally clean up touch state
    touchStartTile = null;
    touchSwiped = false;
    activeTouchId = null;

    if (currentMoveState === MoveState.IDLE && !isProcessing && !inputLocked && !wasSwiped && tappedTile) {
      handleTileClick(tappedTile);
    }
  }

  dom.gameBoard.addEventListener('touchend', handleTouchEnd, { passive: false });
  dom.gameBoard.addEventListener('touchcancel', handleTouchEnd, { passive: false });

  // Mouse fallback for desktop browsers & emulator
  dom.gameBoard.addEventListener('mousedown', (e) => {
    if (Date.now() - lastTouchEndTime < 500) return; // Prevent synthetic mouse event after touch
    if (currentMoveState !== MoveState.IDLE || isProcessing || inputLocked) return;

    isMouseDown = true;
    mouseStartX = e.clientX;
    mouseStartY = e.clientY;
    mouseSwiped = false;
    mouseDownTile = getTileFromEventTarget(e.target, e.clientX, e.clientY);
  });

  window.addEventListener('mousemove', (e) => {
    if (currentMoveState !== MoveState.IDLE || isProcessing || inputLocked || !isMouseDown || !mouseDownTile || mouseSwiped) return;

    const dx = e.clientX - mouseStartX;
    const dy = e.clientY - mouseStartY;
    const absDx = Math.abs(dx);
    const absDy = Math.abs(dy);

    if (Math.max(absDx, absDy) >= 20) {
      mouseSwiped = true;
      let tr = mouseDownTile.row;
      let tc = mouseDownTile.col;

      if (absDx > absDy) {
        tc += (dx > 0 ? 1 : -1);
      } else {
        tr += (dy > 0 ? 1 : -1);
      }

      const sourceTile = mouseDownTile;
      mouseDownTile = null;

      if (tr < 0 || tr >= ROWS || tc < 0 || tc >= COLS) {
        clearSelection();
        return;
      }

      clearSelection();
      handlePlayerSwap(sourceTile, board[tr][tc]);
    }
  });

  window.addEventListener('mouseup', () => {
    if (!isMouseDown) return;
    isMouseDown = false;
    const wasSwiped = mouseSwiped;
    const tappedTile = mouseDownTile;
    mouseDownTile = null;
    setTimeout(() => {
      mouseSwiped = false;
    }, 40);

    if (currentMoveState === MoveState.IDLE && !isProcessing && !inputLocked && !wasSwiped && tappedTile) {
      handleTileClick(tappedTile);
    }
  });

  dom.gameBoard.addEventListener('click', (e) => {
    // Prevent synthetic clicks interfering with state
    if (e.cancelable) e.preventDefault();
  });

  function handleTileClick(tile) {
    if (currentMoveState !== MoveState.IDLE || isProcessing || inputLocked || !tile) return;
    resetHintTimer();

    // Booster action mode
    if (activeBooster) {
      executeBoosterAction(tile);
      return;
    }

    if (!selectedTile) {
      selectedTile = tile;
      if (tile.candyEl) tile.candyEl.classList.add('selected');
      sound.playClick();
    } else {
      if (selectedTile === tile) {
        // Tap same tile -> deselect
        clearSelection();
      } else if (isAdjacent(selectedTile, tile)) {
        // Tap adjacent tile -> swap
        const first = selectedTile;
        clearSelection();
        handlePlayerSwap(first, tile);
      } else {
        // Tap non-adjacent tile -> switch selection
        clearSelection();
        selectedTile = tile;
        if (tile.candyEl) tile.candyEl.classList.add('selected');
        sound.playClick();
      }
    }
  }

  function clearSelection() {
    if (selectedTile && selectedTile.candyEl) {
      selectedTile.candyEl.classList.remove('selected');
    }
    selectedTile = null;
  }

  function isAdjacent(t1, t2) {
    if (!t1 || !t2) return false;
    const dr = Math.abs(t1.row - t2.row);
    const dc = Math.abs(t1.col - t2.col);
    return (dr === 1 && dc === 0) || (dr === 0 && dc === 1);
  }

  function verifyAndRepairBoard() {
    let repaired = 0;
    for (let r = 0; r < ROWS; r++) {
      for (let c = 0; c < COLS; c++) {
        const tile = board[r] ? board[r][c] : null;
        if (!tile || !tile.candy) {
          const randomType = CANDY_TYPES[Math.floor(Math.random() * CANDY_TYPES.length)].id;
          if (tile) {
            tile.candy = randomType;
            tile.special = null;
          }
          repaired++;
        }
      }
    }
    if (repaired > 0) {
      console.warn(`[SugarQuest] verifyAndRepairBoard: repaired ${repaired} empty cells.`);
      renderBoard();
    }
    return repaired === 0;
  }

  // =========================================================
  // SWAP & MATCH CORE ENGINE (SAFE STATE MACHINE)
  // =========================================================
  async function handlePlayerSwap(t1, t2) {
    if (currentMoveState !== MoveState.IDLE || isProcessing || inputLocked || !t1 || !t2 || t1 === t2) return;

    console.log(`[SugarQuest] moveStart -> [${t1.row},${t1.col}] with [${t2.row},${t2.col}]`);
    currentMoveState = MoveState.SWAPPING;
    isProcessing = true;
    inputLocked = true;
    resetHintTimer();
    clearSelection();

    if (moveWatchdogTimer) clearTimeout(moveWatchdogTimer);
    moveWatchdogTimer = setTimeout(() => {
      console.warn('[SugarQuest] Move watchdog triggered! Forcefully recovering to IDLE.');
      verifyAndRepairBoard();
      clearAllAnimationTimers();
      currentMoveState = MoveState.IDLE;
      isProcessing = false;
      inputLocked = false;
      resetInputState();
      renderBoard();
      moveWatchdogTimer = null;
    }, 4000);

    try {
      // 1. SWAP
      console.log('[SugarQuest] swap');
      currentComboCount = 0;
      sound.playSwap();
      triggerHaptic(15);
      swapCandies(t1, t2);
      renderBoard();
      await safeWait(140);

      // 2. CHECKING_MATCHES
      currentMoveState = MoveState.CHECKING_MATCHES;
      console.log('[SugarQuest] matchCheck');

      const isSpecialCombo = (t1.special && t2.special) || (t1.special === 'rainbow' || t2.special === 'rainbow');

      if (isSpecialCombo) {
        // Special combo sequence
        movesLeft = Math.max(0, movesLeft - 1);
        updateMovesDisplay();

        currentMoveState = MoveState.CLEARING;
        console.log('[SugarQuest] clear (special combo)');

        if (t1.special === 'rainbow' || t2.special === 'rainbow') {
          await processRainbowSwap(t1, t2);
        } else if (t1.special.startsWith('striped') && t2.special.startsWith('striped')) {
          showComboBanner('CROSS BLAST!');
          sound.playComboFanfare(2);
          popCrossBlast(t2.row, t2.col);
          t1.candy = null; t1.special = null;
          t2.candy = null; t2.special = null;
          renderBoard();
          await safeWait(180);
        } else if (
          (t1.special.startsWith('striped') && t2.special === 'bomb') ||
          (t2.special.startsWith('striped') && t1.special === 'bomb')
        ) {
          showComboBanner('MEGA BLAST!');
          sound.playComboFanfare(3);
          popMegaBlast(t2.row, t2.col);
          t1.candy = null; t1.special = null;
          t2.candy = null; t2.special = null;
          renderBoard();
          await safeWait(200);
        } else if (t1.special === 'bomb' && t2.special === 'bomb') {
          showComboBanner('SUPER BOMB!');
          sound.playComboFanfare(4);
          popBigBombBlast(t2.row, t2.col);
          t1.candy = null; t1.special = null;
          t2.candy = null; t2.special = null;
          renderBoard();
          await safeWait(200);
        }

        // Falling & Refill after special combo
        currentMoveState = MoveState.FALLING;
        console.log('[SugarQuest] fall (special combo)');
        const dropDistances = applyGravity();
        await safeWait(50);

        currentMoveState = MoveState.REFILLING;
        console.log('[SugarQuest] refill (special combo)');
        applyRefill(dropDistances);
        renderBoard();
        animateFalling(dropDistances);
        await safeWait(170);

        // Subsequent cascade check
        currentMoveState = MoveState.CHECKING_CASCADE;
        console.log('[SugarQuest] cascadeCheck (special combo)');
        const subsequentMatches = findMatches();
        if (subsequentMatches.allMatches.length > 0) {
          await processMatchesAndCascade(subsequentMatches);
        }

        verifyAndRepairBoard();
        checkGameEndConditions();

        if (movesLeft > 0 && !isGoalAchieved() && !hasPossibleMoves()) {
          showToast('No more moves! Reshuffling board...');
          await shuffleBoard();
          verifyAndRepairBoard();
        }
        return;
      }

      // Standard move match check
      const matchData = findMatches();
      if (matchData.allMatches.length > 0) {
        // VALID MOVE
        movesLeft = Math.max(0, movesLeft - 1);
        updateMovesDisplay();

        await processMatchesAndCascade(matchData, t2);

        verifyAndRepairBoard();
        checkGameEndConditions();

        if (movesLeft > 0 && !isGoalAchieved() && !hasPossibleMoves()) {
          showToast('No more moves! Reshuffling board...');
          await shuffleBoard();
          verifyAndRepairBoard();
        }
      } else {
        // INVALID MOVE: SWAP_BACK
        currentMoveState = MoveState.SWAP_BACK;
        console.log('[SugarQuest] swapBack');
        await safeWait(140);
        sound.playSwap();
        triggerHaptic(10);
        swapCandies(t1, t2);
        renderBoard();
        await safeWait(100);
        return;
      }
    } catch (err) {
      console.error(`[SugarQuest] Move resolution error at state ${currentMoveState}:`, err);
      verifyAndRepairBoard();
    } finally {
      if (moveWatchdogTimer) {
        clearTimeout(moveWatchdogTimer);
        moveWatchdogTimer = null;
      }
      clearAllAnimationTimers();
      currentMoveState = MoveState.IDLE;
      isProcessing = false;
      inputLocked = false;
      resetInputState();
      renderBoard();
      resetHintTimer();
      console.log('[SugarQuest] moveEnd (state: IDLE, input unlocked)');
    }
  }

  function swapCandies(t1, t2) {
    const tempCandy = t1.candy;
    const tempSpecial = t1.special;

    t1.candy = t2.candy;
    t1.special = t2.special;

    t2.candy = tempCandy;
    t2.special = tempSpecial;
  }

  async function processRainbowSwap(t1, t2) {
    sound.playSpecialCreated();
    triggerHaptic(30);

    const rainbowTile = t1.special === 'rainbow' ? t1 : t2;
    const otherTile = t1.special === 'rainbow' ? t2 : t1;

    rainbowTile.candy = null;
    rainbowTile.special = null;

    if (otherTile.special === 'rainbow') {
      // Rainbow + Rainbow: Clear entire board with Sugar Rush!
      showComboBanner('SUGAR RUSH! 🌈');
      sound.playComboFanfare(4);
      for (let r = 0; r < ROWS; r++) {
        for (let c = 0; c < COLS; c++) {
          popTile(board[r][c], 100);
        }
      }
    } else if (otherTile.special && otherTile.special.startsWith('striped')) {
      // Rainbow + Striped: Turn all candies of that color into striped candies, then detonate!
      const targetColor = otherTile.candy;
      otherTile.candy = null;
      otherTile.special = null;
      showComboBanner('RAINBOW STRIPE FRENZY! ⚡');
      sound.playComboFanfare(3);
      for (let r = 0; r < ROWS; r++) {
        for (let c = 0; c < COLS; c++) {
          if (board[r][c].candy === targetColor) {
            board[r][c].special = Math.random() > 0.5 ? 'striped_h' : 'striped_v';
          }
        }
      }
      renderBoard();
      await safeWait(240);
      for (let r = 0; r < ROWS; r++) {
        for (let c = 0; c < COLS; c++) {
          if (board[r][c].special && board[r][c].special.startsWith('striped')) {
            popTile(board[r][c], 80);
          }
        }
      }
    } else if (otherTile.special === 'bomb') {
      // Rainbow + Bomb: Turn all candies of that color into bombs, then detonate!
      const targetColor = otherTile.candy;
      otherTile.candy = null;
      otherTile.special = null;
      showComboBanner('SUPER BOMB FRENZY! 💣');
      sound.playComboFanfare(4);
      for (let r = 0; r < ROWS; r++) {
        for (let c = 0; c < COLS; c++) {
          if (board[r][c].candy === targetColor) {
            board[r][c].special = 'bomb';
          }
        }
      }
      renderBoard();
      await safeWait(240);
      for (let r = 0; r < ROWS; r++) {
        for (let c = 0; c < COLS; c++) {
          if (board[r][c].special === 'bomb') {
            popTile(board[r][c], 90);
          }
        }
      }
    } else {
      // Clear all candies of otherTile color with electric sugar beams & chime cascade
      const targetColor = otherTile.candy;
      otherTile.candy = null;
      otherTile.special = null;

      showComboBanner('COLOR BLAST! ✨');
      sound.playComboFanfare(2);

      const matchingTiles = [];
      for (let r = 0; r < ROWS; r++) {
        for (let c = 0; c < COLS; c++) {
          if (board[r][c].candy === targetColor) {
            matchingTiles.push(board[r][c]);
          }
        }
      }

      // Sequentially zap each matching candy across the board with electric sugar lasers
      const pentatonic = [523.25, 587.33, 659.25, 783.99, 880.00, 1046.50];
      for (let i = 0; i < matchingTiles.length; i++) {
        const target = matchingTiles[i];
        spawnSugarBeam(rainbowTile.row, rainbowTile.col, target.row, target.col);
        sound.playChime(pentatonic[i % pentatonic.length]);
        triggerScreenShake('mild');
        popTile(target, 80);
        await safeWait(40);
      }
    }
    renderBoard();
    await safeWait(250);
  }

  function findMatches() {
    const matchedTilesSet = new Set();
    const groups = [];

    // Horizontal match check
    for (let r = 0; r < ROWS; r++) {
      let streak = 1;
      for (let c = 1; c <= COLS; c++) {
        if (
          c < COLS &&
          board[r][c].candy &&
          board[r][c].candy !== 'rainbow' &&
          board[r][c].candy === board[r][c - 1].candy
        ) {
          streak++;
        } else {
          if (streak >= 3) {
            const group = [];
            for (let k = 0; k < streak; k++) {
              const tile = board[r][c - 1 - k];
              group.push(tile);
              matchedTilesSet.add(tile);
            }
            groups.push({ type: 'h', tiles: group, length: streak, color: board[r][c - 1].candy });
          }
          streak = 1;
        }
      }
    }

    // Vertical match check
    for (let c = 0; c < COLS; c++) {
      let streak = 1;
      for (let r = 1; r <= ROWS; r++) {
        if (
          r < ROWS &&
          board[r][c].candy &&
          board[r][c].candy !== 'rainbow' &&
          board[r][c].candy === board[r - 1][c].candy
        ) {
          streak++;
        } else {
          if (streak >= 3) {
            const group = [];
            for (let k = 0; k < streak; k++) {
              const tile = board[r - 1 - k][c];
              group.push(tile);
              matchedTilesSet.add(tile);
            }
            groups.push({ type: 'v', tiles: group, length: streak, color: board[r - 1][c].candy });
          }
          streak = 1;
        }
      }
    }

    // Check for L-shape or T-shape intersections -> Sugar Bomb
    const intersections = [];
    const hGroups = groups.filter(g => g.type === 'h');
    const vGroups = groups.filter(g => g.type === 'v');

    hGroups.forEach(hg => {
      vGroups.forEach(vg => {
        if (hg.color === vg.color) {
          const common = hg.tiles.find(t => vg.tiles.includes(t));
          if (common) {
            intersections.push({ tile: common, color: hg.color, hg, vg });
          }
        }
      });
    });

    return {
      allMatches: Array.from(matchedTilesSet),
      groups: groups,
      intersections: intersections
    };
  }

  async function processMatchesAndCascade(initialMatchData, swappedTargetTile = null) {
    let matchData = initialMatchData;
    let cascadeIterations = 0;
    const MAX_CASCADE_ITERATIONS = 10; // Hard safety cap against infinite cascade hangs

    while (matchData.allMatches.length > 0 && cascadeIterations < MAX_CASCADE_ITERATIONS) {
      cascadeIterations++;
      currentComboCount++;

      // CLEARING
      currentMoveState = MoveState.CLEARING;
      console.log(`[SugarQuest] clear (cascade ${cascadeIterations}, matches: ${matchData.allMatches.length})`);
      sound.playPop(currentComboCount);
      triggerHaptic(20);

      // Score and Combo Banner
      const basePts = matchData.allMatches.length * 20 * currentComboCount;
      addScore(basePts);

      if (currentComboCount === 2) {
        showComboBanner('SWEET!');
        sound.playComboFanfare(1);
      } else if (currentComboCount === 3) {
        showComboBanner('TASTY!');
        sound.playComboFanfare(2);
      } else if (currentComboCount === 4) {
        showComboBanner('DELICIOUS!');
        sound.playComboFanfare(3);
      } else if (currentComboCount >= 5) {
        showComboBanner('SUGAR RUSH!');
        sound.playComboFanfare(4);
      }

      // Determine special candies to forge (5-match -> Rainbow Core, L/T-shape -> Sugar Bomb, 4-match -> Striped Laser)
      const specialsToSpawn = [];
      const preservedTiles = new Set();

      // Check intersections first (L or T shape -> Sugar Bomb)
      if (matchData.intersections && matchData.intersections.length > 0) {
        matchData.intersections.forEach(inter => {
          if (!preservedTiles.has(inter.tile)) {
            specialsToSpawn.push({ tile: inter.tile, special: 'bomb', color: inter.color });
            preservedTiles.add(inter.tile);
          }
        });
      }

      matchData.groups.forEach(g => {
        if (g.length >= 5) {
          // 5-match in a row -> Rainbow Core
          const tile = (swappedTargetTile && g.tiles.includes(swappedTargetTile))
            ? swappedTargetTile
            : g.tiles[Math.floor(g.tiles.length / 2)];
          if (!preservedTiles.has(tile)) {
            specialsToSpawn.push({ tile: tile, special: 'rainbow', color: 'rainbow' });
            preservedTiles.add(tile);
          }
        } else if (g.length === 4) {
          // 4-match in a row -> Striped Laser (horizontal clears row, vertical clears column)
          const tile = (swappedTargetTile && g.tiles.includes(swappedTargetTile))
            ? swappedTargetTile
            : g.tiles[1];
          if (!preservedTiles.has(tile)) {
            const specType = g.type === 'h' ? 'striped_h' : 'striped_v';
            specialsToSpawn.push({ tile: tile, special: specType, color: g.color });
            preservedTiles.add(tile);
          }
        }
      });

      // Mark matched candies with visual bounce
      matchData.allMatches.forEach(tile => {
        if (tile.candyEl && !preservedTiles.has(tile)) {
          tile.candyEl.classList.add('match-pop');
        }
      });

      // Trigger screen shake
      if (matchData.groups.some(g => g.length >= 5) || (matchData.intersections && matchData.intersections.length > 0)) {
        triggerScreenShake('medium');
      } else if (matchData.groups.some(g => g.length === 4) || currentComboCount >= 3) {
        triggerScreenShake('mild');
      } else if (currentComboCount >= 5) {
        triggerScreenShake('strong');
      }

      await safeWait(140);

      // Pop all matched candies
      matchData.allMatches.forEach(tile => {
        if (!preservedTiles.has(tile)) {
          popTile(tile, 25 * currentComboCount);
        }
      });

      // Transform preserved tiles into forged special candies
      specialsToSpawn.forEach(sp => {
        sp.tile.candy = sp.color;
        sp.tile.special = sp.special;
        createdSpecials++;
        sound.playSpecialCreated();
        updateObjectiveDisplay();
        spawnCandyBlast(sp.tile.row, sp.tile.col, sp.color, sp.special, currentComboCount);
      });

      renderBoard();
      await safeWait(100);

      // FALLING
      currentMoveState = MoveState.FALLING;
      console.log(`[SugarQuest] fall (cascade ${cascadeIterations})`);
      const dropDistances = applyGravity();
      await safeWait(50);

      // REFILLING
      currentMoveState = MoveState.REFILLING;
      console.log(`[SugarQuest] refill (cascade ${cascadeIterations})`);
      applyRefill(dropDistances);
      renderBoard();
      animateFalling(dropDistances);
      await safeWait(170);

      // CHECKING_CASCADE
      currentMoveState = MoveState.CHECKING_CASCADE;
      console.log(`[SugarQuest] cascadeCheck (cascade ${cascadeIterations})`);
      swappedTargetTile = null;
      matchData = findMatches();
    }
  }

  function popTile(tile, pts = 25) {
    if (!tile || !tile.candy) return;

    // Track collected candies
    if (tile.candy && tile.candy !== 'rainbow') {
      collectedCandies[tile.candy] = (collectedCandies[tile.candy] || 0) + 1;
    }

    // Clear jelly tile underneath
    if (tile.isJelly) {
      tile.isJelly = false;
      remainingJellies = Math.max(0, remainingJellies - 1);
      tile.el.classList.remove('jelly-tile');
      addScore(60);
    }

    // Break adjacent frosting blockers
    checkAdjacentFrosting(tile.row, tile.col);

    const special = tile.special;
    const oldCandy = tile.candy;

    // Immediate model clearing prevents duplicate activations
    tile.special = null;
    tile.candy = null;

    spawnCandyBlast(tile.row, tile.col, oldCandy, special);
    addScore(pts, tile.row, tile.col);
    updateObjectiveDisplay();

    // Trigger special blasts if this tile had a special power
    if (special === 'striped_h') {
      triggerStripedBlast(tile.row, true);
    } else if (special === 'striped_v') {
      triggerStripedBlast(tile.col, false);
    } else if (special === 'bomb') {
      triggerBombBlast(tile.row, tile.col);
    }
  }

  function triggerStripedBlast(index, isHorizontal) {
    sound.playLaser();
    spawnLaserBeam(index, isHorizontal);
    triggerScreenShake('mild');
    if (isHorizontal) {
      for (let c = 0; c < COLS; c++) {
        const t = board[index][c];
        if (t && (t.candy || t.isJelly)) {
          clearSingleTile(t, 30);
        }
      }
    } else {
      for (let r = 0; r < ROWS; r++) {
        const t = board[r][index];
        if (t && (t.candy || t.isJelly)) {
          clearSingleTile(t, 30);
        }
      }
    }
  }

  function triggerBombBlast(centerRow, centerCol) {
    sound.playBomb();
    triggerScreenShake('medium');
    for (let r = centerRow - 1; r <= centerRow + 1; r++) {
      for (let c = centerCol - 1; c <= centerCol + 1; c++) {
        if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
          const t = board[r][c];
          if (t && (t.candy || t.isJelly)) {
            clearSingleTile(t, 35);
          }
        }
      }
    }
  }

  function popCrossBlast(row, col) {
    sound.playLaser();
    spawnLaserBeam(row, true);
    spawnLaserBeam(col, false);
    triggerScreenShake('medium');
    for (let c = 0; c < COLS; c++) clearSingleTile(board[row][c], 40);
    for (let r = 0; r < ROWS; r++) clearSingleTile(board[r][col], 40);
  }

  function popMegaBlast(centerRow, centerCol) {
    sound.playBomb();
    triggerScreenShake('strong');
    for (let dr = -1; dr <= 1; dr++) {
      const r = centerRow + dr;
      if (r >= 0 && r < ROWS) {
        for (let c = 0; c < COLS; c++) clearSingleTile(board[r][c], 40);
      }
    }
    for (let dc = -1; dc <= 1; dc++) {
      const c = centerCol + dc;
      if (c >= 0 && c < COLS) {
        for (let r = 0; r < ROWS; r++) clearSingleTile(board[r][c], 40);
      }
    }
  }

  function popBigBombBlast(centerRow, centerCol) {
    sound.playBomb();
    triggerScreenShake('strong');
    for (let r = centerRow - 2; r <= centerRow + 2; r++) {
      for (let c = centerCol - 2; c <= centerCol + 2; c++) {
        if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
          clearSingleTile(board[r][c], 45);
        }
      }
    }
  }

  let activeDetonations = 0;

  function clearSingleTile(t, pts = 30) {
    if (!t || (!t.candy && !t.isJelly)) return;
    if (t.candy && t.candy !== 'rainbow') {
      collectedCandies[t.candy] = (collectedCandies[t.candy] || 0) + 1;
    }
    if (t.isJelly) {
      t.isJelly = false;
      remainingJellies = Math.max(0, remainingJellies - 1);
      t.el.classList.remove('jelly-tile');
      addScore(60, t.row, t.col);
    }
    checkAdjacentFrosting(t.row, t.col);

    const oldSpec = t.special;
    const oldCandy = t.candy;
    t.special = null;
    t.candy = null;

    spawnCandyBlast(t.row, t.col, oldCandy, oldSpec, currentComboCount);
    addScore(pts, t.row, t.col);
    updateObjectiveDisplay();

    // Trigger secondary chain detonations if tile was a special candy (with recursion guard to prevent hangs)
    if (oldSpec && activeDetonations < 6) {
      activeDetonations++;
      try {
        if (oldSpec === 'striped_h') {
          triggerStripedBlast(t.row, true);
        } else if (oldSpec === 'striped_v') {
          triggerStripedBlast(t.col, false);
        } else if (oldSpec === 'bomb') {
          triggerBombBlast(t.row, t.col);
        }
      } finally {
        activeDetonations--;
      }
    }
  }

  function checkAdjacentFrosting(r, c) {
    const neighbors = [
      [r - 1, c], [r + 1, c], [r, c - 1], [r, c + 1]
    ];
    neighbors.forEach(([nr, nc]) => {
      if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS) {
        const t = board[nr][nc];
        if (t && t.hasFrosting) {
          t.hasFrosting = false;
          remainingFrosting = Math.max(0, remainingFrosting - 1);
          t.el.classList.remove('frosting-tile');
          addScore(70);
          sound.playPop(1);
          updateObjectiveDisplay();
        }
      }
    });
  }

  function applyGravity() {
    const dropDistances = Array.from({ length: ROWS }, () => Array(COLS).fill(0));
    for (let c = 0; c < COLS; c++) {
      let emptyCount = 0;
      for (let r = ROWS - 1; r >= 0; r--) {
        if (!board[r][c].candy) {
          emptyCount++;
        } else if (emptyCount > 0) {
          const targetRow = r + emptyCount;
          board[targetRow][c].candy = board[r][c].candy;
          board[targetRow][c].special = board[r][c].special;
          board[r][c].candy = null;
          board[r][c].special = null;
          dropDistances[targetRow][c] = emptyCount;
        }
      }
    }
    return dropDistances;
  }

  function applyRefill(dropDistances) {
    for (let c = 0; c < COLS; c++) {
      let emptyCount = 0;
      for (let r = 0; r < ROWS; r++) {
        if (!board[r][c].candy) {
          emptyCount++;
        }
      }
      for (let r = 0; r < emptyCount; r++) {
        const randomType = CANDY_TYPES[Math.floor(Math.random() * CANDY_TYPES.length)].id;
        board[r][c].candy = randomType;
        board[r][c].special = null;
        if (dropDistances) {
          dropDistances[r][c] = emptyCount + (emptyCount - r);
        }
      }
    }
  }

  function animateFalling(dropDistances) {
    if (!dropDistances) return;
    for (let r = 0; r < ROWS; r++) {
      for (let c = 0; c < COLS; c++) {
        const drop = dropDistances[r][c];
        if (drop > 0 && board[r][c].candyEl) {
          board[r][c].candyEl.style.setProperty('--drop-rows', Math.min(8, drop));
          board[r][c].candyEl.style.animationDelay = `${c * 10}ms`;
          board[r][c].candyEl.classList.add('falling');
        }
      }
    }
  }

  async function processBoardCascade() {
    const dropDistances = applyGravity();
    applyRefill(dropDistances);
    renderBoard();
    animateFalling(dropDistances);
    await safeWait(170);
  }

  // Ultra-fast zero-allocation check to see if any match-3 exists on board
  function hasAnyMatch() {
    for (let r = 0; r < ROWS; r++) {
      for (let c = 0; c < COLS - 2; c++) {
        const k = board[r][c].candy;
        if (k && k !== 'rainbow' && k === board[r][c + 1].candy && k === board[r][c + 2].candy) {
          return true;
        }
      }
    }
    for (let c = 0; c < COLS; c++) {
      for (let r = 0; r < ROWS - 2; r++) {
        const k = board[r][c].candy;
        if (k && k !== 'rainbow' && k === board[r + 1][c].candy && k === board[r + 2][c].candy) {
          return true;
        }
      }
    }
    return false;
  }

  function hasPossibleMoves() {
    for (let r = 0; r < ROWS; r++) {
      for (let c = 0; c < COLS; c++) {
        // Rainbow candies always have valid moves with any neighbor
        if (board[r][c].special === 'rainbow') return true;

        // Check adjacent special candies (any two specials can be swapped together)
        if (board[r][c].special) {
          if (c < COLS - 1 && board[r][c + 1].special) return true;
          if (r < ROWS - 1 && board[r + 1][c].special) return true;
        }

        // Check Swap Right
        if (c < COLS - 1) {
          if (board[r][c + 1].special === 'rainbow') return true;
          swapCandies(board[r][c], board[r][c + 1]);
          const found = hasAnyMatch();
          swapCandies(board[r][c], board[r][c + 1]);
          if (found) return true;
        }

        // Check Swap Down
        if (r < ROWS - 1) {
          if (board[r + 1][c].special === 'rainbow') return true;
          swapCandies(board[r][c], board[r + 1][c]);
          const found = hasAnyMatch();
          swapCandies(board[r][c], board[r + 1][c]);
          if (found) return true;
        }
      }
    }
    return false;
  }

  async function shuffleBoard() {
    sound.playSwap();
    showComboBanner('SHUFFLE!');

    const allCandies = [];
    for (let r = 0; r < ROWS; r++) {
      for (let c = 0; c < COLS; c++) {
        allCandies.push({ candy: board[r][c].candy, special: board[r][c].special });
      }
    }

    // Fisher-Yates shuffle
    for (let i = allCandies.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      const temp = allCandies[i];
      allCandies[i] = allCandies[j];
      allCandies[j] = temp;
    }

    let idx = 0;
    for (let r = 0; r < ROWS; r++) {
      for (let c = 0; c < COLS; c++) {
        board[r][c].candy = allCandies[idx].candy;
        board[r][c].special = allCandies[idx].special;
        idx++;
      }
    }
    renderBoard();
    await safeWait(220);
  }

  // =========================================================
  // BOOSTERS ENGINE
  // =========================================================
  function setBoosterActive(type) {
    if (profile.boosters[type] <= 0) {
      showToast(`No ${type} boosters left! Visit the Shop 🛒`);
      sound.playClick();
      return;
    }

    if (activeBooster === type) {
      activeBooster = null;
      document.querySelectorAll('.booster-btn').forEach(b => b.classList.remove('active'));
      showToast('Booster cancelled.');
    } else {
      activeBooster = type;
      document.querySelectorAll('.booster-btn').forEach(b => b.classList.remove('active'));
      const btn = document.getElementById(`booster-${type}`);
      if (btn) btn.classList.add('active');
      showToast(`Tap a candy to use ${type.toUpperCase()}!`);
    }
    sound.playClick();
  }

  async function executeBoosterAction(tile) {
    if (!activeBooster || !tile || isProcessing || inputLocked) return;
    isProcessing = true;
    inputLocked = true;
    try {
      const type = activeBooster;
      activeBooster = null;
      document.querySelectorAll('.booster-btn').forEach(b => b.classList.remove('active'));

      profile.boosters[type]--;
      saveProfile();
      updateGlobalHUD();

      if (type === 'hammer') {
        sound.playExplosion();
        triggerHaptic(30);
        popTile(tile, 50);
        renderBoard();
        await processBoardCascade();
        const m = findMatches();
        if (m.allMatches.length > 0) {
          await processMatchesAndCascade(m);
        }
      } else if (type === 'bomb') {
        sound.playExplosion();
        triggerHaptic(40);
        triggerBombBlast(tile.row, tile.col);
        renderBoard();
        await processBoardCascade();
        const m = findMatches();
        if (m.allMatches.length > 0) {
          await processMatchesAndCascade(m);
        }
      } else if (type === 'rainbow') {
        sound.playSpecialCreated();
        tile.special = 'rainbow';
        tile.candy = 'rainbow';
        renderBoard();
      } else if (type === 'swap') {
        if (!swapFirstTile) {
          swapFirstTile = tile;
          if (tile.candyEl) tile.candyEl.classList.add('selected');
          showToast('Tap an adjacent candy to free-swap!');
          return;
        } else {
          const t1 = swapFirstTile;
          const t2 = tile;
          swapFirstTile = null;
          clearSelection();
          if (isAdjacent(t1, t2)) {
            sound.playSwap();
            swapCandies(t1, t2);
            renderBoard();
            const matchData = findMatches();
            if (matchData.allMatches.length > 0) {
              await processMatchesAndCascade(matchData, t2);
            }
          } else {
            showToast('Must select adjacent tile!');
          }
        }
      }

      verifyAndRepairBoard();
      checkGameEndConditions();
      if (movesLeft > 0 && !isGoalAchieved() && !hasPossibleMoves()) {
        showToast('No more moves! Reshuffling board...');
        await shuffleBoard();
        verifyAndRepairBoard();
      }
    } catch (err) {
      console.error('Booster action error:', err);
      verifyAndRepairBoard();
    } finally {
      clearAllAnimationTimers();
      currentMoveState = MoveState.IDLE;
      isProcessing = false;
      inputLocked = false;
      resetInputState();
      renderBoard();
    }
  }

  function usePlusFiveMoves() {
    if ((profile.boosters.moves || 0) > 0) {
      profile.boosters.moves--;
      movesLeft += 5;
      updateMovesDisplay();
      updateGlobalHUD();
      saveProfile();
      sound.playSpecialCreated();
      showToast('+5 Moves Added! ➕');
    } else if (profile.coins >= 50) {
      profile.coins -= 50;
      movesLeft += 5;
      updateMovesDisplay();
      updateGlobalHUD();
      saveProfile();
      sound.playSpecialCreated();
      showToast('+5 Moves Added for 50 🪙!');
    } else {
      showToast('Need 50 coins for +5 moves! Visit the Shop 🛒');
    }
  }

  dom.boosterHammer.addEventListener('click', () => setBoosterActive('hammer'));
  dom.boosterSwap.addEventListener('click', () => setBoosterActive('swap'));
  dom.boosterBomb.addEventListener('click', () => setBoosterActive('bomb'));
  dom.boosterRainbow.addEventListener('click', () => setBoosterActive('rainbow'));
  if (dom.boosterMoves) dom.boosterMoves.addEventListener('click', usePlusFiveMoves);

  // =========================================================
  // SCORES, STARS & VICTORY / GAMEOVER CHECK
  // =========================================================
  function addScore(pts, row, col) {
    score += pts;
    if (dom.gameScore) dom.gameScore.textContent = score.toLocaleString();
    showFloatingScore(pts, row, col);

    if (score > profile.highScore) {
      profile.highScore = score;
      saveProfile();
      if (dom.menuHighScore) dom.menuHighScore.textContent = profile.highScore.toLocaleString();
    }

    // Score progress bar
    const starGoals = (currentLevelData && currentLevelData.starScores) || [3000, 6000, 10000];
    const maxGoal = starGoals[2] || 10000;
    const pct = Math.min(100, Math.floor((score / maxGoal) * 100));
    if (dom.scoreProgressFill) dom.scoreProgressFill.style.width = `${pct}%`;

    // Calculate stars
    let newStars = 0;
    if (score >= starGoals[0]) newStars = 1;
    if (score >= starGoals[1]) newStars = 2;
    if (score >= starGoals[2]) newStars = 3;

    if (newStars > earnedStars) {
      earnedStars = newStars;
      if (dom.starSlots) {
        for (let i = 0; i < 3; i++) {
          if (dom.starSlots[i]) {
            if (i < earnedStars) {
              dom.starSlots[i].classList.add('earned');
            } else {
              dom.starSlots[i].classList.remove('earned');
            }
          }
        }
      }
    }
  }

  function updateMovesDisplay() {
    dom.gameMoves.textContent = movesLeft;
    if (movesLeft <= 5) {
      dom.gameMoves.classList.add('low-moves');
    } else {
      dom.gameMoves.classList.remove('low-moves');
    }
  }

  function isGoalAchieved() {
    if (currentLevelData.goalType === 'score') {
      return score >= currentLevelData.targetScore;
    } else if (currentLevelData.goalType === 'jelly') {
      return remainingJellies === 0;
    } else if (currentLevelData.goalType === 'frosting') {
      return remainingFrosting === 0;
    } else if (currentLevelData.goalType === 'collect') {
      const keys = Object.keys(currentLevelData.collectCandies);
      return keys.every(k => (collectedCandies[k] || 0) >= currentLevelData.collectCandies[k]);
    } else if (currentLevelData.goalType === 'specials') {
      return createdSpecials >= currentLevelData.targetSpecials;
    } else if (currentLevelData.goalType === 'hybrid') {
      if (currentLevelData.targetJellies && remainingJellies > 0) return false;
      if (currentLevelData.targetFrosting && remainingFrosting > 0) return false;
      if (currentLevelData.collectCandies) {
        const keys = Object.keys(currentLevelData.collectCandies);
        if (!keys.every(k => (collectedCandies[k] || 0) >= currentLevelData.collectCandies[k])) return false;
      }
      if (currentLevelData.targetSpecials && createdSpecials < currentLevelData.targetSpecials) return false;
      if (currentLevelData.targetScore && score < currentLevelData.targetScore) return false;
      return true;
    }
    return false;
  }

  function checkGameEndConditions() {
    if (isGoalAchieved()) {
      handleLevelVictory();
    } else if (movesLeft <= 0) {
      handleGameOver();
    }
  }

  function handleLevelVictory() {
    sound.playWin();
    triggerHaptic(50);

    // Guaranteed at least 1 star for clearing the level
    earnedStars = Math.max(1, earnedStars);

    // Moves bonus score
    const movesBonus = movesLeft * 100;
    addScore(movesBonus);

    // Coins reward
    const coinsEarned = 30 + earnedStars * 10;
    profile.coins += coinsEarned;

    // Update unlocked level, high scores, stars & milestone recipes
    const currentLvl = typeof currentLevelData.level === 'number' ? currentLevelData.level : 1;
    const prevStars = profile.levelStars[currentLvl] || 0;
    if (earnedStars > prevStars) {
      profile.levelStars[currentLvl] = earnedStars;
    }
    const prevHigh = profile.levelHighScores[currentLvl] || 0;
    if (score > prevHigh) {
      profile.levelHighScores[currentLvl] = score;
    }
    if (currentLvl === profile.unlockedLevel && currentLvl < LEVELS.length) {
      profile.unlockedLevel = currentLvl + 1;
    }

    // Milestone Recipe Unlocks (Levels 10, 20, 30... 100)
    if (currentLvl % 10 === 0 && !profile.unlockedRecipes[currentLvl]) {
      profile.unlockedRecipes[currentLvl] = true;
      profile.coins += 100;
      showToast(`🏆 Milestone Recipe Mastered! +100 bonus coins!`);
    }

    // Daily Challenge completion
    if (currentLevelData.isDaily) {
      const today = new Date().toISOString().slice(0, 10);
      if (profile.lastDailyDate !== today) {
        const yesterday = new Date(Date.now() - 86400000).toISOString().slice(0, 10);
        profile.dailyStreak = (profile.lastDailyDate === yesterday) ? (profile.dailyStreak + 1) : 1;
        profile.lastDailyDate = today;
        profile.coins += 150;
        profile.boosters.hammer = (profile.boosters.hammer || 0) + 1;
        profile.stats.dailyChallengesCompleted = (profile.stats.dailyChallengesCompleted || 0) + 1;
        showToast(`🎉 Daily Challenge Complete! +150 Coins & 1 Hammer!`);
      }
    }

    // Stats
    profile.stats.totalLevelsWon = (profile.stats.totalLevelsWon || 0) + 1;
    profile.stats.highestSingleScore = Math.max(profile.stats.highestSingleScore || 0, score);
    saveProfile();
    updateGlobalHUD();

    // Populate Level Complete modal
    dom.winLevelTitle.textContent = `${currentLevelData.name} Cleared!`;
    dom.winFinalScore.textContent = score.toLocaleString();
    dom.winCoinsEarned.textContent = `+${coinsEarned} 🪙`;
    dom.winMovesBonus.textContent = `+${movesBonus} pts (${movesLeft} moves left)`;

    dom.winStars.forEach((star, idx) => {
      if (idx < earnedStars) {
        star.classList.add('earned');
      } else {
        star.classList.remove('earned');
      }
    });

    setTimeout(() => openModal(dom.modalLevelComplete), 400);
  }

  function handleGameOver() {
    sound.playGameOver();
    triggerHaptic(40);

    // Deduct 1 life
    profile.lives = Math.max(0, profile.lives - 1);
    profile.lastLifeTime = Date.now();
    saveProfile();
    updateGlobalHUD();

    dom.gameoverScore.textContent = score.toLocaleString();

    if (currentLevelData.goalType === 'score') {
      dom.gameoverTarget.textContent = `${currentLevelData.targetScore.toLocaleString()} pts`;
    } else if (currentLevelData.goalType === 'jelly') {
      dom.gameoverTarget.textContent = `${remainingJellies} Jellies Left`;
    } else if (currentLevelData.goalType === 'frosting') {
      dom.gameoverTarget.textContent = `${remainingFrosting} Frosting Left`;
    } else if (currentLevelData.goalType === 'collect') {
      dom.gameoverTarget.textContent = 'Orders Incomplete';
    } else if (currentLevelData.goalType === 'specials') {
      dom.gameoverTarget.textContent = `${Math.max(0, currentLevelData.targetSpecials - createdSpecials)} Specials Needed`;
    }

    setTimeout(() => openModal(dom.modalGameOver), 400);
  }

  // =========================================================
  // COMBO BANNER & HINT TIMERS
  // =========================================================
  let comboTimer = null;
  function showComboBanner(text) {
    if (comboTimer) clearTimeout(comboTimer);
    dom.comboBanner.textContent = text;
    dom.comboBanner.classList.add('show');
    comboTimer = setTimeout(() => {
      dom.comboBanner.classList.remove('show');
    }, 1200);
  }

  function resetHintTimer() {
    if (hintTimer) clearTimeout(hintTimer);
    document.querySelectorAll('.candy.hint-pulse').forEach(c => c.classList.remove('hint-pulse'));

    hintTimer = setTimeout(() => {
      findAndShowHint();
    }, 5000);
  }

  function findAndShowHint() {
    for (let r = 0; r < ROWS; r++) {
      for (let c = 0; c < COLS; c++) {
        // Try swap right
        if (c < COLS - 1) {
          swapCandies(board[r][c], board[r][c + 1]);
          const m = findMatches();
          swapCandies(board[r][c], board[r][c + 1]);
          if (m.allMatches.length > 0) {
            if (board[r][c].candyEl) board[r][c].candyEl.classList.add('hint-pulse');
            if (board[r][c + 1].candyEl) board[r][c + 1].candyEl.classList.add('hint-pulse');
            return;
          }
        }
        // Try swap down
        if (r < ROWS - 1) {
          swapCandies(board[r][c], board[r + 1][c]);
          const m = findMatches();
          swapCandies(board[r][c], board[r + 1][c]);
          if (m.allMatches.length > 0) {
            if (board[r][c].candyEl) board[r][c].candyEl.classList.add('hint-pulse');
            if (board[r + 1][c].candyEl) board[r + 1][c].candyEl.classList.add('hint-pulse');
            return;
          }
        }
      }
    }
  }

  function wait(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  // =========================================================
  // ENHANCED LEVEL SELECTOR & WORLD TABS
  // =========================================================
  let activeWorldTab = 0; // 0 = All Worlds, 1..10 = specific world

  const WORLD_METADATA = [
    { num: 1, name: "Candy Town", icon: "🍬" },
    { num: 2, name: "Lemon Lake", icon: "🍋" },
    { num: 3, name: "Chocolate Mtn", icon: "🍫" },
    { num: 4, name: "Cookie Forest", icon: "🍪" },
    { num: 5, name: "Rainbow Meadow", icon: "🌈" },
    { num: 6, name: "Cotton Peaks", icon: "🧁" },
    { num: 7, name: "Caramel Canyon", icon: "🍮" },
    { num: 8, name: "Gummy Lagoon", icon: "🍨" },
    { num: 9, name: "Starlight Bakery", icon: "🍩" },
    { num: 10, name: "Sugar Kingdom", icon: "👑" }
  ];

  function renderWorldTabs() {
    if (!dom.worldTabsContainer) return;
    dom.worldTabsContainer.innerHTML = '';

    // 'All' Tab
    const allBtn = document.createElement('button');
    allBtn.className = `world-tab ${activeWorldTab === 0 ? 'active' : ''}`;
    allBtn.textContent = 'All (1-100)';
    allBtn.addEventListener('click', () => {
      sound.playClick();
      activeWorldTab = 0;
      renderWorldTabs();
      renderLevelSelector();
    });
    dom.worldTabsContainer.appendChild(allBtn);

    // World 1..10 Tabs
    WORLD_METADATA.forEach(w => {
      let worldStars = 0;
      for (let i = (w.num - 1) * 10 + 1; i <= w.num * 10; i++) {
        worldStars += profile.levelStars[i] || 0;
      }
      const tab = document.createElement('button');
      tab.className = `world-tab ${activeWorldTab === w.num ? 'active' : ''}`;
      tab.innerHTML = `${w.icon} W${w.num} <span style="font-size:0.75rem;opacity:0.85;">(${worldStars}/30⭐)</span>`;
      tab.addEventListener('click', () => {
        sound.playClick();
        activeWorldTab = w.num;
        renderWorldTabs();
        renderLevelSelector();
      });
      dom.worldTabsContainer.appendChild(tab);
    });
  }

  function renderLevelSelector() {
    if (!dom.levelsContainer) return;
    dom.levelsContainer.innerHTML = '';

    // Calculate total stars
    let totalStars = 0;
    for (let i = 1; i <= 100; i++) {
      totalStars += (profile.levelStars[i] || 0);
    }
    if (dom.levelsTotalStars) {
      dom.levelsTotalStars.textContent = `⭐ ${totalStars}/300 Stars`;
    }

    const filteredLevels = activeWorldTab === 0
      ? LEVELS
      : LEVELS.filter(l => l.worldNum === activeWorldTab);

    filteredLevels.forEach(lvl => {
      const isUnlocked = lvl.level <= profile.unlockedLevel;
      const isCurrent = lvl.level === profile.unlockedLevel;
      const stars = profile.levelStars[lvl.level] || 0;
      const bestScore = profile.levelHighScores[lvl.level] || 0;

      const card = document.createElement('div');
      card.className = `level-card ${isUnlocked ? 'unlocked' : 'locked'} ${isCurrent ? 'current' : ''}`;

      let goalIcon = '🎯';
      if (lvl.goalType === 'jelly') goalIcon = '🍧';
      else if (lvl.goalType === 'frosting') goalIcon = '🧊';
      else if (lvl.goalType === 'collect') goalIcon = '🧺';
      else if (lvl.goalType === 'specials') goalIcon = '⚡';
      else if (lvl.goalType === 'hybrid') goalIcon = lvl.level === 100 ? '🏆' : '👑';

      if (isUnlocked) {
        card.innerHTML = `
          ${lvl.isMilestone ? `<div class="level-milestone-tag">${lvl.level === 100 ? 'FINALE' : 'MILESTONE'}</div>` : ''}
          <div class="level-number">${lvl.level}</div>
          <div class="level-name">${lvl.name}</div>
          <div class="level-goal-badge">${goalIcon} ${lvl.goalType.toUpperCase()}</div>
          <div class="level-stars">
            ${'⭐'.repeat(stars)}${'☆'.repeat(3 - stars)}
          </div>
          ${bestScore > 0 ? `<div class="level-best-score">Best: ${bestScore.toLocaleString()}</div>` : ''}
        `;
        let cardTriggered = false;
        const triggerLevelPlay = (e) => {
          if (cardTriggered) return;
          cardTriggered = true;
          setTimeout(() => { cardTriggered = false; }, 300);
          startPlayLevel(lvl.level);
        };
        card.addEventListener('click', triggerLevelPlay);
        card.addEventListener('touchend', (e) => {
          if (e.cancelable) e.preventDefault();
          triggerLevelPlay(e);
        });
      } else {
        card.innerHTML = `
          <div class="level-lock-icon">🔒</div>
          <div class="level-number">Level ${lvl.level}</div>
          <div class="level-name">${lvl.world}</div>
        `;
      }
      dom.levelsContainer.appendChild(card);
    });
  }

  // =========================================================
  // EVENT LISTENERS: MENU, PAUSE, DIALOGS
  // =========================================================
  // =========================================================
  // DAILY CHALLENGE SYSTEM (Offline Date-Seeded)
  // =========================================================
  const DAILY_MODIFIERS = [
    { type: 'double-score', name: 'Double Cascade Blitz', desc: 'Score double points on cascading combo matches!', icon: '✨' },
    { type: 'rainbow-frenzy', name: 'Rainbow Sparkle Frenzy', desc: 'Board starts with 2 free Rainbow Cores ready to detonate!', icon: '🌈' },
    { type: 'berry-harvest', name: 'Strawberry & Berry Feast', desc: 'Harvest 30 Strawberries and Blueberries in 24 strategic moves!', icon: '🍓' },
    { type: 'frosting-fortress', name: 'Frosting Fortress Siege', desc: 'Break through 18 chilled frosting blockers guarding the sweets!', icon: '🧊' },
    { type: 'jelly-sweeper', name: 'Jelly Sweeper Rush', desc: 'Clear 20 sparkling jellies before your 24 moves run out!', icon: '🍧' }
  ];

  function getDailyChallenge() {
    const today = new Date().toISOString().slice(0, 10);
    let hash = 0;
    for (let i = 0; i < today.length; i++) {
      hash = ((hash << 5) - hash) + today.charCodeAt(i);
      hash |= 0;
    }
    const seed = Math.abs(hash);
    const mod = DAILY_MODIFIERS[seed % DAILY_MODIFIERS.length];
    const targetScore = 7000 + (seed % 6) * 1000;
    return {
      date: today,
      modifier: mod,
      targetScore: targetScore,
      moves: 24,
      rewardCoins: 150
    };
  }

  function openDailyChallengeModal() {
    const dc = getDailyChallenge();
    const today = dc.date;
    const isCompletedToday = (profile.lastDailyDate === today);

    if (dom.dailyDateText) dom.dailyDateText.textContent = `Today: ${today}`;
    if (dom.dailyStreakBadge) dom.dailyStreakBadge.textContent = `🔥 ${profile.dailyStreak || 0}-Day Streak`;
    if (dom.dailyModBadge) dom.dailyModBadge.textContent = `${dc.modifier.icon} ${dc.modifier.name}`;
    if (dom.dailyModDesc) dom.dailyModDesc.textContent = dc.modifier.desc;
    if (dom.dailyRewardBadge) dom.dailyRewardBadge.textContent = isCompletedToday ? 'Completed Today! ✅' : '+150 🪙 Coins & +1 🔨 Hammer';

    if (dom.btnStartDaily) {
      dom.btnStartDaily.textContent = isCompletedToday ? 'PLAY AGAIN (PRACTICE) ▶' : 'START DAILY CHALLENGE ▶';
    }
    openModal(dom.modalDailyChallenge);
  }

  function startDailyChallenge() {
    const dc = getDailyChallenge();
    closeModal(dom.modalDailyChallenge);
    sound.init();

    currentLevelData = {
      level: 'Daily',
      name: dc.modifier.name,
      world: 'Daily Sweet Quest',
      moves: dc.moves,
      goalType: 'score',
      targetScore: dc.targetScore,
      starScores: [dc.targetScore, Math.floor(dc.targetScore * 1.4), Math.floor(dc.targetScore * 2.0)],
      description: dc.modifier.desc,
      isDaily: true,
      layout: 'center-cross'
    };

    score = 0;
    movesLeft = dc.moves;
    earnedStars = 0;
    isProcessing = false;
    activeBooster = null;
    selectedTile = null;
    swapFirstTile = null;
    currentComboCount = 0;

    dom.gameLevelName.textContent = 'Daily Challenge';
    dom.gameMoves.textContent = movesLeft;
    dom.gameMoves.classList.remove('low-moves');
    dom.gameScore.textContent = '0';
    dom.scoreProgressFill.style.width = '0%';
    dom.starSlots.forEach(s => s.classList.remove('earned'));

    initBoardLayout();

    if (dc.modifier.type === 'frosting-fortress') {
      currentLevelData.goalType = 'frosting';
      currentLevelData.targetFrosting = 16;
      remainingFrosting = applyObstacleLayout('border', 'frosting', 16);
    } else if (dc.modifier.type === 'jelly-sweeper') {
      currentLevelData.goalType = 'jelly';
      currentLevelData.targetJellies = 18;
      remainingJellies = applyObstacleLayout('checker', 'jelly', 18);
    } else if (dc.modifier.type === 'berry-harvest') {
      currentLevelData.goalType = 'collect';
      currentLevelData.collectCandies = { red: 20, blue: 20 };
      collectedCandies = {};
    }

    fillInitialBoard();

    // Modifier perk: Rainbow frenzy spawns 2 rainbow cores!
    if (dc.modifier.type === 'rainbow-frenzy') {
      board[3][3].special = 'rainbow';
      board[3][3].candy = 'rainbow';
      board[4][4].special = 'rainbow';
      board[4][4].candy = 'rainbow';
    }

    renderBoard();
    updateObjectiveDisplay();
    resetHintTimer();
    switchScreen(dom.screenGame);
  }

  // =========================================================
  // ACHIEVEMENTS SYSTEM (12 Badges)
  // =========================================================
  const ACHIEVEMENTS_DEF = [
    { id: 'first_win', name: 'Sugar Starter', icon: '🍬', desc: 'Complete Level 1', target: 1, current: () => (profile.unlockedLevel > 1 ? 1 : 0), rewardCoins: 50 },
    { id: 'reach_w2', name: 'World Voyager', icon: '⛵', desc: 'Unlock World 2 (Reach Level 11)', target: 11, current: () => Math.min(11, profile.unlockedLevel), rewardCoins: 100 },
    { id: 'reach_w5', name: 'Halfway Hero', icon: '⭐', desc: 'Reach World 5 (Level 51)', target: 51, current: () => Math.min(51, profile.unlockedLevel), rewardCoins: 250, rewardBooster: 'hammer' },
    { id: 'beat_all_100', name: 'Sugar Legend', icon: '👑', desc: 'Complete all 100 levels!', target: 100, current: () => Math.min(100, (profile.stats.totalLevelsWon || 0)), rewardCoins: 1000, rewardBooster: 'rainbow' },
    { id: 'stars_30', name: 'Starlight Novice', icon: '🌟', desc: 'Earn 30 total stars', target: 30, current: () => Object.values(profile.levelStars).reduce((a, b) => a + b, 0), rewardCoins: 100 },
    { id: 'stars_150', name: 'Constellation Master', icon: '✨', desc: 'Earn 150 total stars', target: 150, current: () => Object.values(profile.levelStars).reduce((a, b) => a + b, 0), rewardCoins: 300, rewardBooster: 'swap' },
    { id: 'stars_300', name: 'Celestial Perfection', icon: '💫', desc: 'Collect all 300 stars across 100 levels!', target: 300, current: () => Object.values(profile.levelStars).reduce((a, b) => a + b, 0), rewardCoins: 1500, rewardBooster: 'bomb' },
    { id: 'combo_5', name: 'Combo Virtuoso', icon: '🔥', desc: 'Achieve a 5x combo cascade', target: 5, current: () => profile.stats.maxCombo || 0, rewardCoins: 120 },
    { id: 'specials_25', name: 'Rainbow Alchemist', icon: '⚡', desc: 'Craft 25 special candies', target: 25, current: () => profile.stats.totalSpecialsCreated || 0, rewardCoins: 150, rewardBooster: 'rainbow' },
    { id: 'clear_100_jelly', name: 'Jelly Liberator', icon: '🍧', desc: 'Clear 100 sweet jelly tiles', target: 100, current: () => profile.stats.totalJelliesCleared || 0, rewardCoins: 150 },
    { id: 'break_100_frost', name: 'Frosting Breaker', icon: '🧊', desc: 'Crack 100 frosting blockers', target: 100, current: () => profile.stats.totalFrostingBroken || 0, rewardCoins: 150 },
    { id: 'daily_streak_3', name: 'Dedicated Confectioner', icon: '🗓️', desc: 'Achieve a 3-day Daily Challenge streak', target: 3, current: () => profile.dailyStreak || 0, rewardCoins: 300, rewardBooster: 'hammer' }
  ];

  function renderAchievements() {
    if (!dom.achievementsList) return;
    dom.achievementsList.innerHTML = '';

    let claimedCount = 0;
    ACHIEVEMENTS_DEF.forEach(ach => {
      const cur = ach.current();
      const isComplete = cur >= ach.target;
      const isClaimed = !!profile.claimedAchievements[ach.id];
      if (isClaimed) claimedCount++;

      const pct = Math.min(100, Math.floor((cur / ach.target) * 100));

      const card = document.createElement('div');
      card.className = `achievement-card ${isComplete ? 'completed' : ''} ${isClaimed ? 'claimed' : ''}`;

      card.innerHTML = `
        <div class="ach-icon">${ach.icon}</div>
        <div class="ach-info">
          <div class="ach-title">${ach.name}</div>
          <div class="ach-desc">${ach.desc}</div>
          <div class="ach-progress-bar">
            <div class="ach-progress-fill" style="width: ${pct}%"></div>
          </div>
          <div class="ach-progress-text">${Math.min(cur, ach.target)} / ${ach.target}</div>
        </div>
        <div class="ach-action">
          ${isClaimed
            ? '<button class="ach-btn claimed" disabled>Claimed ✅</button>'
            : isComplete
              ? `<button class="ach-btn claimable" data-id="${ach.id}">CLAIM 🎁</button>`
              : '<button class="ach-btn locked" disabled>In Progress</button>'
          }
        </div>
      `;

      const claimBtn = card.querySelector('.ach-btn.claimable');
      if (claimBtn) {
        claimBtn.addEventListener('click', () => {
          profile.claimedAchievements[ach.id] = true;
          profile.coins += ach.rewardCoins;
          if (ach.rewardBooster) {
            profile.boosters[ach.rewardBooster] = (profile.boosters[ach.rewardBooster] || 0) + 1;
          }
          saveProfile();
          updateGlobalHUD();
          sound.playSpecialCreated();
          showToast(`Claimed ${ach.name}! +${ach.rewardCoins} coins awarded! 🪙`);
          renderAchievements();
        });
      }

      dom.achievementsList.appendChild(card);
    });

    if (dom.achievementsProgressSummary) {
      dom.achievementsProgressSummary.textContent = `${claimedCount} of ${ACHIEVEMENTS_DEF.length} Badges Claimed`;
    }
  }

  // =========================================================
  // SWEET RECIPE COMPENDIUM (10 Milestone Collectibles)
  // =========================================================
  const RECIPES_DEF = [
    { level: 10, name: "Strawberry Swirl Tart", world: "Candy Town", icon: "🥧", desc: "A crisp butter pastry shell filled with wild strawberry nectar and crystallized sugar swirls." },
    { level: 20, name: "Lemon Chiffon Delight", world: "Lemon Lake", icon: "🍋", desc: "Cloud-light sponge cake drenched in Meyer lemon glaze with candied citrus pearls." },
    { level: 30, name: "Alpine Cocoa Truffle", world: "Chocolate Mountain", icon: "🍫", desc: "Rich single-origin dark cocoa ganache rolled in golden hazelnut praline dust." },
    { level: 40, name: "Gingerbread Cookie Crunch", world: "Cookie Forest", icon: "🍪", desc: "Spiced cinnamon and molasses cookies baked crispy with royal sugar lace." },
    { level: 50, name: "Rainbow Macaron Tower", world: "Rainbow Meadow", icon: "🌈", desc: "Delicate almond meringue shells filled with seven colorful berry buttercreams." },
    { level: 60, name: "Cotton Candy Soufflé", world: "Cotton Candy Peaks", icon: "🧁", desc: "Warm spun sugar cloud that melts instantaneously on the tongue with vanilla bean aroma." },
    { level: 70, name: "Salted Caramel Fudge", world: "Caramel Canyon", icon: "🍮", desc: "Slow-simmered golden butterscotch square finished with hand-harvested sugar crystals." },
    { level: 80, name: "Gummy Berry Gelato", world: "Gummy Lagoon", icon: "🍨", desc: "Velvety chilled berry cream swirled with sparkling jewel-toned fruit drops." },
    { level: 90, name: "Starlight Glazed Donut", world: "Starlight Bakery", icon: "🍩", desc: "Brioche ring glazed with galaxy blueberry syrup and shimmering edible stardust." },
    { level: 100, name: "Royal Sugar Crown Cake", world: "Sugar Kingdom", icon: "👑", desc: "The ultimate 10-tier imperial confections monument, crowned with caramelized sugar jewels!" }
  ];

  function renderRecipes() {
    if (!dom.recipesList) return;
    dom.recipesList.innerHTML = '';

    let mastered = 0;
    RECIPES_DEF.forEach(r => {
      const isUnlocked = profile.unlockedLevel > r.level || profile.unlockedRecipes[r.level];
      if (isUnlocked) mastered++;

      const card = document.createElement('div');
      card.className = `recipe-card ${isUnlocked ? 'unlocked' : 'locked'}`;
      card.innerHTML = `
        <div class="recipe-icon">${isUnlocked ? r.icon : '🔒'}</div>
        <div class="recipe-info">
          <div class="recipe-title">${r.name}</div>
          <div class="recipe-world">World ${r.level / 10}: ${r.world}</div>
          <div class="recipe-desc">${isUnlocked ? r.desc : `Unlocks upon clearing Milestone Level ${r.level}!`}</div>
        </div>
        <div class="recipe-badge">${isUnlocked ? 'MASTERED ✨' : `Lvl ${r.level} 🔒`}</div>
      `;
      dom.recipesList.appendChild(card);
    });

    if (dom.recipesMasteredCount) {
      dom.recipesMasteredCount.textContent = `${mastered} / 10 Mastered`;
    }
  }

  // Hook new menu buttons
  if (dom.btnOpenDaily) {
    dom.btnOpenDaily.addEventListener('click', () => {
      sound.playClick();
      openDailyChallengeModal();
    });
  }
  if (dom.btnStartDaily) {
    dom.btnStartDaily.addEventListener('click', startDailyChallenge);
  }
  if (dom.btnOpenAchievements) {
    dom.btnOpenAchievements.addEventListener('click', () => {
      sound.playClick();
      renderAchievements();
      openModal(dom.modalAchievements);
    });
  }
  if (dom.btnOpenCollection) {
    dom.btnOpenCollection.addEventListener('click', () => {
      sound.playClick();
      renderRecipes();
      openModal(dom.modalCollection);
    });
  }

  // Extra moves button in Game Over modal
  if (dom.gameoverBtnExtraMoves) {
    dom.gameoverBtnExtraMoves.addEventListener('click', () => {
      if ((profile.boosters.moves || 0) > 0) {
        profile.boosters.moves--;
        movesLeft += 5;
        updateMovesDisplay();
        updateGlobalHUD();
        saveProfile();
        closeModal(dom.modalGameOver);
        sound.playSpecialCreated();
        showToast('+5 Moves Added! Keep going! 🍬');
      } else if (profile.coins >= 50) {
        profile.coins -= 50;
        movesLeft += 5;
        updateMovesDisplay();
        updateGlobalHUD();
        saveProfile();
        closeModal(dom.modalGameOver);
        sound.playSpecialCreated();
        showToast('+5 Moves Added for 50 🪙! Keep going! 🍬');
      } else {
        showToast('Need 50 coins for +5 moves! Visit Shop 🛒');
      }
    });
  }

  // Free instant retry in Game Over modal
  if (dom.gameoverBtnRetry) {
    dom.gameoverBtnRetry.addEventListener('click', () => {
      sound.playClick();
      closeModal(dom.modalGameOver);
      startPlayLevel(typeof currentLevelData.level === 'number' ? currentLevelData.level : 1);
    });
  }

  let lastQuickPlayTime = 0;
  const triggerQuickPlay = (e) => {
    const now = Date.now();
    if (now - lastQuickPlayTime < 300) return;
    lastQuickPlayTime = now;
    startPlayLevel(profile.unlockedLevel || 1);
  };
  dom.btnPlayQuick.addEventListener('click', triggerQuickPlay);
  dom.btnPlayQuick.addEventListener('touchend', (e) => {
    if (e.cancelable) e.preventDefault();
    triggerQuickPlay(e);
  });

  dom.btnOpenLevels.addEventListener('click', () => {
    sound.init();
    renderWorldTabs();
    renderLevelSelector();
    switchScreen(dom.screenLevels);
  });

  dom.levelsBackBtn.addEventListener('click', () => {
    switchScreen(dom.screenMainMenu);
  });

  dom.btnOpenHowToPlay.addEventListener('click', () => {
    openModal(dom.modalHowToPlay);
  });

  dom.btnOpenShop.addEventListener('click', () => {
    updateGlobalHUD();
    openModal(dom.modalShop);
  });

  dom.btnOpenSettings.addEventListener('click', () => {
    dom.settingSfxToggle.checked = profile.settings.sfx;
    dom.settingMusicToggle.checked = profile.settings.music;
    dom.settingHapticToggle.checked = profile.settings.haptic;
    openModal(dom.modalSettings);
  });

  // Sound toggle button in header
  dom.soundBtn.addEventListener('click', () => {
    sound.init();
    profile.settings.sfx = !profile.settings.sfx;
    profile.settings.music = profile.settings.sfx;
    saveProfile();
    updateGlobalHUD();
    if (profile.settings.music) sound.startBgm(); else sound.stopBgm();
  });

  // Settings toggles
  dom.settingSfxToggle.addEventListener('change', (e) => {
    profile.settings.sfx = e.target.checked;
    saveProfile();
    updateGlobalHUD();
  });

  dom.settingMusicToggle.addEventListener('change', (e) => {
    profile.settings.music = e.target.checked;
    saveProfile();
    updateGlobalHUD();
    if (profile.settings.music) sound.startBgm(); else sound.stopBgm();
  });

  dom.settingHapticToggle.addEventListener('change', (e) => {
    profile.settings.haptic = e.target.checked;
    saveProfile();
  });

  dom.btnResetData.addEventListener('click', () => {
    if (confirm('Are you sure you want to reset all stars, high scores, and progress?')) {
      localStorage.removeItem(STORAGE_KEY);
      profile = loadProfile();
      updateGlobalHUD();
      closeModal(dom.modalSettings);
      showToast('Progress has been reset.');
    }
  });

  // Shop Booster Buy Buttons
  document.querySelectorAll('.buy-btn, .btn-buy-booster').forEach(btn => {
    btn.addEventListener('click', () => {
      sound.init();
      const card = btn.closest('.shop-item-card');
      const item = btn.getAttribute('data-item') || card?.getAttribute('data-booster');
      const cost = parseInt(btn.getAttribute('data-cost') || card?.getAttribute('data-cost') || '150', 10);
      if (!item) return;

      if (profile.coins >= cost) {
        profile.coins -= cost;
        const addCount = (item === 'hammer' || item === 'swap') ? 2 : 1;
        profile.boosters[item] = (profile.boosters[item] || 0) + addCount;
        saveProfile();
        updateGlobalHUD();
        sound.playSpecialCreated();
        showToast(`Bought ${item.toUpperCase()} booster!`);
      } else {
        sound.playClick();
        showToast('Not enough coins! Complete levels to earn coins 🪙');
      }
    });
  });

  // Daily Free Coins in Shop (once per 24 hours)
  dom.btnClaimFreeCoins.addEventListener('click', () => {
    sound.init();
    const oneDay = 24 * 60 * 60 * 1000;
    if (Date.now() - (profile.lastDailyClaim || 0) > oneDay) {
      profile.coins += 100;
      profile.lastDailyClaim = Date.now();
      saveProfile();
      updateGlobalHUD();
      sound.playWin();
      showToast('Claimed +100 Free Coins! 🎁');
    } else {
      showToast('Already claimed today! Check back tomorrow 🎁');
    }
  });

  // Pause Controls
  dom.gamePauseBtn.addEventListener('click', () => {
    openModal(dom.modalPause);
  });

  dom.gameRestartQuickBtn.addEventListener('click', () => {
    sound.playClick();
    startPlayLevel(currentLevelData.level);
    showToast('Level restarted!');
  });

  dom.pauseBtnResume.addEventListener('click', () => {
    closeModal(dom.modalPause);
    sound.playClick();
  });

  dom.pauseBtnRestart.addEventListener('click', () => {
    closeModal(dom.modalPause);
    startPlayLevel(currentLevelData.level);
  });

  dom.pauseBtnSound.addEventListener('click', () => {
    sound.init();
    profile.settings.sfx = !profile.settings.sfx;
    profile.settings.music = profile.settings.sfx;
    saveProfile();
    updateGlobalHUD();
    if (profile.settings.music) sound.startBgm(); else sound.stopBgm();
  });

  dom.pauseBtnMenu.addEventListener('click', () => {
    closeModal(dom.modalPause);
    switchScreen(dom.screenMainMenu);
  });

  // Win Modal actions
  dom.winBtnNext.addEventListener('click', () => {
    closeModal(dom.modalLevelComplete);
    if (currentLevelData.level < LEVELS.length) {
      startPlayLevel(currentLevelData.level + 1);
    } else {
      showToast('🎉 All levels completed! You are the Sugar Master!');
      switchScreen(dom.screenMainMenu);
    }
  });

  dom.winBtnReplay.addEventListener('click', () => {
    closeModal(dom.modalLevelComplete);
    startPlayLevel(currentLevelData.level);
  });

  dom.winBtnMenu.addEventListener('click', () => {
    closeModal(dom.modalLevelComplete);
    renderLevelSelector();
    switchScreen(dom.screenLevels);
  });

  // Game Over Modal actions
  dom.gameoverBtnExtraMoves.addEventListener('click', () => {
    sound.init();
    if (profile.coins >= 50) {
      profile.coins -= 50;
      saveProfile();
      updateGlobalHUD();
      closeModal(dom.modalGameOver);
      movesLeft += 5;
      updateMovesDisplay();
      sound.playSpecialCreated();
      showToast('+5 Extra Moves Added! Keep blasting!');
    } else {
      sound.playClick();
      showToast('Need 50 coins for extra moves! 🪙');
    }
  });

  dom.gameoverBtnRetry.addEventListener('click', () => {
    closeModal(dom.modalGameOver);
    if (profile.lives <= 0) {
      if (profile.coins >= 100) {
        if (confirm("Out of lives! Refill to 5 lives for 100 coins?")) {
          profile.coins -= 100;
          profile.lives = 5;
          saveProfile();
          updateGlobalHUD();
          setupLevel(currentLevelData.level - 1);
        } else {
          switchScreen(dom.screenMainMenu);
        }
      } else {
        showToast('No lives left! Start a new session to restore 5 lives ❤️');
        switchScreen(dom.screenMainMenu);
      }
    } else {
      setupLevel(currentLevelData.level - 1);
    }
  });

  dom.gameoverBtnMenu.addEventListener('click', () => {
    closeModal(dom.modalGameOver);
    switchScreen(dom.screenMainMenu);
  });

  // Mascot tap easter egg
  const mascotAvatar = document.querySelector('.mascot-avatar');
  if (mascotAvatar) {
    mascotAvatar.addEventListener('click', () => {
      sound.init();
      sound.playComboFanfare(3);
      dom.mascotBubble.textContent = "🍬 Pip: Yummy! Let's blast some sugar combos!";
    });
  }

  // Refill lives on pill click
  if (dom.hudLivesBtn) {
    dom.hudLivesBtn.addEventListener('click', () => {
      sound.init();
      if (profile.lives < profile.maxLives) {
        if (confirm(`Refill to 5 lives for 100 coins? You have ${profile.coins} coins.`)) {
          if (profile.coins >= 100) {
            profile.coins -= 100;
            profile.lives = profile.maxLives;
            saveProfile();
            updateGlobalHUD();
            sound.playWin();
            showToast('Lives refilled to 5 ❤️!');
          } else {
            showToast('Not enough coins to refill lives.');
          }
        }
      } else {
        showToast('Lives are already full! ❤️');
      }
    });
  }

  // Coins pill click opens Shop
  if (dom.hudCoinsBtn) {
    dom.hudCoinsBtn.addEventListener('click', () => {
      sound.init();
      updateGlobalHUD();
      openModal(dom.modalShop);
    });
  }

  // =========================================================
  // INITIALIZATION & WEBSITE INTEGRATION
  // =========================================================
  window.addEventListener('load', () => {
    checkLifeRegen();
    updateGlobalHUD();

    // User interaction unlock for Web Audio API
    const unlockAudio = () => {
      sound.init();
      document.body.removeEventListener('click', unlockAudio);
      document.body.removeEventListener('touchstart', unlockAudio);
    };
    document.body.addEventListener('click', unlockAudio, { passive: true });
    document.body.addEventListener('touchstart', unlockAudio, { passive: true });
  });

  // =========================================================
  // WEBSITE INTERACTIVE FEATURES & NAVIGATION
  // =========================================================
  // Mobile Hamburger Navigation Drawer
  const mobileMenuBtn = document.getElementById('mobile-menu-btn');
  const mobileDrawer = document.getElementById('mobile-drawer');
  const mobileNavLinks = document.querySelectorAll('.mobile-nav-link');

  if (mobileMenuBtn && mobileDrawer) {
    mobileMenuBtn.addEventListener('click', () => {
      const isOpen = mobileDrawer.classList.toggle('open');
      mobileMenuBtn.classList.toggle('active', isOpen);
      mobileMenuBtn.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
    });

    mobileNavLinks.forEach(link => {
      link.addEventListener('click', () => {
        mobileDrawer.classList.remove('open');
        mobileMenuBtn.classList.remove('active');
        mobileMenuBtn.setAttribute('aria-expanded', 'false');
      });
    });
  }

  // Header Scroll Shadow
  const siteHeader = document.getElementById('site-header');
  if (siteHeader) {
    window.addEventListener('scroll', () => {
      if (window.scrollY > 20) {
        siteHeader.classList.add('scrolled');
      } else {
        siteHeader.classList.remove('scrolled');
      }
    }, { passive: true });
  }

  // Unified PLAY NOW handler: Scrolls to game arena and launches game
  const playNowButtons = document.querySelectorAll(
    '#nav-btn-play, #hero-btn-play, #hero-play-btn, .mobile-play-cta, .footer-play-btn, .btn-about-play'
  );

  playNowButtons.forEach(btn => {
    btn.addEventListener('click', (e) => {
      const gameSection = document.getElementById('game-section');
      if (gameSection) {
        gameSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }

      if (mobileDrawer && mobileDrawer.classList.contains('open')) {
        mobileDrawer.classList.remove('open');
        mobileMenuBtn?.classList.remove('active');
        mobileMenuBtn?.setAttribute('aria-expanded', 'false');
      }

      if (dom.modalPause?.classList.contains('active')) {
        closeModal(dom.modalPause);
      }

      // If not already in the game screen, launch gameplay immediately
      if (!dom.screenGame || !dom.screenGame.classList.contains('active')) {
        startPlayLevel(profile.unlockedLevel || 1);
      }
    });
  });

  // Smooth scrolling for in-page anchors
  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
      const targetId = this.getAttribute('href');
      if (targetId && targetId !== '#' && targetId.length > 1) {
        const targetElement = document.querySelector(targetId);
        if (targetElement) {
          e.preventDefault();
          targetElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
      }
    });
  });

  // Back to Top Button
  const backToTopBtn = document.getElementById('back-to-top-btn');
  if (backToTopBtn) {
    backToTopBtn.addEventListener('click', (e) => {
      e.preventDefault();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
  }

  // Theater Mode / Fullscreen Toggle
  const btnTheaterMode = document.getElementById('btn-theater-mode');
  const gameEmbedWrapper = document.getElementById('game-embed-wrapper');
  if (btnTheaterMode && gameEmbedWrapper) {
    btnTheaterMode.addEventListener('click', () => {
      const isTheater = gameEmbedWrapper.classList.toggle('theater-mode');
      if (isTheater) {
        btnTheaterMode.innerHTML = '<span class="tool-icon">✕</span> Exit Fullscreen';
        showToast('Theater Mode Activated! Tap Exit to restore.');
      } else {
        btnTheaterMode.innerHTML = '<span class="tool-icon">⛶</span> Fullscreen';
      }
    });

    // Escape key exits theater mode
    window.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && gameEmbedWrapper.classList.contains('theater-mode')) {
        gameEmbedWrapper.classList.remove('theater-mode');
        btnTheaterMode.innerHTML = '<span class="tool-icon">⛶</span> Fullscreen';
      }
    });
  }

  // FAQ Accordion
  const faqItems = document.querySelectorAll('.faq-item');
  faqItems.forEach(item => {
    const question = item.querySelector('.faq-question');
    if (question) {
      question.addEventListener('click', () => {
        const wasActive = item.classList.contains('active');
        faqItems.forEach(otherItem => {
          if (otherItem !== item) otherItem.classList.remove('active');
        });
        item.classList.toggle('active', !wasActive);
      });
    }
  });

  // Contact Form Submission
  const contactForm = document.getElementById('contact-form');
  const contactFeedback = document.getElementById('contact-feedback');
  if (contactForm && contactFeedback) {
    contactForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const nameInput = document.getElementById('contact-name');
      const emailInput = document.getElementById('contact-email');
      const subjectInput = document.getElementById('contact-subject');
      const messageInput = document.getElementById('contact-message');

      const name = nameInput ? nameInput.value.trim() : 'Player';
      const email = emailInput ? emailInput.value.trim() : '';
      const message = messageInput ? messageInput.value.trim() : '';

      if (!name || !email || !message) {
        contactFeedback.textContent = 'Please fill out all required fields with a valid email.';
        contactFeedback.className = 'contact-feedback error';
        return;
      }

      sound.init();
      sound.playWin();
      contactFeedback.textContent = `🎉 Sweet! Thank you, ${name}! Your inquiry regarding "${subjectInput ? subjectInput.value : 'Game Feedback'}" has been recorded. Pip and the TJ Sugar Quest bakery team will be in touch!`;
      contactFeedback.className = 'contact-feedback success';
      contactForm.reset();

      setTimeout(() => {
        contactFeedback.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
      }, 100);
    });
  }

  // Mascot Showcase click on landing page
  const showcaseMascot = document.querySelector('.hero-avatar');
  const showcaseBubble = document.querySelector('.showcase-bubble');
  if (showcaseMascot && showcaseBubble) {
    showcaseMascot.addEventListener('click', () => {
      sound.init();
      sound.playComboFanfare(2);
      const quotes = [
        "🍬 Pip: Match 3 candies in a row for sweet crunching!",
        "⚡ Pip: Match 4 to forge a striped Line Blast!",
        "💣 Pip: Match 5 in an L-shape for an explosive Candy Bomb!",
        "🌈 Pip: Match 5 in a row to bake a Rainbow Sugar Orb!"
      ];
      showcaseBubble.textContent = quotes[Math.floor(Math.random() * quotes.length)];
    });
  }

  // Floating Candies clickable easter egg
  const floatCandies = document.querySelectorAll('.float-candy');
  floatCandies.forEach(candy => {
    candy.addEventListener('click', () => {
      sound.init();
      sound.playPop();
      candy.style.transform = 'scale(1.4) rotate(20deg)';
      setTimeout(() => {
        candy.style.transform = '';
      }, 250);
    });
  });

  if (typeof window !== 'undefined') {
    window.__SugarQuestGame = {
      setupLevel,
      startPlayLevel,
      switchScreen,
      getGameState: () => currentGameState,
      handlePlayerSwap,
      resetInputState,
      hasPossibleMoves,
      shuffleBoard,
      getMoveState: () => currentMoveState,
      isProcessing: () => isProcessing,
      getBoard: () => board,
      getLevelData: () => currentLevelData,
      getMovesLeft: () => movesLeft,
      getScore: () => score,
      setMovesLeft: (val) => { movesLeft = val; }
    };
  }

})();
