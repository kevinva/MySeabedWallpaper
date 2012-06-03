package cn.kevin.seabedwallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarPreference extends DialogPreference implements
		OnSeekBarChangeListener {
	
	private SeekBar seekBar;
	private TextView textView2;
	private int theProgress;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	protected void onBindDialogView(View view){
		super.onBindDialogView(view);
		
		System.out.println("onBindDialogView");
		
		this.seekBar = (SeekBar)view.findViewById(R.id.seekBar1);
		this.textView2 = (TextView)view.findViewById(R.id.textView2);
		this.seekBar.setOnSeekBarChangeListener(this);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
		int progress = prefs.getInt("wave_speed_progress", 70);
		this.textView2.setText("" + progress + "%");
		this.seekBar.setProgress(progress);
	}
	
	
	protected void onDialogClosed(boolean positiveResult){
		if(positiveResult){
			System.out.println("yes");
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt("wave_speed_progress", this.theProgress);
			editor.commit();
		}else{
			System.out.println("no");
		}
	}
	

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		this.textView2.setText("" + progress + "%");
		this.theProgress = progress;
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

}
