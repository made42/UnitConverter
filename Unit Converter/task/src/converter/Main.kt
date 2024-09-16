package converter

enum class Type {
    LENGTH, WEIGHT, TEMPERATURE
}

class Unit(val type: Type, val measures: List<String>, private val rate: Double) {
    fun convert(value: Double, target: Unit): Double {
        if (type == Type.TEMPERATURE) {
            return when {
                measures.contains("celsius") -> {
                    when {
                        target.measures.contains("fahrenheit") -> (value * (9/5.toDouble())) + 32.0
                        target.measures.contains("kelvin") -> value + 273.15
                        else -> value
                    }
                }
                measures.contains("fahrenheit") -> {
                    when {
                        target.measures.contains("celsius") -> (value - 32) * 5/9.toDouble()
                        target.measures.contains("kelvin") -> (value + 459.67) * 5/9.toDouble()
                        else -> value
                    }
                }
                measures.contains("kelvin") -> {
                    when {
                        target.measures.contains("celsius") -> value - 273.15
                        target.measures.contains("fahrenheit") -> value * 9/5.toDouble() - 459.67
                        else -> value
                    }
                }
                else -> value
            }
        }
        return value * rate / target.rate
    }
}

val units = listOf(
    Unit(Type.LENGTH, listOf("m", "meter", "meters"), 1.0),
    Unit(Type.LENGTH, listOf("km", "kilometer", "kilometers"), 1000.0),
    Unit(Type.LENGTH, listOf("cm", "centimeter", "centimeters"), 0.01),
    Unit(Type.LENGTH, listOf("mm", "millimeter", "millimeters"), 0.001),
    Unit(Type.LENGTH, listOf("mi", "mile", "miles"), 1609.35),
    Unit(Type.LENGTH, listOf("yd", "yard", "yards"), 0.9144),
    Unit(Type.LENGTH, listOf("ft", "foot", "feet"), 0.3048),
    Unit(Type.LENGTH, listOf("in", "inch", "inches"), 0.0254),
    Unit(Type.WEIGHT, listOf("g", "gram", "grams"), 1.0),
    Unit(Type.WEIGHT, listOf("kg", "kilogram", "kilograms"), 1000.0),
    Unit(Type.WEIGHT, listOf("mg", "milligram", "milligrams"), 0.001),
    Unit(Type.WEIGHT, listOf("lb", "pound", "pounds"), 453.592),
    Unit(Type.WEIGHT, listOf("oz", "ounce", "ounces"), 28.3495),
    Unit(Type.TEMPERATURE, listOf("c", "degree Celsius", "degrees Celsius", "celsius", "dc"), 1.0),
    Unit(Type.TEMPERATURE, listOf("f", "degree Fahrenheit", "degrees Fahrenheit", "fahrenheit", "df"), 1.0),
    Unit(Type.TEMPERATURE, listOf("k", "kelvin", "kelvins"), 1.0),
)

fun main() {
    while (true) {
        print("Enter what you want to convert (or exit): ")
        val input = readln().split(" ").map { it.lowercase() }.toMutableList()
        if (input.first() == "exit") return
        val value = input.removeFirst().toDoubleOrNull()
        if (value == null) { println("Parse error\n"); continue }
        val source = when (val token = input.removeFirst()) {
            in setOf("degree", "degrees") -> "$token ${input.removeFirst()}"
            else -> token
        }
        input.removeFirst()
        val target = when (val token = input.removeFirst()) {
            in setOf("degree", "degrees") -> "$token ${input.removeFirst()}"
            else -> token
        }
        val sourceUnit = units.firstOrNull { source in it.measures.map { it.lowercase() } }
        val targetUnit = units.firstOrNull { target in it.measures.map { it.lowercase() } }
        if (sourceUnit == null || targetUnit == null || sourceUnit.type != targetUnit.type) {
            println("Conversion from ${if (sourceUnit == null) "???" else sourceUnit.measures[2]} to ${if (targetUnit == null) "???" else targetUnit.measures[2]} is impossible")
            continue
        }
        if (value < 0 && sourceUnit.type in setOf(Type.LENGTH, Type.WEIGHT)) {
            println("${sourceUnit.type.name} shouldn't be negative\n")
            continue
        }
        val converted = sourceUnit.convert(value, targetUnit)
        println("$value ${sourceUnit.measures[if (value == 1.0) 1 else 2]} is $converted ${targetUnit.measures[if (converted == 1.0) 1 else 2]}\n")
    }
}