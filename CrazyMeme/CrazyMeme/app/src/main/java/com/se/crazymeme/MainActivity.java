package com.se.crazymeme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class MainActivity extends AppCompatActivity {

    private Button community;
    private Button user_info;
    private Button product;
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private ImageView img4;
    private ImageView img5;
    //轮播图片
    private int[] imgArray;
    private final int num_imgs = 20;
    private Bitmap bitmap;
//    private FaceHelper faceHelper;

    //返回码，本地图库
    private static final int RESULT_IMAGE = 100;
    //返回码，相机
    private static final int RESULT_CAMERA = 200;
    //拍照后照片的Uri
    private Uri imageUri;
    private Uri cropUri;
    public static final int CHOOSE_PHOTO = 2;
    public static final int CROP_PHOTO = 3;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        community = (Button) findViewById(R.id.btn_shequ);
        product = (Button) findViewById(R.id.btn_zhizuo) ;

        RotateAnimation rotate  = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(4000);//设置动画持续时间
        rotate.setRepeatCount(-1);//设置重复次数
        rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        rotate.setStartOffset(10);//执行前的等待时间
        img1 = (ImageView) findViewById(R.id.img1);
        img1.setAnimation(rotate);
        img2 = (ImageView) findViewById(R.id.img2);
        img2.setAnimation(rotate);
        img3 = (ImageView) findViewById(R.id.img3);
        img3.setAnimation(rotate);
        img4 = (ImageView) findViewById(R.id.img4);
        img4.setAnimation(rotate);
        img5 = (ImageView) findViewById(R.id.img5);
        img5.setAnimation(rotate);

        imgArray = new int[num_imgs];
        int[] a = {R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5,
                R.drawable.a6, R.drawable.a7, R.drawable.a8, R.drawable.a9, R.drawable.a10, R.drawable.a11,
                R.drawable.a12, R.drawable.a13, R.drawable.a14, R.drawable.a15, R.drawable.a16,
                R.drawable.a17, R.drawable.a18,R.drawable.a19, R.drawable.a20};
        imgArray = a;
        img3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int rand;
                rand = getNum(0,num_imgs-1);
                img1.setImageResource(imgArray[rand]);
                img2.setImageResource(imgArray[(rand + 1)%num_imgs]);
                img3.setImageResource(imgArray[(rand + 2)%num_imgs]);
                img4.setImageResource(imgArray[(rand + 3)%num_imgs]);
                img5.setImageResource(imgArray[(rand + 4)%num_imgs]);
                return true;
            }
        });
        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebActivity.class);
                startActivity(intent);
            }
        });
        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Manufacture.class);
                startActivity(intent);
//                checkPermission();
//                load();
//                startPhotoCrop();
            }
        });
    }

    //检查权限
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            //发现没有权限，调用requestPermissions方法向用户申请权限，requestPermissions接收三个参数，第一个是context，第二个是一个String数组，我们把要申请的权限
            //名放在数组中即可，第三个是请求码，只要是唯一值就行
        } else {
            openAlbum();//有权限就打开相册
        }
    }

    public void openAlbum() {
        //通过intent打开相册，使用startactivityForResult方法启动actvity，会返回到onActivityResult方法，所以我们还得复写onActivityResult方法
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }
    //弹出窗口向用户申请权限


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);//弹出授权的窗口，这条语句也可以删除，没有影响
        //获得了用户的授权结果，保存在grantResults中，判断grantResult中的结果来决定接下来的操作
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "授权失败，无法操作", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnkitKat(data);//高于4.4版本使用此方法处理图片
                    } else {
                        handleImageBeforeKitKat(data);//低于4.4版本使用此方法处理图片
                    }
                }
                break;
            case CROP_PHOTO:
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    img1.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnkitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android,providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    //获得图片路径
    public String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);   //内容提供器
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));   //获取路径
            }
        }
        cursor.close();
        return path;
    }

    //展示图片
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            save(imagePath);
            Bitmap bitImage = compressPixel(imagePath); //压缩图片
