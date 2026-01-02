package com.calmburst.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.calmburst.MainActivity
import com.calmburst.R
import com.calmburst.data.PreferencesManager
import com.calmburst.data.QuoteRepository
import com.calmburst.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

/**
 * Home fragment displaying the current motivational quote.
 *
 * This fragment serves as the main landing screen for the Calm Burst app.
 * It displays a motivational quote including:
 * - Quote text (large, readable serif font)
 * - Author attribution
 * - Year of origin
 * - Contextual information about the quote
 *
 * Features:
 * - Material Design card layout with elevation
 * - Earthy color palette for a calming visual experience
 * - Full accessibility support with content descriptions
 * - Large touch targets (minimum 48dp) for easy interaction
 * - Loads a random quote on first open
 * - Button to load a new random quote
 * - Navigation button to settings screen
 *
 * @see com.calmburst.data.PreferencesManager
 * @see com.calmburst.data.QuoteRepository
 * @see SettingsFragment
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var quoteRepository: QuoteRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesManager = PreferencesManager(requireContext())
        quoteRepository = QuoteRepository(requireContext())

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        // Load quote every time fragment becomes visible (including after returning from Settings)
        loadQuote()
    }

    /**
     * Sets up click listeners for interactive elements.
     */
    private fun setupClickListeners() {
        binding.settingsButton.setOnClickListener {
            navigateToSettings()
        }

        binding.newQuoteButton.setOnClickListener {
            loadNewRandomQuote()
        }
    }

    /**
     * Loads the current quote from preferences, or a random one if none exists.
     */
    private fun loadQuote() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Check if we have a saved quote
                val savedQuoteText = preferencesManager.lastQuoteText.first()

                if (savedQuoteText.isNullOrEmpty()) {
                    // No saved quote - load a random one
                    loadNewRandomQuote()
                } else {
                    // Load saved quote data
                    val author = preferencesManager.lastQuoteAuthor.first()
                    val year = preferencesManager.lastQuoteYear.first()
                    val context = preferencesManager.lastQuoteContext.first()

                    // Update UI (check binding is still valid)
                    _binding?.let { b ->
                        displayQuote(savedQuoteText, author, year, context)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeFragment", "Error loading quote", e)
                // Try to load a new quote on error
                loadNewRandomQuote()
            }
        }
    }

    /**
     * Loads a new random quote from the repository and saves it.
     */
    private fun loadNewRandomQuote() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Get a random quote from the repository
                val quote = quoteRepository.getRandomQuote()

                // Save it to preferences
                preferencesManager.saveLastQuote(quote)

                // Update UI (check binding is still valid)
                _binding?.let {
                    displayQuote(quote.text, quote.author, quote.year, quote.context)
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeFragment", "Error loading new quote", e)
            }
        }
    }

    /**
     * Displays a quote in the UI.
     */
    private fun displayQuote(text: String, author: String?, year: String?, context: String?) {
        val b = _binding ?: return

        b.quoteText.text = text
        b.quoteCard.visibility = View.VISIBLE
        b.emptyStateText.visibility = View.GONE

        // Author
        if (!author.isNullOrEmpty()) {
            b.authorText.text = getString(R.string.quote_author, author)
            b.authorText.visibility = View.VISIBLE
        } else {
            b.authorText.visibility = View.GONE
        }

        // Year
        if (!year.isNullOrEmpty()) {
            b.yearText.text = year
            b.yearText.visibility = View.VISIBLE
        } else {
            b.yearText.visibility = View.GONE
        }

        // Context
        if (!context.isNullOrEmpty()) {
            b.contextText.text = context
            b.contextText.visibility = View.VISIBLE
        } else {
            b.contextText.visibility = View.GONE
        }
    }

    /**
     * Navigates to the SettingsFragment.
     */
    private fun navigateToSettings() {
        (activity as? MainActivity)?.showSettingsFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
