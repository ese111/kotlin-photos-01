package com.example.photoalbum

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.scale
import androidx.databinding.DataBindingUtil
import com.example.photoalbum.databinding.ActivityAlbumOpenBinding
import com.google.android.material.snackbar.Snackbar

class AlbumOpenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumOpenBinding
    private lateinit var getAlbumContent: ActivityResultLauncher<Intent>

    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1004

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_album_open)

//        getAlbumContent =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                if (it.resultCode == RESULT_OK) {
//                    Log.d("AppTest", "RectangleActivity/ data : ${it.data?.data}")
//                    try {
//
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                } else {
//                    Snackbar.make(binding.root, "사진 불러오기 취소", Snackbar.LENGTH_SHORT).show()
//                }
//            }

        setAlbumOpenBtn()
    }

    private fun setAlbumOpenBtn() {
        binding.btnOpenAlbum.setOnClickListener {

            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_READ_EXTERNAL_STORAGE)

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_READ_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("AppTest", "권한 승인")
                    Snackbar.make(binding.root, "권한이 승인되었습니다", Snackbar.LENGTH_SHORT).show()
                    val images = imagesLoad()
                    val bitmaps = mutableListOf<Bitmap>()
                    images.forEach { image -> bitmaps.add(uriToBitmap(image)) }
                    bitmaps.forEach { bitmap ->  bitmap.scale(100, 100) }
                    Log.d("AppTest", bitmaps.toString())

                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        Log.d("AppTest", "권한 최초 거절")
                        Snackbar.make(binding.root, "권한이 승인되지 않았습니다", Snackbar.LENGTH_SHORT).show()

                    } else {
                        Log.d("AppTest", "두 번째 거절")
                        Snackbar.make(binding.root, "'설정'에서 직접 권한 승인이 필요합니다", Snackbar.LENGTH_SHORT).show()

                        // '설정' 화면으로 이동하기
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", packageName, null))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun imagesLoad() : List<String> {
        val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val uriArr = ArrayList<String>()

        if(cursor != null){
            Log.d("AppTest", "check")
            while (cursor.moveToNext()){
                val uri = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                uriArr.add(uri)
            }
            Log.d("AppTest", "uriArr : $uriArr")
            cursor.close()
            return uriArr
        }else{
            Log.d("AppTest", "cursor null")
        }
        return uriArr
    }

    private fun uriToBitmap(image: String) : Bitmap {
        return BitmapFactory.decodeFile(image)
    }
}