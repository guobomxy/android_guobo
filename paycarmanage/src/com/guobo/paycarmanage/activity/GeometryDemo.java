package com.guobo.paycarmanage.activity;


import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.Symbol.Stroke;
import com.baidu.mapapi.map.TextItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.guobo.paycarmanage.R;
import com.guobo.paycarmanage.adapter.YundanListAdapter;
import com.guobo.paycarmanage.beans.YunDanItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


/**
 * 此demo用来展示如何在地图上用GraphicsOverlay添加点、线、多边形、圆
 * 同时展示如何在地图上用TextOverlay添加文字
 *
 */

public class GeometryDemo extends Activity{

	//地图相关
	MapView mMapView = null;
	
	//UI相关
	Button resetBtn = null;
	Button clearBtn = null;
	
	SlidingMenu menu;
	
	TextView yundanT;
	TextView sijiT;
	ListView yundanlist;
	ListView sijilist;
	List<YunDanItem> dataMap = new ArrayList<YunDanItem>();
	//YundanListAdapter yundanAdapter;
	YundanListAdapter yundanAdapter;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_geometry);
        

        //初始化滑动菜单
        menu = new SlidingMenu(this);
		// 设置滑动方向
		menu.setMode(SlidingMenu.RIGHT);
		// 设置监听开始滑动的触碰范围
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		// 设置边缘阴影的宽度，通过dimens资源文件中的ID设置
		menu.setShadowWidthRes(R.dimen.shadow_width);
		// 设置边缘阴影的颜色/图片，通过资源文件ID设置
		//menu.setShadowDrawable(R.drawable.shadow);
		// 设置menu全部打开后，主界面剩余部分与屏幕边界的距离，通过dimens资源文件ID设置
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置是否淡入淡出
		menu.setFadeEnabled(true);
		// 设置淡入淡出的值，只在setFadeEnabled设置为true时有效
		menu.setFadeDegree(0.35f);
		// 将menu绑定到Activity，同时设置绑定类型
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		// 设置menu的layout
		menu.setMenu(R.layout.slide_menu);
		// 设置menu的背景
		// menu.setBackgroundColor(getResources().getColor(
		// android.R.color.background_dark));
		// 获取menu的layout
		View menuroot = menu.getMenu();

        
        
        
