package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextWatcher
import android.view.View
import android.view.autofill.AutofillValue
import android.view.inputmethod.EditorInfo
import androidx.annotation.VisibleForTesting
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.api.analityc.AnalyticTracker
import com.verygoodsecurity.vgscollect.core.api.analityc.action.AutofillAction
import com.verygoodsecurity.vgscollect.core.model.state.*
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType
import com.verygoodsecurity.vgscollect.core.storage.DependencyListener
import com.verygoodsecurity.vgscollect.core.storage.DependencyType
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.conection.InputRunnable
import com.verygoodsecurity.vgscollect.view.card.getAnalyticName
import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator
import com.verygoodsecurity.vgscollect.view.card.validation.rules.ValidationRule

/** @suppress */
internal abstract class BaseInputField(context: Context) : TextInputEditText(context),
    DependencyListener, OnVgsViewStateChangeListener {

    companion object {
        fun getInputField(context: Context, parent: InputFieldView): BaseInputField {
            val field = when (parent.getFieldType()) {
                FieldType.CARD_NUMBER -> CardInputField(context)
                FieldType.CVC -> CVCInputField(context)
                FieldType.DATE_RANGE -> DateRangeInputField(context)
                FieldType.CARD_EXPIRATION_DATE -> ExpirationDateInputField(context)
                FieldType.CARD_HOLDER_NAME -> PersonNameInputField(context)
                FieldType.SSN -> SSNInputField(context)
                FieldType.INFO -> InfoInputField(context)
            }
            field.vgsParent = parent
            return field
        }
    }

    internal var vaultStorage: VGSVaultStorageType = VGSVaultStorageType.PERSISTENT
        set(value) {
            field = value
            inputConnection?.getOutput()?.content?.vaultStorage = value
            inputConnection?.run()
        }

    internal var vaultAliasFormat: VGSVaultAliasFormat = VGSVaultAliasFormat.UUID
        set(value) {
            field = value
            inputConnection?.getOutput()?.content?.vaultAliasFormat = value
            inputConnection?.run()
        }

    internal var isEnabledTokenization: Boolean = true
        get() {
            return field
        }
        set(value) {
            field = value
            inputConnection?.getOutput()?.content?.isEnabledTokenization = value
            inputConnection?.run()
        }

    internal var stateListener: OnVgsViewStateChangeListener? = null
        set(value) {
            field = value
            inputConnection?.setOutputListener(value)
            inputConnection?.run()
        }
    internal var isRequired: Boolean = true
        set(value) {
            field = value
            inputConnection?.getOutput()?.isRequired = value
            inputConnection?.run()
        }

    internal var enableValidation: Boolean = true
        set(value) {
            field = value
            inputConnection?.getOutput()?.enableValidation = value
            inputConnection?.run()
        }

    protected var isListeningPermitted = true
    private var isEditorActionListenerConfigured = false
    private var isKeyListenerConfigured = false

    protected abstract var fieldType: FieldType

    protected var inputConnection: InputRunnable? = null
    protected val validator: CompositeValidator = CompositeValidator()

    protected var vgsParent: InputFieldView? = null

    private var onFieldStateChangeListener: OnFieldStateChangeListener? = null

    private var userFocusChangeListener: OnFocusChangeListener? = null
    private var onEditorActionListener: InputFieldView.OnEditorActionListener? = null
    private var userKeyListener: OnKeyListener? = null

    private var isBackgroundVisible = true

    private var activeTextWatcher: TextWatcher? = null

    init {
        isListeningPermitted = true
        setupInputConnectionListener()
        setupEditorActionListener()
        setupOnKeyListener()
        isListeningPermitted = false

        setupViewAttributes()
        setupAutofill()
    }

    internal open fun setupAutofill() {}

    private fun setupEditorActionListener() {
        setOnEditorActionListener { _, actionId, event ->
            val consumedAction =
                onEditorActionListener?.onEditorAction(vgsParent, actionId, event) ?: false

            consumedAction
        }
    }

    internal fun setIsListeningPermitted(state: Boolean) {
        isListeningPermitted = state
    }

    private fun setupViewAttributes() {
        id = ViewCompat.generateViewId()

        compoundDrawablePadding = resources.getDimension(R.dimen.half_vgsfield_padding).toInt()
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        inputConnection?.getOutput()?.apply {

            userFocusChangeListener?.onFocusChange(vgsParent, focused)

            if (focused != isFocusable) {
                isFocusable = focused
                hasUserInteraction = true
                inputConnection?.run()
            }
        }
    }

    private fun setupInputConnectionListener() {
        addTextChangedListener {
            updateTextChanged(it.toString())
            vgsParent?.notifyOnTextChanged(it.isNullOrEmpty())
        }
    }

    protected open fun updateTextChanged(str: String) {
        inputConnection?.also {
            with(it.getOutput()) {
                if (str.isNotEmpty()) {
                    hasUserInteraction = true
                }
                content?.data = str
            }
            it.run()
        }
    }

    override fun onAttachedToWindow() {
        isListeningPermitted = true
        applyFieldType()
        inputConnection?.getOutput()?.enableValidation = enableValidation
        inputConnection?.getOutput()?.content?.isEnabledTokenization = isEnabledTokenization
        inputConnection?.getOutput()?.content?.vaultStorage = vaultStorage
        inputConnection?.getOutput()?.content?.vaultAliasFormat = vaultAliasFormat
        super.onAttachedToWindow()
        applyInternalFieldStateChangeListener()
        isListeningPermitted = false
    }

    private fun setupOnKeyListener() {
        setOnKeyListener { view, i, keyEvent ->
            userKeyListener?.onKey(vgsParent, i, keyEvent) ?: false
        }
    }

    protected fun refreshInputConnection() {
        isListeningPermitted = true
        applyFieldType()
        isListeningPermitted = false
    }

    protected abstract fun applyFieldType()

    protected fun applyNewTextWatcher(textWatcher: TextWatcher?) {
        activeTextWatcher?.let { removeTextChangedListener(activeTextWatcher) }
        textWatcher?.let { addTextChangedListener(textWatcher) }
        activeTextWatcher = textWatcher
    }

    protected fun collectCurrentState(stateContent: FieldContent): VGSFieldState {
        val state = VGSFieldState().apply {
            isRequired = this@BaseInputField.isRequired
            isFocusable = this@BaseInputField.hasFocus()
            type = this@BaseInputField.fieldType
            content = stateContent

            fieldName = this@BaseInputField.tag as? String
        }

        return state
    }

    internal fun setHasBackground(state: Boolean) {
        isBackgroundVisible = state
        if (isBackgroundVisible) {
            setBackgroundResource(android.R.color.transparent)
        }
    }

    protected fun isRTL(): Boolean {
        val direction = getResolvedLayoutDirection()
        return direction == View.LAYOUT_DIRECTION_RTL
                || direction == View.TEXT_DIRECTION_ANY_RTL
                || direction == View.TEXT_DIRECTION_FIRST_STRONG_RTL
                || direction == View.TEXT_DIRECTION_RTL
    }

    private fun getResolvedLayoutDirection(): Int = layoutDirection

    protected fun refreshOutputContent() {
        updateTextChanged(text.toString())
    }

    protected fun refreshInput() {
        val currentSelection = selectionStart
        setText(text)
        val textLength = text?.length ?: 0

        when {
            currentSelection > textLength -> setSelection(textLength)
            selectionStart > currentSelection -> setSelection(selectionStart)
            selectionStart < currentSelection -> setSelection(currentSelection)
        }
    }

    override fun setTag(tag: Any?) {
        tag?.run {
            super.setTag(tag)
            inputConnection?.getOutput()?.fieldName = this as String
        }
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        if (isListeningPermitted) {
            super.addTextChangedListener(watcher)
        }
    }

    private var minH: Int = 0
    private var minW: Int = 0
    internal fun setMinimumPaddingLimitations(w: Int, h: Int) {
        minH = h
        minW = w
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        val l = left + minW
        val r = right + minW
        val t = top + minH
        val b = bottom + minH
        super.setPadding(l, t, r, b)
    }

    override fun setCompoundDrawables(
        left: Drawable?,
        top: Drawable?,
        right: Drawable?,
        bottom: Drawable?
    ) {
        if (isRTL()) {
            super.setCompoundDrawables(right, top, left, bottom)
        } else {
            super.setCompoundDrawables(left, top, right, bottom)
        }
    }

    override fun dispatchDependencySetting(dependency: Dependency) {
        if (dependency.dependencyType == DependencyType.TEXT) {
            setText(dependency.value.toString())
        }
    }

    private fun requestFocusOnView(id: Int) {
        val nextView = rootView?.findViewById<View>(id)

        when (nextView) {
            null -> return
            is InputFieldView -> nextView.statePreparer.getView().requestFocus()
            is BaseInputField -> nextView.requestFocus()
            else -> nextView.requestFocus()
        }
    }

    override fun setOnKeyListener(l: OnKeyListener?) {
        if (!isKeyListenerConfigured) {
            isKeyListenerConfigured = true
            super.setOnKeyListener(l)
        } else {
            userKeyListener = l
        }
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        return super.requestFocus(direction, previouslyFocusedRect).also {
            setSelection(text?.length ?: 0)
        }
    }

    override fun onEditorAction(actionCode: Int) {
        when {
            actionCode == EditorInfo.IME_ACTION_NEXT
                    && nextFocusDownId != View.NO_ID -> requestFocusOnView(nextFocusDownId)
            actionCode == EditorInfo.IME_ACTION_PREVIOUS
                    && nextFocusUpId != View.NO_ID -> requestFocusOnView(nextFocusUpId)
        }
        super.onEditorAction(actionCode)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun applyInternalFieldStateChangeListener() {
        inputConnection?.setOutputListener(this)
    }

    override fun emit(viewId: Int, state: VGSFieldState) {
        val userState = state.mapToFieldState()
        onFieldStateChangeListener?.onStateChange(userState)
    }

    fun setOnFieldStateChangeListener(onFieldStateChangeListener: OnFieldStateChangeListener?) {
        this.onFieldStateChangeListener = onFieldStateChangeListener
        inputConnection?.run()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun prepareFieldTypeConnection() {
        applyFieldType()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun refreshInternalState() {
        inputConnection?.run()
    }

    internal fun setOnFocusChangeListener(l: OnFocusChangeListener?, isUserListener: Boolean) {
        if (isUserListener) {
            userFocusChangeListener = l
        }
    }

    override fun setOnEditorActionListener(l: OnEditorActionListener?) {
        if (!isEditorActionListenerConfigured) {
            isEditorActionListenerConfigured = true
            super.setOnEditorActionListener(l)
        }
    }

    fun setEditorActionListener(onEditorActionListener: InputFieldView.OnEditorActionListener?) {
        this.onEditorActionListener = onEditorActionListener
    }

    internal open fun getState(): FieldState? {
        return inputConnection?.getOutput()?.mapToFieldState()
    }

    internal var tracker: AnalyticTracker? = null

    override fun autofill(value: AutofillValue?) {
        super.autofill(value)
        logAutofillAction()
    }

    private fun logAutofillAction() {
        val m = with(mutableMapOf<String, String>()) {
            put("field", fieldType.getAnalyticName())
            this
        }

        tracker?.logEvent(
            AutofillAction(m)
        )
    }

    protected fun printWarning(tag: String, resId: Int) {
        VGSCollectLogger.warn(tag, context.getString(resId))
    }

    open fun applyValidationRules(rules: List<ValidationRule>) {
        validator.clearRules()
        rules.forEach { rule ->
            rule.algorithm?.let { validator.addRule(it) }
            rule.regex?.let { validator.addRule(it) }
            rule.length?.let { validator.addRule(it) }
            rule.lengthMatch?.let { validator.addRule(it) }
        }
    }

    open fun appendValidationRule(rule: ValidationRule) {
        rule.algorithm?.let { validator.addRule(it) }
        rule.regex?.let { validator.addRule(it) }
        rule.length?.let { validator.addRule(it) }
        rule.lengthMatch?.let { validator.addRule(it) }
    }

    fun isContentEquals(inputField: BaseInputField): Boolean {
        val thisContent = inputConnection?.getOutput()?.content
        val thisData = thisContent?.rawData ?: thisContent?.data
        val otherContent = inputField.inputConnection?.getOutput()?.content
        val otherData = otherContent?.rawData ?: otherContent?.data
        return thisData.equals(otherData)
    }
}

internal fun TextInputEditText.setCompoundDrawablesRelativeOrNull(
    start: Drawable? = null,
    top: Drawable? = null,
    end: Drawable? = null,
    bottom: Drawable? = null
) {
    this.setCompoundDrawablesRelative(start, top, end, bottom)
}

internal val TextInputEditText.localVisibleRect: Rect
    get() {
        val rect = Rect()
        this.getLocalVisibleRect(rect)
        return rect
    }