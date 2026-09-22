package com.example.model

enum class MascotMood {
    IDLE,
    HAPPY,
    EXCITED,
    VICTORY,
    DISAPPOINTED
}

enum class MascotOutfit(
    val title: String,
    val description: String,
    val emoji: String,
    val costCoins: Int
) {
    CLASSIC_EXPLORER("Candy Scout", "Classic explorer jacket and sweet candy backpack", "🎒", 0),
    SUGAR_WIZARD("Sugar Magician", "Enchanted starry cape and lollipop wand", "🪄", 500),
    ROYAL_CANDY_KING("Candy Monarch", "Golden crown and royal caramel cloak", "👑", 1200),
    CHEF_BAKER("Pastry Chef", "White chef hat and chocolate frosting apron", "🧁", 350),
    ASTRONAUT("Sugar Astronaut", "Space helmet exploring cosmic sugar nebulas", "🚀", 800);

    val displayName: String get() = title
    val iconEmoji: String get() = emoji
}
