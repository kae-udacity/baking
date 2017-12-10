package com.example.android.baking.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.baking.R;
import com.example.android.baking.data.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StepDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepDetailsFragment extends Fragment {

    @BindView(R.id.text_view_step_description)
    TextView textViewStepDescription;

    @BindView(R.id.button_previous_step)
    Button buttonPreviousStep;

    @BindView(R.id.button_next_step)
    Button buttonNextStep;

    private StepDetailsOnClickListener listener;

    public StepDetailsFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment StepDetailsFragment.
     */
    public static StepDetailsFragment newInstance(Bundle bundle) {
        StepDetailsFragment stepDetailsFragment = new StepDetailsFragment();
        stepDetailsFragment.setArguments(bundle);
        return stepDetailsFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_details, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        List<Step> steps = null;
        int position = -1;
        if (bundle != null) {
            steps = bundle.getParcelableArrayList(getString(R.string.steps));
            position = bundle.getInt(getString(R.string.step_position));
        }

        if (steps != null) {
            Step currentStep = null;
            if (position != -1) {
                currentStep = steps.get(position);
            }

            if (currentStep != null) {
                textViewStepDescription.setText(currentStep.getDescription());
            }

            setUpStepButton(buttonPreviousStep, steps, position, -1);
            setUpStepButton(buttonNextStep, steps, position, 1);
        }

        return view;
    }

    private void setUpStepButton(Button button, final List<Step> steps, final int position, final int offset) {
        int newPosition = position + offset;
        Step step = null;
        if (newPosition > -1 && newPosition < steps.size()) {
            step = steps.get(newPosition);
        }
        if (step != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onStepSelected(position + offset);
                    textViewStepDescription.setText(null);
                }
            });
        } else {
            button.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StepDetailsOnClickListener) {
            listener = (StepDetailsOnClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement StepDetailsOnClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface StepDetailsOnClickListener {
        void onStepSelected(int position);
    }
}
