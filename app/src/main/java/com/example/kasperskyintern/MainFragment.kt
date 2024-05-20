package com.example.kasperskyintern

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.kasperskyintern.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = _binding!!
    private val viewModel: MainFragmentViewModel by viewModels()
    private var keyboardUp = MutableStateFlow(false)
    private val adapter by lazy { TranslationAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.recyclerView.adapter = adapter

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        lifecycleScope.launch {
            viewModel.translation.collect {
                fetchTranslation(it)
            }
        }
        lifecycleScope.launch {
            keyboardUp.collect{
                if (!it)
                    textChanged()
            }
        }
        lifecycleScope.launch {
            viewModel
                .translationHistory
                .flowOn(Dispatchers.IO)
                .collect {
                    adapter.translations = it.reversed().toMutableList()
                }
        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            binding.root.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.root.rootView.height

            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // keyboard is opened
                if(!keyboardUp.value)
                    keyboardUp.value = true
            } else {
                // keyboard is closed
                if(keyboardUp.value)
                    keyboardUp.value = false
            }
        }

        return binding.root
    }

    private fun textChanged() {
        binding.textInputEditText.clearFocus()
        viewModel.textChanged()
    }

    private fun fetchTranslation(text: String) {
        if(text != "") {
            binding.cardView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.textInputLayout.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.translatedTextLayout.visibility = View.VISIBLE
            binding.translatedText.text = text
        }
        else {
            binding.cardView.layoutParams.height = resources.getDimension(R.dimen.dimen_400dp).toInt()
            binding.textInputLayout.layoutParams.height = 0
            binding.translatedTextLayout.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}