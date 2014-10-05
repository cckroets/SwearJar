package in2chris.calhacks.io.swearjar.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;
import in2chris.calhacks.io.swearjar.R;
import roboguice.fragment.RoboDialogFragment;
import roboguice.inject.InjectView;


/**
 * Created by ckroetsc on 10/4/14.
 */
public class PunishmentDialogFragment extends RoboDialogFragment {

  @InjectView(R.id.shaming)
  View mShamingOption;

  @InjectView(R.id.donate)
  View mDonateOption;

  OnPunishmentSelectedListener mListener;

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dialog_punishments, container, false);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    return dialog;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mShamingOption.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getDialog().dismiss();
        mListener.onShamingOptionSelected();
      }
    });
    mDonateOption.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getDialog().dismiss();
        mListener.onDonationOptionSelected();
      }
    });
  }

  @Override
  public void onCancel(DialogInterface dialog) {
    super.onCancel(dialog);
    Toast.makeText(getActivity(), "No punishment taken!", Toast.LENGTH_SHORT);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (OnPunishmentSelectedListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnPunishmentSelectedListener");
    }
  }

  public interface OnPunishmentSelectedListener {
    void onShamingOptionSelected();
    void onDonationOptionSelected();
  }
}
