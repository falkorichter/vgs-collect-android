package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.DefaultCardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DankortTest {

    private lateinit var filter: VGSCardFilter

    @Before
    fun setupFilter() {
        filter = DefaultCardBrandFilter(CardType.values())
    }

    @Test
    fun test_1() {
        val brand = filter.detect("5019")
        Assert.assertEquals(brand?.name, CardType.DANKORT.name)
    }
}