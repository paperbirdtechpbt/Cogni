package com.pbt.cogni.viewModel

import android.Manifest
import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.firebase.client.ChildEventListener
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.pbt.cogni.R
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.chat.adapter.ChatAdapter
import com.pbt.cogni.activity.chat.upload.ProgressRequestBody
import com.pbt.cogni.callback.PermissionCallBack
import com.pbt.cogni.model.Chat
import com.pbt.cogni.util.AppConstant.Companion.MESSAGES
import com.pbt.cogni.util.AppConstant.Companion.TYPING
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.Config.BASE_FIREBASE_URL
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ChatViewModel(app: Application) : AndroidViewModel(app), ProgressRequestBody.UploadCallbacks{

    val context = app

    var data = MutableLiveData<ArrayList<Chat>>(ArrayList<Chat>())
    var reffChatRoomID: DatabaseReference? = null
    var typingReference: Firebase? = null
    var listener: ValueEventListener? = null
    var chatReff: Firebase? = null
    var message: ObservableField<String>? = null
    var chatRoomID: ObservableField<String>? = null
    var room: ObservableField<String>? = null
    var room1: ObservableField<String>? = null
    var userId: ObservableField<Int>? = null
    var chatID: ObservableField<Int>? = null
    var reciverId: ObservableField<Int>? = null
    var currentUser: ObservableField<String>? = null
    var mAdapter: ChatAdapter? = null
    var isTyping: ObservableField<String>? = null
    var reciverName: ObservableField<String>? = null
    var permissionIsGranted: PermissionCallBack? = null
    var selectedImage: ObservableField<String>? = null
    var imageUri: ObservableField<Uri>? = null
    var isVisiBled: ObservableField<Boolean> ? = null

    init {
        reciverName = ObservableField("")
        message = ObservableField("")
        currentUser = ObservableField("")
        chatID = ObservableField(0)
        userId = ObservableField(0)
        reciverId = ObservableField(0)
        isVisiBled = ObservableField(false)
        isTyping = ObservableField("")
        chatRoomID = ObservableField("")
        room = ObservableField("")
        room1 = ObservableField("")
        data = MutableLiveData<ArrayList<Chat>>(ArrayList<Chat>())
        selectedImage = ObservableField(context.resources.getString(R.string.choose_image))
        imageUri = ObservableField()

    }

    fun onMesageTextChanged(s: CharSequence, start: Int, befor: Int, count: Int) {

        if (s.length > 0) {
            var type = object {
                var id = userId!!.get()!!.toInt()
                var isTyping = s.toString()
            }
            FirebaseDatabase.getInstance().getReference(MESSAGES)
                .child(chatRoomID!!.get().toString())
                .child(TYPING).child(currentUser!!.get().toString()).setValue(type)
        }
    }

    fun initChat(context: Context, reciverID: Int, userID: Int,userName : String) {

        userId!!.set(userID)
        reciverName!!.set(userName)
        reciverId!!.set(reciverID)
        Firebase.setAndroidContext(context)

        room!!.set(userId!!.get().toString() + "_" + reciverId!!.get().toString())
        room1!!.set(reciverId!!.get().toString() + "_" + userId!!.get().toString())

        reffChatRoomID = FirebaseDatabase.getInstance().getReference();

        setData(context)
    }

    fun captureImage(view: View) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        )
            permissionIsGranted!!.isGranted(false)
        else
            permissionIsGranted!!.isGranted(true)
    }

