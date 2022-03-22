package com.example.photoalbum

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photoalbum.data.AlbumRectangle

class AlbumViewModel : ViewModel() {

    var selectedIndex: Int = -1

    var gridList = MutableLiveData<List<AlbumRectangle>>()

    fun initList(){
        gridList.value = createAlbumRectangle()
    }

    private fun createAlbumRectangle(): List<AlbumRectangle> {
        val albumRectangleList = mutableListOf<AlbumRectangle>()
        for (i in 0 until 40) {
            albumRectangleList.add(AlbumRectangle(i))
        }
        return albumRectangleList
    }

    fun setBitmap(bitmap: Bitmap) {
        if(selectedIndex != -1) {
            gridList.value?.get(selectedIndex)?.bitmap = bitmap
        }
    }
}