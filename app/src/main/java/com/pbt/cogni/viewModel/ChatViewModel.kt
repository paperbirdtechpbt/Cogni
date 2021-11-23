package com.pbt.cogni.viewModel

import android.Manifest
import android.app.Application
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
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
import com.pbt.cogni.R
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.chat.ChatActivity
import com.pbt.cogni.activity.chat.adapter.ChatAdapter
import com.pbt.cogni.activity.chat.upload.ProgressRequestBody
import com.pbt.cogni.callback.PermissionCallBack
import com.pbt.cogni.model.BaseExpense
import com.pbt.cogni.model.Chat
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.util.AppConstant.Companion.MESSAGES
import com.pbt.cogni.util.AppConstant.Companion.TYPING
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.ClickListener
import com.pbt.cogni.util.Config.BASE_FIREBASE_URL
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ChatViewModel(app: Application) : AndroidViewModel(app), ProgressRequestBody.UploadCallbacks,ClickListener{



    companion object {
        private const val TAG: String = "ChatViewModel"
       var imgResponseUrl:String=""

    }

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
    var imageUri: ObservableField<String>? = null

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

    fun setData(context: Context) {

        listener = reffChatRoomID?.child(MESSAGES)?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {

                    AppUtils.logDebug(TAG, "ChatRoom ID Chage : " + snapshot.getValue())

                    val map = snapshot.value as Map<*, *>
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
                        val obj = map.get("user1") as Map<*, *>
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
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                AppUtils.logWarning(TAG, " 140 onChildAdded " + dataSnapshot.getValue())
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
                        mManager.cancelAll()
                    } else {

                        val map = dataSnapshot.value as Map<*, *>
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

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                AppUtils.logWarning(TAG, " 186 onChildChanged " + dataSnapshot.getValue())
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

        Firebase(BASE_FIREBASE_URL + chatRoomID ).child(TYPING).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, p1: String?) {

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



    fun sendMessage(view: View) {

        if (!AppUtils.isNetworkConnected(context)) {
            Toast.makeText(context, "Please Connect To Internet !", Toast.LENGTH_SHORT).show()
        } else {

            if (!message!!.get().toString().isEmpty()) {
                val chat: Chat = Chat.createFromParcel(Parcel.obtain())
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

    fun sendImageMessage(){
//        arrayImageChat.forEach((element, index) => {
//            let chat = new Chat(userID, new Date().getTime(), element.extension, element.image, 0,element.fileName);
//            firebase.database().ref("messages/" + chatID).push(chat).then((snap) => {
//                const key = snap.key
//                        sendNotification(chat, key, userID, receiverID)
//                var elem = document.getElementById('MyChatMessage');
//                elem.scrollTop = elem.scrollHeight;
//            })
//        });
    }


//     fun uploadImage(){
//
////         val file = File(param)
////         val requestFile: RequestBody =
////             file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
////         val body: MultipartBody.Part =
////             MultipartBody.Part.createFormData("image", file.name, requestFile)
//         var file : File  =  File(imageUri?.get()!!)
//
//        var contentType : String  = "file"
////        file: File, content_type: String?, listener: UploadCallbacks
//        val fileBody = ProgressRequestBody(file, contentType,this)
//        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.getName(), fileBody)
//
//
////        val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
////        val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestFile)
//
//        val apiclient = ApiClient.getClient()
//        val apiInterface = apiclient?.create(ApiInterface::class.java)
//        val request: Call<HttpResponse>? = apiInterface?.uploadImage(filePart)
//        request?.enqueue(object : Callback<HttpResponse?> {
//            override fun onResponse(call: Call<HttpResponse?>?, response: Response<HttpResponse?>) {
//                if (response.isSuccessful()) {
//
//                 val httpResponse:HttpResponse= response.body()!!
//
//                    imageURLL=httpResponse.data.get("image").toString()
//
////                    imageUri.set()
//                    AppUtils.logDebug(TAG,"Response ===>> "+"$imageURLL")
//                }
//            }
//
//            override fun onFailure(call: Call<HttpResponse?>?, t: Throwable?) {
//
//                AppUtils.logError(TAG," Network Error  ===>> "+t!!.message)
//                /* we can also stop our progress update here, although I have not check if the onError is being called when the file could not be downloaded, so I will just use this as a backup plan just in case the onError did not get called. So I can stop the progress right here. */
//            }
//        })
//
//
//    }
    fun sendImageToChat(view: View) {

        val file : File  =  File(imageUri?.get()!!)
        val contentType : String  = "file"
        val fileBody = ProgressRequestBody(file, contentType,this)
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.getName(), fileBody)
AppUtils.logDebug(TAG,"File path--$file")

        val apiclient = ApiClient.getClient()
        val apiInterface = apiclient?.create(ApiInterface::class.java)
        val request: Call<HttpResponse>? = apiInterface?.uploadImage(filePart)
        request?.enqueue(object : Callback<HttpResponse?> {
            override fun onResponse(call: Call<HttpResponse?>?, response: Response<HttpResponse?>) {
                if (response.isSuccessful()) {

                    val listLatLong: BaseExpense =
                        Gson().fromJson(response.body()?.data.toString(), BaseExpense::class.java)

                    sendImageToChatt(listLatLong.image,listLatLong.fileName,listLatLong.extension)
                    AppUtils.logDebug(TAG,"Response ===>> ${response.body()}")

                    ChatActivity.binding?.rlImageSend?.visibility = View.GONE
                    ChatActivity.binding?.chatViewModel?.isVisiBled?.set(false)
                }
            }
            override fun onFailure(call: Call<HttpResponse?>?, t: Throwable?) {

                AppUtils.logError(TAG," Network Error  ===>> "+t!!.message)
            }
        })


//        if(imageUri?.get() != null){
////            uploadImage()
//            val chat: Chat = Chat.createFromParcel(Parcel.obtain())
//            chat.sender = userId!!.get()
//            chat.timestamp = Date().time
//            chat.type = "jpg"
//            chat.fileName = "asdasdadasda.jpg"
//
////            chat.text = message?.get()
//            chat.text = imageURLL
//            AppUtils.logDebug(TAG,"MY URL--------->>>>\n$imageURLL")
//            chat.read = 0
//            Firebase(BASE_FIREBASE_URL).child(chatRoomID!!.get().toString()).push()
//                .setValue(chat)
//            message?.set("")
//
//            AppUtils.logDebug(TAG,"ImageURLLLLL-->>>$imageURLL")
//
//        }
    }

     fun sendImageToChatt(imageURLL: String?, fileName: String, extension: String) {
        if(imageUri?.get() != null){
//            uploadImage()

            val bar = imageURLL
            val chat: Chat = Chat.createFromParcel(Parcel.obtain())
            chat.sender = userId!!.get()
            chat.timestamp = Date().time
            chat.type = extension
            chat.fileName = fileName
//            chat.text = message?.get()
            chat.text ="$bar"
            AppUtils.logDebug(TAG,imageURLL!!)
            chat.read = 0
            Firebase(BASE_FIREBASE_URL).child(chatRoomID!!.get().toString()).push()
                .setValue(chat)
            message?.set("")

            AppUtils.logDebug(TAG,"ImageURLLLLL-->>>$imageURLL")

        }
        else
            AppUtils.logDebug(TAG,"Image Uri is Null")
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

    override fun onItemClick(position: Int, v: View?) {
       Toast.makeText(context,"CLick on $position", Toast.LENGTH_SHORT).show()
    }
}