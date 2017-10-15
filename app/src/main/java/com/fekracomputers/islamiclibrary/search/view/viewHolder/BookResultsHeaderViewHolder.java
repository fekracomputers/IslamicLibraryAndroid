package com.fekracomputers.islamiclibrary.search.view.viewHolder;

import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.model.BookPartsInfo;
import com.fekracomputers.islamiclibrary.search.model.BookSearchResultsContainer;

/**
 * بسم الله الرحمن الرحيم
 * Created by moda_ on 22/2/2017.
 */
public class BookResultsHeaderViewHolder extends ParentViewHolder {
    private TextView bookNameTextView;
    private TextView numberOfResultsTextView;
    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;
    private final ImageView mArrowExpandImageView;
    BookPartsInfo bookPartsInfo;

    public BookResultsHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        bookNameTextView = itemView.findViewById(R.id.number_of_results_per_book);
        numberOfResultsTextView = itemView.findViewById(R.id.book_name_header);
        mArrowExpandImageView = itemView.findViewById(R.id.arrow_expand_imageview);

    }

    public void bind(BookSearchResultsContainer bookSearchResultsContainer) {
        bookNameTextView.setText(bookSearchResultsContainer.getBookName());
        numberOfResultsTextView.setText(String.valueOf(bookSearchResultsContainer.getChildCount()));
        this.bookPartsInfo=bookSearchResultsContainer.bookPartsInfo;
    }



    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (expanded) {
            mArrowExpandImageView.setRotation(ROTATED_POSITION);
        } else {
            mArrowExpandImageView.setRotation(INITIAL_POSITION);
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        ObjectAnimator imageViewObjectAnimator;
        if (expanded) { // rotate clockwise
             imageViewObjectAnimator = ObjectAnimator.ofFloat(mArrowExpandImageView ,
                    View.ROTATION, ROTATED_POSITION,INITIAL_POSITION);

        } else { // rotate counterclockwise
             imageViewObjectAnimator = ObjectAnimator.ofFloat(mArrowExpandImageView ,
                    View.ROTATION,INITIAL_POSITION, ROTATED_POSITION);
        }

        imageViewObjectAnimator.setDuration(1000);
        imageViewObjectAnimator.setInterpolator(new OvershootInterpolator());
        imageViewObjectAnimator.start();
    }
}
