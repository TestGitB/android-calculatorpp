/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.view.MotionEvent;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.DirectionDragButton;
import org.solovyev.android.view.DragButton;
import org.solovyev.android.view.DragDirection;
import org.solovyev.android.view.SimpleOnDragListener;
import org.solovyev.common.utils.Point2d;

/**
 * User: serso
 * Date: 9/16/11
 * Time: 11:48 PM
 */
public class DigitButtonDragProcessor implements SimpleOnDragListener.DragProcessor {

	@NotNull
	private final CalculatorView calculatorView;

	public DigitButtonDragProcessor(@NotNull CalculatorView calculatorView) {
		this.calculatorView = calculatorView;
	}

	@Override
	public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
		assert dragButton instanceof DirectionDragButton;
		calculatorView.processDigitButtonAction(((DirectionDragButton) dragButton).getDirectionText(dragDirection));
		return true;
	}

}