#!/usr/bin/env python3
import json

# Define the 10 Worlds
WORLDS = [
    {"num": 1, "name": "Candy Town", "theme": "starter"},
    {"num": 2, "name": "Lemon Lake", "theme": "citrus"},
    {"num": 3, "name": "Chocolate Mountain", "theme": "chocolate"},
    {"num": 4, "name": "Cookie Forest", "theme": "cookie"},
    {"num": 5, "name": "Rainbow Meadow", "theme": "rainbow"},
    {"num": 6, "name": "Cotton Candy Peaks", "theme": "cotton"},
    {"num": 7, "name": "Caramel Canyon", "theme": "caramel"},
    {"num": 8, "name": "Gummy Lagoon", "theme": "gummy"},
    {"num": 9, "name": "Starlight Bakery", "theme": "starlight"},
    {"num": 10, "name": "Sugar Kingdom", "theme": "royal"}
]

# 100 Handcrafted level definitions
LEVEL_NAMES = [
    # World 1: Candy Town (1-10)
    "Sugar Starter", "Jelly Jubilee", "Candy Harvest", "Strawberry Glaze", "Town Jellies",
    "Apple Orchard", "Sugar Plaza", "Jelly Avenue", "Confection Cross", "The Great Sugar Gate",
    # World 2: Lemon Lake (11-20)
    "Citrus Shore", "Frosting Bay", "Lemon Sparkle", "Sunny Rapids", "Zesty Reef",
    "Citron Splash", "Lemonade Fall", "Frosting Pier", "Tangerine Tide", "Citrus Citadel",
    # World 3: Chocolate Mountain (21-30)
    "Cocoa Trail", "Dark Truffle Slope", "Mountain Berries", "Choco Avalanche", "Fudge Ridge",
    "Grape Crag", "Jelly Canyon", "Ganache Cavern", "Cocoa Blast", "Cocoa Caldera",
    # World 4: Cookie Forest (31-40)
    "Gingerbread Path", "Crunchy Thicket", "Forest Harvest", "Waffle Grove", "Shortbread Meadow",
    "Cookie Canopy", "Sugar Bark Trail", "Macaron Glade", "Crispy Hollow", "Ancient Cookie Tree",
    # World 5: Rainbow Meadow (41-50)
    "Prism Blossom", "Color Cascade", "Rainbow Swirl", "Spectrum Valley", "Chroma Frost",
    "Iris Garden", "Rainbow Brook", "Kaleidoscope", "Prism Storm", "Prism Palace",
    # World 6: Cotton Candy Peaks (51-60)
    "Fluffy Foothills", "Sugar Mist", "Pink Spun Cloud", "Breeze Crest", "Cotton Loft",
    "Zephyr Glaze", "Altitude Harvest", "Cumulus Frost", "Cotton Whirlwind", "Cotton Cloud Spire",
    # World 7: Caramel Canyon (61-70)
    "Toffee Gorge", "Caramel Crevasse", "Butterscotch Bluff", "Sticky Chasm", "Amber Rapids",
    "Toffee Terraces", "Golden Caramel", "Molasses Ravine", "Caramel Cascade", "Caramel Falls",
    # World 8: Gummy Lagoon (71-80)
    "Gummy Shore", "Gelatin Bay", "Chewy Coral", "Gummy Tidepool", "Bouncy Reef",
    "Jellyfish Cove", "Berry Gummy Trench", "Gumdrop Atoll", "Taffy Whirlpool", "Gummy Kraken",
    # World 9: Starlight Bakery (81-90)
    "Cosmic Dough", "Nebula Frosting", "Stardust Sprinkles", "Supernova Glaze", "Astral Scone",
    "Galaxy Cupcake", "Meteor Crumb", "Constellation Tart", "Orbit Soufflé", "Celestial Oven",
    # World 10: Sugar Kingdom (91-100)
    "Royal Courtyard", "Imperial Icing", "King's Harvest", "Palace Tapestry", "Crown Jewels",
    "Throne of Sugar", "Château Bonbon", "Royal Confectionery", "Sovereign Cascade", "The Grand Sugar Crown Finale"
]

LAYOUT_TYPES = ['default', 'checker', 'center-cross', 'border', 'donut', 'pillars', 'four-corners', 'diamonds']

