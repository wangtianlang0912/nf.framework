package nf.framework.fragment;

import nf.framework.expand.dialog.ProgressDialog;
import android.support.v4.app.Fragment;

public class AbsBaseFragment extends Fragment {

	private ProgressDialog progressDialog = null;

	protected void showProgressBar() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(getActivity());
		}
		if (!progressDialog.isShowing() && !getActivity().isFinishing())
			progressDialog.show();
	}

	protected void dismissProgressBar() {

		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
}
