package org.solovyev.android.calculator.functions;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import org.solovyev.android.Activities;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.entities.EntityRemovalDialog;

import jscl.math.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EditFunctionFragment extends BaseFunctionFragment {

    public EditFunctionFragment() {
        super(R.layout.fragment_function_edit);
    }

    public static void show(@Nonnull FragmentActivity activity) {
        show(null, activity.getSupportFragmentManager());
    }

    public static void show(@Nullable CppFunction function, @Nonnull Context context) {
        if (!(context instanceof FunctionsActivity)) {
            final Intent intent = new Intent(context, FunctionsActivity.getClass(context));
            Activities.addIntentFlags(intent, false, context);
            intent.putExtra(FunctionsActivity.EXTRA_FUNCTION, function);
            context.startActivity(intent);
        } else {
            show(function, ((FunctionsActivity) context).getSupportFragmentManager());
        }
    }

    public static void show(@Nullable CppFunction function, @Nonnull FragmentManager fm) {
        App.showDialog(create(function), "function-editor", fm);
    }

    @Nonnull
    private static BaseFunctionFragment create(@Nullable CppFunction function) {
        final BaseFunctionFragment fragment = new EditFunctionFragment();
        if (function != null) {
            final Bundle args = new Bundle();
            args.putParcelable(ARG_FUNCTION, function);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    protected void onPrepareDialog(@NonNull AlertDialog.Builder builder) {
        super.onPrepareDialog(builder);
        if (!isNewFunction()) {
            builder.setNeutralButton(R.string.c_remove, null);
        }
    }

    private void showRemovalDialog(@NonNull final CppFunction function) {
        EntityRemovalDialog.showForFunction(getActivity(), function.name,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Check.isTrue(which == DialogInterface.BUTTON_POSITIVE);
                    functionsRegistry.remove(function.toJsclBuilder().create());
                    dismiss();
                }
            });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEUTRAL:
                Check.isNotNull(function);
                showRemovalDialog(function);
                break;
            default:
                super.onClick(dialog, which);
                break;
        }
    }

    @Override
    protected boolean applyData(@Nonnull @NonNull CppFunction function) {
        try {
            final Function oldFunction = isNewFunction() ? null : functionsRegistry.getById(function.id);
            functionsRegistry.add(function.toJsclBuilder(), oldFunction);
            return true;
        } catch (RuntimeException e) {
            setError(bodyLabel, e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    protected boolean validateName() {
        if (!super.validateName()) {
            return false;
        }
        final String name = nameView.getText().toString();
        final Function existingFunction = functionsRegistry.get(name);
        if (existingFunction != null) {
            if (!existingFunction.isIdDefined()) {
                Check.shouldNotHappen();
                setError(nameLabel, getString(R.string.function_already_exists));
                return false;
            }
            if (isNewFunction()) {
                // trying to create a new function with existing name
                setError(nameLabel, getString(R.string.function_already_exists));
                return false;
            }
            Check.isNotNull(function);
            if (!existingFunction.getId().equals(function.getId())) {
                // trying ti change the name of existing function to some other function's name
                setError(nameLabel, getString(R.string.function_already_exists));
                return false;
            }
        }
        clearError(nameLabel);
        return true;
    }
}
