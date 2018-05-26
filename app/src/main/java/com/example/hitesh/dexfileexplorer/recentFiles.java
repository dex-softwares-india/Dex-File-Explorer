package com.example.hitesh.dexfileexplorer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class recentFiles extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String TAG="permission";
    private String mParam1;
    private String mParam2;
    List<fileItem> recent;
    files_adapter adapter;
    ListView listView;
    private OnFragmentInteractionListener mListener;

    public recentFiles() {
        // Required empty public constructor
    }


    public static recentFiles newInstance(String param1, String param2) {
        recentFiles fragment = new recentFiles();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent_files, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView=view.findViewById(R.id.recent);
        if (isStoragePermissionGranted()){
            Uri uri;
            Cursor cursor;
            String absolutePathOfImage;
            int column_index_data, column_index_folder_name;
            uri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Log.i("uri",String.valueOf(uri));
            String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            cursor = getContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int c=0;
            recent=new ArrayList<>();
            while (cursor.moveToNext()&&c<=20) {
                c++;
                absolutePathOfImage = cursor.getString(column_index_data);
                Log.e("Column", absolutePathOfImage);
                Log.e("Folder", cursor.getString(column_index_folder_name));
                int count=recent.size();
                File f=new File(absolutePathOfImage);
                Date lastModDate=new Date(f.lastModified());
                DateFormat format=DateFormat.getDateTimeInstance();
                String date_modify=format.format(lastModDate);
                recent.add(count,new fileItem(f.getName(),f.getAbsolutePath(),date_modify,f.length()+" Byte","files2",""));
                Uri uri_item=Uri.fromFile(new File(absolutePathOfImage));
                //String type=getContext().getContentResolver().getType(uri);
                //itemList.get(count).setType(type);
                String mime = "*/*";
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                if (mimeTypeMap.hasExtension(
                        mimeTypeMap.getFileExtensionFromUrl(uri_item.toString())))
                    mime = mimeTypeMap.getMimeTypeFromExtension(
                            mimeTypeMap.getFileExtensionFromUrl(uri_item.toString()));
                recent.get(count).setType(mime);
                Log.i("hmm",mime);

            }
            adapter=new files_adapter(getContext(),recent);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    fileItem item= (fileItem) adapter.getItem(i);

                        File file = new File(item.getPath());

                        Uri uri =  Uri.fromFile(file);
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                        String mime = "*/*";
                        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                        if (mimeTypeMap.hasExtension(
                                mimeTypeMap.getFileExtensionFromUrl(uri.toString())))
                            mime = mimeTypeMap.getMimeTypeFromExtension(
                                    mimeTypeMap.getFileExtensionFromUrl(uri.toString()));
                        intent.setDataAndType(uri,mime);
                        Log.i("hmm",mime);

                        startActivity(intent);
                }
            });
        }
}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
