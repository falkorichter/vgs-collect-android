package com.verygoodsecurity.vgscollect.view.card.number

import android.app.Activity
import android.os.Build
import android.text.InputType
import android.view.Gravity
import android.view.View
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.view.internal.CardInputField
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class VGSCardNumberEditTextTest {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var view: VGSCardNumberEditText


    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        view = VGSCardNumberEditText(activity)
    }

    @Test
    fun test_view() {
        view.onAttachedToWindow()
        val internal = view.getView()
        assertNotNull(internal)
    }

    @Test
    fun test_check_internal_view() {
        val internal = view.getView()
        assertNotNull(internal)

        val child = view.getView()
        Assert.assertTrue(child is CardInputField)
    }

    @Test
    fun test_attach_view() {
        view.onAttachedToWindow()

        assertEquals(1, view.childCount)
    }


    @Test
    fun test_field_type() {
        val type = view.getFieldType()
        Assert.assertEquals(FieldType.CARD_NUMBER, type)
    }


    @Test
    fun test_set_divider() {
        assertNotNull(view)

        view.setDivider(' ')
        assertEquals(' ', view.getDivider())
    }

    @Test
    fun test_set_card_brand_icon_gravity_start() {
        assertNotNull(view)

        view.setCardBrandIconGravity(Gravity.START)
        assertEquals(Gravity.START, view.getCardPreviewIconGravity())
    }

    @Test
    fun test_set_card_brand_icon_gravity_left() {
        assertNotNull(view)

        view.setCardBrandIconGravity(Gravity.LEFT)
        assertEquals(Gravity.LEFT, view.getCardPreviewIconGravity())
    }

    @Test
    fun test_set_card_brand_icon_gravity_end() {
        assertNotNull(view)

        view.setCardBrandIconGravity(Gravity.END)
        assertEquals(Gravity.END, view.getCardPreviewIconGravity())
    }

    @Test
    fun test_set_card_brand_icon_gravity_right() {
        assertNotNull(view)

        view.setCardBrandIconGravity(Gravity.RIGHT)
        assertEquals(Gravity.RIGHT, view.getCardPreviewIconGravity())
    }

    @Test
    fun test_set_card_brand_icon_gravity_wrong() {
        assertNotNull(view)

        view.setCardBrandIconGravity(Gravity.TOP)
        assertEquals(Gravity.END, view.getCardPreviewIconGravity())
        view.setCardBrandIconGravity(Gravity.BOTTOM)
        assertEquals(Gravity.END, view.getCardPreviewIconGravity())
        view.setCardBrandIconGravity(Gravity.NO_GRAVITY)
        assertEquals(Gravity.NO_GRAVITY, view.getCardPreviewIconGravity())
        view.setCardBrandIconGravity(Gravity.CENTER_VERTICAL)
        assertEquals(Gravity.END, view.getCardPreviewIconGravity())
        view.setCardBrandIconGravity(Gravity.CENTER_HORIZONTAL)
        assertEquals(Gravity.END, view.getCardPreviewIconGravity())
    }

    @Test
    fun test_input_type_number() {
        assertNotNull(view)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_input_type_number_password() {
        assertNotNull(view)

        val passType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)
        assertEquals(passType, view.getInputType())

        view.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD)
        assertEquals(passType, view.getInputType())
    }

    @Test
    fun test_input_type_text_password() {
        assertNotNull(view)

        val passType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)

        val correctType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        val it = view.getInputType()
        assertEquals(correctType, it)
    }

    @Test
    fun test_input_type_other() {
        assertNotNull(view)

        view.setInputType(InputType.TYPE_CLASS_TEXT)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }


    @Test
    fun test_field_state_change_listener_first() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        val listener = Mockito.mock(OnFieldStateChangeListener::class.java)
        view.setOnFieldStateChangeListener(listener)
        Mockito.verify(listener, Mockito.times(0)).onStateChange(any())

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        Mockito.verify(listener, Mockito.times(1)).onStateChange(any())
    }

    @Test
    fun test_field_state_change_listener_last() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val listener = Mockito.mock(OnFieldStateChangeListener::class.java)
        view.setOnFieldStateChangeListener(listener)

        Mockito.verify(listener, Mockito.times(1)).onStateChange(any())
    }

    @Test
    fun test_on_focus_change_listener() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val listener = mock(View.OnFocusChangeListener::class.java)
        view.onFocusChangeListener = listener
        view.requestFocus()

        Mockito.verify(listener, Mockito.times(1)).onFocusChange(view, true)
    }

    private fun <T> any(): T = Mockito.any<T>()
}