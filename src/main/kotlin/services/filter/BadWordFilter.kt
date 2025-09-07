package me.m64diamondstar.services.filter

import org.yaml.snakeyaml.Yaml
import java.io.InputStream

class BadWordFilter {

    private val regexList: List<Regex> by lazy {
        loadRegex("/bad_words.yml")
    }

    private fun loadRegex(resourcePath: String): List<Regex> {
        val yaml = Yaml()
        val stream: InputStream = BadWordFilter::class.java.getResourceAsStream(resourcePath)
            ?: throw IllegalStateException("Bad words config not found: $resourcePath")

        @Suppress("UNCHECKED_CAST")
        val data: Map<String, Any> = yaml.load(stream)

        val rawPatterns = data["regex"] as? List<String> ?: emptyList()

        return rawPatterns.map { pattern ->
            pattern.toRegex()
        }
    }

    fun containsBadWord(text: String): Boolean {
        return regexList.any { it.containsMatchIn(text) }
    }

    fun findMatches(text: String): List<Regex> {
        return regexList.filter { it.containsMatchIn(text) }
    }

}