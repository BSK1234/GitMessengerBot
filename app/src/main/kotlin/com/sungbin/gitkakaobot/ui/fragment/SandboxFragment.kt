package com.sungbin.gitkakaobot.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sungbin.gitkakaobot.databinding.FragmentTestBinding


/**
 * Created by SungBin on 2020-12-17.
 */

class SandboxFragment : BaseFragment() {

    private val binding by lazy { FragmentTestBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = binding.root

}