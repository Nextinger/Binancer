package com.nextinger.binancer.utils.backdrop

import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar

/**
 * @author Semper-Viventem
 * @see https://github.com/Semper-Viventem/Material-backdrop/blob/master/backdrop/src/main/kotlin/ru/semper_viventem/backdrop/BackdropUtils.kt
 */
internal class BackdropUtils {

    fun findToolbar(viewGroup: ViewGroup): Toolbar? {
        for (chileId in 0..viewGroup.childCount) {
            val childView = viewGroup.getChildAt(chileId)
            if (childView is Toolbar) {
                return childView
            }
        }

        return null
    }
}