package cn.kevin.seabedwallpaper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class SeabedWall extends WallpaperService {

	public static final int DRAW = 1;
	
	@Override
	public Engine onCreateEngine() {
		// TODO Auto-generated method stub
		return new SeabedEngine();
	}
	
	public class SeabedEngine extends Engine{
		
		private Handler mHandler = new Handler(){
			
			public void handleMessage(Message msg){
				switch(msg.what){
				case DRAW:
					drawWallpaper();
					break;				
				}
			}
			
		};
		
		private int screenHeight = -1;
		private int screenWidth = -1;		
		private float sizeRate = 1.0f;
		private float xPos1 = 0.0f;
		private float xPos2 = 0.0f;
		private Bitmap background = null;
		private Bitmap wave1 = null;
		private Bitmap wave2 = null;
		private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			// TODO Auto-generated method stub
			super.onCreate(surfaceHolder);
			setTouchEventsEnabled(true);
		}

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			mHandler.removeMessages(DRAW);
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			super.onSurfaceCreated(holder);

			
			mHandler.sendEmptyMessage(DRAW);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			super.onSurfaceDestroyed(holder);
			mHandler.removeMessages(DRAW);
		}		
		
		private void drawWallpaper(){
			SurfaceHolder holder = this.getSurfaceHolder();
			Canvas canvas = holder.lockCanvas();
			if(this.screenWidth < 0){
				this.screenWidth = canvas.getWidth();
			}
			if(this.screenHeight < 0){
				this.screenHeight = canvas.getHeight();
			}
			drawBackground(canvas);
			drawWave(canvas);
			holder.unlockCanvasAndPost(canvas);
			
			mHandler.removeMessages(DRAW);
			mHandler.sendEmptyMessageDelayed(DRAW, 20);
		}
		
		private void drawBackground(Canvas canvas){
			if(this.background == null){
				Bitmap newBackground = BitmapFactory.decodeResource(getResources(), R.drawable.background);
				this.sizeRate = this.screenWidth * 1.0f / newBackground.getWidth();
				this.background = Bitmap.createScaledBitmap(newBackground, screenWidth, screenHeight, true);
			}				
			
			canvas.drawBitmap(background, 0, 0, this.mPaint);
			canvas.drawARGB(128, 90, 100, 110);
		}
		
		private void drawWave(Canvas canvas){
			if(this.wave1 == null){
				Bitmap newWave = BitmapFactory.decodeResource(getResources(), R.drawable.wavesmall);				
				int width = (int)(newWave.getWidth() * this.sizeRate);
				int height = (int)(newWave.getHeight() * this.sizeRate);
				this.wave1 = Bitmap.createScaledBitmap(newWave, width, height, true);
				
				xPos1 = 0.0f;
				xPos2 = this.wave1.getWidth();				
				
				//System.out.println("origin width: " + newWave.getWidth() + ", new width: " + wave1.getWidth());
			}
			if(this.wave2 == null){
				Bitmap newWave = BitmapFactory.decodeResource(getResources(), R.drawable.wavesmall);
				int width = (int)(newWave.getWidth() * this.sizeRate);
				int height = (int)(newWave.getHeight() * this.sizeRate);
				this.wave2 = Bitmap.createScaledBitmap(newWave, width, height, true);
			}
		
			if(xPos1 <= -this.wave1.getWidth()){
				xPos1 = this.wave2.getWidth();
			}
			if(xPos2 <= -this.wave2.getWidth()){
				xPos2 = this.wave1.getWidth();
			}			
			canvas.drawBitmap(wave1, xPos1, screenHeight * 0.5f, this.mPaint);
			xPos1 -= 8;
			canvas.drawBitmap(wave2, xPos2, screenHeight * 0.5f, this.mPaint);
			xPos2 -= 8;

		}
		
	}

}
