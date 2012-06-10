package cn.kevin.seabedwallpaper;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class SeabedWall extends WallpaperService {

	public static final int DRAW = 1;
	
	@Override
	public Engine onCreateEngine() {
		// TODO Auto-generated method stub
		return new SeabedEngine();
	}
	
	public class SeabedEngine extends Engine implements OnSharedPreferenceChangeListener{
		
		private Handler mHandler = new Handler(){
			
			public void handleMessage(Message msg){
				switch(msg.what){
				case DRAW:
					drawWallpaper();
					break;				
				}
			}			
		};
		

		private String bgTypeStr = null;		
		private int screenHeight = -1;
		private int screenWidth = -1;
		private float speedRate = 0;
		private float sizeRate = 1.0f;
		private float xPos1 = 0.0f;
		private float xPos2 = 0.0f;
		private float touchPointX = -1.0f;
		private float touchPointY = -1.0f;
		private boolean touching = false;
		private Bitmap background = null;
		private Bitmap wave1 = null;
		private Bitmap wave2 = null;
		private Bitmap touchBitmap = null;
		private Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		private Paint wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		private ColorMatrix bgColormatrix;
		private ColorMatrix waveColorMatrix;
		private float[] bgColorArray = new float[20];
		private float[] waveColorArray = new float[20];
		
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			// TODO Auto-generated method stub
			super.onCreate(surfaceHolder);
			setTouchEventsEnabled(true);

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SeabedWall.this);
			prefs.registerOnSharedPreferenceChangeListener(this);
			
			this.bgColormatrix = new ColorMatrix();
			this.waveColorMatrix = new ColorMatrix();			
			String colorStr = prefs.getString("background_color", "None");
			this.setDrawingColor(colorStr);
			
			this.bgTypeStr = prefs.getString("background_type", "Texture");
			
			String touchStr = prefs.getString("touch_icon", "None");
			this.setTouchIcon(touchStr);
			
			int progress = prefs.getInt("wave_speed_progress", 70);
			this.speedRate = progress / 100.0f;			

		}

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			mHandler.removeMessages(DRAW);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SeabedWall.this);
			prefs.unregisterOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			super.onSurfaceCreated(holder);
			Canvas canvas = holder.lockCanvas();
			this.screenWidth = canvas.getWidth();
			this.screenHeight = canvas.getHeight();
			Bitmap newBackground = BitmapFactory.decodeResource(getResources(), R.drawable.background);
			this.sizeRate = this.screenWidth * 1.0f / newBackground.getWidth();
			newBackground.recycle();
			newBackground = null;
			holder.unlockCanvasAndPost(canvas);
			this.setBackgroundType();
			mHandler.sendEmptyMessage(DRAW);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			super.onSurfaceDestroyed(holder);
			mHandler.removeMessages(DRAW);
			if(this.background != null){
				this.background.recycle();
				this.background = null;
			}
		}		
		
		private void drawWallpaper(){
			SurfaceHolder holder = this.getSurfaceHolder();
			Canvas canvas = holder.lockCanvas();
			
			drawBackground(canvas);
			
			drawWave(canvas);	
			
			if(touching && touchBitmap != null){
				System.out.println("draw touch!!!");
				
				canvas.drawBitmap(touchBitmap, touchPointX, touchPointY, bgPaint);
			}
			
			holder.unlockCanvasAndPost(canvas);
			
			mHandler.removeMessages(DRAW);
			mHandler.sendEmptyMessageDelayed(DRAW, 15);
		}
		
		private void drawBackground(Canvas canvas){

			/*
			int[] colors = new int[]{Color.WHITE, Color.GREEN};
			this.bgBitmapShader = null;
			this.bgBitmapShader = new BitmapShader(this.background, Shader.TileMode.CLAMP, Shader.TileMode.MIRROR);
			this.bgLinearGradient = null;
			this.bgLinearGradient = new LinearGradient(0, 0, screenWidth, screenHeight, colors, null, TileMode.REPEAT);
			this.bgComposeShader = null;
			this.bgComposeShader = new ComposeShader(this.bgBitmapShader, this.bgLinearGradient, PorterDuff.Mode.DARKEN);
			this.bgPaint.setShader(bgComposeShader);
			
			//canvas.drawRect(0, 0, screenWidth, screenHeight, bgPaint);		
			*/
			/*
			if(this.background == null){
				if(this.bgTypeStr.equals("Texture")){					
					Bitmap newBackground = BitmapFactory.decodeResource(getResources(), R.drawable.background);
					this.background = Bitmap.createScaledBitmap(newBackground, screenWidth, screenHeight, true);						
				}else if(this.bgTypeStr.equals("Gradient")){	
					Bitmap newBackground = BitmapFactory.decodeResource(getResources(), R.drawable.solidbackground);
					this.background = Bitmap.createScaledBitmap(newBackground, screenWidth, screenHeight, true);
				}else if(this.bgTypeStr.equals("None")){
					this.background = null;
				}
			}
			*/
			
			if(this.bgTypeStr.equals("Texture")){
				canvas.drawBitmap(background, 0, 0, bgPaint);				
			}else if(this.bgTypeStr.equals("Gradient")){
				canvas.drawRect(0, 0, this.screenWidth, this.screenHeight, this.bgPaint);
			}else if(this.bgTypeStr.equals("None")){
				canvas.drawColor(Color.BLACK);
			}


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
			
			canvas.drawBitmap(wave1, xPos1, screenHeight * 0.5f, this.wavePaint);			
			xPos1 -= 10 * this.speedRate;
			canvas.drawBitmap(wave2, xPos2, screenHeight * 0.5f, this.wavePaint);
			xPos2 -= 10 * this.speedRate;

		}
		
		private void setColorValues(int r, int g, int b, float[] array){
			for(int i = 0; i < array.length; i++){
                if(i == 0 || i == 6 || i == 12 || i == 18){
                	array[i] = 1;
				}else if(i == 4){
					array[i] = r;
				}else if(i == 9){
					array[i] = g;
				}else if(i == 14){
					array[i] = b;
				}else{
					array[i] = 0;
				}
			}			
		}		

		
		/*
		private Bitmap createRGBImage(Bitmap bitmap, int color){
			int bitmapW = bitmap.getWidth();
			int bitmapH = bitmap.getHeight();
			int[] arrayColor = new int[bitmapW * bitmapH];
			int count = 0;
			for(int i = 0; i < bitmapH; i++){
				for(int j = 0; j < bitmapW; j++){
					int color1 = bitmap.getPixel(j, i);
					System.out.println("color: " + color1);
					if(color1 != 0){
						
					}else{
						color1 = color;
					}
					
					arrayColor[count] = color;
					count++;
				}
				
			}
			
			bitmap = Bitmap.createBitmap(arrayColor, bitmapW, bitmapH, Config.ARGB_4444);
			return bitmap;
		}
		*/
		
		private void setDrawingColor(String colorStr){
			if(colorStr.equals("Red")){
				this.setColorValues(0, -150, -100, this.bgColorArray);
				this.setColorValues(0, 0, -150, this.waveColorArray);
				
			}else if(colorStr.equals("Blue")){
				this.setColorValues(-200, -150, 0, this.bgColorArray);
				this.setColorValues(188, 0, -100, this.waveColorArray);

			}else if(colorStr.equals("Green")){
				this.setColorValues(-150, 0, -143, this.bgColorArray);
				this.setColorValues(-100, -100, 0, this.waveColorArray);

			}else if(colorStr.equals("None")){
				this.setColorValues(0, 0, 0, this.bgColorArray);
				this.setColorValues(0, 0, 0, this.waveColorArray);
			}
			this.bgColormatrix.set(this.bgColorArray);
			this.waveColorMatrix.set(this.waveColorArray);
			this.wavePaint.setColorFilter(new ColorMatrixColorFilter(this.waveColorMatrix));
			this.bgPaint.setColorFilter(new ColorMatrixColorFilter(this.bgColormatrix));
		}
		
		private void setTouchIcon(String touchStr){
			if(this.touchBitmap != null){
				this.touchBitmap.recycle();
				this.touchBitmap = null;						
			}
			if(touchStr.equals("Circle")){
				Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.touch_circle);
				this.touchBitmap = Bitmap.createScaledBitmap(tempBitmap, tempBitmap.getWidth() * 2 / 3, tempBitmap.getHeight() * 2 / 3, true);
				
			}else if(touchStr.equals("Heart")){
				Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.touch_heart);
				this.touchBitmap = Bitmap.createScaledBitmap(tempBitmap, tempBitmap.getWidth() * 2 / 3, tempBitmap.getHeight() * 2 / 3, true);
			
			}else if(touchStr.equals("None")){
				if(this.touchBitmap != null){
					this.touchBitmap.recycle();
					this.touchBitmap = null;
				}
			}
		}
		
		private void setBackgroundType(){
			if(this.background != null){
				this.background.recycle();
				this.background = null;
			}
			if(this.bgTypeStr.equals("Texture")){					
				Bitmap newBackground = BitmapFactory.decodeResource(getResources(), R.drawable.background);
				this.sizeRate = this.screenWidth * 1.0f / newBackground.getWidth();
				this.background = Bitmap.createScaledBitmap(newBackground, screenWidth, screenHeight, true);
						
			}else if(this.bgTypeStr.equals("Gradient")){	
				Bitmap newBackground = BitmapFactory.decodeResource(getResources(), R.drawable.solidbackground);
				this.sizeRate = this.screenWidth * 1.0f / newBackground.getWidth();
				this.background = Bitmap.createScaledBitmap(newBackground, screenWidth, screenHeight, true);
				
				int[] colors = new int[]{Color.WHITE, this.bgPaint.getColor()};
				BitmapShader bitmapShader = new BitmapShader(this.background, TileMode.REPEAT, TileMode.MIRROR);				
				LinearGradient bgLinearGradient = new LinearGradient(0, 0, screenWidth, screenHeight, colors, null, TileMode.REPEAT);
				ComposeShader bgComposeShader = new ComposeShader(bitmapShader, bgLinearGradient, PorterDuff.Mode.DARKEN);
				this.bgPaint.setShader(bgComposeShader);
				
			}else if(this.bgTypeStr.equals("None")){
				if(this.background != null){
					this.background.recycle();
					this.background = null;
				}
			}
		}

		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			// TODO Auto-generated method stub
			if(key.equals("background_color")){
				String colorStr = sharedPreferences.getString(key, "None");
				this.setDrawingColor(colorStr);
				
			}else if(key.equals("background_type")){
				this.bgTypeStr = sharedPreferences.getString(key, "Texture");
				this.setBackgroundType();
				
			}else if(key.equals("touch_icon")){
				String touchStr =  sharedPreferences.getString(key, "None");
				this.setTouchIcon(touchStr);
				
			}else if(key.equals("wave_speed_progress")){
				int progress = sharedPreferences.getInt(key, 70);
				this.speedRate = progress / 100.0f;
			}
		}		
		

		@Override
		public void onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			super.onTouchEvent(event);
			
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				System.out.println("down");
				
				touching = true;
				this.touchPointX = event.getX() - 20;
				this.touchPointY = event.getY() - 20;
			}
			
			if(event.getAction() == MotionEvent.ACTION_MOVE){
				System.out.println("move");
				
				this.touchPointX = event.getX() - 20;
				this.touchPointY = event.getY() - 20;				

			}
			
			if(event.getAction() == MotionEvent.ACTION_UP){
				System.out.println("up");
				
				touching = false;
			}
		}
		
	}

}
