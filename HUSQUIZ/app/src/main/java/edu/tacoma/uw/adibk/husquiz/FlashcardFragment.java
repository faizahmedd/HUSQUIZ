/**
 * This class creates the flashcard fragment that is called when the flashcard list activity is called.
 */
package edu.tacoma.uw.adibk.husquiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import edu.tacoma.uw.adibk.husquiz.model.Course;
import edu.tacoma.uw.adibk.husquiz.model.Flashcard;

public class FlashcardFragment extends Fragment {
    /**
     * A flashcard object
     */
    private Flashcard mFlashcard;
    /**
     * The static id of the flashcard
     */
    public static final String ARG_PARAM = "item_id";

    public static final String QUIZ = "quiz";

    public static final String COURSE = "course";
    /**
     * The parameter name
     */
    private static final String ARG_PARAM1 = "param1";
    /**
     * Another parameter name
     */
    private static final String ARG_PARAM2 = "param2";
    /**
     * Another parameter name
     */
    private String mParam1;
    /**
     * Another parameter name
     */
    private String mParam2;

    public FlashcardFragment() {
        // Required empty public constructor
    }

    /**
     * This method checks if the flashcard exists
     * @param savedInstanceState default bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_PARAM)) {
            mFlashcard = (Flashcard) getArguments().getSerializable(ARG_PARAM);
        }
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null)
        {
            appBarLayout.setTitle("Question " + (mFlashcard.getmFlashcardID()));
        }
    }

    /**
     * This method checks if the flashcard exists & sets the question & answer
     * @param inflater default inflater
     * @param container default viewgroup
     * @param savedInstanceState default bundle
     * @return the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flashcard, container, false);
        final TextView question = (TextView) view.findViewById(R.id.question);
        final TextView answer = (TextView) view.findViewById(R.id.answer);
        if (mFlashcard != null) {
            Button answerButton = view.findViewById(R.id.answer_button);
            question.setText(mFlashcard.getmQuestion());
            answer.setText(mFlashcard.getmAnswer());
            answerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    answer.setVisibility(View.VISIBLE);
                }
            });
        }
        return view;
    }
}