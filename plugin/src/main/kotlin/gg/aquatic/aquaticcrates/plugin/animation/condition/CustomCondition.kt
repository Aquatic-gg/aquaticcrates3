package gg.aquatic.aquaticcrates.plugin.animation.condition

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.requirement.AbstractRequirement

class CustomCondition: AbstractRequirement<Animation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("condition", "", true)
    )

    override fun execute(
        binder: Animation,
        args: Map<String, Any?>,
        textUpdater: (Animation, String) -> String
    ): Boolean {
        val condition = args["condition"] as String
        return evaluateLogicalCondition(textUpdater(binder, condition))
    }

    // Evaluates logical conditions with support for && and ||
    fun evaluateLogicalCondition(condition: String): Boolean {
        // Split by || (lowest precedence)
        val orParts = condition.split("||").map { it.trim() }

        return orParts.any { orPart ->
            // For each OR part, split by && (higher precedence)
            val andParts = orPart.split("&&").map { it.trim() }
            andParts.all { andPart ->
                // Evaluate each individual condition
                evaluateSimpleCondition(andPart)
            }
        }
    }

    // Supports basic comparisons like ==, !=, <, >, <=, >= for numbers AND strings
    fun evaluateSimpleCondition(condition: String): Boolean {
        val comparatorRegex = Regex("(.*)\\s*(==|!=|<|>|<=|>=)\\s*(.*)")
        val matchResult = comparatorRegex.find(condition) ?: return false

        val leftOperand = matchResult.groupValues[1].trim()
        val operator = matchResult.groupValues[2].trim()
        val rightOperand = matchResult.groupValues[3].trim()

        return when {
            leftOperand.toDoubleOrNull() != null && rightOperand.toDoubleOrNull() != null -> {
                // Numeric comparison
                compareNumbers(leftOperand.toDouble(), rightOperand.toDouble(), operator)
            }
            else -> {
                // String comparison
                compareStrings(leftOperand, rightOperand, operator)
            }
        }
    }

    fun compareNumbers(left: Double, right: Double, operator: String): Boolean {
        return when (operator) {
            "==" -> left == right
            "!=" -> left != right
            "<" -> left < right
            ">" -> left > right
            "<=" -> left <= right
            ">=" -> left >= right
            else -> false
        }
    }

    fun compareStrings(left: String, right: String, operator: String): Boolean {
        return when (operator) {
            "==" -> left == right
            "!=" -> left != right
            else -> false // Only equality/inequality are valid for strings
        }
    }
}