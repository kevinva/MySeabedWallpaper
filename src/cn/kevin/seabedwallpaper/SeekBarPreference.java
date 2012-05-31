package cn.kevin.seabedwallpaper;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarPreference extends DialogPreference implements
		OnSeekBarChangeListener {
	
	private SeekBar seekBar;
	private TextView textView2;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	protected void onBindDialogView(View view){
		super.onBindDialogView(view);
		
		this.seekBar = (SeekBar)view.findViewById(R.id.seekBar1);
		this.textView2 = (TextView)view.findViewById(R.id.textView2);
		this.seekBar.setOnSeekBarChangeListener(this);
	}
	
	
	protected void onDialogClosed(boolean positiveResult){
		if(positiveResult){
			
		}else{
			
		}
	}
	

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		this.textView2.setText("" + progress + "%");
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

}
