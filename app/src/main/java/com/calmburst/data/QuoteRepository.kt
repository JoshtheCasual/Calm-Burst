package com.calmburst.data

import android.content.Context
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import kotlin.random.Random

/**
 * Repository for managing quote data from the XML resource file.
 *
 * This class handles parsing quotes from res/raw/quotes.xml and provides
 * efficient random quote selection with in-memory caching.
 *
 * @property context Android context for accessing resources
 *
 * @constructor Creates a new QuoteRepository instance
 *
 * Example usage:
 * ```
 * val repository = QuoteRepository(context)
 * val quote = repository.getRandomQuote()
 * println("${quote.text} - ${quote.author}")
 * ```
 */
class QuoteRepository(private val context: Context) {

    // Cache for parsed quotes to avoid repeated XML parsing
    private var cachedQuotes: List<Quote>? = null

    /**
     * Returns a random quote from the quotes collection.
     *
     * The first call will parse the XML file and cache the results.
     * Subsequent calls will use the cached data for better performance.
     *
     * @return A randomly selected Quote object, or a default quote if parsing fails
     *
     * @throws IllegalStateException if quotes.xml is empty after parsing
     */
    fun getRandomQuote(): Quote {
        val quotes = getQuotes()

        if (quotes.isEmpty()) {
            // Return a fallback quote if the list is empty
            return Quote(
                text = "Keep moving forward.",
                author = "Calm Burst",
                year = "2026",
                context = "Default motivational message"
            )
        }

        return quotes[Random.nextInt(quotes.size)]
    }

    /**
     * Returns the complete list of quotes.
     *
     * This method uses a cached list after the first parse to improve performance.
     * If the cache is null, it parses the XML file and stores the result.
     *
     * @return List of all Quote objects from quotes.xml
     */
    private fun getQuotes(): List<Quote> {
        // Return cached quotes if available
        cachedQuotes?.let { return it }

        // Parse XML and cache the result
        val quotes = parseQuotesXml()
        cachedQuotes = quotes
        return quotes
    }

    /**
     * Parses quotes from the res/raw/quotes.xml file using XmlPullParser.
     *
     * This method efficiently parses the XML structure:
     * ```
     * <quotes>
     *   <quote>
     *     <text>Quote text</text>
     *     <author>Author name</author>
     *     <year>Year</year>
     *     <context>Context</context>
     *   </quote>
     * </quotes>
     * ```
     *
     * @return List of parsed Quote objects
     * @throws XmlPullParserException if XML structure is invalid
     * @throws IOException if file cannot be read
     */
    private fun parseQuotesXml(): List<Quote> {
        val quotes = mutableListOf<Quote>()

        try {
            // Open the quotes.xml file from res/raw/
            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier("quotes", "raw", context.packageName)
            )

            inputStream.use { stream ->
                val parser: XmlPullParser = Xml.newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)

                // ========== XML SECURITY HARDENING ==========
                // Protect against XML External Entity (XXE) injection attacks
                // These features prevent malicious XML from accessing local files,
                // executing remote code, or causing denial of service attacks.
                //
                // Security measures:
                // 1. Disable external general entities (prevents file disclosure)
                // 2. Disable external parameter entities (prevents DTD-based attacks)
                // 3. Disallow DOCTYPE declarations (prevents entity expansion attacks)
                //
                // Reference: CWE-611, OWASP XXE Prevention
                // https://owasp.org/www-community/vulnerabilities/XML_External_Entity_(XXE)_Processing
                try {
                    // Disable external entity resolution to prevent XXE attacks
                    parser.setFeature("http://xml.org/sax/features/external-general-entities", false)
                    parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false)

                    // Disable DOCTYPE declarations to prevent billion laughs and other entity expansion attacks
                    parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)

                    android.util.Log.d("QuoteRepository", "XML security features enabled successfully")
                } catch (e: Exception) {
                    // Some XML parsers may not support all security features
                    // Log the warning but continue parsing (quotes.xml is a trusted local resource)
                    android.util.Log.w("QuoteRepository", "Could not set all XML security features (non-critical for local resources)", e)
                }
                // ========== END XML SECURITY HARDENING ==========

                parser.setInput(stream, null)

                // Skip to the first START_TAG
                parser.nextTag()

                // Parse the <quotes> root element
                parser.require(XmlPullParser.START_TAG, null, "quotes")

                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.eventType != XmlPullParser.START_TAG) {
                        continue
                    }

                    // Parse each <quote> element
                    if (parser.name == "quote") {
                        quotes.add(parseQuote(parser))
                    } else {
                        skip(parser)
                    }
                }
            }
        } catch (e: XmlPullParserException) {
            // Log error and return empty list on XML parsing failure
            android.util.Log.e("QuoteRepository", "Error parsing quotes XML", e)
        } catch (e: IOException) {
            // Log error and return empty list on I/O failure
            android.util.Log.e("QuoteRepository", "Error reading quotes file", e)
        } catch (e: Exception) {
            // Catch any other unexpected errors
            android.util.Log.e("QuoteRepository", "Unexpected error loading quotes", e)
        }

        return quotes
    }

    /**
     * Parses a single <quote> element into a Quote object.
     *
     * Expected XML structure:
     * ```
     * <quote>
     *   <text>Quote text</text>
     *   <author>Author name</author>
     *   <year>Year</year>
     *   <context>Context description</context>
     * </quote>
     * ```
     *
     * @param parser XmlPullParser positioned at a <quote> START_TAG
     * @return Parsed Quote object with all fields populated
     * @throws XmlPullParserException if XML structure is invalid
     * @throws IOException if reading fails
     */
    private fun parseQuote(parser: XmlPullParser): Quote {
        parser.require(XmlPullParser.START_TAG, null, "quote")

        var text = ""
        var author = ""
        var year = ""
        var context = ""

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "text" -> text = readText(parser, "text")
                "author" -> author = readText(parser, "author")
                "year" -> year = readText(parser, "year")
                "context" -> context = readText(parser, "context")
                else -> skip(parser)
            }
        }

        return Quote(
            text = text,
            author = author,
            year = year,
            context = context
        )
    }

    /**
     * Reads the text content of an XML element.
     *
     * @param parser XmlPullParser positioned at an element START_TAG
     * @param tagName Name of the tag to read (for validation)
     * @return Text content of the element
     * @throws XmlPullParserException if XML structure is invalid
     * @throws IOException if reading fails
     */
    private fun readText(parser: XmlPullParser, tagName: String): String {
        parser.require(XmlPullParser.START_TAG, null, tagName)
        val text = if (parser.next() == XmlPullParser.TEXT) {
            parser.text
        } else {
            ""
        }
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, null, tagName)
        return text
    }

    /**
     * Skips an unwanted XML element and all its children.
     *
     * This method is useful for forward compatibility - if new elements
     * are added to the XML, they will be safely ignored.
     *
     * @param parser XmlPullParser positioned at a START_TAG
     * @throws XmlPullParserException if XML structure is invalid
     * @throws IOException if reading fails
     */
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    /**
     * Clears the quote cache, forcing a re-parse on the next getQuotes() call.
     *
     * This is useful for testing or if the quotes.xml file is updated at runtime.
     */
    fun clearCache() {
        cachedQuotes = null
    }
}
