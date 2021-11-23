package com.nextinger.binancer.data.objects.filter

import com.nextinger.binancer.data.objects.enums.DisplayCurrency
import com.nextinger.binancer.data.objects.enums.SortBy

data class TickerFilter(var displayCurrency: DisplayCurrency,
                        var sortBy: SortBy,
                        var tickerRefreshDelay: Long)
