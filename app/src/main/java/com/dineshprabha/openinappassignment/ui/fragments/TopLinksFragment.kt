package com.dineshprabha.openinappassignment.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dineshprabha.openinappassignment.adapter.TopLinkAdapter
import com.dineshprabha.openinappassignment.databinding.FragmentTopLinksBinding
import com.dineshprabha.openinappassignment.models.UserClickResponse
import com.dineshprabha.openinappassignment.utils.Constants
import com.dineshprabha.openinappassignment.utils.NetworkResult
import com.dineshprabha.openinappassignment.utils.TokenManager
import com.dineshprabha.openinappassignment.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TopLinksFragment : Fragment() {

    private lateinit var binding: FragmentTopLinksBinding
    private val viewModel by viewModels<MainViewModel>()
    private val linkAdapter by lazy { TopLinkAdapter() }

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentTopLinksBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tokenManager.saveToken(Constants.BEARER_TOKEN)
        viewModel.fetchData()
        setupTopLinkRV()
        setupObserver()
    }

    private fun setupTopLinkRV() {
        binding.rvToplinks.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = linkAdapter
        }
    }

    private fun setupObserver() {
        viewModel.userClickLiveData.observe(viewLifecycleOwner, Observer {
            when(it){
                is NetworkResult.Success -> {
                    if(it.data != null){
                        val userClickResponse :UserClickResponse = it.data
                        linkAdapter.differ.submitList(userClickResponse.data.top_links)
                    }
                }
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                is NetworkResult.Loading -> {

                }
                else -> Unit
            }
        })

    }


}