//            Bitmap bitImage = BitmapFactory.decodeFile(imagePath);//格式化图片
            bitImage = FaceHelper.genFaceBitmap(bitImage);
            img1.setImageBitmap(bitImage);//为imageView设置图片
//            startPhotoCrop();

        } else {
            Toast.makeText(MainActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }
    private void save(String imagePath){
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();//获得SHaredPreferences.Editor对象
        editor.putBoolean("imageChange",true);//添加一个名为imageChange的boolean值，数值为true
        editor.putString("imagePath",imagePath);//保存imagePath图片路径
        editor.apply();//提交
    }
    private void load() {
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);//获得SharedPreferences的对象
        //括号里的判断是去找imageChange这个对应的数值，若是找不到，则是返回false，找到了的话就是我们上面定义的true，就会执行其中的语句
        if (preferences.getBoolean("imageChange", false)) {
            String imagePath = preferences.getString("imagePath", "");//取出保存的imagePath，若是找不到，则是返回一个空
            imageUri = Uri.parse(imagePath);
            displayImage(imagePath);//调用显示图片方法，为ImageView设置图片
        }
    }

    /**
     * 开启裁剪相片
     */
    public void startPhotoCrop() {
        //创建file文件，用于存储剪裁后的照片
//        File cropImage = new File(Environment.getExternalStorageDirectory(), "crop_image.jpg");
        File cropImage = new File(getExternalCacheDir(), "crop_image.jpg");
        try {
            if (cropImage.exists()) {
                cropImage.delete();
            }
            cropImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        cropUri = Uri.fromFile(cropImage);
        cropUri = imageUri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        //设置源地址uri
        intent.setDataAndType(cropUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        //设置目的地址uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        //设置图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("return-data", false);//data不需要返回,避免图片太大异常
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, CROP_PHOTO);
    }

    private Bitmap compressPixel(String filePath){
        Bitmap bmp = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        //setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = 2;

        //inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[16 * 1024];
        try {
            //load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
            if (bmp == null) {

                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(filePath);
                    BitmapFactory.decodeStream(inputStream, null, options);
                    inputStream.close();
                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }finally {
            return bmp;
        }
    }

    private void delay(int ms) {
        try {
            Thread.currentThread();
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getNum(int startNum,int endNum){
        if(endNum > startNum){
            Random random = new Random();
            return random.nextInt(endNum - startNum) + startNum;
        }
        return 0;
    }
//    private Uri imgPath() {
//        Uri imageUri = null;
//        String status = Environment.getExternalStorageState();
//        if (status.equals(Environment.MEDIA_MOUNTED)) {
//            //创建File对象，用于存储拍照后的照片
//            File outputImage = new File(getExternalCacheDir(), "out_image.jpg");//SD卡的应用关联缓存目录
//            try {
//                if (outputImage.exists()) {
//                    outputImage.delete();
//                }
//                outputImage.createNewFile();
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    imageUri = FileProvider.getUriForFile(MainActivity.this,
//                            "com.hanrui.android.fileprovider", outputImage);//添加这一句表示对目标应用临时授权该Uri所代表的文件
//                } else {
//                    imageUri = Uri.fromFile(outputImage);
//                }
//                //启动相机程序
//                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                startActivityForResult(intent, RESULT_CAMERA);
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                Toast.makeText(MainActivity.this, "没有找到储存目录", Toast.LENGTH_LONG).show();
//            }
//        } else {
//            Toast.makeText(MainActivity.this, "没有储存卡", Toast.LENGTH_LONG).show();
//        }
//        dialog.dismiss();
//        return imageUri;
//    }
//
//    private void showDialogCustom() {
//        //创建对话框
//        String[] mCustomItems = new String[]{"本地相册", "相机拍照"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("请选择：");
//        builder.setItems(mCustomItems, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == 0) {
//                    //相册
//
//                } else if (which == 1) {
//                    //照相机
//
//                }
//            }
//        });
//        builder.create().show();
//    }
//
//
//    private void openAlbum() {
//        Intent intent = new Intent("android.intent.action.GET_CONTENT");
//        intent.setType("image/*");
//        startActivityForResult(intent, RESULT_IMAGE);//打开相册
//
//    }
}