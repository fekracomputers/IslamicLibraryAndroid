package com.fekracomputers.islamiclibrary.reading.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.databases.BookDatabaseHelper;
import com.fekracomputers.islamiclibrary.model.BookPartsInfo;
import com.fekracomputers.islamiclibrary.model.PageInfo;
import com.fekracomputers.islamiclibrary.model.PartInfo;
import com.fekracomputers.islamiclibrary.widget.KeyboardAwareEditText;

import static com.fekracomputers.islamiclibrary.reading.ReadingActivity.KEY_BOOK_ID;
import static com.fekracomputers.islamiclibrary.reading.ReadingActivity.KEY_CURRENT_PAGE_INFO;
import static com.fekracomputers.islamiclibrary.reading.ReadingActivity.KEY_CURRENT_PARTS_INFO;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 16/5/2017.
 */
public class PageNumberPickerDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    PageNumberPickerDialogFragmentListener mListener;
    private BookPartsInfo mPartsInfo;
    private PageInfo currentPageInfo;
    private BookDatabaseHelper mBookDatabaseHelper;
    private PartInfo mCurrentPart;
    private KeyboardAwareEditText partNumberEditor;
    private KeyboardAwareEditText pageNumberEditor;


    public PageNumberPickerDialogFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        currentPageInfo = bundle.getParcelable(KEY_CURRENT_PAGE_INFO);
        mPartsInfo = bundle.getParcelable(KEY_CURRENT_PARTS_INFO);
        int bookId = bundle.getInt(KEY_BOOK_ID);
        mBookDatabaseHelper = BookDatabaseHelper.getInstance(getContext(), bookId);
        if (currentPageInfo.partNumber <= mPartsInfo.firstPart.partNumber) {
            mCurrentPart = mPartsInfo.firstPart;
            currentPageInfo = mBookDatabaseHelper.getPageInfoByPagePageNumberAndPartNumber(mPartsInfo.firstPart.partNumber,mPartsInfo.firstPart.firstPage);
        } else {
            mCurrentPart = mBookDatabaseHelper.getPartInfo(currentPageInfo.partNumber);

        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Pass null as the parent view because its going in the dialog layout
        View contentView = inflater.inflate(R.layout.dialog_page_number_picker, null);

        builder.setView(contentView)
                // Add action buttons
                .setPositiveButton(R.string.action_go, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        validateAndGo();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                })
                .setTitle(R.string.go_to_page);

        AlertDialog alertDialog = builder.create();

        ViewGroup partSelectionLayout = (ViewGroup) contentView.findViewById(R.id.part_selection_layout);
        partNumberEditor = (KeyboardAwareEditText) contentView.findViewById(R.id.part_number_editor);
        TextView totalPartNumberTv = (TextView) contentView.findViewById(R.id.total_part_number_tv);
        pageNumberEditor = (KeyboardAwareEditText) contentView.findViewById(R.id.page_number_editor);
        final TextView totalPageNumberTv = (TextView) contentView.findViewById(R.id.total_page_number_tv);

        if (mPartsInfo.isMultiPart()) {
            partSelectionLayout.setVisibility(View.VISIBLE);
            String partNumberString = getString(R.string.page_number, mPartsInfo.lastPart);
            totalPartNumberTv.setText(partNumberString);
            partNumberEditor.setHint(getString(R.string.page_number, currentPageInfo.partNumber));
            partNumberEditor.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String requiredpartString = s.toString();
                    if (!TextUtils.isEmpty(requiredpartString)) {
                        int requiredPart = Integer.valueOf(requiredpartString);
                        int firstpartNumber = mPartsInfo.firstPart.partNumber;
                        int lastpartNumber = mPartsInfo.lastPart;

                        if (requiredPart < firstpartNumber || requiredPart > lastpartNumber) {
                            partNumberEditor.setNumberEditorValid(false, getResources().getString(R.string.invalid_part_number,
                                    firstpartNumber, lastpartNumber));

                        } else {
                            partNumberEditor.setNumberEditorValid(true, null);
                            mCurrentPart = mBookDatabaseHelper.getPartInfo(requiredPart);
                            totalPageNumberTv.setText(getString(R.string.page_number, mCurrentPart.lastPage));
                            pageNumberEditor.setText("");
                            pageNumberEditor.setError(null);
                            pageNumberEditor.setHint("");

                        }
                    }
                    validatePositiveButtonState();
                }
            });


            partNumberEditor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    return actionId == EditorInfo.IME_ACTION_GO && validateAndGo();
                }
            });
            partNumberEditor.setHint(getString(R.string.page_number, currentPageInfo.partNumber));


        } else {
            partSelectionLayout.setVisibility(View.GONE);
        }
        pageNumberEditor.setHint(String.valueOf(currentPageInfo.pageNumber));
        totalPageNumberTv.setText(getString(R.string.page_number, mCurrentPart.lastPage));

        pageNumberEditor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return actionId == EditorInfo.IME_ACTION_GO && validateAndGo();
            }
        });
        pageNumberEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String requiredPageString = s.toString();
                if (!TextUtils.isEmpty(requiredPageString)) {
                    int requiredPage = Integer.parseInt(requiredPageString);
                    int firstPageNumber = mCurrentPart.firstPage;
                    int lastPageNumber = mCurrentPart.lastPage;
                    if (requiredPage < firstPageNumber || requiredPage > lastPageNumber) {
                        pageNumberEditor.setNumberEditorValid(false, getResources().getString(R.string.invalid_page_number,
                                firstPageNumber, lastPageNumber));

                    } else {
                        pageNumberEditor.setNumberEditorValid(true, null);

                    }
                }
                validatePositiveButtonState();
            }
        });


        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return alertDialog;
    }

    private boolean isPartAndPageValid() {
        boolean InRangeCheckPassed;

        boolean pageNumberValid = !pageNumberEditor.getText().toString().isEmpty() && pageNumberEditor.getError() == null;
        if (mPartsInfo.isMultiPart()) {
            boolean partNumberValid = !partNumberEditor.getText().toString().isEmpty() && partNumberEditor.getError() == null;
            InRangeCheckPassed = partNumberValid && pageNumberValid;
        } else {
            InRangeCheckPassed = pageNumberValid;
        }
        if (InRangeCheckPassed) {
            boolean checkAgainstDatabase = mBookDatabaseHelper.isPartPageCombinationValid(mCurrentPart.partNumber,
                    Integer.valueOf(pageNumberEditor.getText().toString()));
            if (!checkAgainstDatabase) {
                Toast.makeText(getContext(), R.string.error_page_missing, Toast.LENGTH_SHORT).show();
            }
            return checkAgainstDatabase;
        } else return false;
    }

    private boolean validateAndGo() {
        if (isPartAndPageValid()) {
            dismiss();
            mListener.onPageNumberDialogPositiveClick(Integer.valueOf(pageNumberEditor.getText().toString()),
                    mCurrentPart.partNumber);

            return true;
        } else
            return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        validatePositiveButtonState();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (PageNumberPickerDialogFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BatchDownloadConfirmationListener");
        }
    }

    private void validatePositiveButtonState() {
        AlertDialog dialog = (AlertDialog) getDialog();
        if (isPartAndPageValid()) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        } else {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }
    }

    public interface PageNumberPickerDialogFragmentListener {
        void onPageNumberDialogPositiveClick(int pageNumber, int partNumber);
    }


}
