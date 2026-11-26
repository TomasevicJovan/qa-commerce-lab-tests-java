package io.github.tomasevicjovan.qacommercelab.data;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.tomasevicjovan.qacommercelab.data.ProductInfo.onDeal;
import static io.github.tomasevicjovan.qacommercelab.data.ProductInfo.product;
import static io.github.tomasevicjovan.qacommercelab.data.ProductInfo.shipsFree;

/**
 * Products the scenarios refer to, from qa-commerce-lab docs/BUSINESS.md (checked against the shop's seed data).
 * Feature files name products the way a customer sees them; {@link #byName} turns that name into the expected data.
 */
public final class Catalog {

    public static final ProductInfo ARC_HEADPHONES = product("p1", "Arc Wireless Headphones", "Audio", 12_900);
    public static final ProductInfo ORBIT_LAMP = product("p2", "Orbit Desk Lamp", "Workspace", 7_900);
    public static final ProductInfo STILL_MUG = product("p5", "Still Ceramic Mug", "Kitchen", 2_400);
    public static final ProductInfo POCKET_SPEAKER = product("p6", "Pocket Mini Speaker", "Audio", 5_900);
    public static final ProductInfo FOLD_ORGANIZER = product("p8", "Fold Travel Organizer", "Travel Accessories", 3_500);
    public static final ProductInfo BREW_KETTLE = onDeal("p10", "Brew Pour-Over Kettle", "Kitchen", 6_900, 9_900);
    public static final ProductInfo PULSE_CONTROLLER = onDeal("p16", "Pulse Wireless Controller", "Gaming", 5_900, 7_900);
    public static final ProductInfo NOVA_CHAIR = onDeal("p19", "Nova Gaming Chair", "Gaming", 24_900, 34_900);
    public static final ProductInfo FLOW_YOGA_MAT = shipsFree("p23", "Flow Yoga Mat", "Fitness", 3_900);
    public static final ProductInfo TRAIL_BOTTLE = shipsFree("p26", "Trail Insulated Bottle", "Outdoor & Camping", 2_900);
    public static final ProductInfo NOTE_JOURNAL = shipsFree("p29", "Note Dotted Journal", "Stationery", 1_800);
    public static final ProductInfo PURE_TOOTHBRUSH = shipsFree("p34", "Pure Bamboo Toothbrush Set", "Personal Care", 1_200);
    public static final ProductInfo SHELL_PHONE_CASE = shipsFree("p40", "Shell Phone Case", "Phones & Tablets", 1_900);
    public static final ProductInfo JUMP_ROPE = shipsFree("p62", "Jump Speed Rope", "Fitness", 1_400);
    public static final ProductInfo POCKET_NOTEBOOK = product("p87", "Pocket Notebook 3-pack", "Stationery", 1_995);
    public static final ProductInfo TWIST_GRIP = product("p88", "Twist Phone Grip", "Phones & Tablets", 1_295);

    private static final Map<String, ProductInfo> BY_NAME = List.of(
                    ARC_HEADPHONES, ORBIT_LAMP, STILL_MUG, POCKET_SPEAKER, FOLD_ORGANIZER, BREW_KETTLE, PULSE_CONTROLLER, NOVA_CHAIR,
                    FLOW_YOGA_MAT, TRAIL_BOTTLE, NOTE_JOURNAL, PURE_TOOTHBRUSH, SHELL_PHONE_CASE, JUMP_ROPE, POCKET_NOTEBOOK, TWIST_GRIP)
            .stream().collect(Collectors.toUnmodifiableMap(ProductInfo::name, Function.identity()));

    private Catalog() {
    }

    public static ProductInfo byName(String name) {
        var product = BY_NAME.get(name);
        if (product == null) {
            throw new IllegalArgumentException("'%s' is not in data/Catalog.java. Add it there with its expected price.".formatted(name));
        }
        return product;
    }
}
