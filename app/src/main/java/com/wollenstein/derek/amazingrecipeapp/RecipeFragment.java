package com.wollenstein.derek.amazingrecipeapp;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeFragment extends Fragment {
    public static final String ARG_RECIPE_NUMBER = "recipeNumber";
    public static final String ARG_RECIPE_TEXT = "recipeName";

    private int mRecipeNumber;
    private String mRecipeName;

    private OnFragmentInteractionListener mListener;
    private TextView mTextView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipeNumber The recipeNumber to display.
     * @param recipeText The recipeText to display.
     * @return A new instance of fragment RecipeFragment.
     */
    public static RecipeFragment newRecipeFragment(int recipeNumber, String recipeText) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RECIPE_NUMBER, recipeNumber);
        args.putString(ARG_RECIPE_TEXT, recipeText);
        fragment.setArguments(args);
        return fragment;
    }

    public RecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipeNumber = getArguments().getInt(ARG_RECIPE_NUMBER);
            mRecipeName = getArguments().getString(ARG_RECIPE_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_recipe, container, false);
        mTextView = (TextView) inflated.findViewById(R.id.recipeTextView);
        mTextView.setText(mRecipeName);
        return inflated;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String coolRecipe);
    }

}
