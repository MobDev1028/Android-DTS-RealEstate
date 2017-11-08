package com.dts.dts.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by BilalHaider on 5/2/2016.
 */
public class Helper {
    public static String getErrorAlert(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        String message = "Server Error";
        if (response != null) {
            try {
                String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                Log.d("RAW-NETWORK-ERROR", jsonString);
                Gson gson = new GsonBuilder().create();
                JsonObject result = gson.fromJson(jsonString, JsonObject.class);
                //JsonObject errorObj = result.getAsJsonObject("error");
                if (result.get("message") != null)
                    message = result.get("message").getAsString();
                else if (result.get("error") != null)
                    message = result.get("error").getAsString();
            } catch (UnsupportedEncodingException | NullPointerException | JsonSyntaxException e) {
                e.printStackTrace();
                error.setStackTrace(e.getStackTrace());
            }
            return message;
        }
        return message;
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static void hideKeyboard(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    /*public static void setImageFromDevice(ImageLoader imageLoader, CircularImageView imageView, DoctorDB datum, Context context, boolean showThumbnail) {
        String path;
        if (showThumbnail) {
            path = Environment.getExternalStorageDirectory() + "/.doktorderki_temp/" + datum.getDoctorId() + "_th.jpg";
        } else
            path = Environment.getExternalStorageDirectory() + "/.doktorderki_temp/" + datum.getDoctorId() + ".jpg";
        File file = new File(path);
        if (file.exists()) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            if (datum.getImageUpdated()) {
                getImageFromNetwork(imageLoader, datum, imageView, context, showThumbnail);
            }
        } else {
            getImageFromNetwork(imageLoader, datum, imageView, context, showThumbnail);
        }
    }

    private static void getImageFromNetwork(ImageLoader imageLoader, final DoctorDB datum, final ImageView imageView, final Object context, final boolean showThumbnail) {
        imageLoader.get(datum.getProfilePicUrl(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    if (showThumbnail) {
                        imageView.setImageBitmap(Bitmap.createScaledBitmap(response.getBitmap()
                                , Constants.IMAGE_SIZE_TH, Constants.IMAGE_SIZE_TH, false));
                    } else
                        imageView.setImageBitmap(response.getBitmap());
                    new SaveImageOnDeviceTask().execute(response.getBitmap(), datum.getDoctorId(), context);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, Constants.IMAGE_SIZE, Constants.IMAGE_SIZE);
    }

    public static void parseUserCreditsAndBuyingOptions(Context context, JsonObject response) {
        try {
            JsonObject user = response.getAsJsonObject("user");
            if (user != null) {
                new LocalPreferences(context).saveUserCredits(user.get("credits").getAsInt());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JsonElement options = response.get("buying_options");
            if (options != null) {
                new LocalPreferences(context).saveBuyingOptions(String.format("{\"buying_options\":%s}", options.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class SaveImageOnDeviceTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            saveBitmapOnDevice((Bitmap) params[0], (String) params[1], (Context) params[2]);
            return null;
        }
    }

    private static void saveBitmapOnDevice(Bitmap bitmap, String doctorId, Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                return;

            }
        }
        File tempFolder = new File(Environment.getExternalStorageDirectory(), "/.doktorderki_temp/");
        if (!tempFolder.exists())
            tempFolder.mkdir();
        try {
            FileOutputStream fos = new FileOutputStream(new File(tempFolder, doctorId + ".jpg"));
            FileOutputStream th_fos = new FileOutputStream(new File(tempFolder, doctorId + "_th.jpg"));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            bitmap = Bitmap.createScaledBitmap(bitmap, Constants.IMAGE_SIZE_TH, Constants.IMAGE_SIZE_TH, false);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, th_fos);
            fos.flush();
            fos.close();
            th_fos.flush();
            th_fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static int convertDip2Pixels(Context context, float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKatOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKatOrAbove && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