//    fun getImageUri(context: Context, bitmap: Bitmap?): Uri? {
////        val bytes = ByteArrayOutputStream()
////        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
////        val path: String = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null)
////        var uri: Uri = Uri.parse(path)
////        val fileName = "unknown"
////        if (uri.getScheme().toString().compareTo("content") === 0) {
////            val cursor = context.contentResolver.query(uri, null, null, null, null)
////            if (cursor!!.moveToFirst()) {
////                val column_index =
////                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) //Instead of "MediaStore.Images.Media.DATA" can be used "_data"
////                var filePathUri = android.net.Uri.parse(cursor.getString(column_index))
////                selectedImage?.set(filePathUri.getLastPathSegment().toString())
////            }
////
////        } else if (uri.getScheme()?.compareTo("file") === 0) {
////            selectedImage?.set(uri.getLastPathSegment().toString())
////        } else {
////            selectedImage?.set(fileName.toString() + "_" + uri.getLastPathSegment())
////        }
////        var uri: Uri? = AppUtils.bitmapToUriConverter(bitmap,context)
////        imageUri?.set(uri)
////        return uri;
//    }

    fun setData(context: Context) {

        listener = reffChatRoomID?.child(MESSAGES)?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {

                    AppUtils.logDebug(TAG, "ChatRoom ID Chage : " + snapshot.getValue())

                    val map = snapshot!!.value as Map<*, *>
                    if (map.containsKey(room!!.get().toString())) {
                        chatRoomID!!.set(room!!.get())
                    } else if (map.containsKey(room1!!.get().toString())) {
                        chatRoomID!!.set(room1!!.get())
                    } else {
                        chatRoomID!!.set("")
                    }

                    if (chatRoomID!!.get().toString().isEmpty()) {
                        chatRoomID!!.set(room!!.get())
                    }

                    AppUtils.logDebug(TAG," Current Room : "+chatRoomID!!.get())

                    initChat(context, chatRoomID!!.get().toString())

                    reffChatRoomID?.child(MESSAGES)?.removeEventListener(listener!!)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun initChat(context: Context, chatRoomID: String) {

        var checkTypeReff =
            FirebaseDatabase.getInstance().getReference(MESSAGES).child(chatRoomID).child(TYPING);

        chatReff = Firebase(BASE_FIREBASE_URL + chatRoomID)

        checkTypeReff.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {

                if (snapshot.getValue() == null) {

                    val typing = object {
                        var user1 = object {
                            var id = reciverId!!.get()!!.toInt()
                            var isTyping = ""
                        }
                        var user2 = object {
                            var id = userId!!.get()!!.toInt()
                            var isTyping = ""
                        }
                    }
                    FirebaseDatabase.getInstance().getReference(MESSAGES).child("${chatRoomID}")
                        .child(TYPING).setValue(typing)
                    currentUser!!.set("user1")
                } else {

                    val map = snapshot.value as Map<*, *>
                    if(map.get("user1") != null) {
                        var obj = map.get("user1") as Map<*, *>
                        if (obj.get("id") == userId!!.get()!!.toInt()) {
                            AppUtils.logDebug(TAG, "query Change Call ===>> " + obj.get("id"))
                            currentUser!!.set("user1")
                        } else {
                            AppUtils.logDebug(TAG, "query Change Call else ==> " + obj.get("id"))
                            currentUser!!.set("user2")
                        }
                    }else
                        currentUser!!.set("user2")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        chatReff!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String??) {

                AppUtils.logWarning(TAG, " 140 onChildAdded " + dataSnapshot!!.getValue())
                try {

                    if (!dataSnapshot.key.equals(TYPING)) {

                        val chat = dataSnapshot.getValue(Chat::class.java)

                        if (!chat.sender!!.equals(userId!!.get()) && (chat.read == 0 || chat.read == 1)) {
                            chat.read = 2
                            dataSnapshot.ref.setValue(chat)
                        }

                        chat.key = dataSnapshot.key
                        mAdapter?.add(chat)

                        mAdapter?.notifyDataSetChanged()

                        data.value?.add(chat)

                        val mManager: NotificationManager =
                            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        mManager.cancelAll();
                    } else {

                        val map = dataSnapshot!!.value as Map<*, *>
                        if (!map.isEmpty()) {
                            try {
                                val obj = JSONObject(map.get("user1").toString())
                                if (userId!!.equals(obj.getString("id"))) {
                                    currentUser!!.set("user1")
                                } else {
                                    currentUser!!.set("user2")
                                }
                            } catch (e: Exception) {
                                AppUtils.logError(TAG, "Exception : ==${e.message}")
                            }

                        }
                        AppUtils.logDebug(TAG, " Typing ====>> " + Gson().toJson(map.get("user1")))
                    }

                } catch (e: Exception) {
                    e.message
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String??) {
                AppUtils.logWarning(TAG, " 186 onChildChanged " + dataSnapshot!!.getValue())
                val map = dataSnapshot.getValue().toString()
                if (!map.contains("user1") && !map.contains("user2")) {
                    data.value!!.forEachIndexed { index, chat ->
                        if (chat.sender == userId!!.get() && dataSnapshot.key.equals(chat.key)) {
                            mAdapter?.getItem(index)?.read = 2
                            AppUtils.logDebug(TAG, "index ==>> " + mAdapter?.getItem(index)!!.text)
                        }
                    }
                    mAdapter?.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {}
            override fun onCancelled(firebaseError: FirebaseError) {
            }
        })

        Firebase(BASE_FIREBASE_URL + chatRoomID )!!.child(TYPING).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot?, p1: String??) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, p1: String??) {

                val map = dataSnapshot!!.value as Map<*, *>
                if(map.get("id")!= null) {
                    if (!map.get("id").toString().equals(userId!!.get())) {
                        isTyping!!.set("Typing...")
                        Handler(Looper.getMainLooper()).postDelayed({
                            isTyping!!.set("")
                        }, 2000)
                    }
                }
            }

            override fun onChildRemoved(p0: DataSnapshot?) {}
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
            override fun onCancelled(p0: FirebaseError?) {}
        });
    }

    fun sendImageToChat(view: View) {
        if(imageUri?.get() != null){
            uploadImage()
        }
    }

    fun sendMessage(view: View) {

        if (!AppUtils.isNetworkConnected(context)) {
            Toast.makeText(context, "Please Connect To Internet !", Toast.LENGTH_SHORT).show()
        } else {

            if (!message!!.get().toString().isEmpty()) {
                var chat: Chat = Chat.createFromParcel(Parcel.obtain())
                chat.sender = userId!!.get()
                chat.timestamp = Date().time
                chat.type = "text"
                chat.fileName = ""
                chat.text = message?.get()
                chat.read = 0
                Firebase(BASE_FIREBASE_URL).child(chatRoomID!!.get().toString()).push()
                    .setValue(chat)
                message?.set("")
            }
        }
    }


    fun uploadImage(){

        var file : File  =  File(AppUtils.getRealPathFromURI(imageUri?.get()!!,context)!!)
        var contentType : String  = "file";
//        file: File, content_type: String?, listener: UploadCallbacks
        val fileBody = ProgressRequestBody(file, contentType,this)
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.getName(), fileBody)


