package com.sungbin.gitkakaobot.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sungbin.androidutils.extensions.*
import com.sungbin.androidutils.util.Util
import com.sungbin.gitkakaobot.R
import com.sungbin.gitkakaobot.adapter.BotAdapter
import com.sungbin.gitkakaobot.databinding.FragmentBotBinding
import com.sungbin.gitkakaobot.model.Bot
import com.sungbin.gitkakaobot.model.BotType
import com.sungbin.gitkakaobot.ui.activity.DashboardActivity.Companion.botList
import com.sungbin.gitkakaobot.ui.activity.DashboardActivity.Companion.initBotList
import com.sungbin.gitkakaobot.util.BotUtil
import com.sungbin.gitkakaobot.util.UiUtil

class BotFragment : Fragment() {

    private val binding by lazy { FragmentBotBinding.inflate(layoutInflater) }
    private lateinit var adapter: BotAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initBotList()
        statusViewInit()

        adapter = BotAdapter(botList.value ?: arrayListOf(), requireActivity())
        binding.rvBot.adapter = adapter
        binding.rvBot.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = true
            setFab(binding.efabAdd)
        }
        ItemTouchHelper(dragCallback).attachToRecyclerView(binding.rvBot)

        binding.efabAdd.setOnClickListener {
            binding.tslContainer.startTransform()
        }

        binding.btnAdd.setOnClickListener {
            if (binding.tietBotName.text.toString().isBlank()) {
                UiUtil.snackbar(it, getString(R.string.bot_please_input_script_name))
            } else {
                val botType =
                    if (binding.mbtgContainer.checkedButtonId == R.id.btn_javascript) BotType.JS else BotType.SIMPLE
                val bot = Bot(
                    name = binding.tietBotName.text.toString(),
                    isCompiled = false,
                    power = false,
                    type = botType,
                    optimization = -1,
                    lastRunTime = "없음",
                    index = BotUtil.getLastIndex(botList.value) + 1,
                    uuid = Util.makeRandomUUID()
                )
                botList.run {
                    value!!.add(bot)
                    postValue(value)
                }
                BotUtil.createNewBot(bot)
                binding.mbtgContainer.check(R.id.btn_javascript)
                binding.tietBotName.clear()
                binding.tslContainer.finishTransform()
                UiUtil.snackbar(it, getString(R.string.bot_added_new_bot))
                binding.tietBotName.hideKeyboard()
            }
        }

        botList.observe(viewLifecycleOwner) {
            adapter = BotAdapter(it, requireActivity())
            binding.rvBot.adapter = adapter
            statusViewInit()
        }
    }

    private fun statusViewInit() {
        if (botList.value.isNullOrEmpty()) {
            binding.fblEmptyFile.show()
            binding.lavEmpty.playAnimation()
            binding.rvBot.hide(true)
        } else {
            binding.fblEmptyFile.hide(true)
            binding.lavEmpty.cancelAnimation()
            binding.rvBot.show()
        }
    }

    private val dragCallback = object : ItemTouchHelper.Callback() {
        var dragFrom = -1
        var dragTo = -1
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                0
            )
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            if (dragFrom == -1) {
                dragFrom = fromPosition
            }
            dragTo = toPosition
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit
        override fun isLongPressDragEnabled() = true
        override fun isItemViewSwipeEnabled() = false
        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                moveItem(dragFrom, dragTo)
            }
            dragTo = -1
            dragFrom = -1
        }

        private fun moveItem(oldPos: Int, newPos: Int) {
            val oldItem = botList.value!![oldPos]
            val newItem = botList.value!![newPos]
            BotUtil.changeBotIndex(oldItem, newPos)
            BotUtil.changeBotIndex(newItem, oldPos)
            botList.value!!.run {
                removeAt(oldPos)
                add(oldPos, newItem)
                removeAt(newPos)
                add(newPos, oldItem)
            }
            binding.rvBot.post {
                adapter.run {
                    notifyItemChanged(oldPos)
                    notifyItemChanged(newPos)
                }
            }
        }
    }

}