package pe.nammu.sampledaummap

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.daum.mf.map.api.MapView
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    val multiplePermissionsCode = 100;
    val requiredPermissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION)

    var mapViewContainer : ViewGroup? = null
    var mapView : MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //이 부분이 중요!
        //터미널에서 뽑아내는 키값과 소스로 뽑아내는것이 다름
        //카카오는 소스상에서 나오는 것을 넣어야만 동작

       // Log.e("KeyHash","" + getHashKey(this.applicationContext))

        if (Build.VERSION.SDK_INT > 23) {
            Log.e("Init", "PermissionCheck")
            permissionCheck()
        }


        onMapReady()
    }

    private fun onMapReady(){
        mapView = MapView(this)
        mapViewContainer = findViewById(R.id.map_view) as ViewGroup
        mapViewContainer?.addView(mapView)
    }

    private fun permissionCheck(){
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for(permission in requiredPermissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }

        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            Log.e("Init","Permission List")
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), multiplePermissionsCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            multiplePermissionsCode -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.e("TAG", "The user has denied to $permission")
                            Log.e("TAG", "I can't work for you anymore then. ByeBye!")
                        }
                    }
                }
            }
        }
    }

    fun getHashKey(context : Context) : String? {

        var keyHash: String? = null
        try {
            val info: PackageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                var md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                keyHash = String(Base64.encode(md.digest(), 0))
            }
        } catch (e: Exception) {
            Log.e("name not found", e.toString())
        }
        return keyHash
    }


}
