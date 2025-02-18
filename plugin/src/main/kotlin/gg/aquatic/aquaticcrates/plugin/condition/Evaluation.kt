package gg.aquatic.aquaticcrates.plugin.condition

object Evaluation {

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

    // Evaluate simple conditions, supporting arithmetic and comparison operations
    fun evaluateSimpleCondition(condition: String): Boolean {
        // First handle arithmetic operations (% + - * /)
        val evaluatedExpression = evaluateMathOperations(condition)

        // Then handle logical comparisons
        val comparatorRegex = Regex("(.*)\\s*(==|!=|<|>|<=|>=)\\s*(.*)")
        val matchResult = comparatorRegex.find(evaluatedExpression) ?: return false

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

    // Evaluates arithmetic expressions (% + - * /)
    fun evaluateMathOperations(expression: String): String {
        // Remove any spaces for simpler parsing
        var mathExpression = expression.replace("\\s+".toRegex(), "")

        // Regex to find and evaluate math operations
        val mathRegex = Regex("(\\d+\\.?\\d*)\\s*([%+\\-*/])\\s*(\\d+\\.?\\d*)")

        while (mathRegex.containsMatchIn(mathExpression)) {
            val matchResult = mathRegex.find(mathExpression) ?: break

            val leftOperand = matchResult.groupValues[1].toDouble()
            val operator = matchResult.groupValues[2]
            val rightOperand = matchResult.groupValues[3].toDouble()

            val result = when (operator) {
                "%" -> leftOperand % rightOperand
                "+" -> leftOperand + rightOperand
                "-" -> leftOperand - rightOperand
                "*" -> leftOperand * rightOperand
                "/" -> if (rightOperand != 0.0) leftOperand / rightOperand else throw IllegalArgumentException("Division by zero.")
                else -> throw IllegalArgumentException("Unsupported operator: $operator")
            }

            // Replace the matched portion with the result in the string
            mathExpression = mathExpression.replace(matchResult.value, result.toString())
        }

        return mathExpression
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