package com.example.ui_kit.components.product

import androidx.compose.ui.graphics.Color

enum class ShopifyColor(val stringValue: String, val color: Color) {
    BLACK("black", Color(0xFF121212)),
    WHITE("white", Color(0xFFFFFFFF)),
    RED("red", Color(0xFFF44336)),
    BLUE("blue", Color(0xFF2196F3)),
    NAVY("navy", Color(0xFF1A237E)),
    GREEN("green", Color(0xFF4CAF50)),
    YELLOW("yellow", Color(0xFFFFEB3B)),
    ORANGE("orange", Color(0xFFFF9800)),
    PINK("pink", Color(0xFFE91E63)),
    PURPLE("purple", Color(0xFF9C27B0)),
    BROWN("brown", Color(0xFF795548)),
    GREY("grey", Color(0xFF9E9E9E)),
    GRAY("gray", Color(0xFF9E9E9E)),

    BEIGE("beige", Color(0xFFF5F5DC)),
    CREAM("cream", Color(0xFFFFFDD0)),
    IVORY("ivory", Color(0xFFFFFFF0)),
    KHAKI("khaki", Color(0xFFC3B091)),
    OLIVE("olive", Color(0xFF808000)),
    TAUPE("taupe", Color(0xFF483C32)),
    CAMEL("camel", Color(0xFFC19A6B)),
    MUSTARD("mustard", Color(0xFFFFDB58)),

    LIGHT_BLUE("lightblue", Color(0xFFADD8E6)),
    LIGHT_GREEN("lightgreen", Color(0xFF90EE90)),
    LIGHT_GREY("lightgrey", Color(0xFFD3D3D3)),
    LIGHT_GRAY("lightgray", Color(0xFFD3D3D3)),
    LIGHT_PINK("lightpink", Color(0xFFFFB6C1)),
    MINT("mint", Color(0xFF98FF98)),
    PEACH("peach", Color(0xFFFFE5B4)),
    LAVENDER("lavender", Color(0xFFE6E6FA)),
    CORAL("coral", Color(0xFFFF7F50)),

    MAROON("maroon", Color(0xFF800000)),
    BURGUNDY("burgundy", Color(0xFF800020)),
    TEAL("teal", Color(0xFF008080)),
    CHARCOAL("charcoal", Color(0xFF36454F)),

    GOLD("gold", Color(0xFFFFD700)),
    SILVER("silver", Color(0xFFC0C0C0)),
    BRONZE("bronze", Color(0xFFCD7F32)),
    ROSE_GOLD("rosegold", Color(0xFFB76E79));

    companion object {
        fun fromString(name: String): Color? {
            val normalizedInput = name.replace(" ", "")
                .replace("-", "")
                .replace("_", "")
                .trim()

            return entries.find { it.stringValue.equals(normalizedInput, ignoreCase = true) }?.color
        }
    }
}