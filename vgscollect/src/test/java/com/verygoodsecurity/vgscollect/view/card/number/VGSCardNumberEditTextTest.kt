package com.verygoodsecurity.vgscollect.view.card.number

import android.app.Activity
import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import android.view.View
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.TestApplication
import com.verygoodsecurity.vgscollect.any
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.BrandParams
import com.verygoodsecurity.vgscollect.view.card.CardBrand
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.validation.CheckSumValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthMatchValidator
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.rules.PaymentCardNumberRule
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.view.internal.CardInputField
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
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
        val internal = view.statePreparer.getView()
        assertNotNull(internal)
    }

    @Test
    fun test_check_internal_view() {
        val internal = view.statePreparer.getView()
        assertNotNull(internal)

        val child = view.statePreparer.getView()
        assertTrue(child is CardInputField)
    }

    @Test
    fun test_attach_view() {
        view.onAttachedToWindow()

        assertEquals(1, view.childCount)
    }

    @Test
    fun test_field_type() {
        val type = view.getFieldType()
        assertEquals(FieldType.CARD_NUMBER, type)
    }

    @Test
    fun test_set_divider() {
        assertNotNull(view)

        view.setDivider('=')
        assertEquals('=', view.getDivider())
        view.setDivider('1')
        assertEquals(' ', view.getDivider())
        view.setDivider('#')
        assertEquals(' ', view.getDivider())
        view.setDivider('\\')
        assertEquals(' ', view.getDivider())
        view.setDivider('/')
        assertEquals('/', view.getDivider())
        view.setDivider(null)
        assertEquals(null, view.getDivider())
        view.setDivider(' ')
        assertEquals(' ', view.getDivider())
    }

    @Test
    fun test_set_output_divider() {
        assertNotNull(view)

        view.setOutputDivider('=')
        assertEquals('=', view.getOutputDivider())
        view.setOutputDivider('1')
        assertEquals(null, view.getOutputDivider())
        view.setOutputDivider('#')
        assertEquals(null, view.getOutputDivider())
        view.setOutputDivider('\\')
        assertEquals(null, view.getOutputDivider())
        view.setOutputDivider('/')
        assertEquals('/', view.getOutputDivider())
        view.setOutputDivider(null)
        assertEquals(null, view.getOutputDivider())
        view.setOutputDivider(' ')
        assertEquals(' ', view.getOutputDivider())
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
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val listener = mock(OnFieldStateChangeListener::class.java)
        view.setOnFieldStateChangeListener(listener)
        verify(listener, times(0)).onStateChange(any())

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        verify(listener, times(1)).onStateChange(any())
    }

    @Test
    fun test_field_state_change_listener_last() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val listener = mock(OnFieldStateChangeListener::class.java)
        view.setOnFieldStateChangeListener(listener)

        verify(listener, times(1)).onStateChange(any())
    }

    @Test
    fun test_on_focus_change_listener() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val listener = mock(View.OnFocusChangeListener::class.java)
        view.onFocusChangeListener = listener
        view.requestFocus()

        verify(listener, times(1)).onFocusChange(view, true)
    }

    @Test
    fun test_state() {
        val text = "4111111111111111"
        val stateResult = FieldState.CardNumberState()
        stateResult.hasFocus = false
        stateResult.isEmpty = false
        stateResult.isValid = true
        stateResult.isRequired = true
        stateResult.contentLength = text.length
        stateResult.fieldName = "number"
        stateResult.fieldType = FieldType.CARD_NUMBER
        stateResult.bin = "41111111"
        stateResult.last = "1111"
        stateResult.number = "411111######1111"
        stateResult.drawableBrandResId = R.drawable.ic_visa_dark

        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)
        view.setText(text)
        view.setFieldName("number")

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()


        val state = view.getState()
        assertNotNull(state)
        assertEquals(stateResult.hasFocus, state!!.hasFocus)
        assertEquals(stateResult.isEmpty, state.isEmpty)
        assertEquals(stateResult.isValid, state.isValid)
        assertEquals(stateResult.isRequired, state.isRequired)
        assertEquals(stateResult.contentLength, state.contentLength)
        assertEquals(stateResult.fieldName, state.fieldName)
        assertEquals(stateResult.fieldType, state.fieldType)

        assertEquals(stateResult.bin, state.bin)
        assertEquals(stateResult.last, state.last)
        assertEquals(stateResult.number, state.number)
        assertEquals(stateResult.drawableBrandResId, state.drawableBrandResId)
    }

    @Test
    fun test_length() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowableNumberLength(arrayOf(12, 15))
            .build()
        view.setRule(rule)
        view.setText("12312312312")

        child.refreshInternalState()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(false, state!!.isValid)
        assertEquals(11, state.contentLength)
        assertEquals(listOf(LengthMatchValidator.DEFAULT_ERROR_MSG), state.validationErrors)

        view.setText("123123123123")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(true, state2!!.isValid)
        assertEquals(12, state2.contentLength)
        assertEquals("123123", state2.bin)
        assertEquals(emptyList<String>(), state2.validationErrors)

        view.setText("123123123123123")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(true, state3!!.isValid)
        assertEquals(15, state3.contentLength)
        assertEquals("123123", state3.bin)
        assertEquals(emptyList<String>(), state2.validationErrors)
    }

    @Test
    fun test_length_min_with_custom_error_message() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val errorMessage = "Invalid length."
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowableMinLength(7, errorMessage)
            .build()
        view.setRule(rule)
        view.setText("123123")

        child.refreshInternalState()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(false, state!!.isValid)
        assertEquals(6, state.contentLength)
        assertEquals(listOf(errorMessage), state.validationErrors)

        view.setText("1231234")

        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(true, state2!!.isValid)
        assertEquals(7, state2.contentLength)
        assertEquals("123123", state2.bin)
        assertEquals(emptyList<String>(), state2.validationErrors)

        view.setText("1231231231231231231")

        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(true, state3!!.isValid)
        assertEquals(19, state3.contentLength)
        assertEquals("123123", state3.bin)
        assertEquals(emptyList<String>(), state3.validationErrors)
    }


    @Test
    fun test_length_max() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowableMaxLength(17)
            .build()
        view.setRule(rule)
        view.setText("1231231")

        child.refreshInternalState()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(false, state!!.isValid)
        assertEquals(7, state.contentLength)

        view.setText("1231231231233")

        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(true, state2!!.isValid)
        assertEquals(13, state2.contentLength)
        assertEquals("123123", state2.bin)

        view.setText("12312312312312312")

        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(true, state3!!.isValid)
        assertEquals(17, state3.contentLength)
        assertEquals("123123", state3.bin)

        view.setText("123123123123123123")

        child.refreshInternalState()

        val state4 = view.getState()
        assertNotNull(state)
        assertEquals(false, state4!!.isValid)
        assertEquals(18, state4.contentLength)
        assertEquals("", state4.bin)
    }


    @Test
    fun test_length_min_max() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowableMaxLength(17)
            .setAllowableMinLength(15)
            .build()
        view.setRule(rule)

        view.setText("12312312312312")
        child.refreshInternalState()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(false, state!!.isValid)
        assertEquals(14, state.contentLength)


        view.setText("123123123123123")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(true, state2!!.isValid)
        assertEquals(15, state2.contentLength)
        assertEquals("123123", state2.bin)

        view.setText("12312312312312312")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(true, state3!!.isValid)
        assertEquals(17, state3.contentLength)
        assertEquals("123123", state3.bin)

        view.setText("12312312312312312")
        child.refreshInternalState()

        val state4 = view.getState()
        assertNotNull(state)
        assertEquals(true, state4!!.isValid)
        assertEquals(17, state4.contentLength)
        assertEquals("123123", state4.bin)

        view.setText("123123123123123123")
        child.refreshInternalState()

        val state5 = view.getState()
        assertNotNull(state)
        assertEquals(false, state5!!.isValid)
        assertEquals(18, state5.contentLength)
        assertEquals("", state5.bin)
    }

    @Test
    fun test_luhn() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAlgorithm(ChecksumAlgorithm.LUHN)
            .build()
        view.setRule(rule)

        view.setText("1111111111111117")
        child.refreshInternalState()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(true, state!!.isValid)
        assertEquals(16, state.contentLength)
        assertEquals("111111", state.bin)


        view.setText("1111111111112")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(true, state2!!.isValid)
        assertEquals(13, state2.contentLength)
        assertEquals("111111", state2.bin)

        view.setText("1111111111112222331")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(true, state3!!.isValid)
        assertEquals(19, state3.contentLength)
        assertEquals("111111", state3.bin)
    }

    @Test
    fun test_regex() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setRegex("^[0-9]{15}(?:[0-9]{1})?\$")
            .build()
        view.setRule(rule)


        view.setText("4111111111111111")
        child.refreshInternalState()

        val state0 = view.getState()
        assertNotNull(state0)
        assertEquals(true, state0!!.isValid)
        assertEquals(16, state0.contentLength)
        assertEquals("41111111", state0.bin)


        view.setText("0111111111111111")
        child.refreshInternalState()

        val state1 = view.getState()
        assertNotNull(state1)
        assertEquals(true, state1!!.isValid)
        assertEquals(16, state1.contentLength)
        assertEquals("011111", state1.bin)

        view.setText("01111111111111112")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state2)
        assertEquals(false, state2!!.isValid)
        assertEquals(17, state2.contentLength)

        view.setText("01111111111111")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state3)
        assertEquals(false, state3!!.isValid)
        assertEquals(14, state3.contentLength)
    }

    @Test
    fun test_override_default_validation() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowableMinLength(12)
            .setAllowableMaxLength(14)
            .setAllowToOverrideDefaultValidation(true)
            .build()
        view.setRule(rule)


        view.setText("41111111111111")
        child.refreshInternalState()

        val state0 = view.getState()
        assertNotNull(state0)
        assertEquals(true, state0!!.isValid)
        assertEquals(14, state0.contentLength)
        assertEquals("411111", state0.bin)

        view.setText("411111111111")
        child.refreshInternalState()

        val state1 = view.getState()
        assertNotNull(state1)
        assertEquals(true, state1!!.isValid)
        assertEquals(12, state1.contentLength)
        assertEquals("411111", state1.bin)


        view.setText("41111111111")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state2)
        assertEquals(false, state2!!.isValid)
        assertEquals(11, state2.contentLength)
    }

    @Test
    fun test_wrong_override_default_validation() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowToOverrideDefaultValidation(true)
            .build()
        view.setRule(rule)


        view.setText("41111111111111111")
        child.refreshInternalState()

        val state0 = view.getState()
        assertNotNull(state0)
        assertEquals(false, state0!!.isValid)
        assertEquals(17, state0.contentLength)

        view.setText("4111111111111111")
        child.refreshInternalState()

        val state1 = view.getState()
        assertNotNull(state1)
        assertEquals(true, state1!!.isValid)
        assertEquals(16, state1.contentLength)
        assertEquals("41111111", state1.bin)


        view.setText("411111111111111")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state2)
        assertEquals(false, state2!!.isValid)
        assertEquals(15, state2.contentLength)
    }

    @Test
    fun test_length_luhn() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAlgorithm(ChecksumAlgorithm.LUHN)
            .setAllowableNumberLength(arrayOf(16))
            .build()
        view.setRule(rule)

        view.setText("1111111111111117")
        child.refreshInternalState()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(true, state!!.isValid)
        assertEquals(16, state.contentLength)
        assertEquals("111111", state.bin)


        view.setText("1111111111112")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(false, state2!!.isValid)
        assertEquals(13, state2.contentLength)

        view.setText("1111111111112222331")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(false, state3!!.isValid)
        assertEquals(19, state3.contentLength)
    }

    @Test
    fun test_luhn_length_regex() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAlgorithm(ChecksumAlgorithm.LUHN)
            .setAllowableNumberLength(arrayOf(15))
            .setRegex("^[0-9]{15}(?:[0-9]{1})?\$")
            .build()
        view.setRule(rule)

        view.setText("4111111111111167")
        child.refreshInternalState()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(false, state!!.isValid)
        assertEquals(16, state.contentLength)
        assertEquals(listOf(CheckSumValidator.DEFAULT_ERROR_MSG), state.validationErrors)


        view.setText("411111111111116")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(false, state2!!.isValid)
        assertEquals(15, state2.contentLength)
        assertEquals(listOf(LengthMatchValidator.DEFAULT_ERROR_MSG), state2.validationErrors)

        view.setText("411111111111111")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(false, state3!!.isValid)
        assertEquals(15, state3.contentLength)
        assertEquals(listOf(LengthMatchValidator.DEFAULT_ERROR_MSG, CheckSumValidator.DEFAULT_ERROR_MSG), state3.validationErrors)
    }

    @Test
    fun test_luhn_length_regex_with_allow_override() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAlgorithm(ChecksumAlgorithm.LUHN)
            .setAllowableNumberLength(arrayOf(15))
            .setRegex("^[0-9]{15}(?:[0-9]{1})?\$")
            .setAllowToOverrideDefaultValidation(true)
            .build()
        view.setRule(rule)

        view.setText("4111111111111167")
        child.refreshInternalState()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(false, state!!.isValid)
        assertEquals(16, state.contentLength)
        assertEquals(listOf(CheckSumValidator.DEFAULT_ERROR_MSG, LengthMatchValidator.DEFAULT_ERROR_MSG), state.validationErrors)

        view.setText("411111111111116")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state2)
        assertEquals(true, state2!!.isValid)
        assertEquals(15, state2.contentLength)
        assertEquals(emptyList<String>(), state2.validationErrors)

        view.setText("377400111111115")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state3)
        assertEquals(true, state3!!.isValid)
        assertEquals(15, state3.contentLength)
        assertEquals(emptyList<String>(), state3.validationErrors)
    }

    @Test
    fun test_default_validation() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        view.setText("411111111111117")
        child.refreshInternalState()

        val state1 = view.getState()
        assertNotNull(state1)
        assertEquals(false, state1!!.isValid)
        assertEquals(15, state1.contentLength)
        assertEquals(
            listOf(
                LengthMatchValidator.DEFAULT_ERROR_MSG,
                CheckSumValidator.DEFAULT_ERROR_MSG
            ), state1.validationErrors
        )

        view.setText("4111111111111111")
        child.refreshInternalState()

        val state2= view.getState()
        assertNotNull(state2)
        assertEquals(true, state2!!.isValid)
        assertEquals(16, state2.contentLength)
        assertEquals(emptyList<String>(), state2.validationErrors)
    }

    @Test
    fun test_default_validation_with_append() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)
        view.enableValidation(true)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val errorMessage = "Only digits allowed."
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setRegex("\\d+", errorMessage)
            .setAllowToOverrideDefaultValidation(true)
            .build()
        view.appendRule(rule)

        view.setText("12345f")
        child.refreshInternalState()

        val state1 = view.getState()
        assertNotNull(state1)
        assertEquals(false, state1!!.isValid)
        assertEquals(6, state1.contentLength)
        assertEquals(listOf(errorMessage), state1.validationErrors)

        view.setText("4111111f11111111")
        child.refreshInternalState()

        val state2= view.getState()
        assertNotNull(state2)
        assertEquals(false, state2!!.isValid)
        assertEquals(16, state2.contentLength)
        assertEquals(listOf(errorMessage), state1.validationErrors)

        view.setText("4111111111111111")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state2)
        assertEquals(true, state3!!.isValid)
        assertEquals(16, state3.contentLength)
        assertEquals(emptyList<String>(), state3.validationErrors)
    }

    @Test
    fun set_typeface() {
        assertEquals(null, view.getTypeface())
        view.getTypeface().let {
            view.setTypeface(it, Typeface.BOLD)
        }

        assertEquals(view.getTypeface(), Typeface.DEFAULT_BOLD)
        view.setTypeface(null, Typeface.NORMAL)
        assertEquals(view.getTypeface(), Typeface.DEFAULT)
    }

    @Test
    fun test_custom_brand_state() {
        val text = "4111111111111111"

        val params = BrandParams(
            "###### ##### ########",
            ChecksumAlgorithm.LUHN,
            arrayOf(16, 19),
            arrayOf(3, 5)
        )

        val newVisa = CardBrand(
            "^41111",
            "newVisa-Brand",
            R.drawable.ic_card_back_preview_dark,
            params
        )

        val stateResult = FieldState.CardNumberState()
        stateResult.bin = "41111111"
        stateResult.last = "1111"
        stateResult.number = "411111######1111"
        stateResult.cardBrand = CardType.VISA.name
        stateResult.drawableBrandResId = R.drawable.ic_visa_dark

        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)
        view.setFieldName("number")

        view.setText(text)
        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        with(view.getState()) {
            assertNotNull(this)

            assertEquals(stateResult.bin, this!!.bin)
            assertEquals(stateResult.last, this.last)
            assertEquals(stateResult.number, this.number)
            assertEquals(stateResult.cardBrand, this.cardBrand)
            assertEquals(stateResult.drawableBrandResId, this.drawableBrandResId)
        }


        view.addCardBrand(newVisa)

        view.setText(text)
        child.prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        with(view.getState()) {
            assertNotNull(this)

            assertEquals(stateResult.last, this!!.last)
            assertEquals(stateResult.number, this.number)
            assertNotEquals(stateResult.bin, this.bin)
            assertNotEquals(stateResult.cardBrand, this.cardBrand)
            assertNotEquals(stateResult.drawableBrandResId, this.drawableBrandResId)

            assertEquals(newVisa.cardBrandName, this.cardBrand)
            assertEquals(newVisa.drawableResId, this.drawableBrandResId)
        }
    }

    @Test
    fun test_custom_brand_state_with_divider() {
        val text = "4111111111111111"

        val params = BrandParams(
            "###### ##### ########",
            ChecksumAlgorithm.LUHN,
            arrayOf(16, 19),
            arrayOf(3, 5)
        )

        val newVisa = CardBrand(
            "^41111",
            "newVisa-Brand",
            R.drawable.ic_card_back_preview_dark,
            params
        )

        val stateResult = FieldState.CardNumberState()
        stateResult.cardBrand = CardType.VISA.name
        stateResult.drawableBrandResId = R.drawable.ic_visa_dark

        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)
        view.setFieldName("number")

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        view.addCardBrand(newVisa)
        view.setDivider('=')


        view.setText(text)

        with(view.getState()) {
            assertNotNull(this)
            assertNotEquals(stateResult.cardBrand, this!!.cardBrand)
            assertNotEquals(stateResult.drawableBrandResId, this.drawableBrandResId)

            assertEquals(newVisa.cardBrandName, this.cardBrand)
            assertEquals(newVisa.drawableResId, this.drawableBrandResId)
        }
    }

    @Test
    fun test_alias_format() {
        view.setVaultAliasFormat(VGSVaultAliasFormat.UUID)

        val child = view.statePreparer.getView()

        assertEquals((child as CardInputField).vaultAliasFormat, VGSVaultAliasFormat.UUID)
    }

    @Test
    fun test_storage_type() {
        view.setVaultStorageType(VGSVaultStorageType.VOLATILE)

        val child = view.statePreparer.getView()
        assertEquals((child as CardInputField).vaultStorage, VGSVaultStorageType.VOLATILE)
    }

    @Test
    fun test_default_tokenization_settings() {
        val child = view.statePreparer.getView()
        assertEquals((child as CardInputField).isEnabledTokenization, true)
        assertEquals(child.vaultAliasFormat, VGSVaultAliasFormat.FPE_SIX_T_FOUR)
        assertEquals(child.vaultStorage, VGSVaultStorageType.PERSISTENT)
    }

}