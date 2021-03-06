package com.ceslab.team7_ble_meet.dashboard

import androidx.recyclerview.widget.DiffUtil
import com.ceslab.team7_ble_meet.Model.UserAPI

class SpotDiffCallback(
    private val old: List<UserAPI>,
    private val new: List<UserAPI>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].id == new[newPosition].id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}