//        CharSequence titleLable="自定义绘制功能";
//        setTitle(titleLable);
        
        //初始化地图
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapView.getController().setZoom(12.5f);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);  
       //界面加载时添加绘制图层  
       addCustomElementsDemo();
       
       yundanT = (TextView) findViewById(R.id.yundanlistT_slide);
       yundanT.setOnTouchListener(onTouchListener);
       sijiT = (TextView) findViewById(R.id.sijilistT_slide);
       sijiT.setOnTouchListener(onTouchListener);
       yundanlist = (ListView) findViewById(R.id.yundanlist_slide);
       //yundanlist.setVisibility(View.GONE);
       sijilist = (ListView) findViewById(R.id.sijilist_slide);
       sijilist.setVisibility(View.GONE);
       
       for(int i = 0;i<200;i++){
    	   YunDanItem y = new YunDanItem();
    	   y.setNumber(i+"345678432567");
    	   y.setName(i+"woshi");
    	   y.setCreateTime("2013-11-10 11:11:11");
    	   y.setIssucc(i%3);
    	   
    	   dataMap.add(y);
       }
       
    }
    

    
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(v.getId()){
			
			case R.id.yundanlistT_slide:
				
				
				if(sijilist.getVisibility() == View.VISIBLE){
					sijilist.setVisibility(View.GONE);
				}
				if(yundanlist.getVisibility() == View.GONE){
					
					yundanlist.setVisibility(View.VISIBLE);
					AnimationSet animationSet=new AnimationSet(true); 
					ScaleAnimation scaleAnimation=new ScaleAnimation(
							1, 1, 0.1f, 1,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0); 
					scaleAnimation.setDuration(1000); 
					animationSet.addAnimation(scaleAnimation); 
					yundanlist.startAnimation(scaleAnimation); 
					
					//yundanAdapter = new YundanListAdapter(GeometryDemo.this, dataMap);
					yundanAdapter = new YundanListAdapter(GeometryDemo.this, dataMap);
					yundanlist.setAdapter(yundanAdapter);
				}else{
					yundanlist.setVisibility(View.GONE);
				}
				//menu.toggle();
				break;
			case R.id.sijilistT_slide:
				if(yundanlist.getVisibility() == View.VISIBLE){
					yundanlist.setVisibility(View.GONE);
				}
				if(sijilist.getVisibility() == View.GONE){
					
					sijilist.setVisibility(View.VISIBLE);
					AnimationSet animationSet=new AnimationSet(true); 
					ScaleAnimation scaleAnimation=new ScaleAnimation(
							1, 1, 0.1f, 1,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0); 
					scaleAnimation.setDuration(1000); 
					animationSet.addAnimation(scaleAnimation); 
					sijilist.startAnimation(scaleAnimation); 
					
//					yundanAdapter = new YundanListAdapter(GeometryDemo.this, dataMap);
//					yundanlist.setAdapter(yundanAdapter);
				}else{
					sijilist.setVisibility(View.GONE);
				}
				break;
			}
			return false;
		}
	};
    

    /**
     * 添加点、线、多边形、圆、文字
     */
    public void addCustomElementsDemo(){
    	GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
        mMapView.getOverlays().add(graphicsOverlay);
    	//添加点
        //graphicsOverlay.setData(drawPoint());
    	//添加折线
        graphicsOverlay.setData(drawLine());
    	//添加多边形
        //graphicsOverlay.setData(drawPolygon());
    	//添加圆
        //graphicsOverlay.setData(drawCircle());
    	//绘制文字
        //TextOverlay textOverlay = new TextOverlay(mMapView);
        //mMapView.getOverlays().add(textOverlay);
        //textOverlay.addText(drawText());
        //执行地图刷新使生效
        mMapView.refresh();
    }
    
    public void resetClick(){
    	//添加绘制元素
    	addCustomElementsDemo();
    }
   
    public void clearClick(){
    	//清除所有图层
    	mMapView.getOverlays().clear();
    }
    /**
     * 绘制折线，该折线状态随地图状态变化
     * @return 折线对象
     */
    public Graphic drawLine(){
    	double mLat = 39.97923;
       	double mLon = 116.357428;
       	
    	int lat = (int) (mLat*1E6);
	   	int lon = (int) (mLon*1E6);   	
	   	GeoPoint pt1 = new GeoPoint(lat, lon);
	   
	   	mLat = 39.94923;
       	mLon = 116.397428;
    	lat = (int) (mLat*1E6);
	   	lon = (int) (mLon*1E6);
	   	GeoPoint pt2 = new GeoPoint(lat, lon);
//	   	mLat = 39.97923;
//       	mLon = 116.437428;
       	mLat = 30.3248197735;
       	mLon = 120.27763510845;
		lat = (int) (mLat*1E6);
	   	lon = (int) (mLon*1E6);
	    GeoPoint pt3 = new GeoPoint(lat, lon);
	  
	    //构建线
  		Geometry lineGeometry = new Geometry();
  		//设定折线点坐标
  		GeoPoint[] linePoints = new GeoPoint[3];
  		linePoints[0] = pt1;
  		linePoints[1] = pt2;
  		linePoints[2] = pt3; 
  		lineGeometry.setPolyLine(linePoints);
  		//设定样式
  		Symbol lineSymbol = new Symbol();
  		Symbol.Color lineColor = lineSymbol.new Color();
  		lineColor.red = 25;
  		lineColor.green = 25;
  		lineColor.blue = 122;
  		lineColor.alpha = 255;
  		lineSymbol.setLineSymbol(lineColor, 5);
  		//生成Graphic对象
  		Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
  		return lineGraphic;
    }
   /**
    * 绘制多边形，该多边形随地图状态变化
    * @return 多边形对象
    */
    public Graphic drawPolygon(){
    	double mLat = 39.93923;
       	double mLon = 116.357428;
    	int lat = (int) (mLat*1E6);
	   	int lon = (int) (mLon*1E6);   	
	   	GeoPoint pt1 = new GeoPoint(lat, lon);
	   	mLat = 39.91923;
       	mLon = 116.327428;
		lat = (int) (mLat*1E6);
	   	lon = (int) (mLon*1E6);
	    GeoPoint pt2 = new GeoPoint(lat, lon);
	    mLat = 39.89923;
       	mLon = 116.347428;
		lat = (int) (mLat*1E6);
	   	lon = (int) (mLon*1E6);
	    GeoPoint pt3 = new GeoPoint(lat, lon);
	    mLat = 39.89923;
       	mLon = 116.367428;
		lat = (int) (mLat*1E6);
	   	lon = (int) (mLon*1E6);
	    GeoPoint pt4 = new GeoPoint(lat, lon);
	    mLat = 39.91923;
       	mLon = 116.387428;
		lat = (int) (mLat*1E6);
	   	lon = (int) (mLon*1E6);
	    GeoPoint pt5 = new GeoPoint(lat, lon);
	    
	    //构建多边形
  		Geometry polygonGeometry = new Geometry();
  		//设置多边形坐标
  		GeoPoint[] polygonPoints = new GeoPoint[5];
  		polygonPoints[0] = pt1;
  		polygonPoints[1] = pt2;
  		polygonPoints[2] = pt3; 
  		polygonPoints[3] = pt4; 
  		polygonPoints[4] = pt5; 
  		polygonGeometry.setPolygon(polygonPoints);
  		//设置多边形样式
  		Symbol polygonSymbol = new Symbol();
 		Symbol.Color polygonColor = polygonSymbol.new Color();
 		polygonColor.red = 0;
 		polygonColor.green = 0;
 		polygonColor.blue = 255;
 		polygonColor.alpha = 126;
 		polygonSymbol.setSurface(polygonColor,1,5);
  		//生成Graphic对象
  		Graphic polygonGraphic = new Graphic(polygonGeometry, polygonSymbol);
  		return polygonGraphic;
    }
    /**
     * 绘制单点，该点状态不随地图状态变化而变化
     * @return 点对象
     */
    public Graphic drawPoint(){
       	double mLat = 39.98923;
       	double mLon = 116.397428;
    	int lat = (int) (mLat*1E6);
	   	int lon = (int) (mLon*1E6);   	
	   	GeoPoint pt1 = new GeoPoint(lat, lon);
	   	
	   	//构建点
  		Geometry pointGeometry = new Geometry();
  		//设置坐标
  		pointGeometry.setPoint(pt1, 10);
  		//设定样式
  		Symbol pointSymbol = new Symbol();
 		Symbol.Color pointColor = pointSymbol.new Color();
 		pointColor.red = 0;
 		pointColor.green = 126;
 		pointColor.blue = 255;
 		pointColor.alpha = 255;
 		pointSymbol.setPointSymbol(pointColor);
  		//生成Graphic对象
  		Graphic pointGraphic = new Graphic(pointGeometry, pointSymbol);
  		return pointGraphic;
    }
    /**
     * 绘制圆，该圆随地图状态变化
     * @return 圆对象
     */
    public Graphic drawCircle() {
    	double mLat = 39.90923; 
       	double mLon = 116.447428; 
    	int lat = (int) (mLat*1E6);
	   	int lon = (int) (mLon*1E6);   	
	   	GeoPoint pt1 = new GeoPoint(lat, lon);
	   	
	   	//构建圆
  		Geometry circleGeometry = new Geometry();
  	
  		//设置圆中心点坐标和半径
  		circleGeometry.setCircle(pt1, 2500);
  		//设置样式
  		Symbol circleSymbol = new Symbol();
 		Symbol.Color circleColor = circleSymbol.new Color();
 		circleColor.red = 0;
 		circleColor.green = 255;
 		circleColor.blue = 0;
 		circleColor.alpha = 126;
  		circleSymbol.setSurface(circleColor,1,3, new Stroke(3, circleSymbol.new Color(0xFFFF0000)));
  		//生成Graphic对象
  		Graphic circleGraphic = new Graphic(circleGeometry, circleSymbol);
  		return circleGraphic;
   }
    /**
     * 绘制文字，该文字随地图变化有透视效果
     * @return 文字对象
     */
    public TextItem drawText(){
       	double mLat = 39.86923;
       	double mLon = 116.397428;
    	int lat = (int) (mLat*1E6);
	   	int lon = (int) (mLon*1E6);   	
	   	//构建文字
	   	TextItem item = new TextItem();
    	//设置文字位置
    	item.pt = new GeoPoint(lat,lon);
    	//设置文件内容
    	item.text = "百度地图SDK";
    	//设文字大小
    	item.fontSize = 40;
    	Symbol symbol = new Symbol();
    	Symbol.Color bgColor = symbol.new Color();
    	//设置文字背景色
    	bgColor.red = 0;
    	bgColor.blue = 0;
    	bgColor.green = 255;
    	bgColor.alpha = 50;
    	
    	Symbol.Color fontColor = symbol.new Color();
    	//设置文字着色
    	fontColor.alpha = 255;
    	fontColor.red = 0;
    	fontColor.green = 0;
    	fontColor.blue  = 255;
    	//设置对齐方式
    	item.align = TextItem.ALIGN_CENTER;
    	//设置文字颜色和背景颜色
    	item.fontColor = fontColor;
    	item.bgColor  = bgColor ; 
    	return item;
    }
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        mMapView.destroy();
        super.onDestroy();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU){
			menu.toggle();
		}
		return super.onKeyDown(keyCode, event);
	}
   
}


/*        //UI初始化
clearBtn = (Button)findViewById(R.id.button1);
resetBtn = (Button)findViewById(R.id.button2);
//暂时隐掉按钮
clearBtn.setVisibility(View.GONE);
resetBtn.setVisibility(View.VISIBLE);
OnClickListener clearListener = new OnClickListener(){
    public void onClick(View v){
        clearClick();
    }
};
OnClickListener restListener = new OnClickListener(){
    public void onClick(View v){
        resetClick();
    }
};

clearBtn.setOnClickListener(clearListener);
resetBtn.setOnClickListener(restListener);*/

//yundanlist.setAlpha(0f);
//yundanlist.setVisibility(View.VISIBLE);
//yundanlist.animate().alpha(1).setDuration(5000).setListener(null);
//
