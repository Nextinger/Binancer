package com.nextinger.binancer.data.objects

import com.nextinger.binancer.data.objects.enums.State

data class ContentState(val state: State,
                        val message: String? = null)