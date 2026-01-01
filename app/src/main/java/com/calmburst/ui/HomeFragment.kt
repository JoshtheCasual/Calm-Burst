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
import com.calmburst.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

/**
 * Home fragment displaying the last shown motivational quote.
 *
 * This fragment serves as the main landing screen for the Calm Burst app.
 * It displays the most recently shown motivational quote, including:
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
 * - Empty state message when no quote has been shown yet
 * - Navigation button to settings screen
 *
 * The quote data is retrieved from [PreferencesManager] which stores the
 * last notification's quote information.
 *
 * @see com.calmburst.data.PreferencesManager
 * @see SettingsFragment
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferencesManager: PreferencesManager

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

        setupClickListeners()
        loadLastQuote()
    }

    /**
     * Sets up click listeners for interactive elements.
     * Currently handles navigation to SettingsFragment.
     */
    private fun setupClickListeners() {
        binding.settingsButton.setOnClickListener {
            navigateToSettings()
        }
    }

    /**
     * Loads and displays the last shown quote from preferences.
     * If no quote exists, shows an empty state message.
     */
    private fun loadLastQuote() {
        viewLifecycleOwner.lifecycleScope.launch {
            preferencesManager.lastQuoteText.collect { quoteText ->
                if (quoteText.isNullOrEmpty()) {
                    showEmptyState()
                } else {
                    showQuote(quoteText)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            preferencesManager.lastQuoteAuthor.collect { author ->
                binding.authorText.text = getString(R.string.quote_author, author ?: "Unknown")
                binding.authorText.visibility = if (author.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            preferencesManager.lastQuoteYear.collect { year ->
                binding.yearText.text = year ?: ""
                binding.yearText.visibility = if (year.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            preferencesManager.lastQuoteContext.collect { context ->
                binding.contextText.text = context ?: ""
                binding.contextText.visibility = if (context.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    /**
     * Displays the quote text and shows the quote card.
     *
     * @param quoteText The motivational quote text to display
     */
    private fun showQuote(quoteText: String) {
        binding.quoteText.text = quoteText
        binding.quoteCard.visibility = View.VISIBLE
        binding.emptyStateText.visibility = View.GONE
    }

    /**
     * Shows the empty state when no quote has been displayed yet.
     * Hides the quote card and displays a placeholder message.
     */
    private fun showEmptyState() {
        binding.quoteCard.visibility = View.GONE
        binding.emptyStateText.visibility = View.VISIBLE
    }

    /**
     * Navigates to the SettingsFragment.
     * Uses the MainActivity's navigation method to handle the fragment transaction.
     */
    private fun navigateToSettings() {
        (activity as? MainActivity)?.showSettingsFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