def generate_level(lvl):
    idx = lvl - 1
    w_num = ((lvl - 1) // 10) + 1
    world_info = WORLDS[w_num - 1]
    name = LEVEL_NAMES[idx]
    is_milestone = (lvl % 10 == 0)
    sub = (lvl - 1) % 10  # 0 to 9 within world

    # Base base target score escalates with level
    base_score = 1500 + int(lvl * 380) + int((lvl ** 1.35) * 50)
    # Round to clean 100
    base_score = (base_score // 100) * 100

    moves = 20 + min(12, int(lvl * 0.12))
    if is_milestone:
        moves += 3

    layout = LAYOUT_TYPES[(lvl * 3 + sub * 2) % len(LAYOUT_TYPES)]

    # Determine goal type based on progression
    if is_milestone:
        goal_type = 'hybrid'
    elif sub in [0, 6]:
        goal_type = 'score'
    elif sub in [1, 4, 7]:
        goal_type = 'jelly' if w_num % 2 == 1 else 'frosting'
    elif sub in [2, 5]:
        goal_type = 'collect'
    elif sub in [3]:
        goal_type = 'frosting' if w_num % 2 == 1 else 'jelly'
    else:  # sub == 8
        goal_type = 'specials'

    level_obj = {
        "level": lvl,
        "world": world_info["name"],
        "worldNum": w_num,
        "name": name,
        "moves": moves,
        "goalType": goal_type,
        "layout": layout,
        "isMilestone": is_milestone
    }

    star1 = base_score
    star2 = int(base_score * 1.5 // 100 * 100)
    star3 = int(base_score * 2.2 // 100 * 100)
    level_obj["starScores"] = [star1, star2, star3]
    level_obj["targetScore"] = base_score

    if goal_type == 'score':
        level_obj["description"] = f"Match candies and score {base_score:,} points!"
    elif goal_type == 'jelly':
        count = min(28, 10 + int(lvl * 0.18))
        level_obj["targetJellies"] = count
        level_obj["description"] = f"Clear {count} sweet jellies across the board!"
    elif goal_type == 'frosting':
        count = min(26, 10 + int(lvl * 0.16))
        level_obj["targetFrosting"] = count
        level_obj["description"] = f"Crack {count} frosting blockers with adjacent matches!"
    elif goal_type == 'collect':
        colors = ['red', 'orange', 'yellow', 'green', 'blue', 'purple']
        c1 = colors[(lvl * 2) % 6]
        c2 = colors[(lvl * 2 + 3) % 6]
        needed = min(32, 12 + int(lvl * 0.2))
        level_obj["collectCandies"] = {c1: needed, c2: needed}
        c1_name = {"red": "Strawberries", "orange": "Oranges", "yellow": "Lemons", "green": "Apples", "blue": "Blueberries", "purple": "Grapes"}[c1]
        c2_name = {"red": "Strawberries", "orange": "Oranges", "yellow": "Lemons", "green": "Apples", "blue": "Blueberries", "purple": "Grapes"}[c2]
        level_obj["description"] = f"Collect {needed} {c1_name} and {needed} {c2_name}!"
    elif goal_type == 'specials':
        specials_needed = min(10, 3 + int(lvl * 0.08))
        level_obj["targetSpecials"] = specials_needed
        level_obj["description"] = f"Craft {specials_needed} Special Candies (Striped, Bomb or Rainbow)!"
    elif goal_type == 'hybrid':
        j_count = min(28, 12 + int(lvl * 0.16))
        f_count = min(24, 10 + int(lvl * 0.14))
        level_obj["targetJellies"] = j_count
        level_obj["targetFrosting"] = f_count
        if lvl == 100:
            level_obj["description"] = f"Clear {j_count} jellies & {f_count} frosting, and score {base_score:,} pts in the Grand Finale!"
        else:
            level_obj["description"] = f"Clear {j_count} jellies and break {f_count} frosting blocks!"

    return level_obj

all_levels = [generate_level(i) for i in range(1, 101)]

print(f"Generated {len(all_levels)} levels successfully.")
with open('/tmp/levels_100.json', 'w') as f:
    json.dump(all_levels, f, indent=2)
