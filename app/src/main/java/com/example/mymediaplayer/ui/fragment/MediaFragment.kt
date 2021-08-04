package com.example.mymediaplayer.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymediaplayer.R
import com.example.mymediaplayer.databinding.FragmentMediaBinding
import com.example.mymediaplayer.ui.navigation.IMediaNavigation
import com.example.mymediaplayer.ui.recycler.LineDivider
import com.example.mymediaplayer.ui.recycler.MediaItemAdapter
import com.example.mymediaplayer.ui.recycler.MediaItemHolder
import com.example.mymediaplayer.util.UiState
import com.example.mymediaplayer.util.extensions.getFragmentComponent
import com.example.mymediaplayer.viewmodel.MediaViewModel
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaFragment: Fragment() {

    @Inject
    lateinit var mediaViewModel: MediaViewModel
    @Inject
    lateinit var mediaNavigation: IMediaNavigation

    private lateinit var binding: FragmentMediaBinding
    private lateinit var adapterRecyclerView: MediaItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMediaBinding.inflate(layoutInflater)
        getFragmentComponent().inject(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        recyclerDataCollector()
    }

    private fun initRecyclerView() {
        adapterRecyclerView = object : MediaItemAdapter() {
            override fun onClickItem(holder: MediaItemHolder, position: Int) {
                holder.itemView.setOnClickListener {
                    getItem(position).apply {
                        mediaNavigation.openMedia(mediaMetadata.title.toString(),
                                                  playbackProperties!!.uri)
                    }
                }
            }
        }

        binding.recyclerView.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterRecyclerView
            addItemDecoration(LineDivider(requireContext(), R.drawable.recycler_line_divider))
        }
    }

    private fun recyclerDataCollector() {
       viewLifecycleOwner.lifecycleScope.launch {
           mediaViewModel.recyclerState.collect {
               when(it) {
                   is UiState.Success<ArrayList<MediaItem>> -> adapterRecyclerView
                       .updateItem(it.resources)

                   is UiState.Error -> Toast
                       .makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                       .show()
               }
           }
       }
    }
}