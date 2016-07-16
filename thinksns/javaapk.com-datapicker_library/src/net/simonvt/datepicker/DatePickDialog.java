package net.simonvt.datepicker;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

/*
 *
 */
public class DatePickDialog extends Dialog implements DatePicker.OnDateChangedListener {
	private static final String tag = DatePickDialog.class.getSimpleName();
	
	private Context mContext;
	private TextView tvTitle;
	private CharSequence title;
	private Calendar mCalendar;

	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";

	private int year;
	private int monthOfYear;
	private int dayOfMonth;

	public IgetDate getdate;
	DatePicker mDatePicker;

	public static String date = null;
	
	/**
	 * 
	 * @param context
	 * @param title   ����
	 * @param positiveText   ȷ��
	 * @param negativeText	 ȡ��
	 */
	public DatePickDialog(Context context,IgetDate getdate) {
		super(context);
		this.getdate = getdate;
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.time_layout);

		initView();
	}
	
	public interface IgetDate {
		 void getDate(int year, int month, int day);

	}

	private void initView() {
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(title);
		
		mCalendar = Calendar.getInstance();
		year = mCalendar.get(Calendar.YEAR);
		monthOfYear = mCalendar.get(Calendar.MONTH);
		dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

		mDatePicker = (DatePicker) findViewById(R.id.datePicker);
		mDatePicker.init(year, monthOfYear, dayOfMonth, this);
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		mDatePicker.init(year, month, day, this);
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(YEAR, mDatePicker.getYear());
		state.putInt(MONTH, mDatePicker.getMonth());
		state.putInt(DAY, mDatePicker.getDayOfMonth());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int year = savedInstanceState.getInt(YEAR);
		int month = savedInstanceState.getInt(MONTH);
		int day = savedInstanceState.getInt(DAY);
		mDatePicker.init(year, month, day, this);
	}
}
