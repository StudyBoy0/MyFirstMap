package com.example.mymap.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;

import android.graphics.Color;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.example.mymap.db.LocationDBHelper;
import com.example.mymap.callback.CallBack;
import com.example.mymap.entity.LineDataEntity;
import com.example.mymap.entity.GPSEntity;
import com.example.mymap.network.NetWork;
import com.example.mymap.R;
import com.example.mymap.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {

    private MapView mapView = null;
    private AMap aMap;
    private AMapLocationClient locationClient;
    private ImageButton button01, button02, button03, button04;
    private Button StandardMap, SatelliteMap, drawLine;
    private Polyline mPolyline;
    private CameraUpdate cameraUpdate;

    private LocationDBHelper locationDBHelper;
    private LineDataDialog lineDataDialog;

    double[] coords = {113.640205, 24.640474, 113.6400983208499, 24.640619226477376,
            113.6500983208499, 24.650619226477376, 113.6600983208499, 24.660619226477376,
            113.6700983208499, 24.670619226477376, 113.6800983208499, 24.680619226477376,
    };
    TextView tv;

    public NetWork network = new NetWork(new CallBack<GPSEntity>() {
        @Override
        public void success(GPSEntity result) {
            if (SystemUtils.isHostValidate(MainActivity.this)) {
                return;
            }
            showToast(result.toString());
            //获取经纬度数据
            String lat = result.getLat();
            String lng = result.getLng();
            LatLng latLng = new LatLng(Double.parseDouble(lng), Double.parseDouble(lat));

            Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.p1)));
            cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 17, 0, 30));
            aMap.moveCamera(cameraUpdate);//地图移向指定区域
        }

        @Override
        public void failed(String errorMsg) {
            if (SystemUtils.isHostValidate(MainActivity.this)) {
                return;
            }
            showToast(errorMsg);
        }
    });

    public void showToast(String content) {
        if (!TextUtils.isEmpty(content)) {
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLocation();
        button01 = (ImageButton) findViewById(R.id.imgButton01);
        button02 = (ImageButton) findViewById(R.id.imgButton02);
        button03 = (ImageButton) findViewById(R.id.imgButton03);
        button04 = (ImageButton) findViewById(R.id.imgButton04);
        StandardMap = (Button) findViewById(R.id.StandardMap);
        SatelliteMap = (Button) findViewById(R.id.SatelliteMap);
        drawLine = (Button) findViewById(R.id.drawLine);
        mapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        //设置当前定位蓝点
        locationStyle();

        closeControl();

        requestLocation();

        setOnClick();

        lineDataDialog = new LineDataDialog(new CallBack<List<LatLng>>() {
            @Override
            public void success(List<LatLng> result) {
                setLine(result);
            }

            @Override
            public void failed(String errorMsg) { }
        });

        locationDBHelper = new LocationDBHelper(getApplicationContext(), new CallBack<List<LineDataEntity>>() {
            @Override
            public void success(List<LineDataEntity> result) {
                if (SystemUtils.isHostValidate(MainActivity.this)) {
                    return;
                }
                showDateLineList(result);
            }

            @Override
            public void failed(String errorMsg) {
                if (SystemUtils.isHostValidate(MainActivity.this)) {
                    return;
                }
                showToast(errorMsg);
            }
        });
        locationDBHelper.init();
    }

    /**
     * 显示打点历史列表
     * @param result
     */
    private void showDateLineList(List<LineDataEntity> result) {
        if (lineDataDialog != null) {
            lineDataDialog.showLineDataDialog(this, result);
        }
    }


    //注册监听器
    private void setOnClick() {
        button01.setOnClickListener(this);
        button02.setOnClickListener(this);
        button03.setOnClickListener(this);
        button04.setOnClickListener(this);
        StandardMap.setOnClickListener(this);
        SatelliteMap.setOnClickListener(this);
        drawLine.setOnClickListener(this);
    }

    //设置当前定位蓝点
    private void locationStyle() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.p2));
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    //隐藏缩放按钮
    private void closeControl() {
        aMap.getUiSettings().setZoomControlsEnabled(false);
    }

    //初始化定位
    private void initLocation() {
        locationClient = new AMapLocationClient(getApplicationContext());
        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if(aMapLocation!=null && aMapLocation.getErrorCode() == 0){
                    if (locationDBHelper != null) {
                        locationDBHelper.saveLatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    }
                }
            }
        });
    }

    //绘制轨迹线
    private void setLine(List<LatLng> list) {
        addPolylineInPlayGround(list);
    }

    /**
     * 添加轨迹线
     */
    private void addPolylineInPlayGround(List<LatLng> list) {
        List<Integer> colorList = new ArrayList<Integer>();
        List<BitmapDescriptor> bitmapDescriptors = new ArrayList<BitmapDescriptor>();

        int[] colors = new int[]{Color.argb(255, 0, 255, 0), Color.argb(255, 255, 255, 0), Color.argb(255, 255, 0, 0)};

        //用一个数组来存放纹理
        List<BitmapDescriptor> textureList = new ArrayList<BitmapDescriptor>();
        textureList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture));

        List<Integer> texIndexList = new ArrayList<Integer>();
        texIndexList.add(0);//对应上面的第0个纹理
        texIndexList.add(1);
        texIndexList.add(2);

        Random random = new Random();
        for (int i = 0; i < list.size(); i++) {
            colorList.add(colors[random.nextInt(3)]);
            bitmapDescriptors.add(textureList.get(0));

        }

        mPolyline = aMap.addPolyline(new PolylineOptions().setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.custtexture))
                .addAll(list)
                .useGradient(true)
                .width(18));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(list.get(0));
        builder.include(list.get(list.size() - 2));

        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }

    @Override
    protected void onStart() {
        super.onStart();
        //申请对应权限
        requestMyPermission();
    }

    //权限的申请
    private void requestMyPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.INTERNET};
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "使用本地图前请同意权限的申请！", 1, permissions);
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, 1, permissions).setRationale("使用本地图前请同意权限的申请！").setPositiveButtonText("同意").setNegativeButtonText("拒绝").build());
        }
    }

    private void requestLocation() {
        locationClient.setLocationOption(getDefaultOption());
        locationClient.startLocation();
    }

    /**
     * 默认的定位参数
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(5 * 1000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    private void changeCamera(CameraUpdate update, AMap.CancelableCallback callback) {
        aMap.animateCamera(update);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
        //清空回调接口
        network.release();

        locationClient.stopLocation();
        locationClient.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    //在此情况下向用户显示对话框，并将其引导到应用程序的系统设置屏幕
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (!EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    //事件监听
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgButton01:
                //改变地图的缩放级别，实现缩放回中
                aMap.moveCamera(CameraUpdateFactory.zoomTo(12));
                break;
            case R.id.imgButton02:
                //请求网络
                network.request();
                break;
            case R.id.imgButton03:
                changeCamera(CameraUpdateFactory.zoomOut(), null);
                break;
            case R.id.imgButton04:
                changeCamera(CameraUpdateFactory.zoomIn(), null);
                break;
            case R.id.StandardMap:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                break;
            case R.id.SatelliteMap:
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.drawLine:
                //查询获取存储的经纬度
                locationDBHelper.searchLocation();
                break;
        }
    }
}