//        val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//        val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestFile)


        val apiclient = ApiClient.getClient()
        val apiInterface = apiclient?.create(ApiInterface::class.java)
        val request: Call<JsonObject>? = apiInterface?.uploadImage(filePart)
        request?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>?, response: Response<JsonObject?>) {
                if (response.isSuccessful()) {
//                    imageUri.set()
                    AppUtils.logDebug(TAG,"Response ===>> "+response.body())
                }
            }

            override fun onFailure(call: Call<JsonObject?>?, t: Throwable?) {
                AppUtils.logError(TAG," Network Error  ===>> "+t!!.message)
                /* we can also stop our progress update here, although I have not check if the onError is being called when the file could not be downloaded, so I will just use this as a backup plan just in case the onError did not get called. So I can stop the progress right here. */
            }
        })
    }

    companion object {
        private const val TAG: String = "ChatViewModel"
    }

    override fun onProgressUpdate(percentage: Int) {
       AppUtils.logDebug(TAG,"ProgressUpdate : "+percentage)
    }

    override fun onError() {
        AppUtils.logError(TAG,"onError to File Upload : ")
    }

    override fun onFinish() {
        AppUtils.logDebug(TAG,"onFinish Upload : ===>> ")
    }

    fun showDialog(activity: Context?,bitmap: Bitmap) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_send_fiile)
        val imagePreview = dialog.findViewById(R.id.btnSendMessage) as ImageView
        imagePreview.setImageBitmap(bitmap)
        val dialogButton: ImageView = dialog.findViewById(R.id.btnSendMessage)
        dialogButton.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        dialog.show()
    }
}