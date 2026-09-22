package com.example.model

enum class CollectionCategory(val title: String) {
    CANDIES("Candies"),
    SPECIAL_CANDIES("Special Candies"),
    SUGAR_CREATURES("Sugar Creatures"),
    WORLD_COLLECTIBLES("World Collectibles"),
    MYSTERY_ITEMS("Mystery Items")
}

data class CollectionItem(
    val id: String,
    val name: String,
    val category: CollectionCategory,
    val description: String,
    val iconEmoji: String,
    val unlockWorld: Int = 1,
    val flavorNote: String = ""
) {
    val isUnlocked: Boolean get() = true
}

val ALL_COLLECTION_ITEMS = listOf(
    // Candies
    CollectionItem("c_red", "Ruby Jelly Bean", CollectionCategory.CANDIES, "Juicy strawberry bean bursting with red sweetness.", "🔴", 1, "Pip's favorite hiking snack!"),
    CollectionItem("c_orange", "Amber Lozenge", CollectionCategory.CANDIES, "Citrusy orange lozenge with sweet tangerine notes.", "🟠", 1, "Glows under afternoon sunlight."),
    CollectionItem("c_yellow", "Lemon Drop", CollectionCategory.CANDIES, "Tangy lemon candy shaped like a glistening raindrop.", "🟡", 2, "Harvested fresh from Lemon Lake."),
    CollectionItem("c_green", "Emerald Mint", CollectionCategory.CANDIES, "Crisp peppermint square delivering instant freshness.", "🟢", 2, "Cool breeze in candy form!"),
    CollectionItem("c_blue", "Sapphire Gumdrop", CollectionCategory.CANDIES, "Chewy blueberry dome with glistening sugar glaze.", "🔵", 3, "Soft and bouncy!"),
    CollectionItem("c_purple", "Amethyst Grape", CollectionCategory.CANDIES, "Sweet purple grape cluster with rich berry depth.", "🟣", 3, "Rich aroma from royal vineyards."),

    // Special Candies
    CollectionItem("sp_striped", "Striped Sugar", CollectionCategory.SPECIAL_CANDIES, "Formed by matching 4 candies in a row. Blasts a laser beam across a whole row or column!", "⚡", 1, "Beams of pure sugar energy."),
    CollectionItem("sp_wrapped", "Sugar Bomb", CollectionCategory.SPECIAL_CANDIES, "Formed by matching 5 candies in T or L shape. Explodes in a 3x3 blast twice!", "💥", 2, "Double explosion power!"),
    CollectionItem("sp_donut", "Rainbow Core", CollectionCategory.SPECIAL_CANDIES, "Formed by 5 in a straight line. Clears every candy of the chosen color!", "🍩", 3, "The pinnacle of confectionery science."),
    CollectionItem("sp_lightning", "Sugar Lightning", CollectionCategory.SPECIAL_CANDIES, "Electrifies the board with dual row and column disintegration.", "⚡", 4, "High-voltage sweetness."),

    // Sugar Creatures
    CollectionItem("cr_pip", "Pip the Explorer", CollectionCategory.SUGAR_CREATURES, "The brave candy adventurer charting the 5 sweet worlds.", "🐰", 1, "Loves strawberry jellies and adventure!"),
    CollectionItem("cr_gummybear", "Gummy Bear Guardian", CollectionCategory.SUGAR_CREATURES, "Gentle jelly protector who guides travelers through Lemon Lake.", "🧸", 2, "Squishy and steadfast."),
    CollectionItem("cr_chocobird", "Cocoa Hummingbird", CollectionCategory.SUGAR_CREATURES, "Feathers made of spun milk chocolate who flits around Chocolate Mountain.", "🍫", 3, "Sings sweet cocoa melodies."),
    CollectionItem("cr_gingerbread", "Master Gingerbread", CollectionCategory.SUGAR_CREATURES, "Ancient baker sprite wandering the wafer groves of Cookie Forest.", "🍪", 4, "Holds the secret cinnamon spice."),
    CollectionItem("cr_cottondragon", "Cotton Candy Wyrm", CollectionCategory.SUGAR_CREATURES, "Fluffy pastel dragon soaring among the Rainbow Clouds.", "🐉", 5, "Breathes sugary sparkle mist."),

    // World Collectibles
    CollectionItem("s_candytown", "Candy Town Flag", CollectionCategory.WORLD_COLLECTIBLES, "Souvenir from the starting capital of the sweet kingdom.", "🚩", 1, "Where your adventure began."),
    CollectionItem("s_lemonlake", "Lemon Lilypad", CollectionCategory.WORLD_COLLECTIBLES, "Golden floating flower found along Lemon Lake.", "🪷", 2, "Sweet and tangy scent."),
    CollectionItem("s_chocomountain", "Chocolate Truffle Peak", CollectionCategory.WORLD_COLLECTIBLES, "Rare cocoa crystal mined from Chocolate Mountain.", "🏔️", 3, "100% pure enchanted cocoa."),
    CollectionItem("s_cookieforest", "Gilded Wafer Leaf", CollectionCategory.WORLD_COLLECTIBLES, "Crispy golden autumn leaf from Cookie Forest.", "🍁", 4, "Baked to golden perfection."),
    CollectionItem("s_rainbowclouds", "Prismatic Cloud Vial", CollectionCategory.WORLD_COLLECTIBLES, "Captured essence of Rainbow Clouds sky kingdom.", "🌈", 5, "Reflects all colors of sweet joy."),

    // Mystery Items
    CollectionItem("m_royalchest", "Royal Sugar Chest", CollectionCategory.MYSTERY_ITEMS, "Legendary vault locked with the 7-Day Sugar Seal.", "👑", 1, "Contains bountiful gold and lightning."),
    CollectionItem("m_goldencompass", "Sugar Compass", CollectionCategory.MYSTERY_ITEMS, "Points directly to undiscovered confection caches.", "🧭", 3, "Never loses its sweet magnetic pull."),
    CollectionItem("m_prismscepter", "Prismatic Scepter", CollectionCategory.MYSTERY_ITEMS, "Ancient relic capable of aligning all five sweet elements.", "🪄", 5, "Glows with infinite rainbow energy.")
)

val DEFAULT_COLLECTION_ITEMS = ALL_COLLECTION_ITEMS
