package a.b.framehelper;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.util.AttributeSet;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class RSpinner extends Spinner{

	public RSpinner(Context context) {
		super(context);
		init();
	}
	public RSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode,
			Theme popupTheme) {
		super(context, attrs, defStyleAttr, defStyleRes, mode, popupTheme);
		init();
	}

	public RSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
		super(context, attrs, defStyleAttr, defStyleRes, mode);
		init();
	}

	public RSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
		super(context, attrs, defStyleAttr, mode);
		init();
	}

	public RSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public RSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RSpinner(Context context, int mode) {
		super(context, mode);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setAdapter(SpinnerAdapter adapter) {
		// TODO Auto-generated method stub
		super.setAdapter(adapter);
	}
	
}
